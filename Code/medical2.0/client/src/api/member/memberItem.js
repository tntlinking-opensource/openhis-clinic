import request from '@/utils/request'

export const getMemberItemById = (id) =>
    request({
        url: '/member/memberItem/' + id,
        method: 'get'
    })

export const listMemberItemPage = (search) =>
    request({
        url: '/member/memberItem/list',
        method: 'post',
        data: search
    })

export const listMemberItemAll = (search) =>
    request({
        url: '/member/memberItem/listAll',
        method: 'post',
        data: search
    })


export const saveMemberItem = (memberItem) => 
    request({
        url: '/member/memberItem/save',
        method: 'post',
        data: memberItem
    })
  
export const deleteMemberItem = (memberItem) =>
    request({
        url: '/member/memberItem/delete',
        method: 'post',
        data: memberItem
    })
    
export const bulkInsertMemberItem = (memberItems) =>
    request({
        url: '/member/memberItem/bulkInsert',
        method: 'post',
        data: memberItems
    })
    
export const bulkUpdateMemberItem = (memberItems) =>
    request({
        url: '/member/memberItem/bulkUpdate',
        method: 'post',
        data: memberItems
    })

export const bulkDeleteMemberItem = (memberItems) =>
    request({
        url: '/member/memberItem/bulkDelete',
        method: 'post',
        data: memberItems
    })
    
