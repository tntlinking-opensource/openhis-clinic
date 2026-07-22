import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/gen/genScheme')

// 导出标准CRUD接口（保持向后兼容）
export const getGenSchemeById = baseApi.getById
export const listGenSchemePage = baseApi.listPage
export const listGenSchemeAll = baseApi.listAll
export const saveGenScheme = baseApi.save
export const deleteGenScheme = baseApi.delete
export const bulkInsertGenScheme = baseApi.bulkInsert
export const bulkUpdateGenScheme = baseApi.bulkUpdate
export const bulkDeleteGenScheme = baseApi.bulkDelete
