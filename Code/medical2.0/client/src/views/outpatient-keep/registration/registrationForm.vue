<template>
  <el-dialog
    class="registrationForm"
    :title="dialogProps.title"
    :visible.sync="dialogProps.visible"
    :close-on-click-modal="false"
    width="50%"
    @open="onDialogOpen()"
    v-loading="loading"
  >
    <div slot="title" class="dialog-header">
      {{ dialogProps.title }}
      <OperationIcon
        v-show="dialogProps.action == 'view' && permission.edit"
        type="primary"
        text="编辑"
        placement="top-start"
        icon-name="el-icon-edit"
        @click="switchEdit"
      ></OperationIcon>
    </div>

    <el-form
      :model="bizFormModel"
      :rules="formRules"
      ref="registrationForm"
      label-width="120px"
      label-position="right"
      class="edit-form"
    >
      <div class="tab-item" v-show="tabIndex == '1'">
        <div class="registrationItemBox">
          <h3>基本信息</h3>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="患者" prop="name">
                <el-input
                  v-if="dialogProps.action != 'add'"
                  :disabled="true"
                  v-model="bizFormModel.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.name"
                  value-key="id"
                  filterable
                  allow-create
                  @change="GetPatientInfo()"
                  placeholder="请选择患者"
                >
                  <el-option
                    v-for="patientId in patientId_List"
                    :key="patientId.id"
                    :label="patientId.name"
                    :value="patientId"
                  >
                    <span style="float: left">{{ patientId.name }}</span>
                    <span
                      style="float: right; color: #8492a6; font-size: 13px"
                      >{{ patientId.phone }}</span
                    >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="性别" prop="gender.name">
                <el-input
                  v-if="dialogProps.action != 'add'"
                  :disabled="true"
                  v-model="bizFormModel.gender.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.gender"
                  value-key="value"
                  filterable
                  placeholder="请选择性别"
                  @clear="
                    bizFormModel.gender = {
                      value: null,
                      name: null,
                    }
                  "
                >
                  <el-option
                    v-for="gender in gender_List"
                    :key="gender.value"
                    :label="gender.name"
                    :value="gender"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="年龄" prop="age">
                <el-input
                  :disabled="dialogProps.action != 'add'"
                  v-model="bizFormModel.age"
                  :maxlength="45"
                  :placeholder="
                    dialogProps.action == 'view' ? '' : '请输入年龄'
                  "
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="月" prop="month">
                <el-input
                  :disabled="dialogProps.action != 'add'"
                  v-model="bizFormModel.month"
                  :maxlength="45"
                  :placeholder="dialogProps.action == 'view' ? '' : '请输入月'"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
           <el-col :span="24 / 2">
              <el-form-item label="身份证号" prop="card">
                <el-input
                  :disabled="dialogProps.action != 'add'"
                  v-model="bizFormModel.card"
                  :maxlength="45"
                  :placeholder="
                    dialogProps.action == 'view' ? '' : '请输入身份证号'
                  "
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="联系方式" prop="phone">
                <el-input
                  :disabled="dialogProps.action != 'add'"
                  v-model="bizFormModel.phone"
                  :maxlength="45"
                  :placeholder="
                    dialogProps.action == 'view' ? '' : '请输入联系方式'
                  "
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
             <!-- <el-col :span="24 / 2">
              <el-form-item label="出生日期" prop="birthday">
                <el-date-picker
                  :disabled="dialogProps.action != 'add'"
                  v-model="bizFormModel.birthday"
                  type="date"
                  :picker-options="setDisabled"
                  value-format="yyyy-MM-dd"
                  :placeholder="
                    dialogProps.action == 'view' ? '' : '请输入出生日期'
                  "
                ></el-date-picker>
              </el-form-item>
            </el-col> -->
            
            <el-col :span="24 / 2">
              <el-form-item label="与患者关系" prop="withPatientNexus">
                <el-input
                  v-if="dialogProps.action != 'add'"
                  :disabled="true"
                  v-model="bizFormModel.withPatientNexus.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.withPatientNexus"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择与患者关系"
                  @clear="
                    bizFormModel.withPatientNexus = {
                      value: null,
                      name: null,
                    }
                  "
                >
                  <el-option
                    v-for="withPatientNexus in withPatientNexus_List"
                    :key="withPatientNexus.value"
                    :label="withPatientNexus.name"
                    :value="withPatientNexus"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <div class="registrationItemBox">
          <h3>登记信息</h3>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="科室" prop="department.id">
                <el-input
                  v-if="dialogProps.action == 'view'"
                  :disabled="true"
                  v-model="bizFormModel.department.name"
                ></el-input>
                <el-cascader
                  v-else
                  v-model="bizFormModel.department.id"
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
            <el-col :span="24 / 2">
              <el-form-item label="来源" prop="source">
                <el-input
                  v-if="dialogProps.action == 'view'"
                  :disabled="true"
                  v-model="bizFormModel.source.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.source"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择来源"
                  @clear="
                    bizFormModel.source = {
                      value: null,
                      name: null,
                    }
                  "
                >
                  <el-option
                    v-for="source in source_List"
                    :key="source.value"
                    :label="source.name"
                    :value="source"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="医生" prop="doctor.name">
                <el-input
                  v-if="dialogProps.action == 'view'"
                  :disabled="true"
                  v-model="bizFormModel.doctor.name"
                ></el-input>
                <el-select
                  v-else
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
                    v-for="doctor in doctor_List"
                    :key="doctor.id"
                    :label="doctor.name"
                    :value="doctor"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="治疗类型" prop="treatType.name">
                <el-input
                  v-if="dialogProps.action == 'view'"
                  :disabled="true"
                  v-model="bizFormModel.treatType.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.treatType"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择治疗类型"
                  @clear="
                    bizFormModel.treatType = {
                      value: null,
                      name: null,
                    }
                  "
                >
                  <el-option
                    v-for="treatType in treatType_List"
                    :key="treatType.value"
                    :label="treatType.name"
                    :value="treatType"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="免挂号费" prop="freeRegistrationFee">
                <el-switch
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.freeRegistrationFee"
                  active-color="#13ce66"
                  inactive-color="#dbdfe6"
                  active-value="1"
                  inactive-value="0"
                ></el-switch>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item
                label="挂号费"
                prop="registrationFee"
                :rules="
                  bizFormModel.freeRegistrationFee === '0'
                    ? formRules.registrationFee
                    : [
                        {
                          required: false,
                        },
                      ]
                "
              >
                <el-input
                  v-if="dialogProps.action == 'view'"
                  :disabled="true"
                  v-model="bizFormModel.registrationFee"
                ></el-input>
                <number-input
                  v-else
                  v-model="bizFormModel.registrationFee"
                  currency="CNY"
                  :precision="2"
                  :disabled="bizFormModel.freeRegistrationFee == '1'"
                ></number-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <div class="registrationItemBox">
          <h3>其他信息</h3>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="体温" prop="temperature">
                <div class="ipt-box">
                  <el-input
                    :disabled="dialogProps.action == 'view'"
                    v-model="bizFormModel.temperature"
                    :maxlength="45"
                    :placeholder="
                      dialogProps.action == 'view' ? '' : '请输入体温'
                    "
                  ></el-input>
                  <span>℃</span>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="脉搏" prop="pulse">
                <div class="ipt-box">
                  <el-input
                    :disabled="dialogProps.action == 'view'"
                    v-model="bizFormModel.pulse"
                    :maxlength="45"
                    :placeholder="
                      dialogProps.action == 'view' ? '' : '请输入脉搏'
                    "
                  ></el-input>
                  <span>次/min</span>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="呼吸" prop="breathe">
                <div class="ipt-box">
                  <el-input
                    :disabled="dialogProps.action == 'view'"
                    v-model="bizFormModel.breathe"
                    :maxlength="45"
                    :placeholder="
                      dialogProps.action == 'view' ? '' : '请输入呼吸'
                    "
                  ></el-input>
                  <span>次/min</span>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item
                label="支付方式"
                prop="payType"
                v-if="dialogProps.action == 'view'"
              >
                <el-input
                  :disabled="true"
                  v-model="bizFormModel.payType.name"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="是否去过疫区" prop="isGoHigharea">
                <el-switch
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.isGoHigharea"
                  active-color="#13ce66"
                  inactive-color="#dbdfe6"
                  active-value="1"
                  inactive-value="0"
                ></el-switch>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item
                label="疫区地点"
                prop="highArea"
                :rules="
                  bizFormModel.isGoHigharea === '1'
                    ? formRules.highArea
                    : [
                        {
                          required: false,
                        },
                      ]
                "
              >
                <el-input
                  :disabled="
                    dialogProps.action == 'view' ||
                    bizFormModel.isGoHigharea === '0'
                  "
                  v-model="bizFormModel.highArea"
                  :maxlength="128"
                  :placeholder="
                    dialogProps.action == 'view' ? '' : '请输入疫区地点'
                  "
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="备注信息" prop="remarks">
                <el-input
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.remarks"
                  type="textarea"
                  :maxlength="255"
                  :placeholder="
                    dialogProps.action == 'view' ? '' : '请输入备注信息'
                  "
                  clearable
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button
        v-if="dialogProps.action != 'view'"
        type="primary"
        :disabled="flage"
        :plain="true"
        @click="onSubmit('registrationForm')"
        >保 存</el-button
      >
      <el-button
        v-if="dialogProps.action != 'view'"
        :plain="true"
        @click="onDialogClose()"
        >取 消</el-button
      >
      <el-button
        v-if="dialogProps.action == 'view'"
        :plain="true"
        @click="onDialogClose()"
        >关 闭</el-button
      >
    </span>

    <!--收费支付弹框 开始-->
    <el-dialog
      title="收费"
      :visible.sync="payRegistrationFeeVisible"
      width="50%"
      :close-on-click-modal="false"
      :before-close="dialogBeforeClose"
      append-to-body
    >
      <div>
        <el-divider></el-divider>
        <div id="registrationTotalFee_Container">
          <div id="registrationTotalFee_Title">应收金额（元）：</div>
          <div id="registrationTotalFee_Fee">
            {{ this.bizFormModel.registrationFee }}
          </div>
        </div>
        <div class="pay-registration-flex-start">
          <p class="pay-registration-p">实收金额</p>
          <el-input
            v-model="bizFormModel.practicleRegistrationFee"
            placeholder="请输入实收金额"
            type="number"
            class="pay-registration-content"
          ></el-input>
        </div>
        <div class="pay-registration-flex-start">
          <p class="pay-registration-p">支付方式</p>
          <el-radio-group
            v-model="bizFormModel.payType.value"
            class="pay-registration-content"
          >
            <el-radio
              v-for="ptype in payType_List"
              :key="ptype.value"
              :label="ptype.value"
              >{{ ptype.name }}</el-radio
            >
          </el-radio-group>
        </div>
        <div class="pay-registration-flex-start">
          <p class="pay-registration-p">备注</p>
          <el-input
            v-model="bizFormModel.practicalRegistrationPayRemarks"
            class="pay-registration-content"
          ></el-input>
        </div>
        <el-divider></el-divider>
      </div>
      <div slot="footer">
        <el-button @click="payRegistrationFeeVisible = false">取 消</el-button>
        <el-button type="primary" @click="payRegistrationFeeClick"
          >确认收费</el-button
        >
      </div>
    </el-dialog>
    <!--收费支付弹框 结束-->
  </el-dialog>
