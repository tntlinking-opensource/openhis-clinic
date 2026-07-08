/**
 * 标准API请求客户端
 * 提供两个axios实例：
 * - service: 默认30秒超时，用于常规API请求
 * - serviceLong: 5分钟超时，用于批量操作、导出等
 */
import axios from 'axios'
import { getLocalToken, removeLocalToken, clearLocalData } from '@/utils/auth'
import router from '@/router'

// 创建 axios 实例 - 默认超时30秒
const service = axios.create({
    baseURL: process.env.BASE_API,
    timeout: 30000
})

// 创建长超时实例 - 用于批量操作、导出等，超时5分钟
export const serviceLong = axios.create({
    baseURL: process.env.BASE_API,
    timeout: 300000
})

// request拦截器 - 通用
const requestInterceptor = config => {
    if (getLocalToken()) {
        config.headers['X-Token'] = getLocalToken()
    }
    return config
}

const requestInterceptorError = error => {
    console.error(error)
    return Promise.reject(error)
}

// response拦截器 - 通用
const responseInterceptor = response => {
    if (response.headers["content-disposition"]) {
        let res = {
            data: response.data,
            headers: response.headers["content-disposition"]
        }
        return res
    } else {
        // 统一将 code 转为数字，解决后端返回字符串 "100" 而前端用 === 100 比较的问题
        if (response.data && response.data.code !== undefined) {
            response.data.code = Number(response.data.code)
        }

        const data = response.data

        // 自动处理会话过期（code 20011）
        if (data && data.code === 20011) {
            clearLocalData()
            ELEMENT.Message({
                message: '登录已过期，请重新登录',
                type: 'warning',
                duration: 2000
            })
            router.replace('/login')
            return Promise.reject(data)
        }

        // 业务警告和错误由组件自行处理（通过 showMessage/outputError）
        // 拦截器只负责会话过期处理，避免与组件层重复提示

        return data
    }
}

const responseInterceptorError = error => {
    console.error(error.response ? error.response : error)
    const respData = error.response && error.response.data ? error.response.data : null
    let errorData = {
        type: 'error',
        code: error.response && error.response.status ? error.response.status : '1',
        msg: '未知的错误',
        data: respData || error
    }

    // 优先使用后端返回的业务错误信息
    if (respData) {
        if (respData.msg) {
            errorData.msg = respData.msg
        } else if (respData.message) {
            errorData.msg = respData.message
        }
    } else if (error.message) {
        errorData.msg = error.message
    }

    // 处理 401 错误（未授权/未登录）
    if (error.response && error.response.status === 401) {
        clearLocalData()
        ELEMENT.Message({
            message: '登录已过期，请重新登录',
            type: 'warning',
            duration: 2000
        })
        router.replace('/login')
    } else {
        // 网络错误等非业务错误也自动提示
        ELEMENT.Message({
            showClose: true,
            message: errorData.msg,
            type: 'error',
            duration: 5000
        })
    }

    return Promise.reject(errorData)
}

// 应用拦截器到两个实例
service.interceptors.request.use(requestInterceptor, requestInterceptorError)
service.interceptors.response.use(responseInterceptor, responseInterceptorError)

serviceLong.interceptors.request.use(requestInterceptor, requestInterceptorError)
serviceLong.interceptors.response.use(responseInterceptor, responseInterceptorError)

export default service
