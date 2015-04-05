package com.masec.core.dao;

import com.masec.core.model.User;
import com.masec.core.model.UserId;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;


@Repository( "userDao" )
public class UserDaoImpl extends HibernateDaoSupport implements UserDao
{
    @Override
    public User findByUserNameAndApplication( UserId id )
    {
    	
        return ( User )getHibernateTemplate().get( User.class, id);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public List<User> findAll(int offset, int limit)
    {
    	return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery("from com.masec.core.model.User");
                q.setMaxResults(limit);
                q.setFirstResult(offset);
                return q.list();
            }
        });
        //return getHibernateTemplate().find( "from com.masec.core.model.User" );
    }

    @Override
    public void save( User user )
    {
    	//System.out.println(user.getId().getUserName() + "," + user.getPassword() + "," + user.getId().getApplicationCtx());
        getHibernateTemplate().save( user );
    }

    @Override
    public void update( User user )
    {
    	User u = ( User )getHibernateTemplate().get( User.class, user.getId());
    	if (null != u)
    	{
    		if (null != user.getEmail())
    		{
    			u.setEmail(user.getEmail());
    		}
    		if (null != user.getExtendProfile())
    		{
    			u.setExtendProfile(user.getExtendProfile());
    		}
    		if(null != user.getFirstName())
    		{
    			u.setFirstName(user.getFirstName());
    		}
    		if(null != user.getLastName())
    		{
    			u.setLastName(user.getLastName());
    		}    		
    		if(null != user.getPassword())
    		{
    			u.setPassword(user.getPassword());
    		}
    		if(null != user.getPhone())
    		{
    			u.setPhone(user.getPhone());
    		}
    		if(null != user.getPicture())
    		{
    			u.setPicture(user.getPicture());
    		}
    		if(null != user.getSalt())
    		{
    			u.setSalt(user.getSalt()); 
    		}
    		if(null != user.getSecQn1())
    		{
    			u.setSecQn1(user.getSecQn1());
    		}
    		if(null != user.getSecQn1Ans())
    		{
    			u.setSecQn1Ans(user.getSecQn1Ans());
    		}
    		if(null != user.getSecQn2())
    		{
    			u.setSecQn2(user.getSecQn2());
    		}
    		if(null != user.getSecQn2Ans())
    		{
    			u.setSecQn2Ans(user.getSecQn2Ans());
    		}
    		if(null != user.getSecQn3())
    		{
    			u.setSecQn3(user.getSecQn3());
    		}
    		if(null != user.getSecQn3Ans())
    		{
    			u.setSecQn3Ans(user.getSecQn3Ans());
    		}
    		if(null != user.getSince())
    		{
    			u.setSince((Long)user.getSince());
    		}    		
    		getHibernateTemplate().update( u );
    	}
    }

    @Override
    public void delete( User user )
    {
        getHibernateTemplate().delete( user );
    }

    @Override
    public void shutdown()
    {
        getHibernateTemplate().getSessionFactory().openSession().createSQLQuery( "SHUTDOWN" ).executeUpdate();
    }

    @Autowired
    public void init( SessionFactory sessionFactory )
    {
        setSessionFactory( sessionFactory );
    }
}
