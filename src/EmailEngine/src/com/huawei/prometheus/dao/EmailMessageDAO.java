
package com.huawei.prometheus.dao;

import java.util.List;
import java.util.Map;

import com.huawei.prometheus.comm.bean.email.AttachFile;
import com.huawei.prometheus.comm.bean.email.EmailMessage;



/**
 * 
 * <p>Title: EmailMessage数据库操作类  </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 */
public interface EmailMessageDAO {
    
    public void saveFile(Map<String, Object> contentMap);
    
    public List<Map<String, Object>> getFile(Map<String, Object> contentMap);
    
    /**
     * 新增一条记录
     * @param message 消息
     * @return 消息数据库ID
     */
    public int createMessage(EmailMessage message);
    
    
    public int createEmailAttach(AttachFile attachFile);
    
    
    public List<EmailMessage> getNeedToSendMailList(Map<String, Object> contentMap);
    
    
    public List<AttachFile> getAttachList(String emailId);
    
    
    public void updateMessage(Map<String, Object> contentMap);
}
