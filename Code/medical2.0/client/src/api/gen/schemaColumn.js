import request from '@/utils/request'

export const getSchemaColumnById = (id) =>
    request({
        url: '/gen/schemaColumn/' + id,
        method: 'get'
    })

export const listSchemaColumnPage = (search) =>
    request({
        url: '/gen/schemaColumn/list',
        method: 'post',
        data: search
    })

export const listSchemaColumnAll = (search) =>
    request({
        url: '/gen/schemaColumn/listAll',
        method: 'post',
        data: search
    })    


export const saveSchemaColumn = (schemaColumn) => 
    request({
        url: '/gen/schemaColumn/save',
        method: 'post',
        data: schemaColumn
    })

export const deleteSchemaColumn = (schemaColumn) =>
    request({
        url: '/gen/schemaColumn/delete',
        method: 'post',
        data: schemaColumn
    })
    
