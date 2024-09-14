import request from '@/utils/request'

export const getLeaveById = (id) =>
    request({
        url: '/test/leave/' + id,
        method: 'get'
    })

export const listLeavePage = (search) =>
    request({
        url: '/test/leave/list',
        method: 'post',
        data: search
    })

export const listLeaveAll = (search) =>
    request({
        url: '/test/leave/listAll',
        method: 'post',
        data: search
    })


export const saveLeave = (leave) => 
    request({
        url: '/test/leave/save',
        method: 'post',
        data: leave
    })
  
export const deleteLeave = (leave) =>
    request({
        url: '/test/leave/delete',
        method: 'post',
        data: leave
    })
    
export const getLeaveByTaskId = (TaskId) =>
    request({
        url: '/test/leave/task/' + TaskId,
        method: 'get'
    })

export const saveDraftLeave = (formData) => 
    request({
        url: '/test/leave/saveDraft',
        method: 'post',
        data: formData
    })

export const createLeave = (processId, formData) => 
    request({
        url: '/test/leave/' + processId + '/create',
        method: 'post',
        data: formData
    })

export const reapplyLeave = (taskId, formData) => 
    request({
        url: '/test/leave/' + taskId + '/reapply',
        method: 'post',
        data: formData
    })

export const agreeLeave = (taskId, formData) => 
    request({
        url: '/test/leave/' + taskId + '/agree',
        method: 'post',
        data: formData
    })

export const forwardLeave = (taskId, formData) => 
    request({
        url: '/test/leave/' + taskId + '/forward',
        method: 'post',
        data: formData
    })


export const commissionLeave = (taskId, formData) => 
    request({
        url: '/test/leave/' + taskId + '/commission',
        method: 'post',
        data: formData
    })
    
export const proposeLeave = (taskId, formData) => 
    request({
        url: '/test/leave/' + taskId + '/propose',
        method: 'post',
        data: formData
    })

export const backLeave = (taskId, formData) => 
    request({
        url: '/test/leave/' + taskId + '/back',
        method: 'post',
        data: formData
    })
export const rejectLeave = (taskId, formData) => 
    request({
        url: '/test/leave/' + taskId + '/reject',
        method: 'post',
        data: formData
    })

export const terminateLeave = (taskId, formData) => 
    request({
        url: '/test/leave/' + taskId + '/terminate',
        method: 'post',
        data: formData
    })
    
export const reverseLeave = (taskId) => 
    request({
        url: '/test/leave/' + taskId + '/reverse',
        method: 'post'
    })

export const listBackActivity = (taskId) => 
    request({
        url: '/test/leave/' + taskId + '/listBackActivity',
        method: 'get'
    })