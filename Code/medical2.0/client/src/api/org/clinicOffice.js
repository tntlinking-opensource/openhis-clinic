import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/org/clinicOffice')

export const getClinicOfficeById = baseApi.getById
export const listClinicOfficePage = baseApi.listPage
export const listClinicOfficeAll = baseApi.listAll
export const saveClinicOffice = baseApi.save
export const deleteClinicOffice = baseApi.delete
export const bulkInsertClinicOffice = baseApi.bulkInsert
export const bulkUpdateClinicOffice = baseApi.bulkUpdate
export const bulkDeleteClinicOffice = baseApi.bulkDelete
