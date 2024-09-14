import request from '@/utils/request'

export const getSupplierStockById = (id) =>
    request({
        url: '/stock/supplierStock/' + id,
        method: 'get'
    })
export const getSupplierStockBySid = (sid) =>
    request({
        url: '/stock/supplierStock/stock/' + sid,
        method: 'get'
    })
export const listSupplierStockPage = (search) =>
    request({
        url: '/stock/supplierStock/list',
        method: 'post',
        data: search
    })

export const listSupplierStockAll = (search) =>
    request({
        url: '/stock/supplierStock/listAll',
        method: 'post',
        data: search
    })


export const saveSupplierStock = (supplierStock) =>
    request({
        url: '/stock/supplierStock/save',
        method: 'post',
        data: supplierStock
    })

export const deleteSupplierStock = (supplierStock) =>
    request({
        url: '/stock/supplierStock/delete',
        method: 'post',
        data: supplierStock
    })

export const bulkInsertSupplierStock = (supplierStocks) =>
    request({
        url: '/stock/supplierStock/bulkInsert',
        method: 'post',
        data: supplierStocks
    })

export const bulkUpdateSupplierStock = (supplierStocks) =>
    request({
        url: '/stock/supplierStock/bulkUpdate',
        method: 'post',
        data: supplierStocks
    })

export const bulkDeleteSupplierStock = (supplierStocks) =>
    request({
        url: '/stock/supplierStock/bulkDelete',
        method: 'post',
        data: supplierStocks
    })

    export const saveSupplierStockList = (storageEvt) =>
    request({
        url: '/stock/supplierStock/inStorage',
        method: 'post',
        data: storageEvt
    })

    export const updateStockStockList = (storageEvt) =>
    request({
        url: '/stock/supplierStock/updateStock',
        method: 'post',
        data: storageEvt
    })

    export const saveSupplierStockListV1 = (storageEvt) =>
        request({
            url: '/stock/supplierStock/inStorageByCompany',
            method: 'post',
            data: storageEvt
        })
