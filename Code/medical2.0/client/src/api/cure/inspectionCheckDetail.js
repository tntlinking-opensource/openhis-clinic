import request from '@/utils/request'

export const getInspectionCheckDetailById = (id) =>
    request({
        url: '/cure/inspectionCheckDetail/' + id,
        method: 'get'
    })

export const listInspectionCheckDetailPage = (search) =>
    request({
        url: '/cure/inspectionCheckDetail/list',
        method: 'post',
        data: search
    })

export const listInspectionCheckDetailAll = (search) =>
    request({
        url: '/cure/inspectionCheckDetail/listAll',
        method: 'post',
        data: search
    })


export const saveInspectionCheckDetail = (inspectionCheckDetail) => 
    request({
        url: '/cure/inspectionCheckDetail/save',
        method: 'post',
        data: inspectionCheckDetail
    })
  
export const deleteInspectionCheckDetail = (inspectionCheckDetail) =>
    request({
        url: '/cure/inspectionCheckDetail/delete',
        method: 'post',
        data: inspectionCheckDetail
    })
    
export const bulkInsertInspectionCheckDetail = (inspectionCheckDetails) =>
    request({
        url: '/cure/inspectionCheckDetail/bulkInsert',
        method: 'post',
        data: inspectionCheckDetails
    })
    
export const bulkUpdateInspectionCheckDetail = (inspectionCheckDetails) =>
    request({
        url: '/cure/inspectionCheckDetail/bulkUpdate',
        method: 'post',
        data: inspectionCheckDetails
    })

export const bulkDeleteInspectionCheckDetail = (inspectionCheckDetails) =>
    request({
        url: '/cure/inspectionCheckDetail/bulkDelete',
        method: 'post',
        data: inspectionCheckDetails
    })
    
