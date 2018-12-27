package com.huawei.prometheus.adapter.email;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.UIDFolder;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.prometheus.adapter.email.data.EmailAccount;
import com.huawei.prometheus.adapter.email.data.EmailData;
import com.huawei.prometheus.adapter.email.data.EmailParser;
import com.huawei.prometheus.comm.ConfigList;
import com.huawei.prometheus.comm.ConfigProperties;
import com.huawei.prometheus.comm.LogUtils;
import com.huawei.prometheus.comm.StringUtils;
import com.huawei.prometheus.comm.bean.base.DBMessage;
import com.huawei.prometheus.comm.bean.email.AttachFile;
import com.huawei.prometheus.comm.bean.email.EmailMessage;
import com.huawei.prometheus.comm.exception.CommonException;
import com.huawei.prometheus.dao.service.EmailMessageDBService;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.util.MailSSLSocketFactory;

/**
 * 
 * <p>Title:  </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 * @since
 */
public class EmailConnectionManager implements Runnable
{
    private static Logger log = LoggerFactory.getLogger(EmailConnectionManager.class);
    
    private static final int ONESECOND = 1000;
    
    
    private static final String STORE_CONNECTION_TIMEOUT = "300000";
    
    
    private static final int EMAIL_SUBJECT_MAX_SIZE = 500;
        
    private volatile boolean runFlag = false;
    
    private String vdnId;


    private int interval = Integer.valueOf(ConfigProperties.getKey(ConfigList.EMAIL,
            "EMAIL_QUERY_INTERVAL"))
            * ONESECOND;
    
    
    private static final String EMAIL_TO = "to";
    
    private static final String EMAIL_CC = "cc";
    
    private static final String EMAIL_BCC = "bcc";
    
    
    private EmailMessageDBService dbService;

    /**
     * 构造函数
     */
    public EmailConnectionManager(String vdnId)
    {
       
        runFlag = true;
        this.vdnId = vdnId;
        dbService = new EmailMessageDBService();
    }
    
    /**
     *  (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
    	
        
        while (runFlag)
        {
            if(EmailData.ALLACCOUNTS_MAP.get(vdnId) != null)
            {
            	try
                {
	            	
	                for (EmailAccount e : EmailData.ALLACCOUNTS_MAP.get(vdnId))
	                {
                        if (!StringUtils.isNullOrEmpty(e.getReceiverServer()))
                        {
                            //收邮件
                            receive(e);
                        }
                        if (!StringUtils.isNullOrEmpty(e.getSendServer()))
                        {
                            //发邮件
                            send(e, false);
                        }
	                }
                }
                catch (Exception ee)
                {
                    log.error("Email connection manager has a exception, error message is {}", LogUtils.encodeForLog(ee.getMessage()));
                }
            }
            
            try
            {
                Thread.sleep(interval);
            }
            catch (Exception e)
            {
                log.error("Email connection manager sleep fail, error message is {}.",
                  LogUtils.encodeForLog(e.getMessage()));
            }
        }
    }
    
    /**
     * 接收邮件
     * @param account email账号类
     */
    private void receive(EmailAccount account)
    {
        log.debug("Begin receive email.({}).", LogUtils.formatEmail(account.getUserName()));
        Store store = null;
        Folder folder = null;
        try
        {
            store = getConnectedStore(account);
            folder = getFolder(account, store);
            Message[] msgs = getMessagesWithUid(folder);

            log.debug("Begin deal emails.({}).", LogUtils.formatEmail(account.getUserName()));
            int length = msgs.length;
            for (int i = 0; i < length; i++)
            {
                EmailMessage em = null;
                log.debug("Begin deal the NO. " + i 
                        + " of total "
                        + length
                        + " emails.({}).", LogUtils.formatEmail(account.getUserName()));
                try
                {
                    em = receiveOneMail(account, folder, msgs[i]);
                    if (em == null)
                    {
                        continue;
                    }

                    log.debug("End deal one email.({}).", LogUtils.formatEmail(account.getUserName()));
                }
                catch (MessagingException e)
                {
                    log.error("Receive one email fail with exception ({}, {})", LogUtils.formatEmail(account.getUserName()), LogUtils.encodeForLog(e));
                }
                catch (IOException e)
                {
                    log.error("Receive one email fail with exception ({}, {})", LogUtils.formatEmail(account.getUserName()), LogUtils.encodeForLog(e));
                }
            }
            log.debug("End deal emails.({}).", LogUtils.formatEmail(account.getUserName()));
        }
        catch (MessagingException e)
        {
            log.error("Receive Email fail with a MessagingException.\r\n({}, {})",
            		LogUtils.formatEmail(account.getUserName()), LogUtils.encodeForLog(e));
        }
        catch (GeneralSecurityException e)
        {
            log.error("Receive Email fail with a GeneralSecurityException.\r\n({}, {})",
            		LogUtils.formatEmail(account.getUserName()), LogUtils.encodeForLog(e));
        }
        finally
        {
            try
            {
                if (folder != null)
                {
                    folder.close(true);
                }
            }
            catch (MessagingException e)
            {
                log.error("Email's receive create_comment response failed with a error.\r\n ({}, {})",
                		LogUtils.formatEmail(account.getUserName()), LogUtils.encodeForLog(e));
            }
            try
            {
                if (store != null)
                {
                    store.close();
                }
            }
            catch (MessagingException e)
            {
                log.error(LogUtils.encodeForLog(e));
            }
        }
        log.debug("End receive email.({}).", LogUtils.formatEmail(account.getUserName()));
    }
    

    
    
