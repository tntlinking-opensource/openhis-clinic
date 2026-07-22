import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/outpatient/registration')

// 标准CRUD接口
export const getRegistrationById = baseApi.getById
export const listRegistrationPage = baseApi.listPage
export const listRegistrationAll = baseApi.listAll
export const saveRegistration = baseApi.save
export const deleteRegistration = baseApi.delete
export const bulkInsertRegistration = baseApi.bulkInsert
export const bulkUpdateRegistration = baseApi.bulkUpdate
export const bulkDeleteRegistration = baseApi.bulkDelete

// 自定义接口
export const updateStatus = (id, status, departmentId, doctorId) =>
    request({
        url: '/outpatient/registration/status',
        method: 'post',
        params: { id, status, departmentId, doctorId }
    })

export const refundRegistrationPay = (id, status,
    refundRegistrationPayType, refundRegistrationRemarks, exitNumberDate) =>
    request({
        url: '/outpatient/registration/refundRegistrationPay',
        method: 'post',
        params: { id, status, refundRegistrationPayType, refundRegistrationRemarks, exitNumberDate }
    })

export const listRegistrationPages = (search) =>
    request({
        url: '/outpatient/registration/v2/list',
        method: 'post',
        data: search
    })

export const listDoctorsAll = () =>
    request({
        url: '/outpatient/registration/doctor',
        method: 'get'
    })
export const listDoctorsAllnew = () =>
    request({
        url: '/outpatient/registration/doctornew',
        method: 'get'
    })
    export const registationupdatenew = (registrations) =>
    request({
        url: '/outpatient/registration/registationupdate',
        method: 'post',
        data: registrations
    })
