import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/stock/inventoryVerification')

// 导出标准CRUD接口（保持向后兼容）
export const getInventoryVerificationById = baseApi.getById
export const listInventoryVerificationPage = baseApi.listPage
export const listInventoryVerificationAll = baseApi.listAll
export const deleteInventoryVerification = baseApi.delete
export const bulkInsertInventoryVerification = baseApi.bulkInsert
export const bulkUpdateInventoryVerification = baseApi.bulkUpdate
export const bulkDeleteInventoryVerification = baseApi.bulkDelete

// 自定义接口
export const saveInventoryVerification = (type, variety) =>
    request({
        url: '/stock/inventoryVerification/save',
        method: 'post',
        params: { type, variety }
    })

export const accomplishInventoryVerification = (inventoryVerifications) =>
    request({
        url: '/stock/inventoryVerification/accomplishInventoryVerification',
        method: 'post',
        data: inventoryVerifications
    })
