import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/org/company')

// 导出标准CRUD接口（保持向后兼容）
export const getCompanyById = baseApi.getById
export const listCompanyPage = baseApi.listPage
export const listCompanyAll = baseApi.listAll
export const saveCompany = (data) =>
    request({
        url: '/org/company/saveWithFile',
        method: 'post',
        data
    })
export const deleteCompany = baseApi.delete
export const bulkInsertCompany = baseApi.bulkInsert
export const bulkUpdateCompany = baseApi.bulkUpdate
export const bulkDeleteCompany = baseApi.bulkDelete

// 自定义接口
export const getCompanys = (id) =>
    request({
        url: '/org/company/getCompanys',
        method: 'post',
        data: id
    })

export const treeCompany = (search) =>
    request({
        url: '/org/company/tree',
        method: 'post',
        data: search
    })
