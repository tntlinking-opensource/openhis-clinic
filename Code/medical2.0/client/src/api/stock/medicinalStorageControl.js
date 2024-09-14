import request from '@/utils/request'

export const listPage = (search) =>
    request({
        url: '/stock/medicinalStorageControl/listAll',
        method: 'post',
        data:search
    })
