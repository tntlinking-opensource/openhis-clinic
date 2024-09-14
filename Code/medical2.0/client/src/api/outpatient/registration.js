import request from '@/utils/request'

export const getRegistrationById = (id) =>
    request({
        url: '/outpatient/registration/' + id,
        method: 'get'
    })
export const updateStatus = (id,status,departmentId,doctorId) =>
    request({
        url: `/outpatient/registration/status?id=${id}&status=${status}&departmentId=${departmentId}&doctorId=${doctorId}`,
        method: 'get'
    })

export const refundRegistrationPay = (id,status,
    refundRegistrationPayType,refundRegistrationRemarks,exitNumberDate) =>
    request({
        url:`/outpatient/registration/refundRegistrationPay?id=${id}&status=${status}&refundRegistrationPayType=${refundRegistrationPayType}&refundRegistrationRemarks=${refundRegistrationRemarks}&exitNumberDate=${exitNumberDate}`,
        method:'get'
    })

export const listRegistrationPage = (search) =>
    request({
        url: '/outpatient/registration/list',
        method: 'post',
        data: search
    })

export const listRegistrationPages = (search) =>
    request({
        url: '/outpatient/registration/v2/list',
        method: 'post',
        data: search
    })

export const listRegistrationAll = (search) =>
    request({
        url: '/outpatient/registration/listAll',
        method: 'post',
        data: search
    })


export const saveRegistration = (registration) =>
    request({
        url: '/outpatient/registration/save',
        method: 'post',
        data: registration
    })

export const deleteRegistration = (registration) =>
    request({
        url: '/outpatient/registration/delete',
        method: 'post',
        data: registration
    })

export const bulkInsertRegistration = (registrations) =>
    request({
        url: '/outpatient/registration/bulkInsert',
        method: 'post',
        data: registrations
    })

export const bulkUpdateRegistration = (registrations) =>
    request({
        url: '/outpatient/registration/bulkUpdate',
        method: 'post',
        data: registrations
    })

export const bulkDeleteRegistration = (registrations) =>
    request({
        url: '/outpatient/registration/bulkDelete',
        method: 'post',
        data: registrations
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
