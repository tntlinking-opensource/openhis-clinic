import request from '@/utils/request'

export const getDispensingById = (id) =>
    request({
        url: '/stock/dispensing/' + id,
        method: 'get'
    })

export const listDispensingPage = (search) =>
    request({
        url: '/stock/dispensing/list',
        method: 'post',
        data: search
    })

export const listDispensingAll = (search) =>
    request({
        url: '/stock/dispensing/listAll',
        method: 'post',
        data: search
    })


export const saveDispensing = (dispensing) => 
    request({
        url: '/stock/dispensing/save',
        method: 'post',
        data: dispensing
    })
  
export const deleteDispensing = (dispensing) =>
    request({
        url: '/stock/dispensing/delete',
        method: 'post',
        data: dispensing
    })
    
export const bulkInsertDispensing = (dispensings) =>
    request({
        url: '/stock/dispensing/bulkInsert',
        method: 'post',
        data: dispensings
    })
    
export const bulkUpdateDispensing = (dispensings) =>
    request({
        url: '/stock/dispensing/bulkUpdate',
        method: 'post',
        data: dispensings
    })

export const bulkDeleteDispensing = (dispensings) =>
    request({
        url: '/stock/dispensing/bulkDelete',
        method: 'post',
        data: dispensings
    })
export const getList = (data) =>
    request({
        url: '/stock/dispensing/reportList',
        method: 'post',
        data: data
    })
export const getOrganizationList = (data) =>
    request({
        url: '/stock/dispensing/getOrganizationList',
        method: 'post',
        data: data
    })
export const getAmount = (data) =>
    request({
        url: '/stock/dispensing/reportAmount',
        method: 'post',
        data: data
    })
export const getOrganizationAmount = (data) =>
    request({
        url: '/stock/dispensing/getOrganizationAmount',
        method: 'post',
        data: data
    })
    
