
package com.huawei.adapter.dao.init;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.common.config.ConfigList;
import com.huawei.adapter.common.config.ConfigProperties;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.util.exception.CommonException;
import com.huawei.adapter.common.util.utils.LogUtils;

/**
 * 
 * <p>Title:  Mybatis初始化</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
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
     * DB数据库连接工厂
     */
    private static SqlSessionFactory dbSqlSessionFactory = null;

    /**
     * DB数据库连接配置文件路径
     */
    private static String DB_MYBATIS_CONFIG_FILEPATH = "/dbMybatisConfiguration.xml";

    /**
     * 初始化uidb是否成功
     */
    private static boolean isInitDBOk = false;

    /**
     * 初始化DB数据库配置信息
     */
    public static void initDBSqlSessionFactory()
    {
        establishDBSqlSessionFactory(); 
    }

    /**
     * 初始化数据库配置信息
     */
    private static void establishDBSqlSessionFactory()
    {
        log.debug("begin to establishDBSqlSessionFactory.");

        Reader reader = null;

        Properties properties = new Properties();
        if (Constants.DB_TYPE_ORACLE.equalsIgnoreCase(ConfigProperties.getKey(ConfigList.DB,
                "DB_DBTYPE")))
        {
            properties.put("DB_DB_CONNECT_DRIVER", ORACLE_DRIVER);
        }
        else
        {
            properties.put("DB_DB_CONNECT_DRIVER", MYSQL_DRIVER);
        }
        properties.put("DB_DB_CONNECT_URL", ConfigProperties.getKey(ConfigList.DB, "DB_DB_CONNECT_URL"));
        properties.put("DB_DB_CONNECT_NAME", ConfigProperties.getKey(ConfigList.DB, "DB_DB_CONNECT_NAME"));
        properties.put("DB_DB_CONNECT_PASSWORD", ConfigProperties.getKey(ConfigList.DB, "DB_DB_CONNECT_PASSWORD"));
        properties.put("DB_DBTYPE", ConfigProperties.getKey(ConfigList.DB, "DB_DBTYPE"));
        try
        {
            try
			{
            	//读取
				reader = Resources.getResourceAsReader(DB_MYBATIS_CONFIG_FILEPATH);
				dbSqlSessionFactory = new SqlSessionFactoryBuilder().build(reader, "db", properties);
				log.info("UIDB init dbMybatisConfiguration.xml success.");
				isInitDBOk = true;
			}  
            catch (Exception e)
			{
				throw new CommonException(e);
			}
        }
        catch (CommonException ex)
        {
            log.error("UIDB init dbMybatisConfiguration.xml Failed. IOException : \r\n {}",
                    LogUtils.encodeForLog(ex.getMessage()));
            isInitDBOk = false;
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
                    log.error("close reader has exception:  \r\n {}", LogUtils.encodeForLog(e.getMessage()));
                }
            }
        }
    }

    /**
     * DB是否初始化成功
     * @return
     */
    public static boolean isInitDBOk()
    {
        return isInitDBOk;
    }

    /**
     * 获取 DB 数据库会话工厂对象
     * @return SqlSessionFactory
     */
    public static SqlSessionFactory getDBSqlSessionFactory()
    {
        if (null == dbSqlSessionFactory)
        {
            establishDBSqlSessionFactory();
        }
        return dbSqlSessionFactory;
    }
}
