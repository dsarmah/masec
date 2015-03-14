package com.masec.core;

import java.util.HashMap;
import java.util.Map;

import com.masec.Constants;

public class LoginContextConfig 
{

	private static Map <String, Object> cfgMap = null;	
	private static LoginContextConfig loginConfig = null;
	
	public static LoginContextConfig getLoginCtxCfg(SecurityContext ctx)
	{
		if (null == loginConfig)
		{
			cfgMap = new HashMap <String, Object> ();
			loginConfig = new LoginContextConfig();			
		}
		
		if (cfgMap.get(ctx.application) == null)
		{
			init(ctx);
		}
		
		return loginConfig;
	}
	
	private LoginContextConfig()
	{
	}

	private static void init(SecurityContext ctx)
	{
		Map <String, Object> conf = new HashMap <String, Object> ();
		conf.put("loginmodule", "com.masec.core.MasecLoginModule");
		conf.put("type", "native");
		cfgMap.put(ctx.application, conf);
	}
	
	// TODO - This method should be available to call from service so that this config can be updated at run time.
	public void loadConfig(SecurityContext ctx)
	{
		if (!ctx.application.equals(Constants.MASEC))
		{
			// TODO: Read this from database 
			Map <String, Object> conf = new HashMap <String, Object> ();
			conf.put("loginmodule", "com.sun.security.auth.module.LdapLoginModule");
			conf.put("userProvider", "ldap://ldap-svr/ou=people,dc=example,dc=com");
			conf.put("userFilter", "(&(uid={USERNAME})(objectClass=inetOrgPerson))");
			conf.put("authzIdentity", "{EMPLOYEENUMBER}");		
			conf.put("type", "ldap");
			cfgMap.put(ctx.application, conf);
		}
	}
	
	public Map <String, Object> getLoginCfg()
	{
		return cfgMap;
	}
}
