import request from '@/utils/request'

export const getRoleById = (id) =>
    request({
        url: '/admin/role/' + id,
        method: 'get'
    })

export const listRolePage = (search) =>
    request({
        url: '/admin/role/list',
        method: 'post',
        data: search
    })

export const listRoleAll = (search) =>
    request({
        url: '/admin/role/listAll',
        method: 'post',
        data: search
    })


export const saveRole = (role) => 
    request({
        url: '/admin/role/save',
        method: 'post',
        data: role
    })

export const deleteRole = (role) =>
    request({
        url: '/admin/role/delete',
        method: 'post',
        data: role
    })
    
export const bulkInsertRole = (roles) =>
    request({
        url: '/admin/role/bulkInsert',
        method: 'post',
        data: roles
    })
    
export const bulkUpdateRole = (roles) =>
    request({
        url: '/admin/role/bulkUpdate',
        method: 'post',
        data: roles
    })

export const bulkDeleteRole = (roles) =>
    request({
        url: '/admin/role/bulkDelete',
        method: 'post',
        data: roles
    })
    
