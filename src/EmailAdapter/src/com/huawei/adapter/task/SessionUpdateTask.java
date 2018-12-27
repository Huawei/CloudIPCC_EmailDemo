
package com.huawei.adapter.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.Session;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.data.MessageQueueData;
import com.huawei.adapter.common.util.exception.CommonException;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.dao.service.SessionDBService;

/**
 * 
 * <p>Title: 更新消息会话状态为建立 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class SessionUpdateTask extends Thread
{
    private static final Logger LOG = LoggerFactory.getLogger(SessionUpdateTask.class);

    private static SessionUpdateTask instance;

    /**
     * 当前服务器的集群ID
     */
    private String clusterId;

    private boolean isAlive;

    private SessionDBService service = new SessionDBService();

    private SessionUpdateTask(String clusterId)
    {
        this.clusterId = clusterId;
    }

    @Override
    public void run()
    {
        while (isAlive)
        {
            try
            {
                try
                {
                    doUpdateToConnect();
                }
                catch (Exception e)
                {
                    throw new CommonException(e);
                }
            }
            catch (CommonException e)
            {
                LOG.error("occurs unkown exception : \r\n {}", LogUtils.encodeForLog(e.getMessage()));
            }
            // 队列中没有待更新消息会话，则间隔1s后再判断
            try
            {
                sleep(Constants.SESSION_CLEAN_INTERVAL);
            }
            catch (InterruptedException e)
            {
                LOG.error("sleep failed, \r\n {}", LogUtils.encodeForLog(e.getMessage()));
            }
        }
    }

    /**
     * 更新短信会话状态为建立
     */
    private void doUpdateToConnect()
    {
        int size = MessageQueueData.getSessionUpdateQueue().size();
        if (0 < size)
        {
            //获取待更新的会话
            List<Session> list = new ArrayList<Session>();
            for (int i = 0; i < size && i < Constants.MAX_BATCH_UPDATE_CONNECT; i++)
            {
                Session session = MessageQueueData.getSessionUpdateQueue().poll();
                if(null != session)
                {
                    list.add(session);
                    LOG.debug(Constants.PRINT_USER + " begin to update session to connect, session {}",
                            new Object[]{LogUtils.encodeForLog(session.getVdnId()), 
                                    LogUtils.encodeForLog(session.getSessionUserName()), 
                                    LogUtils.encodeForLog(session.toString())});
                }
            }
            service.updateSession(clusterId, list);
            if (size > Constants.MAX_BATCH_UPDATE_CONNECT)
            {
                //队列中还有未更新的会话
                return;
            }
        }
    }

    /**
     * @param clusterId 集群名
     */
    public static void begin(String clusterId)
    {
        if (null != instance)
        {
            return;
        }
        instance = new SessionUpdateTask(clusterId);
        instance.isAlive = true;
        instance.setName("SessionUpdateTask");
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
