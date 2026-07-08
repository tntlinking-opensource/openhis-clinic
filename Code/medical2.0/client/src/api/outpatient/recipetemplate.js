import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/outpatient/recipetemplate')

// 导出标准CRUD接口（保持向后兼容）
export const getRecipetemplateById = baseApi.getById
export const listRecipetemplatePage = baseApi.listPage
export const listRecipetemplateAll = baseApi.listAll
export const saveRecipetemplate = baseApi.save
export const deleteRecipetemplate = baseApi.delete
export const bulkInsertRecipetemplate = baseApi.bulkInsert
export const bulkUpdateRecipetemplate = baseApi.bulkUpdate
export const bulkDeleteRecipetemplate = baseApi.bulkDelete

// 自定义接口
export const allSave = (recipetemplate) =>
    request({
        url: '/outpatient/recipetemplate/allSave',
        method: 'post',
        data: recipetemplate
    })
