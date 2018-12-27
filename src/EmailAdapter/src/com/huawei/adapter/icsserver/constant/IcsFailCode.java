package com.huawei.adapter.icsserver.constant;

/**
 * 
 * <p>Title: 失败原因码定义类 </p>
 * <p>Description:  
 *   本类定义了Icsgateway所提供的所有失败码, 失败码由三部分组成,
 *   模块编号，icsgateway以10开头
 *   错误码分类,100表示系统公共错误码,200表示准实时呼叫错误码,300表示非实时呼叫错误码，
 *   子错误码，各个分类下子错误码编号
 * </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: 华为技术有限公司</p>
 * @since
 */
public class IcsFailCode
{
    /** *********************************************
     *  系统公共错误原因码                                    *
     * **********************************************/
    
     /**
     * 平台未知异常
     */
    public static final String PLATFORM_UNKNOWN = "10-100-001";
    
    /**
     * 服务端EVENT_METHOD配置错误
     */
    public static final String INVALID_EVENT_METHOD = "10-100-002";
    
    /**
     * was 服务未启动
     */
    public static final String WAS_PROVIDER_NOT_AVAILABLE = "10-100-003";
    
    /**
     * webMAnyService 服务未启动
     */
    public static final String WAS_WEBMANYSERVICE_NOT_AVAILABLE = "10-100-004";
    
    /**
     * 无权限调用接口
     */
    public static final String WECC_REST_NORIGHT = "10-100-005";
    
    /**
     * 用户未登陆
     */
    public static final String WECC_USER_NOT_LOGIN = "10-100-006";
    
    /**
     * 用户请求参数为空或不合法
     */
    public static final String WECC_INVALID_PARA = "10-100-007";

    /**
     * 用户已登陆
     */
    public static final String WECC_USER_HAS_LOGIN = "10-100-008";

    /**
     * ResourceUnavailableException
     */
    public static final String WECC_PLAT_RESOURCEUNAVAILABLE = "10-100-009";
    
    /**
     * MethodNotSupportedException
     */
    public static final String WECC_PLAT_METHODNOTSUPPORT = "10-100-010";
    
    /**
     * InvalidStateException
     */
    public static final String WECC_PLAT_INVALIDSTATE = "10-100-011";
    
    
    /**
     * 用户的webmserver为null
     */
    public static final String WECC_WEBMSERVICE_ISNULL = "10-100-012";
    
    /**
     * vdnId不存在
     */
    public static final String WECC_VDN_NOTEXIST = "10-100-013";

    
    /**
     * 接入码不存在
     */
    public static final String WECC_ACCESSCODE_NOTEXIST = "10-100-014";
    
    
    /** *********************************************
     *  准实时呼叫类错误原因码                                    *
     * **********************************************/

    
    /**
     * 发起呼叫时，已经有该接入码对应的呼叫
     */
    public static final String WECC_REALTIME_CALLISEXIST = "10-200-001";

    
    /**
     * 超过用户最大呼叫数
     */
    public static final String WECC_REALTIME_OVERMAXCALL = "10-200-002";
    
    /**
     * 呼叫不存在
     */
    public static final String WECC_REALTIME_CALLISNOTEXIST = "10-200-003";
    
    
    /**
     * 编码转换失败
     */
    public static final String WECC_REALTIME_ENCODINGFAILED = "10-200-004";
    
    /**
     * 座席不存在
     */
    public static final String WECC_REALTIME_AGENTISNOTEXIST = "10-200-005";
    
    
    /**
     * 呼叫不是排队态
     */
    public static final String WECC_REALTIME_CALLISNOTQUEUE = "10-200-006";
}
