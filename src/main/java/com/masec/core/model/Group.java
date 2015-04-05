package com.masec.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Entity
@Table( name = "MASECGROUPS" )
public class Group implements Serializable
{
    /**
     * Serialization id
     */
    private static final long serialVersionUID = 144L;
    
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "GID" )
    private Long autoId;
	
    
    @EmbeddedId
    private GroupId id;
       
    @Column( name = "DESCRIPTION" )
    private String description;

    public Long getAutoId() {
		return autoId;
	}

	public GroupId getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}


	public void setAutoId(Long autoId) {
		this.autoId = autoId;
	}


	public void setId(GroupId id) {
		this.id = id;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	
}
