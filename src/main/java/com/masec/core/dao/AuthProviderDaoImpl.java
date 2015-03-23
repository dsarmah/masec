package com.masec.core.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.masec.core.model.AuthProvider;

@Repository( "authProviderDao" )
public class AuthProviderDaoImpl  extends HibernateDaoSupport implements AuthProviderDao
{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<AuthProvider> findByTypeAndApplication(String type, String application) 
	{
		return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery("from com.masec.core.model.AuthProvider AS ap where ap.id.application = :application AND ap.providerType = :type");
                q.setParameter("application", application);
                q.setParameter("type", type);
                return q.list();
            }
        });
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<AuthProvider> findAll(int offset, int limit) 
	{

		return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery("from com.masec.core.model.AuthProvider");
                q.setMaxResults(limit);
                q.setFirstResult(offset);
                return q.list();
            }
        });
	}

	@Override
	public void save(AuthProvider provider) 
	{
		getHibernateTemplate().save( provider );		
	}

	@Override
	public void update(AuthProvider provider) 
	{
		AuthProvider p = (AuthProvider) getHibernateTemplate().get(AuthProvider.class, provider.getId());
		if (null != p )
		{
			if (null != provider.getConfiguration())
			{
				p.setConfiguration(provider.getConfiguration());
			}
			
			if (null != provider.getProviderType())
			{
				p.setProviderType(provider.getProviderType());
			}
			
			getHibernateTemplate().update( p );
		}
	}

	@Override
	public void delete(AuthProvider provider) 
	{
		getHibernateTemplate().delete( provider );		
	}

   @Autowired
    public void init( SessionFactory sessionFactory )
    {
        setSessionFactory( sessionFactory );
    }

@SuppressWarnings({ "unchecked", "rawtypes" })
@Override
public List<AuthProvider> findAllByTypeOrderByApplication(String type) 
{
	return getHibernateTemplate().executeFind(new HibernateCallback() {
        public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Query q = session.createQuery("from com.masec.core.model.AuthProvider AS ap where ap.providerType = :type order by ap.id.application ASC");
            q.setParameter("type", type);
            return q.list();
        }
    });

}
   
}
