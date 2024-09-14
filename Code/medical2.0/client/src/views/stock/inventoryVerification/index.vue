<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <inventoryVerification-form ref='inventoryVerificationForm' :permission='permission' v-on:save-finished='getInventoryVerificationList()'></inventoryVerification-form>
    <div class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
                <el-col :span="24/3">
                  <el-form-item   label="创建时间" prop="createDate">
                     <el-date-picker
                        v-model="queryModel.createDate"
                        type="datetimerange"
                        align="right"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        format="yyyy-MM-dd HH:mm:ss"
                        :default-time="['00:00:00', '23:59:59']">
                      </el-date-picker>
              </el-form-item>
               </el-col>

               <el-col :span="24/3">
                  <el-form-item   label="盘点单号" prop="code">
                <el-input
                  v-model="queryModel.code"
                  :clearable="true"
                  placeholder="请输入盘点单号"
                 style="width:200px"
                ></el-input>
              </el-form-item>
               </el-col>
                      <!-- <el-col :span="6">
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
              </el-col> -->
              
               <el-col :span="3" style="text-align:right;padding-right:5px;float:right;">
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
             
            </el-form>
          </el-row>
          <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId'  :routerId='$route.meta.routerId' @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
        </div>
        <!--  搜索栏  结束 -->

        <!-- 工具栏 开始 -->
        <!-- <div class="page-container-header-end">
          <div>
            <el-button v-show='permission.add' type='primary' icon='el-icon-plus' :plain='true' @click='onCreateInventoryVerification()'>新增盘点</el-button>
          </div>
        </div> -->
        <div>
          <el-row>
               
              <el-col :span="3" style="text-align:right;padding-right:5px;float:right;">
                    <el-button-group>
                        <el-button
                          v-show="permission.add"
                          type="primary"
                          icon="el-icon-plus"                   
                          @click="onCreateInventoryVerification('0')"
                          >新增药品盘点</el-button
                        >
                       
                    </el-button-group>
                  
                  </el-col>
                   <el-col :span="3" style="text-align:right;padding-right:5px;float:right;">
                    <el-button-group>
                        <el-button
                          v-show="permission.add"
                          type="primary"
                          icon="el-icon-plus"                   
                          @click="onCreateInventoryVerification('1')"
                          >新增材料盘点</el-button
                        >
                       
                    </el-button-group>
                  
                  </el-col>
                  <el-col :span="6" style="text-align:right;padding-right:5px;float:right;">
                  <el-checkbox v-model="check">是否包含库存为零</el-checkbox>
               </el-col>
          </el-row>
        </div>
        <!-- 工具栏 结束 -->
        <br>
      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>        
            <el-table  style="margin: auto" class='drag_table' ref="tableRef" height="calc(100vh - 260px)" :data='inventoryVerificationList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>                
              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
              <el-table-column align='center' v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align' :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span v-if='columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-if="columnViews[index].prop=='type'">
                    <span v-if="getAttrValue(row, columnViews[index].prop)=='0'">
                      药品
                    </span>
                    <span v-else>
                       材料
                    </span>
                  </span>
                  <span v-else-if="columnViews[index].prop=='status'">
                    <span v-if="getAttrValue(row, columnViews[index].prop)=='0'" style="color:#FF9966">
                        正在盘点
                    </span>
                    <span v-else style="color:#99CC66">
                      盘点完成
                    </span>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='120 + "px"' fixed="right" :key="Math.random()">        
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='inventoryVerificationList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                 <template slot-scope='scope'>
                 <center>
                    <el-button type="text"    @click="onViewInventoryVerification(scope.row,scope.$index)">查看</el-button>
                  <el-button type="text" v-if="scope.row.status=='0'"  @click="onDeleteInventoryVerification(scope.row,scope.$index)">删除</el-button>
                 </center>
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
            :page-sizes='[20, 50, 100, inventoryVerificationTotal]'
            :page-size='10'
            layout='total, sizes, prev, pager, next, jumper'
            :total='inventoryVerificationTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->     </div>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listInventoryVerificationPage, getInventoryVerificationById, deleteInventoryVerification,saveInventoryVerification } from '@/api/stock/inventoryVerification'
