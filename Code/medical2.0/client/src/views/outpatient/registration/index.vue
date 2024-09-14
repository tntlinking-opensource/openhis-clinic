<template>
  <el-row v-loading="loading">
    <!-- 历史记录  -->
    <History :bussObject="curentRow"></History>
    <!-- 编辑窗口  -->
    <registration-form
      ref="registrationForm"
      :permission="permission"
      v-on:save-finished="getRegistrationList"
    ></registration-form>
    <ys-form ref="ysForm" :permission="permission" @typeclick="rettonys"></ys-form>
    <beforehand-form ref="beforehandForm" :permission="permission" @typeclick="rettonys"></beforehand-form>
    <el-card class="page-container">
      <el-tabs v-model="activeName" @tab-click="handleClick"
               style="width: 100%; height: 100%">
        <el-tab-pane label="登记信息" name="djxx">
          <!--  搜索栏  开始 -->
          <div class="query-form-container" style="margin-top:20px;">
            <el-row v-if="!moreCodition" class="search-row">
              <el-form
                :model="queryModel"
                @submit.native.prevent
                label-position="left"
                label-width="70px"
                ref="queryForm"
                :inline-message="true"
              >
                <el-col :span="8">
                  <el-form-item label="登记时间" prop="createDate">
                    <el-date-picker
                      :picker-options="filerData.pickerOptions"
                      v-model="queryModel.createDate"
                      type="datetimerange"
                      range-separator="至"
                      start-placeholder="开始日期"
                      end-placeholder="结束日期"
                      :default-time="['00:00:00', '23:59:59']"
                    >
                    </el-date-picker>
                  </el-form-item>
                </el-col>
                <el-col :span="4" style="text-align:right">
                  <el-form-item label="科室" prop="department.id" style="padding-right:20px">
                    <el-cascader
                      style="width:180px"
                      v-model="queryModel.department.id"
                      :options="department_List"
                      :key="key_department"
                      :props="{
                    value: 'id',
                    label: 'name',
                    checkStrictly: true,
                    emitPath: false,
                  }"
                      filterable
                      clearable
                      placeholder="请选择科室"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="4" style="text-align:right">
                  <el-form-item label="医生" prop="doctor">
                    <el-select
                      v-model="queryModel.doctor"
                      value-key="id"
                      filterable
                      clearable
                      placeholder="请选择医生"
                    >
                      <el-option
                        v-for="doctor in doctor_List"
                        :key="doctor.id"
                        :label="doctor.name"
                        :value="doctor"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="4" style="text-align:right">
                  <el-form-item label="来源" prop="source">
                    <el-select
                      v-model="queryModel.source"
                      value-key="value"
                      filterable
                      clearable
                      placeholder="请选择来源"
                    >
                      <el-option
                        v-for="source in source_List"
                        :key="source.value"
                        :label="source.name"
                        :value="source.value"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="4" style="text-align:right">
                  <el-form-item label="患者" prop="patientId">
                    <el-select
                      v-model="queryModel.patientId"
                      value-key="id"
                      filterable
                      clearable
                      placeholder="请选择患者"
                    >
                      <el-option
                        v-for="patientId in patientId_List"
                        :key="patientId.id"
                        :label="patientId.name"
                        :value="patientId"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="4">
                  <el-form-item label="状态" prop="status">
                    <el-select
                      v-model="queryModel.status"
                      value-key="id"
                      filterable
                      clearable
                      placeholder="请选择登记状态"
                    >
                      <el-option
                        v-for="status in statusList"
                        :key="status.id"
                        :label="status.name"
                        :value="status"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="3" style="display:flex;justivy-content:space-around">
                  <el-button
                    type="primary"
                    icon="el-icon-search"
                    @click="onSearch()"
                    :plain="true"
                  >搜索
                  </el-button>
                  <el-button
                    type="info"
                    icon="el-icon-refresh-left"
                    @click="reset"
                    :plain="true"
                  >重置
                  </el-button>
                </el-col>
                <el-col :span="17" style="text-align:right;padding-right:5px">
                  <el-button-group>
                    <el-button
                      v-show="permission.add"
                      type="primary"
                      icon="el-icon-plus"
                      @click="onCreateRegistration()"
                    >新增登记
                    </el-button
                    >
                  </el-button-group>
                </el-col>
              </el-form>
            </el-row>
            <QueryForm
              v-else
              v-model="moreParm"
              :tableId="tableId"
              :schemeId="schemeId"
              :routerId="$route.meta.routerId"
              @search="onSearch()"
              @moreCodition="onMoreCodition()"
            ></QueryForm>
          </div>
          <!--  搜索栏  结束 -->

          <!-- 工具栏 开始 -->

          <!-- 工具栏 结束 -->

          <!-- 表格栏  开始 -->
          <el-row>
            <el-col :span="24">
              <div @mouseleave="moveTableOutside">
                <el-table
                  class="drag_table"
                  :data="registrationList"
                  border
                  height="calc(100vh - 200px)"
                  @sort-change="onSortChange"
                  @header-dragend="onChangeWidth"
                  :cell-class-name="cellClassName"
                  :header-cell-class-name="headerCellClassName"
                  highlight-current-row
                  ref="mutipleTables"
                >
                  <el-table-column
                    label="序号"
                    type="index"
                    :index="indexMethod"
                    align="center">
                  </el-table-column>
                  <el-table-column
                    v-for="(cv, index) in columnViews"
                    v-if="cv.display"
                    :prop="cv.prop"
                    :key="`columnViews_${index}`"
                    :label="cv.label"
                    sortable="custom"
                    :align="cv.align"
                    :min-width="cv.miniWidth + 'px'"
                    :width="cv.width + 'px'"
                    header-align="center"
                    :column-key="index.toString()"
                    :render-header="renderHeader"
                  >
                    <template slot-scope="{ row, $index }">
                  <span
                    v-if="
                      columnViews[index].showType == 'Switch' ||
                      columnViews[index].showType == 'Checkbox' ||
                      columnViews[index].showType == 'Radio'
                    "
                  >
                    <li
                      v-if="getAttrValue(row, columnViews[index].prop) == '1'"
                      class="el-icon-check"
                      style="color: #f56c6c"
                    ></li>
                  </span>
                      <div v-else-if="columnViews[index].label == '状态'">
                    <span v-if="getAttrValue(row, columnViews[index].prop) == '待就诊'"
                          style="color: #f56c6c">待就诊</span>
                        <span v-else>{{
                            getAttrValue(
                              row,
                              columnViews[index].prop,
                              columnViews[index].javaType
                            )
                          }}</span>
                      </div>
                      <span v-else>{{
                          getAttrValue(
                            row,
                            columnViews[index].prop,
                            columnViews[index].javaType
                          )
                        }}</span>
                    </template>
                  </el-table-column>
                  <!--表行级操作按钮-->
                  <el-table-column
                    label="操作"
                    header-align="center"
                    :width="ColumnWidth"
                    fixed="right"
                    :key="Math.random()"
                  >
                    <template slot="header" slot-scope="scope">
                      <span>操作</span>
                      <view-columns-select
                        v-model="columnViews"
                        v-on:save-column-view="saveColumn"
                        v-on:show-all-column="showAllColumn"
                        v-on:show-default-column="showDefaultColumn"
                      ></view-columns-select>
                      <export-excel-button
                        v-show="permission.export"
                        :data="registrationList"
                        :tHeader="getHeads()"
                        :filterVal="getFilterVal()"
                        :plain="true"
                      ></export-excel-button>
                    </template>
                    <template slot-scope="scope">
                      <!-- 已就诊 -->
                      <div style="display:flex;justify-content:space-around">
                        <p class="btn" v-if="scope.row.status.value === 'registrationStatus_3'|| scope.row.status.value === null">
                      <span @click="onEditRegistration(scope.$index, scope.row)"
                      >查看或修改</span>
                          <span
                            @click="updateStatusId(scope.row.id, scope.row.status.value,scope.row.doctor,scope.row.clinicOffice,scope.row.subscribeDate)"
                          >签到</span>
                        </p>
                        <p
                          class="btn1"
                          v-if="scope.row.status.value === 'registrationStatus_0' || scope.row.status.value === 'registrationStatus_5' || scope.row.status.name === ''"
                        >
                          <span v-show="permission.edit" @click="applyForStudentExcel(scope.$index, scope.row)">远程诊疗&nbsp;</span>
                          <span @click="onEditRegistration(scope.$index, scope.row)">修改</span>
                          <el-popconfirm title="确定退号吗？" @confirm="chargeClick(scope.row)">
                            <span slot="reference">退号</span>
                          </el-popconfirm>
                          <span class="btn1" @click="openRegistration(scope.row.id)">打印</span>

                        </p>
                        <p
                          class="btn2"
                          v-if="
                      scope.row.status.value === 'registrationStatus_1' ||
                      scope.row.status.value === 'registrationStatus_2' ||
                      scope.row.status.value === 'registrationStatus_4'
                    "
                          @click="onViewRegistration(scope.$index, scope.row)"
                        >
                          查看
                        </p>
                        <p
                          class="btn1"
                          v-if="
                      scope.row.registrationFee > 0 &&(scope.row.status.value === 'registrationStatus_1' ||
                      scope.row.status.value === 'registrationStatus_2')
                    "
                          @click="openRegistration(scope.row.id)"
                        >
                          打印
                        </p>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-col>
          </el-row>
          <!-- 表格栏  结束 -->
          <!-- 分页栏     开始 -->
          <el-row>
            <el-col :span="24">
              <el-pagination
                background
                @size-change="onSizeChange"
                @current-change="onCurrentChange"
                :current-page.sync="currentPage"
                :page-sizes="[20, 50, 100, registrationTotal]"
                :page-size="20"
                layout="total, sizes, prev, pager, next, jumper"
                :total="registrationTotal"
              >
              </el-pagination>
            </el-col>
          </el-row>
          <!-- 分页栏     结束 -->
          <!--退费弹框 开始-->
          <el-dialog
            title="退费信息"
            :visible.sync="refundRegistraionVisible"
            width="50%"
            :close-on-click-modal="false"
            :before-close="dialogBeforeClose"
          >
            <div>
              <el-divider></el-divider>
              <div id="refundTotalFee_Container">
                <div id="refundTotalFee_Title">应退金额（元）：</div>
                <div id="refundTotalFee_Fee">{{ refundTotalFee }}</div>
              </div>
              <div class="refund-registration-flex-start">
                <p class="refund-registration-p">支付方式</p>
                <el-radio-group v-model="refundReistrationPayType" class="refund-registration-content">
                  <el-radio
                    v-for="payType in payType_List"
                    :key="payType.value"
                    :label="payType.value"
                  >{{ payType.name }}
                  </el-radio
                  >
                </el-radio-group>
              </div>
              <div class="refund-registration-flex-start">
                <p class="refund-registration-p">备注</p>
                <el-input v-model="refundRegistrationRemarks" class="refund-registration-content"></el-input>
              </div>
              <el-divider></el-divider>
            </div>
            <div slot="footer">
              <el-button @click="refundRegistraionVisible = false">取 消</el-button>
              <el-button type="primary" @click="refundRegistrationPayClick"
              >确认退费
              </el-button
              >
            </div>
          </el-dialog>
          <!--退费弹框 结束-->

          <!-- 签到弹出框 -->
          <el-dialog
            title="添加科室和医生"
            :visible.sync="signDialogVisible"
            width="30%"
            :before-close="handleClose">
            <el-form
              :model="bizFormModel"
              :rules="formRules"
              ref="registrationForm"
              label-width="120px"
              label-position="right"
              style="marginTop: 10px"
              class="edit-form"
            >
              <el-row>
                <el-col :span="24 / 2">
                  <el-form-item label="科室" prop="clinicOffice.id">
                    <el-cascader
                      v-model="bizFormModel.clinicOffice.id"
                      @change="sortDoc"
                      :options="departments_List"
                      :key="keys_department"
                      :props="{
                    value: 'id',
                    label: 'name',
                    checkStrictly: true,
                    emitPath: false,
                  }"
                      filterable
                      clearable
                      placeholder="请选择科室"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="24 / 2">
                  <el-form-item label="医生" prop="doctor.name">
                    <el-select
                      v-model="bizFormModel.doctor"
                      value-key="id"
                      filterable
                      clearable
                      placeholder="请选择医生"
                      @clear="
                    bizFormModel.doctor = {
                      id: null,
                      name: null,
                    }
                  "
                    >
                      <el-option
                        v-for="doctor in this.selects_doctor"
                        :key="doctor.id"
                        :label="doctor.name"
                        :value="doctor"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
          <el-button @click="signDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="signIn">确 定</el-button>
        </span>
          </el-dialog>
          <!-- 远程诊疗申请 -->
          <el-dialog
            title="远程诊疗申请"
            :visible.sync="applyForDialogVisible"
            width="30%"
            :before-close="handleClose">
            <el-form
              :model="bizFormModel"
              :rules="formRules"
              ref="registrationForm"
              label-width="120px"
              label-position="right"
              style="marginTop: 10px"
              class="edit-form"
            >
              <el-row>
                <el-form-item label="医院名称" prop="hospital.OrganizeId">
                  <el-select
                    v-model="bizFormModel.hospital"
                    @change="getks()"
                    value-key="Name"
                    filterable
                    clearable
                    placeholder="请选择医院"
                    @clear="
                    bizFormModel.hospital = {
                      OrganizeId: null,
                      Name: null,
                    }
                  "
                  >
                    <el-option
                      v-for="hospital in this.selects_mechanism"
                      :key="hospital.OrganizeId"
                      :label="hospital.Name"
                      :value="hospital"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="患者姓名" prop="name">
                  <el-input
                    :disabled="true"
                    v-model="bizFormModel.patientId.name"
                  ></el-input>
                </el-form-item>
                <el-form-item label="性别" prop="gender.name">
                  <el-input
                    :disabled="true"
                    v-model="bizFormModel.patientId.gender.name"
                  ></el-input>
                </el-form-item>
                <el-form-item label="身份证号" prop="card">
                  <el-input
                    :disabled="true"
                    v-model="bizFormModel.card"
                  ></el-input>
                </el-form-item>
                <el-form-item label="诊疗科室" prop="clinicOffice">
                  <el-select
                    v-model="bizFormModel.clinicOffice"
                    @change="getys()"
                    value-key="ks"
                    filterable
                    clearable
                    placeholder="请选择科室"
                    @clear="
                    bizFormModel.clinicOffice = {
                      ks: null,
                      ksmc: null,
                    }
                  "
                  >
                    <el-option
                      v-for="clinicOffice in this.departments_List"
                      :key="clinicOffice.ks"
                      :label="clinicOffice.ksmc"
                      :value="clinicOffice"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="诊疗医生" prop="doctor">
                  <el-select
                    v-model="bizFormModel.doctor"
                    value-key="ysgh"
                    filterable
                    clearable
                    placeholder="请选择医生"
                    @clear="
                    bizFormModel.doctor = {
                      ysgh: null,
                      ysxm: null,
                    }
                  "
                  >
                    <el-option
                      v-for="doctor in this.doctors_List"
                      :key="doctor.ysgh"
                      :label="doctor.ysxm"
                      :value="doctor"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="诊疗日期:" style="margin-right: 3px;">
                  <el-date-picker
                    v-model="bizFormModel.diagnosisTime"
                    type="datetime"
                    placeholder="选择日期时间"
                    align="right"
                    :picker-options="pickerOptions">
                  </el-date-picker>
                </el-form-item>
              </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
          <el-button @click="applyForDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="savedRequest">确 定</el-button>
        </span>
          </el-dialog>
        </el-tab-pane>
        <el-tab-pane label="预约分诊" name="yyfz">
          <el-row style="margin-top:40px;">
            <el-col :span="7" v-for="(item,index) in registrationList" :key="index" :offset="1"
                    style="padding-top:20px;">
              <div>
                <el-card class="box-card" :body-style="{ padding:'0px'}" shadow="hover">
                  <div style="width:400px; height:160px; padding-left:100px; padding-top:20px;">
                    <span style="font-size: 16px; font-weight:540;">{{ item.patientId.name }}  </span>

                    <span
                      style="font-size: 14px; color: darkgray;">   {{
                        item.patientId.gender.name
                      }}   {{ item.patientId.age }}{{ "岁" }}</span>
                    <br/>
                    <br/>
                    <span
                      style="font-size: 14px; color: darkgray;">登记科室：{{
                        item.clinicOffice.name
                      }} {{ "(" + item.treatType.name + ")" }}</span>
                    <br/>
                    <span
                      style="font-size: 14px; color: darkgray;">登记时间：{{
                        $moment(item.createDate).format("HH:mm")
                      }}</span>

                  </div>

                  <div class="bottom clearfix">
                    <el-row style="padding-left:40px;">
                      <el-col :span="10">
                        <el-button type="text" style="font-size: 15px; color:darkgray;" @click="buttonyz(item)">预诊
                        </el-button>
                      </el-col>
                      <el-col :span="10">
                        <el-button type="text" style="font-size: 15px; color:darkgray;" @click="buttonys(item)">选择医生
                        </el-button>
                      </el-col>
                    </el-row>
                  </div>
                </el-card>
              </div>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </el-row>
