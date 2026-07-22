import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/org/administrativeDivision')

export const getAdministrativeDivisionById = baseApi.getById
export const listAdministrativeDivisionPage = baseApi.listPage
export const listAdministrativeDivisionAll = baseApi.listAll
export const saveAdministrativeDivision = baseApi.save
export const deleteAdministrativeDivision = baseApi.delete
export const bulkInsertAdministrativeDivision = baseApi.bulkInsert
export const bulkUpdateAdministrativeDivision = baseApi.bulkUpdate
export const bulkDeleteAdministrativeDivision = baseApi.bulkDelete
