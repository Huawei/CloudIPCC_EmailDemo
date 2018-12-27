
package com.huawei.adapter.icsevent;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.ICSEvent;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.util.thread.pool.ThreadPool;

/**
 * 
 * <p>Title:</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class ICSEventPollTask extends Thread
{
    private static final Logger LOG = LoggerFactory.getLogger(ICSEventPollTask.class);

    
    /**
     * 没有事件时暂停200ms
     */
    private static ICSEventPollTask task = null;



    private ThreadPool eventHandleThreadPool;
    private ICSEventPollTask(int maxThreads)
    {
        eventHandleThreadPool = new ThreadPool("ics-event-handle", Constants.MIN_ICSCHAT_EVENT_THREADS, maxThreads, 0);
    }

    public static void addTask(ICSEvent icsEvent)
    {
        if (null != task)
        {
            task.eventHandleThreadPool.addTask(new ICSEventHandleThread(icsEvent));
        }
        else
        {
            LOG.error("ICSEventPollTask dose not init task ");
        }
    }
   
    
    
    public static void init(int maxThreads)
    {
        if (null != task)
        {
            return;
        }
        task = new ICSEventPollTask(maxThreads);
    }
}
