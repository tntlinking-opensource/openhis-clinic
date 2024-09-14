import request from '@/utils/request'

export const getQueryConditionById = (id) =>
    request({
        url: '/sys/queryCondition/' + id,
        method: 'get'
    })

export const listQueryConditionPage = (search) =>
    request({
        url: '/sys/queryCondition/list',
        method: 'post',
        data: search
    })

export const listQueryConditionAll = (search) =>
    request({
        url: '/sys/queryCondition/listAll',
        method: 'post',
        data: search
    })


export const saveQueryCondition = (queryCondition) => 
    request({
        url: '/sys/queryCondition/save',
        method: 'post',
        data: queryCondition
    })

export const deleteQueryCondition = (queryCondition) =>
    request({
        url: '/sys/queryCondition/delete',
        method: 'post',
        data: queryCondition
    })
    
