
package com.huawei.prometheus.adapter.email.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * <p>Title:  Email 数据类</p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 华为技术有限公司</p>
 * @since
 */
public class EmailData
{

    
    /**
     * Email账户map，key为vdnid
     */
    public static final Map<String,List<EmailAccount>> ALLACCOUNTS_MAP = new ConcurrentHashMap<String,List<EmailAccount>>();

    /**
     * Email vdn list，记录所有有邮箱配置的vdn
     */
    public static final List<String> ALLVDN_LIST = new ArrayList<String>();
    
    /**
     * Email 帐户
     */
    public static final Map<String, EmailAccount> ACCOUNT_MAP = new ConcurrentHashMap<String, EmailAccount>();

    
}
