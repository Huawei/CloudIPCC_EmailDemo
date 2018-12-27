
package com.huawei.adapter.icsserver.bean;



/**
 * 
 * <p>Title:用户发送的参数  </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class ChatMessageParam
{
    /**
     * 呼叫callId
     */
    private String callId;

    /**
     * 内容
     */
    private String content;
    
    /**
     * 是否需要对端发送确认消息
     */
    private boolean needCheck;
    
    /**
     * 消息ID
     */
    private int chatId;
    
    /**
     * 发送时间
     */
    private long sendTime;
    

	public String getCallId()
	{
		return callId;
	}

	public void setCallId(String callId)
	{
		this.callId = callId;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public boolean isNeedCheck()
    {
        return needCheck;
    }

    public void setNeedCheck(boolean needCheck)
    {
        this.needCheck = needCheck;
    }

    public int getChatId()
    {
        return chatId;
    }

    public void setChatId(int chatId)
    {
        this.chatId = chatId;
    }

    public long getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(long sendTime)
    {
        this.sendTime = sendTime;
    }
    
    /**
     * 覆盖toString方法
     * @return 完整消息内容
     */
    @Override
    public String toString()
    {
        StringBuffer sbstr = new StringBuffer();
        sbstr.append("ChatMessageParam " + "{")
                .append("callId = ")
                .append(callId)
                .append("chatId = ")
                .append(chatId)
                .append("needCheck = ")
                .append(needCheck)
                .append(", content = ")
                .append("******")
                .append(" }");
        return sbstr.toString();
    }
      
}
