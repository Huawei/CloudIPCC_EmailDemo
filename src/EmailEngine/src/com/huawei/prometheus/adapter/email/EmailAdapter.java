
package com.huawei.prometheus.adapter.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.prometheus.adapter.email.data.EmailAccount;
import com.huawei.prometheus.adapter.email.data.EmailData;
import com.huawei.prometheus.comm.Adapter;
import com.huawei.prometheus.comm.ConfigList;
import com.huawei.prometheus.comm.ConfigProperties;
import com.huawei.prometheus.comm.LogUtils;
import com.huawei.prometheus.comm.bean.email.PrometheusEmailConfig;
import com.huawei.prometheus.comm.exception.CommonException;

/**
 * <p>
 * Title: EmailAdapter
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * <pre> </pre>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: 华为技术有限公司
 * </p>

 */
public class EmailAdapter implements Adapter 
{
    private static Logger log = LoggerFactory.getLogger(EmailAdapter.class);

    private static final int DEFAULT_RECEIVE_PORT = 110;
    
    private static final int DEFAULT_SEND_PORT = 25;
    
    private static final int STOP_TIME = 100;
    
    private static Map<String,EmailConnectionManager> emailConnectionManagerMap = new HashMap<String,EmailConnectionManager>();
    
    private static Map<String,Thread> emailConnectionManagerThreadMap = new HashMap<String,Thread>();
    
    private static Map<String, String> vdnMap;
    
    
    /**
     *  (non-Javadoc)
     * @see com.huawei.hps.comm.out.Adapter#init()
     * @return 是否成功
     */
    @Override
    public boolean init()
    {
    	try
    	{
	        try
	        {
	            EmailData.ALLACCOUNTS_MAP.clear();
	            EmailData.ACCOUNT_MAP.clear();
	            EmailData.ALLVDN_LIST.clear();
	            
	            int receiverPort = DEFAULT_RECEIVE_PORT;
	            int sendPort = DEFAULT_SEND_PORT;
	            
	            List<PrometheusEmailConfig> emailList = initEmailAccouts();
	            if(emailList.size() > 0)
	            {
	                for(PrometheusEmailConfig email:emailList)
	                {
	                    EmailAccount account = new EmailAccount();
	                    account.setUserName(email.getUserName());
	                    account.setEmailAddress(email.getAddress());
	                    account.setMailPWD(email.getMailPWD());
	                    account.setServiceCode(email.getServiceNo());
	                    account.setSendServer(email.getSendServer());
	                    if(email.getSendPort() == null || email.getSendPort().isEmpty())
	                    {
	                        account.setSendPort(sendPort); 
	                    }
	                    else
	                    {
	                        account.setSendPort(Integer.parseInt(email.getSendPort())); 
	                    }
	                    account.setSendNeedSSL(email.getIsSSLSend() == 1 ? true : false);
	                    account.setReceiverType(email.getReceiveType().toLowerCase(Locale.US));
	                    account.setReceiverServer(email.getReceiveServer());
	                    if(email.getReceivePort() == null || email.getReceivePort().isEmpty())
	                    {
	                        account.setReceiverPort(receiverPort); 
	                    }
	                    else
	                    {
	                        account.setReceiverPort(Integer.parseInt(email.getReceivePort())); 
	                    }
	                    account.setReceiveNeedSSL(email.getIsSSLReceive() == 1? true : false);
	                    account.setNeedAuth(email.getIsAuth() == 1 ? true : false);
	                    account.setEnableNTLM(email.getEnableNTLM() == 1 ? true : false);
	                    account.setDeleteMessageOnServer(email.getIsDelEmail() == 1 ? true : false);
	                    String vdnId = email.getVdnId().toString();
	                    account.setVndId(email.getVdnId().toString());
	                    
	                    List<EmailAccount> list = EmailData.ALLACCOUNTS_MAP.get(vdnId);
	                    if(list == null)
	                    {
	                        //如果vdn下没有记录，则新增
	                        list = new ArrayList<EmailAccount>();
	                        list.add(account);
	                        EmailData.ALLACCOUNTS_MAP.put(vdnId, list);
	                    }
	                    else
	                    {
	                        //如果list已存在，则添加
	                        list.add(account);
	                    }
	                    EmailData.ACCOUNT_MAP.put(account.getEmailAddress(), account);
	                    
	                    //判断当前vdn是否在vdnlist已经存在,如果不存在，则添加
	                    if(!EmailData.ALLVDN_LIST.contains(vdnId))
	                    {
	                        EmailData.ALLVDN_LIST.add(vdnId);
	                    }
	                }
	            }
	          
	        }
	        catch(RuntimeException e)
	        {
	            log.error("EmailAdaptor Demo init error:" + LogUtils.encodeForLog(e.getMessage()));
	            throw new CommonException(e);
	        }
	        catch(Exception e)
	        {
	        	throw new CommonException(e);
	        }
	    }
	    catch (CommonException e)
	    {
	    	log.error("EmailAdaptor Demo init error:" + LogUtils.encodeForLog(e.getMessage()));
            return false;
	    }
    	
        return true;
    }
    
