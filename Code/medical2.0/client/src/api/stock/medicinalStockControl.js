import request from '@/utils/request'

export const listAll = (search,ypType) =>
    request({
        url: '/stock/medicinalStockControl/listAll',
        method: 'post',
      data: {
        ...search,
        ypType
      }
    })


