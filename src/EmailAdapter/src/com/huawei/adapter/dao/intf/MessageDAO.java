package com.huawei.adapter.dao.intf;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huawei.adapter.bean.Message;

/**
 * 
 * <p>Title: 消息的数据库操作 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public interface MessageDAO
{


    /**
     * 查询待分发的消息
     * @param param
     * @return
     */
    public List<Message> fetchMessage(Map<String, Object> param);



    /**
     * 批量更新消息的状态
     * @param ids
     */
    public void updateMessageStatus(@Param("ids") List<Long> ids, @Param("status") int status);

    /**
     * 更新所有状态为2的记录状态为失败
     * @param cause
     */
    public void updateMessageAllToFailed();

}
