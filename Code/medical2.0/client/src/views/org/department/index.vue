<template>
  <el-row v-loading='loading'>
    <el-container>
      <!-- 历史记录  -->
      <History :bussObject='curentRow' ></History>
      <!-- 编辑窗口  -->
      <department-form ref='departmentForm' :permission='permission' v-on:save-finished='getDepartmentList()'></department-form>
      <!-- 非系统管理员不可见  -->
      <div class="page-left-container" v-show='currentUser.id == 1001'>
        <el-aside>
          <el-table ref='treeTable' :data='companys' row-key='id' :tree-props="{children: 'children', hasChildren: 'hasChildren'}" highlight-current-row :cell-style="function() {return {borderBottom: 'none'} }" @current-change='onLeftCurrentChange'>
            <el-table-column label='公司' prop='name'></el-table-column>
          </el-table>
        </el-aside>
      </div>
      <div class="page-container">
      <!--  搜索栏  开始 -->
      <div class='query-form-container'>
        <el-main>
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
          <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId' :curNode='currentCompany' exclude='company_id' :routerId='$route.meta.routerId' @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
        </el-main>
      </div>
      <!--  搜索栏  结束 -->

      <!-- 工具栏 开始 -->
      <div class="page-container-header-end">
        <div>
          <el-button v-show='permission.add' type='primary' icon='el-icon-plus'  @click='onCreateDepartment()'>添加</el-button>
        </div>
      </div>
      <!-- 工具栏 结束 -->
      
      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>
            <el-table class='drag_table' :data='departmentList' row-key='id' :tree-props="{children: 'children', hasChildren: 'hasChildren'}"border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
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
                  <export-excel-button v-show='permission.export' :data='departmentList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                    @click='onViewDepartment(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='添加下级' placement='top-start' icon-name='el-icon-circle-plus-outline'
                    @click='onCreateDepartment(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                    @click='onEditDepartment(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document'
                    @click='onCopyDepartment(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove && (!(scope.row.children) || scope.row.children.length <=0)' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete'
                    @click='onDeleteDepartment(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.view' type='info' content='历史记录' placement='top-start' icon-name='el-icon-info'
                    @click='onShowHistory(scope.$index, scope.row)'></OperationIcon>
                </template>
              </el-table-column>
            </el-table>
    	  </div>
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
      </div>
    </el-container>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { treeDepartment, getDepartmentById, deleteDepartment } from '@/api/org/department'
import { listResourcePermission } from '@/api/admin/common/permission'
import DepartmentForm from './departmentForm'
import { treeCompany } from '@/api/org/company'

import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
export default {
  extends: MainUI,
  components: {
    DepartmentForm,
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
    	'company_id': '=',
        'name': 'like',
      },
      queryModel:  {
        'company': {     // 父表 公司
          'id': null,
          'name': '',
        },
        'name': '',   // 名称
      },
      search: {
        params: [],
        offset: 0,
        limit: 10,
        columnName: '',       // 排序字段名
        order: ''             // 排序
      },
      currentCompany: {},     //树形结构中选择的公司
      departmentList: [],     // 数表数据
      companys: [],           // 公司树表

      oprColumnWidth: 165,  // 操作列宽
      tableId: '52676365136650250',
      schemeId: '52676365136650309'
    }
  },
  methods: {
    getDepartmentList() {
      this.search.params = []
      if(validatenull(this.currentCompany)) {
        this.$alert('请选择公司', '提示', {
          confirmButtonText: '确定',
          type: 'info'
        })
        return
      }
      this.search.params.push({
      	columnName: 'company_id',
      	queryType: '=',
      	value: this.currentCompany.id
      })

      this.setLoad()
      /* 查询参数 和数据权限 */
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
      // 数据权限: 部门org_department
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      treeDepartment(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.departmentList = responseData.data
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
        this.getDepartmentList()
      } else {
          this.$refs['queryForm'].validate(valid => {
            if (valid) {
              this.getDepartmentList()
            } else {
              return false
            }
          })
       }
    },
    async pageInit() {
      this.setLoad()
      try {
        let params = []
        /* 非系统管理员，只能查询自己所在公司的数据 */
        if(currentUser.id != 1001) {
          params.push({'columnName':'id', 'queryType': '=', 'value': currentUser.company.id})
        }
        // 数据权限: 公司org_company
        this.pushDataPermissions(params, this.$route.meta.routerId, '41040096140492800')
        let [treeCompanyRespData, listPermissionRespData] = await Promise.all([
          treeCompany({params: params, columnName: '', 'order': ''}),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(treeCompanyRespData.code == 100 && listPermissionRespData.code == 100) {
          this.companys = treeCompanyRespData.data
          this.$nextTick(() => {
            if(this.companys && this.companys.length > 0 && this.$refs.treeTable){
              this.$refs.treeTable.setCurrentRow(this.companys[0])
            }
          })
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'department:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'department:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'department:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'department:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'department:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : treeCompanyRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onViewDepartment(index, row) {
      this.setLoad()
      getDepartmentById(row.id).then(responseData => {
        if(responseData.code == 100) {
          let department = responseData.data
          if(validatenull(department.parent)) {
            department.parent = {id: null}
          }
          this.$refs.departmentForm.$emit('openViewDepartmentDialog', department)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreateDepartment(index, row) {
      if(validatenull(this.currentCompany)) {
        this.$alert('请选择公司', '提示', {
          confirmButtonText: '确定',
          type: 'info'
        })
        return
      }
      if(validatenull(row)) {
        row={
          id: null,
          name: '',
          'company': this.currentCompany
        }
      }
      this.$refs.departmentForm.$emit('openAddDepartmentDialog', row)
    },
    onEditDepartment(index, row) {
      this.setLoad()
      getDepartmentById(row.id).then(responseData => {
        if(responseData.code == 100) {
          let department = responseData.data
          if(validatenull(department.parent)) {
            department.parent = {id: null}
          }
          this.$refs.departmentForm.$emit('openEditDepartmentDialog', department)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyDepartment(index, row) {
      this.setLoad()
      getDepartmentById(row.id).then(responseData => {
        if(responseData.code == 100) {
          let department = responseData.data
          if(validatenull(department.parent)) {
            department.parent = {id: null}
          }
          this.$refs.departmentForm.$emit('openCopyDepartmentDialog', department)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteDepartment(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteDepartment(row).then(responseData => {
          if(responseData.code == 100) {
            this.getDepartmentList()
            this.showMessage({type: 'success', msg: '删除成功'})
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

      this.getDepartmentList()
    },
    onLeftCurrentChange(currentRow, oldCurrentRow) {
      if(currentRow != oldCurrentRow) {
        this.currentCompany = currentRow
        this.queryModel['company'] = currentRow
        this.initOptions(this.queryModel)
        this.onSearch()
      }
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
