package com.masec.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserGroupId implements Serializable
{
	private static final long serialVersionUID = 909L;

	@Column( name = "GNAME" )
    private String gName;
	
	@Column( name = "UNAME" )
    private String uName;

	public String getgName() {
		return gName;
	}

	public String getuName() {
		return uName;
	}

	public void setgName(String gName) {
		this.gName = gName;
	}

	public void setuName(String uName) {
		this.uName = uName;
	}


}
