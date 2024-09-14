<template>
  <div>
    <!-- 编辑窗口  -->
    <wf-viewer ref='viewForm' :permission='permission'></wf-viewer>
    <component ref='wfForm' :is='wfForm' v-on:after-load='afterWfFormload()'></component>
    <el-row v-loading='loading'>
      <div class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row class='search-row'>
            <el-col :span='24' >
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
            </el-col>
          </el-row>
        </div>
      <!--  搜索栏  结束 -->
      <!-- 工具栏 开始 -->
      <div class="page-container-header">
        <div>
          <el-tabs class="wf-tabs" v-model='activeName' type='card'>
            <el-tab-pane name='List'>
              <span slot="label"><i class='el-icon-tickets'></i> 列表</span>
            </el-tab-pane>
            <el-tab-pane name='Previews'>
              <span slot="label"><i class='el-icon-menu'></i> 卡片</span>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
          <el-row v-show='activeName=="List"'>
            <el-col :span='24'>
              <el-table :data='dataList' border @sort-change='onSortChange' highlight-current-row>
                <el-table-column prop='key' label='编号' sortable='custom' header-align='center' min-width = '120px'></el-table-column>
                <el-table-column prop='name' label='名称' sortable='custom' header-align='center' min-width = '180px'></el-table-column>
                <el-table-column prop='description' label='描述' sortable='custom' header-align='center' min-width = '240px'></el-table-column>
                <el-table-column prop='version' label='版本' sortable='custom' header-align='center' align='center' width = '85px'></el-table-column>
                <!--表行级操作按钮-->
                <el-table-column label='操作' header-align='center' width='85px' fixed='right'>
                  <template slot='header' slot-scope='scope'>
                    <span>操作</span>
                    <export-excel-button v-show='permission.export' :data='dataList' :tHeader='["编号", "名称", "版本"]' :filterVal='["key", "name", "version"]' :plain='true'></export-excel-button>
                  </template>
                  <template slot-scope='scope'>
                    <OperationIcon type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                      @click='onViewProcess(scope.$index, scope.row)'></OperationIcon>
                    <OperationIcon type='primary' v-show='permission.start' content='发起流程' placement='top-start' icon-name='el-icon-video-play'
                      @click='onStartProcess(scope.$index, scope.row)'></OperationIcon>
                   </template>
                </el-table-column>
              </el-table>
            </el-col>
          </el-row>

          <el-row v-show='activeName=="Previews"' :gutter="10" style="margin: 0">
            <el-col :span='4' v-for='(proc, index) in dataList' :key='index' style="margin-bottom: 10px">
              <el-card class="wf-cards" :body-style='{ padding: "0px" }' shadow="hover">
                <el-image fit='fill' :src='baseAPI + "/rest/process-definition/" + proc.id + "/diagram"' class='image'>
                  <div slot='placeholder' class='image-slot'>
                    加载中<span class='dot'>...</span>
                  </div>
                  <div slot='error' class='image-slot'>
                    <i class='el-icon-picture-outline'></i>
                  </div>
                </el-image>
                <div style='padding: 14px;'>
                  <span class="wf-cards-name">{{proc.name}}</span>
                  <div class='bottom clearfix'>
                    <time class='time'>{{ proc.description }}</time>
                    <el-button class='button' @click='onStartProcess(index, proc)'>发起流程</el-button>
                    <el-button class='button' @click='onViewProcess(index, proc)'>查看流程</el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 表格栏  结束 -->
          <!-- 分页栏     开始 -->
          <el-row>
            <el-col :span='24'>
              <el-pagination background  @size-change='onSizeChange' @current-change='onCurrentChange' :current-page.sync='currentPage' :page-sizes='[search.limit, 20, 50, 100, dataTotal]' :page-size='search.limit' layout='total, sizes, prev, pager, next, jumper' :total='dataTotal'>
              </el-pagination>
            </el-col>
          </el-row>
          <!-- 分页栏     结束 -->
        </div>
    </el-row>
  </div>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listProcessDefinitionPage, countProcessDefinition, getStartForm, getProcessBpmnById } from '@/api/wf/processdefinition'
import { listResourcePermission } from '@/api/admin/common/permission'
import WfViewer from '@/views/wf/designer/viewerForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import WfBaseUI from '@/views/wf/common/wfBaseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: WfBaseUI,
  components: {
    WfViewer,
    ExportExcelButton,
    OperationIcon
  },
  data() {
    return {
      baseAPI: process.env.BASE_API,
      wfForm:  null,  // 工作流表单
      currentProc: null, // 当前流程
      currentFormKey: null, // 当前的表单URL

      activeName: 'List',
      permission: {
        view: false,
        start: false,
        export: false
      },
      queryModel:  {
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
      currentPage: 1,
      dataTotal: 0,
      dataList: []
    }
  },
  methods: {
    getProcessDefList() {
      this.setLoad()
      let parms = {
        // startableBy: 'admin',
        tenantIdIn: this.currentUser.company.id,  // 筛选用户所在的公司流程
        includeProcessDefinitionsWithoutTenantId: true, // 包含公共流程
        startableInTasklist: true,
        latestVersion: true,
        active: true,
        sortBy: this.sort.sortBy,
        sortOrder: this.sort.sortOrder,
        firstResult: this.search.offset,
        maxResults: this.search.limit
      }
      if(!validatenull(this.queryModel.name)){
        parms.nameLike = '%' + this.queryModel.name + '%'
      }

      listProcessDefinitionPage(parms).then(responseData => {
        if(responseData instanceof Array) {
          this.dataList = responseData
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
          this.dataTotal = responseData.count
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
          this.getProcessDefList()
        } else {
          return false
        }
      })
    },
    onSizeChange(val) {
      this.currentPage = 1
      this.search.limit = val;
      this.search.offset = (this.currentPage - 1) * val
      this.getProcessDefList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getProcessDefList()
    },
    async pageInit() {
      this.getProcessDefList()

      this.setLoad()
      try {
        let [listPermissionRespData] = await Promise.all([
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listPermissionRespData.code == 100) {
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'process:view'
          })
          this.permission.start = listPermissionRespData.data.find(item => {
            return item.permission === 'process:start'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'process:export'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listRoleRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
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
    onStartProcess(index, row) {
      this.setLoad()
      getStartForm(row.id).then(responseData => {
        if(responseData.hasOwnProperty('key')) {
          if(responseData.key) {
            this.currentProc = row
            this.currentFormKey = responseData.key
            this.loadWfForm(responseData.key)
          } else {
            this.showMessage({type:'warning' , msg: '流程未配置form，请联系管理员。'})
          }
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onSortChange( orderby ) {
      this.sort = {
        sortBy: validatenull(orderby.prop) ? orderby.prop : 'key',
        sortOrder: orderby.order === 'descending' ? 'desc' : 'asc'
      }
      this.getProcessDefList()
    },
    afterWfFormload() {
      this.$refs.wfForm.$emit('openCreateDialog', this.currentProc, this.currentFormKey)
    }
  },
  mounted() {
    this.pageInit()
  }
}
</script>

<style>
  .time {
    font-size: 13px;
    color: #999;
  }

  .bottom {
    margin-top: 13px;
    line-height: 12px;
  }

  .image {
    width: 100%;
    display: block;
  }

  .clearfix:before,
  .clearfix:after {
      display: table;
      content: '';
  }

  .clearfix:after {
      clear: both
  }
</style>
