
package com.huawei.prometheus.comm.bean.base;




/**
 * 数据库中存储的需要处理的消息
 * <p>Title:  </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 * @since
 */
public class DBMessage
{
    /**
     * 消息类别： 发往坐席
     */
    public static final String TYPE_TO_AGENT = "ToAgent";
    
    /**
     * 消息类别： 发往用户
     */
    public static final String TYPE_TO_USER = "ToUser";
    
    /**
     * 消息状态，未处理
     */
    public static final int MESSAGE_STATUS_INITIAL = 1; 
 
    /**
     * 消息状态，正在处理
     */
    public static final int MESSAGE_STATUS_DOING = 2; 
    
    /**
     * 消息状态，消息处理完成
     */
    public static final int MESSAGE_STATUS_FINISH = 3;

    /**
     * 消息状态，消息处理失败
     */
    public static final int MESSAGE_STATUS_FAIL = 4;
    

    /**
     * 主键Id
     */
    private String id;
    

    
    /**
     * 消息类别
     */
    private String type;

    /**
     * 呼叫次数
     */
    private int callTime;
    
    /**
     * 消息处理状态， 1 初始化 2 处理中 3 成功完成 4 失败
     */
    private int status;
    
    /**
     * 接入码
     */
    private String accessCode;
    
    /**
     * 消息的内容，json字符串格式
     */
    private String content;
    
    /**
     * 原始消息的ID
     */
    private String origenMessageId;
    

    
    /**
     * 失败信息
     */
    private String failInfo;
    
    /**
     * 消息处理机器编号
     */
    private int handler;
    
    
    /**
     * toString 方法
     * @return String值
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("DBMessage {");
        sb.append(" Id:" + id);
        sb.append(" Type:" + type);
        sb.append('}');
        return sb.toString();
    }
    
  
    
    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

   


    public String getType()
    {
        return type;
    }


    public void setType(String type)
    {
        this.type = type;
    }



    public int getCallTime()
    {
        return callTime;
    }


    public void setCallTime(int callTime)
    {
        this.callTime = callTime;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }


    public String getAccessCode()
    {
        return accessCode;
    }

    public void setAccessCode(String accessCode)
    {
        this.accessCode = accessCode;
    }

    public String getOrigenMessageId()
    {
        return origenMessageId;
    }

    public void setOrigenMessageId(String origenMessageId)
    {
        this.origenMessageId = origenMessageId;
    }

    public String getFailInfo()
    {
        return failInfo;
    }

    public void setFailInfo(String failInfo)
    {
        this.failInfo = failInfo;
    }

    public int getHandler()
    {
        return handler;
    }

    public void setHandler(int handler)
    {
        this.handler = handler;
    }
}
