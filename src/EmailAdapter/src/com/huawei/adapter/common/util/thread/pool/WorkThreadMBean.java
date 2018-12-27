package com.huawei.adapter.common.util.thread.pool;

/**
 * 
 * <p>Title: 获取工作线程的统计信息 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public interface WorkThreadMBean
{

    /**
     * 获取队列中等待处理的任务数.
     * @return 队列中等待处理的任务数
     */
    int getQueueSize();

    /**
     * 获取已经处理完成的任务数.
     * @return 已经处理完成的任务数
     */
    int getFinishedTask();

    /**
     * 获取关联的键的数量.
     * @return 关联的键的数量
     * @since 3.6C10
     */
    int getReferenceKeySize();

}
