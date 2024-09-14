import request from '@/utils/request'

export const getLesseeById = (id) =>
    request({
        url: '/org/lessee/' + id,
        method: 'get'
    })

export const listLesseePage = (search) =>
    request({
        url: '/org/lessee/list',
        method: 'post',
        data: search
    })

export const listLesseeAll = (search) =>
    request({
        url: '/org/lessee/listAll',
        method: 'post',
        data: search
    })


export const saveLessee = (lessee) => 
    request({
        url: '/org/lessee/save',
        method: 'post',
        data: lessee
    })
  
export const deleteLessee = (lessee) =>
    request({
        url: '/org/lessee/delete',
        method: 'post',
        data: lessee
    })
    
export const bulkInsertLessee = (lessees) =>
    request({
        url: '/org/lessee/bulkInsert',
        method: 'post',
        data: lessees
    })
    
export const bulkUpdateLessee = (lessees) =>
    request({
        url: '/org/lessee/bulkUpdate',
        method: 'post',
        data: lessees
    })

export const bulkDeleteLessee = (lessees) =>
    request({
        url: '/org/lessee/bulkDelete',
        method: 'post',
        data: lessees
    })
    
