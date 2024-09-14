import request from '@/utils/request'

export const listarticlepage = (search) =>
    request({
        url: '/taskmanagement/article/list',
        method: 'post',
        data: search
    })
export const listarticlepages = (search) =>
    request({
        url: '/taskmanagement/article/lists',
        method: 'post',
        data: search
    })

export const editSave = (savemodel) =>
    request({
        url: '/taskmanagement/article/save',
        method: 'post',
        data: savemodel
    })
export const editUpdate = (savemodel) =>
    request({
        url: '/taskmanagement/article/update',
        method: 'post',
        data: savemodel
    })
export const editAudit = (savemodel) =>
    request({
        url: '/taskmanagement/article/audit',
        method: 'post',
        data: savemodel
    })
export const getarticleid = (savemodel) =>
    request({
        url: '/taskmanagement/article/listgetid',
        method: 'post',
        data: savemodel
    })
