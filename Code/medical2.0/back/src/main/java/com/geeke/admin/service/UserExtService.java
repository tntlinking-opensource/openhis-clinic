package com.geeke.admin.service;

import com.geeke.admin.dao.UserExtDao;
import com.geeke.admin.entity.UserExt;
import com.geeke.common.service.CrudService;
import com.geeke.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户管理Service
 * @author lys
 * @version 2021-08-25
 */

@Service("userExtService")
@Transactional(readOnly = true)
public class UserExtService extends CrudService<UserExtDao, UserExt>{

    @Autowired
    private UserExtDao userExtDao;

    public UserExt getExtByUserId(String userId)
    {
        return userExtDao.getUserExtByUserId(userId, SessionUtils.getLoginTenantId());
    }

    public UserExt getOldExtByUserId(String userId)
    {
        List<UserExt> allUserExtByUserId = userExtDao.getAllUserExtByUserId(userId);
        if(allUserExtByUserId.size()>0)
        {
            return allUserExtByUserId.get(0);
        }
        return null;
    }

    public void update(UserExt userId)
    {
        userExtDao.update(userId);
    }

    public void insert(UserExt userExt)
    {
        userExtDao.insert(userExt);
    }
}