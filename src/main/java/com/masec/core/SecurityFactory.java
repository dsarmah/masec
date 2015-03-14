package com.masec.core;

import com.masec.Constants;

public class SecurityFactory 
{
	public static SecurityContext getSecurityContext()
	{
		return SecurityContext.getContext(Constants.MASEC); 
	}
	
	public static SecurityContext getSecurityContext(String application)
	{
		return SecurityContext.getContext(application); 
	}
}
