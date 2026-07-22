import request, { serviceLong } from '@/utils/request'

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
    serviceLong({
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
