package com.masec.core.dao;

import com.masec.core.model.Group;
import com.masec.core.model.GroupId;

import java.util.List;

public interface GroupDao
{
    public Group findByGroupNameAndApplication( GroupId id );
    public List<Group> findAll(int offset, int limit, String application);
    public void save( Group g );
    public void update( Group g);
    public void delete( Group g );
}
