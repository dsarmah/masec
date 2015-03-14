package com.masec.core;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import com.masec.Constants;

public class LoginServiceImpl implements LoginService 
{	
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
	}
	
	public SecurityContext getSecContext() 
	{
		return secContext;
	}

	@Override
	public Map<String, Object> login(String userName, String password) 
	{
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
		} 
		catch (LoginException e) 
		{			
			loginStatus = false;
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
            //System.out.println("MyConfig.name=" + name);
        	LoginContextConfig lCfg = LoginContextConfig.getLoginCtxCfg(secContext);
        	@SuppressWarnings("unchecked")
			Map <String, String> lMap = (Map <String, String>) lCfg.getLoginCfg().get(name);
            if ("native".equals(lMap.get("type"))) 
            {
                Map <String, Object> map = new HashMap  <String, Object> ();
                map.put(Constants.SECCTX, secContext);
                AppConfigurationEntry[] entries = new AppConfigurationEntry[1]; 
                AppConfigurationEntry entry = new AppConfigurationEntry
                        //("com.masec.core.MasecLoginModule",
                		(lMap.get("loginmodule"),
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                        map);
                entries[0] = entry;
                return(entries);
            }
            else if ("ldap".equals(lMap.get("type")))
            {
            	// TODO
            }
            else if ("cloudconnector".equals(lMap.get("type")))
            {
            	// TODO
            }
            
            return origConf.getAppConfigurationEntry(name);
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
