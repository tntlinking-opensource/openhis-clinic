<template>
  <el-container>
    <el-aside width="400px">
      <el-card style="height: 100%">
        <el-row style="display: flex; justify-content: flex-start">
          <!-- <el-select
            v-model="SearchPatientId"
            value-key="id"
            filterable
            clearable
            style="width: 100%"
            placeholder="请选择患者"
            @change="GetDispensingTable"
          >
            <el-option
              v-for="patientId in AllPatientOption"
              :key="patientId.id"
              :label="patientId.name"
              :value="patientId.id"
            ></el-option>
          </el-select> -->
          <el-input
            v-model="selectCondition"
            placeholder="请输入患者姓名/卡号"
            prefix-icon="el-icon-search"
            class="input-with-select"
            @keydown.enter.native="search"
          ></el-input>
          <!-- <el-button
            style="width: 100px"
            slot="append"
            icon="el-icon-search"
            @click="search"
            >搜索</el-button
          > -->
        </el-row>
        <el-row>
          <el-tabs stretch v-model="ActiveName">
            <el-tab-pane name="IsWaitingDrug">
              <span slot="label"
                >待发药<el-badge
                  :value="WaitingTotal"
                  :max="99"
                  type="primary"
                  style="line-height: normal"
                ></el-badge></span
              >
              <el-scrollbar view-style="height:calc(100vh - 267px)">
                <el-collapse
                  v-model="patientInfoRow"
                  accordion
                  @change="selectPatientChange()"
                >
                  <div v-for="(item, index) in WaitingTable" :key="item">
                    <el-collapse-item class="already-medocal" :name="item">
                      <template slot="title">
                        <span
                          style="
                            font-weight: bold;
                            font-size: 12px;
                            color: #606266;
                            flex: 1;
                          "
                          >{{ PageRegistration.offset + index + 1 }}. {{ item.patientId.name }}/ {{ item.patientId.gender.name }} /
                      {{ item.patientId.age }}岁</span
                        >
                        <span class="redStateStyle">{{
                          item.chargeDate
                        }}</span>
                      </template>
                      <div
                        v-for="(o,index) in prescriptionMainList"
                        :key="index"
                        @click="AlreadyPatientDescriptionsQuery(o,index)"
                        :class="{clickTest:index==overColor}"
                      >
                       <div  style="

                            border-top: 1px dashed #dcdfe6;
                            margin: 0px 30px 0 15px;"></div>
                          <!-- <br> -->
                      <p style="font-weight: bold;margin-top: 15px;margin-left:30px;font-size: 12px;
                            color: #606266;">
                        {{ o.recipelInfo.name }}
                      </p>

                      <p style="font-weight: bold;margin-top: 6px;margin-left:30px;font-size: 12px;
                            color: #606266;">
                        {{ o.recipelInfo.id }}

                      </p>
                       <div style="
                            border-top: 1px dashed #dcdfe6;
                            margin: 0px 30px 0 15px;"></div>
                        <!-- <el-descriptions
                          :column="1"
                          :colon="false"
                          style="
                            border-top: 1px dashed #dcdfe6;
                            margin: 0px 30px 0 15px;
                          "
                        >
                          <el-descriptions-item
                            contentStyle="font-weight: bold;margin-top: 6px;"
                            >{{ o.recipelInfo.name }}</el-descriptions-item
                          >
                          <el-descriptions-item
                            >{{ o.recipelInfo.id }}
                          </el-descriptions-item>
                        </el-descriptions> -->
                      </div>
                    </el-collapse-item>
                  </div>
                </el-collapse>
              </el-scrollbar>
              <el-pagination
                small
                background
                @current-change="DispensingPage"
                :page-size="dispensingPageSize"
                layout="prev, pager, next"
                :total="WaitingTotal"
              >
              </el-pagination>
            </el-tab-pane>
            <el-tab-pane label="已发药" name="IsAll">
              <el-scrollbar view-style="height:calc(100vh - 267px);">
                <el-collapse
                  v-model="patientInfoRow"
                  accordion
                  @change="selectPatientChange()"
                >
                  <div v-for="(item, index) in dispensingTable" :key="item">
                    <el-collapse-item class="already-medocal" :name="item">
                      <template slot="title">
                        <span
                          style="
                            font-weight: bold;
                            font-size: 12px;
                            color: #606266;
                            flex: 1;
                          "
                          >{{ PageRegistration.offset + index + 1 }}. {{ item.patientId.name }}/ {{ item.patientId.gender.name }} /
                      {{ item.patientId.age }}岁</span
                        >
                        <span>{{
                          item.dispensingDate
                        }}</span>
                      </template>
                      <div
                        v-for="(o,index) in prescriptionMainList"
                        :key="index"
                        @click="AlreadyPatientDescriptionsQuery(o,index)"
                        :class="{clickTest:index==overColor}"
                      >
                       <div  style="

                            border-top: 1px dashed #dcdfe6;
                            margin: 0px 30px 0 15px;"></div>
                          <!-- <br> -->
                      <p style="font-weight: bold;margin-top: 15px;margin-left:30px;font-size: 12px;
                            color: #606266;">
                        {{ o.recipelInfo.name }}
                      </p>

                      <p style="font-weight: bold;margin-top: 6px;margin-left:30px;font-size: 12px;
                            color: #606266;">
                        {{ o.recipelInfo.id }}

                      </p>
                       <div style="
                            border-top: 1px dashed #dcdfe6;
                            margin: 0px 30px 0 15px;"></div>
                        <!-- <el-descriptions
                          :column="1"
                          :colon="false"
                          style="
                            border-top: 1px dashed #dcdfe6;
                            margin: 0px 30px 0 15px;
                          "
                        >
                          <el-descriptions-item
                            contentStyle="font-weight: bold;margin-top: 6px;"
                            >{{ o.recipelInfo.name }}</el-descriptions-item
                          >
                          <el-descriptions-item
                            >{{ o.recipelInfo.id }}
                          </el-descriptions-item>
                        </el-descriptions> -->
                      </div>
                    </el-collapse-item>
                  </div>
                </el-collapse>
              </el-scrollbar>
              <el-pagination
                small
                background
                @current-change="allPage"
                :page-size="dispensingPageSize"
                layout="prev, pager, next"
                :total="OtherTotal"
              >
              </el-pagination>
            </el-tab-pane>
            <el-tab-pane label="已退药" name="return">
              <el-scrollbar view-style="height:calc(100vh - 267px);">
                <el-collapse
                  v-model="patientInfoRow"
                  accordion
                  @change="selectPatientChange()"
                >
                  <div v-for="(item, index) in returnTable" :key="item">
                    <el-collapse-item class="already-medocal" :name="item">
                      <template slot="title">
                        <span
                          style="
                            font-weight: bold;
                            font-size: 12px;
                            color: #606266;
                            flex: 1;
                          "
                          >{{ PageRegistration.offset + index + 1 }}. {{ item.patientId.name }}/ {{ item.patientId.gender.name }} /
                      {{ item.patientId.age }}岁</span
                        >
                        <span>{{
                          item.returnDate
                        }}</span>
                      </template>
                      <div
                        v-for="(o,index) in prescriptionMainList"
                        :key="index"
                        @click="AlreadyPatientDescriptionsQuery(o,index)"
                        :class="{clickTest:index==overColor}"
                      >
                       <div  style="

                            border-top: 1px dashed #dcdfe6;
                            margin: 0px 30px 0 15px;"></div>
                          <!-- <br> -->
                      <p style="font-weight: bold;margin-top: 15px;margin-left:30px;font-size: 12px;
                            color: #606266;">
                        {{ o.recipelInfo.name }}
                      </p>

                      <p style="font-weight: bold;margin-top: 6px;margin-left:30px;font-size: 12px;
                            color: #606266;">
                        {{ o.recipelInfo.id }}

                      </p>
                       <div style="
                            border-top: 1px dashed #dcdfe6;
                            margin: 0px 30px 0 15px;"></div>
                        <!-- <el-descriptions
                          :column="1"
                          :colon="false"
                          style="
                            border-top: 1px dashed #dcdfe6;
                            margin: 0px 30px 0 15px;
                          "
                        >
                          <el-descriptions-item
                            contentStyle="font-weight: bold;margin-top: 6px;"
                            >{{ o.recipelInfo.name }}</el-descriptions-item
                          >
                          <el-descriptions-item
                            >{{ o.recipelInfo.id }}
                          </el-descriptions-item>
                        </el-descriptions> -->
                      </div>
                    </el-collapse-item>
                  </div>
                </el-collapse>
              </el-scrollbar>
              <el-pagination
                small
                background
                @current-change="returnPage"
                :page-size="dispensingPageSize"
                layout="prev, pager, next"
                :total="returnTotal"
              >
              </el-pagination>
            </el-tab-pane>
          </el-tabs>
        </el-row>
      </el-card>
    </el-aside>
    <el-main>
      <el-card class="box-card sub-card" style="height: 50px; display: flex">
        <div class="flex-space-between">
          <div class="goods-back" v-if="ActiveName == 'IsAll'">
            已发药
          </div>
          <el-popconfirm
          style="margin-right:10px"
            title="确定发药吗？"
            @confirm="DispensingMedicineClick(0)"
            v-if="ActiveName == 'IsWaitingDrug' && buttonState">
            <el-button slot="reference" type="primary">发药</el-button>
          </el-popconfirm>
          <el-popconfirm
          style="margin-right:10px"
            title="确定退药吗？"
            @confirm="DispensingMedicineClick(1)"
            v-if="ActiveName == 'IsAll' && buttonState">
            <el-button slot="reference" type="primary" plain>退药</el-button>
          </el-popconfirm>
          <el-button
            type="primary"
            v-if="ActiveName == 'IsAll'"
            @click="print"
            >打印</el-button
          >
        </div>
      </el-card>
      <el-card class="box-card" shadow="always">
        <el-scrollbar
          view-style="height:calc(100vh - 198px);padding-right: 20px;"
        >
          <el-card class="box-card main-card">
            <el-form
              ref="patientInfoForm"
              :model="patientInfoForm"
              label-width="auto"
            >
              <el-row :gutter="24">
                <el-col :span="5">
                  <el-form-item label="患者姓名" prop="name">
                    <!-- <el-input
                      disabled
                      v-model="patientInfoForm.name"
                    ></el-input> -->
                     <div class="nameStyle">
                      {{patientInfoForm.name?patientInfoForm.name:""}}
                      <div class="imgStyle" v-if="allMember.length>0" >
                       <el-popover
                          placement="top-start"
                          title=""
                          width="200"
                          trigger="hover"
                          >
                           <div v-for="(item,index) in allMember" :key="index" style="padding:5px">
                                    <el-tag>{{item.memberName}}</el-tag>
                            </div>
                           <img  slot="reference"  src="../../../assets/images/vip.png" style="width:25px;height:25px"/>
                        </el-popover>
                    </div>

                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="3">
                  <el-form-item label="性别" prop="gender">
                    <!-- <el-select
                      disabled
                      v-model="patientInfoForm.gender.name"
                      style="width: 100%"
                    >
                      <el-option label="男" value="男"></el-option>
                      <el-option label="女" value="女"></el-option>
                    </el-select> -->
                      <span>{{patientInfoForm.gender.name?patientInfoForm.gender.name:""}}</span>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="年龄" prop="age">
                    <!-- <el-input
                      disabled
                      v-model="patientInfoForm.age"
                      style="width: 49%"
                    >
                      <template slot="append">岁</template>
                    </el-input>
                    <el-input
                      disabled
                      v-model="patientInfoForm.month"
                      style="width: 49%"
                    >
                      <template slot="append">月</template>
                    </el-input> -->
                     <span>
                      {{patientInfoForm.age?patientInfoForm.age:0}}岁{{patientInfoForm.month?patientInfoForm.month:0}}月
                    </span>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="联系电话" prop="phone">
                    <!-- <el-input
                      disabled
                      v-model="patientInfoForm.phone"
                    ></el-input> -->
                     <span>
                      {{patientInfoForm.phone}}
                    </span>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="身份证号" prop="card">
                    <!-- <el-input
                      disabled
                      v-model="patientInfoForm.card"
                    ></el-input> -->
                    <span>{{patientInfoForm.card}}</span>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="西医诊断" prop="westernDiagnose">
                    <!-- <el-input
                      disabled
                      v-model="patientInfoForm.westernDiagnose"
                    ></el-input> -->
                     <span>
                      {{patientInfoForm.westernDiagnose?patientInfoForm.westernDiagnose:""}}
                    </span>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="中医诊断" prop="chinaDiagnose">
                    <!-- <el-input
                      disabled
                      v-model="patientInfoForm.chinaDiagnose"
                    ></el-input> -->
                    <span>
                      {{patientInfoForm.chinaDiagnose?patientInfoForm.chinaDiagnose:""}}
                    </span>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-card>
          <el-card class="box-card main-card" v-if="medicalEditTabs.length > 0">
            <div>
              <el-tabs
                v-model="medicalEditTabsValue"
                type="card"
                @tab-click="clickMedicalEditTab"
              >
                <el-tab-pane
                  v-for="(item, index) in medicalEditTabs"
                  :key="item"
                  :label="item.title"
                  :name="item"
                >
                  <span slot="label">
                    {{ item.title }}
                  </span>
                  <el-row type="flex" justify="space-between" align="center" style="padding-top:10px" v-if="ActiveName == 'IsWaitingDrug' && buttonState">
                    <div class="single">
                      本处方费用：{{ item.content.recipelInfo.amountReceivedTotal }}元
                    </div>
                    <el-popconfirm
                      title="确定发药吗？"
                      @confirm="singleMedicineClick(0)"
                      style="padding-top:7px"
                      >
                      <el-button slot="reference" type="primary" plain>本处方发药</el-button>
                    </el-popconfirm>
                  </el-row>
                  <el-row type="flex" justify="space-between" align="center" style="padding-top:10px" v-if="ActiveName == 'IsAll'">
                    <div class="single">
                      本处方费用：{{ item.content.recipelInfo.amountReceivedTotal }}元
                    </div>
                    <el-popconfirm
                      title="确定退药吗？"
                      @confirm="singleMedicineClick(1)"
                      style="padding-top:7px"
                      >
                      <el-button slot="reference" type="primary" plain>本处方退药</el-button>
                    </el-popconfirm>
                  </el-row>
                  <el-row style="padding-top:8px" v-if="ActiveName == 'return'">
                    <div class="single">
                      本处方费用：{{ item.content.recipelInfo.amountReceivedTotal }}元
                    </div>
                  </el-row>
                 <div v-if="item.type!='recipelType_2'">
                    <el-row>
                    <el-divider content-position="left">处方信息</el-divider>

                    <el-input
                      disabled
                      v-if="item.type!='recipelType_3'"
                      :value="item.content.recipelInfo.smallType.name"
                      style="width:100px"
                    >
                    </el-input>
                      <el-checkbox v-if="item.type == 'recipelType_0'" style="margin-left: 20px;" v-model="item.content.recipelInfo.chronicDisease" disabled >是否慢病</el-checkbox>
                  </el-row>
                 </div>
                  <div v-else style="">
                     <el-row>
                    <el-divider content-position="left">处方信息</el-divider>

                    <el-input
                      disabled
                      v-if="item.type!='recipelType_3'"
                      :value="item.content.recipelInfo.smallType.name"
                      style="width:100px"
                    >
                    </el-input>
                  </el-row>
                  </div>

                  <div v-if="item.type!='recipelType_2'">
                    <el-table :data="item.content.notExtra" style="width: 100%">
                    <el-table-column label="序号" type="index" align="center" width="width">
                    </el-table-column>
                    <el-table-column
                      prop="drugStuffId.name"
                      label="名称"
                      width="width"
                    >
                    <template slot-scope="scope">
                      {{ scope.row.drugStuffId.name }}
                      <div v-if="item.type=='recipelType_0'">
                        <span class="nameStyle"
                          >{{ scope.row.drugStuffId.drug.dosis
                          }}{{ scope.row.drugStuffId.drug.dosisUnit.name }} *
                          {{ scope.row.drugStuffId.drug.preparation
                          }}{{ scope.row.drugStuffId.drug.preparationUnit.name }}/{{
                            scope.row.drugStuffId.drug.pack.name
                          }}</span
                        >
                      </div>
                    </template>
                    </el-table-column>
                    <!-- <el-table-column
                      prop="drugStuffId.name"
                      label="名称"
                      v-else-if="item.type.value == 'recipelType_1'"
                    >

                    </el-table-column> -->
                    <el-table-column
                      prop="drugStuffId.drug.factory.name"
                      label="厂家"
                      width="width"
                    >
                    </el-table-column>
                    <el-table-column
                      prop="westernMedicineUse.name"
                      label="用法"
                      width="width"
                      v-if="item.type == 'recipelType_0'"
                    >
                    </el-table-column>
                    <el-table-column
                      prop="singleDosage"
                      label="单次用量"
                      align="center"
                      v-if="item.type == 'recipelType_0'"
                      width="width"
                    >
                      <template slot-scope="scope"
                        >{{ scope.row.singleDosage
                        }}{{ scope.row.drugStuffId.dosisUnit.name }}</template
                      >
                    </el-table-column>
                    <el-table-column
                      prop="frequency"
                      label="频次"
                      align="center"
                      width="width"
                      v-if="item.type == 'recipelType_0'"
                    >
                      <template slot-scope="scope"
                        >{{ scope.row.infuseUse.name
                        }}{{ scope.row.frequency.name }}</template
                      >
                    </el-table-column>
                    <el-table-column
                      prop="chineseMedicineUse.name"
                      label="煎法"
                      align="center"
                      width="width"
                      v-if="item.chineseMedicineUse != undefined&&item.chineseMedicineUse != ''"
                    >
                    </el-table-column>
                    <el-table-column prop="days.name" label="天数" align="center" v-if="item.type == 'recipelType_0'">
                    </el-table-column>
                    <el-table-column
                      prop="unitPrice"
                      label="单价"
                      width="width"
                    >
                    <template slot-scope="scope">
                      <span
                        >{{ (scope.row.unitPrice).toFixed(4) }}元/{{
                          scope.row.isUnpackSell == 1
                            ? scope.row.drugStuffId.preparationUnit.name
                            : scope.row.drugStuffId.pack.name
                        }}</span
                      >
                    </template>
                    </el-table-column>
                    <el-table-column prop="total" label="总量" width="width">
                      <template slot-scope="scope">
                        <span v-if="scope.row.drugStuffId.costItem != null">
                          {{ scope.row.total
                          }}{{ scope.row.drugStuffId.dosisUnit.name }}
                        </span>
                        <span v-else-if="scope.row.drugStuffId.stuff != null">
                          {{
                            Math.floor(
                              scope.row.total / scope.row.drugStuffId.stuff.packNumber
                            ) > 0
                              ? Math.floor(
                                  scope.row.total /
                                    scope.row.drugStuffId.stuff.packNumber
                                ) +
                                scope.row.drugStuffId.stuff.packUnit.name +
                                (scope.row.total %
                                  scope.row.drugStuffId.stuff.packNumber >
                                0
                                  ? (scope.row.total %
                                      scope.row.drugStuffId.stuff.packNumber) +
                                    scope.row.drugStuffId.stuff.minUnit.name
                                  : "")
                              : scope.row.total +
                                scope.row.drugStuffId.stuff.minUnit.name
                          }}
                        </span>
                        <span v-else-if="scope.row.drugStuffId.drug != null">
                          {{
                            Math.floor(
                              scope.row.total /
                                (scope.row.drugStuffId.drug.preparation - 0)
                            ) > 0
                              ? Math.floor(
                                  scope.row.total /
                                    (scope.row.drugStuffId.drug.preparation - 0)
                                ) +
                                scope.row.drugStuffId.drug.pack.name +
                                (scope.row.total %
                                  (scope.row.drugStuffId.drug.preparation - 0) >
                                0
                                  ? (scope.row.total %
                                      (scope.row.drugStuffId.drug.preparation - 0)) +
                                    scope.row.drugStuffId.drug.preparationUnit.name
                                  : "")
                              : scope.row.total +
                                scope.row.drugStuffId.drug.preparationUnit.name
                          }}
                        </span>
                      </template>
                    </el-table-column>
                      <el-table-column prop="allFee" width="width" label="应付金额">
                        <template slot-scope="scope">
                          {{ (scope.row.allFee).toFixed(2) }}元
                        </template>
                      </el-table-column>
                      <el-table-column prop="actualPayment" width="width" label="实付金额">
                        <template slot-scope="scope">
                          {{ (scope.row.actualPayment) == null ? (scope.row.allFee).toFixed(2) : (scope.row.actualPayment).toFixed(2)}}元
                          <!--{{ (scope.row.actualPayment).toFixed(2) }}元-->
                        </template>
                      </el-table-column>
                  </el-table>
                  <!--<div v-if="item.type=='recipelType_1'" style="margin:10px 0">
                    <el-input style="width:50px" disabled v-model="item.content.recipelInfo.dosage">
                    </el-input>
                    剂

                    <el-input style="width:100px" disabled v-model="item.content.recipelInfo.frequency.name">
                    </el-input>
                    每次服用
                    <el-input style="width:80px" disabled v-model="item.content.recipelInfo.dosage">
                    </el-input>{{isSpecial?'格':'ml'}}
                  </div>-->
                  <el-row v-if="item.type=='recipelType_1'">
                    <el-input disabled v-model="item.content.recipelInfo.dosage" oninput="value=value.replace(/[^\d.]/g,'')" style="width: 60px"></el-input>
                    &nbsp;剂 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;用法：
                    <el-select disabled v-model="item.content.recipelInfo.recipelUse" placeholder="请选择" style="width: 110px">
                      <el-option v-for="pitem in ChineseUseOption" :key="pitem.value"
                                 :label="pitem.name"
                                 :value="{ name: pitem.name, value: pitem.value }">
                      </el-option>
                    </el-select>
                    <el-select disabled v-model="item.content.recipelInfo.frequency" placeholder="请选择" style="width: 110px">
                      <el-option v-for="pitem in ChineseTimeOption" :key="pitem.value"
                                 :label="pitem.name"
                                 :value="{ name: pitem.name, value: pitem.value }">
                      </el-option>
                    </el-select>
                    <el-select disabled v-model="item.content.recipelInfo.takeFrequency" placeholder="请选择" style="width: 110px">
                      <el-option v-for="pitem in JSON.parse(
                          JSON.stringify(FrequencyOption)
                        )" :key="pitem.value" :label="pitem.name" :value="{
                          name: pitem.name,
                          value: pitem.value,
                        }">
                      </el-option>
                    </el-select>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 一次&nbsp;<el-input disabled v-model="item.content.recipelInfo.singleDosage" style="width: 60px"></el-input>&nbsp;
                    <span>ml</span>
                  </el-row>
                  </div>
                    <div v-else style="margin-left:-10px;">
                      <div v-for="(items,index) in item.infusion.zushu" :key="index" style="border:1px solid #DCDCDC;padding:10px;margin:10px;">

                        <div>
                      <span  style="float:left;height:25px;line-height:25px;font-size:14px;">&nbsp;&nbsp;第{{index+1}}组</span>
                      <span  style="float:right;height:25px;line-height:25px;font-size:14px;">&nbsp;&nbsp;滴/分钟</span>

                             <el-input
                              style="float:right;width:10%"
                              :value="item.infusion.drippingSpeed[index]"
                              placeholder=""
                              :disabled="
                               true
                              "
                            >

                            </el-input>

                            <span  style="float:right;height:25px;line-height:25px;font-size:14px;">&nbsp;&nbsp;天&nbsp;&nbsp;</span>
                             <el-input
                              style="float:right;width:10%"
                              :value="item.infusion.days[index].name"
                              placeholder=""
                              :disabled="
                               true
                              "
                            >

                            </el-input>
                             <!-- <el-select
                               style="float:right;width:10%"
                              v-model="item.infusion.days[index]"
                              :disabled="
                               true
                              "

                              placeholder="请选择天数"
                            >
                              <el-option
                                v-for="item in DayNumOption"
                                :key="item.value"
                                :label="item.name"
                                :value="{ name: item.name, value: item.value }"
                              >
                              </el-option>
                            </el-select> -->

                            <span  style="float:right;height:25px;line-height:25px;font-size:18px;">&nbsp;&nbsp;</span>
                                <el-input
                              style="float:right;width:10%"
                              :value="item.infusion.frequency[index].name"
                              placeholder=""
                              :disabled="
                               true
                              "
                            >

                            </el-input>
                             <!-- <el-select
                               style="float:right;width:10%;"
                              v-model="item.infusion.frequency[index]"
                              :disabled="
                               true
                              "

                              placeholder="请选择频次"
                            >
                              <el-option
                                v-for="item in FrequencyOption"
                                :key="item.value"
                                :label="item.name"
                                :value="{ name: item.name, value: item.value }"
                              >
                              </el-option>
                            </el-select> -->

                            <span  style="float:right;height:25px;line-height:25px;font-size:18px;">&nbsp;&nbsp;</span>
                            <el-input
                              style="float:right;width:10%"
                              :value="item.infusion.infuseUse[index].name"
                              placeholder=""
                              :disabled="
                               true
                              "
                            >

                            </el-input>
                          <!-- <el-select
                              style="float:right;width:10%"
                              v-model="item.infusion.infuseUse[index]"
                              :disabled="
                                true
                              "
                              placeholder="请选择用法"
                            >
                              <el-option
                                v-for="item in infuseUseOption"
                                :key="item.value"
                                :label="item.name"
                                :value="{ name: item.name, value: item.value }"
                              >
                              </el-option>
                            </el-select> -->
                        </div>
                        <div style="border:17px solid #0000"></div>
                    <el-table :data="item.infusion.infusionProject[index]" style="width: 100%">
                    <!-- <el-table-column label="组数" prop="infuseGroup" align="center">
                    </el-table-column> -->
                    <el-table-column label="序号" type="index" align="center" width="width">
                    </el-table-column>
                    <el-table-column
                      prop="drugStuffId.name"
                      label="名称"
                      width="width"
                    >
                    <template slot-scope="scope">
                       <!-- v-if="item.type.value == 'recipelType_2'" -->
                      {{ scope.row.drugStuffId.name }}
                      <div v-if="scope.row.drugStuffId.drug != null">
                        <span class="nameStyle"
                          >{{ scope.row.drugStuffId.drug.dosis
                          }}{{ scope.row.drugStuffId.drug.dosisUnit.name }} *
                          {{ scope.row.drugStuffId.drug.preparation
                          }}{{ scope.row.drugStuffId.drug.preparationUnit.name }}/{{
                            scope.row.drugStuffId.drug.pack.name
                          }}</span
                        >
                      </div>
                    </template>
                    </el-table-column>
                    <el-table-column
                      prop="skinTest.name"
                      label="皮试"
                      width="width"
                    >
                    </el-table-column>
                    <el-table-column
                      prop="drugStuffId.drug.factory.name"
                      label="厂家"
                      width="width"
                    >
                    </el-table-column>


                     <el-table-column
                      prop="singleDosage"
                      label="单次用量"
                      align="center"
                      width="width"
                      v-if="item.type == 'recipelType_2'"
                    >
                      <template slot-scope="scope"
                        >{{ scope.row.singleDosage
                        }}{{ scope.row.drugStuffId.preparationUnit.name }}</template
                      >
                    </el-table-column>
                    <el-table-column
                      prop="unitPrice"
                      label="单价"
                      width="width"
                    >
                    <template slot-scope="scope">
                      <span
                        >{{ (scope.row.unitPrice).toFixed(4) }}元/{{
                          scope.row.isUnpackSell == 1
                            ? scope.row.drugStuffId.preparationUnit.name
                            : scope.row.drugStuffId.pack.name
                        }}</span
                      >
                    </template>
                    </el-table-column>

                    <el-table-column prop="total" label="总量" width="width">
                      <template slot-scope="scope">
                        <span v-if="scope.row.drugStuffId.costItem != null">
                          {{ scope.row.total
                          }}{{ scope.row.drugStuffId.dosisUnit.name }}
                        </span>
                        <span v-else-if="scope.row.drugStuffId.stuff != null">
                          {{
                            Math.floor(
                              scope.row.total / scope.row.drugStuffId.stuff.packNumber
                            ) > 0
                              ? Math.floor(
                                  scope.row.total /
                                    scope.row.drugStuffId.stuff.packNumber
                                ) +
                                scope.row.drugStuffId.stuff.packUnit.name +
                                (scope.row.total %
                                  scope.row.drugStuffId.stuff.packNumber >
                                0
                                  ? (scope.row.total %
                                      scope.row.drugStuffId.stuff.packNumber) +
                                    scope.row.drugStuffId.stuff.minUnit.name
                                  : "")
                              : scope.row.total +
                                scope.row.drugStuffId.stuff.minUnit.name
                          }}
                        </span>
                        <span v-else-if="scope.row.drugStuffId.drug != null">
                          {{
                            Math.floor(
                              scope.row.total /
                                (scope.row.drugStuffId.drug.preparation - 0)
                            ) > 0
                              ? Math.floor(
                                  scope.row.total /
                                    (scope.row.drugStuffId.drug.preparation - 0)
                                ) +
                                scope.row.drugStuffId.drug.pack.name +
                                (scope.row.total %
                                  (scope.row.drugStuffId.drug.preparation - 0) >
                                0
                                  ? (scope.row.total %
                                      (scope.row.drugStuffId.drug.preparation - 0)) +
                                    scope.row.drugStuffId.drug.preparationUnit.name
                                  : "")
                              : scope.row.total +
                                scope.row.drugStuffId.drug.preparationUnit.name
                          }}
                        </span>
                      </template>
                    </el-table-column>
                      <el-table-column prop="allFee" width="width" label="应付金额">
                        <template slot-scope="scope">
                          {{ (scope.row.allFee).toFixed(2) }}元
                        </template>
                      </el-table-column>
                      <el-table-column prop="allFee" width="width" label="实付金额">
                        <template slot-scope="scope">
                          {{ (scope.row.actualPayment).toFixed(2) }}元
                        </template>
                      </el-table-column>

                  </el-table>
                  </div>
                  </div>

                  <el-divider style="margin-top: 5px" content-position="left"
                    >附加费</el-divider
                  >
                  <el-table :data="item.content.isExtra" style="width: 100%">
                    <el-table-column label="序号" type="index" align="center" width="width">
                    </el-table-column>
                    <el-table-column
                      prop="drugStuffId.name"
                      label="名称"
                      width="width"
                    >
                    </el-table-column>
                    <el-table-column
                      prop="drugStuffId.drug.factory.name"
                      label="厂家"
                      width="width"
                    >
                    </el-table-column>
                    <el-table-column
                      prop="unitPrice"
                      label="单价"
                      width="width"
                    >
                    <template slot-scope="scope">
                      <span
                        >{{ (scope.row.unitPrice).toFixed(4) }}元/{{
                          scope.row.isUnpackSell == 1
                            ? scope.row.drugStuffId.preparationUnit.name
                            : scope.row.drugStuffId.pack.name
                        }}</span
                      >
                    </template>
                    </el-table-column>
                    <el-table-column prop="total" label="总量" width="width" >
                      <template slot-scope="scope">
                        <span v-if="scope.row.drugStuffId.costItem != null">
                          {{ scope.row.total
                          }}{{ scope.row.drugStuffId.dosisUnit.name }}
                        </span>
                        <span v-else-if="scope.row.drugStuffId.stuff != null">
                          {{
                            Math.floor(
                              scope.row.total / scope.row.drugStuffId.stuff.packNumber
                            ) > 0
                              ? Math.floor(
                                  scope.row.total /
                                    scope.row.drugStuffId.stuff.packNumber
                                ) +
                                scope.row.drugStuffId.stuff.packUnit.name +
                                (scope.row.total %
                                  scope.row.drugStuffId.stuff.packNumber >
                                0
                                  ? (scope.row.total %
                                      scope.row.drugStuffId.stuff.packNumber) +
                                    scope.row.drugStuffId.stuff.minUnit.name
                                  : "")
                              : scope.row.total +
                                scope.row.drugStuffId.stuff.minUnit.name
                          }}
                        </span>
                        <span v-else-if="scope.row.drugStuffId.drug != null">
                          {{
                            Math.floor(
                              scope.row.total /
                                (scope.row.drugStuffId.drug.preparation - 0)
                            ) > 0
                              ? Math.floor(
                                  scope.row.total /
                                    (scope.row.drugStuffId.drug.preparation - 0)
                                ) +
                                scope.row.drugStuffId.drug.pack.name +
                                (scope.row.total %
                                  (scope.row.drugStuffId.drug.preparation - 0) >
                                0
                                  ? (scope.row.total %
                                      (scope.row.drugStuffId.drug.preparation - 0)) +
                                    scope.row.drugStuffId.drug.preparationUnit.name
                                  : "")
                              : scope.row.total +
                                scope.row.drugStuffId.drug.preparationUnit.name
                          }}
                        </span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="allFee" width="width" label="应付金额">
                      <template slot-scope="scope">
                        {{ (scope.row.allFee).toFixed(2) }}元
                      </template>
                    </el-table-column>
                    <el-table-column prop="allFee" width="width" label="实付金额">
                      <template slot-scope="scope">
                        {{ (scope.row.actualPayment).toFixed(2) }}元
                      </template>
                    </el-table-column>
                  </el-table>
                  <el-row>
                    <el-divider content-position="left">医嘱事项</el-divider>
                    <el-input
                      type="textarea"
                      :rows="4"
                      v-model="item.content.recipelInfo.entrust"
                      disabled
                    >
                    </el-input>
                  </el-row>
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-card>
        </el-scrollbar>
      </el-card>
      <el-card v-if="allType != 0">
        <span>共{{ allType }}种，{{ totalMoney.toFixed(2) }}元</span>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
