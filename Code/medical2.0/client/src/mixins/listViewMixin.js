/**
 * 列表视图混入
 * 封装通用的分页、排序、CRUD调度和权限加载逻辑
 *
 * 使用方式：
 * 1. 在组件中 mixins: [listViewMixin]
 * 2. 在 data 中定义 listApi, getApi, deleteApi, entityName, permissionPrefix
 * 3. 实现 loadData() 方法加载列表数据（不包含权限）
 * 4. 实现 handleListResponse() 方法处理列表响应
 *
 * 注意：pageInit() 仅在页面初始化时调用一次（加载数据+权限），
 *       loadData() 在搜索/分页/排序时调用（仅加载列表数据）。
 */
import { listResourcePermission } from '@/api/resourcePermission'
import { mapPermissions } from '@/utils/searchParamsBuilder'

export default {
  data() {
    return {
      search: {
        params: [],
        offset: 0,
        limit: 20,
        columnName: '',
        order: ''
      },
      currentPage: 1,
      permission: {
        view: false,
        add: false,
        edit: false,
        remove: false,
        export: false,
        skip: false
      },
      // 子组件需要定义以下属性
      // listApi: Function,       // 列表查询API
      // getApi: Function,        // 获取单条API
      // deleteApi: Function,     // 删除API
      // entityName: String,      // 实体名称（用于权限映射，如 'Drug'）
      // permissionPrefix: String // 权限前缀（如 'drug'）
    }
  },
  methods: {
    /**
     * 序号计算
     */
    indexMethod(index) {
      return (this.currentPage - 1) * this.search.limit + index + 1
    },

    /**
     * 搜索
     */
    onSearch() {
      if (this.moreCodition) {
        this.search.offset = 0
        this.currentPage = 1
        this.loadData()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.loadData()
          } else {
            return false
          }
        })
      }
    },

    /**
     * 每页数量变化
     */
    onSizeChange(val) {
      this.currentPage = 1
      this.search.limit = val
      this.search.offset = 0
      this.loadData()
    },

    /**
     * 页码变化
     */
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.loadData()
    },

    /**
     * 排序变化 — 接收 el-table sort-change 的 orderby 对象
     * @param {Object} orderby - { prop: string, order: string }
     */
    onSortChange(orderby) {
      if (!orderby.prop) {
        this.search.columnName = ''
        this.search.order = ''
      } else {
        this.search.columnName = orderby.prop
        this.search.order = orderby.order === 'descending' ? 'desc' : 'asc'
      }
      this.loadData()
    },

    /**
     * 重置搜索表单
     */
    reset() {
      if (this.$refs.queryForm) {
        this.$refs.queryForm.resetFields()
      }
      this.onSearch()
    },

    /**
     * 页面初始化 — 仅在 mounted 时调用一次
     * 加载列表数据 + 权限信息
     * 子组件可覆盖此方法以添加自定义逻辑
     */
    async pageInit() {
      this.setLoad()
      try {
        // 初始化选项
        if (this.initOptions) {
          this.initOptions(this.queryModel)
        }

        // 构建搜索参数 — 子组件应在 appendSearchParams() 中添加查询条件
        this.search.params = []
        if (this.appendSearchParams) {
          this.appendSearchParams()
        }

        // 并行加载数据和权限
        const prefix = this.permissionPrefix || this.entityName
        const [listRespData, permissionRespData] = await Promise.all([
          this.listApi(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])

        if (listRespData.code === 100 && permissionRespData.code === 100) {
          this.handleListResponse(listRespData)
          this.permission = mapPermissions(permissionRespData.data, prefix)
        } else {
          this.showMessage(permissionRespData.code !== 100 ? permissionRespData : listRespData)
        }
        this.resetLoad()
      } catch (error) {
        this.outputError(error)
      }
    },

    /**
     * 处理列表响应 — 子组件需要覆盖
     * @param {Object} responseData 响应数据
     */
    handleListResponse(responseData) {
      // 子组件实现：设置列表数据和总数
      // 例如：this.supplierTotal = responseData.data.total
      //       this.supplierList = responseData.data.rows
    },

    /**
     * 添加搜索参数 — 子组件需要覆盖
     * 在此方法中构建 this.search.params
     *
     * 示例：
     * appendSearchParams() {
     *   this.search.params = [
     *     { columnName: 'company_id', queryType: '=', value: currentUser.company.id },
     *     { columnName: 'name', queryType: 'like', value: this.queryModel.name },
     *     { columnName: 'linkman', queryType: 'like', value: this.queryModel.linkman }
     *   ]
     * }
     */
    appendSearchParams() {
      // 子组件实现
    },

    /**
     * 加载列表数据 — 搜索/分页/排序时调用
     * 子组件可覆盖此方法以添加自定义逻辑
     * 默认实现：调用 listApi 并触发 handleListResponse
     */
    loadData() {
      this.setLoad()
      // 重建搜索参数
      this.search.params = []
      if (this.appendSearchParams) {
        this.appendSearchParams()
      }
      this.listApi(this.search).then(responseData => {
        if (responseData.code === 100) {
          this.handleListResponse(responseData)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },

    /**
     * 查看实体
     */
    onViewEntity(index, row, formRef) {
      this.setLoad()
      this.getApi(row.id).then(responseData => {
        if (responseData.code === 100) {
          this.$refs[formRef][`openView${this.entityName}Dialog`](responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },

    /**
     * 编辑实体
     */
    onEditEntity(index, row, formRef) {
      this.setLoad()
      this.getApi(row.id).then(responseData => {
        if (responseData.code === 100) {
          this.$refs[formRef][`openEdit${this.entityName}Dialog`](responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },

    /**
     * 复制实体
     */
    onCopyEntity(index, row, formRef) {
      this.setLoad()
      this.getApi(row.id).then(responseData => {
        if (responseData.code === 100) {
          this.$refs[formRef][`openCopy${this.entityName}Dialog`](responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },

    /**
     * 删除实体
     */
    onDeleteEntity(index, row, deleteApi) {
      this.$confirm('确认删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteApi(row).then(responseData => {
          if (responseData.code === 100) {
            this.$message({ type: 'success', message: '删除成功' })
            this.loadData()
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      }).catch(() => {})
    },

    /**
     * 创建实体 — 子组件可覆盖
     */
    onCreateEntity(formRef) {
      this.$refs[formRef][`openAdd${this.entityName}Dialog`]()
    }
  }
}
