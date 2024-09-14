import request from '@/utils/request'

export const claimTask = (taskId) => 
    request({
        url: '/wf/task/claim/' + taskId,
        method: 'get'
    })

// 获取用户的指派任务和候选任务
export const pageTask = (search) =>
    request({
        url: '/wf/task/page',
        method: 'get',
        params: search
    })

export const listTask = (search) =>
    request({
        url: '/rest/task',
        method: 'get',
        params: search
    })

export const countTask = (search) =>
    request({
        url: '/rest/task/count',
        method: 'get',
        params: search
    })

export const delTaskAttachment = (taskId, attachmentId) =>
    request({
        url: '/rest/task/' + taskId + '/attachment/' + attachmentId,
        method: 'DELETE'
    })  