package com.masec;

import java.util.HashMap;
import java.util.Map;

import com.masec.core.service.SecurityServiceFactory;

/*
 * Please check SimpleLogin.java before you check this out.
 * 
 * Suppose, you have a SaaS application where some tenants (all the users of a tenant) use Masec's default directory service and some others use 
 * their own LDAP. This means, you need to configure specific LDAP configurations for some of the tenants.
 */

public class SaaSLogin 
{
        
    public static void main(String[] a)
    {
    	// For simplicity, lets define tenant names here (in practice you may have these defined in some table...
    	// Tenant names must be unique. In this example dt1 and ld1 are two tenants who are using same SaaS application.
    	
    	String defaultTenant1 = "dt1";	//for example this tenant uses Masec's database based security provider    	
    	String ldapTenant1 = "ld1";		//for example this tenant uses its own corporate LDAP server

    	/*** When you create named SecurityService, users are automatically partitioned *****/
    	/*** creating named security service means passing unique name (tenant name) when creating security service. If you pass duplicate name, then it will return the same objects ***/
    	
    	SecurityService defaultSecSrv = SecurityFactory.getSecCtx(defaultTenant1); 
    	
    	// you must be able to login to the security to use it. At this point you do not have any user. So, setup a technical user for your SaaS application so it can call security APIs.
    	// A technical user necessary otherwise you wouldn't be able to add any user in to this system. This means, user technical user to add an user to the application. After that 
    	// added user can add other users. When you setup technical user, you don't have to be already logged in. So be careful to use this API.
    	//
    	// It first tries to login if successful then return session id - otherwise it creates the new technical user.  
    	
    	
    	// before you can use, you need to create/re-use a technical user which you can use to create new user (register new user)
    	String sessDt1 = IdentityManagement.setApplicationTechnicalUser(defaultSecSrv, "admin101", "admin101Pw");
    	
    	if (null != sessDt1)
    	{
    		System.out.println("SUCCESS: technical user session: " + sessDt1);
    	}
    	else
    	{
    		System.out.println("FAILED: technical user session is NULL");
    		return;
    	}
    	
    	// lets create a new user for "dt1"
    	Map <String, Object> user1 = new HashMap <String, Object> ();
    	user1.put(Constants.USERNAME, "u9");
        user1.put(Constants.PASSWORD, "jkjkk");
        user1.put(Constants.FIRSTNAME, "tom");
        user1.put(Constants.LASTNAME, "jerry");
        
        
        if (!IdentityManagement.addUser(defaultSecSrv, sessDt1, user1))
        {
        	System.out.println("FAILED to add new user");
        }
        else
        {
	        System.out.println("SUCCESS: added new user: u9");
	        
	        String sessionId = IdentityManagement.login(defaultSecSrv, "u9", "jkjkk");
	        
	        if (null != sessionId)
	        {
	        	System.out.println("SUCCESS: user: u9 is logged in and sessionId is: " + sessionId);
	        	if (!IdentityManagement.deleteUser(defaultSecSrv, sessDt1, user1))
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
    	
        /*
         * This tenant has LDAP configure. We do not support user management for LDAP. If you use user management functions then they will manage user's in local database.
         * So, we expect that you already have at least one user in LDAP wich you can use as technical user for your SaaS application to login.
         * 
         * But before even login against LDAP, you need to configure LDAP server in Masec. All configuration APIs are controlled by login. This means you must login to configure Masec for LDAP.
         * So create a technical user for your SaaS application in the local database. 
         */

        SecurityService serviceLd1 = SecurityServiceFactory.getSecurityService(ldapTenant1);    	
        String sessLd1 = IdentityManagement.setApplicationTechnicalUser(serviceLd1, "admin201", "admin201Pw");
        
        // You need to configure LDAP for serviceLd1 so that you can authenticate against LDAP.
    	// We will authenticate against LDAP directory service - we will not add/remove users in the LDAP.
    	
    	//LDAP configuration for serviceLd1
    	Map <String, String> lC1 = new HashMap <String, String> ();
        lC1.put("debug", "false");
        lC1.put("useSSL", "false");
        lC1.put("userProvider", "ldap://localhost:389/ou=People,dc=maxcrc,dc=com");
        lC1.put("userFilter", "(&(uid={USERNAME})(objectClass=inetOrgPerson))");
        lC1.put("authzIdentity", "{USERNAME}");
         
        // LDAP map for serviceLd1 
        Map <String, Object> ld1 = new HashMap <String, Object> ();
        ld1.put(Constants.PROVIDERNAME, "provider1");
        ld1.put(Constants.PROVIDERTYPE, "ldap");
        ld1.put(Constants.PROVIDERCONF, lC1);
        
        // Add the sessionId of the technical user
        ld1.put(Constants.LOGINSESSIONID, sessLd1);
        
     	//Add LDAP map for serviceLd1
        
        Map <String, Object> ret = serviceLd1.addAuthProvider(ld1);
        if (!(Boolean) ret.get(Constants.STATUS))
        {
        	System.out.println("Failed: adding LDAP configurations to serviceLd1");
        	return;
        }
        
        // refresh the configurations        
        IdentityManagement.refreshLdapConfigurations(serviceLd1, sessLd1);
        
            	 
        // Test login...
        if (null != IdentityManagement.login(serviceLd1, "dilip", "dilip123"))
    	{
    		System.out.println("SUCCESS: LDAP login");
    	}
    	else
    	{
    		System.out.println("FAILED: LDAP Login");
    	}
        
        
        // delete the LDAP configurations...
        Map <String, Object> p = new HashMap <String, Object> ();
        p.put(Constants.PROVIDERNAME, "provider1");
        p.put(Constants.PROVIDERTYPE, "ldap");
        
        // Add the sessionId of the technical user
        p.put(Constants.LOGINSESSIONID, sessLd1);
        
        ret = serviceLd1.deleteAuthProvider(p);
        if (!(Boolean)ret.get(Constants.STATUS))
        {
        	System.out.println("FAILED: removing LDAP configurations failed.");
        }
        
        // to shutdown embedded HSQLDB
        IdentityManagement.shutdown(serviceLd1, null, null, sessLd1);
    }
}
