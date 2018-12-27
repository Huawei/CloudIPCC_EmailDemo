package com.huawei.adapter.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.data.MessageQueueData;
import com.huawei.adapter.common.util.exception.CommonException;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.dao.service.MessageDBService;

/**
 * 
 * <p>Title: 消息清理任务 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @author wWX390857
 * @since
 */
public class MessageCleanTask extends Thread
{
    private static final Logger LOG = LoggerFactory.getLogger(MessageCleanTask.class);

    private static MessageCleanTask instance;

    /**
     * 线程是否启动
     */
    private boolean isAlive = false;

    private MessageDBService service = new MessageDBService();

    private MessageCleanTask()
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
                    doDeleteMessage();
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
     * 执行删除已经分发消息
     */
    private void doDeleteMessage()
    {
        int size = MessageQueueData.getMessageCleanQueue().size();
        if (0 < size)
        {
            // 获取待删除的消息的ID
            List<Long> ids = new ArrayList<Long>();
            Long id;
            for (int i = 0; i < size && i < Constants.MAX_BATCH_DELETE_IDS; i++)
            {
                id = MessageQueueData.getMessageCleanQueue().poll();
                ids.add(id);
                LOG.debug("Message's Id [{}] is cleaned", id);
            }
            service.batchUpdateMessageStatus(ids, Constants.DB_MESSAGE_SUCCESS);
        }

        if (size < Constants.MAX_BATCH_DELETE_IDS)
        {
            // 队列中没有待删除消息，则间隔1s后再判断
            try
            {
                sleep(Constants.CLEAN_INTERVAL);
            }
            catch (InterruptedException e)
            {
                LOG.error("sleep failed, \r\n {}",
                        LogUtils.encodeForLog(e.getMessage()));
            }
        }
    }

    public static void begin()
    {
        if (null != instance)
        {
            return;
        }
        instance = new MessageCleanTask();
        instance.isAlive = true;
        instance.setDaemon(true);
        instance.setName("MessageCleanTask");
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
