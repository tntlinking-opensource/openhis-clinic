<template>
  <el-dialog
    class="beforehandForm"
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
      ref="beforehandForm"
      label-width="120px"
      label-position="right"
      style="margintop: 10px"
      class="edit-form"
    >
      <el-tabs
        v-model="activeName"
        type="border-card"
        @tab-click="handleClick"
        style="width: 100%; height: 100%"
      >
        <el-tab-pane label="体征信息" name="tzxx">
          <span slot="label">体征信息</span>
          <div class="registrationItemBox">
            <h3></h3>
            <el-row>
              <el-col :span="24 / 2">
                <el-form-item label="体温" prop="temperature">
                  <div class="ipt-box">
                    <el-input
                      v-model="bizFormModel.registration.temperature"
                      :maxlength="45"
                      :placeholder="'请输入体温'"
                    ></el-input>
                    <span>&nbsp;℃</span>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 2">
                <el-form-item label="脉搏" prop="pulse">
                  <div class="ipt-box">
                    <el-input
                      v-model="bizFormModel.registration.pulse"
                      :maxlength="45"
                      :placeholder="'请输入脉搏'"
                    ></el-input>
                    <span>&nbsp;次/min</span>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 2">
                <el-form-item label="呼吸" prop="breathe">
                  <div class="ipt-box">
                    <el-input
                      v-model="bizFormModel.registration.breathe"
                      :maxlength="45"
                      :placeholder="'请输入呼吸'"
                    ></el-input>
                    <span>&nbsp;次/min</span>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 2">
                <el-form-item label="血压" prop="bloodPressure">
                  <div class="ipt-box">
                    <el-input
                      v-model="bizFormModel.registration.bloodPressure"
                      :maxlength="45"
                      :placeholder="'请输入血压'"
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
                    v-model="bizFormModel.registration.isGoHigharea"
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
                    bizFormModel.registration.isGoHigharea === '1'
                      ? formRules.highArea
                      : [
                          {
                            required: false,
                          },
                        ]
                  "
                >
                  <el-input
                    :disabled="bizFormModel.registration.isGoHigharea === '0'"
                    v-model="bizFormModel.registration.highArea"
                    :maxlength="128"
                    :placeholder="'请输入疫区地点'"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="备注信息" prop="remarks">
                  <el-input
                    v-model="bizFormModel.registration.remarks"
                    type="textarea"
                    :maxlength="255"
                    :placeholder="'请输入备注信息'"
                    clearable
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
        <el-tab-pane label="病历信息" name="blxx">
            <span slot="label">病历信息</span>
            <el-form :label-position="labelPosition" label-width="80px" :model="bizFormModel.medicalRecord">
                <el-form-item label="医生" :required="true" prop="doctor">
            <el-select
              v-model="bizFormModel.medicalRecord.doctor"
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
  <el-form-item label="主诉" :required="true">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.patientTell"></el-input>
  </el-form-item>
  <el-form-item label="现病史">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.nowHistory"></el-input>
  </el-form-item>
  <el-form-item label="既往史">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.beforeHistory"></el-input>
  </el-form-item>
  <el-form-item label="个人史">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.personalHistory"></el-input>
  </el-form-item>
  <el-form-item label="过敏史">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.allergyHistory"></el-input>
  </el-form-item>
  <el-form-item label="疾病史">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.diseaseHistory"></el-input>
  </el-form-item>
  <el-form-item label="婚孕史">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.pregnancyHistory"></el-input>
  </el-form-item>
  <el-form-item label="传染病史">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.infectiousHistory"></el-input>
  </el-form-item>
  <el-form-item label="月经史">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.lunariaHistory"></el-input>
  </el-form-item>
  <el-form-item label="家族史">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.familyHistory"></el-input>
  </el-form-item>
  <el-form-item label="手术史">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.surgeryHistory"></el-input>
  </el-form-item>
  <el-form-item label="输血史">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.transfusionHistory"></el-input>
  </el-form-item>
  <el-form-item label="体格检查">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.physiqueCheck"></el-input>
  </el-form-item>
  <el-form-item label="辅助检查">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.assistCheck"></el-input>
  </el-form-item>
  <el-form-item label="急诊诊断">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.emergencyDiagnose"></el-input>
  </el-form-item>
  <el-form-item label="急诊效果">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.emergencyEffect"></el-input>
  </el-form-item>
  <el-form-item label="其他检查">
    <el-input type="textarea" v-model="bizFormModel.medicalRecord.otherCheck"></el-input>
  </el-form-item>
</el-form>
            </el-tab-pane>
      </el-tabs>
    </el-form>
    <div slot="footer">
      <el-button @click="onDialogclose">关 闭</el-button>
      <el-button type="primary" @click="buttoninser">确 认</el-button>
    </div>
  </el-dialog>
