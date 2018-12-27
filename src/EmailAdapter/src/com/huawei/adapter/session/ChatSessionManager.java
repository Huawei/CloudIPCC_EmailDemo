package com.huawei.adapter.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.Message;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.constant.FailureCause;
import com.huawei.adapter.common.util.utils.LogUtils;

/**
 * 
 * <p>Title: 用户会话管理 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class ChatSessionManager
{
    private static final Logger logger = LoggerFactory.getLogger(ChatSessionManager.class);

    /**
     * 设备会话管理集 
     * key为vdnId + "_" + deviceId 设备信息
     */
    private static Map<String, ChatSession> chatSessions = new ConcurrentHashMap<String, ChatSession>();

    /**
     * 用户与设备的关联关系
     * key为vdnId + "_" + uerName  用户信息
     * value为vdnId + "_" + deviceId 设备信息
     */
    private static Map<String, String> userNameWithDeviceIdMap = new ConcurrentHashMap<String, String>(); 

    private static byte[] lockObject = new byte[0];

    
    /**
     * 根据用户名获取用户会话信息
     * @param vdnId
     * @param userName
     * @return
     */
    public static ChatSession getChatSessionByUserName(int vdnId, String userName)
    {
        String key = vdnId + "_" + userName;
        String deviceString = userNameWithDeviceIdMap.get(key);
        if (null != deviceString)
        {
            ChatSession session = chatSessions.get(deviceString);
            if (null == session)
            {
                //如果用户会话都已经没有，则userNameWithDeviceIdMap应该清楚用户与设备的关联关系
                userNameWithDeviceIdMap.remove(key);
            }
            return session;
        }
        else
        {
            return null;
        }
    }

    /**
     * 获取当前创建的用户会话数
     * @return
     */
    public static int getCurrentChatSessionCount()
    {
        return chatSessions.size();
    }

    /**
     * 获取一个用户会话，获取会话失败时，尝试创建一个会话.
     * @param message 消息内容
     * @return ChatSession null表示创建或获取会话失败
     */
    public static ChatSession getChatSession(Message message)
    {
        // 构建key
        String key = message.getVdnId() + "_" + message.getSessionUserName();
        if (0 == message.getIsCreateSession())
        {
            // 不需要创建会话
            return chatSessions.get(key);
        }
        else
        {
            // 需要创建会话
            return createChatSession(key, message);
        }
    }

    /**
     * 构建用户会话
     * @param key chatSessions的主键 设备信息
     * @param alarm 消息内容
     * @return null表示用户会话创建失败
     */
    private static ChatSession createChatSession(String key, Message message)
    {
        ChatSession user = new ChatSession(message);
        synchronized (lockObject)
        {
            if (chatSessions.containsKey(key))
            {
                // 用户会话已经存在，则返回创建会话失败
                logger.error(
                        Constants.PRINT_USER + " createChatSession failed. "
                                + "Because the chat session has exited when create chat session. The alarm is {}",
                        new Object[]{LogUtils.encodeForLog(message.getVdnId()), 
                                LogUtils.encodeForLog(message.getSessionUserName()),
                                LogUtils.encodeForLog(message.toString())});
                return null;
            }
            int ret = user.doCreateCall();
            if (FailureCause.SUCCESS.getCause() == ret)
            {
                chatSessions.put(key, user);
                userNameWithDeviceIdMap.put(user.getVdnId() + "_" + user.getUserName(), key);
                return user;
            }
            return null;
        }
    }

    /**
     * 删除用户会话
     * @param vdnId vdnId
     * @param deviceId 设备编号
     * @return
     */
    public static void delChatSession(int vdnId, String deviceId)
    {
        String key = vdnId + "_" + deviceId;
        synchronized (lockObject)
        {
            ChatSession session = chatSessions.remove(key);
            if (session != null)
            {
                userNameWithDeviceIdMap.remove(vdnId + "_" + session.getUserName());
            }
        }
    }

    public static Map<String, ChatSession> getAllChatSessions()
    {
        return chatSessions;
    }

}
