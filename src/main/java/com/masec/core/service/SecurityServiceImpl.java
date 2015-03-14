package com.masec.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.masec.Constants;
import com.masec.SecurityService;
import com.masec.core.LoginServiceImpl;
import com.masec.core.SecurityContext;
import com.masec.core.SecurityFactory;
import com.masec.core.model.User;
import com.masec.core.model.UserId;

public class SecurityServiceImpl implements SecurityService
{
	LoginServiceImpl lSrvc;
	private static UserService userService;
	
	public SecurityServiceImpl()
	{
		SecurityContext ctx = SecurityFactory.getSecurityContext();
		init(ctx);
	}
	
	public SecurityServiceImpl(String application)
	{
		SecurityContext ctx = SecurityFactory.getSecurityContext(application);
		init(ctx);
	}
	
	private void init(SecurityContext ctx)
	{
		lSrvc = new LoginServiceImpl(ctx);
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext( "classpath:masecDbContext.xml" );
		userService = ( UserService)applicationContext.getBean( "userService" );
	}
	
	@Override
	public Map<String, Object> login(Map<String, Object> user) 
	{		
		Map <String, Object> ret = lSrvc.login((String)user.get(Constants.USERNAME), (String)user.get(Constants.PASSWORD));		
		return ret;
	}

