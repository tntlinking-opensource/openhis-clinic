<template>
  <div>
    <el-row v-loading='loading'>
      <div class="page-container">
        <!--  搜索栏  开始 -->
        <div class="query-form-container">
          <el-main>
            <el-row v-if='!moreCodition' class='search-row'>
              <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
                <el-col :span="6">
                  <el-form-item label='事由' prop='name'>
                    <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入事由'></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-button-group>
                    <el-tooltip  effect="light" content="搜索" placement="top-start">
                      <el-button type="primary" icon="el-icon-search" @click='onSearch()' :plain='true'></el-button>
                    </el-tooltip>
                      <el-tooltip  effect="light" content="重置" placement="top-start">
                      <el-button type="primary" icon="el-icon-refresh-left" @click='$refs.queryForm.resetFields()' :plain='true'></el-button>
                    </el-tooltip>
                    <el-tooltip  effect="light" content="更多" placement="top-start">
                      <el-button type="primary" icon="el-icon-d-arrow-right" @click='onMoreCodition()' :plain='true'></el-button>
                    </el-tooltip>
                  </el-button-group> 
               </el-col>
              </el-form>
            </el-row>
            <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId' :routerId='$route.meta.routerId' @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
          </el-main>
        </div>
      <!--  搜索栏  结束 -->
      <!-- 表格栏  开始 -->
        <el-row>
          <el-col :span='24'>
            <div @mouseleave='moveTableOutside'>
              <el-table class='drag_table' :data='expenseList' @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
                <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                  <template slot-scope='{row,$index}'>
                    <span v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                      <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                    </span>
                    <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType)}}</span>
                  </template>
                </el-table-column>
                <!--表行级操作按钮-->
                <el-table-column label='操作' header-align='center' width='100px' fixed='right'>
                  <template slot='header' slot-scope="scope">
                    <span>操作</span>
                    <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                    <export-excel-button v-show='permission.export' :data='expenseList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                  </template>
                  <template slot-scope='scope'>
                    <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                    @click='onViewExpense(scope.$index, scope.row)'></OperationIcon>
                    <OperationIcon v-show='permission.remove' type='info' content='审批历史' placement='top-start' icon-name='el-icon-info'
                    @click='onShowHistory(scope.$index, scope.row)'></OperationIcon>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-col>
        </el-row>
            <!-- 表格栏  结束 -->
            <!-- 分页栏     开始 -->
            <el-row>
              <el-col :span='24'>
                <el-pagination
                  background
                  @size-change='onSizeChange'
                  @current-change='onCurrentChange'
                  :current-page.sync='currentPage'
                  :page-sizes='[10, 20, 50, 100, expenseTotal]'
                  :page-size='10'
                  layout='total, sizes, prev, pager, next, jumper'
                  :total='expenseTotal'>
                </el-pagination>
              </el-col>
            </el-row>
            <!-- 分页栏     结束 -->
      </div>
    </el-row>
    <!-- 审批历史  -->
    <Comment ref='commentForm'></Comment>
    <!-- 审批单据 -->
    <component ref='wfForm' :is='wfForm' v-on:save-finished='getTaskList()' v-on:after-load='afterWfFormload()'></component>
  </div>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listExpensePage } from '@/api/test/expense'
import { getHistProcInstance } from '@/api/wf/procinst'
import { getStartForm } from '@/api/wf/processdefinition'
import { listResourcePermission } from '@/api/admin/common/permission'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import WfMainUI from '@/views/wf/common/wfMainUI'
import OperationIcon from '@/components/OperationIcon'
import Comment from '@/views/wf/common/comment'
export default {
  extends: WfMainUI,
  components: {
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    Comment
  },
  data() {
    return {
      wfForm:  null,    // 工作流表单
      currentRow: null,      // 当前查看申请

      permission: {
        view: false,
        add: false,
        edit: false,
        remove: false,
        export: false
      },
      queryTypes: {
        'name': 'like',
      },
      queryModel:  {
        'name': '',   // 事由
      },
      search: {
        params: [],
        offset: 0,
        limit: 10,
        columnName: '',       // 排序字段名
        order: ''             // 排序
      },
      currentPage: 1,
      expenseTotal: 0,
      expenseList: [],

      tableId: '4016',
      schemeId: '6014'
    }
  },
  methods: {
    getExpenseList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = []
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 事由
        this.search.params.push({
      	  columnName: 'name',
      	  queryType: 'like',
          value: this.queryModel.name
        })
      }
      // 数据权限: 费用申请表test_expense
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listExpensePage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.expenseTotal = responseData.data.total
          this.expenseList = responseData.data.rows
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onSearch() {
      if(this.moreCodition) {
        this.search.offset = 0
        this.currentPage = 1
        this.getExpenseList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getExpenseList()
          } else {
            return false
          }
        })
      }
    },
    onSizeChange(val) {
      this.currentPage = 1
      this.search.limit = val;
      this.search.offset = (this.currentPage - 1) * val
      this.getExpenseList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getExpenseList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = []
        // 数据权限: 费用申请表test_expense
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listExpenseRespData, listPermissionRespData] = await Promise.all([
          listExpensePage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listExpenseRespData.code == 100 && listPermissionRespData.code == 100) {
          this.expenseTotal = listExpenseRespData.data.total
          this.expenseList = listExpenseRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'expense:read'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'expense:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'expense:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'expense:delete'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'expense:export'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listExpenseRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onViewExpense(index, row) {
      this.setLoad()
      getHistProcInstance(row.procInst).then(responseData => {
        if(responseData.hasOwnProperty('processDefinitionId')) {
          let definitionId = responseData.processDefinitionId
          if(definitionId) {
            getStartForm(definitionId).then(startFormResp => {
              if(startFormResp.hasOwnProperty('key')) {
                if(startFormResp.key) {
                  this.currentRow = {
                    formKey: startFormResp.key,
                    definitionId: definitionId,
                    row: row
                  }
                  this.loadWfForm(startFormResp.key)
                } else {
                  this.showMessage({type:'warning' , msg: '流程未配置form，请联系管理员。'})
                }
              }else{
                this.showMessage(startFormResp)
              }
              this.resetLoad()
            })
          } else {
            this.showMessage({type:'warning' , msg: '流程实例没有找到流程定义。'})
          }
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    // 重写mainUI的方法
    onShowHistory(index, row) {
      this.$refs.commentForm.$emit('openComment', row.procInst)
    },
    afterWfFormload() {
      this.$refs.wfForm.$emit('openViewBizDialog', this.currentRow.formKey, this.currentRow.definitionId, this.currentRow.row)
    },
    onSortChange( orderby ) {
      if(validatenull(orderby.prop)) {
        this.search.columnName = ''
        this.search.order = ''
      } else  {
        this.search.columnName = orderby.prop
        this.search.order = orderby.order === 'descending' ? 'desc' : 'asc'
      }

      this.getExpenseList()
    },
    initOptions(This) {
    }
  },
  watch: {
  },
  mounted() {
    this.pageInit()
  }
}
</script>
