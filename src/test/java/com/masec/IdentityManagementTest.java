package com.masec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.masec.core.service.SecurityServiceFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class IdentityManagementTest extends TestCase
{
    public IdentityManagementTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( IdentityManagementTest.class );
    }

    protected SecurityService service, service1, service2, service3;
    private static String adminUser = "admin101";
    private static String adminPw = "admin101Pw";
    
    private static Map <String, Object> admin = new HashMap<String, Object>();
    private static String sessionId;
    private static String sessionId1;
    private static String sessionId2;
    private static String sessionId3;
    
    protected void setUp()
    {
    	admin.put(Constants.USERNAME, adminUser);
    	admin.put(Constants.PASSWORD, adminPw);
    	
    	service = SecurityServiceFactory.getSecurityService();
    	sessionId = IdentityManagement.setApplicationTechnicalUser(service, adminUser, adminPw);
        service1 = SecurityServiceFactory.getSecurityService();
        sessionId1 = IdentityManagement.setApplicationTechnicalUser(service1, adminUser, adminPw);
        service2 = SecurityServiceFactory.getSecurityService("myapp");
        sessionId2 = IdentityManagement.setApplicationTechnicalUser(service2, adminUser, adminPw);
        service3 = SecurityServiceFactory.getSecurityService("telemetry");
        sessionId3 = IdentityManagement.setApplicationTechnicalUser(service3, adminUser, adminPw);
    }
    
    protected void tearDown()
    {
    	if (null != service)
    	{
    		IdentityManagement.shutdown(service, null, null, sessionId);
    	}
    	if (null != service1)
    	{
    		IdentityManagement.shutdown(service1, null, null, sessionId1);
    	}
        if (null != service2)
    	{
        	IdentityManagement.shutdown(service2, null, null, sessionId2);
    	}
        if (null != service3)
    	{
        	IdentityManagement.shutdown(service3, null, null, sessionId3);
    	}
    }
    
    public boolean addUsers(SecurityService service)
    {
    	//System.out.println("addUsers(...)");
    	Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, "u9");
        user.put(Constants.PASSWORD, "jkjkk");
        
        Map <String, Object> ret = service.addUser(admin, user);
        return (Boolean) ret.get(Constants.STATUS);
    	
    }
    
    public boolean deleteUsers(SecurityService service)
    {
    	//System.out.println("deleteUsers(...)");
    	Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, "u9");
        
        Map <String, Object> ret = service.deleteUser(admin, user);
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
        
        Map <String, Object> sessid = new HashMap <String, Object> ();
        sessid.put(Constants.LOGINSESSIONID, sessionId);
        
        Map <String, Object> ret = service.addUser(sessid, user);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        assertTrue(login(service, "test1", "jkjkk"));
        
        ret = service.addUser(admin, user); 
        assertFalse((Boolean) ret.get(Constants.STATUS)); //Same user can not added twice
        
        user.put(Constants.FIRSTNAME, "jkjkk");
        ret = service.updateUser(user);
        assertTrue((Boolean) ret.get(Constants.STATUS)); 
        
        user = new HashMap <String, Object> ();
        user.put(Constants.LOGINSESSIONID, sessionId);
        
        user.put(Constants.USERNAME, "test1");
        user.put(Constants.FIRSTNAME, "tom");
        ret = service.updateUser(user);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        
        user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, "test1");
        user.put(Constants.PASSWORD, "indigo");
        ret = service.updatePassword(user);
        assertFalse((Boolean) ret.get(Constants.STATUS));
        
        user.put(Constants.USERNAME, "test1");
        user.put(Constants.NEWPASSWORD, "indigo");
        ret = service.updatePassword(user);
        assertFalse((Boolean) ret.get(Constants.STATUS));
        
        user.put(Constants.USERNAME, "test1");
        user.put(Constants.PASSWORD, "jkjkk");
        user.put(Constants.NEWPASSWORD, "indigo");
        ret = service.updatePassword(user);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        assertTrue(login(service, "test1", "indigo"));
        
        sessid.put(Constants.OFFSET, 0);
        sessid.put(Constants.LIMIT, 10);
        
        List <Map <String, Object>> rows = service.listUser(sessid);
        assertTrue(rows.size() > 0);
        service.deleteUser(sessid, user);
        
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
    	p.put(Constants.LOGINSESSIONID, sessionId2);
    	
    	ret = service2.addAuthProvider(p);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	
    	Map <String, Object> p1 = new HashMap <String, Object> ();
    	//p1.put(Constants.PROVIDERAPP, "uuapp");
    	p1.put(Constants.PROVIDERNAME, "3003");
    	p1.put(Constants.PROVIDERTYPE, "ldap");
    	p1.put(Constants.PROVIDERCONF, ldapConf);
    	p1.put(Constants.LOGINSESSIONID, sessionId2);
    	
    	ret = service2.addAuthProvider(p1);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	
    	Map <String, Object> p2 = new HashMap <String, Object> ();
    	//p2.put(Constants.PROVIDERAPP, "uuapp");
    	p2.put(Constants.PROVIDERNAME, "5005");
    	p2.put(Constants.PROVIDERTYPE, "ldap");
    	p2.put(Constants.PROVIDERCONF, ldapConf);
    	p2.put(Constants.LOGINSESSIONID, sessionId2);
    	
    	ret = service2.addAuthProvider(p2);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	
    	service2.refreshLdapConfigurations(p2);

    	Map <String, Object> cfg = service2.getCurrentLognConfig(p2);
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

		p1.put(Constants.LOGINSESSIONID, sessionId3);
		ret = service3.addAuthProvider(p1);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
		
    	
    	service3.refreshLdapConfigurations(p1);

    	Map <String, Object> cfg3 = service3.getCurrentLognConfig(p1);
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
				
        
        
        p.put(Constants.LOGINSESSIONID, sessionId2);
    	ret = service2.deleteAuthProvider(p);    	
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	
    	p1.put(Constants.LOGINSESSIONID, sessionId2);
    	ret = service2.deleteAuthProvider(p1);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	
    	p2.put(Constants.LOGINSESSIONID, sessionId2);
    	ret = service2.deleteAuthProvider(p2);
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	
    	p1.put(Constants.LOGINSESSIONID, sessionId3);
    	ret = service3.deleteAuthProvider(p1);
    	assertTrue((Boolean) ret.get(Constants.STATUS));

        assertTrue(login(service2, "u9", "jkjkk"));  
        assertTrue( deleteUsers(service2) ); //TBD

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
    	
    	pp.put(Constants.LOGINSESSIONID, sessionId2);
    	
    	ret = service2.addAuthProvider(pp);
    	//System.out.println(ret.get(Constants.ERRORMSG));
    	assertTrue((Boolean) ret.get(Constants.STATUS));
    	
    	service2.refreshLdapConfigurations(pp);

    	cfg = service2.getCurrentLognConfig(pp);
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
    	
    	// Testing group
    	Map <String, Object> g = new HashMap <String, Object> ();
        g.put(Constants.G_NAME, "g1");
        g.put(Constants.G_DESCRIPTION, "test group");
        
        g.put(Constants.LOGINSESSIONID, sessionId2);
        
        ret = service2.addGroup(g);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        
        Map <String, Object> gName = new HashMap <String, Object> ();
        gName.put(Constants.LOGINSESSIONID, sessionId2);
        gName.put(Constants.G_NAME, "g1");
        
        Map <String, Object> g1 = service2.findGroupByName(gName);
        assertEquals("test group", (String)g1.get(Constants.G_DESCRIPTION));                
        
        gName.put(Constants.LOGINSESSIONID, sessionId2);
        gName.put(Constants.OFFSET, 0);
        gName.put(Constants.LIMIT, 5);
        
        List<Map <String, Object>> g2 = service2.findAllUserGroups(gName);
        assertEquals(g2.size(), 0);
        
        g.put(Constants.G_DESCRIPTION, "my test group");
        ret = service2.updateGroup(g);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        
        Map <String, Object> g3 = service2.findGroupByName(gName);
        assertEquals("my test group", (String)g3.get(Constants.G_DESCRIPTION));
        
        ret = service2.deleteGroup(g);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        
        // Testing users in groups
        List<Map <String, Object>> ug = service2.findAllUserGroups(gName);
        assertEquals(ug.size(), 0);
        
        // negative test - add user to a non existing group
        Map <String, Object> gu = new HashMap <String, Object> ();
        gu.put(Constants.G_NAME, "g1");
        gu.put(Constants.USERNAME, "user1");
        gu.put(Constants.LOGINSESSIONID, sessionId2);
        
        ret = service2.addUerToGroup(gu);
        assertFalse((Boolean) ret.get(Constants.STATUS));
        
        // positive  - add user to group
        g = new HashMap <String, Object> ();
        g.put(Constants.G_NAME, "g1");
        g.put(Constants.G_DESCRIPTION, "test group");
        g.put(Constants.LOGINSESSIONID, sessionId2);
        
        ret = service2.addGroup(g);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        
        ret = service2.addUerToGroup(gu);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        
        // negative - if add same user twice
        ret = service2.addUerToGroup(gu);
        assertFalse((Boolean) ret.get(Constants.STATUS));
        
        // add one more user -
        gu.put(Constants.USERNAME, "user2");
        ret = service2.addUerToGroup(gu);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        
        // create one more group... and add user2
        g.put(Constants.G_NAME, "g2");
        ret = service2.addGroup(g);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        gu.put(Constants.G_NAME, "g2");
        ret = service2.addUerToGroup(gu);
        
        // get all the groups...g1 and g2
        ug = service2.findAllUserGroups(gName);
        assertEquals(ug.size(), 3);
        
        // get all groups for user1 i.e., g1
        Map<String, Object> user1 = new HashMap<String, Object>();
        user1.put(Constants.USERNAME, "user1");
        user1.put(Constants.LOGINSESSIONID, sessionId2);
        List<Map <String, Object>> lg = service2.findGroupsByUser(user1);
        assertEquals(lg.size(), 1);
        assertEquals((String)lg.get(0).get(Constants.G_NAME), "g1");
        
        // get all groups for user2 i.e., g1 and g2
        user1.put(Constants.USERNAME, "user2");
        lg = service2.findGroupsByUser(user1);
        assertEquals(lg.size(), 2);
        assertEquals((String)lg.get(0).get(Constants.G_NAME), "g1");
        assertEquals((String)lg.get(1).get(Constants.G_NAME), "g2");
        
        List <String> gr1 = new ArrayList <String> ();
        gr1.add("g1");

        user1.put(Constants.G_NAME, gr1);
        ug = service2.partOfAnyGroup(user1);
        assertEquals(ug.size(), 1);
        assertEquals(ug.get(0).get(Constants.G_NAME), "g1");
        
        // delete user from group - user2 from g2
        ret = service2.deleteUserFromGroup(gu);
        assertTrue((Boolean) ret.get(Constants.STATUS));
        
        Map <String, Object> group = new HashMap <String, Object> ();
        group.put(Constants.G_NAME, "g1");
        group.put(Constants.LOGINSESSIONID, sessionId2);
        service2.deleteGroup(group);
        group.put(Constants.G_NAME, "g2");
        service2.deleteGroup(group);
        
        ug = service2.findAllUserGroups(gName);
        assertEquals(ug.size(), 0);
        
        ug = service2.findAllUserGroups(gName);
        assertEquals(ug.size(), 0);
    }    
}
