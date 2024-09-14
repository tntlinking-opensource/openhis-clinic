package com.geeke.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.common.entity.DataPermission;
import com.geeke.admin.common.service.PermissionService;
import com.geeke.admin.entity.Router;
import com.geeke.admin.entity.User;
import com.geeke.admin.service.UserService;
import com.geeke.common.data.Parameter;
import com.geeke.config.cache.RedisProperties;
import com.geeke.config.shiro.StatelessSessionManager;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
import com.geeke.sys.entity.PersonalTheme;
import com.geeke.sys.entity.Theme;
import com.geeke.sys.service.LoginService;
import com.geeke.sys.service.PersonalThemeService;
import com.geeke.sys.service.ThemeService;
import com.geeke.utils.JwtUtils;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Lists;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ThemeService themeService;

    @Autowired
    private PersonalThemeService personalThemeService;

    @Autowired
    private RedisProperties redisProperties;

    /**
     * 登陆前检查所处诊所
     *
     * @param loginName
     * @return
     */
    @RequestMapping("/getUserTenant")
    public ResponseEntity<?> getUserTenant(@RequestParam(value = "loginName") String loginName, @RequestParam(value = "password") String password) {

        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(loginName, password);
        try {
            currentUser.login(token);
        } catch (UnknownAccountException e) {
            logger.info("用户 {} 登录账号密码错误", loginName);
            return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_10010));
        } catch (DisabledAccountException e) {
            logger.info("用户 {} 账号禁用", loginName);
            return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_10011));
        } catch (AuthenticationException e) {
            logger.info("用户 {} 验证错误", loginName);
            return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_10012));
        }

        List<Company> clinics = companyService.getClinicsByLoginName(loginName);
        return ResponseEntity.ok(clinics);
    }

    @PostMapping("/token")
    public ResponseEntity<?> getToken(HttpServletRequest request, String loginName, String password, String companyId) {
        Map<String, Object> response = null;
        List<String> idList = new ArrayList<>();
        System.err.println(redisProperties);
        try {
            //登陆
            HttpStatus httpStatus = loginService.authLogin(loginName, password, companyId);

            if (HttpStatus.UNAUTHORIZED.equals(httpStatus)) {        //账号密码错误
                return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_10010));
            } else if (HttpStatus.FORBIDDEN.equals(httpStatus)) {            //账号禁用
                return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_10011));
            } else if (HttpStatus.SERVICE_UNAVAILABLE.equals(httpStatus)) {  //验证错误
                return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_10012));
            }
            User user = SessionUtils.getUser();
            //确认是否是诊所在做切换
            if (!StringUtils.isNullOrEmpty(companyId)) {
                //多诊所登录
                user = changeClinics(user, companyId);
                Company company = companyService.get(companyId);
                SessionUtils.setLoginTenantId(companyId);
                SessionUtils.setLoginTenant(company);
            } else {
                //单诊所登录
                companyId = user.getCompanyId();
                SessionUtils.setLoginTenantId(companyId);
                SessionUtils.setLoginTenant(user.getCompany());
            }

            //更新用户，返回当前登陆人信息
            response = getLoginUserInfo(request, user, companyId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //把删除的修改回来
            if (!idList.isEmpty()) {
                for (String updateId : idList) {
                    userService.updateDelFlag(updateId, "0");
                }
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(response));
    }

