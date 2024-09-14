import request from '@/utils/request'

export const getThemeById = (id) =>
    request({
        url: '/sys/theme/' + id,
        method: 'get'
    })

export const listThemePage = (search) =>
    request({
        url: '/sys/theme/list',
        method: 'post',
        data: search
    })

export const listThemeAll = (search) =>
    request({
        url: '/sys/theme/listAll',
        method: 'post',
        data: search
    })


export const saveTheme = (theme) => 
    request({
        url: '/sys/theme/save',
        method: 'post',
        data: theme
    })

export const deleteTheme = (theme) =>
    request({
        url: '/sys/theme/delete',
        method: 'post',
        data: theme
    })
    
export const bulkInsertTheme = (themes) =>
    request({
        url: '/sys/theme/bulkInsert',
        method: 'post',
        data: themes
    })
    
export const bulkUpdateTheme = (themes) =>
    request({
        url: '/sys/theme/bulkUpdate',
        method: 'post',
        data: themes
    })

export const bulkDeleteTheme = (themes) =>
    request({
        url: '/sys/theme/bulkDelete',
        method: 'post',
        data: themes
    })
    
