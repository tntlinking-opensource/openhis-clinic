import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/sys/codeRule')

export const getCodeRuleById = baseApi.getById
export const listCodeRulePage = baseApi.listPage
export const listCodeRuleAll = baseApi.listAll
export const saveCodeRule = baseApi.save
export const deleteCodeRule = baseApi.delete
export const bulkInsertCodeRule = baseApi.bulkInsert
export const bulkUpdateCodeRule = baseApi.bulkUpdate
export const bulkDeleteCodeRule = baseApi.bulkDelete
