import { createCrudApi } from '@/utils/apiFactory'
import request from '@/utils/request'
import qs from "qs";

const baseApi = createCrudApi('/outpatient/medicalRecord')

// Re-export standard CRUD with original names for backward compatibility
export const getMedicalRecordById = baseApi.getById
export const listMedicalRecordPage = baseApi.listPage
export const listMedicalRecordAll = baseApi.listAll
export const saveMedicalRecord = (data) =>
    request({
        url: '/outpatient/medicalRecord/saveWithFile',
        method: 'post',
        data
    })
export const deleteMedicalRecord = baseApi.delete
export const bulkInsertMedicalRecord = baseApi.bulkInsert
export const bulkUpdateMedicalRecord = baseApi.bulkUpdate
export const bulkDeleteMedicalRecord = baseApi.bulkDelete

// Keep custom endpoints as-is
export const allSaveMedicalRecord = (medicalRecords) =>
    request({
        url: '/outpatient/medicalRecord/allSave',
        method: 'post',
        data: medicalRecords
    })
export const editSave = (medicalRecords) =>
    request({
        url: '/outpatient/medicalRecord/v2/allSave',
        method: 'post',
        data: medicalRecords
    })
export const getHistoryRecipel = (search) =>
    request({
        url: '/outpatient/medicalRecord/history/recipel',
        method: 'post',
        data:search
    })
    export const allQueryMedicalRecord = (medicalRecords) =>
    request({
        url: '/outpatient/medicalRecord/v2/allQuery/' + medicalRecords,
        method: 'post',
        // data: medicalRecords
    })

    export const ureport = (params) =>{
        let str = ''
        let type = params.type==='recipelType_0'?'westMedicine':params.type==='recipelType_1'?'chineseMedicine':'costItem'
        if(params.recipelInfoId){
            str = '&recipelInfoId=' + params.recipelInfoId
        }

        window.open(process.env.UREPORT_URL + '?_u=Newtouch:'+ type +'.ureport.xml' + str + '&type=0')
        // request({
        //     url: '/ureport/preview?_u=mysql:recipel.ureport.xml&_t=1,6,7,8,9&id=' + params.id + str,
        //     method: 'get'
        // })
    }

    export const getrecordpatlist = (id) =>
    request({
        url: '/outpatient/medicalRecord/recordpat/'+id,
        method: 'get',
    })

export const getZdList = (searchParam,zdType) =>
  request({
    url: '/outpatient/medicalRecord/getxtzd',
    method: 'post',
    data:qs.stringify({
      searchParam,
      zdType
    })
  })

export const getZhList = (searchParam) =>
  request({
    url: '/outpatient/medicalRecord/getzyzh',
    method: 'post',
    data:qs.stringify({
      searchParam,
    })
  })
