import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/sys/paramConfig')

export const getSysParamConfigById = baseApi.getById
export const listSysParamConfigPage = baseApi.listPage
export const listSysParamConfigAll = baseApi.listAll
export const saveSysParamConfig = baseApi.save
export const deleteSysParamConfig = baseApi.delete

export const saveSysParamConfigList = (paramConfig) =>
    request({
        url: '/sys/paramConfig/save/list',
        method: 'post',
        data: paramConfig
    })