</template>

<script>
import BaseUI from "@/views/components/baseUI";
import { listDictItemAll } from "@/api/sys/dictItem";
import {
  saveRegistration,
  listDoctorsAll,
  listDoctorsAllnew,
  registationupdatenew,
} from "@/api/outpatient/registration";
export default {
  extends: BaseUI,
  name: "beforehand-form",
  data() {
    return {
      activeName: "tzxx",
      dialogProps: {
        visible: false,
        action: "",
        title: "",
        source: {
          name: "现场",
          value: "registrationSource_0",
        },
      },
      queryTypes: {
        department_id: "=",
        patient_id: "=",
        doctor_id: "=",
      },
      bizFormModel: {
          registration:{
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
          id: null,
          name: null,
        },
        id: null,
        pulse: null, // 脉搏
        breathe: null, // 呼吸
        temperature: null, // 体温
        isGoHigharea: "0", // 是否去过疫区
        highArea: null, // 疫区地点
        remarks: null, // 备注信息
        doctor:null,
        status:null,
          },
          medicalRecord:{
              doctor:'',
              patientTell:'',
          },
      },
      formRules: {
        highArea: [
          { required: true, message: "请输入疫区地点", trigger: "blur" },
        ],
      },
      labelPosition: 'right',
        blxxformLabelAlign: {
            doctor:'',
        },
        doctor_List: [], // 医生
        status_List: [], // 状态
    };
  },
  methods: {
    handleClick(tab, event) {
      //this.$message.success(this.tabPosition);

      if (tab.name == "tzxx") {

      } else if (tab.name == "schedulingmx") {


      } else if (tab.name == "schedulingzl") {

      }

    },
    buttoninser(){
        if(this.activeName=="tzxx" || this.activeName=="blxx"){
            if(this.bizFormModel.medicalRecord.patientTell!='' && this.bizFormModel.medicalRecord.patientTell!=null){
                if(this.bizFormModel.medicalRecord.doctor!='' && this.bizFormModel.medicalRecord.doctor!=null){
                    this.bizFormModel.registration.doctor=this.bizFormModel.medicalRecord.doctor;
                    this.bizFormModel.registration.status={name:"待就诊",value:"registrationStatus_0"};
                }
            }
            if(this.bizFormModel.medicalRecord.doctor==null && this.bizFormModel.medicalRecord.patientTell!=null){
                this.$message.warning("请选择医生!")
                return
            }
            if(this.bizFormModel.medicalRecord.doctor!=null && this.bizFormModel.medicalRecord.patientTell==null){
                this.$message.warning("请填写主诉!")
                return
            }

            registationupdatenew(this.bizFormModel)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.$emit('typeclick',"1");
            this.arrlistcreat();
            this.dialogProps.visible = false;

          } else {

            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.outputError(error);
        });
        }


    },
    initys() {
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
      // listDoctorsAllnew().then((responseData) => {
      //    // debugger
      //   //console.log(responseData,'医生');
      //   this.doctor_List = responseData.data;
      // })
    },
    arrlistcreat() {
      this.bizFormModel = {
          registration:{
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
          id: null,
          name: null,
        },

        pulse: "", // 脉搏
        breathe: "", // 呼吸
        temperature: "", // 体温
        isGoHigharea: "0", // 是否去过疫区
        highArea: "", // 疫区地点
        remarks: "", // 备注信息
          },
          medicalRecord:{},
      };
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["beforehandForm"].clearValidate();
      });
    },
    onDialogclose() {
      //this.$emit("typeclick");
      this.dialogProps.visible = false;
      this.arrlistcreat();
    },
  },
  mounted: async function () {
    this.$nextTick(() => {
      this.$on("openViewbeforehandDialog", function (item) {
        //debugger;
        this.arrlistcreat();
        this.dialogProps.action = "view";
        this.dialogProps.title = "预诊";

        this.bizFormModel.registration.id = item.id;
        console.log("this.bizFormModel.registration.id=="+this.bizFormModel.registration.id)
        this.bizFormModel.registration.patientId.id = item.patientId.id;
        console.log("this.bizFormModel.registration.patientId.id=="+this.bizFormModel.registration.patientId.id)
        this.bizFormModel.registration.patientId.name = item.patientId.name;
        console.log("this.bizFormModel.registration.patientId.name"+this.bizFormModel.registration.patientId.name)
        this.initys();
        console.log(this.bizFormModel);
        this.dialogProps.visible = true;
      });
    });
  },
};
</script>
<style lang="scss" scoped>
.ipt-box .el-input--mini {
  width: 70%;
}
.beforehandForm {
  .el-dialog__body {
    padding: 0 10px;
  }
}
</style>
