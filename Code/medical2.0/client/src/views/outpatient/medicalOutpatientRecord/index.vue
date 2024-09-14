<template>
  <el-container class="changeMedicalOutPatient">
    <el-aside width="360px">
      <el-card class="box-card" shadow="always" body-style="{width: '100%',height: '100%'}">
        <div slot="header" class="aside-header">
          <span>今日就诊</span>
        </div>
        <el-row>
          <el-input placeholder="请输入患者姓名/联系方式/身份证号" v-model="SearchPatientInfo" class="input-with-select">
            <!-- <el-select v-model="select" slot="prepend" placeholder="请选择搜索方式" style="width:100px;">
                            <el-option label="患者姓名" value="1"></el-option>
                            <el-option label="联系方式" value="2"></el-option>
                            <el-option label="身份证号" value="3"></el-option>
                            <el-option label="健康卡号" value="4"></el-option>
                        </el-select> -->
            <el-button style="width: 100px" slot="append" icon="el-icon-search" @click="handlePatientClick">
              搜索
            </el-button>
          </el-input>
        </el-row>
        <!-- <el-row>
                    <el-button style="width:100%;">快速接诊</el-button>
                </el-row> -->
        <el-row>
          <el-tabs v-model="TodayActiveName" @tab-click="handlePatientClick" stretch="true">
            <el-tab-pane name="prepare">
							<span slot="label">
								待就诊
								<el-badge :value="PreparePatientTotal" :max="99" class="mark"
                          style="line-height: normal"></el-badge>
							</span>
              <el-scrollbar view-style="height:calc(100vh - 308px);padding-top: 15px;">
                <div v-for="(item, index) in PreparePatientList" :key="item"
                     @click="clickPreparePatient(item, index)"
                     :class="{ click: index == backgroundColors }">
                  <p style="
                      font-weight: bold;
                      font-size: 12px;
                      color: #606266;
                      margin-left: 10px;
                      margin-top: 10px;
                      padding-top: 6px;
                    ">
										<span style="margin-top: 10px">
											{{ index + 1 }}. {{ item.patientId.name }} /
											{{ item.patientId.gender.name }} /
											{{ item.patientId.birthdayText }}岁
										</span>
                  </p>
                  <p style="
                      font-weight: bold;
                      font-size: 12px;
                      color: #606266;
                      flex: 1;
                      margin-left: 10px;
                    ">
                    {{ item.doctor.name }} /
                    <span style="color: #018cb7">{{ item.source.name }}</span>
                  </p>
                  <p style="
                      margin-top: -40px;
                      margin-left: 250px;
                      font-weight: bold;
                      font-size: 12px;
                      color: #606266;
                      flex: 1;
                    ">
                    {{ item.formatUpdateDate }}
                  </p>
                  <!-- <el-descriptions :column="2" :colon="false">
                    <el-descriptions-item
                      labelStyle="margin-right: 0px;"
                      contentStyle="font-weight: bold;"
                      >{{ index + 1 }}. {{ item.patientId.name }} /
                      {{ item.patientId.gender.name }} /
                      {{ item.patientId.birthdayText }}岁</el-descriptions-item
                    >
                    <el-descriptions-item  ></el-descriptions-item>
                    <el-descriptions-item labelStyle="margin-right: 0px;"
                      >{{ item.doctor.name }} /
                      <span style="color: #018cb7">{{
                        item.source.name
                      }}</span></el-descriptions-item
                    >
                    <el-descriptions-item  contentStyle="margin-left: 90px;">{{
                      item.formatUpdateDate
                    }}</el-descriptions-item>

                  </el-descriptions> -->
                  <el-divider></el-divider>
                  <!-- </div> -->
                </div>
              </el-scrollbar>
              <el-pagination small background @current-change="handleCurrentPreparePatientChange"
                             :page-size="SearchPreModel.limit" layout="prev, pager, next"
                             :total="PreparePatientTotal">
              </el-pagination>
            </el-tab-pane>
            <el-tab-pane name="already">
							<span slot="label">
								已就诊
								<el-badge :value="AlreadyPatientListTotal" :max="99" type="primary"
                          style="line-height: normal"></el-badge>
							</span>
              <el-scrollbar view-style="height:calc(100vh - 293px);">
                <el-collapse v-model="activeSelAlreadyPatientItem" accordion
                             @change="AlreadyPatientDeletilQuery">
                  <div v-for="(item, index) in AlreadyPatientList" :key="item">
                    <div class="test">
                      <el-collapse-item :class="{ alreadyMedocal: item.id == clickChangeColor }"
                                        :name="item">
                        <template slot="title">
													<span style="
                              font-weight: bold;
                              font-size: 12px;
                              color: #606266;
                              flex: 1;
                            ">&nbsp;&nbsp;&nbsp;&nbsp;{{ index + 1 }}.
														{{ item.patientId.name }} /
														{{ item.patientId.gender.name }} /
														{{ item.patientId.birthdayText }}岁</span>
                          <span style="font-size: 12px; color: #606266">{{
                              item.formatReceptionEndDate
                            }}</span>
                        </template>
                        <div v-for="(o, num) in AlreadyPatientDescriptions" :key="num"
                             @click="AlreadyPatientDescriptionsQuery(o, num)"
                             :class="{ clickTest: num == overColor }">
                          <div style="
                              border-top: 1px dashed #dcdfe6;
                              margin: 0px 30px 0 15px;
                            "></div>
                          <p style="
                              font-weight: bold;
                              margin-top: 15px;
                              margin-left: 30px;
                              font-size: 12px;
                              color: #606266;
                            ">
                            {{ o.name }}
                          </p>

                          <p style="
                              font-weight: bold;
                              margin-top: 6px;
                              margin-left: 30px;
                              font-size: 12px;
                              color: #606266;
                            ">
                            {{ o.code }}
                            <span style="position: relative">
                          <div v-if="o.status === -1" class="refundBox">
                            已作废
                          </div>
                          <div v-if="o.status === 1 && o.chargeStatus === 0"
                               class="refundBox">
                            未收费
                          </div>
                          <div v-if="o.status === 1 && o.chargeStatus === 1"
                               class="refundBox">
                            已收费
                          </div>
                          <div v-if="o.status === 1 && o.chargeStatus === -1"
                               class="refundBox">
                            已退费
                          </div>
                          </span>
                          </p>
                          <div style="
                              border-top: 1px dashed #dcdfe6;
                              margin: 0px 30px 0 15px;
                            "></div>
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
                            >{{ o.name }}</el-descriptions-item
                          >
                          <el-descriptions-item
                            >{{ o.code }}
                            <span style="position: relative">
                              <div v-if="o.status === -1" class="refundBox">
                                已作废
                              </div>
                              <div
                                v-if="o.status === 1 && o.chargeStatus === 0"
                                class="refundBox"
                              >
                                未收费
                              </div>
                              <div
                                v-if="o.status === 1 && o.chargeStatus === 1"
                                class="refundBox"
                              >
                                已收费
                              </div>
                              <div
                                v-if="o.status === 1 && o.chargeStatus === -1"
                                class="refundBox"
                              >
                                已退费
                              </div>
                            </span>
                          </el-descriptions-item>
                        </el-descriptions> -->
                        </div>
                      </el-collapse-item>
                    </div>
                  </div>
                </el-collapse>
              </el-scrollbar>
              <el-pagination small background @current-change="handleCurrentAlreadyPatientChange"
                             :page-size="SearchAlreadyModel.limit" layout="prev, pager, next"
                             :total="AlreadyPatientListTotal">
              </el-pagination>
            </el-tab-pane>
          </el-tabs>
        </el-row>
      </el-card>
    </el-aside>
    <el-main>
      <el-card class="box-card" shadow="always">
        <el-scrollbar view-style="height:calc(100vh - 210px);padding-right: 20px;">
          <el-card class="box-card main-card">
            <div slot="header" style="padding: 5px 0px; font-size: 14px; font-weight: bold">
              <span>基础信息</span>
              <!-- <el-button v-if="isReadOnly==false" style="float: right; padding: 3px 0" type="text">清空</el-button> -->
            </div>
            <el-form :model="BasicInfoModel" ref="BasicInfoForm" label-width="auto"
                     :rules="BasicInfoFormRules">
              <el-row :gutter="24">
                <el-col :span="4">
                  <el-form-item label="患者姓名:" prop="name">
                    <!-- <el-input
                      :disabled="isBaseReadOnly"
                      v-model="BasicInfoModel.name"
                      style="width:100px"
                    ></el-input> -->
                    <div class="nameStyle">
                      {{ BasicInfoModel.name ? BasicInfoModel.name : "" }}
                      <div class="imgStyle" v-if="member.length > 0">
                        <el-popover placement="top-start" title="" width="200" trigger="hover">
                          <div v-for="(item, index) in member" :key="index"
                               style="padding: 5px">
                            <el-tag>{{ item.memberName }}</el-tag>
                          </div>
                          <img slot="reference" src="../../../assets/images/vip.png"
                               style="width: 25px; height: 25px"/>
                        </el-popover>
                      </div>
                    </div>
                    <div class="nameStyle" style="color:#ff0000" v-if="poverty.length > 0"> [贫]
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="3">
                  <el-form-item label="性别:" prop="gender">
                    <!-- <el-select
                      :disabled="isBaseReadOnly"
                      v-model="BasicInfoModel.gender.name"
                      placeholder="请选择性别"
                      style="width: 100%"
                    >
                      <el-option label="男" value="男"></el-option>
                      <el-option label="女" value="女"></el-option>
                    </el-select> -->
                    <span>
											{{
                        BasicInfoModel.gender.name
                          ? BasicInfoModel.gender.name
                          : ""
                      }}
										</span>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="年龄:" prop="name">
                    <!-- <el-input
                      :disabled="isBaseReadOnly"
                      placeholder="请输入"
                      v-model="BasicInfoModel.age"
                      style="width: 49%"
                    >
                      <template slot="append">岁</template>
                    </el-input>
                    <el-input
                      :disabled="isBaseReadOnly"
                      placeholder="请输入"
                      v-model="BasicInfoModel.month"
                      style="width: 49%"
                    >
                      <template slot="append">月</template>
                    </el-input> -->
                    <span>
											{{ BasicInfoModel.age ? BasicInfoModel.age : 0 }}岁{{
                        BasicInfoModel.month ? BasicInfoModel.month : 0
                      }}月
										</span>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="联系方式:" prop="phone">
                    <!-- <el-input
                      :disabled="isBaseReadOnly"
                      v-model="BasicInfoModel.phone"
                    ></el-input> -->
                    <span>
											{{ BasicInfoModel.phone ? BasicInfoModel.phone : "" }}
										</span>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="身份证号:" prop="card">
                    <!-- <el-input
                      :disabled="isBaseReadOnly"
                      v-model="BasicInfoModel.card"
                    ></el-input> -->
                    <span>
											{{ BasicInfoModel.card ? BasicInfoModel.card : "" }}
										</span>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="24">
                <!-- <el-form-item label="治疗类型" prop="treatType">
                                        <el-radio-group v-model="MedicalRecordModel.registration.treatType">
                                            <el-radio :disabled="isReadOnly==true" v-for="typeItem in TreatTypeOption" :key="typeItem" :label="typeItem.name"></el-radio>
                                        </el-radio-group>
                                    </el-form-item> -->
                <el-col :span="6">
                  <el-form-item label="治疗类型:">
                    <el-radio :disabled="isReadOnly" v-model="registration.treatType.value"
                              label="treatType_0">初诊
                    </el-radio>
                    <el-radio :disabled="isReadOnly" v-model="registration.treatType.value"
                              label="treatType_1">复诊
                    </el-radio>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="传染病:" prop="infectType">
                    <el-radio :disabled="isReadOnly" v-model="registration.infectType.value" label="infectType_0">否
                    </el-radio>
                    <el-radio :disabled="isReadOnly" v-model="registration.infectType.value" label="infectType_1">是
                    </el-radio>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="发病时间:" style="margin-right: 3px;">
                    <el-date-picker
                      :disabled="isReadOnly"
                      v-model="registration.morbidityTime"
                      type="datetime"
                      placeholder="选择日期时间"
                      align="right"
                      :picker-options="pickerOptions">
                    </el-date-picker>
                  </el-form-item>
                </el-col>

                <el-col :span="2">
                  <el-button @click="jzjlclick" type="primary" style="margin-left: 16px">就诊记录
                  </el-button>
                  <el-drawer title="就诊日志" :visible.sync="drawer" :direction="direction"
                             :before-close="JZrecordclick">
                    <el-table :data="recordtableData" border style="width: 100%">
                      <el-table-column fixed prop="jzsj" label="日期" :show-overflow-tooltip="true"
                                       width="140">
                      </el-table-column>
                      <el-table-column prop="jbxx" :show-overflow-tooltip="true" label="基本信息">
                      </el-table-column>
                      <el-table-column fixed="right" label="操作" width="60">
                        <template slot-scope="scope">
                          <el-button @click="jzjlhandleClick(scope.row)" type="text"
                                     size="small">查看
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-drawer>
                </el-col>
              </el-row>
            </el-form>
          </el-card>

          <el-card class="box-card main-card">
            <div slot="header" style="padding: 5px 0px; font-size: 14px; font-weight: bold">
              <span>病历</span>
              <div style="float: right; margin-top: -3px; display: flex">
                <!-- <el-upload
                                    class="upload-demo"
                                    ref="upload"
                                    action="https://jsonplaceholder.typicode.com/posts/"
                                    :on-preview="handleFilePreview"
                                    :on-remove="handleFileRemove"
                                    :on-progress="handleFileProgress"
                                    :on-change="handleFileBeforeUpload"
                                    :file-list="fileList"
                                    :show-file-list="true"
                                    :auto-upload="false">
                                    <el-button type="text" slot="trigger"><i class="el-icon-upload2 el-icon--right"></i>上传附件</el-button>
                                </el-upload> -->

                <!-- <el-button type="text"><i class="el-icon-upload2 el-icon--right"></i>上传附件</el-button> -->
                <div>
                  <!-- <el-button type="text"><i class="el-icon-document el-icon--right"></i>模板调用</el-button>
                                    <el-button type="text"><i class="el-icon-document-add el-icon--right"></i>存为模板</el-button> -->
                  <el-button v-show="blmbVisit" @click="saveblmbclick" type="primary"
                             style="margin-left: 16px">保存模板
                  </el-button>
                  <el-button v-show="blmbVisit" @click="blmbclick" type="primary"
                             style="margin-left: 16px">病历模板
                  </el-button>
                  <el-button v-show="blmbVisit" @click="HistoricalCases" type="primary"
                             style="margin-left: 16px">历史病历
                  </el-button>

                  <el-popover placement="bottom-end" width="400" popper-class="medical-edit-popover"
                              trigger="click">
                    <el-card class="box-card">
                      <div slot="header" class="clearfix">
                        <span>病历设置</span>
                        <el-button style="float: right; padding: 3px 0" type="text"
                                   @click="ClearMedicineCofigure()">恢复默认
                        </el-button>
                      </div>
                      <div>
                        <el-row>选择需要的病历字段</el-row>
                        <el-checkbox-group v-model="ChooseRecordList">
                          <el-checkbox :disabled="isReadOnly"
                                       v-for="item in MedicineRecordLabelList" :label="item"
                                       :key="item">{{ item.label }}
                          </el-checkbox>
                        </el-checkbox-group>
                      </div>
                    </el-card>
                    <el-button :disabled="isReadOnly" slot="reference" type="text"><i
                      class="el-icon-setting el-icon--right"></i>配置
                    </el-button>
                  </el-popover>
                </div>
              </div>
            </div>
            <el-form :disabled="isReadOnly" :model="MedicalRecordModel" ref="BasicInfoForm"
                     :rules="BasicInfoFormRules" label-width="auto" label-suffix="：">
              <el-row :gutter="24">
                <el-col :span="24">
                  <el-form-item label="主诉" prop="patientTell">
                    <el-input v-model="MedicalRecordModel.patientTell"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="24">
                <el-col :span="6">
                  <el-form-item label="体温" prop="">
                    <div class="ipt-box">
                      <el-input :maxlength="45" :disabled="isReadOnly"
                                v-model="BasicInfoModel.temperature" @input="refresh"></el-input>
                      <span>&nbsp;℃</span>
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="脉搏" prop="">
                    <div class="ipt-box">
                      <el-input :maxlength="45" :disabled="isReadOnly"
                                v-model="BasicInfoModel.pulse" @input="refresh"></el-input>
                      <span>&nbsp;次/min</span>
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="呼吸" prop="">
                    <div class="ipt-box">
                      <el-input :maxlength="45" :disabled="isReadOnly"
                                v-model="BasicInfoModel.breathe" @input="refresh"></el-input>
                      <span>&nbsp;次/min</span>
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="血压" prop="">
                    <div class="ipt-box">
                      <el-input :maxlength="45" :disabled="isReadOnly"
                                v-model="BasicInfoModel.bloodPressure" @input="refresh"></el-input>
                      <span>&nbsp;mmHg</span>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="24" v-for="(item, index) in ChooseRecordList" :key="index">
                <el-col :span="24">
                  <el-form-item :label="item.label" :prop="item.name">
                    <el-input v-model="MedicalRecordModel[`${item.name}`]"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="24">
                <el-col :span="24">
                  <el-form-item label="西医诊断" prop="westernDiagnose">
                    <el-input v-model="MedicalRecordModel.westernDiagnose"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="24">
                <el-col :span="24">
                  <el-form-item label="中医诊断" prop="chinaDiagnose">
                    <el-input v-model="MedicalRecordModel.chinaDiagnose"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <!--<el-row :gutter="24">
                <el-col :span="24">
                  <el-form-item label="医嘱事项" prop="doctorAdvice">
                    <el-input v-model="MedicalRecordModel.doctorAdvice"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>-->
              <el-row :gutter="24">
                <el-col :span="24">
                  <upload-file :objectId="MedicalFileObjId" ref="medicalFile" @getFileList="getFileList"
                               :action="MedicalFlags" @beforeRemove="beforeRemove"
                               @deleteFile="deleteFile"></upload-file>
                </el-col>
              </el-row>
            </el-form>
          </el-card>

          <el-card class="box-card main-card">
            <div slot="header" style="padding: 5px 0px; font-size: 14px; font-weight: bold">
              <span>开具处方</span>
            </div>
            <div style="min-height: 400px">
              <el-tabs v-model="medicalEditTabsValue" type="card" @tab-remove="removeMedicalEditTab"
                       @tab-click="clickMedicalEditTab">
                <el-tab-pane :closable="false" v-if="!isReadOnly" disabled key="add" name="add">
									<span slot="label">
										<el-popover placement="bottom-start" popper-class="medical-type-popover"
                                trigger="click">
											<el-button type="text" slot="reference"><i
                        class="el-icon-plus el-icon--right"></i>开处方</el-button>
											<ul class="medical-type-ul">
												<li v-for="(item, index) in medicalTypeList" :key="item">
													<el-button @click="addMedicalEditTab(item)" plain size="mini"
                                     style="border: none">{{ item.name }}</el-button>
												</li>
											</ul>
										</el-popover>
									</span>
                </el-tab-pane>
                <el-tab-pane v-for="(item, index) in medicalEditTabs" :key="item" :label="item.title"
                             :name="item" :closable="item.closable">
									<span slot="label">
										<!-- <el-checkbox :disabled="isReadOnly"></el-checkbox>  -->
										{{ item.title }}
									</span>
                  <!-- 西药处方 -->
                  <div v-if="
                      item.type === 'recipelType_0' ||
                      item.type === 'recipelType_5'
                    " style="margin-top: 10px">
                    <el-row>
                      <el-divider content-position="left">处方信息</el-divider>
                      <div v-if="item.type === 'recipelType_0' || item.type === 'recipelType_2'">
                        <el-select
                          :disabled="
                          isReadOnly ||
                          item.content.recipelInfo.chargeStatus != 0 ||
                          item.content.recipelInfo.status == -1
                        " v-model="item.content.recipelInfo.smallType" placeholder="请选择处方分类" style="width: 100px">
                          <el-option v-for="smallTypeItem in RecipelSmallTypeList"
                                     :key="smallTypeItem.value" :label="smallTypeItem.name" :value="{
                            name: smallTypeItem.name,
                            value: smallTypeItem.value,
                          }">
                          </el-option>
                        </el-select>
                      </div>
                      <el-popover placement="top-start" v-if="
                          !isReadOnly &&
                          item.content.recipelInfo.chargeStatus == 0 &&
                          item.content.recipelInfo.status != -1
                        " width="700" trigger="focus">
                        <el-table :data="WesternMedicineTable" :height="300" border
                                  highlight-current-row @row-click="RowClickWesternTable">
                          <el-table-column prop="drug.type.name" label="药品类型">
                          </el-table-column>
                          <el-table-column prop="drug.goodsName" label="药品名称">
                          </el-table-column>
                          <el-table-column prop="gg" label="规格" width="120">
                            <template slot-scope="scope">
                              {{
                                scope.row.drug.dosis
                              }}{{ scope.row.drug.dosisUnit.name }} *
                              {{
                                scope.row.drug.preparation
                              }}{{ scope.row.drug.preparationUnit.name }}/{{
                                scope.row.drug.pack.name
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column prop="drug.factory.name" label="厂家" width="100">
                          </el-table-column>
                          <el-table-column label="销售价" width="80">
                            <template slot-scope="scope">
                              {{
                                (scope.row.drug.price).toFixed(2) +
                                "/" +
                                scope.row.drug.pack.name
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column label="拆零价" width="80">
                            <template slot-scope="scope">
                              {{
                                scope.row.drug.isUnpackSell == 1
                                  ? (scope.row.drug.retailPrice).toFixed(4) +
                                  "/" +
                                  scope.row.drug.preparationUnit.name
                                  : "--"
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column prop="surplusStock" label="可用库存" width="100">
                            <template slot-scope="scope">
                              {{
                                Math.floor(
                                  scope.row.surplusStock /
                                  scope.row.drug.preparation
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
                        <el-input prefix-icon="el-icon-plus" suffix-icon="el-icon-search"
                                  style="width: 30%" slot="reference" ref="WesternInput"
                                  v-model="SearchWesternInput" @input="GetWesternTable"
                                  @focus="GetWesternTable" placeholder="输入药品名称或拼音码"></el-input>

                      </el-popover>
                      <el-checkbox style="margin-left: 20px;" v-model="item.content.recipelInfo.chronicDisease"
                                   :disabled="isReadOnly ||
                                   item.content.recipelInfo.chargeStatus != 0 ||
                                   item.content.recipelInfo.status == -1">
                        是否慢病{{
                          item.content.recipelInfo.chronicDisease ? "(慢病处方最多可开" + systemParamConfig.chronicDays + "天)" : "(非慢病处方最多可开" + systemParamConfig.normalDays + "天)"
                        }}
                      </el-checkbox>
                      <el-button type="primary" v-if="!isReadOnly" style="float: right" plain
                                 @click="historyRecipel(item.type, index)">历史处方
                      </el-button>
                      <span style="float: right">&nbsp;&nbsp;&nbsp;</span>
                      <el-button type="primary" v-if="!isReadOnly"
                                 style="float: right; margin-left: 120px" plain
                                 @click="templateRecipel(item.type, index)">模板处方
                      </el-button>
                      <!-- <span style="float:right;">&nbsp;</span> -->
                      <el-button type="primary" v-if="!isReadOnly"
                                 style="float: right; margin-right: -105px" plain
                                 @click="saveTemplate(item.type, index)">存为模板
                      </el-button>
                    </el-row>
                    <el-row>
                      <el-table :data="
                          getDataFilterTable(
                            item.content.recipelDetailEvtList,
                            0,
                            item
                          )
                        " border style="width: 100%" class="tableStyle" @cell-click="checkInventory">
                        <el-table-column type="index" label="序号" align="center">
                        </el-table-column>
                        <el-table-column prop="drugStuffId" label="药品名称" align="center">
                          <template slot-scope="scope">
                            {{ scope.row.drugStuffId.name }}
                            <span style="color: forestgreen">
															{{
                                scope.row.drugStuffId.drug.dosis +
                                scope.row.drugStuffId.drug.dosisUnit.name +
                                "*" +
                                scope.row.drugStuffId.drug.preparation +
                                scope.row.drugStuffId.drug.preparationUnit
                                  .name +
                                "/" +
                                scope.row.drugStuffId.drug.pack.name
                              }}
														</span>
                          </template>
                        </el-table-column>
                        <!-- <el-table-column

                          prop="drugStuffId"
                          label="材料名称"
                          align="center"
                        >
                          <template slot-scope="scope">
                            {{ scope.row.drugStuffId.name }}
                            <span style="color: forestgreen">
                              {{
                                scope.row.drugStuffId.stuff.packNumber +
                                scope.row.drugStuffId.stuff.minUnit.name +
                                "/" +

                                scope.row.drugStuffId.stuff.packUnit.name
                              }}
                            </span>
                          </template>
                        </el-table-column> -->
                        <el-table-column prop="singleDosage" label="单次用量" align="center"
                        >
                          <template slot-scope="scope">
                            <el-input v-model="scope.row.singleDosage"
                                      ref="westernMedicine"
                                      oninput="value=value.replace(/[^\d.]/g,'')"
                                      @input="MedicalCalculate()" :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1"
                            >
                              <template slot="append">{{
                                  scope.row.drugStuffId.dosisUnit.name
                                }}
                              </template>
                            </el-input>
                          </template>
                        </el-table-column>
                        <el-table-column prop="westernMedicineUse" label="用法" align="center"
                        >
                          <template slot-scope="scope">
                            <el-select v-model="scope.row.westernMedicineUse" :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              " placeholder="" @change="changeUse(scope.row.westernMedicineUse)">
                              <el-option v-for="item in WesternUseOption"
                                         :key="item.value" :label="item.name"
                                         :value="{ name: item.name, value: item.value }">
                              </el-option>
                            </el-select>
                          </template>
                        </el-table-column>
                        <el-table-column prop="frequency" label="频次" align="center">
                          <template slot-scope="scope">
                            <el-select v-model="scope.row.frequency" :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              " @change="MedicalCalculate()" placeholder="">
                              <el-option v-for="item in FrequencyOption" :key="item.value"
                                         :label="item.name" :value="{
                                  name: item.name,
                                  value: item.value,
                                }">
                              </el-option>
                            </el-select>
                          </template>
                        </el-table-column>
                        <el-table-column prop="days" label="天数" align="center">
                          <template slot-scope="scope">
                            <el-input-number v-model="scope.row.days.name" :min='0'
                                             :max="item.content.recipelInfo.chronicDisease? systemParamConfig.chronicDays: systemParamConfig.normalDays"
                                             :disabled="isReadOnly || item.content.recipelInfo.chargeStatus != 0 || item.content.recipelInfo.status == -1"
                                             :controls="false" @change="onChronicDiseaseChange">
                            </el-input-number>
                            <!-- <el-select v-model="scope.row.days" :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              " @change="MedicalCalculate()" placeholder="">
                              <el-option v-for="item in DayNumOption" :key="item.value"
                                         :label="item.name"
                                         :value="{ name: item.name, value: item.value }">
                              </el-option>
                            </el-select> -->
                          </template>
                        </el-table-column>
                        <el-table-column prop="total" label="总量" align="center">
                          <template slot-scope="scope">
                            {{
                              Math.floor(scope.row.total / scope.row.drugStuffId.drug.preparation) > 0
                                ? Math.floor(scope.row.total / scope.row.drugStuffId.drug.preparation) +
                                scope.row.drugStuffId.pack.name +
                                (scope.row.total % scope.row.drugStuffId.drug.preparation > 0
                                  ? (scope.row.total % scope.row.drugStuffId.drug.preparation) +
                                  scope.row.drugStuffId.preparationUnit.name
                                  : "")
                                : scope.row.total +
                                scope.row.drugStuffId.preparationUnit.name
                            }}
                          </template>
                        </el-table-column>
                        <!-- <el-table-column

                          prop="total"
                          label="总量"
                          align="center"
                          width="100"
                        >
                          <template slot-scope="scope">
                            {{
                               Math.floor(
                                  scope.row.total /
                                    scope.row.drugStuffId.stuff.packNumber
                                ) > 0
                                  ? Math.floor(
                                      scope.row.total /
                                        scope.row.drugStuffId.stuff.packNumber
                                    ) +
                                    scope.row.drugStuffId.pack.name +
                                    (scope.row.total %
                                      scope.row.drugStuffId.stuff.packNumber >
                                    0
                                      ? (scope.row.total %
                                          scope.row.drugStuffId.stuff
                                            .packNumber) +
                                        scope.row.drugStuffId.preparationUnit.name
                                      : "")
                                  : scope.row.total +
                                    scope.row.drugStuffId.preparationUnit.name
                                      }}
                          </template>
                        </el-table-column> -->
                        <el-table-column prop="isUnpackSell" label="单价"
                                         align="center">
                          <template slot-scope="scope">
                            <el-select v-model="scope.row.isUnpackSell" :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              " placeholder="请选择" @change="MedicalCalculate()" style="width: 110px">
                              <el-option :label="
                                  (scope.row.drugStuffId.price).toFixed(4) +
                                  '/' +
                                  scope.row.drugStuffId.pack.name
                                " :value="0"></el-option>
                              <el-option :label="
                                  (scope.row.drugStuffId.retailPrice).toFixed(4) +
                                  '/' +
                                  scope.row.drugStuffId.preparationUnit.name
                                " :value="1"></el-option>
                            </el-select>
                          </template>
                        </el-table-column>

                        <el-table-column prop="allFee" label="金额" align="center">
                        </el-table-column>
                        <el-table-column v-if="
                            !isReadOnly &&
                            item.content.recipelInfo.chargeStatus == 0 &&
                            item.content.recipelInfo.status != -1
                          " label="操作" fixed="right" align="center" width="80">
                          <template slot-scope="scope">
                            <i class="el-icon-circle-close"
                               @click="DeleteMedicalRow(scope.$index, scope.row)"></i>
                            <span style="display: none">{{ changeData }}</span>
                          </template>
                        </el-table-column>
                      </el-table>
                    </el-row>
                  </div>
                  <!-- 中药处方 -->
                  <div v-if="item.type === 'recipelType_1'" style="margin-top: 15px">
                    <el-row>
                      <el-divider content-position="left">处方信息</el-divider>
                      <!--<el-select :disabled="
                          isReadOnly ||
                          item.content.recipelInfo.chargeStatus != 0 ||
                          item.content.recipelInfo.status == -1
                        " v-model="item.content.recipelInfo.smallType" placeholder="请选择处方分类" style="width: 100px">
                        <el-option v-for="smallTypeItem in RecipelSmallTypeList"
                                   :key="smallTypeItem.value" :label="smallTypeItem.name" :value="{
                            name: smallTypeItem.name,
                            value: smallTypeItem.value,
                          }">
                        </el-option>
                      </el-select>-->

                      <el-popover placement="top-start" v-if="
                          !isReadOnly &&
                          item.content.recipelInfo.chargeStatus == 0 &&
                          item.content.recipelInfo.status != -1
                        " width="700" trigger="focus">
                        <el-table :data="ChineseMedicineTable" :height="300" border
                                  highlight-current-row @row-click="RowClickChineseTable">
                          <el-table-column prop="drug.type.name" label="药品类型">
                          </el-table-column>
                          <el-table-column prop="drug.goodsName" label="药品名称">
                          </el-table-column>
                          <el-table-column prop="gg" label="规格" width="120">
                            <template slot-scope="scope">
                              {{
                                scope.row.drug.dosis
                              }}{{ scope.row.drug.dosisUnit.name }} *
                              {{
                                scope.row.drug.preparation
                              }}{{ scope.row.drug.preparationUnit.name }}/{{
                                scope.row.drug.pack.name
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column prop="drug.factory.name" label="厂家" width="100">
                          </el-table-column>
                          <el-table-column label="销售价" width="80">
                            <template slot-scope="scope">
                              {{
                                (scope.row.drug.price).toFixed(4) +
                                "/" +
                                scope.row.drug.pack.name
                              }}
                            </template>
                          </el-table-column>
                          <!--
                                                    <el-table-column label="拆零价" width="80">
                                                        <template slot-scope="scope">
                                                            {{scope.row.isUnpackSell === "1" ? (scope.row.retailPrice + "/" + scope.row.preparationUnit.name) : "--"}}
                                                        </template>
                                                    </el-table-column>
                                                    -->
                          <el-table-column prop="surplusStock" label="库存" width="100">
                            <template slot-scope="scope">
                              {{
                                Math.floor(
                                  scope.row.surplusStock /
                                  scope.row.drug.preparation
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
                        <el-input prefix-icon="el-icon-plus" suffix-icon="el-icon-search"
                                  style="width: 30%" slot="reference" ref="WesternInput"
                                  v-model="SearchChineseInput" @input="GetChineseTable"
                                  @focus="GetChineseTable" placeholder="输入药品名称或拼音码"></el-input>
                      </el-popover>
                      <el-button type="primary" v-if="!isReadOnly" style="float: right" plain
                                 @click="historyRecipel(item.type, index)">历史处方
                      </el-button>
                      <span style="float: right">&nbsp;&nbsp;&nbsp;</span>
                      <el-button type="primary" v-if="!isReadOnly"
                                 style="float: right; margin-left: 120px" plain
                                 @click="templateRecipel(item.type, index)">模板处方
                      </el-button>
                      <!-- <span style="float:right;">&nbsp;&nbsp;&nbsp;</span> -->
                      <el-button type="primary" v-if="!isReadOnly"
                                 style="float: right; margin-right: -105px" plain
                                 @click="saveTemplate(item.type, index)">存为模板
                      </el-button>
                    </el-row>
                    <el-row :gutter="24" style="min-height: 100px; display: flex; flex-wrap: wrap">
                      <el-col :span="6" v-for="(citem, index) in getDataFilterTable(
                          item.content.recipelDetailEvtList,
                          0,
                          item
                        )" :key="citem" :offset="0">
                        <el-card :index="index" class="box-card chinese-medicine-card"
                                 style="width: 240px; margin-bottom: 10px">
                          <div slot="header" class="clearfix"
                               style="padding: 5px 0px; font-size: 14px">
                            <span>{{ citem.drugStuffId.name }}</span>
                            <el-button v-if="
                                !isReadOnly &&
                                item.content.recipelInfo.chargeStatus == 0 &&
                                item.content.recipelInfo.status != -1
                              " :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              " @click="DeleteMedicalRow(index, citem, item)" icon="el-icon-close"
                                       style="float: right; padding: 3px 0" type="text">
                            </el-button>
                          </div>
                          <div>
                            <el-input :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              " placeholder="数量" size="mini" ref='herbMedicine'
                                      oninput="value=value.replace(/[^\d.]/g,'')" @input="
                                changeSingleDosage(
                                  citem.singleDosage,
                                  index,
                                  item
                                )
                              " v-model="citem.singleDosage" style="width: 80px">
                              <template slot="append">{{
                                  citem.drugStuffId.pack.name
                                }}
                              </template>
                            </el-input>
                            <el-select :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              " size="mini" v-model="citem.chineseMedicineUse" placeholder="" style="width: 105px">
                              <el-option v-for="oitem in ChineseUseTimeOption"
                                         :key="oitem.value" :label="oitem.name" :value="{
                                  name: oitem.name,
                                  value: oitem.value,
                                }">
                              </el-option>
                            </el-select>
                          </div>
                        </el-card>
                      </el-col>
                    </el-row>
                    <el-row>
                      <el-input :disabled="
                          isReadOnly ||
                          item.content.recipelInfo.chargeStatus != 0 ||
                          item.content.recipelInfo.status == -1
                        " v-model="item.content.recipelInfo.dosage" oninput="value=value.replace(/[^\d.]/g,'')"
                                @input="MedicalCalculate()" style="width: 60px"></el-input>

                      &nbsp;剂 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;用法：
                      <el-select :disabled="
                          isReadOnly ||
                          item.content.recipelInfo.chargeStatus != 0 ||
                          item.content.recipelInfo.status == -1
                        " v-model="item.content.recipelInfo.recipelUse" placeholder="请选择" style="width: 110px"
                                 @change="exchage">
                        <el-option v-for="pitem in ChineseUseOption" :key="pitem.value"
                                   :label="pitem.name"
                                   :value="{ name: pitem.name, value: pitem.value }">
                        </el-option>
                      </el-select>
                      <el-select :disabled="
                          isReadOnly ||
                          item.content.recipelInfo.chargeStatus != 0 ||
                          item.content.recipelInfo.status == -1
                        " v-model="item.content.recipelInfo.frequency" placeholder="请选择" style="width: 110px"
                                 @change="exchage">
                        <el-option v-for="pitem in ChineseTimeOption" :key="pitem.value"
                                   :label="pitem.name"
                                   :value="{ name: pitem.name, value: pitem.value }">
                        </el-option>
                      </el-select>
                      <el-select :disabled="
                          isReadOnly ||
                          item.content.recipelInfo.chargeStatus != 0 ||
                          item.content.recipelInfo.status == -1
                        " v-model="item.content.recipelInfo.takeFrequency" placeholder="请选择" style="width: 110px"
                                 @change="exchage">
                        <el-option v-for="pitem in JSON.parse(
                            JSON.stringify(FrequencyOption)
                          )" :key="pitem.value" :label="pitem.name" :value="{
                            name: pitem.name,
                            value: pitem.value,
                          }">
                        </el-option>
                      </el-select>
                      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 一次&nbsp;<el-input :disabled="
                          isReadOnly ||
                          item.content.recipelInfo.chargeStatus != 0 ||
                          item.content.recipelInfo.status == -1
                        " v-model="item.content.recipelInfo.singleDosage" style="width: 60px"></el-input>&nbsp;<span
                      v-if="isSpecial">ml</span><span v-else>ml</span>
                      <span style="float: right">中药金额:{{
                          item.content.recipelInfo.medicalAmount
                            ? item.content.recipelInfo.medicalAmount
                            : 0
                        }}元</span>
                      <el-input :disabled="
                          isReadOnly ||
                          item.content.recipelInfo.chargeStatus!= 0 ||
                          item.content.recipelInfo.status == -1
                        " v-model="item.content.recipelInfo.chinessNotes" placeholder="输入备注选项"
                                style="margin-top:10px"></el-input>
                    </el-row>
                  </div>
                  <!-- 输液处方 -->
                  <div v-if="item.type === 'recipelType_2'" style="margin-top: 15px">
                    <div>
                      <el-row>
                        <el-divider content-position="left">处方信息</el-divider>

                        <el-select :disabled="
                            isReadOnly ||
                            item.content.recipelInfo.chargeStatus != 0 ||
                            item.content.recipelInfo.status == -1
                          " v-model="item.content.recipelInfo.smallType" placeholder="请选择处方分类" style="width: 100px">
                          <el-option v-for="smallTypeItem in RecipelSmallTypeList"
                                     :key="smallTypeItem.value" :label="smallTypeItem.name" :value="{
                              name: smallTypeItem.name,
                              value: smallTypeItem.value,
                            }">
                          </el-option>
                        </el-select>

                        <el-button v-if="
                            !isReadOnly &&
                            item.content.recipelInfo.chargeStatus == 0 &&
                            item.content.recipelInfo.status != -1
                          " @click="addGroup(item)" type="primary" plain>加一组
                        </el-button>
                        <!-- <button v-if="
                            !isReadOnly &&
                            item.content.recipelInfo.chargeStatus == 0 &&
                            item.content.recipelInfo.status != -1
                          " @click="minusGroup(item)">减一组</button> -->
                        <el-button type="primary" v-if="!isReadOnly" style="float: right" plain
                                   @click="historyRecipel(item.type, index)">历史处方
                        </el-button>
                        <span style="float: right">&nbsp;&nbsp;&nbsp;</span>
                        <el-button type="primary" v-if="!isReadOnly"
                                   style="float: right; margin-left: 120px" plain
                                   @click="templateRecipel(item.type, index)">模板处方
                        </el-button>
                        <!-- <span style="float:right;">&nbsp;&nbsp;&nbsp;</span> -->
                        <el-button type="primary" v-if="!isReadOnly"
                                   style="float: right; margin-right: -105px" plain
                                   @click="saveTemplate(item.type, index)">存为模板
                        </el-button>
                      </el-row>
                    </div>
                    <div style="margin-left: -10px">
                      <div v-for="(items, index) in item.infusion.zushu" :key="index" style="
                          border: 1px solid #dcdcdc;
                          padding: 10px;
                          margin: 10px;
                        " class="shuzhuClass">
                        <el-row>
                          <div>
                            <el-popover placement="top-start" v-if="
                                !isReadOnly &&
                                item.content.recipelInfo.chargeStatus == 0 &&
                                item.content.recipelInfo.status != -1
                              " width="700" trigger="focus">
                              <el-table :data="InfusionTable" :height="300" border
                                        highlight-current-row @row-click="
                                  (row, column, e) =>
                                    RowClickInfusionTable(row, column, e, index)
                                ">
                                <el-table-column prop="drug.type.name" label="药品类型">
                                </el-table-column>
                                <el-table-column prop="drug.goodsName" label="药品名称">
                                </el-table-column>
                                <el-table-column prop="gg" label="规格" width="120">
                                  <template slot-scope="scope">
                                    {{
                                      scope.row.drug.dosis
                                    }}{{ scope.row.drug.dosisUnit.name }} *
                                    {{
                                      scope.row.drug.preparation
                                    }}{{
                                      scope.row.drug.preparationUnit.name
                                    }}/{{ scope.row.drug.pack.name }}
                                  </template>
                                </el-table-column>
                                <el-table-column prop="drug.factory.name" label="厂家"
                                                 width="100"></el-table-column>
                                <el-table-column label="销售价" width="80">
                                  <template slot-scope="scope">
                                    {{
                                      (scope.row.drug.price).toFixed(2) +
                                      "/" +
                                      scope.row.drug.pack.name
                                    }}
                                  </template>
                                </el-table-column>
                                <el-table-column label="拆零价" width="80">
                                  <template slot-scope="scope">
                                    {{
                                      scope.row.drug.isUnpackSell == 1
                                        ? (scope.row.drug.retailPrice).toFixed(4) +
                                        "/" +
                                        scope.row.drug.preparationUnit.name
                                        : "--"
                                    }}
                                  </template>
                                </el-table-column>
                                <el-table-column prop="surplusStock" label="库存"
                                                 width="100">
                                  <template slot-scope="scope">
                                    {{
                                      Math.floor(
                                        scope.row.surplusStock /
                                        scope.row.drug.preparation
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
                                          scope.row.drug.preparationUnit
                                            .name
                                          : "")
                                        : scope.row.surplusStock +
                                        scope.row.drug.preparationUnit.name
                                    }}
                                  </template>
                                </el-table-column>
                              </el-table>
                              <el-input prefix-icon="el-icon-plus"
                                        suffix-icon="el-icon-search" style="width: 20%"
                                        slot="reference"
                                        v-model="SearchInfusion[index]"
                                        @input="GetInfusionTable(index)"
                                        @focus="GetInfusionTable(index)"
                                        placeholder="输入药品名称或拼音码"></el-input>
                            </el-popover>
                            <span>&nbsp;&nbsp;第{{ index + 1 }}组</span>
                            <span style="
                                float: right;
                                height: 25px;
                                line-height: 25px;
                                font-size: 14px;
                              ">&nbsp;&nbsp;</span>
                            <el-button style="float: right" type="primary" plain
                                       icon="el-icon-delete" circle v-if="
                                !isReadOnly &&
                                item.content.recipelInfo.chargeStatus == 0 &&
                                item.content.recipelInfo.status != -1
                              " @click="minusGroup(item, index)"></el-button>
                            <span style="
                                float: right;
                                height: 25px;
                                line-height: 25px;
                                font-size: 14px;
                              ">&nbsp;&nbsp;</span>
                            <span style="
                                float: right;
                                height: 25px;
                                line-height: 25px;
                                font-size: 14px;
                              ">&nbsp;&nbsp;滴/分钟</span>

                            <el-input style="float: right; width: 10%"
                                      v-model="item.infusion.drippingSpeed[index]"
                                      placeholder="请选择滴速" :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              ">
                            </el-input>

                            <span style="
                                float: right;
                                height: 25px;
                                line-height: 25px;
                                font-size: 14px;
                              ">&nbsp;&nbsp;天&nbsp;&nbsp;</span>

                            <el-input-number v-model="item.infusion.days[index].name" style="float: right; width: 10%"
                                             :disabled="isReadOnly || item.content.recipelInfo.chargeStatus != 0 || item.content.recipelInfo.status == -1"
                                             @change="MedicalCalculate()" :controls="false" placeholder="请选择天数">
                            </el-input-number>

                            <!-- <el-select style="float: right; width: 10%"
                                       v-model="item.infusion.days[index]" :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              " @change="MedicalCalculate()" placeholder="请选择天数">
                              <el-option v-for="item in DayNumOption" :key="item.value"
                                         :label="item.name"
                                         :value="{ name: item.name, value: item.value }">
                              </el-option>
                            </el-select> -->

                            <span style="
                                float: right;
                                height: 25px;
                                line-height: 25px;
                                font-size: 18px;
                              ">&nbsp;&nbsp;</span>

                            <el-select style="float: right; width: 10%"
                                       v-model="item.infusion.frequency[index]" :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              " @change="MedicalCalculate()" placeholder="请选择频次">
                              <el-option v-for="item in FrequencyOption" :key="item.value"
                                         :label="item.name" :value="{
                                  name: item.name,
                                  value: item.value,
                                }">
                              </el-option>
                            </el-select>

                            <span style="
                                float: right;
                                height: 25px;
                                line-height: 25px;
                                font-size: 18px;
                              ">&nbsp;&nbsp;</span>

                            <el-select style="float: right; width: 10%"
                                       v-model="item.infusion.infuseUse[index]" :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              " placeholder="请选择用法">
                              <el-option v-for="item in infuseUseOption" :key="item.value"
                                         :label="item.name"
                                         :value="{ name: item.name, value: item.value }">
                              </el-option>
                            </el-select>
                          </div>
                          <div style="border: 7px solid #0000"></div>
                          <el-table :data="
                              getDataFilterTable(
                                item.infusion.infusionProject[index],
                                0,
                                item
                              )
                            " border style="width: 100%" class="tableStyle" @cell-click="checkInventory">
                            <el-table-column type="index" label="序号"
                                             align="center"></el-table-column>
                            <!-- <el-table-column
                         v-show="true"
                          prop="infuseGroup"
                          label="组数"
                          width="100"
                          align="center"
                        ></el-table-column> -->
                            <el-table-column prop="drugStuffId" label="药品名称" align="center" width="260">
                              <template slot-scope="scope">
                                {{ scope.row.drugStuffId.name }}
                                <span style="color: forestgreen">
																	{{
                                    scope.row.drugStuffId.drug.dosis +
                                    scope.row.drugStuffId.drug.dosisUnit.name +
                                    "*" +
                                    scope.row.drugStuffId.drug.preparation +
                                    scope.row.drugStuffId.drug.preparationUnit
                                      .name +
                                    "/" +
                                    scope.row.drugStuffId.drug.pack.name
                                  }}
																</span>
                              </template>
                            </el-table-column>
                            <el-table-column prop="skinTest" label="皮试" align="center"
                            >
                              <template slot-scope="scope">
                                <el-select v-model="scope.row.skinTest" :disabled="
                                    isReadOnly ||
                                    item.content.recipelInfo.chargeStatus !=
                                      0 ||
                                    item.content.recipelInfo.status == -1
                                  " placeholder="">
                                  <el-option v-for="item in InfusionOption"
                                             :key="item.value" :label="item.name" :value="{
                                      name: item.name,
                                      value: item.value,
                                    }">
                                  </el-option>
                                </el-select>
                              </template>
                            </el-table-column>
                            <el-table-column prop="singleDosage" label="单次用量" align="center"
                            >
                              <template slot-scope="scope">
                                <el-input v-model="scope.row.singleDosage"
                                          ref='transfusion'
                                          oninput="value=value.replace(/[^\d.]/g,'')"
                                          @input="MedicalCalculate()" :disabled="
                                    isReadOnly ||
                                    item.content.recipelInfo.chargeStatus !=
                                      0 ||
                                    item.content.recipelInfo.status == -1
                                  ">
                                  <template slot="append">{{
                                      scope.row.drugStuffId.dosisUnit.name
                                    }}
                                  </template>
                                </el-input>
                              </template>
                            </el-table-column>
                            <!-- <el-table-column
                          prop="drippingSpeed"
                          label="滴速"
                          align="center"
                          width="80"
                        >
                          <template slot-scope="scope">
                            <el-input
                              v-model="scope.row.drippingSpeed"

                              :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              "
                            >

                            </el-input>
                            <span >
                                滴/分钟
                              </span>
                          </template>
                        </el-table-column> -->
                            <!-- <el-table-column
                          prop="infuseUse"
                          label="用法"
                          align="center"
                          width="120"
                        >
                          <template slot-scope="scope">
                            <el-select
                              v-model="scope.row.infuseUse"
                              :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              "
                              placeholder=""
                            >
                              <el-option
                                v-for="item in infuseUseOption"
                                :key="item.value"
                                :label="item.name"
                                :value="{ name: item.name, value: item.value }"
                              >
                              </el-option>
                            </el-select>
                          </template>
                        </el-table-column> -->
                            <!-- <el-table-column
                          prop="frequency"
                          label="频次"
                          align="center"
                          width="120"
                        >
                          <template slot-scope="scope">
                            <el-select
                              v-model="scope.row.frequency"
                              :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              "
                              @change="MedicalCalculate()"
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
                        </el-table-column> -->
                            <!-- <el-table-column
                          prop="days"
                          label="天数"
                          align="center"
                          width="100"
                        >
                          <template slot-scope="scope">
                            <el-select
                              v-model="scope.row.days"
                              :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              "
                              @change="MedicalCalculate()"
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
                        </el-table-column> -->
                            <el-table-column prop="total" label="总量" align="center"
                            >
                              <template slot-scope="scope">
                                <!-- <el-input v-model="scope.row.total" type="number" disabled>
                                                                <template slot="append">
                                                                    {{scope.row.isUnpackSell === '1' ? scope.row.drugStuffId.preparationUnit.name : scope.row.drugStuffId.pack.name}}
                                                                </template>
                                                            </el-input> -->
                                {{
                                  Math.floor(
                                    scope.row.total /
                                    scope.row.drugStuffId.drug.preparation
                                  ) > 0
                                    ? Math.floor(
                                    scope.row.total /
                                    scope.row.drugStuffId.drug.preparation
                                    ) +
                                    scope.row.drugStuffId.pack.name +
                                    (scope.row.total %
                                    scope.row.drugStuffId.drug.preparation >
                                    0
                                      ? (scope.row.total %
                                      scope.row.drugStuffId.drug
                                        .preparation) +
                                      scope.row.drugStuffId.preparationUnit
                                        .name
                                      : "")
                                    : scope.row.total +
                                    scope.row.drugStuffId.preparationUnit.name
                                }}
                              </template>
                            </el-table-column>
                            <el-table-column prop="isUnpackSell" label="单价"
                                             align="center">
                              <template slot-scope="scope">
                                <el-select v-model="scope.row.isUnpackSell" :disabled="
                                    isReadOnly ||
                                    item.content.recipelInfo.chargeStatus !=
                                      0 ||
                                    item.content.recipelInfo.status == -1
                                  " placeholder="请选择" @change="MedicalCalculate()" style="width: 100%">
                                  <el-option :label="
                                      (scope.row.drugStuffId.price).toFixed(4) +
                                      '/' +
                                      scope.row.drugStuffId.pack.name
                                    " :value="0"></el-option>
                                  <el-option :label="
                                      (scope.row.drugStuffId.retailPrice).toFixed(4) +
                                      '/' +
                                      scope.row.drugStuffId.preparationUnit.name
                                    " :value="1"></el-option>
                                </el-select>
                              </template>
                            </el-table-column>
                            <el-table-column prop="allFee" label="金额"
                                             align="center"></el-table-column>
                            <el-table-column v-if="
                                !isReadOnly &&
                                item.content.recipelInfo.chargeStatus == 0 &&
                                item.content.recipelInfo.status != -1
                              " label="操作" fixed="right" align="center" width="50">
                              <template slot-scope="scope">
                                <i class="el-icon-circle-close" @click="
                                    DeleteMedicalRow(scope.$index, scope.row)
                                  "></i>
                              </template>
                            </el-table-column>
                          </el-table>
                        </el-row>
                      </div>
                    </div>
                  </div>
                  <!-- 诊疗项目 -->
                  <div v-if="item.type === 'recipelType_3'" style="margin-top: 15px">
                    <el-row>
                      <el-divider content-position="left">处方信息</el-divider>
                      <el-popover placement="top-start" v-if="
                          !isReadOnly &&
                          item.content.recipelInfo.chargeStatus == 0 &&
                          item.content.recipelInfo.status != -1
                        " width="540" trigger="focus">
                        <el-table :data="TreatmentTable" :height="300" border
                                  highlight-current-row @row-click="RowClickTreatmentTable">
                          <el-table-column prop="itemName" label="项目名称" width="200">
                          </el-table-column>
                          <el-table-column prop="itemType.name" label="项目类型" width="100">
                          </el-table-column>
                          <el-table-column prop="isPackage" label="套餐项目" width="100">
                            <template slot-scope="scope">
                              {{ scope.row.isPackage === "1" ? "是" : "否" }}
                            </template>
                          </el-table-column>
                          <el-table-column prop="salePrice" label="单价" width="120">
                            <template slot-scope="scope">
                              {{ (scope.row.salePrice).toFixed(4) }}/{{
                                scope.row.unit.name
                              }}
                            </template>
                          </el-table-column>
                        </el-table>
                        <el-input prefix-icon="el-icon-plus" suffix-icon="el-icon-search"
                                  style="width: 30%" slot="reference" ref="TreatmentInput"
                                  v-model="SearchCostItemInput" @input="GetCostItemTable"
                                  @focus="GetCostItemTable" placeholder="输入诊疗项目名称或拼音码"></el-input>
                      </el-popover>
                      <el-button type="primary" v-if="!isReadOnly" style="float: right" plain
                                 @click="historyRecipel(item.type, index)">历史处方
                      </el-button>
                      <span style="float: right">&nbsp;&nbsp;&nbsp;</span>
                      <el-button type="primary" v-if="!isReadOnly"
                                 style="float: right; margin-left: 120px" plain
                                 @click="templateRecipel(item.type, index)">模板处方
                      </el-button>
                      <!-- <span style="float:right;">&nbsp;&nbsp;&nbsp;</span> -->
                      <el-button type="primary" v-if="!isReadOnly"
                                 style="float: right; margin-right: -105px" plain
                                 @click="saveTemplate(item.type, index)">存为模板
                      </el-button>
                    </el-row>
                    <el-row>
                      <el-table :data="
                          getDataFilterTable(
                            item.content.recipelDetailEvtList,
                            0,
                            item
                          )
                        " border style="width: 100%" class="tableStyle">
                        <el-table-column type="index" label="序号" align="center">
                        </el-table-column>
                        <el-table-column prop="drugStuffId.name" label="项目名称" align="center">
                        </el-table-column>
                        <el-table-column prop="drugStuffId.costItem.itemType.name" label="项目类别"
                                         align="center"></el-table-column>
                        <el-table-column prop="drugStuffId.costItem.isPackage" label="套餐项目"
                                         align="center">
                          <template slot-scope="scope">
                            {{
                              scope.row.drugStuffId.costItem.isPackage === "1"
                                ? "是"
                                : "否"
                            }}
                          </template>
                        </el-table-column>
                        <el-table-column prop="total" label="数量" align="center">
                          <template slot-scope="scope">
                            <el-input v-model="scope.row.total"
                                      ref="DiagnosisTreatment"
                                      oninput="value=value.replace(/[^\d.]/g,'')"
                                      @input="MedicalCalculate()" :disabled="
                                isReadOnly ||
                                item.content.recipelInfo.chargeStatus != 0 ||
                                item.content.recipelInfo.status == -1
                              ">
                              <template slot="append">{{
                                  scope.row.drugStuffId.pack.name
                                }}
                              </template>
                            </el-input>
                          </template>
                        </el-table-column>
                        <el-table-column label="单价" align="center">
                          <template slot-scope="scope">
                            {{ (scope.row.drugStuffId.price).toFixed(4) }}/{{
                              scope.row.drugStuffId.pack.name
                            }}
                          </template>
                        </el-table-column>
                        <el-table-column prop="allFee" label="合计金额" align="center">
                        </el-table-column>
                        <el-table-column v-if="
                            !isReadOnly &&
                            item.content.recipelInfo.chargeStatus == 0 &&
                            item.content.recipelInfo.status != -1
                          " label="操作" fixed="right" align="center" width="50">
                          <template slot-scope="scope">
                            <i class="el-icon-circle-close"
                               @click="DeleteMedicalRow(scope.$index, scope.row)"></i>
                          </template>
                        </el-table-column>
                        <el-table-column v-else-if="inspectionSign == 1" label="操作"
                                         fixed="right" align="center" width="80">
                          <template slot-scope="scope">
                            <div v-if="inspectionType[scope.$index] == 1">
                              <el-button @click="lookInspection(scope.row.drugStuffId)"
                                         type="text">查看
                              </el-button>
                            </div>
                            <div v-else>
                              <el-button type="text" disabled>还未填写</el-button>
                            </div>
                          </template>
                        </el-table-column>
                      </el-table>
                    </el-row>
                  </div>
                  <div style="margin-top: 10px">
                    <el-row>
                      <el-divider content-position="left">附加费</el-divider>
                      <el-popover placement="top-start" width="550" trigger="click" v-if="
                          !isReadOnly &&
                          item.content.recipelInfo.chargeStatus == 0 &&
                          item.content.recipelInfo.status != -1
                        ">
                        <el-table :data="SurchargeTable" :max-height="300" border
                                  highlight-current-row @row-click="RowClickSurchargeTable">
                          <el-table-column label="费用名称">
                            <template slot-scope="scope">
                              {{
                                scope.row.stuffType === "4"
                                  ? scope.row.stuff.name
                                  : scope.row.itemName
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column label="费用类型" width="130">
                            <template slot-scope="scope">
                              {{
                                scope.row.stuffType === "4"
                                  ? scope.row.stuff.type.name
                                  : "诊疗（其它费用）"
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column label="销售价" width="100">
                            <template slot-scope="scope">
                              {{
                                scope.row.stuffType === "4"
                                  ? (scope.row.stuff.priceOutSell).toFixed(2) +
                                  "/" +
                                  scope.row.stuff.packUnit.name
                                  : scope.row.salePrice +
                                  "/" +
                                  scope.row.unit.name
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column label="拆零价" width="100">
                            <template slot-scope="scope">
                              {{
                                scope.row.stuffType === "4" &&
                                scope.row.stuff.isUnpackSell == "1"
                                  ? scope.row.stuff.retailPrice +
                                  "/" +
                                  scope.row.stuff.minUnit.name
                                  : "--"
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column label="库存" width="100">
                            <template slot-scope="scope">
                              {{
                                scope.row.stuffType === "4"
                                  ? Math.floor(
                                  scope.row.surplusStock /
                                  scope.row.stuff.packNumber
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
                                  : "不限制"
                              }}
                            </template>
                          </el-table-column>
                        </el-table>
                        <el-input prefix-icon="el-icon-plus" suffix-icon="el-icon-search"
                                  style="width: 30%" slot="reference" v-model="SearchSurchargeInput"
                                  @input="GetSurchargeTable" @focus="GetSurchargeTable"
                                  placeholder="输入附加费名称或拼音码">
                        </el-input>
                      </el-popover>
                    </el-row>
                    <div v-if="item.type != 'recipelType_2'">
                      <el-row>
                        <el-table :data="
                            getDataFilterTable(
                              item.content.recipelDetailEvtList,
                              1,
                              item
                            )
                          " border style="width: 100%" class="tableStyle">
                          <el-table-column type="index" label="序号" width="50" align="center">
                          </el-table-column>
                          <el-table-column prop="drugStuffId.name" label="费用名称" width="200"
                                           align="center"></el-table-column>
                          <el-table-column label="费用类型" width="130" align="center">
                            <template slot-scope="scope">
                              {{
                                scope.row.stuffType === "4"
                                  ? scope.row.drugStuffId.stuff.type.name
                                  : "诊疗（其它费用）"
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column prop="price" label="单价" width="100" align="center">
                            <template slot-scope="scope">
															<span v-if="scope.row.stuffType === '3'">
																{{
                                  (scope.row.drugStuffId.price).toFixed(4) +
                                  "/" +
                                  scope.row.drugStuffId.pack.name
                                }}
															</span>
                              <span v-if="scope.row.stuffType === '4'">
																{{
                                  scope.row.isUnpackSell == 0
                                    ? (scope.row.drugStuffId.price).toFixed(4) +
                                    "/" +
                                    scope.row.drugStuffId.pack.name
                                    : scope.row.drugStuffId.retailPrice +
                                    "/" +
                                    scope.row.drugStuffId.preparationUnit.name
                                }}
															</span>
                            </template>
                          </el-table-column>
                          <el-table-column label="数量" align="center" width="200">
                            <template slot-scope="scope">
                              <el-input :disabled="
                                  isReadOnly ||
                                  item.content.recipelInfo.chargeStatus != 0 ||
                                  item.content.recipelInfo.status == -1
                                " ref="additionalCharge" v-model="scope.row.singleDosage" @input="MedicalCalculate()"
                                        style="width: 60px">
                              </el-input>
                              <el-select :disabled="
                                  isReadOnly ||
                                  item.content.recipelInfo.chargeStatus != 0 ||
                                  item.content.recipelInfo.status == -1
                                " v-model="scope.row.isUnpackSell" @change="MedicalCalculate()" placeholder="请选择"
                                         style="width: 80px">
                                <el-option :label="scope.row.drugStuffId.pack.name"
                                           :value="0"></el-option>
                                <el-option
                                  v-if="scope.row.drugStuffId.isUnpackSell == 1"
                                  :label="
                                    scope.row.drugStuffId.preparationUnit.name
                                  " :value="1"></el-option>
                              </el-select>
                            </template>
                          </el-table-column>
                          <el-table-column prop="allFee" label="金额" align="center">
                          </el-table-column>
                          <el-table-column v-if="
                              !isReadOnly &&
                              item.content.recipelInfo.chargeStatus == 0 &&
                              item.content.recipelInfo.status != -1
                            " label="操作" fixed="right" width="80" align="center">
                            <template slot-scope="scope">
                              <i class="el-icon-circle-close" @click="
                                  DeleteMedicalRow(scope.$index, scope.row)
                                "></i>
                            </template>
                          </el-table-column>
                        </el-table>
                      </el-row>
                    </div>
                    <div v-else>
                      <el-row>
                        <el-table :data="
                            getDataFilterTable(item.infusion.excharge, 1, item)
                          " border style="width: 100%" class="tableStyle">
                          <el-table-column type="index" label="序号" width="50" align="center">
                          </el-table-column>
                          <el-table-column prop="drugStuffId.name" label="费用名称" width="200"
                                           align="center"></el-table-column>
                          <el-table-column label="费用类型" width="130" align="center">
                            <template slot-scope="scope">
                              {{
                                scope.row.stuffType === "4"
                                  ? scope.row.drugStuffId.stuff.type.name
                                  : "诊疗（其它费用）"
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column prop="price" label="单价" width="120" align="center">
                            <template slot-scope="scope">
															<span v-if="scope.row.stuffType === '3'">
																{{
                                  (scope.row.drugStuffId.price).toFixed(4) +
                                  "/" +
                                  scope.row.drugStuffId.pack.name
                                }}
															</span>
                              <span v-if="scope.row.stuffType === '4'">
																{{
                                  scope.row.isUnpackSell == 0
                                    ? (scope.row.drugStuffId.price).toFixed(4) +
                                    "/" +
                                    scope.row.drugStuffId.pack.name
                                    : scope.row.drugStuffId.retailPrice +
                                    "/" +
                                    scope.row.drugStuffId.preparationUnit.name
                                }}
															</span>
                            </template>
                          </el-table-column>
                          <el-table-column label="数量" align="center" width="200">
                            <template slot-scope="scope">
                              <el-input :disabled="
                                  isReadOnly ||
                                  item.content.recipelInfo.chargeStatus != 0 ||
                                  item.content.recipelInfo.status == -1
                                " ref="additionalCharge" v-model="scope.row.singleDosage" @input="MedicalCalculate()"
                                        style="width: 60px">
                              </el-input>
                              <el-select :disabled="
                                  isReadOnly ||
                                  item.content.recipelInfo.chargeStatus != 0 ||
                                  item.content.recipelInfo.status == -1
                                " v-model="scope.row.isUnpackSell" @change="MedicalCalculate()" placeholder="请选择"
                                         style="width: 80px">
                                <el-option :label="scope.row.drugStuffId.pack.name"
                                           :value="0"></el-option>
                                <el-option
                                  v-if="scope.row.drugStuffId.isUnpackSell == 1"
                                  :label="
                                    scope.row.drugStuffId.preparationUnit.name
                                  " :value="1"></el-option>
                              </el-select>
                            </template>
                          </el-table-column>
                          <el-table-column prop="allFee" label="金额" align="center">
                          </el-table-column>
                          <el-table-column v-if="
                              !isReadOnly &&
                              item.content.recipelInfo.chargeStatus == 0 &&
                              item.content.recipelInfo.status != -1
                            " label="操作" fixed="right" width="80" align="center">
                            <template slot-scope="scope">
                              <i class="el-icon-circle-close" @click="
                                  DeleteExMedicalRow(scope.$index, scope.row)
                                "></i>
                            </template>
                          </el-table-column>
                        </el-table>
                      </el-row>
                    </div>
                    <el-row style="margin-top: 10px">
                      <el-divider content-position="left">医嘱事项</el-divider>
                      <el-input type="textarea" :rows="4" placeholder="请输入医嘱事项"
                                v-model="item.content.recipelInfo.entrust" :disabled="
                          isReadOnly ||
                          item.content.recipelInfo.chargeStatus != 0 ||
                          item.content.recipelInfo.status == -1
                        ">
                      </el-input>
                    </el-row>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-card>
          <div style="height: 10px"></div>
        </el-scrollbar>
      </el-card>
      <el-card class="box-card sub-card" style="height: 50px">
        <span style="line-height: 27px">总金额：{{ payAmount }}元</span>
        <div style="float: right">
          <el-button type="primary" plain v-if="
              medicalEditTabsValue.content &&
              medicalEditTabsValue.content.recipelInfo.status == 1 &&
              medicalEditTabsValue.content.recipelInfo.chargeStatus == 0 &&
              TodayActiveName == 'already'
            " @click="invalidMedical">作废
          </el-button>
          <el-button :type="timeOut ? 'info' : 'primary'" v-if="isReadOnly && TodayActiveName == 'already'"
                     @click="edit">编辑
          </el-button>
          <el-button type="primary" v-if="!isReadOnly && TodayActiveName == 'already'" @click="save(2)">保存
          </el-button>
          <el-button type="primary" v-if="hasBtnPrint" @click="print">打印</el-button>
          <el-button v-show="hasBtnFinishVisit" type="primary" @click="save(1)">完成接诊</el-button>
        </div>
      </el-card>
    </el-main>
    <!-- 历史处方弹出框 -->
    <el-dialog title="历史处方" :visible.sync="historyDialogVisible" width="60%" class="ones"
               :before-close="handleClose">
      <div style="border: 1px solid #eee9e9; margin-top: -30px"></div>
      <br/>
      <div>
        <el-row>
          <el-col :span="24 / 2">
            <el-form :model="queryModel" @submit.native.prevent label-position="left" label-width="70px"
                     ref="queryForm" :inline-message="true">
              <el-form-item label="选择日期" prop="time">
                <el-date-picker v-model="queryModel.dateRange" type="datetimerange" range-separator="至"
                                start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd HH:mm:ss"
                                format="yyyy-MM-dd HH:mm:ss">
                </el-date-picker>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="24 / 4"></el-col>
          <el-col :span="24 / 4" style="float: right">
            <!-- <div class="bg_btn">
                <el-button type="primary" @click="onSearch"
                  >查询</el-button
                >
                <el-button @click="resetCondition()">重置</el-button>
              </div> -->
            <el-button type="primary" icon="el-icon-search" @click="onSearch" :plain="true">搜索</el-button>
            <el-button type="info" icon="el-icon-refresh-left" @click="resetCondition" :plain="true">重置
            </el-button>
          </el-col>
        </el-row>
        <el-row></el-row>
      </div>
      <br/>
      <el-table :data="historyRecipelList" style="width: 100%" :row-key="getRowKeys" :expand-row-keys="expands"
                @expand-change="expandSelect">
        <el-table-column type="expand">
          <template slot-scope="props">
            <!-- 西药处方 -->
            <div class="two" v-if="props.row.recipelType.value == 'recipelType_0'">
              <el-table :data="filterRecipel(props.row.recipelDetail)" style="width: 100%"
                        header-cell-style="{
      background:'#e2d2d2'}">
                <el-table-column label="序号" type="index" align="center">
                  <template slot-scope="scope">
										<span v-if="scope.row.isExtra == 0">
											{{ scope.$index + 1 }}
										</span>
                  </template>
                </el-table-column>
                <el-table-column prop="drugStuffId.name" label="药品信息" width="width">
                  <template slot-scope="scope">
										<span v-if="
                        scope.row.isExtra == 0 && scope.row.stuffType == '0'
                      ">
											{{ scope.row.drugStuffId.name }}
										</span>
                  </template>
                </el-table-column>
                <el-table-column prop="unitPrice" label="单价" width="120">
                  <template slot-scope="scope">
										<span v-if="
                        (scope.row.isExtra == 0 &&
                          scope.row.isUnpackSell == 0 &&
                          scope.row.stuffType == '0') ||
                        scope.row.stuffType == '2'
                      ">
											{{ (scope.row.unitPrice).toFixed(4) }}/{{
                        scope.row.drugStuffId.drug.pack.name
                      }}
										</span>
                    <span v-else-if="
                        (scope.row.isExtra == 0 &&
                          scope.row.isUnpackSell == 1 &&
                          scope.row.stuffType == '0') ||
                        scope.row.stuffType == '2'
                      ">
											{{ (scope.row.unitPrice).toFixed(4) }}/{{
                        scope.row.drugStuffId.drug.preparationUnit.name
                      }}
										</span>
                  </template>
                </el-table-column>
                <el-table-column prop="singleDosage" label="单次用量" width="width">
                  <template slot-scope="scope">
                    <template v-if="scope.row.isExtra == 0">{{
                        scope.row.singleDosage +
                        "" +
                        scope.row.drugStuffId.preparationUnit.name
                      }}
                    </template>
                  </template>
                </el-table-column>
                <el-table-column prop="westernMedicineUse.name" label="用法" width="width">
                  <template slot-scope="scope">
										<span v-if="
                        scope.row.westernMedicineUse != undefined &&
                        scope.row.isExtra == 0
                      ">
											{{ scope.row.westernMedicineUse.name }}
										</span>
                  </template>
                </el-table-column>
                <el-table-column prop="frequency.name" label="频次" width="width">
                  <template slot-scope="scope">
										<span v-if="scope.row.isExtra == 0">
											{{ scope.row.frequency.name }}
										</span>
                  </template>
                </el-table-column>

                <!-- <el-table-column prop="drippingSpeed" label="滴速" width="width">
                        <template slot-scope="scope">
                        <template>{{scope.row.drippingSpeed?scope.row.drippingSpeed:0}}滴/分钟</template>
                      </template>
                    </el-table-column> -->
                <el-table-column prop="days.name" label="天数" width="width">
                  <template slot-scope="scope">
                    <template v-if="scope.row.isExtra == 0">{{ scope.row.days.name }}天</template>
                  </template>
                </el-table-column>
                <el-table-column prop="total" label="总量" width="width">
                  <template slot-scope="scope">
                    <template v-if="
                        scope.row.isExtra == 0 &&
                        scope.row.stuffType != '4' &&
                        scope.row.stuffType != '3'
                      ">
                      {{
                        Math.floor(
                          scope.row.total /
                          scope.row.drugStuffId.drug.preparation -
                          0
                        ) >= 0
                          ? Math.floor(
                          scope.row.total /
                          scope.row.drugStuffId.drug.preparation -
                          0
                          ) +
                          scope.row.drugStuffId.drug.pack.name +
                          ((scope.row.total %
                            scope.row.drugStuffId.drug.preparation) -
                          0 >
                          0
                            ? (scope.row.total %
                            scope.row.drugStuffId.drug.preparation) -
                            0 +
                            scope.row.drugStuffId.drug.preparationUnit.name
                            : "")
                          : scope.row.total +
                          scope.row.drugStuffId.drug.preparationUnit.name
                      }}
                    </template>
                  </template>
                </el-table-column>
              </el-table>
              <!-- <br>
         <el-divider style="margin-top: 20px" content-position="left"
                    >附加费</el-divider
                  >
                  <el-table :data="filterAdditional(props.row.recipelDetail)" style="width: 100%">
                    <el-table-column label="序号" type="index" align="center" width="width">

                    </el-table-column>
                    <el-table-column
                      prop="drugStuffId.name"
                      label="名称"
                      width="width"
                    >
                    <template slot-scope="scope">
                        <span >
                          {{scope.row.drugStuffId.name}}
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
                        >{{ scope.row.unitPrice }}元/{{
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
                    <el-table-column prop="allFee" label="总额" width="width">
                      <template slot-scope="scope">
                        <span >
                          {{scope.row.allFee}}元
                        </span>

                      </template>
                    </el-table-column>
                  </el-table> -->
            </div>

            <!-- 中药处方 -->
            <div class="two" v-else-if="props.row.recipelType.value == 'recipelType_1'">
              <el-table :data="filterRecipel(props.row.recipelDetail)" style="width: 100%">
                <el-table-column label="序号" type="index" align="center">
                </el-table-column>
                <el-table-column prop="drugStuffId.name" label="药品信息" width="width">
                </el-table-column>
                <el-table-column prop="unitPrice" label="单价" width="120">
                  <template slot-scope="scope">
										<span v-if="
                        scope.row.isUnpackSell == 0 &&
                        scope.row.stuffType == '1'
                      ">
											{{ (scope.row.unitPrice).toFixed(4) }}/{{
                        scope.row.drugStuffId.drug.pack.name
                      }}
										</span>
                    <span v-else-if="
                        scope.row.isUnpackSell == 1 &&
                        scope.row.stuffType == '1'
                      ">
											{{ (scope.row.unitPrice).toFixed(4) }}/{{
                        scope.row.drugStuffId.drug.preparationUnit.name
                      }}
										</span>
                  </template>
                </el-table-column>
                <el-table-column prop="singleDosage" label="单次用量" width="width">
                  <template slot-scope="scope">
                    <template v-if="scope.row.stuffType == '1'">{{
                        scope.row.singleDosage +
                        "" +
                        scope.row.drugStuffId.preparationUnit.name
                      }}
                    </template>
                  </template>
                </el-table-column>
                <el-table-column prop="chineseMedicineUse.name" label="用法" width="width">
                  <template slot-scope="scope">
                    <template v-if="scope.row.stuffType == '1'">{{
                        scope.row.chineseMedicineUse.name
                      }}
                    </template>
                  </template>
                </el-table-column>

                <!-- <el-table-column prop="drippingSpeed" label="滴速" width="width">
                        <template slot-scope="scope">
                        <template>{{scope.row.drippingSpeed?scope.row.drippingSpeed:0}}滴/分钟</template>
                      </template>
                    </el-table-column> -->
                <el-table-column prop="total" label="总量" width="width">
                  <template slot-scope="scope">
                    <template v-if="scope.row.stuffType == '1'">
                      {{
                        Math.floor(
                          scope.row.total /
                          scope.row.drugStuffId.drug.preparation -
                          0
                        ) >= 0
                          ? Math.floor(
                          scope.row.total /
                          scope.row.drugStuffId.drug.preparation -
                          0
                          ) +
                          scope.row.drugStuffId.drug.pack.name +
                          ((scope.row.total %
                            scope.row.drugStuffId.drug.preparation) -
                          0 >
                          0
                            ? (scope.row.total %
                            scope.row.drugStuffId.drug.preparation) -
                            0 +
                            scope.row.drugStuffId.drug.preparationUnit.name
                            : "")
                          : scope.row.total +
                          scope.row.drugStuffId.drug.preparationUnit.name
                      }}
                    </template>
                  </template>
                </el-table-column>
              </el-table>
              <div style="margin: 10px 0">
                <el-input style="width: 50px" disabled v-model="props.row.dosage">
                </el-input>
                剂 用法：
                <el-input style="width: 100px" disabled v-model="props.row.recipelUse.name">
                </el-input>
                <el-input style="width: 100px" disabled v-model="props.row.takeFrequency.name">
                </el-input>
                <el-input style="width: 100px" disabled v-model="props.row.frequency.name">
                </el-input>
                每次服用
                <el-input style="width: 80px" disabled v-model="props.row.singleDosage">
                </el-input>
                {{ "ml" }}
              </div>
              <!-- <br>
         <el-divider style="margin-top: 20px" content-position="left"
                    >附加费</el-divider
                  >
                  <el-table :data="filterAdditional(props.row.recipelDetail)" style="width: 100%">
                    <el-table-column label="序号" type="index" align="center" width="width">

                    </el-table-column>
                    <el-table-column
                      prop="drugStuffId.name"
                      label="名称"
                      width="width"
                    >
                    <template slot-scope="scope">
                        <span >
                          {{scope.row.drugStuffId.name}}
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
                        >{{ scope.row.unitPrice }}元/{{
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
                    <el-table-column prop="allFee" label="总额" width="width">
                      <template slot-scope="scope">
                        <span >
                          {{scope.row.allFee}}元
                        </span>

                      </template>
                    </el-table-column>
                  </el-table> -->
            </div>

            <!-- 输液处方 -->
            <div class="two" style="border: 1px solid #dcdcdc"
                 v-else-if="props.row.recipelType.value == 'recipelType_2'">
              <div v-for="(items, index) in getInfuSion(props.row.recipelDetail)
                  .zushu" :key="index" style="border: 1px solid #dcdcdc; padding: 10px; margin: 10px">
                <div>
									<span style="
                      float: left;
                      height: 25px;
                      line-height: 25px;
                      font-size: 14px;
                    ">&nbsp;&nbsp;第{{ index + 1 }}组</span>
                  <span style="
                      float: right;
                      height: 25px;
                      line-height: 25px;
                      font-size: 14px;
                    ">&nbsp;&nbsp;滴/分钟</span>

                  <el-input style="float: right; width: 10%" :value="
                      getInfuSion(props.row.recipelDetail).drippingSpeed[index]
                    " placeholder="" :disabled="true">
                  </el-input>

                  <span style="
                      float: right;
                      height: 25px;
                      line-height: 25px;
                      font-size: 14px;
                    ">&nbsp;&nbsp;天&nbsp;&nbsp;</span>
                  <el-input style="float: right; width: 10%" :value="
                      getInfuSion(props.row.recipelDetail).days[index].name
                    " placeholder="" :disabled="true">
                  </el-input>

                  <span style="
                      float: right;
                      height: 25px;
                      line-height: 25px;
                      font-size: 18px;
                    ">&nbsp;&nbsp;</span>
                  <el-input style="float: right; width: 10%" :value="
                      getInfuSion(props.row.recipelDetail).frequency[index].name
                    " placeholder="" :disabled="true">
                  </el-input>

                  <span style="
                      float: right;
                      height: 25px;
                      line-height: 25px;
                      font-size: 18px;
                    ">&nbsp;&nbsp;</span>
                  <el-input style="float: right; width: 10%" :value="
                      getInfuSion(props.row.recipelDetail).infuseUse[index].name
                    " placeholder="" :disabled="true">
                  </el-input>
                </div>
                <div style="border: 17px solid #0000"></div>
                <el-table :data="
                    getInfuSion(props.row.recipelDetail).infusionProject[index]
                  " style="width: 100%">
                  <el-table-column label="序号" type="index" align="center" width="width">
                  </el-table-column>
                  <el-table-column prop="drugStuffId.name" label="药品名称" width="width">
                  </el-table-column>
                  <el-table-column prop="skinTest.name" label="皮试" width="width">
                  </el-table-column>
                  <el-table-column prop="drugStuffId.drug.factory.name" label="厂家" width="width">
                  </el-table-column>
                  <el-table-column prop="unitPrice" label="单价" width="120">
                    <template slot-scope="scope">
											<span>{{ (scope.row.unitPrice).toFixed(4) }}元/{{
                          scope.row.isUnpackSell == 1
                            ? scope.row.drugStuffId.preparationUnit.name
                            : scope.row.drugStuffId.pack.name
                        }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="singleDosage" label="单次用量" width="width">
                    <template slot-scope="scope">
                      <template>{{
                          scope.row.singleDosage +
                          "" +
                          scope.row.drugStuffId.preparationUnit.name
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
                            scope.row.total /
                            scope.row.drugStuffId.stuff.packNumber
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
                              (scope.row.drugStuffId.drug.preparation -
                                0)) +
                              scope.row.drugStuffId.drug.preparationUnit
                                .name
                              : "")
                            : scope.row.total +
                            scope.row.drugStuffId.drug.preparationUnit.name
                        }}
											</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="allFee" label="总额" width="width">
                    <template slot-scope="scope">
                      {{ scope.row.allFee }}元
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
            <!-- 诊疗项目 -->

            <div class="two" v-else-if="props.row.recipelType.value == 'recipelType_3'">
              <el-table :data="filterRecipel(props.row.recipelDetail)" style="width: 100%">
                <el-table-column type="index" label="序号" width="50" align="center"></el-table-column>
                <el-table-column prop="drugStuffId.name" label="项目名称" align="center"></el-table-column>
                <el-table-column prop="drugStuffId.costItem.itemType.name" label="项目类别" align="center"
                                 width="100"></el-table-column>
                <el-table-column prop="drugStuffId.costItem.isPackage" label="套餐项目" align="center"
                                 width="100">
                  <template slot-scope="scope">
                    {{
                      scope.row.drugStuffId.costItem.isPackage === "1"
                        ? "是"
                        : "否"
                    }}
                  </template>
                </el-table-column>
                <el-table-column prop="total" label="数量" align="center" width="120">
                  <template slot-scope="scope">
                    {{ scope.row.total }} {{ scope.row.drugStuffId.pack.name }}
                  </template>
                </el-table-column>
                <el-table-column label="单价" width="120" align="center">
                  <template slot-scope="scope">
                    {{ (scope.row.drugStuffId.price).toFixed(4) }}/{{
                      scope.row.drugStuffId.pack.name
                    }}
                  </template>
                </el-table-column>
                <el-table-column prop="allFee" label="合计金额" width="120" align="center">
                </el-table-column>
              </el-table>
            </div>
            <br/>
            <div class="two" style="margin: 10px">
              <el-divider style="margin-top: 20px" content-position="left">附加费</el-divider>
              <el-table :data="filterAdditional(props.row.recipelDetail)" style="width: 100%">
                <el-table-column label="序号" type="index" align="center" width="width">
                </el-table-column>
                <el-table-column prop="drugStuffId.name" label="名称" width="width">
                  <template slot-scope="scope">
										<span>
											{{ scope.row.drugStuffId.name }}
										</span>
                  </template>
                </el-table-column>

                <el-table-column prop="unitPrice" label="单价" width="120">
                  <template slot-scope="scope">
										<span>{{ (scope.row.unitPrice).toFixed(4) }}元/{{
                        scope.row.isUnpackSell == 1
                          ? scope.row.drugStuffId.preparationUnit.name
                          : scope.row.drugStuffId.pack.name
                      }}</span>
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
                          scope.row.total /
                          scope.row.drugStuffId.stuff.packNumber
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
                            (scope.row.drugStuffId.drug.preparation -
                              0)) +
                            scope.row.drugStuffId.drug.preparationUnit.name
                            : "")
                          : scope.row.total +
                          scope.row.drugStuffId.drug.preparationUnit.name
                      }}
										</span>
                  </template>
                </el-table-column>
                <el-table-column prop="allFee" label="总额" width="width">
                  <template slot-scope="scope">
                    <span> {{ scope.row.allFee }}元 </span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <el-row>
              <el-divider content-position="left">医嘱事项</el-divider>
              <el-input type="textarea" :rows="4" v-model="props.row.entrust" disabled>
              </el-input>
            </el-row>
          </template>
        </el-table-column>
        <el-table-column label="序号" type="index"></el-table-column>
        <el-table-column prop="patient.name" label="姓名"></el-table-column>
        <el-table-column prop="patient.gender.name" label="性别">
        </el-table-column>
        <el-table-column prop="patient.age" label="年龄"></el-table-column>
        <el-table-column prop="name" label="处方名称"></el-table-column>
        <el-table-column prop="medicalRecord.westernDiagnose" label="西医诊断">
        </el-table-column>
        <el-table-column prop="medicalRecord.chinaDiagnose" label="中医诊断">
        </el-table-column>
        <el-table-column prop="updateDate" label="接诊时间"></el-table-column>

        <el-table-column prop="updateBy" label="医生姓名">
          <template slot-scope="scope">
            {{ GetCreateBy(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column prop="" label="操作">
          <template slot-scope="scope">
            <el-button type="text" @click="invoking(scope.row)">调用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- <span slot="footer" class="dialog-footer">
        <el-button @click="historyDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="historyDialogVisible = false">确 定</el-button>
      </span> -->
    </el-dialog>

    <el-dialog title="历史病历" :visible.sync="caseDialogVisible" width="60%" class="ones"
               :before-close="handleClose">
      <div style="border: 1px solid #eee9e9; margin-top: -30px"></div>
      <br/>
      <div>
        <el-row>
          <el-col :span="24 / 2">
            <el-form :model="queryModel" @submit.native.prevent label-position="left" label-width="70px"
                     ref="queryForm" :inline-message="true">
              <el-form-item label="选择日期" prop="time">
                <el-date-picker v-model="queryModel.dateRange" type="datetimerange" range-separator="至"
                                start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd HH:mm:ss"
                                format="yyyy-MM-dd HH:mm:ss">
                </el-date-picker>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="24 / 4"></el-col>
          <el-col :span="24 / 4" style="float: right">
            <el-button type="primary" icon="el-icon-search" @click="medicalHistoryOnSearch" :plain="true">搜索</el-button>
            <el-button type="info" icon="el-icon-refresh-left" @click="medicalHistoryReset" :plain="true">重置
            </el-button>
          </el-col>
        </el-row>
        <el-row></el-row>
      </div>
      <br/>
      <el-table :data="historicalCasesList" style="width: 100%" :row-key="getRowKeys" :expand-row-keys="expands"
                @expand-change="expandSelect">
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-row v-if="props.row.medicalRecord.patientTell">
              <el-divider content-position="left">主诉</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.patientTell" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.personalHistory">
              <el-divider content-position="left">个人史</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.personalHistory" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.allergyHistory">
              <el-divider content-position="left">过敏史</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.allergyHistory" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.diseaseHistory">
              <el-divider content-position="left">疾病史</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.diseaseHistory" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.infectiousHistory">
              <el-divider content-position="left">传染病史</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.infectiousHistory"
                        disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.surgeryHistory">
              <el-divider content-position="left">手术史</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.surgeryHistory" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.transfusionHistory">
              <el-divider content-position="left">输血史</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.transfusionHistory"
                        disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.physiqueCheck">
              <el-divider content-position="left">体格检查</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.physiqueCheck" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.emergencyDiagnose">
              <el-divider content-position="left">急诊诊断</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.emergencyDiagnose"
                        disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.emergencyEffect">
              <el-divider content-position="left">急诊效果</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.emergencyEffect" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.familyHistory">
              <el-divider content-position="left">家族史</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.familyHistory" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.lunariaHistory">
              <el-divider content-position="left">月经史</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.lunariaHistory" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.pregnancyHistory">
              <el-divider content-position="left">婚育史</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.pregnancyHistory" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.assistCheck">
              <el-divider content-position="left">辅助检查</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.assistCheck" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.nowHistory">
              <el-divider content-position="left">现病史</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.nowHistory" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.beforeHistory">
              <el-divider content-position="left">既往史</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.beforeHistory" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.epidemicDisease">
              <el-divider content-position="left">流行病学史</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.epidemicDisease" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.otherCheck">
              <el-divider content-position="left">其他检查</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.otherCheck" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.handlingSituation">
              <el-divider content-position="left">处理情况</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.handlingSituation"
                        disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.healthEducation">
              <el-divider content-position="left">个体化健康教育</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.healthEducation" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.chinaDiagnose">
              <el-divider content-position="left">中医诊断</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.chinaDiagnose" disabled></el-input>
            </el-row>
            <el-row v-if="props.row.medicalRecord.westernDiagnose">
              <el-divider content-position="left">西医诊断</el-divider>
              <el-input type="textarea" autosize v-model="props.row.medicalRecord.westernDiagnose" disabled></el-input>
            </el-row>
          </template>
        </el-table-column>
        <el-table-column label="序号" type="index"></el-table-column>
        <el-table-column prop="patient.name" label="姓名"></el-table-column>
        <el-table-column prop="patient.gender.name" label="性别">
        </el-table-column>
        <el-table-column prop="patient.age" label="年龄"></el-table-column>
        <el-table-column prop="medicalRecord.westernDiagnose" label="西医诊断">
        </el-table-column>
        <el-table-column prop="medicalRecord.chinaDiagnose" label="中医诊断">
        </el-table-column>
        <el-table-column prop="updateDate" label="接诊时间"></el-table-column>

        <el-table-column prop="updateBy" label="医生姓名">
          <template slot-scope="scope">
            {{ GetCreateBy(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column prop="" label="操作">
          <template slot-scope="scope">
            <el-button type="text" @click="medicalHistoryInvoking(scope.row)">调用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- <span slot="footer" class="dialog-footer">
        <el-button @click="historyDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="historyDialogVisible = false">确 定</el-button>
      </span> -->
    </el-dialog>

    <!-- 模板处方弹出框 -->
    <el-dialog title="模板处方调用" :visible.sync="templateDialogVisible" width="50%" :before-close="handleClose">
      <div>
        <el-row>
          <el-form :model="templateQueryModel" @submit.native.prevent label-position="left" label-width="70px"
                   :inline-message="true">
            <el-col :span="24">
              <el-form-item label="模板类别" prop="category">
                <el-radio v-model="templateQueryModel.category" @change="searchTemplate" label="3">全部
                </el-radio>
                <el-radio v-model="templateQueryModel.category" @change="searchTemplate" label="0">私人模板
                </el-radio>
                <el-radio v-model="templateQueryModel.category" @change="searchTemplate" label="1">公开模板
                </el-radio>
                <el-radio v-model="templateQueryModel.category" @change="searchTemplate" label="2">AI模板
                </el-radio>
              </el-form-item>
            </el-col>
          </el-form>
        </el-row>
      </div>
      <el-table :data="templateRecipelList" style="width: 100%" :row-key="getRowKeys" :expand-row-keys="expands"
                @expand-change="expandSelect">
        <el-table-column type="expand">
          <template slot-scope="props">
            <!-- 西药处方 -->
            <div class="two" v-if="props.row.type.value == 'recipelType_0'">
              <el-table :data="filterRecipel(props.row.recipeTemplateDetail)" style="width: 100%"
                        header-cell-style="{
      background:'#e2d2d2'}">
                <el-table-column label="序号" type="index" align="center">
                  <template slot-scope="scope">
										<span v-if="scope.row.isExtra == 0">
											{{ scope.$index + 1 }}
										</span>
                  </template>
                </el-table-column>
                <el-table-column prop="drugStuffId.name" label="药品信息" width="width">
                  <template slot-scope="scope">
										<span v-if="
                        scope.row.isExtra == 0 && scope.row.stuffType == '0'
                      ">
											{{ scope.row.drugStuffId.name }}
										</span>
                  </template>
                </el-table-column>
                <el-table-column prop="unitPrice" label="单价" width="120">
                  <template slot-scope="scope">
										<span v-if="
                        (scope.row.isExtra == 0 &&
                          scope.row.isUnpackSell == 0 &&
                          scope.row.stuffType == '0') ||
                        scope.row.stuffType == '2'
                      ">
											{{ (scope.row.unitPrice).toFixed(4) }}/{{
                        scope.row.drugStuffId.drug.pack.name
                      }}
										</span>
                    <span v-else-if="
                        (scope.row.isExtra == 0 &&
                          scope.row.isUnpackSell == 1 &&
                          scope.row.stuffType == '0') ||
                        scope.row.stuffType == '2'
                      ">
											{{ (scope.row.unitPrice).toFixed(4) }}/{{
                        scope.row.drugStuffId.drug.preparationUnit.name
                      }}
										</span>
                  </template>
                </el-table-column>
                <el-table-column prop="singleDosage" label="单次用量" width="width">
                  <template slot-scope="scope">
                    <template v-if="scope.row.isExtra == 0">{{
                        scope.row.singleDosage
                          ? scope.row.singleDosage
                          : 0 + "" + scope.row.drugStuffId.preparationUnit.name
                      }}
                    </template>
                  </template>
                </el-table-column>
                <el-table-column prop="westernMedicineUse.name" label="用法" width="width">
                  <template slot-scope="scope">
										<span v-if="
                        scope.row.westernMedicineUse != undefined &&
                        scope.row.isExtra == 0
                      ">
											{{ scope.row.westernMedicineUse.name }}
										</span>
                  </template>
                </el-table-column>
                <el-table-column prop="frequency.name" label="频次" width="width">
                  <template slot-scope="scope">
										<span v-if="scope.row.isExtra == 0">
											{{
                        scope.row.frequency.name ? scope.row.frequency.name : ""
                      }}
										</span>
                  </template>
                </el-table-column>

                <!-- <el-table-column prop="drippingSpeed" label="滴速" width="width">
                        <template slot-scope="scope">
                        <template>{{scope.row.drippingSpeed?scope.row.drippingSpeed:0}}滴/分钟</template>
                      </template>
                    </el-table-column> -->
                <el-table-column prop="days.name" label="天数" width="width">
                  <template slot-scope="scope">
                    <template v-if="scope.row.isExtra == 0">{{
                        scope.row.days.name ? scope.row.days.name : ""
                      }}天
                    </template>
                  </template>
                </el-table-column>
                <el-table-column prop="total" label="总量" width="width">
                  <template slot-scope="scope">
                    <template v-if="
                        scope.row.isExtra == 0 &&
                        scope.row.stuffType != '4' &&
                        scope.row.stuffType != '3'
                      ">
                      {{
                        Math.floor(
                          scope.row.total /
                          scope.row.drugStuffId.drug.preparation -
                          0
                        ) >= 0
                          ? Math.floor(
                          scope.row.total /
                          scope.row.drugStuffId.drug.preparation -
                          0
                          ) +
                          scope.row.drugStuffId.drug.pack.name +
                          ((scope.row.total %
                            scope.row.drugStuffId.drug.preparation) -
                          0 >
                          0
                            ? (scope.row.total %
                            scope.row.drugStuffId.drug.preparation) -
                            0 +
                            scope.row.drugStuffId.drug.preparationUnit.name
                            : "")
                          : scope.row.total +
                          scope.row.drugStuffId.drug.preparationUnit.name
                      }}
                    </template>
                  </template>
                </el-table-column>
              </el-table>
            </div>

            <div class="two" v-else-if="props.row.type.value == 'recipelType_1'">
              <el-table :data="filterRecipel(props.row.recipeTemplateDetail)" style="width: 100%">
                <el-table-column label="序号" type="index" align="center">
                </el-table-column>
                <el-table-column prop="drugStuffId.name" label="药品信息" width="width">
                </el-table-column>
                <el-table-column prop="unitPrice" label="单价" width="120">
                  <template slot-scope="scope">
										<span v-if="
                        scope.row.isUnpackSell == 0 &&
                        scope.row.stuffType == '1'
                      ">
											{{ (scope.row.unitPrice).toFixed(4) }}/{{
                        scope.row.drugStuffId.drug.pack.name
                      }}
										</span>
                    <span v-else-if="
                        scope.row.isUnpackSell == 1 &&
                        scope.row.stuffType == '1'
                      ">
											{{ (scope.row.unitPrice).toFixed(4) }}/{{
                        scope.row.drugStuffId.drug.preparationUnit.name
                      }}
										</span>
                  </template>
                </el-table-column>
                <el-table-column prop="singleDosage" label="单次用量" width="width">
                  <template slot-scope="scope">
                    <template v-if="scope.row.stuffType == '1'">{{
                        scope.row.singleDosage +
                        "" +
                        scope.row.drugStuffId.preparationUnit.name
                      }}
                    </template>
                  </template>
                </el-table-column>
                <el-table-column prop="chineseMedicineUse.name" label="用法" width="width">
                  <template slot-scope="scope">
                    <template v-if="scope.row.stuffType == '1'">{{
                        scope.row.chineseMedicineUse.name
                      }}
                    </template>
                  </template>
                </el-table-column>

                <!-- <el-table-column prop="drippingSpeed" label="滴速" width="width">
                        <template slot-scope="scope">
                        <template>{{scope.row.drippingSpeed?scope.row.drippingSpeed:0}}滴/分钟</template>
                      </template>
                    </el-table-column> -->
                <el-table-column prop="total" label="总量" width="width">
                  <template slot-scope="scope">
                    <template v-if="scope.row.stuffType == '1'">
                      {{
                        Math.floor(
                          scope.row.total /
                          scope.row.drugStuffId.drug.preparation -
                          0
                        ) >= 0
                          ? Math.floor(
                          scope.row.total /
                          scope.row.drugStuffId.drug.preparation -
                          0
                          ) +
                          scope.row.drugStuffId.drug.pack.name +
                          ((scope.row.total %
                            scope.row.drugStuffId.drug.preparation) -
                          0 >
                          0
                            ? (scope.row.total %
                            scope.row.drugStuffId.drug.preparation) -
                            0 +
                            scope.row.drugStuffId.drug.preparationUnit.name
                            : "")
                          : scope.row.total +
                          scope.row.drugStuffId.drug.preparationUnit.name
                      }}
                    </template>
                  </template>
                </el-table-column>
              </el-table>
              <div style="margin: 10px 0">
                <el-input style="width: 50px" disabled v-model="props.row.recipeTemplateInfo.dosage">
                </el-input>
                剂 用法：
                <el-input style="width: 100px" disabled
                          v-model="props.row.recipeTemplateInfo.recipelUse.name">
                </el-input>
                <el-input style="width: 100px" disabled
                          v-model="props.row.recipeTemplateInfo.takeFrequency.name">
                </el-input>
                <el-input style="width: 100px" disabled
                          v-model="props.row.recipeTemplateInfo.frequency.name">
                </el-input>
                每次服用
                <el-input style="width: 80px" disabled
                          v-model="props.row.recipeTemplateInfo.singleDosage">
                </el-input>
                {{ "ml" }}
              </div>
            </div>

            <!-- 输液处方 -->
            <div class="two" style="border: 1px solid #dcdcdc"
                 v-else-if="props.row.type.value == 'recipelType_2'">
              <div v-for="(items, index) in getInfuSion(
                  props.row.recipeTemplateDetail
                ).zushu" :key="index" style="border: 1px solid #dcdcdc; padding: 10px; margin: 10px">
                <div>
									<span style="
                      float: left;
                      height: 25px;
                      line-height: 25px;
                      font-size: 14px;
                    ">&nbsp;&nbsp;第{{ index + 1 }}组</span>
                  <span style="
                      float: right;
                      height: 25px;
                      line-height: 25px;
                      font-size: 14px;
                    ">&nbsp;&nbsp;滴/分钟</span>

                  <el-input style="float: right; width: 10%" :value="
                      getInfuSion(props.row.recipeTemplateDetail).drippingSpeed[
                        index
                      ]
                    " placeholder="" :disabled="true">
                  </el-input>

                  <span style="
                      float: right;
                      height: 25px;
                      line-height: 25px;
                      font-size: 14px;
                    ">&nbsp;&nbsp;天&nbsp;&nbsp;</span>
                  <el-input style="float: right; width: 10%" :value="
                      getInfuSion(props.row.recipeTemplateDetail).days[index]
                        .name
                    " placeholder="" :disabled="true">
                  </el-input>

                  <span style="
                      float: right;
                      height: 25px;
                      line-height: 25px;
                      font-size: 18px;
                    ">&nbsp;&nbsp;</span>
                  <el-input style="float: right; width: 10%" :value="
                      getInfuSion(props.row.recipeTemplateDetail).frequency[
                        index
                      ].name
                    " placeholder="" :disabled="true">
                  </el-input>

                  <span style="
                      float: right;
                      height: 25px;
                      line-height: 25px;
                      font-size: 18px;
                    ">&nbsp;&nbsp;</span>
                  <el-input style="float: right; width: 10%" :value="
                      getInfuSion(props.row.recipeTemplateDetail).infuseUse[
                        index
                      ].name
                    " placeholder="" :disabled="true">
                  </el-input>
                </div>
                <div style="border: 17px solid #0000"></div>
                <el-table :data="
                    getInfuSion(props.row.recipeTemplateDetail).infusionProject[
                      index
                    ]
                  " style="width: 100%">
                  <el-table-column label="序号" type="index" align="center" width="width">
                  </el-table-column>
                  <el-table-column prop="drugStuffId.name" label="药品名称" width="width">
                  </el-table-column>
                  <el-table-column prop="skinTest.name" label="皮试" width="width">
                  </el-table-column>
                  <el-table-column prop="drugStuffId.drug.factory.name" label="厂家" width="width">
                  </el-table-column>
                  <el-table-column prop="unitPrice" label="单价" width="120">
                    <template slot-scope="scope">
											<span>{{ (scope.row.unitPrice).toFixed(4) }}元/{{
                          scope.row.isUnpackSell == 1
                            ? scope.row.drugStuffId.preparationUnit.name
                            : scope.row.drugStuffId.pack.name
                        }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="singleDosage" label="单次用量" width="width">
                    <template slot-scope="scope">
                      <template>{{
                          scope.row.singleDosage +
                          "" +
                          scope.row.drugStuffId.preparationUnit.name
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
                            scope.row.total /
                            scope.row.drugStuffId.stuff.packNumber
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
                              (scope.row.drugStuffId.drug.preparation -
                                0)) +
                              scope.row.drugStuffId.drug.preparationUnit
                                .name
                              : "")
                            : scope.row.total +
                            scope.row.drugStuffId.drug.preparationUnit.name
                        }}
											</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="allFee" label="总额" width="width">
                    <template slot-scope="scope">
                      {{ scope.row.allFee }}元
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>

            <!-- 诊疗项目 -->

            <div class="two" v-else-if="props.row.type.value == 'recipelType_3'">
              <el-table :data="filterRecipel(props.row.recipeTemplateDetail)" style="width: 100%">
                <el-table-column type="index" label="序号" width="50" align="center"></el-table-column>
                <el-table-column prop="drugStuffId.name" label="项目名称" align="center"></el-table-column>
                <el-table-column prop="drugStuffId.costItem.itemType.name" label="项目类别" align="center"
                                 width="100"></el-table-column>
                <el-table-column prop="drugStuffId.costItem.isPackage" label="套餐项目" align="center"
                                 width="100">
                  <template slot-scope="scope">
                    {{
                      scope.row.drugStuffId.costItem.isPackage === "1"
                        ? "是"
                        : "否"
                    }}
                  </template>
                </el-table-column>
                <el-table-column prop="total" label="数量" align="center" width="120">
                  <template slot-scope="scope">
                    {{ scope.row.total }} {{ scope.row.drugStuffId.pack.name }}
                  </template>
                </el-table-column>
                <el-table-column label="单价" width="120" align="center">
                  <template slot-scope="scope">
                    {{ (scope.row.drugStuffId.price).toFixed(4) }}/{{
                      scope.row.drugStuffId.pack.name
                    }}
                  </template>
                </el-table-column>
                <el-table-column prop="allFee" label="合计金额" width="120" align="center">
                </el-table-column>
              </el-table>
            </div>

            <br/>
            <div class="two" style="margin: 10px">
              <el-divider style="margin-top: 20px" content-position="left">附加费</el-divider>
              <el-table :data="filterAdditional(props.row.recipeTemplateDetail)" style="width: 100%">
                <el-table-column label="序号" type="index" align="center" width="120">
                </el-table-column>
                <el-table-column prop="drugStuffId.name" label="名称" width="width">
                  <template slot-scope="scope">
										<span>
											{{ scope.row.drugStuffId.name }}
										</span>
                  </template>
                </el-table-column>

                <el-table-column prop="unitPrice" label="单价" width="120">
                  <template slot-scope="scope">
										<span>{{ (scope.row.unitPrice).toFixed(4) }}元/{{
                        scope.row.isUnpackSell == 1
                          ? scope.row.drugStuffId.preparationUnit.name
                          : scope.row.drugStuffId.pack.name
                      }}</span>
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
                          scope.row.total /
                          scope.row.drugStuffId.stuff.packNumber
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
                            (scope.row.drugStuffId.drug.preparation -
                              0)) +
                            scope.row.drugStuffId.drug.preparationUnit.name
                            : "")
                          : scope.row.total +
                          scope.row.drugStuffId.drug.preparationUnit.name
                      }}
										</span>
                  </template>
                </el-table-column>
                <el-table-column prop="allFee" label="总额" width="width">
                  <template slot-scope="scope">
                    <span> {{ scope.row.allFee }}元 </span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <el-row>
              <el-divider content-position="left">医嘱事项</el-divider>
              <el-input type="textarea" :rows="4" v-model="props.row.recipeTemplateInfo.entrust" disabled>
              </el-input>
            </el-row>
          </template>
        </el-table-column>

        <el-table-column label="序号" type="index"></el-table-column>
        <el-table-column prop="recipetemplateName" label="模板名称" width="width">
        </el-table-column>
        <el-table-column prop="category" label="模板类别" width="width">
          <template slot-scope="scope">
            <span v-if="scope.row.category == '0'"> 私人模板 </span>
            <span v-if="scope.row.category == '1'"> 公用模板 </span>
            <span v-if="scope.row.category == '2'"> AI模板 </span>
          </template>
        </el-table-column>
        <el-table-column prop="diagnosis" label="诊断" width="width">
        </el-table-column>
        <el-table-column prop="createDate" label="创建时间" width="width">
        </el-table-column>
        <el-table-column prop="" label="操作">
          <template slot-scope="scope">
            <el-button type="text" @click="invoking2(scope.row)">调用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- <span slot="footer" class="dialog-footer">
        <el-button @click="templateDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="templateDialogVisible = false">确 定</el-button>
      </span> -->
    </el-dialog>

    <!-- 保存模板 -->
    <el-dialog title="保存模板处方" :visible.sync="saveTemplateDialogVisible" width="40%" :before-close="handleClose">
      <el-form :model="saveTemplateBizFormModel" :rules="templateFormRules" ref="saveTemplateBizFormModel"
               label-width="120px" label-position="right" class="edit-form">
        <div class="tab-item">
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="模板名称" prop="recipetemplateName">
                <el-input v-model="saveTemplateBizFormModel.recipetemplateName" :maxlength="100"
                          :placeholder="'请输入模板名称'"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="模板类别" prop="category">
                <el-radio v-model="saveTemplateBizFormModel.category" label="0">私人模板</el-radio>
                <el-radio v-model="saveTemplateBizFormModel.category" label="1">公开模板</el-radio>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24 / 2">
              <el-form-item label="诊断" prop="diagnosis">
                <el-input v-model="saveTemplateBizFormModel.diagnosis" :maxlength="100"
                          :placeholder="'请输入诊断'"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24 / 2">
              <el-form-item label="模板说明" prop="remarks">
                <el-input v-model="saveTemplateBizFormModel.remarks" :maxlength="100"
                          :placeholder="'请输入模板说明'"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>
      <span slot="footer" class="dialog-footer">
				<el-button @click="saveTemplateDialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="saveRecipeTemplate('saveTemplateBizFormModel')">保 存</el-button>
			</span>
    </el-dialog>

    <!-- 填写报告弹出框 -->
    <el-dialog :title="typeName" :visible.sync="inspectionDialogVisible" v-if="inspectionDialogVisible" width="75%"
               :before-close="handleClose">
      <div slot="title" class="dialog-header">
        {{ typeName }}
      </div>
      <div v-if="costInspection.costItem.itemType.value == 'treatmentItemType_0'">
        <el-form :model="costInspection.inspectionCheckInfo" :rules="formRules" ref="bizFormModel"
                 label-width="120px" label-position="right" class="edit-form">
          <!-- <el-card class="box-card main-card"> -->

          <div style="margin-left: -60px">
            <el-row>
              <el-col :span="24 / 4">
                <el-form-item label="姓名" prop="name">
                  <el-input :disabled="true" v-model="costInspection.inspectionCheckInfo.name"
                            :maxlength="120">
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 4">
                <el-form-item label="采样时间" prop="samplingDate">
                  <!-- <el-input :disabled='true' v-model='bizFormModel.samplingDate'></el-input> -->
                  <el-date-picker :disabled="true"
                                  v-model="costInspection.inspectionCheckInfo.samplingDate" type="datetime"
                                  placeholder="选择日期时间" value-format="yyyy-MM-dd HH:mm:ss"
                                  format="yyyy-MM-dd HH:mm:ss">
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 4">
                <el-form-item label="样本号" prop="samplingNum">
                  <el-input placeholder="请输入内容" :disabled="true"
                            v-model="costInspection.inspectionCheckInfo.samplingNum" :maxlength="100">
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 4">
                <el-form-item label="检验仪器" prop="instrument">
                  <el-input placeholder="请输入内容" :disabled="true"
                            v-model="costInspection.inspectionCheckInfo.instrument" :maxlength="100">
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24 / 4">
                <el-form-item label="检验号" prop="checkoutNum">
                  <el-input placeholder="请输入内容" :disabled="true"
                            v-model="costInspection.inspectionCheckInfo.checkoutNum" :maxlength="100">
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 4">
                <el-form-item label="检验时间" prop="checkoutDate">
                  <el-date-picker :disabled="true"
                                  v-model="costInspection.inspectionCheckInfo.checkoutDate" type="datetime"
                                  placeholder="选择日期时间" value-format="yyyy-MM-dd HH:mm:ss"
                                  format="yyyy-MM-dd HH:mm:ss">
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 4">
                <el-form-item label="检验科室" prop="checkoutOffice">
                  <el-input placeholder="请输入内容" :disabled="true"
                            v-model="costInspection.inspectionCheckInfo.checkoutOffice" :maxlength="100">
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- </el-card> -->
          <br/>
          <el-table :data="costInspection.inspectionCheckInfo.inspectionCheckDetails" style="width: 100%">
            <el-table-column type="index" width="50" label="序号">
            </el-table-column>
            <el-table-column prop="costItem.itemName" label="项目名称" width="width">
            </el-table-column>
            <el-table-column prop="conclusion" label="结果" width="width">
              <template slot-scope="scope">
                <el-input placeholder="请输入内容" :disabled="true" v-model="scope.row.conclusion"
                          :maxlength="100">
                </el-input>
              </template>
            </el-table-column>
            <el-table-column prop="sign" label="高低标记" width="width">
              <template slot-scope="scope">
                <el-select :disabled="true" v-model="scope.row.sign" placeholder="请选择">
                  <el-option v-for="item in signList" :key="item.value" :label="item.name"
                             :value="item.value">
                  </el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="costItem.costItemPackages[0].referenceValue" label="参考标记" width="width">
              <template slot-scope="scope">
                <el-input :disabled="true" type="textarea" v-model="
                    scope.row.costItem.costItemPackages[0].referenceValue
                  ">
                </el-input>
              </template>
            </el-table-column>
            <el-table-column prop="costItem.costItemPackages[0].referenceUnit" label="单位" width="width">
            </el-table-column>

            <el-table-column prop="describes" label="描述" width="width">
              <template slot-scope="scope">
                <el-input :disabled="true" v-model="scope.row.describes" :maxlength="100">
                </el-input>
              </template>
            </el-table-column>
          </el-table>
          <br/>
          <div style="margin-left: -50px">
            <el-form-item label="报告结论" prop="reportConclusion">
              <el-input :disabled="true" type="textarea" :autosize="{ minRows: 2, maxRows: 100 }"
                        placeholder="请输入内容" v-model="costInspection.inspectionCheckInfo.reportConclusion">
              </el-input>
            </el-form-item>
          </div>
          <br/>

          <el-form-item label="" prop="">
            <div style="margin-left: -50px">
              <upload-file :objectId="uploadFiles" :action="true"></upload-file>
            </div>
          </el-form-item>
        </el-form>
      </div>
      <div v-if="costInspection.costItem.itemType.value == 'treatmentItemType_1'">
        <el-form :model="costInspection.inspectionCheckInfo" :rules="formRules" ref="bizFormModel"
                 label-width="120px" label-position="right" class="edit-form">
          <div style="margin-left: -60px">
            <el-row>
              <el-col :span="24 / 4">
                <el-form-item label="姓名" prop="name">
                  <el-input :disabled="true" v-model="costInspection.inspectionCheckInfo.name"
                            :maxlength="120">
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 4">
                <el-form-item label="登记时间" prop="samplingDate">
                  <el-date-picker :disabled="flags"
                                  v-model="costInspection.inspectionCheckInfo.samplingDate" type="datetime"
                                  placeholder="选择日期时间" value-format="yyyy-MM-dd HH:mm:ss"
                                  format="yyyy-MM-dd HH:mm:ss">
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 4">
                <el-form-item label="检验设备" prop="instrument">
                  <el-input :disabled="true" placeholder="请输入内容"
                            v-model="costInspection.inspectionCheckInfo.instrument" :maxlength="100">
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 4">
                <el-form-item label="检查类型" prop="checkType">
                  <el-input :disabled="true" placeholder="请输入内容"
                            v-model="costInspection.inspectionCheckInfo.checkType" :maxlength="100">
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24 / 4">
                <el-form-item label="检查部位" prop="checkPart">
                  <el-input :disabled="true" placeholder="请输入内容"
                            v-model="costInspection.inspectionCheckInfo.checkPart" :maxlength="100">
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 4">
                <el-form-item label="报告医师" prop="doctor">
                  <el-input :disabled="true" placeholder="请输入内容"
                            v-model="costInspection.inspectionCheckInfo.doctor" :maxlength="100"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 4">
                <el-form-item label="检查项目" prop="checkProject">
                  <el-input :disabled="true" placeholder="请输入内容"
                            v-model="costInspection.inspectionCheckInfo.checkProject" :maxlength="100">
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24 / 4">
                <el-form-item label="报告时间" prop="checkoutDate">
                  <el-date-picker :disabled="true"
                                  v-model="costInspection.inspectionCheckInfo.checkoutDate" type="datetime"
                                  placeholder="选择日期时间" value-format="yyyy-MM-dd HH:mm:ss"
                                  format="yyyy-MM-dd HH:mm:ss">
                  </el-date-picker>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24 / 1">
                <el-form-item label="检查所见" prop="checkAdvise">
                  <el-input type="textarea" width="100%" :disabled="true"
                            v-model="costInspection.inspectionCheckInfo.checkAdvise" :maxlength="100">
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24 / 1">
                <el-form-item label="意见" prop="opinion">
                  <el-input type="textarea" width="100%" :disabled="true"
                            v-model="costInspection.inspectionCheckInfo.opinion" :maxlength="100">
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="" prop="">
              <div>
                <upload-file :objectId="uploadFiles" :action="false"></upload-file>
              </div>
            </el-form-item>
          </div>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
				<el-button v-if="true" @click="inspectionDialogVisible = false">取 消</el-button>
        <!-- <el-button type="primary" v-if="!flags" @click="save">保 存</el-button> -->
			</span>
    </el-dialog>

    <!-- 模板处方弹出框 -->
    <el-dialog title="病历模板调用" :visible.sync="blmbDialogVisible" width="50%" :before-close="handleClose">
      <el-car>
        <el-row>
          <el-col :span="24">
            <el-form :inline="true" :model="blmbcxrc" class="demo-form-inline">
              <el-form-item label="模板名称" style="margin-right: 3px">
                <el-input v-model="blmbcxrc.mbmc" placeholder="请输入模板名称"></el-input>
              </el-form-item>

              <el-form-item style="margin-right: 3px">
                <el-button type="primary" @click="blmbdialogonSubmit">查询</el-button>
                <el-button type="info" icon="el-icon-refresh-left" @click="blmbdialogresetCondition"
                           :plain="true">重置
                </el-button>
              </el-form-item>
            </el-form>
          </el-col>
          <el-rol :span="24">
            <el-table :data="blmbtableData" :row-key="blmbgetRowKeys" :expand-row-keys="blmbexpands"
                      @expand-change="blmbexpandSelect" style="width: 100%">
              <el-table-column type="expand">
                <template slot-scope="props">
                  <el-form label-position="left" inline class="demo-table-expand">
                    <el-form-item label="模板编码" v-if="blmbshow">
                      <el-tooltip :content="props.row.mbbm" placement="top-start" effect="dark"
                                  popper-class="atooltip">
												<span style="
                            width: 100%;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            white-space: nowrap;
                            display: inline-block;
                          ">
													{{ props.row.mbbm }}
												</span>
                      </el-tooltip>
                    </el-form-item>

                    <el-form-item style="padding-left: 10%" v-for="(item, index) in blmbList"
                                  :key="index" :label="item.label"
                                  v-show="item.value != '' && item.value != null">
                      <el-tooltip :content="item.value" placement="top-start" effect="dark"
                                  popper-class="atooltip">
												<span style="
                            width: 100%;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            white-space: nowrap;
                            display: inline-block;
                          ">
													{{ item.value }}
												</span>
                      </el-tooltip>
                    </el-form-item>
                  </el-form>
                </template>
              </el-table-column>
              <el-table-column label="序号" type="index" :index="blmbindexMethod" align="center">
              </el-table-column>
              <el-table-column prop="mbbm" v-if="blmbshow" label="模板编码">
              </el-table-column>
              <el-table-column prop="mbmc" label="模板名称"></el-table-column>
              <el-table-column prop="mblx" label="模板类型"></el-table-column>
              <el-table-column prop="bllx" label="病历类型"></el-table-column>
              <el-table-column fixed="right" label="操作" width="80">
                <template slot-scope="scope">
                  <el-button @click.native="diaoyong(scope.row)" type="text" size="small">调用
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-rol>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-pagination background @size-change="blmbonSizeChange" @current-change="blmbonCurrentChange"
                           :current-page.sync="blmbcurrentPage" :page-sizes="[20, 50, 100, blmbpatientTotal]"
                           :page-size="20" layout="total, sizes, prev, pager, next, jumper" :total="blmbpatientTotal">
            </el-pagination>
          </el-col>
        </el-row>
      </el-car>
    </el-dialog>

    <el-dialog title="病历模板保存类型" :visible.sync="blmblxDialogVisible" width="30%" :before-close="handleClose">
      <el-car>
        <el-row>
          <el-form :inline="true" :model="blmbmbmc" class="demo-form-inline" :rules="blmbrules"
                   ref="blmbmbmc">
            <el-col :span="24">
              <el-form-item prop="mbmc" label="模板名称">
                <el-input v-model="blmbmbmc.mbmc"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="模板类型">
                <el-radio v-model="blmbmblx" label="0">通用</el-radio>
                <el-radio v-model="blmbmblx" label="1">个人</el-radio>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="病历类型">
                <el-radio v-model="blmbbllx" label="0">西医病历</el-radio>
                <el-radio v-model="blmbbllx" label="1">中医病历</el-radio>
              </el-form-item>
            </el-col>
          </el-form>
        </el-row>
      </el-car>
      <span slot="footer" class="dialog-footer">
				<el-button :disabled="flage" type="primary" :plain="true" @click="saveblmbsubmit('blmbmbmc')">保 存
				</el-button>
				<el-button :plain="true" type="primary" @click="blmbonDialogClose()">取 消</el-button>
			</span>
    </el-dialog>
  </el-container>
</template>

<script>
  import {
    listDictItemAll
  } from "@/api/sys/dictItem";
  import {
    listAllStock
  } from "@/api/stock/drug";
  import {
    listCostItemAll
  } from "@/api/treatment/costItem";
  import {
    listStuffAll
  } from "@/api/stock/stuff";
  import UploadFile from "../../components/uploadFile.vue";
  import {
    getblmblist,
    saveblmb
  } from "@/api/outpatient/blmb";
  import {
    listRegistrationPage,
    getRegistrationById,
    updateStatus,
  } from "@/api/outpatient/registration";
  import {
    allSaveMedicalRecord,
    allQueryMedicalRecord,
    ureport,
    editSave,
    getHistoryRecipel,
    getrecordpatlist,
  } from "@/api/outpatient/medicalRecord";
  import {
    listAll
  } from "@/api/stock/medicinalStockControl";
  import {
    listRecipelInfoAll,
    invalidStatus,
  } from "@/api/outpatient/recipelInfo";
  import {
    listRecipetemplatePage,
    getRecipetemplateById,
    deleteRecipetemplate,
  } from "@/api/outpatient/recipetemplate";
  import {
    getByRecipelInfoId
  } from "@/api/outpatient/recipelDetail";
  import {
    getPatientById
  } from "@/api/outpatient/patient";
  import {
    saveRecipetemplate,
    allSave
  } from "@/api/outpatient/recipetemplate";
  import NP from "number-precision";
  import {
    BigNumber
  } from "bignumber.js";
  import {
    getByPatientId, getPoverty
  } from "@/api/member/memberManagement";
  import {listSysParamConfigAll} from '@/api/sys/sysParamConfig';
  import {getLocalCurrentCompany} from "@/utils/auth";
  import moment from "moment";

  export default {
    components: {
      UploadFile,
    },
    name: "index",
    methods: {
      getFileList(fileList) {
        console.log(fileList, 'getFileList');
      },
      deleteFile(fileId) {
        console.log(fileId, 'deleteFile');
        this.deleteFileIdList.push(fileId);
      },
      beforeRemove(file, fileList) {
        console.log(file, fileList, 'beforeRemove');
      },
      onChronicDiseaseChange(newVal, oldVal) {
        this.MedicalCalculate();
      },
      //查看检验检查
      lookInspection(row) {
        console.log(row, "是卡夫卡解放立刻");
        if (row.costItem.itemType.value == "treatmentItemType_0") {
          this.costInspection = row;
          this.typeName = "查看检验报告";
          this.costInspection.inspectionCheckInfo.name =
            this.BasicInfoModel.name +
            "/" +
            this.BasicInfoModel.gender.name +
            "/" +
            this.BasicInfoModel.age +
            "岁";

          this.uploadFiles = this.costInspection.inspectionCheckInfo.id;
          console.log(this.costInspection, "技术方案看见分厘卡");
          this.inspectionDialogVisible = true;
        } else if (row.costItem.itemType.value == "treatmentItemType_1") {
          this.costInspection = row;
          this.typeName = "查看检查报告";
          this.costInspection.inspectionCheckInfo.name =
            this.BasicInfoModel.name +
            "/" +
            this.BasicInfoModel.gender.name +
            "/" +
            this.BasicInfoModel.age +
            "岁";

          this.uploadFiles = this.costInspection.inspectionCheckInfo.id;
          this.inspectionDialogVisible = true;
        }
      },

      //基础资料刷新
      refresh() {
        this.$forceUpdate();
      },
      exchage() {
        this.$forceUpdate();
      },
      saveRecipeTemplate(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.saveTemplateBizFormModel.company = this.Company;
            this.saveTemplateBizFormModel.type.value = this.saveTemplateType;
            this.saveTemplateBizFormModel.createId = currentUser.id;
            this.saveTemplateBizFormModel.recipeTemplateInfo =
              this.medicalEditTabs[this.saveTemplateIndex].content.recipelInfo;
            this.saveTemplateBizFormModel.recipeTemplateDetail =
              this.medicalEditTabs[
                this.saveTemplateIndex
                ].content.recipelDetailEvtList;
            console.log(this.saveTemplateBizFormModel, "看结果");
            allSave(this.saveTemplateBizFormModel)
              .then((responseData) => {
                if (responseData.code == 100) {
                  this.$message.success("保存成功");

                  this.saveTemplateDialogVisible = false;
                } else {
                  //this.showMessage(responseData)
                }
              })
              .catch((error) => {
                this.outputError(error);
                this.$message.error(
                  "后台数据异常，请查看数据是否有否或者联系管理员解决！"
                );
              });
          } else {
            this.$message.error("请填写模板名称或者诊断说明！");
            return false;
          }
        });
      },

      //保存模板
      saveTemplate(item, index) {
        this.saveTemplateType = item;
        this.saveTemplateIndex = index;
        this.saveTemplateBizFormModel = {
          type: {
            // 模板类型
            value: null,
            name: null,
          },
          company: {
            // 诊所id
            id: "",
            name: null,
          },
          recipetemplateName: "",
          category: "0",
          diagnosis: "",
          remarks: "",
          recipeTemplateInfo: {},
          recipeTemplateDetail: [{}],
          createId: "",
        };
        this.saveTemplateDialogVisible = true;
      },

      //模板处方搜索事件
      searchTemplate() {
        console.log(this.BasicInfoModel, "name");
        console.log(this.BasicInfoModel.id, "id");
        console.log(this.templateQueryModel.category, "hfjsafakjfkja");
        let search = {
          columnName: "",
          limit: 1000,
          offset: 0,
          order: "",
          params: [{
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
            {
              columnName: "type",
              queryType: "=",
              value: this.templateType,
            },
          ],
        };

        if (this.templateQueryModel.category === "0") {
          search.params.push({
            columnName: "category",
            queryType: "=",
            value: "0",
          });
          search.params.push({
            columnName: "create_id",
            logic: "and",
            queryType: "=",
            value: currentUser.id,
          });
        } else if (this.templateQueryModel.category === "1") {
          search.params.push({
            columnName: "category",
            queryType: "=",
            value: "1",
          });
        } else if (this.templateQueryModel.category === "2") {
          search.params.push({
            columnName: "category",
            queryType: "=",
            value: "2",
          }, {
            columnName: "patient_id",
            logic: "and",
            queryType: "=",
            value: this.BasicInfoModel.id,
          });
        } else {
          search.params.push({
            logic: "AND",
            queryType: "(",
          }, {
            columnName: "category",
            logic: "",
            queryType: "=",
            value: 0,
          }, {
            columnName: "create_id",
            logic: "and",
            queryType: "=",
            value: currentUser.id,
          }, {
            columnName: "category",
            logic: "or",
            queryType: "=",
            value: 1,
          }, {
            logic: "",
            queryType: ")",
          });
        }
        this.templateRecipelList = null;
        listRecipetemplatePage(search)
          .then((responseData) => {
            if (responseData.code == 100) {
              responseData.data.rows.forEach((item) => {
                item.recipeTemplateDetail.forEach((recipeTemplateDetailItem) => {
                  if (
                    recipeTemplateDetailItem.frequency.value == undefined ||
                    recipeTemplateDetailItem.frequency.value == null
                  ) {
                    recipeTemplateDetailItem.frequency.value = "";
                  }
                });
              });
              this.templateRecipelList = responseData.data.rows;
              console.log(this.templateRecipelList, "这就是美丽");
            } else {
            }
            this.resetLoad();
          })
          .catch((error) => {
          });
      },
      //获取输液处方
      getInfuSion(element) {
        let infusion = {
          defaultNumber: 1, //默认组号
          infusionProject: [
            []
          ],
          drippingSpeed: [""], //滴速
          days: [{}], //天数
          frequency: [{}], //频次
          infuseUse: [{}], //用法
          zushu: [1],
          excharge: [],
        };
        let count = 0;
        for (let i = 0; i < element.length; i++) {
          if (element[i].isExtra === 0) {
            if (count < element[i].infuseGroup) {
              count = element[i].infuseGroup;
            }
          }
        }

        for (let i = 1; i < count; i++) {
          infusion.defaultNumber = infusion.defaultNumber + 1;
          infusion.zushu.push(infusion.defaultNumber);
          infusion.infusionProject.push([]);
          infusion.drippingSpeed.push("");
          infusion.days.push({});
          infusion.frequency.push({});
          infusion.infuseUse.push({});
        }
        //this.excharge=null
        let arr = [];
        for (let i = 0; i < element.length; i++) {
          if (element[i].isExtra != 1) {
            infusion.infusionProject[element[i].infuseGroup - 1].push(element[i]);
            infusion.drippingSpeed[element[i].infuseGroup - 1] =
              element[i].drippingSpeed;
            infusion.days[element[i].infuseGroup - 1] = element[i].days;
            infusion.frequency[element[i].infuseGroup - 1] = element[i].frequency;
            infusion.infuseUse[element[i].infuseGroup - 1] = element[i].infuseUse;
          } else {
            infusion.excharge.push(element[i]);
            //arr.push(element.recipelDetailEvtList[i])
          }
        }
        return infusion;
      },
      //过滤处方
      filterRecipel(row) {
        let addObject = row;
        addObject = addObject.filter((item) => item.isExtra != 1);
        return addObject;
      },
      //过滤附加费
      filterAdditional(row) {
        let addObject = row;
        addObject = addObject.filter((item) => item.isExtra != 0);
        return addObject;
      },

      invoking2(row) {
        console.log(this.medicalEditTabs[this.singleRecipel], "看fasfakjflak");
        if (this.medicalEditTabs[this.singleRecipel].type == "recipelType_0") {
          let recipelDetailEvtLists = row.recipeTemplateDetail.filter(
            (item) => item.isExtra == 0
          );
          if (
            this.medicalEditTabs[this.singleRecipel].content.recipelDetailEvtList
              .length +
            recipelDetailEvtLists.length >
            5
          ) {
            this.$message.error("调用处方后药品数量超过5个，请重新规划处方！");
            return;
          }
        }

        console.log(row, "看看这是啥");
        if (
          this.medicalEditTabs[this.singleRecipel].content.recipelDetailEvtList
            .length == 0
        ) {
          this.medicalEditTabs[this.singleRecipel].content.recipelDetailEvtList =
            row.recipeTemplateDetail;
        } else if (
          this.medicalEditTabs[this.singleRecipel].type == "recipelType_2"
        ) {
          this.medicalEditTabs[this.singleRecipel].content.recipelDetailEvtList =
            row.recipeTemplateDetail;
          setTimeout(() => {
            this.MedicalCalculate();
          }, 0);
        } else {
          this.$confirm("该处方已填写，请选择覆盖还是添加", "提示", {
            confirmButtonText: "覆盖",
            cancelButtonText: "添加",
            type: "warning",
          })
            .then(() => {
              this.medicalEditTabs[
                this.singleRecipel
                ].content.recipelDetailEvtList = row.recipeTemplateDetail;
              setTimeout(() => {
                this.MedicalCalculate();
              }, 0);
            })
            .catch(() => {
              let flage = true;
              let arr = [];
              row.recipeTemplateDetail.forEach((item) => {
                this.medicalEditTabs[
                  this.singleRecipel
                  ].content.recipelDetailEvtList = this.medicalEditTabs[
                  this.singleRecipel
                  ].content.recipelDetailEvtList.filter(
                  (recipelDetai) =>
                    //保留存在相同药品是报错的逻辑
                    // if(item.drugStuffId.drugStuffId==recipelDetai.drugStuffId.drugStuffId){
                    //   this.$message({
                    //     type: 'error',
                    //     message: '存在相同药品，无法调用处方！'
                    //   });
                    //   flage=false;
                    //   return;
                    // }
                    // if(){

                    // }

                    //下面是直接进行覆盖
                    item.drugStuffId.drugStuffId !=
                    recipelDetai.drugStuffId.drugStuffId
                );
              });

              row.recipeTemplateDetail.forEach((item) => {
                this.medicalEditTabs[
                  this.singleRecipel
                  ].content.recipelDetailEvtList.push(item);
              });

              setTimeout(() => {
                this.MedicalCalculate();
              }, 0);
            });
        }
        if (this.medicalEditTabs[this.singleRecipel].type == "recipelType_1") {
          this.medicalEditTabs[this.singleRecipel].content.recipelInfo.dosage =
            row.recipeTemplateInfo.dosage;
          this.medicalEditTabs[this.singleRecipel].content.recipelInfo.frequency =
            row.recipeTemplateInfo.frequency;
          this.medicalEditTabs[
            this.singleRecipel
            ].content.recipelInfo.takeFrequency =
            row.recipeTemplateInfo.takeFrequency;
          this.medicalEditTabs[
            this.singleRecipel
            ].content.recipelInfo.singleDosage =
            row.recipeTemplateInfo.singleDosage;
          this.medicalEditTabs[
            this.singleRecipel
            ].content.recipelInfo.recipelUse = row.recipeTemplateInfo.recipelUse;
        } else if (
          this.medicalEditTabs[this.singleRecipel].type == "recipelType_2"
        ) {
          let infusion = {
            defaultNumber: 1, //默认组号
            infusionProject: [
              []
            ],
            drippingSpeed: [""], //滴速
            days: [{}], //天数
            frequency: [{}], //频次
            infuseUse: [{}], //用法
            zushu: [1],
            excharge: [],
          };
          let count = 0;
          for (let i = 0; i < row.recipeTemplateDetail.length; i++) {
            if (row.recipeTemplateDetail[i].isExtra === 0) {
              if (count < row.recipeTemplateDetail[i].infuseGroup) {
                count = row.recipeTemplateDetail[i].infuseGroup;
              }
            }
          }

          for (let i = 1; i < count; i++) {
            infusion.defaultNumber = infusion.defaultNumber + 1;
            infusion.zushu.push(infusion.defaultNumber);
            infusion.infusionProject.push([]);
            infusion.drippingSpeed.push("");
            infusion.days.push({});
            infusion.frequency.push({});
            infusion.infuseUse.push({});
          }
          //this.excharge=null
          let arr = [];
          for (let i = 0; i < row.recipeTemplateDetail.length; i++) {
            if (row.recipeTemplateDetail[i].isExtra != 1) {
              infusion.infusionProject[
              row.recipeTemplateDetail[i].infuseGroup - 1
                ].push(row.recipeTemplateDetail[i]);
              infusion.drippingSpeed[
              row.recipeTemplateDetail[i].infuseGroup - 1
                ] = row.recipeTemplateDetail[i].drippingSpeed;
              infusion.days[row.recipeTemplateDetail[i].infuseGroup - 1] =
                row.recipeTemplateDetail[i].days;
              infusion.frequency[row.recipeTemplateDetail[i].infuseGroup - 1] =
                row.recipeTemplateDetail[i].frequency;
              infusion.infuseUse[row.recipeTemplateDetail[i].infuseGroup - 1] =
                row.recipeTemplateDetail[i].infuseUse;
            } else {
              infusion.excharge.push(row.recipeTemplateDetail[i]);
              //arr.push(element.recipelDetailEvtList[i])
            }
          }
          this.medicalEditTabs[this.singleRecipel].infusion = infusion;
        }
        if (
          row.recipeTemplateInfo.entrust != undefined &&
          row.recipeTemplateInfo.entrust != ""
        ) {
          this.medicalEditTabs[this.singleRecipel].content.recipelInfo.entrust =
            row.recipeTemplateInfo.entrust;
        }
        setTimeout(() => {
          this.MedicalCalculate();
        }, 0);
        this.templateDialogVisible = false;
        console.log(this.medicalEditTabs, "这是一个值的额");
      },
      medicalHistoryInvoking(row) {
        this.ChooseRecordList = [];
        this.MedicalRecordModel = row.medicalRecord
        this.MedicineRecordLabelList.map((item) => {
          if (this.MedicalRecordModel[`${item.name}`]) {
            this.ChooseRecordList.push(item);
          }
        });
        console.log("看看这是什么rows-===" + row.medicalRecord)
        console.log("看看这是什么this-===" + this.MedicalRecordModel)
        this.caseDialogVisible = false;
      },

      invoking(row) {
        if (this.medicalEditTabs[this.singleRecipel].type == "recipelType_0") {
          if (
            this.medicalEditTabs[this.singleRecipel].content.recipelDetailEvtList
              .length +
            row.recipelDetail.length >
            5
          ) {
            this.$message.error("调用处方后药品数量超过5个，请重新规划处方！");
            return;
          }
        }
        console.log(this.medicalEditTabs[this.singleRecipel], "看fasfakjflak");
        console.log(row, "看看这是啥");
        if (
          this.medicalEditTabs[this.singleRecipel].content.recipelDetailEvtList
            .length == 0
        ) {
          this.medicalEditTabs[this.singleRecipel].content.recipelDetailEvtList =
            row.recipelDetail;
        } else if (
          this.medicalEditTabs[this.singleRecipel].type == "recipelType_2"
        ) {
          this.medicalEditTabs[this.singleRecipel].content.recipelDetailEvtList =
            row.recipelDetail;
          setTimeout(() => {
            this.MedicalCalculate();
          }, 0);
        } else {
          this.$confirm("该处方已填写，请选择覆盖还是添加", "提示", {
            confirmButtonText: "覆盖",
            cancelButtonText: "添加",
            type: "warning",
          })
            .then(() => {
              this.medicalEditTabs[
                this.singleRecipel
                ].content.recipelDetailEvtList = row.recipelDetail;
              setTimeout(() => {
                this.MedicalCalculate();
              }, 0);
            })
            .catch(() => {
              let flage = true;
              let arr = [];
              row.recipelDetail.forEach((item) => {
                this.medicalEditTabs[
                  this.singleRecipel
                  ].content.recipelDetailEvtList = this.medicalEditTabs[
                  this.singleRecipel
                  ].content.recipelDetailEvtList.filter(
                  (recipelDetai) =>
                    //保留存在相同药品是报错的逻辑
                    // if(item.drugStuffId.drugStuffId==recipelDetai.drugStuffId.drugStuffId){
                    //   this.$message({
                    //     type: 'error',
                    //     message: '存在相同药品，无法调用处方！'
                    //   });
                    //   flage=false;
                    //   return;
                    // }
                    // if(){

                    // }

                    //下面是直接进行覆盖
                    item.drugStuffId.drugStuffId !=
                    recipelDetai.drugStuffId.drugStuffId
                );
              });

              row.recipelDetail.forEach((item) => {
                this.medicalEditTabs[
                  this.singleRecipel
                  ].content.recipelDetailEvtList.push(item);
              });

              setTimeout(() => {
                this.MedicalCalculate();
              }, 0);
            });
        }
        if (this.medicalEditTabs[this.singleRecipel].type == "recipelType_1") {
          this.medicalEditTabs[this.singleRecipel].content.recipelInfo.dosage =
            row.dosage;
          this.medicalEditTabs[this.singleRecipel].content.recipelInfo.frequency =
            row.frequency;
          this.medicalEditTabs[
            this.singleRecipel
            ].content.recipelInfo.takeFrequency = row.takeFrequency;
          this.medicalEditTabs[
            this.singleRecipel
            ].content.recipelInfo.singleDosage = row.singleDosage;
          this.medicalEditTabs[
            this.singleRecipel
            ].content.recipelInfo.recipelUse = row.recipelUse;
        } else if (
          this.medicalEditTabs[this.singleRecipel].type == "recipelType_2"
        ) {
          let infusion = {
            defaultNumber: 1, //默认组号
            infusionProject: [
              []
            ],
            drippingSpeed: [""], //滴速
            days: [{}], //天数
            frequency: [{}], //频次
            infuseUse: [{}], //用法
            zushu: [1],
            excharge: [],
          };
          let count = 0;
          for (let i = 0; i < row.recipelDetail.length; i++) {
            if (row.recipelDetail[i].isExtra === 0) {
              if (count < row.recipelDetail[i].infuseGroup) {
                count = row.recipelDetail[i].infuseGroup;
              }
            }
          }

          for (let i = 1; i < count; i++) {
            infusion.defaultNumber = infusion.defaultNumber + 1;
            infusion.zushu.push(infusion.defaultNumber);
            infusion.infusionProject.push([]);
            infusion.drippingSpeed.push("");
            infusion.days.push({});
            infusion.frequency.push({});
            infusion.infuseUse.push({});
          }
          //this.excharge=null
          let arr = [];
          for (let i = 0; i < row.recipelDetail.length; i++) {
            if (row.recipelDetail[i].isExtra != 1) {
              infusion.infusionProject[row.recipelDetail[i].infuseGroup - 1].push(
                row.recipelDetail[i]
              );
              infusion.drippingSpeed[row.recipelDetail[i].infuseGroup - 1] =
                row.recipelDetail[i].drippingSpeed;
              infusion.days[row.recipelDetail[i].infuseGroup - 1] =
                row.recipelDetail[i].days;
              infusion.frequency[row.recipelDetail[i].infuseGroup - 1] =
                row.recipelDetail[i].frequency;
              infusion.infuseUse[row.recipelDetail[i].infuseGroup - 1] =
                row.recipelDetail[i].infuseUse;
            } else {
              infusion.excharge.push(row.recipelDetail[i]);
              //arr.push(element.recipelDetailEvtList[i])
            }
          }
          this.medicalEditTabs[this.singleRecipel].infusion = infusion;
        }
        if (row.entrust != undefined && row.entrust != "") {
          this.medicalEditTabs[this.singleRecipel].content.recipelInfo.entrust =
            row.entrust;
        }
        setTimeout(() => {
          this.MedicalCalculate();
        }, 0);
        this.historyDialogVisible = false;
        console.log(this.medicalEditTabs, "这是一个值的额");
      },
      resetCondition() {
        this.queryModel = {
          dateRange: [],
        };
        this.historyRecipel(this.historyType);
      },
      onSearch() {
        this.historyRecipel(this.historyType);
      },
      medicalHistoryOnSearch() {
        this.HistoricalCases();
      },
      medicalHistoryReset() {
        this.queryModel = {
          dateRange: [],
        };
        this.HistoricalCases();
      },
      checkDetails(row, expandedRows) {
        console.log(expandedRows, "查看详情");
        getByRecipelInfoId(expandedRows[0].id)
          .then((res) => {
            if (res.code == "100") {
              console.log(res.data, "查看详情");
              this.historyRecipelDetailList = res.data;
            }
          })
          .catch(() => {
          });
      },
      GetCreateBy(row) {
        console.log(row.updateBy);
        let str = "";
        let count = 0;
        for (let i = 0; i < row.updateBy.length; i++) {
          if (row.updateBy[i] == "(") {
            count = i;
          }
        }
        str = row.updateBy.substring(0, count);
        return str;
      },
      historyRecipel(item, index) {
        this.singleRecipel = index;
        console.log(item, index, "看看这是什么");
        this.historyType = item;
        let search = {
          columnName: "",
          limit: 1000,
          offset: 0,
          order: this.clickId,
          params: [{
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
            {
              columnName: "recipel_type",
              queryType: "=",
              value: item,
            },
          ],
        };
        if (this.queryModel.dateRange.length > 0) {
          search.params.push({
            logic: "AND",
            queryType: "(",
          }, {
            columnName: "update_date",
            logic: "",
            queryType: "between",
            value: this.queryModel.dateRange,
          }, {
            logic: "",
            queryType: ")",
          });
        }
        //  {
        //       logic: "AND",
        //       queryType: "("
        //     },
        //     {
        //       columnName: "create_date",
        //       logic: "",
        //       queryType: 'between',
        //       value: [],
        //     },
        //     {
        //       logic: "",
        //       queryType: ")"
        //     },
        getHistoryRecipel(search)
          .then((res) => {
            if (res.code == "100") {
              console.log(res, "历史处方");
              this.historyDialogVisible = true;
              this.historyRecipelList = res.data;
            }
          })
          .catch((error) => {
            this.$message.error(error);
          });
      },
      HistoricalCases() {
        let search = {
          columnName: "",
          limit: 1000,
          offset: 0,
          order: this.clickId,
          params: [{
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          }],
        };
        if (this.queryModel.dateRange.length > 0) {
          search.params.push({
            logic: "AND",
            queryType: "(",
          }, {
            columnName: "update_date",
            logic: "",
            queryType: "between",
            value: this.queryModel.dateRange,
          }, {
            logic: "",
            queryType: ")",
          });
        }
        getHistoryRecipel(search)
          .then((res) => {
            if (res.code == "100") {
              console.log(res, "历史病历");
              this.caseDialogVisible = true;
              this.historicalCasesList = res.data;
            }
          })
          .catch((error) => {
            this.$message.error(error);
          });
      },

      //模板处方
      templateRecipel(item, index) {
        this.templateType = item;
        this.singleRecipel = index;
        console.log(item, index, "就是这样嘛");
        let search = {
          columnName: "",
          limit: 1000,
          offset: 0,
          order: "",
          params: [{
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
            {
              columnName: "type",
              queryType: "=",
              value: item,
            },
          ],
        };
        search.params.push({
          logic: "AND",
          queryType: "(",
        }, {
          columnName: "category",
          logic: "",
          queryType: "=",
          value: 0,
        }, {
          columnName: "create_id",
          logic: "and",
          queryType: "=",
          value: currentUser.id,
        }, {
          columnName: "category",
          logic: "or",
          queryType: "=",
          value: 1,
        }, {
          logic: "",
          queryType: ")",
        });
        listRecipetemplatePage(search)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.templateRecipelList = responseData.data.rows;
              this.templateDialogVisible = true;
              console.log(this.templateRecipelList, "模板处方");
              // this.recipetemplateTotal = responseData.data.total
              // this.recipetemplateList = responseData.data.rows
            } else {
              // this.showMessage(responseData)
            }
            this.resetLoad();
          })
          .catch((error) => {
            //this.$message.error("后台数据异常，请确认数据是否有误或联系管理员解决！")
          });
      },

      changesDosage(row, index) {
        if (row == "") {
          this.medicalEditTabs[index].recipelInfo.dosage = " ";
        } else {
          this.medicalEditTabs[index].recipelInfo.dosage = row - 0;
        }

        this.MedicalCalculate();
      },
      changeSingleDosage(row, index, item) {
        this.medicalEditTabs.forEach((tabElement) => {
          if (tabElement.type === "recipelType_1" && tabElement.key == item.key) {
            //for (let i = 0; i < tabElement.content.recipelDetailEvtList.length; i++) {
            console.log(
              tabElement.content.recipelDetailEvtList[index],
              "就是看看"
            );
            if (row == "") {
              tabElement.content.recipelDetailEvtList[index].singleDosage = " ";
            } else {
              tabElement.content.recipelDetailEvtList[index].singleDosage = row;
            }
            //}
          }
        });
        this.MedicalCalculate();
      },
      changeUse(row) {
        this.changeData = row;
      },
      addGroup(item) {
        item.infusion.defaultNumber = item.infusion.defaultNumber + 1;
        item.infusion.zushu.push(item.infusion.defaultNumber);
        item.infusion.infusionProject.push([]);
        item.infusion.drippingSpeed.push("");
        item.infusion.days.push({});
        item.infusion.frequency.push({});
        item.infusion.infuseUse.push({});
        this.SearchInfusion.push("");
        console.log(item, "添加");
      },
      minusGroup(item, index) {
        if (item.infusion.defaultNumber > 1) {
          item.infusion.defaultNumber = item.infusion.defaultNumber - 1;
          item.infusion.zushu.splice(index, 1);
          item.infusion.infusionProject.splice(index, 1);
          for (let i = 0; i < item.infusion.infusionProject.length; i++) {
            for (let j = 0; j < item.infusion.infusionProject[i].length; j++) {
              item.infusion.infusionProject[i][j].infuseGroup = i + 1;
            }
          }
          item.infusion.drippingSpeed.splice(index, 1);
          item.infusion.days.splice(index, 1);
          item.infusion.frequency.splice(index, 1);
          item.infusion.infuseUse.splice(index, 1);
          this.SearchInfusion.splice(index, 1);
          console.log(item, "添加");
        }
      },
      indexMethod(index) {
        index + 1;
        return this.defaultNumber;
      },

      //编辑保存
      save(type) {
        if (this.savelock) {
          this.$message.error("保存中，请稍后");
          return;
        }
        let FormCheck = true;
        this.$refs["BasicInfoForm"].validate((valid) => {
          if (valid) {
            //alert('submit!');
          } else {
            FormCheck = false;
            return false;
          }
        });

        if (!FormCheck) {
          this.$message.error("请填写主诉");
          return;
        }

        if (this.beyondInventoryType) {
          this.$message.error("存在药品数量超出可用库存总量,请重新填写!");
          return;
        }

        if (this.beyondStuffInventoryType) {
          this.$message.error("存在材料数量超出可用库存总量，请重新填写!");
          return;
        }

        let infuseFlage = false;
        let flage1 = false;
        let flage2 = false;
        let treatment = false;
        let chargeflage = false;
        let flageChinese1 = false;
        let flageChinese2 = false;
        let recipelInfoEvtList = [];
        let returnForEach = false;
        let hasSelsmallType = false;
        let numberCheck = false;
        let totalCheck = false;
        let paramsCheck = false;
        // if (this.medicalEditTabs.length == 0) {
        //   this.$message.error("处方数量不能为0");
        //   return;
        // }
        console.log(this.medicalEditTabs, 'Tesssssssssssssss')
        this.medicalEditTabs.forEach((tabElement) => {
          console.log(tabElement, "真奇怪");
          //西药处方
          if (
            tabElement.type == "recipelType_0" &&
            !this.MedicalRecordModel.westernDiagnose
          ) {
            returnForEach = true;
          }
          if (tabElement.type == "recipelType_0") {
            let total = 0;
            tabElement.content.recipelDetailEvtList.forEach((item) => {
              if (item.isExtra == 0) {
                if (
                  item.singleDosage &&
                  item.frequency &&
                  item.frequency.value &&
                  item.days &&
                  item.days.name
                ) {
                  total = Math.ceil(
                    BigNumber(item.singleDosage)
                      .multipliedBy(item.frequency.value.split("_")[1])
                      .multipliedBy(item.days.name)
                      .toNumber()
                  );
                  console.log("total == " + total)
                  if (
                    item.drugStuffId.drug.total != 0 &&
                    total > item.drugStuffId.drug.total
                  ) {
                    this.$message.error(
                      item.drugStuffId.name + "总量上限要小于功效说明计算总量，开药总量上限为" + item.drugStuffId.drug.total + item.drugStuffId.drug.dosisUnit.name
                    );
                    totalCheck = true;
                  }
                } else {
                  this.$message.error(tabElement.title + "信息未填写完整");
                  paramsCheck = true;
                }
              }
            });
          }
          //中药处方
          if (
            tabElement.type == "recipelType_1" &&
            !this.MedicalRecordModel.chinaDiagnose
          ) {
            returnForEach = true;
          }
          if (tabElement.type == "recipelType_1") {
            if (tabElement.content.recipelInfo.dosage == "") {
              flageChinese1 = true;
            }

            for (
              let i = 0; i < tabElement.content.recipelDetailEvtList.length; i++
            ) {
              if (tabElement.content.recipelDetailEvtList[i].isExtra == 0) {
                if (
                  tabElement.content.recipelDetailEvtList[i].singleDosage ==
                  " " ||
                  tabElement.content.recipelDetailEvtList[i].singleDosage == ""
                ) {
                  flageChinese2 = true;
                }
              }
              if (tabElement.content.recipelDetailEvtList[i].stuffType === '1'){
                if (tabElement.content.recipelDetailEvtList[i].drugStuffId.drug.total != 0 &&
                  tabElement.content.recipelDetailEvtList[i].total > tabElement.content.recipelDetailEvtList[i].drugStuffId.drug.total) {
                  this.$message.error(
                    tabElement.content.recipelDetailEvtList.drugStuffId.name + "总量上限要小于功效说明计算总量，开药总量上限为" + tabElement.content.recipelDetailEvtList[i].drugStuffId.drug.total + tabElement.content.recipelDetailEvtList[i].drugStuffId.drug.dosisUnit.name
                  );
                  totalCheck = true;
                }
              }

              console.log("hh=" + JSON.stringify(tabElement.content.recipelDetailEvtList))

              /* tabElement.content.recipelDetailEvtList.forEach((item) => {
                 console.log("recipelDetailEvtList.total" + tabElement.content.recipelDetailEvtList[i].total)
                 console.log("item.drugStuffId.drug.total" + item.drugStuffId.drug.total)
                 if (item.drugStuffId.drug.total != 0 &&
                   tabElement.content.recipelDetailEvtList[i].total > item.drugStuffId.drug.total) {
                   this.$message.error(
                     item.drugStuffId.name + "总量上限要小于功效说明计算总量，开药总量上限为" + item.drugStuffId.drug.total + item.drugStuffId.drug.dosisUnit.name
                   );
                   totalCheck = true;
                 }
               })*/
              console.log("kankan i " + i)
            }


          }
          //输液处方
          if (tabElement.type == "recipelType_2") {
            tabElement.content.recipelDetailEvtList = [];
            let arr = 0;
            for (let i = 0; i < tabElement.infusion.infusionProject.length; i++) {
              for (
                let j = 0; j < tabElement.infusion.infusionProject[i].length; j++
              ) {
                tabElement.infusion.infusionProject[i][j].drippingSpeed =
                  tabElement.infusion.drippingSpeed[
                  tabElement.infusion.infusionProject[i][j].infuseGroup - 1
                    ];
                tabElement.infusion.infusionProject[i][j].days =
                  tabElement.infusion.days[
                  tabElement.infusion.infusionProject[i][j].infuseGroup - 1
                    ];
                tabElement.infusion.infusionProject[i][j].frequency =
                  tabElement.infusion.frequency[
                  tabElement.infusion.infusionProject[i][j].infuseGroup - 1
                    ];
                tabElement.infusion.infusionProject[i][j].infuseUse =
                  tabElement.infusion.infuseUse[
                  tabElement.infusion.infusionProject[i][j].infuseGroup - 1
                    ];
                tabElement.content.recipelDetailEvtList[arr] =
                  tabElement.infusion.infusionProject[i][j];
                arr++;

                if (
                  tabElement.infusion.days[
                  tabElement.infusion.infusionProject[i][j].infuseGroup - 1
                    ].name == "" ||
                  tabElement.infusion.days[
                  tabElement.infusion.infusionProject[i][j].infuseGroup - 1
                    ].name == undefined
                ) {
                  flage1 = true;
                }
                //console.log('频次',tabElement.infusion.frequency[tabElement.infusion.infusionProject[i][j].infuseGroup-1]);
                if (
                  tabElement.infusion.frequency[
                  tabElement.infusion.infusionProject[i][j].infuseGroup - 1
                    ].value == "" ||
                  tabElement.infusion.frequency[
                  tabElement.infusion.infusionProject[i][j].infuseGroup - 1
                    ].value == undefined
                ) {
                  flage2 = true;
                }
              }
            }
            for (let i = 0; i < tabElement.infusion.excharge.length; i++) {
              tabElement.content.recipelDetailEvtList.push(
                tabElement.infusion.excharge[i]
              );
            }
            tabElement.content.recipelDetailEvtList.forEach((item) => {
              if (item.isExtra == 0) {
                if (
                  item.singleDosage &&
                  item.frequency &&
                  item.frequency.value &&
                  item.days &&
                  item.days.name
                ) {
                } else {
                  this.$message.error(tabElement.title + "信息未填写完整");
                  infuseFlage = true;
                }
              }
            });
          }
          //诊疗项目
          if (tabElement.type == "recipelType_3") {
            for (
              let i = 0; i < tabElement.content.recipelDetailEvtList.length; i++
            ) {
              if (tabElement.content.recipelDetailEvtList[i].isExtra == 0) {
                if (
                  tabElement.content.recipelDetailEvtList[i].total == "" ||
                  tabElement.content.recipelDetailEvtList[i].singleDosage == "" ||
                  tabElement.content.recipelDetailEvtList[i].total == 0 ||
                  tabElement.content.recipelDetailEvtList[i].singleDosage == 0
                ) {
                  treatment = true;
                }
              }
            }
          }
          //附加费
          for (
            let i = 0; i < tabElement.content.recipelDetailEvtList.length; i++
          ) {
            if (tabElement.content.recipelDetailEvtList[i].isExtra == 1) {
              if (
                tabElement.content.recipelDetailEvtList[i].total == "" ||
                tabElement.content.recipelDetailEvtList[i].singleDosage == "" ||
                tabElement.content.recipelDetailEvtList[i].total == 0 ||
                tabElement.content.recipelDetailEvtList[i].singleDosage == 0
              ) {
                chargeflage = true;
              }
            }
          }
          if (
            !tabElement.content.recipelInfo.smallType &&
            tabElement.type != "recipelType_3"
          ) {
            hasSelsmallType = true;
          }
          if (tabElement.content.recipelDetailEvtList.length == 0) {
            numberCheck = true;
          }
          recipelInfoEvtList.push(tabElement.content);
        });

        if (returnForEach) {
          this.$message.error("存在西药处方或中药处方，请填写对应诊断说明。");
          return;
        }

        if (hasSelsmallType) {
          this.$message.error("请检查每个处方里的分类是否已选择。");
          return;
        }

        if (numberCheck) {
          this.$message.error("请检查每个处方里是否有药品。");
          return;
        }
        if (totalCheck) {
          return;
        }
        if (paramsCheck) {
          return;
        }
        if (infuseFlage) {
          return;
        }
        if (flage1) {
          this.$message.error("请填写输液天数");
          return;
        }
        if (flage2) {
          this.$message.error("请填写输液频次");
          return;
        }
        if (flageChinese1) {
          this.$message.error("请填写中药剂量");
          return;
        }
        if (flageChinese2) {
          this.$message.error("请填写中药药品数量");
          return;
        }
        if (treatment) {
          this.$message.error("请填写诊疗处方的项目数量");
          return;
        }
        if (chargeflage) {
          this.$message.error("请填写附加费的项目数量");
          return;
        }

        this.MedicalRecordModel.company = this.Company;
        this.MedicalRecordModel.doctor.id = this.UserId;

        let medicalRecipelEvt = {
          id: this.registration.id,
          registration: this.registration,
          medicalRecord: this.MedicalRecordModel,
          patient: this.BasicInfoModel,
          recipelInfoEvtList: recipelInfoEvtList,
          type: 0,
          deleteFileIdList: this.deleteFileIdList,
        };
        console.log(medicalRecipelEvt, "怎么回事");
        // return
        let formData = new FormData();
        formData.append("entity", JSON.stringify(medicalRecipelEvt));
        this.pushMedicalFiles(formData)
        this.savelock = true;
        editSave(formData).then((responseData) => {
          if (responseData.code == 100) {
            this.$message.success("操作成功！");
            this.hasBtnFinishVisit = false;
            this.hasBtnPrint = false;
            this.isReadOnly = true;
            this.medicalEditTabs.forEach((item) => {
              item.closable = false;
            });
            this.hint = false;
            this.endHint = true;
            if (type == 2) {
              let itemCopy = JSON.parse(
                JSON.stringify(this.activeSelAlreadyPatientItem)
              );
              this.registration = JSON.parse(
                JSON.stringify(this.activeSelAlreadyPatientItem)
              );
              this.$nextTick(() => {
                this.MedicalRecordModel = this.ReturnMedicalModel();

                this.hasBtnFinishVisit = false;
                this.hasBtnPrint = true;
                this.isReadOnly = true;
                this.recipelInfoId = "";
                this.AlreadyPatientDescriptions = [];

                allQueryMedicalRecord(this.registration.id).then((responseData) => {
                    let arr = responseData.data;
                    console.log(arr, "这是怎么回事");
                    if (responseData.code == 100) {
                      let recipelInfoEvtList =
                        responseData.data.recipelInfoEvtList;
                      recipelInfoEvtList.forEach((element) => {
                        this.AlreadyPatientDescriptions.push(
                          JSON.parse(JSON.stringify(element
                            .recipelInfo))
                        );
                      });

                      this.AlreadyPatientDescriptions.sort(function (a, b) {
                        a.seq = a.seq ? a.seq : 0;
                        b.seq = b.seq ? b.seq : 0;
                        return a.seq - b.seq;
                      });
                      console.log(this.AlreadyPatientDescriptions, "utiy");
                      console.log(responseData.data, "poiu");
                      this.MedicalRecordModel = responseData.data.medicalRecord;
                      this.MedicalRecordModel.registration.id = itemCopy.id;
                      this.MedicalRecordModel.registration.treatType =
                        itemCopy.treatType;
                      this.MedicalRecordModel.registration.morbidityTime =
                        itemCopy.morbidityTime;
                      this.MedicalRecordModel.registration.infectType =
                        itemCopy.infectType;
                      this.BasicInfoModel = responseData.data.patient;
                      this.BasicInfoModel.breathe = this.registration.breathe;
                      this.BasicInfoModel.temperature =
                        this.registration.temperature;
                      console.log(this.BasicInfoModel.temperature, "this.BasicInfoModel.temperature")
                      this.BasicInfoModel.pulse = this.registration.pulse;
                      this.BasicInfoModel.bloodPressure =
                        this.registration.bloodPressure;
                      this.GetMedicineCofigure();
                      this.createMedicalEditTab(recipelInfoEvtList);
                      //附件
                      this.changeMedicalFile("view", this.registration.id)
                    }
                  }
                );
              });
            }
            this.GetPreparePatientList();
            this.GetAlreadyPatientList();
          } else if (responseData.code == "50001") {
            this.$message.warning(responseData.msg);
          } else {
            this.$message.error("更新失败！");
          }
          this.savelock = false;
        });
        this.blmbVisit = false;
      },
      pushMedicalFiles(formData) {
        let fileList = this.$refs.medicalFile.getFileList()
        fileList.forEach((item, index) => {
          formData.append("medicalFiles", item.raw)
        })
        return formData
      },
      //附件上传切换
      changeMedicalFile(type, objectId) {
        console.log(type, objectId, "我是附件上传的信息")
        if ("view" === type) {
          //已就诊
          this.MedicalFileObjId = objectId;
          this.MedicalFlags = "view"
        } else if ("add" === type) {
          //待就诊
          this.MedicalFlags = "add"
        } else if ("edit" === type) {
          //新增
          this.MedicalFlags = "edit"
          this.deleteFileIdList = [];
        }
      },
      //编辑
      edit() {
        // &&this.medicalClickTabsValue.content.recipelInfo.id
        if (
          (this.medicalClickTabsValue && this.BasicInfoModel.name) ||
          (this.medicalClickTabsValue.content.recipelInfo.id &&
            this.medicalClickTabsValue)
        ) {
          console.log(this.medicalEditTabs, "获取值");
          let now = new Date();
          let newDate = new Date(this.activeSelAlreadyPatientItem.createDate);
          now = Date.parse(now);
          newDate = Date.parse(newDate);
          if (this.timeOut) {
            this.$message.error("超过一天的数据无法编辑");
            return;
          }
          this.isReadOnly = false;
          this.endHint = false;
          this.blmbVisit = true;
          // this.chineseTest=[[]]
          //  for (let i = 1; i < this.medicalEditTabs.length; i++) {
          //   if(this.medicalEditTabs[i].type=="recipelType_1"){
          //    this.chineseTest.push([])
          //   }
          // }

          // for (let i = 0; i < this.medicalEditTabs.length; i++) {
          //   if(this.medicalEditTabs[i].type=="recipelType_1"){
          //     for (let j = 0; j < this.medicalEditTabs[i].content.recipelDetailEvtList.length; j++) {
          //       this.chineseTest[this.medicalEditTabs[i].key-1].push(this.medicalEditTabs[i].content.recipelDetailEvtList[j].drugStuffId.name)
          //     }
          //   }
          // }
          // console.log(this.chineseTest,'参数');
        } else {
          this.$message.error("未选择用户");
        }
        //附件上传切换
        this.changeMedicalFile("edit", "");
      },
      print() {
        if (!this.activeSelAlreadyPatientItem) {
          return;
        }
        let filterArr = this.medicalEditTabs.filter(
          (item) => item.content.recipelInfo.status != -1
        );
        let arr = [];
        for (let i = 0; i < filterArr.length; i++) {
          arr.push("");
        }
        filterArr.forEach((item, index) => {
          let params = {
            recipelInfoId: item.content.recipelInfo.id,
            type: item.content.recipelInfo.recipelType.value,
          };
          let str = "";
          let type =
            params.type == "recipelType_0" ?
              // "westMedicine" :
              "chronicDisease" :
              params.type == "recipelType_1" ?
                "chineseMedicine" :
                params.type == "recipelType_2" ?
                  "infuse" :
                  "costItem";
          if (params.recipelInfoId) {
            str = "&recipelInfoId=" + params.recipelInfoId;
          }

          arr[index] = window.open(
            this.BASE_API +
            "/ureport/preview?_u=Newtouch:" +
            type +
            ".ureport.xml&_t=0" +
            str +
            "&type=0",
            index
          );

          arr[index].onload = () => {
            arr[index].document.title = "新致云诊所";
          };
        });
      },
      handleFileRemove(file, fileList) {
        console.log(12);
      },
      handleFileProgress(event, file, fileList) {
        console.log(11);
      },
      handleFileBeforeUpload(file) {
        console.log(13);
      },
      handlePatientClick(tab, event) {
        this.blmbVisit = false;
        if (this.TodayActiveName === "prepare") {
          this.isReadOnly = true;
          this.ChineseButton = false;
          this.$refs["BasicInfoForm"].clearValidate();
          this.GetPreparePatientList();
          this.clickId = "";
        } else {
          this.userClickID = "";
          this.isReadOnly = true;
          this.ChineseButton = false;
          this.$refs["BasicInfoForm"].clearValidate();
          this.GetAlreadyPatientList();
          this.hint = false;
          this.clickId = "";
        }

        this.backgroundColors = 10000;
        this.overColor = 0;
        console.log("---------:" + this.TodayActiveName);
      },
      //获取待就诊患者列表
      GetPreparePatientList() {
        let params = [{
          columnName: "company_id",
          queryType: "=",
          value: this.Company.id,
        },
          {
            columnName: "status",
            queryType: "=",
            value: "",
          },
          {
            columnName: "doctor_id",
            queryType: "=",
            value: this.UserId,
          },
        ];
        if (this.SearchPatientInfo) {
          params.push({
            logic: "AND",
            queryType: "(",
          });
          params.push({
            logic: "",
            columnName: "patientId.name",
            queryType: "like",
            value: this.SearchPatientInfo,
          });
          params.push({
            logic: "OR",
            columnName: "patientId.phone",
            queryType: "like",
            value: this.SearchPatientInfo,
          });
          params.push({
            logic: "OR",
            columnName: "patientId.card",
            queryType: "like",
            value: this.SearchPatientInfo,
          });
          params.push({
            logic: "",
            queryType: ")",
          });
        }
        this.SearchPreModel.params = params;
        this.SearchPreModel.params[1].value = "registrationStatus_0";
        listRegistrationPage(this.SearchPreModel).then((responseData) => {
          if (responseData.code == 100) {
            this.PreparePatientTotal = responseData.data.total;
            if (responseData.data.rows) {
              responseData.data.rows.forEach((element) => {
                let birthday = element.patientId.birthday;
                if (birthday) {
                  const duration = moment.duration(moment().diff(birthday));
                  element.patientId.birthdayText = duration.years();
                } else {
                  element.patientId.birthdayText = "--";
                }
                element.formatUpdateDate = moment(element.updateDate).format(
                  "MM/DD HH:mm"
                );
              });
            }
            this.PreparePatientList = responseData.data.rows;
          }
        });
      },
      //获取已就诊患者列表
      GetAlreadyPatientList() {
        let params = [{
          columnName: "company_id",
          queryType: "=",
          value: this.Company.id,
        },
          {
            columnName: "status",
            queryType: "in",
            value: ["registrationStatus_1", "registrationStatus_4"],
          },
          {
            columnName: "doctor_id",
            queryType: "=",
            value: this.UserId,
          },
        ];
        if (this.SearchPatientInfo) {
          params.push({
            logic: "AND",
            queryType: "(",
          });
          params.push({
            logic: "",
            columnName: "patientId.name",
            queryType: "like",
            value: this.SearchPatientInfo,
          });
          params.push({
            logic: "OR",
            columnName: "patientId.phone",
            queryType: "like",
            value: this.SearchPatientInfo,
          });
          params.push({
            logic: "OR",
            columnName: "patientId.card",
            queryType: "like",
            value: this.SearchPatientInfo,
          });
          params.push({
            logic: "",
            queryType: ")",
          });
        }
        this.SearchAlreadyModel.params = params;
        listRegistrationPage(this.SearchAlreadyModel).then((responseData) => {
          if (responseData.code == 100) {
            this.AlreadyPatientListTotal = responseData.data.total;
            if (responseData.data.rows) {
              responseData.data.rows.forEach((element) => {
                let birthday = element.patientId.birthday;
                if (birthday) {
                  const duration = moment.duration(moment().diff(birthday));
                  element.patientId.birthdayText = duration.years();
                } else {
                  element.patientId.birthdayText = "--";
                }
                element.formatReceptionEndDate = moment(
                  element.receptionEndDate
                ).format("MM/DD HH:mm");
              });
            }
            this.AlreadyPatientList = responseData.data.rows;
          }
        });
      },
      handleCurrentPreparePatientChange(pageNum) {
        this.SearchPreModel.offset = (pageNum - 1) * this.SearchPreModel.limit;
        this.GetPreparePatientList();
      },
      handleCurrentAlreadyPatientChange(pageNum) {
        this.SearchAlreadyModel.offset =
          (pageNum - 1) * this.SearchAlreadyModel.limit;
        this.GetAlreadyPatientList();
      },
      AlreadyPatientDeletilQuery(row) {
        console.log(row, "row");
        if (row.patientId === undefined) {
          return;
        }
        this.recordpatid = row.patientId.id;
        this.clickChangeColor = row.id;

        if (row) {
          this.recordpatid = row.patientId.id;
          this.clickChangeColor = row.id;
        }
        console.log(this.activeSelAlreadyPatientItem.id, this.clickId);
        if (
          this.clickId != "" &&
          this.clickId == this.activeSelAlreadyPatientItem.id &&
          !this.cancellation
        ) {
          console.log("没进来嘛？");
          return;
        } else if (this.activeSelAlreadyPatientItem.id != undefined) {
          this.cancellation = false;
          console.log(
            this.clickId,
            this.activeSelAlreadyPatientItem.id,
            "书法家看法萨克利夫你来撒快发霉了"
          );
          if (this.endHint) {
            this.clickId = this.activeSelAlreadyPatientItem.id;
            console.log(this.activeSelAlreadyPatientItem, "我的");
            if (!this.activeSelAlreadyPatientItem) {
              return;
            }
            let now = new Date();
            let newDate = new Date(
              this.activeSelAlreadyPatientItem.receptionEndDate
            );
            now = Date.parse(now);
            newDate = Date.parse(newDate);
            if (now - newDate > 86400000) {
              this.timeOut = true;
            } else {
              this.timeOut = false;
            }
            let itemCopy = JSON.parse(
              JSON.stringify(this.activeSelAlreadyPatientItem)
            );
            this.registration = JSON.parse(
              JSON.stringify(this.activeSelAlreadyPatientItem)
            );
            this.$nextTick(() => {
              this.MedicalRecordModel = this.ReturnMedicalModel();

              this.hasBtnFinishVisit = false;
              this.hasBtnPrint = true;
              this.isReadOnly = true;
              this.recipelInfoId = "";
              this.AlreadyPatientDescriptions = [];
              //根据患者id获取会员信息
              getByPatientId(this.activeSelAlreadyPatientItem.patientId.id)
                .then((res) => {
                  if (res.code == "100") {
                    console.log(res, "卡九分零四开发");
                    this.member = res.data;
                  } else {
                    this.$message.error("后台数据异常请联系管理！");
                  }
                })
                .catch();
              getPoverty(this.activeSelAlreadyPatientItem.patientId.id)
                .then((res) => {
                  if (res.code == "100") {
                    console.log(res, "贫困户");
                    this.poverty = res.data;
                  } else {
                    this.$message.error("贫困数据异常请联系管理！");
                  }
                })
                .catch();
              allQueryMedicalRecord(this.registration.id).then((responseData) => {
                let arr = responseData.data;
                console.log(arr, "这是怎么回事");
                if (responseData.code == 100) {
                  let recipelInfoEvtList = responseData.data.recipelInfoEvtList;
                  recipelInfoEvtList.forEach((element) => {
                    this.AlreadyPatientDescriptions.push(
                      JSON.parse(JSON.stringify(element.recipelInfo))
                    );
                  });
                  this.AlreadyPatientDescriptions.sort(function (a, b) {
                    a.seq = a.seq ? a.seq : 0;
                    b.seq = b.seq ? b.seq : 0;
                    return a.seq - b.seq;
                  });
                  console.log(this.AlreadyPatientDescriptions, "utiy");
                  console.log(responseData.data, "poiu");
                  this.MedicalRecordModel = responseData.data.medicalRecord;
                  this.MedicalRecordModel.registration.id = itemCopy.id;
                  this.MedicalRecordModel.registration.treatType =
                    itemCopy.treatType;
                  this.MedicalRecordModel.registration.morbidityTime =
                    itemCopy.morbidityTime;
                  this.MedicalRecordModel.registration.infectType =
                    itemCopy.infectType;
                  this.BasicInfoModel = responseData.data.patient;
                  this.BasicInfoModel.breathe = this.registration.breathe;
                  this.BasicInfoModel.temperature = this.registration.temperature;
                  this.BasicInfoModel.pulse = this.registration.pulse;
                  this.BasicInfoModel.bloodPressure =
                    this.registration.bloodPressure;
                  this.GetMedicineCofigure();
                  this.createMedicalEditTab(recipelInfoEvtList);
                }
              });
              // this.GetPatient(itemCopy);
              console.log(this.MedicalRecordModel, "nuide");
            });
            this.overColor = 0;
          } else {
            this.$confirm(
              "当前患者正在进行编辑操作,还未保存是否确定切换?",
              "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
              }
            ).then(() => {
              this.clickId = this.activeSelAlreadyPatientItem.id;
              if (!this.activeSelAlreadyPatientItem) {
                return;
              }
              let now = new Date();
              let newDate = new Date(
                this.activeSelAlreadyPatientItem.receptionEndDate
              );
              now = Date.parse(now);
              newDate = Date.parse(newDate);

              if (now - newDate > 86400000) {
                this.timeOut = true;
              } else {
                this.timeOut = false;
              }
              let itemCopy = JSON.parse(
                JSON.stringify(this.activeSelAlreadyPatientItem)
              );
              this.registration = JSON.parse(
                JSON.stringify(this.activeSelAlreadyPatientItem)
              );
              this.$nextTick(() => {
                this.MedicalRecordModel = this.ReturnMedicalModel();

                this.hasBtnFinishVisit = false;
                this.hasBtnPrint = true;
                this.isReadOnly = true;
                this.recipelInfoId = "";
                this.AlreadyPatientDescriptions = [];
                //根据患者id获取会员信息
                getByPatientId(this.activeSelAlreadyPatientItem.patientId.id)
                  .then((res) => {
                    if (res.code == "100") {
                      console.log(res, "卡九分零四开发");
                      this.member = res.data;
                    } else {
                      this.$message.error("后台数据异常请联系管理！");
                    }
                  })
                  .catch();
                getPoverty(this.activeSelAlreadyPatientItem.patientId.id)
                  .then((res) => {
                    if (res.code == "100") {
                      console.log(res, "贫困户");
                      this.poverty = res.data;
                    } else {
                      this.$message.error("贫困数据异常请联系管理！");
                    }
                  })
                  .catch();
                allQueryMedicalRecord(this.registration.id).then(
                  (responseData) => {
                    let arr = responseData.data;
                    if (responseData.code == 100) {
                      let recipelInfoEvtList =
                        responseData.data.recipelInfoEvtList;
                      recipelInfoEvtList.forEach((element) => {
                        this.AlreadyPatientDescriptions.push(
                          JSON.parse(JSON.stringify(element
                            .recipelInfo))
                        );
                      });
                      this.AlreadyPatientDescriptions.sort(function (a, b) {
                        a.seq = a.seq ? a.seq : 0;
                        b.seq = b.seq ? b.seq : 0;
                        return a.seq - b.seq;
                      });
                      this.MedicalRecordModel = responseData.data.medicalRecord;
                      this.MedicalRecordModel.registration.id = itemCopy.id;
                      this.MedicalRecordModel.registration.treatType =
                        itemCopy.treatType;
                      this.MedicalRecordMode
                      this.MedicalRecordModel.registration.morbidityTime =
                        itemCopy.morbidityTime;
                      this.MedicalRecordModel.registration.infectType =
                        itemCopy.infectType;
                      this.BasicInfoModel = responseData.data.patient;
                      this.BasicInfoModel.breathe = this.registration.breathe;
                      this.BasicInfoModel.temperature =
                        this.registration.temperature;
                      this.BasicInfoModel.pulse = this.registration.pulse;
                      this.BasicInfoModel.bloodPressure =
                        this.registration.bloodPressure;
                      this.GetMedicineCofigure();
                      this.createMedicalEditTab(recipelInfoEvtList);
                    }
                  }
                );
              });
              this.overColor = 0;
              this.endHint = true;
              console.log(this.endHint);
            })
              .catch(() => {
                this.endHint = false;
              });
          }
        }
        //附件上传
        this.changeMedicalFile("view", row.id)
      },
      AlreadyPatientDescriptionsQuery(item, index) {
        console.log(item, "dshafhfkjsafhkj");
        this.hasBtnFinishVisit = false;
        this.medicalEditTabs.forEach((element) => {
          if (element.content.uuid == item.id) {
            this.medicalEditTabsValue = element;
            this.medicalClickTabsValue = this.medicalEditTabsValue;
          }
        });

        this.recipelInfoId = item.id;
        this.overColor = index;
      },
      clickPreparePatient(item, index) {
        // if(item.id===this.clickId){
        //   return
        // }
        this.blmbVisit = true;
        this.recordpatid = item.patientId.id; //获取用户id
        console.log(item, this.userClickID, "点击用户");
        if (this.userClickID != "" && this.userClickID == item.id) {
          this.hint = true;
          return;
        }
        if (!this.hint) {
          this.clickId = item.id;
          let itemCopy = JSON.parse(JSON.stringify(item));
          this.registration = JSON.parse(JSON.stringify(item));
          console.log(this.registration, "查看默认值")
          this.$nextTick(() => {
            this.hasBtnFinishVisit = true;
            this.hasBtnPrint = false;
            this.isReadOnly = false;
            this.ChineseButton = true;
            // this.MedicalRecordModel = this.ReturnMedicalModel();
            // this.MedicalRecordModel.registration.id = itemCopy.id;
            // this.MedicalRecordModel.registration.treatType = itemCopy.treatType;
            this.backgroundColors = index;
            this.GetPatient(itemCopy);
            this.GetMedicineCofigure();
            this.medicalEditTabs = [];
            this.MedicalCalculate();
            this.userClickID = item.id;
            this.hint = true;
            // this.MedicalRecordModel=[];
            allQueryMedicalRecord(item.id).then((responseData) => {
              this.MedicalRecordModel = responseData.data.medicalRecord;
              if (responseData.data.medicalRecord != null) {
                this.ChooseRecordList = [];
                this.MedicineRecordLabelList.map((item) => {
                  if (this.MedicalRecordModel[`${item.name}`]) {
                    this.ChooseRecordList.push(item);
                  }
                });
              } else {
                this.MedicalRecordModel = this.ReturnMedicalModel();
                this.MedicalRecordModel.registration.id = itemCopy.id;
                this.MedicalRecordModel.registration.treatType =
                  itemCopy.treatType;
                this.MedicalRecordModel.registration.morbidityTime =
                  itemCopy.morbidityTime;
                this.MedicalRecordModel.registration.infectType =
                  itemCopy.infectType;
                //  this.patientTell="",
                //  this.ClearMedicineCofigure();
              }
            });
            //根据患者id获取会员信息
            getByPatientId(item.patientId.id)
              .then((res) => {
                if (res.code == "100") {
                  console.log(res, "卡九分零四开发");
                  this.member = res.data;
                } else {
                  this.$message.error("后台数据异常请联系管理！");
                }
              })
              .catch();
            getPoverty(item.patientId.id)
              .then((res) => {
                if (res.code == "100") {
                  console.log(res, "贫困户");
                  this.poverty = res.data;
                } else {
                  this.$message.error("贫困数据异常请联系管理！");
                }
              })
              .catch();
            // this.addMedicalEditTab(this.medicalTypeList[0]);
            console.log(this.registration, "查看默认值111")
          });
        } else {
          this.open(item, index);
        }
        //附件上传
        this.changeMedicalFile("add");
      },
      open(item, index) {
        this.$confirm("当前患者还未完成接诊是否确定切换?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            this.hint = false;
            this.clickPreparePatient(item, index);
          })
          .catch(() => {
            this.hint = true;
          });
      },
      GetPatient(patientId) {
        getPatientById(patientId.patientId.id).then((responseData) => {
          if (responseData.code == 100) {
            console.log(responseData.data, "查询用户");
            this.BasicInfoModel = responseData.data;
            this.BasicInfoModel.breathe = patientId.breathe;
            this.BasicInfoModel.temperature = patientId.temperature;
            this.BasicInfoModel.pulse = patientId.pulse;
            this.BasicInfoModel.bloodPressure = patientId.bloodPressure;
          }
        });
      },
      ReturnMedicalModel() {
        return {
          company: this.Company,
          doctor: {
            id: this.UserId,
          },
          patientTell: "",
          westernDiagnose: "",
          chinaDiagnose: "",
          nowHistory: "",
          beforeHistory: "",
          diagnose: "",
          epidemicDisease: "",
          otherCheck: "",
          handlingSituation: "",
          healthEducation: "",
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
          registration: {
            morbidityTime: "",
            treatType: {
              name: "初诊",
              value: "treatType_0",
            },
            infectType: {
              name: "否",
              value: "infectType_0",
            }
          },

          basics: {},
        };
      },
      //开具处方新增Tab标签页
      addMedicalEditTab(tabTypeData) {
        console.log("111111")
        let tabType = tabTypeData.type;
        let typeMaxTabIndex = 0;
        this.medicalEditTabs.forEach((element) => {
          if (element.type === tabType) {
            typeMaxTabIndex++;
          }
        });

        let isFollowUp =
          this.MedicalRecordModel.registration.treatType.value === "treatType_0" ?
            0 :
            1;
        let isFollowUpTo =
          this.MedicalRecordModel.registration.infectType.value === "infectType_0" ?
            0 :
            1;
        let recipelInfoEvt = {};
        if (tabType == "recipelType_1") {
          recipelInfoEvt = {
            uuid: require("uuid").v1(),
            recipelInfo: {
              company: this.Company,
              recipelType: {
                name: tabTypeData.name,
                value: tabTypeData.type,
              },
              remarks: tabTypeData.name + (typeMaxTabIndex + 1),
              fee: 0,
              status: 1,
              chargeStatus: 0,
              dispensionStatus: 0,
              isFollowUp: isFollowUp,
              isFollowUpTo: isFollowUpTo,
              recipelUse: this.isSpecial ?
                {
                  name: this.ChineseUseOption[0].name,
                  value: this.ChineseUseOption[0].value,
                } :
                {
                  name: "",
                  value: "",
                },
              smallType: this.RecipelSmallTypeList[0],
              chronicDisease: true,
              chinessNotes: "",

            },
            recipelDetailEvtList: [],
          };
        } else {
          recipelInfoEvt = {
            uuid: require("uuid").v1(),
            recipelInfo: {
              company: this.Company,
              recipelType: {
                name: tabTypeData.name,
                value: tabTypeData.type,
              },
              remarks: tabTypeData.name + (typeMaxTabIndex + 1),
              fee: 0,
              status: 1,
              chargeStatus: 0,
              dispensionStatus: 0,
              isFollowUp: isFollowUp,
              isFollowUpTo: isFollowUpTo,
              smallType: this.RecipelSmallTypeList[0],
            },
            recipelDetailEvtList: [],
          };
        }

        // if (tabTypeData.type === "recipelType_1") {
        //   recipelInfoEvt.recipelInfo["dosage"] = 1;
        // }
        let infusion = {};
        if (tabTypeData.type === "recipelType_2") {
          infusion = {
            // infusionProject:this.infusionProject,
            // drippingSpeed:this.drippingSpeed,  //滴速
            // days:this.days,    //天数
            // frequency:this.frequency,   //频次
            // infuseUse:this.infuseUse,     //用法
            // zushu:this.zushu,
            defaultNumber: 1, //默认组号
            infusionProject: [
              []
            ],
            drippingSpeed: [""], //滴速
            days: [{}], //天数
            frequency: [{}], //频次
            infuseUse: [{}], //用法
            zushu: [1],
            excharge: [],
          };
        }
        let costDebug = {
          chineseTest: [],
        };

        this.recipelInfoEvtList.push(recipelInfoEvt);

        let tab = {
          title: tabTypeData.name + (typeMaxTabIndex + 1),
          key: this.medicalEditTabs.length + 1,
          content: recipelInfoEvt,
          closable: true,
          type: tabType,
          infusion: infusion,
          costDebug: costDebug,
        };
        // if(tab.type=="recipelType_1"){
        //   this.chineseTest[tab.key-1]=[]
        // }
        this.SearchWesternInput = "";
        this.SearchChineseInput = "";
        this.SearchCostItemInput = "";
        this.SearchSurchargeInput = "";
        this.SearchInfusion = [""];
        this.medicalEditTabs.push(tab);
        this.medicalEditTabsValue = tab;
        this.medicalClickTabsValue = this.medicalEditTabsValue;
        console.log(this.medicalEditTabsValue, "新增");
        console.log(require("uuid").v1());
        // 获取第一个input的焦点
        // this.$nextTick(() => {
        //   this.$refs.singleDosage-input[0].focus();
        // });
      },
      //开具处方删除Tab标签页
      removeMedicalEditTab(targetName) {
        console.log(targetName);
        let tabs = this.medicalEditTabs;
        let activeName = this.medicalEditTabsValue;
        if (activeName === targetName) {
          tabs.forEach((tab, index) => {
            if (tab === targetName) {
              let nextTab = tabs[index + 1] || tabs[index - 1];
              if (nextTab) {
                activeName = nextTab;
              }
            }
          });
        }

        this.medicalEditTabsValue = activeName;
        this.medicalEditTabs = tabs.filter((tab) => tab !== targetName);
        this.SearchWesternInput = "";
        this.SearchChineseInput = "";
        this.SearchInfusion = [""];
        if (this.medicalEditTabs.length > 0) {
          this.medicalClickTabsValue = this.medicalEditTabsValue;
        } else {
          this.medicalClickTabsValue = {};
        }

        this.MedicalCalculate();
        this.medicalEditTabs = this.medicalEditTabs;
      },

      //开具处方切换Tab标签页触发事件
      clickMedicalEditTab() {
        if (this.medicalEditTabsValue === this.medicalClickTabsValue) {
          return;
        }
        console.log(this.medicalEditTabsValue, "就是看看");
        this.SearchWesternInput = "";
        this.medicalClickTabsValue = this.medicalEditTabsValue;
        console.log(this.isReadOnly, this.TodayActiveName);
        if (this.TodayActiveName == "already") {
          let num = 0;
          for (let i = 0; i < this.medicalEditTabs.length; i++) {
            if (this.medicalEditTabs[i].key == this.medicalEditTabsValue.key) {
              num = i;
            }
          }
          this.AlreadyPatientDescriptionsQuery(
            this.medicalClickTabsValue.content,
            num
          );
        }
      },
      createMedicalEditTab(recipelInfoEvtList) {
        console.log("---------recipelInfoEvtList------------");
        console.log(recipelInfoEvtList);
        this.medicalEditTabs = [];
        if (
          recipelInfoEvtList == undefined ||
          recipelInfoEvtList == null ||
          !recipelInfoEvtList.length > 0
        ) {
          this.MedicalCalculate();
          return;
        } else {
          recipelInfoEvtList.forEach((element) => {
            element.uuid = element.recipelInfo.id;
            let costDebug = {
              chineseTest: [],
            };
            for (let i = 0; i < this.medicalEditTabs.length; i++) {
              for (
                let j = 0; j < this.medicalEditTabs[i].content.recipelDetailEvtList.length; j++
              ) {
                costDebug.chineseTest.push(
                  this.medicalEditTabs[i].content.recipelDetailEvtList[j]
                    .drugStuffId.name
                );
              }
            }
            if (element.recipelInfo.recipelType.value == "recipelType_3") {
              this.inspectionType = [];
              this.inspectionSign = 0;

              for (let i = 0; i < element.recipelDetailEvtList.length; i++) {
                if (element.recipelDetailEvtList[i].drugStuffId.costItem) {
                  if (
                    element.recipelDetailEvtList[i].drugStuffId.costItem.itemType
                      .value == "treatmentItemType_0" ||
                    element.recipelDetailEvtList[i].drugStuffId.costItem.itemType
                      .value == "treatmentItemType_1"
                  ) {
                    this.inspectionSign = 1;
                    this.inspectionType.push(0);
                  }
                }
              }
              for (let i = 0; i < element.recipelDetailEvtList.length; i++) {
                if (
                  element.recipelDetailEvtList[i].drugStuffId
                    .inspectionCheckInfo != undefined
                ) {
                  if (
                    element.recipelDetailEvtList[i].drugStuffId.costItem.itemType
                      .value == "treatmentItemType_0" ||
                    element.recipelDetailEvtList[i].drugStuffId.costItem.itemType
                      .value == "treatmentItemType_1"
                  ) {
                    console.log(
                      element.recipelDetailEvtList[i].drugStuffId.costItem
                        .inspectionCheckInfo,
                      "进来了妈"
                    );
                    this.inspectionType[i] = 1;
                  }
                }
              }
            }
            if (element.recipelInfo.recipelType.value == "recipelType_2") {
              let infusion = {
                defaultNumber: 1, //默认组号
                infusionProject: [
                  []
                ],
                drippingSpeed: [""], //滴速
                days: [{}], //天数
                frequency: [{}], //频次
                infuseUse: [{}], //用法
                zushu: [1],
                excharge: [],
              };
              let count = 0;
              for (let i = 0; i < element.recipelDetailEvtList.length; i++) {
                if (element.recipelDetailEvtList[i].isExtra === 0) {
                  if (count < element.recipelDetailEvtList[i].infuseGroup) {
                    count = element.recipelDetailEvtList[i].infuseGroup;
                  }
                }
              }

              for (let i = 1; i < count; i++) {
                infusion.defaultNumber = infusion.defaultNumber + 1;
                infusion.zushu.push(infusion.defaultNumber);
                infusion.infusionProject.push([]);
                infusion.drippingSpeed.push("");
                infusion.days.push({});
                infusion.frequency.push({});
                infusion.infuseUse.push({});
              }
              //this.excharge=null
              let arr = [];
              for (let i = 0; i < element.recipelDetailEvtList.length; i++) {
                if (element.recipelDetailEvtList[i].isExtra != 1) {
                  infusion.infusionProject[
                  element.recipelDetailEvtList[i].infuseGroup - 1
                    ].push(element.recipelDetailEvtList[i]);
                  infusion.drippingSpeed[
                  element.recipelDetailEvtList[i].infuseGroup - 1
                    ] = element.recipelDetailEvtList[i].drippingSpeed;
                  infusion.days[element.recipelDetailEvtList[i].infuseGroup - 1] =
                    element.recipelDetailEvtList[i].days;
                  infusion.frequency[
                  element.recipelDetailEvtList[i].infuseGroup - 1
                    ] = element.recipelDetailEvtList[i].frequency;
                  infusion.infuseUse[
                  element.recipelDetailEvtList[i].infuseGroup - 1
                    ] = element.recipelDetailEvtList[i].infuseUse;
                } else {
                  infusion.excharge.push(element.recipelDetailEvtList[i]);
                  //arr.push(element.recipelDetailEvtList[i])
                }
              }

              // this.excharge=arr
              let tab = {
                title: element.recipelInfo.name,
                key: element.recipelInfo.seq,
                content: element,
                closable: false,
                type: element.recipelInfo.recipelType.value,
                infusion: infusion,
                costDebug: costDebug,
              };
              this.medicalEditTabs.push(tab);
            } else {
              let tab = {
                title: element.recipelInfo.name,
                key: element.recipelInfo.seq,
                content: element,
                closable: false,
                type: element.recipelInfo.recipelType.value,
                costDebug: costDebug,
              };
              this.medicalEditTabs.push(tab);
            }
          });

          // this.infusionProject=[]
          // this.infusionProject[0].push([])
          // console.log(this.infusionProject,'集合查看');

          // this.medicalEditTabs.forEach(items=>{
          //   if(items.type=="recipelType_2"){
          //       let count=0
          //       for (let i = 0; i < items.content.recipelDetailEvtList; i++) {
          //         if(this.medicalEditTabs[i].type=='recipelType_2'){
          //           if(count<this.medicalEditTabs[i].content.recipelDetailEvtList.infuseGroup){
          //             count=this.medicalEditTabs[i].content.recipelDetailEvtList.infuseGroup
          //           }
          //         }

          //       }
          //       for (let i = 1; i < count; i++) {
          //         this.zushu.push(i+1)
          //         }
          //          console.log(this.zushu,'组数');
          //     items.content.recipelDetailEvtList.forEach(infusionItem=>{
          //      console.log(infusionItem,'集合查看')
          //       if(infusionItem.infuseGroup-1==0){
          //         this.infusionProject[0].push(infusionItem)
          //       }else{
          //         this.infusionProject[infusionItem.infuseGroup-1].push(infusionItem)
          //       }

          //     // items.content.recipelDetailEvtList.infuseGroup
          //     console.log(infusionItem,'kankanqiqi')
          //     })

          //   }
          // })

          // for (let i = 0; i < this.infusionProject.length; i++) {
          //      for (let j = 0; j < this.infusionProject[i].length; j++) {
          //     this.drippingSpeed[this.infusionProject[i][j].infuseGroup-1]=this.infusionProject[i][j].drippingSpeed
          //     this.days[this.infusionProject[i][j].infuseGroup-1]=this.infusionProject[i][j].days
          //     this.frequency[this.infusionProject[i][j].infuseGroup-1]=this.infusionProject[i][j].frequency
          //     this.infuseUse[this.infusionProject[i][j].infuseGroup-1]=this.infusionProject[i][j].infuseUse

          //     }

          // }
          this.medicalEditTabsValue = this.medicalEditTabs[0];
          this.medicalClickTabsValue = this.medicalEditTabsValue;
          this.MedicalCalculate();
        }
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
        this.GetOption("1088952622484547519");
        this.GetOption("1008534118685450388");
        this.GetSysParamConfig();
      },
      GetOption(optionId) {
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
          } else if (optionId == "1014474470772899990")
            this.FrequencyOption = responseData.data;
          else if (optionId == "1014474470772899985")
            this.ChineseTimeOption = responseData.data;
          else if (optionId == "1014474470772900028")
            this.WesternUseOption = responseData.data;
          else if (optionId == "1014474470772900052")
            this.DayNumOption = responseData.data;
          else if (optionId == "1014474470772900062")
            this.infuseUseOption = responseData.data;
          else if (optionId == "1014474470772900068")
            this.InfusionOption = responseData.data;
          else if (optionId == "1014474470772900058")
            this.ChineseUseTimeOption = responseData.data;
          else if (optionId == "1088952622484547519")
            this.RecipelSmallTypeList = responseData.data;
          else if (optionId == "1008534118685450388") {
            this.TreatTypeOption = [];
            if (responseData.data) {
              responseData.data.forEach((element) => {
                this.TreatTypeOption.push({
                  name: element.name,
                  value: element.value,
                });
              });
            }
          } else if (optionId == "1654447687630167011") {
            this.InfectTypeOption = [];
            if (responseData.data) {
              responseData.data.forEach((element) => {
                this.InfectTypeOption.push({
                  name: element.name,
                  value: element.value,
                });
              });
            }
          }
        });
      },
      //获取系统参数配置
      GetSysParamConfig() {
        listSysParamConfigAll(this.systemParamConfigSearch).then(responseData => {
          if (responseData.code == 100) {
            if (responseData.data.length >= 1) {
              responseData.data.forEach(data => {
                if (data.configName === "chronicDays") {
                  this.systemParamConfig.chronicDays = new Number(data.configValue);
                }
                if (data.configName === "normalDays") {
                  this.systemParamConfig.normalDays = new Number(data.configValue);
                }
              })
              console.log(this.systemParamConfig, "this.systemParamConfig")
            }
          } else {
            this.showMessage(responseData)
          }
        }).catch(error => {
          this.outputError(error)
        })
      },

      //病历配置恢复默认
      ClearMedicineCofigure() {
        console.log(this.MedicalRecordModel);
        this.ChooseRecordList.map((item) => {
          this.MedicalRecordModel[`${item.name}`] = "";
        });
        this.ChooseRecordList = [];
      },
      GetMedicineCofigure() {
        this.ChooseRecordList = [];
        this.MedicineRecordLabelList.map((item) => {
          if (this.MedicalRecordModel[`${item.name}`]) {
            this.ChooseRecordList.push(item);
          }
        });
      },
      GetMedicalRecordAll() {
        let params = [{
          columnName: "company_id",
          queryType: "=",
          value: this.Company.id,
        },
          {
            columnName: "type",
            queryType: "in",
            value: [],
          },
          {
            columnName: "goods_name",
            queryType: "like",
            value: "",
          },
        ];
        this.SearchWesternModel.params = JSON.parse(JSON.stringify(params));
        this.SearchChineseModel.params = JSON.parse(JSON.stringify(params));
        //this.GetWesternTable();
        //this.GetChineseTable();
      },
      GetWesternTable() {
        this.SearchWesternModel.params[1].columnName = "drug.type";
        this.SearchWesternModel.params[1].value = [
          "medicalType_0",
          "medicalType_2",
        ];
        //判断是否输入的是英文
        var pattern2 = new RegExp("[A-Za-z]+");
        if (pattern2.test(this.SearchWesternInput)) {
          // console.log(this.SearchWesternInput,'字符');
          this.SearchWesternModel.params[2].value =
            this.SearchWesternInput.toUpperCase();
          this.SearchWesternModel.params[2].columnName = "drug.pinyin_code";
        } else {
          this.SearchWesternModel.params[2].value = this.SearchWesternInput;
          this.SearchWesternModel.params[2].columnName = "drug.goods_name";
        }

        this.SearchWesternModel.params.push({
          columnName: "surplus_stock",
          queryType: ">",
          value: 0,
        });
        this.SearchWesternModel.params.push({
          columnName: "drug.status",
          queryType: "=",
          value: "1",
        });
        this.SearchWesternModel.params.push({
          columnName: "drug.del_flag",
          queryType: "=",
          value: "0",
        });
        listAll(this.SearchWesternModel)
          .then((responseData) => {
            if (responseData.code == 100) {
              // responseData.data.forEach((element) => {
              //     let isUnpackSell = element.isUnpackSell; //允许拆零销售
              //     let stockNumber = element.stockNumber; //库存数量
              //     let packName = element.pack.name; //包装（单位）
              //     let preparation = element.preparation; //制剂
              //     let preparationUnit = element.preparationUnit.name; //制剂单位
              //     let price = element.price; //售价
              //     let retailPrice = element.retailPrice; //拆开后拆零价
              //     let inventory = element.inventory;    //库存

              //     let stockText = "";
              //     //向下取整
              //     let packNumber = Math.floor(inventory / preparation);
              //     //取余
              //     let retailNumber = inventory % preparation;
              //     if(packNumber > 0)
              //     {
              //         stockText = packNumber + packName;
              //     }
              //     if(retailNumber > 0)
              //     {
              //         if(packNumber > 0)
              //         {
              //             stockText = stockText + retailNumber + preparationUnit;
              //         }
              //         else
              //         {
              //             stockText = retailNumber + preparationUnit;
              //         }
              //     }

              //     element["stockText"] = stockText;
              // });
              this.WesternMedicineTable = responseData.data;
            }
          })
          .catch((error) => {
            this.outputError(error);
          });

        // listAll(search).then((res)=>{
        //   if(res.code==100){
        //     console.log(res.data,'qiaoqiaokan');
        //   }
        // }).catch((error)=>{
        //   this.outputError(error)
        // })
      },
      GetInfusionTable(index) {
        this.SearchWesternModel.params[1].columnName = "drug.type";
        this.SearchWesternModel.params[1].value = [
          "medicalType_0",
          "medicalType_2",
        ];
        //判断是否输入的是英文
        var pattern2 = new RegExp("[A-Za-z]+");
        debugger
        if (pattern2.test(this.SearchInfusion[0])) {
          // console.log(this.SearchWesternInput,'字符');
          this.SearchWesternModel.params[2].value =
            this.SearchInfusion[0].toUpperCase();
          this.SearchWesternModel.params[2].columnName = "drug.pinyin_code";
        } else {
          this.SearchWesternModel.params[2].value = this.SearchInfusion[0];
          this.SearchWesternModel.params[2].columnName = "drug.goods_name";
        }

        this.SearchWesternModel.params.push({
          columnName: "surplus_stock",
          queryType: ">",
          value: 0,
        });
        this.SearchWesternModel.params.push({
          columnName: "drug.status",
          queryType: "=",
          value: "1",
        });
        this.SearchWesternModel.params.push({
          columnName: "drug.del_flag",
          queryType: "=",
          value: "0",
        });
        listAll(this.SearchWesternModel).then((responseData) => {
          if (responseData.code == 100) {
            // responseData.data.forEach((element) => {
            //     let isUnpackSell = element.isUnpackSell; //允许拆零销售
            //     let stockNumber = element.stockNumber; //库存数量
            //     let packName = element.pack.name; //包装（单位）
            //     let preparation = element.preparation; //制剂
            //     let preparationUnit = element.preparationUnit.name; //制剂单位
            //     let price = element.price; //售价
            //     let retailPrice = element.retailPrice; //拆开后拆零价
            //     let inventory = element.inventory;    //库存

            //     let stockText = "";
            //     //向下取整
            //     let packNumber = Math.floor(inventory / preparation);
            //     //取余
            //     let retailNumber = inventory % preparation;
            //     if(packNumber > 0)
            //     {
            //         stockText = packNumber + packName;
            //     }
            //     if(retailNumber > 0)
            //     {
            //         if(packNumber > 0)
            //         {
            //             stockText = stockText + retailNumber + preparationUnit;
            //         }
            //         else
            //         {
            //             stockText = retailNumber + preparationUnit;
            //         }
            //     }

            //     element["stockText"] = stockText;
            // });
            this.InfusionTable = responseData.data;
          }
        });
      },
      GetChineseTable() {
        this.SearchChineseModel.params[1].columnName = "drug.type";
        this.SearchChineseModel.params[1].value = ["medicalType_1"];
        //判断是否输入的是英文
        var pattern3 = new RegExp("[A-Za-z]+");
        if (pattern3.test(this.SearchChineseInput)) {
          // console.log(this.SearchWesternInput,'字符');
          this.SearchChineseModel.params[2].value =
            this.SearchChineseInput.toUpperCase();
          this.SearchChineseModel.params[2].columnName = "drug.pinyin_code";
        } else {
          this.SearchChineseModel.params[2].value = this.SearchChineseInput;
          this.SearchChineseModel.params[2].columnName = "drug.goods_name";
        }

        this.SearchChineseModel.params.push({
          columnName: "surplus_stock",
          queryType: ">",
          value: 0,
        });
        this.SearchWesternModel.params.push({
          columnName: "drug.status",
          queryType: "=",
          value: "1",
        });
        this.SearchWesternModel.params.push({
          columnName: "drug.del_flag",
          queryType: "=",
          value: "0",
        });
        listAll(this.SearchChineseModel).then((responseData) => {
          if (responseData.code == 100) {
            this.ChineseMedicineTable = responseData.data;
          }
        });
      },
      //西药表格行点击选择添加到已选择西药处方的表格
      RowClickWesternTable(row) {
        let recipelDetailEvtList = this.medicalClickTabsValue.content.recipelDetailEvtList;
        let count = 0;
        let hasFlag = false;
        if (
          row.drug.days.value &&
          row.drug.frequency.value &&
          row.drug.singleDosage &&
          row.drug.total != 0
        ) {
          let total = Math.ceil(
            BigNumber(row.drug.singleDosage)
              .multipliedBy(row.drug.frequency.value.split("_")[1])
              .multipliedBy(row.drug.days.name)
              .toNumber()
          );
          if (total > row.drug.total) {
            this.$message.error("该药品设置的总量上限要小于默认功效说明计算总量");
            return;
          }
        }

        recipelDetailEvtList.forEach((element) => {
          if (element.isExtra === 0) {
            count++;
            if (row.drug.id === element.drugStuffId.drugStuffId) {
              hasFlag = true;
            }
          }
        });
        if (hasFlag) {
          this.$message.error("已有该药品");
          return;
        }
        if (count == 5) {
          this.$message.error("每个西药处方最多只能添加5个药品");
          return;
        }

        let drugStuff = {
          drugStuffId: row.drug.id,
          name: row.drug.goodsName,
          price: row.drug.price,
          retailPrice: row.drug.retailPrice,
          isUnpackSell: row.drug.isUnpackSell,
          dosisUnit: row.drug.dosisUnit,
          preparationUnit: row.drug.preparationUnit,
          pack: row.drug.pack,
          drug: row.drug,
          surplusStock: row.surplusStock,
        };

        let recipelDetailEvt = {
          allFee: 0,
          total: 0,
          singleDosage: 0,
          company: this.Company,
          stuffType: 0,
          isExtra: 0,
          isUnpackSell: BigNumber(row.drug.isUnpackSell).toNumber(),
          drugStuffId: drugStuff,
          days: {
            name: 0,
            value: 'recipelDetailDays_0'
          }
        };

        this.medicalClickTabsValue.content.recipelDetailEvtList.push(
          JSON.parse(JSON.stringify(recipelDetailEvt))
        );
        setTimeout(() => {
          this.MedicalCalculate();
        }, 0);
        this.$message.success("添加成功！");
        this.$nextTick(() => {
          this.$refs['westernMedicine'].slice(-2, -1)[0].focus()
        })
      },

      //输液表格行点击选择添加到已选择西药处方的表格
      RowClickInfusionTable(row, column, event, index) {
        console.log(this.medicalClickTabsValue, "就像");
        let content = this.medicalClickTabsValue.content;
        let recipelDetailEvtList =
          this.medicalClickTabsValue.infusion.infusionProject[index];
        let count = 0;
        let hasFlag = false;
        console.log(row);

        recipelDetailEvtList.forEach((element) => {
          if (element.isExtra === 0) {
            count++;
            console.log(count, "看数据");
            if (row.id === element.drugStuffId.drugStuffId) {
              hasFlag = true;
            }
          }
        });
        // if (hasFlag) {
        //   this.$message.error("已有该药品");
        //   return;
        // }
        if (count == 5) {
          this.$message.error("每个输液处方最多只能添加5个药品");
          return;
        }

        let drugStuff = {
          drugStuffId: row.drug.id,
          name: row.drug.goodsName,
          price: row.drug.price,
          retailPrice: row.drug.retailPrice,
          isUnpackSell: row.drug.isUnpackSell,
          dosisUnit: row.drug.dosisUnit,
          preparationUnit: row.drug.preparationUnit,
          pack: row.drug.pack,
          drug: row.drug,
          surplusStock: row.surplusStock,
        };

        let recipelDetailEvt = {
          allFee: 0,
          total: 0,
          company: this.Company,
          stuffType: 0,
          isExtra: 0,
          isUnpackSell: BigNumber(row.drug.isUnpackSell).toNumber(),
          singleDosage: 0,
          skinTest: {},
          drugStuffId: drugStuff,
          infuseGroup: index + 1,
          drippingSpeed: "",
          days: {},
          frequency: {},
          infuseUse: {},
        };

        // this.medicalClickTabsValue.content.recipelDetailEvtList.push(
        //   JSON.parse(JSON.stringify(recipelDetailEvt))
        // );
        this.medicalClickTabsValue.infusion.infusionProject[index].push(
          JSON.parse(JSON.stringify(recipelDetailEvt))
        );
        this.$message.success("添加成功！");
        console.log(drugStuff);
        console.log(content);
        this.$nextTick(() => {
          this.$refs['transfusion'].slice(-2, -1)[0].focus()
          console.log("++++++++++++++++++++++", this.$refs['transfusion'])
        })
      },

      //点击西药表格后的删除按钮删除指定下标的数据
      DeleteMedicalRow(index, row, item) {
        // console.log(
        //   this.chineseTest,
        //   "就是"
        // );
        // if(row.stuffType==1){
        //   for (let i = 0; i < this.chineseTest[item.key-1].length; i++) {
        //     if(row.drugStuffId.drug.name==this.chineseTest[item.key-1][i]){
        //       this.chineseTest[item.key-1].splice(i,1)
        //       //i--;
        //     }
        //   }
        // }
        this.medicalClickTabsValue.content.recipelDetailEvtList =
          this.medicalClickTabsValue.content.recipelDetailEvtList.filter(
            (item) => item !== row
          );
        if (row.infuseGroup > 0) {
          this.medicalClickTabsValue.content.recipelDetailEvtList =
            this.medicalClickTabsValue.content.recipelDetailEvtList.filter(
              (item) =>
                item.infuseGroup !== row.infuseGroup &&
                this.medicalClickTabsValue.content.recipelDetailEvtList.indexOf(
                  item
                ) !== index
            );
          this.medicalClickTabsValue.infusion.infusionProject[
          row.infuseGroup - 1
            ].splice(index, 1);
        }
        this.MedicalCalculate();
        // console.log(row,'传说');
      },
      DeleteExMedicalRow(index, row) {
        this.medicalClickTabsValue.content.recipelDetailEvtList =
          this.medicalClickTabsValue.content.recipelDetailEvtList.filter(
            (item) => item.id !== row.id
          );
        this.medicalClickTabsValue.infusion.excharge.splice(index, 1);
        this.MedicalCalculate();
      },
      RowClickChineseTable(row) {
        let recipelDetailEvtList =
          this.medicalClickTabsValue.content.recipelDetailEvtList;
        let hasFlag = false;

        recipelDetailEvtList.forEach((element) => {
          if (element.isExtra === 0) {
            if (row.id === element.drugStuffId.drugStuffId) {
              hasFlag = true;
            }
          }
        });

        if (hasFlag) {
          this.$message.error("已有该药品");
          return;
        }

        let drugStuff = {
          drugStuffId: row.drug.id,
          name: row.drug.goodsName,
          price: row.drug.price,
          retailPrice: row.drug.retailPrice,
          isUnpackSell: row.drug.isUnpackSell,
          dosisUnit: row.drug.dosisUnit,
          preparationUnit: row.drug.preparationUnit,
          pack: row.drug.pack,
          drug: row.drug,
          surplusStock: row.surplusStock,
        };

        let recipelDetailEvt = {
          allFee: 0,
          total: 0,
          company: this.Company,
          stuffType: 1,
          isExtra: 0,
          isUnpackSell: 0, //中药处方默认按不拆零销售开处方
          drugStuffId: drugStuff,
          singleDosage: 0,
        };
        console.log(
          this.medicalClickTabsValue.content.recipelDetailEvtList,
          "查看中药"
        );
        this.medicalClickTabsValue.content.recipelDetailEvtList.push(
          JSON.parse(JSON.stringify(recipelDetailEvt))
        );
        setTimeout(() => {
          this.MedicalCalculate();
        }, 0);
        this.$message.success("添加成功！");
        this.$nextTick(() => {
          this.$refs['herbMedicine'].slice(-1)[0].focus()
          console.log("++++++++++++++++++++++", this.$refs['herbMedicine'])
        })
      },
      GetChineseFee(tabData, rowData) {
        console.log(tabData);
        console.log(tabData.content.recipelInfo, "1212");
        if (rowData) {
          let recipelInfo = tabData.content.recipelInfo;
          tabData.content.recipelInfo.dosage = recipelInfo.dosage ?
            recipelInfo.dosage :
            "";
          rowData.singleDosage = rowData.singleDosage ? rowData.singleDosage : 0;
          rowData.allFee =
            rowData.drugStuffId.price * rowData.singleDosage * recipelInfo.dosage;
        }

        this.GetChineseSumFee(tabData);
      },
      GetCostItemTable() {
        let SearchModel = {
          columnName: "",
          limit: 10,
          offset: 0,
          order: "",
          params: [{
            columnName: "company_id",
            queryType: "=",
            value: this.Company.id,
          },
            {
              columnName: "item_name",
              queryType: "like",
              value: "",
            },
            {
              columnName: "item_type",
              queryType: "<>",
              value: "treatmentItemType_4",
            },
          ],
        };
        var pattern2 = new RegExp("[A-Za-z]+");
        if (pattern2.test(this.SearchCostItemInput)) {
          // console.log(this.SearchWesternInput,'字符');
          SearchModel.params[1].value = this.SearchCostItemInput.toUpperCase();
          SearchModel.params[1].columnName = "pinyin_code";
        } else {
          SearchModel.params[1].value = this.SearchCostItemInput;
          SearchModel.params[1].columnName = "item_name";
        }
        listCostItemAll(SearchModel).then((responseData) => {
          if (responseData.code == 100) {
            this.TreatmentTable = responseData.data;
          }
        });
      },
      RowClickTreatmentTable(row) {
        let recipelDetailEvtList =
          this.medicalClickTabsValue.content.recipelDetailEvtList;
        let hasFlag = false;

        recipelDetailEvtList.forEach((element) => {
          if (element.isExtra === 0) {
            if (row.id === element.drugStuffId.drugStuffId) {
              hasFlag = true;
            }
          }
        });

        if (hasFlag) {
          return;
        }

        let drugStuff = {
          drugStuffId: row.id,
          name: row.itemName,
          price: row.salePrice,
          isUnpackSell: 0,
          pack: row.unit,
          costItem: row,
        };

        let recipelDetailEvt = {
          allFee: 0,
          total: 0,
          company: this.Company,
          stuffType: 3,
          isExtra: 0,
          isUnpackSell: 0, //诊疗处方默认按不拆零销售开处方
          drugStuffId: drugStuff,
        };

        this.medicalClickTabsValue.content.recipelDetailEvtList.push(
          JSON.parse(JSON.stringify(recipelDetailEvt))
        );
        this.$message.success("添加成功！");
        this.$nextTick(() => {
          this.$refs['DiagnosisTreatment'].slice(-2, -1)[0].focus()
          console.log("++++++++++++++++++++++", this.$refs['DiagnosisTreatment'])
        })
      },
      GetCostItemFee(index, row) {
        row.allFee = row.drugStuffId.price * row.total;
      },
      GetSurchargeTable() {
        this.SurchargeTable = [];
        this.GetSurchargeCostItemTable();
        this.GetSurchargeStuffTable();
        console.log(this.SurchargeTable);
      },
      GetSurchargeCostItemTable() {
        let SearchModel = {
          columnName: "",
          limit: 10,
          offset: 0,
          order: "",
          params: [{
            columnName: "company_id",
            queryType: "=",
            value: this.Company.id,
          },
            {
              columnName: "item_name",
              queryType: "like",
              value: "",
            },
            {
              columnName: "item_type",
              queryType: "=",
              value: "treatmentItemType_4",
            },
          ],
        };
        var pattern3 = new RegExp("[A-Za-z]+");
        if (pattern3.test(this.SearchSurchargeInput)) {
          SearchModel.params[1].value = this.SearchSurchargeInput.toUpperCase();
          SearchModel.params[1].columnName = "pinyin_code";
        } else {
          //console.log(this.SearchSurchargeInput,'字符');
          SearchModel.params[1].value = this.SearchSurchargeInput;
          SearchModel.params[1].columnName = "item_name";
        }
        listCostItemAll(SearchModel).then((responseData) => {
          if (responseData.code == 100) {
            responseData.data.forEach((element) => {
              element["stuffType"] = "3";
              this.SurchargeTable.push(element);
            });
          }
        });
      },
      GetSurchargeStuffTable() {
        let SearchModel = {
          columnName: "",
          limit: 10,
          offset: 0,
          order: "",
          params: [{
            columnName: "company_id",
            queryType: "=",
            value: this.Company.id,
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
        if (pattern2.test(this.SearchSurchargeInput)) {
          // console.log(this.SearchWesternInput,'字符');
          SearchModel.params[1].value = this.SearchSurchargeInput.toUpperCase();
          SearchModel.params[1].columnName = "stuff.pinyin_code";
        } else {
          SearchModel.params[1].value = this.SearchSurchargeInput;
          SearchModel.params[1].columnName = "stuff.name";
        }
        SearchModel.params.push({
          columnName: "stuff.status",
          queryType: "=",
          value: "1",
        });
        SearchModel.params.push({
          columnName: "stuff.del_flag",
          queryType: "=",
          value: "0",
        });
        listAll(SearchModel).then((responseData) => {
          if (responseData.code == 100) {
            responseData.data.forEach((element) => {
              element["stuffType"] = "4";
              this.SurchargeTable.push(element);
            });
          }
        });
      },
      RowClickSurchargeTable(row) {
        let recipelDetailEvtList =
          this.medicalClickTabsValue.content.recipelDetailEvtList;
        let hasFlag = false;

        recipelDetailEvtList.forEach((element) => {
          if (element.isExtra === 1) {
            if (row.id === element.drugStuffId.drugStuffId) {
              hasFlag = true;
            }
          }
        });

        if (hasFlag) {
          return;
        }

        let stuffType = row.stuffType;
        let drugStuff = {};
        if (stuffType === "3") {
          drugStuff = {
            drugStuffId: row.id,
            name: row.itemName,
            price: row.salePrice,
            isUnpackSell: 0,
            pack: row.unit,
            costItem: row,
          };
        } else if (stuffType === "4") {
          drugStuff = {
            drugStuffId: row.stuff.id,
            name: row.stuff.name,
            price: row.stuff.priceOutSell,
            retailPrice: row.stuff.retailPrice,
            isUnpackSell: row.stuff.isUnpackSell ?
              BigNumber(row.stuff.isUnpackSell).toNumber() :
              0,
            preparationUnit: row.stuff.minUnit,
            pack: row.stuff.packUnit,
            stuff: row.stuff,
            surplusStock: row.surplusStock,
          };
        }

        let recipelDetailEvt = {
          allFee: 0,
          total: 0,
          singleDosage: 0,
          company: this.Company,
          stuffType: stuffType,
          isExtra: 1,
          isUnpackSell: 0,
          drugStuffId: drugStuff,
        };

        if (this.medicalClickTabsValue.type == "recipelType_2") {
          this.medicalClickTabsValue.infusion.excharge.push(
            JSON.parse(JSON.stringify(recipelDetailEvt))
          );
        } else {
          this.medicalClickTabsValue.content.recipelDetailEvtList.push(
            JSON.parse(JSON.stringify(recipelDetailEvt))
          );
        }
        this.$message.success("添加成功！");
        this.$nextTick(() => {
          this.$refs['additionalCharge'].slice(-2, -1)[0].focus()
          console.log("++++++++++++++++++++++", this.$refs['additionalCharge'])
        })
      },
      getDataFilterTable(data, isExtra, item) {
        let arr = [];
        if (data.length == undefined) {
          arr.push(data);
        } else if (data.length >= 1) {
          for (let i = 0; i < data.length; i++) {
            if (data[i].isExtra == 0 && data[i].drugStuffId.drug) {
              if (data[i].drugStuffId.drug.type.value == "medicalType_0") {
                //   if(data[i].singleDosage==0&&data[i].singleDosage==" "&&data[i].drugStuffId.drug.singleDosage){
                //   data[i].singleDosage = data[i].drugStuffId.drug.singleDosage
                // }
                //console.log(item,'这是一个新址');
                if (
                  data[i].singleDosage == 0 &&
                  data[i].singleDosage == " " &&
                  data[i].drugStuffId.drug.singleDosage
                ) {
                  let flages = false;
                  for (let i = 0; i < item.costDebug.chineseTest.length; i++) {
                    if (
                      item.costDebug.chineseTest[i] ==
                      data[i].drugStuffId.drug.name
                    ) {
                      flages = true;
                      break;
                    }
                  }
                  if (!flages) {
                    data[i].singleDosage = data[i].drugStuffId.drug.singleDosage ?
                      data[i].drugStuffId.drug.singleDosage :
                      "";
                    item.costDebug.chineseTest.push(
                      data[i].drugStuffId.drug.name
                    );
                  }
                }
                if (
                  !data[i].frequency &&
                  data[i].drugStuffId.drug.frequency.value
                ) {
                  data[i].frequency = data[i].drugStuffId.drug.frequency;
                }
                if (!data[i].days && data[i].drugStuffId.drug.days.value) {
                  data[i].days = data[i].drugStuffId.drug.days;
                }
                if (
                  !data[i].westernMedicineUse &&
                  data[i].drugStuffId.drug.westernMedicineUse.value
                ) {
                  data[i].westernMedicineUse =
                    data[i].drugStuffId.drug.westernMedicineUse;
                }
              }
              if (data[i].drugStuffId.drug.type.value == "medicalType_1") {
                let flages = false;
                if (
                  data[i].singleDosage == 0 &&
                  data[i].singleDosage == " " &&
                  data[i].drugStuffId.drug.singleDosage
                ) {
                  for (let i = 0; i < item.costDebug.chineseTest.length; i++) {
                    if (
                      item.costDebug.chineseTest[i] ==
                      data[i].drugStuffId.drug.name
                    ) {
                      flages = true;
                      break;
                    }
                  }
                  if (!flages) {
                    data[i].singleDosage = data[i].drugStuffId.drug.singleDosage ?
                      data[i].drugStuffId.drug.singleDosage :
                      "";
                    item.costDebug.chineseTest.push(
                      data[i].drugStuffId.drug.name
                    );
                  }
                }
                if (
                  !data[i].chineseMedicineUse &&
                  data[i].drugStuffId.drug.chineseMedicineUse.value
                ) {
                  data[i].chineseMedicineUse =
                    data[i].drugStuffId.drug.chineseMedicineUse;
                }
              }
            }

            arr.push(data[i]);
          }
        }
        //  this.MedicalCalculate()

        // if(data.length>1){
        //   if(data[0].infuseGroup!=undefined){
        //    return data.filter((item) => item.isExtra === isExtra);

        // }else{
        //  return data.filter((item) => item.isExtra === isExtra);
        // }

        // }
        return arr.filter((item) => item.isExtra === isExtra);
      },
      GetSurchargeFee(index, row) {
        row.singleDosage = row.singleDosage ? row.singleDosage : 0;
        if (row.isUnpackSell == "1") {
          row.total = row.singleDosage;
          row.allFee = row.total * row.drugStuffId.retailPrice;
        } else {
          if (row.stuffType === "4") {
            row.total = row.singleDosage * row.drugStuffId.stuff.packNumber;
          } else if (row.stuffType === "3") {
            row.total = row.singleDosage;
          }

          row.allFee = row.singleDosage * row.drugStuffId.price;
        }
      },
      MedicalCalculate() {
        let n1 = 0; //西药处方个数
        let n2 = 0; //中药处方个数
        let n3 = 0; //输液处方个数
        let n4 = 0; //诊疗项目处方个数
        let seq = 0; //处方顺序
        let fullAmount = 0; //所有处方的费用汇总
        // if(!this.medicalEditTabs.length>0){
        //   fullAmount=
        // }
        this.medicalEditTabs.forEach((tabElement) => {
          let medicalFullAmount = 0; //处方所有费用 = 处方费用汇总 + 附加费汇总
          let medicalAmount = 0; //处方费用汇总
          let surchargeAmount = 0; //附加费汇总
          let detailSeq = 0; //处方明细顺序
          let recipelInfo = tabElement.content.recipelInfo;
          let recipelTypeName = recipelInfo.recipelType.name;
          let recipelDetailEvtList = tabElement.content.recipelDetailEvtList ?
            tabElement.content.recipelDetailEvtList :
            [];
          seq++;
          //西药处方
          if (tabElement.type === "recipelType_0") {
            n1++;
            tabElement.title = recipelTypeName + n1;
            recipelDetailEvtList.forEach((rowElement) => {
              if (rowElement.isExtra === 0) {
                detailSeq++;
                rowElement.seq = detailSeq;

                rowElement.singleDosage = rowElement.singleDosage ? rowElement.singleDosage : "";
                rowElement.total = rowElement.total ? rowElement.total : 0;
                rowElement.allFee = rowElement.allFee ? rowElement.allFee : 0;
                if (rowElement.frequency && rowElement.days) {
                  let total = Math.ceil(
                    BigNumber(rowElement.singleDosage - 0)
                      .multipliedBy(rowElement.frequency.value.split("_")[1])
                      .multipliedBy(rowElement.days.name)
                      .dividedBy(rowElement.drugStuffId.drug.dosis)
                      .toNumber()
                  );
                  if (rowElement.isUnpackSell == "1") {
                    rowElement.unitPrice = rowElement.drugStuffId.retailPrice;
                    rowElement.total = total;
                    rowElement.allFee = (BigNumber(rowElement.total)
                      .multipliedBy(rowElement.drugStuffId.retailPrice)
                      .toNumber()).toFixed(2);
                  } else {
                    rowElement.unitPrice = rowElement.drugStuffId.price;
                    rowElement.total = BigNumber(
                      Math.ceil(
                        BigNumber(total)
                          .dividedBy(rowElement.drugStuffId.drug.preparation)
                          .toNumber()
                      )
                    )
                      .multipliedBy(rowElement.drugStuffId.drug.preparation)
                      .toNumber();
                    rowElement.allFee = BigNumber(
                      Math.ceil(
                        BigNumber(total)
                          .dividedBy(rowElement.drugStuffId.drug.preparation)
                          .toNumber()
                      )
                    )
                      .multipliedBy(rowElement.drugStuffId.price)
                      .toNumber();
                  }
                }
                if (
                  rowElement.drugStuffId.surplusStock < rowElement.total &&
                  !this.isReadOnly &&
                  tabElement.content.recipelInfo.dispensionStatus == 0
                ) {
                  if (!this.beyondInventoryType) {
                    this.beyondInventoryType = true;
                  }
                  this.$message.error(
                    rowElement.drugStuffId.name +
                    "库存不足，请修改数量或者添加库存！"
                  );
                } else {
                  this.beyondInventoryType = false;
                }
                medicalAmount = BigNumber(medicalAmount)
                  .plus(rowElement.allFee)
                  .toNumber();
              }
            });
            medicalFullAmount = BigNumber(medicalFullAmount)
              .plus(medicalAmount)
              .toNumber();
          }
          //中药处方
          else if (tabElement.type === "recipelType_1") {
            n2++;
            tabElement.title = recipelTypeName + n2;
            recipelInfo.dosage = recipelInfo.dosage ? recipelInfo.dosage : "";
            recipelDetailEvtList.forEach((rowElement) => {
              if (rowElement.isExtra === 0) {
                detailSeq++;
                rowElement.seq = detailSeq;
                if (
                  rowElement.singleDosage == " " ||
                  rowElement.singleDosage == ""
                ) {
                  if (recipelInfo.dosage == "") {
                    rowElement.singleDosage = "";
                    rowElement.total = BigNumber(0)
                      .multipliedBy(0)
                      .multipliedBy(rowElement.drugStuffId.drug.preparation)
                      .toNumber();
                    rowElement.allFee = BigNumber(rowElement.drugStuffId.price)
                      .multipliedBy(0)
                      .multipliedBy(0)
                      .toNumber();
                    rowElement.unitPrice = rowElement.drugStuffId.price;

                    medicalAmount = BigNumber(medicalAmount)
                      .plus(rowElement.allFee)
                      .toNumber();
                  } else {
                    rowElement.singleDosage = "";
                    rowElement.total = BigNumber(0)
                      .multipliedBy(recipelInfo.dosage)
                      .multipliedBy(rowElement.drugStuffId.drug.preparation)
                      .toNumber();
                    rowElement.allFee = BigNumber(rowElement.drugStuffId.price)
                      .multipliedBy(0)
                      .multipliedBy(recipelInfo.dosage)
                      .toNumber();
                    rowElement.unitPrice = rowElement.drugStuffId.price;

                    medicalAmount = BigNumber(medicalAmount)
                      .plus(rowElement.allFee)
                      .toNumber();
                  }
                } else {
                  if (recipelInfo.dosage == "") {
                    rowElement.singleDosage = rowElement.singleDosage ?
                      rowElement.singleDosage - 0 :
                      0;
                    rowElement.total = BigNumber(rowElement.singleDosage)
                      .multipliedBy(0)
                      .multipliedBy(rowElement.drugStuffId.drug.preparation)
                      .toNumber();
                    rowElement.allFee = BigNumber(rowElement.drugStuffId.price)
                      .multipliedBy(rowElement.singleDosage)
                      .multipliedBy(0)
                      .toNumber();
                    rowElement.unitPrice = rowElement.drugStuffId.price;

                    medicalAmount = BigNumber(medicalAmount)
                      .plus(rowElement.allFee)
                      .toNumber();
                  } else {
                    rowElement.singleDosage = rowElement.singleDosage ?
                      rowElement.singleDosage - 0 :
                      0;
                    rowElement.total = BigNumber(rowElement.singleDosage)
                      .multipliedBy(recipelInfo.dosage)
                      .multipliedBy(rowElement.drugStuffId.drug.preparation)
                      .toNumber();
                    rowElement.allFee = BigNumber(rowElement.drugStuffId.price)
                      .multipliedBy(rowElement.singleDosage)
                      .multipliedBy(recipelInfo.dosage)
                      .toNumber();
                    rowElement.unitPrice = rowElement.drugStuffId.price;

                    medicalAmount = BigNumber(medicalAmount)
                      .plus(rowElement.allFee)
                      .toNumber();
                  }
                }

                if (
                  rowElement.drugStuffId.surplusStock < rowElement.total &&
                  !this.isReadOnly &&
                  tabElement.content.recipelInfo.dispensionStatus == 0
                ) {
                  if (!this.beyondInventoryType) {
                    this.beyondInventoryType = true;
                  }
                  this.$message.error(
                    rowElement.drugStuffId.name +
                    "库存不足，请修改数量或者添加库存！"
                  );
                } else {
                  this.beyondInventoryType = false;
                }
              }
            });
            medicalFullAmount = BigNumber(medicalFullAmount)
              .plus(medicalAmount)
              .toNumber();
          }
          //输液处方
          else if (tabElement.type === "recipelType_2") {
            n3++;
            tabElement.title = recipelTypeName + n3;
            //  tabElement.content.recipelDetailEvtList=this.infusionProject

            let arr = 0;
            tabElement.content.recipelDetailEvtList = [];
            for (let i = 0; i < tabElement.infusion.infusionProject.length; i++) {
              for (
                let j = 0; j < tabElement.infusion.infusionProject[i].length; j++
              ) {
                tabElement.infusion.infusionProject[i][j].drippingSpeed =
                  tabElement.infusion.drippingSpeed[
                  tabElement.infusion.infusionProject[i][j].infuseGroup - 1
                    ];
                tabElement.infusion.infusionProject[i][j].days =
                  tabElement.infusion.days[
                  tabElement.infusion.infusionProject[i][j].infuseGroup - 1
                    ];
                tabElement.infusion.infusionProject[i][j].frequency =
                  tabElement.infusion.frequency[
                  tabElement.infusion.infusionProject[i][j].infuseGroup - 1
                    ];
                tabElement.infusion.infusionProject[i][j].infuseUse =
                  tabElement.infusion.infuseUse[
                  tabElement.infusion.infusionProject[i][j].infuseGroup - 1
                    ];
                tabElement.content.recipelDetailEvtList[arr] =
                  tabElement.infusion.infusionProject[i][j];
                arr++;
              }
            }
            // let countArr=0
            // countArr=arr
            // for (let i = 0; i < this.excharge.length; i++) {
            //   tabElement.content.recipelDetailEvtList.push(this.excharge[i])
            //  // tabElement.content.recipelDetailEvtList[countArr+1]=this.excharge[i]
            //   //countArr++;
            // }
            for (let i = 0; i < tabElement.infusion.excharge.length; i++) {
              tabElement.content.recipelDetailEvtList.push(
                tabElement.infusion.excharge[i]
              );
            }
            recipelDetailEvtList = tabElement.content.recipelDetailEvtList ?
              tabElement.content.recipelDetailEvtList :
              [];
            console.log(this.medicalEditTabs, "kankan");

            //  recipelInfo.frequency = recipelInfo.frequency ? recipelInfo.frequency : 0;
            let infusionRecipelDetailEvtList =
              tabElement.content.recipelDetailEvtList;
            infusionRecipelDetailEvtList.forEach((rowElement) => {
              if (rowElement.isExtra === 0) {
                detailSeq++;
                rowElement.seq = detailSeq;
                rowElement.singleDosage = rowElement.singleDosage ?
                  rowElement.singleDosage :
                  "";
                rowElement.total = rowElement.total ? rowElement.total : 0;
                rowElement.allFee = rowElement.allFee ? rowElement.allFee : 0;
                if (rowElement.frequency.value && rowElement.days.name) {
                  let total = Math.ceil(
                    BigNumber(rowElement.singleDosage - 0)
                      .multipliedBy(rowElement.frequency.value.split("_")[1])
                      .multipliedBy(rowElement.days.name)
                      .dividedBy(rowElement.drugStuffId.drug.dosis)
                      .toNumber()
                  );
                  if (rowElement.isUnpackSell == "1") {
                    rowElement.unitPrice = rowElement.drugStuffId.retailPrice;
                    rowElement.total = total;
                    rowElement.allFee = BigNumber(rowElement.total)
                      .multipliedBy(rowElement.drugStuffId.retailPrice)
                      .toNumber();
                  } else {
                    rowElement.unitPrice = rowElement.drugStuffId.price;
                    rowElement.total = BigNumber(
                      Math.ceil(
                        BigNumber(total)
                          .dividedBy(rowElement.drugStuffId.drug.preparation)
                          .toNumber()
                      )
                    )
                      .multipliedBy(rowElement.drugStuffId.drug.preparation)
                      .toNumber();
                    rowElement.allFee = BigNumber(
                      Math.ceil(
                        BigNumber(total)
                          .dividedBy(rowElement.drugStuffId.drug.preparation)
                          .toNumber()
                      )
                    )
                      .multipliedBy(rowElement.drugStuffId.price)
                      .toNumber();
                  }
                }
                if (
                  rowElement.drugStuffId.surplusStock < rowElement.total &&
                  !this.isReadOnly &&
                  tabElement.content.recipelInfo.dispensionStatus == 0
                ) {
                  if (!this.beyondInventoryType) {
                    this.beyondInventoryType = true;
                  }
                  this.$message.error(
                    rowElement.drugStuffId.name +
                    "库存不足，请修改数量或者添加库存！"
                  );
                } else {
                  this.beyondInventoryType = false;
                }
                medicalAmount = BigNumber(medicalAmount)
                  .plus(rowElement.allFee)
                  .toNumber();
              }
            });
            medicalFullAmount = BigNumber(medicalFullAmount)
              .plus(medicalAmount)
              .toNumber();
          }
          //诊疗项目处方
          else if (tabElement.type === "recipelType_3") {
            n4++;
            tabElement.title = recipelTypeName + n4;
            recipelDetailEvtList.forEach((rowElement) => {
              if (rowElement.isExtra === 0) {
                detailSeq++;
                rowElement.seq = detailSeq;
                rowElement.total = rowElement.total ? rowElement.total : "";
                rowElement.allFee = BigNumber(rowElement.drugStuffId.price)
                  .multipliedBy(rowElement.total - 0)
                  .toNumber();
                rowElement.unitPrice = rowElement.drugStuffId.price;

                medicalAmount = BigNumber(medicalAmount)
                  .plus(rowElement.allFee)
                  .toNumber();
              }
            });
            medicalFullAmount = BigNumber(medicalFullAmount)
              .plus(medicalAmount)
              .toNumber();
          } else if (tabElement.type === "recipelType_5") {
            n1++;
            tabElement.title = recipelTypeName + n1;
            recipelDetailEvtList.forEach((rowElement) => {
              // if (rowElement.isExtra === 0) {
              //   detailSeq++;
              //   rowElement.seq = detailSeq;
              //   rowElement.total = rowElement.total ? rowElement.total : "";
              //   rowElement.allFee = BigNumber(rowElement.drugStuffId.price)
              //     .multipliedBy(rowElement.total-0)
              //     .toNumber();
              //   rowElement.unitPrice = rowElement.drugStuffId.price;

              medicalAmount = BigNumber(medicalAmount)
                .plus(rowElement.allFee)
                .toNumber();
              // }
            });
            medicalFullAmount = BigNumber(medicalFullAmount)
              .plus(medicalAmount)
              .toNumber();
          }

          //每个处方的附加费
          recipelDetailEvtList.forEach((rowElement) => {
            console.log("精神不好估计按时归还健康卡");
            // if(rowElement.infuseGroup==undefined){
            if (rowElement.isExtra === 1) {
              detailSeq++;
              rowElement.seq = detailSeq;
              rowElement.singleDosage =
                rowElement.singleDosage && rowElement.singleDosage != 0 ?
                  rowElement.singleDosage :
                  "";
              if (rowElement.isUnpackSell == "1") {
                rowElement.total = rowElement.singleDosage - 0;
                rowElement.allFee = BigNumber(rowElement.drugStuffId.retailPrice)
                  .multipliedBy(rowElement.total)
                  .toNumber();
                rowElement.unitPrice = rowElement.drugStuffId.retailPrice;
                console.log(rowElement, "奇怪");
                if (
                  rowElement.drugStuffId.surplusStock < rowElement.total &&
                  !this.isReadOnly &&
                  tabElement.content.recipelInfo.dispensionStatus == 0
                ) {
                  if (!this.beyondInventoryType) {
                    this.beyondStuffInventoryType = true;
                  }
                  this.$message.error(
                    rowElement.drugStuffId.name +
                    "库存不足，请修改数量或者添加库存！"
                  );
                } else {
                  this.beyondStuffInventoryType = false;
                }
              } else {
                //材料
                if (rowElement.stuffType == "4") {
                  rowElement.total = BigNumber(rowElement.singleDosage - 0)
                    .multipliedBy(rowElement.drugStuffId.stuff.packNumber)
                    .toNumber();

                  if (
                    rowElement.drugStuffId.surplusStock < rowElement.total &&
                    !this.isReadOnly &&
                    tabElement.content.recipelInfo.dispensionStatus == 0
                  ) {
                    if (!this.beyondInventoryType) {
                      this.beyondStuffInventoryType = true;
                    }
                    this.$message.error(
                      rowElement.drugStuffId.name +
                      "库存不足，请修改数量或者添加库存！"
                    );
                  } else {
                    this.beyondStuffInventoryType = false;
                  }
                }
                //诊疗项目
                else if (rowElement.stuffType == "3") {
                  rowElement.total = rowElement.singleDosage - 0;
                }

                rowElement.unitPrice = rowElement.drugStuffId.price;
                rowElement.allFee = BigNumber(rowElement.singleDosage - 0)
                  .multipliedBy(rowElement.drugStuffId.price)
                  .toNumber();
              }

              surchargeAmount = BigNumber(surchargeAmount)
                .plus(rowElement.allFee)
                .toNumber();
            }
            //}
          });

          medicalFullAmount = BigNumber(medicalFullAmount)
            .plus(surchargeAmount)
            .toNumber();

          recipelInfo.name = tabElement.title;
          recipelInfo.seq = seq;
          recipelInfo.medicalAmount = medicalAmount;
          recipelInfo.surchargeAmount = surchargeAmount;
          recipelInfo.fee = medicalFullAmount;
          fullAmount = BigNumber(fullAmount).plus(recipelInfo.fee).toNumber();
        });

        this.payAmount = fullAmount;
      },
      FinishVisit() {
        let FormCheck = true;
        this.$refs["BasicInfoForm"].validate((valid) => {
          if (valid) {
            //alert('submit!');
          } else {
            console.log("error submit!!");
            FormCheck = false;
            return false;
          }
        });

        if (!FormCheck) {
          return;
        }

        let recipelInfoEvtList = [];

        console.log(this.medicalEditTabs);
        let returnForEach = false;
        let hasSelsmallType = false;
        let numberCheck = false;
        if (this.medicalEditTabs.length == 0) {
          this.$message.error("处方数量不能为0");
          return;
        }
        this.medicalEditTabs.forEach((tabElement) => {
          //西药处方
          if (
            tabElement.type == "recipelType_0" &&
            !this.MedicalRecordModel.westernDiagnose
          ) {
            returnForEach = true;
          }
          //中药处方
          if (
            tabElement.type == "recipelType_1" &&
            !this.MedicalRecordModel.chinaDiagnose
          ) {
            returnForEach = true;
          }
          if (
            !tabElement.content.recipelInfo.smallType &&
            tabElement.type != "recipelType_3"
          ) {
            hasSelsmallType = true;
          }
          if (tabElement.content.recipelDetailEvtList.length == 0) {
            numberCheck = true;
          }
          recipelInfoEvtList.push(tabElement.content);
        });

        if (returnForEach) {
          this.$message.error("存在西药处方或中药处方，请填写对应诊断说明。");
          return;
        }

        if (hasSelsmallType) {
          this.$message.error("请检查每个处方里的分类是否已选择。");
          return;
        }

        if (numberCheck) {
          this.$message.error("请检查每个处方里是否有药品。");
          return;
        }
        this.MedicalRecordModel.company = this.Company;
        this.MedicalRecordModel.doctor.id = this.UserId;

        let medicalRecipelEvt = {
          medicalRecord: this.MedicalRecordModel,
          patient: this.BasicInfoModel,
          recipelInfoEvtList: recipelInfoEvtList,
          isInstant: this.isInstant,
        };
        console.log(medicalRecipelEvt);
        let formData = new FormData();
        formData.append("entity", JSON.stringify(medicalRecipelEvt));
        this.hint = false;
        allSaveMedicalRecord(formData).then((responseData) => {
          if (responseData.code == 100) {
            this.$message.success("操作成功！");
            this.hasBtnFinishVisit = false;
            this.hasBtnPrint = false;
            this.isReadOnly = true;
            this.GetPreparePatientList();
            this.GetAlreadyPatientList();
          } else {
            this.$message.error("完成接诊失败！");
          }
        });
      },
      invalidMedical() {
        console.log(this.medicalClickTabsValue);
        if (
          !this.medicalClickTabsValue.content ||
          !this.medicalClickTabsValue.content.recipelInfo.id
        ) {
          this.$message.error("未选择处方");
          return;
        }
        let id = this.medicalClickTabsValue.content.recipelInfo.id;

        invalidStatus(id).then((responseData) => {
          if (responseData.code == 100) {
            this.$message.success("处方作废成功！");
            this.cancellation = true;
            this.AlreadyPatientDeletilQuery();
          } else {
            this.$message.error("处方作废失败！");
          }
        });
      },
      checkInventory(row) {
        console.log(row.total, "当前行");
        // alert('hahahahahhahaha')
        if (row.total > row.drugStuffId.surplusStock) {
          //
          if (!this.beyondInventoryType) {
            this.beyondInventoryType = true;
          }
          this.$message.error(
            row.drugStuffId.name + "超出库存总量，请重新输入或者入库药品!"
          );
        } else {
          this.beyondInventoryType = false;
        }
      },
      //默认之展开一行
      // 折叠面板每次只能展开一行
      expandSelect(row, expandedRows) {
        console.log("expandedRows", expandedRows);
        console.log("row", row);
        var that = this;
        if (expandedRows.length) {
          that.expands = [];
          if (row) {
            that.expands.push(row.id); // 每次push进去的是每行的ID
            expandedRows;
          }
        } else {
          that.expands = []; // 默认不展开
        }
        console.log("that.expands", that.expands);
      },

      blmbexpandSelect(row, expandedRows) {
        this.blmbtableData.forEach((item) => {
          if (item.mbbm == row.mbbm) {
            this.blmbList.map((mapitem) => {
              if (mapitem.name == "personalHistory") {
                mapitem.value = item.grs;
              } else if (mapitem.name == "allergyHistory") {
                mapitem.value = item.gms;
              } else if (mapitem.name == "diseaseHistory") {
                mapitem.value = item.jbs;
              } else if (mapitem.name == "infectiousHistory") {
                mapitem.value = item.crbs;
              } else if (mapitem.name == "surgeryHistory") {
                mapitem.value = item.sss;
              } else if (mapitem.name == "transfusionHistory") {
                mapitem.value = item.sxs;
              } else if (mapitem.name == "physiqueCheck") {
                mapitem.value = item.tgjc;
              } else if (mapitem.name == "emergencyDiagnose") {
                mapitem.value = item.jzzd;
              } else if (mapitem.name == "emergencyEffect") {
                mapitem.value = item.jzxg;
              } else if (mapitem.name == "familyHistory") {
                mapitem.value = item.jzs;
              } else if (mapitem.name == "lunariaHistory") {
                mapitem.value = item.yjz;
              } else if (mapitem.name == "pregnancyHistory") {
                mapitem.value = item.hys;
              } else if (mapitem.name == "assistCheck") {
                mapitem.value = item.fzjc;
              } else if (mapitem.name == "nowHistory") {
                mapitem.value = item.xbs;
              } else if (mapitem.name == "beforeHistory") {
                mapitem.value = item.jws;
              } else if (mapitem.name == "epidemicDisease") {
                mapitem.value = item.lxbxs;
              } else if (mapitem.name == "otherCheck") {
                mapitem.value = item.qtjc;
              } else if (mapitem.name == "handlingSituation") {
                mapitem.value = item.clqk;
              } else if (mapitem.name == "healthEducation") {
                mapitem.value = item.gthjkjy;
              }
            });
          }
        });

        console.log("expandedRows", expandedRows);
        console.log("row", row);
        var that = this;
        if (expandedRows.length) {
          that.blmbexpands = [];
          if (row) {
            that.blmbexpands.push(row.mbbm); // 每次push进去的是每行的ID
            expandedRows;
          }
        } else {
          that.blmbexpands = []; // 默认不展开
        }
        console.log("that.expands", that.expands);
      },
      //病历模板弹框事件
      blmbclick() {
        this.Getblmbtable();
        this.blmbDialogVisible = true;
      },
      //病历历史调用
      onMedicalHistory() {
        console.log("我是病历历史");
        this.$message.warning("开发中。。。");
      },
      blmbonSizeChange(val) {
        this.blmbcurrentPage = 1;
        this.blmbcxrc.limit = val;
        this.blmbcxrc.offset = (this.blmbcurrentPage - 1) * val;
        this.Getblmbtable();
      },
      blmbonSizeChange(val) {
        this.blmbcurrentPage = 1;
        this.blmbcxrc.limit = val;
        this.blmbcxrc.offset = (this.blmbcurrentPage - 1) * val;
        this.Getblmbtable();
      },
      blmbonCurrentChange(val) {
        this.blmbcxrc.offset = (val - 1) * this.blmbcxrc.limit;
        this.blmbcurrentPage = val;
        this.Getblmbtable();
      },
      blmbindexMethod(index) {
        return (this.blmbcurrentPage - 1) * this.blmbcxrc.limit + index + 1;
      },
      Getblmbtable() {
        this.blmbtableData = [];
        this.blmbcxrc.mbmc = this.blmbcxrc.mbmc;
        getblmblist(this.blmbcxrc)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.blmbpatientTotal = responseData.data.total;
              if (responseData.data.total > 0) {
                responseData.data.rows.forEach((item) => {
                  if (item.mblx == "0") {
                    item.mblx = "通用";
                  } else if (item.mblx == "1") {
                    item.mblx = "个人";
                  } else {
                    {
                      item.mblx = "";
                    }
                  }
                  if (item.bllx == "0") {
                    item.bllx = "西医病历";
                  } else if (item.bllx == "1") {
                    item.bllx = "中医病历";
                  } else {
                    {
                      item.bllx = "";
                    }
                  }
                  this.blmbtableData.push({
                    // mbbm:item.mbbm,
                    // mbmc: item.mbmc,
                    // mblx: item.mblx,
                    // bllx: item.bllx,
                    cjrq: item.createdTime,
                    cjr: item.cjr,
                    mbbm: item.mbbm,
                    companyId: item.companyId,
                    delFlag: item.delFlag,
                    createdBy: item.createdBy,
                    createdTime: item.createdTime,
                    updatedBy: item.updatedBy,
                    updatedTime: item.updatedTime,
                    createdID: item.createdID,
                    mbmc: item.mbmc,
                    mblx: item.mblx,
                    bllx: item.bllx,
                    zs: item.zs,
                    grs: item.grs,
                    gms: item.gms,
                    jbs: item.jbs,
                    crbs: item.crbs,
                    sss: item.sss,
                    sxs: item.sxs,
                    tgjc: item.tgjc,
                    jzzd: item.jzzd,
                    jzxg: item.jzxg,
                    jzs: item.jzs,
                    yjz: item.yjz,
                    hys: item.hys,
                    fzjc: item.fzjc,
                    xbs: item.xbs,
                    jws: item.jws,
                    lxbxs: item.lxbxs,
                    qtjc: item.qtjc,
                    clqk: item.clqk,
                    gthjkjy: item.gthjkjy,
                    temperature: item.temperature,
                    pulse: item.pulse,
                    breathe: item.breathe,
                    bloodPressure: item.bloodPressure,
                    westernDiagnose: item.westernDiagnose,
                    chinaDiagnose: item.chinaDiagnose,
                  });
                });
              }
            }
          })
          .catch((error) => {
            this.$message.error(error);
          });
      },
      blmbdialogonSubmit() {
        this.Getblmbtable();
      },
      blmbdialogresetCondition() {
        this.blmbcxrc = {
          limit: 20,
          offset: 0,
          companyId: currentUser.company.id,
          mbmc: "",
          cjrid: currentUser.id,
        };
        this.Getblmbtable();
      },
      diaoyong(row) {
        console.log("进来没有")
        this.ChooseRecordList = [];
        this.MedicalRecordModel.patientTell = row.zs;
        this.MedicineRecordLabelList.map((item) => {
          if (
            row.grs != "" &&
            row.grs != null &&
            item.name == "personalHistory"
          ) {
            this.MedicalRecordModel.personalHistory = row.grs;
            this.ChooseRecordList.push(item);
          } else if (
            row.gms != "" &&
            row.gms != null &&
            item.name == "allergyHistory"
          ) {
            this.MedicalRecordModel.allergyHistory = row.gms;
            this.ChooseRecordList.push(item);
          } else if (
            row.jbs != "" &&
            row.jbs != null &&
            item.name == "diseaseHistory"
          ) {
            this.MedicalRecordModel.diseaseHistory = row.jbs;
            this.ChooseRecordList.push(item);
          } else if (
            row.crbs != "" &&
            row.crbs != null &&
            item.name == "infectiousHistory"
          ) {
            this.MedicalRecordModel.infectiousHistory = row.crbs;
            this.ChooseRecordList.push(item);
          } else if (
            row.sss != "" &&
            row.sss != null &&
            item.name == "surgeryHistory"
          ) {
            this.MedicalRecordModel.surgeryHistory = row.sss;
            this.ChooseRecordList.push(item);
          } else if (
            row.sxs != "" &&
            row.sxs != null &&
            item.name == "transfusionHistory"
          ) {
            this.MedicalRecordModel.transfusionHistory = row.sxs;
            this.ChooseRecordList.push(item);
          } else if (
            row.tgjc != "" &&
            row.tgjc != null &&
            item.name == "physiqueCheck"
          ) {
            this.MedicalRecordModel.physiqueCheck = row.tgjc;
            this.ChooseRecordList.push(item);
          } else if (
            row.jzzd != "" &&
            row.jzzd != null &&
            item.name == "emergencyDiagnose"
          ) {
            this.MedicalRecordModel.emergencyDiagnose = row.jzzd;
            this.ChooseRecordList.push(item);
          } else if (
            row.jzxg != "" &&
            row.jzxg != null &&
            item.name == "emergencyEffect"
          ) {
            this.MedicalRecordModel.emergencyEffect = row.jzxg;
            this.ChooseRecordList.push(item);
          } else if (
            row.jzs != "" &&
            row.jzs != null &&
            item.name == "familyHistory"
          ) {
            this.MedicalRecordModel.familyHistory = row.jzs;
            this.ChooseRecordList.push(item);
          } else if (
            row.yjz != "" &&
            row.yjz != null &&
            item.name == "lunariaHistory"
          ) {
            this.MedicalRecordModel.lunariaHistory = row.yjz;
            this.ChooseRecordList.push(item);
          } else if (
            row.hys != "" &&
            row.hys != null &&
            item.name == "pregnancyHistory"
          ) {
            this.MedicalRecordModel.pregnancyHistory = row.hys;
            this.ChooseRecordList.push(item);
          } else if (
            row.fzjc != "" &&
            row.fzjc != null &&
            item.name == "assistCheck"
          ) {
            this.MedicalRecordModel.assistCheck = row.fzjc;
            this.ChooseRecordList.push(item);
          } else if (
            row.xbs != "" &&
            row.xbs != null &&
            item.name == "nowHistory"
          ) {
            this.MedicalRecordModel.nowHistory = row.xbs;
            this.ChooseRecordList.push(item);
          } else if (
            row.jws != "" &&
            row.jws != null &&
            item.name == "beforeHistory"
          ) {
            this.MedicalRecordModel.beforeHistory = row.jws;
            this.ChooseRecordList.push(item);
          } else if (
            row.lxbxs != "" &&
            row.lxbxs != null &&
            item.name == "epidemicDisease"
          ) {
            this.MedicalRecordModel.epidemicDisease = row.lxbxs;
            this.ChooseRecordList.push(item);
          } else if (
            row.qtjc != "" &&
            row.qtjc != null &&
            item.name == "otherCheck"
          ) {
            this.MedicalRecordModel.otherCheck = row.qtjc;
            this.ChooseRecordList.push(item);
          } else if (
            row.clqk != "" &&
            row.clqk != null &&
            item.name == "handlingSituation"
          ) {
            this.MedicalRecordModel.handlingSituation = row.clqk;
            this.ChooseRecordList.push(item);
          } else if (
            row.gthjkjy != "" &&
            row.gthjkjy != null &&
            item.name == "healthEducation"
          ) {
            this.MedicalRecordModel.healthEducation = row.gthjkjy;
            this.ChooseRecordList.push(item);
          }
        });
        console.log(row, "调用模板")
        this.BasicInfoModel.temperature = row.temperature
        this.BasicInfoModel.pulse = row.pulse
        this.BasicInfoModel.breathe = row.breathe
        this.BasicInfoModel.bloodPressure = row.bloodPressure
        this.MedicalRecordModel.westernDiagnose = row.westernDiagnose
        this.MedicalRecordModel.chinaDiagnose = row.chinaDiagnose
        this.blmbDialogVisible = false;
      },
      saveblmbclick() {
        this.blmblxDialogVisible = true;
      },
      //关闭弹窗
      blmbonDialogClose() {
        this.blmbmbmc.mbmc = "";
        this.blmbmblx = "0";
        this.blmbmblx = "0";
        this.blmblxDialogVisible = false;
      },
      saveblmbsubmit(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.saveblmblist.zs = this.MedicalRecordModel.patientTell;
            this.saveblmblist.grs = this.MedicalRecordModel.personalHistory;
            this.saveblmblist.gms = this.MedicalRecordModel.allergyHistory;
            this.saveblmblist.jbs = this.MedicalRecordModel.diseaseHistory;
            this.saveblmblist.crbs = this.MedicalRecordModel.infectiousHistory;
            this.saveblmblist.sss = this.MedicalRecordModel.surgeryHistory;
            this.saveblmblist.sxs = this.MedicalRecordModel.transfusionHistory;
            this.saveblmblist.tgjc = this.MedicalRecordModel.physiqueChec;
            this.saveblmblist.jzzd = this.MedicalRecordModel.emergencyDiagnose;
            this.saveblmblist.jzxg = this.MedicalRecordModel.emergencyEffect;
            this.saveblmblist.jzs = this.MedicalRecordModel.familyHistory;
            this.saveblmblist.yjz = this.MedicalRecordModel.lunariaHistory;
            this.saveblmblist.hys = this.MedicalRecordModel.pregnancyHistory;
            this.saveblmblist.fzjc = this.MedicalRecordModel.assistChec;
            this.saveblmblist.xbs = this.MedicalRecordModel.nowHistory;
            this.saveblmblist.jws = this.MedicalRecordModel.beforeHistory;
            this.saveblmblist.lxbxs = this.MedicalRecordModel.epidemicDisease;
            this.saveblmblist.qtjc = this.MedicalRecordModel.otherCheck;
            this.saveblmblist.clqk = this.MedicalRecordModel.handlingSituation;
            this.saveblmblist.gthjkjy = this.MedicalRecordModel.healthEducation;
            this.saveblmblist.mbmc = this.blmbmbmc.mbmc;
            this.saveblmblist.mblx = this.blmbmblx;
            this.saveblmblist.bllx = this.blmbbllx;
            this.saveblmblist.temperature = this.BasicInfoModel.temperature
            this.saveblmblist.pulse = this.BasicInfoModel.pulse
            this.saveblmblist.breathe = this.BasicInfoModel.breathe
            this.saveblmblist.bloodPressure = this.BasicInfoModel.bloodPressure
            this.saveblmblist.westernDiagnose = this.MedicalRecordModel.westernDiagnose
            this.saveblmblist.chinaDiagnose = this.MedicalRecordModel.chinaDiagnose
            saveblmb(this.saveblmblist)
              .then((responseData) => {
                if (responseData.code == 100) {
                  this.blmbmbmc.mbmc = "";
                  this.blmbmblx = "0";
                  this.blmbmblx = "0";
                  this.blmbonDialogClose();
                  this.$message.success(responseData.msg);
                }
              })
              .catch((error) => {
                this.$message.error(error);
              });
          } else {
            return false;
          }
        });
      },
      JZrecordclick(done) {
        done();
      },
      Getrecordlist() {
        this.recordtableData = [];
        getrecordpatlist(this.recordpatid).then((responseData) => {
          if (responseData.code == 100) {
            console.log();
            responseData.data.forEach((item) => {
              if (item.xyzd != "" && item.xyzd != null) {
                item.xyzd;
              } else {
                item.xyzd = "零售";
              }
              if (item.zyzd != "" && item.zyzd != null) {
                item.zyzd;
              } else {
                item.zyzd = "";
              }
              this.recordtableData.push({
                jzsj: item.jzsj,
                jbxx: item.xm +
                  " /" +
                  item.nl +
                  "岁" +
                  " /" +
                  item.xyzd +
                  " /" +
                  item.zyzd,
                regid: item.regid,
                patid: item.patid,
              });
            });
          }
        });
        // .catch((error) => {
        //        this.$message.error(error);
        //      });
      },
      jzjlclick() {
        if (this.recordpatid != "" && this.recordpatid != null) {
          this.drawer = true;
          this.Getrecordlist();
        } else {
          this.$message.warning("请选择患者！");
        }
      },
      jzjlhandleClick(row) {
        this.drawer = false;

        this.clickId = row.regid;
        this.$nextTick(() => {
          this.MedicalRecordModel = this.ReturnMedicalModel();

          this.hasBtnFinishVisit = false;
          this.hasBtnPrint = true;
          this.isReadOnly = true;
          this.recipelInfoId = "";
          this.AlreadyPatientDescriptions = [];
          //根据患者id获取会员信息
          getByPatientId(row.patid)
            .then((res) => {
              if (res.code == "100") {
                console.log(res, "卡九分零四开发");
                this.member = res.data;
              } else {
                this.$message.error("后台数据异常请联系管理！");
              }
            })
            .catch();
          getPoverty(row.patid)
            .then((res) => {
              if (res.code == "100") {
                console.log(res, "贫困户");
                this.poverty = res.data;
              } else {
                this.$message.error("后台数据异常请联系管理！");
              }
            })
            .catch();
          //   //根据患者id获取会员信息
          // getByPatientId(row.patid).then(res=>{
          //   if(res.code=='100'){
          //     console.log(res,'卡九分零四开发');
          //     this.member=res.data
          //   }else{
          //     this.$message.error("后台数据异常请联系管理！")
          //   }
          // }).catch()
          allQueryMedicalRecord(row.regid).then((responseData) => {
            let arr = responseData.data;
            console.log(arr, "这是怎么回事");
            if (responseData.code == 100) {
              let recipelInfoEvtList = responseData.data.recipelInfoEvtList;
              recipelInfoEvtList.forEach((element) => {
                this.AlreadyPatientDescriptions.push(
                  JSON.parse(JSON.stringify(element.recipelInfo))
                );
              });
              this.AlreadyPatientDescriptions.sort(function (a, b) {
                a.seq = a.seq ? a.seq : 0;
                b.seq = b.seq ? b.seq : 0;
                return a.seq - b.seq;
              });
              this.MedicalRecordModel = responseData.data.medicalRecord;
              this.MedicalRecordModel.registration.id =
                responseData.data.registration.id;
              this.MedicalRecordModel.registration.treatType =
                responseData.data.registration.treatType;
              this.MedicalRecordModel.registration.morbidityTime =
                responseData.data.registration.morbidityTime;
              this.MedicalRecordModel.registration.infectType =
                responseData.data.registration.infectType;
              this.BasicInfoModel = responseData.data.patient;
              this.BasicInfoModel.breathe =
                responseData.data.registration.breathe;
              this.BasicInfoModel.temperature =
                responseData.data.registration.temperature;
              this.BasicInfoModel.pulse = responseData.data.registration.pulse;
              this.BasicInfoModel.bloodPressure =
                responseData.data.registration.bloodPressure;
              this.GetMedicineCofigure();
              this.createMedicalEditTab(recipelInfoEvtList);
              this.hint = false;
              this.userClickID = "";
            }
          });
          // this.GetPatient(itemCopy);
          console.log(this.MedicalRecordModel, "nuide");
        });
        this.overColor = 0;
        // //this.recordpatid=row.patientId.id;
        // //this.clickChangeColor=row.id
        // console.log(row.regid,this.clickId);
        // // if(this.clickId!='' && this.clickId==this.activeSelAlreadyPatientItem.id){
        // //   console.log("没进来嘛？");
        // //   return

        // // }else
        // if(row.regid!=undefined){

        //   console.log(this.clickId,row.regid,'书法家看法萨克利夫你来撒快发霉了');
        //    if(this.endHint){
        //   this.clickId=row.regid
        // // console.log(row.regid, "我的");
        // // if (!this.activeSelAlreadyPatientItem) {
        // //   return;
        // // }
        // let now = new Date()
        //   let newDate = new Date(this.activeSelAlreadyPatientItem.receptionEndDate)
        //   now = Date.parse(now)
        //   newDate = Date.parse(newDate)
        //   if(now-newDate>86400000){
        //     this.timeOut = true
        //   }else{
        //     this.timeOut = false
        //   }
        // let itemCopy = JSON.parse(
        //   JSON.stringify(this.activeSelAlreadyPatientItem)
        // );
        // this.registration = JSON.parse(
        //   JSON.stringify(this.activeSelAlreadyPatientItem)
        // );
        // this.$nextTick(() => {
        //   this.MedicalRecordModel = this.ReturnMedicalModel();

        //   this.hasBtnFinishVisit = false;
        //   this.hasBtnPrint = true;
        //   this.isReadOnly = true;
        //   this.recipelInfoId = "";
        //   this.AlreadyPatientDescriptions = [];
        //   //根据患者id获取会员信息
        //   getByPatientId(row.patid).then(res=>{
        //     if(res.code=='100'){
        //       console.log(res,'卡九分零四开发');
        //       this.member=res.data
        //     }else{
        //       this.$message.error("后台数据异常请联系管理！")
        //     }
        //   }).catch()
        //   allQueryMedicalRecord(row.regid).then((responseData) => {

        //     let arr=responseData.data
        //     console.log(arr,'这是怎么回事');
        //     if (responseData.code == 100) {

        //       let recipelInfoEvtList = responseData.data.recipelInfoEvtList;
        //       recipelInfoEvtList.forEach((element) => {
        //         this.AlreadyPatientDescriptions.push(
        //           JSON.parse(JSON.stringify(element.recipelInfo))
        //         );
        //       });
        //       this.AlreadyPatientDescriptions.sort(function (a, b) {
        //         a.seq = a.seq ? a.seq : 0;
        //         b.seq = b.seq ? b.seq : 0;
        //         return a.seq - b.seq;
        //       });
        //       console.log(this.AlreadyPatientDescriptions, "utiy");
        //       console.log(responseData.data, "poiu");
        //       this.MedicalRecordModel = responseData.data.medicalRecord;
        //       this.MedicalRecordModel.registration.id = responseData.registration.id;
        //       this.MedicalRecordModel.registration.treatType = responseData.registration.treatType;
        //       this.BasicInfoModel = responseData.data.patient;
        //       this.BasicInfoModel.breathe = responseData.registration.breathe;
        //       this.BasicInfoModel.temperature =
        //         responseData.registration.temperature;
        //       this.BasicInfoModel.pulse = responseData.registration.pulse;
        //       this.BasicInfoModel.bloodPressure = responseData.registration.bloodPressure;
        //       this.GetMedicineCofigure();
        //       this.createMedicalEditTab(recipelInfoEvtList);
        //     }
        //   });
        //   // this.GetPatient(itemCopy);
        //   console.log(this.MedicalRecordModel, "nuide");
        // });
        // this.overColor=0
        // }else{
        //   this.$confirm('当前患者正在进行编辑操作,还未保存是否确定切换?', '提示', {
        //     confirmButtonText: '确定',
        //     cancelButtonText: '取消',
        //     type: 'warning'
        //   }).then(() => {
        // this.clickId=this.activeSelAlreadyPatientItem.id
        // if (!this.activeSelAlreadyPatientItem) {
        //   return;
        // }
        // let now = new Date()
        //   let newDate = new Date(this.activeSelAlreadyPatientItem.receptionEndDate)
        //   now = Date.parse(now)
        //   newDate = Date.parse(newDate)

        //   if(now-newDate>86400000){
        //     this.timeOut = true
        //   }else{
        //     this.timeOut = false
        //   }
        // let itemCopy = JSON.parse(
        //   JSON.stringify(this.activeSelAlreadyPatientItem)
        // );
        // this.registration = JSON.parse(
        //   JSON.stringify(this.activeSelAlreadyPatientItem)
        // );
        // this.$nextTick(() => {
        //   this.MedicalRecordModel = this.ReturnMedicalModel();

        //   this.hasBtnFinishVisit = false;
        //   this.hasBtnPrint = true;
        //   this.isReadOnly = true;
        //   this.recipelInfoId = "";
        //   this.AlreadyPatientDescriptions = [];
        //    //根据患者id获取会员信息
        //   getByPatientId(row.patid).then(res=>{
        //     if(res.code=='100'){
        //       console.log(res,'卡九分零四开发');
        //       this.member=res.data
        //     }else{
        //       this.$message.error("后台数据异常请联系管理！")
        //     }
        //   }).catch()
        //   allQueryMedicalRecord(row.regid).then((responseData) => {
        //     let arr=responseData.data
        //     if (responseData.code == 100) {

        //       let recipelInfoEvtList = responseData.data.recipelInfoEvtList;
        //       recipelInfoEvtList.forEach((element) => {
        //         this.AlreadyPatientDescriptions.push(
        //           JSON.parse(JSON.stringify(element.recipelInfo))
        //         );
        //       });
        //       this.AlreadyPatientDescriptions.sort(function (a, b) {
        //         a.seq = a.seq ? a.seq : 0;
        //         b.seq = b.seq ? b.seq : 0;
        //         return a.seq - b.seq;
        //       });
        //       this.MedicalRecordModel = responseData.data.medicalRecord;
        //       this.MedicalRecordModel.registration.id = responseData.registration.id;
        //       this.MedicalRecordModel.registration.treatType = responseData.registration.treatType;
        //       this.BasicInfoModel = responseData.data.patient;
        //       this.BasicInfoModel.breathe = this.registration.breathe;
        //       this.BasicInfoModel.temperature =
        //         this.registration.temperature;
        //       this.BasicInfoModel.pulse = this.registration.pulse;
        //       this.BasicInfoModel.bloodPressure = this.registration.bloodPressure;
        //       this.GetMedicineCofigure();
        //       this.createMedicalEditTab(recipelInfoEvtList);
        //     }
        //   });
        // });
        // this.overColor=0
        // this.endHint=true
        // console.log(this.endHint);
        //   }).catch(() => {
        //     this.endHint=false

        //   });
        // }
        // }
      },
    },
    data() {
      return {
        queryModel: {
          dateRange: [],
        },
        templateQueryModel: {
          category: "3",
        },
        templateType: "",
        historyRecipelDetailList: [],
        historyRecipelList: [],
        historicalCasesList: [],
        historyRecipelAdditionalList: [],
        historyDialogVisible: false,
        caseDialogVisible: false,
        gender: 'man',
        pickerOptions: {
          shortcuts: [{
            text: '今天',
            onClick(picker) {
              picker.$emit('pick', new Date());
            }
          }, {
            text: '昨天',
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24);
              picker.$emit('pick', date);
            }
          }, {
            text: '一周前',
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', date);
            }
          }]
        },
        morbidityTime: '',
        registration: {
          morbidityTime: "",
          treatType: {
            name: "初诊",
            value: "treatType_0",
          },
          infectType: {
            name: "否",
            value: "infectType_0",
          },
        },
        basics: {},
        savelock: false,
        timeOut: false,
        medicalEditTabsValue: {},
        medicalClickTabsValue: {},
        medicalTypeList: [{
          name: "西药处方",
          type: "recipelType_0",
        },
          {
            name: "中药处方",
            type: "recipelType_1",
          },
          {
            name: "输液处方",
            type: "recipelType_2",
          },
          {
            name: "诊疗项目",
            type: "recipelType_3",
          },
        ],
        //是否快速接诊
        isInstant: 0,
        ChineseButton: false,
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
        //搜索患者信息
        SearchPatientInfo: "",
        //就诊搜索条件
        SearchPreModel: {
          columnName: "",
          limit: 20,
          offset: 0,
          order: "",
        },
        SearchAlreadyModel: {
          columnName: "reception_end_date",
          limit: 20,
          offset: 0,
          order: "desc",
        },
        activeSelAlreadyPatientItem: "",
        AlreadyPatientDescriptions: [],
        //处方小类选项
        RecipelSmallTypeList: [],
        //中药用法下拉列表
        ChineseUseOption: [],
        ChineseTimeOption: [],
        ChineseUseTimeOption: [],
        //西药频度下拉列表
        FrequencyOption: [],
        //西药用法下拉列表
        WesternUseOption: [],
        //西药天数下拉列表
        DayNumOption: [],
        infuseUseOption: [],
        InfusionOption: [],
        TreatTypeOption: [],
        InfectTypeOption: [],
        //西药处方表格主数据
        WesternMedicineTable: [],
        InfusionTable: [],
        //中药处方表格数据
        ChineseMedicineTable: [],
        //诊疗项目表格
        TreatmentTable: [],
        //附加费可选表格数据
        SurchargeTable: [],
        //搜索西药
        SearchWesternInput: "",
        //搜索中药
        SearchChineseInput: "",
        //搜索诊疗项目
        SearchCostItemInput: "",
        //搜索附加费
        SearchSurchargeInput: "",
        //搜索输液
        SearchInfusion: [""],
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
        fileIdFile: {
          fileId: "",
          action: "add",
          deletes: [], // 待删除（已上传）的文件列表
          uploads: [], // 待上传的文件列表
        },
        //病历配置字段
        MedicineRecordLabelList: [{
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
          {
            label: "现病史",
            name: "nowHistory",
            value: "",
          },
          {
            label: "既往史",
            name: "beforeHistory",
            value: "",
          },
          {
            label: "流行病学史",
            name: "epidemicDisease",
            value: "",
          },
          {
            label: "其他检查",
            name: "otherCheck",
            value: "",
          },
          {
            label: "处理情况",
            name: "handlingSituation",
            value: "",
          },
          {
            label: "个体化健康教育",
            name: "healthEducation",
            value: "",
          },
        ],
        //选择的病例配置
        ChooseRecordList: [],
        //开具处方Tabs标签页
        medicalEditTabs: [],
        //基础信息
        BasicInfoModel: {
          name: "",
          gender: {
            name: "",
          },
          age: "",
          month: "",
          phone: "",
          card: "",
          withPatientNexus: {
            name: "",
          },
          healthCardNo: "",
          pulse: "",
          breathe: "",
          temperature: "",
        },
        //病历表单
        MedicalRecordModel: this.ReturnMedicalModel(),
        //患者表单验证
        // BasicInfoFormRules: {
        //   treatType: [
        //     { required: true, message: "请选择治疗类型", trigger: "blur" },
        //   ],
        //   name: [{ required: true, message: "请输入患者姓名", trigger: "blur" }],
        //   gender: [{ required: true, message: "请选择性别", trigger: "blur" }],
        //   age: [{ required: true, message: "请输入年龄", trigger: "blur" }],
        //   phone: [{ required: true, message: "请输入联系方式", trigger: "blur" }],
        //   card: [{ required: true, message: "请输入身份证号", trigger: "blur" }],
        //   patientTell: [
        //     { required: true, message: "请输入主诉", trigger: "blur" },
        //   ],
        // },

        //开具处方数据集合
        recipelInfoEvtList: [],
        payAmount: 0,
        //打印时候的处方ID
        recipelInfoId: "",
        //是否显示完成接诊按钮
        hasBtnFinishVisit: true,
        //是否显示打印按钮
        hasBtnPrint: false,
        //只读
        isReadOnly: true,
        isBaseReadOnly: true,
        //特殊诊所
        isSpecial: false,
        BASE_API: process.env.BASE_API,
        defaultNumber: 1, //默认组号
        infusionProject: [
          []
        ],
        drippingSpeed: [""], //滴速
        days: [{}], //天数
        frequency: [{}], //频次
        infuseUse: [{}], //用法
        zushu: [1],
        excharge: [], //输液附加费
        changeData: {},
        hint: false, //用户切换提示
        clickId: "", //点击id
        chineseTest: [
          []
        ],
        timer: 0,
        historyType: "", //历史处方的处方类型
        singleRecipel: 0, //历史处方调用
        templateRecipelList: [],
        expands: [],
        getRowKeys(row) {
          return row.id;
        },
        templateDialogVisible: false, //模板处方弹出框
        saveTemplateDialogVisible: false, //保存模板处方
        saveTemplateBizFormModel: {
          type: {
            // 模板类型
            value: null,
            name: null,
          },
          company: {
            // 诊所id
            id: "",
            name: null,
          },
          recipetemplateName: "",
          category: "0",
          diagnosis: "",
          remarks: "",
          recipeTemplateInfo: {},
          recipeTemplateDetail: [{}],
          createId: "",
        }, //保存模板对象
        templateFormRules: {
          diagnosis: [{
            required: true,
            message: "请输入诊断",
            trigger: "blur"
          }],
          recipetemplateName: [{
            required: true,
            message: "请输入模板名称",
            trigger: "blur"
          },],
        }, //保存模板校验
        saveTemplateType: "", //模板类型
        saveTemplateIndex: 0, //对应的处方索引
        backgroundColors: 10000, //背景颜色
        overColor: 0, //完成就诊背景颜色
        userClickID: "", //用户id
        endHint: true, //完成就诊后切换用户是否提示
        clickChangeColor: "",
        inspectionType: [],
        inspectionSign: 0,
        inspectionDialogVisible: false,
        typeName: "",
        costInspection: {}, //检验检查保存
        uploadFiles: "", //文件id
        member: [], //会员卡
        poverty: [], //贫困

        //病历模板弹框
        blmbDialogVisible: false,
        blmblxDialogVisible: false,
        //病历模板查询条件

        blmbcurrentPage: 1,
        blmbpatientTotal: 0,
        blmbcxrc: {
          limit: 20,
          offset: 0,
          companyId: currentUser.company.id,
          mbmc: "",
        },
        blmbshow: false,
        blmbtableData: [],
        blmbgetRowKeys(row) {
          return row.mbbm;
        },
        blmbexpands: [],
        blmbList: [{
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
          {
            label: "现病史",
            name: "nowHistory",
            value: "",
          },
          {
            label: "既往史",
            name: "beforeHistory",
            value: "",
          },
          {
            label: "流行病学史",
            name: "epidemicDisease",
            value: "",
          },
          {
            label: "其他检查",
            name: "otherCheck",
            value: "",
          },
          {
            label: "处理情况",
            name: "handlingSituation",
            value: "",
          },
          {
            label: "个体化健康教育",
            name: "healthEducation",
            value: "",
          },
        ],
        saveblmblist: {
          mbbm: "",
          companyId: currentUser.company.id,
          delFlag: "0",
          createdBy: currentUser.name + "(" + currentUser.loginname + ")",
          createdTime: "",
          updatedBy: "",
          updatedTime: "",
          createdID: currentUser.id,
          mbmc: "",
          mblx: "",
          bllx: "",
          zs: "",
          grs: "",
          gms: "",
          jbs: "",
          crbs: "",
          sss: "",
          sxs: "",
          tgjc: "",
          jzzd: "",
          jzxg: "",
          jzs: "",
          yjz: "",
          hys: "",
          fzjc: "",
          xbs: "",
          jws: "",
          lxbxs: "",
          qtjc: "",
          clqk: "",
          gthjkjy: "",
        },
        blmbmbmc: {
          mbmc: "",
        },
        blmbmblx: "0",
        blmbbllx: "0",
        blmbrules: {
          mbmc: [{
            required: true,
            message: "请输入模板名称",
            trigger: "blur"
          }],
        },
        drawer: false,
        direction: "rtl",
        recordpatid: "",
        recordtableData: [],
        blmbVisit: false,

        beyondInventoryType: false, //药品超出库存状态
        beyondStuffInventoryType: false, //材料超出库存
        cancellation: false, //
        chronicDisease: false, //是否慢病
        systemParamConfig: {
          chronicDays: 90,
          normalDays: 7,
        }, //系统参数配置
        systemParamConfigSearch: {
          params: [
            {
              columnName: "category",
              queryType: "=",
              value: "western_medicine_disease_category"
            },
            {
              columnName: "company_id",
              queryType: "=",
              value: getLocalCurrentCompany().id
            }
          ]
        },
        //文件上传
        MedicalFlags: "add",
        MedicalFileObjId: "",
        deleteFileIdList: [],
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
    },
    mounted() {
      let specialCompany = JSON.parse(sessionStorage.getItem("currentCompany"));
      if (specialCompany && specialCompany.id == "1088657523871555640") {
        this.isSpecial = true;
      }
      this.GetAllOption();
      this.GetMedicalRecordAll();
      this.GetPreparePatientList();
      this.GetAlreadyPatientList();
      this.TodayActiveName = "prepare";

      // this.addMedicalEditTab(this.medicalTypeList[0]);
      console.log(BigNumber(6).multipliedBy(2.8).toNumber());
      console.log(BigNumber(6).multipliedBy("2.8").toNumber());
    },
    watch: {
      checkInventory: function (row) {
        console.log(row, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
      },
    },
  };
</script>

<style>
  .el-input-number {
    width: 1px;
    line-height: 10px;
    height: 28px;
  }

  .ones .el-table thead th {
    background-color: #f7f1f1 !important;
  }

  .two .el-table thead th {
    background-color: #e8fcfc !important;
  }

  .changeMedicalOutPatient .alreadyMedocal .el-collapse-item__header {
    border-bottom: 1px solid #dcdfe6;
    margin-right: 15px;
    background-color: rgb(234, 242, 251) !important;
  }
</style>

<style scoped>
  .shuzhuClass .el-input-number {
    width: 10% !important;
  }

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
    font-size: 16px;
    font-weight: bold;
    color: midnightblue;
  }

  /deep/ .chinese-medicine-card > .el-card__header {
    padding: 0px 20px;
  }

  /deep/ .el-divider--horizontal {
    margin: 5px 0 10px 0px;
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

  /* /deep/ .el-collapse-item__header {
    background: #409eff !important;
} */
  /* /deep/ .el-descriptions__body{
  background: #e7edf0 !important;
} */

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

  /* /deep/ .already-medocal .el-collapse-item__header{
  border-bottom: 1px solid #dcdfe6;
  margin-right: 15px;
  background: #409eff !important;
} */

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
<style>
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

  .click {
    background-color: rgb(234, 242, 251) !important;
    color: #fff;
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
  }
</style>
<style lang="scss">
  .demo-table-expand {
    font-size: 0;
  }

  .demo-table-expand label {
    width: 90px;
    color: #99a9bf;
  }

  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 45%;
  }

  .demo-table-expand .el-form-item__content {
    margin-right: 0;
    margin-bottom: 0;
    width: 45%;
  }
</style>
