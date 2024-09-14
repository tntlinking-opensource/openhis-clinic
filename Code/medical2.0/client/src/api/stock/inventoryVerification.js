import request from '@/utils/request'

export const getInventoryVerificationById = (id) =>
    request({
        url: '/stock/inventoryVerification/' + id,
        method: 'get'
    })

export const listInventoryVerificationPage = (search) =>
    request({
        url: '/stock/inventoryVerification/list',
        method: 'post',
        data: search
    })

export const listInventoryVerificationAll = (search) =>
    request({
        url: '/stock/inventoryVerification/listAll',
        method: 'post',
        data: search
    })


export const saveInventoryVerification = (type,variety) => 
    request({
        url: '/stock/inventoryVerification/save/'+type+"/"+variety,
        method: 'get',

    })
export const accomplishInventoryVerification = (inventoryVerifications) => 
    request({
        url: '/stock/inventoryVerification/accomplishInventoryVerification',
        method: 'post',
        data:inventoryVerifications
    })
  
export const deleteInventoryVerification = (inventoryVerification) =>
    request({
        url: '/stock/inventoryVerification/delete',
        method: 'post',
        data: inventoryVerification
    })
    
export const bulkInsertInventoryVerification = (inventoryVerifications) =>
    request({
        url: '/stock/inventoryVerification/bulkInsert',
        method: 'post',
        data: inventoryVerifications
    })
    
export const bulkUpdateInventoryVerification = (inventoryVerifications) =>
    request({
        url: '/stock/inventoryVerification/bulkUpdate',
        method: 'post',
        data: inventoryVerifications
    })

export const bulkDeleteInventoryVerification = (inventoryVerifications) =>
    request({
        url: '/stock/inventoryVerification/bulkDelete',
        method: 'post',
        data: inventoryVerifications
    })
    
