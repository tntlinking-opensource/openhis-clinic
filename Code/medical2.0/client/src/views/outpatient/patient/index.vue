<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <patient-form ref='patientForm' :permission='permission' v-on:save-finished='getPatientList()'></patient-form>
    <el-card class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
            <el-col :span="6">
              <el-form-item label='患者姓名' prop='name'>
                <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入患者姓名'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='联系方式' prop='phone'>
                <el-input v-model='queryModel.phone' :clearable='true' placeholder='请输入联系方式'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='身份证号' prop='card'>
                <el-input v-model='queryModel.card' :clearable='true' placeholder='请输入身份证号'></el-input>
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
            <el-col :span="3" style="text-align:right;padding-right:5px">
              <el-button-group>
                  <el-button
                    v-show="permission.add"
                    type="primary"
                    icon="el-icon-plus"
                    @click="onCreatePatient()"
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
            <el-table ref="patientTableRef"  :data='patientList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
              <el-table-column
              label="序号"
              width="70px"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
              <el-table-column v-for="(cv, index) in columnViews" v-if="cv.display"  :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align'   header-align='center' :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
<!--                  <span v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                    <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check' style='color:#F56C6C;'></li>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>-->
                  <span>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType )}}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column  label='操作' fixed="right" header-align='center' :key="Math.random()" :width='140' >
<!--                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='patientList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>-->
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                    @click='onViewPatient(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                    @click='onEditPatient(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document'
                    @click='onCopyPatient(scope.$index, scope.row)'></OperationIcon>
                  <!-- <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start' icon-name='el-icon-delete'
                    @click='onDeletePatient(scope.$index, scope.row)'></OperationIcon> -->
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
            :page-sizes='[ 20, 50, 100, patientTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='patientTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->     </el-card>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listPatientPage, getPatientById, deletePatient } from '@/api/outpatient/patient'
import { listResourcePermission } from '@/api/admin/common/permission'
import PatientForm from './patientForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
export default {
  extends: MainUI,
  components: {
    PatientForm,
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
        'phone': 'like',
        'card': '=',
      },
      queryModel: {
        'name': '',   // 患者姓名
        'phone': '',   // 联系方式
        'card': '',   // 身份证号
      },
      search: {
        params: [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}],
        offset: 0,
        limit: 20,
		columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      currentPage: 1,
      patientTotal: 0,
      patientList: [],


      oprColumnWidth: 140,  // 操作列宽
      tableId: '1008489176147648530',
      schemeId: '1008489176147648553'
    }
  },
  methods: {
    reset(){
      this.$refs.queryForm.resetFields()
      this.onSearch()
    },
    getPatientList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 患者姓名
        this.search.params.push({
      	  columnName: 'name',
      	  queryType: 'like',
          value: this.queryModel.name
        })
        // 查询参数: 联系方式
        this.search.params.push({
      	  columnName: 'phone',
      	  queryType: 'like',
          value: this.queryModel.phone
        })
        // 查询参数: 身份证号
        this.search.params.push({
      	  columnName: 'card',
      	  queryType: '=',
          value: this.queryModel.card
        })
      }
      // 数据权限: 患者表patient
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listPatientPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.patientTotal = responseData.data.total
          this.patientList = responseData.data.rows
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    indexMethod(index){
       return (this.currentPage-1)*this.search.limit+index +1;
    },
    onSearch() {
      if(this.moreCodition) {
        this.search.offset = 0
        this.currentPage = 1
        this.getPatientList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getPatientList()
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
      this.getPatientList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getPatientList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
        // 数据权限: 患者表patient
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listPatientRespData, listPermissionRespData] = await Promise.all([
          listPatientPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listPatientRespData.code == 100 && listPermissionRespData.code == 100) {
          this.patientTotal = listPatientRespData.data.total
          this.patientList = listPatientRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'patient:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'patient:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'patient:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'patient:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'patient:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listPatientRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onViewPatient(index, row) {
      this.setLoad()
      getPatientById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.patientForm.$emit('openViewPatientDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreatePatient() {
      this.$refs.patientForm.$emit('openAddPatientDialog')
    },
    onEditPatient(index, row) {
      this.setLoad()
      getPatientById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.patientForm.$emit('openEditPatientDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyPatient(index, row) {
      this.setLoad()
      getPatientById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.patientForm.$emit('openCopyPatientDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeletePatient(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deletePatient(row).then(responseData => {
          if(responseData.code == 100) {
            this.getPatientList()
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

      this.getPatientList()
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
                  this.$refs.patientTableRef.doLayout()// 对 Table 进行重新布局
              })
          },
          deep: true
      }
  },
  updated(){
  //  if(this.$refs.patientTableRef){
      this.$nextTick(() => {
            // tableRef是el-table绑定的ref属性值
            this.$refs.patientTableRef.doLayout()// 对 Table 进行重新布局
        })
    //}
  },
  mounted() {
    this.pageInit()
  }
}
</script>
<style lang="scss" scoped>
  .page-container{
    padding: 0;
  };

  /deep/.el-table{
    .el-table__fixed-body-wrapper{
      top: 47px !important;
    }
  };

  /deep/ .el-table__fixed-right-patch{
    width:5px !important
  };

  /deep/ .el-table colgroup col[name='gutter']{
    width:5px !important
  };

  /deep/ .el-table__body{
    width:100% !important
  };

  .drag_table{
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
