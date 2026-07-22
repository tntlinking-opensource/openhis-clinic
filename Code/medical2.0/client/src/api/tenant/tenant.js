import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/org/tenant')

export const getTenantById = baseApi.getById
export const listTenantPage = baseApi.listPage
export const listTenantAll = baseApi.listAll
export const saveTenant = baseApi.save
export const deleteTenant = baseApi.delete
export const bulkInsertTenant = baseApi.bulkInsert
export const bulkUpdateTenant = baseApi.bulkUpdate
export const bulkDeleteTenant = baseApi.bulkDelete
