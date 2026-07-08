import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/sys/propertySet')

export const getPropertySetById = baseApi.getById
export const listPropertySetPage = baseApi.listPage
export const listPropertySetAll = baseApi.listAll
export const savePropertySet = baseApi.save
export const deletePropertySet = baseApi.delete
export const bulkInsertPropertySet = baseApi.bulkInsert
export const bulkUpdatePropertySet = baseApi.bulkUpdate
export const bulkDeletePropertySet = baseApi.bulkDelete
