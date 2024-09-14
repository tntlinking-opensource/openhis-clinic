package com.geeke.admin.dao;

import com.geeke.admin.entity.User;
import com.geeke.common.persistence.CrudDao;
import com.geeke.toll.entity.OutpatientLog;
import com.geeke.utils.PageRequestClinic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户管理DAO接口
 * @author lys
 * @version 2021-08-25
 */
@Mapper
public interface UserDao extends CrudDao<User> {
    /**
     * 修改 密码
     */
    public int updateLoginPassword(@Param("id")String id, @Param("pass")String pass);

    /** 获取需要监测登录状态用户 */
    List<User> loginUser();

    List<User> getListByLoginName(@Param("loginName") String loginName);

    void updateDelFlag(@Param("id") String id, @Param("delFalg") String delFalg);

    User getAdmin(String companyId);

    User getByPhone(String phone);

    List<User> listPageForNoAdmin(PageRequestClinic var1);

    List<User> getUserByCompanyIdAndJob(@Param("companyId")String companyId,@Param("jobType") String jobType);

    List<User> NEWgetUserByCompanyIdAndJob(@Param("companyId")String companyId,@Param("jobType") String jobType);

    int countForNoAdmin(PageRequestClinic var1);

    @Select("select tenant_id from user_tenant where user_id = #{userId} and del_flag != '1' ")
    List<String> getClinicId(@Param("userId") String userId);

    @Select("select tenant_id from user_tenant where login_name = #{phone} and del_flag != '1' ")
    List<String> getClinicIdByPhone(@Param("phone") String phone);

    @Select("select count(1) from user_tenant where login_name = #{phone} and del_flag != '1' ")
    int countClinicIdByPhone(@Param("phone") String phone);

    @Select("select user_id from user_tenant where tenant_id = #{tenantId}")
    List<String> getUserIdByClinicId(@Param("tenantId") String tenantId);

    User getByLoginName(@Param("loginName") String loginName);

    void insertUserTenant(@Param("id") String id, @Param("userId") String userId,
                          @Param("tenantId") String tenantId,@Param("loginName") String loginName);

    void updateUserTenantDelFlag(@Param("userId") String userId, @Param("delFlag") String delFlag);

    void updateAdminPhoneAndPass(@Param("loginName") String loginName, @Param("pass") String pass, @Param("id") String id);

    User getCompanyIdAndOpenId(String companyId, String openId);

    void updateWxUser(User user);

    List<User> getUserByWxCompanyIdAndJob(@Param("companyId") String companyId,@Param("office") String office,@Param("job") String job);

}