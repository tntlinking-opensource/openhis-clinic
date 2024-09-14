import request from '@/utils/request'

export const getRecipelDetailById = (id) =>
    request({
        url: '/outpatient/recipelDetail/' + id,
        method: 'get'
    })
export const getByRecipelInfoId = (recipelInfoId) =>
    request({
        url: '/outpatient/recipelDetail/getByRecipelInfoId/' + recipelInfoId,
        method: 'get'
    })
export const listRecipelDetailPage = (search) =>
    request({
        url: '/outpatient/recipelDetail/list',
        method: 'post',
        data: search
    })

export const listRecipelDetailAll = (search) =>
    request({
        url: '/outpatient/recipelDetail/listAll',
        method: 'post',
        data: search
    })


export const saveRecipelDetail = (recipelDetail) => 
    request({
        url: '/outpatient/recipelDetail/save',
        method: 'post',
        data: recipelDetail
    })
  
export const deleteRecipelDetail = (recipelDetail) =>
    request({
        url: '/outpatient/recipelDetail/delete',
        method: 'post',
        data: recipelDetail
    })
    
export const bulkInsertRecipelDetail = (recipelDetails) =>
    request({
        url: '/outpatient/recipelDetail/bulkInsert',
        method: 'post',
        data: recipelDetails
    })
    
export const bulkUpdateRecipelDetail = (recipelDetails) =>
    request({
        url: '/outpatient/recipelDetail/bulkUpdate',
        method: 'post',
        data: recipelDetails
    })

export const bulkDeleteRecipelDetail = (recipelDetails) =>
    request({
        url: '/outpatient/recipelDetail/bulkDelete',
        method: 'post',
        data: recipelDetails
    })
    
