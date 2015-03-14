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

    public boolean addUsers(SecurityService service)
    {
    	System.out.println("addUsers(...)");
    	Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, "u9");
        user.put(Constants.PASSWORD, "jkjkk");
        
        Map <String, Object> ret = service.addUser(user);
        return (Boolean) ret.get(Constants.STATUS);
    	
    }
    
    public boolean deleteUsers(SecurityService service)
    {
    	System.out.println("deleteUsers(...)");
    	Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, "u9");
        
        Map <String, Object> ret = service.deleteUser(user);
        return (Boolean) ret.get(Constants.STATUS);
    	
    }
    
    public boolean login(SecurityService service, String userid, String pw)
    {
    	System.out.println("addUsers(...)");
    	Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, userid);
        user.put(Constants.PASSWORD, pw);
        
        Map <String, Object> ret = service.login(user);
        return (Boolean) ret.get(Constants.STATUS);
    	
    }
    
    public void testBasic()
    {
        SecurityService service = SecurityServiceFactory.getSecurityService();                
        assertTrue( addUsers(service) );
        assertTrue(login(service, "u9", "jkjkk"));
        assertFalse(login(service, "u22", "jkjkk"));
        assertFalse(login(service, "u9", "0jkjkk"));
        assertTrue( deleteUsers(service) );
        assertFalse(login(service, "u9", "jkjkk"));
    }
    
    public void testAdvance()
    {
        SecurityService service1 = SecurityServiceFactory.getSecurityService();
        SecurityService service2 = SecurityServiceFactory.getSecurityService("myapp");
        assertTrue( addUsers(service1) );
        assertTrue(login(service1, "u9", "jkjkk"));
        assertFalse(login(service2, "u9", "jkjkk"));
        assertTrue( addUsers(service2) );
        assertTrue(login(service2, "u9", "jkjkk"));
        assertTrue( deleteUsers(service2) );
        assertFalse(login(service2, "u9", "jkjkk"));
        assertTrue( deleteUsers(service1) );
    }
    
    public void testMedium()
    {
        SecurityService service = SecurityServiceFactory.getSecurityService();
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
    }
}
