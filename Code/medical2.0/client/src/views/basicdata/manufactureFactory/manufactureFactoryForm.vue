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
      ref="manufactureFactoryForm"
      label-width="120px"
      label-position="right"
      class="edit-form"
      style="margintop: 10px"
    >
      <div class="tab-item" v-show="tabIndex == '1'">
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="厂家名称" prop="name">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.name"
                :maxlength="128"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入厂家名称'
                "
                autofocus
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="联系人" prop="people">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.people"
                :maxlength="45"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入联系人'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="电话" prop="phone">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.phone"
                :maxlength="45"
                :placeholder="dialogProps.action == 'view' ? '' : '请输入电话'"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="是否启用" prop="status">
              <el-switch
                :disabled="dialogProps.action == 'view'"
                v-model="value"
                active-color="#13ce66"
                inactive-color="#dbdfe6"
                active-value="1"
                inactive-value="0"
              ></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="所在地区" prop="">
              <v-distpicker
                :disabled="dialogProps.action == 'view'"
                :province="province"
                :city="city"
                :area="area"
                @province="onChangeProvince"
                @city="onChangeCity"
                @area="onChangeArea"
              ></v-distpicker>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="厂家分类" prop="type">
              <div>
                <el-radio
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.type"
                  label="1"
                  border
                  >药品厂家</el-radio
                >
                <el-radio
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.type"
                  label="2"
                  border
                  >材料厂家</el-radio
                >
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-form-item label="地址" prop="address">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.address"
                type="textarea"
                :maxlength="128"
                :placeholder="dialogProps.action == 'view' ? '' : '请输入地址'"
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
        :disabled="flage"
        type="primary"
        :plain="true"
        @click="onSubmit('manufactureFactoryForm')"
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
import { saveManufactureFactory } from "@/api/basicdata/manufactureFactory";
import BaseUI from "@/views/components/baseUI";
import OperationIcon from "@/components/OperationIcon";
import VDistpicker from "v-distpicker";
export default {
  extends: BaseUI,
  name: "manufactureFactory-form",
  components: {
    OperationIcon,
    VDistpicker,
  },
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: "1",
      //省市区三级联动
      province: "",
      city: "",
      area: "",
      value: "1",
      flage: false, //防止重复提交
      dialogProps: {
        visible: false,
        action: "",
        title: "",
      },
      formRules: {
        name: [{ required: true, message: "请输入厂家名称", trigger: "blur" }],
        type: [
          { required: true, message: "请选择供应商类型", trigger: "change" },
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
    onSubmit(formName) {
      this.flage = true;
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.doSave();
        } else {
          this.flage = false;
          return false;
        }
      });
    },
    doSave() {
      this.setLoad();
      this.bizFormModel.province = this.province;
      this.bizFormModel.city = this.city;
      this.bizFormModel.area = this.area;
      this.bizFormModel.status = this.value;
      saveManufactureFactory(this.bizFormModel)
        .then((responseData) => {
          this.flage = false;
          if (responseData.code == 100) {
            this.dialogProps.visible = false;
            this.$emit("save-finished", "1");
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.flage = false;
          this.outputError(error);
        });
    },
    switchEdit() {
      this.dialogProps.action = "edit";
      this.dialogProps.title = "修改生产厂家";
      this.initOptions(this.bizFormModel);
    },
    onDialogClose() {
      this.dialogProps.visible = false;
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["manufactureFactoryForm"].clearValidate();
      });
    },
    initFormModel(This) {
      return {
        company: {
          // 诊所id
          id: currentUser.company.id,
          name: null,
        },
        name: "", // 厂家名称
        people: "", // 联系人
        phone: "", // 电话
        province: "", // 省
        city: "", // 市
        area: "", // 区
        address: "", // 地址
        remarks: "", // 备注信息
      };
    },
    initOptions(This) {},
    // 获取到省市区三级联动的值
    onChangeProvince(data) {
      this.province = data.value;
    },
    onChangeCity(data) {
      this.city = data.value;
    },
    onChangeArea(data) {
      this.area = data.value;
    },
  },
  watch: {},
  mounted: function () {
    this.$nextTick(() => {
      this.$on(
        "openViewManufactureFactoryDialog",
        function (manufactureFactory) {
          this.dialogProps.action = "view";
          this.dialogProps.title = "查看生产厂家";
          this.bizFormModel = {
            ...this.initFormModel(),
            ...manufactureFactory,
          };
          this.initOptions(this.bizFormModel);
          this.tabIndex = "1";
          this.value = this.bizFormModel.status;
          this.dialogProps.visible = true;
          this.province = this.bizFormModel.province;
          this.city = this.bizFormModel.city;
          this.area = this.bizFormModel.area;
        }
      );
      this.$on(
        "openEditManufactureFactoryDialog",
        function (manufactureFactory) {
          this.dialogProps.action = "edit";
          this.dialogProps.title = "修改生产厂家";
          this.bizFormModel = {
            ...this.initFormModel(),
            ...manufactureFactory,
          };
          this.initOptions(this.bizFormModel);
          this.value = this.bizFormModel.status;
          this.tabIndex = "1";
          this.dialogProps.visible = true;
          this.province = this.bizFormModel.province;
          this.city = this.bizFormModel.city;
          this.area = this.bizFormModel.area;
        }
      );
      this.$on("openAddManufactureFactoryDialog", function () {
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加生产厂家";
        this.bizFormModel = this.initFormModel();
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.dialogProps.visible = true;
        this.province = "";
        this.city = "";
        this.area = "";
      });
      this.$on(
        "openCopyManufactureFactoryDialog",
        function (manufactureFactory) {
          this.dialogProps.action = "add";
          this.dialogProps.title = "添加生产厂家";
          this.bizFormModel = {
            ...this.initFormModel(),
            ...manufactureFactory,
          };
          this.initOptions(this.bizFormModel);
          this.tabIndex = "1";
          this.bizFormModel.id = null; //把id设置为空，添加一个新的
          this.dialogProps.visible = true;
        }
      );
    });
  },
};
</script>
<style lang="scss">
.distpicker-address-wrapper {
  select {
    padding: 0px 10px !important;
    height: 30px !important;
    font-size: 15px !important;
    line-height: 30px !important;
  }
}
</style>