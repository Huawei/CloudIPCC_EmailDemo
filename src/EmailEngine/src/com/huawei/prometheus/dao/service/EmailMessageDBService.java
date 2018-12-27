
package com.huawei.prometheus.dao.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.prometheus.comm.FileSvrHelper;
import com.huawei.prometheus.comm.LogUtils;
import com.huawei.prometheus.comm.bean.email.AttachFile;
import com.huawei.prometheus.comm.bean.email.EmailMessage;
import com.huawei.prometheus.dao.EmailMessageDAO;
import com.huawei.prometheus.dao.init.Mybatis;




public class EmailMessageDBService
{
    private static Logger log = LoggerFactory.getLogger(EmailMessageDBService.class);
    
    /**
     * 存储文件到数据库
     * @param m 参数  
     *    key       value 
     *    content    二进制文件内容
     *    tableName  表名 oracle时使用
     *    id         返回值，记录ID
     * @return 数据库主键ID
     */
    public String saveFile(Map<String, Object> m)
    {
        
        SqlSessionFactory factory = Mybatis.getEmailSqlSessionFactory();
        if (null == factory)
        {
            return null;
        }
        
        String path = saveToServer(m);
        SqlSession sqlSession = null;
        try
        {
            sqlSession = factory.openSession();
            EmailMessageDAO dao = sqlSession.getMapper(EmailMessageDAO.class);
            m.put("contentPath", path);
            dao.saveFile(m);
            sqlSession.commit();
        }
        catch (Exception e)
        {
            if (sqlSession != null)
            {
                sqlSession.rollback();
            }
            
            log.error("saveFile fail. The exception is {}", e.getMessage(), e);
        }
        finally
        {
            if (sqlSession != null)
            {
                sqlSession.close();
            }
        }
        return (String)m.get("id").toString();
    }
    
    
    private String saveToServer(Map<String, Object> m)
    {
        String path = null;
        try
        {
            byte[] b = (byte[])m.get("content");
            path = FileSvrHelper.getInstance()
                    .saveToFileServer("email",
                            new ByteArrayInputStream(b));
            return path;
        }
        catch (IOException e1)
        {
            log.error("Save file to fileserver fail.", e1);
            return path;
        }
    }
    
    
    /**
     * 获取文件内容
     * @param m
     *   key                 value
     *   id                   文件ID 
     * @return 文件内容
     */
    public InputStream getFile(Map<String, Object> m)
    {
        String path = getContentPath(m);
        File file = new File(FileSvrHelper.getInstance().getMountDir() + File.separator + path);
        FileInputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(file);
        }
        catch (FileNotFoundException e)
        {
            log.error("FileNotFoundException catched when get file input stream. {} ",
                    LogUtils.encodeForLog(e.getMessage()));
        }
        return inputStream;
    }
    
    private String getContentPath(Map<String, Object> m)
    {
        SqlSessionFactory factory = Mybatis.getEmailSqlSessionFactory();
        if (null == factory)
        {
            return "";
        }
        SqlSession sqlSession = null;
        Map<String, Object> result = null;
        try
        {
            sqlSession = factory.openSession();
            EmailMessageDAO dao = sqlSession.getMapper(EmailMessageDAO.class);
            List<Map<String, Object>> contentPaths = dao.getFile(m);
            if (!contentPaths.isEmpty())
            {
                result =  contentPaths.get(0);
            }
        }
        catch (Exception e)
        {
            if (sqlSession != null)
            {
                sqlSession.rollback();
            }
            
            log.error("getFile fail. The exception is {}", e.getMessage());
        }
        finally
        {
            if (sqlSession != null)
            {
                sqlSession.close();
            }
        }
        
        return result == null ? null : (String)result.get("CONTENT_PATH");
    }
    
    
    /**
     * 新增一条记录
     * @param message 消息
     * @return 消息数据库ID
     */
    public int createMessage(EmailMessage message)
    {
        SqlSessionFactory factory = Mybatis.getEmailSqlSessionFactory();
        if (null == factory)
        {
            return 0;
        }
        SqlSession sqlSession = null;
        int count = 0;
        try
        {
            sqlSession = factory.openSession();
            EmailMessageDAO dao = sqlSession.getMapper(EmailMessageDAO.class);
            
            count = dao.createMessage(message);
            
            if (count != 0)
            {
                List<AttachFile> attachs = ((EmailMessage)message).getAttchments();
                if (attachs != null)
                {
                    for (AttachFile attach : attachs)
                    {
                        attach.setEmailMessageId(message.getId());
                        dao.createEmailAttach(attach);
                    }
                }
            }
            
            sqlSession.commit();
            
        }
        catch (Exception e)
        {
            if (sqlSession != null)
            {
                sqlSession.rollback();
            }
            
            log.error("Create email message fail. The exception is {}",
                    e.getMessage(), e);
        }
        finally
        {
            if (sqlSession != null)
            {
                sqlSession.close();
            }
        }
        return count;
    }
    
    
    public List<EmailMessage> getNeedToSendMailList(String emailAddress)
    {
        SqlSessionFactory factory = Mybatis.getEmailSqlSessionFactory();
        if (null == factory)
        {
            return null;
        }
        Map<String, Object> queryMap = new HashMap<String, Object>();
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("type", EmailMessage.TYPE_TO_USER);
        condition.put("from", emailAddress);
        condition.put("handler", 0);
        queryMap.put("condition", condition);
        queryMap.put("count", 10);
        
        SqlSession sqlSession = null;
        List<EmailMessage> message = null;
        try
        {
            sqlSession = factory.openSession();
            EmailMessageDAO dao = sqlSession.getMapper(EmailMessageDAO.class);
            message = dao.getNeedToSendMailList(queryMap);
            if (message != null && !message.isEmpty())
            {
                for (EmailMessage email : message)
                {
                    List<AttachFile> attachs = dao.getAttachList(email.getId());
                    email.setAttchments(attachs);
                }
            }
            
        }
        catch (Exception e)
        {
            log.error("getNeedToSendMailList fail. The exception is {}",
                    e.getMessage());
        }
        finally
        {
            if (sqlSession != null)
            {
                sqlSession.close();
            }
        }
        return message;
    }

    
    public void updateMessage(Map<String, Object> p)
    {
        SqlSessionFactory factory = Mybatis.getEmailSqlSessionFactory();
        if (null == factory)
        {
            return;
        }
        SqlSession sqlSession = null;
        try
        {
            sqlSession = factory.openSession();
            EmailMessageDAO dao = sqlSession.getMapper(EmailMessageDAO.class);
            dao.updateMessage(p);
            sqlSession.commit();
        }
        catch (Exception e)
        {
            if (sqlSession != null)
            {
                sqlSession.rollback();
            }
            
            log.error("Update message fail. The exception is {}",
                    e.getMessage());
        }
        finally
        {
            if (sqlSession != null)
            {
                sqlSession.close();
            }
        }
        return;
    }
    
    public byte[] getFileByteArray(Map<String, Object> m)
    {
        byte[] result = null;
        InputStream is = null;
        
        try
        {
            is = getFile(m);
            result = FileSvrHelper.inputStreamToByte(is);
        }
        catch (IOException e)
        {
            log.error("IOException when get File ByteArray", e);
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
                    log.error("Close input stream fail, error message is {}.",
                            ex.getMessage(),
                            ex);
                }
            }
        }
        return result;
    }
    
}
