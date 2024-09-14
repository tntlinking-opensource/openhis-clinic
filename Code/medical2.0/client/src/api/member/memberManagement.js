import request from '@/utils/request'

export const getMemberManagementById = (id) =>
    request({
        url: '/member/memberManagement/' + id,
        method: 'get'
    })
export const getByPatientId = (id) =>
    request({
        url: '/member/memberManagement/getByPatientId/' + id,
        method: 'get'
    })

export const getPoverty = (id) =>
  request({
    url: '/toll/outpatientLog/getPoverty/' + id,
    method: 'post'
  })

export const listMemberManagementPage = (search) =>
    request({
        url: '/member/memberManagement/list',
        method: 'post',
        data: search
    })

export const listMemberManagementAll = (search) =>
    request({
        url: '/member/memberManagement/listAll',
        method: 'post',
        data: search
    })


export const saveMemberManagement = (memberManagement) =>
    request({
        url: '/member/memberManagement/save',
        method: 'post',
        data: memberManagement
    })

export const deleteMemberManagement = (memberManagement) =>
    request({
        url: '/member/memberManagement/delete',
        method: 'post',
        data: memberManagement
    })

export const bulkInsertMemberManagement = (memberManagements) =>
    request({
        url: '/member/memberManagement/bulkInsert',
        method: 'post',
        data: memberManagements
    })

export const bulkUpdateMemberManagement = (memberManagements) =>
    request({
        url: '/member/memberManagement/bulkUpdate',
        method: 'post',
        data: memberManagements
    })

export const bulkDeleteMemberManagement = (memberManagements) =>
    request({
        url: '/member/memberManagement/bulkDelete',
        method: 'post',
        data: memberManagements
    })
export const getMember = (recipleInfo) =>
    request({
        url: '/member/memberManagement/getMember',
        method: 'post',
        data: recipleInfo
    })
