import request from '@/utils/request'

export const getMemberSetById = (id) =>
    request({
        url: '/member/memberSet/' + id,
        method: 'get'
    })

export const listMemberSetPage = (search) =>
    request({
        url: '/member/memberSet/list',
        method: 'post',
        data: search
    })

export const listMemberSetAll = (search) =>
    request({
        url: '/member/memberSet/listAll',
        method: 'post',
        data: search
    })


export const saveMemberSet = (memberSet) => 
    request({
        url: '/member/memberSet/save',
        method: 'post',
        data: memberSet
    })
  
export const deleteMemberSet = (memberSet) =>
    request({
        url: '/member/memberSet/delete',
        method: 'post',
        data: memberSet
    })
    
export const bulkInsertMemberSet = (memberSets) =>
    request({
        url: '/member/memberSet/bulkInsert',
        method: 'post',
        data: memberSets
    })
    
export const bulkUpdateMemberSet = (memberSets) =>
    request({
        url: '/member/memberSet/bulkUpdate',
        method: 'post',
        data: memberSets
    })

export const bulkDeleteMemberSet = (memberSets) =>
    request({
        url: '/member/memberSet/bulkDelete',
        method: 'post',
        data: memberSets
    })
    
