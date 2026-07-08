/**
 * 查询参数构建器
 * 提供流式API构建查询参数，提高代码可读性
 *
 * 使用示例：
 * import { SearchParamsBuilder } from '@/utils/searchParamsBuilder'
 *
 * const params = SearchParamsBuilder.create()
 *   .eq('company_id', companyId)
 *   .like('name', name)
 *   .like('linkman', linkman)
 *   .eq('type', type)
 *   .build()
 */
export class SearchParamsBuilder {
  constructor() {
    this.params = []
  }

  /**
   * 创建新的构建器实例
   */
  static create() {
    return new SearchParamsBuilder()
  }

  /**
   * 创建带租户过滤的构建器实例
   */
  static createWithTenant(companyId) {
    return new SearchParamsBuilder().eq('company_id', companyId)
  }

  /**
   * 等于查询
   */
  eq(columnName, value) {
    if (value !== null && value !== undefined && value !== '') {
      this.params.push({ columnName, queryType: '=', value })
    }
    return this
  }

  /**
   * 不等于查询
   */
  ne(columnName, value) {
    if (value !== null && value !== undefined) {
      this.params.push({ columnName, queryType: '!=', value })
    }
    return this
  }

  /**
   * 模糊查询
   */
  like(columnName, value) {
    if (value !== null && value !== undefined && value.toString().trim() !== '') {
      this.params.push({ columnName, queryType: 'like', value })
    }
    return this
  }

  /**
   * 左模糊查询
   */
  leftLike(columnName, value) {
    if (value !== null && value !== undefined && value.toString().trim() !== '') {
      this.params.push({ columnName, queryType: 'left_like', value })
    }
    return this
  }

  /**
   * 右模糊查询
   */
  rightLike(columnName, value) {
    if (value !== null && value !== undefined && value.toString().trim() !== '') {
      this.params.push({ columnName, queryType: 'right_like', value })
    }
    return this
  }

  /**
   * 大于查询
   */
  gt(columnName, value) {
    if (value !== null && value !== undefined) {
      this.params.push({ columnName, queryType: '>', value })
    }
    return this
  }

  /**
   * 大于等于查询
   */
  ge(columnName, value) {
    if (value !== null && value !== undefined) {
      this.params.push({ columnName, queryType: '>=', value })
    }
    return this
  }

  /**
   * 小于查询
   */
  lt(columnName, value) {
    if (value !== null && value !== undefined) {
      this.params.push({ columnName, queryType: '<', value })
    }
    return this
  }

  /**
   * 小于等于查询
   */
  le(columnName, value) {
    if (value !== null && value !== undefined) {
      this.params.push({ columnName, queryType: '<=', value })
    }
    return this
  }

  /**
   * IN查询
   */
  in(columnName, values) {
    if (values && values.length > 0) {
      this.params.push({ columnName, queryType: 'in', value: values })
    }
    return this
  }

  /**
   * NOT IN查询
   */
  notIn(columnName, values) {
    if (values && values.length > 0) {
      this.params.push({ columnName, queryType: 'not in', value: values })
    }
    return this
  }

  /**
   * BETWEEN查询
   */
  between(columnName, start, end) {
    if (start !== null && start !== undefined && end !== null && end !== undefined) {
      this.params.push({ columnName, queryType: 'between', value: [start, end] })
    }
    return this
  }

  /**
   * IS NULL查询
   */
  isNull(columnName) {
    this.params.push({ columnName, queryType: 'is null', value: null })
    return this
  }

  /**
   * IS NOT NULL查询
   */
  isNotNull(columnName) {
    this.params.push({ columnName, queryType: 'is not null', value: null })
    return this
  }

  /**
   * 添加自定义参数
   */
  add(param) {
    if (param) {
      this.params.push(param)
    }
    return this
  }

  /**
   * 添加OR逻辑参数
   */
  or(columnName, queryType, value) {
    if (value !== null && value !== undefined) {
      this.params.push({ columnName, queryType, value, logic: 'OR' })
    }
    return this
  }

  /**
   * 构建参数列表
   */
  build() {
    return [...this.params]
  }
}

/**
 * 创建分页搜索对象
 */
export function createSearch(options = {}) {
  const {
    companyId = currentUser?.company?.id,
    limit = 20
  } = options

  return {
    params: companyId ? [{ columnName: 'company_id', queryType: '=', value: companyId }] : [],
    offset: 0,
    limit,
    columnName: '',
    order: ''
  }
}

/**
 * 创建权限对象
 */
export function createPermissions() {
  return {
    view: false,
    add: false,
    edit: false,
    remove: false,
    export: false
  }
}

/**
 * 映射权限数据
 */
export function mapPermissions(permissionData, prefix) {
  return {
    view: permissionData.find(item => item.permission === `${prefix}:read`),
    add: permissionData.find(item => item.permission === `${prefix}:create`),
    edit: permissionData.find(item => item.permission === `${prefix}:update`),
    remove: permissionData.find(item => item.permission === `${prefix}:delete`),
    export: permissionData.find(item => item.permission === `${prefix}:export`)
  }
}

export default {
  SearchParamsBuilder,
  createSearch,
  createPermissions,
  mapPermissions
}
