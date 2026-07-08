import { createCrudApi } from '@/utils/apiFactory'
import request from '@/utils/request'

const baseApi = createCrudApi('/toll/tollDetail')

// Standard CRUD re-exports for backward compatibility
export const getTollDetailById = baseApi.getById
export const listTollDetailPage = baseApi.listPage
export const listTollDetailAll = baseApi.listAll
export const saveTollDetail = baseApi.save
export const deleteTollDetail = baseApi.delete
export const bulkInsertTollDetail = baseApi.bulkInsert
export const bulkUpdateTollDetail = baseApi.bulkUpdate
export const bulkDeleteTollDetail = baseApi.bulkDelete

// Custom methods
export const getCreateBy = (tollDetails) =>
    request({
        url: '/toll/tollInfo/getCreateBy',
        method: 'post',
        data: tollDetails
    })

export const orgtolldetail = (tollDetails) =>
    request({
        url: '/toll/tollInfo/orgtolldetail',
        method: 'post',
        data: tollDetails
    })
