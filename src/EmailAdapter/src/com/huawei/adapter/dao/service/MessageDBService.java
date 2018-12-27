package com.huawei.adapter.dao.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.Message;
import com.huawei.adapter.bean.StatisticInfo;
import com.huawei.adapter.common.config.ConfigList;
import com.huawei.adapter.common.config.ConfigProperties;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.data.GlobalData;
import com.huawei.adapter.common.util.exception.CommonException;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.dao.init.Mybatis;
import com.huawei.adapter.dao.intf.MessageDAO;


/**
 * 
 * <p>Title: 消息数据库操作service</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @author wWX390857
 * @version V1.0 2017-8-16
 * @since
 */
public class MessageDBService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDBService.class);

    
    /**
     * 从数据库中获取待发送的消息
     * 
     * @param clusterId 当前服务器集群名
     * @param createCallCount 最大可以发起呼叫的记录数
     * @param accessList  接入码
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Message> fetchMessage(String clusterId, int createCallCount,
            List<StatisticInfo> accessList)
    {
        SqlSessionFactory factory = Mybatis.getDBSqlSessionFactory();
        if (null == factory)
        {
            return null;
        }
        SqlSession sqlSession = null;
        try
        {
            try
            {
                List<Message> allMessages = new ArrayList<Message>();
                sqlSession = factory.openSession();
                MessageDAO dao = sqlSession.getMapper(MessageDAO.class);
                int size = accessList.size();
                StatisticInfo accessCode;
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("i_clusterId", clusterId);
                param.put("i_createCallCount", createCallCount);
                List<Message> result;
                for (int i = 0; i < size; i++)
                {
                    /**
                     * 1. 获取未建立呼叫的每个接入码的前n个用户的消息，用于创建呼叫 2. 获取已经建立呼叫的每个接入码的前n条消息
                     */
                    accessCode = accessList.get(i);
                    if (GlobalData.isCanCreateCall(accessCode))
                    {
                        param.put("i_createCallCount", createCallCount);
                    }
                    else
                    {
                        // 不能创建呼叫
                        param.put("i_createCallCount", 0);
                    }
                    param.put("i_vdnId", accessCode.getVdnId());
                    param.put("i_accesscode", accessCode.getAccessCode());
                    result = dao.fetchMessage(param);
                    String dbType = ConfigProperties.getKey(ConfigList.DB,
                            "DB_DBTYPE");
                    if (Constants.DB_TYPE_ORACLE.equalsIgnoreCase(dbType))
                    {
                        // oracle数据库
                        if (null != param.get("o_resultCursor"))
                        {
                            result = (List<Message>) param
                                    .get("o_resultCursor");
                        }
                    }
                    if (null == result)
                    {
                        result = new ArrayList<Message>();
                    }
                    allMessages.addAll(result);
                }
                return allMessages;
            }
            catch (Exception e)
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
        catch (CommonException e)
        {
            LOGGER.error("fetchMessage failed, \r\n{}",
                    LogUtils.encodeForLog(e.getMessage()));
        }
        return null;
    }

    /**
     * 批量更新失败的消息状态
     * 
     * @param ids
     * @param status 3表示完成， 4表示失败
     */
    public boolean batchUpdateMessageStatus(List<Long> ids, int status)
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
                MessageDAO dao = sqlSession.getMapper(MessageDAO.class);
                dao.updateMessageStatus(ids, status);
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
            LOGGER.error("batchUpdateMessageStatus failed, \r\n{}",
                    LogUtils.encodeForLog(e.getMessage()));
        }
        return false;
    }

    

}
