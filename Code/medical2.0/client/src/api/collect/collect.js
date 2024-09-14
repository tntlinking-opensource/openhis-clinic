import request from '@/utils/request'

export const listCollectsByUserId = (userId) =>
  request({
    url: '/collect/sysCollect/listAllByUserId/' + userId,
    method: 'get',
  })

export const saveCollect = (collect) =>
  request({
    url: '/collect/sysCollect/save',
    method: 'post',
    data: collect
  })

export const updateCollect = (userId, collect) =>
  request({
    url: '/collect/sysCollect/updateBatch/' + userId,
    method: 'post',
    data: collect
  })

export const deleteCollect = (collect) =>
  request({
    url: '/collect/sysCollect/delete',
    method: 'delete',
    data: collect
  })
  
