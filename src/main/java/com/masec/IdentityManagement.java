package com.masec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.masec.core.service.SecurityServiceImpl;

public class IdentityManagement 
{
	public static String setApplicationTechnicalUser(SecurityService ctx, String techUser, String techPassword)
	{
		Map<String, Object> user = new HashMap <String, Object> ();
		user.put(Constants.USERNAME, techUser);
		user.put(Constants.PASSWORD, techPassword);
		return (String) ctx.setApplicationTechnicalUser(user).get(Constants.LOGINSESSIONID);
	}
	
	public static Boolean addUser(SecurityService ctx, String sessId, Map<String, Object> user)
	{
		Map<String, Object> sess = new HashMap <String, Object> ();
		sess.put(Constants.LOGINSESSIONID, sessId);
		return (Boolean) ctx.addUser(sess, user).get(Constants.STATUS);
	}
	
	public static Boolean deleteUser(SecurityService ctx, String sessId, Map<String, Object> user)
	{
		Map<String, Object> sess = new HashMap <String, Object> ();
		sess.put(Constants.LOGINSESSIONID, sessId);
		return (Boolean) ctx.deleteUser(sess, user).get(Constants.STATUS);
	}
	
    public static String login(SecurityService service,  String userName, String pw)
    {
    	Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, userName);
        user.put(Constants.PASSWORD, pw);
        Map <String, Object> ret = service.login(user);
        return (String) ret.get(Constants.LOGINSESSIONID);    	
    }
    
    public static Boolean updateUser(SecurityService service,  String sessionId, Map <String, Object> user)
    {   
    	user.put(Constants.LOGINSESSIONID, sessionId);
        Map <String, Object> ret = service.updateUser(user);
        return (Boolean) ret.get(Constants.STATUS);    	
    }
    
    public static Boolean updatePassword(SecurityService service,  String sessionId, String oldPasswd, String newPasswd, String userName)
    {
    	System.out.println("CLientUtil.updatePassword");
    	System.out.println(userName + ", " + sessionId);
    	Map <String, Object> user = new HashMap <String, Object> ();
    	user.put(Constants.LOGINSESSIONID, sessionId);
        user.put(Constants.PASSWORD, oldPasswd);
        user.put(Constants.NEWPASSWORD, newPasswd);
        user.put(Constants.USERNAME, userName);
        
        Map <String, Object> ret = service.updatePassword(user);
        return (Boolean) ret.get(Constants.STATUS);    	
    }
    
    public static List<Map<String, Object>> listUsers(SecurityService service, String sessionId, int offset, int limit)
	{
    	Map <String, Object> user = new HashMap <String, Object> ();
    	user.put(Constants.LOGINSESSIONID, sessionId);
        user.put(Constants.OFFSET, offset);
        user.put(Constants.LIMIT, limit);
        
        return service.listUser(user);
	}
    
	public static void refreshLdapConfigurations(SecurityService ctx, String sessionId)
	{
		Map<String, Object> sess = new HashMap <String, Object> ();
		sess.put(Constants.LOGINSESSIONID, sessionId);
		ctx.refreshLdapConfigurations(sess);
	}
	
	public static Map<String, Object> findGroupByName( SecurityService ctx, String sessionId, String groupName )
	{
		Map <String, Object> req = new HashMap <String, Object> ();
		req.put(Constants.LOGINSESSIONID, sessionId);
		req.put(Constants.G_NAME, groupName);
		return ctx.findGroupByName(req);
	}
	
    public  static Boolean addGroup( SecurityService ctx, String sessionId, Map<String, Object> group )
    {
    	group.put(Constants.LOGINSESSIONID, sessionId);
    	Map <String, Object> ret = ctx.addGroup(group);
        return (Boolean) ret.get(Constants.STATUS);
    }
    
    public static Boolean updateGroup( SecurityService ctx, String sessionId, Map<String, Object> group )
    {
    	group.put(Constants.LOGINSESSIONID, sessionId);
    	Map <String, Object> ret = ctx.updateGroup(group);
        return (Boolean) ret.get(Constants.STATUS);
    }
    
    public static Boolean deleteGroup( SecurityService ctx, String sessionId, String groupName )
    {
    	Map <String, Object> g = new HashMap <String, Object> ();
    	g.put(Constants.LOGINSESSIONID, sessionId);
    	g.put(Constants.G_NAME, groupName);
    	Map <String, Object> ret = ctx.deleteGroup(g);
        return (Boolean) ret.get(Constants.STATUS);
    }
    
    public static List<Map<String, Object>> findAllUserGroups(SecurityService ctx, String sessionId, int offset, int limit )
    {
		Map <String, Object> g = new HashMap <String, Object> ();
    	g.put(Constants.LOGINSESSIONID, sessionId);
        g.put(Constants.OFFSET, offset);
        g.put(Constants.LIMIT, limit);        
		return ctx.findAllUserGroups(g);

    }
    
    public static List<Map<String, Object>> findGroupsByUser(SecurityService ctx, String sessionId, String userName)
    {
		Map <String, Object> g = new HashMap <String, Object> ();
    	g.put(Constants.LOGINSESSIONID, sessionId);
        g.put(Constants.USERNAME, userName);    
		return ctx.findGroupsByUser(g);
    }
    
    public static List<Map<String, Object>> partOfAnyGroup(SecurityService ctx, String sessionId, String userName, List<String> groupNames)
    {
		Map <String, Object> g = new HashMap <String, Object> ();
    	g.put(Constants.LOGINSESSIONID, sessionId);
        g.put(Constants.USERNAME, userName);
        g.put(Constants.G_NAME, groupNames);
		return ctx.partOfAnyGroup(g);
    }
    
    public static Boolean addUerToGroup( SecurityService ctx, String sessionId, String userName, String groupName )
    {
    	Map <String, Object> g = new HashMap <String, Object> ();
    	g.put(Constants.LOGINSESSIONID, sessionId);
        g.put(Constants.USERNAME, userName);
        g.put(Constants.G_NAME, groupName);
        Map <String, Object> ret = ctx.addUerToGroup(g);
        return (Boolean) ret.get(Constants.STATUS);
    }
    
    public static Boolean deleteUserFromGroup(SecurityService ctx, String sessionId, String userName, String groupName )
    {
    	Map <String, Object> g = new HashMap <String, Object> ();
    	g.put(Constants.LOGINSESSIONID, sessionId);
        g.put(Constants.USERNAME, userName);
        g.put(Constants.G_NAME, groupName);
        Map <String, Object> ret = ctx.deleteUserFromGroup(g);
        return (Boolean) ret.get(Constants.STATUS);
    }
    
    //////////////////////////
    
    public static List<Map<String, Object>> listAuthProviderByType(SecurityService ctx, String sessionId, String providerType)
    {
    	Map <String, Object> p = new HashMap <String, Object> ();
    	p.put(Constants.LOGINSESSIONID, sessionId);
        p.put(Constants.USERNAME, providerType);
        return ctx.listAuthProviderByType(p);
    }
	
    public static List<Map<String, Object>> listAuthProviders(SecurityService ctx, String sessionId, int offset, int limit)
    {
    	Map <String, Object> p = new HashMap <String, Object> ();
    	p.put(Constants.LOGINSESSIONID, sessionId);
        p.put(Constants.OFFSET, offset);
        p.put(Constants.LIMIT, limit);        
		return ctx.listAuthProviders(p);
    }
	
	public static Boolean addAuthProvider(SecurityService ctx, String sessionId, Map<String, Object> provider)
	{
		provider.put(Constants.LOGINSESSIONID, sessionId);
    	Map <String, Object> ret = ctx.addAuthProvider(provider);
        return (Boolean) ret.get(Constants.STATUS);
	}
	
	public static Boolean deleteAuthProvider(SecurityService ctx, String sessionId, String providerName)
	{
		Map <String, Object> provider = new HashMap <String, Object> ();
		provider.put(Constants.LOGINSESSIONID, sessionId);
		provider.put(Constants.PROVIDERNAME, providerName);
    	Map <String, Object> ret = ctx.deleteAuthProvider(provider);
        return (Boolean) ret.get(Constants.STATUS);
	}
	
	public static Boolean updateAuthProvider(SecurityService ctx, String sessionId, Map<String, Object> provider)
	{
		provider.put(Constants.LOGINSESSIONID, sessionId);
    	Map <String, Object> ret = ctx.updateAuthProvider(provider);
        return (Boolean) ret.get(Constants.STATUS);
	}

	public static Boolean refreshAuthProvider(SecurityService ctx, String sessionId, Map<String, Object> provider)
	{
		provider.put(Constants.LOGINSESSIONID, sessionId);
    	ctx.refreshLdapConfigurations(provider);
        return true;
	}
	
	
	public static void shutdown(SecurityService secCtx, String userName, String password, String sessionId) 
	{
		Map <String, Object> req = new HashMap <String, Object> ();
		req.put(Constants.LOGINSESSIONID, sessionId);
		req.put(Constants.USERNAME, userName);
		req.put(Constants.PASSWORD, password);
		secCtx.shutdown(req);
	}
}
