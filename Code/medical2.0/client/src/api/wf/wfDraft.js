import request from '@/utils/request'

export const getWfDraftById = (id) =>
    request({
        url: '/wf/wfDraft/' + id,
        method: 'get'
    })

export const listWfDraftPage = (search) =>
    request({
        url: '/wf/wfDraft/list',
        method: 'post',
        data: search
    })

export const listWfDraftAll = (search) =>
    request({
        url: '/wf/wfDraft/listAll',
        method: 'post',
        data: search
    })


export const saveWfDraft = (formData) => 
    request({
        url: '/wf/wfDraft/save',
        method: 'post',
        data: formData
    })

export const deleteWfDraft = (wfDraft) =>
    request({
        url: '/wf/wfDraft/delete',
        method: 'post',
        data: wfDraft
    })


export const getAttachments = (id) =>
    request({
        url: '/wf/wfDraft/' + id + '/attachment',
        method: 'get'
    })    
    
