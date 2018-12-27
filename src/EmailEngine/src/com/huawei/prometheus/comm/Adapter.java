package com.huawei.prometheus.comm;

/**
 * <p>Title: Adapter </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Huawei</p>
 */
public interface Adapter
{
    /**
     * 初始化
     * @return 是否成功
     */
    boolean init();
    
    /**
     * 启动线程
     * @return 是否成功
     */
    boolean start();
    
    /**
     * 停止线程
     * @return 是否成功
     */
    boolean stop();
}
