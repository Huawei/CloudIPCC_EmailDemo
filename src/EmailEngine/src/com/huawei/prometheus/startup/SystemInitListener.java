
package com.huawei.prometheus.startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.prometheus.adapter.email.EmailAdapter;
import com.huawei.prometheus.comm.ConfigProperties;
import com.huawei.prometheus.dao.init.Mybatis;

public class SystemInitListener implements ServletContextListener
{

    private static final Logger LOG = LoggerFactory.getLogger(SystemInitListener.class);
    
    @Override
    public void contextDestroyed(ServletContextEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0)
    {
        LOG.info("Begin Start Email Engine Demo...");
        
        LOG.info("Begin load config files...");
        if (!ConfigProperties.loadConfig())
        {
            LOG.error("Load config file failed, we are shutdown now.");
            return;
        }
        Mybatis.initEmailSqlSessionFactory();
        EmailAdapter emailAdaptor = new EmailAdapter();
        if (!emailAdaptor.init())
        {
            LOG.error("Email Engine Demo init failed.");
            return;
        }
        emailAdaptor.start();
        LOG.info("Start Email Engine Demo finish...");
        
    }
}
