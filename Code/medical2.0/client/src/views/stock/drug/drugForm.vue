<template>
  <el-dialog
    :title="dialogProps.title"
    :visible.sync="dialogProps.visible"
    :close-on-click-modal="false"
    width="50%"
    @open="onDialogOpen()"
    v-loading="loading"
  >
    <!-- <div slot="title" class="dialog-header">
      {{ dialogProps.title }}
      <OperationIcon
        v-show="dialogProps.action == 'view' && permission.edit"
        type="primary"
        text="编辑"
        placement="top-start"
        icon-name="el-icon-edit"
        @click="switchEdit"
      ></OperationIcon>
    </div> -->

    <el-form
      :model="bizFormModel"
      :rules="formRules"
      ref="drugForm"
      label-width="120px"
      label-position="right"
      class="edit-form"
    >
      <div class="tab-item" v-show="tabIndex == '1'">
        <div class="specifications">药品信息</div>
        <div>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="药品名称" prop="goodsName">
                <el-input
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.goodsName"
                  @input="pinyinInput"
                  :maxlength="128"
                  :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入药品名称'
                "
                  autofocus
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="别名" prop="brandName">
                <el-input
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.brandName"
                  :maxlength="128"
                  :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入别名'
                "
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="拼音码" prop="pinyinCode">
                <el-input
                  disabled
                  v-model="bizFormModel.pinyinCode"
                  :maxlength="64"
                  placeholder="请输入拼音码"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="药品类型" prop="type.value">
                <el-input
                  v-if="dialogProps.action == 'view'"
                  :disabled="true"
                  v-model="bizFormModel.type.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.type"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择药品类型"
                  @clear="
                  bizFormModel.type = {
                    value: null,
                    name: null,
                  }
                "
                >
                  <el-option
                    v-for="type in type_List"
                    :key="type.value"
                    :label="type.name"
                    :value="type"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>

          </el-row>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="药品来源" prop="source">
                <el-input
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.source"
                  :maxlength="128"
                  :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入药品来源'
                "
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="性质" prop="nature.value">
                <el-input
                  v-if="dialogProps.action == 'view'"
                  :disabled="true"
                  v-model="bizFormModel.nature.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.nature"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择性质"
                  @clear="
                  bizFormModel.nature = {
                    value: null,
                    name: null,
                  }
                "
                >
                  <el-option
                    v-for="nature in nature_List"
                    :key="nature.value"
                    :label="nature.name"
                    :value="nature"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="生产厂家" prop="factory.id">
                <el-input
                  v-if="dialogProps.action == 'view'"
                  :disabled="true"
                  v-model="bizFormModel.factory.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.factory"
                  value-key="id"
                  filterable
                  clearable
                  placeholder="请选择生产厂家"
                  @clear="
                  bizFormModel.factory = {
                    id: null,
                    name: null,
                  }
                "
                >
                  <el-option
                    v-for="factory in factory_List"
                    :key="factory.id"
                    :label="factory.name"
                    :value="factory"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="国药准字" prop="standardCode">
                <el-input
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.standardCode"
                  :maxlength="64"
                  :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入国药准字'
                "
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="本位码" prop="bitCode">
                <el-input
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.bitCode"
                  :maxlength="64"
                  :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入本位码'
                "
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="条形码" prop="barCode">
                <el-input
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.barCode"
                  :maxlength="64"
                  :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入条形码'
                "
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="医保编码" prop="insuranceCode">
                <el-input
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.insuranceCode"
                  :maxlength="64"
                  :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入医保编码'
                "
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="药品编码" prop="code">
                <el-input
                  disabled
                  v-model="bizFormModel.code"
                  :maxlength="64"
                  placeholder="保存后自动生成"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label='是否启用' prop='status'>
              <el-switch :disabled="dialogProps.action == 'view' && updateType=='0'" v-model='value'
                         active-color='#13ce66' inactive-color='#dbdfe6' active-value='1'
                         inactive-value='0'></el-switch>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider class="line"></el-divider>
        <!-- 规格 -->
        <div class="specifications">规格信息 &nbsp;&nbsp;<span style="color:#C0C0C0;">(例：2.5g*12片/袋)</span></div>
        <div class="specifications-box">
          <!-- 剂量 -->
          <div class="dose-box">
            <div class="dose-count">
              <el-form-item label="剂量" prop="dosis" style="display:flex;align-item:center">
                <el-input
                  style="width:100px"
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.dosis"
                  :maxlength="45"
                  :placeholder="
                    dialogProps.action == 'view' ? '' : '请输入剂量'
                  "
                ></el-input>
              </el-form-item>
            </div>
            <div class="dose-unit">
              <el-form-item label="" prop="dosisUnit.value">
                <el-input
                  v-if="dialogProps.action == 'view'"
                  :disabled="true"
                  v-model="bizFormModel.dosisUnit.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.dosisUnit"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="单位"
                  @clear="
                    bizFormModel.dosisUnit = {
                      value: null,
                      name: null,
                    }
                  "
                >
                  <el-option
                    v-for="dosisUnit in dosisUnit_List"
                    :key="dosisUnit.value"
                    :label="dosisUnit.name"
                    :value="dosisUnit"
                  ></el-option>
                </el-select>
              </el-form-item>
            </div>
          </div>
          <div style="margin: 0 20px 10px">*</div>
          <div class="dose-box">
            <div class="dose-count">
              <el-form-item label="制剂" prop="preparation" label-width="50px" style="display:flex;align-item:center">
                <el-input
                  style="width:100px"
                  :disabled="dialogProps.action == 'view'"
                  v-model="bizFormModel.preparation"
                  :maxlength="45"
                  :placeholder="
                    dialogProps.action == 'view' ? '' : '请输入制剂'
                  "
                ></el-input>
              </el-form-item>
            </div>
            <div class="dose-unit">
              <el-form-item label="" prop="preparationUnit.value">
                <el-input
                  v-if="dialogProps.action == 'view'"
                  :disabled="true"
                  v-model="bizFormModel.preparationUnit.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.preparationUnit"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="单位"
                  @clear="
                    bizFormModel.preparationUnit = {
                      value: null,
                      name: null,
                    }
                  "
                >
                  <el-option
                    v-for="preparationUnit in preparationUnit_List"
                    :key="preparationUnit.value"
                    :label="preparationUnit.name"
                    :value="preparationUnit"
                  ></el-option>
                </el-select>
              </el-form-item>
            </div>
          </div>
          <div style="margin: 0 20px 20px">/</div>
          <div class="dose-box">
            <div class="dose-count">
              <el-form-item label="包装" prop="pack.value" style="display:flex;align-item:center">
                <el-input
                  style="width:100px"
                  v-if="dialogProps.action == 'view'"
                  :disabled="true"
                  v-model="bizFormModel.pack.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.pack"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="单位"
                  style="width:100px"
                  @clear="
                    bizFormModel.pack = {
                      value: null,
                      name: null,
                    }
                  "
                >
                  <el-option
                    v-for="pack in pack_List"
                    :key="pack.value"
                    :label="pack.name"
                    :value="pack"
                  ></el-option>
                </el-select>
              </el-form-item>
            </div>
            <div class="dose-unit"></div>
          </div>
          <!-- <el-row>
            <el-col :span="24 / 2">

            </el-col>
            <el-col :span="24 / 2">

            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24 / 2"> </el-col>
          </el-row> -->
        </div>

        <el-divider class="line"></el-divider>
        <div class="specifications">销售信息</div>
        <div class="specifications-box2">
          <el-row>
            <el-col :span="24 / 3">
              <el-form-item
                class="price"
                label="售价"
                prop="price"
                :rules="
                bizFormModel.isUnpackSell === '1'
                  ? [
                      {
                        required: false,
                        message: '请输入售价',
                        trigger: 'blur',
                      },
                    ]
                  : [
                      {
                        required: true,
                        message: '请输入售价',
                        trigger: 'blur',
                      },
                    ]
              "
              >
                <el-input
                  v-if="dialogProps.action == 'view'"
                  :disabled="true"
                  v-model="bizFormModel.price"
                >
                  <template slot="append">
                    {{ bizFormModel.pack.name }}
                  </template>
                </el-input>
                <el-input
                  :type="type"
                  v-else
                  placeholder="请输入售价"
                  v-model="bizFormModel.price"
                  @focus="
                  (e) => {
                    if (bizFormModel.price == 0) {
                      bizFormModel.price = '';
                    }  else if(bizFormModel.price != 0 && bizFormModel.price != ''){
                      myFunction(bizFormModel.price)
                    }

                  }
                "
                  @blur="
                  (e) => {
                    if (bizFormModel.price == '') {
                      bizFormModel.price = 0;
                    }
                  }
                "
                  @input="getPrice"
                  type="number"
                  currency="CNY"
                  :precision="2"
                >
                  <template slot="append">
                    {{ bizFormModel.pack.name }}
                  </template>
                </el-input>
                <!-- <span class="unit3">/{{ bizFormModel.pack.name }}</span> -->
              </el-form-item>
            </el-col>
            <el-col :span="24 / 6">

            </el-col>
            <el-col :span="24 / 6">
              <el-form-item style="margin-letf:-1000px" label="允许拆零销售" prop="isUnpackSell">
                <el-switch
                  :disabled="dialogProps.action == 'view' && updateType=='0'"
                  v-model="bizFormModel.isUnpackSell"
                  active-color="#13ce66"
                  inactive-color="#dbdfe6"
                  active-value="1"
                  inactive-value="0"
                ></el-switch>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 3">
              <el-form-item
                class="price"
                label="拆零售价"
                prop="retailPrice"
                :rules="
                bizFormModel.isUnpackSell === '1'
                  ? formRules.retailPrice
                  : [
                      {
                        required: false,
                        message: '请输入拆零售价',
                        trigger: 'blur',
                      },
                    ]
              "
              >
                <el-input
                  v-if="dialogProps.action == 'view' && updateType=='0'"
                  type="number"
                  placeholder="请输入折零售价"
                  :disabled="true"
                  v-model="bizFormModel.retailPrice"
                >
                  <template slot="append">
                    {{ bizFormModel.preparationUnit.name }}
                  </template>
                </el-input>
                <el-input
                  v-else
                  type="number"
                  placeholder="请输入折零售价"
                  style="width:175px"
                  @focus="
                  (e) => {
                    if (bizFormModel.retailPrice == 0) {
                      bizFormModel.retailPrice = '';
                    }
                  }
                "
                  @blur="
                  (e) => {
                    if (bizFormModel.retailPrice == '') {
                      bizFormModel.retailPrice = 0;
                    }
                  }
                "
                  v-model="bizFormModel.retailPrice"
                  :disabled="bizFormModel.isUnpackSell === '0'"
                  currency="CNY"
                  :precision="2"
                >
                  <template slot="append">
                    {{ bizFormModel.preparationUnit.name }}
                  </template>
                </el-input>
                <!-- <span class="unit3">/{{ bizFormModel.preparationUnit.name }}</span> -->
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <el-divider class="line"></el-divider>
        <div class="specifications">功效说明</div>
        <div class="specifications-box2">
          <el-row class="info">
            <el-col :span="5">
              <el-form-item :label="bizFormModel.type.value=='medicalType_1'?'煎法':'用法'">
                <el-input
                  v-if="dialogProps.action == 'view'&&bizFormModel.type.value=='medicalType_1' && updateType=='0'"
                  :disabled="true"
                  v-model="bizFormModel.chineseMedicineUse.name"
                ></el-input>
                <el-input
                  v-else-if="dialogProps.action == 'view'&&bizFormModel.type.value!='medicalType_1' && updateType=='0'"
                  :disabled="true"
                  v-model="bizFormModel.westernMedicineUse.name"
                ></el-input>
                <el-select
                  v-else-if="bizFormModel.type.value=='medicalType_1'"
                  v-model="bizFormModel.chineseMedicineUse"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择"
                  @clear="
                  bizFormModel.chineseMedicineUse = {
                    value: null,
                    name: null,
                  }
                "
                >
                  <el-option
                    v-for="chineseMedicineUse in ChineseUseTimeOption"
                    :key="chineseMedicineUse.value"
                    :label="chineseMedicineUse.name"
                    :value="chineseMedicineUse"
                  ></el-option>
                </el-select>
                <el-select
                  v-else-if="bizFormModel.type.value!='medicalType_1'"
                  v-model="bizFormModel.westernMedicineUse"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择"
                  @clear="
                  bizFormModel.westernMedicineUse = {
                    value: null,
                    name: null,
                  }
                "
                >
                  <el-option
                    v-for="westernMedicineUse in WesternUseOption"
                    :key="westernMedicineUse.value"
                    :label="westernMedicineUse.name"
                    :value="westernMedicineUse"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item
                class="price"
                label="单次用量"
                prop="singleDosage"
              >
                <div>
                  <el-input
                    v-if="dialogProps.action == 'view' && updateType=='0'"
                    :disabled="true"
                    v-model="bizFormModel.singleDosage"
                    style="width:100px;float:left;"
                  >
                    <template slot="append">
                      {{ bizFormModel.preparationUnit.name }}
                    </template>
                  </el-input>
                  <el-input
                    v-else
                    placeholder="请输入"
                    v-model="bizFormModel.singleDosage"
                    style="width:80px;float:left;"
                    @focus="
                  (e) => {
                    if (bizFormModel.singleDosage == 0) {
                      bizFormModel.singleDosage = '';
                    }
                  }
                "
                    @blur="
                  (e) => {
                    if (bizFormModel.singleDosage == '') {
                      bizFormModel.singleDosage = 0;
                    }
                  }
                "

                    min="0"
                    oninput="value=value.replace(/[^\d]/g,'')"
                  >
                    <template slot="append">
