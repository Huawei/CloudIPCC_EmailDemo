
package com.huawei.prometheus.adapter.email.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeUtility;

import org.apache.james.mime4j.message.BinaryBody;
import org.apache.james.mime4j.message.Body;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.Entity;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.message.Multipart;
import org.apache.james.mime4j.message.TextBody;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.parser.MimeEntityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.prometheus.comm.ConfigList;
import com.huawei.prometheus.comm.ConfigProperties;
import com.huawei.prometheus.comm.StringUtils;
import com.huawei.prometheus.comm.bean.email.AttachFile;
import com.huawei.prometheus.comm.exception.CommonException;
import com.huawei.prometheus.dao.service.EmailMessageDBService;


/**
 * <p>Title: Email Parser </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 * @since
 */
public class EmailParser
{
    private static Logger log = LoggerFactory.getLogger(EmailParser.class);
    
    private StringBuffer txtBody;
    
    private StringBuffer htmlBody;
    
    private List<BodyPart> attachments;
    
    
    private Message mimeMsg = null;
    
    private EmailMessageDBService dbService;

    
    /**
     * 构造函数
     * @param emailFileId emailFileId
     * @throws IOException IOException
     */
    public EmailParser(String emailFileId) throws IOException
    {
        InputStream is = null;
        try
        {
            dbService = new EmailMessageDBService();
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("id", emailFileId);
            is = dbService.getFile(m);
            MimeEntityConfig config = new MimeEntityConfig();
            config.setMaxHeaderCount(Integer.MAX_VALUE);
            config.setMaxContentLen(Integer.MAX_VALUE);
            config.setMaxLineLen(Integer.MAX_VALUE);
            this.mimeMsg = new Message(is, config);
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException ex)
                {
                    log.error("Close input stream fail, error message is {}.", ex.getMessage());
                }
            }
        }
        
    }
    
    /**
     * 根据解析邮件
     * @throws IOException IO异常
     */
    public void parse() throws IOException
    {
        txtBody = new StringBuffer();
        htmlBody = new StringBuffer();
        attachments = new ArrayList<BodyPart>();
        if (this.mimeMsg.isMultipart())
        {
            Multipart multipart = (Multipart)mimeMsg.getBody();
            parseBodyParts(multipart);
        }
        else
        {
            //If it's single part message, just get text body
            if (mimeMsg.isMimeType("text/plain"))
            {
                String txt = getTxtPart(mimeMsg);
                txtBody.append(txt);
            }
            else if (mimeMsg.isMimeType("text/html"))
            {
                String html = getTxtPart(mimeMsg);
                htmlBody.append(html);
            }
        }
    }
    
    /**
     * 解析邮件Body
     * @param multipart 邮件正文
     * @throws IOException io异常
     */
    private void parseBodyParts(Multipart multipart) throws IOException
    {
        for (BodyPart part : multipart.getBodyParts())
        {
            if (part == null)
            {
                continue;
            }
            // 附件要放在最前面，因为附件也可能是下面的mimeType
            // 如果是附件,后续处理
            if ((part.getDispositionType() != null
                    && !"".equals(part.getDispositionType())) 
                    || part.getHeader().getField("Content-ID") != null)
            {
                
                //If DispositionType is null or empty, it means that it's multipart, not attached file
                if (part.getDispositionType() != null && "inline".equalsIgnoreCase(part.getDispositionType()))
                {
                    //对于inline的做特殊处理
                    if (part.isMimeType("text/plain"))
                    {
                        String txt = getTxtPart(part);
                        txtBody.append(txt);
                    }
                    else if (part.isMimeType("text/html"))
                    {
                        String html = getTxtPart(part);
                        htmlBody.append(html);
                    }
                }
                else
                {
                    attachments.add(part);
                }
            }
            else if (part.isMimeType("text/plain"))
            {
                
                String txt = getTxtPart(part);
                txtBody.append(txt);
            }
            else if (part.isMimeType("text/html"))
            {
                
                String html = getTxtPart(part);
                htmlBody.append(html);
            }
            //If current part contains other, parse it again by recursion
            if (part.isMultipart())
            {
                parseBodyParts((Multipart)part.getBody());
            }
        }
    }
    
    /**
     * 获取邮件块文字内容
     * @param part 邮件分块
     * @return 邮件块文字内容
     * @throws IOException IOException
     */
    private String getTxtPart(Entity part) throws IOException
    {
        //Get content from body
        TextBody tb = (TextBody)part.getBody();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        tb.writeTo(baos);
        //测试代码
        //if ("".equals(tb.getMimeCharset()) || "US-ASCII".equals(tb.getMimeCharset()))
        if ("".equals(tb.getMimeCharset()) || null == tb.getMimeCharset())
        {
            String defaultDecodeCharset = ConfigProperties.getKey(ConfigList.EMAIL, "EMAIL_DEFAULT_DECODE");
            return new String(baos.toByteArray(), defaultDecodeCharset);
        }
        return new String(baos.toByteArray(), tb.getMimeCharset());
    }
    
    public String getTextBody()
    {
        return this.txtBody.toString();
    }
    
    public String getHtmlBody()
    {
        return this.htmlBody.toString();
    }
    
    /**
     * 保存邮件附件
     * @return 附件列表
     * @throws IOException IOException
     */
    public List<AttachFile> saveAttachFiles() throws IOException
    {
        List<AttachFile> attachList = new ArrayList<AttachFile>(); 
        AttachFile af = null;
        for (BodyPart attach : attachments)
        {
            if (attach == null)
            {
                continue;
            }
            af = new AttachFile();
            // 页面中的图片
            String attName = "";
            
            if (attach.getHeader() != null
                    && (attach.getHeader().getField("Content-ID") != null 
                            && (attach.getDispositionType() == null 
                                    || !"attachment".equalsIgnoreCase(attach.getDispositionType()))))
            {
                Field field = attach.getHeader().getField("Content-ID");
                if (field != null)
                {
                    attName = field.getBody();
                }
                af.setType(AttachFile.TYPE_EMBEDED_IMAG);
            }
            else
            {
                af.setType(AttachFile.TYPE_ATTACH);
                attName = attach.getFilename();
            }
            
            if (attName == null)
            {
                attName = "";
            }
            String attId = "";
            //Create file with specified name
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try
            {
                try
                {
                    //对中文字符进行解码
                    if (attName.endsWith("?="))
                    {
                        try
                        {
                            attName = decodeWord(attName); 
                        }
                        catch (Exception e)
                        {
                            log.error("Parse attach name fail. attName is ", attName);
                        }
                    }
                    //Get attach stream, write it to file
                    Body bd = attach.getBody();
                    if (bd instanceof BinaryBody)
                    {
                        BinaryBody bb = (BinaryBody)attach.getBody();
                        bb.writeTo(bos);
                    }
                    else if (bd instanceof TextBody)
                    {
                        TextBody bb = (TextBody)attach.getBody();
                        bb.writeTo(bos);
                    }
                    else
                    {
                        
                        Method m = attach.getBody().getClass().getMethod("writeTo", OutputStream.class);
                        m.invoke(attach.getBody(), bos);
                    }
                    
                    Map<String, Object> m = new HashMap<String, Object>();
                    m.put("content", bos.toByteArray());
                    m.put("fileName", attName);
                    dbService.saveFile(m);
                    if (m.get("id") != null)
                    {
                        attId = m.get("id").toString();
                        af.setId(attId);
                        af.setFileName(attName);
                        attachList.add(af);
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
                log.error("Get attach part fail. {}", e);
            }
            finally
            {
                try
                {
                	bos.close();
                }
                catch (IOException e)
                {
                    log.error("Close ByteArray OutputStream fail. {}", e);
                }
            }
        }
        return attachList;
    }
    
    /**
     * 解码Base64编码或QP编码的字符串，可以解析用换行分割的码
     * 编码格式 形如 ：  
     * =?charset?B?xxxxxxxxxx?=\r\n=?charset?Q?xxxxxxxxxx?=\r\n\t=?charset?Q?xxxxxxxxxx?=
     * @param a 需要解码的字符串
     * @return 解码后的结果
     */
    public static String decodeWord(String a)
    {
        if (StringUtils.isNullOrEmpty(a))
        {
            return null;
        }
        
        // 进行初步判断，满足条件的也不一定是需要解码的
        if (!a.endsWith("?=") || !a.startsWith("=?"))
        {
            return a;
        }
        
        StringBuffer sb = new StringBuffer();
        
        // =? 和?= 的长度
        final int two = 2;
        while (a.length() > 0)
        {
            // 寻找每个独立串
            int start = a.indexOf("=?");
            int end = a.indexOf("?=");
            
            // 找不到就返回原值
            if (start < 0 || end < 0 || start > end)
            {
                sb.append(a);
                break;
            }
            
            // QP编码的时候中间也会有?=出现，找之后紧跟换行符的?=,或者?=在字符串结尾的地方了
            while (end >= 0 
                    && (end != a.length() - two) 
                    && a.charAt(end + two) != '\r' 
                    && a.charAt(end + two) != '\n'
                    && a.charAt(end + two) != '\t'
                    && a.charAt(end + two) != ' ')
            {
                end = a.indexOf("?=", end + 1);
            }
            
            if (end < 0)
            {
                sb.append(a);
                break;
            }
            
            // 截出在两个 =?  ?= 对之间的内容
            String c = a.substring(0, start);
            if (!StringUtils.isNullOrEmpty(c))
            {
                //去掉回车符,换行符,制表符\t
//                c.trim();
                while (c.length() > 0)
                {
                    if (c.startsWith("\r"))
                    {
                        c = c.substring(1);
                    }
                    else if (c.startsWith("\n"))
                    {
                        c = c.substring(1);
                    }
                    else if (c.startsWith("\t"))
                    {
                        c = c.substring(1);
                    }
                    else
                    {
                        break;
                    }
                }
                
                sb.append(c);
            }
                
            //取出需要解码的部分
            String b = a.substring(start, end + two);
            //对=? ?= 中间的部分进行解码
            sb.append(decodeSingleWord(b));
            //截掉已处理部分
            a = a.substring(end + two);
        }
        return sb.toString();
    }
    

    /**
     * 解码单个未用换行符进行分割的编码的串, 针对未指名编码方式的串，使用缺省编码格式进行解码
     * @author l00165392 李飞虎
     * @param b 需要解码的字符串
     * @return 解码后的结果
     */
    private static String decodeSingleWord(String b) 
    {
        String defaultDecodeCharset = ConfigProperties.getKey(ConfigList.EMAIL, "EMAIL_DEFAULT_DECODE");
        if (!StringUtils.isNullOrEmpty(b))
        {
            String regex = "^=\\?.*\\?(B|Q)\\?.*\\?=$";
            Pattern pattern = Pattern.compile(regex);   
            Matcher matcher = pattern.matcher(b); 
            if (matcher.find())
            {
                try
                {
                    String subject = b;
                    if (subject.contains("??B?"))
                    {
                        return MimeUtility.decodeText(
                                b.replace("??B?", "?" + defaultDecodeCharset + "?B?"));
                    }
                    else if (subject.contains("??Q?"))
                    {
                        return MimeUtility.decodeText(
                                b.replace("??Q?", "?" + defaultDecodeCharset + "?Q?"));
                    }
                    else
                    {
                        return MimeUtility.decodeWord(b);
                    }
                }
                catch (Exception e)
                {
                    log.error("Decode word fail, error message is {}", e);
                }
            }
        }
        return "";
    }
    
}