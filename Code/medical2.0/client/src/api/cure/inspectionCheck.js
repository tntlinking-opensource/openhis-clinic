import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/cure/inspectionCheck')

export const getInspectionCheckById = baseApi.getById
export const listInspectionCheckPage = baseApi.listPage
export const listInspectionCheckAll = baseApi.listAll
export const saveInspectionCheck = baseApi.save
export const deleteInspectionCheck = baseApi.delete
export const bulkInsertInspectionCheck = baseApi.bulkInsert
export const bulkUpdateInspectionCheck = baseApi.bulkUpdate
export const bulkDeleteInspectionCheck = baseApi.bulkDelete
