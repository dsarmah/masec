package com.masec;

import java.util.List;
import java.util.Map;

public interface SecurityService 
{
	
	public Map<String, Object> setApplicationTechnicalUser (Map<String, Object> user);
	
	public Map<String, Object> login (Map<String, Object> user); 
	
	public void shutdown (Map<String, Object> user); 
	
	/**** All user management related APIs are only supported for local directory service (i.e., database) not against LDAP  ****/
	public Map<String, Object> addUser(Map<String, Object> userWhoAdds, Map<String, Object> newUser);
	
	public List<Map<String, Object>> listUser(Map<String, Object> request);	
	
	public Map<String, Object> deleteUser(Map<String, Object> userWhoDeletes, Map<String, Object> deleteUser);
	
	public Map<String, Object> updateUser(Map<String, Object> user);
	
	public Map<String, Object> updatePassword(Map<String, Object> user);	
	/*** End of User management APIs ****/
	
	
	/*** Start group related APIs ****/
	/**** All group related APIs are only supported for local directory service (i.e., database) not against LDAP  ****/
	public Map<String, Object> findGroupByName( Map<String, Object> request );	
    
    public Map<String, Object> addGroup( Map<String, Object> group );
    
    public Map<String, Object> updateGroup( Map<String, Object> group );
    
    public Map<String, Object> deleteGroup( Map<String, Object> group );
    
    public List<Map<String, Object>> findAllUserGroups(Map<String, Object> request );
    
    public List<Map<String, Object>> findGroupsByUser(Map<String, Object> request);
    
    public List<Map<String, Object>> partOfAnyGroup(Map<String, Object> request);
    
    public Map<String, Object> addUerToGroup( Map<String, Object> ugroup );
    
    public Map<String, Object> deleteUserFromGroup( Map<String, Object> ugroup );
    
    /**** End of Group related APIs  ****/
    
	
    /**** Begin LDAP Configurations APIs  ****/
    public List<Map<String, Object>> listAuthProviderByType(Map<String, Object> request);
	
    public List<Map<String, Object>> listAuthProviders(Map<String, Object> request);
	
	public Map<String, Object> addAuthProvider(Map<String, Object> provider);
	
	public Map<String, Object> deleteAuthProvider(Map<String, Object> provider);
	
	public Map<String, Object> updateAuthProvider(Map<String, Object> provider);
	
	public void refreshLdapConfigurations(Map<String, Object> request);

	/**** End of LDAP Configurations APIs  ****/
	
	public Map<String, Object> getCurrentLognConfig(Map<String, Object> request);
	
}
