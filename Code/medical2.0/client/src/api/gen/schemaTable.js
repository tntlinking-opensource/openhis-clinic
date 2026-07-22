import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/gen/schemaTable')

// 导出标准CRUD接口（保持向后兼容）
export const getSchemaTableById = baseApi.getById
export const listSchemaTablePage = baseApi.listPage
export const listSchemaTableAll = baseApi.listAll
export const saveSchemaTable = baseApi.save
export const deleteSchemaTable = baseApi.delete
