
package com.huawei.adapter.bean;


/**
 * 
 * <p>Title:  服务器状态信息</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class ServerInfoBean
{
    /**
     * 服务器唯一标记，系统初始化的时候生成
     */
    private String clusterId;

    /**
     * 集群名称
     */
    private String clusterName;

    /**
     * 服务器状态
     * 0:表示服务器已经失去连接
     * 1：表示服务器还连接
     */
    private int severStatus;

    public int getSeverStatus()
    {
        return severStatus;
    }

    public void setSeverStatus(int severStatus)
    {
        this.severStatus = severStatus;
    }

    public String getClusterId()
    {
        return clusterId;
    }

    public void setClusterId(String clusterId)
    {
        this.clusterId = clusterId;
    }
   
    public String getClusterName()
    {
        return clusterName;
    }

    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{clusterId:").append(clusterId).append(",");
        sb.append("clusterName:").append(clusterName).append(",");
        sb.append("severStatus:").append(severStatus).append("}");
        return sb.toString();
    }
    
}
