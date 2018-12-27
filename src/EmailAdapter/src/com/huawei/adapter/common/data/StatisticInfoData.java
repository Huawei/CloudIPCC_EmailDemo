package com.huawei.adapter.common.data;

import java.util.ArrayList;
import java.util.List;

import com.huawei.adapter.bean.StatisticInfo;

/**
 * 
 * <p>Title: 接入码统计信息 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public abstract class StatisticInfoData
{
    /**
     * 接入码信息
     */
    private static List<StatisticInfo> accessStatisList = new ArrayList<StatisticInfo>();

    /**
     * 获取所有接入码统计信息
     * @return
     */
    public static List<StatisticInfo> getAccessStatisticInfo()
    {
        return accessStatisList;
    }

    /**
     * 更新所有接入码统计信息
     * @param accessStatistList
     */
    public static void setAccessStatisticInfo(List<StatisticInfo> accessStatisList)
    {
        StatisticInfoData.accessStatisList = accessStatisList;
    }
}
