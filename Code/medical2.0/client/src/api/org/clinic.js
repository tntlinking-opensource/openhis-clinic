import request from '@/utils/request'

export const getClinicById = (id) =>
    request({
        url: '/org/clinic/' + id,
        method: 'get'
    })

export const listClinicPage = (search) =>
    request({
        url: '/org/clinic/list',
        method: 'post',
        data: search
    })

export const listClinicAll = (search) =>
    request({
        url: '/org/clinic/listAll',
        method: 'post',
        data: search
    })


export const saveClinic = (formData) =>
    request({
        url: '/org/clinic/save',
        method: 'post',
        data: formData
    })
  
export const deleteClinic = (clinic) =>
    request({
        url: '/org/clinic/delete',
        method: 'post',
        data: clinic
    })
    
export const bulkInsertClinic = (clinics) =>
    request({
        url: '/org/clinic/bulkInsert',
        method: 'post',
        data: clinics
    })
    
export const bulkUpdateClinic = (clinics) =>
    request({
        url: '/org/clinic/bulkUpdate',
        method: 'post',
        data: clinics
    })

export const bulkDeleteClinic = (clinics) =>
    request({
        url: '/org/clinic/bulkDelete',
        method: 'post',
        data: clinics
    })
    
