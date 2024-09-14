import request from '@/utils/request'

export const getRecipetemplateById = (id) =>
    request({
        url: '/outpatient/recipetemplate/' + id,
        method: 'get'
    })

export const listRecipetemplatePage = (search) =>
    request({
        url: '/outpatient/recipetemplate/list',
        method: 'post',
        data: search
    })

export const listRecipetemplateAll = (search) =>
    request({
        url: '/outpatient/recipetemplate/listAll',
        method: 'post',
        data: search
    })


export const saveRecipetemplate = (recipetemplate) => 
    request({
        url: '/outpatient/recipetemplate/save',
        method: 'post',
        data: recipetemplate
    })
export const allSave = (recipetemplate) => 
    request({
        url: '/outpatient/recipetemplate/allSave',
        method: 'post',
        data: recipetemplate
    })
  
export const deleteRecipetemplate = (recipetemplate) =>
    request({
        url: '/outpatient/recipetemplate/delete',
        method: 'post',
        data: recipetemplate
    })
    
export const bulkInsertRecipetemplate = (recipetemplates) =>
    request({
        url: '/outpatient/recipetemplate/bulkInsert',
        method: 'post',
        data: recipetemplates
    })
    
export const bulkUpdateRecipetemplate = (recipetemplates) =>
    request({
        url: '/outpatient/recipetemplate/bulkUpdate',
        method: 'post',
        data: recipetemplates
    })

export const bulkDeleteRecipetemplate = (recipetemplates) =>
    request({
        url: '/outpatient/recipetemplate/bulkDelete',
        method: 'post',
        data: recipetemplates
    })
    
