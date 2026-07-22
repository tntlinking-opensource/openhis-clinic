import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/admin/user')

// 标准CRUD接口
export const getUserById = baseApi.getById
export const listUserPage = baseApi.listPage
export const listUserAll = baseApi.listAll
export const saveUser = (data) =>
    request({
        url: '/admin/user/saveWithFile',
        method: 'post',
        data
    })
export const deleteUser = baseApi.delete
export const bulkInsertUser = baseApi.bulkInsert
export const bulkUpdateUser = baseApi.bulkUpdate
export const bulkDeleteUser = baseApi.bulkDelete

// 自定义接口
export const updateUser = (user) =>
    request({
        url: '/admin/user/update',
        method: 'post',
        data: user
    })

export const changeLoginPassword = (id, password) =>
    request({
        url: '/admin/user/' + id + '/loginPassword',
        method: 'put',
        params: {
            password: password
        }
    })

export const changemylist = (mobile) =>
    request({
        url: '/admin/user/phone/' + mobile,
        method: 'get',
    })
