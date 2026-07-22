import request, { serviceLong } from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/stock/inventoryVerificationDetail')

export const getInventoryVerificationDetailById = baseApi.getById
export const listInventoryVerificationDetailAll = baseApi.listAll
export const saveInventoryVerificationDetail = baseApi.save
export const deleteInventoryVerificationDetail = baseApi.delete
export const bulkInsertInventoryVerificationDetail = baseApi.bulkInsert
export const bulkUpdateInventoryVerificationDetail = baseApi.bulkUpdate
export const bulkDeleteInventoryVerificationDetail = baseApi.bulkDelete

export const getInventoryVerificationDetailByInventoryId = (search) =>
    request({
        url: '/stock/inventoryVerificationDetail/getInventoryVerificationDetailByInventoryId',
        method: 'post',
        data: search
    })

export const listInventoryVerificationDetailPage = (search, type) =>
    request({
        url: '/stock/inventoryVerificationDetail/list/' + type,
        method: 'post',
        data: search
    })

export const saveAll = (saveInformation) =>
    request({
        url: '/stock/inventoryVerificationDetail/saveAll',
        method: 'post',
        data: saveInformation
    })

export const exportExcel = (information) =>
    serviceLong({
        url: '/stock/inventoryVerificationDetail/exportExcel',
        method: 'post',
        data: information,
        responseType: 'blob'
    })
