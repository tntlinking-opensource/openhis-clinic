import request from '@/utils/request'

export const getDictTypeById = (id) =>
    request({
        url: '/sys/dictType/' + id,
        method: 'get'
    })

export const listDictTypePage = (search) =>
    request({
        url: '/sys/dictType/list',
        method: 'post',
        data: search
    })

export const listDictTypeAll = (search) =>
    request({
        url: '/sys/dictType/listAll',
        method: 'post',
        data: search
    })


export const saveDictType = (dictType) => 
    request({
        url: '/sys/dictType/save',
        method: 'post',
        data: dictType
    })

export const deleteDictType = (dictType) =>
    request({
        url: '/sys/dictType/delete',
        method: 'post',
        data: dictType
    })
    
export const bulkInsertDictType = (dictTypes) =>
    request({
        url: '/sys/dictType/bulkInsert',
        method: 'post',
        data: dictTypes
    })
    
export const bulkUpdateDictType = (dictTypes) =>
    request({
        url: '/sys/dictType/bulkUpdate',
        method: 'post',
        data: dictTypes
    })

export const bulkDeleteDictType = (dictTypes) =>
    request({
        url: '/sys/dictType/bulkDelete',
        method: 'post',
        data: dictTypes
    })
    
export const importDictType = (dictType) =>
request({
  url: '/sys/dictType/importJson',
  method: 'post',
  data: dictType
})