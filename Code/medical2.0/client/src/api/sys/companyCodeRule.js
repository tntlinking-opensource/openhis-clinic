import request from '@/utils/request'

export const getCompanyCodeRuleById = (id) =>
    request({
        url: '/sys/companyCodeRule/' + id,
        method: 'get'
    })

export const listCompanyCodeRulePage = (search) =>
    request({
        url: '/sys/companyCodeRule/list',
        method: 'post',
        data: search
    })

export const listCompanyCodeRuleAll = (search) =>
    request({
        url: '/sys/companyCodeRule/listAll',
        method: 'post',
        data: search
    })


export const saveCompanyCodeRule = (companyCodeRule) => 
    request({
        url: '/sys/companyCodeRule/save',
        method: 'post',
        data: companyCodeRule
    })

export const deleteCompanyCodeRule = (companyCodeRule) =>
    request({
        url: '/sys/companyCodeRule/delete',
        method: 'post',
        data: companyCodeRule
    })
    
