package com.huawei.adapter.icsserver.bean;




/**
 * 
 * <p>Title: 准实时呼叫发起参数 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class CallInfoParam
{
    /**
     * 媒体类型
     */
    private int mediaType;
    
    /**
     * 主叫
     */
    private String caller;
    
    /**
     * 接入码
     */
    private String accessCode;
    
    /**
     * 外呼数据 回呼时传入回呼号码
     */
    private String callData;
    

    private long uvid = -1;

    /**
     * 验证码
     */
    private String verifyCode;
    
    /**
     * 实际主叫
     */
    private String realCaller;
    
    /**
     * 显示名
     */
    private String displayName;

    
    /**
     * 用户信息
     */
    private String userInfo;
    
    /**
     * 返回媒体类型
     * @return 媒体类型
     */
    public int getMediaType()
    {
        return mediaType;
    }
    
    /**
     * 设置媒体类型
     * @param mediaType 媒体类型
     */
    public void setMediaType(int mediaType)
    {
        this.mediaType = mediaType;
    }
    
    /**
     * 返回呼叫者
     * @return 呼叫者
     */
    public String getCaller()
    {
        return caller;
    }
    
    /**
     * 设置呼叫者
     * @param caller 呼叫者
     */
    public void setCaller(String caller)
    {
        this.caller = caller;
    }
    
    /**
     * 返回接入码
     * @return 接入码
     */
    public String getAccessCode()
    {
        return accessCode;
    }
    
    /**
     * 设置接入码
     * @param accessCode 接入码
     */
    public void setAccessCode(String accessCode)
    {
        this.accessCode = accessCode;
    }
    
    /**
     * 返回随路数据
     * @return 随路数据
     */
    public String getCallData()
    {
        return callData;
    }
    
    /**
     * 设置随路数据
     * @param callData 随路数据
     */
    public void setCallData(String callData)
    {
        this.callData = callData;
    }

    
    
    public long getUvid()
	{
		return uvid;
	}

	public void setUvid(long uvid)
	{
		this.uvid = uvid;
	}
	
	

	public String getVerifyCode()
    {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode)
    {
        this.verifyCode = verifyCode;
    }

    public String getRealCaller()
    {
        return realCaller;
    }

    public void setRealCaller(String realCaller)
    {
        this.realCaller = realCaller;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo(String userInfo)
    {
        this.userInfo = userInfo;
    }

    /**
     * bean toString 方法
     * @return bean字符串
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("CallInfoParam [mediaType=");
        builder.append(mediaType);
        builder.append(", caller=");
        builder.append(caller);
        builder.append(", accessCode=");
        builder.append(accessCode);
        builder.append(", callData=");
        builder.append("******");
        builder.append(", uvid=");
        builder.append(uvid);
        builder.append("]");
        return builder.toString();
    }
    
    
}
