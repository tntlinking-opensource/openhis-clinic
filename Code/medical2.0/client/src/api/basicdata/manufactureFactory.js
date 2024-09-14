import request from '@/utils/request'

export const getManufactureFactoryById = (id) =>
    request({
        url: '/basicdata/manufactureFactory/' + id,
        method: 'get'
    })

export const listManufactureFactoryPage = (search) =>
    request({
        url: '/basicdata/manufactureFactory/list',
        method: 'post',
        data: search
    })

export const listManufactureFactoryAll = (search) =>
    request({
        url: '/basicdata/manufactureFactory/listAll',
        method: 'post',
        data: search
    })


export const saveManufactureFactory = (manufactureFactory) => 
    request({
        url: '/basicdata/manufactureFactory/save',
        method: 'post',
        data: manufactureFactory
    })
  
export const deleteManufactureFactory = (manufactureFactory) =>
    request({
        url: '/basicdata/manufactureFactory/delete',
        method: 'post',
        data: manufactureFactory
    })
    
export const bulkInsertManufactureFactory = (manufactureFactorys) =>
    request({
        url: '/basicdata/manufactureFactory/bulkInsert',
        method: 'post',
        data: manufactureFactorys
    })
    
export const bulkUpdateManufactureFactory = (manufactureFactorys) =>
    request({
        url: '/basicdata/manufactureFactory/bulkUpdate',
        method: 'post',
        data: manufactureFactorys
    })

export const bulkDeleteManufactureFactory = (manufactureFactorys) =>
    request({
        url: '/basicdata/manufactureFactory/bulkDelete',
        method: 'post',
        data: manufactureFactorys
    })

export const ureportTest = (id) =>
    request({
        url: '/basicdata/manufactureFactory/bulkDelete',
        method: 'get'
    })
    
