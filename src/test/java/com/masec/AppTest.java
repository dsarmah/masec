package com.masec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.masec.core.service.SecurityServiceFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase
{
    public AppTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    protected SecurityService service, service1, service2, service3;
    
    protected void setUp()
    {
    	service = SecurityServiceFactory.getSecurityService();
        service1 = SecurityServiceFactory.getSecurityService();
        service2 = SecurityServiceFactory.getSecurityService("myapp");
        service3 = SecurityServiceFactory.getSecurityService("telemetry");
    }
    
    protected void tearDown()
    {
    	if (null != service)
    	{
    		service.shutdown();
    	}
    	if (null != service1)
    	{
    		service1.shutdown();
    	}
        if (null != service2)
    	{
        	service2.shutdown();
    	}
        if (null != service3)
    	{
        	service3.shutdown();
    	}
    }
    
    public boolean addUsers(SecurityService service)
    {
    	//System.out.println("addUsers(...)");
    	Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, "u9");
        user.put(Constants.PASSWORD, "jkjkk");
        
        Map <String, Object> ret = service.addUser(user);
        return (Boolean) ret.get(Constants.STATUS);
    	
    }
    
    public boolean deleteUsers(SecurityService service)
    {
    	//System.out.println("deleteUsers(...)");
    	Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, "u9");
        
        Map <String, Object> ret = service.deleteUser(user);
        return (Boolean) ret.get(Constants.STATUS);
    	
    }
    
    public boolean login(SecurityService service, String userid, String pw)
    {
    	//System.out.println("login(...)");
    	Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, userid);
        user.put(Constants.PASSWORD, pw);
        
        Map <String, Object> ret = service.login(user);
        return (Boolean) ret.get(Constants.STATUS);
    	
    }

    @SuppressWarnings("unchecked")
	public void testUnit()
    {
    	assertFalse(login(service2, "u9", "jkjkk"));
        
        assertTrue( addUsers(service1) );
        assertTrue(login(service1, "u9", "jkjkk"));
        
        assertTrue( addUsers(service2) );
        assertTrue(login(service2, "u9", "jkjkk"));
        assertTrue( deleteUsers(service2) );
        assertFalse(login(service2, "u9", "jkjkk"));
        assertTrue( deleteUsers(service1) );
        
    	Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, "test1");
        user.put(Constants.PASSWORD, "jkjkk");
        user.put(Constants.FIRSTNAME, "jkjkk");
        
        Map <String, Object> ret = service.addUser(user);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        assertTrue(login(service, "test1", "jkjkk"));
        
        ret = service.addUser(user); 
        assertFalse((Boolean) ret.get(Constants.STATUS)); //Same user can not added twice
        
        user.put(Constants.FIRSTNAME, "jkjkk");
        ret = service.updateUser(user);
        assertFalse((Boolean) ret.get(Constants.STATUS)); //Password can not be updated by this API.
        
        user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, "test1");
        user.put(Constants.FIRSTNAME, "tom");
        ret = service.updateUser(user);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        
        user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, "test1");
        user.put(Constants.PASSWORD, "indigo");
        ret = service.updatePassword(user);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        
        assertTrue(login(service, "test1", "indigo"));
        
        List <Map <String, Object>> rows = service.listUser(0, 10);
        assertTrue(rows.size() > 0);
        service.deleteUser(user);
        
        String ldapConf = "{" + 
        		"\"" + "debug" + "\":" + 
    			"\"" + "false" + "\"," +
    			"\"" + "useSSL" + "\":" + 
    			"\"" + "false" + "\"," +
    			//"\"" + Constants.LDAP_LOGINMODULE + "\":" + 
    			//"\"" + "com.sun.security.auth.module.LdapLoginModule" + "\"," +
    			"\"" + Constants.LDAP_USERPROVIDER + "\":" + 
    			"\"" + "ldap://localhost:389/ou=People,dc=maxcrc,dc=com" + "\"," +
				"\"" + Constants.LDAP_USERFILTER + "\":" + 				
				"\"" + "(&(uid={USERNAME})(objectClass=inetOrgPerson))" + "\"," +
				"\"" + Constants.LDAP_USERIDENTITY + "\":" +  
				"\"" + "{USERNAME}" + "\"" + 
				"}";
    	
        String ldapConfErr = "{" + 
        		"\"" + "debug" + "\":" + 
    			"\"" + "false" + "\"," +
    			"\"" + "useSSL" + "\":" + 
    			"\"" + "false" + "\"," +
    			//"\"" + Constants.LDAP_LOGINMODULE + "\":" + 
    			//"\"" + "com.sun.security.auth.module.LdapLoginModule" + "\"," +
    			"\"" + Constants.LDAP_USERPROVIDER + "\":" + 
    			"\"" + "ldap://localhost:3899/ou=People,dc=maxcrc,dc=com" + "\"," +
				"\"" + Constants.LDAP_USERFILTER + "\":" + 				
				"\"" + "(&(uid={USERNAME})(objectClass=inetOrgPerson))" + "\"," +
				"\"" + Constants.LDAP_USERIDENTITY + "\":" +  
				"\"" + "{USERNAME}" + "\"" + 
				"}";
    	
    	Map <String, Object> p = new HashMap <String, Object> ();
    	//p.put(Constants.PROVIDERAPP, "myapp");
    	p.put(Constants.PROVIDERNAME, "101-dd");
    	p.put(Constants.PROVIDERTYPE, "ldap");
    	p.put(Constants.PROVIDERCONF, ldapConfErr);
    	    	
    	ret = service2.addAuthProvider(p);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	
    	Map <String, Object> p1 = new HashMap <String, Object> ();
    	//p1.put(Constants.PROVIDERAPP, "uuapp");
    	p1.put(Constants.PROVIDERNAME, "3003");
    	p1.put(Constants.PROVIDERTYPE, "ldap");
    	p1.put(Constants.PROVIDERCONF, ldapConf);
    	    	
    	ret = service2.addAuthProvider(p1);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	
    	Map <String, Object> p2 = new HashMap <String, Object> ();
    	//p2.put(Constants.PROVIDERAPP, "uuapp");
    	p2.put(Constants.PROVIDERNAME, "5005");
    	p2.put(Constants.PROVIDERTYPE, "ldap");
    	p2.put(Constants.PROVIDERCONF, ldapConf);
    	
    	ret = service2.addAuthProvider(p2);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	
    	service2.refreshLdapConfigurations();

    	Map <String, Object> cfg = service2.getCurrentLognConfig();
    	//System.out.println("GOT IT=" + cfg);
    	
		@SuppressWarnings("unchecked")
		//List <Map <String, String>> lm = (List <Map <String, String>>) cfg.get("LDAP-List");
		Map <String, Object> lm = (Map <String, Object>) cfg.get("LDAP-List");
		List <Map <String, String>> ll = (List <Map <String, String>>) lm.get("myapp");
		int i=0;
		for (Map <String, String> mm : ll)
		{
			//System.out.println(mm.get(Constants.PROVIDERAPP));
			//System.out.println(mm.get(Constants.PROVIDERCONF));
			assertEquals("myapp", mm.get(Constants.PROVIDERAPP));
			if (i==0)
			{
				assertEquals("101-dd", mm.get(Constants.PROVIDERNAME));
			}
			else if (i==1)
			{
				assertEquals("3003", mm.get(Constants.PROVIDERNAME));
			}
			else
			{
				assertEquals("5005", mm.get(Constants.PROVIDERNAME));
			}
			assertEquals("ldap", mm.get(Constants.PROVIDERTYPE));
			i++;
		}

		
		ret = service3.addAuthProvider(p1);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
		
    	service3.refreshLdapConfigurations();

    	Map <String, Object> cfg3 = service3.getCurrentLognConfig();
    	//System.out.println("GOT IT=" + cfg3);
    	Map <String, Object> lm3 = (Map <String, Object>) cfg3.get("LDAP-List");
    	
    	ll = (List <Map <String, String>>) lm3.get("telemetry");
		
		//System.out.println("telemetry...");
		for (Map <String, String> mm : ll)
		{
			//System.out.println(mm.get(Constants.PROVIDERAPP) + ":" + mm.get(Constants.PROVIDERNAME));
			//System.out.println(mm.get(Constants.PROVIDERCONF));
			assertEquals("telemetry", mm.get(Constants.PROVIDERAPP));
			//assertEquals("3003", mm.get(Constants.PROVIDERNAME));
			assertEquals("ldap", mm.get(Constants.PROVIDERTYPE));
			i++;
		}
		
		assertFalse(login(service1, "sarmah", "1234"));
		assertTrue(login(service2, "sarmah", "1234"));
		assertTrue(login(service2, "dilip", "dilip123"));
		assertFalse(login(service2, "dilip", "dilip"));
		
		assertTrue(login(service3, "sarmah", "1234"));
		
		//make sure one can login through MASEC if ldap fails...
		assertTrue(addUsers(service2));
        assertTrue(login(service2, "u9", "jkjkk"));                
				
        
        
        
    	ret = service2.deleteAuthProvider(p);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	ret = service2.deleteAuthProvider(p1);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	ret = service2.deleteAuthProvider(p2);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	
    	ret = service3.deleteAuthProvider(p1);
    	assertTrue((Boolean) ret.get(Constants.STATUS));

        assertTrue(login(service2, "u9", "jkjkk"));  
        assertTrue( deleteUsers(service2) );

        /**************************/
        
        Map <String, String> lC = new HashMap <String, String> ();
        lC.put("debug", "false");
        lC.put("useSSL", "false");
        lC.put("userProvider", "ldap://localhost:389/ou=People,dc=maxcrc,dc=com");
        lC.put("userFilter", "(&(uid={USERNAME})(objectClass=inetOrgPerson))");
        lC.put("authzIdentity", "{USERNAME}");
        
        //System.out.println(lC);
        
        Map <String, Object> pp = new HashMap <String, Object> ();
    	pp.put(Constants.PROVIDERNAME, "mymer");
    	pp.put(Constants.PROVIDERTYPE, "ldap");
    	pp.put(Constants.PROVIDERCONF, lC);
    	
    	ret = service2.addAuthProvider(pp);
    	//System.out.println(ret.get(Constants.ERRORMSG));
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	
    	service2.refreshLdapConfigurations();

    	cfg = service2.getCurrentLognConfig();
    	//System.out.println("GOT MAP=" + cfg);
    	
		@SuppressWarnings("unchecked")
		//List <Map <String, String>> lm = (List <Map <String, String>>) cfg.get("LDAP-List");
		Map <String, Object> lmm = (Map <String, Object>) cfg.get("LDAP-List");
		List <Map <String, String>> lll = (List <Map <String, String>>) lmm.get("myapp");

		for (Map <String, String> mm : lll)
		{
			//System.out.println(mm.get(Constants.PROVIDERAPP));
			//System.out.println(mm.get(Constants.PROVIDERCONF));
			assertEquals("myapp", mm.get(Constants.PROVIDERAPP));
			if (i==0)
			{
				assertEquals("mymer", mm.get(Constants.PROVIDERNAME));
			}
			assertEquals("ldap", mm.get(Constants.PROVIDERTYPE));
			i++;
		}
		assertTrue(login(service2, "dilip", "dilip123"));
		ret = service2.deleteAuthProvider(pp);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
        
    }    
}
