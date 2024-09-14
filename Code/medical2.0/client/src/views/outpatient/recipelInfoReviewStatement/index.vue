<template>
  <el-row v-loading='loading'>
    <div class="page-container">
      <!--  搜索栏  开始 -->
      <div class='query-form-container'>
        <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
          <el-row class='search-row'>
            <el-col :span="12">
              <el-form-item label='开单时间' prop='createDate'>
                <data-range-picker v-model='queryModel.createDate' ></data-range-picker>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label='统计方法' prop='statistical'>
                <el-select v-model='queryModel.statistical' value-key='value' filterable clearable placeholder='请选择操作'>
                  <el-option v-for='statistical in statistical_List' :key='statistical.value' :label='statistical.name' :value='statistical'></el-option>
                </el-select>
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
              </el-button-group>
            </el-col>
          </el-row>
        </el-form>
        <el-form>
          <el-card class="box-card" shadow="always">
          <el-row>
            <el-col :span="24/4">
              <el-form-item label='处方总数：' prop='statistical'>
                {{prescriptionStatistics.total}}
              </el-form-item>
            </el-col>
            <el-col :span="24/4">
              <el-form-item label='已发药数：' prop='statistical'>
                {{prescriptionStatistics.dispensedMedicine}}
              </el-form-item>
            </el-col>
            <el-col :span="24/4">
              <el-form-item label='未发药数：' prop='statistical'>
                {{prescriptionStatistics.undispensedMedicine}}
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24/4">
              <el-form-item label='已点评数：' prop='statistical'>
                {{prescriptionStatistics.alreadyReviewed}}
              </el-form-item>
            </el-col>
            <el-col :span="24/4">
              <el-form-item label='合格数：' prop='statistical'>
                {{prescriptionStatistics.good}}
              </el-form-item>
            </el-col>
            <el-col :span="24/4">
              <el-form-item label='不合格数：' prop='statistical'>
                {{prescriptionStatistics.bad}}
              </el-form-item>
            </el-col>
            <el-col :span="24/4">
              <el-form-item label='合格率：' prop='statistical'>
                {{prescriptionStatistics.rate}}%
              </el-form-item>
            </el-col>
          </el-row>
          </el-card>
        </el-form>
        <!-- 工具栏 开始 -->
        <div class="page-container-header-end">
          <div class="toolbar" >
<!--            <el-button v-show='permission.export' type='primary' icon='el-icon-upload2'  @click='onExport()'>添加</el-button>-->
            <el-button type='primary' icon='el-icon-upload2'  @click='onExport()'>导出</el-button>
          </div>
        </div>
        <!-- 工具栏 结束 -->
        <!--  搜索栏  结束 -->
        <!-- 表格栏  开始 -->
        <el-row>
          <el-col :span='24'>
            <div @mouseleave='moveTableOutside'>
              <el-table ref="statementTable" class='drag_table' :data='statementList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
                <el-table-column
                  label="序号"
                  type="index"
                  :index="indexMethod"
                  align="center">
                </el-table-column>
                <el-table-column
                  label='开单诊所'
                  prop='company'
                  align="center">
                </el-table-column>
                <el-table-column
                  v-if="clinicOfficeShow"
                  label='开单科室'
                  prop='clinicOffice'
                  align="center">
                </el-table-column>
                <el-table-column
                  v-if="doctorShow"
                  label='开单医生'
                  prop='doctor'
                  align="center">
                </el-table-column>
                <el-table-column
                  label='点评处方'
                  prop='comment'
                  align="center">
                </el-table-column>
                <el-table-column
                  label='合格处方'
                  prop='rational'
                  align="center">
                </el-table-column>
                <el-table-column
                  label='不合格处方'
                  prop='belowStandard'
                  align="center">
                  <template slot-scope="scope">
                    {{scope.row.comment - scope.row.rational}}
                  </template>
                </el-table-column>
                <el-table-column
                  label='处方合格率'
                  prop='rate'
                  align="center">
                  <template slot-scope="scope">
                    {{scope.row.rate}}%
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-col>
        </el-row>
        <!-- 表格栏  结束 -->
      </div>
    </div>
  </el-row>
</template>

<script>
  import { getCompanyById } from '@/api/org/company'
  import { listPageStatement,getPrescriptionStatistics } from '@/api/outpatient/recipelInfoReview'
  import { listResourcePermission } from '@/api/admin/common/permission'
  import DataRangePicker from '@/components/DataRangePicker'
  import MainUI from '@/views/components/mainUI'