import {
  allQueryMedicalRecord,
  listMedicalRecordPage,
} from "@/api/outpatient/medicalRecord";
import { listRegistrationPages } from "@/api/outpatient/registration";
import { getPatientById, listPatientAll } from "@/api/outpatient/patient";
import {listDictItemAll} from "@/api/sys/dictItem";
import { updateStockStockList } from "@/api/stock/supplierStock";
import SupplierStockForm from "./supplierStockForm";
import ExportExcelButton from "@/components/ExportExcelButton";
import ViewColumnsSelect from "@/views/components/ViewColumnsSelect";
import QueryForm from "@/views/components/queryForm";
import MainUI from "@/views/components/mainUI";
import OperationIcon from "@/components/OperationIcon";
import History from "@/views/components/history";
import {getMember,getByPatientId} from "@/api/member/memberManagement"
export default {
  extends: MainUI,
  components: {
    SupplierStockForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History,
  },
  data() {
    return {
      dispensingPageSize: 15, //每页数量
      //tabs当前为待发药
      ActiveName: "IsWaitingDrug",
      Offset: 0,
      SearchPatientId: "",
      SearchTime: [],
      //所有患者
      AllPatientOption: [],
      WaitingTotal: 0,
      OtherTotal: 0,
      returnTotal: 0,
      //待发药表格数据
      WaitingTable: [],
      //全部发药表格数据
      dispensingTable: [],
      //已退药表格
      returnTable:[],
      //当前就诊信息
      NowVisitInfo: {},
      //当前患者信息
      VisitId: "",
      NowPatientInfo: {
        address: "",
        age: "",
        area: "",
        birthday: "",
        card: "",
        city: "",
        company: "",
        createBy: "",
        createDate: "",
        healthCardNo: "",
        id: "",
        month: "",
        name: "",
        phone: "",
        province: "",
        remarks: "",
        updateBy: "",
        updateDate: "",
        withPatientNexus: "",
        gender: "",
        westernDiagnose: "",
        chinaDiagnose: "",
        dispensingStatus: "",
      },
      //所有处方
      prescriptionALllList: [],
      //复选框选择的表格数据
      CheckMedicineList: [],
      BASE_API: process.env.BASE_API,
      buttonState: false,
      //总金额
      totalMoney: 0,
      //总数
      allType: 0,
      //新的传参实体类
      PageRegistration: {
        columnName: "",
        limit: 0,
        offset: 0,
        order: "desc",
        companyId: "",
        recipeStatus: "0",
        updateDate: "2022-06-01 00:00:00",
        status: "registrationStatus_1",
        chargeStatus: 1,
        dispensionStatus: 0,
        recipelType:'recipelType_3',
        patientName:'',
        patientCode:''
      },
      allCurrentPage: 1, //已发药页码
      dispensingCurrentPage: 1, //未发药页码
      returnCurrentPage: 1, //已退药页码
      prescriptionMainList: [],//处方列表
      patientInfoRow:{},
      patientInfoForm: {
        name: "",
        gender: {
          name: "",
          value: "",
        },
        age: "",
        month: "",
        phone: "",
        card: "",
        westernDiagnose: "无",
        chinaDiagnose: "无",
      },
      medicalEditTabs: [],
      medicalEditTabsValue: {},
      medicalClickTabsValue: {},
      //特殊诊所
      isSpecial: false,
      selectCondition:"",
      defaultNumber:1,//默认组号
      infusionProject:[[]],
      drippingSpeed:[""],  //滴速
      days:[{}],    //天数
      frequency:[{}],   //频次
      infuseUse:[{}],     //用法
      zushu:[1],
      overColor:0,
      allMember:[],  //所有会员卡
      //中药用法下拉列表
      ChineseUseOption: [],
      ChineseTimeOption: [],
      ChineseUseTimeOption: [],
      //西药频度下拉列表
      FrequencyOption: [],
    };
  },
  computed: {
    Company() {
      let company = JSON.parse(sessionStorage.getItem("currentUser")).company;
      return {
        id: company.id,
        label: company.label,
        name: company.name,
      };
    },
    UserId() {
      return JSON.parse(sessionStorage.getItem("currentUser")).id;
    },
    AllFee() {
      let WesternFee = this.WesternCheck ? this.WesternFee : 0;
      let ChineseFee = this.ChineseCheck ? this.ChineseFee : 0;
      let InfusionFee = this.InfusionCheck ? this.InfusionFee : 0;
      let SurchargeFee = this.SurchargeCheck ? this.SurchargeFee : 0;
      let allFee = WesternFee + ChineseFee + InfusionFee + SurchargeFee;
      return allFee;
    },
    AllTotal() {
      let WesternTotal = this.WesternCheck ? this.WesternTable.length : 0;
      let ChineseTotal = this.ChineseCheck ? this.ChineseTable.length : 0;
      let InfusionTotal = this.InfusionCheck ? this.InfusionTable.length : 0;
      let SurchargeTotal = this.SurchargeCheck ? this.SurchargeTable.length : 0;
      let AllTotal =
        WesternTotal + ChineseTotal + InfusionTotal + SurchargeTotal;

      return AllTotal;
    },
  },
  watch: {
    ActiveName() {
      this.SearchPatientId = "";
      this.allCurrentPage = 1
      this.medicalEditTabsValue = {}
      this.medicalEditTabs = []
      this.patientInfoForm = {
        name: "",
        gender: {
          name: "",
          value: "",
        },
        age: "",
        month: "",
        phone: "",
        card: "",
        westernDiagnose: "无",
        chinaDiagnose: "无",
      },
      this.buttonState = false
      this.dispensingCurrentPage = 1
      this.GetDispensingTable();
      this.overColor=0
    },
  },
  mounted() {
    this.Init();
    let specialCompany = JSON.parse(sessionStorage.getItem("currentCompany"));
    if (specialCompany && specialCompany.id == "1088657523871555640") {
      this.isSpecial = true;
    }
  },
  methods: {
    selectPatientChange() {
      if (!this.patientInfoRow) {
        return;
      }
      this.buttonState = true
      this.$nextTick(() => {
        this.$refs["patientInfoForm"].clearValidate();
      });
      if (this.ActiveName == "1") this.buttonState = true;
      this.prescriptionMainList = [];
      this.getSaveInfo(this.patientInfoRow.id);
      this.overColor=0

      //根据患者id获取会员信息
        getByPatientId(this.patientInfoRow.patientId.id).then(res=>{
          if(res.code=='100'){
            this.allMember=res.data
          }else{
            this.$message.error("后台数据异常请联系管理！")
          }
        }).catch()
    },
    //根据就诊人id查询处方病例
    getSaveInfo(visitId) {
      allQueryMedicalRecord(visitId).then((responseData) => {
        if (responseData.code == 100) {
            this.prescriptionMainList = responseData.data.recipelInfoEvtList.filter(item=>item.recipelInfo.recipelType.value!='recipelType_3')
            if(this.ActiveName=='IsWaitingDrug'){
              this.prescriptionMainList =  this.prescriptionMainList.filter(item=>item.recipelInfo.dispensionStatus==0&&item.recipelInfo.chargeStatus==1)
            }else if(this.ActiveName=='IsAll'){
              this.prescriptionMainList =  this.prescriptionMainList.filter(item=>item.recipelInfo.dispensionStatus==1)
            }else{
              this.prescriptionMainList =  this.prescriptionMainList.filter(item=>item.recipelInfo.dispensionStatus==-1)
            }

            this.prescriptionMainList.forEach((item) => {
            item.isExtra = [];
            item.notExtra = [];
            item.recipelDetailEvtList.forEach((items) => {
              if (items.isExtra == 1) {
                item.isExtra.push(items);
              } else {
                item.notExtra.push(items);
              }
            });
          });
          this.patientInfoForm = responseData.data.patient;
          this.patientInfoForm.westernDiagnose =
            responseData.data.medicalRecord.westernDiagnose;
          this.patientInfoForm.chinaDiagnose =
            responseData.data.medicalRecord.chinaDiagnose;
          this.patientInfoForm.pulse = responseData.data.registration.pulse;
          this.patientInfoForm.breathe = responseData.data.registration.breathe;
          this.patientInfoForm.temperature =
            responseData.data.registration.temperature;
          this.patientInfoForm.bloodPressure =
            responseData.data.registration.bloodPressure;
          this.VisitId = visitId;
          this.medicalRecord = responseData.data.medicalRecord;
          this.totalMoney = 0;
          let money = 0;
          this.prescriptionMainList.forEach((item) => {
            console.log(item.recipelInfo.fee, "qian");
            money += item.recipelInfo.fee;
          });
          this.totalMoney = money.toFixed(2);
          this.createMedicalEditTab(this.prescriptionMainList);
        }
      });
    },
    createMedicalEditTab(recipelInfoEvtList) {
      console.log("---------recipelInfoEvtList------------");
      console.log(recipelInfoEvtList);
      this.medicalEditTabs = [];
      if (recipelInfoEvtList == undefined || recipelInfoEvtList == null) {
        return;
      }

      recipelInfoEvtList.forEach((element) => {
        element.uuid = element.recipelInfo.id;
        if(element.recipelInfo.recipelType.value=="recipelType_2"){
           let infusion={
            defaultNumber:1,//默认组号
            infusionProject:[[]],
            drippingSpeed:[""],  //滴速
            days:[{}],    //天数
            frequency:[{}],   //频次
            infuseUse:[{}],     //用法
            zushu:[1],
         }
         let count=0
         for (let i = 0; i < element.recipelDetailEvtList.length; i++) {
              if(count<element.recipelDetailEvtList[i].infuseGroup){
                count=element.recipelDetailEvtList[i].infuseGroup
              }
         }

      for (let i = 1; i < count; i++) {
        infusion.defaultNumber=infusion.defaultNumber+1;
        infusion.zushu.push(infusion.defaultNumber)
        infusion.infusionProject.push([])
        infusion.drippingSpeed.push("")
        infusion.days.push({})
        infusion.frequency.push({})
        infusion.infuseUse.push({})
      }

          let arr=[]
       for (let i = 0; i < element.recipelDetailEvtList.length; i++) {


             if(element.recipelDetailEvtList[i].isExtra!=1){

             infusion.infusionProject[element.recipelDetailEvtList[i].infuseGroup-1].push(element.recipelDetailEvtList[i])
             infusion.drippingSpeed[element.recipelDetailEvtList[i].infuseGroup-1]=element.recipelDetailEvtList[i].drippingSpeed
             infusion.days[element.recipelDetailEvtList[i].infuseGroup-1]=element.recipelDetailEvtList[i].days
             infusion.frequency[element.recipelDetailEvtList[i].infuseGroup-1]=element.recipelDetailEvtList[i].frequency
             infusion.infuseUse[element.recipelDetailEvtList[i].infuseGroup-1]=element.recipelDetailEvtList[i].infuseUse
             }else{


               arr.push(element.recipelDetailEvtList[i])
             }

         }
        let tab = {
          title: element.recipelInfo.name,
          key: element.recipelInfo.seq,
          content: element,
          closable: false,
          type: element.recipelInfo.recipelType.value,
          infusion:infusion
        };
        this.medicalEditTabs.push(tab);
       }else{
          let tab = {
          title: element.recipelInfo.name,
          key: element.recipelInfo.seq,
          content: element,
          closable: false,
          type: element.recipelInfo.recipelType.value,
        };
        this.medicalEditTabs.push(tab);
       }
      });
      this.medicalEditTabsValue = this.medicalEditTabs[0];
      this.medicalClickTabsValue = this.medicalEditTabsValue;
    },
    AlreadyPatientDescriptionsQuery(item,index) {
      console.log(item, "点击处方");
      this.medicalEditTabs.forEach((element) => {
        if (element.content.uuid == item.uuid) {
          this.medicalEditTabsValue = element;
          this.medicalClickTabsValue = this.medicalEditTabsValue;
        }
      });
      this.recipelInfoId = item.id;
      this.overColor=index
    },
    print() {
      console.log(this.medicalEditTabsValue.content)
      if (!this.medicalEditTabsValue.content) {
        this.$message.error("未选择数据");
        return;
      }
        let params = {
          recipelInfoId: this.medicalEditTabsValue.content.recipelInfo.id,
          type: this.medicalEditTabsValue.content.recipelInfo.recipelType.value,
        };
        let str = "";
        let type =
          params.type == "recipelType_0"
            // ? "westMedicine"
            ? "chronicDisease"
            : params.type == "recipelType_1"
            ? "chineseMedicine"
            : params.type == "recipelType_2"
            ? "infuse"
            : "costItem";
        if (params.recipelInfoId) {
          str = "&recipelInfoId=" + params.recipelInfoId;
        }
        let page = window.open(
          this.BASE_API +
            "/ureport/preview?_u=Newtouch:" +
            type +
            ".ureport.xml&_t=0" +
            str +
            "&type=0"
        );
        page.onload=()=>{
          page.document.title = "新致云诊所"
        }
      //    let arr = []
      // this.prescriptionMainList.forEach((item, index) => {
      //   let params = {
      //     recipelInfoId: item.recipelInfo.id,
      //     type: item.recipelInfo.recipelType.value,
      //   };
      //   let str = "";
      //   if (params.recipelInfoId) {
      //     str = "&recipelInfoId=" + params.recipelInfoId;
      //   }
      //   arr[index] = window.open(
      //     this.BASE_API +
      //       "/ureport/preview?_u=Newtouch:charge.ureport.xml&_t=1,6,7,8,9" +
      //       str,
      //     index
      //   );
      //   arr[index].onload=()=>{
      //     arr[index].document.title = "新致云诊所"
      //   }
      // });
    },
    Init() {
      this.GetAllPatient();
      this.GetDispensingTable();
      this.initOptions()
    },
    initOptions(){
      this.getOption("1014474470772899981")
      this.getOption("1014474470772899985")
      this.getOption("1014474470772900058")
      this.getOption("1014474470772899990")
    },
    getOption(optionId){
      let model = {
        params: [{
          columnName: "dict_type_id",
          queryType: "=",
          value: optionId,
        },],
      };
      listDictItemAll(model).then((responseData) => {
        if (optionId == "1014474470772899981") {
          this.ChineseUseOption = responseData.data;
          if (this.isSpecial) {
            this.ChineseUseOption = this.ChineseUseOption.filter(
              (item) => item.name == "水冲"
            );
          }
        }else if (optionId == "1014474470772899985")
          this.ChineseTimeOption = responseData.data;
        else if (optionId == "1014474470772899990")
          this.FrequencyOption = responseData.data;
        else if (optionId == "1014474470772900058")
          this.ChineseUseTimeOption = responseData.data;
      });
    },
    GetSearchTime() {
      this.GetDispensingTable();
    },
    //获取所有患者
    GetAllPatient() {
      let searchModel = {
        params: [
          {
            columnName: "company_id",
            queryType: "=",
            value: this.Company.id,
          },
        ],
      };
      listPatientAll(searchModel).then((responseData) => {
        if (responseData.code == 100) {
          this.AllPatientOption = responseData.data;
        }
      });
    },
    DispensingPage(pageNum) {
      this.dispensingCurrentPage = pageNum;
      this.GetDispensingTable();
    },
    allPage(pageNum) {
      this.allCurrentPage = pageNum;
      this.GetDispensingTable();
    },
    returnPage(pageNum) {
      this.returnCurrentPage = pageNum;
      this.GetDispensingTable();
    },
    clickMedicalEditTab(tab, event)
    {
       if (this.medicalEditTabsValue === this.medicalClickTabsValue) {
        return;
      }

      this.medicalClickTabsValue = this.medicalEditTabsValue;
      console.log(this.medicalEditTabsValue,'看看');
      let num=0
      for (let i = 0; i < this.medicalEditTabs.length; i++) {
        if(this.medicalEditTabs[i].key==this.medicalEditTabsValue.key){
          num=i
        }
      }
       this.AlreadyPatientDescriptionsQuery(this.medicalClickTabsValue.content,num)
    },
    GetDispensingTable() {
      this.PageRegistration.limit = this.dispensingPageSize;
      this.PageRegistration.companyId = this.Company.id;
      if(this.ActiveName=='IsWaitingDrug'){
        this.PageRegistration.offset =
        (this.dispensingCurrentPage - 1) * this.dispensingPageSize;
        this.PageRegistration.dispensionStatus = 0;
        this.PageRegistration.columnName="charge_date"
        this.PageRegistration.chargeStatus=1
        if (this.selectCondition.trim()) {
          this.PageRegistration.patientName=this.selectCondition
          this.PageRegistration.patientCode=this.selectCondition
        }else{
          this.PageRegistration.patientName=''
          this.PageRegistration.patientCode=''
        }
        this.allMember=[]
      }else if(this.ActiveName=='IsAll'){
        this.PageRegistration.offset =
        (this.allCurrentPage - 1) * this.dispensingPageSize;
        this.PageRegistration.dispensionStatus = 1;
        this.PageRegistration.columnName="dispensing_date"
        this.PageRegistration.chargeStatus=1
        if (this.selectCondition.trim()) {
          this.PageRegistration.patientName=this.selectCondition
          this.PageRegistration.patientCode=this.selectCondition
        }else{
          this.PageRegistration.patientName=''
          this.PageRegistration.patientCode=''
        }
        this.allMember=[]
      }else{
        this.PageRegistration.offset =
        (this.returnCurrentPage - 1) * this.dispensingPageSize;
        this.PageRegistration.dispensionStatus = -1;
        this.PageRegistration.columnName="return_date"
        this.PageRegistration.chargeStatus=3
        if (this.selectCondition.trim()) {
          this.PageRegistration.patientName=this.selectCondition
          this.PageRegistration.patientCode=this.selectCondition
        }else{
          this.PageRegistration.patientName=''
          this.PageRegistration.patientCode=''
        }
        this.allMember=[]
      }
      listRegistrationPages(this.PageRegistration).then((responseData) => {
        if (responseData.code == 100) {
          if (this.ActiveName=='IsWaitingDrug') {
            this.WaitingTable = responseData.data.rows
            this.WaitingTotal = responseData.data.total;
          } else if(this.ActiveName=='IsAll'){
            this.dispensingTable = responseData.data.rows;
            this.OtherTotal = responseData.data.total;
          }else{
            this.returnTable = responseData.data.rows;
            this.returnTotal = responseData.data.total;
          }
        }
      });
    },

    indexMethod(index) {
      return ((this.Offset + 10) / 10 - 1) * 10 + index + 1;
    },
    indexMethod2(index) {
      return ((this.Offset + 10) / 10 - 1) * 10 + index + 1;
    },
    //单独发药退药
    singleMedicineClick(type){
      let prescriptionIdList = [];
      this.CheckMedicineList = [];
      prescriptionIdList.push(this.medicalClickTabsValue.content.recipelInfo.id);
      this.medicalClickTabsValue.content.recipelDetailEvtList.forEach((items) => {
          this.CheckMedicineList.push({
            recipelInfoId: items.recipelInfo.id,
            id: items.drugStuffId.drugStuffId,
            type: items.stuffType=='4'?1:0,
            number: items.total,
          });
        });
      let model = {
        registrationId: this.VisitId,
        recipelInfoIdList: prescriptionIdList,
        dispensingStatus:2,
        dispensingType: type,
        dispensingDetailEvtList: this.CheckMedicineList,
      };
      updateStockStockList(model)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.GetDispensingTable();
            this.buttonState = false;
            this.$message.success("操作成功！");
          } else {
            this.showMessage(responseData);
          }
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
    //点击发药按钮0-发药/1-退药
    DispensingMedicineClick(type) {
      let prescriptionIdList = [];
      this.CheckMedicineList = [];
      console.log(this.medicalEditTabs,'发药情况')
      this.medicalEditTabs.forEach((item) => {
        prescriptionIdList.push(item.content.recipelInfo.id);
        item.content.recipelDetailEvtList.forEach((items) => {
          this.CheckMedicineList.push({
            recipelInfoId: items.recipelInfo.id,
            id: items.drugStuffId.drugStuffId,
            type: items.stuffType=='4'?1:0,
            number: items.total,
          });
        });
      });
      let model = {
        registrationId: this.VisitId,
        recipelInfoIdList: prescriptionIdList,
        dispensingStatus:2,
        dispensingType: type,
        dispensingDetailEvtList: this.CheckMedicineList,
      };
     // return
      updateStockStockList(model)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.GetDispensingTable();
            this.buttonState = false;
            this.$message.success("操作成功！");
          }else if(responseData.code=='50001'){
            this.$message.warning(responseData.msg)
          }
          else {
            this.showMessage(responseData);
          }
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
  },
};
</script>
<style lang="scss" scoped>
$borderColor: rgb(219, 219, 219); //灰色边框下划线
.el-row {
  padding: 0;
}
</style>
<style scoped>
/deep/ .el-aside {
  /* background-color: #e6d6d3; */
  color: #333;
  /* text-align: center; */
  /* line-height: 200px; */
  margin-right: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
  height: calc(100vh - 118px);
}

