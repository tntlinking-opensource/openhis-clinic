import request from '@/utils/request'

export const getHistProcInstance = (procInstId) =>
    request({
        url: '/rest/history/process-instance/' + procInstId,
        method: 'get'
    })

export const getProcInstComments = (procInstId) =>
    request({
        url: '/wf/procinst/' + procInstId + '/comments',
        method: 'get'
    })

export const histoicFlow = (procInstId) =>
    request({
        url: '/wf/procinst/' + procInstId + '/histoicFlow',
        method: 'get'
    })

export const getProcInstAttachments = (procInstId) =>
    request({
        url: '/wf/procinst/' + procInstId + '/attachments',
        method: 'get'
    })

export const countProcInstance = (params) =>
    request({
        url: '/rest/process-instance/count',
        method: 'get',
        params: params
    })