    /**
     * 处理一封邮件
     * @param account 邮件帐户
     * @param folder 文件夹
     * @param msg 邮件消息
     * @return 数据库里存储的邮件对象
     * @throws MessagingException 邮件异常
     * @throws IOException IO异常
     */
    private EmailMessage receiveOneMail(EmailAccount account, Folder folder, Message msg) 
            throws MessagingException, IOException
    {
        EmailMessage em = new EmailMessage();
        String uid = getUid(account, folder, msg);
        log.debug("Begin parse emails.({}).", LogUtils.formatEmail(account.getUserName()));
        MimeMessage mm = (MimeMessage)msg;
        
        
        setBasicEmailInfo(account, uid, mm, em);
        
        
        String mailFileId = saveEmailToDB(mm, uid, account);
           
        if (StringUtils.isNullOrEmpty(mailFileId))
        {
            log.error("Save email to db fail.({})", LogUtils.formatEmail(account.getUserName()));
            return null;
        }
      
     
        try
        {
            parseEmail(em, mailFileId);
        }
        catch (Exception e)
        {
            log.error("Parse email with mailFileId" 
                    + mailFileId 
                    + " failed with a error. " 
                    + "This mail will not been received again unless restart the server. \r\n({}, {})",
                    LogUtils.formatEmail(account.getUserName()), e);
            return null;
        }
     
        em.setStartTime(new Date());
      
       
        if (dbService.createMessage(em) == 0)
        {
            log.error("save to email message failed with mailFileId" 
                    + mailFileId 
                    + ". This mail will not been received again unless restart the server.",
                    LogUtils.formatEmail(account.getUserName()));
            return null;
        }

        deleteMessageOnServer(account, msg);
  
        return em;
    }
    
  
  

    /**
     * 删除服务器上的邮件
     * @param account 邮件帐户
     * @param msg 邮件消息
     * @throws MessagingException 邮件异常
     */
    private void deleteMessageOnServer(EmailAccount account, Message msg) 
        throws MessagingException
    {
        
        msg.setFlag(Flags.Flag.DELETED, true);
       
    }

  
    private void parseEmail(EmailMessage em, String mailFileId)
        throws IOException
    {
        EmailParser ep = new EmailParser(mailFileId);
        ep.parse();
        em.setContent(mailFileId);
        em.setHtmlContent(formatScript(ep.getHtmlBody()));
        em.setTextContent(ep.getTextBody());
        em.setAttchments(ep.saveAttachFiles());
    }

    private String saveEmailToDB(MimeMessage mm, String uid, EmailAccount account)
            throws IOException, MessagingException
    {
        String mailFileId = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();;
        try
        {
            try
            {
                mm.writeTo(os);
                // 保存邮件
                log.debug("Begin save email from {} to db.",  LogUtils.formatEmail(account.getUserName()));
                
                
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("content", os.toByteArray());
                m.put("fileName", uid + ".eml");
                m.put("tableName", "T_WECC_FILE_EMAIL");
                m.put("collectionName", "email");
                
                dbService.saveFile(m);
                os.close();
                if (m.get("id") != null)
                {
                    mailFileId = m.get("id").toString();
                }
            }
            catch (RuntimeException e)
            {
                throw new CommonException(e);
            }
            catch (Exception e)
            {
                throw new CommonException(e);
            }
        }
        catch (CommonException e)
        {
            log.error("Save email file to db fail.({}, {})", LogUtils.formatEmail(account.getUserName()), e);
            return null;
        }
        finally
        {
            try
            {
                
                os.close();
                
            }
            catch (Exception e)
            {
                log.error("Close out put stream fail. ({}, {})", 
                        LogUtils.formatEmail(account.getUserName()), LogUtils.encodeForLog(e.getMessage()));
            }
        }
        return mailFileId;
    }
    
    
    
