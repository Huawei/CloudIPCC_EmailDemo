
package com.huawei.adapter.dao.intf;

import org.apache.ibatis.annotations.Param;

/**
 * 
 * <p>Title: 服务器状态更新 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public interface ServerStatusDAO
{
    /**
     * 更新服务器状态
     * 
     * @param clusterId
     *            当前服务器集群名
     * @return
     */
    public int updateServerStatus(@Param("clusterId") String clusterId);
}
