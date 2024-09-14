import request from '@/utils/request'

export const getAdministrativeDivisionById = (id) =>
    request({
        url: '/org/administrativeDivision/' + id,
        method: 'get'
    })

export const listAdministrativeDivisionPage = (search) =>
    request({
        url: '/org/administrativeDivision/list',
        method: 'post',
        data: search
    })

export const listAdministrativeDivisionAll = (search) =>
    request({
        url: '/org/administrativeDivision/listAll',
        method: 'post',
        data: search
    })


export const saveAdministrativeDivision = (administrativeDivision) => 
    request({
        url: '/org/administrativeDivision/save',
        method: 'post',
        data: administrativeDivision
    })
  
export const deleteAdministrativeDivision = (administrativeDivision) =>
    request({
        url: '/org/administrativeDivision/delete',
        method: 'post',
        data: administrativeDivision
    })
    
export const bulkInsertAdministrativeDivision = (administrativeDivisions) =>
    request({
        url: '/org/administrativeDivision/bulkInsert',
        method: 'post',
        data: administrativeDivisions
    })
    
export const bulkUpdateAdministrativeDivision = (administrativeDivisions) =>
    request({
        url: '/org/administrativeDivision/bulkUpdate',
        method: 'post',
        data: administrativeDivisions
    })

export const bulkDeleteAdministrativeDivision = (administrativeDivisions) =>
    request({
        url: '/org/administrativeDivision/bulkDelete',
        method: 'post',
        data: administrativeDivisions
    })
    
