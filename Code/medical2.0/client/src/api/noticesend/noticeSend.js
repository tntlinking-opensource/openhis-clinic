import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/noticesend/noticeSend')

// 导出标准CRUD接口（保持向后兼容）
export const getNoticeSendById = baseApi.getById
export const listNoticeSendPage = baseApi.listPage
export const listNoticeSendAll = baseApi.listAll
export const saveNoticeSend = baseApi.save
export const deleteNoticeSend = baseApi.delete
export const bulkInsertNoticeSend = baseApi.bulkInsert
export const bulkUpdateNoticeSend = baseApi.bulkUpdate
export const bulkDeleteNoticeSend = baseApi.bulkDelete

// 自定义接口
export const getCompanyTree = (code) =>
    request({
        url: '/noticesend/noticeSend/tree/' + code,
        method: 'get'
    })