export default {
  extends: MainUI,
  components: {
    DataRangePicker,
  },
  computed: {
    clinicOfficeShow(){
      return this.queryModel.statistical.value === "clinicOffice" || this.queryModel.statistical.value === "doctor" ? true : false
    },
    doctorShow(){
      return this.queryModel.statistical.value === "doctor" ? true : false
    },

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
      search: {
        params: [],
        offset: 0,
        limit: 100,
        columnName: '',       // 排序字段名
        order: ''             // 排序
      },
      currentPage: 1,
      statementTotal: 0,
      statementList: [],

      queryModel: {
        // 开单时间
        'createDate': this.latestMarch(),
        // 统计方法
        'statistical': {name: "按医生汇总", value: "doctor"},
      },

      statistical_List: [
        {name: "按诊所汇总", value: "company"},
        {name: "按科室汇总", value: "clinicOffice"},
        {name: "按医生汇总", value: "doctor"},
      ],
      loginCompany:{}, // 当前登录公司
      prescriptionStatistics:{}, //处方统计
    }
  },
  methods: {
    getStatementList(){
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = []
      this.search.params.push({
        columnName: 'recipelInfo.create_date',
        queryType: (this.queryModel.createDate && this.queryModel.createDate.length === 2) ? 'between' : ">=",
        value: (this.queryModel.createDate && this.queryModel.createDate.length === 2) ? this.queryModel.createDate : '2023-01-01 00:00:00'
      })
      this.search.params.push({
        columnName: 'company.parent_id',
        queryType: '=',
        value: this.loginCompany.parent.id
      })
      this.search.params.push({
        columnName: 'groupBy',
        queryType: '=',
        value: this.getGroupBy(this.queryModel.statistical)
      })

      // 数据权限
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listPageStatement(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.statementTotal = responseData.data.total
          this.statementList = responseData.data.rows
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
      this.initPrescriptionStatistics()
    },
    getGroupBy(statistical){
      let groupBy = "recipelInfo.company_id,company.name,registration.department_id,clinicOffice.name,registration.doctor_id,doctor.name";
      if (statistical.value === "doctor") {
        groupBy = "recipelInfo.company_id,company.name,registration.department_id,clinicOffice.name,registration.doctor_id,doctor.name";
      }
      if (statistical.value === "company") {
        groupBy = "recipelInfo.company_id,company.name";
      }
      if (statistical.value === "clinicOffice") {
        groupBy = "recipelInfo.company_id,company.name,registration.department_id,clinicOffice.name";
      }
      return groupBy
    },
    onSearch() {
      this.$refs['queryForm'].validate(valid => {
        if (valid) {
          this.search.offset = 0
          this.currentPage = 1
          this.getStatementList()
        } else {
          return false
        }
      })
    },
    async initOptions(This){
      //获取机构公司
      let [companyRespData] = await Promise.all([getCompanyById(this.currentUser.company.id)])
      this.loginCompany = companyRespData.data
    },
    async pageInit() {
      this.setLoad()
      try {
        await this.initOptions(this.queryModel)
        this.initPrescriptionStatistics()
        this.search.params = []
        this.search.params.push({
          columnName: 'recipelInfo.create_date',
          queryType: (this.queryModel.createDate && this.queryModel.createDate.length === 2) ? 'between' : ">=",
          value: (this.queryModel.createDate && this.queryModel.createDate.length === 2) ? this.queryModel.createDate : '2023-01-01 00:00:00'
        })
        this.search.params.push({
          columnName: 'company.parent_id',
          queryType: '=',
          value: this.loginCompany.parent.id
        })
        this.search.params.push({
          columnName: 'groupBy',
          queryType: '=',
          value: this.getGroupBy(this.queryModel.statistical)
        })
        // 数据权限: 处方审查
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listStatementRespData, listPermissionRespData] = await Promise.all([
          listPageStatement(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listStatementRespData.code == 100 && listPermissionRespData.code == 100) {
          this.statementTotal = listStatementRespData.data.total
          this.statementList = listStatementRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'review:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'review:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'review:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'review:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'review:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listStatementRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    initPrescriptionStatistics(){
      let param = {
        id:this.loginCompany.parent.id,
        startTime: this.queryModel.createDate ? this.queryModel.createDate[0] : '',
        endTime: this.queryModel.createDate ? this.queryModel.createDate[1] : '',
      }
      getPrescriptionStatistics(param).then(responseData => {
        if(responseData.code == 100) {
          this.prescriptionStatistics = responseData.data
        } else {
          this.showMessage(responseData)
        }
      }).catch(error => {
        this.outputError(error)
      })
    },
    formatDate(date){
      let year = date.getFullYear();
      let month = String(date.getMonth() + 1).padStart(2, '0');
      let day = String(date.getDate()).padStart(2, '0');
      let hours = String(date.getHours()).padStart(2, '0');
      let minutes = String(date.getMinutes()).padStart(2, '0');
      let seconds = String(date.getSeconds()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    onExport(){
      console.log("导出")
    },
    latestMarch(){
      let end = new Date();
      let start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
      return [this.formatDate(start),this.formatDate(end)]
    },
  },
  watch: {
  },
  mounted() {
    this.pageInit()
  }
}
</script>
<style>
  .toolbar{
    margin-top: 8px;
  }
</style>
