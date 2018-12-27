
package com.huawei.prometheus.comm.bean.email;

import java.util.Date;
import java.util.List;

import com.huawei.prometheus.comm.bean.base.DBMessage;

/**
 * <p>Title:  邮件内容 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 * @since
 */
public class EmailMessage extends DBMessage
{
    
	 /**
     * 邮件正文
     */
    public static final String CONTENT_TYPE_EMAIL_FILE = "EMAIL_FILE";
    
    /**
     * 邮件附件
     */
    public static final String CONTENT_TYPE_EMAIL_ATTACH = "EMAIL_ATTACH";
    
    private String uid;

    /**
     * 主题
     */
    private String subject;
    
    private Date sendDate;
    
    private String textContent;
    
    private String htmlContent;
    
    /**
     * 邮件抄送人员,如果是多人，以','间隔
     */
    private String cc;
    
    /**
     * 邮件密送人员，如果是多人，以','间隔
     */
    private String bcc;
    
    
    //
    private List<AttachFile> attchments;
    
    private String from;
    
    private String to;
    
    
    /**
     * ics收到邮件的时间点，不等于sendDate
     */
    private Date startTime;
    
    
    /**
     * 从邮件服务器取下来的时间
     */
    private Date receiveDate;
    
    
    /**
     * 收件箱
     */
    private String receiveEmail;
    
    
    /**
     * toString 方法
     * @return String值
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("EmailMessage ").append(super.toString())
                .append(", uid =")
                .append(uid)
                .append(", from = ")
                .append(from)
                .append(", to = ")
                .append(to)
                .append(", subject = ")
                .append(subject)
                .append(" }");
        return sb.toString();
    }
    
    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

   

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public Date getSendDate()
    {
        return sendDate == null ? null : new Date(sendDate.getTime());
    }

    public void setSendDate(Date sendDate)
    {
        this.sendDate = sendDate == null ? null : new Date(sendDate.getTime());
    }

    public String getTextContent()
    {
        return textContent;
    }

    public void setTextContent(String textContent)
    {
        this.textContent = textContent;
    }

    public String getHtmlContent()
    {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent)
    {
        this.htmlContent = htmlContent;
    }

    public List<AttachFile> getAttchments()
    {
        return attchments;
    }

    public void setAttchments(List<AttachFile> attchments)
    {
        this.attchments = attchments;
    }

    public String getCc()
    {
        return cc;
    }

    public void setCc(String cc)
    {
        this.cc = cc;
    }

    public String getBcc()
    {
        return bcc;
    }

    public void setBcc(String bcc)
    {
        this.bcc = bcc;
    }

    public Date getStartTime()
    {
        return startTime == null ? null : new Date(startTime.getTime());
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime == null ? null : new Date(startTime.getTime());
    }

  
    public Date getReceiveDate()
    {
        return receiveDate == null ? null : new Date(receiveDate.getTime());
    }

    public void setReceiveDate(Date receiveDate)
    {
        this.receiveDate = receiveDate == null ? null : new Date(receiveDate.getTime());
    }


    public String getReceiveEmail()
    {
        return receiveEmail;
    }

    public void setReceiveEmail(String receiveEmail)
    {
        this.receiveEmail = receiveEmail;
    }
    
    
    
}
