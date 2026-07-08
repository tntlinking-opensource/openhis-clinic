import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/org/department')

// 导出标准CRUD接口（保持向后兼容）
export const getDepartmentById = baseApi.getById
export const listDepartmentPage = baseApi.listPage
export const listDepartmentAll = baseApi.listAll
export const saveDepartment = baseApi.save
export const deleteDepartment = baseApi.delete

// 自定义接口
export const treeDepartment = (search) =>
    request({
        url: '/org/department/tree',
        method: 'post',
        data: search
    })
