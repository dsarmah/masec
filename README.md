# masec - Identity Management
A simple App centric Java component to manage users and authentications in the cloud. Suitable for SaaS applications.

# Technology used
- Java JAAS
- Spring ORM

# What you can do?
You can manage and authenticate users for an application (or tenant) or group of applications (tenants). Applications can share users. You can partition users by application. You can embed this code in your Java applications easily. You can also run this as a separate component in a web container and make it a central user management and authentication service - this is desirable if you plan to manage identity for multiple applications. Users data including hashed passwords, salts and configurations are stored in the database.

- SaaS support: can configure login by tenant
- It has built-in user directory service
- It provides Java APIs to manage users for built-in user directory
- It provides authentication API using JaaS
- Multiple LDAP/active directory services can be configured per tenant/application
- Dynamic configuration - configurations loading at runtime 

# How to use it?

1. First you need to acquire a security service:

		// Default security service 
		SecurityService service = SecurityServiceFactory.getSecurityService();
        
		OR
		
		// Specific to an application - in this case you are creating security service for "myapp" tenant or application
		// All the users for "myapp" application/tenant are only available to "myapp".
		SecurityService service = SecurityServiceFactory.getSecurityService("myapp"); 

2. To login, simply call:
        
        Map <String, Object> ret = service.login(user);
        success = (Boolean) ret.get(Constants.STATUS);
		
3. To add a user:

		Map <String, Object> ret = service.addUser(user);

4. To add LDAP configuration and refresh the configuration at run time:
	
		// LDAP configurations
    	Map <String, String> lC2 = new HashMap <String, String> ();
        lC2.put("debug", "false");
        lC2.put("useSSL", "false");
        lC2.put("userProvider", "ldap://localhost:389/ou=People,dc=maxcrc,dc=com");
        lC2.put("userFilter", "(&(uid={USERNAME})(objectClass=inetOrgPerson))");
        lC2.put("authzIdentity", "{USERNAME}");
         
        // LDAP map
        Map <String, Object> ldapConfig = new HashMap <String, Object> ();
        ldapConfig.put(Constants.PROVIDERNAME, "nico-provider");
        ldapConfig.put(Constants.PROVIDERTYPE, "ldap");
        ldapConfig.put(Constants.PROVIDERCONF, lC2);
		
		SecurityService serviceLd = SecurityServiceFactory.getSecurityService("nicodime"); 
		Map <String, Object> ret = serviceLd.addAuthProvider(ldapConfig);
		
		serviceLd.refreshLdapConfigurations();
		
Check SecurityService.java for all the available APIs. All available fields for User object are described in Constants.java file.

# Example
Check in the test folder: SimpleLogin.java and SaaSLogin.java

# Data Persistence	
By default, data are stored in an embedded HSQLDB database. So it runs without needing to change anything. For production deployment, you should update the spring orm configuration file to connect to a standalone database of your choice. Schema for HSQL and PostgreSql are provided. 

# Future
1. Add users group support in native directory service
2. Add REST API
3. SSO support
4. Add policy engine
5. Create cloud connector to connect on-premise active directory server.