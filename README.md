# masec - Identity Management
App centric Java component to manage users and authentications in the cloud. Suitable for SaaS applications.

# Technology used
- Java JAAS
- Spring ORM

# What you can do?
You can embed this code in your Java applications to manage and authenticate users, and update password. You can also run this as separate component in a web container and make it a central user management and authentication service - this is desirable if you plan to manage identity for multiple applications. You can share users for all your applications or separate them as per application. Users data including hashed password and salts are stored in the database.

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
By default, data are stored in an embedded in-memory HSQLDB database. So it runs as it is without needing to change anything. For production deployment, you should update the spring orm configuration file to connect to a standalone database of your choice. Schema for HSQL and PostgreSql are provided. 

# Future
1. Add REST API
2. SSO support
3. Add policy engine
4. Create cloud connector to connect on-premise active directory server.