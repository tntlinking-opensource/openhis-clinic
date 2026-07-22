import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/stock/supplierOutbound')

// 导出标准CRUD接口（保持向后兼容）
export const getSupplierOutbound = baseApi.getById
export const listSupplierOutbound = baseApi.listPage
export const saveSupplierOutbound = (data) =>
    request({
        url: '/stock/supplierOutbound/saveEvt',
        method: 'post',
        data
    })

// 自定义接口
export const cancelSupplierOutbound = (id) =>
    request({
        url: '/stock/supplierOutbound/cancel/' + id,
        method: 'get'
    })

export const examineSupplierOutbound = (id) =>
    request({
        url: '/stock/supplierOutbound/examine/' + id,
        method: 'get'
    })
