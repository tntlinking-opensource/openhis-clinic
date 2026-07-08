import request from '@/utils/request'
import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/treatment/costItem')

// 标准CRUD接口
export const getCostItemById = baseApi.getById
export const listCostItemPage = baseApi.listPage
export const listCostItemAll = baseApi.listAll
export const saveCostItem = (data) =>
    request({
        url: '/treatment/costItem/saveDto',
        method: 'post',
        data
    })
export const deleteCostItem = baseApi.delete
export const bulkInsertCostItem = baseApi.bulkInsert
export const bulkUpdateCostItem = baseApi.bulkUpdate
export const bulkDeleteCostItem = baseApi.bulkDelete

// 自定义接口
// 上级租户的诊疗项目列表信息
export const listCostItemPageByParent = search =>
  request({
    url: "/treatment/costItem/listByInstitution",
    method: "post",
    data: search
  });

// 获取院版所有诊疗项目
export const listCostItemPageByInstitutionAll = search =>
  request({
    url: "/hosdata/HosCollectData/getHosInstitutions",
    method: "post",
    data: search
  });

// 选择租户的诊疗项目同步至诊所
export const listCostItemPageByInstitution = search =>
  request({
    url: "/treatment/costItem/syncToClinic",
    method: "post",
    data: search
  });

// 选择院版诊疗项目同步
export const listCostItemPageByInstitutionSync = search =>
  request({
    url: "/hosdata/HosCollectData/HisInstitutionsToClinic",
    method: "post",
    data: search
  });
