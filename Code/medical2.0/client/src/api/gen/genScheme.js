import request from '@/utils/request'

export const getGenSchemeById = (id) =>
    request({
        url: '/gen/genScheme/' + id,
        method: 'get'
    })

export const listGenSchemePage = (search) =>
    request({
        url: '/gen/genScheme/list',
        method: 'post',
        data: search
    })

export const listGenSchemeAll = (search) =>
    request({
        url: '/gen/genScheme/listAll',
        method: 'post',
        data: search
    })


export const saveGenScheme = (genScheme) => 
    request({
        url: '/gen/genScheme/save',
        method: 'post',
        data: genScheme
    })

export const deleteGenScheme = (genScheme) =>
    request({
        url: '/gen/genScheme/delete',
        method: 'post',
        data: genScheme
    })
    
export const bulkInsertGenScheme = (genSchemes) =>
    request({
        url: '/gen/genScheme/bulkInsert',
        method: 'post',
        data: genSchemes
    })
    
export const bulkUpdateGenScheme = (genSchemes) =>
    request({
        url: '/gen/genScheme/bulkUpdate',
        method: 'post',
        data: genSchemes
    })

export const bulkDeleteGenScheme = (genSchemes) =>
    request({
        url: '/gen/genScheme/bulkDelete',
        method: 'post',
        data: genSchemes
    })
    
