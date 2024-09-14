import request from '@/utils/request'

export const getRegistrationById = (id) =>
    request({
        url: '/registration/registration/' + id,
        method: 'get'
    })

export const listRegistrationPage = (search) =>
    request({
        url: '/registration/registration/list',
        method: 'post',
        data: search
    })

export const listRegistrationAll = (search) =>
    request({
        url: '/registration/registration/listAll',
        method: 'post',
        data: search
    })


export const saveRegistration = (registration) => 
    request({
        url: '/registration/registration/save',
        method: 'post',
        data: registration
    })
  
export const deleteRegistration = (registration) =>
    request({
        url: '/registration/registration/delete',
        method: 'post',
        data: registration
    })
    
export const bulkInsertRegistration = (registrations) =>
    request({
        url: '/registration/registration/bulkInsert',
        method: 'post',
        data: registrations
    })
    
export const bulkUpdateRegistration = (registrations) =>
    request({
        url: '/registration/registration/bulkUpdate',
        method: 'post',
        data: registrations
    })

export const bulkDeleteRegistration = (registrations) =>
    request({
        url: '/registration/registration/bulkDelete',
        method: 'post',
        data: registrations
    })
    
