<template>
  <div class="review-container">
    <!-- 编辑窗口  -->
    <review-form ref='reviewForm' :permission='permission' @save-finished='getReviewList()'
                 @save-and-continue="onSaveAndContinue"></review-form>
    <!--  搜索栏  开始 -->
    <div class='query-form-container'>
      <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
        <el-row v-if='!moreCodition' class='search-row'>
          <el-col :span="12">
            <el-form-item label='开单时间' prop='createDate'>
              <data-range-picker v-model='queryModel.createDate' ></data-range-picker>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label='开单诊所' prop='company'>
              <el-select v-model='queryModel.company' @change="onChangeCompany" value-key='value' filterable clearable placeholder='请选择操作'>
                <el-option v-for='company in company_List' :key='company.value' :label='company.name' :value='company'></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label='开单医生' prop='doctor'>
              <el-select v-model='queryModel.doctor' value-key='value' filterable clearable placeholder='请选择操作'>
                <el-option v-for='doctor in doctor_List' :key='doctor.value' :label='doctor.name' :value='doctor'></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row >
        <el-row class='search-row'>
          <el-col :span="6">
            <el-form-item label='处方类型' prop='recipelType'>
              <el-select v-model='queryModel.recipelType' value-key='value' filterable clearable placeholder='请选择操作'>
                <el-option v-for='recipelType in recipelType_List' :key='recipelType.value' :label='recipelType.name' :value='recipelType'></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label='处方状态' prop='recipelStatus'>
              <el-select v-model='queryModel.recipelStatus' value-key='value' filterable clearable placeholder='请选择操作'>
                  <el-option v-for='recipelStatus in recipelStatus_List' :key='recipelStatus.value' :label='recipelStatus.name' :value='recipelStatus'></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label='审查状态' prop='reviewStatus'>
              <el-select v-model='queryModel.reviewStatus' value-key='value' filterable clearable placeholder='请选择操作'>
                <el-option v-for='reviewStatus in reviewStatus_List' :key='reviewStatus.value' :label='reviewStatus.name' :value='reviewStatus'></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label='审查结果' prop='reviewResult'>
              <el-select v-model='queryModel.reviewResult' value-key='value' filterable clearable placeholder='请选择操作'>
                <el-option v-for='reviewResult in reviewResult_List' :key='reviewResult.value' :label='reviewResult.name' :value='reviewResult'></el-option>
              </el-select>
            </el-form-item>
          </el-col>

        </el-row>
        <el-row>
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
    </div>
    <!-- 工具栏 开始 -->
    <!--      <div class="page-container-header-end">
            <div>
              <el-button v-show='permission.add' type='primary' icon='el-icon-plus'  @click='onCreateTheme()'>添加</el-button>
            </div>
          </div>-->
    <!-- 工具栏 结束 -->

    <!--  搜索栏  结束 -->
    <!-- 表格栏  开始 -->
    <el-row>
      <el-col :span='24'>
        <div @mouseleave='moveTableOutside'>
          <el-table ref="reviewTable" class='drag_table' :data='reviewList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
            <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
            <el-table-column
              label='开单诊所'
              prop='recipelInfo.company.name'
              align="center">
            </el-table-column>
            <el-table-column
              label='开单科室'
              prop='recipelInfo.registration.clinicOffice.name'
              align="center">
            </el-table-column>
            <el-table-column
              label='开单医生'
              prop='recipelInfo.registration.doctor.name'
              align="center">
            </el-table-column>
            <el-table-column
              label='开单日期'
              prop='recipelInfo.createDate'
              align="center">
            </el-table-column>
            <el-table-column
              label='处方编号'
              prop='recipelInfo.code'
              align="center">
            </el-table-column>

            <el-table-column
              label='患者姓名'
              prop='recipelInfo.registration.patientId.name'
              align="center">
            </el-table-column>

            <el-table-column
              label='性别'
              prop='recipelInfo.registration.patientId.gender.name'
              align="center">
            </el-table-column>
            <el-table-column
              label='年龄'
              prop='recipelInfo.registration.patientId.age'
              align="center">
            </el-table-column>

            <el-table-column
              label='处方分类'
              prop='recipelInfo.recipelType.name'
              align="center">
            </el-table-column>
            <el-table-column
              label='审查状态'
              prop='review_status'
              align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.reviewStatus == 1" style="color:green">
                  已审查
                </span>
                <span v-else style="color:orange">
                  未审查
                </span>
              </template>
            </el-table-column>
            <el-table-column
              label='诊断信息'
              prop='recipelInfo.recipelType.name'
              width="220"
              align="center">
              <template slot-scope="scope">
                中药诊断：{{scope.row.medicalRecord.chinaDiagnose}};
                西药诊断：{{scope.row.medicalRecord.westernDiagnose}};
              </template>
            </el-table-column>
            <el-table-column
              label='处方状态'
              prop='recipelInfo.recipelType.name'
              align="center">
              <template slot-scope="scope">
                {{recipellnfoStatus(scope.row)}}
              </template>
            </el-table-column>
            <el-table-column
              label='审查结果'
              prop='reviewResult'
              align="center">
              <template slot-scope="scope">
               {{scope.row.reviewStatus==0?"":scope.row.reviewResult == 1?"合理":"不合理"}}
              </template>
            </el-table-column>
            <el-table-column
              label='审查内容'
              prop='reviewContent'
              align="center">
            </el-table-column>
            <el-table-column
              label='审查人'
              prop='reviewerName'
              align="center">
            </el-table-column>
            <el-table-column
              label='审查时间'
              prop='createDate'
              width="180"
              align="center">
            </el-table-column>
            <!--表行级操作按钮-->
            <el-table-column label='操作' header-align='center' :width='60 + "px"' fixed="right" :key="Math.random()">
              <!--<template slot='header' slot-scope="scope">
                <span>操作</span>
              </template>-->
              <template slot-scope='scope'>
                <OperationIcon type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                               @click='onEditReview(scope.$index, scope.row)'></OperationIcon>
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
          :page-sizes='[10, 20, 50, 100, reviewTotal]'
          :page-size='10'
          layout='total, sizes, prev, pager, next, jumper'
          :total='reviewTotal'>
        </el-pagination>
      </el-col>
    </el-row>
    <!-- 分页栏     结束 -->
  </div>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listRecipelInfoReviewPage, getRecipelInfoReviewById,getReviewFormByRecipelInfoId } from '@/api/outpatient/recipelInfoReview'
