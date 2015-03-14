package com.masec;

import java.util.List;
import java.util.Map;

public interface SecurityService 
{
	public Map<String, Object> login (Map<String, Object> user); 
	
	public Map<String, Object> addUser(Map<String, Object> user);
	
	public List<Map<String, Object>> listUser(int offset, int limit);
	
	public void shutdown();
	
	public Map<String, Object> deleteUser(Map<String, Object> user);
	
	public Map<String, Object> updateUser(Map<String, Object> user);
	
	public Map<String, Object> updatePassword(Map<String, Object> user);	
}
