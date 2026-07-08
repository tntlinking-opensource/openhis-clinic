package com.geeke.config.shiro;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.geeke.admin.entity.User;
import com.geeke.admin.service.UserService;
import com.geeke.common.data.Parameter;
import com.geeke.common.data.SearchParamsBuilder;
import com.geeke.utils.SessionUtils;
import com.google.common.collect.Lists;

/**
 * @author: lys
 * @description: 自定义Realm
 * @date: 2019/10/24 10:06
 */
public class UserRealm extends AuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(UserRealm.class);

    @Autowired
    @Lazy
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //查询用户的权限
    	List<String> permissions = SessionUtils.getUserPermission();
        logger.debug("permission的值为: {}", permissions);
        //为当前用户设置角色和权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addStringPermissions(permissions);        
        
        return authorizationInfo;
    }

    /**
     * 密码哈希迭代次数 — 提升至10000次以增强安全性（原为6次）
     * 与 ShiroConfiguration 中的 HashedCredentialsMatcher 保持一致
     */
    private static final int PASSWORD_HASH_ITERATIONS = 10000;

    /**
     * 旧版密码哈希迭代次数 — 用于渐进式密码迁移
     * 登录时先用新迭代次数验证，失败后回退到旧迭代次数验证；
     * 旧迭代次数验证成功后，用新迭代次数重新哈希密码并更新数据库。
     */
    private static final int LEGACY_PASSWORD_HASH_ITERATIONS = 6;

    /**
     * 验证当前登录的Subject
     * LoginController.login()方法中执行Subject.login()时 执行此方法
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        String loginName = (String) authcToken.getPrincipal();
        // 获取用户密码
        String password = new String((char[]) authcToken.getCredentials());

        User user = this.getUserByLoginName(loginName);
        if (user == null) {
            //没找到帐号
            throw new UnknownAccountException();
        }

        // 验证密码 — 先用新迭代次数验证，失败后回退到旧迭代次数
        boolean passwordMatched = false;
        Md5Hash md5New = new Md5Hash(password, user.getId(), PASSWORD_HASH_ITERATIONS);
        if (md5New.toHex().equals(user.getLoginPassword())) {
            passwordMatched = true;
        } else {
            // 回退到旧迭代次数验证（兼容迁移期）
            Md5Hash md5Legacy = new Md5Hash(password, user.getId(), LEGACY_PASSWORD_HASH_ITERATIONS);
            if (md5Legacy.toHex().equals(user.getLoginPassword())) {
                passwordMatched = true;
                // 用新迭代次数重新哈希并更新数据库
                try {
                    user.setLoginPassword(md5New.toHex());
                    userService.updatePassword(user.getId(), md5New.toHex());
                    logger.info("用户 {} 密码已自动迁移到新的哈希迭代次数", loginName);
                } catch (Exception e) {
                    logger.warn("用户 {} 密码自动迁移失败: {}", loginName, e.getMessage());
                }
            }
        }

        if (!passwordMatched) {
            throw new UnknownAccountException();
        }

        // 用户账号被禁用
        if(user.getIsLocked() != 0) {
        	throw new DisabledAccountException();
        }

        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getLoginName(),
                password,
                getName()
        );

        //session中不需要保存密码
        user.setLoginPassword("");

        // 设置超级用户、系统管理员用户信息
        if("1000".equals(user.getId()) || "1001".equals(user.getId())) {
        	user.getCompany().setIds("0.");    // 设置公司的全编号

        }

        //将用户信息放入session中
        SessionUtils.setUser(user);
        return authenticationInfo;
    }
    
    private User getUserByLoginName(String loginName) {
    	List<Parameter> parms = SearchParamsBuilder.create()
    	        .eq("login_name", loginName)
    	        .build();
        List<User> list = userService.listAll(parms, null);
        if (list == null || list.size() < 1) {
            logger.info("{} 用户账号不存在", loginName);
            return null;
        }
        return list.get(0);
    }
}
