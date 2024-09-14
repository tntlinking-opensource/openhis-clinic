import request from '@/utils/request'

export const getProcessBpmnById = (id) =>
    request({
        url: '/rest/process-definition/' + id + '/xml',
        method: 'get'
    })

export const getProcessBpmnByKey = (key, tenantId) =>
    request({
        url: '/rest/process-definition/key/' + key + (tenantId ? '/tenant-id/' + tenantId : '') + '/xml',
        method: 'get'
    })

export const getProcessByKey = (key, tenantId) =>
    request({
        url: '/rest/process-definition/key/' + key + (tenantId ? '/tenant-id/' + tenantId : ''),
        method: 'get'
    })


export const getProcessDiagramById = (id) =>
    request({
        url: '/rest/process-definition/' + id + '/diagram',
        method: 'get'
    })

export const listProcessDefinitionPage = (search) =>
    request({
        url: '/rest/process-definition',
        method: 'get',
        params: search
    })

export const countProcessDefinition = (search) =>
    request({
        url: '/rest/process-definition/count',
        method: 'get',
        params: search
    })    

export const suspendProcessDefinition = (id, suspendModle) =>
    request({
        url: '/rest/process-definition/' + id + '/suspended',
        method: 'put',
        data: suspendModle
    }) 

export const getStartForm = (id) =>
    request({
        url: '/rest/process-definition/' + id + '/startForm',
        method: 'get'
    })

export const getStartFormByKey = (key, tenantId) =>
    request({
        url: '/rest/process-definition/key/' + key + (tenantId ? '/tenant-id/' + tenantId : '') + '/startForm',
        method: 'get'
    })
