package com.masec.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GroupId implements Serializable
{
	private static final long serialVersionUID = 909L;

	@Column( name = "GNAME" )
    private String gName;
	
	@Column( name = "APPLICATION" )
    private String application;

	public String getgName() 
	{
		return gName;
	}

	public String getApplication() 
	{
		return application;
	}

	public void setgName(String gName) 
	{
		this.gName = gName;
	}

	public void setApplication(String application) 
	{
		this.application = application;
	}


}
