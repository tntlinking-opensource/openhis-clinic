import request from '@/utils/request'


export const listscheduling = (search) =>
    request({
        url: '/outpatient/scheduling/listAll',
        method: 'post',
        data: search
    })
export const listscheduzlling = (searchs) =>
    request({
        url: '/outpatient/scheduling/listzl',
        method: 'post',
        data: searchs
    })
export const listzlrday = (searchs) =>
    request({
        url: '/outpatient/scheduling/listzlrday',
        method: 'post',
        data: searchs
    })
export const listschedumxling = (searchs) =>
    request({
        url: '/outpatient/scheduling/listmx',
        method: 'post',
        data: searchs
    })
export const editSave = (savemodel) =>
    request({
        url: '/outpatient/scheduling/save',
        method: 'post',
        data: savemodel
    })
export const editDelete = (deletemodel) =>
    request({
        url: '/outpatient/scheduling/delete',
        method: 'post',
        data: deletemodel
    })
