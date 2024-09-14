import request from '@/utils/request'

export const columnQuery = (schemeId, tableId) =>
    request({
        url: '/common/columnQuery',
        method: 'get',
        params: {
            schemeId: schemeId,
            tableId: tableId
        }        
    })
   
