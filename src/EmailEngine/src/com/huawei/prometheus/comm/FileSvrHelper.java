
package com.huawei.prometheus.comm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: FileSvrService </p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: 华为技术有限公司</p>
 */
public class FileSvrHelper
{
    private static FileSvrHelper instance = new FileSvrHelper();
    
    private static Logger log = LoggerFactory.getLogger(FileSvrHelper.class);
    
    private static final int MAX_SUB_DIRS_FOR_WORKNO = 100;
    
    //mountDir 表示本服务器上mount的文件服务器的路径
    private String mountDir = ConfigProperties.getKey(ConfigList.BASIC, "MOUNTDIR");
    
    public static FileSvrHelper getInstance()
    {
        return instance;
    }
    
    public String getMountDir()
    {
        return mountDir;
    }
    
    public void setMountDir(String mountDir)
    {
        this.mountDir = mountDir;
    }
    
    /**
     * 保存文件到文件服务器上。目录结构为{集合名词}/{weekOfYear}/{当前文件的的hash值%100}/
     * 文件是一个一个UUID
     * 集合名词定义如下
     *  photo         存放图片
     *  chatattach    存放文本交谈中传输的附件
     *  email         存放原始邮件
     *  emailattach   存放邮件的附件
     *  
     * @param colName   表格名称
     * @param in        输入流
     * @return  成功返回相对于svrRootPath的文件路径
     * @throws IOException 文件异常
     */
    public String saveToFileServer(String colName, InputStream in) throws IOException
    {
        String uuid = UUID.randomUUID().toString();
        
        int bucket = Math.abs(uuid.hashCode() % MAX_SUB_DIRS_FOR_WORKNO);
        
        int weekOfYear = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        
        String contentDir = "/" + colName + "/" + weekOfYear + "/" + bucket;
        
        File theDir = new File(mountDir + contentDir);
        
    
        if (theDir.exists() || theDir.mkdirs())
        {
            final int bufSize = 1024;
            
            byte[] buffer = new byte[bufSize];
            int length = 0;
            
            String filePath = contentDir + "/" + uuid;
            
            FileOutputStream fout = null;
            
            try
            {
                fout = new FileOutputStream(mountDir + filePath);
                while ((length = in.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, length);
                }
            }
            finally
            {
                if (fout != null)
                {
                    try
                    {
                        fout.close();
                    }
                    catch (IOException e)
                    {
                        log.error("close file failed {} ", filePath, e);
                    }
                }
            }
            return filePath;
        }
        
        return null;
    }
    
    /**
     * 将输入流转化为byte数组
     * @param is 输入流
     * @return 输入内容
     * @throws IOException 异常
     */
    public static byte[] inputStreamToByte(InputStream is) throws IOException
    {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        byte imgdata[] = null;
        try
        {
            int ch;
            while ((ch = is.read()) != -1)
            {
                bytestream.write(ch);
            }
            imgdata = bytestream.toByteArray();
        }
        catch (Exception e)
        {
            log.error("InputStreamToByte fail.", e);
        }
        finally
        {
            
            try
            {
                bytestream.close();
            }
            catch (IOException ex)
            {
                log.error("Close input stream fail, error message is {}.", ex.getMessage(), ex);
            }
            
        }
        return imgdata;
    }
    
    
   
}
