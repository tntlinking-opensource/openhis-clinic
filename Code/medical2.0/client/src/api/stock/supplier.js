import request from '@/utils/request'

export const getSupplierById = (id) =>
    request({
        url: '/stock/supplier/' + id,
        method: 'get'
    })

export const listSupplierPage = (search) =>
    request({
        url: '/stock/supplier/list',
        method: 'post',
        data: search
    })

export const listSupplierAll = (search) =>
    request({
        url: '/stock/supplier/listAll',
        method: 'post',
        data: search
    })


export const saveSupplier = (supplier) => 
    request({
        url: '/stock/supplier/save',
        method: 'post',
        data: supplier
    })
  
export const deleteSupplier = (supplier) =>
    request({
        url: '/stock/supplier/delete',
        method: 'post',
        data: supplier
    })
    
export const bulkInsertSupplier = (suppliers) =>
    request({
        url: '/stock/supplier/bulkInsert',
        method: 'post',
        data: suppliers
    })
    
export const bulkUpdateSupplier = (suppliers) =>
    request({
        url: '/stock/supplier/bulkUpdate',
        method: 'post',
        data: suppliers
    })

export const bulkDeleteSupplier = (suppliers) =>
    request({
        url: '/stock/supplier/bulkDelete',
        method: 'post',
        data: suppliers
    })
    
