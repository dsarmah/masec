package com.masec;

import java.util.HashMap;
import java.util.Map;

import com.masec.core.service.SecurityServiceFactory;

public class SecurityFactory 
{
	static Map <String, SecurityService> _secMap = new HashMap <String, SecurityService> ();
	
	public static SecurityService getSecCtx(String application)
	{
		SecurityService sec = _secMap.get(application);
		if (null == sec)
		{
			sec = SecurityServiceFactory.getSecurityService(application);
			_secMap.put(application, sec);
		}			
		return sec;
	}
	
	public static SecurityService getSecCtx()
	{
		return getSecCtx(Constants.MASEC);
	}
}
