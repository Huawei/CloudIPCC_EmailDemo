package com.huawei.adapter.common.data;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.Message;
import com.huawei.adapter.bean.Session;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.util.utils.LogUtils;

/**
 * 
 * <p>Title: 消息内容全局存储 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public abstract class MessageQueueData
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageQueueData.class);
    
    /**
     * 待删除的消息
     */
    private static final Queue<Long> MESSAGE_CLEANED_QUEUE = new LinkedBlockingQueue<Long>();

    /**
     * 待更新的消息
     */
    private static final Queue<Message> MESSAGE_UPDATE_FAILED_QUEUE = new LinkedBlockingQueue<Message>();

    /**
     * 待删除的消息会话
     */
    private static final Queue<Session> MESSAGE_SESSION_CLEANED_QUEUE = new LinkedBlockingQueue<Session>();

    /**
     * 待更新的消息会话
     */
    private static final Queue<Session> MESSAGE_SESSION_UPDATE_QUEUE = new LinkedBlockingQueue<Session>();



    /**
     * 将需要删除的消息的id放入待删消息队列中
     * @param id
     */
    public static void addToMessageCleanQueue(long id)
    {
        if (MESSAGE_CLEANED_QUEUE.offer(id))
        {
            LOGGER.info("The id  [{}] is added to MessageCleanTask's queue success",
                    new Object[]{LogUtils.encodeForLog(id)});
        }
        else
        {
            LOGGER.error("The id  [{}] is added to MessageCleanTask's queue failed", 
                    new Object[]{LogUtils.encodeForLog(id)});
        }
    }

    /**
     * 将需要更新的消息放入更新队列中
     * 
     * @param message
     */
    public static void addToMessageFailedUpdateQueue(Message message)
    {
        if (MESSAGE_UPDATE_FAILED_QUEUE.offer(message))
        {
            LOGGER.info(Constants.PRINT_USER + " add message to FailedUpdateTask's queue success, message {}",
                    new Object[]{LogUtils.encodeForLog(message.getVdnId()),
                            LogUtils.encodeForLog(message.getSessionUserName()), 
                            LogUtils.encodeForLog(message.toString())});
        }
        else
        {
            LOGGER.error(Constants.PRINT_USER + " add message to FailedUpdateTask's queue failed, message {}",
                    new Object[]{LogUtils.encodeForLog(message.getVdnId()),
                            LogUtils.encodeForLog(message.getSessionUserName()), 
                            LogUtils.encodeForLog(message.toString())});
        }
    }

    /**
     * 将需要删除的消息会话放入待删除消息会话队列中
     * 
     * @param Message
     */
    public static void addToSessionCleanQueue(Session session)
    {
        if (MESSAGE_SESSION_CLEANED_QUEUE.offer(session))
        {
            LOGGER.info(Constants.PRINT_USER + " add session to SessionCleanTask's queue success, session {}",
                    new Object[]{LogUtils.encodeForLog(session.getVdnId()),
                            LogUtils.encodeForLog(session.getSessionUserName()), 
                            LogUtils.encodeForLog(session.toString())});
        }
        else
        {
            LOGGER.error(Constants.PRINT_USER + " add session to SessionCleanTask's queue failed, session {}",
                    new Object[]{LogUtils.encodeForLog(session.getVdnId()),
                            LogUtils.encodeForLog(session.getSessionUserName()), 
                            LogUtils.encodeForLog(session.toString())});
        }
    }

    /**
     * 将呼叫建立需要更新的消息会话放入队列中
     * 
     * @param message
     */
    public static void addToSessionUpdateQueue(Session session)
    {
        if (MESSAGE_SESSION_UPDATE_QUEUE.offer(session))
        {
            LOGGER.info(Constants.PRINT_USER + " add session to SessionUpdateTask's queue success, session {}",
                    new Object[]{LogUtils.encodeForLog(session.getVdnId()),
                            LogUtils.encodeForLog(session.getSessionUserName()), 
                            LogUtils.encodeForLog(session.toString())});
        }
        else
        {
            LOGGER.error( Constants.PRINT_USER + " add session to SessionUpdateTask's queue failed, session {}",
                    new Object[]{LogUtils.encodeForLog(session.getVdnId()),
                            LogUtils.encodeForLog(session.getSessionUserName()), 
                            LogUtils.encodeForLog(session.toString())});
        }
    }

    /**
     * 获取待删除消息队列
     * 
     * @return
     */
    public static Queue<Long> getMessageCleanQueue()
    {
        return MESSAGE_CLEANED_QUEUE;
    }

    /**
     * 获取待更新消息队列
     * 
     * @return
     */
    public static Queue<Message> getMessageFailedUpdateQueue()
    {
        return MESSAGE_UPDATE_FAILED_QUEUE;
    }

    /**
     * 获取待删除会话队列
     * 
     * @return
     */
    public static Queue<Session> getSessionCleanQueue()
    {
        return MESSAGE_SESSION_CLEANED_QUEUE;
    }

    /**
     * 获取待更新的会话列表
     * 
     * @return
     */
    public static Queue<Session> getSessionUpdateQueue()
    {
        return MESSAGE_SESSION_UPDATE_QUEUE;
    }

    


}
