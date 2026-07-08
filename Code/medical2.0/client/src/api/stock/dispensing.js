import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/stock/dispensing')

// 导出标准CRUD接口（保持向后兼容）
export const getDispensingById = baseApi.getById
export const listDispensingPage = baseApi.listPage
export const listDispensingAll = baseApi.listAll
export const saveDispensing = baseApi.save
export const deleteDispensing = baseApi.delete
export const bulkInsertDispensing = baseApi.bulkInsert
export const bulkUpdateDispensing = baseApi.bulkUpdate
export const bulkDeleteDispensing = baseApi.bulkDelete

// 自定义接口
export const getList = (data) =>
    request({
        url: '/stock/dispensing/reportList',
        method: 'post',
        data: data
    })

export const getOrganizationList = (data) =>
    request({
        url: '/stock/dispensing/getOrganizationList',
        method: 'post',
        data: data
    })

export const getAmount = (data) =>
    request({
        url: '/stock/dispensing/reportAmount',
        method: 'post',
        data: data
    })

export const getOrganizationAmount = (data) =>
    request({
        url: '/stock/dispensing/getOrganizationAmount',
        method: 'post',
        data: data
    })
