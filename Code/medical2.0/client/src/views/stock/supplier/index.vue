<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <supplier-form ref='supplierForm' :permission='permission' @save-finished='loadData'></supplier-form>
    <el-card class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
                      <el-col :span="6">
              <el-form-item label='名称' prop='name'>
                <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入名称'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='联系人' prop='linkman'>
                <el-input v-model='queryModel.linkman' :clearable='true' placeholder='请输入联系人'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='电话' prop='phone'>
                <el-input v-model='queryModel.phone' :clearable='true' placeholder='请输入电话'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='地址' prop='address'>
                <el-input v-model='queryModel.address' placeholder='请输入地址'></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label='厂商类型' prop='type'>
                  <el-select v-model="queryModel.type" placeholder="请选择供应商类型">
                    <el-option
                      v-for="item in types"
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
            <el-col :span="15" style="text-align:right;padding-right:5px">
              <el-button-group>
                  <el-button
                    v-show="permission.add"
                    type="primary"
                    icon="el-icon-plus"                   
                    @click="onCreateEntity('supplierForm')"
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
            <el-table ref="tableRef" height="calc(100vh - 300px)" class='drag_table' :data='supplierList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>                
              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
              <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span v-if='columnViews[index].showType === "Switch" || columnViews[index].showType === "Checkbox" || columnViews[index].showType === "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) === "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else-if="columnViews[index].showType === 'Radios'">
                    <span v-if='getAttrValue(row, columnViews[index].prop) === "1"'>
                      药品供应商
                    </span>
                    <span v-else-if='getAttrValue(row, columnViews[index].prop) === "2"'>
                      材料供应商
                    </span>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='140 + "px"' :key="'operate'">        
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='supplierList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view' 
                    @click='onViewEntity(scope.$index, scope.row, "supplierForm")'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit' 
                    @click='onEditEntity(scope.$index, scope.row, "supplierForm")'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document' 
                    @click='onCopyEntity(scope.$index, scope.row, "supplierForm")'></OperationIcon>
                  <!-- <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete' 
                    @click='onDeleteSupplier(scope.$index, scope.row)'></OperationIcon> -->
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
            :page-sizes='[20, 50, 100, supplierTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='supplierTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->     </el-card>
  </el-row>
</template>

<script>
import { listSupplierPage, getSupplierById, deleteSupplier } from '@/api/stock/supplier'
import listViewMixin from '@/mixins/listViewMixin'
import SupplierForm from './supplierForm'
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
    SupplierForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History
  },
  data() {
    return {
      // listViewMixin 配置
      listApi: listSupplierPage,
      getApi: getSupplierById,
      deleteApi: deleteSupplier,
      entityName: 'Supplier',
      permissionPrefix: 'supplier',

      queryTypes: {
        'name': 'like',
        'linkman': 'like',
        'phone': 'like',
        'address': 'like',
      },
      types:[
        {
          value: '1',
          label: '药品供应商'
        },
        {
          value: '2',
          label: '材料供应商'
        },
      ],//供应商类型
      queryModel: {
        'name': '',   // 名称
        'linkman': '',   // 联系人
        'phone': '',   // 电话
        'address': '',   // 地址
      },
      supplierTotal: 0,
      supplierList: [],

      oprColumnWidth: 140,  // 操作列宽
      tableId: '1005526731044757538',
      schemeId: '1005526731044757555'
    }
  },
  methods: {
    appendSearchParams() {
      this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      } else {
        this.search.params.push({columnName: 'name', queryType: 'like', value: this.queryModel.name})
        this.search.params.push({columnName: 'linkman', queryType: 'like', value: this.queryModel.linkman})
        this.search.params.push({columnName: 'phone', queryType: 'like', value: this.queryModel.phone})
        this.search.params.push({columnName: 'address', queryType: 'like', value: this.queryModel.address})
        this.search.params.push({columnName: 'type', queryType: '=', value: this.queryModel.type})
      }
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
    },
    handleListResponse(responseData) {
      this.supplierTotal = responseData.data.total
      this.supplierList = responseData.data.rows
    },
    initOptions(This) {
    }
  },
  watch: {
   
     // tableData是el-table绑定的数据
      tableData: {
          // 解决表格显示错位问题
          handler () {
              this.$nextTick(() => {
                  // tableRef是el-table绑定的ref属性值
                  this.$refs.tableRef.doLayout()// 对 Table 进行重新布局
              })
          },
          deep: true
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

</style>