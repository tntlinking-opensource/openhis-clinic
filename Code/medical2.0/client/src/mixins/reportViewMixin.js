/**
 * 报表视图 Mixin
 * 提供医生工作量报表、药品销售报表等通用报表的公共逻辑
 *
 * 使用方式：
 * 1. 在组件中 mixins: [reportViewMixin]
 * 2. 在 data 中覆盖以下属性：
 *    - listApi: 列表查询 API 函数
 *    - statApi: 统计 API 函数（可选）
 *    - exportApi: 导出 API 函数
 *    - dictTypeId: 字典类型 ID（用于加载下拉选项）
 *    - searchParams: 额外的查询参数
 * 3. 如果需要自定义 appendSearchParams，覆盖该方法
 */
import MainUI from '@/views/components/mainUI'
import listViewMixin from '@/mixins/listViewMixin'
import { listDictItemAll } from '@/api/sys/dictItem'
import { BigNumber } from 'bignumber.js'
import { daysAgo, formatDateTime } from '@/utils/common'
import { downloadBlob } from '@/utils/downloadBlob'

export default {
  extends: MainUI,
  mixins: [listViewMixin],
  data() {
    return {
      // 子组件必须覆盖
      listApi: null,
      statApi: null,
      exportApi: null,
      // 字典类型 ID，子组件可覆盖
      dictTypeId: '1004078055755374603',
      // 子组件可覆盖初始查询条件
      queryModel: {
        dateRange: [daysAgo(30), new Date()]
      },
      search: {
        offset: 0,
        limit: 20,
        order: '',
        columnName: '',
        params: [
          {
            columnName: 'company_id',
            queryType: '=',
            value: currentUser.company.id
          },
          {
            logic: 'AND',
            queryType: '('
          },
          {
            columnName: 'create_date',
            logic: '',
            queryType: 'between',
            value: []
          },
          {
            logic: '',
            queryType: ')'
          }
        ]
      },
      pageSize: 20,
      dispensingTotal: 0,
      dispensingList: [],
      oprColumnWidth: 140,
      filerData: {
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > Date.now() - 8.64e6
          }
        }
      },
      allTotal: {},
      type_List: [],
      queryTypes: {
        goods_name: 'like',
        type: '=',
        bar_code: 'like'
      },
      amountTotal: {}
    }
  },
  methods: {
    /**
     * 导出 Excel（子组件可覆盖 columnName）
     */
    exportExcel() {
      if (!this.exportApi) return
      this.exportApi(this.search).then((res) => {
        downloadBlob(res, '报表导出.xlsx')
      }).catch((error) => {
        this.outputError(error)
      })
    },

    /**
     * 获取 30 天前的日期时间字符串
     * @returns {string} 30天前的日期时间字符串
     */
    addCreateDate() {
      return formatDateTime(daysAgo(30))
    },

    /**
     * 子组件覆盖此方法来添加自定义查询条件
     */
    appendSearchParams() {
      // 默认无额外条件，子组件覆盖
    },

    /**
     * 处理列表响应数据
     */
    handleListResponse(responseData) {
      this.dispensingList = responseData.data.rows
      this.dispensingTotal = responseData.data.total
    },

    /**
     * 加载数据（列表 + 统计）
     */
    loadData() {
      this.setLoad()
      this.search.params = []
      this.appendSearchParams()
      this.listApi(this.search).then(responseData => {
        if (responseData.code === 100) {
          this.handleListResponse(responseData)
          if (this.statApi) {
            this.statApi(this.search).then((res) => {
              if (res.code === 100) {
                this.amountTotal = res.data
                this.resetLoad()
              }
            }).catch(() => {})
          } else {
            this.resetLoad()
          }
        } else {
          this.showMessage(responseData)
          this.resetLoad()
        }
      }).catch(error => {
        this.outputError(error)
      })
    },

    /**
     * 初始化字典选项
     */
    initOptions() {
      if (!this.dictTypeId) return
      const type_search = {
        params: [
          {
            columnName: 'dict_type_id',
            queryType: '=',
            value: this.dictTypeId
          }
        ]
      }
      type_search.params.forEach((item) => {
        if (this.queryTypes[item.columnName]) {
          item.queryType = this.queryTypes[item.columnName]
        }
      })
      this.pushDataPermissions(
        type_search.params,
        this.$route.meta.routerId,
        '4005'
      )
      this.type_List.splice(0, this.type_List.length)
      listDictItemAll(type_search).then((responseData) => {
        this.type_List = responseData.data
      })
    },

    /**
     * 重置查询条件
     */
    resetCondition() {
      this.queryModel = {
        ...this.queryModel,
        dateRange: [this.addCreateDate(), new Date()]
      }
      this.currentPage = 1
      this.onSearch()
    },

    /**
     * BigNumber 格式化（千分位，2位小数）
     */
    bigNum(num) {
      if (num || num === '0') {
        return new BigNumber(num).toFormat(2)
      }
      return ''
    }
  },
  mounted() {
    this.initOptions()
    this.pageInit()
  }
}
