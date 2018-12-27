
package com.huawei.prometheus.adapter.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 邮件身份认证
 * <p>Title:  </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 * @since
 */
public class MailAuth extends Authenticator
{
    private String userName;
    
    private String password;
    
    /**
     * 构造函数
     * @param userName 用户名
     * @param passwd 密码
     */
    public MailAuth(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }
    
    public PasswordAuthentication getPasswordAuthentication() 
    {
        return new PasswordAuthentication(userName, password);  
    } 
}
