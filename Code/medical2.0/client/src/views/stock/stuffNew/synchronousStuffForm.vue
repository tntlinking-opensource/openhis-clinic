<template>
  <el-dialog :title="dialogProps.title" :visible.sync="dialogProps.visible" :close-on-click-modal="true" width="50%"
    @open="onDialogOpen()" v-loading="loading" :modal="false">
    <el-form :model="bizFormModel" :rules="formRules" ref="stuffForm" label-width="120px" style="marginTop: 10px"
      label-position="right" class="edit-form">
      <div class="tab-item" v-show="tabIndex == '1'">
        <div class="specifications">材料信息</div>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="材料名称" prop="name">
              <el-input :disabled="dialogProps.action == 'view' || dialogProps.action == 'edit'" v-model="bizFormModel.name" :maxlength="64"
                @input="pinyinInput" :placeholder="dialogProps.action == 'view' ? '' : '请输入材料名称'
    " autofocus></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="常用名" prop="commonName">
              <el-input :disabled="dialogProps.action == 'view' || dialogProps.action == 'edit'" v-model="bizFormModel.commonName" :maxlength="45"
                :placeholder="dialogProps.action == 'view' ? '' : '请输入常用名'
    "></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="拼音码" prop="pinyinCode">
              <el-input disabled v-model="bizFormModel.pinyinCode" :maxlength="45" placeholder="请输入拼音码"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="材料类型" prop="type.value">
              <el-input v-if="dialogProps.action == 'view'" :disabled="true"
                v-model="bizFormModel.type.name"></el-input>
              <el-select v-else v-model="bizFormModel.type" :disabled="dialogProps.action == 'edit'" value-key="value" filterable clearable placeholder="请选择材料类型"
                @clear="
    bizFormModel.type = {
      value: null,
      name: null,
    }
    ">
                <el-option v-for="item in type_List" :key="item.value" :label="item.name" :value="item"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="生产厂家" prop="factory.id">
              <el-input v-if="dialogProps.action == 'view'" :disabled="true"
                v-model="bizFormModel.factory.name"></el-input>
              <el-select v-else v-model="bizFormModel.factory" :disabled="dialogProps.action == 'edit'" value-key="id" filterable clearable placeholder="请选择生产厂家"
                @clear="
    bizFormModel.factory = {
      id: null,
      name: null,
    }
    ">
                <el-option v-for="factory in factory_List" :key="factory.id" :label="factory.name"
                  :value="factory"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="性质" prop="nature.value">
              <el-input v-if="dialogProps.action == 'view'" :disabled="true"
                v-model="bizFormModel.nature.name"></el-input>
              <el-select v-else v-model="bizFormModel.nature" :disabled="dialogProps.action == 'edit'" value-key="value" filterable clearable placeholder="请选择性质"
                @clear="
    bizFormModel.nature = {
      value: null,
      name: null,
    }
    ">
                <el-option v-for="nature in nature_List" :key="nature.value" :label="nature.name"
                  :value="nature"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="条形码" prop="barCode">
              <el-input :disabled="dialogProps.action == 'view' || dialogProps.action == 'edit'" v-model="bizFormModel.barCode" :maxlength="64"
                :placeholder="dialogProps.action == 'view' ? '' : '请输入条形码'
    "></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="注册证名称" prop="registrationName">
              <el-input :disabled="dialogProps.action == 'view' || dialogProps.action == 'edit'" v-model="bizFormModel.registrationName" :maxlength="64"
                :placeholder="dialogProps.action == 'view' ? '' : '请输入注册证名称'
    "></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="注册证号" prop="registrationCode">
              <el-input :disabled="dialogProps.action == 'view' || dialogProps.action == 'edit'" v-model="bizFormModel.registrationCode" :maxlength="64"
                :placeholder="dialogProps.action == 'view' ? '' : '请输入注册证号'
    "></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="规格" prop="specifications">
              <el-input :disabled="dialogProps.action == 'view' || dialogProps.action == 'edit'" v-model="bizFormModel.specifications" :maxlength="45"
                :placeholder="dialogProps.action == 'view' ? '' : '请输入规格'"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="包装数量" prop="packNumber">
              <el-input v-if="dialogProps.action == 'view'  || dialogProps.action == 'edit'" :disabled="true"
                v-model="bizFormModel.packNumber"></el-input>
              <number-input v-else v-model="bizFormModel.packNumber" :precision="0"></number-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="最小单位" prop="minUnit.value">
              <el-input v-if="dialogProps.action == 'view'" :disabled="true"
                v-model="bizFormModel.minUnit.name"></el-input>
              <el-select v-else v-model="bizFormModel.minUnit" value-key="value" filterable clearable :disabled="dialogProps.action == 'edit'"
                placeholder="请选择最小单位" @clear="
    bizFormModel.minUnit = {
      value: null,
      name: null,
    }
    ">
                <el-option v-for="minUnit in minUnit_List" :key="minUnit.value" :label="minUnit.name"
                  :value="minUnit"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="包装单位" prop="packUnit.value">
              <el-input v-if="dialogProps.action == 'view'" :disabled="true"
                v-model="bizFormModel.packUnit.name"></el-input>
              <el-select v-else v-model="bizFormModel.packUnit" value-key="value" filterable clearable :disabled="dialogProps.action == 'edit'"
                placeholder="请选择包装单位" @clear="
    bizFormModel.packUnit = {
      value: null,
      name: null,
    }
    ">
                <el-option v-for="packUnit in packUnit_List" :key="packUnit.value" :label="packUnit.name"
                  :value="packUnit"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="允许对外销售" prop="isOutSell">
              <el-switch :disabled="dialogProps.action == 'view' || dialogProps.action == 'edit'" v-model="bizFormModel.isOutSell"
                active-color="#13ce66" inactive-color="#dbdfe6" active-value="1" inactive-value="0"></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="对外销售价格" prop="priceOutSell">
              <el-input v-if="dialogProps.action == 'view'" :disabled="true"
                v-model="bizFormModel.priceOutSell"></el-input>
              <number-input v-else v-model="bizFormModel.priceOutSell" :disabled="bizFormModel.isOutSell === '0'"
                currency="CNY" :precision="2"></number-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="允许拆零销售" prop="isUnpackSell">
              <el-switch :disabled="dialogProps.action == 'view' || dialogProps.action == 'edit'" v-model="bizFormModel.isUnpackSell"
                active-color="#13ce66" inactive-color="#dbdfe6" active-value="1" inactive-value="0"></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="拆开后零售价" prop="retailPrice">
              <el-input v-if="dialogProps.action == 'view'" :disabled="true"
                v-model="bizFormModel.retailPrice"></el-input>
              <number-input v-else v-model="bizFormModel.retailPrice" :disabled="bizFormModel.isUnpackSell === '0'"
                currency="CNY" :precision="2"></number-input>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label='是否启用' prop='status'>
              <el-switch :disabled='dialogProps.action == "view" || dialogProps.action == "edit"' v-model='value' active-color='#13ce66'
                inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="材料编码" prop="code">
              <el-input disabled v-model="bizFormModel.code" :maxlength="45" placeholder="保存后自动生成"></el-input>
            </el-form-item>
          </el-col>

        </el-row>
        <el-divider class="line"></el-divider>
        <div class="specifications">其他信息</div>
        <div>
          <el-row>
            <el-col :span="24 / 3">
              <el-form-item label="有效期" prop="indate">
                <el-input :disabled="(dialogProps.action == 'view'|| dialogProps.action == 'edit') && updateType == '0'" v-model="bizFormModel.indate"
                  :maxlength="64" placeholder="请输入有效期">
                  <template slot="append">天</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 3">
              <el-form-item label="库存下限" prop="inventoryFloor">
                <el-input :disabled="(dialogProps.action == 'view'|| dialogProps.action == 'edit') && updateType == '0'"
                  v-model="bizFormModel.inventoryFloor" :maxlength="64" placeholder="请输入库存下限">
                  <template slot="append">
                    {{ bizFormModel.packUnit.name }}
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 3">
              <el-form-item label="库存上限" prop="inventoryLimit">
                <el-input :disabled="(dialogProps.action == 'view'|| dialogProps.action == 'edit') && updateType == '0'"
                  v-model="bizFormModel.inventoryLimit" :maxlength="64" placeholder="请输入库存上限">
                  <template slot="append">
                    {{ bizFormModel.packUnit.name }}
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24 / 3">
              <el-form-item label="货位号" prop="locationNumber">
                <el-input :disabled="(dialogProps.action == 'view'|| dialogProps.action == 'edit') && updateType == '0'"
                  v-model="bizFormModel.locationNumber" :maxlength="64" placeholder="请输入货位号"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='updateType === "1" || dialogProps.action == "add"' :disabled="flage" type='primary' :plain='true'
        @click='onSubmit("stuffForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action == "add"' :disabled="flage" type='primary' :plain='true'
        @click='onGoSubmit("stuffForm")'>保 存 继 续</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { validatenull } from "@/utils/validate";