/deep/ .el-main {
  /* background-color: #E9EEF3; */
  color: #333;
  /* text-align: center; */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
  /* line-height: 200px; */
  height: calc(100vh - 118px);
}

/deep/ .el-card__header {
  padding: 12px 20px;
}
/deep/ .el-form-item--mini {
  margin-bottom: 8px !important;
}

/deep/ .main-card {
  margin-bottom: 10px;
}

/deep/ .main-card > .el-card__header {
  padding: 0px 20px;
}

/deep/ .main-card > .el-card__body {
  font-size: 14px;
}

/deep/ .sub-card > .el-card__body {
  padding: 12px 20px;
  font-size: 18px;
  font-weight: bold;
  color: midnightblue;
  line-height: 28px;
}

/deep/ .chinese-medicine-card > .el-card__header {
  padding: 0px 20px;
}

/deep/ .el-divider--horizontal {
  margin: 10px 0 10px 0px;
}

/deep/ .el-tabs__header {
  margin: 0 0 0;
}

/deep/ .el-collapse-item__content {
  padding-bottom: 0;
}

/deep/ .el-divider--vertical {
  width: 5px;
  height: 2em;
  margin-left: 0;
  background-color: #409eff;
}

/deep/ .el-input-group__append {
  padding: 4px;
}

