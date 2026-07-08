import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/member/memberSet')

export const getMemberSetById = baseApi.getById
export const listMemberSetPage = baseApi.listPage
export const listMemberSetAll = baseApi.listAll
export const saveMemberSet = baseApi.save
export const deleteMemberSet = baseApi.delete
export const bulkInsertMemberSet = baseApi.bulkInsert
export const bulkUpdateMemberSet = baseApi.bulkUpdate
export const bulkDeleteMemberSet = baseApi.bulkDelete
