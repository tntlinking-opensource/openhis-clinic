import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/noticesend/noticeReceiveSite')

// 导出标准CRUD接口（保持向后兼容）
export const getNoticeReceiveSiteById = baseApi.getById
export const listNoticeReceiveSitePage = baseApi.listPage
export const listNoticeReceiveSiteAll = baseApi.listAll
export const saveNoticeReceiveSite = baseApi.save
export const deleteNoticeReceiveSite = baseApi.delete
export const bulkInsertNoticeReceiveSite = baseApi.bulkInsert
export const bulkUpdateNoticeReceiveSite = baseApi.bulkUpdate
export const bulkDeleteNoticeReceiveSite = baseApi.bulkDelete
