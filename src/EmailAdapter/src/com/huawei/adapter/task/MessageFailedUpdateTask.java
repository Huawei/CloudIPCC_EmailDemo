package com.huawei.adapter.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.Message;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.data.MessageQueueData;
import com.huawei.adapter.common.util.exception.CommonException;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.dao.service.MessageDBService;

/**
 * 
 * <p>Title: 更新发送失败的消息的任务 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class MessageFailedUpdateTask extends Thread
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageFailedUpdateTask.class);

    private static MessageFailedUpdateTask instance;

    private boolean isAlive;

    private MessageDBService service = new MessageDBService();

    private MessageFailedUpdateTask()
    {
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
                    doUpdateToFailed();
                }
                catch (Exception e)
                {
                    throw new CommonException(e);
                }
            }
            catch (CommonException e)
            {
                LOGGER.error("occurs unkown exception : \r\n {}",
                        LogUtils.encodeForLog(e.getMessage()));
            }
            try
            {
                sleep(Constants.SESSION_CLEAN_INTERVAL);
            }
            catch (InterruptedException e)
            {
                LOGGER.error("sleep failed, \r\n {}",
                        LogUtils.encodeForLog(e.getMessage()));
            }
        }
    }

    /**
     * 更新发送失败的消息
     */
    private void doUpdateToFailed()
    {
        int size = MessageQueueData.getMessageFailedUpdateQueue().size();
        if (0 < size)
        {
            // 获取待更新的消息
            List<Long> list = new ArrayList<Long>();
            
            for (int i = 0; i < size && i < Constants.MAX_BATCH_UPDATE_FAILED; i++)
            {
                Message message = MessageQueueData.getMessageFailedUpdateQueue().poll();
                if (null != message)
                {
                    list.add(Long.valueOf(message.getId()));
                    LOGGER.debug(Constants.PRINT_USER + " begin to update message to failed, alarm {}",
                            new Object[]{LogUtils.encodeForLog(message.getVdnId()), 
                                    LogUtils.encodeForLog(message.getSessionUserName()),
                                    LogUtils.encodeForLog(message.toString())});
                }
            }
            service.batchUpdateMessageStatus(list, Constants.DB_MESSAGE_FAILED);
        }
    }

    /**
     * @param clusterId
     *            集群名
     */
    public static void begin()
    {
        if (null != instance)
        {
            return;
        }
        instance = new MessageFailedUpdateTask();
        instance.isAlive = true;
        instance.setDaemon(true);
        instance.setName("MessageFailedUpdateTask");
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
