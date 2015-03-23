package com.masec;

public class Constants 
{
	public final static String MASEC = "masec";
	public final static String STATUS = "status";
	public final static String ERRORMSG = "errorMsg";
	public final static String SECCTX = "securityContext";
	
	// USER OBJECT - all are String except specified in the comment.
	public final static String USERNAME = "userName";
	public final static String PASSWORD = "password";
	public final static String SALT = "salt"; //internal - no need to assign by caller
	public final static String FIRSTNAME = "firstName";
	public final static String LASTNAME = "lastName";
	public final static String EMAIL = "email";
	public final static String PHONE = "phone";
	public final static String PICTURE = "picture"; //byte[]
	public final static String SINCE = "since"; //Long
	public final static String SECQN1 = "secQn1";
	public final static String SECQN1ANS = "secQn1Ans";
	public final static String SECQN2 = "secQn2";
	public final static String SECQN2ANS = "secQn2Ans";
	public final static String SECQN3 = "secQn3";
	public final static String SECQN3ANS = "secQn3Ans";
	public final static String EXTENDEDPROFILE = "extendProfile";
	
	//****** END OF USER OBJECT *******//
	
	// AuthProvider configuration OBJECT - all fields are String	
	public final static String PROVIDERTYPE = "providerType";
	public final static String PROVIDERNAME = "providerName";
	public final static String PROVIDERCONF = "configuration";
	public final static String PROVIDERAPP = "apllication";
		
	// LDAP Configurations...
	public final static String LDAP_LOGINMODULE = "loginmodule";
	public final static String LDAP_USERPROVIDER = "userProvider";
	public final static String LDAP_USERFILTER = "userFilter";
	public final static String LDAP_USERIDENTITY = "authzIdentity";
	
}
