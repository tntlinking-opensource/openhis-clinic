<template>
  <el-row v-loading='loading'>
    <!-- 编辑窗口  -->
    <action-form ref='actionForm' :permission='permission' v-on:save-finished='getActionList()'></action-form>
    <div class="page-container">
      <!--  搜索栏  开始 -->
      <div class="query-form-container">
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
              <el-col :span="6">
                <el-form-item label='操作' prop='actionType'>
                  <el-select v-model='queryModel.actionType' value-key='value' filterable clearable placeholder='请选择操作'>
                    <el-option v-for='actionType in actionType_List' :key='actionType.value' :label='actionType.name' :value='actionType'></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label='对象名称' prop='objectName'>
                  <el-input v-model='queryModel.objectName' :clearable='true' placeholder='请输入对象名称'></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label='操作时间' prop='createDate'>
                  <data-range-picker v-model='queryModel.createDate' type='datetimerange'></data-range-picker>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label='操作者' prop='createBy'>
                  <el-input v-model='queryModel.createBy' :clearable='true' placeholder='请输入操作者'></el-input>
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
          <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId' :routerId='$route.meta.routerId' @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
      </div>

      <!-- 工具栏 开始 -->
      <div class="page-container-header-end">
        <div>
          <el-button type='primary' icon='el-icon-plus'  @click='onCreateAction()'>添加</el-button>
        </div>
      </div>
      <!-- 工具栏 结束 -->

      <!--  搜索栏  结束 -->
      <!-- 表格栏  开始 -->
          <el-row>
            <el-col :span='24'>
              <div @mouseleave='moveTableOutside'>
                <el-table class='drag_table' :data='actionList' @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
                  <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                    <template slot-scope='{row,$index}'>
                      <span v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                        <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                      </span>
                      <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType)}}</span>
                    </template>
                  </el-table-column>
                  <!--表行级操作按钮-->
                  <el-table-column label='操作' header-align='center' :width='oprColumnWidth + "px"' fixed='right'>
                    <template slot='header' slot-scope="scope">
                      <span>操作</span>
                      <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                      <export-excel-button v-show='permission.export' :data='actionList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                    </template>
                    <template slot-scope='scope'>
                      <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                        @click='onViewAction(scope.$index, scope.row)'></OperationIcon>
                      <OperationIcon v-show='permission.restore && scope.row.status == "0" && scope.row.actionType.value == "deleted"' type='primary' content='还原' placement='top-start' icon-name='el-icon-refresh-left'
                        @click='onRestoreAction(scope.$index, scope.row)'></OperationIcon>
                      <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                        @click='onEditAction(scope.$index, scope.row)'></OperationIcon>
                      <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document'
                        @click='onCopyAction(scope.$index, scope.row)'></OperationIcon>
                      <OperationIcon v-show='permission.remove' type='danger' content='隐藏' placement='top-start' icon-name='el-icon-delete'
                        @click='onDeleteAction(scope.$index, scope.row)'></OperationIcon>
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
                :page-sizes='[10, 20, 50, 100, actionTotal]'
                :page-size='10'
                layout='total, sizes, prev, pager, next, jumper'
                :total='actionTotal'>
              </el-pagination>
            </el-col>
          </el-row>
      <!-- 分页栏     结束 -->
    </div>
  </el-row>
</template>