    /**
     * 格式化 script 标签
     * @param msg
     * @return
     */
    private String formatScript(String msg)
    {
        if (msg == null || msg.isEmpty())
        {
            return msg;
        }
        
        String regex = "(?i)(<)(\\s*/?\\s*script\\s*[^>]*)(>)";
        return msg.replaceAll(regex, "&lt;$2&gt;");
    }
    
    /**
     * 设置邮件的基本信息
     * @param account 邮箱帐户
     * @param uid 邮件UID
     * @param mm 原始邮件
     * @param em 数据库邮件对象
     * @throws MessagingException 邮件异常
     */
    private void setBasicEmailInfo(EmailAccount account, String uid,
            MimeMessage mm, EmailMessage em) throws MessagingException
    {
        String from = getFrom(mm);
        em.setFrom(from);
        em.setCc(getToOrCcOrBcc(mm, EMAIL_CC));
        em.setBcc(getToOrCcOrBcc(mm, EMAIL_BCC));
        em.setTo(getToOrCcOrBcc(mm, EMAIL_TO));
        em.setReceiveEmail(account.getEmailAddress());
        em.setSubject(getSubject(mm));
        em.setSendDate(mm.getSentDate());
        em.setStatus(DBMessage.MESSAGE_STATUS_INITIAL);
        em.setUid(uid);
        em.setAccessCode(account.getServiceCode());
        em.setType(EmailMessage.TYPE_TO_AGENT);
    }

    

    /**
     * 获取邮件主题
     * @param mm 邮件消息
     * @return 主题
     * @throws MessagingException 邮件异常
     */
    private String getSubject(MimeMessage mm)
        throws MessagingException
    {
        String subject = mm.getSubject() == null ? "" : mm.getSubject();
        
        String  head  = "";
        try
        {
	        try
	        {
	            String[] headers = mm.getHeader("SUBJECT");
	            if (headers != null && headers.length > 0)
	            {
	                head = headers[0]; //获取邮件的头
	            }
	            subject = EmailParser.decodeWord(head);
	            //为防止oracle数据库长度超过限制，主题做截取
	            if (subject != null && subject.length() > EMAIL_SUBJECT_MAX_SIZE)
	            {
	                subject = subject.substring(0, EMAIL_SUBJECT_MAX_SIZE);
	            }
	        }
	        catch (RuntimeException e)
	        {
	            throw e;
	        }
	        catch (Exception e)
	        {
	        	throw new CommonException(e);
	        }
        }
        catch (CommonException e)
        {
        	log.error("Decode subject fail, use java mail subject. Head is {}", head);
        }
        
        return subject;
    }
  

    /**
     * 获取邮件的UID
     * @param account 帐户
     * @param folder 文件夹
     * @param msg 消息
     * @return UID
     * @throws MessagingException 邮箱异常
     */
    private String getUid(EmailAccount account, Folder folder, Message msg) 
        throws MessagingException
    {
        String uid;
        if (account.getReceiverType().equals("imap"))
        {
            uid = Long.toString(((IMAPFolder)folder).getUID(msg));
        }
        else
        {
            uid = ((POP3Folder)folder).getUID(msg);
        }
        return uid;
    }

    /**
     * 获取邮箱中的邮件，只获取UID
     * @param folder 邮箱文件夹
     * @return 邮件数组
     * @throws MessagingException 邮箱异常
     */
    private Message[] getMessagesWithUid(Folder folder)
        throws MessagingException
    {
        if (!folder.isOpen())
        {
            folder.open(Folder.READ_WRITE);
        }
        Message[] msgs = folder.getMessages();
        FetchProfile profile = new FetchProfile();
        profile.add(UIDFolder.FetchProfileItem.UID);
        profile.add(FetchProfile.Item.ENVELOPE);
        folder.fetch(msgs, profile);
        return msgs;
    }

