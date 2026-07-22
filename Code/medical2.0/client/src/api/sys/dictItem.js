import { createCrudApi } from '@/utils/apiFactory'
import request from '@/utils/request'

const baseApi = createCrudApi('/sys/dictItem')

// 导出标准CRUD接口（保持向后兼容）
export const getDictItemById = baseApi.getById
export const listDictItemPage = baseApi.listPage
export const listDictItemAll = baseApi.listAll
export const saveDictItem = baseApi.save
export const deleteDictItem = baseApi.delete

// 新增：按字典类型 code 获取字典项（推荐使用，替代硬编码雪花 ID）
export const listDictItemByCode = (code) =>
  request({ url: '/sys/dictItem/listByCode/' + code, method: 'get' })