	@Override
	public Map<String, Object> addUser(Map<String, Object> user)  
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		try
		{
	    	User u = new User();
	    	UserId id = new UserId();
	    	id.setUserName((String)user.get(Constants.USERNAME));
	    	id.setApplicationCtx(lSrvc.getSecContext().application);
	    	u.setId(id);
	    	u.setPassword((String)user.get(Constants.PASSWORD));	    	
	        userService.save(u );
	        
			ret.put(Constants.STATUS, new Boolean(true));
		}
		catch (org.springframework.dao.DataIntegrityViolationException v)
		{			
			ret.put(Constants.STATUS, new Boolean(false));
		}
		catch (Exception x)
		{
			x.printStackTrace();
			ret.put(Constants.STATUS, new Boolean(false));
		}
		return ret;
	}

	@Override
	public List<Map<String, Object>> listUser(int offset, int limit) 
	{
		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		List<User> users = userService.findAll(offset, limit);
        
        for( User u : users )
        {
        	Map<String, Object> row = convertToMap(u);
        	ret.add(row);
        	
            //System.out.println( "\t" + u.getId().getUserName() );
        }
		// TODO Auto-generated method stub
		return ret;
	}

	@Override
	public void shutdown() 
	{
		userService.shutdown();		
	}
	
	@Override
	public Map<String, Object> deleteUser(Map<String, Object> user) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		try
		{
			User u = convertToUser(user, lSrvc.getSecContext().application);
			userService.delete(u);
			ret.put(Constants.STATUS, new Boolean(true));
		}
		catch (Exception x)
		{
			x.printStackTrace();
			ret.put(Constants.STATUS, new Boolean(false));
		}
		return ret;
	}

	@Override
	public Map<String, Object> updateUser(Map<String, Object> user) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		try
		{
			User u = convertToUser(user, lSrvc.getSecContext().application);
			// Don't allow to update password and salt. For that user must call updatePassword(...) method
			if (null != u.getPassword() || null != u.getSalt())
			{
				ret.put(Constants.STATUS, new Boolean(false));
				ret.put(Constants.ERRORMSG, "You can not update password and salt! Use updatePassword(...) to update password and salt.");
				return ret;
			}
			userService.update(u);
			ret.put(Constants.STATUS, new Boolean(true));
		}
		catch (Exception x)
		{
			x.printStackTrace();
			ret.put(Constants.STATUS, new Boolean(false));
		}
		return ret;
	}

	@Override
	public Map<String, Object> updatePassword(Map<String, Object> user) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		try
		{
			User u = convertToUser(user, lSrvc.getSecContext().application);
			userService.updatePassword(u);
			ret.put(Constants.STATUS, new Boolean(true));
		}
		catch (Exception x)
		{
			x.printStackTrace();
			ret.put(Constants.STATUS, new Boolean(false));
		}
		return ret;
	}
	
	private Map<String, Object> convertToMap(User u)
	{
		Map<String, Object> row = new HashMap <String, Object> ();
    	row.put(Constants.EMAIL, u.getEmail());
    	row.put(Constants.EXTENDEDPROFILE, u.getExtendProfile());
    	row.put(Constants.FIRSTNAME, u.getFirstName());
    	row.put(Constants.LASTNAME, u.getLastName());
    	row.put(Constants.PASSWORD, u.getPassword());
    	row.put(Constants.PHONE, u.getPhone());
    	row.put(Constants.PICTURE, u.getPicture());
    	row.put(Constants.SECQN1, u.getSecQn1());
    	row.put(Constants.SECQN1ANS, u.getSecQn1Ans());
    	row.put(Constants.SECQN2, u.getSecQn2());
    	row.put(Constants.SECQN2ANS, u.getSecQn2Ans());
    	row.put(Constants.SECQN3, u.getSecQn3());
    	row.put(Constants.SECQN3ANS, u.getSecQn3Ans());
    	row.put(Constants.SINCE, u.getSince());
    	row.put(Constants.USERNAME, u.getId().getUserName());
    	row.put(Constants.SALT, u.getSalt());
    	return row;
	}
	
	private User convertToUser(Map<String, Object> row, String applicationCtx)
	{
		User u = new User();
		UserId id = new UserId();
		
    	u.setEmail((String)row.get(Constants.EMAIL));
    	u.setExtendProfile((String)row.get(Constants.EXTENDEDPROFILE));
    	u.setFirstName((String)row.get(Constants.FIRSTNAME));
    	u.setLastName((String)row.get(Constants.LASTNAME));
    	u.setPassword((String)row.get(Constants.PASSWORD));
    	u.setPhone((String)row.get(Constants.PHONE));
    	u.setPicture((byte[])row.get(Constants.PICTURE));
    	u.setSalt((String) row.get(Constants.SALT)); 
    	u.setSecQn1((String)row.get(Constants.SECQN1));
    	u.setSecQn1Ans((String)row.get(Constants.SECQN1ANS));
    	u.setSecQn2((String)row.get(Constants.SECQN2));
    	u.setSecQn2Ans((String)row.get(Constants.SECQN2ANS));
    	u.setSecQn3((String)row.get(Constants.SECQN3));
    	u.setSecQn3Ans((String)row.get(Constants.SECQN3ANS));
    	u.setSince((Long)row.get(Constants.SINCE));
    	id.setUserName((String)row.get(Constants.USERNAME));
    	id.setApplicationCtx(applicationCtx);
    	u.setId(id);
    	
    	return u;
	}
	
	/*
	private User convertNonNullToUser(Map<String, Object> row, String applicationCtx)
	{
		User u = new User();
		UserId id = new UserId();
		
		if (null != row.get(Constants.EMAIL))
		{
			u.setEmail((String)row.get(Constants.EMAIL));
		}
		if (null != row.get(Constants.EXTENDEDPROFILE))
		{
			u.setExtendProfile((String)row.get(Constants.EXTENDEDPROFILE));
		}
		if(null != row.get(Constants.FIRSTNAME))
		{
			u.setFirstName((String)row.get(Constants.FIRSTNAME));
		}
		if(null != row.get(Constants.LASTNAME))
		{
			u.setLastName((String)row.get(Constants.LASTNAME));
		}
		if(null != row.get(Constants.PASSWORD))
		{
			u.setPassword((String)row.get(Constants.PASSWORD));
		}
		if(null != row.get(Constants.PHONE))
		{
			u.setPhone((String)row.get(Constants.PHONE));
		}
		if(null != row.get(Constants.PICTURE))
		{
			u.setPicture((byte[])row.get(Constants.PICTURE));
		}
		if(null != row.get(Constants.SALT))
		{
			u.setSalt((String) row.get(Constants.SALT)); 
		}
		if(null != row.get(Constants.SECQN1))
		{
			u.setSecQn1((String)row.get(Constants.SECQN1));
		}
		if(null != row.get(Constants.SECQN1ANS))
		{
			u.setSecQn1Ans((String)row.get(Constants.SECQN1ANS));
		}
		if(null != row.get(Constants.SECQN2))
		{
			u.setSecQn2((String)row.get(Constants.SECQN2));
		}
		if(null != row.get(Constants.SECQN2ANS))
		{
			u.setSecQn2Ans((String)row.get(Constants.SECQN2ANS));
		}
		if(null != row.get(Constants.SECQN3))
		{
			u.setSecQn3((String)row.get(Constants.SECQN3));
		}
		if(null != row.get(Constants.SECQN3ANS))
		{
			u.setSecQn3Ans((String)row.get(Constants.SECQN3ANS));
		}
		if(null != row.get(Constants.SINCE))
		{
			u.setSince((Long)row.get(Constants.SINCE));
		}
		id.setUserName((String)row.get(Constants.USERNAME));
		id.setApplicationCtx(applicationCtx);    	
		u.setId(id);
    	
    	return u;
	}
	*/
}