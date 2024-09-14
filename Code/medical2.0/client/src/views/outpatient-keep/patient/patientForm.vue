<template>
  <el-dialog
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
      ref="patientForm"
      label-width="120px"
      label-position="right"
      class="edit-form"
    >
      <div class="tab-item" v-show="tabIndex == '1'">
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="患者姓名" prop="name">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.name"
                :maxlength="45"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入患者姓名'
                "
                autofocus
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="性别" prop="gender.value">
              <el-input
                v-if="dialogProps.action == 'view'"
                :disabled="true"
                v-model="bizFormModel.gender.name"
              ></el-input>
              <el-select
                v-else
                v-model="bizFormModel.gender"
                value-key="value"
                filterable
                clearable
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
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.age"
                :maxlength="45"
                :placeholder="dialogProps.action == 'view' ? '' : '请输入年龄'"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="月" prop="month">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.month"
                :maxlength="45"
                :placeholder="dialogProps.action == 'view' ? '' : '请输入月'"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.birthday"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入出生日期'
                "
                @change="birthdayChange"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="联系方式" prop="phone">
              <el-input
                :disabled="dialogProps.action == 'view'"
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
            <el-form-item label="身份证号" prop="card">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.card"
                :maxlength="45"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入身份证号'
                "
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="与患者关系" prop="withPatientNexus.value">
              <el-input
                v-if="dialogProps.action == 'view'"
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
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="健康卡号" prop="healthCardNo">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.healthCardNo"
                :maxlength="45"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入健康卡号'
                "
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="省" prop="province">
               <el-select
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.province"
                value-key="value"
                filterable
                clearable
                :placeholder="dialogProps.action == 'view' ? '' : '请选择省份'"
                @clear="
                  bizFormModel.province = '';
                  bizFormModel.city= '';
                  bizFormModel.area= '';
                  
                "
                @change="changeProvince"
              >
                <el-option
                  v-for="prov in province_List"
                  :key="prov.id"
                  :label="prov.name"
                  :value="prov.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="市" prop="city">
              <el-select
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.city"
                :maxlength="45"
                value-key="value"
                filterable
                clearable
                :placeholder="dialogProps.action == 'view' ? '' : '请选择市'"
                @clear="
                  bizFormModel.city= '';
                  bizFormModel.area= '';
                "
                @change="changeCity"
              >
                <el-option
                  v-for="city in city_List"
                  :key="city.id"
                  :label="city.name"
                  :value="city.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="区" prop="area">
              <el-select
                :disabled="dialogProps.action == 'view'"
                :maxlength="45"
                v-model="bizFormModel.area"
                value-key="value"
                filterable
                clearable
                :placeholder="dialogProps.action == 'view' ? '' : '请选择区县'"
                @clear="
                  bizFormModel.area= '';
                "
              >
                <el-option
                  v-for="area in area_List"
                  :key="area.id"
                  :label="area.name"
                  :value="area.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="详细地址" prop="address">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.address"
                :maxlength="45"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入详细地址'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
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
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button
        v-if="dialogProps.action != 'view'"
        type="primary"
        :disabled="flage"
        :plain="true"
        @click="onSubmit('patientForm')"
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
  </el-dialog>
