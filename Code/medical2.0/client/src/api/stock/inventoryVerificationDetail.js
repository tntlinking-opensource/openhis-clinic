import request from '@/utils/request'

export const getInventoryVerificationDetailById = (id) =>
    request({
        url: '/stock/inventoryVerificationDetail/' + id,
        method: 'get'
    })
export const getInventoryVerificationDetailByInventoryId = (search) =>
    request({
        url: '/stock/inventoryVerificationDetail/getInventoryVerificationDetailByInventoryId',
        method: 'post',
        data:search
    })

export const listInventoryVerificationDetailPage = (search,type) =>
    request({
        url: '/stock/inventoryVerificationDetail/list/'+type,
        method: 'post',
        data: search
    })

export const listInventoryVerificationDetailAll = (search) =>
    request({
        url: '/stock/inventoryVerificationDetail/listAll',
        method: 'post',
        data: search
    })


export const saveInventoryVerificationDetail = (inventoryVerificationDetail) => 
    request({
        url: '/stock/inventoryVerificationDetail/save',
        method: 'post',
        data: inventoryVerificationDetail
    })

export const saveAll = (saveInformation) => 
    request({
        url: '/stock/inventoryVerificationDetail/saveAll',
        method: 'post',
        data: saveInformation
    })

export const exportExcel = (information) => 
    request({
        url: '/stock/inventoryVerificationDetail/exportExcel',
        method: 'post',
        data: information,
        responseType: 'blob'
    })  

export const deleteInventoryVerificationDetail = (inventoryVerificationDetail) =>
    request({
        url: '/stock/inventoryVerificationDetail/delete',
        method: 'post',
        data: inventoryVerificationDetail
    })
    
export const bulkInsertInventoryVerificationDetail = (inventoryVerificationDetails) =>
    request({
        url: '/stock/inventoryVerificationDetail/bulkInsert',
        method: 'post',
        data: inventoryVerificationDetails
    })
    
export const bulkUpdateInventoryVerificationDetail = (inventoryVerificationDetails) =>
    request({
        url: '/stock/inventoryVerificationDetail/bulkUpdate',
        method: 'post',
        data: inventoryVerificationDetails
    })

export const bulkDeleteInventoryVerificationDetail = (inventoryVerificationDetails) =>
    request({
        url: '/stock/inventoryVerificationDetail/bulkDelete',
        method: 'post',
        data: inventoryVerificationDetails
    })
    
