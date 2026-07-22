import request, { serviceLong } from '@/utils/request'

export const getCreateBy = (tollDetails) =>
    request({
        url: '/toll/tollInfo/getCreateBy',
        method: 'post',
        data: tollDetails
    })
export const getWorkload = (tollDetails) =>
    request({
        url: '/toll/tollInfo/getWorkload',
        method: 'post',
        data: tollDetails
    })
export const getWorkLoadStat = (tollDetails) =>
    request({
        url: '/toll/tollInfo/getWorkLoadStat',
        method: 'post',
        data: tollDetails
    })
export const exportExcel = (tollDetails) =>
    serviceLong({
        url: '/toll/tollInfo/exportExcel',
        method: 'post',
        data: tollDetails,
        responseType:'blob'
    })

export const getdoctorDetailstatistics = (tollDetails) =>
    request({
        url: '/toll/tollInfo/getdoctorDetailstatistics',
        method: 'post',
        data: tollDetails
    })
export const getdoctorDetailstatisticsStat = (tollDetails) =>
    request({
        url: '/toll/tollInfo/getdoctorDetailstatisticsStat',
        method: 'post',
        data: tollDetails
    })
