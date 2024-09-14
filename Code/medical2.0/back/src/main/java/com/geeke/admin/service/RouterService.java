package com.geeke.admin.service;

import com.geeke.admin.dao.RouterDao;
import com.geeke.admin.entity.Router;
import com.geeke.common.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 路由管理Service
 * @author lys
 * @version 2021-11-18
 */
 
@Service("routerService")
@Transactional(readOnly = true)
public class RouterService extends TreeService<RouterDao, Router> {


 public List<Router> getAllRoutersByVersion(String roleId)
 {
     return this.dao.listAllByVersion(roleId);
 }

    public List<String> getUserIndateWarning(String userId) {
        List<String> userIndateWarning = this.dao.getUserIndateWarning(userId);
        return userIndateWarning;
    }
}