import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/sys/queryCondition')

export const getQueryConditionById = baseApi.getById
export const listQueryConditionPage = baseApi.listPage
export const listQueryConditionAll = baseApi.listAll
export const saveQueryCondition = baseApi.save
export const deleteQueryCondition = baseApi.delete
