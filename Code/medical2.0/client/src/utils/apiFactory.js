/**
 * API工厂函数
 * 生成标准CRUD接口，消除API层重复代码
 *
 * 使用方式：
 * // api/stock/supplier.js
 * import { createCrudApi } from '@/utils/apiFactory'
 *
 * const baseApi = createCrudApi('/stock/supplier')
 *
 * // 添加自定义接口
 * export const customMethod = (data) => request({ url: '/stock/supplier/custom', method: 'post', data })
 *
 * // 导出所有接口
 * export default {
 *   ...baseApi,
 *   customMethod
 * }
 */
import request from './request'

/**
 * 创建标准CRUD API
 * @param {string} baseUrl - API基础路径
 * @returns {Object} 包含标准CRUD方法的对象
 */
export function createCrudApi(baseUrl) {
  return {
    /**
     * 根据ID获取实体
     * @param {string} id - 实体ID
     */
    getById: (id) => request({
      url: `${baseUrl}/${id}`,
      method: 'get'
    }),

    /**
     * 分页查询
     * @param {Object} data - 查询参数
     */
    listPage: (data) => request({
      url: `${baseUrl}/list`,
      method: 'post',
      data
    }),

    /**
     * 查询全部数据
     * @param {Object} data - 查询参数
     */
    listAll: (data) => request({
      url: `${baseUrl}/listAll`,
      method: 'post',
      data
    }),

    /**
     * 保存实体（新增或更新）
     * @param {Object} data - 实体数据
     */
    save: (data) => request({
      url: `${baseUrl}/save`,
      method: 'post',
      data
    }),

    /**
     * 删除实体
     * @param {Object} data - 实体数据
     */
    delete: (data) => request({
      url: `${baseUrl}/delete`,
      method: 'post',
      data
    }),

    /**
     * 批量新增
     * @param {Array} data - 实体数组
     */
    bulkInsert: (data) => request({
      url: `${baseUrl}/bulkInsert`,
      method: 'post',
      data
    }),

    /**
     * 批量更新
     * @param {Array} data - 实体数组
     */
    bulkUpdate: (data) => request({
      url: `${baseUrl}/bulkUpdate`,
      method: 'post',
      data
    }),

    /**
     * 批量删除
     * @param {Array} data - 实体数组
     */
    bulkDelete: (data) => request({
      url: `${baseUrl}/bulkDelete`,
      method: 'post',
      data
    })
  }
}

/**
 * 创建带分页的查询API
 * @param {string} baseUrl - API基础路径
 * @returns {Object} 包含分页查询方法的对象
 */
export function createPagedApi(baseUrl) {
  return {
    /**
     * 分页查询
     * @param {Object} data - 查询参数
     */
    listPage: (data) => request({
      url: `${baseUrl}/list`,
      method: 'post',
      data
    }),

    /**
     * 查询全部数据
     * @param {Object} data - 查询参数
     */
    listAll: (data) => request({
      url: `${baseUrl}/listAll`,
      method: 'post',
      data
    })
  }
}

/**
 * 创建自定义请求方法
 * @param {string} url - 请求URL
 * @param {string} method - 请求方法
 * @returns {Function} 请求函数
 */
export function createRequest(url, method = 'post') {
  return (data) => request({
    url,
    method,
    data
  })
}

export default {
  createCrudApi,
  createPagedApi,
  createRequest
}
