import request from '@/utils/request'

export const getRecipelInfoReviewById = (id) =>
    request({
        url: '/outpatient/review/' + id,
        method: 'get'
    })

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

export const listRecipelInfoReviewPage = (search) =>
    request({
        url: '/outpatient/review/list',
        method: 'post',
        data: search
    })

export const listRecipelInfoReviewAll = (search) =>
    request({
        url: '/outpatient/review/listAll',
        method: 'post',
        data: search
    })


export const saveRecipelInfoReview = (RecipelInfoReview) =>
    request({
        url: '/outpatient/review/save',
        method: 'post',
        data: RecipelInfoReview
    })

export const deleteRecipelInfoReview = (RecipelInfoReview) =>
    request({
        url: '/outpatient/review/delete',
        method: 'post',
        data: RecipelInfoReview
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

