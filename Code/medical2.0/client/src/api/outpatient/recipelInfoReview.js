import { createCrudApi } from '@/utils/apiFactory'
import request from '@/utils/request'

const baseApi = createCrudApi('/outpatient/review')

// Re-export standard CRUD with original names for backward compatibility
export const getRecipelInfoReviewById = baseApi.getById
export const listRecipelInfoReviewPage = baseApi.listPage
export const listRecipelInfoReviewAll = baseApi.listAll
export const saveRecipelInfoReview = baseApi.save
export const deleteRecipelInfoReview = baseApi.delete

// Keep custom endpoints as-is
export const getRecipelInfoReviewByRecipelInfoId = (id) =>
    request({
        url: '/outpatient/review/recipelInfo/' + id,
        method: 'get'
    })
export const getReviewFormByRecipelInfoId = (id) =>
    request({
        url: '/outpatient/review/form/' + id,
        method: 'get'
    })

export const listPageStatement = (search) =>
  request({
    url: '/outpatient/review/list/statement',
    method: 'post',
    data: search
  })

export const getPrescriptionStatistics = (param) =>
  request({
    url: `/outpatient/review/prescription/statistics/${param.id}?startTime=${param.startTime}&endTime=${param.endTime}`,
    method: 'get'
  })


//处方撤销
export const cancelElectronicPrescription = (recipelInfoReview, undoRea) =>
  request({
    url: '/outpatient/review/pre/revoke',
    method: 'post',
    data: recipelInfoReview,  // 请求体
    params: {
      undoRea: undoRea       // 查询参数
    }
  });

//取药查询
export const getMdMedicineInfo = (recipelInfoReview) =>
  request({
    url: '/outpatient/review/pre/getMdMedicineInfo',
    method: 'post',
    data: recipelInfoReview,  // 请求体
  });

//审核查询
export const getMdExamineInfo = (recipelInfoReview) =>
  request({
    url: '/outpatient/review/pre/getMdExamineInfo',
    method: 'post',
    data: recipelInfoReview,  // 请求体
  });

/*export const bulkInsertRecipelInfoReview = (RecipelInfoReviews) =>
    request({
        url: '/outpatient/review/bulkInsert',
        method: 'post',
        data: RecipelInfoReviews
    })

export const bulkUpdateRecipelInfoReview = (RecipelInfoReviews) =>
    request({
        url: '/outpatient/review/bulkUpdate',
        method: 'post',
        data: RecipelInfoReviews
    })

export const bulkDeleteRecipelInfoReview = (RecipelInfoReviews) =>
    request({
        url: '/outpatient/review/bulkDelete',
        method: 'post',
        data: RecipelInfoReviews
    })*/
