<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <patient-form ref='patientForm' @save-finished='getPatientList()'></patient-form>
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
                  type="primary"
                  icon="el-icon-plus"
                  @click="onCreatePatient()"
                >添加</el-button>
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
            <el-table ref="patientTableRef" class="patient-table" :data='patientList' border height="calc(100vh - 350px)" @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
              <el-table-column
                label="序号"
                width="70px"
                type="index"
                :index="indexMethod"
                align="center">
              </el-table-column>
              <el-table-column v-for="(cv, index) in columnViews" v-if="cv.display"  :prop='cv.prop' :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align'   header-align='center' :column-key='index.toString()'>
                <template slot-scope='{row,$index}'>
                  <span>{{ getAttrValue(row, cv.prop, cv.javaType )}}</span>
                </template>
              </el-table-column>
              <el-table-column  label='操作'  header-align='center' key="operationColumn" width='160' >
                <template slot-scope='scope'>
                  <OperationIcon type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                                 @click='onViewPatient(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                                 @click='onEditPatient(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon type='primary' content='复制' placement='top-start' icon-name='el-icon-document'
                                 @click='onCopyPatient(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon type='info' content='历史记录' placement='top-start' icon-name='el-icon-info'
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
      <!-- 分页栏     结束 -->
    </el-card>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listPatientPage, getPatientById, deletePatient } from '@/api/outpatient/patient'
import PatientForm from '@/views/outpatient/patient/patientForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'

export default {
  name: 'PatientManage',
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
      queryTypes: {
        'name': 'like',
        'phone': 'like',
        'card': '=',
      },
      queryModel: {
        'name': '',
        'phone': '',
        'card': '',
      },
      search: {
        params: [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}],
        offset: 0,
        limit: 20,
        columnName: '',
        order: ''
      },
      currentPage: 1,
      patientTotal: 0,
      patientList: [],
      loading: false,
      moreCodition: false,
      moreParm: {},
      oprColumnWidth: 140,
      tableId: '1008489176147648530',
      schemeId: '1008489176147648553',
      columnViews: [],
      curentRow: {}
    }
  },
  methods: {
    reset(){
      this.$refs.queryForm.resetFields()
      this.onSearch()
    },
    getPatientList() {
      this.setLoad()
      this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
      if(this.moreCodition) {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        this.search.params.push({
          columnName: 'name',
          queryType: 'like',
          value: this.queryModel.name
        })
        this.search.params.push({
          columnName: 'phone',
          queryType: 'like',
          value: this.queryModel.phone
        })
        this.search.params.push({
          columnName: 'card',
          queryType: '=',
          value: this.queryModel.card
        })
      }
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
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)

        let listPatientRespData = await listPatientPage(this.search)

        if(listPatientRespData.code == 100) {
          this.patientTotal = listPatientRespData.data.total
          this.patientList = listPatientRespData.data.rows
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
        this.resetLoad()
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
    onSortChange(orderby) {
      if(validatenull(orderby.prop)) {
        this.search.columnName = ''
        this.search.order = ''
      } else {
        this.search.columnName = orderby.prop
        this.search.order = orderby.order === 'descending' ? 'desc' : 'asc'
      }
      this.getPatientList()
    },
    initOptions(This) {},
    setLoad() {
      this.loading = true
    },
    resetLoad() {
      this.loading = false
    },
    moveTableOutside() {},
    onChangeWidth() {},
    cellClassName() {},
    headerCellClassName() {},

    onMoreCodition() {
      this.moreCodition = !this.moreCodition
    },
    onShowHistory(index, row) {
      this.curentRow = row
    }
  },
  watch: {
    patientList: {
      handler () {
        this.$nextTick(() => {
          if(this.$refs.patientTableRef){
            this.$refs.patientTableRef.doLayout()
          }
        })
      },
      deep: true
    }
  },
  updated(){
    this.$nextTick(() => {
      if(this.$refs.patientTableRef){
        this.$refs.patientTableRef.doLayout()
      }
    })
  },
  mounted() {
    this.pageInit()
  }
}
</script>

<style scoped>
/* 穿透作用域，作用于固定列 */
::v-deep(.el-table__fixed-right .cell) {
  display: flex !important;
  align-items: center !important;
  justify-content: flex-start !important; /* 根据需求改为 center */
  gap: 5px; /* 按钮间距 */
  padding: 0 5px; /* 内边距 */
  height: 100% !important;
}

/* 修复固定列表头对齐 */
::v-deep(.el-table__fixed-right .el-table__header-wrapper th) {
  height: 48px !important;
  line-height: 48px !important;
  vertical-align: middle !important;
}

/* 修复表格行高度 */
::v-deep(.el-table__fixed-right .el-table__body-wrapper .el-table__row) {
  height: 48px !important;
}

/* 表格内容居中 */
::v-deep(.el-table__cell) {
  vertical-align: middle !important;
}
</style>
