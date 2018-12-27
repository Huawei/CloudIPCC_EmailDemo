
package com.huawei.adapter.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.data.GlobalData;
import com.huawei.adapter.common.util.exception.CommonException;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.dao.service.ServerStatusDBService;
import com.huawei.adapter.dao.service.SessionDBService;

/**
 * 
 * <p>Title: 主备服务器仲裁线程 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class MasterDetectTask extends Thread
{
    private static final Logger LOG = LoggerFactory.getLogger(MasterDetectTask.class);

    // 最大失败次数
    private static final int MAX_FAILED_TIMES = 3;

    private static MasterDetectTask instance;

    private String clusterId;

    /**
     * 线程是否启动
     */
    private boolean isAlive = false;

    private SessionDBService sessionDBService = new SessionDBService();

    // 当前连接失败次数
    private int connectFailedTimes = 0;

    private MasterDetectTask()
    {
    }

    @Override
    public void run()
    {
        while (isAlive)
        {
            /**
             * 主备服务器的判断原理 在数据库t_wecc_serverstate表中进行更新，如果更新成功则说明是主机。
             * 更新成功条件 
             * 1. 数据表t_wecc_serverstate的cluster_id为null，能更新成功 .
             * 2. 数据表t_wecc_serverstate的cluster_id为当前服务器时，能更新成功 .
             * 3. 数据表t_wecc_serverstate的UPDATE_TIME与数据库的时间进行比较，如果2分钟没有更新，则能更新成功
             */
            doCheck();
            try
            {
                sleep(Constants.MASTRT_DETECT_INTERVAL);
            }
            catch (InterruptedException e)
            {
                LOG.error("sleep failed, \r\n {}",
                        LogUtils.encodeForLog(e.getMessage()));
            }
        }
    }

    /**
     * 进行检查
     */
    private void doCheck()
    {
        /**
         * 主备服务器的判断原理
         * 在数据库t_sms_serverstate表中进行更新，如果更新成功则说明是主机。
         * 更新成功条件
         * 1. 数据表t_sms_serverstate的cluster_id为null，能更新成功
         * 2. 数据表t_sms_serverstate的cluster_id为当前服务器时，能更新成功
         * 3. 数据表t_sms_serverstate的UPDATE_TIME与数据库的时间进行比较，如果2分钟没有更新，则能更新成功
         */
        try
        {
            try
            {
                int result = ServerStatusDBService.getInstance().updateServerStatus(clusterId);
                if (0 == result)
                {
                    if (GlobalData.isMaster())
                    {
                        //原先是主机，现在变为备机
                        serverToSlave();
                    }
                }
                else 
                {
                    if (!GlobalData.isMaster())
                    {
                        //原先是备机，现在变为主机
                        serverToMaster();
                    }
                }
                
                if (connectFailedTimes >= MAX_FAILED_TIMES)
                {
                   //数据库恢复
                    LOG.warn("reconnect to database success");
                }
                connectFailedTimes = 0;
            }
            catch (Exception e)
            {
                throw new CommonException(e);
            }
        }
        catch (CommonException e)
        {
            //数据库异常
            connectFailedTimes++;
            if (connectFailedTimes == MAX_FAILED_TIMES)
            {
                //连接失败次数大于
                LOG.warn("connect to database failed");
            }
        }
    }

    /**
     * 服务器变为备机
     */
    private void serverToSlave()
    {
        LOG.warn("The server is changed to slave");
        GlobalData.setMaster(false);
    }

    /**
     * 服务器变为主机
     */
    private void serverToMaster()
    {
        LOG.warn("The server is changed to master");
        sessionDBService.deleteAllSession();
        GlobalData.setMaster(true);
    }

    /**
     * 
     * @param clusterId
     *            当前服务器的集群名
     */
    public static void begin(String clusterId)
    {
        if (null != instance)
        {
            return;
        }
        instance = new MasterDetectTask();
        instance.clusterId = clusterId;
        instance.isAlive = true;
        instance.setName("MasterDetectTask");
        instance.start();
    }

    public static void end()
    {
        if (null != instance)
        {
            instance.isAlive = false;
        }
    }
}
