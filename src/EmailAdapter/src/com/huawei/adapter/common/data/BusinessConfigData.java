package com.huawei.adapter.common.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.huawei.adapter.bean.AccessCodeInfo;

/**
 * 
 * <p>Title: 业务配置数据 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 */
public abstract class BusinessConfigData
{

    /**
     * key为接入码
     * AccessCodeInfo为接入信息
     */
    private static Map<String, AccessCodeInfo> accessMap = new HashMap<String, AccessCodeInfo>();

    public static void setAccessMap(Map<String, AccessCodeInfo> accessMap)
    {
        BusinessConfigData.accessMap = accessMap;
    }

    
    public static int getVdnIdByAccessCode(String accessCode)
    {
        AccessCodeInfo accessCodeInfo = accessMap.get(accessCode);
        if (null != accessCodeInfo)
        {
            return accessCodeInfo.getVdnId();
        }
        return 0;
    }
   
    /**
     * 获取配置的接入码
     */
    public static List<AccessCodeInfo> getAccessCodeList()
    {
        Iterator<Entry<String, AccessCodeInfo>> iterator = accessMap.entrySet().iterator();
        Entry<String, AccessCodeInfo> entry;
        AccessCodeInfo access;
        List<AccessCodeInfo> list = new ArrayList<AccessCodeInfo>();
        while (iterator.hasNext())
        {
            entry =  iterator.next();
            access = entry.getValue();
            list.add(access);
        }
        return list;
    }
}
