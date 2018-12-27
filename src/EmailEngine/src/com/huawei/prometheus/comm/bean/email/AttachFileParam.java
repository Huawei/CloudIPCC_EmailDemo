
package com.huawei.prometheus.comm.bean.email;


/**
 * <p>Title:  邮件附件 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 * @since
 */
public class AttachFileParam
{
    /**
     * 附件
     */
    public static final String TYPE_ATTACH = "ATTACH";
    
    
    /**
     * 内嵌图片
     */
    public static final String TYPE_EMBEDED_IMAG = "IMAGE";
    
    private String id;
    
    private String fileName;

    private String type;
    
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
