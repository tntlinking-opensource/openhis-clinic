import request from '@/utils/request'

export const getCompanyById = (id) =>
    request({
        url: '/org/company/' + id,
        method: 'get'
    })

export const listCompanyPage = (search) =>
    request({
        url: '/org/company/list',
        method: 'post',
        data: search
    })

export const listCompanyAll = (search) =>
    request({
        url: '/org/company/listAll',
        method: 'post',
        data: search
    })
export const getCompanys = (id) =>
    request({
        url: '/org/company/getCompanys',
        method: 'post',
        data: id
    })

export const treeCompany = (search) =>
    request({
        url: '/org/company/tree',
        method: 'post',
        data: search
    })

export const saveCompany = (formData) =>
    request({
        url: '/org/company/save',
        method: 'post',
        data: formData
    })
  
export const deleteCompany = (company) =>
    request({
        url: '/org/company/delete',
        method: 'post',
        data: company
    })
    
export const bulkInsertCompany = (companys) =>
    request({
        url: '/org/company/bulkInsert',
        method: 'post',
        data: companys
    })
    
export const bulkUpdateCompany = (companys) =>
    request({
        url: '/org/company/bulkUpdate',
        method: 'post',
        data: companys
    })

export const bulkDeleteCompany = (companys) =>
    request({
        url: '/org/company/bulkDelete',
        method: 'post',
        data: companys
    })
    
