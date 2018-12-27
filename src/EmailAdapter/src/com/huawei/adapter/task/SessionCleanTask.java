
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
import com.huawei.adapter.session.ChatSessionManager;

/**
 * 
 * <p>Title: 消息会话处理任务 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class SessionCleanTask extends Thread
{
    private static final Logger LOG = LoggerFactory.getLogger(SessionCleanTask.class);

    private static SessionCleanTask instance;

    /**
     * 线程是否启动
     */
    private boolean isAlive = false;

    /**
     * 服务器集群名
     */
    private String clusterId;

    private SessionDBService service = new SessionDBService();

    private SessionCleanTask(String clusterId)
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
                    doCleanSession();
                }
                catch (Exception e)
                {
                    throw new CommonException(e);
                }
            }
            catch (CommonException e)
            {
                LOG.error("occurs unkown exception : \r\n {}", 
                        LogUtils.encodeForLog(e.getMessage()));
            }
        }
    }

    /**
     * 删除已经结束的会话
     */
    private void doCleanSession()
    {
        int size = MessageQueueData.getSessionCleanQueue().size();
        if (0 < size)
        {
            // 获取待删除的消息会话
            List<Session> list = new ArrayList<Session>();
            for (int i = 0; i < size && i < Constants.MAX_BATCH_DELETE_SESSIONS; i++)
            {
                Session session = MessageQueueData.getSessionCleanQueue().poll();
                if(null != session)
                {
                    list.add(session);
                    LOG.debug(Constants.PRINT_USER + " begin to clean session, message {}",
                            new Object[]{LogUtils.encodeForLog(session.getVdnId()), 
                                    LogUtils.encodeForLog(session.getSessionUserName()), 
                                    LogUtils.encodeForLog(session.toString())});
                }
            }
            if (0 == list.size())
            {
                //队列中没有待删除的消息会话，间隔1s后在判断
                doSleep(Constants.SESSION_CLEAN_INTERVAL);
                return;
            }
            if (service.batchDeleteSession(list, clusterId))
            {
                // 删除消息会话
                for (int i = 0; i < list.size(); i++)
                {
                    Session session = list.get(i);
                    ChatSessionManager.delChatSession(session.getVdnId(), session.getSessionUserName());
                }

                if (size > Constants.MAX_BATCH_DELETE_SESSIONS)
                {
                    // 还有未删除数据会话
                    return;
                }
            }
            else
            {
                // 数据库异常，导致删除会话失败，则将内容放回队列中
                for (int i = 0; i < list.size(); i++)
                {
                    Session session = list.get(i);
                    MessageQueueData.addToSessionCleanQueue(session);
                    LOG.error(Constants.PRINT_USER + " begin to clean session failed, alarm {}",
                            new Object[]{LogUtils.encodeForLog(session.getVdnId()), 
                                    LogUtils.encodeForLog(session.getSessionUserName()), 
                                    LogUtils.encodeForLog(session.toString())});
                }
                doSleep(Constants.SESSION_CLEAN_FAILED_INTERVAL);
                return;
            }
        }
        // 队列中没有待删除消息会话，则间隔5s后再判断
        doSleep(Constants.SESSION_CLEAN_INTERVAL);
    }

    private void doSleep(long times)
    {
        try
        {
            sleep(times);
        }
        catch (InterruptedException e)
        {
            LOG.error("sleep failed, \r\n {}", 
                    LogUtils.encodeForLog(e.getMessage()));
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
        instance = new SessionCleanTask(clusterId);
        instance.isAlive = true;
        instance.setName("SessionCleanTask");
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
