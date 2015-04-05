package com.masec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;


@Path("/masec")
public class RestSecurityService 
{
	private Logger log = Logger.getLogger(this.getClass());	
	
	@POST
	@Path("/shutdown")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response shutdown(Map<String, Object> obj)
	{
		log.info("shutdown...");
		IdentityManagement.shutdown(SecurityFactory.getSecCtx((String)obj.get(Constants.APPLICATION)), (String)obj.get(Constants.USERNAME), (String)obj.get(Constants.PASSWORD), (String)obj.get(Constants.LOGINSESSIONID) );
		return Response.ok("Shutdown!").build();
	}
	
	@POST
	@Path("/addorusetechuser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response  addTechUser (Map<String, Object> obj)
	{
		log.info("addTechUser...");
		String sessId = IdentityManagement.setApplicationTechnicalUser(SecurityFactory.getSecCtx((String)obj.get(Constants.APPLICATION)), (String)obj.get(Constants.USERNAME), (String)obj.get(Constants.PASSWORD));
		Map<String, Object> ret = new HashMap <String, Object> ();
		ret.put(Constants.LOGINSESSIONID, sessId);
		ret.put(Constants.STATUS, true);
		if (null == sessId)
		{
			ret.put(Constants.STATUS, false);
		}
		GenericEntity< Map<String, Object> > entity = new GenericEntity< Map<String, Object> > ( ret ) { };
		return Response.ok().entity(entity).build();		
	}
	
	
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response  login (Map<String, Object> obj)
	{		
		String sessId = IdentityManagement.login(SecurityFactory.getSecCtx((String)obj.get(Constants.APPLICATION)), (String)obj.get(Constants.USERNAME), (String)obj.get(Constants.PASSWORD));
		Map<String, Object> ret = new HashMap <String, Object> ();
		ret.put(Constants.LOGINSESSIONID, sessId);
		ret.put(Constants.STATUS, true);
		if (null == sessId)
		{
			ret.put(Constants.STATUS, false);
		}
		GenericEntity< Map<String, Object> > entity = new GenericEntity< Map<String, Object> > ( ret ) { };
		return Response.ok().entity(entity).build();		
	}

	private Response getBooleanMapResponse(boolean succ)
	{
		Map<String, Object> ret = new HashMap <String, Object> ();
		ret.put(Constants.STATUS, true);
		if (!succ)
		{
			ret.put(Constants.STATUS, false);
		}		
		GenericEntity< Map<String, Object> > entity = new GenericEntity< Map<String, Object> > ( ret ) { };
		return Response.ok().entity(entity).build();	
	}

	private Response getListMapResponse(List<Map<String, Object>> list)
	{
		GenericEntity< List <Map<String, Object>> > entity = new GenericEntity< List <Map<String, Object>> > ( list ) { };
		return Response.ok().entity(entity).build();	
	}
	
	@POST
	@Path("/adduser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response  addUser (Map<String, Object> user)
	{
		Boolean succ = IdentityManagement.addUser(SecurityFactory.getSecCtx((String)user.get(Constants.APPLICATION)), (String)user.get(Constants.LOGINSESSIONID), user);		
		return getBooleanMapResponse(succ);		
	}
	
	@POST
	@Path("/deleteuser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response  deleteUser (Map<String, Object> user)
	{
		Boolean succ = IdentityManagement.deleteUser(SecurityFactory.getSecCtx((String)user.get(Constants.APPLICATION)), (String)user.get(Constants.LOGINSESSIONID), user);
		return getBooleanMapResponse(succ);		
	}
	
	@POST
	@Path("/updateuser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response  updateUser (Map<String, Object> user)
	{
		Boolean succ = IdentityManagement.updateUser(SecurityFactory.getSecCtx((String)user.get(Constants.APPLICATION)), (String)user.get(Constants.LOGINSESSIONID), user);
		return getBooleanMapResponse(succ);		
	}
	
	@POST
	@Path("/updatepassword")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response  updatePassword (Map<String, Object> user)
	{
		Boolean succ = IdentityManagement.updatePassword(SecurityFactory.getSecCtx((String)user.get(Constants.APPLICATION)), 
				(String)user.get(Constants.LOGINSESSIONID), 
				(String)user.get(Constants.PASSWORD), 
				(String)user.get(Constants.NEWPASSWORD), 
				(String)user.get(Constants.USERNAME));
		return getBooleanMapResponse(succ);		
	}
	
	@POST
	@Path("/listusers")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listUser(Map<String, Object> user)
	{
		return getListMapResponse( IdentityManagement.listUsers(SecurityFactory.getSecCtx((String)user.get(Constants.APPLICATION)), (String)user.get(Constants.LOGINSESSIONID), (Integer)user.get(Constants.OFFSET), (Integer)user.get(Constants.LIMIT)) );		
	}
	
	@POST
	@Path("/findgroupsbyname")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findGroupByName( Map<String, Object> req )
	{
		Map<String, Object> grp = IdentityManagement.findGroupByName(SecurityFactory.getSecCtx((String)req.get(Constants.APPLICATION)), (String)req.get(Constants.LOGINSESSIONID), (String)req.get(Constants.G_NAME));
		GenericEntity< Map<String, Object> > entity = new GenericEntity< Map<String, Object> > ( grp ) { };
		return Response.ok().entity(entity).build();
	}
	
	@POST
	@Path("/addgroup")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response addGroup( Map<String, Object> group )
    {
    	Boolean succ = IdentityManagement.addGroup(SecurityFactory.getSecCtx((String)group.get(Constants.APPLICATION)), (String)group.get(Constants.LOGINSESSIONID), group);
    	return getBooleanMapResponse(succ);
    }
    
	@POST
	@Path("/updategroup")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response updateGroup( Map<String, Object> group )
    {
    	boolean succ = IdentityManagement.updateGroup(SecurityFactory.getSecCtx((String)group.get(Constants.APPLICATION)), (String)group.get(Constants.LOGINSESSIONID), group);
    	return getBooleanMapResponse(succ);    	
    }
    
	@POST
	@Path("/deletegroup")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response deleteGroup( Map<String, Object> req )
    {
    	boolean succ = IdentityManagement.deleteGroup(SecurityFactory.getSecCtx((String)req.get(Constants.APPLICATION)), (String)req.get(Constants.LOGINSESSIONID), (String)req.get(Constants.G_NAME));
    	return getBooleanMapResponse(succ);
    }
    
	@POST
	@Path("/findallusergroups")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response findAllUserGroups( Map<String, Object> g )
    {
		List<Map<String, Object>> list = IdentityManagement.findAllUserGroups(SecurityFactory.getSecCtx((String)g.get(Constants.APPLICATION)), (String)g.get(Constants.LOGINSESSIONID), (Integer)g.get(Constants.OFFSET), (Integer)g.get(Constants.LIMIT));
    	return getListMapResponse(list);

    }
    
	@POST
	@Path("/findgroupsbyuser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response findGroupsByUser( Map<String, Object> req )
    {
        List<Map<String, Object>> list = IdentityManagement.findGroupsByUser(SecurityFactory.getSecCtx((String)req.get(Constants.APPLICATION)), (String)req.get(Constants.LOGINSESSIONID), (String)req.get(Constants.USERNAME));
    	return getListMapResponse(list);
    }
    
	@SuppressWarnings("unchecked")
	@POST
	@Path("/partofanygroup")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response partOfAnyGroup( Map<String, Object> req )
    {
        List<Map<String, Object>> list = IdentityManagement.partOfAnyGroup(SecurityFactory.getSecCtx((String)req.get(Constants.APPLICATION)), (String)req.get(Constants.LOGINSESSIONID), (String)req.get(Constants.USERNAME), (List<String>)req.get(Constants.G_NAME));
    	return getListMapResponse(list);
    }
    
	@POST
	@Path("/addusertogroup")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response addUerToGroup( Map<String, Object> req )
    {    	
        Boolean succ = IdentityManagement.addUerToGroup(SecurityFactory.getSecCtx((String)req.get(Constants.APPLICATION)), (String)req.get(Constants.LOGINSESSIONID), (String)req.get(Constants.USERNAME), (String)req.get(Constants.G_NAME));
        return getBooleanMapResponse(succ);
    }

	@POST
	@Path("/removeuserfromgroup")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUserFromGroup( Map<String, Object> req )
    {
		Boolean succ = IdentityManagement.deleteUserFromGroup(SecurityFactory.getSecCtx((String)req.get(Constants.APPLICATION)), (String)req.get(Constants.LOGINSESSIONID), (String)req.get(Constants.USERNAME), (String)req.get(Constants.G_NAME));
        return getBooleanMapResponse(succ);
    }
    
    //////////////////////////
    
	@POST
	@Path("/listauthproviderbytype")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response listAuthProviderByType( Map<String, Object> req )
    {
		List<Map<String, Object>> list = IdentityManagement.listAuthProviderByType(SecurityFactory.getSecCtx((String)req.get(Constants.APPLICATION)), (String)req.get(Constants.LOGINSESSIONID), (String)req.get(Constants.PROVIDERTYPE));
    	return getListMapResponse(list);
    }
	
	@POST
	@Path("/listauthproviders")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response listAuthProviders( Map<String, Object> g )
    {
		List<Map<String, Object>> list = IdentityManagement.listAuthProviders(SecurityFactory.getSecCtx((String)g.get(Constants.APPLICATION)), (String)g.get(Constants.LOGINSESSIONID), (Integer)g.get(Constants.OFFSET), (Integer)g.get(Constants.LIMIT));
    	return getListMapResponse(list);
    }
	
	@POST
	@Path("/addauthprovider")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addAuthProvider( Map<String, Object> provider )
	{
		Boolean succ = IdentityManagement.addAuthProvider(SecurityFactory.getSecCtx((String)provider.get(Constants.APPLICATION)), (String)provider.get(Constants.LOGINSESSIONID), provider);
        return getBooleanMapResponse(succ);
	}
	
	@POST
	@Path("/deleteauthprovider")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteAuthProvider( Map<String, Object> req )
	{
		Boolean succ = IdentityManagement.deleteAuthProvider(SecurityFactory.getSecCtx((String)req.get(Constants.APPLICATION)), (String)req.get(Constants.LOGINSESSIONID), (String)req.get(Constants.PROVIDERNAME));
        return getBooleanMapResponse(succ);
	}
	
	@POST
	@Path("/updateauthprovider")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateAuthProvider( Map<String, Object> provider )
	{
		Boolean succ = IdentityManagement.updateAuthProvider(SecurityFactory.getSecCtx((String)provider.get(Constants.APPLICATION)), (String)provider.get(Constants.LOGINSESSIONID), provider);
        return getBooleanMapResponse(succ);
	}
	
	@POST
	@Path("/refreshauthprovider")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response refreshAuthProvider( Map<String, Object> provider )
	{
		Boolean succ = IdentityManagement.refreshAuthProvider(SecurityFactory.getSecCtx((String)provider.get(Constants.APPLICATION)), (String)provider.get(Constants.LOGINSESSIONID), provider);
        return getBooleanMapResponse(succ);
	}
}
