
package com.huawei.prometheus.adapter.email.data;


/**
 * <p>Title:  邮件帐户</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 * @since
 */
public class EmailAccount
{
    private String userName;

    private String mailPWD;
    
    /**
     * 系统接入码
     */
    private String serviceCode;

    private String receiverServer;
    
    private int receiverPort;
    
    /**
     * 接收服务器要求SSL连接
     */
    private boolean receiveNeedSSL;
    
    private String sendServer;
    
    private int sendPort;
    
    /**
     * 发送服务器要求SSL连接
     */
    private boolean sendNeedSSL;
    
    
    /**
     * 邮件接收方式，包括pop3或者imap
     */
    private String receiverType;
    
    /**
     * 是否需要服务器身份认证
     */
    private boolean needAuth = false;
    
    /**
     * 是否支持 NTLM
     */
    private boolean enableNTLM = false;

    
    /**
     * 邮箱地址
     */
    private String emailAddress = "";
    
    /**
     * 是否从服务器上删除邮件
     */
    private boolean deleteMessageOnServer = false;
    
    
    /**
     *邮箱对应的vdnid
     */
    private String vndId;  
    
    /**
     * 构造函数
     * @param userName 账户名
     * @param address 邮箱地址
     * @param mailPWD 密码
     */
    public EmailAccount(String userName, String address, String mailPWD)
    {
        this.userName = userName;
        this.emailAddress = address;
        this.mailPWD = mailPWD;
    }
    
    public EmailAccount()
    {
        
    }
    
    public String getVndId()
    {
        return vndId;
    }

    public void setVndId(String vndId)
    {
        this.vndId = vndId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getMailPWD() {
		return mailPWD;
	}

	public void setMailPWD(String mailPWD) {
		this.mailPWD = mailPWD;
	}

	public String getServiceCode()
    {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode)
    {
        this.serviceCode = serviceCode;
    }

    public String getReceiverServer()
    {
        return receiverServer;
    }

    public void setReceiverServer(String receiverServer)
    {
        this.receiverServer = receiverServer;
    }

    public String getSendServer()
    {
        return sendServer;
    }

    public void setSendServer(String sendServer)
    {
        this.sendServer = sendServer;
    }

    public int getReceiverPort()
    {
        return receiverPort;
    }

    public void setReceiverPort(int receiverPort)
    {
        this.receiverPort = receiverPort;
    }

    public int getSendPort()
    {
        return sendPort;
    }

    public void setSendPort(int sendPort)
    {
        this.sendPort = sendPort;
    }
    
    public void setReceiverType(String receiverType)
    {
        this.receiverType = receiverType;
    }

    public String getReceiverType()
    {
        return receiverType;
    }

    public boolean isReceiveNeedSSL()
    {
        return receiveNeedSSL;
    }

    public void setReceiveNeedSSL(boolean receiveNeedSSL)
    {
        this.receiveNeedSSL = receiveNeedSSL;
    }

    public boolean isSendNeedSSL()
    {
        return sendNeedSSL;
    }

    public void setSendNeedSSL(boolean sendNeedSSL)
    {
        this.sendNeedSSL = sendNeedSSL;
    }

    public boolean isNeedAuth()
    {
        return needAuth;
    }

    public void setNeedAuth(boolean needAuth)
    {
        this.needAuth = needAuth;
    }
    
    /**
     * Get the field enableNTLM value
     * @return Returns the enableNTLM.
     */
    public boolean isEnableNTLM()
    {
        return enableNTLM;
    }
    
    /**
     * Set the field enableNTLM value
     * @param enableNTLM The enableNTLM to set.
     */
    public void setEnableNTLM(boolean enableNTLM)
    {
        this.enableNTLM = enableNTLM;
    }
    
  


    public String getEmailAddress()
    {
        return emailAddress;
    }



    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public boolean isDeleteMessageOnServer()
    {
        return deleteMessageOnServer;
    }

    public void setDeleteMessageOnServer(boolean deleteMessageOnServer)
    {
        this.deleteMessageOnServer = deleteMessageOnServer;
    }

    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("userName:").append(userName).append(",");
        sb.append("serviceCode:").append(serviceCode).append(",");
        sb.append("receiverServer:").append(receiverServer).append(",");
        sb.append("receiverPort:").append(receiverPort).append(",");
        sb.append("receiveNeedSSL:").append(receiveNeedSSL).append(",");
        sb.append("receiverPort:").append(receiverPort).append(",");
        sb.append("sendServer:").append(sendServer).append(",");
        sb.append("sendPort:").append(sendPort).append(",");
        sb.append("sendNeedSSL:").append(sendNeedSSL).append(",");
        sb.append("receiverType:").append(receiverType).append(",");
        sb.append("needAuth:").append(needAuth).append(",");
        sb.append("enableNTLM:").append(enableNTLM).append(",");
        sb.append("emailAddress:").append(emailAddress).append(",");
        sb.append("deleteMessageOnServer:").append(deleteMessageOnServer).append("}");
     
        return sb.toString();
    }
}
