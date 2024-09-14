<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <supplier-form ref='supplierForm' :permission='permission' v-on:save-finished='getSupplierList()'></supplier-form>
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
                    @click="onCreateSupplier()"
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
                  <span v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else-if="columnViews[index].showType == 'Radios'">
                    <span v-if='getAttrValue(row, columnViews[index].prop) == "1"'>
                      药品供应商
                    </span>
                    <span v-else-if='getAttrValue(row, columnViews[index].prop) == "2"'>
                      材料供应商
                    </span>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='140 + "px"' fixed="right" :key="Math.random()">        
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='supplierList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view' 
                    @click='onViewSupplier(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit' 
                    @click='onEditSupplier(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document' 
                    @click='onCopySupplier(scope.$index, scope.row)'></OperationIcon>
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
import { validatenull } from '@/utils/validate'
import { listSupplierPage, getSupplierById, deleteSupplier } from '@/api/stock/supplier'
import { listResourcePermission } from '@/api/admin/common/permission'
import SupplierForm from './supplierForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
export default {
  extends: MainUI,
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
      permission: {
        view: false,
        add: false,
        edit: false,
        remove: false,
        export: false
      },
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
      search: {
        params: [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}],    
        offset: 0,
        limit: 20,
		columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      currentPage: 1,
      supplierTotal: 0,
      supplierList: [],
        
      
      oprColumnWidth: 140,  // 操作列宽
      tableId: '1005526731044757538',
      schemeId: '1005526731044757555'
    }
  },
  methods: {
    indexMethod(index){
       return (this.currentPage-1)*this.search.limit+index +1;
    },
    reset(){
      this.$refs.queryForm.resetFields()
      this.onSearch()
    },
    getSupplierList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 名称
        this.search.params.push({
      	  columnName: 'name',
      	  queryType: 'like',
          value: this.queryModel.name
        })
        // 查询参数: 联系人
        this.search.params.push({
      	  columnName: 'linkman',
      	  queryType: 'like',
          value: this.queryModel.linkman
        })
        // 查询参数: 电话
        this.search.params.push({
      	  columnName: 'phone',
      	  queryType: 'like',
          value: this.queryModel.phone
        })
        // 查询参数: 地址
        this.search.params.push({
      	  columnName: 'address',
      	  queryType: 'like',
          value: this.queryModel.address
        })
        // 查询参数: 类型
        this.search.params.push({
      	  columnName: 'type',
      	  queryType: '=',
          value: this.queryModel.type
        })
      }
      // 数据权限: 供应商supplier
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listSupplierPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.supplierTotal = responseData.data.total
          this.supplierList = responseData.data.rows
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
        this.getSupplierList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getSupplierList()
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
      this.getSupplierList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getSupplierList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
        // 数据权限: 供应商supplier
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listSupplierRespData, listPermissionRespData] = await Promise.all([
          listSupplierPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listSupplierRespData.code == 100 && listPermissionRespData.code == 100) {
          this.supplierTotal = listSupplierRespData.data.total
          this.supplierList = listSupplierRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'supplier:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'supplier:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'supplier:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'supplier:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'supplier:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listSupplierRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error) 
      }
    },
    onViewSupplier(index, row) {
      this.setLoad()
      getSupplierById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.supplierForm.$emit('openViewSupplierDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreateSupplier() {
      this.$refs.supplierForm.$emit('openAddSupplierDialog')
    },
    onEditSupplier(index, row) {
      this.setLoad()
      getSupplierById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.supplierForm.$emit('openEditSupplierDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopySupplier(index, row) {
      this.setLoad()
      getSupplierById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.supplierForm.$emit('openCopySupplierDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteSupplier(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteSupplier(row).then(responseData => {
          if(responseData.code == 100) {
            this.getSupplierList()
            this.showMessage({type: 'success', msg: '删除成功'})
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

      this.getSupplierList()
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
  updated(){
    if(this.$refs.tableRef){
       this.$nextTick(() => {
              // tableRef是el-table绑定的ref属性值
              this.$refs.tableRef.doLayout()// 对 Table 进行重新布局
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
 // 设置表格header的高度
 /deep/ th {
   height: 44px;
 }
/deep/ th.gutter:last-of-type {
  height: 0 !important;
}
 // 设置表格body的高度
 /deep/.el-table__body-wrapper {
  //解决数据展示超出body高度不滚动bug
  overflow-y: auto;
   // 减去的是表格header的高度
   height: calc(100% - 44px) !important;
 }

 .el-table__fixed-right {
      height: 100% !important;
  }
}
</style>
<style scoped>
/deep/ .el-table__body-wrapper{
    height: calc(100% - 44px) !important;
  }
</style>