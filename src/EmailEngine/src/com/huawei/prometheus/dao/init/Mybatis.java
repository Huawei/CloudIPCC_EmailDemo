
package com.huawei.prometheus.dao.init;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.prometheus.comm.ConfigList;
import com.huawei.prometheus.comm.ConfigProperties;
import com.huawei.prometheus.comm.LogUtils;
import com.huawei.prometheus.comm.exception.CommonException;

/**
 * 
 * <p>Title:  Mybatis初始化</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 */
public class Mybatis
{
    private static final Logger log = LoggerFactory.getLogger(Mybatis.class);
    
    /**
     * oracle驱动
     */
    private static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    


    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    
    /**
     * Email数据库连接工厂
     */
    private static SqlSessionFactory emailSqlSessionFactory = null;


    
    /**
     * 数据库连接配置文件路径
     */
    private static String EMAIL_MYBATIS_CONFIG_FILEPATH = "/emailMybatisConfiguration.xml";

    
    /**
     * 初始化Email数据库配置信息
     */
    public static void initEmailSqlSessionFactory()
    {
        establishEmailSqlSessionFactory(); 
    }
    


    
    /**
     * 初始化数据库配置信息 Email
     */
    private static void establishEmailSqlSessionFactory()
    {
        log.debug("begin to establishEmailSqlSessionFactory.");
        
        Reader reader = null;
        
        Properties properties = new Properties();
        if ("oracle".equalsIgnoreCase(ConfigProperties.getKey(ConfigList.BASIC,
                "EMAIL_DBTYPE")))
        {
            properties.put("EMAIL_DB_CONNECT_DRIVER", ORACLE_DRIVER);
        }
        else if ("mysql".equalsIgnoreCase(ConfigProperties.getKey(ConfigList.BASIC, 
                "EMAIL_DBTYPE")))
        {
            properties.put("EMAIL_DB_CONNECT_DRIVER", MYSQL_DRIVER);
        }
        
        properties.put("EMAIL_DB_CONNECT_URL",
                ConfigProperties.getKey(ConfigList.BASIC, "EMAIL_DB_CONNECT_URL"));
        
        properties.put("EMAIL_DB_CONNECT_NAME",
                ConfigProperties.getKey(ConfigList.BASIC,
                        "EMAIL_DB_CONNECT_NAME"));
        properties.put("EMAIL_DB_CONNECT_PASSWORD",
                ConfigProperties.getKey(ConfigList.BASIC,
                        "EMAIL_DB_CONNECT_PASSWORD"));
        properties.put("EMAIL_DBTYPE",
                ConfigProperties.getKey(ConfigList.BASIC, "EMAIL_DBTYPE"));
        try
        {
            try
			{
            	//读取
				reader = Resources.getResourceAsReader(EMAIL_MYBATIS_CONFIG_FILEPATH);
				emailSqlSessionFactory = new SqlSessionFactoryBuilder().build(reader, "email", properties);
				log.info("Email init EmailMybatisConfiguration.xml success.");
			}  
            catch (IOException ex)
	        {
				throw new CommonException(ex);
	        } 
            catch (Exception e)
			{
				throw new CommonException(e);
			}
        }
        catch (CommonException ex)
        {
            log.error("Email init EmailMybatisConfiguration.xml Failed. IOException : {}", 
                    LogUtils.encodeForLog(ex.getMessage()));
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    log.error("close reader has exception: {}",
                            LogUtils.encodeForLog(e.getMessage()));
                }
            }
        }
    }
    
    
    
 
    

    
    /**
     * 获取 Email 数据库会话工厂对象
     * @return SqlSessionFactory
     */
    public static SqlSessionFactory getEmailSqlSessionFactory()
    {
       
        if (null == emailSqlSessionFactory)
        {
            establishEmailSqlSessionFactory();
        }
        return emailSqlSessionFactory;
    }
    
    
}
