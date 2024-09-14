import request from '@/utils/request'

export const getSysParamConfigById = (id) =>
    request({
        url: '/sys/paramConfig/' + id,
        method: 'get'
    })

export const listSysParamConfigPage = (search) =>
    request({
        url: '/sys/paramConfig/list',
        method: 'post',
        data: search
    })

export const listSysParamConfigAll = (search) =>
    request({
        url: '/sys/paramConfig/listAll',
        method: 'post',
        data: search
    })


export const saveSysParamConfig = (paramConfig) =>
    request({
        url: '/sys/paramConfig/save',
        method: 'post',
        data: paramConfig
    })

export const saveSysParamConfigList = (paramConfig) =>
    request({
        url: '/sys/paramConfig/save/list',
        method: 'post',
        data: paramConfig
    })

export const deleteSysParamConfig = (paramConfig) =>
    request({
        url: '/sys/paramConfig/delete',
        method: 'delete',
        data: paramConfig
    })
