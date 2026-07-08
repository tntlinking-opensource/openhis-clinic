import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/admin/role')

// 导出标准CRUD接口（保持向后兼容）
export const getRoleById = baseApi.getById
export const listRolePage = baseApi.listPage
export const listRoleAll = baseApi.listAll
export const saveRole = baseApi.save
export const deleteRole = baseApi.delete
export const bulkInsertRole = baseApi.bulkInsert
export const bulkUpdateRole = baseApi.bulkUpdate
export const bulkDeleteRole = baseApi.bulkDelete
