import request from '@/utils/request'

export const getInspectionCheckById = (id) =>
    request({
        url: '/cure/inspectionCheck/' + id,
        method: 'get'
    })

export const listInspectionCheckPage = (search) =>
    request({
        url: '/cure/inspectionCheck/list',
        method: 'post',
        data: search
    })

export const listInspectionCheckAll = (search) =>
    request({
        url: '/cure/inspectionCheck/listAll',
        method: 'post',
        data: search
    })


export const saveInspectionCheck = (inspectionCheck) => 
    request({
        url: '/cure/inspectionCheck/save',
        method: 'post',
        data: inspectionCheck
    })
  
export const deleteInspectionCheck = (inspectionCheck) =>
    request({
        url: '/cure/inspectionCheck/delete',
        method: 'post',
        data: inspectionCheck
    })
    
export const bulkInsertInspectionCheck = (inspectionChecks) =>
    request({
        url: '/cure/inspectionCheck/bulkInsert',
        method: 'post',
        data: inspectionChecks
    })
    
export const bulkUpdateInspectionCheck = (inspectionChecks) =>
    request({
        url: '/cure/inspectionCheck/bulkUpdate',
        method: 'post',
        data: inspectionChecks
    })

export const bulkDeleteInspectionCheck = (inspectionChecks) =>
    request({
        url: '/cure/inspectionCheck/bulkDelete',
        method: 'post',
        data: inspectionChecks
    })
    
