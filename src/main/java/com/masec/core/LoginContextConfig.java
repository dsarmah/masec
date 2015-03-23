package com.masec.core;

import java.util.HashMap;
import java.util.List;
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
	//public void loadLdapConfig(SecurityContext ctx, List<Map<String, String>> ldap)
	public void loadLdapConfig(SecurityContext ctx, Map<String, Object> ldap)
	{
		//if (!ctx.application.equals(Constants.MASEC))
		{
			/*
			Map <String, Object> conf = new HashMap <String, Object> ();
			conf.put("loginmodule", "com.sun.security.auth.module.LdapLoginModule");
			conf.put("userProvider", "ldap://ldap-svr/ou=people,dc=example,dc=com");
			conf.put("userFilter", "(&(uid={USERNAME})(objectClass=inetOrgPerson))");
			conf.put("authzIdentity", "{EMPLOYEENUMBER}");		
			conf.put("type", "ldap");
			*/
			//ldap.put("loginmodule", "com.sun.security.auth.module.LdapLoginModule");
			//cfgMap.put(ctx.application, ldap);
		
		}
		cfgMap.clear();
		cfgMap.put("LDAP-List", ldap);
	}
	
	public Map <String, Object> getLoginCfg()
	{
		return cfgMap;
	}
}
