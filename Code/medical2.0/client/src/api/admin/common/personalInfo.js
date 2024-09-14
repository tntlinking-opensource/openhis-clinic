import request from '@/utils/request'

export const getCurrentUser = () =>
    request({
        url: '/person/me',
        method: 'get'
    })

export const updateCurrentUser = (user) =>
    request({
        url: '/person/me',
        method: 'put',
        data: user
    })    