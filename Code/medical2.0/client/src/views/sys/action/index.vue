<template>
  <el-row v-loading='loading'>
    <!-- 编辑窗口  -->
    <action-form ref='actionForm' :permission='permission' @save-finished='loadData'></action-form>
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
                      <span v-if='columnViews[index].showType === "Switch" || columnViews[index].showType === "Checkbox" || columnViews[index].showType === "Radio"'>
                        <li v-if='getAttrValue(row, columnViews[index].prop) === "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                      </span>
                      <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType)}}</span>
                    </template>
                  </el-table-column>
                  <!--表行级操作按钮-->
                  <el-table-column label='操作' header-align='center' :width='oprColumnWidth + "px"'>
                    <template slot='header' slot-scope="scope">
                      <span>操作</span>
                      <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                      <export-excel-button v-show='permission.export' :data='actionList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                    </template>
                    <template slot-scope='scope'>
                      <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                        @click='onViewEntity(scope.$index, scope.row, "actionForm")'></OperationIcon>
                      <OperationIcon v-show='permission.restore && scope.row.status === "0" && scope.row.actionType.value === "deleted"' type='primary' content='还原' placement='top-start' icon-name='el-icon-refresh-left'
                        @click='onRestoreAction(scope.$index, scope.row)'></OperationIcon>
                      <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                        @click='onEditEntity(scope.$index, scope.row, "actionForm")'></OperationIcon>
                      <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document'
                        @click='onCopyEntity(scope.$index, scope.row, "actionForm")'></OperationIcon>
                      <OperationIcon v-show='permission.remove' type='danger' content='隐藏' placement='top-start' icon-name='el-icon-delete'
                        @click='onDeleteEntity(scope.$index, scope.row, deleteAction)'></OperationIcon>
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
import listViewMixin from '@/mixins/listViewMixin'
import ActionForm from './actionForm'
import DataRangePicker from '@/components/DataRangePicker'
import NumberRangeInput from '@/components/NumberRangeInput'
import { listDictItemAll } from '@/api/sys/dictItem'
import { getDictItemsByCode, DICT_CODE } from '@/utils/dictCache'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: MainUI,
  mixins: [listViewMixin],
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
      // listViewMixin 配置
      listApi: listActionPage,
      getApi: getActionById,
      deleteApi: deleteAction,
      entityName: 'Action',
      permissionPrefix: 'action',
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
      actionTotal: 0,
      actionList: [],

      actionType_List: [],    // 操作

      tableId: '4000',
      schemeId: '6012'
    }
  },
  methods: {
    appendSearchParams() {
      if (this.moreCodition) {
        this.search.params = this.compositeCondition()
      } else {
        this.search.params = [
          { columnName: 'action_type', queryType: '=', value: validatenull(this.queryModel.actionType.value) ? '' : this.queryModel.actionType.value },
          { columnName: 'object_name', queryType: 'like', value: this.queryModel.objectName },
          { columnName: 'create_by', queryType: 'like', value: this.queryModel.createBy },
          { columnName: 'create_date', queryType: 'between', value: this.queryModel.createDate }
        ]
      }
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
    },
    handleListResponse(responseData) {
      this.actionTotal = responseData.data.total
      this.actionList = responseData.data.rows
    },
    onCreateAction() {
      this.$refs.actionForm.openAddActionDialog()
    },
    onRestoreAction(index, row) {
      this.setLoad()
      restoreAction(row).then(responseData => {
        if(responseData.code === 100) {
          row.status = '1'
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    initOptions(This) {
      getDictItemsByCode(DICT_CODE.ACTION_TYPE).then((data) => {
        this.actionType_List = data
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
