import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/schedule/scheduleJob')

export const getScheduleJobById = baseApi.getById
export const listScheduleJobPage = baseApi.listPage
export const listScheduleJobAll = baseApi.listAll
export const saveScheduleJob = baseApi.save
export const deleteScheduleJob = baseApi.delete
export const bulkInsertScheduleJob = baseApi.bulkInsert
export const bulkUpdateScheduleJob = baseApi.bulkUpdate
export const bulkDeleteScheduleJob = baseApi.bulkDelete

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
