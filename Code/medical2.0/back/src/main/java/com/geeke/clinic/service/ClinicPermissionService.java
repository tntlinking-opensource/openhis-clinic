package com.geeke.clinic.service;


import com.geeke.admin.common.dao.PermissionDAO;
import com.geeke.admin.common.entity.DataPermission;
import com.geeke.admin.common.entity.Permission;
import com.geeke.clinic.dao.ClinicPermissionDAO;
import com.geeke.utils.IdGen;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * 资源服务实现类
 *
 * @author lys
 * @date 2019/07/26
 */
@Service
public class ClinicPermissionService {

    @Autowired
    private ClinicPermissionDAO clinicPermissionDAO;
    @Autowired
    private PermissionDAO permissionDAO;

    /**
     * 保存版本的角色权限
     * @param roleId
     * @return
     */
    @Transactional
    public int savePermission(String roleId, Permission permission) {
        int rows = saveAllPermission(roleId, permission);

        //查询子类角色并更新权限
        List<String> ids = clinicPermissionDAO.queryChildNodesByPid(roleId);
        ids.stream().forEach(id -> saveAllPermission(id,permission));
        return rows;
    }

    private int saveAllPermission(String roleId, Permission permission) {
        String[] routerIds = permission.getRouterIds();
        String[] resourceIds = permission.getResourceIds();
        List<DataPermission> dataPermissions = permission.getDataPermissions();
        // 先删除后添加
        this.permissionDAO.removeRouterByRoleId(roleId);
        this.permissionDAO.removeResourceByRoleId(roleId);
        this.permissionDAO.removeDataPermssionByRoleId(roleId);

        int rows = 0;
        if(routerIds != null && routerIds.length > 0) {
            Map<String, String> map = Maps.newHashMap();
            for(String routerId: routerIds) {
                map.put(routerId, IdGen.uuid());
            }

            rows += this.permissionDAO.saveRouterPermission(roleId, map);
        }

        if(resourceIds != null && resourceIds.length > 0) {
            Map<String, String> map = Maps.newHashMap();
            for(String resourceId: resourceIds) {
                map.put(resourceId, IdGen.uuid());
            }

            rows += this.permissionDAO.saveResourcePermission(roleId, map);
        }

        if(dataPermissions != null && dataPermissions.size() > 0) {
            String[] ids = new String[dataPermissions.size()];
            for(int idx = 0; idx < dataPermissions.size(); idx++) {
                ids[idx] = IdGen.uuid();
            }

            rows += this.permissionDAO.saveDataPermission(dataPermissions, ids);
        }
        return rows;
    }
}
