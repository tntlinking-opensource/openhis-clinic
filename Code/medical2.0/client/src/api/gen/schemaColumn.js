import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/gen/schemaColumn')

// 导出标准CRUD接口（保持向后兼容）
export const getSchemaColumnById = baseApi.getById
export const listSchemaColumnPage = baseApi.listPage
export const listSchemaColumnAll = baseApi.listAll
export const saveSchemaColumn = baseApi.save
export const deleteSchemaColumn = baseApi.delete
