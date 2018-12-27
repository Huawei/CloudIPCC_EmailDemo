
package com.huawei.adapter.startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.common.business.AccessConfigReader;
import com.huawei.adapter.common.config.ConfigList;
import com.huawei.adapter.common.config.ConfigProperties;
import com.huawei.adapter.common.config.PasswdPropertyPlaceholder;
import com.huawei.adapter.common.config.RootKeyManager;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.dao.init.Mybatis;
import com.huawei.adapter.icsevent.ICSEventPollTask;
import com.huawei.adapter.icsserver.http.Request;
import com.huawei.adapter.task.AccessCodeStatisticTask;
import com.huawei.adapter.task.MasterDetectTask;
import com.huawei.adapter.task.MessageCleanTask;
import com.huawei.adapter.task.MessageDBFetchTask;
import com.huawei.adapter.task.MessageFailedUpdateTask;
import com.huawei.adapter.task.SessionCleanTask;
import com.huawei.adapter.task.SessionHeartBeatTask;
import com.huawei.adapter.task.SessionUpdateTask;

/**
 * 
 * <p>Title: 适配器的启动、停止 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class StartupListener implements ServletContextListener
{
    private static final Logger LOG = LoggerFactory.getLogger(StartupListener.class);

    private String binPath;

    /**
     * 服务器集群名
     */
    private String clusterId;

    @Override
    public void contextDestroyed(ServletContextEvent event)
    {
        MessageDBFetchTask.end();
        AccessCodeStatisticTask.end();
        MasterDetectTask.end();
        MessageCleanTask.end();
        MessageFailedUpdateTask.end();
        SessionUpdateTask.end();
        SessionCleanTask.end();
        SessionHeartBeatTask.end();
    }

    @Override
    public void contextInitialized(ServletContextEvent event)
    {
        LOG.info("==========System initialization begin");
        binPath = event.getServletContext().getRealPath("/");
        LOG.info("Start Parse keys.properties begin...");
        if (!RootKeyManager.parseKeysProperties(binPath))
        {
            LOG.error("Parse keys.properties failed");
            PasswdPropertyPlaceholder.clean();
            return;
        }
        LOG.info("Start Parse keys.properties end...");

        /**
         * 初始化工作秘钥
         */
        PasswdPropertyPlaceholder.init();

        /**
         * 配置信息初始化
         */
        LOG.info("Start load config files...");
        if (!ConfigProperties.loadConfig())
        {
            LOG.error("Load config file failed, we are shutdown now.");
            PasswdPropertyPlaceholder.clean();
            RootKeyManager.cleanKey();
            return;
        }
        

        clusterId = ConfigProperties.getKey(ConfigList.BASIC, "CLUSTER_ID");
        LOG.info("The system clusterId is " + LogUtils.encodeForLog(clusterId));

        RootKeyManager.cleanKey();
        LOG.info("Start load config files end...");

        LOG.info("Start Init Access Config...");
        if (!initAccessConfig())
        {
            LOG.error("Start Init Access Config failed...");
            return;
        }
        LOG.info("Start Init Access Config end...");
        initOther();
    }


    private void initOther()
    {
        LOG.info("Start Init ICSGW Connect...");
        if (!initICSGW())
        {
            LOG.error("Start Init ICSGW Connect failed...");
            return;
        }
        LOG.info("Start Init ICSGW Connect end...");

        LOG.info("Start Init DB Connect...");
        if (!initDB())
        {
            LOG.error("Start Init DB Connect failed...");
            return;
        }
        LOG.info("Start Init DB Connect end...");
        


        LOG.info("Start Hear Beat task...");
        SessionHeartBeatTask.begin();
        LOG.info("Start Hear Beat task end...");
  

        LOG.info("Start Init Master Detect Task...");
        MasterDetectTask.begin(clusterId);
        LOG.info("Start Init Master Detect Task end...");

        LOG.info("Start DB Fetch Task...");
        initDBFetchTask();
        LOG.info("Start DB Fetch Task end...");

        LOG.info("Start Task...");
        initTask();
        LOG.info("Start Task end...");
    }

    /**
     * 初始化数据库连接
     * 
     * @return
     */
    private boolean initDB()
    {
        Mybatis.initDBSqlSessionFactory();
        return Mybatis.isInitDBOk();
    }


    /**
     * 初始化分发线程
     */
    private void initDBFetchTask()
    {
        int createCallCount;
        try
        {
            /**
             * 获取用于发起呼叫的数据10-50
             */
            createCallCount = Integer.valueOf(ConfigProperties.getKey(ConfigList.BASIC, "MAX_FETCH_FOR_CREATE_CALL_COUNT"));
            if (createCallCount < Constants.MIN_FETCH_FOR_CREATE_CALL_COUNT)
            {
                createCallCount = Constants.MIN_FETCH_FOR_CREATE_CALL_COUNT;
            }
            if (createCallCount > Constants.MAX_FETCH_FOR_CREATE_CALL_COUNT)
            {
                createCallCount = Constants.MAX_FETCH_FOR_CREATE_CALL_COUNT;
            }

        }
        catch (NumberFormatException e)
        {
            LOG.error("get MAX_FETCH_FOR_CREATE_CALL_COUNT failed, use default value. \r\n {}", LogUtils.encodeForLog(e.getMessage()));
            createCallCount = Constants.DEFAULT_FETCH_FOR_CREATE_CALL_COUNT;
        }

      
        int maxThreads;
        try
        {
            /**
             * 获取消息处理线程数10-50
             */
            maxThreads = Integer.valueOf(ConfigProperties.getKey(ConfigList.BASIC, "MAX_ALARM_PROCESSOR_THREADS"));
            if (maxThreads < Constants.MIN_MESSAGE_PROCESSOR_THREADS)
            {
                maxThreads = Constants.MIN_MESSAGE_PROCESSOR_THREADS;
            }
            if (maxThreads > Constants.MAX_MESSAGE_PROCESSOR_THREADS)
            {
                maxThreads = Constants.MAX_MESSAGE_PROCESSOR_THREADS;
            }
        }
        catch (NumberFormatException e)
        {
            LOG.error("get MAX_ALARM_PROCESSOR_THREADS failed, use default value. \r\n {}", LogUtils.encodeForLog(e.getMessage()));
            maxThreads = Constants.DEFAULT_MESSAGE_PROCESSOR_THREADS;
        }
        int maxChatSessions;
        try
        {
            /**
             * 获取最大处理会话数500-2000
             */
            maxChatSessions = Integer.valueOf(ConfigProperties.getKey(ConfigList.BASIC, "MAX_CHAT_SESSION"));
            if (maxChatSessions < Constants.MIN_CHAT_SESSION)
            {
                maxChatSessions = Constants.MIN_CHAT_SESSION;
            }
            if (maxChatSessions > Constants.MAX_CHAT_SESSION)
            {
                maxChatSessions = Constants.MAX_CHAT_SESSION;
            }
        }
        catch (NumberFormatException e)
        {
            LOG.error("get MAX_CHAT_SESSION failed, use default value. \r\n {}", LogUtils.encodeForLog(e.getMessage()));
            maxChatSessions = Constants.DEFAULT_CHAT_SESSION;
        }
        MessageDBFetchTask.begin(clusterId, createCallCount, maxThreads, maxChatSessions);
    }

    /**
     * 初始化ICSGW连接
     */
    private boolean initICSGW()
    {
        Request.init();

        AccessCodeStatisticTask.begin();
        int maxThreads;
        try
        {
            /**
             * 获取消息处理线程数10-100
             */
            maxThreads = Integer.valueOf(ConfigProperties.getKey(ConfigList.BASIC, "MAX_ICSEVENT_PROCESSOR_THREADS"));
            if (maxThreads < Constants.MIN_ICSEVENT_PROCESSOR_THREADS)
            {
                maxThreads = Constants.MIN_ICSEVENT_PROCESSOR_THREADS;
            }
            if (maxThreads > Constants.MAX_ICSEVENT_PROCESSOR_THREADS)
            {
                maxThreads = Constants.MAX_ICSEVENT_PROCESSOR_THREADS;
            }
        }
        catch (NumberFormatException e)
        {
            LOG.error("get MAX_ICSEVENT_PROCESSOR_THREADS failed, use default value. \r\n {}", LogUtils.encodeForLog(e.getMessage()));
            maxThreads = Constants.DEFAULT_ICSEVENT_PROCESSOR_THREADS;
        }
        ICSEventPollTask.init(maxThreads);
        return true;
    }

    private void initTask()
    {
        MessageCleanTask.begin();
        MessageFailedUpdateTask.begin();
        SessionCleanTask.begin(clusterId);
        SessionUpdateTask.begin(clusterId);
    }

    /**
     * 初始化接入配置
     * 
     * @return
     */
    private boolean initAccessConfig()
    {
        String appConfigFile = binPath + "WEB-INF/config/accesscodes.xml";
        return AccessConfigReader.readAccessXML(appConfigFile);
    }
}
