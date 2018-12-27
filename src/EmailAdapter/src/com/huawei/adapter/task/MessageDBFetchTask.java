package com.huawei.adapter.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.Message;
import com.huawei.adapter.bean.StatisticInfo;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.data.BusinessConfigData;
import com.huawei.adapter.common.data.GlobalData;
import com.huawei.adapter.common.data.StatisticInfoData;
import com.huawei.adapter.common.util.exception.CommonException;
import com.huawei.adapter.common.util.thread.pool.ThreadPool;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.dao.service.MessageDBService;
import com.huawei.adapter.session.ChatSessionManager;
import com.huawei.adapter.session.MessageProcessor;

/**
 * 
 * <p>Title: 从DB中定时获取待分发的消息 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class MessageDBFetchTask extends Thread
{
    private static final Logger LOG = LoggerFactory.getLogger(MessageDBFetchTask.class);

    private static MessageDBFetchTask instance;

    /**
     * 线程是否启动
     */
    private boolean isAlive = false;

    /**
     * 服务器集群名
     */
    private String clusterId;

    /**
     * 每个接入码可以发起呼叫的记录数
     */
    private int createCallCount;

    /**
     * 会话数
     */
    private int maxChatSessions;

    /**
     * 消息处理线程池
     */
    private ThreadPool messageProcessorPool;

    /**
     * 可以创建呼叫的接入码数
     */
    private int accessNumForCreateNum;

    private MessageDBService messageDBService = new MessageDBService();

    private MessageDBFetchTask(String clusterId, int createCallCount, int maxThreads, int maxChatSessions)
    {
        this.clusterId = clusterId;
        this.createCallCount = createCallCount;
        this.maxChatSessions = maxChatSessions;
        this.messageProcessorPool = new ThreadPool("message-processor-pool", Constants.MIN_MESSAGE_PROCESSOR_THREADS, maxThreads, 0);

    }

    @Override
    public void run()
    {
        while (isAlive)
        {
            try
            {
                try
                {
                    doFetchMessage();
                }
                catch (Exception e)
                {
                    throw new CommonException(e);
                }
            }
            catch (CommonException e)
            {
                LOG.error("occurs unkown exception : \r\n {}", 
                        LogUtils.encodeForLog(e.getMessage()));
            }
        }
    }

    /**
     * 定时读取消息进行分发处理
     */
    private void doFetchMessage()
    {
        if (GlobalData.isMaster())
        {
            // 主机时，才需要从数据库获取待分发的消息
            List<Message> list;

            List<StatisticInfo> accessList = getValidAccessCodes();
            int currentChatSession = ChatSessionManager.getCurrentChatSessionCount(); // 当前已经创建的用户会话数
            int surplusSession = maxChatSessions - currentChatSession; // 适配器剩余，可用会话数
            if (surplusSession > (createCallCount * accessNumForCreateNum))
            {
                // 剩余会话数大于每次创建的会话数
                list = messageDBService.fetchMessage(clusterId, createCallCount, accessList);
            }
            else
            {
                int count = surplusSession / accessNumForCreateNum; // 获取每个接入码可以创建的最大呼叫数
                list = messageDBService.fetchMessage(clusterId, count, accessList);
            }
            if (null == list || list.isEmpty())
            {
                doSleep(Constants.MASTER_DB_FETCH_INTERVAL);
            }
            else
            {
                int length = list.size();
                LOG.debug(Constants.PRINT_USER + " Fetch {} alarms from database",
                        new Object[]{-1, -1,
                        LogUtils.encodeForLog(length)});
                for (int i = 0; i < length; i++)
                {
                    Message message = list.get(i);
                    message.setVdnId(BusinessConfigData.getVdnIdByAccessCode(message.getAccessCode()));
                    LOG.debug( Constants.PRINT_USER + " begin to put into MessageProcessor, message {}",
                            new Object[]{LogUtils.encodeForLog(message.getVdnId()), 
                                    LogUtils.encodeForLog(message.getSessionUserName()), 
                                    LogUtils.encodeForLog(message.toString())});
                    messageProcessorPool.addTask(new MessageProcessor(message));
                }
            }
        }
        else
        {
            // 备服务器每分钟执行1次
            doSleep(Constants.SLAVE_DB_FETCH_INTERVAL);
        }
    }

    private void doSleep(long times)
    {
        try
        {
            sleep(times);
        }
        catch (InterruptedException e)
        {
            LOG.error("sleep failed, \r\n {}",
                    LogUtils.encodeForLog(e.getMessage()));
        }
    }

    /**
     * 获取有效的接入码
     * 
     * @return
     */
    private List<StatisticInfo> getValidAccessCodes()
    {
        List<StatisticInfo> tempList = StatisticInfoData.getAccessStatisticInfo();
        accessNumForCreateNum = 0;
        int size = tempList.size();
        for (int i = 0; i < size; i++)
        {
            if (GlobalData.isCanCreateCall(tempList.get(i)))
            {
                //有可用座席，可以创建呼叫
                accessNumForCreateNum++;
            }
        }
        return tempList;
    }

    /**
     * 启动告从数据库取出消息进行分发
     * @param clusterId 当前服务器集群号
     * @param createCallCount 最大可以发起呼叫的记录数
     * @param maxThreads 消息处理线程数
     * @param maxChatSessions 适配器运行的最大会话
     */
    public static void begin(String clusterId, int createCallCount,  int maxThreads, int maxChatSessions)
    {
        if (null != instance)
        {
            return;
        }
        instance = new MessageDBFetchTask(clusterId, createCallCount, maxThreads, maxChatSessions);
        instance.isAlive = true;
        instance.setName("MessageDBFetchTask");
        instance.start();
    }

    public static void end()
    {
        if (null != instance)
        {
            instance.isAlive = false;
        }
    }

}
