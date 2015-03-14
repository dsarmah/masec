package com.masec.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.masec.core.service.UserService;

public class SecurityContext 
{

	public String application;
	public ApplicationContext applicationContext;
	public UserService userService;
	
	private SecurityContext(String application)
	{
		this.application = application;
        this.applicationContext = new ClassPathXmlApplicationContext( "classpath:masecDbContext.xml" );
        this.userService = ( UserService)applicationContext.getBean( "userService" );
	}
	
	public static SecurityContext getContext(String app) 
	{
		return new SecurityContext(app);
	}

}
