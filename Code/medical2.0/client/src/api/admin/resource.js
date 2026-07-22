import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/admin/resource')

// 导出标准CRUD接口（保持向后兼容）
export const getResourceById = baseApi.getById
export const listResourcePage = baseApi.listPage
export const listResourceAll = baseApi.listAll
export const saveResource = baseApi.save
export const deleteResource = baseApi.delete
export const bulkInsertResource = baseApi.bulkInsert
export const bulkUpdateResource = baseApi.bulkUpdate
export const bulkDeleteResource = baseApi.bulkDelete
