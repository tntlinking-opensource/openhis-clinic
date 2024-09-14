import request from '@/utils/request'

export const getSupplierStorageById = (id) =>
    request({
        url: '/stock/supplierStorage/' + id,
        method: 'get'
    })

export const listSupplierStoragePage = (search) =>
    request({
        url: '/stock/supplierStorage/list',
        method: 'post',
        data: search
    })

export const listSupplierStorageAll = (search) =>
    request({
        url: '/stock/supplierStorage/listAll',
        method: 'post',
        data: search
    })


export const saveSupplierStorage = (supplierStorage) =>
    request({
        url: '/stock/supplierStorage/save',
        method: 'post',
        data: supplierStorage
    })

export const deleteSupplierStorage = (supplierStorage) =>
    request({
        url: '/stock/supplierStorage/delete',
        method: 'post',
        data: supplierStorage
    })

export const bulkInsertSupplierStorage = (supplierStorages) =>
    request({
        url: '/stock/supplierStorage/bulkInsert',
        method: 'post',
        data: supplierStorages
    })

export const bulkUpdateSupplierStorage = (supplierStorages) =>
    request({
        url: '/stock/supplierStorage/bulkUpdate',
        method: 'post',
        data: supplierStorages
    })
export const bulkUpdateSupplierStorage1 = (supplierStorages) =>
    request({
        url: '/stock/supplierStorage/cancel',
        method: 'post',
        data: supplierStorages
    })

export const bulkDeleteSupplierStorage = (supplierStorages) =>
    request({
        url: '/stock/supplierStorage/bulkDelete',
        method: 'post',
        data: supplierStorages
    })

export const bulkInSupplierStorage = (supplierStorages) =>
    request({
        url: '/stock/supplierStock/auditStorage',
        method: 'post',
        data: supplierStorages
    })

export const listByCode = (search) =>
    request({
        url: '/stock/supplierStorage/listByCode',
        method: 'get',
        params: search
    })
