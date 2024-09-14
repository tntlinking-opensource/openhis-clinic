import axios from 'axios'
import { getLocalToken } from '@/utils/auth'

// 创建axios实例
const service = axios.create({
    // api的base_url
    baseURL: process.env.BASE_API,
    // 请求超时时间
    timeout: 150000
})

// request拦截器
service.interceptors.request.use(config => {
    // Do something before request is sent
    if (getLocalToken()) {
        // 让每个请求携带token--['X-Token']为自定义key 请根据实际情况自行修改
        config.headers['X-Token'] = getLocalToken()
      //  config.headers['User-Id']=JSON.parse(sessionStorage.currentCompany).id
    }
    return config
}, error => {
    // Do something with request error
    console.error(error) // for debug
    Promise.reject(error)
})

// respone拦截器
service.interceptors.response.use(
    response => {

        if(response.headers["content-disposition"]){
            let res = {
                data:response.data,
                headers:response.headers["content-disposition"]
            }
            return res;
        }else{
            console.log(response.data);
            return response.data
        }

    },
    error => {
        console.error(error.response ? error.response : error)
        let errorData = {
            type: 'error',
            code: error.response && error.response.status ? error.response.status: '1',
            msg: error.message ? error.message: '未知的错误',
            data: error.response && error.response.data ? error.response.data: error
        }

        // 工作流异常处理
        if(error.response && error.response.data && error.response.data.message) {
            errorData.msg = error.response.data.message
        }

        return errorData
    })

export default service
