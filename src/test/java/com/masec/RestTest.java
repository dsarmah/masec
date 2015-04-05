package com.masec;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class RestTest extends JerseyTest 
{
	
	private final static String APPCTX = "rest-test"; 

	@Override
	protected Application configure() 
	{
		 enable(TestProperties.LOG_TRAFFIC);
	     enable(TestProperties.DUMP_ENTITY);
		 ClientConfig clientConfig = new DefaultClientConfig();
		 clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		 Client client = Client.create(clientConfig);
		 return new ResourceConfig(RestSecurityService.class);
	}
	
	 
    @Test
    public void testRestApis()  
    {
    	
    	Map <String, Object> ret = this.login(null, "90user1", "po2312");
    	assertFalse((Boolean)ret.get(Constants.STATUS));
    	
    	ret = this.addOrUseTechUser("rest321", "po2312");
    	String sessId = (String)ret.get(Constants.LOGINSESSIONID);
    	assertFalse (sessId == null);
    	
    	//add user
    	ret = this.addUser(sessId, "90user1", "po2312");
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    	
    	//login
    	ret = this.login(null, "90user1", "po2312");
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    	
    	//update the password
    	ret = this.updatePassword((String)ret.get(Constants.LOGINSESSIONID), "90user1", "QQ2312");
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    	    	
    	//negative login test...
    	ret = this.login(null, "90user1", "po2312");
    	assertFalse((Boolean)ret.get(Constants.STATUS));
    	
    	//positive login test...
    	ret = this.login(null, "90user1", "QQ2312");
    	assertTrue((Boolean)ret.get(Constants.STATUS));

    	String sId = (String)ret.get(Constants.LOGINSESSIONID);
    	
    	//list of users
    	List <Map <String, Object>> us = this.getUsers(sId, 0, 10);
    	assertTrue(us.size()>=1);
    	Map <String, Object> iu = us.get(0);
    	assertTrue(iu.get(Constants.PASSWORD)==null);
    	
    	//add group
    	ret = this.addGroup(sId, "g1", "g1 desc");
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    	    	
    	//update group
    	ret = this.updateGroup(sId, "g1", "g1 desc added");
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    	
    	//add user in the group
    	ret = this.addUserToGroup(sId, "g1", "90user1");
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    	
    	//get all groups
    	List <Map <String, Object>> rl = this.getAllGroups(sId, 0, 10);
    	assertTrue(rl.size()==1);
    	
    	//get all groups for this user
    	rl = this.getGroupsByUser(sId, "90user1");
    	assertTrue( rl.size()==1 );
    	
    	//is this user part of any of this groups? return only the groups which it is part of from the given given group list
    	
    	
    	//remove a user from the group
    	ret = this.removeUserToGroup(sId, "g1", "90user1");
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    	
    	//remove the group
    	ret = this.deleteGroup(sId, "g1");
    	assertTrue((Boolean)ret.get(Constants.STATUS));  	
    	    	
    	//add LDAP configuration
        String ldapConf = "{" + 
    			"\"" + "useSSL" + "\":" + 
    			"\"" + "false" + "\"," +
    			"\"" + Constants.LDAP_USERPROVIDER + "\":" + 
    			"\"" + "ldap://localhost:389/ou=People,dc=maxcrc,dc=com" + "\"," +
				"\"" + Constants.LDAP_USERFILTER + "\":" + 				
				"\"" + "(&(uid={USERNAME})(objectClass=inetOrgPerson))" + "\"," +
				"\"" + Constants.LDAP_USERIDENTITY + "\":" +  
				"\"" + "{USERNAME}" + "\"" + 
				"}";
    	
    	Map <String, Object> p1 = new HashMap <String, Object> ();
    	p1.put(Constants.PROVIDERNAME, "3003");
    	p1.put(Constants.PROVIDERTYPE, "ldap");
    	p1.put(Constants.PROVIDERCONF, ldapConf);
    	p1.put(Constants.LOGINSESSIONID, sId);
    	p1.put(Constants.APPLICATION, APPCTX);
    	
    	Entity< Map<String, Object>> e = Entity.entity(p1, MediaType.APPLICATION_JSON_TYPE);
    	
    	Response response =  target("masec/addauthprovider").request().post(e);
    	ret =  this.getMapResponse(response);
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    	
    	//refresh LDAP configuration
    	response =  target("masec/refreshauthprovider").request().post(e);
    	ret =  this.getMapResponse(response);
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    
    	//list of LDAP configuration   	
    	List <Map <String, Object>> ap = this.getAuthProviders(sId, 0, 10);
    	assertTrue(ap.size()==1);
    	
    	//login against LDAP
    	ret = this.login(sId, "dilip", "dilip123");
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    	
    	//update LDAP configuration   	
    	response =  target("masec/updateauthprovider").request().post(e);
    	ret =  this.getMapResponse(response);
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    	
    	//remove LDAP configuration    	
    	response =  target("masec/deleteauthprovider").request().post(e);
    	ret =  this.getMapResponse(response);
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    	
    	//delete user
    	ret = this.deleteUser(sessId, "90user1", "QQ2312");
    	assertTrue((Boolean)ret.get(Constants.STATUS));
    
    	//shutdown embedded HSQLDB for test
    	this.shutdownTestDB();
    	
    }
    
    private Map <String, Object> addOrUseTechUser(String u, String p)
    {
    	Entity< Map<String, String>> e = this.getUserEntity(null, u, p);
    	Response response =  target("masec/addorusetechuser").request().post(e);    	
    	return this.getMapResponse(response);
    }
    
    private Map <String, Object> login (String s, String u, String p)
    {
    	Entity< Map<String, String>> e = this.getUserEntity(s, u, p);
    	Response response =  target("masec/login").request().post(e);    	
    	return this.getMapResponse(response);
    }
    
    private Map <String, Object> addUser (String s, String u, String p)
    {
    	Entity< Map<String, String>> e = this.getUserEntity(s, u, p);
    	Response response =  target("masec/adduser").request().post(e);
    	return this.getMapResponse(response);
    }
    
    private Map <String, Object> updatePassword (String s, String u, String newP)
    {
    	Map <String, String> map = new HashMap <String, String> ();
    	if (null != s)
    	{
    		map.put(Constants.LOGINSESSIONID, s);
    	}
    	map.put(Constants.USERNAME, u);
    	map.put(Constants.NEWPASSWORD, newP);
    	map.put(Constants.APPLICATION, APPCTX);
    	Entity< Map<String, String>> e = Entity.entity(map, MediaType.APPLICATION_JSON_TYPE);
    	
    	Response response =  target("masec/updatepassword").request().post(e);
    	return this.getMapResponse(response);
    }
    
    private Map <String, Object> deleteUser (String s, String u, String p)
    {
    	Entity< Map<String, String>> e = this.getUserEntity(s, u, p);
    	Response response =  target("masec/deleteuser").request().post(e);
    	return this.getMapResponse(response);
    }
    
    private List <Map <String, Object>> getUsers(String s, int offset, int limit)
    {
    	Map <String, Object> r = new HashMap <String, Object> ();
        r.put(Constants.LOGINSESSIONID, s);       
        r.put(Constants.OFFSET, offset);
        r.put(Constants.LIMIT, limit);
        r.put(Constants.APPLICATION, APPCTX);
    	Entity< Map<String, Object>> e = Entity.entity(r, MediaType.APPLICATION_JSON_TYPE);
    	
    	Response response =  target("masec/listusers").request().post(e);
    	return this.getListResponse(response);
    }
    
    private Map <String, Object> addGroup(String s, String n, String d)
    {
    	Map <String, String> g = new HashMap <String, String> ();
        g.put(Constants.G_NAME, n);
        g.put(Constants.G_DESCRIPTION, d);        
        g.put(Constants.LOGINSESSIONID, s);
        g.put(Constants.APPLICATION, APPCTX);
    	Entity< Map<String, String>> e = Entity.entity(g, MediaType.APPLICATION_JSON_TYPE);
    	
    	Response response =  target("masec/addgroup").request().post(e);
    	return this.getMapResponse(response);
    }
    
    private Map <String, Object> updateGroup(String s, String n, String d)
    {
    	Map <String, String> g = new HashMap <String, String> ();
        g.put(Constants.G_NAME, n);
        g.put(Constants.G_DESCRIPTION, d);        
        g.put(Constants.LOGINSESSIONID, s);
        g.put(Constants.APPLICATION, APPCTX);
    	Entity< Map<String, String>> e = Entity.entity(g, MediaType.APPLICATION_JSON_TYPE);
    	
    	Response response =  target("masec/updategroup").request().post(e);
    	return this.getMapResponse(response);
    }
    
    private Map <String, Object> deleteGroup(String s, String n)
    {
    	Map <String, String> g = new HashMap <String, String> ();
        g.put(Constants.G_NAME, n);
        g.put(Constants.LOGINSESSIONID, s);
        g.put(Constants.APPLICATION, APPCTX);
    	Entity< Map<String, String>> e = Entity.entity(g, MediaType.APPLICATION_JSON_TYPE);
    	
    	Response response =  target("masec/deletegroup").request().post(e);
    	return this.getMapResponse(response);
    }
    
    private Map <String, Object> addUserToGroup(String sessionId, String gName, String user)
    {
    	Map <String, String> gu = new HashMap <String, String> ();
        gu.put(Constants.G_NAME, gName);
        gu.put(Constants.USERNAME, user);
        gu.put(Constants.LOGINSESSIONID, sessionId);
        gu.put(Constants.APPLICATION, APPCTX);
    	Entity< Map<String, String>> e = Entity.entity(gu, MediaType.APPLICATION_JSON_TYPE);
    	
    	Response response =  target("masec/addusertogroup").request().post(e);
    	return this.getMapResponse(response);
    }
    
    private Map <String, Object> removeUserToGroup(String sessionId, String gName, String user)
    {
    	Map <String, String> gu = new HashMap <String, String> ();
        gu.put(Constants.G_NAME, gName);
        gu.put(Constants.USERNAME, user);
        gu.put(Constants.LOGINSESSIONID, sessionId);
        gu.put(Constants.APPLICATION, APPCTX);
    	Entity< Map<String, String>> e = Entity.entity(gu, MediaType.APPLICATION_JSON_TYPE);
    	
    	Response response =  target("masec/removeuserfromgroup").request().post(e);
    	return this.getMapResponse(response);
    }
    
    private List <Map <String, Object>> getAllGroups(String s, int offset, int limit)
    {
    	Map <String, Object> gName = new HashMap <String, Object> ();
        gName.put(Constants.LOGINSESSIONID, s);       
        gName.put(Constants.OFFSET, offset);
        gName.put(Constants.LIMIT, limit);
        gName.put(Constants.APPLICATION, APPCTX);
    	Entity< Map<String, Object>> e = Entity.entity(gName, MediaType.APPLICATION_JSON_TYPE);
    	
    	Response response =  target("masec/findallusergroups").request().post(e);
    	return this.getListResponse(response);
    }
    
    private List <Map <String, Object>> getGroupsByUser(String s, String u)
    {
    	Map <String, Object> gName = new HashMap <String, Object> ();
        gName.put(Constants.LOGINSESSIONID, s);       
        gName.put(Constants.USERNAME, u);
        gName.put(Constants.APPLICATION, APPCTX);
    	Entity< Map<String, Object>> e = Entity.entity(gName, MediaType.APPLICATION_JSON_TYPE);
    	
    	Response response =  target("masec/findgroupsbyuser").request().post(e);
    	return this.getListResponse(response);
    }
    
    
    private Entity< Map<String, String>> getUserEntity(String s, String u, String p)
    {
    	Map <String, String> map = new HashMap <String, String> ();
    	if (null != s)
    	{
    		map.put(Constants.LOGINSESSIONID, s);
    	}
    	map.put(Constants.USERNAME, u);
    	map.put(Constants.PASSWORD, p);
    	map.put(Constants.APPLICATION, APPCTX);
    	Entity< Map<String, String>> e = Entity.entity(map, MediaType.APPLICATION_JSON_TYPE);
    	return e;
    }        
    
    private List <Map <String, Object>> getAuthProviders(String s, int offset, int limit)
    {
    	Map <String, Object> gName = new HashMap <String, Object> ();
        gName.put(Constants.LOGINSESSIONID, s);       
        gName.put(Constants.OFFSET, offset);
        gName.put(Constants.LIMIT, limit);
        gName.put(Constants.APPLICATION, APPCTX);
    	Entity< Map<String, Object>> e = Entity.entity(gName, MediaType.APPLICATION_JSON_TYPE);
    	
    	Response response =  target("masec/listauthproviders").request().post(e);
    	return this.getListResponse(response);
    }
    
    @SuppressWarnings("unchecked")
	private Map <String, Object> getMapResponse(Response response)
    {
    	String str = response.readEntity(String.class);
    	
    	Map<String, Object> ret = new HashMap<String, Object> ();
		try 
		{
			ret = new ObjectMapper().readValue(str, Map.class);
		} 
		catch (Exception e1) 
		{
			e1.printStackTrace();
		}
		return ret;
    }
    
    @SuppressWarnings("unchecked")
	private List<Map<String, Object>> getListResponse(Response response)
    {
    	String str = response.readEntity(String.class);
    	
    	List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		try 
		{
			ret = new ObjectMapper().readValue(str, List.class);
		} 
		catch (Exception e1) 
		{
			e1.printStackTrace();
		}
		return ret;
    }
    
    
    private void shutdownTestDB()
    {
	    Map<String, Object> obj = new HashMap<String, Object> ();
		Entity<Map<String, Object>> e = Entity.entity(obj, MediaType.APPLICATION_JSON_TYPE);
		Response response =  target("masec/shutdown").request().post(e);
		System.out.println(response.getStatus());
    }
}
