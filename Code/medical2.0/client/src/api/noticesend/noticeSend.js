import request from '@/utils/request'

export const getNoticeSendById = (id) =>
    request({
        url: '/noticesend/noticeSend/' + id,
        method: 'get'
    })

export const listNoticeSendPage = (search) =>
    request({
        url: '/noticesend/noticeSend/list',
        method: 'post',
        data: search
    })

export const listNoticeSendAll = (search) =>
    request({
        url: '/noticesend/noticeSend/listAll',
        method: 'post',
        data: search
    })


export const saveNoticeSend = (noticeSend) =>
    request({
        url: '/noticesend/noticeSend/save',
        method: 'post',
        data: noticeSend
    })

export const deleteNoticeSend = (noticeSend) =>
    request({
        url: '/noticesend/noticeSend/delete',
        method: 'post',
        data: noticeSend
    })

export const bulkInsertNoticeSend = (noticeSends) =>
    request({
        url: '/noticesend/noticeSend/bulkInsert',
        method: 'post',
        data: noticeSends
    })

export const bulkUpdateNoticeSend = (noticeSends) =>
    request({
        url: '/noticesend/noticeSend/bulkUpdate',
        method: 'post',
        data: noticeSends
    })

export const bulkDeleteNoticeSend = (noticeSends) =>
    request({
        url: '/noticesend/noticeSend/bulkDelete',
        method: 'post',
        data: noticeSends
    })


export const getCompanyTree = (code) =>
  request({
    url: '/noticesend/noticeSend/tree/' + code,
    method: 'get'
  });
