<template>
  <div>
    <el-row v-if="dataList.length > 0">
      <el-col :span="24">
        <ul class="data-list">
          <li v-for="(item,index) in dataList" :key="index">
            <span class="title"><label for="" class="title-icon"><i class="el-icon-document" style="color: #fff"></i></label> {{item.extInfo.desc?item.extInfo.desc : '——'}}</span>
            <span class="text-span"><i class="el-icon-time" style="color: #999"></i> {{item.startTime}}</span>
            <OperationIcon content='查看' placement='top-start' icon-name='el-icon-view' @click='onView(index, item)'></OperationIcon>
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
  import { listHistTaskPage, countHistTask } from '@/api/wf/history'
  import { listResourcePermission } from '@/api/admin/common/permission'
  import { parseExtInfoForList } from '@/views/wf/utils/wfUtil'
  import ExportExcelButton from '@/components/ExportExcelButton'
  import WfBaseUI from '@/views/wf/common/wfBaseUI'
  import OperationIcon from '@/components/OperationIcon'
  import Comment from '@/views/wf/common/comment'
  export default {
    extends: WfBaseUI,
    name: "MyApplied",
    components: {
      ExportExcelButton,
      OperationIcon,
      Comment
    },
    data () {
      return {
        wfForm:  null,  // 工作流表单
        currentTask: null,  // 当前任务
        permission: {
          view: false,
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
          sortBy: 'endTime',
          sortOrder: 'desc'
        },
        currentPage: 1,
        dataTotal: 0,
        dataList: [],
        processinststatus: 'all',
      }
    },
    watch:{

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
    },
    methods: {
      setCardLoad(isLoad) {
        this.$emit('setCardLoad', isLoad)
      },
      getTaskList() {
        this.setLoad()
        this.setCardLoad(true)
        let parms = {
          taskOwner: currentUser.id,
          finished: true,
          taskVariables: [
            {
              "name": "task_flag",
              "value": "myapplied",
              "operator": "eq"
            }
          ],
          sorting: [{
            sortBy: this.sort.sortBy,
            sortOrder: this.sort.sortOrder
          }]
        }
        if(this.processinststatus !== 'all'){
          parms.processVariables = []
          parms.processVariables.push(
            {
              "name": "PROCESS_INST_STATUS",
              "value": this.processinststatus,  //ACTIVE--审批中  、  COMPLETED-- 已结束 、  TERMINATED-- 已终止
              "operator":"eq"
            }
          )
        }
        if(!validatenull(this.queryModel.description)){
          parms.taskDescriptionLike = '%' + this.queryModel.description + '%'
        }

        listHistTaskPage(parms, this.search.offset, this.search.limit).then(responseData => {
          if(responseData instanceof Array) {
            this.dataList = parseExtInfoForList(responseData)
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
          this.setCardLoad(false)
        }).catch(error => {
          this.outputError(error)
        })

        this.setLoad()
        this.setCardLoad(true)
        countHistTask(parms).then(responseData => {
          if(typeof(responseData.count) == 'number') {
            this.dataTotal = responseData.count
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
          this.setCardLoad(false)
        }).catch(error => {
          this.outputError(error)
        })
      },
      onView(index, row) {
        // task有formKey值，直接使用
        if(row.formKey) {
          this.currentTask = row
          this.loadWfForm(row.formKey)
          this.$emit('parentWf', 'MY_APPLIED_VIEW', this.currentTask, this.wfForm)
          return
        }
        this.setLoad()
        getStartForm(row.processDefinitionId).then(responseData => {
          if(responseData.hasOwnProperty('key')) {
            if(responseData.key) {
              row.formKey = responseData.key
              this.currentTask = row
              this.loadWfForm(row.formKey)
              this.$emit('parentWf', 'MY_APPLIED_VIEW', this.currentTask, this.wfForm)
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
              return item.permission === 'myapplied:read'
            })
            this.permission.export = listPermissionRespData.data.find(item => {
              return item.permission === 'myapplied:export'
            })
          } else {
            this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listRoleRespData)
          }
          this.resetLoad()
        } catch(error) {
          this.outputError(error)
        }
      },
      afterWfFormload() {
        // this.$refs.wfForm.$emit('openViewDialog', this.currentTask)
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
      .opt-icon {
        margin-left: 10px;
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
