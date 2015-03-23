package com.masec.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;

import com.masec.Constants;

public class LoginServiceImpl implements LoginService 
{	
	private Logger log = Logger.getLogger(this.getClass());
	
	private static Map <String, SecurityContext> ctxMap = new HashMap <String, SecurityContext>();
	
	private SecurityContext secContext;
	
	public LoginServiceImpl(SecurityContext ctx)
	{
		secContext = ctxMap.get(ctx.application); 
		if (null == secContext)
		{
			ctxMap.put(ctx.application, ctx);
			secContext = ctx;
		}
		log.debug("Security Context: " + secContext.application + " @hash " + this.getSecContext().hashCode());
	}
	
	public SecurityContext getSecContext() 
	{
		return secContext;
	}

	@Override
	public Map<String, Object> login(String userName, String password) 
	{
		log.debug("login [Security Context: " + secContext.application + "] @hash " + this.getSecContext().hashCode());
		Boolean loginStatus = true;		
		
		CallbackHandler handler = new LoginCallbackHandler(userName,password);

		try 
		{
			Configuration conf = new JaasConfig();
	        Configuration.setConfiguration(conf);

	        LoginContext loginContext = new LoginContext(secContext.application, new Subject(), handler, conf);
	    
	        //System.out.println("Current config=" + Configuration.getConfiguration());

	        //AppConfigurationEntry[] entries = Configuration.getConfiguration().getAppConfigurationEntry(secContext.application);
	        //System.out.println("entries=" + entries);			
	        
	        loginContext.login();
	        log.info("LOGIN Succeed ["+ secContext.application + "] user: " + userName);
		} 
		catch (LoginException e) 
		{
			log.info("LOGIN Failed ["+ secContext.application + "] user: " + userName);
			loginStatus = false;
			//e.printStackTrace();
		}
		//System.out.println(loginStatus);
		
		Map <String, Object> ret = new HashMap <String, Object>();
		ret.put(Constants.STATUS, loginStatus);		
		return ret;
	}
	
	 private class JaasConfig extends Configuration 
	 {
        private Configuration origConf;
        public JaasConfig() 
        { 
        	origConf = Configuration.getConfiguration(); 
        }
        
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) 
        {
        	log.debug("getAppConfigurationEntry ["+ secContext.application + "] name: " + name);

        	LoginContextConfig lCfg = LoginContextConfig.getLoginCtxCfg(secContext);
        	
        	@SuppressWarnings("unchecked")
    		Map <String, Object> lm = (Map <String, Object>) lCfg.getLoginCfg().get("LDAP-List");
			
            //if ("native".equals(lMap.get("type")))
        	if (Constants.MASEC.equals(name))
            {            	
               return getMasecEntry(secContext);
            }
        	else if (null != lm)
        	{
	    		List <Map <String, String>> ll = (List <Map <String, String>>) lm.get(secContext.application);
	    		if (null != ll && ll.size() > 0)
	    		{
	    			log.debug("logCfg Map: " + ll);
	    			AppConfigurationEntry[] entries = new AppConfigurationEntry[ll.size()+1];
	    			int i = 0;
		    		for (Map <String, String> m : ll)
		    		{
		    			AppConfigurationEntry entry = new AppConfigurationEntry
		                        ("com.sun.security.auth.module.LdapLoginModule",	                		
		                        AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT,
		                        m);
		                entries[i++] = entry;
		                log.info("login module LdapLoginModule [" + secContext.application + "][" + m.get(Constants.LDAP_USERPROVIDER + "]"));
		    		}
		    		entries[ll.size()] = getMasecEntry(secContext)[0];
		    		return(entries);
	    		}
	    		
	    		else
	    		{
	    			return getMasecEntry(secContext);	
	    		}
	    		
        	}
        	return getMasecEntry(secContext);
            
        }
        
        private AppConfigurationEntry[] getMasecEntry(SecurityContext ctx)
        {
        	 Map <String, Object> map = new HashMap  <String, Object> ();
             map.put(Constants.SECCTX, ctx);
             AppConfigurationEntry[] entries = new AppConfigurationEntry[1]; 
             AppConfigurationEntry entry = new AppConfigurationEntry
                     ("com.masec.core.MasecLoginModule",                		
                     AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT,
                     map);
             entries[0] = entry;
             log.info("login module MasecLoginModule [" + secContext.application + "]");
             return(entries);
        }
        
        public void refresh() 
        { 	        	
        }
	}	

	public static void main(String[] a)
	{
		 SecurityContext ctx = SecurityFactory.getSecurityContext();
		 LoginServiceImpl l = new LoginServiceImpl(ctx);
		 Map <String, Object> ret = l.login("sarmah1", "sarmah");
		 System.out.println(ret.get(Constants.STATUS));
	}
}