<!--                      {{ bizFormModel.preparationUnit.name }}-->
                      {{ bizFormModel.dosisUnit.name }}
                    </template>
                  </el-input>
                  <!-- <span class="unit2">/{{ bizFormModel.preparationUnit.name }}</span> -->
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="5" v-if="bizFormModel.type.value!='medicalType_1'">
              <el-form-item label="频次">
                <el-input
                  v-if="dialogProps.action == 'view'&&updateType=='0'"
                  :disabled="true"
                  v-model="bizFormModel.frequency.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.frequency"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择"
                  @clear="
                  bizFormModel.frequency = {
                    value: null,
                    name: null,
                  }
                "
                >
                  <el-option
                    v-for="frequency in FrequencyOption"
                    :key="frequency.value"
                    :label="frequency.name"
                    :value="frequency"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5" v-if="bizFormModel.type.value!='medicalType_1'">
              <el-form-item label="天数">
                <el-input
                  v-if="dialogProps.action == 'view'&&updateType=='0'"
                  :disabled="true"
                  v-model="bizFormModel.days.name"
                ></el-input>
                <el-select
                  v-else
                  v-model="bizFormModel.days"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择"
                  @clear="
                  bizFormModel.days = {
                    value: null,
                    name: null,
                  }
                "
                >
                  <el-option
                    v-for="days in DayNumOption"
                    :key="days.value"
                    :label="days.name"
                    :value="days"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item
                class="price"
                label="总量上限"
                prop="total"
              >
                <el-input
                  v-if="dialogProps.action == 'view'&&updateType=='0'"
                  :disabled="true"
                  v-model="bizFormModel.total"
                  style="width:90px"
                >
                  <template slot="append">
                    {{ bizFormModel.preparationUnit.name }}
                  </template>
                </el-input>
                <el-input
                  v-else
                  placeholder="请输入"
                  v-model="bizFormModel.total"
                  style="width:90px"
                  @focus="
                  (e) => {
                    if (bizFormModel.total == 0) {
                      bizFormModel.total = '';
                    }
                  }
                "
                  @blur="
                  (e) => {
                    if (bizFormModel.total == '') {
                      bizFormModel.total = 0;
                    }
                  }
                "
                  oninput="value=value.replace(/[^\d]/g,'')"
                  min="0"
                >
                  <template slot="append">
                    {{ bizFormModel.preparationUnit.name }}
                  </template>
                </el-input>
                <!-- <span class="unit4">/{{ bizFormModel.preparationUnit.name }}</span> -->
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <el-divider class="line"></el-divider>
        <div class="specifications">其他信息</div>
        <div class="appendUnit">
          <el-row>
            <el-col :span="24 / 3">
              <el-form-item label="有效期预警" prop="indate">
                <el-input
                  :disabled="dialogProps.action == 'view'&&updateType=='0'"
                  v-model="bizFormModel.indate"
                  :maxlength="64"
                  placeholder="请输入预警天数"
                >
                  <template slot="append">天</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 3">
              <el-form-item label="库存下限" prop="inventoryFloor">
                <el-input
                  :disabled="dialogProps.action == 'view'&&updateType=='0'"
                  v-model="bizFormModel.inventoryFloor"
                  :maxlength="64"
                  placeholder="请输入库存下限"
                >
                  <template slot="append">{{ bizFormModel.pack.name }}</template>
                </el-input>
                <!-- <span class="unit3">/{{ bizFormModel.pack.name }}</span> -->
              </el-form-item>
            </el-col>
            <el-col :span="24 / 3">
              <el-form-item label="库存上限" prop="inventoryLimit">
                <el-input
                  :disabled="dialogProps.action == 'view'&&updateType=='0'"
                  v-model="bizFormModel.inventoryLimit"
                  :maxlength="64"
                  placeholder="请输入库存上限"
                >
                  <template style="padding:0 8px" slot="append">{{ bizFormModel.pack.name }}</template>
                </el-input>
                <!-- <span class="unit3">/{{ bizFormModel.pack.name }}</span> -->
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24 / 3">
              <el-form-item label="货位号" prop="locationNumber">
                <el-input
                  :disabled="dialogProps.action == 'view'&&updateType=='0'"
                  v-model="bizFormModel.locationNumber"
                  :maxlength="64"
                  placeholder="请输入货位号"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='updateType==="1"||dialogProps.action == "add"' :disabled="flage" type='primary' :plain='true'
                 @click='onSubmit("drugForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action == "add"' :disabled="flage" type='primary' :plain='true'
                 @click='onGoSubmit("drugForm")'>保 存 继 续</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
  </el-dialog>
