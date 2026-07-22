import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/sys/theme')

export const getThemeById = baseApi.getById
export const listThemePage = baseApi.listPage
export const listThemeAll = baseApi.listAll
export const saveTheme = baseApi.save
export const deleteTheme = baseApi.delete
export const bulkInsertTheme = baseApi.bulkInsert
export const bulkUpdateTheme = baseApi.bulkUpdate
export const bulkDeleteTheme = baseApi.bulkDelete
