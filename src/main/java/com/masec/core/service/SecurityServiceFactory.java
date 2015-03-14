package com.masec.core.service;

import com.masec.SecurityService;

public class SecurityServiceFactory 
{
	public static SecurityService getSecurityService()
	{
		return new SecurityServiceImpl();
	}
	
	public static SecurityService getSecurityService(String application)
	{
		return new SecurityServiceImpl(application);
	}
}
