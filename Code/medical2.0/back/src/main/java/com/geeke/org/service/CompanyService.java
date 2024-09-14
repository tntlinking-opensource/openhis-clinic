package com.geeke.org.service;

import com.geeke.admin.common.entity.DataPermission;
import com.geeke.admin.common.entity.Permission;
import com.geeke.admin.common.service.PermissionService;
import com.geeke.admin.entity.Role;
import com.geeke.admin.entity.Router;
import com.geeke.admin.service.RoleService;
import com.geeke.clinic.service.ClinicVersionService;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.TreeService;
import com.geeke.org.dao.CompanyDao;
import com.geeke.org.entity.Company;
import com.geeke.sys.service.SysFileService;
import com.geeke.utils.IdGen;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 公司管理Service
 * @author lys
 * @version 2022-05-25
 */
 
@Service("companyService")
@Transactional(readOnly = true)
public class CompanyService extends TreeService<CompanyDao, Company> {
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private ClinicVersionService clinicVersionService;

    @Transactional(readOnly = false)
    public Company saveTenant(Company company) {
        company.setSort(0);
        return save(company);
    }
    @Transactional(readOnly = false)
    public Company save(Company company,
                        MultipartFile[] fileIdUploads,
                        String[] deleteIds) throws java.io.IOException {
        if(StringUtils.isBlank(company.getFileId())) {
            company.setFileId(IdGen.uuid());
        }
        // 保存附件
        sysFileService.changeAndSaveSysFileList(fileIdUploads, company.getFileId());
        // 根据附件ID删除附件信息
        if (null != deleteIds && deleteIds.length > 0) {
            sysFileService.delete(deleteIds);
        }
        return save(company);
    }

    @Transactional(readOnly = false)
    public Role saveClinicAndRole(Company company)
    {
        if(StringUtils.isBlank(company.getFileId()))
        {
            company.setFileId(IdGen.uuid());
        }
        company.setSort(0);
        //保存更新诊所
        company.setParent(company.getLesseeId());
        Company c = save(company);
        doUpdate(c);
        //保存角色
        Role role = new Role();
        role.setName("admin");
        role.setCode("admin");
        role.setCompany(c);
        role.setIsLocked(0);
        String vid = company.getVersion().getId();
        String roleId = clinicVersionService.getRoleIdById(vid);
        role.setParentId(roleId);
        roleService.deleteRoleByCode("admin",company.getId());
        Role save = roleService.save(role);
        //保存权限关系
        savePermisson(company, save);
        return save;
    }

    private void savePermisson(Company company, Role save) {
        String versionRoleId = company.getVersion().getRoleId();
        List<DataPermission> dataPermissions = permissionService.listDataPermissionByRoleId(versionRoleId);
        List<String> resourcePermission = permissionService.listResourcePermission(versionRoleId);
        List<Router> routers = permissionService.listRouterPermissionByRole(versionRoleId);
        List<String> routerIds = routers.stream().map(r -> r.getId()).collect(Collectors.toList());
        Permission permission = new Permission();
        permission.setDataPermissions(dataPermissions);
        permission.setResourceIds(resourcePermission.toArray(new String[resourcePermission.size()]));
        permission.setRouterIds(routerIds.toArray(new String[routers.size()]));
        permissionService.savePermission(save.getId(),permission);
    }



    public Page<Company> listTenantPage(List<Parameter> parameters, int offset, int limit, String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        int total = this.dao.countTenant(pageRequest);
        List<Company> list = null;
        if (total > 0) {
            list = this.dao.listTenantPage(pageRequest);
        }

        return new Page((long)total, list);
    }

    public List<Company> listAllTenant(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = new PageRequest(parameters, orderby);
        return this.dao.listAllTenant(pageRequest);
    }

    public Page<Company> listClinicPage(List<Parameter> parameters, int offset, int limit, String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        int total = this.dao.countClinic(pageRequest);
        List<Company> list = null;
        if (total > 0) {
            list = this.dao.listClinicPage(pageRequest);
        }

        return new Page((long)total, list);
    }

    public List<Company> listAllClinic(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = new PageRequest(parameters, orderby);
        return this.dao.listAllClinic(pageRequest);
    }

    /**根据登录名查询诊所*/
    public List<Company> getClinicsByLoginName(String loginName)
    {
        return this.dao.selectClinicsByLoginName(loginName);
    }


    @Transactional
    public List<Company> getCompanys(String id) {
        return this.dao.getCompanys(id);
    }

    @Transactional
    public String getInstitution(String id) {
        return this.dao.getInstitution(id);
    }

    public List<Company> getSubordinateClinic(String companyId) {

        return this.dao.getCompanys(companyId);
    }
}