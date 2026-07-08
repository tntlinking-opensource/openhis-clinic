import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/member/memberManagement')

// 标准CRUD接口
export const getMemberManagementById = baseApi.getById
export const listMemberManagementPage = baseApi.listPage
export const listMemberManagementAll = baseApi.listAll
export const saveMemberManagement = baseApi.save
export const deleteMemberManagement = baseApi.delete
export const bulkInsertMemberManagement = baseApi.bulkInsert
export const bulkUpdateMemberManagement = baseApi.bulkUpdate
export const bulkDeleteMemberManagement = baseApi.bulkDelete

// 自定义接口
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

export const getMember = (recipleInfo) =>
    request({
        url: '/member/memberManagement/getMember',
        method: 'post',
        data: recipleInfo
    })
