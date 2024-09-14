import request from '@/utils/request'

export const getMedicalRecordById = (id) =>
    request({
        url: '/outpatient/medicalRecord/' + id,
        method: 'get'
    })

export const listMedicalRecordPage = (search) =>
    request({
        url: '/outpatient/medicalRecord/list',
        method: 'post',
        data: search
    })

export const listMedicalRecordAll = (search) =>
    request({
        url: '/outpatient/medicalRecord/listAll',
        method: 'post',
        data: search
    })


export const saveMedicalRecord = (medicalRecord) =>
    request({
        url: '/outpatient/medicalRecord/save',
        method: 'post',
        data: medicalRecord
    })

export const deleteMedicalRecord = (medicalRecord) =>
    request({
        url: '/outpatient/medicalRecord/delete',
        method: 'post',
        data: medicalRecord
    })

export const bulkInsertMedicalRecord = (medicalRecords) =>
    request({
        url: '/outpatient/medicalRecord/bulkInsert',
        method: 'post',
        data: medicalRecords
    })

export const bulkUpdateMedicalRecord = (medicalRecords) =>
    request({
        url: '/outpatient/medicalRecord/bulkUpdate',
        method: 'post',
        data: medicalRecords
    })

export const bulkDeleteMedicalRecord = (medicalRecords) =>
    request({
        url: '/outpatient/medicalRecord/bulkDelete',
        method: 'post',
        data: medicalRecords
    })

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
        let type = params.type=='recipelType_0'?'westMedicine':params.type=='recipelType_1'?'chineseMedicine':'costItem'
        if(params.recipelInfoId){
            str = '&recipelInfoId=' + params.recipelInfoId
        }

        window.open('http://localhost:9999/ureport/preview?_u=Newtouch:'+ type +'.ureport.xml' + str + '&type=0')
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
    