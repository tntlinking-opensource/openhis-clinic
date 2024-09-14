<template>
  <el-row v-loading='loading'>
    <el-container>
      <!-- 历史记录  -->
      <History :bussObject='curentRow' ></History>
      <!-- 编辑窗口  -->
      <role-form ref='roleForm' :permission='permission' v-on:save-finished='getRoleList()'></role-form>
      <role-permission ref="rolePermission"></role-permission>
      <!-- 非系统管理员不可见  -->
      <div class="page-left-container" v-show='currentUser.id == 1001'>
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
                <el-form-item label='名称' prop='name'>
                  <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入名称'></el-input>
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
            <el-col :span="15" style="text-align:right;padding-right:5px">
              <el-button-group>
                  <el-button
                    v-show="permission.add"
                    type="primary"
                    icon="el-icon-plus"                   
                    @click="onCreateRole()"
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
            <el-table ref="mutipleTable" height="calc(100vh - 254px)" class='drag_table' :data='roleList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
              <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) == "0"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType)}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='155 + "px"'  :key="Math.random()">
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='roleList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                    @click='onViewRole(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                    @click='onEditRole(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document'
                    @click='onCopyRole(scope.$index, scope.row)'></OperationIcon>
                    <OperationIcon v-show='permission.edit' type='primary' content='权限设置' placement='top-start' icon-name='el-icon-menu'
                      @click='handleSetPermission(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete'
                    @click='onDeleteRole(scope.$index, scope.row)'></OperationIcon>
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
            :page-sizes='[20, 50, 100, roleTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='roleTotal'>
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
import { listRolePage, getRoleById, deleteRole } from '@/api/admin/role'
import { listResourcePermission } from '@/api/admin/common/permission'
import RoleForm from './roleForm'
import { treeCompany } from '@/api/org/company'

import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import RolePermission from '@/views/admin/common/rolePermission'
import History from '@/views/components/history'
export default {
  extends: MainUI,
  components: {
    RoleForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    RolePermission,
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
        limit: 20,
        columnName: '',       // 排序字段名
        order: ''             // 排序
      },
      currentCompany: {},     //树形结构中选择的公司
      currentPage: 1,
      roleTotal: 0,
      roleList: [],     // 数表数据
      companys: [],           // 公司树表

      oprColumnWidth: 165,  // 操作列宽
      tableId: '4012',
      schemeId: '6011'
    }
  },
  methods: {
    indexMethod(index){
       return (this.currentPage-1)*this.search.limit+index +1;
    },
    reset(){
      this.$refs.queryForm.resetFields()
      this.onSearch()
    },
    getRoleList() {
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
      // 数据权限: 角色sys_role
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listRolePage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.roleTotal = responseData.data.total
          this.roleList = responseData.data.rows
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
        this.getRoleList()
      } else {
          this.$refs['queryForm'].validate(valid => {
            if (valid) {
              this.search.offset = 0
              this.currentPage = 1
              this.getRoleList()
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
      this.getRoleList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getRoleList()
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
          treeCompany({params: params, columnName: '', order: ''}),
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
            return item.permission === 'role:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'role:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'role:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'role:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'role:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : treeCompanyRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onViewRole(index, row) {
      this.setLoad()
      getRoleById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.roleForm.$emit('openViewRoleDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreateRole() {
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
      this.$refs.roleForm.$emit('openAddRoleDialog', row)
    },
    onEditRole(index, row) {
      this.setLoad()
      getRoleById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.roleForm.$emit('openEditRoleDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyRole(index, row) {
      this.setLoad()
      getRoleById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.roleForm.$emit('openCopyRoleDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteRole(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteRole(row).then(responseData => {
          if(responseData.code == 100) {
            this.getRoleList()
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

      this.getRoleList()
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
    },
    handleSetPermission(index, row) {
      this.$refs.rolePermission.$emit('openSetPermissionDialog', row)
    }
  },
  watch: {
    roleList(val){
      if(val){
        this.$nextTick(() => {
            this.$refs.mutipleTable.doLayout();
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
  /deep/.el-table{
    .el-table__fixed-body-wrapper{
      top: 47px !important;
    }
  }
  /deep/ .el-table__fixed-right-patch{
    width:5px !important
  }
  /deep/ .el-table colgroup col[name='gutter']{
    width:5px !important
  }
  /deep/ .el-table__body{
    width:100% !important
  }
  .drag_table {
 // 设置表格header的高度
 /deep/ th {
   height: 44px;
 }
/deep/ th.gutter:last-of-type {
  height: 0 !important;
}
 // 设置表格body的高度
 /deep/.el-table__body-wrapper {
  //解决数据展示超出body高度不滚动bug
  overflow-y: auto;
   // 减去的是表格header的高度
   height: calc(100% - 44px) !important;
 }
  .el-table__fixed-right {
      height: 100% !important;
  }
}
</style>
