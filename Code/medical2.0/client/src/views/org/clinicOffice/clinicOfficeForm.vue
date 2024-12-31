<template>
  <el-dialog
    :title="dialogProps.title"
    :visible.sync="dialogProps.visible"
    :close-on-click-modal="false"
    width="30%"
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
      ref="clinicOfficeForm"
      label-width="120px"
      label-position="right"
      class="edit-form"
      style="marginTop: 10px"
    >
      <div class="tab-item" v-show="tabIndex == '1'">
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="诊所" prop="company.id">
             <el-input
                v-if="isCompanyShow"
                :disabled="true"
                v-model="aa.name"
              ></el-input>
              <el-input
                v-if="dialogProps.action == 'view'"
                :disabled="true"
                v-model="bizFormModel.company.name"
              ></el-input>
              <el-select
                v-if="!aa"
                v-model="bizFormModel.company"
                value-key="id"
                filterable
                clearable
                placeholder="请选择所属诊所"

              >
                <el-option
                  v-for="company in company_List"
                  :key="company.id"
                  :label="company.name"
                  :value="company"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="科室编号" prop="code">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.code"
                :maxlength="64"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入科室编号'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="科室类别" prop="category">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.category"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入医保科室类别代码'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="科室名称" prop="name">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.name"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入科室名称'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="简介" prop="itro">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.itro"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入简介'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="科室负责人姓名" prop="dept_resper_name">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.dept_resper_name"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入负责人姓名'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="科室负责人电话" prop="dept_resper_tel">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.dept_resper_tel"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入负责人电话'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="科室成立日期" prop="dept_estbdat">
              <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.dept_estbdat'
                              type='datetime' value-format='yyyy-MM-dd HH:mm:ss'
                              :placeholder='dialogProps.action == "view"? "" : "请输入科室成立日期"'></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="批准床位数量" prop="aprv_bed_cnt">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.aprv_bed_cnt"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入批准数量'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="统筹区编号" prop="poolarea_no">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.poolarea_no"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入统筹区编号'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="医师人数" prop="dr_psncnt">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.dr_psncnt"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入医师人数'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="药师人数" prop="phar_psncnt">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.phar_psncnt"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入药师人数'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="护士人数" prop="nurs_psncnt">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.nurs_psncnt"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入护士人数'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="技师人数" prop="tecn_psncnt">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.tecn_psncnt"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入技师人数'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

