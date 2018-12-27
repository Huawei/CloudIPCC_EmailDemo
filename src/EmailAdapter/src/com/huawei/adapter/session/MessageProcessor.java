package com.huawei.adapter.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.Message;
import com.huawei.adapter.bean.Session;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.constant.FailureCause;
import com.huawei.adapter.common.data.MessageQueueData;
import com.huawei.adapter.common.util.thread.pool.WorkTask;
import com.huawei.adapter.common.util.utils.LogUtils;

/**
 * 
 * <p>Title: 消息处理 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 */
public class MessageProcessor implements WorkTask
{

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

    private Message message;

    private String key;

    public MessageProcessor(Message message)
    {
        this.message = message;
        this.key = message.getVdnId() + "_" + message.getSessionUserName();

    }

    @Override
    public void run()
    {
        sendMessage();
    }

    private void sendMessage()
    {
        LOGGER.debug(Constants.PRINT_USER + " begin to sendMessage, message is {}",
                new Object[]{message.getVdnId(), message.getSessionUserName(), LogUtils.encodeForLog(message)});

        ChatSession chatSession = ChatSessionManager.getChatSession(message);
        if (null != chatSession)
        {
            if (String.valueOf(message.getAccessCode()).equals(message.getAccessCode()))
            {
                int retCode = chatSession.doSendMessage(message);
                if (FailureCause.SUCCESS.getCause() != retCode)
                {
                    // 发送失败，放入消息状态待更新队列中
                    LOGGER.error(
                            Constants.PRINT_USER + " sendMessage failed. The result is {} "
                                    + "Because call the send interface failed. The message is {}",
                            new Object[]{message.getVdnId(), message.getSessionUserName(), retCode, message});
                    MessageQueueData.addToMessageFailedUpdateQueue(message);
                }
            }
            else
            {
                if (0 == message.getIsCreateSession())
                {
                    // 获取用户会话时，当前用户会话的接入码与消息中的接入码不一致
                    LOGGER.error(
                            Constants.PRINT_USER + " sendMessage failed. "
                                    + "Because the accesscode is not same with get chatsession. The message is {}",
                            new Object[]{message.getVdnId(), message.getSessionUserName(), message});
                    MessageQueueData.addToMessageFailedUpdateQueue(message);
                }
                else
                {
                    // 创建呼叫会话时，实际不会出现该场景
                    LOGGER.error(
                            Constants.PRINT_USER + " sendMessage failed. "
                                    + "Because the accesscode is not same with create chatsession. The message is {}",
                            new Object[]{message.getVdnId(), message.getSessionUserName(), message});
                }
            }
        }
        else
        {
            if (0 == message.getIsCreateSession())
            {
                // 不需要创建会话时，如果从集合中获取不到用户会话，则更新状态为用户未登陆
                LOGGER.error(
                        Constants.PRINT_USER + " sendMessage failed."
                                + "Because get chatsession failed. The message is {}",
                        new Object[]{message.getVdnId(), message.getSessionUserName(), message});
    
            }
            else
            {
                // 创建用户会话失败
                LOGGER.error(
                        Constants.PRINT_USER + " sendMessage failed. "
                                + "Because create chat session failed. The message is {}",
                        new Object[]{message.getVdnId(), message.getSessionUserName(),  message});
                MessageQueueData.addToSessionCleanQueue(new Session(message));
            }
            MessageQueueData.addToMessageFailedUpdateQueue(message);
        }

    }

    @Override
    public int getKey()
    {
        return key.hashCode();
    }

}
