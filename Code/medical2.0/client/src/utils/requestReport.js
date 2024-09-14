import axios from 'axios'
import { getLocalToken } from '@/utils/auth'

// 创建axios实例
const service = axios.create({
    // api的base_url
    baseURL: process.env.REPORT_SERVER_URL,
    // baseURL: 'http://59.80.34.149/dataease/backend/',
    // 请求超时时间
    timeout: 40000
})

// request拦截器
service.interceptors.request.use(config => {
    // Do something before request is sent
    if (getLocalToken()) {
        // 让每个请求携带token--['X-Token']为自定义key 请根据实际情况自行修改
        config.headers['Authorization'] = getLocalToken()
    }
  
  return config
}, error => {
    // Do something with request error
  Promise.reject(error)
})

// respone拦截器
service.interceptors.response.use(
    response => {
      return response.data
    },
    error => {
      let errorData = {
        type: 'error',
        code: error.response && error.response.status ? error.response.status: '1',
        msg: error.message ? error.message: '未知的错误',
        data: error.response && error.response.data ? error.response.data: error
      }

      return errorData
    })

export default service
