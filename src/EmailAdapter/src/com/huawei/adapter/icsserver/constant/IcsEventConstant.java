package com.huawei.adapter.icsserver.constant;


/**
 * 
 * <p>Title: WECC对外事件常量类 </p>
 * <p>Description:WECC对外事件常量类</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: 华为技术有限公司</p>
 * @since
 */
public class IcsEventConstant
{
	 
/****************************WeccWebmServiceProviderListener事件****************************/
    
    /**
     * webmServiceProviderInService.用户签入成功
     */
    public static final String WECC_PROVIDER_IN_SERVER = "WECC_PROVIDER_IN_SERVER";
    
    /**
     * webmAnyServiceProviderShutDownService.用户被签出
     */
    public static final String WECC_PROVIDER_SHUTDOWN = "WECC_PROVIDER_SHUTDOWN";
    

    /**
     * 座席直发事件
     */
    public static final String WECC_MESSAGE_WITHOUTCALL_EVENT = "WECC_MESSAGE_WITHOUTCALL_EVENT";
    
    /****************************WeccWebCallListener事件****************************/
    /**
     * webCallConnected.平台呼叫建立成功
     */
    public static final String WECC_WEBM_CALL_CONNECTED = "WECC_WEBM_CALL_CONNECTED";
    
    /**
     * webCallConnected平台呼叫断连
     */
    public static final String WECC_WEBM_CALL_DISCONNECTED = "WECC_WEBM_CALL_DISCONNECTED";
    
    /**
     * webCallQueuing平台呼叫排队等待
     */
    public static final String WECC_WEBM_CALL_QUEUING = "WECC_WEBM_CALL_QUEUING";
    
    
    /**
     * webCallBaulk排队超时
     */
    public static final String WECC_WEBM_QUEUE_TIMEOUT = "WECC_WEBM_QUEUE_TIMEOUT";
    
    
    /**
     * webCallBaulk用户取消排队
     */
    public static final String WECC_WEBM_CANCEL_QUEUE = "WECC_WEBM_CANCEL_QUEUE";
    
    /**
     * webCallBaulk平台呼叫失败
     */
    public static final String WECC_WEBM_CALL_FAIL = "WECC_WEBM_CALL_FAIL";
    
    
    /**
     * webCallTransferEnd平台呼叫转移
     */
    public static final String WECC_WEBM_CALL_TRANSFER = "WECC_WEBM_CALL_TRANSFER";
    

    
    
    /****************************WeccChatCallEvent事件****************************/

    /**
     * chatCallPostDataSucc成功发送消息
     */
    public static final String WECC_CHAT_POSTDATA_SUCC = "WECC_CHAT_POSTDATA_SUCC";

    /**
     * chatCallPostDataFailed发送消息失败
     */
    public static final String WECC_CHAT_POSTDATA_FAIL = "WECC_CHAT_POSTDATA_FAIL";
    
    /**
     * chatCallReceiveData成功接受消息
     */
    public static final String WECC_CHAT_RECEIVEDATA = "WECC_CHAT_RECEIVEDATA";
    

    
    

    
}
