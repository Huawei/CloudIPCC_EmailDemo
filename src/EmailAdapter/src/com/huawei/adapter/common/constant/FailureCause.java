package com.huawei.adapter.common.constant;

/**
 * 
 * <p>Title: 发送失败原因 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public enum FailureCause
{
    SUCCESS(0, "Success"),

    /**
     * 登录失败
     */
    LOGIN_FAILED(1, "login failed"),

    /**
     * 呼叫接口调用失败
     */
    CALL_CREATE_FAILED(2, "Call create-call interface failed"),

    /**
     * 发送接口调用失败
     */
    CALL_SEND_FAILED(3, "Call send interface failed"),

    /**
     * 座席未收到
     */
    AGENT_NOT_RECEIVER_FAILED(4, "Agent Not received"),

    /**
     * 呼叫结束
     */
    CALL_FINISHED(5, "Call has finished"),

    /**
     * 未登录
     */
    NOT_LOGIN(6, "Not login"),

    /**
     * 主备变换
     */
    TO_MASTER(7, "To master");

    private int cause;

    private String desc;

    private FailureCause(int cause, String desc)
    {
        this.cause = cause;
        this.desc = desc;
    }

    public String getDesc()
    {
        return desc;
    }

    public int getCause()
    {
        return cause;
    }

}
