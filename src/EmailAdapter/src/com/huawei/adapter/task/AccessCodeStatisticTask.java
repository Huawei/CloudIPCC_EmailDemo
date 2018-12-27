
package com.huawei.adapter.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.AccessCodeInfo;
import com.huawei.adapter.bean.StatisticInfo;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.data.BusinessConfigData;
import com.huawei.adapter.common.data.GlobalData;
import com.huawei.adapter.common.data.StatisticInfoData;
import com.huawei.adapter.common.util.exception.CommonException;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.icsserver.bean.StatisticParam;
import com.huawei.adapter.icsserver.service.QueueService;

/**
 * 
 * <p> Title: 定时更新接入码的统计信息</p>
 * <p>Description: </p>
 * <pre></pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class AccessCodeStatisticTask extends Thread
{
    private static final Logger LOG = LoggerFactory.getLogger(AccessCodeStatisticTask.class);

    private static AccessCodeStatisticTask instance;

    private boolean isAlive;

    private QueueService queueService = new QueueService();

    private boolean isGetStatisticOk = true;

    private AccessCodeStatisticTask()
    {
    }

    @Override
    public void run()
    {
        while (isAlive)
        {
            if (GlobalData.isMaster())
            {
                // 只有主服务器，才需要查询
                doQueryStatistic();
            }
            try
            {
                sleep(Constants.STATISTIC_UPDATE_INTERVAL);
            }
            catch (InterruptedException e)
            {
                LOG.error("sleep failed, \r\n {}",
                        LogUtils.encodeForLog(e.getMessage()));
            }
        }
    }

    /**
     * 执行查询统计信息
     */
    @SuppressWarnings("unchecked")
    private void doQueryStatistic()
    {
        StatisticParam param;
        Map<String, Object> resultMap;
        String retcode;
        Map<String, Map<String, Object>> contentMap;
        List<StatisticParam> queryParam = getQueryParam();
        List<StatisticInfo> tempStatisticList = new ArrayList<StatisticInfo>();
        for (int i = 0; i < queryParam.size(); i++)
        {
            param = queryParam.get(i);
            try
            {
                try
                {
                    resultMap = queueService.getSkillStatistic(param);
                    retcode = String.valueOf(resultMap.get("retcode"));
                    if ("0".equals(retcode))
                    {
                        // 查询成功，解析结果
                        contentMap = (Map<String, Map<String, Object>>) resultMap.get("result");
                        doParseResult(param.getVdnId(), contentMap, tempStatisticList);
                    }
                    else
                    {
                        LOG.error("VdnId [{}] AccessCode[{}] getSkillStatistic.  return retcode is {} and msg is {}",
                                new Object[]{param.getVdnId(),
                                        param.getAccessCodes(), 
                                        retcode,
                                        String.valueOf(resultMap.get("message"))});
                    }
                    if (!isGetStatisticOk)
                    {
                        isGetStatisticOk = true;
                        LOG.warn("Retrive to get ics statistic success.");
                    }
                }
                catch (Exception e)
                {
                    throw new CommonException(e);
                }
            }
            catch (CommonException e)
            {
                if (isGetStatisticOk)
                {
                    LOG.error("GetSkillStatistic occurs exception: \r\n {}", LogUtils.encodeForLog(e));
                }
                isGetStatisticOk = false;
            }
        }
        StatisticInfoData.setAccessStatisticInfo(tempStatisticList);
    }

    /**
     * 解析ICSGW返回的结果
     * 
     * @param vdnId
     * @param contentMap
     * @param tempStatisticList
     * @return
     */
    private void doParseResult(int vdnId,Map<String, Map<String, Object>> contentMap,List<StatisticInfo> tempStatisticList)
    {
        StatisticInfo statisticInfo;
        Iterator<Entry<String, Map<String, Object>>> iterator = contentMap.entrySet().iterator();
        Entry<String, Map<String, Object>> entry;
        Map<String, Object> tempValue;
        int loggedOnAgents;// 当前登录座席数
        int availAgents;// 当前可用座席数
        int queueSize; // 当前排队座席数
        int maxQueueSize;// 队列最大排队人数
        while (iterator.hasNext())
        {
            entry = iterator.next();
            tempValue = entry.getValue();
            loggedOnAgents = Integer.valueOf(String.valueOf(tempValue.get("loggedOnAgents")));
            availAgents = Integer.valueOf(String.valueOf(tempValue.get("availAgents")));
            queueSize = Integer.valueOf(String.valueOf(tempValue.get("queueSize")));
            maxQueueSize = Integer.valueOf(String.valueOf(tempValue.get("maxQueueSize")));
            statisticInfo = new StatisticInfo(vdnId, entry.getKey(), loggedOnAgents, availAgents);
            statisticInfo.setQueueSize(queueSize);
            statisticInfo.setMaxQueueSize(maxQueueSize);
            tempStatisticList.add(statisticInfo);
        }
    }

    /**
     * 获取统计信息查询条件
     * 
     * @return
     */
    private List<StatisticParam> getQueryParam()
    {
        List<AccessCodeInfo> accessList = BusinessConfigData.getAccessCodeList();
        List<StatisticParam> statisticParams = new ArrayList<StatisticParam>();
        if (0 == accessList.size())
        {
            return statisticParams;
        }
        int length = accessList.size();
        AccessCodeInfo access;
        Map<Integer, List<String>> accessCodeMap = new HashMap<Integer, List<String>>();
        List<String> tempVdns;
        for (int i = 0; i < length; i++)
        {
            access = accessList.get(i);
            tempVdns = accessCodeMap.get(Integer.valueOf(access.getVdnId()));
            if (null == tempVdns)
            {
                tempVdns = new ArrayList<String>();
            }
            tempVdns.add(access.getAccessCode());
            accessCodeMap.put(Integer.valueOf(access.getVdnId()), tempVdns);
        }

        Iterator<Entry<Integer, List<String>>> iterator = accessCodeMap.entrySet().iterator();
        StatisticParam param;
        Entry<Integer, List<String>> entry;
        while (iterator.hasNext())
        {
            entry = iterator.next();
            param = new StatisticParam();
            param.setAccessCodes(entry.getValue());
            param.setVdnId(entry.getKey());
            statisticParams.add(param);
        }
        return statisticParams;
    }

    /**
     * 定时查询技能队列上的统计信息
     * 
     */
    public static boolean begin()
    {
        if (null != instance)
        {
            return true;
        }

        instance = new AccessCodeStatisticTask();
        instance.isAlive = true;
        instance.setDaemon(true);
        instance.setName("AccessCodeStatisticTask");
        instance.start();
        return true;
    }

    public static void end()
    {
        if (null != instance)
        {
            instance.isAlive = false;
        }
    }
}
