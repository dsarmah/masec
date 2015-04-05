package com.masec.core.dao;

import com.masec.core.model.UserGroup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;


@Repository( "userGroupDao" )
public class UserGroupDaoImpl extends HibernateDaoSupport implements UserGroupDao
{

	@Autowired
    public void init( SessionFactory sessionFactory )
    {
        setSessionFactory( sessionFactory );
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<UserGroup> findUserGroups(String application, int offset, int limit) 
	{
		return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery("from com.masec.core.model.UserGroup AS g where g.application = :application");
                q.setParameter("application", application);
                q.setMaxResults(limit);
                q.setFirstResult(offset);
                return q.list();
            }
    	});
	}

	@Override
	public void save(UserGroup g) 
	{
		getHibernateTemplate().save( g );		
		
	}

	@Override
	public void update(UserGroup g) 
	{
		UserGroup u = ( UserGroup )getHibernateTemplate().get( UserGroup.class, g.getId());
    	if (null != u)
    	{
    		if (null != g.getApplication())
    		{
    			u.setApplication(g.getApplication());
    		}
    		getHibernateTemplate().update( u );
    	}	
	}

	@Override
	public void delete(UserGroup g) 
	{
		getHibernateTemplate().delete( g );
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<UserGroup> findGroupsByUser(String application, String userName) 
	{
		return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery("from com.masec.core.model.UserGroup AS g where g.application = :application AND g.id.uName = :user");
                q.setParameter("application", application);
                q.setParameter("user", userName);
                return q.list();
            }
    	});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<UserGroup> isPartOfAnyGroup(String application, String userName, List<String> groups) 
	{
		return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery("from com.masec.core.model.UserGroup AS g where g.application = :application AND g.id.uName = :user");
                q.setParameter("application", application);
                q.setParameter("user", userName);
                List<UserGroup> retg = q.list();
                
                List<UserGroup> ret = new ArrayList <UserGroup> ();
                
                for (UserGroup ug: retg)
                {
                	for (String ag: groups)
                	{
                		if (ag != null && ag.equals(ug.getId().getgName()))
                		{
                			ret.add(ug);
                			break;
                		}
                	}
                }
                
                return ret;
            }
    	});
	}
}
