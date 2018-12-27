
package com.huawei.adapter.icsevent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.bean.ICSEvent;
import com.huawei.adapter.common.constant.Constants;
import com.huawei.adapter.common.util.utils.JsonUtil;
import com.huawei.adapter.common.util.utils.LogUtils;
import com.huawei.adapter.icsserver.constant.IcsEventConstant;




public class EventReceiveServlet extends HttpServlet
{
    private static final Logger LOG = LoggerFactory.getLogger(EventReceiveServlet.class);
    /**
     * 
     */
    private static final long serialVersionUID = -3619252839050265777L;
    
    private static final int BYTE_ARRAY_LENGTH = 1024;
    

    /**
     * 需要处理的的ICS事件
     */
    private static final List<String> EVENT_LIST = new ArrayList<String>();
    
    static
    {
        // 添加需要处理的事件
        EVENT_LIST.add(IcsEventConstant.WECC_PROVIDER_SHUTDOWN);
        EVENT_LIST.add(IcsEventConstant.WECC_WEBM_CALL_CONNECTED);
        EVENT_LIST.add(IcsEventConstant.WECC_WEBM_CALL_DISCONNECTED);
        EVENT_LIST.add(IcsEventConstant.WECC_WEBM_CALL_FAIL);
        EVENT_LIST.add(IcsEventConstant.WECC_WEBM_QUEUE_TIMEOUT);
        EVENT_LIST.add(IcsEventConstant.WECC_WEBM_CANCEL_QUEUE);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException
    {
        response.setStatus(200);
        String ip = request.getRemoteHost();
        String eventContent = null; 
        ServletInputStream in = null;
        try
        {
            in = request.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            int len = request.getContentLength() < BYTE_ARRAY_LENGTH?request.getContentLength():BYTE_ARRAY_LENGTH;
            byte[] readByte = new byte[len];
            int readLength;
            while ((readLength = in.read(readByte))>0) 
            {
                String string = new String(readByte, 0, readLength, "utf-8");
                stringBuffer.append(string);
            }
            eventContent = stringBuffer.toString();
            
        }
        catch (IOException e)
        {
            LOG.error("Read requestContent fail, \r\n {}", LogUtils.encodeForLog(e.getMessage()));
        }
        finally
        {
            if (null != in) 
            {
                try 
                {
                    in.close();
                }
                catch (IOException e) 
                {
                    LOG.error("release inputstream failed \r\n {}", LogUtils.encodeForLog(e.getMessage()));
                }
            }
        }
        
        if (null == eventContent || eventContent.equals(""))
        {
            LOG.error("receive a null event from " + LogUtils.encodeForLog(ip));
            return;
        }
        
        Map<String, Object> result = JsonUtil.getMapFromJsonStr(eventContent);
        if (null != result 
                && null != result.get("retcode") 
                && null != result.get("event"))
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = (Map<String, Object>)result.get("event");
            ICSEvent icsEvent = new ICSEvent();
            icsEvent.setVdnId((Integer) event.get("vdnId"));
            icsEvent.setUserName(String.valueOf(event.get("userName")));
            icsEvent.setEventType(String.valueOf(event.get("eventType")));
            icsEvent.setContent(event.get("content"));
            if (EVENT_LIST.contains(icsEvent.getEventType()))
            {
                LOG.info(Constants.PRINT_USER + " receive icsevent: {}",
                        new Object[]{icsEvent.getVdnId(),
                        LogUtils.formatUserName(icsEvent.getUserName()),
                        LogUtils.encodeForLog(icsEvent)});
                ICSEventPollTask.addTask(icsEvent);
            }
                
        }
        else
        {
            LOG.error("receive a invalid  event from " + LogUtils.encodeForLog(ip));
        }
    }

}
