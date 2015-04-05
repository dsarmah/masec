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
    public static void main(String[] a)
    {
    	
    	// get the security context... if you do not pass any name as an argument then it returns Masec default security provider which is based on database. 
    	SecurityService service = SecurityFactory.getSecCtx();
    	
    	// before you can use, you need to create/re-use a technical user which you can use to create new user (register new user)
    	String sessDt1 = IdentityManagement.setApplicationTechnicalUser(service, "admin101", "admin101Pw");
    	
    	if (null != sessDt1)
    	{
    		System.out.println("SUCCESS: technical user session: " + sessDt1);
    	}
    	else
    	{
    		System.out.println("FAILED: technical user session is NULL");
    		return;
    	}

    	
    	//Create a user object - check Constants.java for all supported members User
    	Map <String, Object> user = new HashMap <String, Object> ();
    	user.put(Constants.USERNAME, "u9");
        user.put(Constants.PASSWORD, "jkjkk");
        user.put(Constants.FIRSTNAME, "tom");
        user.put(Constants.LASTNAME, "jerry");
        
        
        if (!IdentityManagement.addUser(service, sessDt1, user))
        {
        	System.out.println("FAILED to add new user");
        }
        else
        {
	        System.out.println("SUCCESS: added new user: u9");
	        
	        // login..
	        String sessionId = IdentityManagement.login(service, "u9", "jkjkk");
	        
	        if (null != sessionId)
	        {
	        	System.out.println("SUCCESS: user: u9 is logged in and sessionId is: " + sessionId);
	        	if (!IdentityManagement.deleteUser(service, sessDt1, user))
	            {
	            	System.out.println("FAILED to delete user u9");
	            }	
	        	else
	        	{
	        		System.out.println("SUCCESS: user u9 is deleted");
	        	}	        	
	        }
	        else
	        {
	        	System.out.println("FAILED: user u9 failed to logged in");
	        }
        }  
        
        // close the embedded HSQLDB database
        IdentityManagement.shutdown(service, null, null, sessDt1);
    }
}
