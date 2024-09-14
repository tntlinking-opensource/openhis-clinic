import request from '@/utils/request'


export const stmanagementlist = (search) =>
    request({
        url: '/cure/stmanagement/stmanagementlist',
        method: 'post',
        data: search
    })

    export const updatestresult = (search) =>
    request({
        url: '/cure/stmanagement/updatestresult',
        method: 'post',
        data: search
    })

    export const updatesttime = (search) =>
    request({
        url: '/cure/stmanagement/updatesttime',
        method: 'post',
        data: search
    })

    export const selectdetailid = (search) =>
    request({
        url: '/cure/stmanagement/selectdetailid',
        method: 'post',
        data: search
    })



