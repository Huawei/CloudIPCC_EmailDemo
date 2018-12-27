package com.huawei.adapter.common.constant;

/**
 * 
 * <p>Title: 常量类 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 */
public interface Constants
{
   

    // 媒体类型为文字交谈
    int MEDIA_TYPE = 1;


    // 最小获取用于发起呼叫的数据
    int MIN_FETCH_FOR_CREATE_CALL_COUNT = 10;

    // 最大获取用于发起呼叫的数据
    int MAX_FETCH_FOR_CREATE_CALL_COUNT = 50;

    // 默认获取用户发起呼叫的数据
    int DEFAULT_FETCH_FOR_CREATE_CALL_COUNT = 20;

    // 最小消息处理线程数
    int MIN_MESSAGE_PROCESSOR_THREADS = 10;

    // 最大消息处理线程数
    int MAX_MESSAGE_PROCESSOR_THREADS = 50;

    // 默认消息处理线程数
    int DEFAULT_MESSAGE_PROCESSOR_THREADS = 20;

    // 最小会话数
    int MIN_CHAT_SESSION = 50;

    // 最大会话数
    int MAX_CHAT_SESSION = 2000;

    // 主备机检查周期 30s
    int MASTRT_DETECT_INTERVAL = 30000;

    // 默认会话数
    int DEFAULT_CHAT_SESSION = 500;

    // 备机时从数据库获取待分发消息的间隔, 1分钟
    int SLAVE_DB_FETCH_INTERVAL = 60000;

    // 主机时从数据库获取待分发消息的间隔，1秒
    int MASTER_DB_FETCH_INTERVAL = 1000;

    // 最大删除消息数
    int MAX_BATCH_DELETE_IDS = 200;

    // 批量删除消息间隔, 1秒
    int CLEAN_INTERVAL = 1000;

    // 最大批量更新消息失败会话数
    int MAX_BATCH_UPDATE_FAILED = 50;
    
    // 最大批量更新短信建立会话数
    int MAX_BATCH_UPDATE_CONNECT = 50;

    // 批量删除消息会话间隔, 1秒
    int SESSION_CLEAN_INTERVAL = 1000;

    // 最大批量删除消息会话数
    int MAX_BATCH_DELETE_SESSIONS = 100;

    // 批量删除消息会话失败时间隔, 60秒
    long SESSION_CLEAN_FAILED_INTERVAL = 60000;

    // 最小ICS事件处理线程
    int MIN_ICSEVENT_PROCESSOR_THREADS = 10;

    // 最大ICS事件处理线程
    int MAX_ICSEVENT_PROCESSOR_THREADS = 100;

    // 默认ICS事件处理线程
    int DEFAULT_ICSEVENT_PROCESSOR_THREADS = 20;

    // 统计信息更新周期,5s
    long STATISTIC_UPDATE_INTERVAL = 5000;

    String PRINT_USER = "VdnId = {}, UserName = {}:";

    /**
     * UTF-8编码
     */
    String UTF_8 = "UTF-8";

    /**
     * 最小ICS事件处理线程
     */
    int MIN_ICSCHAT_EVENT_THREADS = 10;

    /**
     * 默认最小ICS事件处理线程
     */
    int DEFAULT_MAX_ICSCHAT_EVENT_THREADS = 50;

    /**
     * 数据库类型为sqlserver
     */
    String DB_TYPE_SQLSERVER = "sqlserver";

    /**
     * 数据库类型为oracle
     */
    String DB_TYPE_ORACLE = "oracle";

    /**
     * 心跳发送失败次数
     */
    int HEARTBEAT_FAILED = 3;

    int DB_MESSAGE_FAILED = 4;
    
    int DB_MESSAGE_SUCCESS = 3;
}
