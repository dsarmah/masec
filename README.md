# masec - Identity Management for Cloud
A simple App centric Java component to manage users and authentications in the cloud. Suitable for SaaS applications. You can use this to manage users, groups and login. Users and groups objects are stored in database. You can also choose to configure LDAP and login against LDAP.

# Technology used
- Java JAAS
- Spring ORM
- REST APIs

# What you can do?
You can manage and authenticate users for an application or group of applications. Applications can share users. You can partition users by application. You can embed this code in your Java applications easily. You can also run this as a separate component in a web container and make it a central user management and authentication service - this is desirable if you plan to manage identity for multiple applications. Users data including hashed passwords, salts and configurations are stored in the database.

- It has built-in users and groups support (stored in database) 
- It provides Java APIs to manage users and groups
- It provides authentication API using JaaS
- REST support
- Besides built-in users/group support, you can choose to configure multiple LDAP/active directories per tenant/application
- Dynamic configuration - you can load and refresh configurations at runtime
- Audit trail for login 

# Multi tenancy support
Multiple ways you can use Masec to achieve your multi-tenancy goal:
- You are SaaS provider and offer one or more applications in the cloud: users are registered for each application and NOT shared among applications 
- Many applications in an enterprise setting: all users are shared by all applications. You use user access engine to control access rights.
- You want to host Masec in the cloud as a multi-tenancy service: you need to add mechanism to separate schema/database for each tenant and a router mechanism.  

# How to use it?

1. First you need to acquire a security service:

		// Default security service 
		SecurityService service = SecurityFactory.getSecCtx();
        
		OR
		
		// Specific to an application - in this case you are creating security service for "myapp" tenant or application
		// All the users for "myapp" application/tenant are only available to "myapp".
		SecurityService service = SecurityFactory.getSecCtx("myapp"); 

2. To login, simply call:
        
        String sessionId = IdentityManagement.login(service, "myuser", "MypaS$1");
		
3. To add a user:

		Boolean succ = IdentityManagement.addUser(service, sessionId, user);

4. To add LDAP configuration and refresh the configuration at run time:
	
		// LDAP configurations
    	Map <String, String> lC2 = new HashMap <String, String> ();
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
		
		Boolean succ = IdentityManagement.addAuthProvider(serviceLd, sessionId, provider);
		
		Boolean succ = IdentityManagement.refreshLdapConfigurations(serviceLd, sessionId, provider);
		

Check com.masec.IdentityManagement.java for all the API. For REST APIs, check com.masec.RestSecurityService.java. All available fields for all objects such as User, Group and so on are described in com.masec.Constants.java file.  

# Example
Check in the test folder for examples: SimpleLogin.java, SaaSLogin.java and RestTest.java. You can run all these tests and examples without making any changes.

# What is Technical user?
Technical user is used in Masec to have initial access to the system. You can think this as a superuser. This user is used to add other users. In practice when user register to your app through web site, you use this technical user session to add new user. Technical users should not have any other access rights except adding other users. You should be extremely careful about technical users especially for web/REST setup - it may provide a big security whole. If you are putting Masec in a web container then you should remove setApplicationTechnicalUser(...) API from  RestSecurityService.java file after you create a technical user. Then use login(...) to login your technical users to get initial sessionid. Then you can use this sessionid to add new users.

# Data Persistence	
By default, data are stored in an embedded HSQLDB database. So it runs without needing to change anything. For production deployment, you should update the spring orm configuration file to connect to a standalone database of your choice. Schema for HSQL and PostgreSql are provided. 

# Future
1. SSO support
2. Add policy engine
3. Create cloud connector for on-premise active directory server.