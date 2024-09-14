import request from '@/utils/request'

export const getCodeRuleById = (id) =>
    request({
        url: '/sys/codeRule/' + id,
        method: 'get'
    })

export const listCodeRulePage = (search) =>
    request({
        url: '/sys/codeRule/list',
        method: 'post',
        data: search
    })

export const listCodeRuleAll = (search) =>
    request({
        url: '/sys/codeRule/listAll',
        method: 'post',
        data: search
    })


export const saveCodeRule = (codeRule) => 
    request({
        url: '/sys/codeRule/save',
        method: 'post',
        data: codeRule
    })

export const deleteCodeRule = (codeRule) =>
    request({
        url: '/sys/codeRule/delete',
        method: 'post',
        data: codeRule
    })
    
export const bulkInsertCodeRule = (codeRules) =>
    request({
        url: '/sys/codeRule/bulkInsert',
        method: 'post',
        data: codeRules
    })
    
export const bulkUpdateCodeRule = (codeRules) =>
    request({
        url: '/sys/codeRule/bulkUpdate',
        method: 'post',
        data: codeRules
    })

export const bulkDeleteCodeRule = (codeRules) =>
    request({
        url: '/sys/codeRule/bulkDelete',
        method: 'post',
        data: codeRules
    })
    
