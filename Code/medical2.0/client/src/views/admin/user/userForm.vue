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
      ref="userForm"
      label-width="120px"
      style="marginTop: 10px"
      label-position="right"
      class="edit-form"
    >
      <div class="tab-item" v-show="tabIndex == '1'">
        <!-- 主表单  开始-->

        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="姓名" prop="name">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.name"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入用户名'
                "
              ></el-input>
            </el-form-item>
          </el-col>
          <!-- <el-col :span="24 / 2">
            <el-form-item label="性别" prop="userExt.sex">
              <el-radio v-model="bizFormModel.userExt.sex" label="1"
                >男</el-radio
              >
              <el-radio v-model="bizFormModel.userExt.sex" label="2"
                >女</el-radio
              >
            </el-form-item>
          </el-col> -->
        </el-row>
        <el-row>
          <!-- <el-col :span="24 / 2">
            <el-form-item label="民族" prop="userExt.nation">
              <el-input
                v-model="bizFormModel.userExt.nation"
                :maxlength="128"
                :placeholder="'请输入民族'"
              ></el-input>
            </el-form-item>
          </el-col> -->
          <el-col :span="24 / 2">
            <el-form-item label="手机号" prop="phone">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.phone"
                :maxlength="11"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入手机号'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <!-- <el-col :span="24 / 2">
            <el-form-item label="民族" prop="userExt.nation">
              <el-input
                v-model="bizFormModel.userExt.nation"
                :maxlength="128"
                :placeholder="'请输入民族'"
              ></el-input>
            </el-form-item>
          </el-col> -->
          <el-col :span="24 / 2">
            <el-form-item label="执业编码" prop="pracPsnCode">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.userExt.pracPsnCode"
                :maxlength="11"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入执业编码'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <!-- <el-col :span="24 / 2">
            <el-form-item label="出生日期" prop="userExt.birthday">
              <el-date-picker
                v-model="bizFormModel.userExt.birthday"
                type="date"
                placeholder="选择日期"
              >
              </el-date-picker>
            </el-form-item>
          </el-col> -->
          <el-col :span="24 / 2">
            <el-form-item label="上传头像" prop="userHeader">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :on-change="handleLicensePreview"
                :before-upload="beforeLicenseUpload"
                :auto-upload="false"
              >
                <img
                  v-if="bizFormModel.userHeader"
                  :src="bizFormModel.userHeader"
                  class="avatar"
                />
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="证件类型" prop="userExt.creditType">
              <el-select
                v-model="bizFormModel.userExt.creditType"
                placeholder="请选择证件类型"
              >
                <el-option label="身份证" value="身份证"> </el-option>
                <el-option label="驾驶证" value="驾驶证"> </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="证件号码" prop="userExt.creditNum">
              <el-input
                v-model="bizFormModel.userExt.creditNum"
                :maxlength="18"
                :placeholder="'请输入证件号码'"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="科室" prop="userExt.office">
              <!-- <el-select
                v-model="bizFormModel.userExt.office"
                placeholder="请选择科室"
              >
                <el-option label="科室1" value="科室1"> </el-option>
                <el-option label="科室2" value="科室2"> </el-option>
              </el-select> -->

              <el-select
                v-if="!aa"
                v-model="bizFormModel.userExt.office"
                value-key="id"
                filterable
                clearable
                placeholder="请选择所属诊所"
              >
                <el-option
                  v-for="company in department_List"
                  :key="company.id"
                  :label="company.name"
                  :value="company.name"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="职位" prop="userExt.post">
              <!-- <el-select
                v-model="bizFormModel.userExt.post"
                placeholder="请选择职位"
              >
                <el-option label="职位1" value="职位1"> </el-option>
                <el-option label="职位2" value="职位2"> </el-option>
              </el-select> -->
              <el-input
                v-model="bizFormModel.userExt.post"
                :placeholder="'请输入职位'"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-form-item label="类别" prop="userExt.jobType">
              <el-radio v-model="bizFormModel.userExt.jobType" label="医生"
                >医生</el-radio
              >
              <el-radio v-model="bizFormModel.userExt.jobType" label="护士"
                >护士</el-radio
              >
              <el-radio v-model="bizFormModel.userExt.jobType" label="药师"
                >药师</el-radio
              >
              <el-radio v-model="bizFormModel.userExt.jobType" label="其它"
                >其它</el-radio
              >
            </el-form-item>
          </el-col>
        </el-row>

  <!--子表：   用户角色 开始-->
        <el-row>
          <el-col>
            <el-form-item label="用户角色" prop="userRoleList">
              <el-select
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.userRoleList"
                value-key="role.id"
                multiple
                clearable
                filterable
                placeholder="请选择角色标识"
              >
                <el-option
                  v-for="role in role_UserRole_List"
                  :key="role.id"
                  :label="role.name"
                  :value="{ id: '', role: role }"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <!--子表：   用户角色 结束-->

        <el-row>
          <el-col :span="24 / 2">
            <el-form-item
              label="本诊所开始执业时间"
              prop="userExt.startWorkTime"
            >
              <el-date-picker
                v-model="bizFormModel.userExt.startWorkTime"
                type="date"
                format="yyyy-MM-dd"
                placeholder="开始时间"
                :picker-options="starttime"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="本诊所结束执业时间" prop="userExt.endWorkTime">
              <el-date-picker
                v-model="bizFormModel.userExt.endWorkTime"
                type="date"
                format="yyyy-MM-dd"
                placeholder="结束时间"
                :picker-options="endTime"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="登录名" prop="phone">
              <el-input
                disabled="false"
                v-model="bizFormModel.phone"
                :maxlength="10"
                :placeholder="dialogProps.action == 'view' ? '' : ''"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item
              label="修改密码"
              prop="loginPasswordUpdate"
              v-if="dialogProps.action == 'edit'"
            >
              <el-checkbox v-model="bizFormModel.loginPasswordUpdate"
                >修改密码</el-checkbox
              >
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item
              label="密码"
              prop="loginPassword"
              v-if="
                dialogProps.action != 'view' &&
                dialogProps.action != 'add' &&
                bizFormModel.loginPasswordUpdate == true
              "
            >
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.loginPassword"
                type="password"
                auto-complete="new-password"
                :maxlength="64"
                :placeholder="dialogProps.action == 'view' ? '' : '请输入密码'"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item
              label="确认密码"
              prop="loginPasswordConfirm"
              v-if="
                dialogProps.action != 'view' &&
                dialogProps.action != 'add' &&
                bizFormModel.loginPasswordUpdate == true
              "
            >
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.loginPasswordConfirm"
                type="password"
                :maxlength="64"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入确认密码'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="是否启用" prop="isLocked">
              <el-switch
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.isLocked"
                active-color="#13ce66"
                inactive-color="#dbdfe6"
                :active-value="0"
                :inactive-value="1"
              ></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="邮箱" prop="email">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.email"
                :maxlength="100"
                :placeholder="dialogProps.action == 'view' ? '' : '请输入邮箱'"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="是否在职" prop="userExt.isDuty">
              <el-radio v-model="bizFormModel.userExt.isDuty" label="0"
                >否</el-radio
              >
              <el-radio v-model="bizFormModel.userExt.isDuty" label="1"
                >是</el-radio
              >
            </el-form-item>
          </el-col>
        </el-row> -->
        <el-row>
          <el-col>
            <el-form-item label="介绍" prop="userExt.description">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.userExt.description"
                type="textarea"
                :maxlength="255"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入介绍信息'
                "
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 主表单  结束-->

      </div>
    </el-form>

    <!-- 按钮  开始-->
    <span slot="footer" class="dialog-footer">
      <el-button
        v-if="dialogProps.action != 'view'"
        type="primary"
        :disabled="flage"
        :plain="true"
        @click="onSubmit('userForm')"
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
    <!-- 按钮 结束-->
  </el-dialog>
