import request from '@/utils/request'

export const getScheduleJobById = (id) =>
    request({
        url: '/schedule/scheduleJob/' + id,
        method: 'get'
    })

export const listScheduleJobPage = (search) =>
    request({
        url: '/schedule/scheduleJob/list',
        method: 'post',
        data: search
    })

export const listScheduleJobAll = (search) =>
    request({
        url: '/schedule/scheduleJob/listAll',
        method: 'post',
        data: search
    })


export const saveScheduleJob = (scheduleJob) => 
    request({
        url: '/schedule/scheduleJob/save',
        method: 'post',
        data: scheduleJob
    })

export const deleteScheduleJob = (scheduleJob) =>
    request({
        url: '/schedule/scheduleJob/delete',
        method: 'post',
        data: scheduleJob
    })
    
export const bulkInsertScheduleJob = (scheduleJobs) =>
    request({
        url: '/schedule/scheduleJob/bulkInsert',
        method: 'post',
        data: scheduleJobs
    })
    
export const bulkUpdateScheduleJob = (scheduleJobs) =>
    request({
        url: '/schedule/scheduleJob/bulkUpdate',
        method: 'post',
        data: scheduleJobs
    })

export const bulkDeleteScheduleJob = (scheduleJobs) =>
    request({
        url: '/schedule/scheduleJob/bulkDelete',
        method: 'post',
        data: scheduleJobs
    })

// 启动定时器
export const startTask = (scheduleJob) =>
  request({
    url: '/schedule/dynamicTask/startTask',
    method: 'post',
    data: scheduleJob
  })


// 启动定时器
export const endTask = (scheduleJob) =>
  request({
    url: '/schedule/dynamicTask/endTask',
    method: 'post',
    data: scheduleJob
  })

// 重启定时器
export const restartTask = (scheduleJob) =>
  request({
    url: '/schedule/dynamicTask/restartTask',
    method: 'post',
    data: scheduleJob
  })
