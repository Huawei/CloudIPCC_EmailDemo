
package com.huawei.adapter.common.data;


import com.huawei.adapter.bean.StatisticInfo;
import com.huawei.adapter.common.config.ConfigList;
import com.huawei.adapter.common.config.ConfigProperties;


/**
 * 
 * <p>Title: 全局缓存 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public abstract class GlobalData
{


    /**
     * 当前服务是否为主服务
     */
    private static boolean isMaster = false;



    public static boolean isMaster()
    {
        return isMaster;
    }

    public static void setMaster(boolean isMaster)
    {
        GlobalData.isMaster = isMaster;
    }

 



    /**
     * 根据当前统计信息，判断是否能创建呼叫
     * 
     * @param info
     * @return
     */
    public static boolean isCanCreateCall(StatisticInfo info)
    {
        if (0 == info.getLoggedOnAgents())
        {
            // 登录座席数为0，则不能发起呼叫
            return false;
        }

        if (info.getAvailAgents() > 0)
        {
            // 有可用座席， 则可以发起呼叫
            return true;
        }
        if (info.getQueueSize() < info.getMaxQueueSize())
        {
            // 小于最大排队数，则可以发起呼叫
            return true;
        }
        return false;
    }
    
    public static String getAppUrl()
    {
        StringBuffer sb = new StringBuffer();
        if ("true".equalsIgnoreCase(ConfigProperties.getKey(ConfigList.ICSGW, "ICSGW_ISSSL")))
        {
            sb.append("https://");
        }
        else
        {
            sb.append("http://");
        }
        sb.append(ConfigProperties.getKey(ConfigList.ICSGW, "ICSGW_SERVICE_IP"));
        sb.append(":");
        sb.append(ConfigProperties.getKey(ConfigList.ICSGW, "ICSGW_SERVICE_PORT"));
        sb.append("/icsgateway/resource");
        return sb.toString();
    }
}