import { listResourcePermission } from '@/api/admin/common/permission'
import { getCompanyById } from '@/api/org/company'
import { listDictItemAll } from '@/api/sys/dictItem'
import { listUserAll } from "@/api/admin/user";
import { listCompanyAll } from "@/api/org/company";
import ReviewForm from './reviewForm'
import ExportExcelButton from '@/components/ExportExcelButton'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import DataRangePicker from '@/components/DataRangePicker'
export default {
  name: "review",
  extends: MainUI,
  components: {
    ReviewForm,
    ExportExcelButton,
    QueryForm,
    OperationIcon,
    DataRangePicker,
  },
  computed: {
    Company() {
      let company = JSON.parse(sessionStorage.getItem("currentUser")).company;
      return {
        id: company.id,
        label: company.label,
        name: company.name,
      };
    },
    Department() {
      let department = JSON.parse(sessionStorage.getItem("currentUser")).department;
      return {
        id: department.id,
        label: department.label,
        name: department.name,
      };
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
      queryTypes: {
        'name': 'like',
      },
      queryModel: {
        // 开单时间
        'createDate': null,
        // 开单诊所
        'company': '',
        // 开单医生
        'doctor': '',
        // 处方类型
        'recipelType': '',
        // 处方状态
        'recipelStatus': '',
        // 审查状态
        'reviewStatus': '',
        //审查结果
        'reviewResult': '',
      },
      search: {
        params: [],
        offset: 0,
        limit: 10,
        columnName: '',       // 排序字段名
        order: ''             // 排序
      },
      currentPage: 1,
      reviewTotal: 0,
      reviewList: [],

      company_List: [],
      doctor_List: [],
      recipelType_List: [],
      recipelStatus_List: [
        {name: "全部", value: ""},
        {name: "待收费", value: "recipelInfo.charge_status&0&recipelInfo.status&1"},
        {name: "已收费", value: "recipelInfo.charge_status&1&recipelInfo.dispension_status&0"},
        {name: "已退费", value: "recipelInfo.charge_status&-1"},
        // {name: "待发药", value: "recipelInfo.dispension_status&0&recipelInfo.charge_status&1"},
        {name: "已发药", value: "recipelInfo.dispension_status&1"},
        {name: "已退药未退费", value: "recipelInfo.dispension_status&-1&recipelInfo.charge_status&1"},
        {name: "已作废", value: "recipelInfo.status&-1"},
      ],
      reviewStatus_List: [
        {name: "全部", value: ""},
        {name: "未审核", value: "0"},
        {name: "已审核", value: "1"},
      ],
      reviewResult_List: [
        {name: "全部", value: ""},
        {name: "不合理", value: "0"},
        {name: "合理", value: "1"},
      ],
      index: null,
      loginCompany:{},
    };
  },
  props: {
  },
  methods: {
    getReviewList() {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = []
      this.search.params.push({
        columnName: 'recipelInfo.create_date',
        queryType: (this.queryModel.createDate && this.queryModel.createDate.length === 2) ? 'between' : ">=",
        value: (this.queryModel.createDate && this.queryModel.createDate.length === 2) ? this.queryModel.createDate : '2023-01-01 00:00:00'
      })
      this.search.params.push({
          columnName: 'recipelInfo.company_id',
          queryType: '=',
          value: this.queryModel.company.value
        })
      this.search.params.push({
          columnName: 'registration.doctor_id',
          queryType: '=',
          value: this.queryModel.doctor.value
        })
      this.search.params.push({
          columnName: 'recipelInfo.recipel_type',
          queryType: '=',
          value: this.queryModel.recipelType.value
        })
      if (this.queryModel.recipelStatus && this.queryModel.recipelStatus !== "") {
        this.search.params.push(...this.createQueryObjects(this.queryModel.recipelStatus.value))
      }
      this.search.params.push({
          columnName: 'review_status',
          queryType: this.queryModel.reviewStatus.value === '0' ? 'is null' : '=',
          value: this.queryModel.reviewStatus.value === '1' ? '1' : null
        })
      this.search.params.push({
          columnName: 'review_result',
          queryType: '=',
          value: this.queryModel.reviewResult.value
        })
      this.search.params.push({
        columnName: 'company.parent_id',
        queryType: '=',
        value: this.loginCompany.parent.id
      })
      // 数据权限
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listRecipelInfoReviewPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          this.reviewTotal = responseData.data.total
          this.reviewList = responseData.data.rows
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    //将recipelStatus处方状态查询条件组装成后台需要的数据结构
    createQueryObjects(inputString){
      let parts = inputString.split('&');
      let queryObjects = [];

      for (let i = 0; i < parts.length; i += 2) {
        const columnName = parts[i];
        const value = parts[i + 1];

        const queryObject = {
          columnName: columnName,
          queryType: '=',
          value: value,
        };
        queryObjects.push(queryObject);
      }
      return queryObjects;
    },
    onSearch() {
      console.log(JSON.stringify(this.permission.edit))
      this.$refs['queryForm'].validate(valid => {
        if (valid) {
          this.search.offset = 0
          this.currentPage = 1
          this.getReviewList()
        } else {
          return false
        }
      })

    },
    onSizeChange(val) {
      this.currentPage = 1
      this.search.limit = val;
      this.search.offset = (this.currentPage - 1) * val
      this.getReviewList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getReviewList()
    },
    async pageInit() {
      this.setLoad()
      try {
        await this.initOptions(this.queryModel)
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
        // 数据权限: 处方审查
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listReviewRespData, listPermissionRespData] = await Promise.all([
          listRecipelInfoReviewPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listReviewRespData.code == 100 && listPermissionRespData.code == 100) {
          this.reviewTotal = listReviewRespData.data.total
          this.reviewList = listReviewRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'recipelInfoReview:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'recipelInfoReview:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'recipelInfoReview:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'recipelInfoReview:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'recipelInfoReview:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listReviewRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onViewTheme(index, row) {
      this.setLoad()
      getRecipelInfoReviewById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.reviewForm.$emit('openViewThemeDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onSaveAndContinue(){
      console.log("保存并继续")
      this.index++
      if (this.index < this.reviewList.length) {
        let row = this.reviewList[this.index];
        getReviewFormByRecipelInfoId(row.recipelInfo.id).then(responseData => {
          if(responseData.code == 100) {
            this.$refs.reviewForm.updateData(responseData.data);
          }else{
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      } else {
        this.$refs.reviewForm.onDialogClose();
      }
    },
   /* onCreateTheme() {
      this.$refs.reviewForm.$emit('openAddThemeDialog')
    },*/
    onEditReview(index, row) {
      this.setLoad()
      getReviewFormByRecipelInfoId(row.recipelInfo.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.reviewForm.$emit('openEditReviewDialog',responseData.data)
          this.index = index
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    async initOptions(This) {
      let recipelType_search = {
        params: [{'columnName': 'dict_type_id', 'queryType': '=', 'value': '1014474470772899974'}]
      }
      // 字段对应表上filter条件
      recipelType_search.params.push.apply(recipelType_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(recipelType_search.params, this.$route.meta.routerId, '1014474470772899974')
      this.recipelType_List = []
      listDictItemAll(recipelType_search).then(responseData => {
        this.recipelType_List = responseData.data
        this.recipelType_List.unshift({name: "全部", value: ""})
      })
      //获取机构公司
      let [companyRespData] = await Promise.all([getCompanyById(this.Company.id)])
      this.loginCompany = companyRespData.data
      //初始化开单医生
      this.initDoctor();
      //初始化开单诊所
      let company_search = {
        params: [
          {
            columnName: "status",
            queryType: "=",
            value: 1,
          },
          {
            columnName: "is_institution",
            queryType: "=",
            value: 0,
          },
          {
            columnName: "parent_id",
            queryType: "=",
            value: this.loginCompany.parent.id,
          },
        ]}
      // 字段对应表上filter条件
      company_search.params.push.apply(company_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(company_search.params, this.$route.meta.routerId, '1014474470772899974')
      this.company_List = []
      listCompanyAll(company_search).then(responseData => {
        responseData.data.forEach(item => {
          this.company_List.push({name: item.name, value: item.id})
        })
        this.company_List.unshift({name: "全部", id: ""})
      })
    },
    onChangeCompany(event){
      let param = {
        columnName: "company.id",
        queryType: "=",
        value: event.value,
      };
      this.initDoctor(param);
    },
    initDoctor(param){
      let doctor_search = {
        params: [
          {
            columnName: "department_id",
            queryType: "=",
            value: this.Department.id,
          },
          {
            columnName: "is_locked",
            queryType: "=",
            value: 0,
          },
          {
            columnName: "company.parent_id",
            queryType: "=",
            value: this.loginCompany.parent.id,
          },
        ]}
      if (param) {
        doctor_search.params.push(param);
      }
      // 字段对应表上filter条件
      doctor_search.params.push.apply(doctor_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(doctor_search.params, this.$route.meta.routerId, '1014474470772899974')
      this.doctor_List = []
      listUserAll(doctor_search).then(responseData => {
        responseData.data.forEach(item => {
          this.doctor_List.push({name: item.name, value: item.id})
        })
        this.doctor_List.unshift({name: "全部", value: ""})
      })
    },
    recipellnfoStatus(row){
      if (row.recipelInfo.status == -1) {
        return "作废处方";
      }
      if (row.recipelInfo.chargeStatus == -1) {
        return "已退费";
      }
      if (row.recipelInfo.dispensionStatus == 1) {
        return "已发药";
      }else if (row.recipelInfo.dispensionStatus == -1) {
        if (row.recipelInfo.chargeStatus == 1) {
          return "已退药未退费";
        }
      }else if (row.recipelInfo.dispensionStatus == 0) {
        if (row.recipelInfo.chargeStatus == 0) {
          return "待收费";
        }else if (row.recipelInfo.chargeStatus == 1) {
          return "已收费";
        }
      }
    },
  },
  watch: {
  },
  mounted() {
    this.pageInit()

  }
}
</script>
<style lang="scss" scoped>
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
<style>
  .review-container{
    margin-top: 8px;
  }
</style>
