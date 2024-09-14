<template>
  <el-container>
    <el-aside width="400px">
      <el-card style="height: 100%">
        <!-- <el-row style="text-align:right">
          <el-button
            style="width: 100px"
            @click="changeToRetail"
            >零售收费</el-button
          >
        </el-row> -->
        <el-row style="display: flex; justify-content: flex-start">
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
          <el-tabs stretch v-model="chargeStatusActiveName">
            <el-tab-pane name="1">
              <span slot="label">
                待收费
                <el-badge
                  :value="patientTotalCount"
                  :max="99"
                  type="primary"
                  style="line-height: normal"
                ></el-badge>
              </span>
              <el-scrollbar view-style="height:calc(100vh - 267px)">
                <el-collapse
                  v-model="patientInfoRow"
                  accordion
                  @change="selectPatientChange()"
                >
                  <div v-for="(item, index) in patientList" :key="item">
                    <el-collapse-item class="already-medocal" :name="item">
                      <template slot="title">
                        <span
                          style="
                            font-weight: bold;
                            font-size: 12px;
                            color: #606266;
                            flex: 1;
                          "
                        >{{ patientQueryCondition.offset + index + 1 }}. {{
                            item.patientId.name
                          }}/ {{ item.patientId.gender.name }} /
                      {{ item.patientId.age }}岁</span
                        >
                        <span style="font-size: 12px; color: #606266">{{
                            item.receptionEndDate
                          }}</span>
                      </template>
                      <div
                        v-for="(o,index) in prescriptionMainList"
                        :key="index"
                        @click="AlreadyPatientDescriptionsQuery(o,index)"
                        :class="{clickTest:index==overColor}"
                      >
                        <div style="

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
                            >{{ o.recipelInfo.id }} -->
                        <!-- <span style="position: relative">
                                        <div v-if="o.recipelInfo.status === -1" class="refundBox">作废</div>
                                        <div v-if="o.recipelInfo.status === 1 && o.recipelInfo.chargeStatus === 0" class="refundBox">未收费</div>
                                        <div v-if="o.recipelInfo.status === 1 && o.recipelInfo.chargeStatus === 1" class="refundBox">已收费</div>
                                        <div v-if="o.recipelInfo.status === 1 && o.recipelInfo.chargeStatus === -1" class="refundBox">已退费</div>
                                    </span> -->
                        <!-- </el-descriptions-item>
                      </el-descriptions> -->
                      </div>
                    </el-collapse-item>
                  </div>
                </el-collapse>
              </el-scrollbar>
              <el-pagination
                small
                background
                @current-change="handlePatientPage"
                :page-size="patientPageSize"
                layout="prev, pager, next"
                :total="patientTotalCount"
              >
              </el-pagination>
            </el-tab-pane>
            <el-tab-pane label="已收费" name="2">
              <el-scrollbar view-style="height:calc(100vh - 267px);">
                <el-collapse
                  v-model="patientInfoRow"
                  accordion
                  @change="selectPatientChange()"
                >
                  <div v-for="(item, index) in isPayPatientList" :key="item">
                    <el-collapse-item class="already-medocal" :name="item">
                      <template slot="title">
                        <span
                          style="
                            font-weight: bold;
                            font-size: 12px;
                            color: #606266;
                            flex: 1;
                          "
                        >{{ patientQueryCondition.offset + index + 1 }}. {{
                            item.patientId.name
                          }}/ {{ item.patientId.gender.name }} /
                      {{ item.patientId.age }}岁</span
                        >
                        <span style="font-size: 12px; color: #606266">{{
                            item.chargeDate
                          }}</span>
                      </template>
                      <div
                        v-for="(o,index) in prescriptionMainList"
                        :key="index"
                        @click="AlreadyPatientDescriptionsQuery(o,index)"
                        :class="{clickTest:index==overColor}"
                      >
                        <div style="

                            border-top: 1px dashed #dcdfe6;
                            margin: 0px 30px 0 15px;"></div>
                        <!-- <br> -->
                        <p style="font-weight: bold;margin-top: 15px;margin-left:30px;font-size: 12px;
                            color: #606266;">
                          {{ o.recipelInfo.name }}<span v-if="o.recipelInfo.dispensionStatus==-1"
                                                        style="font-weight: bold;margin-left: 15px;color:red">已退药</span>
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
                            >{{ o.recipelInfo.name }} <span v-if="o.recipelInfo.dispensionStatus==-1" style="font-weight: bold;margin-left: 15px;color:red">已退药</span> </el-descriptions-item
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
                @current-change="handleIsPayPage"
                :page-size="patientPageSize"
                layout="prev, pager, next"
                :total="isPayTotalCount"
              >
              </el-pagination>
            </el-tab-pane>
            <el-tab-pane label="已退费" name="3">
              <el-scrollbar view-style="height:calc(100vh - 267px);">
                <el-collapse
                  v-model="patientInfoRow"
                  accordion
                  @change="selectPatientChange()"
                >
                  <div

                    v-for="(item, index) in returnPatientList"
                    :key="item"
                  >
                    <el-collapse-item class="already-medocal" :name="item">
                      <template slot="title">
                        <span
                          style="
                            font-weight: bold;
                            font-size: 12px;
                            color: #606266;
                            flex: 1;
                          "
                        >{{ index + 1 }}. {{ item.patientId.name }}/ {{ item.patientId.gender.name }} /
                      {{ item.patientId.age }}岁</span
                        >
                        <span style="font-size: 12px; color: #606266">{{
                            item.retreatsDate
                          }}</span>
                      </template>
                      <div v-for="(o,index) in prescriptionMainList" :key="o"
                           :class="{clickTest:index==overColor}"
                           @click="AlreadyPatientDescriptionsQuery(o,index)"
                      >
                        <div style="

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
                @current-change="handleReturnPage"
                :page-size="patientPageSize"
                layout="prev, pager, next"
                :total="returnTotalCount"
              >
              </el-pagination>
            </el-tab-pane>
          </el-tabs>
        </el-row>
      </el-card>
    </el-aside>
    <el-main>
      <el-card class="box-card sub-card" style="height: 50px; display: flex">
        <!-- <div style="font-weight: bold">收费</div> -->
        <div class="flex-space-between">
          <div class="goods-back" v-if="chargeStatusActiveName == '3'&&!isRetail">
            已退费
          </div>
          <div class="goods-back" v-if="chargeStatusActiveName == '2'&&!isRetail">
            已收费
          </div>
          <div v-if="chargeStatusActiveName == '1' && payState &&!isRetail && diagnosisPrescription" style="margin-right:10px">
            总计：{{ this.totalMoney }}元
          </div>
          <!--<div v-if="chargeStatusActiveName == '1' && payState &&!isRetail && diagnosisPrescription" style="margin-right:10px">
            总计：{{ this.hisTotalMoney.toFixed(2) }}元
          </div>-->
          <el-button
            type="primary"
            @click="chargeClick(1)"
            v-if="chargeStatusActiveName == '1' && payState && !isRetail"
          >收费
          </el-button
          >
          <!--<el-button
            type="primary"
            @click="addressee()"
            v-if="diagnosis !== null"
          >添加收件人
          </el-button
          >-->
          <el-popconfirm
            style="margin-right:10px"
            title="确定退费吗？"
            @confirm="chargeClick(2)"
            v-if="chargeStatusActiveName == '2' && selRowChargeStatus != '4' && medicalEditTabs.length>0"
          >
            <el-button style="height:30px" slot="reference" type="primary" plain>退费</el-button>

          </el-popconfirm>

          <el-button
            v-if="chargeStatusActiveName == '2' && medicalEditTabs.length>0"
            type="primary"
            @click="print"
          >打印
          </el-button
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
              :rules="isRetail ? patientInfoRules : patientInfoNoRules"
              label-width="auto"
            >
              <el-row :gutter="24">
                <el-col :span="4">
                  <el-form-item label="患者姓名" prop="name">
                    <div class="nameStyle">
                      {{ patientInfoForm.name ? patientInfoForm.name : "" }}
                      <div class="imgStyle" v-if="allMember.length>0">
                        <el-popover
                          placement="top-start"
                          title=""
                          width="200"
                          trigger="hover"
                        >
                          <div v-for="(item,index) in allMember" :key="index" style="padding:5px">
                            <el-tag>{{ item.memberName }}</el-tag>
                          </div>
                          <img slot="reference" src="../../../assets/images/vip.png" style="width:25px;height:25px"/>
                        </el-popover>
                      </div>

                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="3">
                  <el-form-item label="性别" prop="gender">
                    <!-- <el-select
                      :disabled="isBaseReadOnly"
                      v-model="patientInfoForm.gender.name"
                      style="width: 100%"
                    >
                      <el-option label="男" value="男"></el-option>
                      <el-option label="女" value="女"></el-option>
                    </el-select> -->
                    <span>{{ patientInfoForm.gender.name ? patientInfoForm.gender.name : "" }}</span>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="年龄" prop="age">
                    <!-- <el-input
                      :disabled="isBaseReadOnly"
                      v-model="patientInfoForm.age"
                      style="width: 49%"
                    >
                      <template slot="append">岁</template>
                    </el-input>
                    <el-input
                      :disabled="isBaseReadOnly"
                      v-model="patientInfoForm.month"
                      style="width: 49%"
                    >
                      <template slot="append">月</template>
                    </el-input> -->
                    <span>
                      {{
                        patientInfoForm.age ? patientInfoForm.age : 0
                      }}岁{{ patientInfoForm.month ? patientInfoForm.month : 0 }}月
                    </span>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="联系方式" prop="phone">
                    <!-- <el-input
                      :disabled="isBaseReadOnly"
                      v-model="patientInfoForm.phone"
                    ></el-input> -->
                    <span>
                      {{ patientInfoForm.phone }}
                    </span>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="身份证号" prop="card">
                    <!-- <el-input
                      :disabled="isBaseReadOnly"
                      v-model="patientInfoForm.card"
                    ></el-input> -->
                    <span>{{ patientInfoForm.card }}</span>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="西医诊断" prop="westernDiagnose">
                    <!-- <el-input
                      :disabled="isBaseReadOnly"
                      v-model="patientInfoForm.westernDiagnose"
                    ></el-input> -->
                    <span>
                      {{ patientInfoForm.westernDiagnose ? patientInfoForm.westernDiagnose : "" }}
                    </span>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="中医诊断" prop="chinaDiagnose">
                    <!-- <el-input
                      :disabled="isBaseReadOnly"
                      v-model="patientInfoForm.chinaDiagnose"
                    ></el-input> -->
                    <span>
                      {{ patientInfoForm.chinaDiagnose ? patientInfoForm.chinaDiagnose : "" }}
                    </span>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="24" v-if="diagnosis !== null">
                <el-col :span="4">
                  <el-form-item label="收件人">
                    <span>
                      {{ diagnosis.recipientName }}
                    </span>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="收件人电话">
                    <span>
                      {{ diagnosis.recipientPhone }}
                    </span>
                  </el-form-item>
                </el-col>
                <el-col :span="15">
                  <el-form-item label="收件人地址">
                    <span>
                      {{ diagnosis.province + diagnosis.market + diagnosis.distinguish+diagnosis.recipientAddress }}
                    </span>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-card>
          <el-card class="box-card main-card" v-if="medicalEditTabs.length > 0">
            <!-- <div>
              <el-popover
                placement="top-start"
                width="700"
                trigger="click"
                v-if="!IsOnlyRead"
              >
                <el-table
                  :data="DrugAllTable"
                  border
                  highlight-current-row
                  @row-click="RowClickDrugTable"
                >
                  <el-table-column prop="goodsName" label="药品名称">
                  </el-table-column>
                  <el-table-column prop="type.name" label="药品类型" width="100">
                  </el-table-column>
                  <el-table-column prop="gg" label="规格" width="100">
                    <template slot-scope="scope">
                      {{ scope.row.dosis }}{{ scope.row.dosisUnit.name }} *
                      {{ scope.row.preparation
                      }}{{ scope.row.preparationUnit.name }}/{{
                        scope.row.pack.name
                      }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="factory.name" label="厂家" width="100">
                  </el-table-column>
                  <el-table-column label="单位" width="80">
                    <template slot-scope="scope">
                      {{
                        scope.row.isUnpackSell === "1" ||
                        scope.row.type.value === "medicalType_1"
                          ? scope.row.preparationUnit.name
                          : scope.row.pack.name
                      }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="price" label="零售价" width="80">
                    <template slot-scope="scope">
                      {{
                        scope.row.isUnpackSell === "1" ||
                        scope.row.type.value === "medicalType_1"
                          ? scope.row.retailPrice +
                            "/" +
                            scope.row.preparationUnit.name
                          : scope.row.price + "/" + scope.row.pack.name
                      }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="stockText" label="库存" width="100">
                  </el-table-column>
                </el-table>
                <el-input
                  prefix-icon="el-icon-plus"
                  suffix-icon="el-icon-search"
                  style="width: 30%"
                  slot="reference"
                  v-model="SearchDrugName"
                  @input="GetDrugTable"
                  placeholder="输入药品名称或拼音码"
                ></el-input>
              </el-popover>
            </div> -->
            <div v-if="!isRetail">
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
                    <!-- <el-checkbox :disabled="isReadOnly"></el-checkbox>  -->
                    {{ item.title }}
                  </span>
                  <el-row style="padding-top:8px" type="flex" justify="space-between" align="center"
                          v-if="chargeStatusActiveName == '1' && payState">
                    <div class="single">
                      本处方费用：{{ item.content.recipelInfo.fee }}元
                    </div>
                    <el-popconfirm
                      title="确定收费吗？"
                      @confirm="singleCharge(1)"
                      style="padding-top:7px"
                    >
                      <el-button v-if="!isRetail && !diagnosisPrescription" style="height:30px" slot="reference" type="primary" plain>本处方收费</el-button>

                    </el-popconfirm>
                  </el-row>
                  <el-row type="flex" justify="space-between" align="center" style="padding-top:8px"
                          v-if="chargeStatusActiveName == '2' && medicalEditTabs.length>0">
                    <div class="single">
                      本处方费用：{{ item.content.recipelInfo.amountReceivedTotal }}元
                    </div>
                    <el-popconfirm
                      title="确定退费吗？"
                      @confirm="singleCharge(2)"
                      style="padding-top:7px"
                    >
                      <el-button style="height:30px" slot="reference" type="primary" plain>本处方退费</el-button>

                    </el-popconfirm>
                  </el-row>
                  <el-row style="padding-top:8px" v-if="chargeStatusActiveName == '3' && medicalEditTabs.length>0">
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
                        style="width:100px">
                      </el-input>
                      <el-checkbox v-if="item.type == 'recipelType_0'" style="margin-left: 20px;"
                                   v-model="item.content.recipelInfo.chronicDisease" disabled>是否慢病
                      </el-checkbox>
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
                      <el-table-column label="序号" type="index" align="center">
                      </el-table-column>
                      <el-table-column
                        prop="drugStuffId.name"
                        label="药品名称"
                        width="width">
                        <template slot-scope="scope">
                          {{ scope.row.drugStuffId.name }}
                          <div v-if="item.type=='recipelType_0'">
                        <span class="nameStyle"
                        >{{
                            scope.row.drugStuffId.drug.dosis
                          }}{{ scope.row.drugStuffId.drug.dosisUnit.name }} *
                          {{
                            scope.row.drugStuffId.drug.preparation
                          }}{{ scope.row.drugStuffId.drug.preparationUnit.name }}/{{
                            scope.row.drugStuffId.drug.pack.name
                          }}</span
                        >
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column
                        prop="drugStuffId.drug.factory.name"
                        label="厂家"
                        width="width"
                        v-if="!isRetail && !diagnosisPrescription"
                      >
                      </el-table-column>

                      <el-table-column
                        prop="westernMedicineUse.name"
                        label="用法"
                        width="width"
                        v-if="item.type == 'recipelType_0' && !diagnosisPrescription"
                      >
                      </el-table-column>
                      <el-table-column
                        prop="singleDosage"
                        label="单次用量"
                        width="width"
                        v-if="item.type!='recipelType_3'"
                      >
                        <template slot-scope="scope">
                          <template>{{
                              scope.row.singleDosage ? scope.row.singleDosage + '' + scope.row.drugStuffId.dosisUnit.name : ""
                            }}
                          </template>
                        </template>
                      </el-table-column>
                      <el-table-column
                        prop="frequency"
                        label="频次"
                        align="center"
                        v-if="item.type == 'recipelType_0' && !diagnosisPrescription"
                        width="width"
                      >
                        <template slot-scope="scope"
                        >{{
                            scope.row.infuseUse.name
                          }}{{ scope.row.frequency.name }}
                        </template
                        >
                      </el-table-column>
                      <el-table-column prop="days.name" label="天数" width="width" align="center"
                                       v-if="item.type == 'recipelType_0'">
                        <template slot-scope="scope">
                          {{ scope.row.days.name }}天
                        </template>
                      </el-table-column>
                      <el-table-column prop="total" label="数量" width="width">
                        <template slot-scope="scope">
                        <span v-if="scope.row.drugStuffId.costItem != null">
                          {{
                            scope.row.total
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
                      <el-table-column prop="allFee" width="width" label="应付金额">
                        <template slot-scope="scope">
                          {{ (scope.row.allFee).toFixed(2) }}元
                        </template>
                      </el-table-column>
                      <el-table-column prop="actualPayment" width="width" label="实付金额">
                          <template slot-scope="scope" v-if="chargeStatusActiveName !== '1'">
                            {{ (scope.row.actualPayment) == null ? (scope.row.allFee).toFixed(2) : (scope.row.actualPayment).toFixed(2)}}元
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
                     <el-input style="width:80px" disabled v-model="item.content.recipelInfo.singleDosage">
                     </el-input>{{'格'}}
                   </div>-->
                    <el-row v-if="item.type=='recipelType_1'">
                      <el-input disabled v-model="item.content.recipelInfo.dosage"
                                oninput="value=value.replace(/[^\d.]/g,'')" style="width: 60px"></el-input>
                      &nbsp;剂 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;用法：
                      <el-select disabled v-model="item.content.recipelInfo.recipelUse" placeholder="请选择"
                                 style="width: 110px">
                        <el-option v-for="pitem in ChineseUseOption" :key="pitem.value"
                                   :label="pitem.name"
                                   :value="{ name: pitem.name, value: pitem.value }">
                        </el-option>
                      </el-select>
                      <el-select disabled v-model="item.content.recipelInfo.frequency" placeholder="请选择"
                                 style="width: 110px">
                        <el-option v-for="pitem in ChineseTimeOption" :key="pitem.value"
                                   :label="pitem.name"
                                   :value="{ name: pitem.name, value: pitem.value }">
                        </el-option>
                      </el-select>
                      <el-select disabled v-model="item.content.recipelInfo.takeFrequency" placeholder="请选择"
                                 style="width: 110px">
                        <el-option v-for="pitem in JSON.parse(
                            JSON.stringify(FrequencyOption)
                          )" :key="pitem.value" :label="pitem.name" :value="{
                            name: pitem.name,
                            value: pitem.value,
                          }">
                        </el-option>
                      </el-select>
                      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 一次&nbsp;<el-input disabled
                                                                             v-model="item.content.recipelInfo.singleDosage"
                                                                             style="width: 60px"></el-input>&nbsp;
                      <span>ml</span>
                    </el-row>
                  </div>

                  <div v-else style="margin-left:-10px;">
                    <div v-for="(items,index) in item.infusion.zushu" :key="index"
                         style="border:1px solid #DCDCDC;padding:10px;margin:10px;">

                      <div>
                        <span
                          style="float:left;height:25px;line-height:25px;font-size:14px;">&nbsp;&nbsp;第{{
                            index + 1
                          }}组</span>
                        <span style="float:right;height:25px;line-height:25px;font-size:14px;">&nbsp;&nbsp;滴/分钟</span>

                        <el-input
                          style="float:right;width:10%"
                          :value="item.infusion.drippingSpeed[index]"
                          placeholder=""
                          :disabled="
                               true
                              "
                        >

                        </el-input>

                        <span
                          style="float:right;height:25px;line-height:25px;font-size:14px;">&nbsp;&nbsp;天&nbsp;&nbsp;</span>
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

                        <span style="float:right;height:25px;line-height:25px;font-size:18px;">&nbsp;&nbsp;</span>
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

                        <span style="float:right;height:25px;line-height:25px;font-size:18px;">&nbsp;&nbsp;</span>
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
                        <!-- <el-table-column label="组数" prop="infuseGroup" width="width">

                        </el-table-column> -->
                        <el-table-column label="序号" type="index" align="center" width="width">
                        </el-table-column>
                        <el-table-column
                          prop="drugStuffId.name"
                          label="药品名称"
                          width="width"
                        >
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
                        <el-table-column
                          prop="singleDosage"
                          label="单次用量"
                          width="width"
                        >
                          <template slot-scope="scope">
                            <template>{{
                                scope.row.singleDosage + '' + scope.row.drugStuffId.dosisUnit.name
                              }}
                            </template>
                          </template>
                        </el-table-column>
                        <el-table-column prop="total" label="数量" width="150">
                          <template slot-scope="scope">
                        <span v-if="scope.row.drugStuffId.costItem != null">
                          {{
                            scope.row.total
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
                        <el-table-column prop="allFee" label="应付金额" width="width">
                          <template slot-scope="scope">
                            {{ (scope.row.allFee).toFixed(2) }}元
                          </template>
                        </el-table-column>
                        <el-table-column prop="actualPayment" label="实付金额" width="width">
                          <template slot-scope="scope">
                            {{ (scope.row.actualPayment).toFixed(2) }}元
                          </template>
                        </el-table-column>

                      </el-table>
                    </div>
                  </div>
                  <el-divider style="margin-top: 5px" content-position="left">附加费</el-divider>
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
                    <el-table-column prop="total" label="数量" width="width">
                      <template slot-scope="scope">
                        <span v-if="scope.row.drugStuffId.costItem != null">
                          {{
                            scope.row.total
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
                    <el-table-column prop="allFee" label="应付金额" width="width">
                      <template slot-scope="scope">
                        {{ (scope.row.allFee).toFixed(2) }}元
                      </template>
                    </el-table-column>
                    <el-table-column prop="actualPayment" label="实付金额" width="width">
                      <template slot-scope="scope">
                        {{ (scope.row.actualPayment = null ? 0 : scope.row.actualPayment).toFixed(2) }}元
                      </template>
                    </el-table-column>
                    <!-- <el-table-column label="操作" align="center" v-if="!IsOnlyRead">
                <template slot-scope="scope">
                  <i
                    class="el-icon-circle-close"
                    @click="DeleteDrugItem(scope.$index, scope.row)"
                  ></i>
                </template>
              </el-table-column> -->
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
    </el-main>
    <el-dialog
      title="收费"
      :visible.sync="chargeDialogVisible"
      width="50%"
      :close-on-click-modal="false"
      :before-close="chargeFormCancel"
    >
      <div>
        <el-divider></el-divider>
        <div id="chargeTotalFee_Container">
          <div id="chargeTotalFee_Title">实收金额（元）：</div>
          <div id="chargeTotalFee_Fee" v-if="this.chargeForm.amountReceived && !diagnosisPrescription">
            {{ this.chargeForm.amountReceived }}元
          </div>
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <div id="chargeTotalFee_Title">应收金额（元）：</div>
          <div id="chargeTotalFee_Fee" v-if="this.chargeForm.amountReceivable">
            {{ this.chargeForm.amountReceivable }}元
          </div>
        </div>
        <el-form ref="chargeForm" :rules="rules" :model="chargeForm" label-width="80px">
          <el-row>

            <el-col :span="8" v-if="!diagnosis">
              <el-form-item label="折扣">
                <el-input
                  v-model="chargeForm.discount"
                  @input="GetAmountReceived"
                  placeholder="折扣"
                >
                  <template slot="append">折</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8" :offset="1" v-if="!diagnosis">
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
          <el-row v-if="chargeForm.paymentType=='payType_0'">
            <el-col :span="8">
              <el-form-item label="实付金额" prop="amountPaidRule">
                <el-input
                  v-model="amountPaid"
                  @input=" inputAmountPaid"
                  placeholder="实付金额">
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
                <!--<el-radio-group v-model="chargeForm.paymentType">
                  <el-radio label="payType_0">现金</el-radio>
                  <el-radio label="payType_1">支付宝</el-radio>
                  <el-radio label="payType_2">微信</el-radio>
                  <el-radio label="payType_3">银行卡</el-radio>
                </el-radio-group>-->
                <el-radio-group
                  v-model="chargeForm.paymentType"
                  class="pay-registration-content"
                >
                  <el-radio
                    v-for="ptype in payType_List"
                    :key="ptype.value"
                    :label="ptype.value"
                  >{{ ptype.name }}
                  </el-radio
                  >
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="备注">
                <el-input type="textarea" :rows="3" v-model="chargeForm.meno"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <el-divider></el-divider>
      </div>
      <div slot="footer">
        <el-button @click="chargeFormCancel">取 消</el-button>
        <el-button type="primary" @click="submit">确认收费</el-button>
      </div>
    </el-dialog>
    <el-dialog
      title="单处方收费"
      :visible.sync="singleDialogVisible"
      width="50%"
      :close-on-click-modal="false"
      :before-close="singleFormCancel"
    >
      <div>
        <el-divider></el-divider>
        <div class="chargeTotalFee_Container">
          <div class="chargeTotalFee_Title">实收金额（元）：</div>
          <div class="chargeTotalFee_Fee" v-if="this.singleForm.amountReceived">
            {{ this.singleForm.amountReceived }}元
          </div>
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <div id="chargeTotalFee_Title">应收金额（元）：</div>
          <div id="chargeTotalFee_Fee" v-if="this.singleForm.amountReceivable">
            {{ this.singleForm.amountReceivable }}元
          </div>
        </div>
        <el-form ref="singleForm" :model="singleForm" label-width="80px">
          <el-row>

            <el-col :span="8">
              <el-form-item label="折扣">
                <el-input
                  v-model="singleForm.discount"
                  @input="GetSingleAmountReceived"
                  placeholder="折扣"
                >
                  <template slot="append">折</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8" :offset="1">
              <el-form-item label="减免金额">
                <el-input
                  v-model="signleAmountOfRemission"
                  placeholder="减免金额"
                  disabled
                  @input="GetSingleAmountReceived"
                >
                  <template slot="append">元</template>
                </el-input>
              </el-form-item>
            </el-col>
            <!-- <el-col :span="10" :offset="1">
              <el-form-item label="应收金额">
                <el-input v-model="singleForm.amountReceivable">
                  <template slot="append">元</template>
                </el-input>
              </el-form-item>
            </el-col> -->
          </el-row>
          <el-row v-if="chargeForm.paymentType=='payType_0'">
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
            <el-col :span="15">
              <el-form-item label="支付方式">
                <!--<el-radio-group v-model="singleForm.paymentType">
                  <el-radio label="payType_0">现金</el-radio>
                  <el-radio label="payType_1">支付宝</el-radio>
                  <el-radio label="payType_2">微信</el-radio>
                  <el-radio label="payType_3">银行卡</el-radio>
                </el-radio-group>-->
                <el-radio-group
                  v-model="chargeForm.paymentType"
                  class="pay-registration-content"
                >
                  <el-radio
                    v-for="ptype in payType_List"
                    :key="ptype.value"
                    :label="ptype.value"
                  >{{ ptype.name }}
                  </el-radio
                  >
                </el-radio-group>
              </el-form-item>

            </el-col>
          </el-row>
          <el-row v-if="member.length>0&&this.medicalEditTabsValue.type=='recipelType_3'">
            <el-col :span="24">
              <el-form-item label="会员卡">
                <el-select
                  v-model="singleForm.member"
                  value-key="id"
                  filterable
                  style="width:300px"
                  allow-create
                  placeholder="请选择会员卡"
                  @change="chooseMember"
                >
                  <el-option
                    v-for="item in member"
                    :key="item.id"
                    :label="item.memberSet.name"
                    :value="item"
                  >
                    <el-row>
                      <el-col :span="8">
                        <div><span style="float: left">{{ item.memberSet.name }}</span></div>
                      </el-col>
                      <el-col :span="8">
                        <div><span

                        >{{ item.memberSet.type.name }}</span
                        ></div>
                      </el-col>
                      <el-col :span="8">
                        <div>
                     <span
                     >{{ item.card }}</span
                     >
                        </div>
                      </el-col>
                    </el-row>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="备注">
                <el-input type="textarea" :rows="3" v-model="singleForm.meno"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <el-divider></el-divider>
      </div>
      <div slot="footer">
        <el-button @click="singleFormCancel">取 消</el-button>
        <el-button type="primary" @click="singleChargeBtn(1)">确认收费</el-button>
      </div>
    </el-dialog>
    <el-dialog
      title="添加收件人信息"
      :visible.sync="addresseeDialogVisible"
      width="50%"
      :close-on-click-modal="false"
      :before-close="chargeFormCancel"
      v-if="diagnosis !== null"
    >
      <div>
        <el-form ref="diagnosis" :rules="rules" :model="diagnosis" label-width="100px">
          <el-row>
            <el-col :span="12">
              <el-form-item label="收件人姓名">
                <el-input :rows="3" v-model="diagnosis.recipientName"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="收件人电话">
                <el-input :rows="3" v-model="diagnosis.recipientPhone"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <!--<el-form-item label='所在地区' prop=''>
                <v-distpicker :province="province" :city="city" :area="area"
                              @province="onChangeProvince" @city="onChangeCity" @area="onChangeArea"></v-distpicker>
              </el-form-item>-->
              <el-form-item label="收件人地址">
                所属省：
                <el-input  :rows="3" v-model="diagnosis.province"></el-input>
                所属市：
                <el-input  :rows="3" v-model="diagnosis.market"></el-input>
                所属镇/区：
                <el-input  :rows="3" v-model="diagnosis.distinguish"></el-input>
                详细地址：
                <el-input  :rows="3" v-model="diagnosis.recipientAddress"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <el-divider></el-divider>
      </div>
      <div slot="footer">
        <el-button @click="chargeFormCancel">取 消</el-button>
        <el-button type="primary" @click="addresseeSave">保 存</el-button>
      </div>
    </el-dialog>
  </el-container>
</template>

<script>
  import {listDrugPage, listAllStock} from "@/api/stock/drug";
  import {saveTollTollInfo} from "@/api/toll/tollInfo";
  import {listRegistrationPages} from "@/api/outpatient/registration";
  import {getMember, getByPatientId} from "@/api/member/memberManagement"
  import {
    listDiagnosisPage,
    saveDiagnosis,
    chargeState,
    getRegistrationId,
    modifiedState
  } from '@/api/outpatient/remoteDiagnosisTreatment'
  import {getPatientById, listPatientAll} from "@/api/outpatient/patient";
  import {getRecipelInfoById} from "@/api/outpatient/recipelInfo";
  import {
    allSaveMedicalRecord,
    allQueryMedicalRecord,
    getMedicalRecordById,
    listMedicalRecordPage,
  } from "@/api/outpatient/medicalRecord";
  import {listDictItemAll} from "@/api/sys/dictItem";
  import BaseUI from "@/views/components/baseUI";
  import {getLocalToken} from '@/utils/auth';
  import axios from 'axios'
  import Vue from "vue";
  Vue.prototype.$axios = axios;

  const loginForm = {Data: "API.Manage", AppId: "Oh_Newtouch_Clinic"}
  const config = {
    headers: {
      Token: getLocalToken()
    }
  }

  export default {
    extends: BaseUI,
    data() {
      let cardIdNo = (rule, value, callback) => {
        if (!value) {
          return callback(new Error("身份证号不能为空"));
        } else {
          let reg = /((^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$))/;
          if (!reg.test(value)) {
            callback(new Error("请输入正确的身份证号"));
          } else {
            callback();
          }
        }
      };
      let amountPaidRule = (rule, value, callback) => {
        //这里为什么不用 value,因为之前 写这个代码的人没有按照element-ui的规则写,他绑定的数据不是表单里面的，气人啊
        console.log(this.amountPaid, this.chargeForm.amountReceived,
          Number(this.amountPaid) <= Number(this.chargeForm.amountReceived), "cao")
        if (!this.amountPaid) {
          callback(new Error("请输入实付金额"));
        } else {
          if (Number(this.amountPaid) < Number(this.chargeForm.amountReceived)) {
            callback(new Error("实付金额不能低于实收金额"));
          } else {
            callback();
          }
        }
      };
      return {
        //省市区三级联动
        province:"",
        market:"",
        distinguish:"",

        hisPatient: null, //his患者会诊信息
        tokenData: null, // hisToken
        orderForm: null, // 订单信息
        hisCheck: null, // his待支付账单
        hisCheckDetail: null, // his待支付账单明细
        diagnosisPrescription: false, // 是否远程诊疗处方
        prescriptionCollection: [], // 远程诊疗处方集合

        isBaseReadOnly: true,
        isRetail: false, //是否零售
        selectCondition: "", //病人查询条件
        chargeStatusActiveName: "1", //收费状态标签页
        addresseeDialogVisible: false,
        chargeDialogVisible: false,
        singleDialogVisible: false,
        chargeTotalFee: 100,
        chargeForm: {
          amountReceivable: 0,
          amountDiscounted: 0,
          discount: 10,
          amountReceived: 0,
          paymentType: "payType_0",
          meno: "",
          amountPaid: 0,
        },
        singleForm: {
          amountReceivable: 0,
          amountDiscounted: 0,
          discount: 10,
          amountReceived: 0,
          paymentType: "payType_0",
          meno: "",
        },
        refundDialogVisible: false,
        patientQueryCondition: {
          columnName: "",
          limit: 0,
          offset: 0,
          order: "",
          companyId: "",
          recipeStatus: "0",
          updateDate: "2022-06-01 00:00:00",
          status: "registrationStatus_1",
          chargeStatus: "",
          dispensionStatus: 3,
          patientName: null,
          patientCode: null,
        },
        patientList: [], //未收费病人列表
        isPayPatientList: [], //已收费病人列表
        returnPatientList: [], //已退费病人列表
        patientTotalCount: 0, //未收费病人总数
        isPayTotalCount: 0, //已收费病人总数
        returnTotalCount: 0, //已退费病人总数
        patientPageSize: 15, //每页显示的病人数量
        patientCurrentPage: 1, //未收费病人当前页
        isPayPatientCurrentPage: 1, //已收费病人当前页
        returnPatientCurrentPage: 1, //已退费病人当前页
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
        patientInfoNoRules: null,
        patientInfoRules: {
          name: [
            {required: true, message: "请输入姓名", trigger: "blur"},
            {min: 1, max: 50, message: "长度在1到50个字符", trigger: "blur"},
          ],
          gender: [{required: true, message: "请选择性别", trigger: "blur"}],
          age: [{required: true, message: "请输入年龄(岁)", trigger: "blur"}],
          month: [{required: true, message: "请输入年龄(月)", trigger: "blur"}],
          phone: [
            {required: true, message: "请输入联系电话", trigger: "blur"},
            {min: 1, max: 20, message: "长度在1-20个字符", trigger: "blur"},
          ],
          card: [
            {
              required: true,
              trigger: "blur",
              validator: cardIdNo,
            },
          ],
        },
        genderList: [
          {
            value: "gender_0",
            name: "男",
          },
          {
            value: "gender_1",
            name: "女",
          },
        ],
        /*待收费处方列表*/
        prescriptionMainList: [],
        refundPrescriptionMainList: [
          {
            prescriptionType: 1,
            isSelect: true,
            totalCost: 50,
            prescriptionDetail: [
              {
                goodsName: "test",
                factoryName: "",
                price: 10,
                quantity: 5,
                unit: "",
                totalCost: 50,
              },
            ],
          },
          {
            prescriptionType: 2,
            isSelect: true,
            totalCost: 150,
            prescriptionDetail: [
              {
                goodsName: "test2",
                factoryName: "",
                tisanesMethod: "",
                price: 10,
                quantity: 5,
                totalQuantity: 15,
                totalCost: 150,
              },
            ],
          },
          {
            prescriptionType: 3,
            isSelect: true,
            totalCost: 50,
            prescriptionDetail: [
              {
                goodsName: "test3",
                price: 10,
                quantity: 5,
                totalCost: 50,
              },
            ],
          },
        ],
        //所有患者
        AllPatientOption: [],
        //选择处方
        WesternCheck: true,
        ChineseCheck: true,
        InfusionCheck: true,
        SurchargeCheck: true,
        CostItemCheck: true,
        //当前病历
        medicalRecord: {},
        //当前就诊号
        VisitId: "",
        //选中的左侧收费记录的收费状态
        selRowChargeStatus: false,
        DrugAllTable: [],
        SearchDrugName: "",
        RetailTable: [],
        //点击患者显示的处方列表
        AlreadyPatientDescriptions: [],
        //只读
        isOnlyRead: false,
        BASE_API: process.env.BASE_API,
        totalMoney: 0, //总金额
        hisTotalMoney: 0, //远程诊疗总金额
        payState: true,
        medicalEditTabs: [],
        medicalEditTabsValue: {},
        medicalClickTabsValue: {},
        //新的传参实体类
        PageRegistration: {
          columnName: "",
          limit: 0,
          offset: 0,
          order: "",
          companyId: "",
          recipeStatus: "0",
          updateDate: "2022-06-01 00:00:00",
          status: "registrationStatus_1",
          chargeStatus: "",
          patientName: null,
          patientCode: null,
        },
        defaultNumber: 1,//默认组号
        infusionProject: [[]],
        drippingSpeed: [""],  //滴速
        days: [{}],    //天数
        frequency: [{}],   //频次
        infuseUse: [{}],     //用法
        zushu: [1],
        excharge: [],  //输液附加费
        overColor: 0,
        member: [],   //会员卡
        allMember: [],  //所有会员卡
        diagnosis: null,  //远程诊疗信息 诊断
        memberCopy: [],  //本次使用次数暂存
        saveMember: {},  //保存会员体验卡
        amountOfRemission: "0",  //减免金额
        signleAmountOfRemission: "0", //单独收费
        amountPaid: 0, //实付金额
        amountOfChange: 0,//找补金额

        payType_List: [], // 支付方式

        //中药用法下拉列表
        ChineseUseOption: [],
        ChineseTimeOption: [],
        ChineseUseTimeOption: [],
        //特殊诊所
        isSpecial: false,
        //西药频度下拉列表
        FrequencyOption: [],
        rules: {
          amountPaidRule: [
            {
              required: true,
              trigger: "blur",
              validator: amountPaidRule,
            },
          ],
        },
      };
    },
    computed: {
      // 获取到省市区三级联动的值
      onChangeProvince(data) {
        this.province = data.value
        console.log("5555555555555555"+data.value)

      },
      onChangeCity(data) {
        this.city = data.value

      },
      onChangeArea(data) {
        this.area = data.value
      },

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
      AllTotal() {
        return (
          // this.WesternFee +
          // this.ChinesePrescription.fee +
          // this.InfusionFee +
          // this.CostItemFee +
          // this.SurchargeFee
          0
        );
      },
    },
    watch: {
      chargeStatusActiveName() {
        this.isRetail = false
        this.search();
        this.overColor = 0
      },
    },
    methods: {

      // 获取远程诊所token
      async getToken() {
        await this.$axios.post('/token/Auth/GetAppFrienAuthToken', loginForm, config)
          .then((response) => {
            this.tokenData = response.data.BusData.data.Token
            console.log(response.data.BusData.data.Token)
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      // his患者会诊信息
      async inquireHis(){
        await this.getToken()
        await this.$axios.post('apis/RemoteTreated/TreatedApplyInfo', {
          Data: {
            sqlsh: this.diagnosis.id // 申请流水号
          },
          OrganizeId: this.diagnosis.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            this.hisPatient = response.data.BusData.data
            console.log("his患者会诊信息"+JSON.stringify(this.hisPatient))
            console.log(response);
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      // 地址新增
      async PatientAddressAdd(){
        await this.getToken()
        await this.$axios.post('apis/patient/PatientAddressAdd', {
          Data: {
            Id: this.diagnosis.id, // 地址id
            patid: this.hisPatient.patid, // 患者id
            OrganizeId: this.diagnosis.hospitalId, // 机构
            xm: this.diagnosis.recipientName, // 收件人姓名
            dh: this.diagnosis.recipientPhone, // 收件人电话
            xian_sheng: this.diagnosis.province, // 收件人省
            xian_shi: this.diagnosis.market, // 收件人市
            xian_xian: this.diagnosis.distinguish, // 收件人县
            xian_dz: this.diagnosis.recipientAddress // 收件人详细地址
          },
          OrganizeId: this.diagnosis.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            console.log("地址新增"+JSON.stringify(response.data.BusData.data))
            console.log(response);
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      // 地址修改
      async PatientAddressUpdate(){
        await this.getToken()
        await this.$axios.post('apis/patient/PatientAddressUpdate', {
          Data: {
            Id: this.diagnosis.id, // 地址id
            patid: this.hisPatient.patid, // 患者id
            OrganizeId: this.diagnosis.hospitalId, // 机构
            xm: this.diagnosis.recipientName, // 收件人姓名
            dh: this.diagnosis.recipientPhone, // 收件人电话
            xian_sheng: this.diagnosis.province, // 收件人省
            xian_shi: this.diagnosis.market, // 收件人市
            xian_xian: this.diagnosis.distinguish, // 收件人县
            xian_dz: this.diagnosis.recipientAddress // 收件人详细地址
          },
          OrganizeId: this.diagnosis.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            console.log("地址修改"+JSON.stringify(response.data.BusData.data))
            console.log(response);
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      // his待支付账单
      async pendingBill(){
        this.hisTotalMoney = 0;
        await this.getToken()
        await this.$axios.post('apis/Outpatient/OutpBillUnpaidByMzh', {
          Data: {
            kh: this.hisPatient.kh, // 卡号
            mzh: this.hisPatient.mzh // 门诊号
          },
          OrganizeId: this.diagnosis.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            this.hisCheck = response.data.BusData.data
            for (let i = 0; i < this.hisCheck.length; i++) {
              this.prescriptionCollection[this.hisCheck[i].PresId]
              this.hisTotalMoney += this.hisCheck[i].PresAmt;
            }
            console.log("his待支付账单"+JSON.stringify(this.hisCheck))
            console.log("查看远程诊疗处方id"+this.prescriptionCollection);
            console.log("查看远程诊疗付款金额"+this.hisTotalMoney);
            console.log(response);
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      // his待支付账单明细
      async billingDetail(presIds){
        await this.getToken()
        await this.$axios.post('apis/Outpatient/OutpBillItemDetail', {
          Data: {
            PresIds: this.prescriptionCollection, // 处方id集合
            mzh: this.hisPatient.mzh // 门诊号
          },
          OrganizeId: this.diagnosis.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            this.hisCheckDetail = response.data.BusData.data
            /*for (let i = 0; i < this.hisCheckDetail.length; i++) {
              this.hisTotalMoney += this.hisCheckDetail[i].Amt;
            }*/
            console.log("his患者会诊信息"+JSON.stringify(this.hisCheckDetail))
            console.log(response);
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      // 查询订单
      async orderQuery(){
        await this.getToken();
        await this.$axios.post('apis/PayOrder/OrderQuery', {
          Data: {
            OrderNo: "", // 订单号
            OrderType: 1, // 订单类型
            CardNo: this.hisPatient.kh, //就诊卡号
            VisitNo: this.hisPatient.mzh,  // 门诊号
            OrderStu: null // 订单状态
          },
          OrganizeId: this.diagnosis.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            this.orderForm = response.data.BusData.data[0]
            console.log("锁定订单后返回信息"+JSON.stringify(response.data.BusData.data));
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      // 创建订单
      async createOrder(){
        await this.getToken()
        await this.$axios.post('apis/PayOrder/BillOrderCreate', {
          Data: {
            kh: this.hisPatient.kh, // 卡号
            mzh: this.hisPatient.mzh, // 门诊号
            OrderType: 1, // 1 为门诊订单 2为住院订单
            addressInfo: {
              patid: this.hisPatient.patid, // 患者id
              OrganizeId: this.diagnosis.hospitalId, // 机构
              xm: this.diagnosis.recipientName, // 收件人姓名
              dh: this.diagnosis.recipientPhone, // 收件人电话
              xian_sheng: this.diagnosis.province, // 收件人省
              xian_shi: this.diagnosis.market, // 收件人市
              xian_xian: this.diagnosis.distinguish, // 收件人县
              xian_dz: this.diagnosis.recipientAddress // 收件人详细地址
            }
          },
          OrganizeId: this.diagnosis.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            this.orderForm = response.data.BusData.data
            console.log("创建订单后返回信息"+JSON.stringify(response.data.BusData.data));
          })
          .catch(function (error) {
            console.log(error);
          });

        await this.$axios.post('apis/PayOrder/BillOrderLockApply', {
          Data: {
            kh: this.hisPatient.kh, // 卡号
            OrderNo: this.orderForm.OrderNo, // 订单号
            OrderAmt: this.orderForm.OrderAmt // 订单金额
          },
          OrganizeId: this.diagnosis.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            console.log("锁定订单后返回信息"+JSON.stringify(response.data.BusData.data));
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      // 锁定订单
      async lockedOrder(){
        await this.getToken();
        await this.$axios.post('apis/PayOrder/BillOrderLockApply', {
          Data: {
            kh: this.hisPatient.kh, // 卡号
            OrderNo: this.orderForm.OrderNo, // 订单号
            OrderAmt: this.orderForm.OrderAmt // 订单金额
          },
          OrganizeId: this.diagnosis.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            console.log("锁定订单后返回信息"+JSON.stringify(response.data.BusData.data));
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      // 订单结算
      async orderSettlement(){
        await this.getToken();
        await this.$axios.post('apis/PayOrder/BillOrderPay', {
          Data: {
            CardNo: this.hisPatient.kh, // 就诊卡号
            OrderNo: this.orderForm.OrderNo, // 订单号
            PayWay: "12", // 支付方式(门诊支付)
            PayFee: this.chargeForm.amountReceivable, // 支付费用
            PayLsh: this.diagnosis.registrationId, // 支付流水号(使用云诊所挂号id)
            PayerId: this.hisPatient.patid, // 支付者身份标识（his患者id）

            PayTime: new Date(), // 支付时间

            OrderType: 1 // 订单类型
          },
          OrganizeId: this.diagnosis.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            console.log("结算订单后返回信息"+JSON.stringify(response.data.BusData.data));
          })
          .catch(function (error) {
            console.log(error);
          });
      },


      singleFormCancel() {
        this.singleDialogVisible = false
        this.singleForm.discount = 10
        this.signleAmountOfRemission = 0
        this.amountPaid = 0
        this.amountOfChange = 0

      },

      //整体收费取消
      chargeFormCancel() {
        this.addresseeDialogVisible = false,
        this.selectPatientChange()
        this.chargeDialogVisible = false
        this.chargeForm.discount = 10
        this.amountPaid = 0
        this.amountOfRemission = 0
        this.amountOfChange = 0
        this.$refs["chargeForm"].resetFields();
      },

      //关于现金支付的找补金额计算
      inputAmountPaid() {
        this.amountOfChange = 0
        let amount = this.amountPaid ? this.amountPaid : 0
        if (amount > 0) {
          console.log(this.singleForm.amountReceived);
          if (this.singleForm.amountReceived > 0) {
            this.amountOfChange = (this.amountPaid - this.singleForm.amountReceived).toFixed(2)
          } else {

            this.amountOfChange = (this.amountPaid - this.chargeForm.amountReceived).toFixed(2)
          }
        }
      },
      //选择体验卡进行扣减
      chooseMember(row) {
        this.saveMember = {};
        let memberCopy = JSON.parse(JSON.stringify(this.memberCopy))
        if (memberCopy) {
          for (let i = 0; i < memberCopy.length; i++) {
            if (memberCopy[i].id == row.id) {
              row = memberCopy[i]
            }
          }
        }
        this.singleForm.amountReceived = this.singleForm.amountReceivable
        console.log(row, '选择的体验卡');
        console.log(this.medicalEditTabsValue, '这个对于你的项目');
        for (let i = 0; i < this.medicalEditTabsValue.content.recipelDetailEvtList.length; i++) {
          for (let j = 0; j < row.memberItems.length; j++) {
            if (row.memberItems[j].costItem.id == this.medicalEditTabsValue.content.recipelDetailEvtList[i].drugStuffId.costItem.id) {
              for (let m = 0; m < row.memberManagementDetails.length; m++) {
                if (row.memberManagementDetails[m].number > row.memberManagementDetails[m].useNumber && row.memberManagementDetails[m].memberItem.id == row.memberItems[j].id) {
                  let recipelDetailNum = this.medicalEditTabsValue.content.recipelDetailEvtList[i].total
                  let memberNum = row.memberManagementDetails[m].number - row.memberManagementDetails[m].useNumber
                  if (memberNum >= recipelDetailNum) {
                    row.memberManagementDetails[m].useNumber += recipelDetailNum
                    this.singleForm.amountReceived = (this.singleForm.amountReceived - this.medicalEditTabsValue.content.recipelDetailEvtList[i].allFee).toFixed(2)
                  } else {
                    row.memberManagementDetails[m].useNumber += memberNum
                    this.singleForm.amountReceived = (this.singleForm.amountReceived - (this.medicalEditTabsValue.content.recipelDetailEvtList[i].drugStuffId.costItem.salePrice * memberNum)).toFixed(2)
                  }
                }
              }
            }
          }
        }
        this.saveMember = row

      },
      clickMedicalEditTab() {
        if (this.medicalEditTabsValue === this.medicalClickTabsValue) {
          return;
        }
        this.medicalClickTabsValue = this.medicalEditTabsValue;
        console.log(this.medicalEditTabsValue);
        let num = 0
        for (let i = 0; i < this.medicalEditTabs.length; i++) {
          if (this.medicalEditTabs[i].key == this.medicalEditTabsValue.key) {
            num = i
          }
        }
        this.AlreadyPatientDescriptionsQuery(this.medicalClickTabsValue.content, num)
      },
      AlreadyPatientDescriptionsQuery(item, index) {
        console.log(item, "点击处方");
        this.medicalEditTabs.forEach((element) => {
          if (element.content.uuid == item.uuid) {
            this.medicalEditTabsValue = element;
            this.medicalClickTabsValue = this.medicalEditTabsValue;
          }
        });

        this.recipelInfoId = item.id;
        this.overColor = index
      },
      search() {
        this.patientCurrentPage = 1;
        this.isPayPatientCurrentPage = 1;
        this.returnPatientCurrentPage = 1;
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
        }
        this.medicalEditTabs = []
        this.medicalEditTabsValue = {}
        this.medicalClickTabsValue = {}
        this.payState = false
        if (this.chargeStatusActiveName == "1") {
          this.selectPatientList();
          this.allMember = []
        } else if (this.chargeStatusActiveName == "2") {
          this.allMember = []
          this.getIsPayPatientList();
        } else {
          this.allMember = []
          this.returnPayPatientList();
        }
      },
      print() {
        if (this.prescriptionMainList.length == 0) {
          this.$message.error("未选择数据");
          return;
        }
        let arr = []
        this.prescriptionMainList.forEach((item, index) => {
          let params = {
            recipelInfoId: item.recipelInfo.id,
            type: item.recipelInfo.recipelType.value,
          };
          let str = "";
          if (params.recipelInfoId) {
            str = "&recipelInfoId=" + params.recipelInfoId;
          }
          arr[index] = window.open(
            this.BASE_API +
            "/ureport/preview?_u=Newtouch:charge.ureport.xml&_t=0" +
            str,
            index
          );
          arr[index].onload = () => {
            arr[index].document.title = "新致云诊所"
          }
        });
      },
      pageInit() {
        this.selectPatientList();
        this.GetAllPatient();
        this.initOptions()
      },
      initOptions() {
        this.getOption("1014474470772899981")
        this.getOption("1014474470772899985")
        this.getOption("1014474470772900058")
        this.getOption("1014474470772899990")
      },
      getOption(optionId) {
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
          } else if (optionId == "1014474470772899985")
            this.ChineseTimeOption = responseData.data;
          else if (optionId == "1014474470772899990")
            this.FrequencyOption = responseData.data;
          else if (optionId == "1014474470772900058")
            this.ChineseUseTimeOption = responseData.data;
        });
      },
      handlePatientPage(pageNum) {
        this.patientCurrentPage = pageNum;
        this.selectPatientList();
      },
      handleIsPayPage(pageNum) {
        this.isPayPatientCurrentPage = pageNum;
        this.getIsPayPatientList();
      },
      handleReturnPage(pageNum) {
        this.returnPatientCurrentPage = pageNum;
        this.returnPayPatientList();
      },
      isPayChange(val) {
        console.log(val);
      },
      //单独收费
      singleCharge(type) {
        if (type == 1) {
          this.IsSingleCorrect();
          this.singleForm.member = ""
          console.log(this.medicalEditTabsValue, '其实看可能');
          console.log(this.medicalEditTabsValue.content.recipelDetailEvtList)
          //如果单独收费时为诊疗项目，判断其项目是否存在体验项目
          if (this.medicalEditTabsValue.type == "recipelType_3") {
            let recipelInfoEvt = {
              recipelInfo: this.medicalEditTabsValue.content.recipelInfo,
              recipelDetailEvtList: this.medicalEditTabsValue.content.recipelDetailEvtList
            }

            getMember(recipelInfoEvt).then(res => {
              if (res.code == "100") {
                //console.log(res,'看看奇观');
                this.member = res.data
                this.memberCopy = JSON.parse(JSON.stringify(res.data))
              } else {
                this.showMessage(res)
              }
            }).catch((error) => {
              this.outputError(error)
            })
          }
          this.singleDialogVisible = true
          this.chargeForm.amountReceived = 0
        } else {
          this.singleChargeBtn(2)
        }
      },
      // 添加收费人信息
      addressee() {
        this.addresseeDialogVisible = true
      },
      // 收件人信息保存
      async addresseeSave() {
        /* this.diagnosis.province = this.province;
         this.diagnosis.market = this.city;
         this.diagnosis.distinguish = this.area;*/
        console.log("this.area" + this.area)

        await modifiedState(this.diagnosis).then(res => {
          if (res.code === '100') {
            this.addresseeDialogVisible = false
            this.$message.success("收件人信息添加成功！")
            // 发起订单锁定
            // this.lockedOrder();
          } else {
            this.$message.error("后台数据异常请联系管理员！")
          }
        }).catch()

        await this.$axios.post('apis/patient/PatientOrderAddressQuery',{
            Data: {
              visitNo: this.hisPatient.mzh, // 门诊号
              OrganizeId: this.diagnosis.hospitalId,
              ks: "00000080" // 科室
            },
            OrganizeId: this.diagnosis.hospitalId,
            AppId: "Oh_Newtouch_Clinic",
            Timestamp: new Date()
        },
        {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            if(response.data.BusData.code === 10000){
              this.PatientAddressUpdate()
            }else if(response.data.BusData.code !== 10000) {
              // 创建订单并锁定
              this.createOrder();
              // this.PatientAddressAdd()
            }
            console.log("查询订单后返回信息"+JSON.stringify(response.data.BusData.data));
          })
          .catch(function (error) {
            console.log(error);
          });
      },
      /*收费退费按钮点击*/
      chargeClick(type) {
        if (this.diagnosisPrescription){
          this.chargeForm.amountReceived = this.hisTotalMoney.toFixed(2)
          this.chargeForm.amountReceivable = this.hisTotalMoney.toFixed(2)
        }
        if (this.prescriptionMainList.length == 0) {
          this.$message.error("未选择数据");
          return;
        }
        if (type == "1") {
          if (!this.isRetail) {
            let IsOpen = this.IsChargeCorrect("0");
            if (!IsOpen) {
              this.$message("当前选择处方不正确！");
              return;
            }
          } else {
            this.GetRetailRecipelDetailEvtList(1);
          }
          this.chargeDialogVisible = true;
          if (this.payType_List.length > 0) {
            this.chargeForm.paymentType = this.payType_List[0].value;
          }
          this.singleForm.amountReceived = 0
        } else {
          if (
            this.prescriptionMainList[0].recipelInfo.recipelType.name ==
            "零售处方"
          ) {
            let IsOpen = this.IsChargeCorrect("1");
            if (!IsOpen) this.$message("当前选择处方不正确！");
            else this.OutRetailSaveRetailPrescription();
          } else {
            let IsOpen = this.IsChargeCorrect("1");
            if (!IsOpen) this.$message("当前选择处方不正确！");
            else this.chargeBtn(2);
          }
        }
        if (this.diagnosisPrescription){
          this.chargeForm.amountReceived = this.hisTotalMoney.toFixed(2)
          this.chargeForm.amountReceivable = this.hisTotalMoney.toFixed(2)
        }
      },
      getPayTypeList() {
        //支付类型查询
        let payType_search = {
          params: [
            {
              columnName: "dict_type_id",
              queryType: "=",
              value: "1008793465990693120",
            },
          ],
        };
        // 字段对应表上filter条件
        payType_search.params.push.apply(payType_search.params, []);
        // 数据权限: 字典项sys_dict_item
        this.pushDataPermissions(
          payType_search.params,
          this.$route.meta.routerId,
          "4005"
        );
        this.payType_List.splice(0, this.payType_List.length);
        listDictItemAll(payType_search).then((responseData) => {
          this.payType_List = responseData.data;
        });
      },
      getPrescriptionTotalCost(data) {
        var totalCost = 0;
        data.forEach((element) => {
          if (element.totalCost) {
            totalCost += element.totalCost;
          }
        });
        return totalCost;
      },
      /*计算当前退费处方合计*/
      getRefundTotalCost() {
        var totalCost = 0;
        this.refundPrescriptionMainList.forEach((element) => {
          if (element.totalCost) {
            totalCost += element.totalCost;
          }
        });
        return totalCost;
      },
      /*切换到零售*/
      changeToRetail() {
        //清空病人
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
        };
        this.RetailTable = [];
        this.isRetail = true;
        this.$nextTick(() => {
          this.$refs.patientInfoForm.resetFields();
        });
      },
      /*病人列表点击*/
      selectPatientChange() {
        if (!this.patientInfoRow) {
          return;
        }
        this.isRetail = false;
        this.$nextTick(() => {
          this.$refs["patientInfoForm"].clearValidate();
        });
        if (this.chargeStatusActiveName == "1") this.payState = true;
        this.prescriptionMainList = [];
        console.log(this.patientInfoRow, 'ajglka');
        //根据登记信息获取病例信息
        // let medicalSearch={
        //   params: [
        //      {
        //       columnName: "company_id",
        //       queryType: "=",
        //       value: currentUser.company.id,
        //     },
        //     {
        //       columnName: "registration_id",
        //       queryType: "=",
        //       value: this.patientInfoRow.id,
        //     }
        //   ],
        //   offset: 0,
        //   limit: 10,
        //   columnName: "", // 排序字段名
        //   order: "", // 排序
        // }
        // listMedicalRecordPage(medicalSearch).then((res)=>{
        //   if(res.code=="100"){
        //     console.log(res,'woailuo');
        //     if(res.data.rows[0].westernDiagnose!=undefined){
        //         this.patientInfoForm.westernDiagnose=res.data.rows[0].westernDiagnose
        //       }else{
        //         this.patientInfoForm.westernDiagnose="无"
        //       }
        //       console.log(res.data.rows[0].chinaDiagnose);
        //       if(res.data.rows[0].chinaDiagnose!=undefined){
        //         this.patientInfoForm.chinaDiagnose=res.data.rows[0].chinaDiagnose
        //       }else{
        //         this.patientInfoForm.chinaDiagnose="无"
        //       }
        //   }
        // }).catch(()=>{})


        //根据患者id获取会员信息
        getByPatientId(this.patientInfoRow.patientId.id).then(res => {
          if (res.code === '100') {
            this.allMember = res.data
          } else {
            this.$message.error("后台数据异常请联系管理！")
          }
        }).catch()

        this.selRowChargeStatus = this.patientInfoRow.chargeStatus;

        this.getSaveInfo(this.patientInfoRow.id);

        // 获取患者远程诊疗信息
        this.diagnosis = null
        console.log("akakkakakakakkakkkakakak"+this.patientInfoRow.id)
        getRegistrationId(this.patientInfoRow.id).then(res => {
          if (res.data.id !== null) {
            // this.diagnosis = res.data;
            // this.diagnosisPrescription = true;
            this.inquireHis();
            setTimeout(() => {
              this.pendingBill();
              }, 1000);
            // this.billingDetail(this.prescriptionCollection);

          } else {
            this.$message.error("后台数据异常请联系管理！")
          }
        }).catch()

        this.overColor = 0

      },
      //获取就诊患者的个人信息
      getPatientById(id) {
        return this.AllPatientOption.find((item) => item.id == id);
      },
      /*查询病人*/
      selectPatientList() {
        var now = new Date();
        this.patientQueryCondition.limit = this.patientPageSize;
        this.patientQueryCondition.companyId = this.Company.id;
        /*待收费*/
        this.isOnlyRead = false;
        this.patientQueryCondition.offset =
          (this.patientCurrentPage - 1) * this.patientPageSize;
        this.patientList = [];
        this.patientQueryCondition.columnName = "reception_end_date";
        this.patientQueryCondition.order = "desc";
        this.patientQueryCondition.chargeStatus = 0;
        this.patientQueryCondition.updateDate = "2022-06-01 00:00:00";
        this.patientQueryCondition.status = "registrationStatus_1";
        if (this.selectCondition.trim()) {
          this.patientQueryCondition.patientName = this.selectCondition
          this.patientQueryCondition.patientCode = this.selectCondition
        } else {
          this.patientQueryCondition.patientName = ''
          this.patientQueryCondition.patientCode = ''
        }
        listRegistrationPages(this.patientQueryCondition)
          .then((responseData) => {
            if (responseData.code == 100) {
              if (responseData.data.rows != null) {
                this.patientList = responseData.data.rows.filter(
                  (item) => item.recipeStatus != "1"
                );
                let num = responseData.data.rows.length - this.patientList.length;
                if (this.chargeStatusActiveName == "1") {
                  this.patientTotalCount = responseData.data.total - num;
                } else {
                  this.isPayTotalCount = responseData.data.total - num;
                }
              } else {
                this.patientList = responseData.data.rows
                if (this.chargeStatusActiveName == "1") {
                  this.patientTotalCount = 0;
                } else {
                  this.isPayTotalCount = 0;
                }
              }

            } else {
              this.$message.error(responseData);
            }
          })
          .catch((error) => {
            console.log(error);
            this.$message.error(error);
          });
      },
      //查询已收费列表
      getIsPayPatientList() {
        var now = new Date();
        this.patientQueryCondition.limit = this.patientPageSize;
        this.patientQueryCondition.companyId = this.Company.id;
        this.isOnlyRead = true;
        this.patientQueryCondition.offset =
          (this.isPayPatientCurrentPage - 1) * this.patientPageSize;
        this.isPayPatientList = [];
        this.patientQueryCondition.columnName = "charge_date";
        this.patientQueryCondition.order = "desc";
        this.patientQueryCondition.chargeStatus = 1;
        this.patientQueryCondition.updateDate = "2022-06-01 00:00:00";
        this.patientQueryCondition.status = "registrationStatus_1";
        if (this.selectCondition.trim()) {
          this.patientQueryCondition.patientName = this.selectCondition
          this.patientQueryCondition.patientCode = this.selectCondition
        } else {
          this.patientQueryCondition.patientName = ''
          this.patientQueryCondition.patientCode = ''
        }
        listRegistrationPages(this.patientQueryCondition)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.isPayTotalCount = responseData.data.total;
              this.isPayPatientList = responseData.data.rows;
            } else {
              this.$message.error(responseData);
            }
          })
          .catch((error) => {
            console.log(error);
            this.$message.error(error);
          });
      },

      //查询已退费列表
      returnPayPatientList() {
        var now = new Date();
        this.patientQueryCondition.limit = this.patientPageSize;
        this.patientQueryCondition.companyId = this.Company.id;
        this.patientQueryCondition.offset =
          (this.returnPatientCurrentPage - 1) * this.patientPageSize;
        this.returnPatientList = [];
        this.patientQueryCondition.columnName = "retreats_date";
        this.patientQueryCondition.order = "desc";
        this.patientQueryCondition.chargeStatus = -1;
        this.patientQueryCondition.updateDate = "2022-06-01 00:00:00";
        this.patientQueryCondition.status = "registrationStatus_1";

        if (this.selectCondition.trim()) {
          this.patientQueryCondition.patientName = this.selectCondition
          this.patientQueryCondition.patientCode = this.selectCondition
        } else {
          this.patientQueryCondition.patientName = ''
          this.patientQueryCondition.patientCode = ''
        }

        listRegistrationPages(this.patientQueryCondition)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.returnTotalCount = responseData.data.total;
              this.returnPatientList = responseData.data.rows;
            } else {
              this.$message.error(responseData);
            }
          })
          .catch((error) => {
            console.log(error);
            this.$message.error(error);
          });
      },

      //根据就诊人id查询处方病例
      getSaveInfo(visitId) {
        this.VisitRecordsList = [];
        allQueryMedicalRecord(visitId).then((responseData) => {
          if (responseData.code == 100) {
            if (this.chargeStatusActiveName != "3") {
              this.prescriptionMainList =
                responseData.data.recipelInfoEvtList.filter(
                  (item) => item.recipelInfo.status != -1
                );
            } else {
              this.prescriptionMainList = responseData.data.recipelInfoEvtList;
            }
            if (this.chargeStatusActiveName == "1") {
              this.prescriptionMainList = this.prescriptionMainList.filter(
                (item) => item.recipelInfo.chargeStatus == 0
              );
            } else if (this.chargeStatusActiveName == "2") {
              this.prescriptionMainList = this.prescriptionMainList.filter(
                (item) => item.recipelInfo.chargeStatus == 1
              );
            } else {
              this.prescriptionMainList = this.prescriptionMainList.filter(
                (item) => item.recipelInfo.chargeStatus == -1
              );
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
            this.VisitId = this.patientInfoForm.id;
            this.medicalRecord = responseData.data.medicalRecord;
            this.totalMoney = 0;
            let money = 0;
            this.prescriptionMainList.forEach((item) => {
              console.log(item.recipelInfo.fee, "qian");
              money += item.recipelInfo.fee;
              console.log("处方金额 = " +money)
            });
            this.totalMoney = money.toFixed(2);
            this.createMedicalEditTab(this.prescriptionMainList);
            console.log(this.prescriptionMainList, "zheme");
            console.log(this.diagnosis, "少时诵诗书");
            console.log(this.totalMoney, "少时诵是诗书");

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
          if (element.recipelInfo.recipelType.value == "recipelType_2") {
            let infusion = {
              defaultNumber: 1,//默认组号
              infusionProject: [[]],
              drippingSpeed: [""],  //滴速
              days: [{}],    //天数
              frequency: [{}],   //频次
              infuseUse: [{}],     //用法
              zushu: [1],
            }
            let count = 0
            for (let i = 0; i < element.recipelDetailEvtList.length; i++) {
              if (count < element.recipelDetailEvtList[i].infuseGroup) {
                count = element.recipelDetailEvtList[i].infuseGroup
              }
            }

            for (let i = 1; i < count; i++) {
              infusion.defaultNumber = infusion.defaultNumber + 1;
              infusion.zushu.push(infusion.defaultNumber)
              infusion.infusionProject.push([])
              infusion.drippingSpeed.push("")
              infusion.days.push({})
              infusion.frequency.push({})
              infusion.infuseUse.push({})
            }

            let arr = []
            for (let i = 0; i < element.recipelDetailEvtList.length; i++) {


              if (element.recipelDetailEvtList[i].isExtra != 1) {

                infusion.infusionProject[element.recipelDetailEvtList[i].infuseGroup - 1].push(element.recipelDetailEvtList[i])
                infusion.drippingSpeed[element.recipelDetailEvtList[i].infuseGroup - 1] = element.recipelDetailEvtList[i].drippingSpeed
                infusion.days[element.recipelDetailEvtList[i].infuseGroup - 1] = element.recipelDetailEvtList[i].days
                infusion.frequency[element.recipelDetailEvtList[i].infuseGroup - 1] = element.recipelDetailEvtList[i].frequency
                infusion.infuseUse[element.recipelDetailEvtList[i].infuseGroup - 1] = element.recipelDetailEvtList[i].infuseUse
              } else {


                arr.push(element.recipelDetailEvtList[i])
              }

            }
            let tab = {
              title: element.recipelInfo.name,
              key: element.recipelInfo.seq,
              content: element,
              closable: false,
              type: element.recipelInfo.recipelType.value,
              infusion: infusion
            };
            this.medicalEditTabs.push(tab);
          } else {
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
      //获取所有患者详情
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
            console.log(responseData.data, "kankan");
            // responseData.data.filter(item=>)
            this.AllPatientOption = responseData.data;
          }
        });
      },
      clinicDateFormatter(row, column) {
        var date = new Date(row[column.property]);
        return (
          this.padleftZero(date.getMonth() + 1) +
          "/" +
          this.padleftZero(date.getDate()) +
          " " +
          this.padleftZero(date.getHours()) +
          ":" +
          this.padleftZero(date.getMinutes())
        );
      },
      padleftZero(str) {
        str = "0000000000" + str;
        return str.substr(str.length - 2);
      },
      GetDrugTable() {
        let SearchModel = {
          columnName: "",
          limit: 10,
          offset: 0,
          order: "",
          params: [
            {
              columnName: "company_id",
              queryType: "=",
              value: this.Company.id,
            },
            {
              columnName: "goods_name",
              queryType: "like",
              value: this.SearchDrugName,
            },
          ],
        };

        var pattern2 = new RegExp("[A-Za-z]+");
        if (pattern2.test(this.SearchDrugName)) {
          // console.log(this.SearchWesternInput,'字符');
          SearchModel.params[1].value = this.SearchDrugName.toUpperCase();
          SearchModel.params[1].columnName = "pinyin_code";
        } else {
          SearchModel.params[1].value = this.SearchDrugName;
          SearchModel.params[1].columnName = "goods_name";
        }
        listAllStock(SearchModel).then((responseData) => {
          if (responseData.code == 100) {
            responseData.data.forEach((element) => {
              let isUnpackSell = element.isUnpackSell; //允许拆零销售
              let stockNumber = element.stockNumber; //库存数量
              let packName = element.pack.name; //包装（单位）
              let preparation = element.preparation; //制剂
              let preparationUnit = element.preparationUnit.name; //制剂单位
              let price = element.price; //售价
              let retailPrice = element.retailPrice; //拆开后零售价
              let type = element.type.value; //药品类型： medicalType_1 - 中药   medicalType_0 - 西药

              let stockText = "";
              let selectDrugTypeItems = [];
              if (type === "medicalType_1") {
                stockText = stockNumber + preparationUnit;
                selectDrugTypeItems.push({
                  value: element.preparationUnit.value,
                  name: element.preparationUnit.name,
                  isUnpackSell: 1,
                });
              } else if (isUnpackSell === "1" && type === "medicalType_0") {
                selectDrugTypeItems.push({
                  value: element.preparationUnit.value,
                  name: element.preparationUnit.name,
                  isUnpackSell: 1,
                });
                selectDrugTypeItems.push({
                  value: element.pack.value,
                  name: element.pack.name,
                  isUnpackSell: 0,
                });
                //向下取整
                let p1 = Math.floor(stockNumber / preparation);
                //取余
                let p2 = stockNumber % preparation;
                if (p1 > 0) {
                  stockText = p1 + packName;
                }
                if (p2 > 0) {
                  if (p1 > 0) {
                    stockText = stockText + p2 + preparationUnit;
                  } else {
                    stockText = p2 + preparationUnit;
                  }
                }
              } else {
                stockText = stockNumber + packName;
                selectDrugTypeItems.push({
                  value: element.pack.value,
                  name: element.pack.name,
                  isUnpackSell: 0,
                });
              }

              element["stockText"] = stockText;
              element["selectDrugTypeItems"] = selectDrugTypeItems;
              element["selectDrugTypeItem"] = selectDrugTypeItems[0];
            });

            this.DrugAllTable = responseData.data;
          }
        });
      },
      RowClickDrugTable(row) {
        let flag = false;
        this.RetailTable.forEach((element) => {
          if (element.id === row.id) {
            flag = true;
          }
        });

        if (!flag) {
          this.RetailTable.push({
            allFee: 0,
            ...JSON.parse(JSON.stringify(row)),
          });
          console.log(this.RetailTable);
        }
      },

      DeleteDrugItem(index) {
        this.RetailTable.splice(index, 1);
      },

      chengeDrugTypeItems(sel) {
        console.log(sel);
      },
      //根据是否零售与新增还是查看获取单位
      GetDrugUnit(index, row) {
        if (row.preparationUnit) {
          if (row.isUnpackSell == "1") {
            return row.preparationUnit.name;
          } else if (row.type.name == "中药") {
            return row.dosisUnit.name;
          } else {
            return row.pack.name;
          }
        } else {
          if (
            row.drugStuffId.drug.isUnpackSell == "1" ||
            row.drugStuffId.drug.type.name == "中药"
          ) {
            return row.drugStuffId.drug.preparationUnit.name;
          } else {
            return row.drugStuffId.drug.pack.name;
          }
        }
      },

      GetAllDrugFee(index, row) {
        if (row.isUnpackSell == "1" && row.type.name != "中药") {
          row.allFee = row.total * row.retailPrice;
        } else {
          row.allFee = row.total * row.price;
        }
      },

      GetDrugFee(index, row) {
        if (row.isUnpackSell === "1" || row.type.value === "medicalType_1") {
          return row.retailPrice + "/" + row.preparationUnit.name;
        } else {
          return row.price + "/" + row.pack.name;
        }
      },

      GetStatusColor(status) {
        return {
          redStateStyle: status == "2",
          greenStateStyle: status == "1",
        };
      },
      GetPrescriptionStatus(status) {
        let returnStatus;
        if (status == "0") returnStatus = "待收费";
        else if (status == "1") returnStatus = "已收费";
        else if (status == "-1") returnStatus = "已退费";
        return returnStatus;
      },

      //计算实际收费
      GetAmountReceived() {
        this.chargeForm.amountReceived = null;
        let discount = this.chargeForm.discount;
        if (this.chargeForm.discount == 0) {
          discount = 10;
        }

        console.log("所有处方应收费用" + this.medicalEditTabsValue.content.recipelDetailEvtList.length)
        for (let i = 0; i < this.medicalEditTabsValue.content.recipelDetailEvtList.length; i++) {
          console.log("i == " + Number((this.medicalEditTabsValue.content.recipelDetailEvtList[i].allFee * discount * 0.1).toFixed(2)))
          this.chargeForm.amountReceived += Number((this.medicalEditTabsValue.content.recipelDetailEvtList[i].allFee * discount * 0.1).toFixed(2))
        }
        this.signleAmountOfRemission = (this.chargeForm.amountReceivable - this.chargeForm.amountReceived).toFixed(2)
        this.chargeForm.amountReceived = this.chargeForm.amountReceived.toFixed(2)


        this.amountOfRemission = (this.chargeForm.amountReceivable - this.chargeForm.amountReceived).toFixed(2)

        if (this.amountPaid > 0) {
          this.inputAmountPaid()
        }
      },
      GetSingleAmountReceived() {
        this.singleForm.amountReceived = null;
        let discount = this.singleForm.discount;
        if (this.singleForm.discount == 0) {
          discount = 10;
        }
        console.log("单处方应收费用" + this.medicalEditTabsValue.content.recipelDetailEvtList.length)
        for (let i = 0; i < this.medicalEditTabsValue.content.recipelDetailEvtList.length; i++) {
          console.log("i == " + Number((this.medicalEditTabsValue.content.recipelDetailEvtList[i].allFee * discount * 0.1).toFixed(2)))
          this.singleForm.amountReceived += Number((this.medicalEditTabsValue.content.recipelDetailEvtList[i].allFee * discount * 0.1).toFixed(2))
        }
        this.signleAmountOfRemission = (this.singleForm.amountReceivable - this.singleForm.amountReceived).toFixed(2)
        this.singleForm.amountReceived = this.singleForm.amountReceived.toFixed(2)
        if (this.amountPaid > 0) {
          this.inputAmountPaid()
        }
      },
      IsSingleCorrect() {
        let allFee = this.medicalEditTabsValue.content.recipelInfo.fee
        this.singleForm.amountReceivable = allFee.toFixed(2);
        this.singleForm.amountReceived = allFee.toFixed(2);
      },
      //收费数据是否合格
      IsChargeCorrect(type) {
        let IsSure = true;
        let allFee = 0;
        this.prescriptionMainList.map((item) => {
          if (
            item.recipelInfo.recipelType.name == "西药处方" &&
            this.WesternCheck
          ) {
            if (item.recipelInfo.isPay != type) {
              IsSure = false;
            } else {
              allFee += item.recipelInfo.fee;
            }
          } else if (
            item.recipelInfo.recipelType.name == "中药处方" &&
            this.ChineseCheck
          ) {
            if (item.recipelInfo.isPay != type) {
              IsSure = false;
            } else {
              allFee += item.recipelInfo.fee;
            }
          } else if (
            item.recipelInfo.recipelType.name == "输液处方" &&
            this.InfusionCheck
          ) {
            if (item.recipelInfo.isPay != type) {
              IsSure = false;
            } else {
              allFee += item.recipelInfo.fee;
            }
          } else if (
            item.recipelInfo.recipelType.name == "诊疗项目" &&
            this.CostItemCheck
          ) {
            if (item.recipelInfo.isPay != type) {
              IsSure = false;
            } else {
              allFee += item.recipelInfo.fee;
            }
          } else if (
            item.recipelInfo.recipelType.name == "附加费" &&
            this.SurchargeCheck
          ) {
            if (item.recipelInfo.isPay != type) {
              IsSure = false;
            } else {
              allFee += item.recipelInfo.fee;
            }
          } else if (item.recipelInfo.recipelType.name == "零售处方") {
            if (type == 0) {
              IsSure = false;
            } else if (
              type == 1 &&
              this.prescriptionMainList[0].recipelInfo.isPay == "2"
            ) {
              IsSure = false;
            }
          }
        });
        this.chargeForm.amountReceivable = allFee.toFixed(2);
        this.chargeForm.amountReceived = allFee.toFixed(2);
        return IsSure;
      },
      getChargeData() {

      },
      GetRetailRecipelDetailEvtList(type) {
        let recipelDetailEvtList = [];
        let allFee = 0;
        if (this.patientInfoForm.gender.value == "gender_0") {
          this.patientInfoForm.gender.name = "男";
        } else {
          this.patientInfoForm.gender.name = "女";
        }
        this.RetailTable.map((item) => {
          allFee += item.allFee;
          let retailType = "0";
          switch (item.type.value) {
            case "medicalType_0":
              retailType = "0";
              break;
            case "medicalType_1":
              retailType = "1";
              break;
            default:
              break;
          }
          recipelDetailEvtList.push({
            company: this.Company,

            drugStuffId: {
              drugStuffId: item.id,
            },
            total: item.total,
            allFee: item.allFee,
            retailType: retailType,
          });
        });
        if (type == 1) {
          this.chargeForm.amountReceivable = allFee.toFixed(2);
          this.chargeForm.amountReceived = allFee.toFixed(2);
        }
        return recipelDetailEvtList;
      },

      //确定收费-退费按钮(type: 1收费 2退费)
      chargeBtn(type) {
        if (!this.isRetail) {
          this.chargeSave(type);
        } else {
          this.SaveRetailPrescription(type);
        }
      },

      //零售退费
      OutRetailSaveRetailPrescription() {
        let tollInfo = {
          company: {
            id: this.Company.id,
          },
          medical: this.medicalRecord,
          amountReceivable: this.prescriptionMainList[0].recipelInfo.fee,
          amountReceived: this.prescriptionMainList[0].recipelInfo.fee,
          amountDiscounted: 0,
          discount: this.chargeForm.discount * 0.1,
          paymentType: {
            name: "现金",
            value: "payType_0",
          },
          amountStatus: {
            name: "已退费",
            value: "amountStatus_2",
          },
          tollType: {
            name: "零售",
            value: "tollType_5",
          },
          patient: {
            id: this.patientInfoForm.id,
          },
        };
        let recipelInfos = [
          {
            recipelInfo: {
              id: this.prescriptionMainList[0].recipelInfo.id,
            },
          },
        ];
        let model = {
          tollInfo: tollInfo,
          recipelInfos: recipelInfos,
          chargeStatus: "4",
        };
        this.saveTollTollInfo(model);
      },

      //零售收费
      SaveRetailPrescription() {
        let recipelDetailEvtList = this.GetRetailRecipelDetailEvtList(2);
        let paymentType = {
          name: "",
          value: this.chargeForm.paymentType,
        };
        switch (this.chargeForm.paymentType) {
          case "payType_0":
            paymentType.name = "现金";
            break;
          case "payType_1":
            paymentType.name = "支付宝";
            break;
          case "payType_2":
            paymentType.name = "微信";
            break;
          case "payType_3":
            paymentType.name = "银行卡";
            break;
          case "payType_4":
            paymentType.name = "医保";
            break;
          default:
            paymentType = {
              name: "现金",
              value: "payType_0",
            };
            break;
        }
        let tollInfo = {
          company: {
            id: this.Company.id,
          },
          amountReceivable: this.chargeForm.amountReceivable,
          amountReceived: this.chargeForm.amountReceived,
          amountDiscounted: this.chargeForm.amountDiscounted,
          discount: this.chargeForm.discount * 0.1,
          paymentType: paymentType,
          amountStatus: {
            name: "已收费",
            value: "amountStatus_1",
          },
          tollType: {
            name: "零售",
            value: "tollType_5",
          },
          patient: {
            ...this.patientInfoForm,
            company: {...this.Company},
            id: "",
          },
        };
        let recipelInfo = {
          company: this.Company,
          recipelType: {
            name: "零售处方",
            value: "recipelType_5",
          },
          fee: this.chargeForm.amountReceivable,
        };
        let model = {
          tollInfo: tollInfo,
          recipelInfos: [
            {
              recipelInfo: recipelInfo,
              recipelDetailEvtList: recipelDetailEvtList,
            },
          ],
          chargeStatus: "2",
        };
        console.log(model);
        this.saveTollTollInfo(model);
      },
      submit() {
        this.$refs["chargeForm"].validate((valid) => {
          if (valid) {
            this.chargeBtn(1);
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      singleChargeBtn(type) {
        let paymentType = {
          name: "",
          value: this.singleForm.paymentType,
        };
        console.log(this.singleForm.paymentType + "收费收费收费收费收费收费收费收费")
        switch (this.singleForm.paymentType) {
          case "payType_0":
            paymentType.name = "现金";
            break;
          case "payType_1":
            paymentType.name = "支付宝";
            break;
          case "payType_2":
            paymentType.name = "微信";
            break;
          case "payType_3":
            paymentType.name = "银行卡";
            break;
          case "payType_4":
            paymentType.name = "医保";
            break;
          default:
            paymentType = {
              name: "现金",
              value: "payType_0",
            };
            break;
        }
        let tollInfo = {
          company: {
            id: this.Company.id,
          },
          medical: this.medicalRecord,
          amountReceivable: this.singleForm.amountReceivable,
          amountReceived: this.singleForm.amountReceived,
          amountDiscounted: this.singleForm.amountDiscounted,
          discount: this.singleForm.discount * 0.1,
          paymentType: paymentType,
          patient: {
            id: this.patientInfoForm.id,
          },
          tollType: {
            name: "",
            value: "",
          },
          amountStatus: {},
        };
        let recipelInfos = [];
        let chargeStatus = 0
        recipelInfos.push({
          recipelInfo: {
            id: this.medicalEditTabsValue.content.recipelInfo.id,
          },
        });
        if (type == 1) {
          tollInfo.amountStatus = {
            name: "已收费",
            value: "amountStatus_1",
          };
          chargeStatus = "2"
        } else {
          tollInfo.amountStatus = {
            name: "已退费",
            value: "amountStatus_2",
          };
          chargeStatus = "4";
        }

        let model = {
          tollInfo: tollInfo,
          recipelInfos: recipelInfos,
          chargeStatus: chargeStatus,
          memberManagement: this.saveMember
        };

        // console.log(this.medicalEditTabsValue.content,'封杀iOS发哈夫');
        //return
        this.saveTollTollInfo(model);
      },
      //确定收费-退费按钮(type: 1收费 2退费)
      chargeSave(type) {
        let paymentType = {
          name: "",
          value: this.chargeForm.paymentType,
        };
        switch (this.chargeForm.paymentType) {
          case "payType_0":
            paymentType.name = "现金";
            break;
          case "payType_1":
            paymentType.name = "支付宝";
            break;
          case "payType_2":
            paymentType.name = "微信";
            break;
          case "payType_3":
            paymentType.name = "银行卡";
            break;
          case "payType_4":
            paymentType.name = "医保";
            break;
          default:
            paymentType = {
              name: "现金",
              value: "payType_0",
            };
            break;
        }
        let tollInfo = {
          company: {
            id: this.Company.id,
          },
          medical: this.medicalRecord,
          amountReceivable: this.chargeForm.amountReceivable,
          amountReceived: this.chargeForm.amountReceived,
          amountDiscounted: this.chargeForm.amountDiscounted,
          discount: this.chargeForm.discount * 0.1,
          paymentType: paymentType,
          patient: {
            id: this.patientInfoForm.id,
          },
          tollType: {
            name: "",
            value: "",
          },
          amountStatus: {},
        };
        let recipelInfos = [];
        let IsAll = 0;

        this.prescriptionMainList.map((item) => {
          switch (item.recipelInfo.recipelType.name) {
            case "中药处方":
              if (type == 1 && item.recipelInfo.isPay == 1) IsAll += 1;
              else if (type == 2 && item.recipelInfo.isPay == 2) IsAll += 1;
              if (this.ChineseCheck) {
                IsAll += 1;
                recipelInfos.push({
                  recipelInfo: {
                    id: item.recipelInfo.id,
                  },
                });
              }
              break;
            case "西药处方":
              if (type == 1 && item.recipelInfo.isPay == 1) IsAll += 1;
              else if (type == 2 && item.recipelInfo.isPay == 2) IsAll += 1;
              if (this.WesternCheck) {
                IsAll += 1;
                recipelInfos.push({
                  recipelInfo: {
                    id: item.recipelInfo.id,
                  },
                });
              }
              break;
            case "输液处方":
              if (type == 1 && item.recipelInfo.isPay == 1) IsAll += 1;
              else if (type == 2 && item.recipelInfo.isPay == 2) IsAll += 1;
              if (this.InfusionCheck) {
                IsAll += 1;
                recipelInfos.push({
                  recipelInfo: {
                    id: item.recipelInfo.id,
                  },
                });
              }
              break;
            case "诊疗项目":
              if (type == 1 && item.recipelInfo.isPay == 1) IsAll += 1;
              else if (type == 2 && item.recipelInfo.isPay == 2) IsAll += 1;
              if (this.CostItemCheck) {
                IsAll += 1;
                recipelInfos.push({
                  recipelInfo: {
                    id: item.recipelInfo.id,
                  },
                });
              }
              break;
            case "附加费":
              if (type == 1 && item.recipelInfo.isPay == 1) IsAll += 1;
              else if (type == 2 && item.recipelInfo.isPay == 2) IsAll += 1;
              if (this.SurchargeCheck) {
                IsAll += 1;
                recipelInfos.push({
                  recipelInfo: {
                    id: item.recipelInfo.id,
                  },
                });
              }
              break;
            default:
              break;
          }
        });

        let chargeStatus = "0";
        if (IsAll == this.prescriptionMainList.length) {
          if (type == 1) {
            tollInfo.amountStatus = {
              name: "已收费",
              value: "amountStatus_1",
            };
            chargeStatus = "2";
          } else {
            tollInfo.amountStatus = {
              name: "已退费",
              value: "amountStatus_2",
            };
            chargeStatus = "4";
          }
        } else {
          if (type == 1) {
            tollInfo.amountStatus = {
              name: "已收费",
              value: "amountStatus_1",
            };
            chargeStatus = "1";
          } else {
            tollInfo.amountStatus = {
              name: "已退费",
              value: "amountStatus_2",
            };
            chargeStatus = "3";
          }
        }
        let state = false
        if (type == 2) {
          this.prescriptionMainList.forEach(item => {
            if (item.recipelInfo.dispensionStatus == 1) {
              state = true
            }
          })

        }

        if (state) {
          this.$message.error('有处方已发药但未退药，请先退药')
          return
        }
        let model = {
          tollInfo: tollInfo,
          recipelInfos: recipelInfos,
          chargeStatus: chargeStatus,
        };
        this.saveTollTollInfo(model);
      },
      //收费
      saveTollTollInfo(model) {
        let type = "null";
        console.log(model, '保存会员体验卡');
        if (this.diagnosis !== null){
          /*modifiedState(this.diagnosis).then(res => {
            if (res.code === '100') {
              this.$message.success("收费成功！")
            } else {
              this.$message.error("后台数据异常请联系管理员！")
            }
          }).catch()*/

          chargeState(model).then(res => {
            if (res.code === '100') {
              this.$message.success("收费成功！")
              if (this.diagnosis != null){
                // 查询远程诊疗订单
                this.orderQuery();
                setTimeout(() => {
                  // 结算远程诊疗订单
                  this.orderSettlement();
                }, 1000);
                // 修改状态
                this.diagnosis.diagnosisTime = null;
                this.diagnosis.medicationStatus = "1"
                this.diagnosis.cardNo = this.hisPatient.kh // 就诊卡号
                this.diagnosis.orderNo = this.orderForm.OrderNo // 订单号
                this.diagnosis.payFee = this.chargeForm.amountReceivable // 支付费用
                modifiedState(this.diagnosis).then(responseData => {
                  if (responseData.code == 100) {
                    this.reset()
                  } else {
                    this.reset()
                    this.showMessage(responseData)
                  }
                  this.resetLoad()
                }).catch(error => {
                  this.outputError(error)
                })
              }
            } else {
              this.$message.error("后台数据异常请联系管理员！")
            }
          }).catch()
          this.chargeDialogVisible = false;
          this.singleDialogVisible = false;
        }else {
          // return
          saveTollTollInfo(model, type)
            .then((responseData) => {
              if (responseData.code == 100) {
                this.$message.success("操作成功！");
                this.selectPatientList();
                console.log('在哈克发撒看风景');
                this.search();
                this.payState = false;
                this.chargeDialogVisible = false;
                this.singleDialogVisible = false;
                this.chargeForm = {
                  amountReceivable: 0,
                  amountDiscounted: 0,
                  discount: 10,
                  amountReceived: 0,
                  paymentType: "payType_0",
                  meno: "",
                }
                this.singleForm = {
                  amountReceivable: 0,
                  amountDiscounted: 0,
                  discount: 10,
                  amountReceived: 0,
                  paymentType: "payType_0",
                  meno: "",
                }
              } else if (responseData.code == '50001') {
                this.$message.warning(responseData.msg)
              } else {
                this.$message.error(responseData.msg);
                this.showMessage(responseData.msg);
              }
            })
            .catch((error) => {
              this.outputError(error);
            });
        }
      },
    },
    mounted() {
      this.pageInit();
      this.GetDrugTable();
      this.getPayTypeList();
    },
  };
</script>


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

  .medical-type-ul {
    list-style-type: none;
    font-size: 13px;
    color: #303133;
    margin: 5px;
    padding: 0;
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

  .refundBox {
    position: absolute;
    top: -23px;
    right: -80px;
    width: 80px;
    height: 38px;
    border: 2px solid #e6a23c;
    text-align: center;
    font-size: 18px;
    border-radius: 80%;
    color: #e6a23c;
    transform: scale(0.5);
  }
</style>
<style scoped>
  .single {
    padding: 8px 0 0;
    font-size: 14px;
    font-weight: 700;
    color: midnightblue;
    line-height: 28px;
  }

  .el-popover.medical-type-popover {
    min-width: 85px !important;
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

  .flex-space-between {
    display: flex;
    justify-content: space-between;
  }

  .flex-start {
    display: flex;
    justify-content: flex-start;
  }

  .prescriptionCard {
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

  .refundBox {
    position: absolute;
    top: -23px;
    right: -80px;
    width: 80px;
    height: 38px;
    border: 2px solid #e6a23c;
    text-align: center;
    font-size: 18px;
    border-radius: 80%;
    color: #e6a23c;
    transform: scale(0.5);
  }

  /deep/ .el-dialog__body {
    padding: 0 7px;
    margin: 0
  }

  /deep/ .el-form-item__label {
    text-align: left;

  }

  /deep/ .el-dialog .el-row {
    padding: 0 10px;
  }

  .clickTest {
    background-color: rgb(234, 242, 251) !important;
    color: #fff;
  }

  .imgStyle {

    /* display: inline-flex; */
    position: absolute;
    top: 0;
    right: -30px;
  }

  .nameStyle {
    position: relative;
    display: inline-block;
    color: forestgreen;
  }
</style>
