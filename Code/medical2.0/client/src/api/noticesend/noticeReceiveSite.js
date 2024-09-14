import request from '@/utils/request'

export const getNoticeReceiveSiteById = (id) =>
    request({
        url: '/noticesend/noticeReceiveSite/' + id,
        method: 'get'
    })

export const listNoticeReceiveSitePage = (search) =>
    request({
        url: '/noticesend/noticeReceiveSite/list',
        method: 'post',
        data: search
    })

export const listNoticeReceiveSiteAll = (search) =>
    request({
        url: '/noticesend/noticeReceiveSite/listAll',
        method: 'post',
        data: search
    })


export const saveNoticeReceiveSite = (noticeReceiveSite) => 
    request({
        url: '/noticesend/noticeReceiveSite/save',
        method: 'post',
        data: noticeReceiveSite
    })

export const deleteNoticeReceiveSite = (noticeReceiveSite) =>
    request({
        url: '/noticesend/noticeReceiveSite/delete',
        method: 'post',
        data: noticeReceiveSite
    })
    
export const bulkInsertNoticeReceiveSite = (noticeReceiveSites) =>
    request({
        url: '/noticesend/noticeReceiveSite/bulkInsert',
        method: 'post',
        data: noticeReceiveSites
    })
    
export const bulkUpdateNoticeReceiveSite = (noticeReceiveSites) =>
    request({
        url: '/noticesend/noticeReceiveSite/bulkUpdate',
        method: 'post',
        data: noticeReceiveSites
    })

export const bulkDeleteNoticeReceiveSite = (noticeReceiveSites) =>
    request({
        url: '/noticesend/noticeReceiveSite/bulkDelete',
        method: 'post',
        data: noticeReceiveSites
    })
    
