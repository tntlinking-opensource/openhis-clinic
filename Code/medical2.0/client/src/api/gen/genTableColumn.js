import request from '@/utils/request'

export const getGenTableColumnById = (id) =>
    request({
        url: '/gen/genTableColumn/' + id,
        method: 'get'
    })

export const listGenTableColumnPage = (search) =>
    request({
        url: '/gen/genTableColumn/list',
        method: 'post',
        data: search
    })

export const listGenTableColumnAll = (search) =>
    request({
        url: '/gen/genTableColumn/listAll',
        method: 'post',
        data: search
    })    


export const saveGenTableColumn = (genTableColumn) => 
    request({
        url: '/gen/genTableColumn/save',
        method: 'post',
        data: genTableColumn
    })

export const deleteGenTableColumn = (genTableColumn) =>
    request({
        url: '/gen/genTableColumn/delete',
        method: 'post',
        data: genTableColumn
    })
    
