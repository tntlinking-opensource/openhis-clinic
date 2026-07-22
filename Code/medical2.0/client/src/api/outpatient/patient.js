import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/outpatient/patient')

export const getPatientById = baseApi.getById
export const listPatientPage = baseApi.listPage
export const listPatientAll = baseApi.listAll
export const savePatient = baseApi.save
export const deletePatient = baseApi.delete
export const bulkInsertPatient = baseApi.bulkInsert
export const bulkUpdatePatient = baseApi.bulkUpdate
export const bulkDeletePatient = baseApi.bulkDelete
