package com.huawei.adapter.bean;

import java.io.Serializable;
import java.util.Map;

import com.huawei.adapter.common.util.utils.LogUtils;

/**
 * 
 * <p>Title:  wecc事件</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class ICSEvent implements Serializable
{
    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 事件类型 
     */
    private String eventType;

    /**
     * 用户名
     */
    private String userName;

    /**
     * vdnId
     */
    private int vdnId;

    /**
     * 
     * 事件内容对象，比如文字交谈时，如果是受到消息则放置ChatMessage对象
     */
    private Object content;

  

    /**
     * 重写toString方法
     * @return String
     */
    @SuppressWarnings("unchecked")
    @Override
    public String toString()
    {
    	StringBuffer sb = new StringBuffer();
   
        sb.append('{');
        sb.append("eventType:" + eventType).append(", ");
        if (content instanceof Map)
        {
            sb.append("content:" + LogUtils.formatICSEventMap((Map<String, Object>)content));
        }
        else 
        {
            sb.append("content:" + content);
        }
        sb.append('}');
        return sb.toString();
    }

	public String getEventType()
	{
		return eventType;
	}

	public void setEventType(String eventType)
	{
		this.eventType = eventType;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public int getVdnId()
	{
		return vdnId;
	}

	public void setVdnId(int vdnId)
	{
		this.vdnId = vdnId;
	}

	public Object getContent()
	{
		return content;
	}

	public void setContent(Object content)
	{
		this.content = content;
	}

}
