<template>
    <el-row v-loading="loading">
    <el-card class="retailCharge">
        <!-- 药品材料切换 -->
        <div style="margin-top:-20px;margin-left:-20px">
            <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
                <el-tab-pane label="零售药品" name="0">

                </el-tab-pane>
                <el-tab-pane label="零售材料" name="1">

                </el-tab-pane>
            </el-tabs>
        </div>

        <!-- 零售药品 -->
        <div v-if="activeName=='0'">
        <el-form
        :model="bizFormModel"
        :rules="formRules"
        ref="bizFormModel"
        label-width="120px"
        label-position="right"
        style="marginTop: 10px"
        class="edit-form"
        >
            <h4>患者信息</h4>
            <div class="patientMessage">
                <el-row>
                    <el-col :span='24/4'>
                         <el-form-item label="患者姓名" prop="name">
                            <el-select
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
                     <el-col :span="24 / 4">
                        <el-form-item label="出生日期" prop="birthday">
                            <el-date-picker
                            v-model="bizFormModel.birthday"
                            type="date"
                            @change="birthdayChanges"
                            :picker-options="setDisabled"
                            value-format="yyyy-MM-dd"
                            :placeholder="
                                '请输入出生日期'
                            "

                            ></el-date-picker>

                        </el-form-item>
                    </el-col>
                     <el-col :span="24 / 4">
                    <el-form-item label="年龄" prop="age">
                        <el-col :span="24 / 2">
                        <el-input

                            v-model="bizFormModel.age"
                            :maxlength="20"
                            :placeholder="
                            '年龄'
                            "
                        >
                        <template slot="append">
                            岁
                        </template>
                        </el-input>
                        </el-col>
                        <el-col :span="24 / 2">
                        <el-input

                            v-model="bizFormModel.month"
                            :maxlength="20"
                            :placeholder="'月'"
                        >
                        <template slot="append">
                            月
                        </template>
                        </el-input>
                        </el-col>
                    </el-form-item>
                    </el-col>
                    <el-col :span="24 / 4">
                    <el-form-item label="性别" prop="gender.name">
                        <el-select
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
                     <el-col :span="24 / 4">
                        <el-form-item label="联系方式" prop="phone">
                            <el-input
                            v-model="bizFormModel.phone"
                            :maxlength="45"
                            :placeholder="
                               '请输入联系方式'
                            "
                            ></el-input>
                        </el-form-item>
                        </el-col>
                </el-row>
            </div>

        <h4>药品信息</h4>
        <!-- 关于药品 -->
         <el-popover
            placement="top-start"
            width="700"
            trigger="focus"
            >
            <el-table
                :data="DrugMedicineTable"
                :height="300"
                border
                highlight-current-row
                @row-click="RowClickWesternTable"
            >
                <el-table-column
                prop="drug.type.name"
                label="药品类型"
                ></el-table-column>
                <el-table-column
                prop="drug.goodsName"
                label="药品名称"
                ></el-table-column>
                <el-table-column prop="gg" label="规格" width="120">
                <template slot-scope="scope">
                    {{ scope.row.drug.dosis
                    }}{{ scope.row.drug.dosisUnit.name }} *
                    {{ scope.row.drug.preparation
                    }}{{ scope.row.drug.preparationUnit.name }}/{{
                    scope.row.drug.pack.name
                    }}
                </template>
                </el-table-column>
                <el-table-column
                prop="drug.factory.name"
                label="厂家"
                width="100"
                ></el-table-column>
                <el-table-column label="包装价" width="80">
                <template slot-scope="scope">
                    {{ scope.row.drug.price + "/" + scope.row.drug.pack.name }}
                </template>
                </el-table-column>
                <el-table-column label="零售价" width="80">
                <template slot-scope="scope">
                    {{
                    scope.row.drug.isUnpackSell == 1
                        ? scope.row.drug.retailPrice +
                        "/" +
                        scope.row.drug.preparationUnit.name
                        : "--"
                    }}
                </template>
                </el-table-column>
                <el-table-column
                prop="surplusStock"
                label="可用库存"
                width="100"
                >
                <template slot-scope="scope">
                    {{
                    Math.floor(
                        scope.row.surplusStock / scope.row.drug.preparation
                    ) > 0
                        ? Math.floor(
                            scope.row.surplusStock /
                            scope.row.drug.preparation
                        ) +
                        scope.row.drug.pack.name +
                        (scope.row.surplusStock %
                            scope.row.drug.preparation >
                        0
                            ? (scope.row.surplusStock %
                                scope.row.drug.preparation) +
                            scope.row.drug.preparationUnit.name
                            : "")
                        : scope.row.surplusStock +
                        scope.row.drug.preparationUnit.name
                    }}
                </template>
                </el-table-column>
            </el-table>
            <el-input
                prefix-icon="el-icon-plus"
                suffix-icon="el-icon-search"
                style="width: 30%"
                slot="reference"
                ref="WesternInput"
                v-model="SearchDrugInput"
                @input="GetWesternTable"
                @focus="GetWesternTable"
                placeholder="输入药品名称或拼音码"
            ></el-input>
            </el-popover>
            <!-- 药品点击回显表格 -->
            <div style="padding-top:20px;">
                  <el-table
                :data="DrugList"
                :height="300"
                border
                highlight-current-row
            >
               <el-table-column
                prop="drug.goodsName"
                label="药品名称"
                ></el-table-column>
                <el-table-column
                prop=""
                label="药品规格"
                >
                <template slot-scope="scope">
                    {{scope.row.drug.dosis+scope.row.drug.dosisUnit.name+'*'+scope.row.drug.preparation+scope.row.drug.preparationUnit.name+'/'+scope.row.drug.pack.name}}
                </template>
                </el-table-column>
                <el-table-column
                prop="drug.type.name"
                label="药品类型"
                ></el-table-column>
                 <el-table-column
                prop="drug.factory.name"
                label="生产厂商"
                ></el-table-column>
                <!-- <el-table-column
                prop="drug.goodsName"
                label="药品名称"
                ></el-table-column>
                <el-table-column prop="gg" label="规格" width="120">
                <template slot-scope="scope">
                    {{ scope.row.drug.dosis
                    }}{{ scope.row.drug.dosisUnit.name }} *
                    {{ scope.row.drug.preparation
                    }}{{ scope.row.drug.preparationUnit.name }}/{{
                    scope.row.drug.pack.name
                    }}
                </template>
                </el-table-column> -->
                <el-table-column label="包装价" >
                <template slot-scope="scope">
                    {{ scope.row.drug.price + "/" + scope.row.drug.pack.name }}
                </template>
                </el-table-column>
                <el-table-column label="零售价" >
                <template slot-scope="scope">
                    {{
                    scope.row.drug.isUnpackSell == 1
                        ? scope.row.drug.retailPrice +
                        "/" +
                        scope.row.drug.preparationUnit.name
                        : "--"
                    }}
                </template>
                </el-table-column>
                <el-table-column
                prop="surplusStock"
                label="可用库存"
                >
                <template slot-scope="scope">
                    {{
                    Math.floor(
                        scope.row.surplusStock / scope.row.drug.preparation
                    ) > 0
                        ? Math.floor(
                            scope.row.surplusStock /
                            scope.row.drug.preparation
                        ) +
                        scope.row.drug.pack.name +
                        (scope.row.surplusStock %
                            scope.row.drug.preparation >
                        0
                            ? (scope.row.surplusStock %
                                scope.row.drug.preparation) +
                            scope.row.drug.preparationUnit.name
                            : "")
                        : scope.row.surplusStock +
                        scope.row.drug.preparationUnit.name
                    }}
                </template>
                </el-table-column>
                    <el-table-column
                      prop="drugTracCodg"
                      label="药品追溯码"
                    > <template slot-scope="scope">
                      <el-button :type="scope.row.completed ? 'success' : 'warning'" @click="openDrugDialog(scope)">输入追溯码</el-button>
                    </template></el-table-column>
                <el-table-column
                prop=""
                label="数量">
                <template slot-scope="scope">
                    <el-input
                    v-model="scope.row.number"
                    @input="compute(scope.row)"
                    oninput="value=value.replace(/[^\d.]/g,'')"
                    >

                    </el-input>
                </template>
                </el-table-column>

                 <el-table-column
                prop="type"
                label="单位">
                <template slot-scope="scope">
                    <el-input
                    disabled
                    v-if="scope.row.drug.isUnpackSell=='0'"
                    v-model="scope.row.drug.pack.name"
                    >

                    </el-input>
                     <el-select
                              v-else
                              v-model="scope.row.type"
                              placeholder=""
                              @change="compute(scope.row)"
                            >
                              <el-option
                                :key="scope.row.drug.pack.value"
                                :label="scope.row.drug.pack.name"
                                :value="scope.row.drug.pack"
                              >
                              </el-option>
                               <el-option
                                :key="scope.row.drug.preparationUnit.value"
                                :label="scope.row.drug.preparationUnit.name"
                                :value="scope.row.drug.preparationUnit"
                              >
                              </el-option>
                            </el-select>
                </template>
                </el-table-column>
                 <el-table-column
                    prop="allFee"
                    label="合计金额(元)"
                    width="120"
                    align="center"
                    ></el-table-column>
                <el-table-column label="操作" >
                <template slot-scope="scope">
                   <el-button type="text" @click="deleteDrugList(scope.row,scope.$index)">删除</el-button>
                </template>
                </el-table-column>
            </el-table>
            </div>

            <!-- 总框 -->

            <div class="statistical_box">
                <div style="padding-top:0px;margin-top:20px;">
                    <div style="float:left;">
                    <span>总金额:{{totalAllFee}}元</span>
                </div>
                  <el-form-item>
                <el-button type="primary" @click="notarize('bizFormModel')">
                    确认收费
                </el-button>
                  </el-form-item>
                </div>
            </div>

        </el-form>
        </div>

        <!-- 零售材料 -->
        <div v-else>
             <el-form
        :model="stuffBizFormModel"
        :rules="stuffFormRules"
        ref="stuffBizFormModel"
        label-width="120px"
        label-position="right"
        style="marginTop: 10px"
        class="edit-form"
        >
            <h4>患者信息</h4>
            <div class="patientMessage">
                <el-row>
                    <el-col :span='24/4'>
                         <el-form-item label="患者姓名" prop="name">
                            <el-select
                            v-model="stuffBizFormModel.name"
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
                     <el-col :span="24 / 4">
                        <el-form-item label="出生日期" prop="birthday">
                            <el-date-picker
                            v-model="stuffBizFormModel.birthday"
                            type="date"
                            @change="birthdayChanges"
                            :picker-options="setDisabled"
                            value-format="yyyy-MM-dd"
                            :placeholder="
                                '请输入出生日期'
                            "

                            ></el-date-picker>

                        </el-form-item>
                    </el-col>
                     <el-col :span="24 / 4">
                    <el-form-item label="年龄" prop="age">
                        <el-col :span="24 / 2">
                        <el-input

                            v-model="stuffBizFormModel.age"
                            :maxlength="20"
                            :placeholder="
                            '年龄'
                            "
                        >
                        <template slot="append">
                            岁
                        </template>
                        </el-input>
                        </el-col>
                        <el-col :span="24 / 2">
                        <el-input

                            v-model="stuffBizFormModel.month"
                            :maxlength="20"
                            :placeholder="'月'"
                        >
                        <template slot="append">
                            月
                        </template>
                        </el-input>
                        </el-col>
                    </el-form-item>
                    </el-col>
                    <el-col :span="24 / 4">
                    <el-form-item label="性别" prop="gender.name">
                        <el-select
                        v-model="stuffBizFormModel.gender"
                        value-key="value"
                        filterable
                        placeholder="请选择性别"
                        @clear="
                            stuffBizFormModel.gender = {
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
                     <el-col :span="24 / 4">
                        <el-form-item label="联系方式" prop="phone">
                            <el-input
                            v-model="stuffBizFormModel.phone"
                            :maxlength="45"
                            :placeholder="
                               '请输入联系方式'
                            "
                            ></el-input>
                        </el-form-item>
                        </el-col>
                </el-row>
            </div>

        <h4>材料信息</h4>
        <!-- 关于材料 -->
         <el-popover
            placement="top-start"
            width="700"
            trigger="focus"
            >
            <el-table
                :data="StuffMedicineTable"
                :height="300"
                border
                highlight-current-row
                @row-click="RowClickStuffTable"
            >
                <el-table-column
                prop="stuff.type.name"
                label="材料类型"
                ></el-table-column>
                <el-table-column
                prop="stuff.name"
                label="材料名称"
                ></el-table-column>
                <el-table-column prop="gg" label="规格" width="120">
                <template slot-scope="scope">
                    {{ scope.row.stuff.packNumber
                    }}*{{ scope.row.stuff.minUnit.name }} /
                    {{ scope.row.stuff.packUnit.name
                    }}
                </template>
                </el-table-column>
                <el-table-column
                prop="stuff.factory.name"
                label="厂家"
                width="100"
                ></el-table-column>
                <el-table-column label="包装价" width="80">
                <template slot-scope="scope">
                    {{ scope.row.stuff.priceOutSell +"/" +scope.row.stuff.packUnit.name }}
                </template>
                </el-table-column>
                <el-table-column label="零售价" width="80">
                <template slot-scope="scope">
                    {{
                    scope.row.stuff.isUnpackSell == "1"
                                  ? scope.row.stuff.retailPrice +
                                    "/" +
                                    scope.row.stuff.minUnit.name
                                  : "--"
                    }}
                </template>
                </el-table-column>
                <el-table-column
                prop="surplusStock"
                label="可用库存"
                width="100"
                >
                <template slot-scope="scope">
                    {{
                    Math.floor(
                        scope.row.surplusStock / scope.row.stuff.packNumber
                    ) > 0
                        ? Math.floor(
                            scope.row.surplusStock /
                            scope.row.stuff.packNumber
                        ) +
                        scope.row.stuff.packUnit.name +
                        (scope.row.surplusStock %
                            scope.row.stuff.packNumber >
                        0
                            ? (scope.row.surplusStock %
                                scope.row.stuff.packNumber) +
                            scope.row.stuff.minUnit.name
                            : "")
                        : scope.row.surplusStock +
                        scope.row.stuff.minUnit.name
                    }}
                </template>
                </el-table-column>
            </el-table>
            <el-input
                prefix-icon="el-icon-plus"
                suffix-icon="el-icon-search"
                style="width: 30%"
                slot="reference"
                ref="WesternInput"
                v-model="SearchStuffInput"
                @input="GetStuffTable"
                @focus="GetStuffTable"
                placeholder="输入材料名称或拼音码"
            ></el-input>
            </el-popover>
            <!-- 材料点击回显表格 -->
            <div style="padding-top:20px;">
                  <el-table
                :data="StuffList"
                :height="300"
                border
                highlight-current-row
            >
               <el-table-column
                prop="stuff.name"
                label="材料名称"
                ></el-table-column>
                <el-table-column
                prop=""
                label="材料规格"
                >
                <template slot-scope="scope">
                    {{scope.row.stuff.packNumber+'*'+scope.row.stuff.minUnit.name+'/'+scope.row.stuff.packUnit.name}}
                </template>
                </el-table-column>
                <el-table-column
                prop="stuff.type.name"
                label="材料类型"
                ></el-table-column>
                 <el-table-column
                prop="stuff.factory.name"
                label="生产厂商"
                ></el-table-column>

                <el-table-column label="包装价" >
                <template slot-scope="scope">
                    {{ scope.row.stuff.priceOutSell +"/" +scope.row.stuff.packUnit.name }}
                </template>
                </el-table-column>
                <el-table-column label="零售价" >
                <template slot-scope="scope">
                    {{
                    scope.row.stuff.isUnpackSell == "1"
                                  ? scope.row.stuff.retailPrice +
                                    "/" +
                                    scope.row.stuff.minUnit.name
                                  : "--"
                    }}
                </template>
                </el-table-column>
                <el-table-column
                prop="surplusStock"
                label="可用库存"
                >
                <template slot-scope="scope">
                    {{
                    Math.floor(
                        scope.row.surplusStock / scope.row.stuff.packNumber
                    ) > 0
                        ? Math.floor(
                            scope.row.surplusStock /
                            scope.row.stuff.packNumber
                        ) +
                        scope.row.stuff.packUnit.name +
                        (scope.row.surplusStock %
                            scope.row.stuff.packNumber >
                        0
                            ? (scope.row.surplusStock %
                                scope.row.stuff.packNumber) +
                            scope.row.stuff.minUnit.name
                            : "")
                        : scope.row.surplusStock +
                        scope.row.stuff.minUnit.name
                    }}
                </template>
                </el-table-column>
                <el-table-column
                prop=""
                label="数量">
                <template slot-scope="scope">
                    <el-input
                    v-model="scope.row.number"
                    @input="compute(scope.row)"
                    oninput="value=value.replace(/[^\d.]/g,'')"
                    >

                    </el-input>
                </template>
                </el-table-column>
                 <el-table-column
                prop="type"
                label="单位">
                <template slot-scope="scope">
                    <el-input
                    disabled
                    v-if="scope.row.stuff.isUnpackSell=='0'"
                    v-model="scope.row.stuff.packUnit.name"
                    >

                    </el-input>
                     <el-select
                              v-else
                              v-model="scope.row.type"
                              placeholder=""
                              @change="compute(scope.row)"
                            >
                              <el-option
                                :key="scope.row.stuff.packUnit.value"
                                :label="scope.row.stuff.packUnit.name"
                                :value="scope.row.stuff.packUnit"
                              >
                              </el-option>
                               <el-option
                                :key="scope.row.stuff.minUnit.value"
                                :label="scope.row.stuff.minUnit.name"
                                :value="scope.row.stuff.minUnit"
                              >
                              </el-option>
                            </el-select>
                </template>
                </el-table-column>
                 <el-table-column
                    prop="allFee"
                    label="合计金额(元)"
                    width="120"
                    align="center"
                    ></el-table-column>
                <el-table-column label="操作" >
                <template slot-scope="scope">
                   <el-button type="text" @click="deleteDrugList(scope.row,scope.$index)">删除</el-button>
                </template>
                </el-table-column>
            </el-table>
            </div>

            <!-- 总框 -->

            <div class="statistical_box">
                <div style="padding-top:0px;margin-top:20px;">
                    <div style="float:left;">
                    <span>总金额:{{totalAllFee}}元</span>
                </div>
                  <el-form-item>
                <el-button type="primary" @click="notarize('stuffBizFormModel')">
                    确认收费
                </el-button>
                  </el-form-item>
                </div>
            </div>

        </el-form>
        </div>
      <el-dialog @keydown.native="handleKeydown" :modal="false"  :visible.sync="drugDialogVisible" title="输入药品追溯码" @open="focusInput" @close="resetInput">
        <el-input
          v-model="currentInput"
          placeholder="请输入药品追溯码"
          ref="inputFocus"
        ></el-input>
        <span slot="footer" class="dialog-footer">
        <el-button @click="submitInput" type="primary">确定</el-button>
        <el-button @click="drugDialogVisible = false">取消</el-button>
      </span>
      </el-dialog>


        <!-- 确认收费弹出框 -->
        <el-dialog
      title="收费"
      :visible.sync="chargeDialogVisible"
      width="50%"
      :close-on-click-modal="false"
      :before-close="chargeFormCancel"
    >
      <div style="margin-top:-50px">
        <el-divider></el-divider>
        <div id="chargeTotalFee_Container">
          <div id="chargeTotalFee_Title">实收金额（元）：</div>
          <div id="chargeTotalFee_Fee" v-if="this.practicalTotalAllFee">
            {{  this.practicalTotalAllFee}}元
          </div>
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
           <div id="chargeTotalFee_Title">应收金额（元）：</div>
            <div id="chargeTotalFee_Fee" v-if="this.totalAllFee">
            {{  this.totalAllFee}}元
          </div>
        </div>
        <el-form ref="chargeForm" :model="totalAllFee" label-width="80px">
          <el-row>

            <el-col :span="8">
              <el-form-item label="折扣">
                <el-input
                  v-model="discount"
                  @input="GetAmountReceived"
                  placeholder="折扣"
                >
                  <template slot="append">折</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8" :offset="1">
              <el-form-item label="减免金额">
                <el-input
                  v-model="amountOfRemission"
                  placeholder="减免金额"
                  disabled
                  @input="GetAmountReceived"
                >
                  <template slot="append">元</template>
                </el-input>
              </el-form-item>
            </el-col>
            <!-- <el-col :span="10" :offset="1">
              <el-form-item label="减免金额">
                <el-input v-model="chargeForm.amountReceivable">
                  <template slot="append">元</template>
                </el-input>
              </el-form-item>
            </el-col> -->
          </el-row>
          <el-row v-if="paymentType=='payType_0'">
             <el-col :span="8">
              <el-form-item label="实付金额">
                <el-input
                  v-model="amountPaid"
                  @input=" inputAmountPaid"
                  placeholder="实付金额"
                >
                <template slot="append">元</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8" :offset="1">
              <el-form-item label="找补金额">
                <el-input
                  v-model="amountOfChange"
                  placeholder="找补金额"
                  disabled
                >
                  <template slot="append">元</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="20">
              <el-form-item label="支付方式">
                <el-radio-group v-model="paymentType">
                  <el-radio label="payType_0">现金</el-radio>
                  <el-radio label="payType_1">支付宝</el-radio>
                  <el-radio label="payType_2">微信</el-radio>
                  <el-radio label="payType_3">银行卡</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="备注">
                <el-input type="textarea" :rows="3" v-model="meno"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <el-divider></el-divider>
      </div>
      <div slot="footer">
        <el-button @click="chargeFormCancel">取 消</el-button>
        <el-button type="primary" @click="chargeBtn">确认收费</el-button>
      </div>
    </el-dialog>
    </el-card>
    </el-row>
</template>
<script>

import { validatenull } from "@/utils/validate";
import { listClinicOfficeAll } from "@/api/org/clinicOffice";
import {
  listPatientAll,
  savePatient,
  getPatientById,
} from "@/api/outpatient/patient";
import { listDictItemAll } from "@/api/sys/dictItem";
import { saveRegistration,listDoctorsAll } from "@/api/outpatient/registration";
import {listAll} from "@/api/stock/medicinalStockControl";
import BaseUI from "@/views/components/baseUI";
import OperationIcon from "@/components/OperationIcon";
import { saveTollInfo } from "@/api/toll/tollInfo";
import { BigNumber } from "bignumber.js";
import { saveTollTollInfo } from "@/api/toll/tollInfo";
export default {
    extends: BaseUI,
    data(){
        return{
            activeName:"0",
            bizFormModel: this.initPatientModel() ,
            PatientFormModel: this.initPatientModel(),
            stuffBizFormModel:this.initPatientModel(),
            StuffPatientForModel:this.initPatientModel(),
            formRules:{
                name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
                birthday:[{required: true, message: "请选择出生日期", trigger: "blur"}],
                age: [{ required: true, message: "请输入年龄", trigger: "blur" }],
                'gender.name': [{ required: true, message: "请选择性别", trigger: 'change' }],
            },
            stuffFormRules:{
                name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
                birthday:[{required: true, message: "请选择出生日期", trigger: "blur"}],
                age: [{ required: true, message: "请输入年龄", trigger: "blur" }],
                'gender.name': [{ required: true, message: "请选择性别", trigger: 'change' }],
            },
            patientId_List:[],  //获取患者信息
            gender_List:[],   //获取性别
            SearchDrugInput:"", //药品搜索
            SearchStuffInput:"", //材料搜索
            DrugMedicineTable:[], //药品表格
            StuffMedicineTable:[], //材料表格
            DrugList:[],//药品集合
            StuffList:[], //材料集合
            number:0,
            totalAllFee:0,  //应收总金额
            practicalTotalAllFee:0, //实收总金额
            discount:0,   //折扣
            amountOfRemission:"0", //优惠金额
            chargeDialogVisible:false,
            paymentType:"payType_0",    //支付方式
            meno:"",            //备注
            amountPaid:0, //实付金额
            amountOfChange:0,//找补金额
           //追溯码弹窗控制
           drugDialogVisible: false,
           currentInput: '',
           inputCount: 0, // 当前输入次数
           maxInputCount: 0, // 最大输入次数
           currentRow: null, // 当前操作的行
           allInputs: [], // 所有输入的追溯码
        }
    },
    methods:{
      handleKeydown(event) {
        if (event.key === 'Enter') {
          // 调用确定操作
          this.submitInput();
        }
      },
      focusInput() {
        // 使用 ref 来访问输入框并调用 focus() 方法
        this.$nextTick(() => {
          this.$refs.inputFocus.focus(); // 自动聚焦输入框
        });
      },
      openDrugDialog(scope) {
        if ( scope.row.number <= 0 || ! scope.row.number) {
          this.$message.error('请先输入有效的数量与是否拆零！');
          return; // 如果数量无效，停止执行
        }
        this.currentRow = scope.row; // 记录当前行
        this.inputCount = 0; // 重置输入计数
        this.allInputs = []; // 清空之前的输入
        this.drugDialogVisible = true;
        // 根据当前行的数量设置基础输入次数和最大输入次数
        const quantity = this.currentRow.number;
        this.maxInputCount = this.currentRow.isSplit ? this.currentRow.preparation * quantity: quantity;
      },

      submitInput() {
        if (this.currentInput) {
          this.allInputs.push(this.currentInput); // 添加当前输入
          this.currentRow.drugTracCodg = this.allInputs.join(','); // 更新当前行的药品追溯码
          this.currentInput = ''; // 清空输入框
          this.inputCount++; // 增加输入计数

          // 检查是否达到最大输入次数
          if (this.inputCount >= this.maxInputCount) {
            this.$set(this.currentRow, 'completed', true); // 设置为完成状态
            this.drugDialogVisible = false; // 关闭对话框
          }
          this.$nextTick(() => {
            this.$refs.inputFocus.focus(); // 自动聚焦输入框
          });
        }
      },
      resetInput() {
        this.currentInput = ''; // 清空输入框
        this.inputCount = 0; // 重置输入计数
        this.currentRow = null; // 清空当前行记录
      },
      updateMaxInputCount(row) {
        this.$set(this.currentRow, 'isSplit', true); // 设置为完成状态
        // 每次开关状态改变时更新最大输入次数
        const quantity = row.packAmount;
        this.maxInputCount = row.isSplit ? quantity * row.preparationAmount: quantity;
      },
        //整体收费取消
        chargeFormCancel(){
        this.chargeDialogVisible = false
        this.discount = 0
        this.amountPaid = 0
        this.amountOfRemission = 0
        this.amountOfChange = 0
        },
        //关于现金支付的找补金额计算
        inputAmountPaid(){
        this.amountOfChange = 0
        let amount = this.amountPaid?this.amountPaid:0
        if(amount>0){
            this.amountOfChange = (this.amountPaid - this.practicalTotalAllFee).toFixed(2)
        }
        },

        //确认收费
        chargeBtn(){
            //进行数据封装
            let tollInfo = this.tollEncapsulation()
            let recipelInfos = this.recipelInfosEncapsulation()
            let count =0;
            if(this.activeName=='0'){
                 this.DrugList.forEach((item)=>{
                let recipelDetail = {
                    company:currentUser.company,
                    drugStuffId:{
                        drugStuffId:item.drugStuffId,
                        name:item.drug.goodsName,
                        price:item.drug.price,
                        retailPrice:item.drug.retailPrice,
                        dosisUnit:item.drug.dosisUnit,
                        preparationUnit:item.drug.preparationUnit,
                        pack:item.drug.pack,
                        isUnpackSell:item.drug.isUnpackSell,
                        durg:item.drug,
                        drugTracCodg:item.drugTracCodg
                    },
                    total:item.total,
                    drugTracCodg:item.drugTracCodg,//医保追溯码
                    allFee:item.allFee,
                    isExtra:0,
                    seq:count+=1,
                    isUnpackSell:item.isUnpackSell,
                    unitPrice:item.isUnpackSell==0?item.drug.price:item.drug.retailPrice,
                    minTotal:item.total,
                    stuffType:item.drug.type.value=="medicalType_1"?"1":"0"
                }
                recipelInfos[0].recipelDetailEvtList.push(recipelDetail)
            })

            }else{
                 this.StuffList.forEach((item)=>{
                let recipelDetail = {
                    company:currentUser.company,
                    drugStuffId:{
                        drugStuffId:item.drugStuffId,
                        name:item.stuff.name,
                        price:item.stuff.priceOutSell,
                        retailPrice:item.stuff.retailPrice,
                        preparationUnit:item.stuff.minUnit,
                        pack:item.stuff.packUnit,
                        isUnpackSell:item.stuff.isUnpackSell,
                        stuff:item.stuff
                    },
                    total:item.total,
                    allFee:item.allFee,
                    isExtra:0,
                    seq:count+=1,
                    isUnpackSell:item.isUnpackSell,
                    unitPrice:item.isUnpackSell==0?item.stuff.priceOutSell:item.stuff.retailPrice,
                    minTotal:item.total,
                    stuffType:'4'
                }
                recipelInfos[0].recipelDetailEvtList.push(recipelDetail)
            })

            }
            let model = {
                tollInfo: tollInfo,
                recipelInfos: recipelInfos,
                chargeStatus: '2',
            }
            console.log(model);
           // return
            saveTollTollInfo(model,null).then((res)=>{
                if (res.code == 100) {

                    this.$message.success("操作成功！");
                    this.chargeDialogVisible = false;
                    this.bizFormModel = this.initPatientModel()
                    this.stuffBizFormModel = this.initPatientModel()
                    this.number = 0
                    this.totalAllFee = 0
                    this.practicalTotalAllFee = 0
                    this.discount = 0
                    this.amountOfRemission = '0';
                    this.amountPaid = 0
                    this.amountOfChange = 0
                    if(this.activeName=="0"){
                          this.DrugList=[]
                          this.$nextTick(() => {
                               this.$refs.bizFormModel.resetFields();
                           })
                    }else{
                        this.StuffList = []
                        this.$nextTick(() => {
                            this.$refs.stuffBizFormModel.resetFields();
                        })
                    }


                }
            }).catch((error)=>{
                this.outputError(error)
            })
        },

        recipelInfosEncapsulation(){
            return [
                {
                    recipelInfo:{
                        company:currentUser.company,  //诊所id
                        fee:this.totalAllFee,         //处方总价
                        isPay:'0',                    //是否支付
                        name:'零售处方',
                        remarks:'零售处方',
                        seq:1,                    //排序
                        smallType:{
                            value:"recipelSmallType_1",  //处方小类
                        },
                        status:1,                 //处方状态
                        chargeStatus:1,          //收费状态
                        dispensionStatus:0,      //发药状态

                    },
                    recipelDetailEvtList:[

                    ],
                }
            ]
        },

        tollEncapsulation(){
            return{
                company:currentUser.company,  //诊所id
                patient:this.activeName=='0'?this.bizFormModel:this.stuffBizFormModel,    //患者
                amountReceivable:this.totalAllFee,  //应收金额
                amountReceived:this.practicalTotalAllFee,  //实收金额
                amountDiscounted:this.amountOfRemission,  //优惠金额
                discount:this.discount,    //折扣额度
                paymentType:{value:this.paymentType},  //支付类型
                amountStatus:{value:'amountStatus_1'}, //单据类型
                tollType:{value:'tollType_5'},      //收费类型
            }
        },

        //根据出生日期计算岁数
        birthdayChanges(){
           if(this.activeName=='0'){
                if(!this.bizFormModel.birthday){
                this.bizFormModel.age="";
                this.bizFormModel.month=""
                return
                };
            const duration =  this.$moment.duration(this.$moment().diff(this.bizFormModel.birthday));
            this.bizFormModel.age = duration.years();
            this.bizFormModel.month = duration.months();
           }else{
                if(!this.stuffBizFormModel.birthday){
                this.stuffBizFormModel.age="";
                this.stuffBizFormModel.month=""
                return
                };
            const stuffation =  this.$moment.duration(this.$moment().diff(this.stuffBizFormModel.birthday));
            this.stuffBizFormModel.age = stuffation.years();
            this.stuffBizFormModel.month = stuffation.months();
           }
        },

        //计算实际收费
        GetAmountReceived() {
            this.practicalTotalAllFee = this.totalAllFee
            let discount = this.discount;
            if (this.discount == 0) {
            discount = 10;
            }
            this.practicalTotalAllFee =
            this.practicalTotalAllFee * discount * 0.1;
            this.practicalTotalAllFee =
            this.practicalTotalAllFee.toFixed(2);
            this.amountOfRemission=(this.totalAllFee-this.practicalTotalAllFee).toFixed(2)
            // this.chargeForm.amountDiscounted=this.chargeForm.amountReceived
            this.inputAmountPaid()
        },

        //进行收费确认
        notarize(formName){
             if(this.activeName=='0'){
                 this.$refs[formName].validate((valid) => {
                if (valid) {
                    let flage = false
                    let flage1 =false
                    if(!this.DrugList.length>0){
                        this.$message.error("请选择药品")
                        return
                    }
                    //进行表格中的数量校验
                    this.DrugList.forEach((item)=>{

                        if(item.number==null||item.number==0){
                            this.$message.error("请填写"+item.drug.goodsName+"的数量")
                            flage = true
                        }
                    })
                    if(flage){

                        return
                    }
                    //  if(!this.StuffList.length>0){
                    //     this.$message.error("请选择材料")
                    //     return
                    // }
                    // //进行表格中的数量校验
                    // this.StuffList.forEach((item)=>{

                    //     if(item.number==null||item.number==0){
                    //         this.$message.error("请填写"+item.stuff.name+"的数量")
                    //         flage = true
                    //     }
                    // })
                    // if(flage){

                    //     return
                    // }

                    this.GetAmountReceived()
                     this.chargeDialogVisible=true

               // alert('submit!');
                } else {
                console.log('error submit!!');
                return false;
                }
            });
             }else{
                 this.$refs[formName].validate((valid) => {
                if (valid) {
                    let flage = false
                    if(!this.StuffList.length>0){
                        this.$message.error("请选择材料")
                        return
                    }
                    //进行表格中的数量校验
                    this.StuffList.forEach((item)=>{

                        if(item.number==null||item.number==0){
                            this.$message.error("请填写"+item.stuff.name+"的数量")
                            flage = true
                        }
                    })
                    if(flage){

                        return
                    }
                    this.GetAmountReceived()
                     this.chargeDialogVisible=true
               // alert('submit!');
                } else {
                console.log('error submit!!');
                return false;
                }
            });
             }

        },

        // 进行删除操作
        deleteDrugList(row,index){
           if(this.activeName=='0'){
            this.DrugList.splice(index,1);
            this.compute(row)
           }else{
               this.StuffList.splice(index,1);
               this.compute(row)
           }
        },

        compute(row){
            this.totalAllFee = 0
           if(this.activeName=='0'){
               this.DrugList.forEach((item)=>{
                let type = item.type.value.split("_")[0]

                if(type === 'medicalPackUnit'){
                    item.allFee = BigNumber(item.number>0?item.number:0).multipliedBy(item.drug.price).toNumber()
                    item.total = item.number*item.drug.preparation
                    item.isUnpackSell = 0
                }else{
                    item.allFee = BigNumber(item.number>0?item.number:0).multipliedBy(item.drug.retailPrice).toNumber()
                    item.total = item.number
                    item.isUnpackSell = 1
                }
                this.totalAllFee = BigNumber(this.totalAllFee).plus(item.allFee).toNumber()
            })
           }else{
               this.StuffList.forEach((item)=>{
                   let type = item.type.value.split("_")[0]

                if(type === 'medicalPackUnit'){
                    item.allFee = BigNumber(item.number>0?item.number:0).multipliedBy(item.stuff.priceOutSell).toNumber()
                    item.total = item.number*item.stuff.packNumber
                    item.isUnpackSell = 0
                }else{
                    item.allFee = BigNumber(item.number>0?item.number:0).multipliedBy(item.stuff.retailPrice).toNumber()
                    item.total = item.number
                    item.isUnpackSell = 1
                }
                this.totalAllFee = BigNumber(this.totalAllFee).plus(item.allFee).toNumber()
               })
           }
           this.totalAllFee = this.totalAllFee.toFixed(2)
           this.practicalTotalAllFee = this.totalAllFee
           console.log(this.DrugList);
        },
        //获取病人
        getPatient(){
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
        },
        // 数据初始化
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
                id:'',
                openId:'',
                updateDate:'',
                createDate:''
            };
            },
        // 关于新增患者
        GetPatientInfo(){
            if(this.activeName == '0'){
                if(this.bizFormModel.name.id){
                this.PatientFormModel = this.bizFormModel.name
                this.bizFormModel = {...this.bizFormModel,...this.PatientFormModel}
                }else{
                    this.PatientFormModel = {
                        ...this.initPatientModel(),
                        name:this.bizFormModel.name
                    }
                    this.bizFormModel = {...this.bizFormModel,...this.PatientFormModel}
                }
            }else{
                if(this.stuffBizFormModel.name.id){
                    this.StuffPatientForModel = this.stuffBizFormModel.name
                    this.stuffBizFormModel = {...this.stuffBizFormModel,...this.StuffPatientForModel}
                }else{
                    this.StuffPatientForModel = {...this.initPatientModel(),name:this.stuffBizFormModel.name}
                    this.stuffBizFormModel = {...this.stuffBizFormModel,...this.StuffPatientForModel}
                }
            }

        },

        //初始化获取字典数据
        initOptions(){
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
        },

        //获取药品
        GetWesternTable(){
            let search = {
                params:[
                    {
                        columnName:"company_id",
                        queryType:"=",
                        value:currentUser.company.id
                    },
                    {
                        columnName:"goods_name",
                        queryType:"like",
                        value:""
                    }

                ]
            }
              //判断是否输入的是英文
                var pattern2 = new RegExp("[A-Za-z]+");
                if (pattern2.test(this.SearchDrugInput)) {
                    // console.log(this.SearchWesternInput,'字符');
                    search.params[1].value =
                    this.SearchDrugInput.toUpperCase();
                    search.params[1].columnName = "drug.pinyin_code";

                } else {
                    search.params[1].value = this.SearchWesternInput;
                    search.params[1].columnName = "drug.goods_name";
                }

                    search.params.push({
                    columnName:"surplus_stock",
                    queryType:">",
                    value:0,
                    })
                    search.params.push({
                    columnName:"drug.status",
                    queryType:"=",
                    value:'1',
                    })
                    search.params.push({
                    columnName:"drug.del_flag",
                    queryType:"=",
                    value:'0',
                    })
                listAll(search).then((responseData) => {
                    if (responseData.code == 100) {
                    this.DrugMedicineTable = responseData.data;
                    }
                }).catch((error)=>{
                    this.outputError(error)
                });
        },

        //获取材料
        GetStuffTable(){
             let SearchModel = {
                columnName: "",
                limit: 10,
                offset: 0,
                order: "",
                params: [
                {
                    columnName: "company_id",
                    queryType: "=",
                    value: currentUser.company.id,
                },
                {
                    columnName: "stuff.name",
                    queryType: "like",
                    value: "",
                },
                {
                    columnName: "stuff.is_out_sell",
                    queryType: "=",
                    value: "1",
                },
                {
                    columnName: "surplus_stock",
                    queryType: ">",
                    value: 0,
                },
                ],
            };
            var pattern2 = new RegExp("[A-Za-z]+");
            if (pattern2.test(this.SearchStuffInput)) {
                // console.log(this.SearchWesternInput,'字符');
                SearchModel.params[1].value = this.SearchStuffInput.toUpperCase();
                SearchModel.params[1].columnName = "stuff.pinyin_code";
            } else {
                SearchModel.params[1].value = this.SearchStuffInput;
                SearchModel.params[1].columnName = "stuff.name";
            }
            SearchModel.params.push({
                    columnName: "stuff.status",
                    queryType: "=",
                    value: '1',
            })
            SearchModel.params.push({
                    columnName: "stuff.del_flag",
                    queryType: "=",
                    value: '0',
            })
            listAll(SearchModel).then((responseData) => {
                if (responseData.code == 100) {
                    //console.log(responseData.data,'获取材料');
                    this.StuffMedicineTable=responseData.data
                }
            });
        },

        //药品点击
        RowClickWesternTable(row){
            console.log(this.DrugList);
            let flag = false
            this.DrugList.forEach((item)=>{
                if(item.id===row.id){
                    flag = true
                }
            })
            if(flag){
                this.$message.warning("该药品已添加，请勿重复添加！")
                return;
            }
            row.number = null
            row.allFee = null
            row.type = row.drug.pack
            row.drugTracCodg = null
            this.DrugList.push(JSON.parse(JSON.stringify(row)));
            this.$message.success("添加药品成功")
        },

        //材料点击
        RowClickStuffTable(row){
            let flag = false
            this.StuffList.forEach((item)=>{
                if(item.id===row.id){
                    flag = true
                }
            })
            if(flag){
                this.$message.warning("该材料已添加，请勿重复添加！")
                return;
            }
            row.number = null
            row.allFee = null
            row.type = row.stuff.packUnit
            this.StuffList.push(JSON.parse(JSON.stringify(row)));
            this.$message.success("添加材料成功")
        }
    },
    watch:{
        activeName:function(newVal,oldVal){
            if(newVal=='0'){
                this.bizFormModel = this.initPatientModel()
                    this.DrugList=[]
                    this.StuffList = [];
                    this.number = 0
                    this.totalAllFee = 0
                    this.practicalTotalAllFee = 0
                    this.discount = 0
                    this.amountOfRemission = '0';
                   this.$nextTick(() => {
                       this.$refs.bizFormModel.resetFields();
                   })
            }else{
                this.stuffBizFormModel = this.initPatientModel();
                this.StuffList = [];
                this.DrugList=[];
                 this.number = 0
                this.totalAllFee = 0
                this.practicalTotalAllFee = 0
                this.discount = 0
                this.amountOfRemission = '0';
                 this.$nextTick(() => {
                       this.$refs.stuffBizFormModel.resetFields();
                   })
            }
        }
    },
    mounted(){
        this.getPatient();
        this.initOptions();
    }

}
</script>
<style scoped>
.retailCharge{
    padding: 0px;
}
.patientMessage{
    margin-left: -53px;
}
.statistical_box{
    position: fixed;
    z-index: 100;
    bottom: 16px;
    background: #fff;
    padding: 1px 16px;
    box-sizing: border-box;
    border: 1px solid #71c7ad;
    box-shadow: 0 0 4px 0 rgb(81 165 140 / 15%), 0 2px 17px 0 rgb(113 199 173 / 11%);
    border-radius: 4px;
    font-size: 10;
    text-align: right;
    /* visibility: visible; */
    width: 1450px;
}
#chargeTotalFee_Container {
  display: flex;
  justify-content: flex-start;
  padding: 20px;
  margin: 10px;
  background-color: rgba(207, 227, 236, 0.803921568627451);
  border-radius: 7px;
}
.chargeTotalFee_Container {
  display: flex;
  justify-content: flex-start;
  padding: 20px;
  margin: 10px;
  background-color: rgba(207, 227, 236, 0.803921568627451);
  border-radius: 7px;
}

#chargeTotalFee_Title {
  font-family: "Arial Normal", "Arial", sans-serif;
  font-weight: 400;
  font-style: normal;
  font-size: 21px;
  margin-right: 8px;
}
.chargeTotalFee_Title {
  font-family: "Arial Normal", "Arial", sans-serif;
  font-weight: 400;
  font-style: normal;
  font-size: 21px;
  margin-right: 8px;
}

#chargeTotalFee_Fee {
  font-weight: 800;
  font-style: black;
  font-size: 25px;
  margin-top: -4px;
}
.chargeTotalFee_Fee {
  font-weight: 800;
  font-style: black;
  font-size: 25px;
  margin-top: -4px;
}
</style>
