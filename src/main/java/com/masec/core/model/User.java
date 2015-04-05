package com.masec.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table( name = "MASECUSERS" )
public class User implements Serializable
{
    /**
     * Serialization id
     */
    private static final long serialVersionUID = 1L;
  
    @EmbeddedId
    private UserId id;
    
    @Column( name = "FIRSTNAME" )
    private String firstName;

    @Column( name = "LASTNAME" )
    private String lastName;

    @Column( name = "EMAIL" )
    private String email;

    @Column( name = "PHONE" )
    private String phone;
    
    @Column( name = "PICTURE" )
    private byte[] picture;
    
    @Column( name = "PASSWORD")
    private String password;

    @Column( name = "SALT" )
    private String salt;
        
    @Column( name = "SINCE" )
    private Long since;
    
    @Column( name = "SECQ1" )
    private String secQn1;

    @Column( name = "SECQ1ANS" )
    private String secQn1Ans;
    
    @Column( name = "SECQ2" )
    private String secQn2;

    @Column( name = "SECQ2ANS" )
    private String secQn2Ans;
    
    @Column( name = "SECQ3" )
    private String secQn3;

    @Column( name = "SECQ3ANS" )
    private String secQn3Ans;

    @Column( name = "EXTENDPROFILE" )
    private String extendProfile;
    
    public User()
    {
    }

    public User( 

    				 UserId id,
    				 String firstName,
                     String lastName,
                     String email,
                     String phone,
                     byte[] picture,                     
                     String password,
                     String salt,                     
                     Long since,
                     String secQn1,
                     String secQn1Ans,
                     String secQn2,
                     String secQn2Ans,
                     String secQn3,
                     String secQn3Ans,
                     String extendProfile)
    {
    	this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.picture = picture;
        this.password = password;
        this.salt = salt;        
        this.since = since;
        this.secQn1 = secQn1;
        this.secQn1Ans = secQn1Ans;
        this.secQn2 = secQn2;
        this.secQn2Ans = secQn2Ans;
        this.secQn3 = secQn3;
        this.secQn3Ans = secQn3Ans;
        this.extendProfile = extendProfile;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Long getSince() {
		return since;
	}

	public void setSince(Long since) {
		this.since = since;
	}

	public String getSecQn1() {
		return secQn1;
	}

	public void setSecQn1(String secQn1) {
		this.secQn1 = secQn1;
	}

	public String getSecQn1Ans() {
		return secQn1Ans;
	}

	public void setSecQn1Ans(String secQn1Ans) {
		this.secQn1Ans = secQn1Ans;
	}

	public String getSecQn2() {
		return secQn2;
	}

	public void setSecQn2(String secQn2) {
		this.secQn2 = secQn2;
	}

	public String getSecQn2Ans() {
		return secQn2Ans;
	}

	public void setSecQn2Ans(String secQn2Ans) {
		this.secQn2Ans = secQn2Ans;
	}

	public String getSecQn3() {
		return secQn3;
	}

	public void setSecQn3(String secQn3) {
		this.secQn3 = secQn3;
	}

	public String getSecQn3Ans() {
		return secQn3Ans;
	}

	public void setSecQn3Ans(String secQn3Ans) {
		this.secQn3Ans = secQn3Ans;
	}

	public String getExtendProfile() {
		return extendProfile;
	}

	public void setExtendProfile(String extendProfile) {
		this.extendProfile = extendProfile;
	}

	public UserId getId() {
		return id;
	}

	public void setId(UserId id) {
		this.id = id;
	}
}
