package com.geeke.admin.service;

import com.geeke.admin.dao.RoleDao;
import com.geeke.admin.entity.Role;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色管理Service
 * @author lys
 * @version 2022-05-24
 */
 
@Service("roleService")
@Transactional(readOnly = true)
public class RoleService extends CrudService<RoleDao, Role>{
    @Autowired
    private RoleDao roleDao;
    public int deleteRoleByCode(String roleCode,String tenantId){
      return roleDao.deleteRoleByRoleCode(roleCode,tenantId);
    }

    /**
     * 查询当前诊所所拥有的角色
     * @param tenantId
     * @return
     */
    public List<Role> listAllByTenantId(String tenantId)
    {
        return roleDao.listAllByTenantId(tenantId);
    }

}