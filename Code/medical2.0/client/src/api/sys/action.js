import request from '@/utils/request'

export const getActionById = (id) =>
    request({
        url: '/sys/action/' + id,
        method: 'get'
    })

export const listActionPage = (search) =>
    request({
        url: '/sys/action/list',
        method: 'post',
        data: search
    })

export const listActionAll = (search) =>
    request({
        url: '/sys/action/listAll',
        method: 'post',
        data: search
    })    


export const saveAction = (action) => 
    request({
        url: '/sys/action/save',
        method: 'post',
        data: action
    })

export const deleteAction = (action) =>
    request({
        url: '/sys/action/delete',
        method: 'post',
        data: action
    })
    
