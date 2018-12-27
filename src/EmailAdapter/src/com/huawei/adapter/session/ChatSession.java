
package com.huawei.adapter.session;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.Message;
import com.huawei.adapter.bean.Session;
import com.huawei.adapter.common.config.ConfigList;
import com.huawei.adapter.common.config.ConfigProperties;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.constant.FailureCause;
import com.huawei.adapter.common.data.MessageQueueData;
import com.huawei.adapter.common.util.exception.CommonException;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.icsserver.bean.CallInfoParam;
import com.huawei.adapter.icsserver.bean.ChatMessageParam;
import com.huawei.adapter.icsserver.bean.UserParam;
import com.huawei.adapter.icsserver.service.UserService;

/**
 * 
 * <p>Title: 用户会话 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class ChatSession
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatSession.class);

    /**
     * 接口调用成功
     */
    private static final String SUCCESS = "0";

    /**
     * 登录的锁
     */
    private byte[] loginObject = new byte[0];

    /**
     * vdn
     */
    private int vdnId;

    /**
     * session会话的实际用户名
     */
    private String sessionUserName;
    
    private String realCaller;
    
    private String displayName;
    
    private String callData;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户接入码
     */
    private String accessCode;

    private UserService userService;

    /**
     * 用户是否登录
     */
    private boolean isLogined = false;

    /**
     * 呼叫是否已经建立
     */
    private boolean isConnected = false;

    /**
     * 呼叫的callId
     */
    private String currentCallId;

    /**
     * 待发送的消息
     */
    private Queue<Message> messageList = new LinkedList<Message>();


    /**
     * 创建呼叫的结果
     */
    private int doCreateResult;

    /**
     * 心跳发送失败次数
     */
    private int doHeartBeatFailTimes = 0;



    /**
     * 登录时间
     */
    private String loginTime;
    
    /**
     * 处理用户签出事件的锁
     */
    private final byte []dealLogoutEventLock= new byte[0];
    
    /**
     * 是否已经清理用户会话
     */
    private boolean hasCleanUserSession = false;

    
    private static String generateUserName()
    {
        char[] chars = "0123456789abcdefghijklmnopqrwtuvzxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        char[] saltchars = new char[20];
        SecureRandom secureRandom = new SecureRandom();
        for(int i = 0; i < 20; i++)
        {
            saltchars[i] = chars[secureRandom.nextInt(62)];
        }
        return new String(saltchars);
    }

    /**
     * 
     * @param vdnId vdnId
     * @param deviceId 设备编号
     * @param accessCode 接入码
     * @throws RestClientException
     */
    public ChatSession(Message message)
    {
        this.vdnId = message.getVdnId();
        this.sessionUserName = message.getSessionUserName();
        this.accessCode = message.getAccessCode();
        this.userName = generateUserName();
        this.realCaller = message.getRealCaller();
        this.displayName = message.getDisplayName();
        this.callData = message.getCallData();
        this.userService = new UserService(vdnId, userName);
    }

    /**
     * 发起呼叫
     * 
     * @param calldata 随路数据
     * @return
     */
    public int doCreateCall()
    {
        synchronized (loginObject)
        {
            try
            {
                try
                {
                    if (doLogin())
                    {
                        // 登录成功
                        CallInfoParam callInfo = new CallInfoParam();
                        callInfo.setMediaType(Constants.MEDIA_TYPE);
                        callInfo.setAccessCode(accessCode);
                        if (null != this.callData && this.callData.length() > 1024)
                        {
                            callInfo.setCallData(this.callData.substring(0, 1024));
                        }
                        else
                        {
                            callInfo.setCallData(this.callData);
                        }
                        if (null != this.realCaller && this.realCaller.length() > 64 )
                        {
                            callInfo.setRealCaller(this.realCaller.substring(0, 64));
                        }
                        else
                        {
                            callInfo.setRealCaller(this.realCaller);
                        }
                        if (null != this.displayName && this.displayName.length() > 64 )
                        {
                            callInfo.setDisplayName(this.displayName.substring(0, 64));
                        }
                        else
                        {
                            callInfo.setDisplayName(this.displayName);
                        }
                        Map<String, Object> resultMap = userService.doCreateCall(callInfo);
                        String retcode = String.valueOf(resultMap.get("retcode"));
                        if (SUCCESS.equals(retcode))
                        {
                            // 呼叫成功, 获取当前callId
                            currentCallId = String.valueOf(resultMap.get("result"));
                            return FailureCause.SUCCESS.getCause();
                        }
                        else
                        {
                            // 发起呼叫失败，则直接签出当前用户
                            String msg = String.valueOf(resultMap.get("message"));
                            LOGGER.error(Constants.PRINT_USER + " failed and return retcode is {} and msg is {}",
                                    new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName), 
                                            LogUtils.encodeForLog(retcode), LogUtils.encodeForLog(msg)});
                            doCreateResult = FailureCause.CALL_CREATE_FAILED.getCause();
                            doLogout();
                        }
                    }
                }
                catch (Exception e)
                {
                    throw new CommonException(e);
                }
            }
            catch (CommonException e)
            {
                doLogout();
                doCreateResult = FailureCause.CALL_CREATE_FAILED.getCause();
                LOGGER.error(Constants.PRINT_USER + " doCreateCall occurs exception:{}",
                        new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName), 
                                LogUtils.encodeForLog(e.getMessage())});
            }
            return doCreateResult;
        }

    }

    /**
     * 进行登出
     * 
     * @return true表示登出成功；false表示登出失败
     */
    public boolean doLogout()
    {
        isLogined = false;
        try
        {
            try
            {
                Map<String, Object> resultMap = userService.logout();
                String retcode = String.valueOf(resultMap.get("retcode"));
                if (SUCCESS.equals(retcode))
                {
                    // 登出成功
                    return true;
                }
                else
                {
                    // 登出失败
                    String msg = String.valueOf(resultMap.get("message"));
                    LOGGER.error(Constants.PRINT_USER + " doLogout failed and return retcode is {} and msg is {}",
                            new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName), 
                                    LogUtils.encodeForLog(retcode), LogUtils.encodeForLog(msg)});
                }
            }
            catch (Exception e)
            {
                throw new CommonException(e);
            }
        }
        catch (CommonException e)
        {
            LOGGER.error(Constants.PRINT_USER + " doLogout occurs exception:{}",
                    new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName), 
                            LogUtils.encodeForLog(e.getMessage())});
        }
        return false;
    }

    /**
     * 进行登录
     * 
     * @return true表示登录成功；false表示登录失败
     */
    private boolean doLogin()
    {
        try
        {
            try
            {
                LOGGER.debug(Constants.PRINT_USER + " begin to doLogin", 
                        new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName)});
                UserParam userParam = new UserParam();
                userParam.setPushUrl(ConfigProperties.getKey(ConfigList.BASIC, "PUSH_URL"));
                Map<String, Object> resultMap = userService.loginEx(userParam);
                String retcode = String.valueOf(resultMap.get("retcode"));
                if (SUCCESS.equals(retcode))
                {
                    // 登录成功
                    @SuppressWarnings("unchecked")
                    Map<String, Object> contentMap = (Map<String, Object>) resultMap.get("result");
                    loginTime = String.valueOf(contentMap.get("loginTime"));
                    isLogined = true;
                    return true;
                }
                else
                {
                    // 登录失败
                    String msg = String.valueOf(resultMap.get("message"));
                    LOGGER.error(Constants.PRINT_USER + " doLogin failed and return retcode is {} and msg is {}",
                            new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName), 
                                    LogUtils.encodeForLog(retcode), LogUtils.encodeForLog(msg)});
                }
            }
            catch (Exception e)
            {
                throw new CommonException(e);
            }
        }
        catch (CommonException e)
        {
            LOGGER.error(Constants.PRINT_USER + " dologin occurs exception:{}",
                    new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName), 
                            LogUtils.encodeForLog(e.getMessage())});
        }
        doCreateResult = FailureCause.LOGIN_FAILED.getCause();
        return false;
    }

    /**
     * 呼叫建立事件处理
     */
    public void doCallConnectEvent(String callId)
    {
        if (!callId.equals(currentCallId))
        {
            LOGGER.error(
                    Constants.PRINT_USER + " not doCallConnectEvent, "
                            + "because the callId is invalid. The callId is [{}]",
                    new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName), LogUtils.encodeForLog(callId)});
            return;
        }
        LOGGER.debug(Constants.PRINT_USER + " doCallConnectEvent.", 
                new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName)});
        MessageQueueData.addToSessionUpdateQueue(new Session(vdnId, sessionUserName, accessCode));
        
        
        isConnected = true; // 设置呼叫已经建立
        int ret;
        while (!messageList.isEmpty())
        {
            Message message = messageList.poll();
            if(null != message)
            {
                ret = doSendMessage(message);
                if (FailureCause.SUCCESS.getCause() != ret)
                {
                    // 发送失败
                    MessageQueueData.addToMessageFailedUpdateQueue(message);
                }
            }
        }
    }

    /**
     * 呼叫释放事件处理 签出用户，并更新待发送消息状态为呼叫已经结束
     */
    public void doCallDisConnectEvent(String callId)
    {
        if (!callId.equals(currentCallId))
        {
            LOGGER.error(
                    Constants.PRINT_USER + " not doCallDisConnectEvent, "
                            + "because the callId is invalid. The callId is [{}]",
                    new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName), LogUtils.encodeForLog(callId)});
            return;
        }
        LOGGER.debug(Constants.PRINT_USER + " doCallDisConnectEvent, dologout the current user.",
                new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName)});
        isConnected = false;
        doLogout();
        while (!messageList.isEmpty())
        {
            Message message = messageList.poll();
            if(null != message)
            {
                MessageQueueData.addToMessageFailedUpdateQueue(message);
            }
        }
    }

    /**
     * 呼叫建立失败事件处理 签出用户，并更新待发送消息状态为呼叫建立失败
     */
    public void doCallCreateFailedEvent(String callId)
    {
        if (!callId.equals(currentCallId))
        {
            LOGGER.error(
                    Constants.PRINT_USER + " not doCallCreateFailedEvent, "
                            + "because the callId is invalid. The callId is [{}]",
                    new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName), LogUtils.encodeForLog(callId)});
            return;
        }
        LOGGER.debug(Constants.PRINT_USER + " doCallCreateFailedEvent, dologout the current user.",
                new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName)});
        doLogout();
        while (!messageList.isEmpty())
        {
            Message message = messageList.poll();
            if(null != message)
            {
                MessageQueueData.addToMessageFailedUpdateQueue(message);
            }
        }
    }

    /**
     * 处理签出事件
     */
    public void doLogoutEvent(int cause)
    {
        synchronized (dealLogoutEventLock)
        {
            LOGGER.debug(Constants.PRINT_USER + " doLogoutEvent.", 
                    new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName)});
            isLogined = false;
            while (!messageList.isEmpty())
            {
                Message message = messageList.poll();
                if(null != message)
                {
                    MessageQueueData.addToMessageFailedUpdateQueue(message);
                }
            }
            
            if (!hasCleanUserSession)
            {
                MessageQueueData.addToSessionCleanQueue(new Session(vdnId, sessionUserName, accessCode));
                hasCleanUserSession = true;
            }
        }
       
    }

   
    /**
     * 发送消息
     * @param message
     * @return
     */
    public int doSendMessage(Message message)
    {
        if (!isLogined)
        {
            // 用户未登录
            return FailureCause.NOT_LOGIN.getCause();
        }
        
        if (isConnected)
        {
            if ("false".equalsIgnoreCase(ConfigProperties.getKey(ConfigList.ICSGW, "IS_NEED_SEND_MESSAGE")))
            {
                //不需要发送消息
                MessageQueueData.addToMessageCleanQueue(message.getId());
                return FailureCause.SUCCESS.getCause();
            }
            // 呼叫已经建立，则直接发送
            try
            {
                try
                {
                    LOGGER.debug(Constants.PRINT_USER + "begin to sendAlarmMessage. Id is {}",
                            new Object[]{LogUtils.encodeForLog(vdnId), 
                                    LogUtils.encodeForLog(sessionUserName), 
                                    LogUtils.encodeForLog(message.getId())});
                    ChatMessageParam param = new ChatMessageParam();
                    param.setCallId(currentCallId);
                    param.setContent(String.valueOf(message.getId()));
                    Map<String, Object> resultMap = userService.doSendMessage(param);
                    String retcode = String.valueOf(resultMap.get("retcode"));
                    if (SUCCESS.equals(retcode))
                    {
                        
                        //发送成功，则直接删除
                        MessageQueueData.addToMessageCleanQueue(message.getId());
                        return FailureCause.SUCCESS.getCause();
                    }
                    else
                    {
                        // 发送失败
                        String msg = String.valueOf(resultMap.get("message"));
                        LOGGER.error(Constants.PRINT_USER + "doLogin failed and return retcode is {} and msg is {}",
                                new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName), 
                                        LogUtils.encodeForLog(retcode), LogUtils.encodeForLog(msg)});
                        return FailureCause.CALL_CREATE_FAILED.getCause();
                    }
                }
                catch (Exception e)
                {
                    throw new CommonException(e);
                }
            }
            catch (CommonException e)
            {
                LOGGER.error(Constants.PRINT_USER + "doSendAlarm occurs exception: \r\n {}",
                        new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName), 
                                LogUtils.encodeForLog(e.getMessage())});
                return FailureCause.CALL_SEND_FAILED.getCause();
            }

        }
        else
        {
            // 如果呼叫未建立，则将消息保存在用户待发送消息中，等呼叫建立后发送
            if (messageList.offer(message))
            {
                return FailureCause.SUCCESS.getCause();
            }
            LOGGER.error(Constants.PRINT_USER + "doSendAlarm occurs exception:{}",
                    new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(sessionUserName), "add alarm to queue failed."});
            return FailureCause.CALL_SEND_FAILED.getCause();
        }
    }

    /**
     * 发送心跳
     */
    public void doHeartBeat()
    {
        try
        {
            try
            {
                Map<String, Object> resultMap = userService.doHeartBeat();
                String retcode = String.valueOf(resultMap.get("retcode"));
                if(SUCCESS.equals(retcode))
                {
                    if(doHeartBeatFailTimes != 0)
                    {
                        doHeartBeatFailTimes = 0;
                        LOGGER.warn(Constants.PRINT_USER + "retrive doHeartBeat",
                                new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(userName)});
                    }
                    return;
                }
                else
                {
                    //心跳检测失败
                    doLogoutEvent(FailureCause.NOT_LOGIN.getCause());
                    return;
                }
            }
            catch(Exception e)
            {
                throw new CommonException(e);
            }
        }
        catch(CommonException e)
        {
            doHeartBeatFailTimes++;
            LOGGER.error(Constants.PRINT_USER + " doHeartBeat [{}] times. exception : \r\n {}", 
                    new Object[]{LogUtils.encodeForLog(vdnId), LogUtils.encodeForLog(userName), 
                            LogUtils.encodeForLog(doHeartBeatFailTimes), LogUtils.encodeForLog(e.getMessage())});
        }
        
        if(Constants.HEARTBEAT_FAILED == doHeartBeatFailTimes)
        {
            //连续失败三次，则签出用户
            doLogoutEvent(FailureCause.NOT_LOGIN.getCause());
        }
    }

    public String getAccessCode()
    {
        return accessCode;
    }

    public String getLoginTime()
    {
        return loginTime;
    }

    public int getVdnId()
    {
        return vdnId;
    }

    public String getUserName()
    {
        return userName;
    }

}
