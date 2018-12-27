
package com.huawei.adapter.icsevent;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.ICSEvent;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.constant.FailureCause;
import com.huawei.adapter.common.util.thread.pool.WorkTask;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.icsserver.constant.IcsEventConstant;
import com.huawei.adapter.session.ChatSession;
import com.huawei.adapter.session.ChatSessionManager;

/**
 * 
 * <p>Title: 事件处理任务 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class ICSEventHandleThread implements WorkTask
{
    private static final Logger LOG = LoggerFactory.getLogger(ICSEventHandleThread.class);


    private ICSEvent icsEvent;

    private String key;

    private ChatSession chatSession;

    public ICSEventHandleThread(ICSEvent icsEvent)
    {
        this.icsEvent = icsEvent;
        key = icsEvent.getVdnId() + "_" + icsEvent.getUserName();
    }

    @Override
    public void run()
    {
        chatSession = ChatSessionManager.getChatSessionByUserName(icsEvent.getVdnId(), icsEvent.getUserName());
        if (null == chatSession)
        {
            LOG.warn("ChatSession is null, so icsEvent is not dealed, [{}]", icsEvent);
            return;
        }
        dealLogout();
        dealCallEvent();
    }

   
    
    
    /**
     * 处理签出事件
     */
    private void dealLogout()
    {
        if (IcsEventConstant.WECC_PROVIDER_SHUTDOWN.equals(icsEvent.getEventType()))
        {
            // 收到用户签出事件
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) icsEvent.getContent();
            String loginTime = String.valueOf(map.get("loginTime"));

            if (loginTime.equals(chatSession.getLoginTime()))
            {
                // 用户的登录时间与签出事件中的登录时间保持一致，执行签出事件
                chatSession.doLogoutEvent(FailureCause.NOT_LOGIN.getCause());
            }
            else
            {
                LOG.warn(Constants.PRINT_USER + " The user has logouted, so not deal logout event, icsEvent is {}",
                        new Object[]{icsEvent.getVdnId(),
                        icsEvent.getUserName(), 
                        LogUtils.encodeForLog(icsEvent)});
            }
        }
    }

    /**
     * 处理呼叫事件
     */
    private void dealCallEvent()
    {
        if (IcsEventConstant.WECC_WEBM_CALL_CONNECTED.equals(icsEvent.getEventType()))
        {
            // 收到呼叫建立事件
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) icsEvent.getContent();
            String callId = String.valueOf(map.get("callId"));
            chatSession.doCallConnectEvent(callId);
        }
        else if (IcsEventConstant.WECC_WEBM_CALL_DISCONNECTED.equals(icsEvent.getEventType()))
        {
            // 收到呼叫释放事件
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) icsEvent.getContent();
            String callId = String.valueOf(map.get("callId"));
            chatSession.doCallDisConnectEvent(callId);
        }
        else if (IcsEventConstant.WECC_WEBM_CALL_FAIL.equals(icsEvent.getEventType())
                || IcsEventConstant.WECC_WEBM_QUEUE_TIMEOUT.equals(icsEvent.getEventType())
                || IcsEventConstant.WECC_WEBM_CANCEL_QUEUE.equals(icsEvent.getEventType()))
        {
            // 收到呼叫失败、排队失败、取消排队事件
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) icsEvent.getContent();
            String callId = String.valueOf(map.get("callId"));
            chatSession.doCallCreateFailedEvent(callId);
        }
    }

    @Override
    public int getKey()
    {
        return key.hashCode();
    }
}
