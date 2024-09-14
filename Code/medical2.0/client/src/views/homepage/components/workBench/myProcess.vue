<template>
  <div>
    <el-row v-if="dataList.length > 0">
      <el-col :span="24">
        <ul class="data-list">
          <li v-for="(item,index) in dataList" :key="index">
            <span class="title"><label for="" class="title-icon"><i class="el-icon-document" style="color: #999"></i></label> {{item.name}}</span>
            <OperationIcon type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                           @click='onViewProcess(index, item)'></OperationIcon>
            <OperationIcon type='primary' content='发起流程' placement='top-start' icon-name='el-icon-video-play'
                           @click='onStartProcess(index, item)'></OperationIcon>
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
  import { listProcessDefinitionPage, countProcessDefinition, getStartForm, getProcessBpmnById } from '@/api/wf/processdefinition'
  import { listResourcePermission } from '@/api/admin/common/permission'
  import WfViewer from '@/views/wf/designer/viewerForm'
  import ExportExcelButton from '@/components/ExportExcelButton'
  import WfBaseUI from '@/views/wf/common/wfBaseUI'
  import OperationIcon from '@/components/OperationIcon'
	export default {
    extends: WfBaseUI,
		name: "MyProcess",
    components: {
      WfViewer,
      ExportExcelButton,
      OperationIcon
    },
    data () {
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
      getProcessDefList() {
        this.setLoad()
        this.setCardLoad(true)
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
          this.setCardLoad(false)
        }).catch(error => {
          this.outputError(error)
        })

        this.setLoad()
        this.setCardLoad(true)
        countProcessDefinition(parms).then(responseData => {
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
            listResourcePermission('759165723333517532')
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
            // this.$refs.viewForm.$emit('openViewer', responseData)
            this.$emit('parentWf', 'MY_PROCESS_VIEW', responseData)
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
              this.$emit('parentWf', 'MY_PROCESS_START', this.currentProc, this.currentFormKey, this.wfForm)
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
        // this.$refs.wfForm.$emit('openCreateDialog', this.currentProc, this.currentFormKey)
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
