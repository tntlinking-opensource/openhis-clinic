import request from '@/utils/request'

export const listResourcePermission = (routerId) =>
    request({
        url: '/permission/resources?routerId=' + routerId,
        method: 'get'
    })

export const treePermission = () =>
    request({
        url: '/permission/permissions',
        method: 'get'
    })

export const listResourcePermissionByRoleId = (roleId) =>
    request({
        url: '/permission/' + roleId + '/resourcePermissions',
        method: 'get'        
    })

export const savePermission = (roleId, permission) =>
    request({
        url: '/permission/' + roleId + '/savePermission',
        method: 'post',
        data: permission
    })

export const listDataPermissionByRoleId = (roleId) =>
    request({
        url: '/permission/' + roleId + '/dataPermissions',
        method: 'get'        
    })