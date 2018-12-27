/**
 * 
 */
package com.huawei.prometheus.comm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>Title: PassPropertyPlacehoder </p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: 华为技术有限公司</p>
 */
public final class PasswdPropertyPlaceholder
{
    /**待加密的后缀*/
    public static final String SUBFIX_PWD = ".password";
    
    /**加密后的后缀*/
    public static final String SUBFIX_ENCRYPWD = ".encryptpassword";
    
    private static Logger log = LoggerFactory.getLogger(PasswdPropertyPlaceholder.class);
    
    private static StandardEncryptor encryptor = new StandardEncryptor("M56pw4TMRO7wXkhuu30EYT==");
    
    private PasswdPropertyPlaceholder()
    {
        
    }
    
    /**
     * 该函数主要作用: 
     * 1) 会对文件中的xxx.password的key进行处理，将其加密，并且替换为xxx.encryptpassword
     * 并覆盖原始文件。
     * 2) 对propsOut(包含的是处理器前原始文件的key/value)的密码相关属性值进行重新设置
     *    （xxx.password/xxx.encryptpassword)，  替换为xxx/密码明文，供程序使用，
     *    程序中只要查询xxx这个键值就可以了。
     *    
     * @param file 需要进行密码替换的文件
     * @param propsOut 替换原始文件的密码相关的key/value，如果原先xxx.password，
     *  体内换成xxx；如果原始文件中是xxx.encryptpassword，也是替换成key，value都是密码的
     *  明文。
     */
    public static void loadProperties(String file, Properties propsOut)
    {

        FileInputStream in = null;
        FileOutputStream out = null;
        try
        {
            Properties propConfig = new Properties();
            in =  new FileInputStream(file);
            propConfig.load(in);
            propConfig.keySet().iterator();
            Iterator<Object> propKeys =  propConfig.keySet().iterator();
            String propKey = null;
            String propVal = null;
            String tmpKey = null;
            String tmpVal = null;
            String tmpDecVal = null;
            // 1. 处理密码
            while (propKeys.hasNext())
            {
                propKey = (String) propKeys.next();
                propVal = propConfig.getProperty(propKey);
                if (propKey.endsWith(SUBFIX_PWD))
                {
                    // 明文 -> 密文
                    // 配置文件中: XYZ.password -> XYZ.encryptpassword
                    tmpKey = propKey.replace(SUBFIX_PWD, SUBFIX_ENCRYPWD);
                    propConfig.setProperty(propKey, "");   // 删除配置文件中的明文密码
                    if (StringUtils.isNullOrBlank(propVal))
                    {
                        continue;
                    }
                    tmpVal = encryptor.encryptAES(propVal);
                    propConfig.setProperty(tmpKey, tmpVal);
                    
                    // 更新内存中数据(XYZ.password -> XYZ)
                    propsOut.remove(propKey);
                    propsOut.setProperty(propKey.substring(0, propKey.length() - SUBFIX_PWD.length()), propVal);
                }
                else if (propKey.endsWith(SUBFIX_ENCRYPWD))
                {
                    // 密文 -> 明文
                    // 更新内存中数据(XYZ.encryptpassword -> XYZ)
                    propsOut.remove(propKey);
                    if (StringUtils.isNullOrBlank(propVal))
                    {
                        propsOut.setProperty(
                                propKey.substring(0, propKey.length() - SUBFIX_ENCRYPWD.length()), 
                                "");
                    }
                    else
                    {
                        propsOut.setProperty(
                                propKey.substring(0, propKey.length() - SUBFIX_ENCRYPWD.length()), 
                                encryptor.decryptAES(propVal));
                    }
                }
            }
            // 2. 保存文件
            // 2. 保存文件
            in.close();
            out = new FileOutputStream(file);
            propConfig.store(out, "Update value");
        }
        catch (IOException e)
        {
            log.error("loadProperties {}", LogUtils.encodeForLog(e.getMessage()));
        }
        finally 
        {
            if (null != in)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    log.error("loadProperties {}", LogUtils.encodeForLog(e.getMessage()));
                }
            }
            if (null != out)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    log.error("loadProperties {}", LogUtils.encodeForLog(e.getMessage()));
                }
            }
        }
    }
}
