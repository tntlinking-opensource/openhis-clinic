import request from '@/utils/request'

export const listHistTaskPage = (search, firstResult, maxResults) => {
    return request({
        url: '/rest/history/task',
        method: 'post',
        params: {firstResult: firstResult, maxResults: maxResults},
        data: search
    })
}

export const countHistTask = (search) => {
    return request({
        url: '/rest/history/task/count',
        method: 'post',
        data: search
    })
}
