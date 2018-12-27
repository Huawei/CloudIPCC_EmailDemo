package com.huawei.adapter.icsserver.bean;




/**
 * 
 * <p>Title:  用户登录参数</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class UserParam
{
    private String pushUrl;


	public String getPushUrl()
    {
        return pushUrl;
    }


    public void setPushUrl(String pushUrl)
    {
        this.pushUrl = pushUrl;
    }


    /**
     * toString
     * @return bean info
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("pushUrl:" + pushUrl);
        sb.append("}");
        return sb.toString();
    }
    
}
