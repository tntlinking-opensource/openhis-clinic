<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <clinicOffice-form ref='clinicOfficeForm' :permission='permission' @save-finished='loadData'></clinicOffice-form>
    <el-card class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>

            <el-col :span="6">
              <el-form-item label='科室名称' prop='name'>
                <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入科室名称'></el-input>
              </el-form-item>
            </el-col>
            <!-- <el-col :span="6">
              <el-form-item label='是否禁用' prop='isLocked'>
                <el-switch v-model='queryModel.isLocked' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
              </el-form-item>
            </el-col> -->
            <el-col :span="5">
              <el-form-item label='是否启用' prop='isLocked'>
                  <el-select v-model="queryModel.isLocked" placeholder="请选择是否启用">
                    <el-option
                      v-for="item in isLocked"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="3" style="display:flex;justivy-content:space-around">
               <el-button
                    type="primary"
                    icon="el-icon-search"
                    @click="onSearch()"
                    :plain="true"
                  >搜索</el-button>
                <el-button
                    type="info"
                    icon="el-icon-refresh-left"
                    @click="reset"
                    :plain="true"
                  >重置</el-button>
            </el-col>
            <el-col :span="10" style="text-align:right;padding-right:5px">
              <el-button-group>
                  <el-button
                    v-show="permission.add"
                    type="primary"
                    icon="el-icon-plus"
                    @click="onCreateEntity('clinicOfficeForm')"
                    >添加</el-button
                  >
              </el-button-group>
            </el-col>
            </el-form>
          </el-row>
          <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId'  :routerId='$route.meta.routerId' @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
        </div>
        <!--  搜索栏  结束 -->

        <!-- 工具栏 开始 -->

        <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>
            <el-table ref="mutipleTable"  min-height="calc(100vh - 250px)" class='drag_table' :data='clinicOfficeList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
              <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span v-if='columnViews[index].showType === "Checkbox" || columnViews[index].showType === "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) === "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>

                  <span v-else-if="columnViews[index].prop==='isLocked'">
                    <span v-if='getAttrValue(row, columnViews[index].prop) === "0"' class='el-icon-check' style='color:#F56C6C;'></span>
                    <span v-else></span>
                  </span>
                  <span v-else-if="columnViews[index].prop==='isDefault'">
                    <span v-if='getAttrValue(row, columnViews[index].prop) === "1"' class='el-icon-check' style='color:#F56C6C;'></span>
                    <span v-else></span>
                  </span>
                   <span v-else-if="columnViews[index].prop==='isRegister'">
                    <span v-if='getAttrValue(row, columnViews[index].prop) === "1"' class='el-icon-check' style='color:#F56C6C;'></span>
                    <span v-else></span>
                  </span>
                   <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='140 + "px"' :key="'operate'">
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='clinicOfficeList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                    @click='onViewEntity(scope.$index, scope.row, "clinicOfficeForm")'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                    @click='onEditEntity(scope.$index, scope.row, "clinicOfficeForm")'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document'
                    @click='onCopyEntity(scope.$index, scope.row, "clinicOfficeForm")'></OperationIcon>
                  <!-- <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete'
                    @click='onDeleteClinicOffice(scope.$index, scope.row)'></OperationIcon> -->
                  <OperationIcon v-show='permission.view' type='info' content='历史记录' placement='top-start' icon-name='el-icon-info'
                    @click='onShowHistory(scope.$index, scope.row)'></OperationIcon>
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
            :page-sizes='[20, 50, 100, clinicOfficeTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='clinicOfficeTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->     </el-card>
  </el-row>
</template>

<script>
import { listClinicOfficePage, getClinicOfficeById, deleteClinicOffice } from '@/api/org/clinicOffice'
import listViewMixin from '@/mixins/listViewMixin'
import ClinicOfficeForm from './clinicOfficeForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
export default {
  extends: MainUI,
  mixins: [listViewMixin],
  components: {
    ClinicOfficeForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History
  },
  data() {
    return {
      listApi: listClinicOfficePage,
      getApi: getClinicOfficeById,
      deleteApi: deleteClinicOffice,
      entityName: 'ClinicOffice',
      permissionPrefix: 'clinicOffice',
      queryTypes: {
        'is_locked': '=',
        'name': 'like',
      },
      queryModel: {
        'isLocked': '',   // 禁用（0：未禁用；1：禁用）
        'name': '',   // 科室名称
      },
      isLocked: [
        {
          value: '0',
          label: '否'
        },
        {
          value: '1',
          label: '是'
        },
      ], //是否启用
      clinicOfficeTotal: 0,
      clinicOfficeList: [],
      oprColumnWidth: 140,  // 操作列宽
      tableId: '1026680647570219053',
      schemeId: '1026680647570219069'
    }
  },
  methods: {
    appendSearchParams() {
      this.search.params.push({
        columnName: 'company_id',
        queryType: '=',
        value: currentUser.company.id
      })
      if (this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      } else {
        // 查询参数: 禁用（0：未禁用；1：禁用）
        this.search.params.push({
          columnName: 'is_locked',
          queryType: '=',
          value: this.queryModel.isLocked
        })
        // 查询参数: 科室名称
        this.search.params.push({
          columnName: 'name',
          queryType: 'like',
          value: this.queryModel.name
        })
      }
    },
    handleListResponse(responseData) {
      this.clinicOfficeTotal = responseData.data.total
      this.clinicOfficeList = responseData.data.rows
    },
    initOptions(This) {
    }
  },
  watch: {
    clinicOfficeList(val) {
      if (val) {
        this.$nextTick(() => {
          this.$refs.mutipleTable.doLayout();
        });
      }
    }
  },
  updated() {
    if (this.$refs.mutipleTable) {
      this.$nextTick(() => {
        this.$refs.mutipleTable.doLayout()
      })
    }
  },
  mounted() {
    this.pageInit()
  }
}
</script>
<style lang="scss" scoped>
.page-container{
  padding: 0;
}

 .drag_table {
   overflow: auto;
}
</style>
