import request from '@/utils/request'

export const getPropertySetById = (id) =>
    request({
        url: '/sys/propertySet/' + id,
        method: 'get'
    })

export const listPropertySetPage = (search) =>
    request({
        url: '/sys/propertySet/list',
        method: 'post',
        data: search
    })

export const listPropertySetAll = (search) =>
    request({
        url: '/sys/propertySet/listAll',
        method: 'post',
        data: search
    })


export const savePropertySet = (propertySet) => 
    request({
        url: '/sys/propertySet/save',
        method: 'post',
        data: propertySet
    })

export const deletePropertySet = (propertySet) =>
    request({
        url: '/sys/propertySet/delete',
        method: 'post',
        data: propertySet
    })
    
export const bulkInsertPropertySet = (propertySets) =>
    request({
        url: '/sys/propertySet/bulkInsert',
        method: 'post',
        data: propertySets
    })
    
export const bulkUpdatePropertySet = (propertySets) =>
    request({
        url: '/sys/propertySet/bulkUpdate',
        method: 'post',
        data: propertySets
    })

export const bulkDeletePropertySet = (propertySets) =>
    request({
        url: '/sys/propertySet/bulkDelete',
        method: 'post',
        data: propertySets
    })
    
