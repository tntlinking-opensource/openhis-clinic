import request from '@/utils/request'

//新增任务
export const inserttask = (search) =>
    request({
        // url: '/taskmanagement/taskmanage/inserttask',
        url: '/taskmanagement/taskmanage/save',
        method: 'post',
        data: search
    })
    
export const storageTask = (search) =>
    request({
        url: '/taskmanagement/taskmanage/storage',
        method: 'post',
        data: search
    })

export const release = (task) =>
    request({
        url: '/taskmanagement/taskmanage/release',
        method: 'post',
        data: task
    })

export const deleteTask = (task) =>
    request({
        url: '/taskmanagement/taskmanage/delete',
        method: 'post',
        data: task
    })

    
//获取任务数据
export const selecttasklist = (search) =>
    request({
        url: '/taskmanagement/taskmanage/selecttasklist',
        method: 'post',
        data: search
    })

    //获取执行人数据
export const getusertree = (search) =>
request({
    url: '/taskmanagement/taskmanage/getusertree',
    method: 'post',
    data: search
})
//获取反馈情况
export const getfeedbacktable = (search) =>
request({
    url: '/taskmanagement/taskmanage/getfeedbacktable',
    method: 'post',
    data: search
})
//任务审核
export const updateaudit = (search) =>
request({
    url: '/taskmanagement/taskmanage/updateaudit',
    method: 'post',
    data: search
})