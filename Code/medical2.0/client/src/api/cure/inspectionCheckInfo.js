import { createCrudApi } from '@/utils/apiFactory'
import request from '@/utils/request'

const baseApi = createCrudApi('/cure/inspectionCheckInfo')

// Re-export standard CRUD with original names for backward compatibility
export const getInspectionCheckInfoById = baseApi.getById
export const listInspectionCheckInfoPage = baseApi.listPage
export const listInspectionCheckInfoAll = baseApi.listAll
export const saveInspectionCheckInfo = (data) =>
    request({
        url: '/cure/inspectionCheckInfo/saveWithFile',
        method: 'post',
        data
    })
export const deleteInspectionCheckInfo = baseApi.delete
export const bulkInsertInspectionCheckInfo = baseApi.bulkInsert
export const bulkUpdateInspectionCheckInfo = baseApi.bulkUpdate
export const bulkDeleteInspectionCheckInfo = baseApi.bulkDelete

// Keep custom endpoints as-is
export const getInspectionCheckInfoByInspecId = (inspecId) =>
    request({
        url: '/cure/inspectionCheckInfo/info/' + inspecId,
        method: 'get'
    })