    /**
     *  (non-Javadoc)
     * @see com.huawei.hps.comm.out.Adapter#start()
     * @return 是否成功
     */
    @Override
    public boolean start()
    {      
        //启动收发线程，一个vdn一个线程，新增
        for(String vdnId: EmailData.ALLVDN_LIST)
        {
            EmailConnectionManager ecm  = new EmailConnectionManager(vdnId);
            Thread thread = new Thread(ecm);
            emailConnectionManagerMap.put(vdnId, ecm);
            emailConnectionManagerThreadMap.put(vdnId, thread);
            thread.start();
        }
        
        return true;
    }
    
    /**
     *  (non-Javadoc)
     * @see com.huawei.hps.comm.out.Adapter#stop()
     * @return 是否成功
     */
    @Override
    public boolean stop()
    {   
        
        
        Set<Entry<String, EmailConnectionManager>> ecmEntrySet = emailConnectionManagerMap.entrySet();
        Iterator<Entry<String, EmailConnectionManager>> iterator = ecmEntrySet.iterator();
        if(iterator != null)
        {
            while(iterator.hasNext())
            {
                Entry<String, EmailConnectionManager> item = iterator.next();
                if(item != null && item.getValue() != null)
                {
                    item.getValue().closeRunner();
                }
            }
        }
        
        try
        {
            Thread.sleep(STOP_TIME);
        }
        catch (InterruptedException e)
        {
            log.error("thread sleep error.\r\n{}", LogUtils.encodeForLog(e.getMessage()));
        }
        return true;
    }



    
    
    /**
     * 根据vdnId获取wasvdnId
     * @param vdnId
     * @return
     */
    public static String readWasVdnIdByVdnId(String vdnId)
    {
        return vdnMap.get(vdnId);
    }
    
    

    
    private static List<PrometheusEmailConfig> initEmailAccouts()
    {
      int emailAccoutNum = 32;

      if (NumberUtils.isDigits(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, "EMAIL_ACCOUNT_NUM")))
      {
        emailAccoutNum = Integer.valueOf(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, "EMAIL_ACCOUNT_NUM")).intValue();
      }
      else
      {
        log.warn("emailaccount.properties 's EMAIL_ACCOUNT_NUM is not number,so use defult.");
      }

      List<PrometheusEmailConfig> emailList = new ArrayList<PrometheusEmailConfig>();
      try
      {
	      try
	      {
	          for (int i = 0; i < emailAccoutNum; i++)
	          {
    	          PrometheusEmailConfig emailAccount = new PrometheusEmailConfig();
    	          emailAccount.setUserName(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".USERNAME"));
    	          emailAccount.setAddress(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".EMAILADDRESS"));
    	          emailAccount.setMailPWD(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".ACCOUNTPASSWORD"));
    	          emailAccount.setServiceNo(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".SERVICENO"));
    	          emailAccount.setSendServer(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".SENDSERVER"));
    	          emailAccount.setSendPort(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".SENDPORT"));
    	          emailAccount.setIsSSLSend(Integer.valueOf(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".SENDNEEDSSL")));
    	          emailAccount.setReceiveType(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".RECEIVERTYPE"));
    	          emailAccount.setReceiveServer(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".RECEIVERSERVER"));
    	          emailAccount.setReceivePort(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".RECEIVERPORT"));
    	          emailAccount.setIsSSLReceive(Integer.valueOf(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".RECEIVERNEEDSSL")));
    	          emailAccount.setIsAuth(Integer.valueOf(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".NEEDAUTH")));
    	          emailAccount.setEnableNTLM(Integer.valueOf(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".ENABLENTLM")));
    	          emailAccount.setIsDelEmail(Integer.valueOf(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".DELETEMESSAGEONSERVER")));
    	          emailAccount.setVdnId(Integer.valueOf(ConfigProperties.getKey(ConfigList.EMAIL_ACCOUNT, 
    	            "EMAILACCOUNT" + i + ".WASVDNID")));
    	
    	          emailList.add(emailAccount);
	          }
	
	      }
	      catch (RuntimeException e)
	      {
	        log.error("Email adaptor init email accounts has an exception.{}", LogUtils.encodeForLog(e));
	        throw new CommonException(e);
	      }
	      catch (Exception e)
	      {
	    	  throw new CommonException(e);
	      }
	    }
	    catch (CommonException e)
	    {
	    	log.error("Email adaptor init email accounts has an exception.{}", LogUtils.encodeForLog(e));
	    }

      return emailList;
    }
}
