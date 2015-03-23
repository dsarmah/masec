package com.masec.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.masec.Constants;
import com.masec.SecurityService;
import com.masec.core.LoginContextConfig;
import com.masec.core.LoginServiceImpl;
import com.masec.core.SecurityContext;
import com.masec.core.SecurityFactory;
import com.masec.core.dao.AuthProviderDao;
import com.masec.core.model.AuthProvider;
import com.masec.core.model.AuthProviderId;
import com.masec.core.model.User;
import com.masec.core.model.UserId;

public class SecurityServiceImpl implements SecurityService
{
	private LoginServiceImpl lSrvc;
	private static UserService userService;
	private static AuthProviderDao authProvider;
	
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
		authProvider = ( AuthProviderDao)applicationContext.getBean( "authProviderDao" );
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
			//x.printStackTrace();
			ret.put(Constants.STATUS, new Boolean(false));
			ret.put(Constants.ERRORMSG, "FAILED: database operations: " + x.getMessage());
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
			//x.printStackTrace();
			ret.put(Constants.STATUS, new Boolean(false));
			ret.put(Constants.ERRORMSG, "FAILED: database operations: " + x.getMessage());
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
			//x.printStackTrace();
			ret.put(Constants.STATUS, new Boolean(false));
			ret.put(Constants.ERRORMSG, "FAILED: database operations: " + x.getMessage());
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
			//x.printStackTrace();
			ret.put(Constants.STATUS, new Boolean(false));
			ret.put(Constants.ERRORMSG, "FAILED: database operations: " + x.getMessage());
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

	@Override
	public List<Map<String, Object>> listAuthProviderByTypeAndApplication(String type, String application) 
	{
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		List<AuthProvider> authP = authProvider.findByTypeAndApplication(type, application);
        
        for( AuthProvider p : authP )
        {
        	Map<String, Object> row = convertApToMap(p);
        	ret.add(row);        	
        }
		return ret;	
	}


	@Override
	public List<Map<String, Object>> listAuthProviders(int offset, int limit) 
	{
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		List<AuthProvider> authP = authProvider.findAll(offset, limit);
        
        for( AuthProvider p : authP )
        {
        	Map<String, Object> row = convertApToMap(p);
        	ret.add(row);        	
        }
		return ret;	

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> addAuthProvider(Map<String, Object> provider) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		try
		{
	    	AuthProvider p = new AuthProvider();
	    	
	    	AuthProviderId id = new AuthProviderId();
	    	
	    	//id.setApplication((String)provider.get(Constants.PROVIDERAPP));
	    	id.setApplication((lSrvc.getSecContext().application));
	    	id.setProviderName((String)provider.get(Constants.PROVIDERNAME));
	    	
	    	p.setId(id);
	    	
	    	Object conf = provider.get(Constants.PROVIDERCONF);
	    	if (conf instanceof String)
	    	{
	    		p.setConfiguration((String)conf);
	    	}
	    	else if (conf instanceof Map)
	    	{
	    		Gson gson = new Gson();
	    		String json = gson.toJson((Map <String, String>)conf);
	    		//System.out.println("JSON=" + json);
	    		p.setConfiguration(json);
	    	}
	    	else
	    	{
	    		//System.out.println(conf);
	    		ret.put(Constants.STATUS, new Boolean(false));
	    		ret.put(Constants.ERRORMSG, "LDAP JAAS configuration information is not correct.");
	    		return ret;
	    	}
	    		
	    	p.setProviderType((String)provider.get(Constants.PROVIDERTYPE));
	    	
	        authProvider.save(p );
	        
			ret.put(Constants.STATUS, new Boolean(true));
		}
		catch (org.springframework.dao.DataIntegrityViolationException v)
		{			
			ret.put(Constants.STATUS, new Boolean(false));
		}
		catch (Exception x)
		{
			//x.printStackTrace();
			ret.put(Constants.STATUS, new Boolean(false));
			ret.put(Constants.ERRORMSG, "FAILED: database operations: " + x.getMessage());
		}
		return ret;

	}

