package com.masec.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AuthProviderId implements Serializable
{
	private static final long serialVersionUID = 809L;

	@Column( name = "PROVIDERNAME" )
    private String providerName;	
	
	@Column( name = "APPLICATION" )
    private String application;

	public String getProviderName() {
		return providerName;
	}

	public String getApplication() {
		return application;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public void setApplication(String application) {
		this.application = application;
	}	
}
