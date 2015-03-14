# masec
App centric Java component to manage users and authentications in the cloud. 

# What you can do?
You can embed this code in your Java applications to manage users, authenticate and update password. You can share users for all your applications or separate them as per application. You can also run this as separate component in a web container and make it a central user management and authentication service. Users data including hashed password are stored in the database.

# How to use it?

1. Firs you need to acquire a security service:

		// Default security service
		SecurityService service = SecurityServiceFactory.getSecurityService();
        
		OR
		
		// Specific to an application - in this case you are creating security service for "myapp" application
		SecurityService service = SecurityServiceFactory.getSecurityService("myapp"); 

2. To login, simply call:

		Map <String, Object> user = new HashMap <String, Object> ();
        user.put(Constants.USERNAME, userid);
        user.put(Constants.PASSWORD, pw);
        
        Map <String, Object> ret = service.login(user);
        success = (Boolean) ret.get(Constants.STATUS);
		
3. To add a user:

		Map <String, Object> ret = service.addUser(user);
		
Check ServiceSecurity.java for all the available APIs. All available field for User object are described in Constants.java file.

# Data Persistence	
By default, embedded in memory HSQLDB database is used to store data. So by default it will run as it is without needing to change anything. For production you should update the spring orm configuration file to connect to a standalone database of your choice. Schema for HSQL and Postgre are provided. 

# Future
1. Add REST API
2. Add LDAP configurations - i.e., application can use native login module or configured one.
3. Add policy engine
4. Create cloud connector to connect on-premise active directory server.