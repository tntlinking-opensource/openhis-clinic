import request from '@/utils/request'

export const getSchemaTableById = (id) =>
    request({
        url: '/gen/schemaTable/' + id,
        method: 'get'
    })

export const listSchemaTablePage = (search) =>
    request({
        url: '/gen/schemaTable/list',
        method: 'post',
        data: search
    })

export const listSchemaTableAll = (search) =>
    request({
        url: '/gen/schemaTable/listAll',
        method: 'post',
        data: search
    })    


export const saveSchemaTable = (schemaTable) => 
    request({
        url: '/gen/schemaTable/save',
        method: 'post',
        data: schemaTable
    })

export const deleteSchemaTable = (schemaTable) =>
    request({
        url: '/gen/schemaTable/delete',
        method: 'post',
        data: schemaTable
    })
    