    /**
     * 获取邮箱文件夹
     * @param account 帐户
     * @param store store对象
     * @return 邮箱文件夹
     * @throws MessagingException 邮箱异常
     */
    private Folder getFolder(EmailAccount account, Store store)
        throws MessagingException
    {
        Folder folder;
        if (account.getReceiverType().equals("imap"))
        {
            folder = (IMAPFolder)store.getFolder("INBOX");
        }
        else
        {
            folder = (POP3Folder)store.getFolder("INBOX");
        }
        return folder;
    }

    
    /**
     * 获取已连接的Store对象
     * @param account 邮件帐户
     * @return Store对象
     * @throws MessagingException 邮箱异常
     * @throws GeneralSecurityException 
     */
    private Store getConnectedStore(EmailAccount account)
        throws  MessagingException, GeneralSecurityException
    {
        Store store = null;
        Properties props = new Properties();
        props.setProperty("mail.pop3.timeout", STORE_CONNECTION_TIMEOUT); 
        props.setProperty("mail.pop3.connectiontimeout", STORE_CONNECTION_TIMEOUT);
        props.setProperty("mail.imap.timeout", STORE_CONNECTION_TIMEOUT); 
        props.setProperty("mail.imap.connectiontimeout", STORE_CONNECTION_TIMEOUT);
        
        Session session = null;
        
        //设置SSL安全连接
        if (account.isReceiveNeedSSL())
        {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            
            props.put("mail.pop3.ssl.enable", "true");
            props.put("mail.pop3.ssl.socketFactory", sf);
            
            props.put("mail.imap.ssl.enable", "true");
            props.put("mail.imap.ssl.socketFactory", sf);
        }
        
        // 是否支持 NTLM(Secure Authentication)
        if (account.isEnableNTLM())
        {
            if ("imap".equalsIgnoreCase(account.getReceiverType()))
            {
                props.setProperty("mail.imap.auth.plain.disable", "true");
                props.setProperty("mail.imap.auth.login.disable", "true");
            }
        }
        
        if (account.isNeedAuth())
        { 
            //服务器需要身份认证  
            props.put("mail.smtp.auth","true");     
            session = Session.getInstance(props, new MailAuth(account.getUserName(), account.getMailPWD()));
        }
        else
        {
            props.put("mail.smtp.auth","false");  
            session = Session.getInstance(props, null);  
        }  
        //接收类型，imap或者pop3
        try 
        {
            store = session.getStore(account.getReceiverType());
            store.connect(account.getReceiverServer(), 
                  account.getReceiverPort(), account.getUserName(), account.getMailPWD());
        }
        catch (MessagingException e)
        {
        	try
        	{
	            try 
	            {
	                if (store != null)
	                {
	                    store.close();
	                }
	            }
	            catch (Exception e2)
	            {
	            	throw new CommonException(e);
	            }
	        }
	        catch (CommonException ex)
	        {
	        	log.error(LogUtils.encodeForLog(ex.getLocalizedMessage()));
	        }
        }
        return store;
    }
    
    private String getFrom(MimeMessage mimeMessage)
    {
        
        InternetAddress address[] = null;
        try
        {
            address = (InternetAddress[])mimeMessage.getFrom();
        }
        catch (MessagingException e)
        {
            log.error("Get address fail. {}", LogUtils.encodeForLog(e));
            return null;
        }
        
        String from = (address != null && address.length > 0) ? address[0].getAddress() : null;
        if (from == null)
        {
            from = "";
        }
        return from;
        
    }
    
