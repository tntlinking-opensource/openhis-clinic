import request from '@/utils/request'

export const getInspectionCheckInfoById = (id) =>
    request({
        url: '/cure/inspectionCheckInfo/' + id,
        method: 'get'
    })
export const getInspectionCheckInfoByInspecId = (inspecId) =>
    request({
        url: '/cure/inspectionCheckInfo/info/' + inspecId,
        method: 'get'
    })

export const listInspectionCheckInfoPage = (search) =>
    request({
        url: '/cure/inspectionCheckInfo/list',
        method: 'post',
        data: search
    })

export const listInspectionCheckInfoAll = (search) =>
    request({
        url: '/cure/inspectionCheckInfo/listAll',
        method: 'post',
        data: search
    })


export const saveInspectionCheckInfo = (inspectionCheckInfo) => 
    request({
        url: '/cure/inspectionCheckInfo/save',
        method: 'post',
        data: inspectionCheckInfo
    })
  
export const deleteInspectionCheckInfo = (inspectionCheckInfo) =>
    request({
        url: '/cure/inspectionCheckInfo/delete',
        method: 'post',
        data: inspectionCheckInfo
    })
    
export const bulkInsertInspectionCheckInfo = (inspectionCheckInfos) =>
    request({
        url: '/cure/inspectionCheckInfo/bulkInsert',
        method: 'post',
        data: inspectionCheckInfos
    })
    
export const bulkUpdateInspectionCheckInfo = (inspectionCheckInfos) =>
    request({
        url: '/cure/inspectionCheckInfo/bulkUpdate',
        method: 'post',
        data: inspectionCheckInfos
    })

export const bulkDeleteInspectionCheckInfo = (inspectionCheckInfos) =>
    request({
        url: '/cure/inspectionCheckInfo/bulkDelete',
        method: 'post',
        data: inspectionCheckInfos
    })
    
