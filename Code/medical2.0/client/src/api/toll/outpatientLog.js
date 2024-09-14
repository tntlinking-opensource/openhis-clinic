import request from '@/utils/request'

export const get = (id) =>
  request({
    url: '/toll/outpatientLog/' + id,
    method: 'get'
  })

export const auth = () =>
  request({
    url: '/auth/userLoginStatus/',
    method: 'get'
  })

export const list = (tollInfos) =>
  request({
    url: '/toll/outpatientLog/list',
    method: 'post',
    data: tollInfos
  })

export const exportExcel = (search) =>
  request({
    url: '/toll/outpatientLog/exportExcel',
    method: 'post',
    data: search,
    responseType: 'blob'
  })

/*export const getStuffsalessummarylists = (tollInfos) =>
  request({
    url: '/toll/tollInfo/getStuffsalessummarylists',
    method: 'post',
    data: tollInfos
  })*/

export const update = (search) =>
  request({
    url: '/toll/outpatientLog/update',
    method: 'put',
    data: search
  })

export const deletes = (outpatientLog) =>
  request({
    url: '/toll/outpatientLog/delete',
    method: 'delete',
    data: outpatientLog
  })
