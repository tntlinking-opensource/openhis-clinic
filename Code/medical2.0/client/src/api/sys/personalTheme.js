import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/sys/personalTheme')

export const getPersonalThemeById = baseApi.getById
export const listPersonalThemePage = baseApi.listPage
export const listPersonalThemeAll = baseApi.listAll
export const savePersonalTheme = baseApi.save
export const deletePersonalTheme = baseApi.delete
