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
      style="marginTop: 10px"
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
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.birthday"
                type="date"
                value-format="yyyy-MM-dd"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入出生日期'
                "
                @change="birthdayChanges"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="年龄" prop="age">
              <el-col :span="24 / 2">
                <el-input
                  :disabled="false"
                  v-model="bizFormModel.age"
                  :maxlength="20"
                  :placeholder="dialogProps.action == 'view' ? '' : '年龄'"
                  @input="exchageAge"
                >
                  <template slot="append">
                    岁
                  </template>
                </el-input>
              </el-col>
              <el-col :span="24 / 2">
                <el-input
                  :disabled="false"
                  v-model="bizFormModel.month"
                  :maxlength="20"
                  :placeholder="dialogProps.action == 'view' ? '' : '月'"
                >
                  <template slot="append">
                    月
                  </template>
                </el-input>
              </el-col>
            </el-form-item>
          </el-col>

        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="身份证号" prop="card">
              <el-input
                :disabled="dialogProps.action == 'view'"
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
        </el-row>
        <el-row>

          <!-- <el-col :span="24 / 2">
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
          </el-col> -->
        </el-row>
        <el-row>

          <!-- <el-form-item label="市" prop="city">
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
          </el-form-item> -->
          <el-form-item label='所在地区' prop=''>
            <v-distpicker :disabled='dialogProps.action == "view"' :province="province" :city="city" :area="area"
                          @province="onChangeProvince" @city="onChangeCity" @area="onChangeArea"></v-distpicker>
          </el-form-item>

          <!-- <el-col :span="24 / 2">
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
          </el-col> -->
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
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true'
                 @click='onSubmit("patientForm")'>保 存
      </el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
  </el-dialog>
</template>
<script>
import {listDictItemAll} from "@/api/sys/dictItem";
import {listAdministrativeDivisionAll} from "@/api/org/administrativeDivision";
import {savePatient} from "@/api/outpatient/patient";
import BaseUI from "@/views/components/baseUI";
import OperationIcon from "@/components/OperationIcon";
import VDistpicker from 'v-distpicker'

