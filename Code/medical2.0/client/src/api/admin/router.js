import request from '@/utils/request'

export const getRouterById = (id) =>
    request({
        url: '/admin/router/' + id,
        method: 'get'
    })

export const listRouterPage = (search) =>
    request({
        url: '/admin/router/list',
        method: 'post',
        data: search
    })

export const listRouterAll = (search) =>
    request({
        url: '/admin/router/listAll',
        method: 'post',
        data: search
    })

export const treeRouter = (search) =>
    request({
        url: '/admin/router/tree',
        method: 'post',
        data: search
    })

export const saveRouter = (router) => 
    request({
        url: '/admin/router/save',
        method: 'post',
        data: router
    })

export const deleteRouter = (router) =>
    request({
        url: '/admin/router/delete',
        method: 'post',
        data: router
    })
    
export const bulkInsertRouter = (routers) =>
    request({
        url: '/admin/router/bulkInsert',
        method: 'post',
        data: routers
    })
    
export const bulkUpdateRouter = (routers) =>
    request({
        url: '/admin/router/bulkUpdate',
        method: 'post',
        data: routers
    })

export const bulkDeleteRouter = (routers) =>
    request({
        url: '/admin/router/bulkDelete',
        method: 'post',
        data: routers
    })
export const getUserIndateWarning = (userId)=>
    request({
        url:'/admin/router/getUserIndateWarning/'+userId,
        method:'get',
    })
    
