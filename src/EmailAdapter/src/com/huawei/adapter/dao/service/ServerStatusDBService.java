
package com.huawei.adapter.dao.service;



import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.common.util.exception.CommonException;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.dao.init.Mybatis;
import com.huawei.adapter.dao.intf.ServerStatusDAO;

/**
 * 
 * <p>Title: 服务器状态更新  </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @author j00204006
 * @version V1.0 2017年3月18日
 * @since
 */
public class ServerStatusDBService
{
    private static final Logger LOG = LoggerFactory.getLogger(ServerStatusDBService.class);

    private static ServerStatusDBService instance;

    private ServerStatusDBService()
    {
    }

    public static ServerStatusDBService getInstance()
    {
        if (null == instance)
        {
            instance = new ServerStatusDBService();
        }
        return instance;
    }

    /**
     * 更新服务器状态
     * @param clusertId
     * @return
     */
    public int updateServerStatus(String clusertId)
    {
        SqlSessionFactory factory = Mybatis.getDBSqlSessionFactory();
        if (null == factory)
        {
            return 0; 
        }
        SqlSession sqlSession = null;
        try
        {
            try
            {
                sqlSession = factory.openSession();
                ServerStatusDAO dao = sqlSession.getMapper(ServerStatusDAO.class);
                int ret = dao.updateServerStatus(clusertId);
                sqlSession.commit();
                return ret;
            }
            catch (Exception e)
            {
                if (null != sqlSession)
                {
                    sqlSession.rollback();
                }
                throw new CommonException(e);
            }
            finally
            {
                if(null != sqlSession)
                {
                    sqlSession.close();
                }
            }
        }
        catch (CommonException e)
        {
            LOG.error("updateServerStatus failed, \r\n{}", LogUtils.encodeForLog(e.getMessage()));
        }
        return 0;
    }
}
