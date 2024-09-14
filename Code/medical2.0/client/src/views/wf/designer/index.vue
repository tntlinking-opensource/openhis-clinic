<template>
  <el-row v-loading='loading'>
    <el-container>
      <!-- 编辑窗口  -->
      <wf-viewer ref='viewForm' :permission='permission'></wf-viewer>
      <wf-designer ref='designForm' :permission='permission' v-on:save-finished='getProcessList()'></wf-designer>
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
        <el-row class='search-row'>
          <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
            <el-col :span="6">
              <el-form-item label='名称' prop='name'>
                <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入流程名称'></el-input>
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
                <!-- <el-tooltip  effect="light" content="更多" placement="top-start">
                  <el-button type="primary" icon="el-icon-d-arrow-right" @click='onMoreCodition()' :plain='true'></el-button>
                </el-tooltip> -->
              </el-button-group> 
            </el-col>
          </el-form>
        </el-row>
      </div>
        <!--  搜索栏  结束 -->

      <!-- 工具栏 开始 -->
      <div class="page-container-header-end">
        <div>
          <el-button type='primary' v-show='permission.add' icon='el-icon-plus' :plain='true' @click='onCreateProcess()'>添加</el-button>
        </div>
      </div>
      <!-- 工具栏 结束 -->
        <!-- 表格栏  开始 -->
        <el-row>
          <el-col :span='24'>
            <el-table :data='processList' border @sort-change='onSortChange' highlight-current-row>
              <el-table-column prop='key' label='编号' sortable='custom' header-align='center' width = '180px'></el-table-column>
              <el-table-column prop='name' label='名称' sortable='custom' header-align='center' width = '280px'></el-table-column>
              <el-table-column prop='description' label='描述' sortable='custom' header-align='center'></el-table-column>
              <el-table-column prop='version' label='版本' sortable='custom' header-align='center' align='center' width = '85px'></el-table-column>
              <el-table-column prop='suspended' label='挂起' sortable='custom' header-align='center' align='center' width = '85px'>
                <template slot-scope='{row,$index}'>
                  <span v-if='permission.suspend'>
                    <OperationIcon v-if='row.suspended == false' type='danger' content='挂起' placement='top-start' icon-name='el-icon-video-pause' @click='onSuspended($index, row)'></OperationIcon>
                    <OperationIcon v-else type='primary' content='启用' placement='top-start' icon-name='el-icon-video-play' @click='onSuspended($index, row)'></OperationIcon>
                  </span>
                  <span v-else>
                    <li v-if='row.suspended' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' width='85px' fixed='right'>
                <template slot='header' slot-scope='scope'>
                  <span>操作</span>
                  <export-excel-button v-show='permission.export' :data='processList' :tHeader='["编号", "名称", "版本", "挂起"]' :filterVal='["key", "name", "version", "suspended"]'   :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon type='info' v-show='permission.edit' content='查看' placement='top-start' icon-name='el-icon-view'
                    @click='onViewProcess(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon type='primary' v-show='permission.edit' content='编辑' placement='top-start' icon-name='el-icon-edit'
                    @click='onEditProcess(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon  type='danger' v-show='permission.remove && scope.row.suspended' content='删除' placement='top-start' icon-name='el-icon-delete'
                                     @click='onDeleteProcess(scope.$index, scope.row)'></OperationIcon>
                 </template>
              </el-table-column>
            </el-table>
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
              :page-sizes='[10, 20, 50, 100, processTotal]'
              :page-size='10'
              layout='total, sizes, prev, pager, next, jumper'
              :total='processTotal'>
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
import { listProcessDefinitionPage, countProcessDefinition, getProcessBpmnById, suspendProcessDefinition } from '@/api/wf/processdefinition'
import { countProcInstance } from '@/api/wf/procinst'
import { deleteDeployment } from '@/api/wf/deployment'
import { listResourcePermission } from '@/api/admin/common/permission'
import { treeCompany } from '@/api/org/company'
import WfDesigner from './designerForm'
import WfViewer from './viewerForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import WfBaseUI from '@/views/wf/common/wfBaseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: WfBaseUI,
  components: {
    WfViewer,
    WfDesigner,
    ExportExcelButton,
    OperationIcon
  },
  data() {
    return {
      permission: {
        view: false,
        add: false,
        edit: false,
        suspend: false,
        export: false,
        remove: false
      },
      queryModel:  {
        'company': {     // 父表 公司
          'id': validatenull(parent) || validatenull(parent.company) ? null : parent.company.id,
          'name': validatenull(parent) || validatenull(parent.company) ? null : parent.company.name,
        },
        'name': ''   // 名称
      },
      search: {
        offset: 0,
        limit: 10
      },
      sort: {
        sortBy: 'key',
        sortOrder: 'asc'
      },
      currentCompany: {},     //树形结构中选择的公司
      companys: [],           // 公司树表
      currentPage: 1,
      processTotal: 0,
      processList: [],
    }
  },
  methods: {
    getProcessList() {
      if(validatenull(this.currentCompany)) {
        this.$alert('请选择公司', '提示', {
          confirmButtonText: '确定',
          type: 'info'
        })
        return
      }
      let parms = {
        latestVersion: true,
        sortBy: this.sort.sortBy,
        sortOrder: this.sort.sortOrder,
        firstResult: this.search.offset,
        maxResults: this.search.limit
      }
      if(!validatenull(this.queryModel.name)){
        parms.nameLike = '%' + this.queryModel.name + '%'
      }
      if(this.currentCompany.id == 'all') {
        parms.withoutTenantId = true
      } else {
        parms.tenantIdIn = this.currentCompany.id
      }

      this.setLoad()
      listProcessDefinitionPage(parms).then(responseData => {
        if(responseData instanceof Array) {
          this.processList = responseData
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })

      this.setLoad()
      countProcessDefinition(parms).then(responseData => {
        if(typeof(responseData.count) == 'number') {
          this.processTotal = responseData.count
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })


    },
    onSearch() {
      this.$refs['queryForm'].validate(valid => {
        if (valid) {
          this.search.offset = 0
          this.currentPage = 1
          this.getProcessList()
        } else {
          return false
        }
      })
    },
    onSizeChange(val) {
      this.currentPage = 1
      this.search.limit = val;
      this.search.offset = (this.currentPage - 1) * val
      this.getProcessList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getProcessList()
    },
    async pageInit() {
      this.setLoad()
      try {
        let params = []
        /* 非系统管理员，只能查询自己所在公司的数据 */
        if(currentUser.id != 1001) {
          params.push({'columnName':'id', 'queryType': '=', 'value': currentUser.company.id})
        }
        let [treeCompanyRespData, listPermissionRespData] = await Promise.all([
          treeCompany({params: params, columnName: '', order: ''}),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(treeCompanyRespData.code == 100 && listPermissionRespData.code == 100) {
          this.companys = treeCompanyRespData.data
          /* 系统管理员，而外增加管理公用流程权限 */
          if(currentUser.id == 1001) {
            this.companys.splice(0, 0, {id:'all', name:'公用流程'})
          }
          this.$nextTick(() => {
            if(this.companys && this.companys.length > 0 && this.$refs.treeTable){
              this.$refs.treeTable.setCurrentRow(this.companys[0])
            }
          })
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'designer:read'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'designer:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'designer:update'
          })
          this.permission.suspend = listPermissionRespData.data.find(item => {
            return item.permission === 'designer:suspend'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'designer:export'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'designer:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : treeCompanyRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onCreateProcess() {
      if(validatenull(this.currentCompany)) {
        this.$alert('请选择公司', '提示', {
          confirmButtonText: '确定',
          type: 'info'
        })
        return
      }

      this.$refs.designForm.$emit('openDesigner', this.currentCompany.id)
    },
    onViewProcess(index, row) {
      this.setLoad()
      getProcessBpmnById(row.id).then(responseData => {
        if(responseData.bpmn20Xml) {
          this.$refs.viewForm.$emit('openViewer', responseData)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onEditProcess(index, row) {
      this.setLoad()
      getProcessBpmnById(row.id).then(responseData => {
        if(responseData.bpmn20Xml) {
          this.$refs.designForm.$emit('openEditProcessDialog', responseData, row.tenantId)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },

    onSuspended(index, row) {
      let str = row.suspended ? '确定启用流程吗?' : '确定挂起流程吗?'
      this.$confirm(str, '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        suspendProcessDefinition(row.id, {"suspended": !row.suspended, "includeProcessInstances":false, "executionDate":null}).then(responseData => {
          if(validatenull(responseData)) {
            row.suspended = !row.suspended
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      }).catch(() => {})
    },
    onDeleteProcess(index, row) {
      this.setLoad()
      countProcInstance({ deploymentId: row.deploymentId}).then(resData => {
        this.resetLoad()
        if(resData.count) {
          this.$alert('还有未处理完的流程实例，请全部处理完成再删除。', '警告', {
            type: 'warning'
          })
        } else {
          this.$confirm('流程删除后，将不能还原。确认是否删除流程：' + row.name + '？', '确认', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.setLoad()
            deleteDeployment(row.deploymentId).then(responseData => {
              if(validatenull(responseData)) {
                this.getProcessList()
              } else {
                this.showMessage(responseData)
              }
              this.resetLoad()
            }).catch(error => {
              this.outputError(error)
            })
          }).catch(() => {})
        }
      }).catch(error => {
        this.outputError(error)
      })
    },
    onSortChange( orderby ) {
      this.sort = {
        sortBy: validatenull(orderby.prop) ? orderby.prop : 'key',
        sortOrder: orderby.order === 'descending' ? 'desc' : 'asc'
      }
      this.getProcessList()
    },
    onLeftCurrentChange(currentRow, oldCurrentRow) {
      if(currentRow != oldCurrentRow) {
        this.currentCompany = currentRow
        this.queryModel['company'] = currentRow
        this.onSearch()
      }
    }
  },
  mounted() {
    this.pageInit()
  }
}
</script>
