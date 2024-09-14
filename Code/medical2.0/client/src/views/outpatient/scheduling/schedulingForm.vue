<template>
  <el-dialog
    class="testForm"
    :title="dialogProps.title"
    :visible.sync="dialogProps.visible"
    :close-on-click-modal="false"
    width="50%"
    @open="onDialogOpen()"
    v-loading="loading"
  >

    <el-form
      :model="bizFormModel"
      :rules="formRules"
      ref="testForm"
      label-width="120px"
      label-position="right"
      style="marginTop: 10px"
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
              <el-form-item label="出生日期" prop="birthday">
                <el-date-picker
                  :disabled="dialogProps.action != 'add'"
                  v-model="bizFormModel.birthday"
                  type="date"
                  @change="birthdayChanges"
                  :picker-options="setDisabled"
                  value-format="yyyy-MM-dd"
                  :placeholder="
                    dialogProps.action == 'view' ? '' : '请输入出生日期'
                  "

                ></el-date-picker>

              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="年龄" prop="age">
                <el-col :span="24 / 2">
                  <el-input

                    v-model="bizFormModel.age"
                    :maxlength="45"
                    :placeholder="
                      dialogProps.action == 'view' ? '' : '年龄'
                    "
                  >
                    <template slot="append">岁</template>
                  </el-input>
                </el-col>
                <el-col :span="24 / 2">
                  <el-input
                    v-model="bizFormModel.month"
                    :maxlength="45"
                    :placeholder="dialogProps.action == 'view' ? '' : '月'"
                  >
                    <template slot="append">月</template>
                  </el-input>
                </el-col>
              </el-form-item>
            </el-col>

          </el-row>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="身份证号" prop="card">
                <el-input
                  :disabled="dialogProps.action != 'add'"
                  v-model="bizFormModel.card"
                  :maxlength="18"
                  :placeholder="
                    dialogProps.action == 'view' ? '' : '请输入身份证号'
                  "
                  @input="getBirthday"
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
            <el-col :span="24 / 2">
              <el-form-item label="监护人姓名" prop="guardianName">
                <el-input
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.guardianName"
                  :maxlength="45"
                  :placeholder="dialogProps.action == 'view' ? '' : '请输入监护人姓名'"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="监护人电话" prop="guardianPhone">
                <el-input
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.guardianPhone"
                  :maxlength="45"
                  :placeholder="dialogProps.action == 'view' ? '' : '请输入监护人电话'"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>

          </el-row>
        </div>
        <div class="registrationItemBox">
          <h3>登记信息</h3>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="科室" prop="clinicOffice.id">
                <el-input
                  v-if="dialogProps.action == 'view'||dialogProps.action == 'add'"
                  :disabled="true"
                  v-model="bizFormModel.clinicOffice.name"
                ></el-input>
                <el-cascader
                  v-else
                  v-model="bizFormModel.clinicOffice.id"
                  @change="sortDoc"
                  :options="department_List"
                  :key="key_department"
                  :disabled="bizFormModel.clinicOffice.id!=null?true:false"
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
                <el-input
                  v-if="dialogProps.action == 'view'||dialogProps.action == 'add'"
                  :disabled="true"
                  v-model="bizFormModel.doctor.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.doctor"
                  value-key="id"
                  :disabled="bizFormModel.doctor.id!=null?true:false"
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
                    v-for="doctor in this.select_doctor"
                    :key="doctor.id"
                    :label="doctor.name"
                    :value="doctor"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>

          </el-row>
          <el-row>


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
                  <span>&nbsp;℃</span>
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
                  <span>&nbsp;次/min</span>
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
                  <span>&nbsp;次/min</span>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="血压" prop="bloodPressure">
                <div class="ipt-box">
                  <el-input
                    :disabled="dialogProps.action == 'view'"
                    v-model="bizFormModel.bloodPressure"
                    :maxlength="45"
                    :placeholder="
                      dialogProps.action == 'view' ? '' : '请输入血压'
                    "
                  ></el-input>
                  <span>&nbsp;mmHg</span>
                </div>
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
            <el-col :span="24 / 2">
              <el-form-item
                label="预约日期" prop="subscribedaterq"
              >
                <el-input
                  :disabled="true"
                  v-model="bizFormModel.subscribedaterq"
                  :maxlength="45"
                  :placeholder="dialogProps.action == 'view' ? '' : '请输入预约日期'"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="预约时间" prop="subscribedatetime">
                <el-time-select
                  v-model="bizFormModel.subscribedatetime"
                  :picker-options="{
                    start: listarry.streatime,
                    step: '00:30',
                    end: listarry.endtime
                  }"
                  placeholder="选择时间">
                </el-time-select>
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
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' type='primary' :disabled="flage" :plain='true'
                 @click='onSubmit("testForm")'>保 存
      </el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>

    <!--收费支付弹框 开始-->
    <el-dialog
      title="收费"
      :visible.sync="payRegistrationFeeVisible"
      width="40%"
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
            >{{ ptype.name }}
            </el-radio
            >
          </el-radio-group>
        </div>
        <div class="pay-registration-flex-start">
          <p class="pay-registration-p">备注</p>
          <el-input
            type="textarea"
            :rows="3"
            v-model="bizFormModel.practicalRegistrationPayRemarks"
            class="pay-registration-content"
          ></el-input>
        </div>
        <el-divider></el-divider>
      </div>
      <div slot="footer">
        <el-button @click="payRegistrationFeeVisible = false">取 消</el-button>
        <el-button type="primary" @click="payRegistrationFeeClick"
        >确认收费
        </el-button>
      </div>
    </el-dialog>
    <!--收费支付弹框 结束-->
  </el-dialog>