</template>

<script>
  import {validatenull} from "@/utils/validate";
  import {saveDiagnosis, getRegistrationId} from '@/api/outpatient/remoteDiagnosisTreatment';
  import {
    listRegistrationPage,
    getRegistrationById,
    deleteRegistration,
    updateStatus,
    refundRegistrationPay,
  } from "@/api/outpatient/registration";
  import {listResourcePermission} from "@/api/admin/common/permission";
  import RegistrationForm from "./registrationForm";
  import YsForm from "./ysForm";
  import BeforehandForm from "./beforehandForm";
  import {listClinicOfficeAll} from "@/api/org/clinicOffice";
  import {listPatientAll} from "@/api/outpatient/patient";
  import {listDoctorsAll} from "@/api/outpatient/registration";
  import ExportExcelButton from "@/components/ExportExcelButton";
  import ViewColumnsSelect from "@/views/components/ViewColumnsSelect";
  import QueryForm from "@/views/components/queryForm";
  import MainUI from "@/views/components/mainUI";
  import OperationIcon from "@/components/OperationIcon";
  import History from "@/views/components/history";
  import {listDictItemAll} from "@/api/sys/dictItem";
  import {getTollInfoByRegistrationId, saveTollInfo} from "@/api/toll/tollInfo";
  import axios from 'axios'
  import Vue from "vue";
  Vue.prototype.$axios = axios;
  import { getLocalToken } from '@/utils/auth';
  import request from '@/utils/request'

  const loginForm = {Data:"API.Manage",AppId:"Oh_Newtouch_Clinic"}
  const config = {
    headers: {
      Token: getLocalToken()
    }
  }
  const institution = {
    Data: {TopOrganizeId: null},
    AppId: "Oh_Newtouch_Clinic",
    Timestamp: new Date()
  }



  const doctors = {
    appId: "Oh_Newtouch_Clinic",
    organizeId: "6d5752a7-234a-403e-aa1c-df8b45d3469f",
    timestamp: new Date(),
    data: {
      ks:""
  }
  }

  const configToken = {
    headers: {
      Authorization: "Bearer "+this.tokenData
    }
  }

  export default {

    extends: MainUI,
    components: {
      RegistrationForm,
      ExportExcelButton,
      ViewColumnsSelect,
      QueryForm,
      OperationIcon,
      History,
      YsForm,
      BeforehandForm,
    },
    data() {
      return {
        longRange:{Data:"API.Manage",AppId:"Oh_Newtouch_Clinic"},

        tokenData:null,
        ColumnWidth: 200,
        filerData: {
          pickerOptions: {
            // disabledDate(time) {
            //   return time.getTime() > Date.now() - 8.64e6;
            // },
          },
        },
        // exitNumberDate:"",      //退号时间
        activeName: "djxx",
        permission: {
          view: false,
          add: false,
          edit: false,
          remove: false,
          export: false,
          skip: false,
        },
        queryTypes: {
          department_id: "=",
          patient_id: "=",
          doctor_id: "=",
        },
        queryModel: {
          department: {
            // 科室
            id: null,
            name: "",
          },
          patientId: {
            // 父表 患者
            id: null,
            name: "",
          },
          doctor: {
            // 医生
            id: null,
            name: "",
          },
          hospital: {
            // 医院
            OrganizeId: null,
            Name: "",
          },
          createDate: [this.addCreateDate(), this.$moment(new Date()).endOf("day")],//时间范围
          cource: ''
        },
        source_List: [
          {
            name: '现场',
            value: 'registrationSource_0'
          },
          {
            name: '网上',
            value: 'registrationSource_1'
          }
        ],
        returnToll: {},
        search: {
          params: [
            {
              columnName: "company_id",
              queryType: "=",
              value: currentUser.company.id,
            },
          ],
          offset: 0,
          limit: 20,
          columnName: "", // 排序字段名
          order: "", // 排序
        },
        statusSearch: {
          params: [{columnName: "company_id", queryType: "=", value: ""}],
          offset: 0,
          limit: 100,
          columnName: "", // 排序字段名
          order: "", // 排序
        },
        statusList: null,
        currentPage: 1,
        registrationTotal: 0,
        registrationList: [],

        key_department: 1, // el-cascader key
        department_List: [], // 科室
        patientId_List: [], // 患者
        doctor_List: [], // 医生

        oprColumnWidth: 140, // 操作列宽
        tableId: "1008534118685450402",
        schemeId: "1008534118685450425",
        BASE_API: process.env.BASE_API,
        payType_List: [], //支付类型集合
        refundRegistrationId: 0,
        refundRegistraionVisible: false, //是否显示退挂号费弹框
        refundTotalFee: 0, //退费费用金额
        refundReistrationPayType: "", //退费支付方式
        refundRegistrationRemarks: "", //退费备注信息

        refundStatus: "registrationStatus_2", //退费状态默认值

        bizFormModel: {
          id: '',
          status: '',
          diagnosisTime:null,
          patientId: {
            gender: {}
          },
          doctor: {},
          clinicOffice: {},
          hospital: {}
        },//网上签到封装对象
        departments_List: [], // 科室
        doctors_List: [], // 医生
        selects_doctor: [],
        selects_mechanism: [],
        signDialogVisible: false,
        keys_department: 1, // el-cascader key
        applyForDialogVisible: false, // 远程诊疗申请弹框变量
        formRules: {

          "hospital.Name": [
            {required: true, message: "请选择医院", trigger: "change"},
          ],
          "doctor.name": [
            {required: true, message: "请选择医生", trigger: "change"},
          ],
          "clinicOffice.id": [
            {required: true, message: "请选择科室", trigger: "change"},
          ],
        },
        types: "",
      };
    },
    methods: {
      handleClick(tab, event) {
        if (tab.name == "djxx") {
          this.pageInit();
        } else if (tab.name == "yyfz") {
          this.pageInitnew();
        }

      },
      buttonyz(data) {
        console.log(data, '预诊');
        this.$refs.beforehandForm.$emit("openViewbeforehandDialog", data);
      },
      buttonys(data) {
        debugger
        console.log(data, '医生');
        if (this.types != "") {
          this.$refs.ysForm.$emit("openViewysDialog", data, this.types);
        } else {
          this.$message.warning("请先进行预诊！")
        }

      },

      // 打开远程诊疗申请窗口
      async applyForStudentExcel(index, row) {
        await getRegistrationId(row.id).then((res) => {
          if (res.data !== null) {
            this.applyForDialogVisible = false
            this.$message.warning("该用户已申请远程诊疗，请前往远程诊疗管理界面查看!")
            this.getRegistrationList();
          } else {
            this.applyForDialogVisible = true
          }
        }).catch((error) => {
          this.$message.error(error)
        })
        // 获取远程诊所token
        await this.$axios.post('/token/Auth/GetAppFrienAuthToken',loginForm,config)
          .then((response) => {
            this.tokenData = response.data.BusData.data.Token
          })
          .catch(function (error) {
            console.log(error);
          });

        // 获取远程诊所医院
        await this.$axios.post('apis/SysOrg/GetOrgList',institution,{
          headers: {
            Authorization: "Bearer "+this.tokenData
          }
        })
          .then((response) => {
            this.bizFormModel.hospital = response.data.BusData.data
            this.selects_mechanism = this.bizFormModel.hospital
            console.log("11111111111111"+this.bizFormModel.hospital);
            console.log("22222222222222"+this.selects_mechanism);
          })
          .catch(function (error) {
            console.log(error);
          });

        // 获取远程诊所科室
        await this.$axios.post('apis/Booking/GetOutpDepartment',{
          Data: null,
          OrganizeId: this.bizFormModel.hospital.OrganizeId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        },{
          headers: {
            Authorization: "Bearer "+this.tokenData
          }
        })
          .then((response) => {
            this.departments_List = response.data.BusData.data
          })
          .catch(function (error) {
            console.log(error);
          });

        // 获取远程诊所医生
        await this.$axios.post('apis/Booking/GetOutpDoctor',{
            appId: "Oh_Newtouch_Clinic",
            organizeId: this.bizFormModel.hospital.OrganizeId,
            timestamp: new Date(),
            data: {
              ks:this.bizFormModel.clinicOffice.ks
            }
          },{
          headers: {
            Authorization: "Bearer "+this.tokenData
          }
        })
          .then((response) => {
            this.doctors_List = response.data.BusData.data
          })
          .catch(function (error) {
            console.log(error);
          });

        await getRegistrationById(row.id)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.bizFormModel = responseData.data
            } else {
              this.showMessage(responseData);
            }
            this.resetLoad();
          })
          .catch((error) => {
            this.outputError(error);
          });
      },
      // 远程诊疗申请保存
      savedRequest() {
        this.bizFormModel.hospital = this.bizFormModel.hospital.Name
        this.bizFormModel.hospitalId = this.bizFormModel.doctor.organizeId
        this.bizFormModel.name = this.bizFormModel.patientId.name
        this.bizFormModel.nameId = this.bizFormModel.patientId.id
        this.bizFormModel.registrationId = this.bizFormModel.id
        this.bizFormModel.gender = this.bizFormModel.patientId.gender.name
        this.bizFormModel.department = this.bizFormModel.clinicOffice.ksmc
        this.bizFormModel.departmentId = this.bizFormModel.clinicOffice.ks
        this.bizFormModel.medic = this.bizFormModel.doctor.ysxm
        this.bizFormModel.medicId = this.bizFormModel.doctor.ysgh
        this.bizFormModel.status = '0'
        this.bizFormModel.id = null
        console.log(this.bizFormModel, '远程诊疗申请保存');
        saveDiagnosis(this.bizFormModel).then((res) => {
          if (res.code === "100") {
            this.applyForDialogVisible = false
            this.$message.success("保存成功！")
            this.getRegistrationList();
          } else {
            this.$message.error("保存失败，请联系后台!")
          }
        }).catch((error) => {
          this.$message.error(error)
        })
      },

      getks(){
        this.$axios.post('apis/Booking/GetOutpDepartment',{
          Data: null,
          OrganizeId: this.bizFormModel.hospital.OrganizeId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        },{
          headers: {
            Authorization: "Bearer "+this.tokenData
          }
        })
          .then((response) => {
            this.departments_List = response.data.BusData.data
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      getys(){
        // 获取远程诊所医生
        this.$axios.post('apis/Booking/GetOutpDoctor',{
          appId: "Oh_Newtouch_Clinic",
          organizeId: this.bizFormModel.hospital.OrganizeId,
          timestamp: new Date(),
          data: {
            ks:this.bizFormModel.clinicOffice.ks
          }
        },{
          headers: {
            Authorization: "Bearer "+this.tokenData
          }
        })
          .then((response) => {
            this.doctors_List = response.data.BusData.data
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      rettonys(value) {
        this.types = value;
        this.pageInitnew();
      },
      chargeClick(refundData) {
        this.refundRegistrationId = refundData.id;
        this.refundTotalFee = refundData.registrationFee;
        this.refundReistrationPayType = refundData.payType.value;
        this.refundRegistrationRemarks = "";
        this.returnToll = refundData
        this.refundRegistrationPayClick()
      },
      sortDoc(clinicOffice, officename) {
        console.log(clinicOffice, '........');
        if (this.doctors_List.length != 0) {
          //过滤医生
          this.selects_doctor = this.doctors_List.filter(doctor => doctor.office == clinicOffice)
          console.log(this.selects_doctor, '.......');
        }
      },
      reset() {
        this.$refs.queryForm.resetFields()
        this.onSearch()
      },
      openRegistration(id) {
        let page = window.open(this.BASE_API + '/ureport/preview?_u=Newtouch:registration.ureport.xml&_t=0&registrationId=' + id)
        page.onload = () => {
          page.document.title = "新致云诊所"
        }
      },
      async refundRegistrationPayClick() {
        console.log(this.returnToll);
        let toll = {
          company: this.returnToll.company,
          registrationFeeId: this.returnToll.id,
          amountReceivable: this.returnToll.registrationFee, //应收应退
          amountReceived: this.returnToll.registrationFee,  //实收
          paymentType: this.returnToll.payType,//支付类型
          patient: this.returnToll.patientId,
          amountStatus: {
            name: '已退费',
            value: 'amountStatus_2'
          },    //单据状态
          tollType: {
            name: '挂号',
            value: 'tollType_4'
          },    //收费类型
          remarks: ""   //备注信息
        }
        console.log(toll);

        const res = await refundRegistrationPay(
          this.refundRegistrationId,
          this.refundStatus,
          this.refundReistrationPayType,
          this.refundRegistrationRemarks,
        );
        if (res.code == "100") {
          //退费成功后修改费用表的状态
          // getTollInfoByRegistrationId(this.refundRegistrationId).then((res)=>{
          //   if(res.code=="100"){
          //     //console.log(res,'路程');
          //     let tollMesssage=res.data
          //     tollMesssage.amountStatus.name="已退费"
          //     tollMesssage.amountStatus.value="amountStatus_2"
          //     saveTollInfo(tollMesssage).then((res)=>{
          //           if(res.code=="100"){
          //            console.log(res,'kaknkanknkan');
          //           }
          //         }).catch(()=>{})
          //   }
          // }).catch(()=>{})
          //在收费表中插入数据
          if (res.data == 1) {
            if (this.returnToll.freeRegistrationFee != "1") {
              saveTollInfo(toll).then((res) => {
                if (res.code == "100") {

                }
              }).catch(() => {
              })
            }

            this.getRegistrationList();
            this.refundRegistraionVisible = false;
          } else {
            this.$message.error(res.data)
          }
        }
      },
      //显示退费弹框
      showRefundRegistrationDialog(refundData) {
        this.returnToll = refundData
        this.refundRegistrationId = refundData.id;
        this.refundTotalFee = refundData.registrationFee;
        this.refundReistrationPayType = refundData.payType.value;
        this.refundRegistrationRemarks = "";
        this.refundRegistraionVisible = true;
      },
      async updateStatusId(id, status, doctor, clinicOffice, subscribeDate) {
        let flage = false
        //判断预约时间是否在当天
        if (subscribeDate) {
          let orderDate = new Date(subscribeDate);
          let orderYear = orderDate.getFullYear()
          let orderMonth = orderDate.getMonth() + 1
          let orderDay = orderDate.getDate()
          let newOrderTime = orderYear + "-" + orderMonth + "-" + orderDay
          console.log(newOrderTime);

          //获取当前时间
          let now = new Date();
          let nowYear = now.getFullYear();
          let nowMonth = now.getMonth() + 1;
          let nowDay = now.getDate();
          let newTime = nowYear + "-" + nowMonth + "-" + nowDay
          if (newOrderTime !== newTime) {
            flage = true
          }
        }

        if (flage) {
          this.$message.warning("还未到预约时间,不能进行签到！")
          return
        }

        this.bizFormModel.id = id
        if (status === "registrationStatus_0") this.bizFormModel.status = "registrationStatus_2";
        if (status === "registrationStatus_3") this.bizFormModel.status = "registrationStatus_0";

        if (doctor && clinicOffice) {
          updateStatus(this.bizFormModel.id, this.bizFormModel.status, clinicOffice.id, doctor.id).then((res) => {
            if (res.code === "100") {

              this.signDialogVisible = false
              this.getRegistrationList();


            } else {
              this.$message.error("保存失败，请联系后台")
            }
          }).catch((error) => {
            this.$message.error(error)
          })
          return;
        }

        this.signDialogVisible = true;

      },
      getRegistrationList() {
        //this.queryModel.createDate = [this.addCreateDate(),new Date()]
        this.setLoad();
        /* 查询参数 和数据权限 */
        this.search.params = [
          {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
        ];
        if (this.moreCodition) {
          this.search.params = this.search.params.concat(
            this.compositeCondition()
          );
        } else {
          // 查询参数: 科室
          this.search.params.push({
            columnName: "department_id",
            queryType: "=",
            value: validatenull(this.queryModel.department.id)
              ? ""
              : this.queryModel.department.id,
          });
          // 查询参数: 患者
          this.search.params.push({
            columnName: "patient_id",
            queryType: "=",
            value: validatenull(this.queryModel.patientId.id)
              ? ""
              : this.queryModel.patientId.id,
          });
          // 查询参数: 医生
          this.search.params.push({
            columnName: "doctor_id",
            queryType: "=",
            value: validatenull(this.queryModel.doctor.id)
              ? ""
              : this.queryModel.doctor.id,
          });
          // 查询参数: 状态
          this.search.params.push({
            columnName: "status",
            queryType: "=",
            value: this.queryModel.status != undefined ? this.queryModel.status.value : '',
          });
          // 查询参数: 来源
          this.search.params.push({
            columnName: "source.value",
            queryType: "=",
            value: this.queryModel.source != undefined ? this.queryModel.source : '',
          });
          this.search.params.push({
            columnName: "create_date",
            queryType: ">=",
            value: this.queryModel.createDate
              ? this.$moment(this.queryModel.createDate[0]).format(
                "YYYY-MM-DD HH:mm:ss"
              )
              : "",
          });
          this.search.params.push({
            columnName: "create_date",
            queryType: "<=",
            value: this.queryModel.createDate
              ? this.$moment(this.queryModel.createDate[1]).format(
                "YYYY-MM-DD HH:mm:ss"
              )
              : "",
          });
        }
        // 数据权限: 挂号表registration
        this.pushDataPermissions(
          this.search.params,
          this.$route.meta.routerId,
          this.tableId
        );

        console.log(this.search);
        listRegistrationPage(this.search)
          .then((responseData) => {

            if (responseData.code == 100) {
              this.registrationTotal = responseData.data.total;
              this.registrationList = responseData.data.rows;
            } else {
              this.showMessage(responseData);
            }
            this.resetLoad();
          })
          .catch((error) => {
            this.outputError(error);
          });
      },
      onSearch() {
        if (this.moreCodition) {
          this.search.offset = 0;
          this.currentPage = 1;
          this.getRegistrationList();
        } else {
          this.$refs["queryForm"].validate((valid) => {
            if (valid) {
              this.search.offset = 0;
              this.currentPage = 1;
              this.getRegistrationList();
            } else {
              return false;
            }
          });
        }
      },
      onSizeChange(val) {
        this.currentPage = 1;
        this.search.limit = val;
        this.search.offset = (this.currentPage - 1) * val;
        this.getRegistrationList();
      },
      onCurrentChange(val) {
        this.search.offset = (val - 1) * this.search.limit;
        this.currentPage = val;
        this.getRegistrationList();
      },
      async pageInitnew() {
        this.setLoad();
        try {
          // debugger;
          this.initOptions(this.queryModel);
          this.search.order = " create_date ";
          this.search.params = [
            {
              columnName: "company_id",
              queryType: "=",
              value: currentUser.company.id,
            },
          ];
          this.search.params.push({
            columnName: "status",
            queryType: "=",
            value: "registrationStatus_5",
          });
          this.search.params.push({
            columnName: "doctor_id",
            queryType: "is null",
            value: null,
          });
          this.search.params.push({
            columnName: "create_date",
            queryType: ">=",
            value: this.$moment(new Date()).format(
              "YYYY-MM-DD 00:00:00"
            ),
          });
          this.search.params.push({
            columnName: "create_date",
            queryType: "<=",
            value: this.$moment(new Date()).format(
              "YYYY-MM-DD 23:59:59"
            ),
          });

          // 数据权限: 挂号表registration
          this.pushDataPermissions(
            this.search.params,
            this.$route.meta.routerId,
            this.tableId
          );
          let [listRegistrationRespData, listPermissionRespData] =
            await Promise.all([
              listRegistrationPage(this.search),
              listResourcePermission(this.$route.meta.routerId),
            ]);
          if (
            listRegistrationRespData.code == 100 &&
            listPermissionRespData.code == 100
          ) {
            console.log(listRegistrationRespData, '档案信息new');
            this.registrationTotal = listRegistrationRespData.data.total;
            this.registrationList = listRegistrationRespData.data.rows;
            this.permission.view = listPermissionRespData.data.find((item) => {
              return item.permission === "registration:read";
            });
            this.permission.export = listPermissionRespData.data.find((item) => {
              return item.permission === "registration:export";
            });
            this.permission.add = listPermissionRespData.data.find((item) => {
              return item.permission === "registration:create";
            });
            this.permission.edit = listPermissionRespData.data.find((item) => {
              return item.permission === "registration:update";
            });
            this.permission.remove = listPermissionRespData.data.find((item) => {
              return item.permission === "registration:delete";
            });
            this.permission.skip = listPermissionRespData.data.find((item) => {
              return item.permission === "registration:jump";
            });
          } else {
            this.showMessage(
              listPermissionRespData.code != 100
                ? listPermissionRespData
                : listRegistrationRespData
            );
          }
          this.resetLoad();
        } catch (error) {
          this.outputError(error);
        }
      },
      async pageInit() {
        this.setLoad();
        try {
          this.initOptions(this.queryModel);

          this.search.params = [
            {
              columnName: "company_id",
              queryType: "=",
              value: currentUser.company.id,
            },
          ];
          this.search.params.push({
            columnName: "create_date",
            queryType: ">=",
            value: this.queryModel.createDate[0]
              ? this.$moment(this.queryModel.createDate[0]).format(
                "YYYY-MM-DD HH:mm:ss"
              )
              : "",
          });
          this.search.params.push({
            columnName: "create_date",
            queryType: "<=",
            value: this.queryModel.createDate[1]
              ? this.$moment(this.queryModel.createDate[1]).format(
                "YYYY-MM-DD HH:mm:ss"
              )
              : "",
          });
          // 数据权限: 挂号表registration
          this.pushDataPermissions(
            this.search.params,
            this.$route.meta.routerId,
            this.tableId
          );
          let [listRegistrationRespData, listPermissionRespData] =
            await Promise.all([
              listRegistrationPage(this.search),
              listResourcePermission(this.$route.meta.routerId),
            ]);
          if (
            listRegistrationRespData.code == 100 &&
            listPermissionRespData.code == 100
          ) {
            console.log(listRegistrationRespData, '档案信息');
            this.registrationTotal = listRegistrationRespData.data.total;
            this.registrationList = listRegistrationRespData.data.rows;
            this.ColumnWidth = 200
            this.permission.view = listPermissionRespData.data.find((item) => {
              return item.permission === "registration:read";
            });
            this.permission.export = listPermissionRespData.data.find((item) => {
              return item.permission === "registration:export";
            });
            this.permission.add = listPermissionRespData.data.find((item) => {
              return item.permission === "registration:create";
            });
            this.permission.edit = listPermissionRespData.data.find((item) => {
              return item.permission === "registration:update";
            });
            this.permission.remove = listPermissionRespData.data.find((item) => {
              return item.permission === "registration:delete";
            });
            this.permission.skip = listPermissionRespData.data.find((item) => {
              return item.permission === "registration:jump";
            });
          } else {
            this.showMessage(
              listPermissionRespData.code != 100
                ? listPermissionRespData
                : listRegistrationRespData
            );
          }
          this.resetLoad();
        } catch (error) {
          this.outputError(error);
        }
      },
      onViewRegistration(index, row) {
        this.setLoad();
        getRegistrationById(row.id)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.$refs.registrationForm.$emit(
                "openViewRegistrationDialog",
                responseData.data
              );
            } else {
              this.showMessage(responseData);
            }
            this.resetLoad();
          })
          .catch((error) => {
            this.outputError(error);
          });
      },
      onCreateRegistration() {
        var longRange = this.longRange;
        console.log("跳转信息状态1：" + this.permission.skip)
        console.log("远程诊疗状态1：" + this.permission.edit)
        console.log("跳转信息状态1：" + this.permission.remove)
        console.log("默认医生：" + this.permission.view)
        this.$refs.registrationForm.$emit("openAddRegistrationDialog",this.permission.remove,this.permission.view);
      },
      onEditRegistration(index, row) {
        this.setLoad();
        getRegistrationById(row.id)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.$refs.registrationForm.$emit(
                "openEditRegistrationDialog",
                responseData.data
              );
            } else {
              this.showMessage(responseData);
            }
            this.resetLoad();
          })
          .catch((error) => {
            this.outputError(error);
          });
      },
      onCopyRegistration(index, row) {
        this.setLoad();
        getRegistrationById(row.id)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.$refs.registrationForm.$emit(
                "openCopyRegistrationDialog",
                responseData.data,
                this.permission.remove,
                this.permission.view
              );
            } else {
              this.showMessage(responseData);
            }
            this.resetLoad();
          })
          .catch((error) => {
            this.outputError(error);
          });
      },
      onDeleteRegistration(index, row) {
        this.$confirm("确定删除吗？", "确认", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            this.setLoad();
            deleteRegistration(row)
              .then((responseData) => {
                if (responseData.code == 100) {
                  this.getRegistrationList();
                  this.showMessage({type: "success", msg: "删除成功"});
                } else {
                  this.showMessage(responseData);
                }
                this.resetLoad();
              })
              .catch((error) => {
                this.outputError(error);
              });
          })
          .catch(() => {
          });
      },
      onSortChange(orderby) {
        if (validatenull(orderby.prop)) {
          this.search.columnName = "";
          this.search.order = "";
        } else {
          this.search.columnName = orderby.prop;
          this.search.order = orderby.order === "descending" ? "desc" : "asc";
        }

        this.getRegistrationList();
      },
      initOptions(This) {
        this.key_department++;
        let department_search = {
          params: [
            {
              columnName: "company_id",
              queryType: "=",
              value: currentUser.company.id,
            },
          ],
        };
        // 响应字段的条件操作符，替换成触发字段的操作符
        department_search.params.forEach((item) => {
          if (this.queryTypes[item.columnName]) {
            item.queryType = this.queryTypes[item.columnName];
          }
        });
        // 字段对应表上filter条件
        department_search.params.push.apply(department_search.params, []);
        // 数据权限: 部门org_department
        this.pushDataPermissions(
          department_search.params,
          this.$route.meta.routerId,
          "52676365136650250"
        );
        this.department_List.splice(0, this.department_List.length);
        listClinicOfficeAll(department_search).then((responseData) => {
          this.department_List = responseData.data;
        });
        let patientId_search = {
          params: [],
        };
        // 响应字段的条件操作符，替换成触发字段的操作符
        patientId_search.params.forEach((item) => {
          if (this.queryTypes[item.columnName]) {
            item.queryType = this.queryTypes[item.columnName];
          }
        });
        // 字段对应表上filter条件
        patientId_search.params.push.apply(patientId_search.params, [
          {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
        ]);
        // 数据权限: 患者表patient
        this.pushDataPermissions(
          patientId_search.params,
          this.$route.meta.routerId,
          "1008489176147648530"
        );
        this.patientId_List.splice(0, this.patientId_List.length);
        listPatientAll(patientId_search).then((responseData) => {
          this.patientId_List = responseData.data;
        });
        let doctor_search = {
          params: [
            {
              columnName: "department_id",
              queryType: "=",
              value: currentUser.department.id,
            },
          ],
        };
        // 响应字段的条件操作符，替换成触发字段的操作符
        doctor_search.params.forEach((item) => {
          if (this.queryTypes[item.columnName]) {
            item.queryType = this.queryTypes[item.columnName];
          }
        });
        // 字段对应表上filter条件
        doctor_search.params.push.apply(doctor_search.params, []);
        // 数据权限:  用户sys_user
        this.pushDataPermissions(
          doctor_search.params,
          this.$route.meta.routerId,
          "4004"
        );
        this.doctor_List.splice(0, this.doctor_List.length);
        listDoctorsAll(doctor_search).then((responseData) => {
          this.doctor_List = responseData.data;
        });

        this.statusSearch.params = [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1008898177293385773",
          },
        ];
        listDictItemAll(this.statusSearch).then((responseData) => {
          this.statusList = responseData.data;
          console.log(this.statusList, "sss");
        });


        //支付类型查询
        let payType_search = {
          params: [
            {
              columnName: "dict_type_id",
              queryType: "=",
              value: "1008793465990693120",
            },
          ],
        };
        // 字段对应表上filter条件
        payType_search.params.push.apply(payType_search.params, []);
        // 数据权限: 字典项sys_dict_item
        this.pushDataPermissions(
          payType_search.params,
          this.$route.meta.routerId,
          "4005"
        );
        this.payType_List.splice(0, this.payType_List.length);
        listDictItemAll(payType_search).then((responseData) => {
          this.payType_List = responseData.data;
        });
        //查询可挂号科室
        this.keys_department++;
        let departments_search = {
          params: [
            {
              columnName: "company_id",
              queryType: "=",
              value: currentUser.company.id,
            },
            {columnName: "is_locked", queryType: "=", value: "0"},
            {columnName: "is_register", queryType: "=", value: "1"},
          ],
        };


        // 字段对应表上filter条件
        departments_search.params.push.apply(departments_search.params, []);
        // 数据权限: 部门org_department
        this.pushDataPermissions(
          departments_search.params,
          this.$route.meta.routerId,
          "52676365136650250"
        );
        this.departments_List.splice(0, this.departments_List.length);//查询科室请求已放入查询医生请求中

        //门诊医生查询
        let doctors_search = {
          params: [
            {
              columnName: "department_id",
              queryType: "=",
              value: currentUser.department.id,
            },
          ],
        };
        // 字段对应表上filter条件
        doctors_search.params.push.apply(doctors_search.params, []);
        // 表有禁用字段
        doctors_search.params.push.apply(doctors_search.params, [
          {columnName: "is_locked", queryType: "=", value: "0"},
        ]);
        // 数据权限:  用户sys_user
        this.pushDataPermissions(
          doctors_search.params,
          this.$route.meta.routerId,
          "4004"
        );
        this.doctors_List.splice(0, this.doctors_List.length);
        listDoctorsAll().then((responseData) => {
          console.log(responseData, '医生');
          this.doctors_List = responseData.data;
          listClinicOfficeAll(departments_search).then((responseData) => {
            this.departments_List = responseData.data;
            for (let i = 0; i < this.departments_List.length; i++) {
              if (this.departments_List[i].isDefault == '1') {
                this.bizFormModel.clinicOffice.id = this.departments_List[i].id
                this.sortDoc(this.departments_List[i].id)
              }
            }
          });

        });
      },
      addCreateDate() {

        let myDate = new Date();
        let lw = new Date(myDate.getTime() - 1000 * 60 * 60 * 24 * 30); //最后一个数字30可改，30天的意思
        console.log(lw.getDate());
        let lastY = lw.getFullYear();
        let lastM = lw.getMonth() + 1;
        let lastD = lw.getDate();
        let housrs = lw.getHours();
        let minutes = lw.getMinutes()
        let seconds = lw.getSeconds()
        let startData =
          lastY +
          "-" +
          (lastM < 10 ? "0" + lastM : lastM) +
          "-" +
          (lastD < 10 ? "0" + lastD : lastD) +
          " " + housrs + ":" + minutes + ":" + seconds //三十天之前日期
        return startData;

      },
      indexMethod(index) {
        return (this.currentPage - 1) * this.search.limit + index + 1;
      },
      signIn() {
        console.log(this.bizFormModel, '签到');
        updateStatus(this.bizFormModel.id, this.bizFormModel.status, this.bizFormModel.clinicOffice.id, this.bizFormModel.doctor.id).then((res) => {
          if (res.code === "100") {

            this.signDialogVisible = false
            this.getRegistrationList();


          } else {
            this.$message.error("保存失败，请联系后台")
          }
        }).catch((error) => {
          this.$message.error(error)
        })
      }
    },
    watch: {
      "queryModel.department.id": function (val, oldVal) {
        if (val != oldVal) {
          let This = this.queryModel;
          this.queryModel.doctor = {
            // 医生
            id: null,
            name: null,
          };
          let doctor_search = {
            params: [
              {
                columnName: "department_id",
                queryType: "=",
                value: currentUser.department.id,
              },
            ],
          };
          // 响应字段的条件操作符，替换成触发字段的操作符
          doctor_search.params.forEach((item) => {
            if (this.queryTypes[item.columnName]) {
              item.queryType = this.queryTypes[item.columnName];
            }
          });
          // 字段对应表上filter条件
          doctor_search.params.push.apply(doctor_search.params, []);
          // 数据权限:  用户sys_user
          this.pushDataPermissions(
            doctor_search.params,
            this.$route.meta.routerId,
            "4004"
          );
          this.doctor_List.splice(0, this.doctor_List.length);
          listDoctorsAll(doctor_search).then((responseData) => {
            this.doctor_List = responseData.data;
          });
        }
      },
      registrationList(val) {
        // if(val){
        //   this.$nextTick(() => {
        //       this.$refs.mutipleTable.doLayout();
        //   });
        // }
      },
      // tableData是el-table绑定的数据
      tableData: {
        // 解决表格显示错位问题
        handler() {
          this.$nextTick(() => {
            // tableRef是el-table绑定的ref属性值
            this.$refs.mutipleTable.doLayout()// 对 Table 进行重新布局
          })
        },
        deep: true
      },
    },
    updated() {
      if (this.$refs.mutipleTables) {
        this.$nextTick(() => {
          this.$refs.mutipleTables.doLayout();
        });
      }
    },
    created() {
      this.pageInit();
    },
    mounted() {
      this.pageInit();
    },
  };
