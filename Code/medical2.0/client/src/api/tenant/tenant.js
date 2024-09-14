import request from '@/utils/request'

export const getTenantById = (id) =>
    request({
        url: '/org/tenant/' + id,
        method: 'get'
    })

export const listTenantPage = (search) =>
    request({
        url: '/org/tenant/list',
        method: 'post',
        data: search
    })

export const listTenantAll = (search) =>
    request({
        url: '/org/tenant/listAll',
        method: 'post',
        data: search
    })


export const saveTenant = (formData) =>
    request({
        url: '/org/tenant/save',
        method: 'post',
        data: formData
    })
  
export const deleteTenant = (Tenant) =>
    request({
        url: '/org/tenant/delete',
        method: 'post',
        data: Tenant
    })
    
export const bulkInsertTenant = (Tenants) =>
    request({
        url: '/org/tenant/bulkInsert',
        method: 'post',
        data: Tenants
    })
    
export const bulkUpdateTenant = (Tenants) =>
    request({
        url: '/org/tenant/bulkUpdate',
        method: 'post',
        data: Tenants
    })

export const bulkDeleteTenant = (Tenants) =>
    request({
        url: '/org/tenant/bulkDelete',
        method: 'post',
        data: Tenants
    })
    
