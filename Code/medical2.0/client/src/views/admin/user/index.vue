<template>
  <el-row v-loading='loading'>
    <el-container>
      <!-- 历史记录  -->
      <History :bussObject='curentRow' ></History>
      <!-- 编辑窗口  -->
      <user-form ref='userForm' :permission='permission' @save-finished='loadData'></user-form>
      <!-- 非系统管理员不可见  -->
      <div class="page-left-container" v-show='currentUser.id === 1001'>
        <el-aside>
          <el-table ref='treeTable' :data='companys' row-key='id' :tree-props="{children: 'children', hasChildren: 'hasChildren'}" highlight-current-row :cell-style="function() {return {borderBottom: 'none'} }" @current-change='onLeftCurrentChange'>
            <el-table-column label='公司' prop='name'></el-table-column>
          </el-table>
        </el-aside>
      </div>
      <el-card class="page-container">
      <!--  搜索栏  开始 -->
      <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
              <el-col :span="6">
                <el-form-item label='部门' prop='department.id'>
                  <el-cascader v-model='queryModel.department.id' :options='department_List' :key='key_department' :props='{value: "id", label: "name", checkStrictly: false, emitPath: false}' filterable clearable placeholder='请选择部门' />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label='用户名' prop='name'>
                  <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入用户名'></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="3" style="display:flex;justivy-content:space-around">
               <el-button
                    type="primary"
                    icon="el-icon-search"
                    @click="onSearch()"
                    :plain="true"
                  >搜索</el-button>
                <el-button
                    type="info"
                    icon="el-icon-refresh-left"
                    @click="reset"
                    :plain="true"
                  >重置</el-button>
            </el-col>
            <el-col :span="9" style="text-align:right;padding-right:5px">
              <el-button-group>
                  <el-button
                    v-show="permission.add"
                    type="primary"
                    icon="el-icon-plus"                   
                    @click="onCreateUser()"
                    >添加</el-button
                  >
              </el-button-group>
            </el-col>
            </el-form>
          </el-row>
          <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId' :curNode='currentCompany' exclude='company_id' :routerId='$route.meta.routerId' @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
      </div>
      <!--  搜索栏  结束 -->
      <!-- 工具栏 开始 -->
      
      <!-- 工具栏 结束 -->


      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>
            <el-table ref="mutipleTable" height="calc(100vh - 254px)" class='drag_table' :data='userList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
              <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span v-if='columnViews[index].showType === "Switch" || columnViews[index].showType === "Checkbox" || columnViews[index].showType === "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) === "0"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='140 + "px"' :key="'operate'">
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='userList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                    @click='onViewEntity(scope.$index, scope.row, "userForm")'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                    @click='onEditEntity(scope.$index, scope.row, "userForm")'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document'
                    @click='onCopyEntity(scope.$index, scope.row, "userForm")'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete'
                    @click='onDeleteEntity(scope.$index, scope.row, deleteUser)'></OperationIcon>
                  <OperationIcon v-show='permission.view' type='info' content='历史记录' placement='top-start' icon-name='el-icon-info'
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
            :page-sizes='[20, 50, 100, userTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='userTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->
      </el-card>
    </el-container>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listUserPage, getUserById, deleteUser } from '@/api/admin/user'
import listViewMixin from '@/mixins/listViewMixin'
import { listResourcePermission } from '@/api/resourcePermission'
import { mapPermissions } from '@/utils/searchParamsBuilder'
import UserForm from './userForm'
import { treeDepartment } from '@/api/org/department'
import { treeCompany } from '@/api/org/company'

