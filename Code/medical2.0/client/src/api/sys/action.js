import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/sys/action')

// 导出标准CRUD接口（保持向后兼容）
export const getActionById = baseApi.getById
export const listActionPage = baseApi.listPage
export const listActionAll = baseApi.listAll
export const saveAction = baseApi.save
export const deleteAction = baseApi.delete
