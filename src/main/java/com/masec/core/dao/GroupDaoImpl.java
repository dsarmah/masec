package com.masec.core.dao;

import com.masec.core.model.Group;
import com.masec.core.model.GroupId;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;


@Repository( "groupDao" )
public class GroupDaoImpl extends HibernateDaoSupport implements GroupDao
{

	@Autowired
    public void init( SessionFactory sessionFactory )
    {
        setSessionFactory( sessionFactory );
    }

	@Override
	public Group findByGroupNameAndApplication(GroupId id) 
	{
		return ( Group )getHibernateTemplate().get( Group.class, id);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Group> findAll(int offset, int limit, String application) 
	{
    	return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery("from com.masec.core.model.Group AS g where g.id.application = :application");
                q.setParameter("application", application);
                q.setMaxResults(limit);
                q.setFirstResult(offset);
                return q.list();
            }
    	});
	}

	@Override
	public void save(Group g) 
	{
		getHibernateTemplate().save( g );		
	}

	@Override
	public void update(Group g) 
	{
    	Group u = ( Group )getHibernateTemplate().get( Group.class, g.getId());
    	if (null != u)
    	{
    		if (null != g.getDescription())
    		{
    			u.setDescription(g.getDescription());
    		}
    		getHibernateTemplate().update( u );
    	}

		
	}

	/*
	 * (non-Javadoc)
	 * When group is deleted all corresponding user/group entries of the UserGroup table are also deleted.
	 */
	@Override
	public void delete(Group g) 
	{
		
		Session sess = getHibernateTemplate().getSessionFactory().openSession();
		Transaction t = sess.beginTransaction();
		t.begin();
		
		try
		{
			Query q = sess.createQuery("delete from com.masec.core.model.Group AS g where g.id.application = :application AND g.id.gName = :group");
			q.setParameter("application", g.getId().getApplication());
			q.setParameter("group", g.getId().getgName());
			q.executeUpdate();
			
			q = sess.createQuery("delete from com.masec.core.model.UserGroup AS g where g.application = :application AND g.id.gName = :group");
			q.setParameter("application", g.getId().getApplication());
			q.setParameter("group", g.getId().getgName());
			q.executeUpdate();
			
			t.commit();		
			//getHibernateTemplate().delete( g );
		
		}
		catch (Exception x)
		{
			x.printStackTrace();
			t.rollback();
		}
		finally
		{
			sess.close();
		}
	}
}
