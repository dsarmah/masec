package com.masec.core.dao;

import java.util.List;

import com.masec.core.model.UserGroup;

public interface UserGroupDao
{
	public List<UserGroup> findUserGroups(String application, int offset, int limit);
	public List<UserGroup> findGroupsByUser(String application, String userName);
	public List<UserGroup> isPartOfAnyGroup(String application, String userName, List<String> groups); 
    public void save( UserGroup g );
    public void update( UserGroup g );
    public void delete( UserGroup g );
}
