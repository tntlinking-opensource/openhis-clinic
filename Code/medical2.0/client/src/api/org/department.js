import request from '@/utils/request'

export const getDepartmentById = (id) =>
    request({
        url: '/org/department/' + id,
        method: 'get'
    })

export const listDepartmentPage = (search) =>
    request({
        url: '/org/department/list',
        method: 'post',
        data: search
    })

export const listDepartmentAll = (search) =>
    request({
        url: '/org/department/listAll',
        method: 'post',
        data: search
    })

export const treeDepartment = (search) =>
    request({
        url: '/org/department/tree',
        method: 'post',
        data: search
    })

export const saveDepartment = (department) => 
    request({
        url: '/org/department/save',
        method: 'post',
        data: department
    })

export const deleteDepartment = (department) =>
    request({
        url: '/org/department/delete',
        method: 'post',
        data: department
    })
    
