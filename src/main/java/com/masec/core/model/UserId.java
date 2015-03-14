package com.masec.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserId implements Serializable
{
	private static final long serialVersionUID = 1909L;

	@Column( name = "UNAME" )
    private String userName;
	
	@Column( name = "APPLICATION" )
    private String applicationCtx;

	public String getUserName() {
		return userName;
	}

	public String getApplicationCtx() {
		return applicationCtx;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setApplicationCtx(String applicationCtx) {
		this.applicationCtx = applicationCtx;
	}
}
