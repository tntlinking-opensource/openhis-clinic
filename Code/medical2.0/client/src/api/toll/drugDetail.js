import request from '@/utils/request'

export const getTollDetailById = (id) =>
    request({
        url: '/toll/tollDetail/' + id,
        method: 'get'
    })

export const listTollDetailPage = (search) =>
    request({
        url: '/toll/tollDetail/list',
        method: 'post',
        data: search
    })

export const listTollDetailAll = (search) =>
    request({
        url: '/toll/tollDetail/listAll',
        method: 'post',
        data: search
    })


export const saveTollDetail = (tollDetail) => 
    request({
        url: '/toll/tollDetail/save',
        method: 'post',
        data: tollDetail
    })
  
export const deleteTollDetail = (tollDetail) =>
    request({
        url: '/toll/tollDetail/delete',
        method: 'post',
        data: tollDetail
    })
    
export const bulkInsertTollDetail = (tollDetails) =>
    request({
        url: '/toll/tollDetail/bulkInsert',
        method: 'post',
        data: tollDetails
    })
    
export const bulkUpdateTollDetail = (tollDetails) =>
    request({
        url: '/toll/tollDetail/bulkUpdate',
        method: 'post',
        data: tollDetails
    })

export const bulkDeleteTollDetail = (tollDetails) =>
    request({
        url: '/toll/tollDetail/bulkDelete',
        method: 'post',
        data: tollDetails
    })
export const getCreateBy = (tollDetails) =>
    request({
        url: '/toll/tollInfo/getCreateBy',
        method: 'post',
        data: tollDetails
    })
export const getDrugSales = (drugDetails) =>
    request({
        url: '/toll/tollInfo/getDrugSales',
        method: 'post',
        data: drugDetails
    })
export const getDrugSalesStat = (drugDetails) =>
    request({
        url: '/toll/tollInfo/getDrugSalesStat',
        method: 'post',
        data: drugDetails
    })
export const exportExcel = (tollDetails) =>
    request({
        url: '/toll/tollInfo/exportExcel',
        method: 'post',
        data: tollDetails,
        responseType:'blob'
    })
export const getdrugmarketstatistics = (drugDetails) =>
    request({
        url: '/toll/tollInfo/getdrugmarketstatistics',
        method: 'post',
        data: drugDetails
    })
export const getdrugmarketstatisticsStat = (drugDetails) =>
    request({
        url: '/toll/tollInfo/getdrugmarketstatisticsStat',
        method: 'post',
        data: drugDetails
    })
    
