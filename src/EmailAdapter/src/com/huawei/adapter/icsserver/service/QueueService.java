
package com.huawei.adapter.icsserver.service;

import java.util.Map;

import com.huawei.adapter.common.data.GlobalData;
import com.huawei.adapter.icsserver.bean.StatisticParam;
import com.huawei.adapter.icsserver.http.Request;

public class QueueService
{
    public Map<String, Object> getSkillStatistic(StatisticParam param)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(GlobalData.getAppUrl()).append("/queuedevice/");
        sb.append("getskillstatistic");
        return Request.post(sb.toString(), param, null);
    }
}
