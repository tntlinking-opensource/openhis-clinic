import request from '@/utils/request'

export const listAll = (search) =>
    request({
        url: '/stock/medicinalStockControl/listAll',
        method: 'post',
        data:search
    })