    private String getToOrCcOrBcc(MimeMessage mimeMessage,String type)
    {
        
        InternetAddress[] address = null;
        try
        {
	        try
	        {
	            if (type.equals(EMAIL_TO))
	            {
	                address = (InternetAddress[])mimeMessage.getRecipients(Message.RecipientType.TO);
	            }
	            else if (type.equals(EMAIL_CC))
	            {
	                address = (InternetAddress[])mimeMessage.getRecipients(Message.RecipientType.CC);
	            }
	            else if (type.equals(EMAIL_BCC))
	            {
	                address = (InternetAddress[])mimeMessage.getRecipients(Message.RecipientType.BCC);
	            }
	            else 
	            {
	                return "";
	            }
	            
	        }
	        catch (Exception e)
	        {
	        	throw new CommonException(e);
	        }
	    }
	    catch (CommonException e)
	    {
	    	 log.error("Get address fail. {}", LogUtils.encodeForLog(e));
	         return null;
	    }
        if (address == null || address.length == 0)
        {
            return "";
        }
        else
        {
            StringBuilder cc = new StringBuilder("");
            for (InternetAddress add : address)
            {
                if (!StringUtils.isNullOrEmpty(add.getAddress()))
                {
                    if ("".equals(cc.toString()))
                    {
                        cc.append(add.getAddress());
                    }
                    else
                    {
                        cc.append("," + add.getAddress());
                    }
                }
            }
            return cc.toString();
        }
    }
    
    
    /**
     * 发送
     * @param account 邮件帐户
     * @param defaultAccount 是否是缺省帐户
     */
    private void send(EmailAccount account, Boolean defaultAccount)
    {
        List<EmailMessage> messageList = null;
        if (defaultAccount)
        {
            messageList = getNeedToSendMailList("default"); 
        }
        else
        {
            messageList = getNeedToSendMailList(account.getEmailAddress()); 
        }
         
        
        if (messageList == null)
        {
            log.debug("No need to send emails.({}).", LogUtils.formatEmail(account.getUserName()));
            return;
        }
        
        for (EmailMessage m : messageList)
        {
            log.debug("Begin send email with id {}.", m.getId());
            try
            {
                sendComplexEmail(account, m);
                updateDBMessageStatus(m, DBMessage.MESSAGE_STATUS_FINISH, "");
            }
            catch (Exception e)
            {
                log.error("Email's send response failed with a error.\r\n ({})", 
                        LogUtils.formatEmail(account.getUserName()), LogUtils.encodeForLog(e));
                updateDBMessageStatus(m, DBMessage.MESSAGE_STATUS_FAIL, e.getMessage());
                continue;
            } 
        }
    }


    /**
     * 更新数据库中消息状态
     * @param m 消息
     * @param status 状态
     * @param errorMessage 失败原因
     */
    private void updateDBMessageStatus(EmailMessage m, int status, String errorMessage)
    {
        Map<String, Object> p = new HashMap<String, Object>();
        

        // For Mongo
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("id", m.getId());
        
        // For Oracle
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("id", m.getId());
        Map<String, Object> update = new HashMap<String, Object>();
        update.put("status", status);
        update.put("failInfo", errorMessage);
        update.put("callTime", 1);
        p.put("condition", condition);
        p.put("update", update);
        
        dbService.updateMessage(p);
    }

    /**
     * 获取需要发送的邮件列表
     * @param account 邮件帐户
     * @return 需要发送的邮件列表
     */
    private List<EmailMessage> getNeedToSendMailList(String emailAddress)
    {
    	List<EmailMessage> messageList = null;
        
    	try
    	{
	        try
	        {
	            messageList = dbService.getNeedToSendMailList(emailAddress);
	        }
	        catch (Exception e)
	        {
	        	throw new CommonException(e);
	        }
	    }
	    catch (CommonException e)
	    {
	    	log.error("Get need send emails fail, error message is {}.", LogUtils.encodeForLog(e.getMessage()));
            return null;
	    }
        return messageList;
    }
    
    

    private static InternetAddress[] getAddressArray(String address)
    {
        String[] receiveArray = address.split(",");
        List<InternetAddress> receiveList = new ArrayList<InternetAddress>();
        
        for (String subReceive : receiveArray)
        {
            if (StringUtils.isNullOrEmpty(subReceive))
            {
                continue;
            }
            try
            {
                receiveList.add(new InternetAddress(subReceive));
            }
            catch (AddressException e)
            {
                log.error("Address exception catched. Address is {}.",  e);
            }
        }
        
        InternetAddress[] tmp = (InternetAddress[])receiveList.toArray(new InternetAddress[receiveList.size()]);
        return tmp;
    }
    
