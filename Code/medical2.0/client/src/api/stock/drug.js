import request from '@/utils/request'

export const getDrugById = (id) =>
    request({
        url: '/stock/drug/' + id,
        method: 'get'
    })

export const listDrugPage = (search) =>
    request({
        url: '/stock/drug/list',
        method: 'post',
        data: search
    })

export const listByCompanyDrugPage = (search) =>
    request({
        url: '/stock/drug/listByCompany',
        method: 'post',
        data: search
    })

export const listByInstitutionPage = (search) =>
    request({
        url: '/stock/drug/listByInstitution',
        method: 'post',
        data: search
    })

export const saveDrugSyncToClinic = (drugs) =>
    request({
        url: '/stock/drug/syncToClinic',
        method: 'post',
        data: drugs
    })

export const listByHospitalDrug = (search) =>
    request({
        url: '/hosdata/HosCollectData/getHosDrugs',
        method: 'post',
        data: search
    })

export const saveHisDrugsToClinic = (drugs) =>
  request({
      url: '/hosdata/HosCollectData/HisDrugsToClinic',
      method: 'post',
      data: drugs
  })

export const listDrugAll = (search) =>
    request({
        url: '/stock/drug/listAll',
        method: 'post',
        data: search
    })

export const inventory = (search) =>
  request({
    url: '/stock/drug/inventory',
    method: 'post',
    data: search
  })

export const listAllStock = (search) =>
    request({
        url: '/stock/drug/listAllStock',
        method: 'post',
        data: search
    })

export const listAllStock2 = (search) =>
    request({
        url: '/stock/drug/listAllStock2',
        method: 'post',
        data: search
    })

export const saveDrug = (drug) =>
    request({
        url: '/stock/drug/save',
        method: 'post',
        data: drug
    })

export const deleteDrug = (drug) =>
    request({
        url: '/stock/drug/delete',
        method: 'post',
        data: drug
    })

export const bulkInsertDrug = (drugs) =>
    request({
        url: '/stock/drug/bulkInsert',
        method: 'post',
        data: drugs
    })

export const bulkUpdateDrug = (drugs) =>
    request({
        url: '/stock/drug/bulkUpdate',
        method: 'post',
        data: drugs
    })

export const bulkDeleteDrug = (drugs) =>
    request({
        url: '/stock/drug/bulkDelete',
        method: 'post',
        data: drugs
    })

export const updateAllIndate = (drug) =>
    request({
        url: '/stock/drug/updateAllIndate',
        method: 'post',
        data:drug
    })

export const updateAllInventory = (drug) =>
    request({
        url: '/stock/drug/updateAllInventory',
        method: 'post',
        data:drug
    })


export const uploadExcel = (formData) =>
    request({
        url: '/stock/drug/uploadExcel',
        method: 'post',
        data: formData
    })
