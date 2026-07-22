package com.geeke.admin.service;

import com.geeke.admin.dao.UserDao;
import com.geeke.admin.dao.UserExtDao;
import com.geeke.admin.dao.UserRoleDao;
import com.geeke.admin.entity.User;
import com.geeke.admin.entity.UserExt;
import com.geeke.admin.entity.UserRole;
import com.geeke.common.constants.ActionConstants;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.common.service.ServiceException;
import com.geeke.medicareutils.config.MedicareConfigProperties;
import com.geeke.org.entity.Clinic;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
import com.geeke.sys.entity.Action;
import com.geeke.sys.entity.ActionRecycle;
import com.geeke.sys.entity.SysFile;
import com.geeke.sys.service.SysFileService;
import com.geeke.utils.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.crypto.hash.Md5Hash;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户管理Service
 * @author lys
 * @version 2021-08-25
 */
 
@Service("userService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService extends CrudService<UserDao, User>{

    private final UserRoleDao userRoleDao;

    private final UserDao userDao;

    private final SysFileService sysFileService;

    private final UserExtService userExtService;

    private final CompanyService companyService;

    private final UserExtDao userExtDao;

    private final MedicareConfigProperties medicareConfigProperties;

    @Override
    public User get(String id) {
        User user = super.get(id);

        /*获取子表列表   用户角色*/
        PageRequest pageRequest = buildPageRequest(com.google.common.collect.ImmutableMap.of(
                "user_id", user.getId(),
                "company_id", SessionUtils.getLoginTenantId()
        ));
        user.setUserRoleList(userRoleDao.listAll(pageRequest));
        UserExt userExt = userExtDao.getUserExtByUserId(user.getId(),SessionUtils.getLoginTenantId());
        user.setUserExt(userExt);
        return user;
    }

    public User CheckNewUserByPhone(String phone) {
        User user = userDao.getByPhone(phone);
        //已经在系统中存在
        if(null != user)
        {
            List<Company> clinicsByLoginName = companyService.getClinicsByLoginName(phone);
            List<String> oldCompanyId = clinicsByLoginName.stream().map(c -> c.getId()).collect(Collectors.toList());
            String newCompanyId = SessionUtils.getLoginTenantId();
          if(oldCompanyId.contains(newCompanyId))
          {
              //同诊所的相同用户报错
              throw new ServiceException("该用户已在本诊所注册。");
          }
        }
        //不存在则返回null
        return user;
    }


    public void updateAdminPhoneAndPass(String loginName,String pass,String userId)
    {
         userDao.updateAdminPhoneAndPass(loginName,pass,userId);
    }
    @Transactional(readOnly = false)
    public String updatePersonAndPass(User userDetail)
    {
        boolean loginPasswordUpdate = userDetail.getLoginPasswordUpdate();
        User user = SessionUtils.getUser();
        String currentUserId = user.getId();
        User dto = this.get(currentUserId);
        dto.setName(userDetail.getName());
        dto.setLoginName(userDetail.getLoginName());
        dto.setLoginPassword(userDetail.getLoginPassword());
        dto.setLoginPasswordUpdate(loginPasswordUpdate);
        String id = this.save(dto).getId();
        if(loginPasswordUpdate)
        {
            Md5Hash md5 = new Md5Hash(userDetail.getLoginPassword(), user.getId(), 10000);
            String md5Password = md5.toHex();
            this.dao.updateLoginPassword(id,md5Password);
        }
        return id;
    }

    public User getAdmin(String companyId)
    {
        return userDao.getAdmin(companyId);
    }

    @Transactional(readOnly = false)
    public User save(User user,MultipartFile[] fileIdUploads,
                     String[] deleteIds) throws java.io.IOException {
        // 用户登录名唯一检查
        User existUser = getUserByLoginName(user);
        String loginTenant = SessionUtils.getLoginTenantId();
        if(null != existUser)
        {
            List<String> oldCompanyId = existUser.getClinics();
            if(oldCompanyId.contains(loginTenant))
            {
                //同诊所的相同用户
                throw new ServiceException("已经存在一个相同的登录名。");
            }
            else
            {
                //不同诊所的相同用户

                //存入此人的诊所信息
                userDao.insertUserTenant(IdGen.uuid(),existUser.getId(),loginTenant,user.getLoginName());
                //存入此人的扩展信息
                UserExt userExt = user.getUserExt();
                userExt.setCompanyId(SessionUtils.getLoginTenantId());
                userExt.setId(IdGen.uuid());
                //此人如果存在，不在新增时修改照片
                userExtService.insert(userExt);
                //存入当前用户在诊所的角色信息
                saveUserRoleInfo(user);
            }
        }
        else
        {
            //非相同用户  存入人
            String id = super.save(user).getId();
            //存入此人的角色
            if (StringUtils.isNoneBlank(id)) {
                // 设置加密字段   密码
                if(user.getLoginPasswordUpdate()) {
                    Md5Hash md5 = new Md5Hash(user.getLoginPassword(), user.getId(), 10000);
                    String md5Password = md5.toHex();
                    dao.updateLoginPassword(user.getId(), md5Password);
                }

                saveUserRoleInfo(user);
            }

            //存入此人的诊所信息
            userDao.insertUserTenant(IdGen.uuid(),id,loginTenant,user.getLoginName());
            //存入此人的扩展数据
            UserExt userExt = user.getUserExt();
            userExt.setUserId(id);
            userExt.setCompanyId(loginTenant);
            //存入此人的照片
            List<SysFile> sysFiles = sysFileService.changeAndSaveSysFileList(fileIdUploads, id);
            if(0 == sysFiles.size())
            {
                logger.debug("用户未上传图片");
            }else
            {
                userExt.setPhotoId(sysFiles.get(0).getId());
            }
            // 根据附件ID删除附件信息
            if (null != deleteIds && deleteIds.length > 0) {
                sysFileService.delete(deleteIds);
            }
            userExtService.save(userExt);
        }
//        colMaps.clear();
//        colMaps.put("login_name", "loginName");
//        colMaps.put("company_id", "companyId");
//        if(exists(dao, user, colMaps)) {
//            throw new ServiceException("已经存在一个相同的登录名。");
//        }

        return user;
    }

    private void saveUserRoleInfo(User user) {
        /* 处理子表     用户角色 */
        PageRequest pageRequest = buildPageRequest(com.google.common.collect.ImmutableMap.of(
                "user_id", user.getId(),
                "company_id", SessionUtils.getLoginTenantId()
        ));
        List<UserRole> list_UserRole = userRoleDao.listAll(pageRequest);
        List<UserRole> deleteUserRoles = Lists.newArrayList(); // 删除列表
        List<UserRole> insertUserRoles = Lists.newArrayList(); // 添加列表
        List<UserRole> updateUserRoles = Lists.newArrayList(); // 更新列表
        for(UserRole userRoleSaved: list_UserRole) {
            boolean found = false;
            for (UserRole userRole : user.getUserRoleList()){
                if(userRoleSaved.getId().equals(userRole.getId())){
                    found = true;
                    break;
                }
            }
            if(!found) {
                deleteUserRoles.add(userRoleSaved);
            }
        }
        for (UserRole userRole : user.getUserRoleList()){

            if (StringUtils.isBlank(userRole.getId())) {
                userRole.setUser(user);
                userRole.preInsert();
                userRole.setCompany(SessionUtils.getLoginTenant());
                insertUserRoles.add(userRole);
            } else {
                userRole.preUpdate();
                updateUserRoles.add(userRole);
            }

        }
        if(deleteUserRoles.size() > 0) {
            userRoleDao.bulkDelete(deleteUserRoles);
        }
        if(updateUserRoles.size() > 0) {
            userRoleDao.bulkUpdate(updateUserRoles);
        }
        if(insertUserRoles.size() > 0) {
            userRoleDao.bulkInsert(insertUserRoles);
        }
    }

    private User getUserByLoginName(User user) {
        User existUser = userDao.getByLoginName(user.getLoginName());
        if(null != existUser)
        {
            List<String> clinics = userDao.getClinicIdByPhone(existUser.getId());
            existUser.setClinics(clinics);
        }
        return existUser;
    }

    @Transactional(readOnly = false)
    public User update(User user,MultipartFile[] fileIdUploads,
                       String[] deleteIds) throws java.io.IOException {
        String id = super.save(user).getId();
        if (StringUtils.isNoneBlank(id)) {
            // 设置加密字段   密码
            if(user.getLoginPasswordUpdate()) {
                if(medicareConfigProperties.getIsDemo().equals("true")){
                    throw new ServiceException("演示系统请勿修改密码！");
                }
                Md5Hash md5 = new Md5Hash(user.getLoginPassword(), user.getId(), 10000);
                String md5Password = md5.toHex();
                dao.updateLoginPassword(user.getId(), md5Password);
            }
            // 复用 saveUserRoleInfo 处理子表用户角色
            saveUserRoleInfo(user);

            UserExt userExt = user.getUserExt();
            //存入此人的照片
            List<SysFile> sysFiles = sysFileService.changeAndSaveSysFileList(fileIdUploads, id);
            if(0 != sysFiles.size())
            {
                userExt.setPhotoId(sysFiles.get(0).getId());
            }

            if (null != deleteIds && deleteIds.length > 0) {
                sysFileService.delete(deleteIds);
            }
            userExt.setUserId(user.getId());
            userExt.setCompanyId(SessionUtils.getLoginTenantId());
            userExtService.save(userExt);

        }
        return user;
    }
    /**
     * 删除
     * @param user
     */
    @Override
    @Transactional(readOnly = false)
    public int delete(User user) {
        /* 处理子表     用户角色、拓展信息、租户信息 */
        PageRequest pageRequest = buildPageRequest(com.google.common.collect.ImmutableMap.of(
                "user_id", user.getId(),
                "company_id", SessionUtils.getLoginTenantId()
        ));
        user.setUserRoleList(userRoleDao.listAll(pageRequest));        
        if(user.getUserRoleList() != null && user.getUserRoleList().size() > 0) {
        	userRoleDao.bulkDelete(user.getUserRoleList());
        }
        UserExt userExt = userExtDao.getUserExtByUserId(user.getId(), SessionUtils.getLoginTenantId());
        if(userExt!=null)
        {
            userExtDao.delete(userExt);
        }
        userDao.updateUserTenantDelFlag(user.getId(),"1");
        int rows = super.delete(user);
        return rows;
    }

    @Transactional(readOnly = false)
    public void delete(User user,int number) {
        /* 处理子表     用户角色、拓展信息、租户信息 */
        PageRequest pageRequest = buildPageRequest(com.google.common.collect.ImmutableMap.of(
                "user_id", user.getId(),
                "company_id", SessionUtils.getLoginTenantId()
        ));
        user.setUserRoleList(userRoleDao.listAll(pageRequest));
        if(user.getUserRoleList() != null && user.getUserRoleList().size() > 0) {
            userRoleDao.bulkDelete(user.getUserRoleList());
        }
        UserExt userExt = userExtDao.getUserExtByUserId(user.getId(), SessionUtils.getLoginTenantId());
        if(userExt!=null)
        {
            userExtDao.delete(userExt);
        }
        userDao.updateUserTenantDelFlag(user.getId(),"1");
        if(1 == number)
        {
            super.delete(user);
        }
    }


    /**
     * 修改 密码
     */
    @Transactional(readOnly = false)
    public int changeLoginPassword(String id, String pass) {
        // Md5密码 — 使用与 UserRealm 一致的迭代次数
        Md5Hash md5 = new Md5Hash(pass, id, 10000);
        String md5Password = md5.toHex();

        int rows = dao.updateLoginPassword(id, md5Password);
        return rows;
    }

    /**
     * 直接更新密码哈希值（用于密码迁移场景，传入已哈希的密码）
     * @param id 用户ID
     * @param hashedPassword 已哈希的密码（调用方负责哈希处理）
     */
    @Transactional(readOnly = false)
    public void updatePassword(String id, String hashedPassword) {
        dao.updateLoginPassword(id, hashedPassword);
    }

    /**
     * 生成操作日志
     * @param actionTypeId  操作类型Id
     * @param entity        操作的实体对象
     * @return
     */
    @Override
    protected Action createAction(String actionTypeId, User entity) {
        Action action = super.createAction(actionTypeId, entity);
        if(action == null) {
            return null;
        }
        // 删除时记录把保存的数据保存到回收站
        if(ActionConstants.ACTION_DELETED.equals(actionTypeId)) {   
            for(UserRole child: entity.getUserRoleList()) {
                ActionRecycle recycle = new ActionRecycle();
                recycle.setTableName(child.getBusTableName());
                recycle.setObjectId(child.getId());
                recycle.setObjectName((String)Reflections.invokeGetter(child, "name"));
                action.getActionRecycleList().add(recycle);            
            }
        }
        return action;
    }

    public List<User> getListByLoginName(String loginName) {
        return userDao.getListByLoginName(loginName);
    }

    public List<String> getUserIdByCinicId(String clinicId) {
        return userDao.getUserIdByClinicId(clinicId);
    }

    @Transactional(readOnly = false)
    public void updateDelFlag(String id, String delFalg) {
        userDao.updateDelFlag(id, delFalg);
    }

    public Page<User> listPageForNoAdmin(List<Parameter> parameters, int offset, int limit, String orderby) {

        String loginTenant = SessionUtils.getLoginTenantId();
        PageRequestClinic pageRequest = new PageRequestClinic(offset, limit, parameters, orderby,loginTenant);

        return paginate(
            () -> this.dao.countForNoAdmin(pageRequest),
            () -> this.dao.listPageForNoAdmin(pageRequest)
        );
    }

    public List<Clinic> getClinicByLoginName(String loginName)
    {
        return null;
    }

    public int countClinicIdByPhone(String phone)
    {
        return userDao.countClinicIdByPhone(phone);
    }

    /**
     * 根据诊所和职位选人下拉框
     * @param companyId
     * @param jobType
     * @return
     */
    public List<User> getUserByCompanyIdAndJob(String companyId, String jobType){
        return this.dao.getUserByCompanyIdAndJob(companyId,jobType);
    }

    @Transactional
    public void saveWxUser(User user){
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        this.dao.insert(user);
    }

    @Transactional(readOnly = true)
    public User getCompanyIdAndOpenId(String companyId, String openId) {
        User user = this.dao.getCompanyIdAndOpenId(companyId,openId);
        return user;
    }

    @Transactional
    public void updateWxUser(User user) {
        user.setUpdateDate(new Date());
        this.dao.updateWxUser(user);
    }

    public List<User> getUserByWxCompanyIdAndJob(String companyId, String office, String job) {
        List<User> users = this.dao.getUserByWxCompanyIdAndJob(companyId,office,job);
        return users;
    }
    public List<User> getUserByCompanyIdAndJobV2(String companyId, String jobType){
        return this.dao.getUserByCompanyIdAndJobV2(companyId,jobType);
    }
}