import { listDictItemAll } from "@/api/sys/dictItem";
import { listManufactureFactoryAll } from "@/api/basicdata/manufactureFactory";
import { saveStuff } from "@/api/stock/stuff";
import BaseUI from "@/views/components/baseUI";
import OperationIcon from "@/components/OperationIcon";

let pinyin = require('js-pinyin');
export default {
  extends: BaseUI,
  name: "stuff-form",
  components: {
    OperationIcon,
  },
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: "1",
      type_List: [], // 材料类型
      factory_List: [], // 生产厂家
      nature_List: [], // 性质
      minUnit_List: [], // 最小单位
      packUnit_List: [], // 包装单位
      value: '1',
      flage: false,
      dialogProps: {
        visible: false,
        action: '',
        title: '',

      },
      formRules: {
        name: [{ required: true, message: "请输入材料名称", trigger: "blur" }],
        packNumber: [
          { required: true, message: "请选择包装数量", trigger: "blur" },
        ],
        "minUnit.value": [
          { required: true, message: "请选择最小单位", trigger: "change" },
        ],
        "packUnit.value": [
          { required: true, message: "请选择包装单位", trigger: "change" },
        ],
      },
      updateType: "0"  //修改标记
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
      /*if (this.bizFormModel.company.id !== this.currentUser.company.id) {
        this.$message.error('无法修改机构药品信息，请联系机构修改药品基础信息')
        return
      }*/
      console.log(this.bizFormModel);
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
    //保存继续
    onGoSubmit(formName) {
      this.flage = true
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.setLoad()
          this.bizFormModel.status = this.value
          saveStuff(this.bizFormModel).then(responseData => {
            this.flage = false
            if (responseData.code == 100) {
              // this.dialogProps.visible = false
              console.log(this.bizFormModel);
              this.bizFormModel.name = ""
              this.bizFormModel.commonName = ""
              this.bizFormModel.code = ""
              this.bizFormModel.type = {}
              this.bizFormModel.factory = {}
              this.bizFormModel.nature = {}
              this.bizFormModel.barCode = ""
              this.bizFormModel.registrationName = ""
              this.bizFormModel.registrationCode = ""
              this.bizFormModel.specifications = ""
              this.bizFormModel.packNumber = 0
              // this.bizFormModel.minUnit={}
              //  this.bizFormModel.packUnit=""
              this.bizFormModel.isOutSell = "0"
              this.bizFormModel.priceOutSell = 0
              this.bizFormModel.isUnpackSell = "0"
              this.bizFormModel.retailPrice = 0
              this.bizFormModel.remarks = ""
              this.bizFormModel.pinyinCode = ""
              this.bizFormModel.status = ""
              this.value = '1'
              this.$emit('save-finished', "1")
            } else {
              this.showMessage(responseData)
            }
            this.resetLoad()
          }).catch(error => {
            this.flage = false
            this.outputError(error)
          })
        } else {
          this.flage = false
          return false
        }
      });
    },
    doSave() {
      this.setLoad()
      this.bizFormModel.status = this.value
      saveStuff(this.bizFormModel).then(responseData => {
        this.flage = false
        if (responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished', "1")
        } else {
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
      this.dialogProps.title = "修改材料信息";
      this.initOptions(this.bizFormModel);
    },
    onDialogClose() {
      this.dialogProps.visible = false;
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["stuffForm"].clearValidate();
      });
    },
    initFormModel(This) {
      return {
        company: {
          // 诊所id
          id: currentUser.company.id,
          name: null,
        },
        name: "", // 材料名称
        commonName: "", // 常用名
        code: "", // 材料编码
        type: {
          // 材料类型
          value: null,
          name: null,
        },
        factory: {
          // 生产厂家
          id: null,
          name: null,
        },
        nature: {
          // 性质
          value: null,
          name: null,
        },
        barCode: "", // 条形码
        registrationName: "", // 注册证名称
        registrationCode: "", // 注册证号
        specifications: "", // 规格
        packNumber: 0, // 包装数量
        minUnit: {
          // 最小单位
          value: null,
          name: null,
        },
        packUnit: {
          // 包装单位
          value: null,
          name: null,
        },
        isOutSell: "0", // 允许对外销售
        priceOutSell: 0, // 对外销售价格
        isUnpackSell: "0", // 允许拆零销售
        retailPrice: 0, // 拆开后零售价
        remarks: "", // 备注信息
      };
    },
    initOptions(This) {
      let type_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1004462867645374476",
          },
        ],
      };
      // 字段对应表上filter条件
      type_search.params.push.apply(type_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        type_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.type_List.splice(0, this.type_List.length);
      listDictItemAll(type_search).then((responseData) => {
        this.type_List = responseData.data;
      });
      let factory_search = {
        params: [],
      };
      // 字段对应表上filter条件
      factory_search.params.push.apply(factory_search.params, [
        {
          columnName: "company_id",
          queryType: "=",
          value: currentUser.company.id,
        },
        {
          columnName: "type",
          queryType: "=",
          value: "2",
        },
      ]);
      // 数据权限: 生产厂家manufacture_factory
      this.pushDataPermissions(
        factory_search.params,
        this.$route.meta.routerId,
        "1016206064147988493"
      );
      this.factory_List.splice(0, this.factory_List.length);
      listManufactureFactoryAll(factory_search).then((responseData) => {
        this.factory_List = responseData.data;
      });
      let nature_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1004078055755374607",
          },
        ],
      };
      // 字段对应表上filter条件
      nature_search.params.push.apply(nature_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        nature_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.nature_List.splice(0, this.nature_List.length);
      listDictItemAll(nature_search).then((responseData) => {
        this.nature_List = responseData.data;
      });
      let minUnit_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1004406758192578593",
          },
        ],
      };
      // 字段对应表上filter条件
      minUnit_search.params.push.apply(minUnit_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        minUnit_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.minUnit_List.splice(0, this.minUnit_List.length);
      listDictItemAll(minUnit_search).then((responseData) => {
        this.minUnit_List = responseData.data;
      });
      let packUnit_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1004406758192578597",
          },
        ],
      };
      // 字段对应表上filter条件
      packUnit_search.params.push.apply(packUnit_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        packUnit_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.packUnit_List.splice(0, this.packUnit_List.length);
      listDictItemAll(packUnit_search).then((responseData) => {
        this.packUnit_List = responseData.data;
      });
    },
    //输入框拼音码
    pinyinInput(value) {
      this.bizFormModel.pinyinCode = pinyin.getCamelChars(value)

    },
  },
  watch: {},
  mounted: function () {
    this.$nextTick(() => {
      this.$on("openViewStuffDialog", function (stuff) {
        this.dialogProps.action = "view";
        this.updateType = "0"
        this.dialogProps.title = "查看材料信息";
        this.bizFormModel = { ...this.initFormModel(), ...stuff };
        this.initOptions(this.bizFormModel);
        this.value = this.bizFormModel.status
        this.tabIndex = "1";

        this.dialogProps.visible = true;
      });
      this.$on("openEditStuffDialog", function (stuff) {

        if ((stuff.stock.storageStock - stuff.stock.usedStock - stuff.stock.reimburseStock)) {
          this.dialogProps.action = "view";
        } else {
          this.dialogProps.action = "edit";
        }
        this.updateType = "0"
        this.dialogProps.title = "修改材料信息";
        this.bizFormModel = { ...this.initFormModel(), ...stuff };
        this.initOptions(this.bizFormModel);
        this.value = this.bizFormModel.status
        this.tabIndex = "1";
        console.log(this.updateType);
        this.dialogProps.visible = true;
      });
      this.$on("openAddStuffDialog", function () {
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加材料信息";
        this.bizFormModel = this.initFormModel();
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
      this.$on("openCopyStuffDialog", function (stuff) {
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加材料信息";
        this.bizFormModel = { ...this.initFormModel(), ...stuff };
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.bizFormModel.id = null; //把id设置为空，添加一个新的
        this.dialogProps.visible = true;
      });
    });
  },
};
</script>
<style scoped>
.line {
  margin-top: -10px;
}

.specifications {
  margin-top: -30px;
  width: 100%;
  font-weight: bold;
  padding-bottom: 8px;
  padding: 20px;
  color: #333;
}

/* .specifications {
  width: 100%;
  font-weight: bold;
  padding-bottom: 8px;
  padding: 20px;
  color: #333;
} */
</style>
