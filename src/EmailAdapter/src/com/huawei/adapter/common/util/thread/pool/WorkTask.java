/*
 * 文件名：WorkTask.java
 * 版权：Copyright 2009 Huawei Tech. Co. Ltd. All rights Reserved.
 * 描述：
 * 修改人：q57619
 * 修改时间：2009-2-13
 * 修改内容：新增
 */
package com.huawei.adapter.common.util.thread.pool;

/**
 * 线程处理任务接口.
 */
public interface WorkTask extends Runnable
{

    /**
     * 如果存在相同键值的任务则交由同一线程处理.
     * @return 任务键值
     */
    int getKey();

}
