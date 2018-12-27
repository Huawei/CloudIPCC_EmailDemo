
package com.huawei.adapter.common.util.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * <p>Title:安全日志记录工具  </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 * @since
 */
public abstract class SecurityLogUtil
{
    /**
     * 成功
     */
    public static final String SUCCESS = "success";
    
    /**
     * 失败
     */
    public static final String FAIL = "fail";
    
    /**
     * 日志
     */
    private static Logger log = LoggerFactory.getLogger(SecurityLogUtil.class);
    
    /**
     * 新增安全日志信息
     * @param userId 用户ID(包括关联终端、端口、网络地址或通信设备）
     * @param eventType 事件类型
     * @param resourceName 被访问的资源名称
     * @param eventResult 事件的结果
     */
    public static void createSecurityLog(String userId, String eventType, String resourceName, String eventResult)
    {
        userId = userId == null ? "" : userId;
        eventType = eventType == null ? "" : eventType;
        resourceName = resourceName == null ? "" : resourceName;
        
        log.info(LogUtils.encodeForLog(userId + " | " + eventType + " | " + resourceName + " | " + eventResult));
    }
}