<!--        <el-row>-->
<!--          <el-col :span="24 / 1">-->
<!--            <el-form-item label="地址" prop="address">-->
<!--              <el-input-->
<!--                :disabled="dialogProps.action == 'view'"-->
<!--                v-model="bizFormModel.address"-->
<!--                :maxlength="255"-->
<!--                :placeholder="dialogProps.action == 'view' ? '' : '请输入地址'"-->
<!--              ></el-input>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
<!--        </el-row>-->
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="是否为登记科室" prop="isRegister">
              <el-switch
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.isRegister"
                active-color="#13ce66"
                inactive-color="#dbdfe6"
                active-value="1"
                inactive-value="0"
              ></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="默认登记科室" prop="isDefault">
              <el-switch
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.isDefault"
                active-color="#13ce66"
                inactive-color="#dbdfe6"
                active-value="1"
                inactive-value="0"
              ></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="是否启用" prop="isLocked">
              <el-switch
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.isLocked"
                active-color="#13ce66"
                inactive-color="#dbdfe6"
                active-value="0"
                inactive-value="1"
              ></el-switch>
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
        @click="onSubmit('clinicOfficeForm')"
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
import { listClinicOfficeAll } from "@/api/org/clinicOffice";
import { saveClinicOffice } from "@/api/org/clinicOffice";
import BaseUI from "@/views/components/baseUI";
import OperationIcon from "@/components/OperationIcon";
import { getLocalCurrentCompany } from "@/utils/auth";
export default {
  extends: BaseUI,
  name: "clinicOffice-form",
  components: {
    OperationIcon,
  },
  data() {
    return {
      aa:{},
      isCompanyShow: false,
      bizFormModel: this.initFormModel(),
      tabIndex: "1",
      company_List: [], // 所属诊所
       flage:false,
      dialogProps: {
        visible: false,
        action: "",
        title: "",

      },
      formRules: {
        "company.id": [
          // { required: true, message: "请选择所属诊所", trigger: "change" },
        ],
        code: [{ required: true, message: "请输入科室编号", trigger: "blur" }],
        name: [{ required: true, message: "请输入科室名称", trigger: "blur" }],
        // category: [{ required: true, message: "请输入医保科室类别代码", trigger: "blur" }], // 科室类别
        // itro: [{ required: true, message: "请输入简介", trigger: "blur" }], // 简介
        // dept_resper_name: [{ required: true, message: "请输入负责人姓名", trigger: "blur" }], // 负责人
        // dept_resper_tel: [{ required: true, message: "请输入负责人电话", trigger: "blur" }], // 负责人电话
        // dept_estbdat: [{ required: true, message: "请输入成立时间", trigger: "blur" }], // 成立时间
        // aprv_bed_cnt: [{ required: true, message: "请输入批准床位数", trigger: "blur"}], // 批准床位数
        // poolarea_no: [{ required: true, message: "请输入统筹区编号", trigger: "blur" }], // 统筹区编号
        // dr_psncnt: [{ required: true, message: "请输入医生人数", trigger: "blur", type: 'number' }], // 医生人数
        // phar_psncnt: [{ required: true, message: "请输入药房人数", trigger: "blur", type: 'number' }], // 药房人数
        // nurs_psncnt: [{ required: true, message: "请输入护士人数", trigger: "blur", type: 'number' }], // 护士人数
        // tecn_psncnt: [{ required: true, message: "请输入技师人数", trigger: "blur", type: 'number' }] // 技师人数
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
    isCompany() {
      const company = getLocalCurrentCompany();
      console.log(company.id,'---');
       console.log(company.name,'---');
      if (company) {
        this.aa=company;
        this.bizFormModel.company={id:company.id,name:company.name};
        console.log(this.bizFormModel.company,'0000');
      //   this.$set(this.bizFormModel.company,'name',company.name);
        // this.bizFormModel.company.name = company.name;
        this.isCompanyShow = true;
      }
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
      saveClinicOffice({...this.bizFormModel,company:this.aa})
        .then((responseData) => {
          this.flage=false
          if (responseData.code == 100) {
            this.dialogProps.visible = false;
            this.$emit("save-finished","1");
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
      this.dialogProps.title = "修改科室";
      this.initOptions(this.bizFormModel);
    },
    onDialogClose() {
      this.dialogProps.visible = false;
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["clinicOfficeForm"].clearValidate();
      });
    },
    initFormModel(This) {
      return {
        company: {
          // 上级节点 所属诊所
          id: validatenull(This) || validatenull(This.id) ? null : This.id,
          name:
            validatenull(This) || validatenull(This.name) ? null : This.name,
        },
        code: "", // 科室编号
        isLocked: "0", // 禁用（0：未禁用；1：禁用）
        name: "", // 科室名称
        sort: "0", // 排序
        category: "", // 科室类别
        itro:"",//简介
        dept_resper_name: "",//负责人
        dept_resper_tel:"",//负责人电话
        dept_estbdat:"",//成立时间
        aprv_bed_cnt:"",//批准床位数
        poolarea_no:"",//统筹区编号
        dr_psncnt:0,//医生人数
        phar_psncnt:0,//药房人数
        nurs_psncnt:0,//护士人数
        tecn_psncnt:0//技师人数
        // address: "", // 地址
      };
    },
    initOptions(This) {
      let company_search = {
        params: [],
      };
      // 表有禁用字段
      company_search.params.push.apply(company_search.params, [
        { columnName: "is_locked", queryType: "=", value: "0" },
      ]);
      // 数据权限: 科室clinic_office
      this.pushDataPermissions(
        company_search.params,
        this.$route.meta.routerId,
        "1026680647570219053"
      );
      this.company_List.splice(0, this.company_List.length);
      listClinicOfficeAll(company_search).then((responseData) => {
        this.company_List = responseData.data;
      });
    },
  },
  watch: {},
  mounted: function () {

    this.$nextTick(() => {

      this.$on("openViewClinicOfficeDialog", function (clinicOffice) {
        this.dialogProps.action = "view";
        this.dialogProps.title = "查看科室";
        this.bizFormModel = { ...this.initFormModel(), ...clinicOffice };
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
      this.$on("openEditClinicOfficeDialog", function (clinicOffice) {
        this.dialogProps.action = "edit";
        this.dialogProps.title = "修改科室";
        this.bizFormModel = { ...this.initFormModel(), ...clinicOffice };
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
      this.$on("openAddClinicOfficeDialog", function () {
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加科室";
        this.bizFormModel = this.initFormModel();
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
      this.$on("openCopyClinicOfficeDialog", function (clinicOffice) {
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加科室";
        this.bizFormModel = { ...this.initFormModel(), ...clinicOffice };
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.bizFormModel.id = null; //把id设置为空，添加一个新的
        this.dialogProps.visible = true;
      });
       this.isCompany();
    });
  },
};
</script>
