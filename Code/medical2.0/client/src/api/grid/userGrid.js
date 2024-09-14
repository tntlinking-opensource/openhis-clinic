import request from '@/utils/request'

export const getUserGridById = (id) =>
    request({
        url: '/grid/userGrid/' + id,
        method: 'get'
    })

export const listUserGridPage = (search) =>
    request({
        url: '/grid/userGrid/list',
        method: 'post',
        data: search
    })

export const listUserGridAll = (search) =>
    request({
        url: '/grid/userGrid/listAll',
        method: 'post',
        data: search
    })


export const saveUserGrid = (userGrid) => 
    request({
        url: '/grid/userGrid/save',
        method: 'post',
        data: userGrid
    })

export const deleteUserGrid = (userGrid) =>
    request({
        url: '/grid/userGrid/delete',
        method: 'post',
        data: userGrid
    })
    
export const bulkInsertUserGrid = (userGrids) =>
    request({
        url: '/grid/userGrid/bulkInsert',
        method: 'post',
        data: userGrids
    })
    
export const bulkUpdateUserGrid = (userGrids) =>
    request({
        url: '/grid/userGrid/bulkUpdate',
        method: 'post',
        data: userGrids
    })

export const bulkDeleteUserGrid = (userGrids) =>
    request({
        url: '/grid/userGrid/bulkDelete',
        method: 'post',
        data: userGrids
    })
    
