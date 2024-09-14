<template>
  <div>
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
          <el-row>
            <el-col :span='24'>
              <el-table :data='dataList' border @sort-change='onSortChange' highlight-current-row>
                <el-table-column prop='extInfo.proc' label='流程' header-align='center' min-width='180px'></el-table-column>
                <el-table-column prop='name' label='活动' sortable='custom' header-align='center' min-width='120px'></el-table-column>
                <el-table-column prop='extInfo.desc' label='标题' sortable='custom' header-align='center' min-width='240px'></el-table-column>
                <el-table-column prop='extInfo.applicantName' label='申请人' header-align='center' width='100px'></el-table-column>
                <el-table-column prop='priority' label='优先级' sortable='custom' header-align='center' align='center' width='100px'></el-table-column>
                <el-table-column prop='due' label='到期时间' sortable='custom' header-align='center' align='center' width='160px'></el-table-column>
                <el-table-column prop='created' label='创建时间' sortable='custom' header-align='center' align='center' width='200px'></el-table-column>
                <!--表行级操作按钮-->
                <el-table-column label='操作' header-align='center' width='85px' fixed='right'>
                  <template slot='header' slot-scope='scope'>
                    <span>操作</span>
                    <export-excel-button v-show='permission.export' :data='dataList' :tHeader='["流程", "活动", "标题", "申请人", "办理人", "优先级", "到期时间", "创建时间"]' :filterVal='["extInfo.proc", "name", "extInfo.desc", "extInfo.applicantName", "extInfo.approverName", "priority", "due", "created"]' :plain='true'></export-excel-button>
                  </template>
                  <template slot-scope='{$index, row}'>
                    <OperationIcon v-if='row.assignee' type='primary' v-show='permission.approve' content='处理任务' placement='top-start' icon-name='el-icon-edit' @click='onApprove($index, row)'></OperationIcon>
                    <OperationIcon v-else type='primary' v-show='permission.approve' content='认领任务' placement='top-start' icon-name='el-icon-user-solid' @click='onClaim($index, row)'></OperationIcon>
                    <OperationIcon type='info' content='审批历史' placement='top-start' icon-name='el-icon-info'
                      @click='onShowComments($index, row)'></OperationIcon>
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
    <!-- 审批历史  -->
    <Comment ref='commentForm'></Comment>
    <!-- 处理窗口  -->
    <component ref='wfForm' :is='wfForm' v-on:save-finished='getTaskList()' v-on:after-load='afterWfFormload()'></component>
  </div>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { getStartForm } from '@/api/wf/processdefinition'
import { pageTask, claimTask } from '@/api/wf/task'
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
      currentTask: null,      // 当前任务

      permission: {
        approve: false,
        export: false
      },
      queryModel:  {
        'description': ''   // 标题
      },
      search: {
        offset: 0,
        limit: 10
      },
      sort: {
        sortBy: 'created',
        sortOrder: 'desc'
      },
      currentPage: 1,
      dataTotal: 0,
      dataList: [],
    }
  },
  methods: {
    getTaskList() {
      let parms = {
        assignee: currentUser.id,
        sortBy: this.sort.sortBy,
        sortOrder: this.sort.sortOrder,
        firstResult: this.search.offset,
        maxResults: this.search.limit
      }
      if(!validatenull(this.queryModel.description)){
        parms.descriptionLike = '%' + this.queryModel.description + '%'
      }
      this.setLoad()
      pageTask(parms).then(responseData => {
        if(responseData.code == 100) {
          this.dataTotal = responseData.data.total
          this.dataList = parseExtInfoForList(responseData.data.rows)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onApprove(index, row) {
      this.currentTask = row
      // task有formKey值，直接使用
      if(row.formKey) {
        this.loadWfForm(row.formKey)
        return
      }
      this.setLoad()
      getStartForm(row.processDefinitionId).then(responseData => {
        if(responseData.hasOwnProperty('key')) {
          if(responseData.key) {
            row.formKey = responseData.key
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
    onClaim(index, row) {
      this.setLoad()
      claimTask(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.getTaskList()
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
    afterWfFormload() {
      this.$refs.wfForm.$emit('openApproveDialog', this.currentTask)
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
          this.permission.approve = listPermissionRespData.data.find(item => {
            return item.permission === 'mytask:approve'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'mytask:export'
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
        this.sort.sortBy = 'created'
      } else if('due' === orderby.prop) {
        this.sort.sortBy = 'dueDate'
      } else if('extInfo.desc' === orderby.prop) {
        this.sort.sortBy = 'description'
      } else {
        this.sort.sortBy = orderby.prop
      }

      if(orderby.order === 'descending') {
        this.sort.sortOrder = 'desc'
      } else {
        this.sort.sortOrder = 'asc'
      }

      this.getTaskList()
    }
  },
  mounted() {
    this.pageInit()
  }
}
</script>
