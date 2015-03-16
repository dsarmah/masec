# masec - Identity Management
App centric Java component to manage users and authentications in the cloud. 

# Technology used
- Java JAAS
- Spring ORM

# What you can do?
You can embed this code in your Java applications to manage and authenticate users, and update password. You can also run this as separate component in a web container and make it a central user management and authentication service - this is desirable if you plan to it to manage identity for multiple applications. You can share users for all your applications or separate them as per application. Users data including hashed password and salts are stored in the database.

# How to use it?

1. First you need to acquire a security service:

		// Default security service 
		SecurityService service = SecurityServiceFactory.getSecurityService();
        
		OR
		
		// Specific to an application - in this case you are creating security service for "myapp" application
		// All the users for "myapp" applications are only available to "myapp".
		SecurityService service = SecurityServiceFactory.getSecurityService("myapp"); 

2. To login, simply call:

		Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, userid);
        user.put(Constants.PASSWORD, pw);
        
        Map <String, Object> ret = service.login(user);
        success = (Boolean) ret.get(Constants.STATUS);
		
3. To add a user:

		Map <String, Object> ret = service.addUser(user);
		
Check ServiceSecurity.java for all the available APIs. All available fields for User object are described in Constants.java file.

# Example
Check the unit test: AppTest.java

# Data Persistence	
By default, data are stored in an embedded in-memory HSQLDB database. So it runs as it is without needing to change anything. For production deployment, you should update the spring orm configuration file to connect to a standalone database of your choice. Schema for HSQL and PostgreSql are provided. 

# Future
1. Add REST API
2. Add LDAP configurations - i.e., application can use native login module or configured one.
3. Add policy engine
4. Create cloud connector to connect on-premise active directory server.