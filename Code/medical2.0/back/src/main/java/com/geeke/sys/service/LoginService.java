package com.geeke.sys.service;

import com.geeke.admin.dao.UserDao;
import com.geeke.admin.entity.User;
import com.geeke.common.constants.ActionConstants;
import com.geeke.common.service.CrudService;
import com.geeke.utils.SessionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author: lys
 * @description: 登录service实现类
 * @date: 2018/10/24 11:53
 */
@Service
public class LoginService extends CrudService<UserDao, User> {
    @Autowired
    private UserDao userDao;

    private Logger logger = LoggerFactory.getLogger(LoginService.class);

    /*
     * 验证用户名和密码
     */
    @Transactional(readOnly = false)
    public HttpStatus authLogin(String username, String password, String tenantId) {
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isAuthenticated())
        	return HttpStatus.OK;
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            currentUser.login(token);
        } catch (UnknownAccountException e) {
        	logger.info("用户 {} 登录账号密码错误", username);
        	return HttpStatus.UNAUTHORIZED;			// 账号密码错误
        }catch(DisabledAccountException e) {
        	logger.info("用户 {} 账号禁用", username);
        	return HttpStatus.FORBIDDEN;			// 账号禁用
        }catch (AuthenticationException e) {
        	logger.info("用户 {} 验证错误", username);
        	return HttpStatus.SERVICE_UNAVAILABLE;			// 验证错误
        }
        //保存当前用户的所有租户信息
        User user = getUserFullClinics();
        //当前登录的租户信息保存到session中
        SessionUtils.setLoginTenantId(tenantId);
        // 保存登录日志
        this.saveAction(this.createAction(ActionConstants.ACTION_LOGIN, user));

        return HttpStatus.OK;
    }

    private User getUserFullClinics() {
        User user = SessionUtils.getUser();
        List<String> clinics = userDao.getClinicId(user.getId());
        user.setClinics(clinics);
        SessionUtils.setUser(user);
        return user;
    }

    /**
     * 获取需要监测登录状态用户
     * @return
     */
    public List<User> loginUser() {
        return userDao.loginUser();
    }

    /**
     * 默认登录
     * @return
     */
    public HttpStatus authLogin() {
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isAuthenticated()){
            return HttpStatus.OK;
        }
        return HttpStatus.NON_AUTHORITATIVE_INFORMATION;
    }

    /**
     * 退出登录
     *
     * @return
     */
    @Transactional(readOnly = false)
    public void logout() {
        // 保存登录日志
        User user = SessionUtils.getUser();
        this.saveAction(this.createAction(ActionConstants.ACTION_LOGOUT, user));

        try {
            Subject currentUser = SecurityUtils.getSubject();
            if(currentUser.isAuthenticated())
            	currentUser.logout();
        } catch (Exception e) {
        }
    }

    @Transactional(readOnly = false)
    public HttpStatus wxLogin(String loginName, String loginPassword) {
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(loginName, loginPassword);
        try {
            currentUser.login(token);
        } catch (UnknownAccountException e) {
            return HttpStatus.UNAUTHORIZED;			// 账号密码错误
        }catch(DisabledAccountException e) {
            return HttpStatus.FORBIDDEN;			// 账号禁用
        }catch (AuthenticationException e) {
            return HttpStatus.SERVICE_UNAVAILABLE;			// 验证错误
        }
        return HttpStatus.OK;
    }
}
