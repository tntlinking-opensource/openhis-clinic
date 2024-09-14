import request from '@/utils/request'

export const getSubordinateClinic = (formData) =>
  request({
      url: '/org/company/getSubordinateClinic',
      method: 'get',
      data: formData
  })
