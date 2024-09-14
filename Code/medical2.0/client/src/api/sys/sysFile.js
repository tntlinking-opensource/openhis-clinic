import request from '@/utils/request'

export const getSysFileById = (id) =>
    request({
        url: '/sys/sysFile/' + id,
        method: 'get',
        responseType: 'blob'
    })

export const listSysFilePage = (search) =>
    request({
        url: '/sys/sysFile/list',
        method: 'post',
        data: search
    })

export const listSysFileAll = (search) =>
    request({
        url: '/sys/sysFile/listAll',
        method: 'post',
        data: search
    })


export const saveSysFile = (sysFile) =>
    request({
        url: '/sys/sysFile/save',
        method: 'post',
        data: sysFile
    })

export const deleteSysFile = (sysFile) =>
    request({
        url: '/sys/sysFile/delete',
        method: 'post',
        data: sysFile
    })

export const bulkInsertSysFile = (sysFiles) =>
    request({
        url: '/sys/sysFile/bulkInsert',
        method: 'post',
        data: sysFiles
    })

export const bulkUpdateSysFile = (sysFiles) =>
    request({
        url: '/sys/sysFile/bulkUpdate',
        method: 'post',
        data: sysFiles
    })

export const bulkDeleteSysFile = (sysFiles) =>
    request({
        url: '/sys/sysFile/bulkDelete',
        method: 'post',
        data: sysFiles
    })

export const fileUploadById = (fileId) =>
  request({
    url: '/sys/fileContent/export/' + fileId,
    method: 'get',
    type:'blob',
    responseType: 'arraybuffer'
  })
