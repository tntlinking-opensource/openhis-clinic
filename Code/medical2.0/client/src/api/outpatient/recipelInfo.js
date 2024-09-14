import request from '@/utils/request'

export const getRecipelInfoById = (id) =>
    request({
        url: '/outpatient/recipelInfo/' + id,
        method: 'get'
    })

export const listRecipelInfoPage = (search) =>
    request({
        url: '/outpatient/recipelInfo/list',
        method: 'post',
        data: search
    })

export const listRecipelInfoAll = (search) =>
    request({
        url: '/outpatient/recipelInfo/listAll',
        method: 'post',
        data: search
    })


export const saveRecipelInfo = (recipelInfo) =>
    request({
        url: '/outpatient/recipelInfo/save',
        method: 'post',
        data: recipelInfo
    })

export const deleteRecipelInfo = (recipelInfo) =>
    request({
        url: '/outpatient/recipelInfo/delete',
        method: 'post',
        data: recipelInfo
    })

export const bulkInsertRecipelInfo = (recipelInfos) =>
    request({
        url: '/outpatient/recipelInfo/bulkInsert',
        method: 'post',
        data: recipelInfos
    })

export const bulkUpdateRecipelInfo = (recipelInfos) =>
    request({
        url: '/outpatient/recipelInfo/bulkUpdate',
        method: 'post',
        data: recipelInfos
    })

export const bulkDeleteRecipelInfo = (recipelInfos) =>
    request({
        url: '/outpatient/recipelInfo/bulkDelete',
        method: 'post',
        data: recipelInfos
    })

export const invalidStatus = (id) =>
    request({
        url: `/outpatient/recipelInfo/invalid?id=${id}`,
        method: 'get'
    })

export const updateNotShowById = (id) =>
    request({
        url: `/outpatient/recipelInfo/update/notShow?id=${id}`,
        method: 'get'
    })
