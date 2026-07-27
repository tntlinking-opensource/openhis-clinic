/**
 * 报表专用HTTP客户端
 * 用于报表模块的API请求，使用 Authorization 头而非 X-Token
 */
import axios from 'axios'
import { getLocalToken, removeLocalToken } from '@/utils/auth'
import { handleSessionExpired } from '@/utils/sessionExpired'

// 创建axios实例
const service = axios.create({
    baseURL: process.env.REPORT_SERVER_URL,
    timeout: 40000
})

// request拦截器
service.interceptors.request.use(config => {
    if (getLocalToken()) {
        config.headers['Authorization'] = getLocalToken()
    }
    return config
}, error => {
    return Promise.reject(error)
})

// response拦截器
service.interceptors.response.use(
    response => {
        return response.data
    },
    error => {
        let errorData = {
            type: 'error',
            code: error.response && error.response.status ? error.response.status : '1',
            msg: error.message ? error.message : '未知的错误',
            data: error.response && error.response.data ? error.response.data : error
        }

        // 处理 401 错误（未授权/未登录）— 与 request.js 共用全局去重的会话过期处理
        if (error.response && error.response.status === 401) {
            removeLocalToken()
            handleSessionExpired()
        }

        return Promise.reject(errorData)
    })

export default service
