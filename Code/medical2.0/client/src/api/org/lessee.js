import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/org/lessee')

export const getLesseeById = baseApi.getById
export const listLesseePage = baseApi.listPage
export const listLesseeAll = baseApi.listAll
export const saveLessee = baseApi.save
export const deleteLessee = baseApi.delete
export const bulkInsertLessee = baseApi.bulkInsert
export const bulkUpdateLessee = baseApi.bulkUpdate
export const bulkDeleteLessee = baseApi.bulkDelete