import {getInventoryVerificationDetailByInventoryId,listInventoryVerificationDetailPage} from '@/api/stock/inventoryVerificationDetail'
import { listResourcePermission } from '@/api/admin/common/permission'
import InventoryVerificationForm from './inventoryVerificationForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
export default {
  extends: MainUI,
  components: { 
    InventoryVerificationForm,
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
      },
      queryModel: {
        code:'',  //盘点单号
        createDate:'', // 创建时间
      },
      search: {
        params: [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}],    
        offset: 0,
        limit: 20,
		columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      currentPage: 1,
      inventoryVerificationTotal: 0,
      inventoryVerificationList: [],
        
      
      oprColumnWidth: 140,  // 操作列宽
      tableId: '1235640942656921602',
      schemeId: '1235640942656921612',
      check:false,   //新增条件
    }
  },
  methods: {
      //搜索重置
      reset(){
        this.queryModel={
          code:"",
          createDate:""
        }
        this.getInventoryVerificationList()
      },

     indexMethod(index){
       return (this.currentPage-1)*this.search.limit+index +1;
    },
    getInventoryVerificationList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        if(this.queryModel.code!=''&&this.queryModel!=undefined){
          this.search.params.push(
            {columnName: 'code', queryType: '=', value: this.queryModel.code}
          )
        }
        if (this.queryModel.createDate && this.queryModel.createDate.length) {
                  this.search.params.push({
                      logic: "AND",
                      queryType: "("
                    },{
                      columnName: "create_date",
                      logic: "",
                      queryType: 'between',
                      value: this.queryModel.createDate,
                    },{
                      logic: "",
                      queryType: ")"
                    })
                }
      }
      // 数据权限: 库存盘点inventory_verification
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listInventoryVerificationPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.inventoryVerificationTotal = responseData.data.total
          this.inventoryVerificationList = responseData.data.rows
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
        this.getInventoryVerificationList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getInventoryVerificationList()
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
      this.getInventoryVerificationList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getInventoryVerificationList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
        // 数据权限: 库存盘点inventory_verification
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listInventoryVerificationRespData, listPermissionRespData] = await Promise.all([
          listInventoryVerificationPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listInventoryVerificationRespData.code == 100 && listPermissionRespData.code == 100) {
          this.inventoryVerificationTotal = listInventoryVerificationRespData.data.total
          this.inventoryVerificationList = listInventoryVerificationRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'inventoryVerification:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'inventoryVerification:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'inventoryVerification:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'inventoryVerification:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'inventoryVerification:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listInventoryVerificationRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error) 
      }
    },
    onViewInventoryVerification(row, index) {
      this.$refs.inventoryVerificationForm.$emit('openViewInventoryVerificationDialog', row)
      // this.setLoad()
      //  this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id},{columnName: 'inventory_verification_id', queryType: '=', value: row.id}]
      // listInventoryVerificationDetailPage(search).then(responseData => {
      //   if(responseData.code == 100) {
      //     this.$refs.inventoryVerificationForm.$emit('openViewInventoryVerificationDialog', responseData.data)
      //   } else {
      //     this.showMessage(responseData)
      //   }
      //   this.resetLoad()
      // }).catch(error => {
      //   this.outputError(error)
      // })
    },
    onCreateInventoryVerification(sign) {
      //this.$refs.inventoryVerificationForm.$emit('openAddInventoryVerificationDialog')
      //判断所有项目是否都已经完成盘点
      let flage=0;
      if(this.inventoryVerificationList){
        this.inventoryVerificationList.forEach(item=>{
        if(item.status=='0'){
          
          flage=1
        }
      })
      }
      if(flage==1){
        this.$message.warning("当前存在盘点任务，请完成上次盘点！")
        return
      }
      let type='0'
      if(this.check){
        type='1'
      }else{
        type='0'
      }
      let variety=sign
      //执行新增盘点
      saveInventoryVerification(type,variety).then(res=>{
        if(res.code=='100'){
          this.$message.success("新增盘点成功")
          this.check=false
            this.getInventoryVerificationList()
        }else{
          this.$message.warning(res.msg)
        }
      }).catch()
    },
    onEditInventoryVerification(index, row) {
      this.setLoad()
      getInventoryVerificationById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.inventoryVerificationForm.$emit('openEditInventoryVerificationDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyInventoryVerification(index, row) {
      this.setLoad()
      getInventoryVerificationById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.inventoryVerificationForm.$emit('openCopyInventoryVerificationDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteInventoryVerification(row, index) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteInventoryVerification(row).then(responseData => {
          if(responseData.code == 100) {
            this.getInventoryVerificationList()
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

      this.getInventoryVerificationList()
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
<style lang="scss" >
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