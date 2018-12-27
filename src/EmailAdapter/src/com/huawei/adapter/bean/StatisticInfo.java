
package com.huawei.adapter.bean;

/**
 * 
 * <p>Title: 接入码统计信息 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class StatisticInfo
{

    private int vdnId;

    private String accessCode;

    public StatisticInfo(int vdnId, String accessCode,
            int loggedOnAgents, int availAgents)
    {
        this.loggedOnAgents = loggedOnAgents;
        this.availAgents = availAgents;
        this.vdnId = vdnId;
        this.accessCode = accessCode;
    }

    /** 当前注册到该ACD组的Agent数目 */
    private int loggedOnAgents;

    /** 该ACD组中当前可用的Agent数目 */
    private int availAgents;

    /**
     * 当前队列排队数
     */
    private int queueSize;

    /**
     * 最大排队数
     */
    private int maxQueueSize;

    public int getVdnId()
    {
        return vdnId;
    }

    public void setVdnId(int vdnId)
    {
        this.vdnId = vdnId;
    }

    public String getAccessCode()
    {
        return accessCode;
    }

    public void setAccessCode(String accessCode)
    {
        this.accessCode = accessCode;
    }

    public int getLoggedOnAgents()
    {
        return loggedOnAgents;
    }

    public int getAvailAgents()
    {
        return availAgents;
    }

    public int getQueueSize()
    {
        return queueSize;
    }

    public int getMaxQueueSize()
    {
        return maxQueueSize;
    }

    public void setQueueSize(int queueSize)
    {
        this.queueSize = queueSize;
    }

    public void setMaxQueueSize(int maxQueueSize)
    {
        this.maxQueueSize = maxQueueSize;
    }

}
