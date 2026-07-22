import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/gen/genTableColumn')

// 导出标准CRUD接口（保持向后兼容）
export const getGenTableColumnById = baseApi.getById
export const listGenTableColumnPage = baseApi.listPage
export const listGenTableColumnAll = baseApi.listAll
export const saveGenTableColumn = baseApi.save
export const deleteGenTableColumn = baseApi.delete
