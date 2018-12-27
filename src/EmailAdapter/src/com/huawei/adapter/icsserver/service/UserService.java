
package com.huawei.adapter.icsserver.service;

import java.util.Map;







import com.huawei.adapter.common.data.GlobalData;
import com.huawei.adapter.icsserver.bean.CallInfoParam;
import com.huawei.adapter.icsserver.bean.ChatMessageParam;
import com.huawei.adapter.icsserver.bean.UserAuthInfoBean;
import com.huawei.adapter.icsserver.bean.UserParam;
import com.huawei.adapter.icsserver.http.Request;

public class UserService
{
    private int vdnId;
    
    private String userName;
    
    private UserAuthInfoBean authInfo;
    
    public UserService(int vdnId, String userName)
    {
        this.vdnId = vdnId;
        this.userName = userName;
        this.authInfo = new UserAuthInfoBean();
    }
    
   
    public Map<String, Object> loginEx(UserParam userParam)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(GlobalData.getAppUrl()).append("/onlinewecc/");
        sb.append(vdnId).append("/").append(userName).append("/loginex");
        return Request.post(sb.toString(), userParam, authInfo);
    }
    
    
    public Map<String, Object> logout()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(GlobalData.getAppUrl()).append("/onlinewecc/");
        sb.append(vdnId).append("/").append(userName).append("/logout");
        return Request.delete(sb.toString(), authInfo);
    }
    
    public Map<String, Object> doHeartBeat()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(GlobalData.getAppUrl()).append("/onlinewecc/");
        sb.append(vdnId).append("/").append(userName).append("/heartbeat");
        return Request.get(sb.toString(), authInfo);
    }
    
    public Map<String, Object> doCreateCall(CallInfoParam callInfoParam)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(GlobalData.getAppUrl()).append("/realtimecall/");
        sb.append(vdnId).append("/").append(userName).append("/docreatecall");
        return Request.post(sb.toString(), callInfoParam, authInfo);
    }
    
    public Map<String, Object> doSendMessage(ChatMessageParam messageParam)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(GlobalData.getAppUrl()).append("/realtimecall/");
        sb.append(vdnId).append("/").append(userName).append("/dosendmessage");
        return Request.post(sb.toString(), messageParam, authInfo);
    }
}
