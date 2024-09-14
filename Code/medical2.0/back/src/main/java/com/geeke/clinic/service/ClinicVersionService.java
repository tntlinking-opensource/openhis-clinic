package com.geeke.clinic.service;

import com.geeke.admin.common.dao.PermissionDAO;
import com.geeke.admin.entity.Role;
import com.geeke.admin.service.RoleService;
import com.geeke.clinic.dao.ClinicVersionDao;
import com.geeke.clinic.entity.ClinicVersion;
import com.geeke.common.service.CrudService;
import com.geeke.org.entity.Company;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 诊所版本Service
 * @author txl
 * @version 2022-05-23
 */
 
@Service("clinicVersionService")
@Transactional(readOnly = false,rollbackFor = Exception.class)
public class ClinicVersionService extends CrudService<ClinicVersionDao, ClinicVersion>{

    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionDAO permissionDAO;
    @Autowired
    private ClinicVersionDao clinicVersionDao;

    public String getRoleIdById(String id)
    {
       return clinicVersionDao.getRoleIdById(id);
    }
    public ClinicVersion save(ClinicVersion entity) {
        Role role = new Role();
        role.setName(entity.getName());
        role.setCode(entity.getCode());
        role.setIsLocked(0);
        Company comp = new Company();
        comp.setId("0");
        role.setCompany(comp);
        if (StringUtils.isBlank(entity.getId())) {
            Role save = roleService.save(role);
            entity.setRoleId(save.getId());
            entity.preInsert();
            this.doInsert(entity);
            this.saveAction(this.createAction("created", entity));
        } else {
            role.setId(entity.getRoleId());
            roleService.save(role);
            entity.preUpdate();
            this.doUpdate(entity);
            this.saveAction(this.createAction("updated", entity));
        }

        return entity;
    }

    public int deleteClinic(ClinicVersion entity) {
        String roleId = entity.getRoleId();
        //删除诊所
        int num = this.delete(entity);
        if(num>0)
        {
            //删除权限和角色
            Role delete = new Role(roleId);
            roleService.delete(delete);
            removePermisson(roleId);
        }
        return num;
    }

    private void removePermisson(String roleId) {
        this.permissionDAO.removeRouterByRoleId(roleId);
        this.permissionDAO.removeResourceByRoleId(roleId);
        this.permissionDAO.removeDataPermssionByRoleId(roleId);
    }

    public int bulkDeleteClinic(List<ClinicVersion> entitys) {
        int num = this.bulkDelete(entitys);
        if(num>0)
        {
            entitys.stream().forEach(e->{
                String roleId = e.getRoleId();
                roleService.delete(new Role(roleId));
                removePermisson(roleId);
            });
        }
        return num;
    }

}