</template>
<script>
import { validatenull } from "@/utils/validate";
import { listDictItemAll } from "@/api/sys/dictItem";
import {listAdministrativeDivisionAll} from "@/api/org/administrativeDivision";
import { savePatient } from "@/api/outpatient/patient";
import BaseUI from "@/views/components/baseUI";
import OperationIcon from "@/components/OperationIcon";
export default {
  extends: BaseUI,
  name: "patient-form",
  components: {
    OperationIcon,
  },
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: "1",
      gender_List: [], // 性别
      withPatientNexus_List: [], // 与患者关系
      flage:false,
      dialogProps: {
        visible: false,
        action: "",
        title: "",
      },
      formRules: {
        name: [{ required: true, message: "请输入患者姓名", trigger: "blur" }],
        "gender.value": [
          { required: true, message: "请选择性别", trigger: "change" },
        ],
        age: [{ required: true, message: "请输入年龄", trigger: "blur" }],
        month: [{ required: true, message: "请输入月", trigger: "blur" }],
        phone: [{ required: true, message: "请输入联系方式", trigger: "blur" }],
      },
      province_List:[], // 省
      city_List:[], //市
      area_List:[] //区
    };
  },
  props: {
    // 权限
    permission: {
      type: Object,
    },
  },
  methods: {
    changeProvince(){
      if(!this.bizFormModel.province) return;
      this.bizFormModel.city= '';
      this.bizFormModel.area= '';
      this.area_List = [];
      let division_search = {
        params: [
          {
            columnName: "parten_id",
            queryType: "=",
            value: this.bizFormModel.province,
          },
          {
            columnName: "level",
            queryType: "=",
            value: "2",
          }
        ],
      };

      listAdministrativeDivisionAll(division_search).then((responseData)=>{
        this.city_List = responseData.data;
      });
    },
    changeCity(){
      if(!this.bizFormModel.city) return;

      this.bizFormModel.area= '';
      let division_search = {
        params: [
          {
            columnName: "parten_id",
            queryType: "=",
            value: this.bizFormModel.city,
          },
          {
            columnName: "level",
            queryType: "=",
            value: "3",
          }
        ],
      };

      listAdministrativeDivisionAll(division_search).then((responseData)=>{
        this.area_List = responseData.data;
      });
    },
    birthdayChange(){
      if(!this.bizFormModel.birthday) return;
      const duration =  this.$moment.duration(this.$moment().diff(this.bizFormModel.birthday));
      this.bizFormModel.age = duration.years();
      this.bizFormModel.month = duration.months();
    },
    onSubmit(formName) {
      this.flage=true
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.doSave();
        } else {
          this.flage=false
          return false;
        }
      });
    },
    doSave() {
      this.setLoad();
      savePatient(this.bizFormModel)
        .then((responseData) => {
          this.flage=false
          if (responseData.code == 100) {
            this.dialogProps.visible = false;
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
      this.dialogProps.title = "修改患者信息";
      this.initOptions(this.bizFormModel);
    },
    onDialogClose() {
      this.dialogProps.visible = false;
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["patientForm"].clearValidate();
      });
    },
    initFormModel(This) {
      return {
        company: {
          // 诊所id
          id: currentUser.company.id,
          name: null,
        },
        name: "", // 患者姓名
        gender: {
          // 性别
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
    initOptions(This) {
      let division_search = {
        params: [
          {
            columnName: "parten_id",
            queryType: "=",
            value: "0",
          },
          {
            columnName: "level",
            queryType: "=",
            value: "1",
          }
        ],
      }

      listAdministrativeDivisionAll(division_search).then((responseData)=>{
        this.province_List = responseData.data;
      });

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
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        gender_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.gender_List.splice(0, this.gender_List.length);
      listDictItemAll(gender_search).then((responseData) => {
        this.gender_List = responseData.data;
      });
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
    },
  },
  watch: {},
  mounted: function () {
    this.$nextTick(() => {
      this.$on("openViewPatientDialog", function (patient) {
        this.dialogProps.action = "view";
        this.dialogProps.title = "查看患者信息";
        this.bizFormModel = { ...this.initFormModel(), ...patient };
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
      this.$on("openEditPatientDialog", function (patient) {
        this.dialogProps.action = "edit";
        this.dialogProps.title = "修改患者信息";
        this.bizFormModel = { ...this.initFormModel(), ...patient };
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
      this.$on("openAddPatientDialog", function () {
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加患者信息";
        this.bizFormModel = this.initFormModel();
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
      this.$on("openCopyPatientDialog", function (patient) {
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加患者信息";
        this.bizFormModel = { ...this.initFormModel(), ...patient };
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.bizFormModel.id = null; //把id设置为空，添加一个新的
        this.dialogProps.visible = true;
      });
    });
  },
};
</script>