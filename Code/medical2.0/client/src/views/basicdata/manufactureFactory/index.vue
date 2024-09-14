<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <manufactureFactory-form ref='manufactureFactoryForm' :permission='permission' v-on:save-finished='getManufactureFactoryList'></manufactureFactory-form>
    <el-card class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
            <el-col :span="6">
              <el-form-item label='厂家名称' prop='name'>
                <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入厂家名称'></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label='厂商类型' prop='type'>
                  <el-select v-model="queryModel.type" placeholder="请选择厂家类型">
                    <el-option
                      v-for="item in types"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
              </el-form-item>
            </el-col>
             <!-- <el-col :span="5">
             
              <el-form-item label="启用状态" prop="status">
                <el-switch
                  v-model="queryModel.status"
                  active-color="#13ce66"
                  inactive-color="#dbdfe6"
                  active-value="1"
                  inactive-value="0"
                ></el-switch>
              </el-form-item>
            </el-col> -->
            <el-col :span="5">
              <el-form-item label='启用状态' prop='status'>
                  <el-select v-model="queryModel.status" placeholder="请选择是否启用">
                    <el-option
                      v-for="item in status"
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
            <el-col :span="4" style="text-align:right;padding-right:5px">
              <el-button-group>
                  <el-button
                    v-show="permission.add"
                    type="primary"
                    icon="el-icon-plus"                   
                    @click="onCreateManufactureFactory()"
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
            <el-table ref="tableRef"  height="calc(100vh - 240px)" class='drag_table' :data='manufactureFactoryList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>                
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
                  <span v-else-if="columnViews[index].showType == 'Select'">
                    <span v-if='getAttrValue(row, columnViews[index].prop) == "1"'>
                      药品厂家
                    </span>
                    <span v-else-if='getAttrValue(row, columnViews[index].prop) == "2"'>
                      材料厂家
                    </span>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :key="Math.random()" :width='140 + "px"' fixed="right">        
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='manufactureFactoryList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view' 
                    @click='onViewManufactureFactory(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit' 
                    @click='onEditManufactureFactory(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document' 
                    @click='onCopyManufactureFactory(scope.$index, scope.row)'></OperationIcon>
                  <!-- <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete' 
                    @click='onDeleteManufactureFactory(scope.$index, scope.row)'></OperationIcon> -->
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
            :page-sizes='[20, 50, 100, manufactureFactoryTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='manufactureFactoryTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->     
      </el-card>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listManufactureFactoryPage, getManufactureFactoryById, deleteManufactureFactory } from '@/api/basicdata/manufactureFactory'
import { listResourcePermission } from '@/api/admin/common/permission'
import ManufactureFactoryForm from './manufactureFactoryForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
export default {
  extends: MainUI,
  components: { 
    ManufactureFactoryForm,
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
      types:[
        {
          value: '1',
          label: '药品厂家'
        }, 
        {
          value: '2',
          label: '材料厂家'
        },
      ],//厂家类型
      status:[
            {
              value: '0',
              label: '否'
            }, 
            {
              value: '1',
              label: '是'
            },
          ],//是否启用

      queryTypes: {
        'name': 'like',
      },
      queryModel: {
        'name': '',   // 厂家名称
      },
      search: {
        params: [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}],    
        offset: 0,
        limit: 20,
		columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      currentPage: 1,
      manufactureFactoryTotal: 0,
      manufactureFactoryList: [],
        
      
      oprColumnWidth: 140,  // 操作列宽
      tableId: '1016206064147988493',
      schemeId: '1016206064147988510'
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
    getManufactureFactoryList(val) {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
      console.log(val);
      if(val=="1") {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 厂家名称
        this.search.params.push({
      	  columnName: 'name',
      	  queryType: 'like',
          value: this.queryModel.name
        })
        // 查询参数: 类型
        this.search.params.push({
      	  columnName: 'type',
      	  queryType: '=',
          value: this.queryModel.type
        })
        // 查询参数: 类型
        this.search.params.push({
      	  columnName: 'status',
      	  queryType: '=',
          value: this.queryModel.status
        })
      }
      // 数据权限: 生产厂家manufacture_factory
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listManufactureFactoryPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.manufactureFactoryTotal = responseData.data.total
          this.manufactureFactoryList = responseData.data.rows
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
        this.getManufactureFactoryList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getManufactureFactoryList()
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
      this.getManufactureFactoryList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
        console.log(val,"qiqiqggg");
      console.log(this.search.limit);
      this.getManufactureFactoryList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
        // 数据权限: 生产厂家manufacture_factory
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listManufactureFactoryRespData, listPermissionRespData] = await Promise.all([
          listManufactureFactoryPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listManufactureFactoryRespData.code == 100 && listPermissionRespData.code == 100) {
          this.manufactureFactoryTotal = listManufactureFactoryRespData.data.total
          this.manufactureFactoryList = listManufactureFactoryRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'manufactureFactory:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'manufactureFactory:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'manufactureFactory:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'manufactureFactory:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'manufactureFactory:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listManufactureFactoryRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error) 
      }
    },
    onViewManufactureFactory(index, row) {
      this.setLoad()
      getManufactureFactoryById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.manufactureFactoryForm.$emit('openViewManufactureFactoryDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
      console.log(this.columnViews);
    },
    onCreateManufactureFactory() {
      this.$refs.manufactureFactoryForm.$emit('openAddManufactureFactoryDialog')
    },
    onEditManufactureFactory(index, row) {
      this.setLoad()
      getManufactureFactoryById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.manufactureFactoryForm.$emit('openEditManufactureFactoryDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyManufactureFactory(index, row) {
      this.setLoad()
      getManufactureFactoryById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.manufactureFactoryForm.$emit('openCopyManufactureFactoryDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteManufactureFactory(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteManufactureFactory(row).then(responseData => {
          if(responseData.code == 100) {
            this.getManufactureFactoryList()
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

      this.getManufactureFactoryList()
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