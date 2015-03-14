package com.masec.core.dao;

import com.masec.core.model.User;
import com.masec.core.model.UserId;

import java.util.List;

public interface UserDao
{
    public User findByUserNameAndApplication( UserId id );
    public List<User> findAll(int offset, int limit);
    public void save( User user);
    public void update( User user);
    public void delete( User user );
    public void shutdown();
}
