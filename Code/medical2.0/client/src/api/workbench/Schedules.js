import request from '@/utils/request'

export const listSchedulesPage = (search) =>
    request({
        url: '/workbench/Schedules/tobeseelist',
        method: 'post',
        data: search
    })

    export const listdispensingPages = (search) =>
    request({
        url: '/workbench/Schedules/dispensing',
        method: 'post',
        data: search
    })

    export const listPatientcharge = (search) =>
    request({
        url: '/workbench/Schedules/patidwsflist',
        method: 'post',
        data: search
    })

    export const updateoverlockids=(strRecipelInfoIds)=>
    request({
        url:'/workbench/Schedules/'+strRecipelInfoIds,
        method:'get',
    })

    export const updateoverlockidlist=(search)=>
    request({
        url:'/workbench/Schedules/updateoverlookidlist',
        method:'post',
        data:search,
    })

    export const selectvisiprogresslist=(search)=>
    request({
        url:'/workbench/Schedules/schedulelists',
        method:'post',
        data:search,
    })


