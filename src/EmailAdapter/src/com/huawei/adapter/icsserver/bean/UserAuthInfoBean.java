
package com.huawei.adapter.icsserver.bean;

public class UserAuthInfoBean
{
    private String guid;
    
    private String cookie;
     
    public UserAuthInfoBean()
    {
        
    }

    public String getGuid()
    {
        return guid;
    }

    
    public String getCookie()
    {
        return cookie;
    }

    public void setGuid(String guid)
    {
        this.guid = guid;
    }

    public void setCookie(String cookie)
    {
        this.cookie = cookie;
    }
    
    
}
