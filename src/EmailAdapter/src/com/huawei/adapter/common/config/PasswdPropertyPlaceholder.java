
package com.huawei.adapter.common.config;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.common.util.utils.StringUtils;

/**
 * 
 * <p>Title:   加解密配置</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public final class PasswdPropertyPlaceholder
{
    /**待加密的后缀*/
    public static final String PWD_SUBFIX = ".password";

    /**加密后的后缀*/
    public static final String ENCRYPWD_SUBFIX = ".encryptpassword";

    private static final Logger LOG = LoggerFactory.getLogger(PasswdPropertyPlaceholder.class);

    private static AESEncryptor encryptor = null;

    /**
     * 是否需要更新工作秘钥
     */
    private static boolean isNeedUpdateWorkKey = false;

    /**
     * 新工作秘钥的加密工具
     */
    private static AESEncryptor newEncryptor = null;

    /**
     * 用于生成工作秘钥的盐值
     */
    public static final String WORKKEY_SALT = "AJpZtNEls30vovL3sjo01Q==";

    private PasswdPropertyPlaceholder()
    {
    }

    /**
     * 在系统启动时，进行工作秘钥的初始化
     */
    public static void init()
    {
        encryptor = new AESEncryptor(RootKeyManager.getWorkKey(), WORKKEY_SALT); 
        isNeedUpdateWorkKey = RootKeyManager.isNeedUpdateWorkKey();
        if (isNeedUpdateWorkKey)
        {
            newEncryptor = new AESEncryptor(RootKeyManager.getNewWorkKey(), WORKKEY_SALT); 
        }
    }

    public static void clean()
    {
        encryptor = null;
        newEncryptor = null;
    }

    /**
     * 该函数主要作用: 
     * 1) 会对文件中的xxx.password的key进行处理，将其加密，并且替换为xxx.encryptpassword
     * 并覆盖原始文件。
     * 2) 对propsOut(包含的是处理器前原始文件的key/value)的密码相关属性值进行重新设置
     *    （xxx.password/xxx.encryptpassword)，  替换为xxx/原始密码，供程序使用，
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
                if (propKey.endsWith(PWD_SUBFIX))
                {
                    // 明文 -> 密文
                    // 配置文件中: XYZ.password -> XYZ.encryptpassword
                    tmpKey = propKey.replace(PWD_SUBFIX, ENCRYPWD_SUBFIX);
                    propConfig.setProperty(propKey, "");   // 删除配置文件中的原始密码
                    if (StringUtils.isNullOrBlank(propVal))
                    {
                        continue;
                    }

                    tmpVal = encryptor.encryptAES(propVal);
                    propConfig.setProperty(tmpKey, tmpVal);
                    // 更新内存中数据(XYZ.password -> XYZ)
                    propsOut.remove(propKey);
                    propsOut.setProperty(propKey.substring(0, propKey.length() - PWD_SUBFIX.length()), propVal);
                }
                else if (propKey.endsWith(ENCRYPWD_SUBFIX))
                {
                    // 密文 -> 明文
                    // 更新内存中数据(XYZ.encryptpassword -> XYZ)
                    propsOut.remove(propKey);
                    if (StringUtils.isNullOrBlank(propVal))
                    {
                        propsOut.setProperty(propKey.substring(0, propKey.length() - ENCRYPWD_SUBFIX.length()), "");
                    }
                    else
                    {
                	    tmpDecVal = encryptor.decryptAES(propVal);
                        propsOut.setProperty(
                                propKey.substring(0, propKey.length() - ENCRYPWD_SUBFIX.length()), 
                                encryptor.decryptAES(propVal));
                        if (isNeedUpdateWorkKey)
                        {
                            //需要更新工作秘钥时，用新的工作秘钥进行加密
                            propConfig.setProperty(propKey, 
                                    newEncryptor.encryptAES(tmpDecVal));
                        }
                    }
                }

                if ("CLUSTER_ID".equals(propKey)
                        && StringUtils.isNullOrBlank(propVal))
                {
                    String uuId = UUID.randomUUID().toString();
                    propConfig.setProperty("CLUSTER_ID", uuId);
                    propsOut.setProperty("CLUSTER_ID", uuId);
                }
            }
            // 2. 保存文件
            in.close();
            out = new FileOutputStream(file);
            propConfig.store(out, "Update value");
        }
        catch (IOException e)
        {
            LOG.error("loadProperties {}", LogUtils.encodeForLog(e.getMessage()));
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
                    LOG.error("loadProperties {}", LogUtils.encodeForLog(e.getMessage()));
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
                    LOG.error("loadProperties {}", LogUtils.encodeForLog(e.getMessage()));
                }
            }
        }
    }
}