</template>
<script>
  import {validatenull} from "@/utils/validate";
  import {listDictItemAll} from "@/api/sys/dictItem";
  import {listManufactureFactoryAll} from "@/api/basicdata/manufactureFactory";
  import {saveDrug, getDrugById, inventory} from "@/api/stock/drug";
  import BaseUI from "@/views/components/baseUI";
  import OperationIcon from "@/components/OperationIcon";
  import {BigNumber} from "bignumber.js";



  let pinyin = require('js-pinyin');
  export default {
    extends: BaseUI,
    name: "drug-form",
    components: {
      OperationIcon,
    },
    data() {
      return {
        bizFormModel: this.initFormModel(),
        tabIndex: "1",
        type_List: [], // 药品类型
        nature_List: [], // 性质
        factory_List: [], // 生产厂家
        dosisUnit_List: [], // 剂量单位
        preparationUnit_List: [], // 制剂单位
        pack_List: [], // 包装
        value: '1',
        flage: false,
        dialogProps: {
          visible: false,
          action: '',
          title: '',
        },
        formRules: {
          goodsName: [
            {required: true, message: "请输入药品名称", trigger: "blur"},
          ],
          "type.value": [
            {required: true, message: "请选择药品类型", trigger: "change"},
          ],
          standardCode: [
            {required: false, message: "请输入国药准字", trigger: "blur"},
          ],
          "preparationUnit.value": [
            {required: true, message: "请选择制剂单位", trigger: "change"},
          ],
          "pack.value": [
            {required: true, message: "请选择包装", trigger: "change"},
          ],
          price: [{required: true, message: "请输入售价", trigger: "blur"}],
          retailPrice: [
            {required: true, message: "请输入折后零售价", trigger: "blur"},
          ],
          preparation: [
            {required: true, message: "请输入制剂数量", trigger: "blur"},
          ],
        },
        FrequencyOption: [],
        WesternUseOption: [],
        DayNumOption: [],
        ChineseUseTimeOption: [],
        isSpecial: false,
        updateType: '0', //修改显示
        // 基本参数传入
      };
    },
    props: {
      // 权限
      permission: {
        type: Object,
      },

    },
    methods: {
      myFunction(){
        this.bizFormModel.price = BigNumber(this.bizFormModel.price).toFormat(4)
      },
      //动态修改拆零价格
      getPrice() {
        console.log(this.bizFormModel, '看啊可能');
        if (this.bizFormModel.isUnpackSell == '1' && this.bizFormModel.preparation != '') {
          this.bizFormModel.retailPrice = ((this.bizFormModel.price - 0) / (this.bizFormModel.preparation - 0)).toFixed(4)
        }
      },


      onSubmit(formName) {
        console.log(this.bizFormModel.company.id)
        console.log(this.currentUser.company.id)
        /*if (this.bizFormModel.company.id !== this.currentUser.company.id) {
          this.$message.error('无法修改机构药品信息，请联系机构修改药品基础信息')
          return
        }*/
        if (this.bizFormModel.type.value != 'medicalType_1') {
          if (this.bizFormModel.days.value && this.bizFormModel.frequency.value && this.bizFormModel.singleDosage && this.bizFormModel.total != 0) {

            let total = Math.ceil(
              BigNumber(this.bizFormModel.singleDosage)
                .multipliedBy(this.bizFormModel.frequency.value.split("_")[1])
                .multipliedBy(this.bizFormModel.days.name)
                .toNumber()
            );
            if (total > this.bizFormModel.total) {
              this.$message.error('总量上限小于功效说明计算总量')
              return
            }
          }
        }
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
        if (this.bizFormModel.type.value != 'medicalType_1') {
          if (this.bizFormModel.days.value && this.bizFormModel.frequency.value && this.bizFormModel.singleDosage && this.bizFormModel.total != 0) {

            let total = Math.ceil(
              BigNumber(this.bizFormModel.singleDosage)
                .multipliedBy(this.bizFormModel.frequency.value.split("_")[1])
                .multipliedBy(this.bizFormModel.days.name)
                .toNumber()
            );
            if (total > this.bizFormModel.total) {
              this.$message.error('总量上限小于功效说明计算总量')
              return
            }
          }
        }
        this.flage = true
        this.$refs[formName].validate(valid => {
          if (valid) {
            this.setLoad()
            this.bizFormModel.status = this.value
            saveDrug(this.bizFormModel).then(responseData => {
              this.flage = false
              if (responseData.code == 100) {
                //this.dialogProps.visible = false
                console.log(this.bizFormModel);
                this.bizFormModel.barCode = ""
                this.bizFormModel.bitCode = ""
                this.bizFormModel.brandName = ""
                this.bizFormModel.code = ""

                this.bizFormModel.dosis = ""
                this.bizFormModel.dosisUnit = {}
                this.bizFormModel.factory = {}
                this.bizFormModel.goodsName = ""
                this.bizFormModel.insuranceCode = ""
                this.bizFormModel.isUnpackSell = "1"
                this.bizFormModel.nature = {}
                // this.bizFormModel.pack={}
                this.bizFormModel.pinyinCode = ""
                this.bizFormModel.preparation = ""
                // this.bizFormModel.preparationUnit={}
                this.bizFormModel.price = ""
                this.bizFormModel.remarks = ""
                this.bizFormModel.retailPrice = ""
                this.bizFormModel.source = ""
                this.bizFormModel.standardCode = ""
                this.bizFormModel.status = ""
                this.bizFormModel.singleDosage = null
                this.bizFormModel.frequency = {}
                this.bizFormModel.days = {}
                this.bizFormModel.total = 0
                // this.bizFormModel.type={}
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
        saveDrug(this.bizFormModel).then(responseData => {
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
        this.dialogProps.title = "修改药品信息";
        this.initOptions(this.bizFormModel);
      },
      onDialogClose() {
        this.dialogProps.visible = false;
        this.$emit('save-finished', "1")
      },
      onDialogOpen() {
        this.$nextTick(() => {
          this.$refs["drugForm"].clearValidate();
        });
      },
      initFormModel(This) {
        return {
          id:"", // 药品id
          goodsName: "", // 药品名称
          company: {
            // 诊所id
            id: currentUser.company.id,
            name: null,
          },
          brandName: "", // 商品名称
          type: {
            // 药品类型
            value: null,
            name: null,
          },
          code: "", // 药品编码
          source: "", // 药品来源
          nature: {
            // 性质
            value: null,
            name: null,
          },
          factory: {
            // 生产厂家
            id: null,
            name: null,
          },
          standardCode: "", // 国药准字
          bitCode: "", // 本位码
          barCode: "", // 条形码
          insuranceCode: "", // 医保编码
          remarks: "", // 备注信息
          dosis: "", // 剂量
          dosisUnit: {
            // 剂量单位
            value: null,
            name: null,
          },
          preparation: "", // 制剂
          preparationUnit: {
            // 制剂单位
            value: null,
            name: null,
          },
          pack: {
            // 包装
            value: null,
            name: null,
          },
          price: 0, // 售价
          isUnpackSell: "1", // 允许拆零销售
          retailPrice: 0, // 拆开后零售价
          chineseMedicineUse: {
            name: null,
            value: null
          },
          westernMedicineUse: {
            name: null,
            value: null
          },
          days: {
            name: null,
            value: null
          },
          frequency: {
            name: null,
            value: null
          },
          singleDosage: 0,
          total: 0
        };
      },
      async initOptions(This) {
        let type_search = {
          params: [
            {
              columnName: "dict_type_id",
              queryType: "=",
              value: "1004078055755374603",
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
        let factory_search = {
          params: [],
          type: "1"
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
            value: '1',
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
        let dosisUnit_search = {
          params: [
            {
              columnName: "dict_type_id",
              queryType: "=",
              value: "1004406758192578588",
            },
          ],
        };
        // 字段对应表上filter条件
        dosisUnit_search.params.push.apply(dosisUnit_search.params, []);
        // 数据权限: 字典项sys_dict_item
        this.pushDataPermissions(
          dosisUnit_search.params,
          this.$route.meta.routerId,
          "4005"
        );
        this.dosisUnit_List.splice(0, this.dosisUnit_List.length);
        listDictItemAll(dosisUnit_search).then((responseData) => {
          this.dosisUnit_List = responseData.data;
          if (this.dialogProps.action == "add") {
            this.bizFormModel.dosisUnit = this.dosisUnit_List[0];
          }

        });
        let preparationUnit_search = {
          params: [
            {
              columnName: "dict_type_id",
              queryType: "=",
              value: "1004406758192578593",
            },
          ],
        };
        // 字段对应表上filter条件
        preparationUnit_search.params.push.apply(
          preparationUnit_search.params,
          []
        );
        // 数据权限: 字典项sys_dict_item
        this.pushDataPermissions(
          preparationUnit_search.params,
          this.$route.meta.routerId,
          "4005"
        );
        this.preparationUnit_List.splice(0, this.preparationUnit_List.length);
        listDictItemAll(preparationUnit_search).then((responseData) => {
          this.preparationUnit_List = responseData.data;
          if (this.dialogProps.action == "add") {
            this.bizFormModel.preparationUnit = this.preparationUnit_List[0];
          }

        });
        let pack_search = {
          params: [
            {
              columnName: "dict_type_id",
              queryType: "=",
              value: "1004406758192578597",
            },
          ],
        };
        // 字段对应表上filter条件
        pack_search.params.push.apply(pack_search.params, []);
        // 数据权限: 字典项sys_dict_item
        this.pushDataPermissions(
          pack_search.params,
          this.$route.meta.routerId,
          "4005"
        );
        this.pack_List.splice(0, this.pack_List.length);

        listDictItemAll(pack_search).then((responseData) => {
          this.pack_List = responseData.data;
          if (this.dialogProps.action == "add") {
            this.bizFormModel.pack = this.pack_List[0];
          }

        });
      },
      //输入框拼音码
      pinyinInput(value) {
        this.bizFormModel.pinyinCode = pinyin.getCamelChars(value)

      },
      GetAllOption() {
        this.GetOption("1014474470772899990");
        this.GetOption("1014474470772900028");
        this.GetOption("1014474470772900052");
        this.GetOption("1014474470772900058");
      },
      GetOption(optionId) {
        let model = {
          params: [
            {
              columnName: "dict_type_id",
              queryType: "=",
              value: optionId,
            },
          ],
        };
        listDictItemAll(model).then((responseData) => {
          if (optionId == "1014474470772899981") {
            this.ChineseUseOption = responseData.data;
            if (this.isSpecial) {
              this.ChineseUseOption = this.ChineseUseOption.filter(
                (item) => item.name == "水冲"
              );
            }
          } else if (optionId == "1014474470772899990")
            this.FrequencyOption = responseData.data;
          else if (optionId == "1014474470772900028")
            this.WesternUseOption = responseData.data;
          else if (optionId == "1014474470772900052")
            this.DayNumOption = responseData.data;
          else if (optionId == "1014474470772900058")
            this.ChineseUseTimeOption = responseData.data;
        });
      },
    },
    watch: {
      "bizFormModel.isUnpackSell": function (newVal, oldVal) {
        if (newVal === "0") {
          this.bizFormModel.retailPrice = "";
        }
      },
      // 售价
      "bizFormModel.price": function (newValue, oldValue) {
        if (newValue < 0) {
          this.$message({
            type: "error",
            message: "价格不可小于0元",
          });
          return;
        }
      },
      //折后价
      "bizFormModel.retailPrice": function (newValue, oldValue) {
        if (this.bizFormModel.isUnpackSell === "1") {
          if (newValue < 0) {
            this.$message({
              type: "error",
              message: "价格不可小于0元",
            });
            return;
          }
          // this.bizFormModel.price = newValue;
        }
      },
      // "bizFormModel.type.value": function (newValue, oldValue) {
      //   // 中药
      //   if (newValue === "medicalType_1") {
      //     this.bizFormModel.dosis = 1;
      //     this.bizFormModel.isUnpackSell = "0";
      //     this.bizFormModel.dosisUnit = {
      //       value: "medicalDosisUnit_0",
      //       name: "g",
      //     };
      //     // this.bizFormModel.preparationUnit.value='medicalPreparationUnit_3'
      //     //  this.bizFormModel.preparationUnit.name='g'

      //     this.bizFormModel.preparation = "1";
      //   }
      // },

      //监听药品类型
      "bizFormModel.type.value": function (newValue, oldValue) {
        console.log(newValue, 'jiushi');
        if (newValue == "medicalType_0") {
          this.formRules.standardCode[0].required = true
        } else if (newValue == "medicalType_2") {
          this.formRules.standardCode[0].required = true
        } else {
          this.formRules.standardCode[0].required = false
        }
      }
    },
    mounted: function () {
      let specialCompany = JSON.parse(sessionStorage.getItem("currentCompany"));
      if (specialCompany && specialCompany.id == "1088657523871555640") {
        this.isSpecial = true;
      }
      this.GetAllOption();


      this.$nextTick(() => {
        this.$on("openViewDrugDialog", function (drug) {
          this.dialogProps.action = "view";
          this.dialogProps.title = "查看药品信息";
          this.bizFormModel = {...this.initFormModel(), ...drug};
          this.updateType = '0'
          this.initOptions(this.bizFormModel);
          this.value = this.bizFormModel.status
          console.log(this.bizFormModel, 'kankan');
          this.tabIndex = "1";
          console.log(this.updateType, '状态');
          this.dialogProps.visible = true;
        });
        this.$on("openEditDrugDialog", function (drug) {
          if ((drug.stock.storageStock - drug.stock.usedStock - drug.stock.reimburseStock) > 0) {
            this.dialogProps.action = "view";
          } else {
            this.dialogProps.action = "edit";
          }
          this.dialogProps.title = "修改药品信息";
          this.bizFormModel = {...this.initFormModel(), ...drug};
          this.initOptions(this.bizFormModel);
          this.value = this.bizFormModel.status
          this.tabIndex = "1";
          this.updateType = '1'
          this.dialogProps.visible = true;
        });
        this.$on("openAddDrugDialog", function () {
          this.dialogProps.action = "add";
          this.dialogProps.title = "添加药品信息";
          this.bizFormModel = this.initFormModel();
          this.initOptions(this.bizFormModel);
          this.tabIndex = "1";
          this.dialogProps.visible = true;
        });
        this.$on("openCopyDrugDialog", function (drug) {
          this.dialogProps.action = "add";
          this.dialogProps.title = "添加药品信息";
          this.bizFormModel = {...this.initFormModel(), ...drug};
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
  /deep/ .el-dialog__body {
    padding: 30px 20px 0;
  }

  /deep/ .info .el-form-item__label {
    width: 80px !important;
  }

  /deep/ .info .el-form-item__content {
    margin-left: 80px !important;
  }

  .edit-form {
    margin-top: -50px;
  }

  .specifications {
    margin-top: -30px;
  }

  .specifications-box {
    margin-top: -10px;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .specifications-box2 {
    margin-top: -10px;
  }

  .dose-box,
  .dose-uint {
    display: flex;
    align-items: end;
    justify-content: center;
  }

  /* .dose-count {
    width: 100px;
  } */
  /deep/ .dose-count .el-form-item__label {
    width: auto !important;
    margin-left: 0 !important;
  }

  /deep/ .dose-count .el-form-item__content {
    width: auto !important;
    margin-left: 0 !important;
  }

  .dose-unit {
    width: 70px;
  }

  /deep/ .dose-unit .el-form-item__content,
  .dose-unit .el-form-item__label {
    width: auto !important;
    margin-left: 0 !important;
  }

  /deep/ .dose-unit .el-input__inner {
    padding-right: 8px !important;
  }
</style>
<style scoped>
  .specifications {
    width: 100%;
    font-weight: bold;
    padding-bottom: 8px;

    padding: 20px;
    color: #333;
  }

  /deep/ .price {
    position: relative !important;
  }

  .price .unit {
    position: absolute;
    right: 15px;
    top: 50%;
    transform: translateY(-50%);
  }

  .unit2 {
    position: absolute;
    right: -28px;
    top: 50%;
    transform: translateY(-50%);
  }

  .unit3 {
    position: absolute;
    right: -13px;
    top: 50%;
    transform: translateY(-50%);
  }

  .unit4 {
    position: absolute;
    right: -1px;
    top: 50%;
    transform: translateY(-50%);
  }

  .line {
    margin-top: -10px;
  }

</style>
<style>
  .appendUnit .el-input-group__append {
    padding-right: 5px !important;
    padding-left: 5px !important;
  }

  .specifications-box2 .el-input-group__append {
    padding: 0 5px !important;
  }

  .specifications-box2 .el-input-group__append {
    padding: 0 5px !important;
  }
</style>
