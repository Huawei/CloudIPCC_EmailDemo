package com.huawei.prometheus.comm.bean.email;



/**
 * <p>Title:PrometheusEmailConfig Bean </p>
 * <p>Description:配置的邮件信息bean </p>
 * @version
 */
public class PrometheusEmailConfig
{
    
    /**
     * 邮箱用户名，不能为空
     */
    private String userName;
    

    /**
     * 邮箱地址
     */
    private String address;
    
    /**
     * 邮箱密码
     */
    private String mailPWD;
    
    /**
     * 接收类型
     * IMAP 或者 Pop3
     */
    private String receiveType;
    
    /**
     * 收邮件服务器地址，支持IP或者域名
     */
    private String receiveServer;
    
    /**
     * 收取邮件端口，默认110
     */
    private String receivePort; 
    
    /**
     * 接收邮件是否启用SSL加密（1：是，0：否）默认0
     */
    private Integer isSSLReceive;


    /**
     * 发送邮件服务器地址，支持IP或者域名
     */
    private String sendServer;
    
    /**
     * 发送邮件端口，默认25
     */
    private String sendPort;
    
    /**
     * 发送邮件是否启用SSL加密（1：是，0：否）默认0
     */
    private Integer isSSLSend;
    
    /**
     * 所属VDNID，Gaea VDNID编号，非WAS VDNID
     */
    private Integer vdnId;
    
    /**
     * 所属于vdnName,需要从t_gaea_vdn表联合查询得到
     */
    private String vdnName;

    /**
     * 接入码，WAS呼叫接入码
     */
    private String serviceNo;
    
    /**
     * 收发邮件后是否从服务器删除原始邮件（1：是，0：否）默认0
     */
    private Integer isDelEmail;
    
    /**
     * 登录邮箱是否启用鉴权（1：是，0：否）默认1
     */
    private Integer isAuth;
    
    /**
     * 是否支持 NTLM(1: 支持, 0:不支持)
     */
    private Integer enableNTLM;
 
    
    /**
     * ToString Method
     * 
     * @return EmailConfig emailConfig
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer(super.toString());
        sb.append("PrometheusEmailConfig{ id=")
                .append(", userName=")
                .append(userName)
                .append(", address=")
                .append(address)
                .append(", password=********")
                .append(", receiveType=")
                .append(receiveType)
                .append(", receiveServer=")
                .append(receiveServer)
                .append(", receivePort=")
                .append(receivePort)
                .append(", isSSLRecive=")
                .append(isSSLReceive)
                .append(", sendServer=")
                .append(sendServer)
                .append(", sendPort=")
                .append(sendPort)
                .append(", isSSLSend=")
                .append(isSSLSend)
                .append(", vdnId=")
                .append(vdnId)
                .append(", vdnName=")
                .append(vdnName)
                .append(", serviceNo=")
                .append(serviceNo)
                .append(", isDelEmail=")
                .append(isDelEmail)
                .append(", isAuth=")
                .append(isAuth)
                .append(" }");
        return sb.toString();
    }
    
   
    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getMailPWD() {
		return mailPWD;
	}


	public void setMailPWD(String mailPWD) {
		this.mailPWD = mailPWD;
	}


	public String getReceiveType()
    {
        return receiveType;
    }

    public void setReceiveType(String receiveType)
    {
        this.receiveType = receiveType;
    }

    public String getReceiveServer()
    {
        return receiveServer;
    }

    public void setReceiveServer(String receiveServer)
    {
        this.receiveServer = receiveServer;
    }

    public String getReceivePort()
    {
        return receivePort;
    }

    public void setReceivePort(String receivePort)
    {
        this.receivePort = receivePort;
    }
    
    public Integer getIsSSLReceive()
    {
        return isSSLReceive;
    }


    public void setIsSSLReceive(Integer isSSLReceive)
    {
        this.isSSLReceive = isSSLReceive;
    }
    
    public String getSendServer()
    {
        return sendServer;
    }

    public void setSendServer(String sendServer)
    {
        this.sendServer = sendServer;
    }

    public String getSendPort()
    {
        return sendPort;
    }

    public void setSendPort(String sendPort)
    {
        this.sendPort = sendPort;
    }

    public Integer getIsSSLSend()
    {
        return isSSLSend;
    }

    public void setIsSSLSend(Integer isSSLSend)
    {
        this.isSSLSend = isSSLSend;
    }

    public Integer getVdnId()
    {
        return vdnId;
    }

    public void setVdnId(Integer vdnId)
    {
        this.vdnId = vdnId;
    }
    
    public String getVdnName()
    {
        return vdnName;
    }



    public void setVdnName(String vdnName)
    {
        this.vdnName = vdnName;
    }

    public String getServiceNo()
    {
        return serviceNo;
    }

    public void setServiceNo(String serviceNo)
    {
        this.serviceNo = serviceNo;
    }

    public Integer getIsDelEmail()
    {
        return isDelEmail;
    }

    public void setIsDelEmail(Integer isDelEmail)
    {
        this.isDelEmail = isDelEmail;
    }

    public Integer getIsAuth()
    {
        return isAuth;
    }
    
    /**
     * Get the field enableNTLM value
     * @return Returns the enableNTLM.
     */
    public Integer getEnableNTLM()
    {
        return enableNTLM;
    }
    
    /**
     * Set the field enableNTLM value
     * @param enableNTLM The enableNTLM to set.
     */
    public void setEnableNTLM(Integer enableNTLM)
    {
        this.enableNTLM = enableNTLM;
    }
    
 
    
    public void setIsAuth(Integer isAuth)
    {
        this.isAuth = isAuth;
    }

   
}
