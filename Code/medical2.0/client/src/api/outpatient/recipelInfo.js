import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/outpatient/recipelInfo')

// 导出标准CRUD接口（保持向后兼容）
export const getRecipelInfoById = baseApi.getById
export const listRecipelInfoPage = baseApi.listPage
export const listRecipelInfoAll = baseApi.listAll
export const saveRecipelInfo = baseApi.save
export const deleteRecipelInfo = baseApi.delete
export const bulkInsertRecipelInfo = baseApi.bulkInsert
export const bulkUpdateRecipelInfo = baseApi.bulkUpdate
export const bulkDeleteRecipelInfo = baseApi.bulkDelete

// 自定义接口
export const invalidStatus = (id) =>
    request({
        url: `/outpatient/recipelInfo/invalid?id=${id}`,
        method: 'get'
    })

export const updateNotShowById = (id) =>
    request({
        url: `/outpatient/recipelInfo/update/notShow?id=${id}`,
        method: 'get'
    })
