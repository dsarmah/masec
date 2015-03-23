package com.masec;

import java.util.HashMap;
import java.util.Map;

import com.masec.core.service.SecurityServiceFactory;

/*
 * This example shows how to add and delete user in the local directory service and login
 * For production, make sure to update masecDbContext.xml file to persist directory service in a suitable database.
 * By default, HSQLDB database is embedded to store users.
 */

public class SimpleLogin 
{
    private boolean addUser(SecurityService service, Map <String, Object> user)
    {
        Map <String, Object> ret = service.addUser(user);
        return (Boolean) ret.get(Constants.STATUS);    	
    }
    
    private boolean deleteUser(SecurityService service, Map <String, Object> user)
    {
        Map <String, Object> ret = service.deleteUser(user);
        return (Boolean) ret.get(Constants.STATUS);    	
    }
    
    private boolean login(SecurityService service, String userName, String pw)
    {
    	Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, userName);
        user.put(Constants.PASSWORD, pw);
        
        Map <String, Object> ret = service.login(user);
        return (Boolean) ret.get(Constants.STATUS);    	
    }
    
    public static void main(String[] a)
    {
    	SimpleLogin sec = new SimpleLogin();
    	//First get a default security service. Default context manages users in local database. It does not use LDAP.
    	SecurityService service = SecurityServiceFactory.getSecurityService();
    	
    	//Create a user object - check Constants.java for all supported members User
    	Map <String, Object> user = new HashMap <String, Object> ();
    	user.put(Constants.USERNAME, "u9");
        user.put(Constants.PASSWORD, "jkjkk");
        user.put(Constants.FIRSTNAME, "tom");
        user.put(Constants.LASTNAME, "jerry");
        
        boolean addU = false;
        if (addU = sec.addUser(service, user))
        {
        	//if add user is successful then you can start authenticating...
        	if (sec.login(service, "u9", "jkjkk"))
        	{
        		System.out.println("Successfully login...");
        	}
        	else
        	{
        		System.out.println("Login Failed...");
        	}
        }
        else
        {
        	System.out.println("User could not be added...");
        }
        
        if (addU)
        {
        	// User exists then delete the user... just to show how to delete
        	sec.deleteUser(service, user);
        }
    }
}
