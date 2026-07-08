import request from '@/utils/requestReport'

/* export function saveShare(data) {
  return request({
    url: '/share/',
    method: 'post',
    loading: true,
    data
  })
} */

export function shareTargets(panelId) {
  return request({
    url: '/share/queryTargets/' + panelId,
    method: 'post',
    loading: true
  })
}

export function removeShares(data) {
  return request({
    url: '/share/removeShares/',
    method: 'post',
    loading: true,
    data
  })
}

export function loadShares(data) {
  return request({
    url: '/share/queryWithResourceId',
    method: 'post',
    loading: true,
    data
  })
}

export function loadTree(data) {
  return request({
    url: '/share/treeList',
    method: 'post',
    loading: true,
    data
  })
}

export function loadShareOutTree() {
  return request({
    url: '/share/shareOut',
    method: 'post',
    loading: true
  })
}

export function fineSave(data) {
  return request({
    url: '/share/fineSave',
    method: 'post',
    loading: true,
    data
  })
}