.aside-header {
  font-weight: bold;
  line-height: 10px;
  height: 10px;
  font-size: 14px;
}

/* .box-card {
    width: 100%;
    height: 100%;
  } */

.el-row {
  margin-bottom: 8px;
}

.el-row:last-child {
  margin-bottom: 0;
}

.already-medocal {
  border-bottom: 1px solid #dcdfe6;
  margin-right: 15px;
}
</style>
<style>
.el-popover.medical-type-popover {
  min-width: 85px !important ;
  margin: 0;
  padding: 0;
}
.el-popover.medical-edit-popover {
  margin: 0;
  padding: 0;
}
.ipt-box .el-input--mini {
  width: 70%;
}
.ipt-box {
  display: flex;
  align-items: center;
}

.flex-space-between {
  display: flex;
  justify-content: space-between;
}

.flex-start {
  display: flex;
  justify-content: flex-start;
}

.el-divider--horizontal {
  margin: 5px 0;
}

.goods-back {
  position: absolute;
  top: 60px;
  right: 200px;
  width: 60px;
  height: 25px;
  text-align: center;
  vertical-align: center;
  color: red;
  border: 1px solid red;
  border-radius: 45%;
  cursor: none;
  -webkit-transform: rotate(-45deg);
  float: right;
  z-index: 1;
}
.single{
  padding: 8px 0 0;
    font-size: 14px;
    font-weight: 700;
    color: midnightblue;
    line-height: 28px;
}
.clickTest{
  background-color: rgb(234, 242, 251) !important;
  color: #fff;
}
.imgStyle{

  /* display: inline-flex; */
  position:absolute;
  top: 0;
  right: -30px;
}
.nameStyle{
  position:relative;
  display: inline-block;
  color: forestgreen;
}
</style>
