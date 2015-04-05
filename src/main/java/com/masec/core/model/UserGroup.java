package com.masec.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Entity
@Table( name = "MASECUSERSGROUP" )
public class UserGroup implements Serializable
{
    /**
     * Serialization id
     */
    private static final long serialVersionUID = 144L;
    
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "ID" )
    private Long ugid;
	
    
    @EmbeddedId
    private UserGroupId id;
       
    @Column( name = "APPLICATION" )
    private String application;

	public Long getUgid() {
		return ugid;
	}

	public UserGroupId getId() {
		return id;
	}

	public String getApplication() {
		return application;
	}

	public void setUgid(Long ugid) {
		this.ugid = ugid;
	}

	public void setId(UserGroupId id) {
		this.id = id;
	}

	public void setApplication(String application) {
		this.application = application;
	}

 
}