	@Override
	public Map<String, Object> deleteAuthProvider(Map<String, Object> provider) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		try
		{
			AuthProvider p = convertToProvider(provider, lSrvc.getSecContext().application);
			authProvider.delete(p);
			ret.put(Constants.STATUS, new Boolean(true));
		}
		catch (Exception x)
		{
			//x.printStackTrace();
			ret.put(Constants.STATUS, new Boolean(false));
			ret.put(Constants.ERRORMSG, "FAILED: database operations: " + x.getMessage());
		}
		return ret;
	}

	@Override
	public Map<String, Object> updateAuthProvider(Map<String, Object> provider) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		try
		{
			AuthProvider p = convertToProvider(provider, lSrvc.getSecContext().application);
			authProvider.update(p);
			ret.put(Constants.STATUS, new Boolean(true));
		}
		catch (Exception x)
		{
			//x.printStackTrace();
			ret.put(Constants.STATUS, new Boolean(false));
			ret.put(Constants.ERRORMSG, "FAILED: database operations: " + x.getMessage());
		}
		return ret;
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

	private Map<String, Object> convertApToMap(AuthProvider p) 
	{

		Map <String, Object> ret = new HashMap <String, Object> ();
		
		if (null != p)
		{
			AuthProviderId id = p.getId();
			
			if (null != id)
			{
				if (null != id.getApplication())
				{
					ret.put(Constants.PROVIDERAPP, id.getApplication());
				}
				if (null != id.getProviderName())
				{
					ret.put(Constants.PROVIDERNAME, id.getProviderName());
				}
			}
			
			if (null != p.getConfiguration())
			{
				ret.put(Constants.PROVIDERCONF, p.getConfiguration());
			}						
			
			if (null != p.getProviderType())
			{
				ret.put(Constants.PROVIDERTYPE, p.getProviderType());
			}
		}
		
		return ret;
	}

	private AuthProvider convertToProvider(Map<String, Object> provider, String application) 
	{
		AuthProvider p = new AuthProvider();
		AuthProviderId id = new AuthProviderId();
		
		id.setApplication(application);

		if (null != provider.get(Constants.PROVIDERNAME))
		{
			id.setProviderName(((String) provider.get(Constants.PROVIDERNAME)));
			p.setId(id);
		}
		
		if (null != provider.get(Constants.PROVIDERTYPE))
		{
			p.setProviderType(((String) provider.get(Constants.PROVIDERTYPE)));
		}
		
		if (null != provider.get(Constants.PROVIDERCONF))
		{
			if (provider.get(Constants.PROVIDERCONF) instanceof String)
			{
				p.setConfiguration(((String) provider.get(Constants.PROVIDERCONF)));
			}
			else if (provider.get(Constants.PROVIDERCONF) instanceof Map)
			{
				Gson gson = new Gson();
	    		String json = gson.toJson((Map <String, String>)provider.get(Constants.PROVIDERCONF));
	    		//System.out.println("JSON=" + json);
	    		p.setConfiguration(json);
			}
		}
					
		return p;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refreshLdapConfigurations() 
	{
		List<AuthProvider> authP = authProvider.findAllByTypeOrderByApplication("ldap");
		//System.out.println(authP.size());
		LoginContextConfig lCfg = LoginContextConfig.getLoginCtxCfg(lSrvc.getSecContext());
		
		Map <String, Object> newCfg = new HashMap <String, Object> ();
		
		List <Map <String, String>> lm = new ArrayList <Map <String, String>> ();
		for( AuthProvider p : authP )
        {
			//System.out.println(p);
			
        	if (null != p.getConfiguration())
        	{        		
        	    try 
        	    {        	    	
        	    	HashMap<String, String> conf = new Gson().fromJson(p.getConfiguration(), new TypeToken<HashMap<String, String>>(){}.getType());
        	    	conf.put(Constants.PROVIDERAPP, p.getId().getApplication());
        	    	conf.put(Constants.PROVIDERNAME, p.getId().getProviderName());
        	    	conf.put(Constants.PROVIDERTYPE, p.getProviderType());
        	    	
        	    	List <Map <String, String>> list;
        	    	if (null == newCfg.get(p.getId().getApplication()))
        	    	{
        	    		list = new ArrayList <Map <String, String>> ();
        	    		newCfg.put(p.getId().getApplication(), list);
        	    	}
        	    	else
        	    	{
        	    		list = (List <Map <String, String>>) newCfg.get(p.getId().getApplication());        	    		
        	    	}
        	    	
        	    	list.add(conf);
        	    	
        	    	
        	    	//conf.put("type", "ldap");
        	    	//System.out.println(map);
        	    	
					//prop.load(new StringReader(p.getConfiguration()));
					//Map <String, Object> conf = new HashMap <String, Object> ();
					
					/*
					conf.put("loginmodule", prop.getProperty("com.sun.security.auth.module.LdapLoginModule"));
					conf.put("userProvider", prop.getProperty("ldap://ldap-svr/ou=people,dc=example,dc=com"));
					conf.put("userFilter", prop.getProperty("(&(uid={USERNAME})(objectClass=inetOrgPerson))"));
					conf.put("authzIdentity", prop.getProperty("{EMPLOYEENUMBER}"));		
					conf.put("type", "ldap");
					*/
        	    	lm.add(conf);										
				} 
        	    catch (Exception e) 
        	    {
					e.printStackTrace();
				}        	    
        	}
        	
        }
		if (lm.size() > 0)
		{
			//System.out.println("MAP:" + lm);
			Map <String, Object> p = new HashMap <String, Object> ();
			p.put("LDAP-List", lm);
			//lCfg.loadLdapConfig(lSrvc.getSecContext(), lm);
			lCfg.loadLdapConfig(lSrvc.getSecContext(), newCfg);
		}
	}
	
	@Override
	public Map <String, Object> getCurrentLognConfig()
	{
		return LoginContextConfig.getLoginCtxCfg(lSrvc.getSecContext()).getLoginCfg();
	}
}