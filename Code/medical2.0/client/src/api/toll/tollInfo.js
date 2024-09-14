import request from '@/utils/request'

export const getTollInfoById = (id) =>
    request({
        url: '/toll/tollInfo/' + id,
        method: 'get'
    })
export const getTollInfoByRegistrationId = (registrationId) =>
    request({
        url: '/toll/tollInfo/getTollInfoByRegistrationId/' + registrationId,
        method: 'get'
    })

export const listTollInfoPage = (search) =>
    request({
        url: '/toll/tollInfo/list',
        method: 'post',
        data: search
    })

export const listTollInfoAll = (search) =>
    request({
        url: '/toll/tollInfo/listAll',
        method: 'post',
        data: search
    })


export const saveTollInfo = (tollInfo) => 
    request({
        url: '/toll/tollInfo/save',
        method: 'post',
        data: tollInfo
    })
  
export const deleteTollInfo = (tollInfo) =>
    request({
        url: '/toll/tollInfo/delete',
        method: 'post',
        data: tollInfo
    })
    
export const bulkInsertTollInfo = (tollInfos) =>
    request({
        url: '/toll/tollInfo/bulkInsert',
        method: 'post',
        data: tollInfos
    })
    
export const bulkUpdateTollInfo = (tollInfos) =>
    request({
        url: '/toll/tollInfo/bulkUpdate',
        method: 'post',
        data: tollInfos
    })

export const bulkDeleteTollInfo = (tollInfos) =>
    request({
        url: '/toll/tollInfo/bulkDelete',
        method: 'post',
        data: tollInfos
    })
    
export const saveTollTollInfo = (tollInfos,type) =>
    request({
        url: '/toll/tollInfo/save/toll/'+type,
        method: 'post',
        data: tollInfos
    })

export const tollTotalForm = (tollInfos) =>
    request({
        url: '/toll/tollInfo/tollTotalForm',
        method: 'post',
        data: tollInfos
    })

export const tollDetailForm = (tollInfos) =>
    request({
        url: '/toll/tollInfo/tollDetailForm',
        method: 'post',
        data: tollInfos
    })

    export const getypjxcmanagementlist = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getypjxcmanagement',
        method: 'post',
        data: tollInfos
    })
    export const getypjxcmanagementsums = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getypjxcmanagementsums',
        method: 'post',
        data: tollInfos
    })

    export const getStuffsalessummarylists = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getStuffsalessummarylists',
        method: 'post',
        data: tollInfos
    })
    export const getStuffsalessummarysumss = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getStuffsalessummarysumss',
        method: 'post',
        data: tollInfos
    })

    export const getcljxcmanagementlist = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getcljxcmanagement',
        method: 'post',
        data: tollInfos
    })
    export const getcljxcmanagementsums = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getcljxcmanagementsums',
        method: 'post',
        data: tollInfos
    })

    export const getypclrkcxlist = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getypclrkcxlist',
        method: 'post',
        data: tollInfos
    })
export const getypclrkcxsums = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getypclrkcxsums',
        method: 'post',
        data: tollInfos
    })
export const exportDrugOrStuffStock = (tollInfos) =>
    request({
        url: '/toll/tollInfo/exportDrugOrStuffStock',
        method: 'post',
        data: tollInfos,
        responseType:'blob'
    })
export const getpharmaceuticalInventoryManagement = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getpharmaceuticalInventoryManagement',
        method: 'post',
        data: tollInfos
    })
export const getpharmaceuticalInventoryManagementsums = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getpharmaceuticalInventoryManagementsums',
        method: 'post',
        data: tollInfos
    })
export const getjglist = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getjglist',
        method: 'post',
        data: tollInfos
    })
    export const getmaterialmanagement = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getmaterialmanagement',
        method: 'post',
        data: tollInfos
    })
export const getmaterialmanagementsums = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getmaterialmanagementsums',
        method: 'post',
        data: tollInfos
    })
export const drugmaterialsstockmanagement = (tollInfos) =>
    request({
        url: '/toll/tollInfo/drugmaterialsstockmanagement',
        method: 'post',
        data: tollInfos
    })
export const drugmaterialsstockmanagementsums = (tollInfos) =>
    request({
        url: '/toll/tollInfo/drugmaterialsstockmanagementsums',
        method: 'post',
        data: tollInfos
    })
export const getconsumablemarketstatistics = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getconsumablemarketstatistics',
        method: 'post',
        data: tollInfos
    })
export const getconsumablemarketstatisticssum = (tollInfos) =>
    request({
        url: '/toll/tollInfo/getconsumablemarketstatisticssum',
        method: 'post',
        data: tollInfos
    })

export const orgtollDetailForm = (tollInfos) =>
    request({
        url: '/toll/tollInfo/orgtollDetailForm',
        method: 'post',
        data: tollInfos
    })