</template>
<script>
import { validatenull } from "@/utils/validate";
import { treeDepartment } from "@/api/org/department";
import {
  listPatientAll,
  savePatient,
  getPatientById,
} from "@/api/outpatient/patient";
import { listUserAll } from "@/api/admin/user";
import { listDictItemAll } from "@/api/sys/dictItem";
import { saveRegistration } from "@/api/outpatient/registration";
import BaseUI from "@/views/components/baseUI";
import OperationIcon from "@/components/OperationIcon";
export default {
  extends: BaseUI,
  name: "registration-form",
  components: {
    OperationIcon,
  },
  data() {
    return {
      setDisabled: {
        disabledDate(time) {
          return time.getTime() > Date.now(); // 可选历史天、可选当前天、不可选未来天
          // return time.getTime() > Date.now() - 8.64e7;  // 可选历史天、不可选当前天、不可选未来天
          // return time.getTime() < Date.now() - 8.64e7;  // 不可选历史天、可选当前天、可选未来天
          // return time.getTime() < Date.now(); // 不可选历史天、不可选当前天、可选未来天

          /*只能选择今天前的近七天*/
          // let now = Date.now();
          // let seven = 7 * 8.64e7;
          // let sevenDays = now - seven;
          // return time.getTime() > now || time.getTime() < sevenDays;  //大于今天和小于七天前的禁用
        },
      },

      payRegistrationFeeVisible: false, //显示收费窗口
      bizFormModel: { ...this.initFormModel(), ...this.initPatientModel() },
      PatientFormModel: this.initPatientModel(),
      tabIndex: "1",
      gender_List: [], // 性别
      flage:false,
      withPatientNexus_List: [], // 与患者关系
      key_department: 1, // el-cascader key
      department_List: [], // 科室
      patientId_List: [], // 患者
      doctor_List: [], // 医生
      treatType_List: [], // 治疗类型
      source_List: [], // 来源
      payType_List: [], // 支付方式
      status_List: [], // 状态
      dialogProps: {
        visible: false,
        action: "",
        title: "",
      },
      formRules: {
        registrationFee: [
          { required: true, message: "请输入挂号费", trigger: "blur" },
        ],
        highArea: [
          { required: true, message: "请输入疫区地点", trigger: "blur" },
        ],
        "treatType.name": [
          { required: true, message: "请选择治疗类型", trigger: "change" },
        ],
        "doctor.name": [
          { required: true, message: "请选择医生", trigger: "change" },
        ],
        phone: [{ required: true, message: "请输入联系方式", trigger: "blur" }],
        age: [{ required: true, message: "请输入年龄", trigger: "blur" }],
        name: [{ required: true, message: "请输入姓名", trigger: "change" }],
        "gender.name": [
          { required: true, message: "请输入性别", trigger: "change" },
        ],
      },
    };
  },
  props: {
    // 权限
    permission: {
      type: Object,
    },
  },
  methods: {
    GetPatientInfo() {
      //挂号id不能被覆盖
      var id = this.bizFormModel.id;
      if (this.bizFormModel.name.id) {
        this.PatientFormModel = this.bizFormModel.name;
        this.bizFormModel = { ...this.bizFormModel, ...this.PatientFormModel };
      } else {
        this.PatientFormModel = {
          ...this.initPatientModel(),
          name: this.bizFormModel.name,
        };
        this.bizFormModel = { ...this.bizFormModel, ...this.PatientFormModel };
      }
      this.bizFormModel.id = id;
    },
    savePatient() {
      var registerId = this.bizFormModel.id;
      this.bizFormModel.id = this.PatientFormModel.id;
      savePatient(this.bizFormModel)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.flage=false
            this.bizFormModel.patientId = {
              id: responseData.data,
              name: this.PatientFormModel.name,
            };
            this.bizFormModel.id = registerId;
            this.doSave();
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.flage=false
          this.outputError(error);
        });
    },
    payRegistrationFeeClick() {
      this.savePatient();
    },
    onSubmit(formName) {
      this.flage=true
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (
            this.bizFormModel.freeRegistrationFee == 1 ||
            this.bizFormModel.registrationFee == 0
          ) {
            this.savePatient();
          } else {
            this.flage=false
            if (this.payType_List.length > 0) {
              this.bizFormModel.payType.value = this.payType_List[0].value;
            }
            this.bizFormModel.practicleRegistrationFee =
              this.bizFormModel.registrationFee;
            this.bizFormModel.practicalRegistrationPayRemarks = "";
            this.payRegistrationFeeVisible = true;
          }
        } else {
          this.flage=false
          return false;
        }
      });
    },
    doSave() {
      this.setLoad();
      if (this.bizFormModel.source.value == "registrationSourc_1") {
        this.bizFormModel.status.value = "registrationStatus_3";
        this.bizFormModel.status.name = "待签到";
      }
      saveRegistration(this.bizFormModel)
        .then((responseData) => {
          this.flage=false
          if (responseData.code == 100) {
            this.dialogProps.visible = false;
            this.payRegistrationFeeVisible = false;
            this.$emit("save-finished");
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.flage=false
          this.outputError(error);
        });
    },
    switchEdit() {
      this.dialogProps.action = "edit";
      this.dialogProps.title = "修改登记信息";
      this.initOptions(this.bizFormModel);
    },
    onDialogClose() {
      this.dialogProps.visible = false;
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["registrationForm"].clearValidate();
      });
    },
    initPatientModel() {
      return {
        company: {
          // 诊所id
          id: currentUser.company.id,
          name: null,
        },
        name: "", // 患者姓名
        gender: {
          // 性别null
          value: null,
          name: null,
        },
        age: "", // 年龄
        month: "", // 月
        birthday: "", // 出生日期
        phone: "", // 联系方式
        card: "", // 身份证号
        withPatientNexus: {
          // 与患者关系
          value: null,
          name: null,
        },
        healthCardNo: "", // 健康卡号
        province: "", // 省
        city: "", // 市
        area: "", // 区
        address: "", // 详细地址
        remarks: "", // 备注信息
      };
    },
    initFormModel(This) {
      return {
        company: {
          // 诊所id
          id: currentUser.company.id,
          name: null,
        },
        department: {
          // 科室
          id: null,
          name: null,
        },
        patientId: {
          // 父表 患者
          id:
            validatenull(This) || validatenull(This.patientId)
              ? null
              : This.patientId.id,
          name:
            validatenull(This) || validatenull(This.patientId)
              ? null
              : This.patientId.name,
        },
        doctor: {
          // 医生
          id: null,
          name: null,
        },
        treatType: {
          // 治疗类型
          value: null,
          name: null,
        },
        receptionStartDate: "", // 接诊开始时间
        source: {
          // 来源
          value: null,
          name: null,
        },
        receptionEndDate: "", // 接诊结束时间
        pulse: "", // 脉搏
        breathe: "", // 呼吸
        temperature: "", // 体温
        isGoHigharea: "0", // 是否去过疫区
        highArea: "", // 疫区地点
        freeRegistrationFee: "0", // 免挂号费
        registrationFee: 0, // 挂号费
        practicleRegistrationFee: 0, //实际支付的挂号费
        practicalRegistrationPayRemarks: "", //实际支付备注
        refundRegistrationPayType: {
          value: null,
          name: null,
        },
        payType: {
          // 支付方式
          value: "payType_0",
          name: "现金",
        },
        remarks: "", // 备注信息
        status: {
          // 状态
          value: "registrationStatus_0",
          name: "待就诊",
        },
        dispensingStatus: "0", // 发药状态
        dispensingDate: "", // 发/退药时间
        chargeStatus: "0", // 收费状态
        chargeDate: "", // 收费时间
      };
    },
    initOptions(This) {
      //查询患者性别
      let gender_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1008489176147648522",
          },
        ],
      };
      // 字段对应表上filter条件
      gender_search.params.push.apply(gender_search.params, []);
      this.gender_List.splice(0, this.gender_List.length);
      listDictItemAll(gender_search).then((responseData) => {
        this.gender_List = responseData.data;
      });

      //查询与患者关系
      let withPatientNexus_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1008489176147648526",
          },
        ],
      };
      // 字段对应表上filter条件
      withPatientNexus_search.params.push.apply(
        withPatientNexus_search.params,
        []
      );
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        withPatientNexus_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.withPatientNexus_List.splice(0, this.withPatientNexus_List.length);
      listDictItemAll(withPatientNexus_search).then((responseData) => {
        this.withPatientNexus_List = responseData.data;
      });

      //查询可挂号科室
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
      // 字段对应表上filter条件
      department_search.params.push.apply(department_search.params, []);
      // 数据权限: 部门org_department
      this.pushDataPermissions(
        department_search.params,
        this.$route.meta.routerId,
        "52676365136650250"
      );
      this.department_List.splice(0, this.department_List.length);
      treeDepartment(department_search).then((responseData) => {
        this.department_List = responseData.data;
      });

      //查询病人列表
      let patientId_search = {
        params: [],
      };
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

      //门诊医生查询
      let doctor_search = {
        params: [
          {
            columnName: "department_id",
            queryType: "=",
            value: currentUser.department.id,
          },
        ],
      };
      // 字段对应表上filter条件
      doctor_search.params.push.apply(doctor_search.params, []);
      // 表有禁用字段
      doctor_search.params.push.apply(doctor_search.params, [
        { columnName: "is_locked", queryType: "=", value: "0" },
      ]);
      // 数据权限:  用户sys_user
      this.pushDataPermissions(
        doctor_search.params,
        this.$route.meta.routerId,
        "4004"
      );
      this.doctor_List.splice(0, this.doctor_List.length);
      listUserAll(doctor_search).then((responseData) => {
        this.doctor_List = responseData.data;
      });

      //治疗类型
      let treatType_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1008534118685450388",
          },
        ],
      };
      // 字段对应表上filter条件
      treatType_search.params.push.apply(treatType_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        treatType_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.treatType_List.splice(0, this.treatType_List.length);
      listDictItemAll(treatType_search).then((responseData) => {
        this.treatType_List = responseData.data;
      });

      //病人来源
      let source_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1016341269718508214",
          },
        ],
      };
      // 字段对应表上filter条件
      source_search.params.push.apply(source_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        source_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.source_List.splice(0, this.source_List.length);
      listDictItemAll(source_search).then((responseData) => {
        this.source_List = responseData.data;
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

      //挂号状态
      let status_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1008898177293385773",
          },
        ],
      };
      // 字段对应表上filter条件
      status_search.params.push.apply(status_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        status_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.status_List.splice(0, this.status_List.length);
      listDictItemAll(status_search).then((responseData) => {
        this.status_List = responseData.data;
      });
    },
  },
  watch: {
    //监视器监视到科室改变，获取科室内的操作员绑定
    "bizFormModel.department.id": function (val, oldVal) {
      if (val != oldVal) {
        let This = this.bizFormModel;
        if (
          this.dialogProps.action == "add" ||
          this.dialogProps.action == "edit"
        ) {
          this.bizFormModel.doctor = {
            // 医生
            id: null,
            name: null,
          };
        }
        let doctor_search = {
          params: [
            {
              columnName: "department_id",
              queryType: "=",
              value: currentUser.department.id,
            },
          ],
        };
        // 字段对应表上filter条件
        doctor_search.params.push.apply(doctor_search.params, []);
        // 表有禁用字段
        doctor_search.params.push.apply(doctor_search.params, [
          { columnName: "is_locked", queryType: "=", value: "0" },
        ]);
        // 数据权限:  用户sys_user
        this.pushDataPermissions(
          doctor_search.params,
          this.$route.meta.routerId,
          "4004"
        );
        this.doctor_List.splice(0, this.doctor_List.length);
        listUserAll(doctor_search).then((responseData) => {
          this.doctor_List = responseData.data;
        });
      }
    },
  },
  mounted: async function () {
    this.$nextTick(() => {
      this.$on("openViewRegistrationDialog", function (registration) {
        this.dialogProps.action = "view";
        this.dialogProps.title = "查看登记信息";
        this.bizFormModel = { ...this.initFormModel(), ...registration };
        this.initOptions(this.bizFormModel);
        getPatientById(registration.patientId.id).then((responseData) => {
          this.PatientFormModel = responseData.data;
          this.bizFormModel = {
            ...this.bizFormModel,
            ...this.PatientFormModel,
          };
        });
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
      this.$on("openEditRegistrationDialog", async function (registration) {
        this.dialogProps.action = "edit";
        this.dialogProps.title = "修改登记信息";
        this.bizFormModel = { ...this.initFormModel(), ...registration };
        this.initOptions(this.bizFormModel);
        const responseData = await getPatientById(registration.patientId.id);
        this.PatientFormModel = responseData.data;

        var registerId = this.bizFormModel.id;

        this.bizFormModel = {
          ...this.bizFormModel,
          ...this.PatientFormModel,
        };
        this.bizFormModel.id = registerId;
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
      this.$on("openAddRegistrationDialog", function () {
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加登记信息";
        this.bizFormModel = this.initFormModel();
        this.PatientFormModel = this.initPatientModel();

        this.bizFormModel = {
          ...this.bizFormModel,
          ...this.PatientFormModel,
        };
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
      this.$on("openCopyRegistrationDialog", function (registration) {
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加登记信息";
        this.bizFormModel = { ...this.initFormModel(), ...registration };
        this.initOptions(this.bizFormModel);
        getPatientById(registration.patientId.id).then((responseData) => {
          this.PatientFormModel = responseData.data;
          this.bizFormModel = {
            ...this.bizFormModel,
            ...this.PatientFormModel,
          };
        });
        this.tabIndex = "1";
        this.bizFormModel.id = null; //把id设置为空，添加一个新的
        this.dialogProps.visible = true;
      });
    });
  },
};
</script>
<style lang="scss">
.ipt-box .el-input--mini {
  width: 70%;
}
.registrationForm {
  .el-dialog__body {
    padding: 0 10px;
  }
}
</style>
<style lang="scss" scoped>
.registrationItemBox {
  width: 100%;
  h3 {
    margin: 0 10px;
  }
}

#registrationTotalFee_Container {
  display: flex;
  justify-content: flex-start;
  padding: 20px;
  margin: 10px;
  background-color: rgba(207, 227, 236, 0.803921568627451);
  border-radius: 7px;
}

#registrationTotalFee_Title {
  font-family: "Arial Normal", "Arial", sans-serif;
  font-weight: 400;
  font-style: normal;
  font-size: 21px;
  margin-right: 8px;
}

#registrationTotalFee_Fee {
  font-weight: 800;
  font-style: black;
  font-size: 25px;
  margin-top: -4px;
}

.pay-registration-flex-start {
  display: flex;
  justify-content: flex-start;
  padding: 0 20px;
  margin: 10px;
}

.pay-registration-p {
  width: 80px;
  text-align: right;
  margin-right: 10px;
}

.pay-registration-content {
  widows: 200px;
  margin-top: 12px;
}
.ipt-box {
  display: flex;
  align-items: cente次/min;
}
</style>