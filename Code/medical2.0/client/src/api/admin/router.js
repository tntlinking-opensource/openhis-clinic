import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/admin/router')

// 导出标准CRUD接口（保持向后兼容）
export const getRouterById = baseApi.getById
export const listRouterPage = baseApi.listPage
export const listRouterAll = baseApi.listAll
export const saveRouter = baseApi.save
export const deleteRouter = baseApi.delete
export const bulkInsertRouter = baseApi.bulkInsert
export const bulkUpdateRouter = baseApi.bulkUpdate
export const bulkDeleteRouter = baseApi.bulkDelete

// 自定义接口
export const treeRouter = (search) =>
    request({
        url: '/admin/router/tree',
        method: 'post',
        data: search
    })

export const getUserIndateWarning = (userId) =>
    request({
        url: '/admin/router/getUserIndateWarning/' + userId,
        method: 'get'
    })
