package com.geeke.org.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.common.persistence.TreeDao;
import com.geeke.org.entity.Company;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 诊所/租户DAO接口
 * @author lys
 * @version 2022-05-25
 */
@Mapper
public interface CompanyDao extends TreeDao<Company> {

    /**分页查询租户*/
    List<Company> listTenantPage(PageRequest var1);

    int countTenant(PageRequest var1);

    /**下拉框查询租户*/
    List<Company> listAllTenant(PageRequest var1);

    /**分页查询诊所*/
    List<Company> listClinicPage(PageRequest var1);

    int countClinic(PageRequest var1);

    /**下拉框查询诊所*/
    List<Company> listAllClinic(PageRequest var1);

    /**根据登录名查询诊所*/
    List<Company> selectClinicsByLoginName(@Param("loginName") String loginName);

    List<Company> getCompanys(String id);

    String getInstitution(@Param("companyId") String id);
}