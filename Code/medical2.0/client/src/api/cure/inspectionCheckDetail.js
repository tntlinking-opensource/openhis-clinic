import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/cure/inspectionCheckDetail')

export const getInspectionCheckDetailById = baseApi.getById
export const listInspectionCheckDetailPage = baseApi.listPage
export const listInspectionCheckDetailAll = baseApi.listAll
export const saveInspectionCheckDetail = baseApi.save
export const deleteInspectionCheckDetail = baseApi.delete
export const bulkInsertInspectionCheckDetail = baseApi.bulkInsert
export const bulkUpdateInspectionCheckDetail = baseApi.bulkUpdate
export const bulkDeleteInspectionCheckDetail = baseApi.bulkDelete
