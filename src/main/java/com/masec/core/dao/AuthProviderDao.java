package com.masec.core.dao;

import java.util.List;

import com.masec.core.model.AuthProvider;

public interface AuthProviderDao 
{
    public List<AuthProvider> findByTypeAndApplication( String type, String application );
    public List<AuthProvider> findAll(int offset, int limit);
    public List<AuthProvider> findAllByTypeOrderByApplication(String type);
    public void save( AuthProvider provider);
    public void update( AuthProvider provider);
    public void delete( AuthProvider provider );
}
