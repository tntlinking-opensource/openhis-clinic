import request from '@/utils/request'

export const downloadAttachment = (taskId, attachmentId) => {
    return request({
        url: '/rest/task/' + taskId + '/attachment/' + attachmentId + '/data',
        method: 'get',
        responseType: 'blob'
    })
}
