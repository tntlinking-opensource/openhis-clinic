import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/gen/genTable')

// 导出标准CRUD接口（保持向后兼容）
export const getGenTableById = baseApi.getById
export const listGenTablePage = baseApi.listPage
export const listGenTableAll = baseApi.listAll
export const saveGenTable = baseApi.save
export const deleteGenTable = baseApi.delete
export const bulkInsertGenTable = baseApi.bulkInsert
export const bulkUpdateGenTable = baseApi.bulkUpdate
export const bulkDeleteGenTable = baseApi.bulkDelete

// 自定义接口
export const importGenTable = (genTable) =>
    request({
        url: '/gen/genTable/importJson',
        method: 'post',
        data: genTable
    })
