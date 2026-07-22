import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/clinic/clinicVersion')

export const getClinicVersionById = baseApi.getById
export const listClinicVersionPage = baseApi.listPage
export const listClinicVersionAll = baseApi.listAll
export const saveClinicVersion = baseApi.save
export const deleteClinicVersion = baseApi.delete
export const bulkInsertClinicVersion = baseApi.bulkInsert
export const bulkUpdateClinicVersion = baseApi.bulkUpdate
export const bulkDeleteClinicVersion = baseApi.bulkDelete
