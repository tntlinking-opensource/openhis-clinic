
import request from '@/utils/request' 

export const listByBoutbound = (outboundId) =>
    request({
        url: '/stock/supplierOutboundDetail/listByBoutbound/'+outboundId,
        method: 'get' 
    }) 
