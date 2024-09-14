import request from '@/utils/request'

export const listSchemaConstraintAll = (tableName) =>
    request({
        url: '/gen/schemaConstraint/listAll/' + tableName,
        method: 'get'
    })
