import request from '@/utils/request'

export const getDrug = (search) =>
    request({
        url: '/stock/currentInventory/getDrug',
        method: 'post',
        data:search
    })

export const getDrugSalesStat = (search) =>
    request({
        url: '/stock/currentInventory/getDrugSalesStat',
        method: 'post',
        data:search  
    })
export const getDrugSalesStatByNumber = (search) =>
    request({
        url: '/stock/currentInventory/getDrugSalesStatByNumber',
        method: 'post',
        data:search  
    })
export const getBatchNumberDrug = (search) =>
    request({
        url: '/stock/currentInventory/getBatchNumberDrug',
        method: 'post',
        data:search
    })
    export const getStuff = (search) =>
    request({
        url: '/stock/currentInventory/getStuff',
        method: 'post',
        data:search
    })

export const getStuffSalesStat = (search) =>
    request({
        url: '/stock/currentInventory/getStuffSalesStat',
        method: 'post',
        data:search
    })
export const getBatchNumberStuff = (search) =>
    request({
        url: '/stock/currentInventory/getBatchNumberStuff',
        method: 'post',
        data:search
    })
export const getStuffSalesStatNumber = (search) =>
    request({
        url: '/stock/currentInventory/getStuffSalesStatNumber',
        method: 'post',
        data:search  
    })
export const exportTable = (search) =>
    request({
        url: '/stock/currentInventory/exportTable',
        method: 'post',
        data:search,
        responseType: 'blob'
    })
    