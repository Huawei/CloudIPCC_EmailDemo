
package com.huawei.adapter.dao.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.Session;
import com.huawei.adapter.common.util.exception.CommonException;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.dao.init.Mybatis;
import com.huawei.adapter.dao.intf.MessageDAO;
import com.huawei.adapter.dao.intf.SessionDAO;

/**
 * 
 * <p>Title: 会话管理 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @author wWX390857
 * @version V1.0 2017年9月4日
 * @since
 */
public class SessionDBService
{
    private static final Logger logger = LoggerFactory.getLogger(SessionDBService.class);

    /**
     * 当前服务器变为主机后，删除所有用户会话
     */
    public void deleteAllSession()
    {
        SqlSessionFactory factory = Mybatis.getDBSqlSessionFactory();
        if (null == factory)
        {
            return;
        }
        SqlSession sqlSession = null;
        try
        {
            try
            {
                sqlSession = factory.openSession();
                SessionDAO sessionDao = sqlSession.getMapper(SessionDAO.class);
                sessionDao.deleteAllSession();
                MessageDAO messageDAO = sqlSession.getMapper(MessageDAO.class);
                messageDAO.updateMessageAllToFailed();
                sqlSession.commit();
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
                if (null != sqlSession)
                {
                    sqlSession.close();
                }
            }
        }
        catch (CommonException e)
        {
            logger.error("deleteAllSession failed, \r\n{}", LogUtils.encodeForLog(e.getMessage()));
        }
    }

    /**
     * 批量删除指定所有用户会话
     * 
     * @param sessions 待删除的会话列表
     * @param clusterId  集群编号
     */
    public boolean batchDeleteSession(List<Session> sessions, String clusterId)
    {
        SqlSessionFactory factory = Mybatis.getDBSqlSessionFactory();
        if (null == factory)
        {
            return false;
        }
        SqlSession sqlSession = null;
        try
        {
            try
            {
                sqlSession = factory.openSession();
                SessionDAO dao = sqlSession.getMapper(SessionDAO.class);
                dao.batchDeleteSession(clusterId, sessions);
                sqlSession.commit();
                return true;
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
            logger.error("batchDeleteSession failed, \r\n{}", LogUtils.encodeForLog(e.getMessage()));
        }
        return false;
    }

    /**
     * 批量更新会话建立时间
     * 
     * @param clusterId 当前服务器集群名
     * @param sessions 待更新的会话集合
     * @return
     */
    public void updateSession(String clusterId, List<Session> sessions)
    {
        SqlSessionFactory factory = Mybatis.getDBSqlSessionFactory();
        if (null == factory)
        {
            return;
        }
        SqlSession sqlSession = null;
        try
        {
            try
            {
                sqlSession = factory.openSession();
                SessionDAO dao = sqlSession.getMapper(SessionDAO.class);
                Session session;
                for (int i = 0; i < sessions.size(); i++)
                {
                    session = sessions.get(i);
                    session.setClusterId(clusterId);
                    dao.updateSession(sessions.get(i));
                }
                sqlSession.commit();
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
            logger.error("updateSession failed, \r\n{}", LogUtils.encodeForLog(e.getMessage()));
        }
    }

    /**
     * 清理60000s还没有建立的呼叫 清理呼叫建立后1800s没有交互的记录
     */
    public void cleanTimeoutSession()
    {
        SqlSessionFactory factory = Mybatis.getDBSqlSessionFactory();
        if(null == factory)
        {
            return;
        }
        SqlSession sqlSession = null;
        try
        {
            try
            {
                sqlSession = factory.openSession();
                SessionDAO dao = sqlSession.getMapper(SessionDAO.class);
                dao.cleanTimeoutSession();
            }
            catch(Exception e)
            {
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
        catch(CommonException e)
        {
            logger.error("cleanTimeoutSession failed, \r\n{}", LogUtils.encodeForLog(e.getMessage()));
        }
        
    }
}
