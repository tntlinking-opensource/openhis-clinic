import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/sys/companyCodeRule')

export const getCompanyCodeRuleById = baseApi.getById
export const listCompanyCodeRulePage = baseApi.listPage
export const listCompanyCodeRuleAll = baseApi.listAll
export const saveCompanyCodeRule = baseApi.save
export const deleteCompanyCodeRule = baseApi.delete
