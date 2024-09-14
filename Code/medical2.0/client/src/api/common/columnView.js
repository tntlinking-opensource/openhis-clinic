import request from '@/utils/request'

export const listColumnView = (routerId, tableId) =>
    request({
        url: '/common/column',
        method: 'get',
        params: {
            routerId: routerId,
            tableId: tableId
        }        
    })
   
export const saveColumnView = (columnView) => 
    request({
        url: '/common/column/save',
        method: 'post',
        data: columnView
    })

export const showAllColumnView = (routerId, tableId) =>
    request({
        url: '/common/column/selectAll',
        method: 'get',
        params: {
            routerId: routerId,
            tableId: tableId
        }        
    })  

export const showDefaultColumnView = (routerId, tableId) =>
    request({
        url: '/common/column/selectDefault',
        method: 'get',
        params: {
            routerId: routerId,
            tableId: tableId
        }        
    })      