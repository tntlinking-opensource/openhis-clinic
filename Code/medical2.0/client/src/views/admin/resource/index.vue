<template>
  <el-row v-loading='loading'>
    <el-container>
      <!-- 历史记录  -->
      <History :bussObject='curentRow' ></History>
      <!-- 编辑窗口  -->
      <resource-form ref='resourceForm' :permission='permission' v-on:save-finished='getResourceList()'></resource-form>
      <div class="page-left-container">
        <el-aside>
          <el-table ref='treeTable' :data='routers' row-key='id' :tree-props="{children: 'children', hasChildren: 'hasChildren'}" highlight-current-row :cell-style="function() {return {borderBottom: 'none'} }" @current-change='onLeftCurrentChange'>
            <el-table-column label='路由' prop='name'></el-table-column>
          </el-table>
        </el-aside>
      </div>
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
          <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId' :curNode='currentRouter' exclude='router_id' :routerId='$route.meta.routerId' @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
      </div>
      <!--  搜索栏  结束 -->
      <!-- 工具栏 开始 -->
      <div class="page-container-header-end">
        <div>
          <el-button v-show='permission.add' :plain='true' @click='onFastCreateResourc()'>快速添加</el-button>
          <el-button v-show='permission.add' type='primary' icon='el-icon-plus'  @click='onCreateResource()'>添加</el-button>
        </div>
      </div>
      <!-- 工具栏 结束 -->
      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>        
            <el-table class='drag_table' :data='resourceList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>                
              <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='oprColumnWidth + "px"' fixed='right'>        
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='resourceList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view' 
                    @click='onViewResource(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit' 
                    @click='onEditResource(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document' 
                    @click='onCopyResource(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete' 
                    @click='onDeleteResource(scope.$index, scope.row)'></OperationIcon>
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
            :page-sizes='[10, 20, 50, 100, resourceTotal]'
            :page-size='10'
            layout='total, sizes, prev, pager, next, jumper'
            :total='resourceTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 --> 
      </div>
    </el-container>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listResourcePage, getResourceById, deleteResource,saveResource } from '@/api/admin/resource'
import { listResourcePermission } from '@/api/admin/common/permission'
import ResourceForm from './resourceForm'
import { treeRouter } from '@/api/admin/router'

import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
export default {
  extends: MainUI,
  components: { 
    ResourceForm,
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
    	'router_id': '=',
      },
      queryModel:  {
        'name': '',   // 名称
        'router': {     // 父表 路由
          'id': null,
          'name': '',
        },
      },
      search: {
        params: [],       
        offset: 0,
        limit: 10,
        columnName: '',       // 排序字段名
        order: ''             // 排序
      },
      currentRouter: {},     //树形结构中选择的路由
      currentPage: 1,
      resourceTotal: 0,
      resourceList: [],     // 数表数据
      routers: [],           // 路由树表
    
      oprColumnWidth: 140,  // 操作列宽
      resourceDefs: [{name: '查询', permission: 'list'}, {name: '查看', permission: 'read'}, {name: '导出', permission: 'export'}, {name: '添加', permission: 'create'}, {name: '删除', permission: 'delete'}, {name: '更新', permission: 'update'}],
      tableId: '4006',
      schemeId: '6008'
    }
  },
  methods: {
    getResourceList() {
      this.search.params = []
      if(validatenull(this.currentRouter)) {
        this.$alert('请选择路由', '提示', {
          confirmButtonText: '确定',
          type: 'info'
        })
        return
      } 
      this.search.params.push({
      	columnName: 'router_id',
      	queryType: '=',
      	value: this.currentRouter.id
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
      // 数据权限: 资源sys_resource
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listResourcePage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.resourceTotal = responseData.data.total
          this.resourceList = responseData.data.rows
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
        this.getResourceList()
      } else {    
          this.$refs['queryForm'].validate(valid => {
            if (valid) {
              this.search.offset = 0
              this.currentPage = 1
              this.getResourceList()
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
      this.getResourceList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getResourceList()
    },
    async pageInit() {
      this.setLoad()
      try {
        let params = []
        // 表有禁用字段 
        params.push.apply(params, [{columnName: 'is_locked', queryType: '=', value: '0'}])
        // 数据权限: 路由sys_router
        this.pushDataPermissions(params, this.$route.meta.routerId, '4003')
        let [treeRouterRespData, listPermissionRespData] = await Promise.all([
          treeRouter({params: params, columnName: '', order: ''}),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(treeRouterRespData.code == 100 && listPermissionRespData.code == 100) {
          this.routers = treeRouterRespData.data
          this.$nextTick(() => {
            if(this.routers && this.routers.length > 0 && this.$refs.treeTable){
              this.$refs.treeTable.setCurrentRow(this.routers[0])
            }
          })
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'resource:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'resource:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'resource:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'resource:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'resource:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : treeRouterRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error) 
      }
    },
    onViewResource(index, row) {
      this.setLoad()
      getResourceById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.resourceForm.$emit('openViewResourceDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onFastCreateResourc() {
      if(validatenull(this.currentRouter)) {
        this.$alert('请选择路由', '提示', {
          confirmButtonText: '确定',
          type: 'info'
        })
        return
      }
      for(let item in this.resourceDefs) {
        let resource = {
          'name': this.resourceDefs[item].name + this.currentRouter.name,   // 名称
          'code': this.currentRouter.code,   // 代码
          'url': this.currentRouter.url,   // Url
          'permission': this.currentRouter.code.substr(this.currentRouter.code.lastIndexOf('/') + 1) + ':' + this.resourceDefs[item].permission,   // 权限
          'canPermission': '1', // 可授权
          'isDefault': 0,  // 是否默认资源
          'isLocked': 0,    // 禁用
          'router': {     // 父表 路由
            'id': this.currentRouter.id,
            'name': this.currentRouter.name,
          },
          'remarks': '',   // 备注信息
        }

        this.setLoad()
        saveResource(resource).then(responseData => {
          this.resetLoad()
          if(this.loadcount == 0) {
            this.getResourceList()
          }
        }).catch(error => {
          this.outputError(error)
          if(this.loadcount == 0) {
            this.getResourceList()
          }
        })
      }
    },
    onCreateResource() {
      if(validatenull(this.currentRouter)) {
        this.$alert('请选择路由', '提示', {
          confirmButtonText: '确定',
          type: 'info'
        })
        return
      } 
      let row={
          'router': this.currentRouter
        }
      this.$refs.resourceForm.$emit('openAddResourceDialog', row)
    },
    onEditResource(index, row) {
      this.setLoad()
      getResourceById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.resourceForm.$emit('openEditResourceDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyResource(index, row) {
      this.setLoad()
      getResourceById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.resourceForm.$emit('openCopyResourceDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },    
    onDeleteResource(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteResource(row).then(responseData => {
          if(responseData.code == 100) {
            this.getResourceList()
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

      this.getResourceList()
    },
    onLeftCurrentChange(currentRow, oldCurrentRow) {
      if(currentRow != oldCurrentRow) {
        this.currentRouter = currentRow
        this.queryModel['router'] = currentRow
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
