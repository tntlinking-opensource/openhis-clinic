import request from '@/utils/request'

export const saveblmb = (search) =>
  request({
    url: '/outpatient/blmb/save',
    method: 'post',
    data: search
  })

export const getblmblist = (search) =>
  request({
    url: '/outpatient/blmb/getblmblist',
    method: 'post',
    data: search
  })

export const deletembbm = (search) =>
  request({
    url: '/outpatient/blmb/deletembbm',
    method: 'post',
    data: search
  })

export const selectmbbm = (search) =>
  request({
    url: '/outpatient/blmb/selectmbbm',
    method: 'post',
    data: search
  })

export const updatembbm = (search) =>
  request({
    url: '/outpatient/blmb/updatembbm',
    method: 'post',
    data: search
  })
