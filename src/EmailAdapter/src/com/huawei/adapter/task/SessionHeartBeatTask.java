package com.huawei.adapter.task;

import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.common.util.exception.CommonException;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.session.ChatSession;
import com.huawei.adapter.session.ChatSessionManager;

/**
 * 
 * <p>Title: 与ICSGW进行用户心跳检测 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class SessionHeartBeatTask extends Thread
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHeartBeatTask.class);

    private static final long CHECK_INTERVAL = 30 * 1000;//任务执行周期

    private static SessionHeartBeatTask task;

    private boolean isAlive;

    private SessionHeartBeatTask()
    {
        
    }

    @Override
    public void run()
    {
        while(isAlive)
        {
            doHeartBeat();
            doSleep();
        }
    }

    /**
     * 心跳连接
     */
    private void doHeartBeat()
    {
        try
        {
            try
            {
                Iterator<Entry<String, ChatSession>> iterator = ChatSessionManager.
                        getAllChatSessions().entrySet().iterator();
                while(iterator.hasNext())
                {
                    iterator.next().getValue().doHeartBeat();
                }
            }
            catch(Exception e)
            {
                throw new CommonException(e);
            }
        }
        catch(CommonException e)
        {
            LOGGER.error("doHeartBeat failed.the error is \r\n {}",
                    LogUtils.encodeForLog(e.getMessage()));
        }
    }

    /**
     * 线程休眠
     */
    private void doSleep()
    {
       try
       {
           sleep(CHECK_INTERVAL);
       }
       catch(InterruptedException e)
       {
           LOGGER.error("sleep failed.the error is \r\n {}",
                   LogUtils.encodeForLog(e.getMessage()));
       }
    }

    public static void begin()
    {
        if(null == task)
        {
            task = new SessionHeartBeatTask();
            task.isAlive = true;
            task.setName("t_wecc_serverstate");
            task.start();
        }
    }

    public static void end()
    {
        if(null != task)
        {
            task.isAlive = false;
        }
    }
}
