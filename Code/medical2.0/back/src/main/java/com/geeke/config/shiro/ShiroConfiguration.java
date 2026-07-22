package com.geeke.config.shiro;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: lys
 * @description: shiro配置类
 * @date: 2017/10/24 10:10
 */
@Configuration
public class ShiroConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ShiroConfiguration.class);

	 /**
	 * Shiro的Web过滤器Factory 命名:shiroFilter
	 */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, AjaxPermissionsAuthorizationFilter ajaxFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //Shiro的核心安全接口,这个属性是必须的
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("authc", ajaxFilter);
        shiroFilterFactoryBean.setFilters(filterMap);
        
        /*定义shiro过滤链  Map结构
         * Map中key(xml中是指value值)的第一个'/'代表的路径是相对于HttpServletRequest.getContextPath()的值来的
         * anon：它对应的过滤器里面是空的,什么都没做,这里.do和.jsp后面的*表示参数,比方说login.jsp?main这种
         * authc：该过滤器下的页面必须验证后才能访问,它是Shiro内置的一个拦截器org.apache.shiro.web.filter.authc.FormAuthenticationFilter
         */
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
         /* 过滤链定义，从上向下顺序执行，一般将 / ** 放在最为下边:这是一个坑呢，一不小心代码就不好使了;
          authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问 */

		// camunda 加载流程示意图
        filterChainDefinitionMap.put("/rest/process-definition/*/diagram", "anon");

        filterChainDefinitionMap.put("/websocket/**", "anon");

        // 认证相关接口（登录、获取Token等）
        filterChainDefinitionMap.put("/auth/token", "anon");
        filterChainDefinitionMap.put("/auth/getToken", "anon");
        filterChainDefinitionMap.put("/auth/getUserTenant", "anon");
        filterChainDefinitionMap.put("/auth/loginedtocken", "anon");
        filterChainDefinitionMap.put("/auth/wxToken", "anon");

        //院版获取诊所信息放行
        filterChainDefinitionMap.put("/hosdata/HosCollectData/listAll", "anon");

//        关于微信用户登陆
        filterChainDefinitionMap.put("/wxUser/initLogin","anon");
        filterChainDefinitionMap.put("/wxUser/getCode","anon");
        filterChainDefinitionMap.put("/wxUser/getPhone","anon");


        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/sys/sysSeting/getFile/1", "anon");
        filterChainDefinitionMap.put("/sys/sysSeting/getFile/2", "anon");
        filterChainDefinitionMap.put("/sys/sysSeting/getFile/3", "anon");
        filterChainDefinitionMap.put("/sys/sysSeting/getFile/4", "anon");
        filterChainDefinitionMap.put("/sys/sysSeting/getFile/5", "anon");
        filterChainDefinitionMap.put("/sys/sysSeting/getFile/6", "anon");
        filterChainDefinitionMap.put("/sys/sysSeting/getFile/**", "anon");
        filterChainDefinitionMap.put("/sys/sysSeting/filedownload/**", "anon");
        filterChainDefinitionMap.put("/sys/sysSeting/listAll", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/login/auth", "anon");
        filterChainDefinitionMap.put("/login/logout", "anon");
        filterChainDefinitionMap.put("/error", "anon");
        filterChainDefinitionMap.put("/ureport/**", "anon");
       // filterChainDefinitionMap.put("/outpatient/patient/**", "anon");
        filterChainDefinitionMap.put("/swagger-ui.html","anon");
        filterChainDefinitionMap.put("/swagger-ui/**","anon");
        filterChainDefinitionMap.put("/webjars/**","anon");
        filterChainDefinitionMap.put("/v3/api-docs/**","anon");
        filterChainDefinitionMap.put("/v2/**","anon");
        filterChainDefinitionMap.put("/swagger-resources/**","anon");
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SessionDAO sessionDAO() {
        // 使用内存SessionDAO替代RedisSessionDAO
        return new MemorySessionDAO();
    }

    @Bean
    public StatelessSessionManager sessionManager(SessionDAO sessionDAO) {
        StatelessSessionManager sessionManager = new StatelessSessionManager();
        sessionManager.setSessionDAO(sessionDAO);
// 禁用会话验证器调度时间
        sessionManager.setSessionValidationSchedulerEnabled(false);
// 会话超时设置为24小时（86400000毫秒），防止Redis内存泄漏
        sessionManager.setGlobalSessionTimeout(86400000);
// 是否在会话过期后调用 SessionDAO 的删除方法，保持为 true 不会影响过期
        sessionManager.setDeleteInvalidSessions(true);
// 禁用 URL 重写
        sessionManager.setSessionIdUrlRewritingEnabled(false);
// 启用会话 ID Cookie
        sessionManager.setSessionIdCookieEnabled(true);
        return sessionManager;

    }
    
	
	/**
     * Shiro Realm 继承自AuthorizingRealm的自定义Realm,即指定Shiro验证用户登录的类为自定义的
     */
    @Bean
    public Realm realm() {
        return new UserRealm();
    }

    
    /**
     * 不指定名字的话，自动创建一个方法名第一个字母小写的bean
     * @param realm
     * @param sessionManager
     * @param
     * @return
     */
    @Bean
    public SecurityManager securityManager(Realm realm, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setSessionManager(sessionManager);
        logger.info("Use the StatelessSessionManager in SecurityManager");
        return securityManager;
    }


    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     * 所以我们需要修改下doGetAuthenticationInfo中的代码;
     * ）
     * 可以扩展凭证匹配器，实现 输入密码错误次数后锁定等功能，下一次
     */
    @Bean(name = "credentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        //散列的次数，默认2次
        hashedCredentialsMatcher.setHashIterations(2);
        //storedCredentialsHexEncoded默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }

    /**
     * Shiro生命周期处理器
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    
    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     */

    /**
     * 禁用 Shiro Filter 的 Servlet Filter 注册，由 ShiroFilterFactoryBean 管理
     */
    @Bean
    public FilterRegistrationBean<AjaxPermissionsAuthorizationFilter> ajaxFilterRegistration(AjaxPermissionsAuthorizationFilter filter) {
        FilterRegistrationBean<AjaxPermissionsAuthorizationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

}
