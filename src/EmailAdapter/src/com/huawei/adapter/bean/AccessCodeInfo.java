
package com.huawei.adapter.bean;

/**
 * 
 * <p>Title: 接入吗配置 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public class AccessCodeInfo
{
 

    private int vdnId;

    /**
     * 接入码
     */
    private String accessCode;

  
    public int getVdnId()
    {
        return vdnId;
    }

    public void setVdnId(int vdnId)
    {
        this.vdnId = vdnId;
    }

    public String getAccessCode()
    {
        return accessCode;
    }

    public void setAccessCode(String accessCode)
    {
        this.accessCode = accessCode;
    }

    public AccessCodeInfo(int vdnId, String accessCode)
    {
        this.vdnId = vdnId;
        this.accessCode = accessCode;
    }

}