</script>
<style lang="scss" scoped>

  .page-container {
    padding: 0;
  }

  .btn,
  .btn1,
  .btn2 {
    display: flex;
    justify-content: center;
    align-items: center;
    color: #01a5e4;
    margin: 0
  }

  .btn span:nth-child(1) {
    color: #01a5e4;
    padding: 0px 10px;
  }

  .btn span:nth-child(1) {
    color: #01a5e4;
    padding: 0px 10px;
  }

  .btn span:nth-child(2) {
    color: #14ca04;
    padding: 0px 10px;
  }

  .btn1 span:nth-child(3) {
    color: #e32929;
    padding: 0px 10px;
  }

  .btn2 {
    color: #01a5e4;
  }

  #refundTotalFee_Container {
    display: flex;
    justify-content: flex-start;
    padding: 20px;
    margin: 10px;
    background-color: rgba(207, 227, 236, 0.803921568627451);
    border-radius: 7px;
  }

  #refundTotalFee_Title {
    font-family: "Arial Normal", "Arial", sans-serif;
    font-weight: 400;
    font-style: normal;
    font-size: 21px;
    margin-right: 8px;
  }

  #refundTotalFee_Fee {
    font-weight: 800;
    font-style: black;
    font-size: 25px;
    margin-top: -4px;
  }

  .refund-registration-flex-start {
    display: flex;
    justify-content: flex-start;
    padding: 0 20px;
    margin: 10px;
  }

  .refund-registration-p {
    width: 80px;
    text-align: right;
    margin-right: 10px;
  }

  .refund-registration-content {
    widows: 250px;
    margin-top: 15px;
  }


  /deep/ .el-table__fixed-right-patch {
    width: 5px !important
  }

  ;

  /deep/ .el-table colgroup col[name='gutter'] {
    width: 5px !important
  }

  ;

  /deep/ .el-table__body {
    width: 100% !important
  }

  ;

  .drag_table {
    // 设置表格header的高度
    /deep/ th {
      height: 44px;
    }

    /deep/ th.gutter:last-of-type {
      height: 0 !important;
    }

    // 设置表格body的高度
    /deep/ .el-table__body-wrapper {
      //解决数据展示超出body高度不滚动bug
      overflow-y: auto;
      // 减去的是表格header的高度
      height: calc(100% - 44px) !important;
    }

    .el-table__fixed-right {
      height: 100% !important;
    }

  }


  .text {
    font-size: 14px;

  }

  .item {
    padding: 18px 0;
  }

  .bottom {
    background-color: rgba(213, 218, 223, 0.87);
    border: 1px solid beige;
    margin-top: 13px;
    height: 40px;
    line-height: 35px;
  }


  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }

  .clearfix:after {
    clear: both
  }

  .box-card {
    margin-right: 20px;
    // width: 400px;
    // height: 200px;
  }

</style>
<style scoped>
  /deep/ .el-table__body-wrapper {
    height: calc(100% - 44px) !important;
  }
</style>
