/**
 * 药品管理 API
 */
import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/stock/drug')

// 标准CRUD接口
export const getDrugById = baseApi.getById
export const listDrugPage = baseApi.listPage
export const listDrugAll = baseApi.listAll
export const saveDrug = baseApi.save
export const deleteDrug = baseApi.delete
export const bulkInsertDrug = baseApi.bulkInsert
export const bulkUpdateDrug = baseApi.bulkUpdate
export const bulkDeleteDrug = baseApi.bulkDelete

// 自定义接口
export const listByCompanyDrugPage = (search) =>
  request({ url: '/stock/drug/listByCompany', method: 'post', data: search })

export const listByInstitutionPage = (search) =>
  request({ url: '/stock/drug/listByInstitution', method: 'post', data: search })

export const saveDrugSyncToClinic = (drugs) =>
  request({ url: '/stock/drug/syncToClinic', method: 'post', data: drugs })

export const listByHospitalDrug = (search) =>
  request({ url: '/hosdata/HosCollectData/getHosDrugs', method: 'post', data: search })

export const saveHisDrugsToClinic = (drugs) =>
  request({ url: '/hosdata/HosCollectData/HisDrugsToClinic', method: 'post', data: drugs })

export const inventory = (search) =>
  request({ url: '/stock/drug/inventory', method: 'post', data: search })

export const listAllStock = (search) =>
  request({ url: '/stock/drug/listAllStock', method: 'post', data: search })

export const listAllStock2 = (search) =>
  request({ url: '/stock/drug/listAllStock2', method: 'post', data: search })

export const updateAllIndate = (drug) =>
  request({ url: '/stock/drug/updateAllIndate', method: 'post', data: drug })

export const updateAllInventory = (drug) =>
  request({ url: '/stock/drug/updateAllInventory', method: 'post', data: drug })

export const uploadExcel = (formData) =>
  request({ url: '/stock/drug/uploadExcel', method: 'post', data: formData })
