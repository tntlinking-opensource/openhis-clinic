<template>
  <div>
    <el-row v-if="dataList.length > 0">
      <el-col :span="24">
        <ul class="data-list">
          <li v-for="(item,index) in dataList" :key="index">
            <span class="title"><label for="" class="title-icon"><i class="el-icon-document" style="color: #fff"></i></label> {{item.extInfo.desc?item.extInfo.desc : '——'}}</span>
            <span class="text-span" style="width: 100px; text-align: center">{{item.extInfo.applicantName}}</span>
            <span class="text-span"><i class="el-icon-time" style="color: #999"></i> {{item.created}}</span>
            <OperationIcon type='primary' content='处理任务' placement='top-start' icon-name='el-icon-edit' @click='onApprove(index, item)'></OperationIcon>
          </li>
        </ul>
      </el-col>
    </el-row>
    <el-row class="card-pagination" v-if="dataList.length > 0">
      <el-col :span='24'>
        <el-pagination
          background
          @size-change='onSizeChange'
          @current-change='onCurrentChange'
          :current-page.sync='currentPage'
          :page-size='5'
          layout='prev, pager, next'
          :total='dataTotal'>
        </el-pagination>
      </el-col>
    </el-row>
    <div class="no-data" v-if="dataList.length === 0">暂无数据</div>
  </div>
</template>

<script>
  import { isLightOrDark } from '@/utils/common'
  import { validatenull } from '@/utils/validate'
  import { getStartForm } from '@/api/wf/processdefinition'
  import { pageTask } from '@/api/wf/task'
  import { listResourcePermission } from '@/api/admin/common/permission'
  import { parseExtInfoForList } from '@/views/wf/utils/wfUtil'
  import ExportExcelButton from '@/components/ExportExcelButton'
  import WfBaseUI from '@/views/wf/common/wfBaseUI'
  import OperationIcon from '@/components/OperationIcon'
  import Comment from '@/views/wf/common/comment'
	export default {
    extends: WfBaseUI,
		name: "MyTask",
    props: ['id', 'panelSetIcon'],
    components: {
      ExportExcelButton,
      OperationIcon,
      Comment
    },
    data () {
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
          limit: 5
        },
        sort: {
          sortBy: 'created',
          sortOrder: 'desc'
        },
        currentPage: 1,
        dataTotal: 0,
        dataList: []
      }
    },
    computed: {
      ...Vuex.mapGetters(['settings']),
      isLight() {
        return function(colorType) {
          return isLightOrDark(this.settings[colorType])
        }
      },
      styleObject() {
        return {
          '--active-color': this.settings.theme
        }
      }
    },
    mounted() {
      this.pageInit()
      this.$nextTick(() => {
        this.$on('', () => {

        })
      })
    },
    methods: {
      setCardLoad(isLoad) {
        this.$emit('setCardLoad', isLoad)
      },
      getTaskList() {
        this.setLoad()
        this.setCardLoad(true)
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

        pageTask(parms).then(responseData => {
          if(responseData.code == 100) {
            this.dataTotal = responseData.data.total
            this.dataList = parseExtInfoForList(responseData.data.rows)
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
          this.setCardLoad(false)
        }).catch(error => {
          this.outputError(error)
        })
      },
      onApprove(index, row) {
        this.currentTask = row
        // task有formKey值，直接使用
        if(row.formKey) {
          this.loadWfForm(row.formKey)
          // this.$emit('parentWfFormload',  this.currentTask, this.wfForm)
          this.$emit('parentWf', 'MY_TASK_APPROVE', this.currentTask, this.wfForm)
          return
        }
        this.setLoad()
        getStartForm(row.processDefinitionId).then(responseData => {
          if(responseData.hasOwnProperty('key')) {
            if(responseData.key) {
              row.formKey = responseData.key
              this.loadWfForm(responseData.key)
              // this.$emit('parentWfFormload',  this.currentTask, this.wfForm)
              this.$emit('parentWf', 'MY_TASK_APPROVE', this.currentTask, this.wfForm)
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
      afterWfFormload() {
        // this.$emit('parentWfFormload',  this.currentTask)
        // this.$refs.wfForm.$emit('openApproveDialog', this.currentTask)
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
            listResourcePermission('759165723333517532')
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
      }
    }
	}
</script>

<style lang="scss" scoped>
  .title-icon {
    background-color: var(--active-color)!important;
    border-radius: 100%;
    padding: 5px;
    width: 22px;
    height: 22px;
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    top: 50%;
    margin-top: -11px;
    left: 0;
    i {
      color: #fff!important;
      font-size: 12px!important;
    }
  }
  .card-pagination {
    position: absolute;
    bottom: 0;
    right: 0;
  }
  ul.data-list {
    list-style: none;
    padding: 0;
    margin: 0;
    li {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 34px;
      font-size: 12px;
      padding: 0 10px;
      &:nth-child(odd) {
        background-color: #fbfbfb;
      }
      &:hover {
        background-color: #f5f5f5;
      }
      span {
        display: inline-block;
        &:first-child {
          flex: 1;
          white-space: nowrap;
          text-overflow: ellipsis;
          overflow: hidden;
          display: inline-block;
          height: 100%;
          line-height: 34px;
          position: relative;
          padding-left: 32px;
        }
        &.text-span {
          padding: 0 20px;
          width: 220px;
          text-align: left;
        }
      }
    }
  }
  .no-data {
    position: absolute;
    width: 100%;
    font-size: 12px;
    text-align: center;
    top: 48.99%;
  }
</style>
