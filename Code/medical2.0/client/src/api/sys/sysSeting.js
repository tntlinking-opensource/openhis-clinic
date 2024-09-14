import request from '@/utils/request'

//根据图片id回显图片
export const getFileById = (id) =>
    request({
        url: '/sys/sysSeting/getFile/' + id,
        method: 'get'
    })
export const getFiled = (id) =>
    request({
        url:'/sys/sysSeting/getFiled/'+id,
        method:'get'
    })
export const getFirmList = (loginName,password) =>
    request({
        url: `/auth/getUserTenant?loginName=${loginName}&password=${password}`,
        method: 'get'
    })


export const listSysSetingAll = (search) =>
    request({
        url: '/sys/sysSeting/listAll',
        method: 'post',
        data: search
    })


export const saveSysSeting = (sysSeting) =>
    request({
        url: '/sys/sysSeting/save',
        method: 'post',
        data: sysSeting
    })

export const deleteSysSeting = (sysSeting) =>
    request({
        url: '/sys/sysSeting/delete',
        method: 'post',
        data: sysSeting
    })

export const getPhotoById = (id) =>
    request({
        url:'/sys/sysSeting/getPhoto/'+id,
        method:'get'
    })

