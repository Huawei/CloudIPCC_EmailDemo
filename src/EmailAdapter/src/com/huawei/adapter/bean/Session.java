package com.huawei.adapter.bean;

/**
 * 
 * <p>Title: 会话  </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class Session
{
    /**
     * vdnId
     */
    private int vdnId;

    /**
     * 设备号
     */
    private String sessionUserName;

    /**
     * 接入码
     */
    private String accessCode;

    /**
     * 集群名
     */
    private String clusterId;

    public Session(Message message)
    {
        this.vdnId = message.getVdnId();
        this.sessionUserName = message.getSessionUserName();
        this.accessCode = message.getAccessCode();
    }

    public Session(int vdnId, String sessionUserName, String accessCode)
    {
        this.vdnId = vdnId;
        this.sessionUserName = sessionUserName;
        this.accessCode = accessCode;
    }

    public int getVdnId()
    {
        return vdnId;
    }

    public void setVdnId(int vdnId)
    {
        this.vdnId = vdnId;
    }

    

    

    public String getSessionUserName()
    {
        return sessionUserName;
    }

    public void setSessionUserName(String sessionUserName)
    {
        this.sessionUserName = sessionUserName;
    }

    public String getAccessCode()
    {
        return accessCode;
    }

    public void setAccessCode(String accessCode)
    {
        this.accessCode = accessCode;
    }

    public String getClusterId()
    {
        return clusterId;
    }

    public void setClusterId(String clusterId)
    {
        this.clusterId = clusterId;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("{").append("vdnId:").append(vdnId).append(",");
        sb.append("sessionUserName:").append(sessionUserName).append(",");
        sb.append("accessCode:").append(accessCode).append("}");
        return sb.toString();
    }
}
