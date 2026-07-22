import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/myTask/myTask')

// 导出标准CRUD接口（保持向后兼容）
export const getMyTaskById = baseApi.getById
export const listMyTaskPage = baseApi.listPage
export const listMyTaskAll = baseApi.listAll
export const saveMyTask = baseApi.save

// 自定义接口
export const updateMyTask = (myTask) =>
    request({
        url: '/myTask/myTask/update',
        method: 'post',
        data: myTask
    })
