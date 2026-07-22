import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/member/memberItem')

export const getMemberItemById = baseApi.getById
export const listMemberItemPage = baseApi.listPage
export const listMemberItemAll = baseApi.listAll
export const saveMemberItem = baseApi.save
export const deleteMemberItem = baseApi.delete
export const bulkInsertMemberItem = baseApi.bulkInsert
export const bulkUpdateMemberItem = baseApi.bulkUpdate
export const bulkDeleteMemberItem = baseApi.bulkDelete
