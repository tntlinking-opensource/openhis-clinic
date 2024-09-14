<template>
  <el-row v-loading='loading'>
    <component ref='wfForm' :is='wfForm' v-on:after-load='afterWfFormload()' v-on:save-finished='afterFinished'></component>
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
      <!-- 表格栏  开始 -->
        <el-row>
          <el-col :span='24'>
            <el-table :data='wfDraftList' border @sort-change='onSortChange' highlight-current-row>
              <el-table-column prop='name' label='名称' sortable='custom' header-align='center'></el-table-column>
              <el-table-column prop='updateDate' label='更新时间' sortable='custom' header-align='center' align='center' width='200px'></el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' width='85px' fixed='right'>
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <export-excel-button v-show='permission.export' :data='wfDraftList' :tHeader='["名称", "更新时间"]' :filterVal='["name", "updateDate"]' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                    @click='onEditWfDraft(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete'
                    @click='onDeleteWfDraft(scope.$index, scope.row)'></OperationIcon>
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
              :page-sizes='[10, 20, 50, 100, wfDraftTotal]'
              :page-size='10'
              layout='total, sizes, prev, pager, next, jumper'
              :total='wfDraftTotal'>
            </el-pagination>
          </el-col>
        </el-row>
      </div>
      <!-- 分页栏     结束 -->
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listWfDraftPage, deleteWfDraft } from '@/api/wf/wfDraft'
import { listResourcePermission } from '@/api/admin/common/permission'
import { getStartForm } from '@/api/wf/processdefinition'
import ExportExcelButton from '@/components/ExportExcelButton'
import WfBaseUI from '@/views/wf/common/wfBaseUI'
import OperationIcon from '@/components/OperationIcon'

export default {
  extends: WfBaseUI,
  components: {
    ExportExcelButton,
    OperationIcon
  },
  data() {
    return {
      wfForm:  null,  // 工作流表单
      currentDraft: null, // 当前草稿
      currentFormKey: null, // 当前的表单URL

      permission: {
        view: false,
        add: false,
        edit: false,
        remove: false,
        export: false
      },
      queryModel: {
        'name': '',   // 名称
      },
      search: {
        params: [{'columnName':'applyer_id', 'queryType': '=', 'value': currentUser.id}],
        offset: 0,
        limit: 10,
        columnName: '',       // 排序字段名
        order: ''             // 排序
      },
      currentPage: 1,
      wfDraftTotal: 0,
      wfDraftList: []
    }
  },
  methods: {
    getWfDraftList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = [{'columnName':'applyer_id', 'queryType': '=', 'value': currentUser.id},
        {columnName: 'name', queryType: 'like', value: this.queryModel.name }]

      listWfDraftPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.wfDraftTotal = responseData.data.total
          this.wfDraftList = responseData.data.rows
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
          this.getWfDraftList()
        } else {
          return false
        }
      })
    },
    onSizeChange(val) {
      this.currentPage = 1
      this.search.limit = val;
      this.search.offset = (this.currentPage - 1) * val
      this.getWfDraftList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getWfDraftList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.search.params = [{'columnName':'applyer_id', 'queryType': '=', 'value': currentUser.id}]
        let [listWfDraftRespData, listPermissionRespData] = await Promise.all([
          listWfDraftPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listWfDraftRespData.code == 100 && listPermissionRespData.code == 100) {
          this.wfDraftTotal = listWfDraftRespData.data.total
          this.wfDraftList = listWfDraftRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'wfDraft:read'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'wfDraft:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'wfDraft:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'wfDraft:delete'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'wfDraft:export'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listWfDraftRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },

    onEditWfDraft(index, row) {
      this.setLoad()
      getStartForm(row.procdefId).then(responseData => {
        if(responseData.hasOwnProperty('key')) {
          if(responseData.key) {
            this.currentDraft = row
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
    afterWfFormload() {
      this.$refs.wfForm.$emit('openDraft', this.currentDraft, this.currentFormKey)
    },
    afterFinished(action) {
      if(action == 'savedraft') { // 保存草稿
        this.getWfDraftList()
      } else {  // 发起流程
        this.setLoad()
        deleteWfDraft(this.currentDraft).then(responseData => {
          if(responseData.code == 100) {
            this.getWfDraftList()
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      }
    },
    onDeleteWfDraft(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteWfDraft(row).then(responseData => {
          if(responseData.code == 100) {
            this.getWfDraftList()
            this.showMessage({type:'success',msg:'删除成功'})
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

      this.getWfDraftList()
    }
  },
  mounted() {
    this.pageInit()
  }
}
</script>
