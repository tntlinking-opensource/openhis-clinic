import request from '@/utils/request'

export const getGenTableById = (id) =>
    request({
        url: '/gen/genTable/' + id,
        method: 'get'
    })

export const listGenTablePage = (search) =>
    request({
        url: '/gen/genTable/list',
        method: 'post',
        data: search
    })

export const listGenTableAll = (search) =>
    request({
        url: '/gen/genTable/listAll',
        method: 'post',
        data: search
    })


export const saveGenTable = (genTable) => 
    request({
        url: '/gen/genTable/save',
        method: 'post',
        data: genTable
    })

export const deleteGenTable = (genTable) =>
    request({
        url: '/gen/genTable/delete',
        method: 'post',
        data: genTable
    })
    
export const bulkInsertGenTable = (genTables) =>
    request({
        url: '/gen/genTable/bulkInsert',
        method: 'post',
        data: genTables
    })
    
export const bulkUpdateGenTable = (genTables) =>
    request({
        url: '/gen/genTable/bulkUpdate',
        method: 'post',
        data: genTables
    })

export const bulkDeleteGenTable = (genTables) =>
    request({
        url: '/gen/genTable/bulkDelete',
        method: 'post',
        data: genTables
    })
    
export const importGenTable = (genTable) =>
request({
  url: '/gen/genTable/importJson',
  method: 'post',
  data: genTable
})