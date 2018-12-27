
package com.huawei.adapter.dao.intf;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huawei.adapter.bean.Session;

/**
 * 
 * <p>Title: 呼叫会话管理 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 */
public interface SessionDAO
{
    /**
     * 删除所有用户会话
     */
    public void deleteAllSession();

    /**
     * 批量删除会话
     * @param clusterId 服务器集群名
     * @param sessions 待删除会话集合
     */
    public void batchDeleteSession(@Param("clusterId") String clusterId,
            @Param("sessions") List<Session> sessions);

    /**
     * 更新会话
     * @param session 待更新的会话
     */
    public void updateSession(Session session);

    /**
     * 清理60000s还没有建立的呼叫
     * 清理呼叫建立后1800s没有交互的记录
     */
    public void cleanTimeoutSession();
}
