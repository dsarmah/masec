package com.masec.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
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
import com.masec.core.dao.GroupDao;
import com.masec.core.dao.UserGroupDao;
import com.masec.core.model.AuthProvider;
import com.masec.core.model.AuthProviderId;
import com.masec.core.model.Group;
import com.masec.core.model.GroupId;
import com.masec.core.model.User;
import com.masec.core.model.UserGroup;
import com.masec.core.model.UserGroupId;
import com.masec.core.model.UserId;

public class SecurityServiceImpl implements SecurityService
{
	private LoginServiceImpl lSrvc;
	private static UserService userService;
	private static AuthProviderDao authProvider;
	private static GroupDao groupService;
	private static UserGroupDao userGroupService;
	
	private static ExpiringMap <String, SecurityInfo> _authMap = new  ExpiringMap <String, SecurityInfo>();
	
	private Logger log = Logger.getLogger(this.getClass());
	
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
		groupService = ( GroupDao)applicationContext.getBean( "groupDao" );
		userGroupService = ( UserGroupDao)applicationContext.getBean( "userGroupDao" );
	}
	
	/*
	private Map<String, Object> isValidSession(String sessionId)
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		ret.put(Constants.STATUS, new Boolean(false));
		
		if (null != sessionId)
		{
			String key = sessionId + "--" + lSrvc.getSecContext().application;
			SecurityInfo v = _authMap.get(key); 
			if (null != v && v.sessionId.equals(sessionId))
			{
				ret.put(Constants.STATUS, true);
				ret.put(Constants.USERNAME, v.userName);
			}
		}
		
		return ret;
	}
	*/
	
	@Override
	public Map<String, Object> login(Map<String, Object> user) 
	{	
		
		if (null != user.get(Constants.LOGINSESSIONID))
		{
			String key = (String)user.get(Constants.LOGINSESSIONID) + "--" + lSrvc.getSecContext().application;
			log.debug("key for expiry map: " + key);
			SecurityInfo v = _authMap.get(key); 
			if (null != v && v.sessionId.equals(user.get(Constants.LOGINSESSIONID)))
			{
				user.put(Constants.STATUS, true);
				user.put(Constants.SECINFO, v);
				return user;
			}					
		}
		
		if (null == user.get(Constants.USERNAME) && null == user.get(Constants.PASSWORD))
		{
			log.debug("no username and password to login...");
			Map <String, Object> ret = new HashMap <String, Object> ();
			ret.put(Constants.STATUS, false);
			return ret;
		}
		
		Map <String, Object> ret = lSrvc.login((String)user.get(Constants.USERNAME), (String)user.get(Constants.PASSWORD));		
		if (null != ret && (Boolean)ret.get(Constants.STATUS))
		{				
			UUID uuid = UUID.randomUUID();
			SecurityInfo sInfo = new SecurityInfo();
			sInfo.sessionId = uuid.toString();
			sInfo.userName = (String)user.get(Constants.USERNAME);
			sInfo.application = lSrvc.getSecContext().application;
			String key = sInfo.sessionId + "--" + lSrvc.getSecContext().application;
			_authMap.put(key, sInfo);
			ret.put(Constants.LOGINSESSIONID, sInfo.sessionId);
			user.put(Constants.SECINFO, sInfo);
			ret.put(Constants.SECINFO, sInfo);
			log.debug("sessionId=" + uuid.toString() + "  for user=" + user.get(Constants.USERNAME) + " return from login=" + ret.get(Constants.STATUS));
			return ret;
		}
		return ret;
	}

	@Override
	public Map<String, Object> addUser(Map<String, Object> user, Map<String, Object> newUser)  
	{		
		Map <String, Object> ret = new HashMap <String, Object>();
		ret.put(Constants.STATUS, new Boolean(false));
		log.debug("addUser username: " + newUser.get(Constants.USERNAME + " check login"));
		if (null != newUser.get(Constants.USERNAME) && null != newUser.get(Constants.PASSWORD) && (Boolean)login(user).get(Constants.STATUS))
		{			
			try
			{
				log.debug("addUser adding user: " + newUser.get(Constants.USERNAME));
				/*
		    	User u = new User();
		    	UserId id = new UserId();
		    	id.setUserName((String)newUser.get(Constants.USERNAME));
		    	id.setApplicationCtx(lSrvc.getSecContext().application);
		    	u.setId(id);
		    	*/
		    	User u = convertToUser(newUser, lSrvc.getSecContext().application);		    	
		    	u.setPassword((String)newUser.get(Constants.PASSWORD));
		    	/*
		    	u.setEmail((String)newUser.get(Constants.EMAIL));
		    	u.setExtendProfile((String)newUser.get(Constants.EXTENDEDPROFILE));
		    	u.setFirstName((String)newUser.get(Constants.FIRSTNAME));
		    	u.setLastName((String)newUser.get(Constants.LASTNAME));
		    	u.setPhone((String)newUser.get(Constants.PHONE));
		    	u.setPicture((byte[])newUser.get(Constants.PICTURE));
		    	u.setSecQn1((String)newUser.get(Constants.SECQN1));
		    	u.setSecQn1Ans((String)newUser.get(Constants.SECQN1ANS));
		    	u.setSecQn2((String)newUser.get(Constants.SECQN2));
		    	u.setSecQn2Ans((String)newUser.get(Constants.SECQN2ANS));
		    	u.setSecQn3((String)newUser.get(Constants.SECQN3));
		    	u.setSecQn3Ans((String)newUser.get(Constants.SECQN3ANS));
		    	*/
		    	
		    	u.setSince(Calendar.getInstance().getTimeInMillis());		    	
		        userService.save(u );		        
				ret.put(Constants.STATUS, new Boolean(true));
			}
			catch (org.springframework.dao.DataIntegrityViolationException v)
			{
				v.printStackTrace();
				ret.put(Constants.STATUS, new Boolean(false));
			}
			catch (Exception x)
			{
				x.printStackTrace();
				ret.put(Constants.STATUS, new Boolean(false));
				ret.put(Constants.ERRORMSG, "FAILED: database operations: " + x.getMessage());
			}
		}
		return ret;
	}

	@Override
	public List<Map<String, Object>> listUser(Map <String, Object> request) 
	{
		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		if ((Boolean)login(request).get(Constants.STATUS))
		{		
			List<User> users = userService.findAll((Integer)request.get(Constants.OFFSET), (Integer)request.get(Constants.LIMIT));
	        
	        for( User u : users )
	        {
	        	Map<String, Object> row = convertToMap(u);
	        	ret.add(row);
	        	
	            //System.out.println( "\t" + u.getId().getUserName() );
	        }	        
		}
		
		return ret;
	}

	public void shutdown(Map<String, Object> user) 
	{
		if ((Boolean)login(user).get(Constants.STATUS))
		{
			userService.shutdown();
		}
	}
	
	@Override
	public Map<String, Object> deleteUser(Map<String, Object> user, Map<String, Object> deleteUser) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		ret.put(Constants.STATUS, new Boolean(false));
		
		if (null != deleteUser.get(Constants.USERNAME) && (Boolean)login(user).get(Constants.STATUS))
		{			
			try
			{
				User u = convertToUser(deleteUser, lSrvc.getSecContext().application);
				userService.delete(u);
				ret.put(Constants.STATUS, new Boolean(true));
			}
			catch (Exception x)
			{
				//x.printStackTrace();
				ret.put(Constants.STATUS, new Boolean(false));
				ret.put(Constants.ERRORMSG, "FAILED: database operations: " + x.getMessage());
			}
		}
		
		return ret;
	}

	@Override
	public Map<String, Object> updateUser(Map<String, Object> user) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		ret.put(Constants.STATUS, new Boolean(false));
		if ((Boolean)login(user).get(Constants.STATUS))
		{			

			try
			{
				User u = convertToUser(user, lSrvc.getSecContext().application);
				// Don't allow to update password and salt. For that user must call updatePassword(...) method
				if (null != u.getPassword() || null != u.getSalt())
				{
					//ret.put(Constants.STATUS, new Boolean(false));
					//ret.put(Constants.ERRORMSG, "You can not update password and salt! Use updatePassword(...) to update password and salt.");
					//return ret;
					u.setPassword(null);
					u.setSalt(null);
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
		}
		
		return ret;
	}

	@Override
	public Map<String, Object> updatePassword(Map<String, Object> user) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		ret.put(Constants.STATUS, new Boolean(false));
		log.debug("Username supplied: " + user.get(Constants.USERNAME));
		if ((Boolean)login(user).get(Constants.STATUS) && null != user.get(Constants.USERNAME))
		{			
			if (null != user.get(Constants.SECINFO) && ((String)user.get(Constants.USERNAME)).equals((String)((SecurityInfo)user.get(Constants.SECINFO)).userName))
			{
				try
				{
					log.debug("Calling updatePassword on userService");
					User u = convertToUser(user, lSrvc.getSecContext().application);
					userService.updatePassword(u, (String)user.get(Constants.NEWPASSWORD));
					ret.put(Constants.STATUS, new Boolean(true));
				}
				catch (Exception x)
				{
					x.printStackTrace();
					ret.put(Constants.STATUS, new Boolean(false));
					ret.put(Constants.ERRORMSG, "FAILED: database operations: " + x.getMessage());
				}
			}
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
	public List<Map<String, Object>> listAuthProviderByType(Map<String, Object> req) 
	{		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		if ((Boolean)login(req).get(Constants.STATUS))
		{		
			List<AuthProvider> authP = authProvider.findByTypeAndApplication((String)req.get(Constants.PROVIDERTYPE), lSrvc.getSecContext().application);
	        
	        for( AuthProvider p : authP )
	        {
	        	Map<String, Object> row = convertApToMap(p);
	        	ret.add(row);        	
	        }	       
		}
		return ret;	
	}


	@Override
	public List<Map<String, Object>> listAuthProviders(Map<String, Object> req) 
	{
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		if ((Boolean)login(req).get(Constants.STATUS))		
		{
			List<AuthProvider> authP = authProvider.findAll((Integer)req.get(Constants.OFFSET), (Integer)req.get(Constants.LIMIT));
	        
	        for( AuthProvider p : authP )
	        {
	        	Map<String, Object> row = convertApToMap(p);
	        	ret.add(row);        	
	        }
	        
	    }
		
		return ret;	

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> addAuthProvider(Map<String, Object> provider) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		ret.put(Constants.STATUS, new Boolean(false));
		if ((Boolean)login(provider).get(Constants.STATUS))
		{			
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
		}
		return ret;

	}

	@Override
	public Map<String, Object> deleteAuthProvider(Map<String, Object> provider) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		ret.put(Constants.STATUS, new Boolean(false));
		
		if ((Boolean)login(provider).get(Constants.STATUS))
		{
			try
			{
				log.debug("deleteAuthProvider for provider: " + provider.get(Constants.PROVIDERNAME));
				AuthProvider p = convertToProvider(provider, lSrvc.getSecContext().application);
				authProvider.delete(p);
				ret.put(Constants.STATUS, new Boolean(true));
			}
			catch (Exception x)
			{
				x.printStackTrace();
				ret.put(Constants.STATUS, new Boolean(false));
				ret.put(Constants.ERRORMSG, "FAILED: database operations: " + x.getMessage());
			}
		}
		
		return ret;
	}

	@Override
	public Map<String, Object> updateAuthProvider(Map<String, Object> provider) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		ret.put(Constants.STATUS, new Boolean(false));
		
		if ((Boolean)login(provider).get(Constants.STATUS))
		{
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
	    		@SuppressWarnings("unchecked")
				String json = gson.toJson((Map <String, String>)provider.get(Constants.PROVIDERCONF));
	    		//System.out.println("JSON=" + json);
	    		p.setConfiguration(json);
			}
		}
					
		return p;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refreshLdapConfigurations(Map<String, Object> req) 
	{
		if ((Boolean)login(req).get(Constants.STATUS))
		{
			List<AuthProvider> authP = authProvider.findAllByTypeOrderByApplication("ldap");
			//System.out.println(authP.size());
			LoginContextConfig lCfg = LoginContextConfig.getLoginCtxCfg(lSrvc.getSecContext());
			
			Map <String, Object> newCfg = new HashMap <String, Object> ();
			
			List <Map <String, String>> lm = new ArrayList <Map <String, String>> ();
			for( AuthProvider p : authP )
	        {
				
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
	}
	
	private Map<String, Object> convertGroupToMap(Group g)
	{
    	Map<String, Object> row = new HashMap <String, Object> ();
    	row.put(Constants.G_NAME, g.getId().getgName());
    	row.put(Constants.G_DESCRIPTION, g.getDescription());
    	return row;
	}
	
	private Group convertMapToGroup(Map<String, Object> m)
	{
		GroupId id = new GroupId();
		id.setApplication(lSrvc.getSecContext().application);
		id.setgName((String)m.get(Constants.G_NAME));
		Group g = new Group();
		g.setId(id);
		g.setDescription((String) m.get(Constants.G_DESCRIPTION));
		return g;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map <String, Object> getCurrentLognConfig(Map <String, Object> req)
	{
		if ((Boolean)login(req).get(Constants.STATUS))
		{
			return LoginContextConfig.getLoginCtxCfg(lSrvc.getSecContext()).getLoginCfg();
		}
		return (Map<String, Object>) (new HashMap<String, Object>().put(Constants.STATUS, new Boolean(false)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> findGroupByName(Map <String, Object> req) 
	{		
		if ((Boolean)login(req).get(Constants.STATUS))
		{
			GroupId id = new GroupId();
			id.setgName((String)req.get(Constants.G_NAME));
			id.setApplication(lSrvc.getSecContext().application);
			Group grp = groupService.findByGroupNameAndApplication(id);
			Map<String, Object> ret = convertGroupToMap(grp);
			ret.put(Constants.STATUS, true);
			return ret;
		}
		return (Map<String, Object>) (new HashMap<String, Object>().put(Constants.STATUS, new Boolean(false)));
	}

	/*
	@Override
	public List<Map<String, Object>> findAllGroups(Map <String, Object> req) 
	{
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		if ((Boolean)login(req).get(Constants.STATUS))
		{
			List<Group> grp = groupService.findAll((Integer)req.get(Constants.OFFSET), (Integer)req.get(Constants.LIMIT), lSrvc.getSecContext().application);
	        
	        for( Group g : grp )
	        {
	        	Map<String, Object> row = convertGroupToMap(g);
	        	ret.add(row);        	
	        }
	     	        
		}	
		
		return ret;	
	}
	*/
	
	@Override
	public Map<String, Object> addGroup(Map<String, Object> group) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		
		if ((Boolean)login(group).get(Constants.STATUS))
		{
			try
			{
				if (null != group.get(Constants.G_NAME))
				{
					Group g = convertMapToGroup(group);
					groupService.save(g);
					ret.put(Constants.STATUS, new Boolean(true));
					return ret;
				}
			}
			catch (Exception x)
			{			
			}
		}
		
		ret.put(Constants.STATUS, new Boolean(false));
		return ret;
	}

	@Override
	public Map<String, Object> updateGroup(Map<String, Object> group) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		
		if ((Boolean)login(group).get(Constants.STATUS))
		{
			try
			{
				if (null != group.get(Constants.G_NAME))
				{
					Group g = convertMapToGroup(group);
					groupService.update(g);
					ret.put(Constants.STATUS, new Boolean(true));
					return ret;
				}
			}
			catch (Exception x)
			{			
			}
		}
		
		ret.put(Constants.STATUS, new Boolean(false));
		return ret;
	}

	@Override
	public Map<String, Object> deleteGroup(Map<String, Object> group) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		
		if ((Boolean)login(group).get(Constants.STATUS))
		{
			try		
			{
				if (null != group.get(Constants.G_NAME))
				{
					Group g = convertMapToGroup(group);
					groupService.delete(g);
					ret.put(Constants.STATUS, new Boolean(true));
					return ret;
				}
			}
			catch (Exception x)
			{			
			}
		}
		
		ret.put(Constants.STATUS, new Boolean(false));
		return ret;
	}

	private Map<String, Object> convertUserGroupToMap(UserGroup g)
	{
		Map<String, Object> row = new HashMap <String, Object> ();
    	row.put(Constants.G_NAME, g.getId().getgName());
    	row.put(Constants.USERNAME, g.getId().getuName());
    	row.put(Constants.APPLICATION, g.getApplication());
    	return row;
	}
	
	/*
	private UserGroup convertMapToUserGroup(Map<String, Object> m)
	{
		UserGroupId id = new UserGroupId();
		id.setuName((String)m.get(Constants.USERNAME));
		id.setgName((String)m.get(Constants.G_NAME));
		UserGroup g = new UserGroup();
		g.setId(id);
		return g;
	}
	*/
	
	@Override
	public List<Map<String, Object>> findAllUserGroups(Map <String, Object> req) 
	{
		log.debug("findAllUserGroups  sessionId: " + req.get(Constants.LOGINSESSIONID) + ", application: " + lSrvc.getSecContext().application + ", " + req.get(Constants.OFFSET) + ", " + req.get(Constants.LIMIT));
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		if ((Boolean)login(req).get(Constants.STATUS))
		{
			List<UserGroup> grp = userGroupService.findUserGroups(lSrvc.getSecContext().application, (Integer)req.get(Constants.OFFSET), (Integer)req.get(Constants.LIMIT));
			log.debug("findAllUserGroups size return: " + grp.size());
	        for( UserGroup g : grp )
	        {
	        	Map<String, Object> row = convertUserGroupToMap(g);
	        	ret.add(row);        	
	        }
		}
		return ret;	
	}

	@Override
	public List<Map<String, Object>> findGroupsByUser(Map <String, Object> req) 
	{
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		if (null != req.get(Constants.USERNAME) && (Boolean)login(req).get(Constants.STATUS))
		{		
			List <UserGroup> ug = userGroupService.findGroupsByUser(lSrvc.getSecContext().application, (String)req.get(Constants.USERNAME));
			for (UserGroup r: ug)
			{
				ret.add(convertUserGroupToMap(r));
			}
		}
		
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> partOfAnyGroup(Map<String, Object> req) 
	{
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		if (null != req.get(Constants.USERNAME) && (Boolean)login(req).get(Constants.STATUS))
		{
			List <UserGroup> ug = userGroupService.isPartOfAnyGroup(lSrvc.getSecContext().application, (String)req.get(Constants.USERNAME), (List<String>)req.get(Constants.G_NAME));
			for (UserGroup r: ug)
			{
				ret.add(convertUserGroupToMap(r));
			}
		}
		
		return ret;
	}

	@Override
	public Map<String, Object> addUerToGroup(Map<String, Object> group) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		ret.put(Constants.STATUS, new Boolean(false));
		log.debug("addUerToGroup...1");
		if ((Boolean)login(group).get(Constants.STATUS))
		{
			try
			{
				if (null != group.get(Constants.G_NAME) && null != group.get(Constants.USERNAME))
				{
					log.debug("addUerToGroup...2");
					GroupId gid = new GroupId();
					gid.setApplication(lSrvc.getSecContext().application);
					gid.setgName((String) group.get(Constants.G_NAME));
					Group g = groupService.findByGroupNameAndApplication(gid);
					
					if (null != g)
					{
						log.debug("addUerToGroup...3");
						UserGroup ug = new UserGroup();
						ug.setApplication(lSrvc.getSecContext().application);
						UserGroupId ugid = new UserGroupId();
						ugid.setgName((String) group.get(Constants.G_NAME));
						ugid.setuName((String) group.get(Constants.USERNAME));
						ug.setId(ugid);
						userGroupService.save(ug);
						ret.put(Constants.STATUS, new Boolean(true));
					}
				}
			}
			catch (Exception x)
			{		
				x.printStackTrace();
			}
		}
		
		return ret;
	}

	@Override
	public Map<String, Object> deleteUserFromGroup(Map<String, Object> group) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		ret.put(Constants.STATUS, new Boolean(false));
		
		if ((Boolean)login(group).get(Constants.STATUS))
		{
			try
			{
				if (null != group.get(Constants.G_NAME) && null != group.get(Constants.USERNAME))
				{
					UserGroup ug = new UserGroup();
					ug.setApplication(lSrvc.getSecContext().application);
					UserGroupId ugid = new UserGroupId();
					ugid.setgName((String) group.get(Constants.G_NAME));
					ugid.setuName((String) group.get(Constants.USERNAME));
					ug.setId(ugid);
					userGroupService.delete(ug);
					ret.put(Constants.STATUS, new Boolean(true));
				}
			}
			catch (Exception x)
			{			
			}
		}
		
		return ret;
	}

	@Override
	public Map<String, Object> setApplicationTechnicalUser(Map<String, Object> user) 
	{
		Map <String, Object> ret = new HashMap <String, Object>();
		ret.put(Constants.STATUS, new Boolean(false));
		if (null != user.get(Constants.USERNAME) && null != user.get(Constants.PASSWORD))
		{
			ret = login(user);
			if (!(Boolean)ret.get(Constants.STATUS))
			{
				try
				{
			    	User u = convertToUser(user, lSrvc.getSecContext().application);		    	
			    	u.setPassword((String)user.get(Constants.PASSWORD));		    	
			    	u.setSince(Calendar.getInstance().getTimeInMillis());		    	
			        userService.save(u );		        
			        
			        UUID uuid = UUID.randomUUID();
					SecurityInfo sInfo = new SecurityInfo();
					sInfo.sessionId = uuid.toString();
					sInfo.userName = (String)user.get(Constants.USERNAME);
					sInfo.application = lSrvc.getSecContext().application;
					String key = sInfo.sessionId + "--" + lSrvc.getSecContext().application;
					_authMap.put(key, sInfo);
					
					ret.put(Constants.LOGINSESSIONID, sInfo.sessionId);				
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
			}
		}
		return ret;
	}
}