</template>

<script>

import BaseUI from "@/views/components/baseUI";
import {saveTollInfo} from "@/api/toll/tollInfo";
import {validatenull} from "@/utils/validate";
import {getPatientById, listPatientAll, savePatient,} from "@/api/outpatient/patient";
import {listDictItemAll} from "@/api/sys/dictItem";
import {listDoctorsAll, saveRegistration} from "@/api/outpatient/registration";

export default {
  extends: BaseUI,
  name: "test-form",
  components: {},
  data() {
    //身份证校验
    const isCnNewID = (rule, value, callback) => {
      var arrExp = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];//加权因子
      var arrValid = [1, 0, "X", 9, 8, 7, 6, 5, 4, 3, 2];//校验码
      if (/^\d{17}\d|x$/i.test(value)) {
        var sum = 0, idx;
        for (var i = 0; i < value.length - 1; i++) {
          // 对前17位数字与权值乘积求和
          sum += parseInt(value.substr(i, 1), 10) * arrExp[i];
        }
        // 计算模（固定算法）
        idx = sum % 11;
        // 检验第18为是否与校验码相等
        if (arrValid[idx] == value.substr(17, 1).toUpperCase()) {
          callback()
        } else {
          callback("身份证格式有误")
        }
      } else {
        callback("身份证格式有误")
      }
    }
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
      bizFormModel: {...this.initFormModel(), ...this.initPatientModel()},
      PatientFormModel: this.initPatientModel(),
      tabIndex: "1",
      listarry: {streatime: '', endtime: ''},//父级传值接收使用
      flage: false,//防止重复提交
      gender_List: [], // 性别
      withPatientNexus_List: [], // 与患者关系
      key_department: 1, // el-cascader key
      department_List: [], // 科室
      patientId_List: [], // 患者
      doctor_List: [], // 医生
      select_doctor: [],
      treatType_List: [], // 治疗类型
      source_List: [], // 来源
      payType_List: [], // 支付方式
      status_List: [], // 状态
      dialogProps: {
        visible: false,
        action: '',
        title: '',
        source: {
          name: '现场',
          value: 'registrationSource_0'
        }
      },
      formRules: {
        registrationFee: [
          {required: true, message: "请输入挂号费", trigger: "blur"},
        ],
        highArea: [
          {required: true, message: "请输入疫区地点", trigger: "blur"},
        ],
        "doctor.name": [
          {required: true, message: "请选择医生", trigger: "change"},
        ],
        "birthday": [
          {required: true, message: "请选择出生日期", trigger: "change"},
        ],
        //phone: [{ required: false, message: "请输入联系方式", trigger: "blur" }],
        age: [{required: true, message: "请输入年龄", trigger: "blur"}],
        name: [{required: true, message: "请输入姓名", trigger: "change"}],
        "gender.name": [
          {required: true, message: "请输入性别", trigger: "change"},
        ],
        guardianName: [{required: false, message: "请输入监护人姓名", trigger: "change"},],
        guardianPhone: [{required: false, message: "请输入监护人电话", trigger: "change"},],
        "subscribedatetime": [
          {required: true, message: "请选择预约时间", trigger: "change"},
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
    getBirthday() {

      if (this.bizFormModel.card.length == 18) {

        let birthDay = this.bizFormModel.card.substring(6, 14)
        console.log(birthDay, '获取身份证号');
        let year = birthDay.substring(0, 4)
        let month = birthDay.substring(4, 6)
        let day = birthDay.substring(6)
        let newBirthday = year + "-" + month + "-" + day
        console.log(newBirthday, '获取身份证号');
        this.bizFormModel.birthday = newBirthday
        this.birthdayChanges()
      } else if (this.bizFormModel.card.length == 0) {
        this.bizFormModel.birthday = ""
        this.birthdayChanges()
      }
    },
    sortDoc(clinicOffice, officename) {
      console.log(clinicOffice, '........');
      if (this.doctor_List.length != 0) {
        //过滤医生
        this.select_doctor = this.doctor_List.filter(doctor => doctor.office == clinicOffice)
        console.log(this.select_doctor, '.......');
      }
    },
    GetPatientInfo() {
      //挂号id不能被覆盖
      var id = this.bizFormModel.id;
      if (this.bizFormModel.name.id) {
        this.PatientFormModel = this.bizFormModel.name;
        this.bizFormModel = {...this.bizFormModel, ...this.PatientFormModel};
      } else {
        this.PatientFormModel = {
          ...this.initPatientModel(),
          name: this.bizFormModel.name,
        };
        this.bizFormModel = {...this.bizFormModel, ...this.PatientFormModel};
      }
      this.bizFormModel.id = id;
    },
    savePatient() {
      this.flage = false
      var registerId = this.bizFormModel.id;
      this.bizFormModel.id = this.PatientFormModel.id;
      console.log(this.bizFormModel.id, 'dayinid');
      if (this.bizFormModel.id != undefined) {
        this.bizFormModel.patientId = {
          id: this.bizFormModel.id,
          name: this.PatientFormModel.name,
        };
        this.bizFormModel.id = registerId;

        this.doSave();
        return;
      }

      savePatient(this.bizFormModel)
        .then((responseData) => {
          this.flage = false
          if (responseData.code == 100) {
            this.bizFormModel.patientId = {
              id: responseData.data,
              name: this.PatientFormModel.name,
            };
            this.bizFormModel.id = registerId;
            // console.log(registerId,'dayinid');
            this.doSave();
          } else {
            this.flage = false
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.flage = false
          this.outputError(error);
        });
    },
    payRegistrationFeeClick() {
      this.savePatient();
    },
    onSubmit(formName) {
      this.flage = true
      for (let i = 0; i < this.treatType_List.length; i++) {
        if (this.bizFormModel.treatType.name == this.treatType_List[i].name) {
          this.bizFormModel.treatType.value = this.treatType_List[i].value
        }

      }
      this.bizFormModel.subscribeDate = this.bizFormModel.subscribedaterq + " " + this.bizFormModel.subscribedatetime;
      this.$refs[formName].validate(valid => {
        if (valid) {
          if (
            this.bizFormModel.freeRegistrationFee == 1
          ) {
            this.savePatient();
          } else {
            this.flage = false
            if (this.bizFormModel.registrationFee == 0) {
              this.$message({
                showClose: true,
                message: '请填写挂号费',
                type: 'error'
              });
              return
            }

            if (this.payType_List.length > 0) {
              this.bizFormModel.payType.value = this.payType_List[0].value;
            }
            this.bizFormModel.practicleRegistrationFee =
              this.bizFormModel.registrationFee;
            this.bizFormModel.practicalRegistrationPayRemarks = "";
            this.payRegistrationFeeVisible = true;
          }
        } else {
          this.flage = false
          return false

        }
      });
    },
    doSave() {
      this.flage = false
      this.setLoad();
      if (this.bizFormModel.source.value == "registrationSourc_1") {
        this.bizFormModel.status.value = "registrationStatus_3";
        this.bizFormModel.status.name = "待签到";
      }
      this.payType_List.forEach(item => {
        if (item.value == this.bizFormModel.payType.value) {
          this.bizFormModel.payType.name = item.name
        }
      })
      console.log(this.bizFormModel, 'zuihou');
      let toll = {
        company: this.bizFormModel.company,
        registrationFeeId: '',
        amountReceivable: this.bizFormModel.registrationFee, //应收应退
        amountReceived: this.bizFormModel.registrationFee,  //实收
        paymentType: this.bizFormModel.payType,//支付类型
        patient: this.bizFormModel.patientId,
        amountStatus: {
          name: '已收费',
          value: 'amountStatus_1'
        },    //单据状态
        tollType: {
          name: '挂号',
          value: 'tollType_4'
        },    //收费类型
        remarks: this.bizFormModel.remarks   //备注信息
      }

      saveRegistration(this.bizFormModel)
        .then((responseData) => {
          this.flage = false
          if (responseData.code == 100) {
            this.dialogProps.visible = false;
            this.payRegistrationFeeVisible = false;
            //保存成功后，如果存在挂号费，在收费表中插入数据
            if (this.bizFormModel.registrationFee > 0) {
              console.log(toll, 'kanwo');
              toll.registrationFeeId = responseData.data
              //在收费表中插入数据
              saveTollInfo(toll).then((res) => {
                if (res.code == "100") {

                }
              }).catch(() => {
              })
            }
            setTimeout(() => {
              this.$emit("save-finished");
            }, 1000);
          } else {

            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.flage = false
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
        this.$refs["testForm"].clearValidate();
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
        clinicOffice: {
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
          value: 'registrationSource_0',
          name: '现场',
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
          value: "registrationStatus_3",
          name: "待签到",
        },
        dispensingStatus: "0", // 发药状态
        dispensingDate: "", // 发/退药时间
        chargeStatus: "0", // 收费状态
        chargeDate: "", // 收费时间
        subscribeDate: "",//预约日期
        subscribedaterq: "",//显示用 不传入后台 拼接预约日期
        subscribedatetime: "",//预约时间 传入后台前拼接到预约日期字段里 这里只做显示和绑定使用
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
          {columnName: "is_locked", queryType: "=", value: "0"},
          {columnName: "is_register", queryType: "=", value: "1"},
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
      this.department_List.splice(0, this.department_List.length);//查询科室请求已放入查询医生请求中
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
        {columnName: "is_locked", queryType: "=", value: "0"},
      ]);
      // 数据权限:  用户sys_user
      this.pushDataPermissions(
        doctor_search.params,
        this.$route.meta.routerId,
        "4004"
      );
      // this.doctor_List.splice(0, this.doctor_List.length);
      // listDoctorsAll().then((responseData) => {
      //   console.log(responseData,'医生');
      //   this.doctor_List = responseData.data;
      //    listClinicOfficeAll(department_search).then((responseData) => {
      //   this.department_List = responseData.data;
      //   for (let i = 0; i < this.department_List.length; i++) {
      //     if(this.department_List[i].isDefault=='1'){
      //       this.bizFormModel.clinicOffice.id=this.department_List[i].id
      //        this.sortDoc(this.department_List[i].id)
      //     }
      //   }
      // });

      // });

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
    //自动计算年龄事件
    birthdayChanges() {

      if (!this.bizFormModel.birthday) {
        this.bizFormModel.age = "";
        this.bizFormModel.month = ""
        return
      }
      ;
      const duration = this.$moment.duration(this.$moment().diff(this.bizFormModel.birthday));
      console.log(duration, '年龄');
      this.bizFormModel.age = duration.years();
      this.bizFormModel.month = duration.months();
      if (this.bizFormModel.age < 12) {
        this.formRules.guardianName[0].required = true
        this.formRules.guardianPhone[0].required = true
        //this.formRules.phone[0].required=false

      } else {
        this.formRules.guardianName[0].required = true
        this.formRules.guardianPhone[0].required = true
        this.$refs["testForm"].clearValidate();
        //this.formRules.phone[0].required=true
      }
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
          {columnName: "is_locked", queryType: "=", value: "0"},
        ]);
        // 数据权限:  用户sys_user
        this.pushDataPermissions(
          doctor_search.params,
          this.$route.meta.routerId,
          "4004"
        );
        this.doctor_List.splice(0, this.doctor_List.length);
        listDoctorsAll().then((responseData) => {
          this.doctor_List = responseData.data;
        });
      }
    },
    "bizFormModel.age": function (newValue, oldValue) {
      if (newValue < 12) {
        this.formRules.guardianName[0].required = true
        this.formRules.guardianPhone[0].required = true
        //this.formRules.phone[0].required=false

      } else {
        this.formRules.guardianName[0].required = false
        this.formRules.guardianPhone[0].required = false
        this.$refs["testForm"].clearValidate();
        //this.formRules.phone[0].required=true
      }
    }

  },
  mounted: async function () {
    this.$nextTick(() => {
      this.$on("openViewRegistrationDialog", function (registration) {
        this.dialogProps.action = "view";
        this.dialogProps.title = "查看登记信息";
        this.bizFormModel = {...this.initFormModel(), ...registration};
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
        this.bizFormModel = {...this.initFormModel(), ...registration};
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
      this.$on("openAddRegistrationDialog", function (rowdata) {
        this.listarry = [];
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加登记信息";
        this.bizFormModel = this.initFormModel();

        this.PatientFormModel = this.initPatientModel();

        this.bizFormModel = {
          ...this.bizFormModel,
          ...this.PatientFormModel,
        };
        this.initOptions(this.bizFormModel);


        this.bizFormModel.treatType.name = "初诊"
        this.bizFormModel.subscribedaterq = rowdata.fn;
        this.bizFormModel.doctor.id = rowdata.userid;
        this.bizFormModel.doctor.name = rowdata.username;
        this.bizFormModel.clinicOffice.id = rowdata.ksid;
        this.bizFormModel.clinicOffice.name = rowdata.ksname;
        console.log(this.bizFormModel, '开始');
        this.tabIndex = "1";
        this.dialogProps.visible = true;

        let myDate = new Date();
        let nowDate = new Date(Date.parse(new Date()));
        let nowDay = nowDate.getDate(); //日
        let nowmonth = nowDate.getMonth() + 1; //月
        let yeare = nowDate.getFullYear(); //年
        let hou = String(myDate.getHours() < 10 ? '0' + myDate.getHours() : myDate.getHours())
        let min = String(myDate.getMinutes() < 10 ? '0' + myDate.getMinutes() : myDate.getMinutes())
        let hhmm = hou + ':' + min;
        let hh = myDate.getHours() >= rowdata.timestreaint ? hou : rowdata.timestreaint;
        let hhflgs = myDate.getMinutes() >= 30 ? true : false;
        if (Date.parse(rowdata.fn) == Date.parse(rowdata.thecurrent)) {
          if (hhflgs) {
            hhmm = String(myDate.getHours() + 1 < 10 ? '0' + myDate.getHours() + 1 : myDate.getHours() + 1);
            hhmm += ':00';
          } else {
            hhmm = hh + ':30';
          }
        } else {
          hhmm = rowdata.timestrea
        }
        this.listarry.streatime = hhmm;
        this.listarry.endtime = rowdata.timeend;
        console.log("初始化数据：");
        console.log(this.bizFormModel);
        console.log(rowdata);

      });
      this.$on("openCopyRegistrationDialog", function (registration) {
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加登记信息";
        this.bizFormModel = {...this.initFormModel(), ...registration};
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
}
</script>
<style lang="scss" scoped>
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
  margin: 10px;
}

.pay-registration-p {
  width: 80px;
  text-align: left;
  margin-right: 10px;
}

.pay-registration-content {
  widows: 200px;
  margin-top: 12px;
}

.ipt-box {
  display: flex;
  align-items: center;
}

/deep/ .el-dialog__body {
  padding: 0 7px;
  margin: 0
}
</style>