    /**
     * 发送带附件的邮件
     * @param account 邮箱帐户
     * @param m 邮件消息
     * @throws EmailException 邮件异常
     * @throws MessagingException 消息异常
     * @throws IOException io异常，读数据库附件异常
     */
    public void sendComplexEmail(EmailAccount account, EmailMessage m)
        throws EmailException, MessagingException, IOException
    {
        String popUser = account.getUserName();
        String sendServer = account.getSendServer();
        int port = account.getSendPort();
        String popPassword = account.getMailPWD();
        
        Properties props = new Properties();
        Session session = null;
        
        //设置SSL安全连接
        if (account.isSendNeedSSL())
        {
            try
            {
                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                
                props.put("mail.smtp.ssl.enable", "true");
                props.put("mail.smtp.ssl.socketFactory", sf);
            }
            catch (GeneralSecurityException e)
            {
                log.error("Send Email fail with a GeneralSecurityException.\r\n({}, {})",
                        LogUtils.formatEmail(account.getUserName()), LogUtils.encodeForLog(e.getMessage()));
            }
        }
        
        // 是否支持 NTLM
        if (account.isEnableNTLM())
        {
            props.setProperty("mail.smtp.auth.mechanisms", "NTLM");
        }
        
        if (account.isNeedAuth())
        { 
            //服务器需要身份认证  
            props.put("mail.smtp.auth","true");     
            session = Session.getInstance(props, new MailAuth(popUser, popPassword));
        }
        else
        {
            props.put("mail.smtp.auth","false");  
            session = Session.getInstance(props, null);  
        }  
        
        Transport trans = null;    

        Message mailMessage = new MimeMessage(session);   
        
        // 设置发件人
        Address fromAddress = new InternetAddress(account.getEmailAddress());  
        mailMessage.setFrom(fromAddress);  
        
        // 设置收件人
        
        String receiveAdd = m.getTo();
        if (!StringUtils.isNullOrEmpty(receiveAdd))
        {
            mailMessage.setRecipients(Message.RecipientType.TO, getAddressArray(receiveAdd));  
        }
       
        // 抄送人
        String strCc = m.getCc();
        if (!StringUtils.isNullOrEmpty(strCc))
        {
            mailMessage.setRecipients(Message.RecipientType.CC, getAddressArray(strCc));
        }
        
        // 密送
        String strBcc = m.getBcc();
        if (!StringUtils.isNullOrEmpty(strBcc))
        {
            mailMessage.setRecipients(Message.RecipientType.BCC, getAddressArray(strBcc));
        }
        
        // 设置主题 
        if (!StringUtils.isNullOrEmpty(m.getSubject()))
        {
            mailMessage.setSubject(m.getSubject()); 
        }
        
        // 第一层
        MimeMultipart mp = new MimeMultipart();  

        // 邮件内容
        MimeBodyPart mbp = new MimeBodyPart(); 
        
        // 邮件内容的content
        MimeMultipart content = new MimeMultipart();
        content.setSubType("alternative");
        
        MimeBodyPart textBody = new MimeBodyPart(); 
        textBody.setContent(m.getTextContent() == null ? " " : m.getTextContent(), "text/plain;charset=utf-8");  
        content.addBodyPart(textBody); 
        
        if (!StringUtils.isNullOrEmpty(m.getHtmlContent()))
        {
            MimeBodyPart htmlBody = new MimeBodyPart(); 
            htmlBody.setContent(m.getHtmlContent(), "text/html;charset=utf-8");  
            content.addBodyPart(htmlBody); 
        }
        
       
        
        mbp.setContent(content);
        
        mp.addBodyPart(mbp);
        List<AttachFile> attachList = m.getAttchments();
        if (attachList != null)
        {
            for (AttachFile a : attachList)
            {
                
                mbp = new MimeBodyPart();  
                DataSource ds = null;
                
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("collectionName", "email");
                param.put("tableName", "T_WECC_FILE_ATTACH");
                param.put("id", a.getId());
                byte[] b = dbService.getFileByteArray(param);
                if (null == b)
                {
                    continue;
                }
                ds = new ByteArrayDataSource(new ByteArrayInputStream(b), "application/octet-stream");
                mbp.setDataHandler(new DataHandler(ds)); //得到附件本身并至入BodyPart
                if (AttachFile.TYPE_ATTACH.equalsIgnoreCase(a.getType()))
                {
                    try
                    {
                        mbp.setFileName(MimeUtility.encodeWord(a.getFileName()));
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        log.error("Encode Filename fail. {} ", e);
                        mbp.setFileName(a.getFileName());
                    }
                }
                else
                {
                
                    mbp.setHeader("Content-ID",a.getFileName());
                }
                
                //得到文件名同样至入BodyPart
                //mbp.getContentType();
                mp.addBodyPart(mbp);  
                
               
            }
            
        }
           
        mailMessage.setContent(mp); //Multipart加入到信件  
        mailMessage.setSentDate(new Date());     //设置信件头的发送日期  
        //发送信件  
        mailMessage.saveChanges();   
        trans = session.getTransport("smtp");
        trans.connect(sendServer, port, popUser, popPassword);  
        trans.sendMessage(mailMessage, mailMessage.getAllRecipients());  
        trans.close();  
        log.debug("End send email with id {} success.", m.getId());
    }
    
    
    /**
     * 关闭执行线程，清理清关资源
     */
    public void closeRunner()
    {
        runFlag = false;
    }
}