//    @GetMapping("/getToken")
//    public ResponseEntity<?> Token(HttpServletRequest request) {
//        Map<String, Object> response = null;
//        List<String> idList = new ArrayList<>();
//        String companyId = null;
//        String loginName = "super";
//        String password = "123456";
//        try {
//            //登陆
//            HttpStatus httpStatus = loginService.authLogin(loginName, password, companyId);
//
//            if (HttpStatus.UNAUTHORIZED.equals(httpStatus)) {        //账号密码错误
//                return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_10010));
//            } else if (HttpStatus.FORBIDDEN.equals(httpStatus)) {            //账号禁用
//                return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_10011));
//            } else if (HttpStatus.SERVICE_UNAVAILABLE.equals(httpStatus)) {  //验证错误
//                return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_10012));
//            }
//            User user = SessionUtils.getUser();
//            //确认是否是诊所在做切换
//            if (!StringUtils.isNullOrEmpty(companyId)) {
//                //多诊所登录
//                user = changeClinics(user, companyId);
//                Company company = companyService.get(companyId);
//                SessionUtils.setLoginTenantId(companyId);
//                SessionUtils.setLoginTenant(company);
//            } else {
//                //单诊所登录
//                companyId = user.getCompanyId();
//                SessionUtils.setLoginTenantId(companyId);
//                SessionUtils.setLoginTenant(user.getCompany());
//            }
//
//            //更新用户，返回当前登陆人信息
//            response = LoginUserInfo(request, user, companyId);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            //把删除的修改回来
//            if (!idList.isEmpty()) {
//                for (String updateId : idList) {
//                    userService.updateDelFlag(updateId, "0");
//                }
//            }
//        }
//        return ResponseEntity.ok(ResultUtil.successJson(response));
//    }

    @PostMapping("/loginedtocken")
    public ResponseEntity<?> getLoginedToken(HttpServletRequest request) {
        // 是否登录过
        HttpStatus httpStatus = loginService.authLogin();

        if (!HttpStatus.OK.equals(httpStatus)) {
            return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_20011));
        }
        User user = SessionUtils.getUser();
        String loginTenantId = SessionUtils.getLoginTenantId();
        Map<String, Object> response = getLoginUserInfo(request, user, loginTenantId);
        return ResponseEntity.ok(ResultUtil.successJson(response));
    }


    /**
     * 登出
     *
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<JSONObject> logout() {
        loginService.logout();
        return ResponseEntity.ok(ResultUtil.successJson("退出成功"));
    }

    /**
     * 获取所有用户登录状态
     *
     * @return
     */
    @GetMapping("/userLoginStatus")
    public List<User> getUserLoginStatus() {
        //TODO：查询需要检查登录状态的用户列表
        List<User> userList = loginService.loginUser();

        //处理所有Session，对应到每个用户
        DefaultWebSecurityManager securittyManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        StatelessSessionManager sessionManager = (StatelessSessionManager) securittyManager.getSessionManager();
        Collection<Session> sessionList = sessionManager.getActiveSessions();//所有当前登录Session
        Map<String, Session> userSessionCache = new HashMap<>();
        for (Session s : sessionList) {
            //java.util.Date startTime = s.getStartTimestamp();
            //java.util.Date lastAccessTime = s.getLastAccessTime();
            JSONObject userJson = SessionUtils.getUserJson(s);
            if (userJson == null) {
                continue;
            }
            User user = userJson.toJavaObject(User.class);
            if (userSessionCache.containsKey(user.getId())) {
                Session cachedSession = userSessionCache.get(user.getId());
                //同一用户可能存在两个Session的情况，取最后访问的Session
                if (s.getLastAccessTime().compareTo(cachedSession.getLastAccessTime()) > 0) {
                    userSessionCache.replace(user.getId(), s);
                }
            } else {
                userSessionCache.put(user.getId(), s);
            }
        }

        //遍历用户，根据上面处理后的Session更新其登录状态和登录时长
        java.util.Date now = new java.util.Date();
        for (User u : userList) {
            boolean isLogined = userSessionCache.containsKey(u.getId());//存在Session则视为已登录
            Long loginedSeconds = 0L;//已登录秒数
            if (isLogined) {
                Session session = userSessionCache.get(u.getId());
                java.util.Date startTime = session.getStartTimestamp();//session开始时间，即登录时间
                loginedSeconds = (now.getTime() - startTime.getTime()) / 1000;
            }
            //TODO:将isLogined、loginedSeconds赋值到用户登录信息，返回给页面
            String days = secondToTime(loginedSeconds);
            if (isLogined){
                u.setStatus("已登录");
                u.setLoginDuration(days);
            }else {
                u.setStatus("未登录");
                u.setLoginDuration("--");
            }
        }
        //TODO：返回值另外用对象封装
        return userList;
    }
    /**
     * 返回日时分秒
     * @param second
     * @return
     */
    private String secondToTime(long second) {

        long days = second / 86400;//转换天数
        second = second % 86400;//剩余秒数

        long hours = second / 3600;//转换小时数
        second = second % 3600;//剩余秒数

        long minutes = second / 60;//转换分钟
        second = second % 60;//剩余秒数

        if (0 < days){
            return days + "天"+hours+"时"+minutes+"分";
        }else {
            // return hours+"小时"+minutes+"分，"+second+"秒";
            return hours+"时"+minutes+"分";
        }

    }

    private Map<String, Object> getLoginUserInfo(HttpServletRequest request, User user, String companyId) {
        // 用户访问资源权限
        List<String> permissionList = permissionService.listResourcePermissionByUser(user.getId(), companyId);
        SessionUtils.setUserPermission(permissionList);

        // 用户路由权限
        List<Router> listRouter = permissionService.listRouterPermission(user.getId(), companyId);

        // 用户数据权限
        List<DataPermission> listDataPermission = permissionService.listDataPermissionByUserId(user.getId(), companyId);

        // 获取主题
        PersonalTheme personalTheme = getPersonalTheme(user.getId());

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("userId", user.getId().toString());
        response.put("username", user.getName());
        response.put("loginname", user.getLoginName());
        response.put("company", user.getCompany());
        response.put("department", user.getDepartment());
        response.put("User", user);
        response.put("routers", listRouter);
        response.put("dataPermisions", listDataPermission);
        response.put("personalTheme", personalTheme);
        JSONObject json = new JSONObject();
        json.put("host", request.getRemoteHost());
        json.put("userId", user.getId());
        json.put("sessionId", SecurityUtils.getSubject().getSession().getId().toString());
        String token = jwtUtils.createJWT(json.toJSONString());
        response.put("token", token);
        logger.info("User's token = {}", token);
        return response;
    }

    private Map<String, Object> LoginUserInfo(HttpServletRequest request, User user, String companyId) {
        // 用户访问资源权限
        List<String> permissionList = permissionService.listResourcePermissionByUser(user.getId(), companyId);
        SessionUtils.setUserPermission(permissionList);

        Map<String, Object> response = new HashMap<String, Object>();

        JSONObject json = new JSONObject();
        json.put("host", request.getRemoteHost());
        json.put("userId", user.getId());
        json.put("sessionId", SecurityUtils.getSubject().getSession().getId().toString());
        String token = jwtUtils.createJWT(json.toJSONString());
        response.put("token", token);
        logger.info("User's token = {}", token);
        return response;
    }



    /**
     * 切换诊所
     *
     * @param user
     * @param wantCompanyId
     * @return
     */
    private User changeClinics(User user, String wantCompanyId) {
        String nowCompanyId = user.getCompanyId();
        if (!wantCompanyId.equals(nowCompanyId)) {
            Company company = companyService.get(wantCompanyId);
            user.setCompany(company);
        }
        return user;
    }


    private PersonalTheme getPersonalTheme(String userId) {
        // 获取个性化主题
        List<Parameter> parms = Lists.newArrayList();
        parms.add(new Parameter("user_id", "=", userId));
        List<PersonalTheme> list = personalThemeService.listAll(parms, null);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        // 系统默认主题
        PersonalTheme defaultTheme = new PersonalTheme();
        defaultTheme.setUserId(Long.parseUnsignedLong(userId));
        List<Theme> themes = themeService.listAll(Lists.newArrayList(), null);
        if (themes != null && themes.size() > 0) {
            for (Theme t : themes) {
                if ("1".equals(t.getIsDefault())) {
                    defaultTheme.setTheme(t.getTheme());
                    return defaultTheme;
                }
            }
            // 未设置默认主题时，第一个作为默认主题
            defaultTheme.setTheme(themes.get(0).getTheme());
        }
        return defaultTheme;
    }

    @PostMapping("/wxToken")
    public ResponseEntity<?> getWxToken(HttpServletRequest request, @RequestBody User user1) {
        Map<String, Object> response = null;
        String loginName = user1.getLoginName();
        String password = user1.getLoginPassword();
        String companyId = null;
        List<String> idList = new ArrayList<>();
        try {
            //登陆
            HttpStatus httpStatus = loginService.authLogin(loginName, password, companyId);

            if (HttpStatus.UNAUTHORIZED.equals(httpStatus)) {        //账号密码错误
                return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_10010));
            } else if (HttpStatus.FORBIDDEN.equals(httpStatus)) {            //账号禁用
                return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_10011));
            } else if (HttpStatus.SERVICE_UNAVAILABLE.equals(httpStatus)) {  //验证错误
                return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_10012));
            }
            User user = SessionUtils.getUser();
            //确认是否是诊所在做切换
            if (!StringUtils.isNullOrEmpty(companyId)) {
                //多诊所登录
                user = changeClinics(user, companyId);
                Company company = companyService.get(companyId);
                SessionUtils.setLoginTenantId(companyId);
                SessionUtils.setLoginTenant(company);
            } else {
                //单诊所登录
                companyId = user.getCompanyId();
                SessionUtils.setLoginTenantId(companyId);
                SessionUtils.setLoginTenant(user.getCompany());
            }

            //更新用户，返回当前登陆人信息
            response = getLoginUserInfo(request, user, companyId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //把删除的修改回来
            if (!idList.isEmpty()) {
                for (String updateId : idList) {
                    userService.updateDelFlag(updateId, "0");
                }
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(response));
    }
}
