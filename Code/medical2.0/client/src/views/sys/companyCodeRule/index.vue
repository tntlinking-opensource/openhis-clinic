<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <companyCodeRule-form ref='companyCodeRuleForm' :permission='permission' v-on:save-finished='getCompanyCodeRuleList()'></companyCodeRule-form>
    <div class="page-container">
      <!--  搜索栏  开始 -->
      <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
              <el-col :span="6">
                <el-form-item label='名称' prop='name'>
                  <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入名称'></el-input>
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
      </div>
      <!--  搜索栏  结束 -->
      <!-- 工具栏 开始 -->
      <div class="page-container-header-end">
        <div>
          <el-button v-show='permission.add' type='primary' icon='el-icon-plus'  @click='onCreateCompanyCodeRule()'>添加</el-button>
        </div>
      </div>
      <!-- 工具栏 结束 -->
      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>
            <el-table class='drag_table' :data='companyCodeRuleList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
              <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType)}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='oprColumnWidth + "px"' fixed='right'>
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='companyCodeRuleList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                    @click='onViewCompanyCodeRule(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                    @click='onEditCompanyCodeRule(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document'
                    @click='onCopyCompanyCodeRule(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete'
                    @click='onDeleteCompanyCodeRule(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='info' content='历史记录' placement='top-start' icon-name='el-icon-info'
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
            :page-sizes='[10, 20, 50, 100, companyCodeRuleTotal]'
            :page-size='10'
            layout='total, sizes, prev, pager, next, jumper'
            :total='companyCodeRuleTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->
    </div>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listCompanyCodeRulePage, getCompanyCodeRuleById, deleteCompanyCodeRule } from '@/api/sys/companyCodeRule'
import { listResourcePermission } from '@/api/admin/common/permission'
import CompanyCodeRuleForm from './companyCodeRuleForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
export default {
  extends: MainUI,
  components: {
    CompanyCodeRuleForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History
  },
  data() {
    return {
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
      queryModel: {
        'name': '',   // 名称
      },
      search: {
        params: [{'columnName':'company_id', 'queryType': '=', 'value': currentUser.company.id}],
        offset: 0,
        limit: 10,
        columnName: '',       // 排序字段名
        order: ''             // 排序
      },
      currentPage: 1,
      companyCodeRuleTotal: 0,
      companyCodeRuleList: [],


      tableId: '443292628418674688',
      schemeId: '443292628418674704'
    }
  },
  methods: {
    getCompanyCodeRuleList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = [{'columnName':'company_id', 'queryType': '=', 'value': currentUser.company.id}]
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 名称
        this.search.params.push({
      	  columnName: 'name',
      	  queryType: 'like',
          value: this.queryModel.name
        })
      }
      // 数据权限: 公司编号规则sys_company_code_rule
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listCompanyCodeRulePage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.companyCodeRuleTotal = responseData.data.total
          this.companyCodeRuleList = responseData.data.rows
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
        this.getCompanyCodeRuleList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getCompanyCodeRuleList()
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
      this.getCompanyCodeRuleList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getCompanyCodeRuleList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = [{'columnName':'company_id', 'queryType': '=', 'value': currentUser.company.id}]
        // 数据权限: 公司编号规则sys_company_code_rule
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listCompanyCodeRuleRespData, listPermissionRespData] = await Promise.all([
          listCompanyCodeRulePage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listCompanyCodeRuleRespData.code == 100 && listPermissionRespData.code == 100) {
          this.companyCodeRuleTotal = listCompanyCodeRuleRespData.data.total
          this.companyCodeRuleList = listCompanyCodeRuleRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'companyCodeRule:read'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'companyCodeRule:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'companyCodeRule:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'companyCodeRule:delete'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'companyCodeRule:export'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listCompanyCodeRuleRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onCreateCompanyCodeRule() {
      this.$refs.companyCodeRuleForm.$emit('openAddCompanyCodeRuleDialog')
    },
    onViewCompanyCodeRule(index, row) {
      this.setLoad()
      getCompanyCodeRuleById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.companyCodeRuleForm.$emit('openViewCompanyCodeRuleDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onEditCompanyCodeRule(index, row) {
      this.setLoad()
      getCompanyCodeRuleById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.companyCodeRuleForm.$emit('openEditCompanyCodeRuleDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyCompanyCodeRule(index, row) {
      this.setLoad()
      getCompanyCodeRuleById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.companyCodeRuleForm.$emit('openCopyCompanyCodeRuleDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteCompanyCodeRule(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteCompanyCodeRule(row).then(responseData => {
          if(responseData.code == 100) {
            this.getCompanyCodeRuleList()
            this.showMessage({type:'success',msg:'删除成功'})
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      }).catch(() => {})
    },
    onSortChange( orderby ) {
      if(validatenull(orderby.prop)) {
        this.search.columnName = ''
        this.search.order = ''
      } else  {
        this.search.columnName = orderby.prop
        this.search.order = orderby.order === 'descending' ? 'desc' : 'asc'
      }

      this.getCompanyCodeRuleList()
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