</template>

<script>
import { validatenull } from "@/utils/validate";
import { listClinicOfficeAll } from "@/api/org/clinicOffice";
import { getPhotoById } from "@/api/sys/sysSeting";
import { treeCompany } from "@/api/org/company";
import { treeDepartment } from "@/api/org/department";
import { listRoleAll } from "@/api/admin/role";
import { saveUser, updateUser, changemylist } from "@/api/admin/user";
import BaseUI from "@/views/components/baseUI";
import OperationIcon from "@/components/OperationIcon";
export default {
  extends: BaseUI,
  name: "user-form",
  components: {
    OperationIcon,
  },
  data() {
    return {
      photoId: null, // 头像id 查看或修改会给它赋值
      bizFormModel: this.initFormModel(),
      tabIndex: "1",
      flage:false,
      key_department: 1, // el-cascader key
      department_List: [], // 部门

      role_UserRole_List: [], // 角色标识

      dialogProps: {
        visible: false,
        action: "",
        title: "",
      },
      formRules: {
        // 主表验证
        "company.id": [
          { required: true, message: "请选择公司", trigger: "change" },
        ],
        "department.id": [
          { required: false, message: "请选择部门", trigger: "change" },
        ],
        // "userExt.creditNum": [
        //   { required: true, message: "请输入证件号码", trigger: "blur" },

        //   {
        //     min: 18,
        //     max: 18,
        //     message: "请输入18位身份证号码",
        //     trigger: "blur",
        //   },
        // ],
        // "userExt.nation": [
        //   { required: true, message: "请输入民族", trigger: "blur" },
        // ],
        // "userExt.birthday": [
        //   { required: true, message: "请选择出生日期", trigger: "change" },
        // ],
        // "userExt.startWorkTime": [
        //   {
        //     required: true,
        //     message: "请选择开始日期",
        //     trigger: "blur",
        //   },
        // ],
        // "userExt.endWorkTime": [
        //   {
        //     required: true,
        //     message: "请选择结束日期",
        //     trigger: "blur",
        //   },
        // ],
        // "userExt.creditType": [
        //   { required: true, message: "请选择证件类型", trigger: "change" },
        // ],
        "userExt.post": [
          { required: true, message: "请选择职位", trigger: "blur" },
        ],
        "userExt.office": [
          { required: true, message: "请选择科室", trigger: "change" },
        ],
        // "userExt.sex": [
        //   { required: true, message: "请选择性别", trigger: "change" },
        // ],
        // "userExt.isDuty": [
        //   { required: true, message: "请选择是否在职", trigger: "change" },
        // ],
        "userExt.jobType": [
          { required: true, message: "请选择类别", trigger: "change" },
        ],
        name: [{ required: true, message: "请输入用户名", trigger: "blur" }],
        loginName: [
          { required: true, message: "请输入登录名", trigger: "blur" },
        ],
        loginPassword: [
          { required: true, message: "请输入密码", trigger: "blur" },
        ],
        // userHeader: [{ required: true, message: "上传头像", trigger: "blur" }],
        loginPasswordConfirm: [
          { required: true, message: "请输入确认密码", trigger: "blur" },
          {
            validator: (rule, value, callback) => {
              console.log(this.bizFormModel.loginPassword);
              if (value !== this.bizFormModel.loginPassword) {
                callback(new Error("两次输入密码不一致!"));
              } else {
                callback();
              }
            },
            trigger: "blur",
          },
        ],
        phone: [
          { required: true, message: "请输入手机号", trigger: "blur" },
          { min: 11, max: 11, message: "请输入11位手机号码", trigger: "blur" },
          {
            pattern:
          // /^1(3[0-9]|4[01456879]|5[0-3,5-9]|6[2567]|7[0-8]|8[0-9]|9[0-3,5-9])d{8}$/,
          // /^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\d{8}$/,
            /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/,
          message: "请输入正确的手机号码",
          },
        ],
        // email: [{ required: true, message: "请输入邮箱", trigger: "blur" }],
        // 子表验证 用户角色
        userRoleList: [
          { required: true, message: "请选择角色标识", trigger: "change" },
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
  computed: {
    ...Vuex.mapGetters(["settings"]),
    tabOptionBtnTop() {
      let top;
      switch (this.settings.size) {
        case "medium":
          top = -46 + "px";
          break;
        case "small":
          top = -42 + "px";
          break;
        case "mini":
          top = -38 + "px";
          break;
        default:
          top = -50 + "px";
      }
      return top;
    },
  },
  methods: {
    // 上传图片
    handleLicensePreview(file) {
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        this.$message.error(this.inter.sctpdx + " 2MB!");
      } else {
        // 存储图片，点击确认按钮时统一上传

        this.bizFormModel.userHeader = URL.createObjectURL(file.raw);
        console.log(this.bizFormModel.userHeader);
        console.log(file.raw);
        this.bizFormModel.fileIdFile = file.raw;
      }
    },

    //图片上传限制
    beforeLicenseUpload(file) {
      const fileSuffix = file.name.substring(file.name.lastIndexOf(".") + 1);
      const whiteList = ["jpg", "jpeg", "png", "gif", "JPG", "JPEG"];

      if (whiteList.indexOf(fileSuffix) === -1) {
        this.$message.error(this.inter.scyyzz);
        return false;
      }
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        this.$message.error(this.inter.sctpdx + " 2MB!");
        return false;
      }
    },

    onSubmit(formName) {
      this.flage=true
      this.$refs[formName].validate((valid, object) => {
        if (valid) {
          if (this.dialogProps.action == "edit") {
            this.doUpdate();
          } else {
            this.doSave();
          }
        } else {
          this.flage=false
          // 处理校验定位
          let arr = [];
          let numArr = [];
          for (let i in object) {
            let dom = this.$refs[i];
            if (!dom) {
              this.tabIndex = 1;
              return;
            } else {
              arr.push(dom);
              if (Object.prototype.toString.call(arr) !== "[object Object]") {
                arr.forEach((item) => {
                  numArr.push(item.$el.dataset.num);
                });
                this.tabIndex = Math.min(...numArr);
              }
            }
          }
          return false;
        }
      });
    },

    createForme() {
      this.bizFormModel.loginName = this.bizFormModel.phone;
      //如果是新增，密码默认手机号最后6位
      if(this.dialogProps.action == "add"){
        this.bizFormModel.loginPassword = this.bizFormModel.phone.substring((this.bizFormModel.phone.length-6),this.bizFormModel.phone.length)
      }
      let userHeaderFile = new FormData();
      userHeaderFile.append("fileIdUploads", this.bizFormModel.fileIdFile);
      userHeaderFile.append("entity", JSON.stringify(this.bizFormModel));
      userHeaderFile.append("deleteIds",JSON.stringify([this.photoId]));
      return userHeaderFile;
    },
    doSave() {
      this.setLoad();

      //
      // saveUser({
      //   ...this.bizFormModel,
      //   loginName: this.bizFormModel.phone,

      // })
      saveUser(this.createForme())
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
    doUpdate() {
      // console.log(this.createForme(),'aghaskgjal');
      // return
      this.setLoad();
      updateUser(this.createForme())
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
    onAddUserRoleRow(tableData) {
      tableData.push({
        user: {
          // 父表 用户标识
          id:
            validatenull(parent) || validatenull(parent.user)
              ? null
              : parent.user.id,
          name:
            validatenull(parent) || validatenull(parent.user)
              ? null
              : parent.user.name,
        },
        role: {
          // 角色标识
          id: null,
          name: "",
        },
        remarks: "", // 备注
      });
    },
    onDeleteRow(index, tableData) {
      tableData.splice(index, 1);
    },
    switchEdit() {
      this.dialogProps.action = "edit";
      this.dialogProps.title = "修改用户";
      this.initOptions(this.bizFormModel);
    },
    onDialogClose() {
      this.dialogProps.visible = false;
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["userForm"].clearValidate();
      });
    },
    initFormModel(This) {
      return {
        company: {
          // 父表 公司
          id:
            validatenull(This) || validatenull(This.company)
              ? null
              : This.company.id,
          name:
            validatenull(This) || validatenull(This.company)
              ? null
              : This.company.name,
          ids:
            validatenull(This) || validatenull(This.company)
              ? null
              : This.company.ids,
          code:
            validatenull(This) || validatenull(This.company)
              ? null
              : This.company.code,
          fullName:
            validatenull(This) || validatenull(This.company)
              ? null
              : This.company.fullName,
        },
        department: {
          // 部门
          id: null,
          name: null,
          ids: null,
          code: null,
        },
        fileIdFile: "", //图片
        userExt: {
          sex: "", //性别
          nation: "", // 民族
          birthday: "", // 出生日期
          description: "", // 介绍
          isDuty: "", // 是否在职
          creditType: "", // 证件类型
          creditNum: "", // 证件号码
          post: "", // 职位
          office: "", // 科室名称
          jobType: "", // 职业类型
          startWorkTime: "", // 开始时间
          endWorkTime: "", //结束时间
          pracPsnCode:"" ,//执业编码
        },
        userHeader: "", // 用户头像回显
        name: "", // 用户名
        loginName: "", // 登录名
        loginPassword: "", // 密码
        loginPasswordUpdate:
          this.dialogProps &&
          (this.dialogProps.action == "add" ||
            this.dialogProps.action == "copy")
            ? true
            : false, // 是否修改密码
        isLocked: 0, // 启用
        phone: "", // 手机号
        email: "", // 邮箱
        qyOpenId: "", // 企业openid
        remarks: "", // 备注信息
        // userRoleList: [], // 子表列表
      };
    },

    initOptions(This) {
      let company_search = {
        params: [],
      };
      // 表有禁用字段
      company_search.params.push.apply(company_search.params, [
        {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
        { columnName: "is_locked", queryType: "=", value: "0" },
      ]);
      // 数据权限: 科室clinic_office
      this.pushDataPermissions(
        company_search.params,
        this.$route.meta.routerId,
        "1026680647570219053"
      );
      this.department_List.splice(0, this.department_List.length);
      listClinicOfficeAll(company_search).then((responseData) => {
        this.department_List = responseData.data;
      });

      var company_id = This.company.id;
      this.key_department++;
      let department_search = {
        params: [
          { columnName: "company_id", queryType: "=", value: This.company.id },
        ],
      };
      // // 字段对应表上filter条件
      // department_search.params.push.apply(department_search.params, []);
      // // 数据权限: 部门org_department
      // this.pushDataPermissions(
      //   department_search.params,
      //   this.$route.meta.routerId,
      //   "52676365136650250"
      // );
      // this.department_List.splice(0, this.department_List.length);
      // treeDepartment(department_search).then((responseData) => {
      //   // this.department_List = responseData.data;
      // });

      // 子表  用户角色
      This = this.userRoleCurrentRow;
      let role_UserRole_search = {
        params: [],
      };
      // 字段对应表上filter条件
      role_UserRole_search.params.push.apply(role_UserRole_search.params, []);
      // 表有禁用字段
      role_UserRole_search.params.push.apply(role_UserRole_search.params, [
        { columnName: "is_locked", queryType: "=", value: "0" },
        { columnName: "company_id", queryType: "=", value: company_id },
      ]);
      // 数据权限: 角色sys_role
      this.pushDataPermissions(
        role_UserRole_search.params,
        this.$route.meta.routerId,
        "4012"
      );
      this.role_UserRole_List.splice(0, this.role_UserRole_List.length);
      listRoleAll(role_UserRole_search).then((responseData) => {
        this.role_UserRole_List = responseData.data;
      });
    },
    //根据手机号获取表单信息
    async byPhoneGetList(phone) {
      const res = await changemylist(phone);
      if (res.code === "100" && res.data) {
        this.bizFormModel = { ...res.data, userHeader: "" };
        await this.getPhoto(res.data.userExt.photoId);
        this.bizFormModel.userExt.jobType = "";
        this.bizFormModel.userExt.startWorkTime = "";
        this.bizFormModel.userExt.endWorkTime = "";
        this.bizFormModel.userExt.isDuty = "";
        this.bizFormModel.userExt.description = "";
        this.bizFormModel.userExt.post = "";
        this.bizFormModel.userExt.office = "";
      } else if (res.code === "100" && !res.data) {
        return;
      } else {
        this.$message({
          type: "error",
          message: "此号码已注册",
        });
      }
    },
    getPhoto(id) {
      if(!id){
        this.bizFormModel.userHeader = ''
      }else{
        getPhotoById(id).then((res) => {
          console.log(res,'怪该i怪');
        const src = `data:image/png;base64,${res}`;
        this.bizFormModel.userHeader = src;
        console.log(src, ".......");
        return src;
      });
      }
    },
  },
  watch: {
    //监听输入框手机号位数
    "bizFormModel.phone": function (newValue, oldValue) {
      //  if( this.dialogProps.action == 'edit' ){
      if (newValue.length === 11 && this.dialogProps.action === "add") {
        this.byPhoneGetList(newValue);
      }
    },
  },
  mounted: function () {
    this.$nextTick(() => {
      this.$on("openViewUserDialog", async function (user) {
        if(user.userExt!=undefined){
          this.photoId = user.userExt.photoId;
          await this.getPhoto(user.userExt.photoId);
        }else{
          this.photoId = null
          this.bizFormModel = this.initFormModel()
        }
        this.dialogProps.action = "view";
        this.dialogProps.title = "查看用户";
        this.bizFormModel = { ...this.bizFormModel, ...user };
        this.tabIndex = "1";
        this.initOptions(this.bizFormModel);
        this.dialogProps.visible = true;
      });
      this.$on("openEditUserDialog", async function (user) {
        if(user.userExt!=undefined){
          this.photoId = user.userExt.photoId;
          await this.getPhoto(user.userExt.photoId);
        }else{
          this.photoId = null
          this.bizFormModel = this.initFormModel()
        }
        this.dialogProps.action = "edit";
        this.dialogProps.title = "修改用户";
        this.bizFormModel = { ...this.bizFormModel, ...user };
        this.tabIndex = "1";
        this.initOptions(this.bizFormModel);
        this.dialogProps.visible = true;
      });
      this.$on("openAddUserDialog", function (parent) {
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加用户";
        this.bizFormModel = this.initFormModel(parent);
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
      this.$on("openCopyUserDialog", function (user) {
        this.dialogProps.action = "copy";
        this.dialogProps.title = "添加用户";
        this.bizFormModel = user;
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.bizFormModel.id = null; //把id设置为空，添加一个新的
        for (var i = 0; i <= this.bizFormModel.userRoleList.length - 1; i++) {
          this.bizFormModel.userRoleList[i].id = null;
        }
        this.bizFormModel.loginPasswordUpdate = true; // 允许修改密码
        this.dialogProps.visible = true;
      });
    });
  },
};
</script>
<style lang="scss" scoped>
.tab-item {
  .avatar-uploader {
    width: 100px;
    height: 100px;
    border: 1px dashed #d9d9d9;
  }
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 100%;
    height: 100%;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409eff;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
  }
  .avatar {
    width: 100px;
    height: 100px;
    display: block;
  }
  position: relative;
  .tab-option {
    position: absolute;
    top: -46px;
    right: 0;
  }
}
</style>
