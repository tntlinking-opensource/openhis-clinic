import request from '@/utils/request'

export const getResourceById = (id) =>
    request({
        url: '/admin/resource/' + id,
        method: 'get'
    })

export const listResourcePage = (search) =>
    request({
        url: '/admin/resource/list',
        method: 'post',
        data: search
    })

export const listResourceAll = (search) =>
    request({
        url: '/admin/resource/listAll',
        method: 'post',
        data: search
    })


export const saveResource = (resource) => 
    request({
        url: '/admin/resource/save',
        method: 'post',
        data: resource
    })

export const deleteResource = (resource) =>
    request({
        url: '/admin/resource/delete',
        method: 'post',
        data: resource
    })
    
export const bulkInsertResource = (resources) =>
    request({
        url: '/admin/resource/bulkInsert',
        method: 'post',
        data: resources
    })
    
export const bulkUpdateResource = (resources) =>
    request({
        url: '/admin/resource/bulkUpdate',
        method: 'post',
        data: resources
    })

export const bulkDeleteResource = (resources) =>
    request({
        url: '/admin/resource/bulkDelete',
        method: 'post',
        data: resources
    })
    
