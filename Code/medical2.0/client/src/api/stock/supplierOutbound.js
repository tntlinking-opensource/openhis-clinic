import request from '@/utils/request'

export const getSupplierOutbound = (id) =>
    request({
        url: '/stock/supplierOutbound/' + id,
        method: 'get'
    })

export const listSupplierOutbound = (search) =>
    request({
        url: '/stock/supplierOutbound/list',
        method: 'post',
        data: search
    })


export const saveSupplierOutbound = (entity) =>
    request({
        url: '/stock/supplierOutbound/save' ,
        method: 'post',
        data:entity

    })

export const cancelSupplierOutbound = (id) =>
    request({
        url: '/stock/supplierOutbound/cancel/'+id,
        method: 'get'
    })

export const examineSupplierOutbound = (id) =>
    request({
        url: '/stock/supplierOutbound/examine/'+id,
        method: 'get'
    })
