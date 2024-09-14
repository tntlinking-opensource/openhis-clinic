<template>
  <el-row v-loading="loading" class="outpatient">
    <div class="leftInfo">
      <div class="leftTitle">今日就诊</div>
      <div class="searchItemBox">
        <el-select
          v-model="SearchPatientId"
          value-key="id"
          filterable
          clearable
          style="width: calc(100% - 50px)"
          placeholder="请选择患者"
        >
          <el-option
            v-for="patientId in AllPatientOption"
            :key="patientId.id"
            :label="patientId.name"
            :value="patientId.id"
          ></el-option>
        </el-select>
        <span style="width: 40px" @click="GetVisitList">搜索</span>
      </div>
      <el-tabs v-model="TodayActiveName">
        <el-tab-pane
          :label="'待就诊(' + PreparePatientTotal + ')'"
          name="prepare"
        >
          <div class="todayVisitBox">
            <div
              class="todayPatientItem"
              :class="{ 'click': index == NowPreIndex }"
              @click="GoVisit(item, index)"
              v-for="(item, index) in PreparePatientList"
              :key="index"
            >
              <div class="patientInfo">
                <p>
                  {{ SearchPreModel.offset * 5 + index + 1 }}.{{
                    item.patientId.name
                  }}
                  / {{ item.patientInfo.gender.name }} /
                  {{ item.patientInfo.age }}岁
                </p>
                <p>
                  {{ item.company.name }} / <span :class="{ 'isNet': item.source.name == '网上' }">{{ item.source.name }}</span>
                </p>
              </div>
              <div class="patientTime">{{ item.time }}</div>
              <div class="patientType">就诊</div>
            </div>
            <div class="pageBox">
              <el-button type="text" @click="PreparePrePage">上一页</el-button>
              <el-button type="text" @click="PrepareNextPage">下一页</el-button>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane
          :label="'已就诊(' + AlreadyPatientListTotal + ')'"
          name="already"
        >
          <div class="todayVisitBox">
            <div
              class="todayPatientItem"
              :class="{ 'click': index == NowAlreadyIndex }"
              v-for="(item, index) in AlreadyPatientList"
              @click="GoAlready(item, index)"
              :key="index"
            >
              <div class="patientInfo">
                <p>
                  {{ SearchAlreadyModel.offset * 5 + index + 1 }}.
                  {{ item.patientId.name }} /
                  {{ item.patientInfo.gender.name }} /
                  {{ item.patientInfo.age }}岁
                  <span style="position: relative">
                    <div
                      v-if="item.chargeStatus === '0' && item.status.value === 'registrationStatus_4'"
                      class="refundBox"
                    >
                      作废
                    </div>
                  </span>
                </p>
                <p>
                  {{ item.company.name }} /
                  <span class="alreadytype">{{ item.status.value === 'registrationStatus_4' ? '已接诊' : item.status.name }}</span>
                </p>
              </div>
              <div class="patientTime">{{ item.time }}</div>
              <div class="patientType">查看详情</div>
            </div>
            <div class="pageBox">
              <el-button type="text" @click="AlreadyPrePage">上一页</el-button>
              <el-button type="text" @click="AlreadyNextPage">下一页</el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      <el-button class="visitRecordBtn">就诊记录</el-button>
      <ul class="visitRecordsBox" v-if="VisitRecordsList.length">
        <li
          v-for="(item, index) in VisitRecordsList"
          :key="index"
          @click="OpenVisitRecords(item)"
        >
          <span class="visitRecordTime"
            ><i class="el-icon-timer"></i
            >{{ item.medicalRecord.diagnoseDate }}</span
          >
          诊断：<span class="diagnosisTitel">{{
            item.medicalRecord.diagnose
          }}</span>
        </li>
      </ul>
      <el-dialog
        :title="RecordsTitle"
        :visible.sync="IsShowRecords"
        :close-on-click-modal="false"
        width="50%"
        v-loading="RecordsLoading"
      >
        <VisitRecords
          v-if="IsShowRecords"
          :NowVisitInfo="NowVisitInfo"
          :checkedForm="checkedForm"
          :InfusionUseOption="InfusionUseOption"
          :InfusionOption="InfusionOption"
          :DayNumOption="DayNumOption"
          :FrequencyOption="FrequencyOption"
        />
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" v-if="!IsOnlyRead" @click="UseRecord"
            >调 用</el-button
          >
          <el-button @click="IsShowRecords = false">关 闭</el-button>
        </span>
      </el-dialog>
    </div>
    <!-- 右侧信息块 -->
    <div class="rightInfo">
      <!-- 上方信息区域 -->
      <div class="topInfoBox">
        <!-- 基础信息块 -->
        <div class="infoItem">
          <div class="infoTitleBox">
            <h4>基础信息</h4>
          </div>
          <el-form
            :model="BasicInfoModel"
            ref="BasicInfoForm"
            :rules="BasicInfoFormRules"
            label-width="90px"
            disabled
          >
            <el-row :gutter="24">
              <el-col :span="7">
                <el-form-item label="治疗类型" prop="treatType">
                  <el-radio-group v-model="BasicInfoModel.treatType">
                    <el-radio label="初诊">初诊</el-radio>
                    <el-radio label="复诊">复诊</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="患者姓名" prop="name">
                  <el-input v-model="BasicInfoModel.name"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="BasicInfoModel.gender.name">
                    <el-radio label="男">男</el-radio>
                    <el-radio label="女">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="年龄" prop="age">
                  <el-row>
                    <el-col :span="12">
                      <el-input v-model="BasicInfoModel.age">
                        <span slot="suffix">年</span>
                      </el-input>
                    </el-col>
                    <el-col :span="12">
                      <el-input v-model="BasicInfoModel.month">
                        <span slot="suffix">月</span>
                      </el-input>
                    </el-col>
                  </el-row>
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-form-item label="联系方式" prop="phone">
                  <el-input v-model="BasicInfoModel.phone"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="身份证号" prop="card">
                  <el-input v-model="BasicInfoModel.card"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="与患者关系" prop="withPatientNexus">
                  <el-select
                    v-model="BasicInfoModel.withPatientNexus.name"
                    placeholder="请选择"
                  >
                    <el-option label="本人">本人</el-option>
                    <el-option label="户主">户主</el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="健康卡号" prop="healthCardNo">
                  <el-input v-model="BasicInfoModel.healthCardNo"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
        <!-- 病历块 -->
        <div class="infoItem">
          <div class="infoTitleBox">
            <h4>病例</h4>
            <div class="infoTitleRight" v-if="!IsOnlyRead">
              <el-popover placement="right" width="380" trigger="click">
                <div class="configureBox">
                  <div class="recordTitleBox">
                    <span>病历设置</span>
                    <el-button type="text" @click="ClearMedicineCofigure"
                      >恢复默认</el-button
                    >
                  </div>
                  <div class="recordLabelBox">
                    <h5>选择需要的病历字段</h5>
                    <el-checkbox-group
                      v-model="ChooseRecordList"
                      class="checkLabelBox"
                    >
                      <el-checkbox
                        class="checkItem"
                        v-for="(item, index) in MedicineRecordLabelList"
                        :key="index"
                        :label="item"
                        >{{ item.label }}</el-checkbox
                      >
                    </el-checkbox-group>
                  </div>
                </div>
                <el-button type="text" slot="reference">配置</el-button>
              </el-popover>
              <el-button @click='IsShowRecordFile = true'>上传附件</el-button>
            </div>
          </div>
          <el-form
            :disabled="IsOnlyRead"
            :model="MedicalRecordModel"
            ref="BasicInfoForm"
            :rules="BasicInfoFormRules"
            label-width="90px"
            size="medium"
          >
            <el-form-item label="主诉：" prop="patientTell">
              <el-input v-model="MedicalRecordModel.patientTell"></el-input>
            </el-form-item>
            <el-form-item label="现病史：" prop="nowHistory">
              <el-input v-model="MedicalRecordModel.nowHistory"></el-input>
            </el-form-item>
            <el-form-item label="既往史：" prop="beforeHistory">
              <el-input v-model="MedicalRecordModel.beforeHistory"></el-input>
            </el-form-item>
            <el-form-item label="其他检查：" prop="otherCheck">
              <el-input v-model="MedicalRecordModel.otherCheck"></el-input>
            </el-form-item>
            <el-form-item label="诊断：" prop="diagnose">
              <el-input v-model="MedicalRecordModel.diagnose"></el-input>
            </el-form-item>
            <el-form-item
              v-for="(item, index) in ChooseRecordList"
              :key="index"
              :label="item.label + '：'"
              :prop="item.name"
            >
              <el-input v-model="item.value"></el-input>
            </el-form-item>
          </el-form>
        </div>
        <!-- 添加处方块 -->
        <div v-if="!IsOnlyRead" class="addPrescriptionBox">
          <h4><span class="titleSign"></span>添加处方</h4>
          <el-button-group>
            <el-button
              icon="el-icon-plus"
              @click="OpenPrescription('WesternInput')"
              >西药处方</el-button
            >
            <el-button
              icon="el-icon-plus"
              @click="OpenPrescription('ChineseInput')"
              >中药处方</el-button
            >
            <el-button
              icon="el-icon-plus"
              @click="OpenPrescription('InfusionBtn')"
              >输液处方</el-button
            >
            <el-button
              icon="el-icon-plus"
              @click="OpenPrescription('TreatmentInput')"
              >诊疗项目</el-button
            >
          </el-button-group>
        </div>
        <!-- 西药处方块 -->
        <div class="infoItem" v-if="IsShowWestern">
          <div class="infoTitleBox">
            <h4>西药处方</h4>
          </div>
          <div class="itemContent">
            <el-popover
              placement="right"
              v-if="!IsOnlyRead"
              width="700"
              trigger="focus"
            >
              <el-table
                :data="WesternMedicineTable"
                border
                highlight-current-row
                @row-click="RowClickWesternTable"
              >
                <el-table-column prop="goodsName" label="药品名称">
                </el-table-column>
                <el-table-column prop="gg" label="规格" width="120">
                <template slot-scope="scope">
                  {{scope.row.dosis}}{{scope.row.dosisUnit.name}} * {{scope.row.preparation}}{{scope.row.preparationUnit.name}}/{{scope.row.pack.name}}
                </template>
                </el-table-column>
                <el-table-column prop="factory.name" label="厂家" width="100">
                </el-table-column>
                <el-table-column
                  label="单位"
                  width="80"
                >
                  <template slot-scope="scope">
                    {{
                      scope.row.isUnpackSell === '1'
                        ? scope.row.preparationUnit.name
                        : scope.row.pack.name
                    }}
                  </template>
                </el-table-column>
                <el-table-column label="零售价" width="80">
                  <template slot-scope="scope">
                    {{
                      scope.row.isUnpackSell === '1' ? (scope.row.retailPrice + "/" + scope.row.preparationUnit.name) : (scope.row.price + "/" + scope.row.pack.name)
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
                ref="WesternInput"
                v-model="SearchWesternInput"
                @input="GetWesternTable"
                placeholder="输入药品名称或拼音码"
              ></el-input>
            </el-popover>

            <el-table
              :data="ChooseWesternMedicineTable"
              border
              style="width: 100%"
              class="tableStyle"
            >
              <el-table-column
                type="index"
                label="序号"
                width="50"
                align="center"
              >
              </el-table-column>
              <el-table-column prop="goodsName" label="名称" align="center">
                <template slot-scope="scope">
                  {{
                    scope.row.goodsName
                      ? scope.row.goodsName
                      : scope.row.drugStuffId.name
                  }}
                  <span style="color: forestgreen;">
                    {{scope.row.goodsName ? (scope.row.dosis + scope.row.dosisUnit.name + "*" + scope.row.preparation + scope.row.preparationUnit.name + "/" + scope.row.pack.name) 
                    : (scope.row.drugStuffId.drug.dosis + scope.row.drugStuffId.drug.dosisUnit.name + "*" + scope.row.drugStuffId.drug.preparation + scope.row.drugStuffId.drug.preparationUnit.name + "/" + scope.row.drugStuffId.drug.pack.name)
                    }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="singleDosage" label="单次用量" width="100">
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.singleDosage"
                    @input="GetWesternFee(scope.$index, scope.row)"
                    :disabled="IsOnlyRead"
                  >
                    <template slot="append">{{
                      scope.row.preparationUnit
                        ? scope.row.preparationUnit.name
                        : scope.row.drugStuffId.drug.preparationUnit.name
                    }}</template>
                  </el-input>
                </template>
              </el-table-column>
              <el-table-column
                prop="westernMedicineUse"
                label="用法"
                width="100"
              >
                <template slot-scope="scope">
                  <el-select
                    v-model="scope.row.westernMedicineUse"
                    :disabled="IsOnlyRead"
                    placeholder=""
                  >
                    <el-option
                      v-for="item in WesternUseOption"
                      :key="item.value"
                      :label="item.name"
                      :value="{ name: item.name, value: item.value }"
                    >
                    </el-option>
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="frequency" label="频度" width="120">
                <template slot-scope="scope">
                  <el-select
                    v-model="scope.row.frequency"
                    :disabled="IsOnlyRead"
                    @change="GetWesternFee(scope.$index, scope.row)"
                    placeholder=""
                  >
                    <el-option
                      v-for="item in FrequencyOption"
                      :key="item.value"
                      :label="item.name"
                      :value="{ name: item.name, value: item.value }"
                    >
                    </el-option>
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="days" label="天数" width="100">
                <template slot-scope="scope">
                  <el-select
                    v-model="scope.row.days"
                    :disabled="IsOnlyRead"
                    @change="GetWesternFee(scope.$index, scope.row)"
                    placeholder=""
                  >
                    <el-option
                      v-for="item in DayNumOption"
                      :key="item.value"
                      :label="item.name"
                      :value="{ name: item.name, value: item.value }"
                    >
                    </el-option>
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="total" label="总量" width="100">
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.total"
                    disabled
                  >
                    <template slot="append">{{GetWesternUnit(scope.$index, scope.row)}}</template>
                  </el-input>
                </template>
              </el-table-column>
              <el-table-column
                prop="price"
                label="单价"
                width="100"
                align="center"
              >
                <template slot-scope="scope">{{GetWesternPrice(scope.$index, scope.row) + "/" + GetWesternUnit(scope.$index, scope.row)}}</template>
              </el-table-column>
              <el-table-column
                prop="allFee"
                label="合计"
                width="100"
                align="center"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.allFee }}</span>
                </template>
              </el-table-column>
              <el-table-column v-if="!IsOnlyRead" label="操作" align="center">
                <template slot-scope="scope">
                  <i
                    class="el-icon-circle-close"
                    @click="DeleteWestern(scope.$index, scope.row)"
                  ></i>
                </template>
              </el-table-column>
            </el-table>
            <div class="itemBottomBox">
              <p>西药金额：{{ WesternFee }}元</p>
              <div style="width: 70%">
                备注:<el-input
                  v-model="WesternRemarks"
                  :disabled="IsOnlyRead"
                  style="width: 90%; margin-left: 10px"
                  placeholder="请输入备注"
                ></el-input>
              </div>
            </div>
          </div>
        </div>
        <!-- 中药处方块 -->
        <div class="infoItem" v-if="IsShowChinese">
          <div class="infoTitleBox">
            <h4>中药处方</h4>
            <div class="infoTitleRight"></div>
          </div>
          <div class="itemContent">
            <el-popover
              placement="right"
              v-if="!IsOnlyRead"
              width="700"
              trigger="focus"
            >
              <el-table
                :data="ChineseMedicineTable"
                border
                highlight-current-row
                @row-click="RowClickChineseTable"
              >
                <el-table-column prop="goodsName" label="药品名称">
                </el-table-column>
                <el-table-column prop="factory.name" label="厂家" width="100">
                </el-table-column>
                <el-table-column prop="preparationUnit.name" label="单位" width="80">
                </el-table-column>
                <el-table-column prop="retailPrice" label="零售价" width="80">
                </el-table-column>
                <el-table-column prop="stockNumber" label="库存" width="100">
                </el-table-column>
              </el-table>
              <el-input
                prefix-icon="el-icon-plus"
                suffix-icon="el-icon-search"
                style="width: 30%"
                slot="reference"
                ref="ChineseInput"
                v-model="SearchChineseInput"
                @input="GetChineseTable"
                placeholder="输入药品名称或拼音码"
              ></el-input>
            </el-popover>
            <div class="chineseContentCenter">
              <div
                class="chineseContentCenterItem"
                v-for="(item, index) in ChooseChineseMedicineTable"
                :key="index"
              >
                <i
                  class="closeBtn el-icon-close"
                  v-if="!IsOnlyRead"
                  @click="DeleteChinese(index)"
                ></i>
                <ChineseMedicine
                  :IsOnlyRead="IsOnlyRead"
                  :ChineseUseTimeOption="ChineseUseTimeOption"
                  :ChineseMedicine="item"
                  @GetChineseFee='GetChineseFee'
                />
              </div>
            </div>
            <div class="itemBottomBox">
              <p>中药金额：{{ ChinesePrescription.fee }}元</p>
              <div style="width: 70%">
                <el-input
                  v-model="ChinesePrescription.dosage"
                  :disabled="IsOnlyRead"
                  @input="GetChineseFee"
                  style="width: 10%"
                ></el-input
                >剂 用法：
                <el-select
                  v-model="ChinesePrescription.recipelUse"
                  :disabled="IsOnlyRead"
                  style="width: 100px"
                  placeholder=""
                >
                  <el-option
                    v-for="item in ChineseUseOption"
                    :key="item.value"
                    :label="item.name"
                    :value="{ name: item.name, value: item.value }"
                  >
                  </el-option>
                </el-select>
                <el-select
                  v-model="ChinesePrescription.frequency"
                  :disabled="IsOnlyRead"
                  style="width: 100px"
                  placeholder=""
                >
                  <el-option
                    v-for="item in ChineseTimeOption"
                    :key="item.value"
                    :label="item.name"
                    :value="{ name: item.name, value: item.value }"
                  >
                  </el-option>
                </el-select>
                <el-select
                  v-model="ChinesePrescription.takeFrequency"
                  :disabled="IsOnlyRead"
                  style="width: 100px"
                  placeholder=""
                >
                  <el-option
                    v-for="item in FrequencyOption"
                    :key="item.value"
                    :label="item.name"
                    :value="{ name: item.name, value: item.value }"
                  >
                  </el-option>
                </el-select>
                一次<el-input
                  v-model="ChinesePrescription.singleDosage"
                  :disabled="IsOnlyRead"
                  style="width: 10%"
                ></el-input
                >ml
              </div>
            </div>
          </div>
        </div>
        <!-- 输液处方块 -->
        <div class="infoItem" v-if="IsShowInfusion">
          <div class="infoTitleBox">
            <h4>输液处方</h4>
            <div class="infoTitleRight">
              <el-button type="text" v-if="!IsOnlyRead" @click="AddInfusionItem"
                >加一组</el-button
              >
            </div>
          </div>
          <div class="itemContent">
            <div
              class="InfusionContentItem"
              v-for="(item, index) in InfusionItemList"
              :key="index"
            >
              <i
                class="closeBtn el-icon-close"
                v-if="!IsOnlyRead"
                @click="DeleteInfusionItem(index)"
              ></i>
              <InfusionPrescription
                @GetInfusionFee="GetInfusionFee"
                :InfusionUseOption="InfusionUseOption"
                :InfusionOption="InfusionOption"
                :DayNumOption="DayNumOption"
                :FrequencyOption="FrequencyOption"
                :InfoModel="item"
                :IsOnlyRead="IsOnlyRead"
                :GroupNumber="index + 1"
                :key="index"
              />
            </div>
            <div class="itemBottomBox">
              <p>输液金额：{{ InfusionFee }}元</p>
              <div style="width: 70%">
                备注:<el-input
                  style="width: 90%; margin-left: 10px"
                  :disabled="IsOnlyRead"
                  v-model="InfusionRemarks"
                  placeholder="请输入备注"
                  ref="InfusionBtn"
                ></el-input>
              </div>
            </div>
          </div>
        </div>
        <!-- 诊疗项目块 -->
        <div class="infoItem" v-if="IsShowTreatment">
          <div class="infoTitleBox">
            <h4>诊疗项目</h4>
          </div>
          <div class="itemContent">
            <el-popover
              v-if="!IsOnlyRead"
              placement="right"
              width="400"
              trigger="click"
            >
              <el-table
                :data="TreatmentTable"
                border
                highlight-current-row
                @row-click="RowClickTreatmentTable"
              >
                <el-table-column prop="itemName" label="项目名称">
                </el-table-column>
                <el-table-column prop="itemType.name" label="项目类型">
                </el-table-column>
                <el-table-column prop="unit.name" label="单位">
                </el-table-column>
                <el-table-column prop="salePrice" label="单价">
                </el-table-column>
              </el-table>
              <el-input
                prefix-icon="el-icon-plus"
                suffix-icon="el-icon-search"
                style="width: 30%"
                slot="reference"
                ref="TreatmentInput"
                v-model="SearchCostItemInput"
                @input="GetCostItemTable"
                placeholder="输入药品名称或拼音码"
              ></el-input>
            </el-popover>

            <el-table
              :data="ChooseTreatmentTable"
              border
              style="width: 100%"
              class="tableStyle"
            >
              <el-table-column
                type="index"
                label="序号"
                width="50"
                align="center"
              >
              </el-table-column>
              <el-table-column prop="itemName" label="项目名称">
                <template slot-scope="scope">
                  {{
                    scope.row.itemName
                      ? scope.row.itemName
                      : scope.row.drugStuffId.name
                  }}
                </template>
              </el-table-column>
              <el-table-column prop="total" label="数量" width="100">
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.total"
                    @input="GetCostItemFee(scope.$index, scope.row)"
                  >
                    <template slot="append">{{
                      scope.row.unit
                        ? scope.row.unit.name
                        : scope.row.drugStuffId.dosisUnit.name
                    }}</template>
                  </el-input>
                </template>
              </el-table-column>
              <el-table-column prop="salePrice" label="单价" width="80">
                <template slot-scope="scope">
                  {{
                    TodayActiveName === 'prepare'
                      ? scope.row.salePrice
                      : scope.row.drugStuffId.price
                  }}
                </template>
              </el-table-column>
              <el-table-column
                prop="allFee"
                label="合计"
                width="100"
                align="center"
              >
                <template slot-scope="scope">
                  {{ scope.row.allFee }}
                </template>
              </el-table-column>
              <el-table-column v-if="!IsOnlyRead" label="操作" align="center">
                <template slot-scope="scope">
                  <i
                    class="el-icon-circle-close"
                    @click="DeleteTreatment(scope.$index, scope.row)"
                  ></i>
                </template>
              </el-table-column>
            </el-table>
            <div class="itemBottomBox">
              <p>诊疗金额：{{ CostItemFee }}元</p>
            </div>
          </div>
        </div>
        <!-- 附加费块 -->
        <div class="infoItem">
          <div class="infoTitleBox">
            <h4>附加费</h4>
          </div>
          <div class="itemContent">
            <el-popover
              placement="right"
              v-if="!IsOnlyRead"
              width="400"
              trigger="click"
            >
              <el-table
                :data="SurchargeTable"
                border
                highlight-current-row
                @row-click="RowClickSurchargeTable"
              >
                <el-table-column prop="name" label="费用名称">
                </el-table-column>
                <el-table-column prop="type.name" label="费用类型" width="100">
                </el-table-column>
                <el-table-column prop="kc" label="库存" width="100">
                </el-table-column>
                <el-table-column prop="priceOutSell" label="单价" width="80">
                </el-table-column>
              </el-table>
              <el-input
                prefix-icon="el-icon-plus"
                suffix-icon="el-icon-search"
                style="width: 30%"
                slot="reference"
                v-model="SearchSurchargeInput"
                @input="GetSurchargeTable"
                placeholder="输入药品名称或拼音码"
              ></el-input>
            </el-popover>

            <el-table
              :data="ChooseSurchargeTable"
              border
              style="width: 100%"
              class="tableStyle"
            >
              <el-table-column
                type="index"
                label="序号"
                width="50"
                align="center"
              >
              </el-table-column>
              <el-table-column prop="name" label="费用名称">
                <template slot-scope="scope">
                  {{
                    scope.row.name ? scope.row.name : scope.row.drugStuffId.name
                  }}
                </template>
              </el-table-column>
              <el-table-column prop="total" label="数量" width="100">
                <template slot-scope="scope">
                  <el-input
                    :disabled="IsOnlyRead"
                    v-model="scope.row.total"
                    @input="GetSurchargeFee(scope.$index, scope.row)"
                  >
                    <template slot="append">套</template>
                  </el-input>
                </template>
              </el-table-column>
              <el-table-column prop="priceOutSell" label="单价" width="80">
                <template slot-scope="scope">
                  {{
                    scope.row.priceOutSell
                      ? scope.row.priceOutSell
                      : scope.row.drugStuffId.price
                  }}
                </template>
              </el-table-column>
              <el-table-column
                prop="allFee"
                label="合计"
                width="100"
                align="center"
              >
                <template slot-scope="scope">
                  {{ scope.row.allFee }}
                </template>
              </el-table-column>
              <el-table-column label="删除" v-if="!IsOnlyRead" align="center">
                <template slot-scope="scope">
                  <i
                    class="el-icon-circle-close"
                    @click="DeleteSurchargeTable(scope.$index, scope.row)"
                  ></i>
                </template>
              </el-table-column>
            </el-table>
            <div class="itemBottomBox">
              <p>附加金额：{{ SurchargeFee }}元</p>
            </div>
          </div>
        </div>
        <el-input v-model="DoctorAdvice" :disabled="IsOnlyRead">
          <template slot="prepend">医嘱事项</template>
        </el-input>
      </div>
      <!-- 下方固定区域 -->
      <div class="bottomInfoBox">
        <h5>总金额：{{ AllTotal }}元</h5>
        <div class="btnGroup" v-if="!IsOnlyRead">
          <el-button class="visitRecordBtn" @click="FinishVisit"
            >完成接诊</el-button
          >
          <el-button>预览</el-button>
          <el-button type="text"><i class="el-icon-printer"></i>打印</el-button>
        </div>
        <div class="btnGroup" v-else>
          <el-button @click="invalidPrescription" :disabled="IsInvalidPrescription">作废处方</el-button>
        </div>
      </div>
    </div>
    <el-dialog
      title="病历附件"
      :visible.sync="IsShowRecordFile"
      width="30%"
      :before-close="handleClose">
      <upload-file v-model='fileIdFile' titile='病历附件' :action='fileIdFile.action' :objectId='fileIdFile.fileId'></upload-file>
    </el-dialog>
    <!--                  生成代码                  -->
    <!-- 历史记录  -->
    <History :bussObject="curentRow"></History>
    <!-- 编辑窗口  -->
  </el-row>
</template>

<script>
import { listDictItemAll } from "@/api/sys/dictItem";
import { listRegistrationPage, getRegistrationById, updateStatus } from "@/api/outpatient/registration";
import { getPatientById, listPatientAll } from "@/api/outpatient/patient";
import { listAllStock } from "@/api/stock/drug";
import { listCostItemPage } from "@/api/treatment/costItem";
import { listStuffPage } from "@/api/stock/stuff";
import {
  allSaveMedicalRecord,
  allQueryMedicalRecord,
} from "@/api/outpatient/medicalRecord";
import MedicalRecordForm from "./medicalRecordForm";
import ExportExcelButton from "@/components/ExportExcelButton";
import ViewColumnsSelect from "@/views/components/ViewColumnsSelect";
import QueryForm from "@/views/components/queryForm";
import MainUI from "@/views/components/mainUI";
import OperationIcon from "@/components/OperationIcon";
import History from "@/views/components/history";
import ChineseMedicine from "./components/ChineseMedicine";
import InfusionPrescription from "./components/InfusionPrescription.vue";
import VisitRecords from "./components/VisitRecords.vue";
import indexVue from '../../../components/Clipboard/index.vue';
import uploadFile from '@/views/components/uploadFile'
export default {
  extends: MainUI,
  components: {
    uploadFile,
    MedicalRecordForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History,
    ChineseMedicine,
    InfusionPrescription,
    VisitRecords,
  },
  data() {
    return {
      IsShowRecordFile: false,
      fileIdFile: {
        fileId: '',
        action: 'add',
        deletes: [],  // 待删除（已上传）的文件列表
        uploads: []   // 待上传的文件列表
      },
      //所有患者
      AllPatientOption: [],
      //当前就诊id
      VisitId: "",
      //待就诊当前下标
      NowPreIndex: 5,
      //已就诊当前下标
      NowAlreadyIndex: 5,
      //是否只读
      IsOnlyRead: false,
      //医嘱
      DoctorAdvice: "",
      //就诊搜索条件
      SearchPreModel: {
        columnName: "",
        limit: 5,
        offset: 0,
        order: "",
      },
      SearchAlreadyModel: {
        columnName: "",
        limit: 5,
        offset: 0,
        order: "",
      },
      //搜索药品条件
      SearchWesternModel: {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "",
      },
      SearchChineseModel: {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "",
      },
      //患者姓名
      SearchPatientId: "",
      //搜索西药
      SearchWesternInput: "",
      //搜索中药
      SearchChineseInput: "",
      //搜索诊疗项目
      SearchCostItemInput: "",
      //搜索附加费
      SearchSurchargeInput: "",
      //西药备注
      WesternRemarks: "",
      //输液备注
      InfusionRemarks: "",
      //西药总价格
      WesternFee: 0,
      //输液总价格
      InfusionFee: 0,
      //诊疗项目总价格
      CostItemFee: 0,
      //附加费总价格
      SurchargeFee: 0,
      //中药处方
      ChinesePrescription: this.ReturnChineseModel(),
      //病历配置字段
      MedicineRecordLabelList: [
        {
          label: "个人史",
          name: "personalHistory",
          value: "",
        },
        {
          label: "过敏史",
          name: "allergyHistory",
          value: "",
        },
        {
          label: "疾病史",
          name: "diseaseHistory",
          value: "",
        },
        {
          label: "传染病史",
          name: "infectiousHistory",
          value: "",
        },
        {
          label: "手术史",
          name: "surgeryHistory",
          value: "",
        },
        {
          label: "输血史",
          name: "transfusionHistory",
          value: "",
        },
        {
          label: "体格检查",
          name: "physiqueCheck",
          value: "",
        },
        {
          label: "急诊诊断",
          name: "emergencyDiagnose",
          value: "",
        },
        {
          label: "急诊效果",
          name: "emergencyEffect",
          value: "",
        },
        {
          label: "家族史",
          name: "familyHistory",
          value: "",
        },
        {
          label: "月经史",
          name: "lunariaHistory",
          value: "",
        },
        {
          label: "婚育史",
          name: "pregnancyHistory",
          value: "",
        },
        {
          label: "辅助检查",
          name: "assistCheck",
          value: "",
        },
      ],
      //选择的病例配置
      ChooseRecordList: [],
      //就诊记录弹框标题
      RecordsTitle: "就诊详情",
      //是否显示就诊记录弹框
      IsShowRecords: false,
      //就诊记录弹框加载状态
      RecordsLoading: false,
      //是否显示西药处方-中药处方-输液处方-诊疗项目
      IsShowWestern: false,
      IsShowChinese: false,
      IsShowInfusion: false,
      IsShowTreatment: false,
      //tabs默认状态 prepare:待就诊 already:已就诊
      TodayActiveName: "prepare",
      //今日待就诊患者列表
      PreparePatientList: [],
      //今日待就诊患者总数
      PreparePatientTotal: 0,
      //今日已就诊患者列表
      AlreadyPatientList: [],
      //今日已就诊患者总数
      AlreadyPatientListTotal: 0,
      //就诊记录列表
      VisitRecordsList: [],
      //当前就诊记录信息
      NowVisitInfo: {},
      checkedForm: {
        record: false,
        Prescription: false,
      },
      //基础信息
      BasicInfoModel: {
        treatType: "",
        name: "",
        gender: "",
        age: "",
        month: "",
        phone: "",
        card: "",
        withPatientNexus: {
          name: ''
        },
        healthCardNo: "",
      },
      //病历表单
      MedicalRecordModel: this.ReturnMedicalModel(),
      InfusionUseOption: [],
      InfusionOption: [],
      //西药处方表格数据
      WesternMedicineTable: [],
      //已选择西药处方
      ChooseWesternMedicineTable: [],
      //中药处方表格数据
      ChineseMedicineTable: [],
      //西药用法下拉列表
      WesternUseOption: [],
      ChineseUseTimeOption: [],
      //西药频度下拉列表
      FrequencyOption: [],
      //西药天数下拉列表
      DayNumOption: [],
      //已选择中药处方
      ChooseChineseMedicineTable: [],
      //中药用法下拉列表
      ChineseUseOption: [],
      ChineseTimeOption: [],
      //输液处方组表格
      InfusionItemList: [],
      //诊疗项目表格
      TreatmentTable: [
        {
          BrandName: "血常规",
          dj: "3",
        },
        {
          BrandName: "身体检查",
          dj: "2",
        },
      ],
      //已选诊疗项目表格
      ChooseTreatmentTable: [],
      //附加费可选表格数据
      SurchargeTable: [],
      //附加费已选表格数据
      ChooseSurchargeTable: [],
      //患者表单验证
      BasicInfoFormRules: {
        treatType: [
          { required: true, message: "请选择治疗类型", trigger: "blur" },
        ],
        name: [{ required: true, message: "请输入患者姓名", trigger: "blur" }],
        gender: [{ required: true, message: "请选择性别", trigger: "blur" }],
        age: [{ required: true, message: "请输入年龄", trigger: "blur" }],
        phone: [{ required: true, message: "请输入联系方式", trigger: "blur" }],
        patientTell: [
          { required: true, message: "请输入主诉", trigger: "blur" },
        ],
        diagnose: [{ required: true, message: "请输入诊断", trigger: "blur" }],
      },
      tableId: "1007027220819460134",
      schemeId: "1007027220819460165",
      //是否允许作废处方
      IsInvalidPrescription: false
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
    AllTotal() {
      return (
        this.WesternFee +
        this.ChinesePrescription.fee +
        this.InfusionFee +
        this.CostItemFee +
        this.SurchargeFee
      );
    },
  },
  watch: {
    InfusionItemList() {
      this.InfusionItemList.map((item, index) => {
        item.infuseGroup = index + 1;
      });
    },
    ChooseChineseMedicineTable() {
      if (!this.IsOnlyRead) this.GetChineseFee(1);
    },
  },
  methods: {
    Init() {
      this.GetAllPatient();
      this.GetAllOption();
      this.GetMedicalRecordAll();
      this.GetVisitList();
      this.GetCostItemTable();
      this.GetSurchargeTable();
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
    GetVisitList() {
      this.GetPreparePatientList();
      this.GetAlreadyPatientList();
    },
    //获取待就诊患者列表
    GetPreparePatientList() {
      let params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: this.Company.id,
        },{
          columnName: "status",
          queryType: "=",
          value: "",
        },{
          columnName: "patient_id",
          queryType: "=",
          value: this.SearchPatientId,
        },{
          columnName: "doctor_id",
          queryType: "=",
          value: this.UserId,
        },
      ];
      this.SearchPreModel.params = params;
      this.SearchPreModel.params[1].value = "registrationStatus_0";
      let model = { age: "", gender: "" };
      listRegistrationPage(this.SearchPreModel).then((responseData) => {
        if (responseData.code == 100) {
          this.PreparePatientTotal = responseData.data.total;
          this.PreparePatientList = responseData.data.rows;
          if (this.PreparePatientTotal == 0) return;
          this.PreparePatientList.map((item, index) => {
            item.patientInfo = model;
            this.GetPatientById(item.patientId.id, index, "pre");
          });
          this.$forceUpdate();
        }
      });
    },
    //获取已就诊患者列表
    GetAlreadyPatientList() {
      let params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: this.Company.id,
        },{
          columnName: "status",
          queryType: "in",
          value: ["registrationStatus_1", "registrationStatus_4"],
        },{
          columnName: "patient_id",
          queryType: "=",
          value: this.SearchPatientId,
        },
        {
          columnName: "doctor_id",
          queryType: "=",
          value: this.UserId,
        },
      ];
      this.SearchAlreadyModel.params = params;
      //this.SearchAlreadyModel.params[1].value = "registrationStatus_1";
      let model = { age: "", gender: "" };
      listRegistrationPage(this.SearchAlreadyModel).then((responseData) => {
        if (responseData.code == 100) {
          this.AlreadyPatientListTotal = responseData.data.total;
          this.AlreadyPatientList = responseData.data.rows;
          if (this.AlreadyPatientListTotal == 0) return;
          this.AlreadyPatientList.map((item, index) => {
            item.patientInfo = model;
            this.GetPatientById(item.patientId.id, index, "already");
          });
        }
      });
    },
    //获取就诊患者的个人信息
    GetPatientById(id, index, type) {
      this.AllPatientOption.map(item => {
        if (item.id == id) {
          if (type == 'pre') {
            this.PreparePatientList[index].patientInfo = item
          } else {
            this.AlreadyPatientList[index].patientInfo = item
          }
        }
      })
    },
    //获取数据字典的值
    GetAllOption() {
      this.GetOption("1014474470772899981");
      this.GetOption("1014474470772899990");
      this.GetOption("1014474470772899985");
      this.GetOption("1014474470772900028");
      this.GetOption("1014474470772900052");
      this.GetOption("1014474470772900062");
      this.GetOption("1014474470772900068");
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
        if (optionId == "1014474470772899981")
          this.ChineseUseOption = responseData.data;
        else if (optionId == "1014474470772899990")
          this.FrequencyOption = responseData.data;
        else if (optionId == "1014474470772899985")
          this.ChineseTimeOption = responseData.data;
        else if (optionId == "1014474470772900028")
          this.WesternUseOption = responseData.data;
        else if (optionId == "1014474470772900052")
          this.DayNumOption = responseData.data;
        else if (optionId == "1014474470772900062")
          this.InfusionUseOption = responseData.data;
        else if (optionId == "1014474470772900068")
          this.InfusionOption = responseData.data;
        else if (optionId == "1014474470772900058")
          this.ChineseUseTimeOption = responseData.data;
      });
    },
    GetClearData() {
      this.VisitId = "";
      this.MedicalRecordModel = this.ReturnMedicalModel();
      this.ChooseWesternMedicineTable = [];
      this.WesternFee = 0;
      this.WesternRemarks = "";
      this.ChooseChineseMedicineTable = [];
      this.ChinesePrescription = this.ReturnChineseModel();
      this.InfusionItemList = [];
      this.InfusionFee = 0;
      this.InfusionRemarks = "";
      this.ChooseTreatmentTable = [];
      this.CostItemFee = 0;
      this.ChooseSurchargeTable = [];
      this.SurchargeFee = 0;
      this.DoctorAdvice = "";
    },
    ReturnMedicalModel() {
      return {
        patientTell: "",
        nowHistory: "",
        beforeHistory: "",
        diagnose: "",
        otherCheck: "",
        personalHistory: "",
        allergyHistory: "",
        diseaseHistory: "",
        infectiousHistory: "",
        surgeryHistory: "",
        transfusionHistory: "",
        physiqueCheck: "",
        emergencyDiagnose: "",
        emergencyEffect: "",
        familyHistory: "",
        lunariaHistory: "",
        pregnancyHistory: "",
        assistCheck: "",
      };
    },
    ReturnChineseModel() {
      return {
        recipelType: { name: "中药处方", value: "recipelType_1" },
        isPay: '0',
        dosage: 0,
        recipelUse: {},
        frequency: {},
        takeFrequency: {},
        singleDosage: "",
        fee: 0,
      };
    },
    //待就诊翻页
    PreparePrePage() {
      if (this.SearchPreModel.offset == 0) return;
      this.SearchPreModel.offset -= 1;
      this.GetPreparePatientList();
    },
    PrepareNextPage() {
      this.SearchPreModel.limit / this.PreparePatientTotal;
      if (
        this.SearchPreModel.offset + 1 >=
        this.PreparePatientTotal / this.SearchPreModel.limit
      )
        return;
      this.SearchPreModel.offset += 1;
      this.GetPreparePatientList();
    },
    //已就诊翻页
    AlreadyPrePage() {
      if (this.SearchAlreadyModel.offset == 0) return;
      this.SearchAlreadyModel.offset -= 1;
      this.GetAlreadyPatientList();
    },
    AlreadyNextPage() {
      if (this.SearchAlreadyModel.offset + 1 >= this.AlreadyPatientListTotal / this.SearchAlreadyModel.limit ) return;
      this.SearchAlreadyModel.offset += 1;
      this.GetAlreadyPatientList();
    },
    //得到处方总价
    GetChineseFee(type) {
      if (type != 1 && !this.ChinesePrescription.dosage) return
      this.ChinesePrescription.fee = 0;
      this.ChooseChineseMedicineTable.map((item) => {
        this.ChinesePrescription.fee += item.allFee;
      });
      this.ChinesePrescription.fee = this.ChinesePrescription.fee * this.ChinesePrescription.dosage
    },
    //根据是否零售与新增还是查看获取单位
    GetWesternUnit(index, row) {
      if(row.preparationUnit) {
        if (row.isUnpackSell == '1') {
          return row.preparationUnit.name
        } else {
          return row.pack.name
        }
      } else {
        if (row.drugStuffId.drug.isUnpackSell == '1') {
          return row.drugStuffId.drug.preparationUnit.name
        } else {
          return row.drugStuffId.drug.pack.name
        }
      }
    },
    //根据是否零售与新增还是查看获取价格
    GetWesternPrice(index, row) {
      if(row.preparationUnit) {
        if (row.isUnpackSell == '1') {
          return row.retailPrice
        } else {
          return row.price
        }
      } else {
        if (row.drugStuffId.drug.isUnpackSell == '1') {
          return row.drugStuffId.drug.retailPrice
        } else {
          return row.drugStuffId.drug.price
        }
      }
    },
     //根据是否零售计算总量与价格
    GetWesternFee(index, row) {
      if (row) {
        if (row.frequency && row.days) {
          row.singleDosage = row.singleDosage ? row.singleDosage : 0
          if (row.isUnpackSell == '1') {
            row.total = Math.ceil(row.singleDosage * row.frequency.value.split('_')[1] * row.days.name)
            row.allFee = row.total * (row.retailPrice ? row.retailPrice : row.drugStuffId.drug.retailPrice)
          } else {
            let total = Math.ceil(row.singleDosage * row.frequency.value.split('_')[1] * row.days.name)
            row.total = Math.ceil(total/row.preparation)
            row.allFee = row.total * (row.price ? row.price : row.drugStuffId.drug.price)
          }
        }
      };
      this.WesternFee = 0;
      this.$nextTick(() => {
        this.ChooseWesternMedicineTable.map((item) => {
          this.WesternFee += item.allFee;
        });
      });
    },
    GetInfusionFee() {
      this.InfusionFee = 0;
      this.$nextTick(() => {
        this.InfusionItemList.map((item) => {
          item.infusionList.map((row) => {
            this.InfusionFee += row.allFee;
          });
        });
      });
    },
    GetCostItemFee(index, row) {
      if (row)
        row.allFee =
          row.total * (row.salePrice ? row.salePrice : row.drugStuffId.price);
      this.CostItemFee = 0;
      this.$nextTick(() => {
        this.ChooseTreatmentTable.map((item) => {
          this.CostItemFee += item.allFee;
        });
      });
    },
    GetSurchargeFee(index, row) {
      if (row)
        row.allFee =
          row.total *
          (row.priceOutSell ? row.priceOutSell : row.drugStuffId.price);
      this.SurchargeFee = 0;
      this.$nextTick(() => {
        this.ChooseSurchargeTable.map((item) => {
          this.SurchargeFee += item.allFee;
        });
      });
    },
    //获取处方表格数据
    GetSurchargeTable() {
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
            columnName: "name",
            queryType: "like",
            value: this.SearchSurchargeInput,
          },
          {
            columnName: "is_out_sell",
            queryType: "=",
            value: '1',
          }
        ],
      };
      listStuffPage(SearchModel).then((responseData) => {
        if (responseData.code == 100) {
          this.SurchargeTable = responseData.data.rows;
        }
      });
    },
    GetCostItemTable() {
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
            columnName: "item_name",
            queryType: "like",
            value: this.SearchCostItemInput,
          },
          {
            columnName: "item_type",
            queryType: "<>",
            value: 'treatmentItemType_4',
          }
        ],
      };
      listCostItemPage(SearchModel).then((responseData) => {
        if (responseData.code == 100) {
          this.TreatmentTable = responseData.data.rows;
        }
      });
    },
    GetMedicalRecordAll() {
      let params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: this.Company.id,
        },
        {
          columnName: "type",
          queryType: "=",
          value: "",
        },
        {
          columnName: "goods_name",
          queryType: "like",
          value: "",
        }
      ];
      this.SearchWesternModel.params = JSON.parse(JSON.stringify(params));
      this.SearchChineseModel.params = JSON.parse(JSON.stringify(params));
      this.GetWesternTable();
      this.GetChineseTable();
    },
    GetWesternTable() {
      this.SearchWesternModel.params[1].value = "medicalType_0";
      this.SearchWesternModel.params[2].value = this.SearchWesternInput;
      listAllStock(this.SearchWesternModel).then((responseData) => {
        if (responseData.code == 100) {
          responseData.data.forEach(element => {
            let isUnpackSell = element.isUnpackSell;    //允许拆零销售
            let stockNumber = element.stockNumber;      //库存数量
            let packName = element.pack.name;           //包装（单位）
            let preparation = element.preparation;      //制剂
            let preparationUnit = element.preparationUnit.name;  //制剂单位
            let price = element.price;                  //售价
            let retailPrice = element.retailPrice;      //拆开后零售价
            
            let stockText = "";
            if(isUnpackSell === '1')
            {
              //向下取整
              let p1 = Math.floor(stockNumber/preparation);
              //取余
              let p2 = stockNumber%preparation;
              if(p1 > 0)
              {
                stockText = p1 + packName;
              }
              if(p2 > 0)
              {
                if(p1 > 0)
                {
                  stockText = stockText + p2 + preparationUnit;
                }
                else
                {
                  stockText = p2 + preparationUnit;
                }
              }
            }
            else
            {
              stockText = stockNumber + packName;
            }

            element['stockText'] = stockText;
          });
          this.WesternMedicineTable = responseData.data;
        }
      });
    },
    GetChineseTable() {
      this.SearchChineseModel.params[1].value = "medicalType_1";
      this.SearchChineseModel.params[2].value = this.SearchChineseInput;
      listAllStock(this.SearchChineseModel).then((responseData) => {
        if (responseData.code == 100) {
          this.ChineseMedicineTable = responseData.data;
        }
      });
    },
    //点击待就诊患者开始就诊
    GoVisit(item, index) {
      this.GetClearData();
      this.ClearMedicineCofigure();
      this.IsShowWestern = false
      this.IsShowChinese = false
      this.IsShowInfusion = false
      this.IsShowTreatment = false
      this.NowAlreadyIndex = 5
      this.NowPreIndex = index
      this.BasicInfoModel = item.patientInfo;
      this.BasicInfoModel.treatType = item.treatType.name;
      this.VisitId = item.id;
      this.IsOnlyRead = false;
      this.GetSaveInfo();
    },
    GoAlready(item, index) {
      this.IsShowWestern = false
      this.IsShowChinese = false
      this.IsShowInfusion = false
      this.IsShowTreatment = false
      this.NowPreIndex = 5
      this.NowAlreadyIndex = indexVue
      this.BasicInfoModel = item.patientInfo;
      this.BasicInfoModel.treatType = item.treatType.name;
      this.VisitId = item.id;
      this.IsOnlyRead = true;
      this.IsInvalidPrescription = !(item.status.value === 'registrationStatus_1' && item.chargeStatus === '0');

      this.GetSaveInfo();
    },
    //根据就诊人id查询处方病例
    GetSaveInfo() {
      this.VisitRecordsList = [];
      let model = {
        company: {
          id: this.Company.id,
        },
        doctor: {
          id: this.UserId,
        },
        id: this.VisitId,
        registrationId:''
      };
      allQueryMedicalRecord(this.VisitId).then((responseData) => {
        if (responseData.code == 100) {
          this.GetVisitRecordData(responseData.data);
          if (this.IsOnlyRead == true) this.GetOnlyReadData(responseData.data);
        }
      });
    },
    GetVisitRecordData(allData) {
      this.VisitRecordsList[0] = allData;
      this.VisitRecordsList[0].info = this.BasicInfoModel;
      this.$forceUpdate();
    },
    //调用历史病例
    UseRecord() {
      let NeedData = JSON.parse(JSON.stringify(this.NowVisitInfo));
      if (!this.checkedForm.record) NeedData.medicalRecord = {};
      if (!this.checkedForm.Prescription) NeedData.recipelInfoEvtList = [];
      this.GetOnlyReadData(NeedData);
      this.IsShowRecords = false;
    },
    //得到只读数据
    GetOnlyReadData(allData) {
      this.ClearMedicineCofigure();
      //得到病例和医嘱
      this.DoctorAdvice = allData.medicalRecord.doctorAdvice;
      this.MedicalRecordModel.patientTell = allData.medicalRecord.patientTell;
      this.MedicalRecordModel.nowHistory = allData.medicalRecord.nowHistory;
      this.MedicalRecordModel.beforeHistory =
        allData.medicalRecord.beforeHistory;
      this.MedicalRecordModel.otherCheck = allData.medicalRecord.otherCheck;
      this.MedicalRecordModel.diagnose = allData.medicalRecord.diagnose;
      this.MedicineRecordLabelList.map((item) => {
        item.value = allData.medicalRecord[`${item.name}`];
        if (item.value) this.ChooseRecordList.push(item);
      });
      //得到处方
      allData.recipelInfoEvtList.map((prescriptionItem) => {
        switch (prescriptionItem.recipelInfo.recipelType.name) {
          case "西药处方":
            this.IsShowWestern = true
            this.WesternRemarks = prescriptionItem.recipelInfo.remarks;
            this.WesternFee = prescriptionItem.recipelInfo.fee;
            this.ChooseWesternMedicineTable =
              prescriptionItem.recipelDetailEvtList;
            break;
          case "中药处方":
            this.IsShowChinese = true
            this.ChooseChineseMedicineTable =
              prescriptionItem.recipelDetailEvtList;
            this.ChinesePrescription = {
              recipelType: prescriptionItem.recipelInfo.recipelType,
              dosage: prescriptionItem.recipelInfo.dosage,
              recipelUse: prescriptionItem.recipelInfo.recipelUse,
              frequency: prescriptionItem.recipelInfo.frequency,
              takeFrequency: prescriptionItem.recipelInfo.takeFrequency,
              singleDosage: prescriptionItem.recipelInfo.singleDosage,
              fee: prescriptionItem.recipelInfo.fee,
            };

            break;
          case "输液处方":
            this.IsShowInfusion = true
            this.InfusionItemList = [];
            let GroupArr = [];
            prescriptionItem.recipelDetailEvtList.map((item) => {
              GroupArr.push(item.infuseGroup);
            });
            GroupArr = [...new Set(GroupArr)].sort();
            GroupArr.map((group) => {
              let InfoModel = {
                infuseUse: {},
                frequency: {},
                days: {},
                drippingSpeed: "",
                infuseGroup: group,
                infusionList: [],
              };
              this.InfusionItemList.push(JSON.parse(JSON.stringify(InfoModel)));
              prescriptionItem.recipelDetailEvtList.map((item) => {
                if (group == item.infuseGroup) {
                  this.InfusionItemList[group - 1].infuseUse = item.infuseUse;
                  this.InfusionItemList[group - 1].frequency = item.frequency;
                  this.InfusionItemList[group - 1].days = item.days;
                  this.InfusionItemList[group - 1].drippingSpeed =
                    item.drippingSpeed;
                  this.InfusionItemList[group - 1].infuseGroup = item.group;
                  this.InfusionItemList[group - 1].infusionList.push(item);
                }
              });
            });
            this.InfusionFee = prescriptionItem.recipelInfo.fee;
            this.InfusionRemarks = prescriptionItem.recipelInfo.remarks;
            break;
          case "诊疗项目":
            this.IsShowTreatment = true
            this.CostItemFee = prescriptionItem.recipelInfo.fee;
            this.ChooseTreatmentTable = prescriptionItem.recipelDetailEvtList;
            break;
          case "附加费":
            this.SurchargeFee = prescriptionItem.recipelInfo.fee;
            this.ChooseSurchargeTable = prescriptionItem.recipelDetailEvtList;
            break;
          default:
            break;
        }
      });
    },
    //添加处方并聚焦
    OpenPrescription(name) {
      switch (name) {
        case "WesternInput":
          this.IsShowWestern = !this.IsShowWestern;
          this.$nextTick(() => {
            if (this.IsShowWestern) this.$refs.WesternInput.focus();
          });
          break;
        case "ChineseInput":
          this.IsShowChinese = !this.IsShowChinese;
          this.$nextTick(() => {
            if (this.IsShowChinese) this.$refs.ChineseInput.focus();
          });
          break;
        case "InfusionBtn":
          this.IsShowInfusion = !this.IsShowInfusion;
          this.$nextTick(() => {
            if (this.IsShowInfusion) this.$refs.InfusionBtn.focus();
          });
          break;
        case "TreatmentInput":
          this.IsShowTreatment = !this.IsShowTreatment;
          this.$nextTick(() => {
            if (this.IsShowTreatment) this.$refs.TreatmentInput.focus();
          });
          break;
        default:
          break;
      }
    },
    //病历配置恢复默认
    ClearMedicineCofigure() {
      this.ChooseRecordList.map((item) => {
        item.value = "";
      });
      this.ChooseRecordList = [];
    },
    //打开历史就诊记录弹框
    OpenVisitRecords(row) {
      this.IsShowRecords = true;
      this.NowVisitInfo = row;
    },
    //西药表格行点击选择添加到已选择西药处方的表格
    RowClickWesternTable(row) {
      this.ChooseWesternMedicineTable.push({
        allFee: 0,
        ...JSON.parse(JSON.stringify(row)),
      });
    },
    //点击西药表格后的删除按钮删除指定下标的数据
    DeleteWestern(index, row) {
      this.ChooseWesternMedicineTable.splice(index, 1);
      this.GetWesternFee();
    },
    //中药表格行点击选择添加到已选择中药处方的表格
    RowClickChineseTable(row) {
      let flag = false;
      this.ChooseChineseMedicineTable.forEach(element => {
        if(element.code === row.code)
        {
          flag = true;
        }
      });

      if(!flag)
      {
        this.ChooseChineseMedicineTable.push({
          allFee: 0,
          ...JSON.parse(JSON.stringify(row)),
        });
      }
    },
    //点击中药处方的关闭按钮，删除这条中药处方
    DeleteChinese(index) {
      this.ChooseChineseMedicineTable.splice(index, 1);
    },
    //输液处方加一组
    AddInfusionItem() {
      let InfoModel = {
        infuseUse: {},
        frequency: {},
        days: {},
        drippingSpeed: "",
        infuseGroup: 0,
        infusionList: [],
      };
      this.InfusionItemList.push(JSON.parse(JSON.stringify(InfoModel)));
    },
    //输液处方删除一组
    DeleteInfusionItem(index) {
      this.InfusionItemList.splice(index, 1);
      this.GetInfusionFee();
    },
    //诊疗项目表格行点击选择添加到已选择诊疗项目处方的表格
    RowClickTreatmentTable(row) {
      let flag = false;
      this.ChooseTreatmentTable.forEach(element => {
        if(element.itemCode === row.itemCode)
        {
          flag = true;
        }
      });

      if(!flag)
      {
        this.ChooseTreatmentTable.push({
          allFee: 0,
          ...JSON.parse(JSON.stringify(row)),
        });
      }
    },
    //点击诊疗项目表格后的删除按钮删除指定下标的数据
    DeleteTreatment(index, row) {
      this.ChooseTreatmentTable.splice(index, 1);
      this.GetCostItemFee();
    },
    //附加费表格数据添加
    RowClickSurchargeTable(row) {
      let flag = false;
      this.ChooseSurchargeTable.forEach(element => {
        if(element.code === row.code)
        {
          flag = true;
        }
      });

      if(!flag)
      {
        this.ChooseSurchargeTable.push({
          allFee: 0,
          ...JSON.parse(JSON.stringify(row)),
        });
      }
    },
    //点击附加费表格后的删除按钮删除指定下标的数据
    DeleteSurchargeTable(index, row) {
      this.ChooseSurchargeTable.splice(index, 1);
      this.GetSurchargeFee();
    },
    FinishVisit() {
      let FinishModel = {
        medicalRecord: {},
        recipelInfoEvtList: [],
      };
      //得到最后的病例
      this.ChooseRecordList.map((item) => {
        this.MedicalRecordModel[`${item.name}`] = item.value;
      });
      this.MedicalRecordModel.doctorAdvice = this.DoctorAdvice;
      this.MedicalRecordModel.company = this.Company;
      this.MedicalRecordModel.fee = this.AllTotal;
      this.MedicalRecordModel.registration = {
        id: this.VisitId,
      };
      this.MedicalRecordModel.doctor = {
        id: this.UserId,
      };
      this.MedicalRecordModel.fileId = this.fileIdFile.fileId
      this.MedicalRecordModel.fileIdFile = {
        deletes: this.fileIdFile.deletes,
        uploads: this.fileIdFile.uploads
      }
      FinishModel.medicalRecord = this.MedicalRecordModel;

      let FinishWesternMedicineList = [];
      this.ChooseWesternMedicineTable.map((row) => {
        FinishWesternMedicineList.push({
          company: row.company,
          drugStuffId: {
            drugStuffId: row.id,
            name: row.goodsName ? row.goodsName : row.drugStuffId.name,
            price: row.price ? row.price : row.drugStuffId.price,
            dosisUnit: row.preparationUnit
              ? row.preparationUnit
              : row.drugStuffId.dosisUnit,
            pack: row.pack ? row.pack : row.drugStuffId.pack,
          },
          singleDosage: row.singleDosage,
          total: row.total,
          allFee: row.allFee,
          westernMedicineUse: row.westernMedicineUse,
          frequency: row.frequency,
          days: row.days,
        });
      });
      let WesternPrescription = {
        recipelInfo: {
          company: this.Company,
          recipelType: { name: "西药处方", value: "recipelType_0" },
          isPay: '0',
          remarks: this.WesternRemarks,
          fee: this.WesternFee,
        },
        recipelDetailEvtList: FinishWesternMedicineList,
      };
      if (FinishWesternMedicineList.length)
        FinishModel.recipelInfoEvtList.push(WesternPrescription);

      let FinishChineseMedicineList = [];
      this.ChooseChineseMedicineTable.map((row) => {
        FinishChineseMedicineList.push({
          company: row.company,
          drugStuffId: {
            drugStuffId: row.id,
            name: row.goodsName ? row.goodsName : row.drugStuffId.name,
            price: row.price ? row.price : row.drugStuffId.price,
            dosisUnit: row.dosisUnit
              ? row.dosisUnit
              : row.drugStuffId.dosisUnit,
            pack: row.pack ? row.pack : row.drugStuffId.pack,
          },
          singleDosage: row.singleDosage,
          chineseMedicineUse: row.chineseMedicineUse,
          allFee: row.allFee,
          total: row.total,
        });
      });
      this.ChinesePrescription.company = this.Company;
      let ChinesePrescription = {
        recipelInfo: this.ChinesePrescription,
        recipelDetailEvtList: FinishChineseMedicineList,
      };
      if (FinishChineseMedicineList.length)
        FinishModel.recipelInfoEvtList.push(ChinesePrescription);

      let FinishInfusion = [];
      this.InfusionItemList.map((item) => {
        item.infusionList.map((row) => {
          FinishInfusion.push({
            company: this.Company,
            drugStuffId: {
              drugStuffId: row.id,
              name: row.goodsName ? row.goodsName : row.drugStuffId.name,
              price: row.price ? row.price : row.drugStuffId.price,
              dosisUnit: row.preparationUnit
                ? row.preparationUnit
                : row.drugStuffId.dosisUnit,
              pack: row.pack ? row.pack : row.drugStuffId.pack,
            },
            infuseUse: item.infuseUse,
            frequency: item.frequency,
            days: item.days,
            drippingSpeed: item.drippingSpeed,
            infuseGroup: item.infuseGroup,
            singleDosage: row.singleDosage,
            total: row.total,
            skinTest: row.skinTest,
            allFee: row.allFee,
          });
        });
      });
      let InfusionPrescription = {
        recipelInfo: {
          company: this.Company,
          recipelType: { name: "输液处方", value: "recipelType_2" },
          isPay: '0',
          remarks: this.InfusionRemarks,
          fee: this.InfusionFee,
        },
        recipelDetailEvtList: FinishInfusion,
      };
      if (FinishInfusion.length)
        FinishModel.recipelInfoEvtList.push(InfusionPrescription);

      let FinishCostList = [];
      this.ChooseTreatmentTable.map((item) => {
        FinishCostList.push({
          company: this.Company,
          drugStuffId: {
            drugStuffId: item.id,
            name: item.itemName ? item.itemName : item.drugStuffId.name,
            price: item.salePrice ? item.salePrice : item.drugStuffId.price,
            dosisUnit: item.unit ? item.unit : item.drugStuffId.dosisUnit,
          },
          allFee: item.allFee,
          total: item.total,
        });
      });
      let CostItemPrescription = {
        recipelInfo: {
          company: this.Company,
          recipelType: { name: "诊疗项目", value: "recipelType_3" },
          isPay: '0',
          fee: this.CostItemFee,
        },
        recipelDetailEvtList: FinishCostList,
      };
      if (FinishCostList.length)
        FinishModel.recipelInfoEvtList.push(CostItemPrescription);

      let FinishSurchargeList = [];
      this.ChooseSurchargeTable.map((item) => {
        FinishSurchargeList.push({
          company: item.company,
          drugStuffId: {
            drugStuffId: item.id,
            name: item.name ? item.name : item.drugStuffId.name,
            price: item.priceOutSell
              ? item.priceOutSell
              : item.drugStuffId.price,
          },
          allFee: item.allFee,
          total: item.total,
        });
      });
      let SurchargePrescription = {
        recipelInfo: {
          company: this.Company,
          recipelType: { name: "附加费", value: "recipelType_4" },
          isPay: '0',
          fee: this.SurchargeFee,
        },
        recipelDetailEvtList: FinishSurchargeList,
      };
      if (FinishSurchargeList.length)
        FinishModel.recipelInfoEvtList.push(SurchargePrescription);

      this.$refs.BasicInfoForm.validate((valid) => {
        if (valid) {
          let model = this.createFormData(FinishModel)
          allSaveMedicalRecord(model).then((responseData) => {
            if (responseData.code == 100) {
              this.$message.success('操作成功！')
              this.GetClearData();
              this.GetVisitList();
            }
          });
        }
      });
    },
    invalidPrescription() {
      getRegistrationById(this.VisitId).then((responseData) => {
        if (responseData.code == 100) {
          let status = responseData.data.status.value;               
          let chargeStatus = responseData.data.chargeStatus;   //收费状态 0- 待收费   1-部分收费   2- 已收费   3-  部分退费    4- 已退费
          if(chargeStatus === '0' && status === "registrationStatus_1")
          {
            updateStatus(this.VisitId, "registrationStatus_4").then((responseData) => {
              if (responseData.code == 100) {
                this.GetAlreadyPatientList();
                this.$message.success('操作成功！')
              }
            });
          }
        }
      });
    },
    createFormData(FinishModel) {
        let formData = new FormData()
        let deletes = []
        for(let idx in FinishModel.medicalRecord.fileIdFile.uploads) {
          formData.append('fileIdUploads', FinishModel.medicalRecord.fileIdFile.uploads[idx].raw)
        }
        deletes = deletes.concat(FinishModel.medicalRecord.fileIdFile.deletes)
        formData.append('entity', JSON.stringify(FinishModel))
        formData.append('deleteIds', JSON.stringify(deletes))

        return formData
      },
  },
  mounted() {
    this.Init();
  },
};
</script>
<style lang='scss'>
.outpatient {
  .leftInfo {
    .el-tabs__nav {
      width: 100%;
      .el-tabs__item {
        width: 50%;
        text-align: center;
      }
    }
    .el-dialog__header {
      border-bottom: 1px solid rgb(219, 219, 219);
    }
    .el-dialog__footer {
      border-top: 1px solid rgb(219, 219, 219);
      padding: 10px;
    }
    .el-dialog__body {
      padding: 0;
    }
  }
  .tableStyle {
    .el-input-group__append {
      padding: 0 10px;
    }
  }
}
</style>
<style lang='scss' scoped>
$borderColor: rgb(219, 219, 219); //灰色边框下划线
$fontAndBgColor: #169bd5; //蓝色按钮和字体
.configureBox {
  width: 100%;
  h5 {
    margin: 4px 0;
  }
  .recordTitleBox {
    width: 100%;
    height: 30px;
    display: flex;
    justify-content: space-between;
    border-bottom: 1px solid $borderColor;
  }
  .recordTypeBox {
    width: 100%;
    height: 60px;
    border-bottom: 1px solid $borderColor;
  }
  .recordLabelBox {
    width: 100%;
    .checkLabelBox {
      width: 100%;
      display: flex;
      flex-wrap: wrap;
      .checkItem {
        width: 24%;
      }
    }
  }
}
.outpatient {
  width: 100%;
  height: calc(100vh - 113px);
  background-color: white;
  display: flex;
  .visitRecordBtn {
    background-color: $fontAndBgColor;
    color: white;
  }
  .leftInfo {
    width: 30%;
    border-right: 1px solid $borderColor;
    height: 100%;
    padding-right: 10px;
    .leftTitle {
      width: 100%;
      height: 40px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-bottom: 1px solid $borderColor;
      color: $fontAndBgColor;
      margin-bottom: 10px;
    }
    .searchItemBox {
      display: flex;
      justify-content: space-between;
      color: $fontAndBgColor;
      margin-bottom: 10px;
      line-height: 50px;
    }
    .todayVisitBox {
      width: 100%;
      height: 430px;
      .todayPatientItem {
        position: relative;
        width: 100%;
        height: 80px;
        border-bottom: 1px solid $borderColor;
        padding: 0px 0 4px 0;
        display: flex;
        .patientImg {
          display: none;
          width: 64px;
          height: 64px;
          font-size: 64px;
          margin-top: 4px;
          color: $fontAndBgColor;
        }
        .patientInfo {
          flex-grow: 1;
          margin-left: 5px;
          span {
            color: rgb(87, 208, 146);
          }
          .isNet {
            color: $fontAndBgColor;
          }
          .alreadytype {
            color: $fontAndBgColor;
          }
          .refundBox {
            position: absolute;
            top: -10px;
            right: -50px;
            width: 55px;
            height: 38px;
            border: 2px solid red;
            text-align: center;
            font-size: 18px;
            border-radius: 80%;
            color: red;
            transform: scale(0.5);
          }
        }
        .patientTime {
          width: 100px;
          height: 100%;
          line-height: 140px;
          font-size: 14px;
          text-align: right;
          color: #979797;
        }
        .patientType {
          display: none;
          width: 100px;
          height: 100%;
          line-height: 130px;
          font-size: 14px;
          text-align: right;
          color: $fontAndBgColor;
          margin-right: 10px;
        }
      }
      .todayPatientItem.click :after,.todayPatientItem:hover :after {
        content: "";
        width: 3px;
        height: 100%;
        background-color: rgb(40, 120, 255);
        position: absolute;
        left: 0;
        top: 0;
      }
      .todayPatientItem.click,.todayPatientItem:hover {
        background-color: rgb(204, 236, 248);
        .patientImg {
          display: block;
        }
        .patientTime {
          display: none;
        }
        .patientType {
          display: block;
        }
      }
      .pageBox {
        width: 30%;
        height: 30px;
        float: right;
        display: flex;
        justify-content: space-between;
      }
    }
    .visitRecordsBox {
      margin-top: 10px;
      padding: 4px 8px;
      width: 100%;
      max-height: 200px;
      overflow-x: auto;
      border: 1px solid $borderColor;
      border-radius: 4px;
      list-style: none;
      font-size: 14px;
      li {
        margin: 4px 0;
      }
      .visitRecordTime {
        display: inline-block;
        width: 200px;
      }
      .diagnosisTitel {
        color: $fontAndBgColor;
      }
    }
  }
  .rightInfo {
    width: 70%;
    height: 100%;
    .topInfoBox {
      width: 100%;
      height: calc(100% - 40px);
      overflow-x: auto;
      padding: 10px;
      .infoItem {
        width: 100%;
        border: 1px solid $borderColor;
        border-radius: 4px;
        margin-bottom: 10px;
        .infoTitleBox {
          width: 100%;
          height: 42px;
          background-color: rgb(234, 234, 234);
          border-bottom: 1px solid $borderColor;
          padding: 0 10px;
          display: flex;
          justify-content: space-between;
          h4 {
            margin: 0;
            line-height: 42px;
          }
          .infoTitleRight {
            width: 40px;
            height: 42px;
            display: flex;
            flex-direction: row-reverse;
            justify-content: space-between;
            align-items: center;
          }
        }
        .el-form {
          padding: 10px;
        }
        .itemContent {
          width: 100%;
          padding: 10px;
          .chineseContentCenter {
            width: 100%;
            display: flex;
            flex-wrap: wrap;
            .chineseContentCenterItem {
              position: relative;
              width: 24%;
              height: 60px;
              margin: 10px 1% 0 0;
              .closeBtn {
                position: absolute;
                right: 10px;
                top: 10px;
                font-size: 14px;
                font-weight: bolder;
                color: #414141;
              }
            }
          }
          .InfusionContentItem {
            width: 100%;
            min-height: 140px;
            border: 1px solid $borderColor;
            position: relative;
            .closeBtn {
              position: absolute;
              right: 10px;
              top: 10px;
              font-size: 14px;
              font-weight: bolder;
              color: #414141;
            }
          }
          .itemBottomBox {
            width: 100%;
            height: 30px;
            display: flex;
            flex-direction: row-reverse;
            justify-content: space-between;
            align-items: center;
            padding: 0;
            margin-bottom: 0;
            margin-top: 10px;
            p {
              color: #5c5a5a;
            }
          }
        }
      }
      .addPrescriptionBox {
        width: 100%;
        height: 42px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        line-height: 42px;
        h4 {
          position: relative;
          padding-left: 10px;
        }
        .titleSign {
          position: absolute;
          display: inline-block;
          width: 5px;
          height: 24px;
          left: 0;
          top: 6px;
          background-color: $fontAndBgColor;
        }
      }
    }
    .bottomInfoBox {
      width: 100%;
      height: 40px;
      padding: 10px;
      border-top: 1px solid $borderColor;
      display: flex;
      justify-content: space-between;
      align-items: center;
      h5 {
        color: $fontAndBgColor;
      }
      .btnGroup {
        width: 210px;
        height: 40px;
        padding: 4px;
        display: flex;
        flex-direction: row-reverse;
        justify-content: space-between;
      }
    }
  }
}
</style>
