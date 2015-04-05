package com.masec.core.service;

import com.masec.core.dao.UserDao;
import com.masec.core.model.User;
import com.masec.core.model.UserId;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service( "userService" )
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserDao userDao;

    @Override
    public User findByUserNameAndApplication( UserId id)
    {
        return userDao.findByUserNameAndApplication(id);
    }

    @Override
    public List<User> findAll(int offset, int limit)
    {
    	List<User> uList = userDao.findAll(offset, limit);
    	for (User u: uList)
    	{
    		u.setPassword(null);
    	}
    	return uList;
    }

    @Override
    public void save( User user ) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException
    {
    	if (null != user.getId().getUserName() && null != user.getId().getApplicationCtx() && null != user.getPassword())
    	{
    		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            // Salt generation 64 bits long
            byte[] bSalt = new byte[8];
            random.nextBytes(bSalt);
            // Digest computation
            byte[] bDigest = Utility.getHash(user.getPassword(), bSalt);
            String sDigest = Utility.byteToBase64(bDigest);
            String sSalt = Utility.byteToBase64(bSalt);
            
            user.setPassword(sDigest);
            user.setSalt(sSalt);
    		userDao.save( user );
    	}
    	else
    	{
    		throw new IOException("username and password can not be null.");
    	}
    }

    @Override
    public void update( User user )
    {
        userDao.update( user );
    }

    @Override
    public void delete( User user )
    {
        userDao.delete( user );
    }

    @Override
    public void shutdown()
    {
        userDao.shutdown();
    }
    
    @Override
    public User authenticate(UserId id, String password)
    {
    	if (null != password)
    	{
    		User u = userDao.findByUserNameAndApplication(id);
    	
    		if (null != u)
    		{
    			try 
    			{
    				byte[] bDigest = Utility.base64ToByte(u.getPassword());
					byte[] bSalt = Utility.base64ToByte(u.getSalt());
					byte[] proposedDigest = Utility.getHash(password, bSalt);
					
					
	    			if (Arrays.equals(proposedDigest, bDigest))
	    			{
	    				u.setPassword(null);
	    				return u;
	    			}
    			} 
    			catch (IOException e) 
    			{
					e.printStackTrace();
				} 
    			catch (NoSuchAlgorithmException e) 
    			{
					e.printStackTrace();
				}
    		}
    	}
    	return null;
    }

    // No specific check is perform here... all checks are already performed by the caller SecurityServiceImpl
	@Override
	public void updatePassword(User user, String newPasswd) throws Exception 
	{
		if (null != user.getId().getUserName() && null != user.getId().getApplicationCtx() && null != newPasswd)
    	{
			
    		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            // Salt generation 64 bits long
            byte[] bSalt = new byte[8];
            random.nextBytes(bSalt);
            // Digest computation
            byte[] bDigest = Utility.getHash(newPasswd, bSalt);
            String sDigest = Utility.byteToBase64(bDigest);
            String sSalt = Utility.byteToBase64(bSalt);
            
            User u = new User();
            u.setPassword(sDigest);
            u.setSalt(sSalt);
            u.setId(user.getId());
    		userDao.update( u );
    	}
    	else
    	{
    		throw new Exception("username and password can not be null.");
    	}		
	}
}
