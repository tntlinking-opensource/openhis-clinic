import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/basicdata/manufactureFactory')

// 导出标准CRUD接口（保持向后兼容）
export const getManufactureFactoryById = baseApi.getById
export const listManufactureFactoryPage = baseApi.listPage
export const listManufactureFactoryAll = baseApi.listAll
export const saveManufactureFactory = baseApi.save
export const deleteManufactureFactory = baseApi.delete
export const bulkInsertManufactureFactory = baseApi.bulkInsert
export const bulkUpdateManufactureFactory = baseApi.bulkUpdate
export const bulkDeleteManufactureFactory = baseApi.bulkDelete

// 自定义接口
export const ureportTest = (id) =>
    request({
        url: '/basicdata/manufactureFactory/bulkDelete',
        method: 'get'
    })
