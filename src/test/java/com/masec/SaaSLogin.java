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
    	// For simplicity, lets define tenant names here (in practice you may have these defined in some table...
    	// Tenant names must be unique
    	
    	String defaultTenant1 = "dt1";
    	String defaultTenant2 = "dt2";
    	
    	String ldapTenant1 = "ld1";
    	String ldapTenant2 = "ld2";
    	
    	
    	SaaSLogin sec = new SaaSLogin();

    	/*** When you create named SecurityService, users are automatically partitioned *****/
    	/*** creating named security service means passing unique name (tenant name) while creating security service. If you pass duplicate name, then it will return the same objects ***/
    	
    	SecurityService serviceDt1 = SecurityServiceFactory.getSecurityService(defaultTenant1);
    	SecurityService serviceDt2 = SecurityServiceFactory.getSecurityService(defaultTenant2);
    	SecurityService serviceLd1 = SecurityServiceFactory.getSecurityService(ldapTenant1);
    	SecurityService serviceLd2 = SecurityServiceFactory.getSecurityService(ldapTenant2);
    	

    	// As per this example's requirement, serviceDt1 and serviceDt2 will manage and authenticate users against default directory service.
    	// So no need to add directory service provider for serviceDt1 and serviceDt2. But we need to add LDAP information for serviceLd1 and
    	// serviceLd2. We will authenticate against LDAP directory service - we will not add/remove users in the LDAP.
    	
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
     	
        // LDAP configurations for serviceLd2
    	Map <String, String> lC2 = new HashMap <String, String> ();
        lC2.put("debug", "false");
        lC2.put("useSSL", "false");
        lC2.put("userProvider", "ldap://localhost:389/ou=People,dc=maxcrc,dc=com");
        lC2.put("userFilter", "(&(uid={USERNAME})(objectClass=inetOrgPerson))");
        lC2.put("authzIdentity", "{USERNAME}");
         
        // LDAP map for serviceLd2 
        Map <String, Object> ld2 = new HashMap <String, Object> ();
        ld2.put(Constants.PROVIDERNAME, "provider2");
        ld2.put(Constants.PROVIDERTYPE, "ldap");
        ld2.put(Constants.PROVIDERCONF, lC2);
        
     	//Add LDAP map for serviceLd1
        Map <String, Object> ret = serviceLd1.addAuthProvider(ld1);
        if (!(Boolean) ret.get(Constants.STATUS))
        {
        	System.out.println("Failed: adding LDAP configurations to serviceLd1");
        	return;
        }
        serviceLd1.refreshLdapConfigurations();
        
        //Add LDAP map for serviceLd2
        ret = serviceLd2.addAuthProvider(ld2);
        if (!(Boolean) ret.get(Constants.STATUS))
        {
        	System.out.println("Failed: adding LDAP configurations to serviceLd2");
        	return;
        }
        serviceLd2.refreshLdapConfigurations();    	
    	 
        // Test login...
        if (sec.login(serviceLd1, "dilip", "dilip123"))
    	{
    		System.out.println("Successfully login...");
    	}
    	else
    	{
    		System.out.println("Login Failed...");
    	}
        
        // Test login...
        if (sec.login(serviceLd2, "dilip111", "dilip123"))
    	{
    		System.out.println("Successfully login...");
    	}
    	else
    	{
    		System.out.println("Login Failed...");
    	}
        
        /**** END: we have completed the adding LDAP configurations and login ****/
        
        
        /*** Below is for users who are using default directory services but they are not sharing users ****/        
        
    	//Create a user object - check Constants.java for all supported members User
    	Map <String, Object> user1 = new HashMap <String, Object> ();
    	user1.put(Constants.USERNAME, "u9");
        user1.put(Constants.PASSWORD, "jkjkk");
        user1.put(Constants.FIRSTNAME, "tom");
        user1.put(Constants.LASTNAME, "jerry");
        
        boolean addU = false;
        if (addU = sec.addUser(serviceDt1, user1))
        {
        	//if add user is successful then you can start authenticating...
        	if (sec.login(serviceDt1, "u9", "jkjkk"))
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
        	sec.deleteUser(serviceDt1, user1);
        }
        
        Map <String, Object> user2 = new HashMap <String, Object> ();
    	user2.put(Constants.USERNAME, "ddu99");
        user2.put(Constants.PASSWORD, "jkjkk");
        user2.put(Constants.FIRSTNAME, "tom");
        user2.put(Constants.LASTNAME, "jerry");
        
        boolean addU2 = false;
        if (addU2 = sec.addUser(serviceDt2, user2))
        {
        	//if add user is successful then you can start authenticating...
        	if (sec.login(serviceDt2, "ddu99", "jkjkk"))
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
        
        if (addU2)
        {
        	// User exists then delete the user... just to show how to delete
        	sec.deleteUser(serviceDt2, user2);
        }
        
        
    }
}