import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
export default {
  extends: MainUI,
  mixins: [listViewMixin],
  components: {
    UserForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History
  },
  data() {
    return {
      listApi: listUserPage,
      getApi: getUserById,
      deleteApi: deleteUser,
      entityName: 'User',
      permissionPrefix: 'user',
      queryTypes: {
        'company_id': '=',
        'department_id': '=',
        'name': 'like',
      },
      queryModel: {
        'company': {     // 父表 公司
          'id': null,
          'name': '',
          'ids': '',
          'code': '',
          'fullName': '',
        },
        'department': {     // 部门
          'id': null,
          'name': '',
          'ids': '',
          'code': '',
        },
        'name': '',   // 用户名
      },
      search: {
        params: [],
        offset: 0,
        limit: 20,
        columnName: '',
        order: ''
      },
      currentCompany: {},     //树形结构中选择的公司
      userTotal: 0,
      userList: [],     // 数表数据
      companys: [],           // 公司树表
      key_department: 1,    // el-cascader key
      department_List: [],    // 部门

      oprColumnWidth: 140,  // 操作列宽
      tableId: '4004',
      schemeId: '6005'
    }
  },
  methods: {
    reset(){
      this.$refs.queryForm.resetFields()
      this.onSearch()
    },
    appendSearchParams() {
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
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      } else {
        this.search.params.push({
          columnName: 'department_id',
          queryType: '=',
          value: validatenull(this.queryModel.department.id) ? '' : this.queryModel.department.id
        })
        this.search.params.push({
          columnName: 'name',
          queryType: 'like',
          value: this.queryModel.name
        })
      }
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
    },
    handleListResponse(responseData) {
      this.userTotal = responseData.data.total
      this.userList = responseData.data.rows
    },
    async pageInit() {
      this.setLoad()
      try {
        let params = []
        /* 非系统管理员，只能查询自己所在公司的数据 */
        if(currentUser.id !== 1001) {
          params.push({'columnName':'id', 'queryType': '=', 'value': currentUser.company.id})
        }
        this.pushDataPermissions(params, this.$route.meta.routerId, '41040096140492800')

        this.search.params = []
        this.appendSearchParams()

        let [treeCompanyRespData, listPermissionRespData] = await Promise.all([
          treeCompany({params: params, columnName: '', order: ''}),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(treeCompanyRespData.code === 100 && listPermissionRespData.code === 100) {
          this.companys = treeCompanyRespData.data
          this.$nextTick(() => {
            if(this.companys && this.companys.length > 0 && this.$refs.treeTable){
              this.$refs.treeTable.setCurrentRow(this.companys[0])
            }
          })
          this.permission = mapPermissions(listPermissionRespData.data, 'user')
          // Load user list after company is selected
          if(!validatenull(this.currentCompany)) {
            let listRespData = await listUserPage(this.search)
            if(listRespData.code === 100) {
              this.handleListResponse(listRespData)
            }
          }
        } else {
          this.showMessage(listPermissionRespData.code !== 100 ? listPermissionRespData : treeCompanyRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onCreateUser() {
      if(validatenull(this.currentCompany)) {
        this.$alert('请选择公司', '提示', {
          confirmButtonText: '确定',
          type: 'info'
        })
        return
      }
      let row={
        'company': this.currentCompany
      }
      this.$refs.userForm.openAddUserDialog(row)
    },
    onLeftCurrentChange(currentRow, oldCurrentRow) {
      if(currentRow !== oldCurrentRow) {
        this.currentCompany = currentRow
        this.queryModel['company'] = currentRow
        this.initOptions(this.queryModel)
        this.onSearch()
      }
    },
    initOptions(This) {
      this.key_department++
      let department_search = {
        params: [{'columnName':'company_id', 'queryType': '=', 'value': This.company.id}]
      }
      department_search.params.forEach(item => {
        if(this.queryTypes[item.columnName]) {
          item.queryType = this.queryTypes[item.columnName]
        }
      })
      department_search.params.push.apply(department_search.params, [])
      this.pushDataPermissions(department_search.params, this.$route.meta.routerId, '52676365136650250')
      this.department_List.splice(0, this.department_List.length)
      treeDepartment(department_search).then(responseData => {
        this.department_List = responseData.data
      }).catch(error => {
        this.outputError(error)
      })
    }
  },
  watch: {
    userList(val){
      if(val){
        this.$nextTick(() => {
            if (this.$refs.mutipleTable) {
              this.$refs.mutipleTable.doLayout();
            }
        });
      }
    }
  },
  updated(){
    if(this.$refs.mutipleTable){
       this.$nextTick(() => {
            this.$refs.mutipleTable.doLayout();
        });
    }
  },
  mounted() {
    this.pageInit()
  }
}
</script>
<style lang="scss" scoped>
.page-container{
  padding: 0;
}
  ::v-deep.el-table{
    .el-table__fixed-body-wrapper{
      top: 47px !important;
    }
  }
  ::v-deep .el-table__fixed-right-patch{
    width:5px !important
  }
  ::v-deep .el-table colgroup col[name='gutter']{
    width:5px !important
  }
  ::v-deep .el-table__body{
    width:100% !important
  }

</style>
