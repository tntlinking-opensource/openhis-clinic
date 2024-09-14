import request from '@/utils/request'

export const getClinicOfficeById = (id) =>
    request({
        url: '/org/clinicOffice/' + id,
        method: 'get'
    })

export const listClinicOfficePage = (search) =>
    request({
        url: '/org/clinicOffice/list',
        method: 'post',
        data: search
    })

export const listClinicOfficeAll = (search) =>
    request({
        url: '/org/clinicOffice/listAll',
        method: 'post',
        data: search
    })


export const saveClinicOffice = (clinicOffice) => 
    request({
        url: '/org/clinicOffice/save',
        method: 'post',
        data: clinicOffice
    })
  
export const deleteClinicOffice = (clinicOffice) =>
    request({
        url: '/org/clinicOffice/delete',
        method: 'post',
        data: clinicOffice
    })
    
export const bulkInsertClinicOffice = (clinicOffices) =>
    request({
        url: '/org/clinicOffice/bulkInsert',
        method: 'post',
        data: clinicOffices
    })
    
export const bulkUpdateClinicOffice = (clinicOffices) =>
    request({
        url: '/org/clinicOffice/bulkUpdate',
        method: 'post',
        data: clinicOffices
    })

export const bulkDeleteClinicOffice = (clinicOffices) =>
    request({
        url: '/org/clinicOffice/bulkDelete',
        method: 'post',
        data: clinicOffices
    })
    