<script>
import { restoreAction } from '@/api/sys/recycle'
import { validatenull } from '@/utils/validate'
import { listActionPage, getActionById, deleteAction } from '@/api/sys/action'
import { listResourcePermission } from '@/api/admin/common/permission'
import ActionForm from './actionForm'
import DataRangePicker from '@/components/DataRangePicker'
import NumberRangeInput from '@/components/NumberRangeInput'
import { listDictItemAll } from '@/api/sys/dictItem'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: MainUI,
  components: {
    DataRangePicker,
    NumberRangeInput,
    ActionForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon
  },
  data() {
    return {
      permission: {
        view: false,
        add: false,
        edit: false,
        remove: false,
        export: false,
        restore: false,
      },
      queryModel:  {
        'actionType': {     // 操作
          'value': '',
          'name': '',
        },
        'objectType': {     // 对象类型
          'id': null,
          'comments': '',
        },
        'objectId': '',   // 业务对象
        'objectName': '',   // 对象名称
        'status': 0,
        'remarks': '',    //备注信息
        createBy:'',      //创建人
        createDate:undefined,  //创建时间
      },
      search: {
        params: [],
        offset: 0,
        limit: 10,
        columnName: '',       // 排序字段名
        order: ''             // 排序
      },
      currentPage: 1,
      actionTotal: 0,
      actionList: [],

      actionType_List: [],    // 操作

      tableId: '4000',
      schemeId: '6012'
    }
  },
  methods: {
    resetFields(){
        this.$refs.queryForm.resetFields();
    },
    getActionList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = []
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 操作
        this.search.params.push({
      	  columnName: 'action_type',
      	  queryType: '=',
          value: validatenull(this.queryModel.actionType.value) ? '' : this.queryModel.actionType.value
        })
        // 查询参数: 对象名称
        this.search.params.push({
      	  columnName: 'object_name',
      	  queryType: 'like',
          value: this.queryModel.objectName
        })
        // 查询参数: 操作者
        this.search.params.push({
      	  columnName: 'create_by',
      	  queryType: 'like',
          value: this.queryModel.createBy
        })
        // 查询参数: 操作时间
        this.search.params.push({
      	  columnName: 'create_date',
      	  queryType: 'between',
          value: this.queryModel.createDate
        })
      }
      // 数据权限: 操作日志sys_action
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listActionPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.actionTotal = responseData.data.total
          this.actionList = responseData.data.rows
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
        this.getActionList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getActionList()
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
      this.getActionList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getActionList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = []
        // 数据权限: 操作日志sys_action
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listActionRespData, listPermissionRespData] = await Promise.all([
          listActionPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listActionRespData.code == 100 && listPermissionRespData.code == 100) {
          this.actionTotal = listActionRespData.data.total
          this.actionList = listActionRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'action:read'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'action:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'action:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'action:delete'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'action:export'
          })
          this.permission.restore = listPermissionRespData.data.find(item => {
            return item.permission === 'action:restore'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listActionRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onCreateAction() {
      this.$refs.actionForm.$emit('openAddActionDialog')
    },
    onViewAction(index, row) {
      this.setLoad()
      getActionById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.actionForm.$emit('openViewActionDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onEditAction(index, row) {
      this.setLoad()
      getActionById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.actionForm.$emit('openEditActionDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyAction(index, row) {
      this.setLoad()
      getActionById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.actionForm.$emit('openCopyActionDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteAction(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteAction(row).then(responseData => {
          if(responseData.code == 100) {
            this.getActionList()
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
    onRestoreAction(index, row) {
      this.setLoad()
      restoreAction(row).then(responseData => {
        if(responseData.code == 100) {
          row.status = '1'
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onSortChange( orderby ) {
      if(validatenull(orderby.prop)) {
        this.search.columnName = ''
        this.search.order = ''
      } else  {
        this.search.columnName = orderby.prop
        this.search.order = orderby.order === 'descending' ? 'desc' : 'asc'
      }

      this.getActionList()
    },
    initOptions(This) {
      let actionType_search = {
        params: [{'columnName': 'dict_type_id', 'queryType': '=', 'value': '5000'}]
      }
      // 字段对应表上filter条件
        actionType_search.params.push.apply(actionType_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(actionType_search.params, this.$route.meta.routerId, '4005')
      this.actionType_List = []
      listDictItemAll(actionType_search).then(responseData => {
        this.actionType_List = responseData.data
      })
    }
  },
  watch: {
  },
  mounted() {
    this.pageInit()
  }
}
</script>
