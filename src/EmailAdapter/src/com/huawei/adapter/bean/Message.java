package com.huawei.adapter.bean;

import java.util.Date;





/**
 * 
 * <p>Title: 待分发的消息实体类 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>

 * @since
 */
public class Message
{
    public Message()
    {
    }

    
    private long id;
    
    private String accessCode;

    private String sessionUserName;


    /**
     *  用户配置参数。对接呼叫中心
     */
    private int vdnId;

    /**
     *  是否需要创建session
     */
    private int isCreateSession;

    /**
     *  发送时间
     */
    private Date sendDate;
    
    private String realCaller;
    
    private String displayName;
    
    private String callData;
    


    public Date getSendDate()
    {
        return sendDate != null ? (Date) sendDate.clone() : null;

    }

    public void setSendDate(Date sendDate)
    {
        this.sendDate = (sendDate != null ? (Date) sendDate.clone() : null);
    }

    public int getVdnId()
    {
        return vdnId;
    }

    public void setVdnId(int vdnId)
    {
        this.vdnId = vdnId;
    }


    public String getAccessCode()
    {
        return accessCode;
    }

    public void setAccessCode(String accessCode)
    {
        this.accessCode = accessCode;
    }

    public int getIsCreateSession()
    {
        return isCreateSession;
    }

    public void setIsCreateSession(int isCreateSession)
    {
        this.isCreateSession = isCreateSession;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }


    public String getSessionUserName()
    {
        return sessionUserName;
    }

    public void setSessionUserName(String sessionUserName)
    {
        this.sessionUserName = sessionUserName;
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

    public String getCallData()
    {
        return callData;
    }

    public void setCallData(String callData)
    {
        this.callData = callData;
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("{").append("id:").append(id).append(",");
        sb.append("realCaller:").append(realCaller).append(",");
        sb.append("accessCode:").append(accessCode).append("}");
        return sb.toString();
    }
}
