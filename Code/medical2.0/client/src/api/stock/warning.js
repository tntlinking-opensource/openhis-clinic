import request from '@/utils/request'

export const getDrugIndateWarning = (search) =>
    request({
        url: '/stock/warning/getDrugIndateWarning',
        method: 'post',
        data:search
    })
export const getDrugInventoryWarning = (search) =>
    request({
        url: '/stock/warning/getDrugInventoryWarning',
        method: 'post',
        data:search
    })
export const getStuffIndateWarning = (search) =>
    request({
        url: '/stock/warning/getStuffIndateWarning',
        method: 'post',
        data:search
    })
export const getStuffInventoryWarning = (search) =>
    request({
        url: '/stock/warning/getStuffInventoryWarning',
        method: 'post',
        data:search
    })
export const exportTable = (search) =>
    request({
        url: '/stock/warning/exportTable',
        method: 'post',
        data:search,
        responseType:"blob"
    })
