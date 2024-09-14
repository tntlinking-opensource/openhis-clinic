import request from '@/utils/request'

export const createDeployment = (deploy) =>
    request({
      url: '/rest/deployment/create',
      method: 'post',
      data: deploy
    })

export const deleteDeployment = (id) =>
  request({
    url: '/rest/deployment/' + id,
    method: 'DELETE'
  })