export default {
  extends: BaseUI,
  name: "patient-form",
  components: {
    OperationIcon,
    VDistpicker
  },
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
      bizFormModel: this.initFormModel(),
      tabIndex: "1",
      gender_List: [], // 性别
      //省市区三级联动
      province: '',
      city: '',
      area: '',
      flage: false,//防止重复提交
      withPatientNexus_List: [], // 与患者关系
      dialogProps: {
        visible: false,
        action: '',
        title: '',
      },
      formRules: {
        name: [{required: true, message: "请输入患者姓名", trigger: "blur"}],
        "gender.value": [
          {required: true, message: "请选择性别", trigger: "change"},
        ],
        age: [{required: true, message: "请输入年龄", trigger: "blur"}],
        month: [{required: true, message: "请输入月", trigger: "blur"}],
        // phone: [{ required: true, message: "请输入联系方式", trigger: "blur" }],
        "birthday": [
          {required: true, message: "请选择出生日期", trigger: "change"},
        ],
        // "card": [
        //   { required: true, message: '请输入身份证号码', trigger: 'blur' },
        //   { min:18,max:18, message: "请输入18位身份证号码", trigger: "blur" },
        //   {	//调用上面定义的方法校验格式是否正确
        //            validator: isCnNewID,trigger: "blur"
        //   }
        // ],
        guardianName: [{required: false, message: "请输入监护人姓名", trigger: "change"},],
        guardianPhone: [{required: false, message: "请输入监护人电话", trigger: "change"},]
      },
      province_List: [], // 省
      city_List: [], //市
      area_List: [] //区
    };
  },
  props: {
    // 权限
    permission: {
      type: Object,
    },
  },
  methods: {
    //通过获取身份证号获取出生年月日
    getBirthday() {
      if (this.bizFormModel.card.length == 18) {
        let birthDay = this.bizFormModel.card.substring(6, 14)
        console.log(birthDay, '获取身份证号');
        let year = birthDay.substring(0, 4)
        let month = birthDay.substring(4, 6)
        let day = birthDay.substring(6)
        let newBirthday = year + "-" + month + "-" + day
        console.log(newBirthday, '获取身份证号-生日');
        this.bizFormModel.birthday = newBirthday
        this.birthdayChanges()

        // 性别
        let sexStr = this.bizFormModel.card.substring(16, 17)
        console.log(sexStr, '获取身份证号-性别');
        // 0女1男
        let sexName = "男";
        if (parseInt(sexStr) % 2 === 0) {
          sexName = "女";
        }
        if (this.gender_List && this.gender_List.length > 0) {
          this.bizFormModel.gender = this.gender_List.find(item => {
            return item.name === sexName
          })
        }
      } else if (this.bizFormModel.card.length == 0) {
        this.bizFormModel.birthday = ""
        this.bizFormModel.gender = {
          value: null,
          name: null,
        }
        this.birthdayChanges()
      }
    },
    //变更年龄事件
    exchageAge() {
      if (this.bizFormModel.age < 12) {
        this.formRules.guardianName[0].required = true
        this.formRules.guardianPhone[0].required = true
        this.formRules.phone[0].required = false

      } else {
        this.formRules.guardianName[0].required = false
        this.formRules.guardianPhone[0].required = false
        this.$refs["patientForm"].clearValidate();
        this.formRules.phone[0].required = true
      }
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
        this.formRules.guardianName[0].required = false
        this.formRules.guardianPhone[0].required = false
        this.$refs["patientForm"].clearValidate();
        //this.formRules.phone[0].required=true
      }
    },

    changeProvince() {
      if (!this.bizFormModel.province) return;
      this.bizFormModel.city = '';
      this.bizFormModel.area = '';
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

      listAdministrativeDivisionAll(division_search).then((responseData) => {
        this.city_List = responseData.data;
      });
    },
    changeCity() {
      if (!this.bizFormModel.city) return;

      this.bizFormModel.area = '';
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

      listAdministrativeDivisionAll(division_search).then((responseData) => {
        this.area_List = responseData.data;
      });
    },
    birthdayChange() {

      if (!this.bizFormModel.birthday) return;
      const duration = this.$moment.duration(this.$moment().diff(this.bizFormModel.birthday));
      this.bizFormModel.age = duration.years();
      this.bizFormModel.month = duration.months();
      if (this.bizFormModel.age < 12) {
        this.formRules.guardianName[0].required = true
        this.formRules.guardianPhone[0].required = true
        this.formRules.phone[0].required = false

      } else {
        this.formRules.guardianName[0].required = true
        this.formRules.guardianPhone[0].required = true
        this.$refs["patientForm"].clearValidate();
        this.formRules.phone[0].required = true
      }
    },
    onSubmit(formName) {
      this.flage = true
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave();
        } else {
          this.flage = false
          return false
        }
      });
    },
    doSave() {
      let flageCard = false
      //校验身份证号码
      if (this.bizFormModel.card) {
        var arrExp = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];//加权因子
        var arrValid = [1, 0, "X", 9, 8, 7, 6, 5, 4, 3, 2];//校验码
        if (/^\d{17}\d|x$/i.test(this.bizFormModel.card)) {
          var sum = 0, idx;
          for (var i = 0; i < this.bizFormModel.card.length - 1; i++) {
            // 对前17位数字与权值乘积求和
            sum += parseInt(this.bizFormModel.card.substr(i, 1), 10) * arrExp[i];
          }
          // 计算模（固定算法）
          idx = sum % 11;
          // 检验第18为是否与校验码相等
          if (arrValid[idx] == this.bizFormModel.card.substr(17, 1).toUpperCase()) {
            flageCard = true
          } else {
            flageCard = false
          }
        } else {
          flageCard = false
        }
      }

      if (!flageCard && this.bizFormModel.card) {
        this.$message.error("身份证号码不正确，请重新输入")
        this.flage = false
        return;
      }

      this.setLoad()
      this.bizFormModel.province = this.province
      this.bizFormModel.city = this.city
      this.bizFormModel.area = this.area
      // this.bizFormModel.id=""
      savePatient(this.bizFormModel).then(responseData => {
        this.flage = false
        if (responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
        } else {
          this.flage = false
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.flage = false
        this.outputError(error)
      })
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

      listAdministrativeDivisionAll(division_search).then((responseData) => {
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
      this.pushDataPermissions(withPatientNexus_search.params, this.$route.meta.routerId, '4005')
      this.withPatientNexus_List.splice(0, this.withPatientNexus_List.length)
      listDictItemAll(withPatientNexus_search).then(responseData => {
        this.withPatientNexus_List = responseData.data
      })
    },
    // 获取到省市区三级联动的值
    onChangeProvince(data) {
      this.province = data.value

    },
    onChangeCity(data) {

      this.city = data.value

    },
    onChangeArea(data) {

      this.area = data.value

    }
  },
  watch: {
    'this.bizFormModel.age': function () {
      console.log(this.bizFormModel.age, '年龄');
      if (this.bizFormModel.age < 12) {
        formRules.guardianName[0].required = true
        formRules.guardianPhone[0].required = true
      } else {
        formRules.guardianName[0].required = true
        formRules.guardianPhone[0].required = true
      }
    }
  },
  mounted: function () {
    this.$nextTick(() => {
      this.$on('openViewPatientDialog', function (patient) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看患者信息'
        this.bizFormModel = {...this.initFormModel(), ...patient}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
        this.province = this.bizFormModel.province
        this.city = this.bizFormModel.city
        this.area = this.bizFormModel.area
      })
      this.$on('openEditPatientDialog', function (patient) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改患者信息'
        this.bizFormModel = {...this.initFormModel(), ...patient}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
        this.province = this.bizFormModel.province
        this.city = this.bizFormModel.city
        this.area = this.bizFormModel.area
      })
      this.$on('openAddPatientDialog', function () {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加患者信息'
        this.bizFormModel = this.initFormModel(this.user)
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
        this.province = ''
        this.city = ''
        this.area = ''
      })
      this.$on('openCopyPatientDialog', function (patient) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加患者信息'
        this.bizFormModel = {...this.initFormModel(this.user), ...patient}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }
}
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
