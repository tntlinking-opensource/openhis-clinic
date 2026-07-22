import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/outpatient/recipelDetail')

// 导出标准CRUD接口（保持向后兼容）
export const getRecipelDetailById = baseApi.getById
export const listRecipelDetailPage = baseApi.listPage
export const listRecipelDetailAll = baseApi.listAll
export const saveRecipelDetail = baseApi.save
export const deleteRecipelDetail = baseApi.delete
export const bulkInsertRecipelDetail = baseApi.bulkInsert
export const bulkUpdateRecipelDetail = baseApi.bulkUpdate
export const bulkDeleteRecipelDetail = baseApi.bulkDelete

// 自定义接口
export const getByRecipelInfoId = (recipelInfoId) =>
    request({
        url: '/outpatient/recipelDetail/getByRecipelInfoId/' + recipelInfoId,
        method: 'get'
    })
