import request from '@/utils/request'

export const getClinicVersionById = (id) =>
    request({
        url: '/clinic/clinicVersion/' + id,
        method: 'get'
    })

export const listClinicVersionPage = (search) =>
    request({
        url: '/clinic/clinicVersion/list',
        method: 'post',
        data: search
    })

export const listClinicVersionAll = (search) =>
    request({
        url: '/clinic/clinicVersion/listAll',
        method: 'post',
        data: search
    })


export const saveClinicVersion = (clinicVersion) => 
    request({
        url: '/clinic/clinicVersion/save',
        method: 'post',
        data: clinicVersion
    })
  
export const deleteClinicVersion = (clinicVersion) =>
    request({
        url: '/clinic/clinicVersion/delete',
        method: 'post',
        data: clinicVersion
    })
    
export const bulkInsertClinicVersion = (clinicVersions) =>
    request({
        url: '/clinic/clinicVersion/bulkInsert',
        method: 'post',
        data: clinicVersions
    })
    
export const bulkUpdateClinicVersion = (clinicVersions) =>
    request({
        url: '/clinic/clinicVersion/bulkUpdate',
        method: 'post',
        data: clinicVersions
    })

export const bulkDeleteClinicVersion = (clinicVersions) =>
    request({
        url: '/clinic/clinicVersion/bulkDelete',
        method: 'post',
        data: clinicVersions
    })
    
