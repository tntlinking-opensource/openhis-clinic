import request from '@/utils/request'

export const listAll = (search,isPre) =>
    request({
        url: '/stock/medicinalStockControl/listAll',
        method: 'post',
      data: {
        ...search,
        isPre
      }
    })


