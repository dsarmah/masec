package com.masec.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table( name = "MASECAUTHPROVIDER" )
public class AuthProvider  implements Serializable
{

	private static final long serialVersionUID = 1787L;

	@EmbeddedId
    private AuthProviderId id;
	
	@Column( name = "PROVIDERTYPE" )
    private String providerType;
	
	@Column( name = "CONFIGURATION" )
    private String configuration;

	public String getProviderType() {
		return providerType;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public AuthProviderId getId() {
		return id;
	}

	public void setId(AuthProviderId id) {
		this.id = id;
	}	
	
}
