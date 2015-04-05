package com.masec.core.service;

import com.masec.core.model.User;
import com.masec.core.model.UserId;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface UserService
{
    public User findByUserNameAndApplication( UserId id );
    public List<User> findAll(int offset, int limit);
    public void save( User user) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException;
    public void update( User user);
    public void updatePassword( User user, String newPasswd) throws Exception;
    public void delete( User user );
    public void shutdown();
    
    public User authenticate(UserId id, String password);
}
