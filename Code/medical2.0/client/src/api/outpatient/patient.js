import request from '@/utils/request'

export const getPatientById = (id) =>
    request({
        url: '/outpatient/patient/' + id,
        method: 'get'
    })

export const listPatientPage = (search) =>
    request({
        url: '/outpatient/patient/list',
        method: 'post',
        data: search
    })

export const listPatientAll = (search) =>
    request({
        url: '/outpatient/patient/listAll',
        method: 'post',
        data: search
    })


export const savePatient = (patient) => 
    request({
        url: '/outpatient/patient/save',
        method: 'post',
        data: patient
    })
  
export const deletePatient = (patient) =>
    request({
        url: '/outpatient/patient/delete',
        method: 'post',
        data: patient
    })
    
export const bulkInsertPatient = (patients) =>
    request({
        url: '/outpatient/patient/bulkInsert',
        method: 'post',
        data: patients
    })
    
export const bulkUpdatePatient = (patients) =>
    request({
        url: '/outpatient/patient/bulkUpdate',
        method: 'post',
        data: patients
    })

export const bulkDeletePatient = (patients) =>
    request({
        url: '/outpatient/patient/bulkDelete',
        method: 'post',
        data: patients
    })
    
