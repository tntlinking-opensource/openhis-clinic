import request from '@/utils/request'

export const getDiagnosisById = (id) =>
    request({
        url: '/outpatient/remoteDiagnosisTreatment/' + id,
        method: 'get'
    })

export const getRegistrationId = (registrationId) =>
    request({
        url: '/outpatient/remoteDiagnosisTreatment/registrationId/' + registrationId,
        method: 'get'
    })

export const listDiagnosisPage = (search) =>
    request({
        url: '/outpatient/remoteDiagnosisTreatment/list',
        method: 'post',
        data: search
    })

export const listDiagnosisAll = (search) =>
    request({
        url: '/outpatient/remoteDiagnosisTreatment/listAll',
        method: 'post',
        data: search
    })


export const saveDiagnosis = (patient) =>
    request({
        url: '/outpatient/remoteDiagnosisTreatment/save',
        method: 'post',
        data: patient
    })

export const modifiedState = (patient) =>
    request({
        url: '/outpatient/remoteDiagnosisTreatment/modifiedState',
        method: 'post',
        data: patient
    })

export const chargeState = (patient) =>
  request({
    url: '/outpatient/remoteDiagnosisTreatment/chargeState',
    method: 'post',
    data: patient
  })

export const deleteDiagnosis = (patient) =>
    request({
        url: '/outpatient/remoteDiagnosisTreatment/delete',
        method: 'post',
        data: patient
    })

export const bulkInsertDiagnosis = (patients) =>
    request({
        url: '/outpatient/remoteDiagnosisTreatment/bulkInsert',
        method: 'post',
        data: patients
    })

export const bulkUpdateDiagnosis = (patients) =>
    request({
        url: '/outpatient/remoteDiagnosisTreatment/bulkUpdate',
        method: 'post',
        data: patients
    })

export const bulkDeleteDiagnosis = (patients) =>
    request({
        url: '/outpatient/remoteDiagnosisTreatment/bulkDelete',
        method: 'post',
        data: patients
    })

