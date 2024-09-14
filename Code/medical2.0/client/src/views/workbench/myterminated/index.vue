<template>
  <div>
    <!-- 审批历史  -->
    <Comment ref='commentForm'></Comment>
    <!-- 处理窗口  -->
    <component ref='wfForm' :is='wfForm' v-on:after-load='afterWfFormload()'></component>
    <el-row v-loading='loading'>
    <div class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row class='search-row'>
            <el-col :span='24'>
              <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
                <el-col :span="6">
                  <el-form-item label='标题' prop='description'>
                    <el-input v-model='queryModel.description' :clearable='true' placeholder='请输入任务标题'></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label='终止原因' prop='deleteReason'>
                    <el-input v-model='queryModel.deleteReason' :clearable='true' placeholder='请输入终止理由'></el-input>
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
        <!-- 表格栏  开始 -->
          <el-row>
            <el-col :span='24'>
              <el-table :data='dataList'  border @sort-change='onSortChange' highlight-current-row>
                <el-table-column prop='extInfo.proc' label='流程' header-align='center' min-width='180px'></el-table-column>
                <el-table-column prop='name' label='活动' sortable='custom' header-align='center' min-width='120px'></el-table-column>
                <el-table-column prop='extInfo.desc' label='标题' sortable='custom' header-align='center' min-width='240px'></el-table-column>
                <el-table-column prop='deleteReason' label='终止原因' sortable='custom' header-align='center' min-width='200px'></el-table-column>
                <el-table-column prop='priority' label='优先级' sortable='custom' header-align='center' align='center' width = '100px'></el-table-column>
                <el-table-column prop='startTime' label='开始时间' sortable='custom' header-align='center' align='center' width = '200px'></el-table-column>
                <el-table-column prop='endTime' label='处理时间' sortable='custom' header-align='center' align='center' width = '200px'></el-table-column>
                <!--表行级操作按钮-->
                <el-table-column label='操作' header-align='center' width='85px' fixed='right'>
                  <template slot='header' slot-scope='scope'>
                    <span>操作</span>
                    <export-excel-button v-show='permission.export' :data='dataList' :tHeader='["流程", "活动", "标题", "优先级", "开始时间", "处理时间"]' :filterVal='["extInfo.proc", "name", "extInfo.desc", "priority", "startTime", "endTime"]' :plain='true'></export-excel-button>
                  </template>
                  <template slot-scope='scope'>
                    <OperationIcon v-show='permission.view' content='查看' placement='top-start' icon-name='el-icon-view' @click='onView(scope.$index, scope.row)'></OperationIcon>
                    <OperationIcon type='info' content='审批历史' placement='top-start' icon-name='el-icon-info'
                      @click='onShowComments(scope.$index, scope.row)'></OperationIcon>
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
                :page-sizes='[10, 20, 50, 100, dataTotal]'
                :page-size='10'
                layout='total, sizes, prev, pager, next, jumper'
                :total='dataTotal'>
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
import { getStartForm } from '@/api/wf/processdefinition'
import { listHistTaskPage, countHistTask } from '@/api/wf/history'
import { listResourcePermission } from '@/api/admin/common/permission'
import { parseExtInfoForList } from '@/views/wf/utils/wfUtil'
import ExportExcelButton from '@/components/ExportExcelButton'
import WfBaseUI from '@/views/wf/common/wfBaseUI'
import OperationIcon from '@/components/OperationIcon'
import Comment from '@/views/wf/common/comment'
export default {
  extends: WfBaseUI,
  components: {
    ExportExcelButton,
    OperationIcon,
    Comment
  },
  data() {
    return {
      wfForm:  null,  // 工作流表单
      currentTask: null,  // 当前任务
      permission: {
        view: false,
        export: false
      },
      queryModel:  {
        'description': '',   // 标题
        'deleteReason': ''
      },
      search: {
        offset: 0,
        limit: 10
      },
      sort: {
        sortBy: 'endTime',
        sortOrder: 'desc'
      },
      currentPage: 1,
      dataTotal: 0,
      dataList: [],
    }
  },
  methods: {
    getTaskList() {
      this.setLoad()
      let parms = {
        taskAssignee: currentUser.id,
        finished: true,
        taskVariables: [
          {
            "name": "task_flag",
            "value": "myterminated",
            "operator": "eq"
          }
        ],
        sorting: [{
          sortBy: this.sort.sortBy,
          sortOrder: this.sort.sortOrder
        }]
      }
      if(!validatenull(this.queryModel.description)){
        parms.taskDescriptionLike = '%' + this.queryModel.description + '%'
      }
      if(!validatenull(this.queryModel.deleteReason)) {
        parms.taskDeleteReasonLike = '%' + this.queryModel.deleteReason + '%'
      }

      listHistTaskPage(parms, this.search.offset, this.search.limit).then(responseData => {
        if(responseData instanceof Array) {
          this.dataList = parseExtInfoForList(responseData)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })

      this.setLoad()
      countHistTask(parms).then(responseData => {
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
    onView(index, row) {
      // task有formKey值，直接使用
      if(row.formKey) {
        this.currentTask = row
        this.loadWfForm(row.formKey)
        return
      }
      this.setLoad()
      getStartForm(row.processDefinitionId).then(responseData => {
        if(responseData.hasOwnProperty('key')) {
          if(responseData.key) {
            row.formKey = responseData.key
            this.currentTask = row
            this.loadWfForm(row.formKey)
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
    onShowComments(index, row) {
      this.$refs.commentForm.$emit('openComment', row.processInstanceId)
    },

    onSearch() {
      this.$refs['queryForm'].validate(valid => {
        if (valid) {
          this.search.offset = 0
          this.currentPage = 1
          this.getTaskList()
        } else {
          return false
        }
      })
    },
    onSizeChange(val) {
      this.currentPage = 1
      this.search.limit = val;
      this.search.offset = (this.currentPage - 1) * val
      this.getTaskList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getTaskList()
    },
    async pageInit() {
      this.getTaskList()
      this.setLoad()
      try {
        let [listPermissionRespData] = await Promise.all([
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listPermissionRespData.code == 100) {
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'myterminated:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'myterminated:export'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listRoleRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onSortChange( orderby ) {
      if(validatenull(orderby.prop)) {
        this.sort.sortBy = 'endTime'
      } else if('name' === orderby.prop) {
        this.sort.sortBy = 'taskName'
      } else if('extInfo.desc' === orderby.prop) {
        this.sort.sortBy = 'taskDescription'
      } else {
        this.sort.sortBy = orderby.prop
      }

      if(orderby.order === 'descending') {
        this.sort.sortOrder = 'desc'
      } else {
        this.sort.sortOrder = 'asc'
      }

      this.getTaskList()
    },
    afterWfFormload() {
      this.$refs.wfForm.$emit('openViewDialog', this.currentTask)
    }
  },
  mounted() {
    this.pageInit()
  }
}
</script>
