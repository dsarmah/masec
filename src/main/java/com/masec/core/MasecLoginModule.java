package com.masec.core;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.masec.Constants;
import com.masec.core.model.UserId;
import com.masec.core.model.User;

public class MasecLoginModule implements LoginModule
{

	@SuppressWarnings("unused")
	private Subject subject;
	private CallbackHandler callbackHandler;
	private Map<String, Object> options;
	
	@Override
	public boolean abort() throws LoginException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean commit() throws LoginException {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void initialize(Subject subject, CallbackHandler handler,  Map state, Map options) {
		this.subject = subject;
		this.callbackHandler = handler;
		this.options = options;
	}

	@Override
	public boolean login() throws LoginException 
	{
		boolean returnValue = true;

		if(callbackHandler == null){
			throw new LoginException("No callback handler supplied.");
		}
		
		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("Username");
		callbacks[1] = new PasswordCallback("Password", false);

		
		try 
		{
			callbackHandler.handle(callbacks);
			String userName = ((NameCallback)callbacks[0]).getName();
			char [] passwordCharArray = ((PasswordCallback)callbacks[1]).getPassword();
			String password = new String(passwordCharArray);
			//System.out.println(userName);
			//System.out.println(password);			
	    
			//==> Authentication.
			
			SecurityContext ctx = (SecurityContext) options.get(Constants.SECCTX);
			//System.out.println(ctx.application);
			UserId id = new UserId();
			id.setUserName(userName);
			id.setApplicationCtx(ctx.application);
			User u = ctx.userService.authenticate(id, password);
			if (null == u)
			{
				returnValue = false;
			}
			else
			{
				//returnValue = userName.equals(password);
				returnValue = true;
			}
		} 
		catch (IOException ioe) 
		{			
			throw new LoginException("IOException occured.");
		} 
		catch (UnsupportedCallbackException e) 
		{			
			throw new LoginException("UnsupportedCallbackException encountered.");
		}
		
		return returnValue;
	}

	@Override
	public boolean logout() throws LoginException {
		// TODO Auto-generated method stub
		return true;
	}

}
