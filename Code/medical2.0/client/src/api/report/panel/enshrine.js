import request from '@/utils/requestReport'

export function saveEnshrine(panelGroupId) {
  return request({
    url: '/store/' + panelGroupId,
    method: 'post',
    loading: true
  })
}

export function deleteEnshrine(id) {
  return request({
    url: '/store/remove/' + id,
    method: 'post',
    loading: true
  })
}

export function enshrineList(data) {
  return request({
    url: '/store/list',
    method: 'post',
    loading: true,
    data
  })
}

