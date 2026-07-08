import request, { serviceLong } from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/sys/sysFile')

// 导出标准CRUD接口（保持向后兼容）
export const listSysFilePage = baseApi.listPage
export const listSysFileAll = baseApi.listAll
export const saveSysFile = baseApi.save
export const deleteSysFile = baseApi.delete
export const bulkInsertSysFile = baseApi.bulkInsert
export const bulkUpdateSysFile = baseApi.bulkUpdate
export const bulkDeleteSysFile = baseApi.bulkDelete

// 自定义接口（带特殊responseType）
export const getSysFileById = (id) =>
    serviceLong({
        url: '/sys/sysFile/' + id,
        method: 'get',
        responseType: 'blob'
    })

export const fileUploadById = (fileId) =>
    serviceLong({
        url: '/sys/fileContent/export/' + fileId,
        method: 'get',
        type: 'blob',
        responseType: 'arraybuffer'
    })
