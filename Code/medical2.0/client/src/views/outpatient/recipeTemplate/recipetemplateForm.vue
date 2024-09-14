<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='80%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-card class="box-card main-card">
    <el-form :model='bizFormModel' :rules='formRules' 
      ref='recipetemplateForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
        <el-col :span='24/4'>
          <el-form-item label='模板编号' prop='code' >
            <el-input :disabled='true' v-model='bizFormModel.code' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "模板编号自动生成"' ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span='24/4'>
          <el-form-item label='模板类型' prop='type' >
            <el-input :disabled='true' v-model='bizFormModel.type.name'></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/4'>
          <el-form-item label='模板名称' prop='recipetemplateName' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.recipetemplateName' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入模板名称"' ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span='24/4'>
          <el-form-item label='模板类别' prop='category' >    
                    <el-radio
                      :disabled='dialogProps.action == "view"'
                      v-model="bizFormModel.category"
                      label="0"
                      >私人模板</el-radio
                    >
                    <el-radio
                      :disabled='dialogProps.action == "view"'
                      v-model="bizFormModel.category"
                      label="1"
                      >公开模板</el-radio
                    >
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='诊断' prop='diagnosis' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.diagnosis' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入诊断"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='模板说明' prop='remarks' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.remarks' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入模板说明"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      </div>
    </el-form>
    </el-card>
    <div
        v-if="templateType==0"
        style="margin-top: 10px"
      >
        <el-row>
          <el-divider content-position="left">处方信息</el-divider>
          <el-select
            :disabled="
              dialogProps.action == 'view'
            "
            v-model="bizFormModel.recipeTemplateInfo.smallType"
            placeholder="请选择处方分类"
            style="width: 100px"
          >
            <el-option
              v-for="smallTypeItem in RecipelSmallTypeList"
              :key="smallTypeItem.value"
              :label="smallTypeItem.name"
              :value="{
                name: smallTypeItem.name,
                value: smallTypeItem.value,
              }"
            >
            </el-option>
          </el-select>
          
          <el-popover
            placement="top-start"
            v-if="
             dialogProps.action != 'view'
            "
            width="700"
            trigger="focus"
          >
            <el-table
              :data="recipeTemplateTable"
              :height="300"
              border
              highlight-current-row 
              @row-click="RowClickWesternTable"
            >
              <el-table-column
                prop="type.name"
                label="药品类型"
              ></el-table-column>
              <el-table-column
                prop="goodsName"
                label="药品名称"
              ></el-table-column>
              <el-table-column prop="gg" label="规格" width="120">
                <template slot-scope="scope">
                  {{ scope.row.dosis
                  }}{{ scope.row.dosisUnit.name }} *
                  {{ scope.row.preparation
                  }}{{ scope.row.preparationUnit.name }}/{{
                    scope.row.pack.name
                  }}
                </template>
              </el-table-column>
              <el-table-column
                prop="factory.name"
                label="厂家"
                width="100"
              ></el-table-column>
              <el-table-column label="包装价" width="80">
                <template slot-scope="scope">
                  {{ scope.row.price + "/" + scope.row.pack.name }}
                </template>
              </el-table-column>
              <el-table-column label="零售价" width="80">
                <template slot-scope="scope">
                  {{
                    scope.row.isUnpackSell == 1
                      ? scope.row.retailPrice +
                        "/" +
                        scope.row.preparationUnit.name
                      : "--"
                  }}
                </template>
              </el-table-column>
              <el-table-column
                prop="inventory"
                label="库存"
                width="100"
              >
                <template slot-scope="scope">
                  {{
                    Math.floor(
                      scope.row.inventory / scope.row.preparation
                    ) > 0
                      ? Math.floor(
                          scope.row.inventory /
                            scope.row.preparation
                        ) +
                        scope.row.pack.name +
                        (scope.row.inventory %
                          scope.row.preparation >
                        0
                          ? (scope.row.inventory %
                              scope.row.preparation) +
                            scope.row.preparationUnit.name
                          : "")
                      : scope.row.inventory +
                        scope.row.preparationUnit.name
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
              v-model="SearchWesternInput"
              @input="GetWesternTable"
              @focus="GetWesternTable"
              placeholder="输入药品名称或拼音码"
            ></el-input>
            
          </el-popover>  
        </el-row>
        <el-row>
          <el-table
            :data="
              getDataFilterTable(
                bizFormModel.recipeTemplateDetail,
                0,
                this.bizFormModel.costDebug
              )
            "
            border
            style="width: 100%"
            class="tableStyle"
            @cell-click="checkInventory"
          >
            <el-table-column
              type="index"
              label="序号"
              width="50"
              align="center"
            ></el-table-column>
            <el-table-column
              prop="drugStuffId"
              label="药品名称"
              align="center"
            >
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
            <el-table-column
              prop="singleDosage"
              label="单次用量"
              align="center"
              width="100"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.singleDosage"
                  oninput="value=value.replace(/[^\d.]/g,'')"
                  @input="MedicalCalculate()"
                  :disabled="
                    dialogProps.action == 'view'
                  "
                >
                  <template slot="append">{{
                    scope.row.drugStuffId.preparationUnit.name
                  }}</template>
                </el-input>
              </template>
            </el-table-column>
            <el-table-column
              prop="westernMedicineUse"
              label="用法"
              align="center"
              width="120"
            >
              <template slot-scope="scope">
                <el-select
                  v-model="scope.row.westernMedicineUse"
                  :disabled="
                    dialogProps.action == 'view'
                  "
                  placeholder=""
                  @change="changeUse(scope.row.westernMedicineUse)"
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
            <el-table-column
              prop="frequency"
              label="频次"
              align="center"
              width="120"
            >
              <template slot-scope="scope">
                <el-select
                  v-model="scope.row.frequency"
                  :disabled="
                     dialogProps.action == 'view'
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
            </el-table-column>
            <el-table-column
              prop="days"
              label="天数"
              align="center"
              width="100"
            >
              <template slot-scope="scope">
                <el-select
                  v-model="scope.row.days"
                  :disabled="
                     dialogProps.action == 'view'
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
            </el-table-column>
            <el-table-column
              prop="total"
              label="总量"
              align="center"
              width="100"
            >
              <template slot-scope="scope">
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
                          scope.row.drugStuffId.preparationUnit.name
                        : "")
                    : scope.row.total +
                      scope.row.drugStuffId.preparationUnit.name
                }}
              </template>
            </el-table-column>
            <el-table-column
              prop="isUnpackSell"
              label="单价"
              width="110"
              align="center"
            >
              <template slot-scope="scope">
                <el-select
                  v-model="scope.row.isUnpackSell"
                  :disabled="
                    dialogProps.action == 'view'
                  "
                  placeholder="请选择"
                  @change="MedicalCalculate()"
                  style="width: 80px"
                >
                  <el-option
                    :label="
                      scope.row.drugStuffId.price +
                      '/' +
                      scope.row.drugStuffId.pack.name
                    "
                    :value="0"
                  ></el-option>
                  <el-option
                    :label="
                      scope.row.drugStuffId.retailPrice +
                      '/' +
                      scope.row.drugStuffId.preparationUnit.name
                    "
                    :value="1"
                  ></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column
              prop="allFee"
              label="金额"
              width="100"
              align="center"
            ></el-table-column>
            <el-table-column
              v-if="
               dialogProps.action != 'view'
              "
              label="操作"
              fixed="right"
              align="center"
            >
              <template slot-scope="scope">
                <i
                  class="el-icon-circle-close"
                  @click="DeleteMedicalRow(scope.$index, scope.row)"
                ></i>
                <span style="display:none">{{changeData}}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-row>
      </div>
      <!-- 中药处方 -->
      <div v-else-if="templateType==1">
        <el-row>
            <el-divider content-position="left">处方信息</el-divider>
            <el-select
              :disabled="
                 dialogProps.action == 'view'
              "
              v-model="bizFormModel.recipeTemplateInfo.smallType"
              placeholder="请选择处方分类"
              style="width: 100px"
            >
              <el-option
                v-for="smallTypeItem in RecipelSmallTypeList"
                :key="smallTypeItem.value"
                :label="smallTypeItem.name"
                :value="{
                  name: smallTypeItem.name,
                  value: smallTypeItem.value,
                }"
              >
              </el-option>
            </el-select>

            <el-popover
              placement="top-start"
              v-if="
                dialogProps.action != 'view'
              "
              width="700"
              trigger="focus"
            >
              <el-table
                :data="ChineseMedicineTable"
                :height="300"
                border
                highlight-current-row
                @row-click="RowClickChineseTable"
              >
                <el-table-column
                  prop="type.name"
                  label="药品类型"
                ></el-table-column>
                <el-table-column
                  prop="goodsName"
                  label="药品名称"
                ></el-table-column>
                <el-table-column prop="gg" label="规格" width="120">
                  <template slot-scope="scope">
                    {{ scope.row.dosis
                    }}{{ scope.row.dosisUnit.name }} *
                    {{ scope.row.preparation
                    }}{{ scope.row.preparationUnit.name }}/{{
                      scope.row.pack.name
                    }}
                  </template>
                </el-table-column>
                <el-table-column
                  prop="factory.name"
                  label="厂家"
                  width="100"
                ></el-table-column>
                <el-table-column label="包装价" width="80">
                  <template slot-scope="scope">
                    {{ scope.row.price + "/" + scope.row.pack.name }}
                  </template>
                </el-table-column>
                <!--                                                     
                                          <el-table-column label="零售价" width="80">
                                              <template slot-scope="scope">
                                                  {{scope.row.isUnpackSell === "1" ? (scope.row.retailPrice + "/" + scope.row.preparationUnit.name) : "--"}}
                                              </template>
                                          </el-table-column> 
                                          -->
                <el-table-column
                  prop="inventory"
                  label="库存"
                  width="100"
                >
                  <template slot-scope="scope">
                    {{
                      Math.floor(
                        scope.row.inventory / scope.row.preparation
                      ) > 0
                        ? Math.floor(
                            scope.row.inventory /
                              scope.row.preparation
                          ) +
                          scope.row.pack.name +
                          (scope.row.inventory %
                            scope.row.preparation >
                          0
                            ? (scope.row.inventory %
                                scope.row.preparation) +
                              scope.row.preparationUnit.name
                            : "")
                        : scope.row.inventory +
                          scope.row.preparationUnit.name
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
                v-model="SearchChineseInput"
                @input="GetChineseTable"
                @focus="GetChineseTable"
                placeholder="输入药品名称或拼音码"
              ></el-input>
              
            </el-popover>
          </el-row>
          <el-row :gutter="24" style="min-height: 100px">
            <el-col
              :span="6"
              v-for="(citem, index) in getDataFilterTable(
                bizFormModel.recipeTemplateDetail,
                0,
                this.bizFormModel.costDebug
              )"
              :key="citem"
              :offset="0"
            >
              <el-card
                :index="index"
                class="box-card chinese-medicine-card"
                style="width: 240px; margin-bottom: 10px"
              >
                <div
                  slot="header"
                  class="clearfix"
                  style="padding: 5px 0px; font-size: 14px"
                >
                  <span>{{ citem.drugStuffId.name }}</span>
                  <el-button
                    v-if="
                      dialogProps.action != 'view'
                    "
                    :disabled="
                      dialogProps.action == 'view'
                    "
                    @click="DeleteMedicalRow(index, citem,item)"
                    icon="el-icon-close"
                    style="float: right; padding: 3px 0"
                    type="text"
                  ></el-button>
                </div>
                <div>
                  <el-input
                    :disabled="
                       dialogProps.action == 'view'
                    "
                    placeholder="数量"
                    size="mini"
                    oninput="value=value.replace(/[^\d.]/g,'')"
                    @input="changeSingleDosage(citem.singleDosage,index,item)"
                    v-model="citem.singleDosage"
                    style="width: 80px"
                    
                  >
                    <template slot="append">{{
                      citem.drugStuffId.pack.name
                    }}</template>
                  </el-input>
                  <el-select
                    :disabled="
                       dialogProps.action == 'view'
                    "
                    size="mini"
                    v-model="citem.chineseMedicineUse"
                    placeholder=""
                    style="width: 105px"
                  >
                    <el-option
                      v-for="oitem in ChineseUseTimeOption"
                      :key="oitem.value"
                      :label="oitem.name"
                      :value="{
                        name: oitem.name,
                        value: oitem.value,
                      }"
                    >
                    </el-option>
                  </el-select>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-row>
            <el-input
              :disabled="
                dialogProps.action == 'view'
              "
              v-model="bizFormModel.recipeTemplateInfo.dosage"
              oninput="value=value.replace(/[^\d.]/g,'')"
              @input="MedicalCalculate()"
              style="width: 60px"
            ></el-input
            >
            
            &nbsp;剂 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;用法：
            <el-select
              :disabled="
                 dialogProps.action == 'view'
              "
              v-model="bizFormModel.recipeTemplateInfo.recipelUse"
              placeholder="请选择"
              style="width: 110px"
            >
              <el-option
                v-for="pitem in ChineseUseOption"
                :key="pitem.value"
                :label="pitem.name"
                :value="{ name: pitem.name, value: pitem.value }"
              >
              </el-option>
            </el-select>
            <el-select
              :disabled="
                 dialogProps.action == 'view'
              "
              v-model="bizFormModel.recipeTemplateInfo.frequency"
              placeholder="请选择"
              style="width: 110px"
            >
              <el-option
                v-for="pitem in ChineseTimeOption"
                :key="pitem.value"
                :label="pitem.name"
                :value="{ name: pitem.name, value: pitem.value }"
              >
              </el-option>
            </el-select>
            <el-select
              :disabled="
                 dialogProps.action == 'view'
              "
              v-model="bizFormModel.recipeTemplateInfo.takeFrequency"
              placeholder="请选择"
              style="width: 110px"
            >
              <el-option
                v-for="pitem in JSON.parse(
                  JSON.stringify(FrequencyOption)
                )"
                :key="pitem.value"
                :label="pitem.name"
                :value="{ name: pitem.name, value: pitem.value }"
              >
              </el-option>
            </el-select>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 一次&nbsp;<el-input
              :disabled="
                 dialogProps.action == 'view'
              "
              v-model="bizFormModel.recipeTemplateInfo.singleDosage"
              style="width: 60px"
            ></el-input
            >&nbsp;<span v-if="isSpecial">格</span><span v-else>格</span>
            <span style="float: right"
              >中药金额:{{
                bizFormModel.recipeTemplateInfo.medicalAmount
                  ? bizFormModel.recipeTemplateInfo.medicalAmount
                  : 0
              }}元</span
            >
          </el-row>
      </div>
      <!-- 输液处方 -->
      <div v-else-if="templateType==2">
           <div >             
            <el-row>
              <el-divider content-position="left">处方信息</el-divider>
              
              <el-select
                :disabled="
                  dialogProps.action == 'view'
                "
                v-model="bizFormModel.recipeTemplateInfo.smallType"
                placeholder="请选择处方分类"
                style="width: 100px"
              >
                <el-option
                  v-for="smallTypeItem in RecipelSmallTypeList"
                  :key="smallTypeItem.value"
                  :label="smallTypeItem.name"
                  :value="{
                    name: smallTypeItem.name,
                    value: smallTypeItem.value,
                  }"
                >
                </el-option>
              </el-select>
                
            
              <el-button 
                  v-if="
                    dialogProps.action != 'view'
                  " @click="addGroup(bizFormModel)" type="primary" plain>加一组</el-button>
            </el-row>

            </div>
          <div  style="margin-left:-10px;">
              <div v-for="(items,index) in bizFormModel.infusion.zushu" :key="index" style="border:1px solid #DCDCDC;padding:10px;margin:10px;">
              
              <el-row>

                <div>
                      <el-popover
                placement="top-start"
                v-if="
                  dialogProps.action != 'view'
                "
                width="700"
                trigger="focus"
              >
                <el-table
                  :data="InfusionTable"
                  :height="300"
                  border
                  highlight-current-row
                  @row-click="(row,column,e)=>RowClickInfusionTable(row,column,e,index)"
                >
                  <el-table-column
                    prop="type.name"
                    label="药品类型"
                  ></el-table-column>
                  <el-table-column
                    prop="goodsName"
                    label="药品名称"
                  ></el-table-column>
                  <el-table-column prop="gg" label="规格" width="120">
                    <template slot-scope="scope">
                      {{ scope.row.dosis
                      }}{{ scope.row.dosisUnit.name }} *
                      {{ scope.row.preparation
                      }}{{ scope.row.preparationUnit.name }}/{{
                        scope.row.pack.name
                      }}
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="factory.name"
                    label="厂家"
                    width="100"
                  ></el-table-column>
                  <el-table-column label="包装价" width="80">
                    <template slot-scope="scope">
                      {{ scope.row.price + "/" + scope.row.pack.name }}
                    </template>
                  </el-table-column>
                  <el-table-column label="零售价" width="80">
                    <template slot-scope="scope">
                      {{
                        scope.row.isUnpackSell == 1
                          ? scope.row.retailPrice +
                            "/" +
                            scope.row.preparationUnit.name
                          : "--"
                      }}
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="inventory"
                    label="库存"
                    width="100"
                  >
                    <template slot-scope="scope">
                      {{
                        Math.floor(
                          scope.row.inventory / scope.row.preparation
                        ) > 0
                          ? Math.floor(
                              scope.row.inventory /
                                scope.row.preparation
                            ) +
                            scope.row.pack.name +
                            (scope.row.inventory %
                              scope.row.preparation >
                            0
                              ? (scope.row.inventory %
                                  scope.row.preparation) +
                                scope.row.preparationUnit.name
                              : "")
                          : scope.row.inventory +
                            scope.row.preparationUnit.name
                      }}
                    </template>
                  </el-table-column>
                </el-table>
                <el-input
                  prefix-icon="el-icon-plus"
                  suffix-icon="el-icon-search"
                  style="width: 20%"
                  slot="reference"
                  ref="WesternInput"
                  v-model="SearchInfusion[index]"
                  @input="GetInfusionTable(index)"
                  @focus="GetInfusionTable(index)"
                  placeholder="输入药品名称或拼音码"
                ></el-input>
                
              </el-popover>
                  <span  >&nbsp;&nbsp;第{{index+1}}组</span>
                    <span  style="float:right;height:25px;line-height:25px;font-size:14px;">&nbsp;&nbsp;</span>
                  <el-button  style="float:right;" type="primary" plain icon="el-icon-delete" circle
                  v-if="
                    dialogProps.action != 'view'
                  " @click="minusGroup(bizFormModel,index)"
                  ></el-button>
                  <span  style="float:right;height:25px;line-height:25px;font-size:14px;">&nbsp;&nbsp;</span>
                    <span  style="float:right;height:25px;line-height:25px;font-size:14px;">&nbsp;&nbsp;滴/分钟</span>
                      
                      <el-input
                      style="float:right;width:10%"
                      v-model="bizFormModel.infusion.drippingSpeed[index]"
                      placeholder="请选择滴速"
                      :disabled="
                        dialogProps.action == 'view'
                      "
                    >
                      
                    </el-input>
                    
                    <span  style="float:right;height:25px;line-height:25px;font-size:14px;">&nbsp;&nbsp;天&nbsp;&nbsp;</span>
                    
                      <el-select
                        style="float:right;width:10%"
                      v-model="bizFormModel.infusion.days[index]"
                      :disabled="
                        dialogProps.action == 'view'
                      "
                      @change="MedicalCalculate()"
                      placeholder="请选择天数"
                    >
                      <el-option
                        v-for="item in DayNumOption"
                        :key="item.value"
                        :label="item.name"
                        :value="{ name: item.name, value: item.value }"
                      >
                      </el-option>
                    </el-select>
                    
                    <span  style="float:right;height:25px;line-height:25px;font-size:18px;">&nbsp;&nbsp;</span>
                      
                      <el-select
                        style="float:right;width:10%;"
                      v-model="bizFormModel.infusion.frequency[index]"
                      :disabled="
                        dialogProps.action == 'view'
                      "
                      @change="MedicalCalculate()"
                      placeholder="请选择频次"
                    >
                      <el-option
                        v-for="item in FrequencyOption"
                        :key="item.value"
                        :label="item.name"
                        :value="{ name: item.name, value: item.value }"
                      >
                      </el-option>
                    </el-select>
                  
                    <span  style="float:right;height:25px;line-height:25px;font-size:18px;">&nbsp;&nbsp;</span>
                    
                  <el-select
                      style="float:right;width:10%"
                      v-model="bizFormModel.infusion.infuseUse[index]"
                      :disabled="
                        dialogProps.action == 'view'
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
                    </el-select>
                                                                                              
            
                </div>
              <div style="border:7px solid #0000"></div>
              <el-table
                :data="
                  getDataFilterTable(
                    bizFormModel.infusion.infusionProject[index],
                    0,
                    bizFormModel.costDebug
                  )
                "
                border
                style="width: 100%"
                class="tableStyle"
                @cell-click="checkInventory"
              >   
                    <el-table-column
                  type="index"
                  label="序号"
                  width="50"
                  align="center"
                ></el-table-column>
               
                <el-table-column
                  prop="drugStuffId"
                  label="药品名称"
                  align="center"
                >
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
                  <el-table-column
                  prop="skinTest"
                  label="皮试"
                  align="center"
                  width="120"
                >
                  <template slot-scope="scope">
                    <el-select
                      v-model="scope.row.skinTest"
                      :disabled="
                        dialogProps.action == 'view'
                      "
                      placeholder=""
                    >
                      <el-option
                        v-for="item in InfusionOption"
                        :key="item.value"
                        :label="item.name"
                        :value="{ name: item.name, value: item.value }"
                      >
                      </el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="singleDosage"
                  label="单次用量"
                  align="center"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.singleDosage"
                      oninput="value=value.replace(/[^\d.]/g,'')"
                      @input="MedicalCalculate()"
                      :disabled="
                        dialogProps.action == 'view'
                      "
                    >
                      <template slot="append">{{
                        scope.row.drugStuffId.preparationUnit.name
                      }}</template>
                    </el-input>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="total"
                  label="总量"
                  align="center"
                  width="100"
                >
                  <template slot-scope="scope">
                    
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
                              scope.row.drugStuffId.preparationUnit.name
                            : "")
                        : scope.row.total +
                          scope.row.drugStuffId.preparationUnit.name
                    }}
                  </template>
                </el-table-column>
                <el-table-column
                  prop="isUnpackSell"
                  label="单价"
                  width="110"
                  align="center"
                >
                  <template slot-scope="scope">
                    <el-select
                      v-model="scope.row.isUnpackSell"
                      :disabled="
                        dialogProps.action == 'view'
                      "
                      placeholder="请选择"
                      @change="MedicalCalculate()"
                      style="width: 80px"
                    >
                      <el-option
                        :label="
                          scope.row.drugStuffId.price +
                          '/' +
                          scope.row.drugStuffId.pack.name
                        "
                        :value="0"
                      ></el-option>
                      <el-option
                        :label="
                          scope.row.drugStuffId.retailPrice +
                          '/' +
                          scope.row.drugStuffId.preparationUnit.name
                        "
                        :value="1"
                      ></el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="allFee"
                  label="金额"
                  width="100"
                  align="center"
                ></el-table-column>
                <el-table-column
                  v-if="
                    dialogProps.action != 'view'
                  "
                  label="操作"
                  fixed="right"
                  align="center"
                  width="100"
                >
                  <template slot-scope="scope">
                    <i
                      class="el-icon-circle-close"
                      @click="DeleteMedicalRow(scope.$index, scope.row)"
                    ></i>
                  </template>
                </el-table-column>
              </el-table>
            </el-row> 
            
            </div>
            
          </div>
              
      </div>

      <div  v-else-if="templateType==3">
          <el-row>
            <el-divider content-position="left">处方信息</el-divider>
            <el-popover
              placement="top-start"
              v-if="
                 dialogProps.action != 'view'
              "
              width="540"
              trigger="focus"
            >
              <el-table
                :data="TreatmentTable"
                :height="300"
                border
                highlight-current-row
                @row-click="RowClickTreatmentTable"
              >
                <el-table-column
                  prop="itemName"
                  label="项目名称"
                  width="200"
                ></el-table-column>
                <el-table-column
                  prop="itemType.name"
                  label="项目类型"
                  width="100"
                ></el-table-column>
                <el-table-column
                  prop="isPackage"
                  label="套餐项目"
                  width="100"
                >
                  <template slot-scope="scope">
                    {{ scope.row.isPackage === "1" ? "是" : "否" }}
                  </template>
                </el-table-column>
                <el-table-column
                  prop="salePrice"
                  label="单价"
                  width="100"
                >
                  <template slot-scope="scope">
                    {{ scope.row.salePrice }}/{{
                      scope.row.unit.name
                    }}
                  </template>
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
                @focus="GetCostItemTable"
                placeholder="输入诊疗项目名称或拼音码"
              ></el-input>
            </el-popover>
          </el-row>
          <el-row>
            <el-table
              :data="
                getDataFilterTable(
                  bizFormModel.recipeTemplateDetail,
                  0,
                  bizFormModel.costDebug
                )
              "
              border
              style="width: 100%"
              class="tableStyle"
            >
              <el-table-column
                type="index"
                label="序号"
                width="50"
                align="center"
              ></el-table-column>
              <el-table-column
                prop="drugStuffId.name"
                label="项目名称"
                align="center"
              ></el-table-column>
              <el-table-column
                prop="drugStuffId.costItem.itemType.name"
                label="项目类别"
                align="center"
                width="100"
              ></el-table-column>
              <el-table-column
                prop="drugStuffId.costItem.isPackage"
                label="套餐项目"
                align="center"
                width="100"
              >
                <template slot-scope="scope">
                  {{
                    scope.row.drugStuffId.costItem.isPackage === "1"
                      ? "是"
                      : "否"
                  }}
                </template>
              </el-table-column>
              <el-table-column
                prop="total"
                label="数量"
                align="center"
                width="120"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.total"
                    oninput="value=value.replace(/[^\d.]/g,'')"
                    @input="MedicalCalculate()"
                    :disabled="
                       dialogProps.action == 'view'
                    "
                  >
                    <template slot="append">{{
                      scope.row.drugStuffId.pack.name
                    }}</template>
                  </el-input>
                </template>
              </el-table-column>
              <el-table-column
                label="单价"
                width="110"
                align="center"
              >
                <template slot-scope="scope">
                  {{ scope.row.drugStuffId.price }}/{{
                    scope.row.drugStuffId.pack.name
                  }}
                </template>
              </el-table-column>
              <el-table-column
                prop="allFee"
                label="合计金额"
                width="120"
                align="center"
              ></el-table-column>
              <el-table-column
                v-if="
                  dialogProps.action != 'view'
                "
                label="操作"
                fixed="right"
                align="center"
                width="80"
              >
                <template slot-scope="scope">
                  <i
                    class="el-icon-circle-close"
                    @click="DeleteMedicalRow(scope.$index, scope.row)"
                  ></i>
                </template>
              </el-table-column>
            </el-table>
          </el-row>
      </div>

      <div style="margin-top: 10px">
                    <el-row>
                      <el-divider content-position="left">附加费</el-divider>
                      <el-popover
                        placement="top-start"
                        width="550"
                        trigger="click"
                        v-if="
                         dialogProps.action != 'view'
                        "
                      >
                        <el-table
                          :data="SurchargeTable"
                          :max-height="300"
                          border
                          highlight-current-row
                          @row-click="RowClickSurchargeTable"
                        >
                          <el-table-column label="费用名称">
                            <template slot-scope="scope">
                              {{
                                scope.row.stuffType === "4"
                                  ? scope.row.name
                                  : scope.row.itemName
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column label="费用类型" width="130">
                            <template slot-scope="scope">
                              {{
                                scope.row.stuffType === "4"
                                  ? scope.row.type.name
                                  : "诊疗（其它费用）"
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column label="销售价" width="100">
                            <template slot-scope="scope">
                              {{
                                scope.row.stuffType === "4"
                                  ? scope.row.priceOutSell +
                                    "/" +
                                    scope.row.packUnit.name
                                  : scope.row.salePrice +
                                    "/" +
                                    scope.row.unit.name
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column label="零售价" width="100">
                            <template slot-scope="scope">
                              {{
                                scope.row.stuffType === "4" &&
                                scope.row.isUnpackSell == "1"
                                  ? scope.row.retailPrice +
                                    "/" +
                                    scope.row.minUnit.name
                                  : "--"
                              }}
                            </template>
                          </el-table-column>
                          <el-table-column label="库存" width="100">
                            <template slot-scope="scope">
                              {{
                                scope.row.stuffType === "4"
                                  ? Math.floor(
                                      scope.row.inventory / scope.row.packNumber
                                    ) > 0
                                    ? Math.floor(
                                        scope.row.inventory /
                                          scope.row.packNumber
                                      ) +
                                      scope.row.packUnit.name +
                                      (scope.row.inventory %
                                        scope.row.packNumber >
                                      0
                                        ? (scope.row.inventory %
                                            scope.row.packNumber) +
                                          scope.row.minUnit.name
                                        : "")
                                    : scope.row.inventory +
                                      scope.row.minUnit.name
                                  : "不限制"
                              }}
                            </template>
                          </el-table-column>
                        </el-table>
                        <el-input
                          prefix-icon="el-icon-plus"
                          suffix-icon="el-icon-search"
                          style="width: 30%"
                          slot="reference"
                          v-model="SearchSurchargeInput"
                          @input="GetSurchargeTable"
                          @focus="GetSurchargeTable"
                          placeholder="输入附加费名称或拼音码"
                        >
                        </el-input>
                      </el-popover>
                    </el-row>
                  <div  v-if="templateType==2">
                      <el-row>
                      <el-table
                        :data="
                          getDataFilterTable(
                           bizFormModel.infusion.excharge,
                            1,
                            this.bizFormModel.costDebug
                          )
                        "
                        border
                        style="width: 100%"
                        class="tableStyle"
                      >
                        <el-table-column
                          type="index"
                          label="序号"
                          width="50"
                          align="center"
                        ></el-table-column>
                        <el-table-column
                          prop="drugStuffId.name"
                          label="费用名称"
                          width="200"
                          align="center"
                        ></el-table-column>
                        <el-table-column
                          label="费用类型"
                          width="130"
                          align="center"
                        >
                          <template slot-scope="scope">
                            {{
                              scope.row.stuffType === "4"
                                ? scope.row.drugStuffId.stuff.type.name
                                : "诊疗（其它费用）"
                            }}
                          </template>
                        </el-table-column>
                        <el-table-column
                          prop="price"
                          label="单价"
                          width="100"
                          align="center"
                        >
                          <template slot-scope="scope">
                            <span v-if="scope.row.stuffType === '3'">
                              {{
                                scope.row.drugStuffId.price +
                                "/" +
                                scope.row.drugStuffId.pack.name
                              }}
                            </span>
                            <span v-if="scope.row.stuffType === '4'">
                              {{
                                scope.row.isUnpackSell == 0
                                  ? scope.row.drugStuffId.price +
                                    "/" +
                                    scope.row.drugStuffId.pack.name
                                  : scope.row.drugStuffId.retailPrice +
                                    "/" +
                                    scope.row.drugStuffId.preparationUnit.name
                              }}
                            </span>
                          </template>
                        </el-table-column>
                        <el-table-column
                          label="数量"
                          align="center"
                          width="200"
                        >
                          <template slot-scope="scope">
                            <el-input
                              :disabled="
                                dialogProps.action == 'view'
                              "
                              v-model="scope.row.singleDosage"
                              @input="MedicalCalculate()"
                              style="width: 60px"
                            ></el-input>
                            <el-select
                              :disabled="
                                dialogProps.action == 'view'
                              "
                              v-model="scope.row.isUnpackSell"
                              @change="MedicalCalculate()"
                              placeholder="请选择"
                              style="width: 80px"
                            >
                              <el-option
                                :label="scope.row.drugStuffId.pack.name"
                                :value="0"
                              ></el-option>
                              <el-option
                                v-if="scope.row.drugStuffId.isUnpackSell == 1"
                                :label="
                                  scope.row.drugStuffId.preparationUnit.name
                                "
                                :value="1"
                              ></el-option>
                            </el-select>
                          </template>
                        </el-table-column>
                        <el-table-column
                          prop="allFee"
                          label="金额"
                          align="center"
                        ></el-table-column>
                        <el-table-column
                          v-if="
                            dialogProps.action == 'view'
                          "
                          label="操作"
                          fixed="right"
                          width="100"
                          align="center"
                        >
                          <template slot-scope="scope">
                            <i
                              class="el-icon-circle-close"
                              @click="DeleteExMedicalRow(scope.$index, scope.row)"
                            ></i>
                          </template>
                        </el-table-column>
                      </el-table>
                    </el-row>
                    </div>
                    <div  v-else>
                      <el-row>
                      <el-table
                        :data="
                          getDataFilterTable(
                           bizFormModel.recipeTemplateDetail,
                            1,
                            this.bizFormModel.costDebug
                          )
                        "
                        border
                        style="width: 100%"
                        class="tableStyle"
                      >
                        <el-table-column
                          type="index"
                          label="序号"
                          width="50"
                          align="center"
                        ></el-table-column>
                        <el-table-column
                          prop="drugStuffId.name"
                          label="费用名称"
                          width="200"
                          align="center"
                        ></el-table-column>
                        <el-table-column
                          label="费用类型"
                          width="130"
                          align="center"
                        >
                          <template slot-scope="scope">
                            {{
                              scope.row.stuffType === "4"
                                ? scope.row.drugStuffId.stuff.type.name
                                : "诊疗（其它费用）"
                            }}
                          </template>
                        </el-table-column>
                        <el-table-column
                          prop="price"
                          label="单价"
                          width="100"
                          align="center"
                        >
                          <template slot-scope="scope">
                            <span v-if="scope.row.stuffType === '3'">
                              {{
                                scope.row.drugStuffId.price +
                                "/" +
                                scope.row.drugStuffId.pack.name
                              }}
                            </span>
                            <span v-if="scope.row.stuffType === '4'">
                              {{
                                scope.row.isUnpackSell == 0
                                  ? scope.row.drugStuffId.price +
                                    "/" +
                                    scope.row.drugStuffId.pack.name
                                  : scope.row.drugStuffId.retailPrice +
                                    "/" +
                                    scope.row.drugStuffId.preparationUnit.name
                              }}
                            </span>
                          </template>
                        </el-table-column>
                        <el-table-column
                          label="数量"
                          align="center"
                          width="200"
                        >
                          <template slot-scope="scope">
                            <el-input
                              :disabled="
                                 dialogProps.action == 'view'
                              "
                              v-model="scope.row.singleDosage"
                              @input="MedicalCalculate()"
                              style="width: 60px"
                            ></el-input>
                            <el-select
                              :disabled="
                                dialogProps.action == 'view'
                              "
                              v-model="scope.row.isUnpackSell"
                              @change="MedicalCalculate()"
                              placeholder="请选择"
                              style="width: 80px"
                            >
                              <el-option
                                :label="scope.row.drugStuffId.pack.name"
                                :value="0"
                              ></el-option>
                              <el-option
                                v-if="scope.row.drugStuffId.isUnpackSell == 1"
                                :label="
                                  scope.row.drugStuffId.preparationUnit.name
                                "
                                :value="1"
                              ></el-option>
                            </el-select>
                          </template>
                        </el-table-column>
                        <el-table-column
                          prop="allFee"
                          label="金额"
                          align="center"
                        ></el-table-column>
                        <el-table-column
                          v-if="
                            dialogProps.action != 'view'
                          "
                          label="操作"
                          fixed="right"
                          width="100"
                          align="center"
                        >
                          <template slot-scope="scope">
                            <i
                              class="el-icon-circle-close"
                              @click="DeleteExMedicalRow(scope.$index, scope.row)"
                            ></i>
                          </template>
                        </el-table-column>
                      </el-table>
                    </el-row>
                    </div>
                    <el-row style="margin-top:10px">
                      <el-divider content-position="left">医嘱事项</el-divider>
                      <el-input
                        type="textarea"
                        :rows="4"
                        placeholder="请输入医嘱事项"
                        v-model="bizFormModel.recipeTemplateInfo.entrust"
                        :disabled="
                           dialogProps.action == 'view'
                        "
                      >
                      </el-input>
                    </el-row>
                  </div>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' type='primary' :plain='true' @click='onSubmit("recipetemplateForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listCompanyAll } from '@/api/org/company'
import { listDictItemAll } from '@/api/sys/dictItem'
import { saveRecipetemplate,allSave } from '@/api/outpatient/recipetemplate'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
import { listAllStock,listAllStock2 } from "@/api/stock/drug";
import { listCostItemAll } from "@/api/treatment/costItem";
import { listStuffAll } from "@/api/stock/stuff";
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
} from "@/api/outpatient/medicalRecord";
import {
  listRecipelInfoAll,
  invalidStatus,
} from "@/api/outpatient/recipelInfo";

import {
 getByRecipelInfoId,
} from "@/api/outpatient/recipelDetail";
import { getPatientById } from "@/api/outpatient/patient";
import NP from "number-precision";
import { BigNumber } from "bignumber.js";
import moment from "moment";
export default {
  extends: BaseUI,
  name: 'recipetemplate-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      clinic:{
        code: "ZSCE001",
        fullName: "惠兰诊所",
        id: "998324809623052310",
        ids: "0.998324809623052303.998324809623052308.",
        label: "惠兰诊所",
        name: "惠兰诊所"
      },
      templateType:0,//模板类型
      tabIndex: '1',
          company_List: [],  // 诊所id
          type_List: [],  // 模板类型
       dialogProps: {
        visible: false,
        action: '',
        title: ''
      },
      formRules: {
        'diagnosis': [
            { required: true, message: '请输入诊断', trigger: 'blur' }
        ],
        'recipetemplateName': [
            { required: true, message: '请输入模板名称', trigger: 'blur' }
        ],
       
      }
    }    
  },
    //搜索药品条件
      SearchWesternModel: {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "",
        params:[
           {
          columnName: "company_id",
          queryType: "=",
          value: "",
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
        ]
      },
      
      SearchChineseModel: {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "",
        params:[
           {
          columnName: "company_id",
          queryType: "=",
          value: "",
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
        ]
      },
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
      //西药处方表格主数据
      recipeTemplateTable: [
      ],
      InfusionTable:[],
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
      SearchInfusion:[""],
      changeData:{},
      payAmount:0,
  props: {
    // 权限
    permission: {
      type: Object
    }
  },  
  methods: {
    //开具处方新增Tab标签页
    addMedicalEditTab(tabTypeData) {
      let tabType = tabTypeData.value;
      // let typeMaxTabIndex = 0;
      // this.medicalEditTabs.forEach((element) => {
      //   if (element.type === tabType) {
      //     typeMaxTabIndex++;
      //   }
      // });

      // let isFollowUp =
      //   this.MedicalRecordModel.registration.treatType.value === "treatType_0"
      //     ? 0
      //     : 1;
      let recipelInfoEvt = {};
      if (tabType == "recipelType_1") {
        recipelInfoEvt = {
          uuid: require("uuid").v1(),
          recipelInfo: {
            company: this.clinic,
            fee: 0,
            recipelUse: this.isSpecial
              ? {
                  name: this.ChineseUseOption[0].name,
                  value: this.ChineseUseOption[0].value,
                }
              : {
                  name: "",
                  value: "",
                },
            smallType:this.RecipelSmallTypeList[0]
          },
          recipelDetailEvtList: [],
        };
      } else {
        recipelInfoEvt = {
          uuid: require("uuid").v1(),
          recipelInfo: {
            company: this.clinic,
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
            smallType:this.RecipelSmallTypeList[0]
          },
          recipelDetailEvtList: [],
        };
      }

      // if (tabTypeData.type === "recipelType_1") {
      //   recipelInfoEvt.recipelInfo["dosage"] = 1;
      // }
      let infusion={}
      if (tabTypeData.type === "recipelType_2") {
        infusion={
            // infusionProject:this.infusionProject,
            // drippingSpeed:this.drippingSpeed,  //滴速
            // days:this.days,    //天数
            // frequency:this.frequency,   //频次
            // infuseUse:this.infuseUse,     //用法
            // zushu:this.zushu,
            defaultNumber:1,//默认组号
            infusionProject:[[]],
            drippingSpeed:[""],  //滴速
            days:[{}],    //天数
            frequency:[{}],   //频次
            infuseUse:[{}],     //用法
            zushu:[1],
            excharge:[]
        }
      }
      let costDebug={
        chineseTest:[]
      }

      this.recipelInfoEvtList.push(recipelInfoEvt);

      let tab = {
        title: tabTypeData.name + (typeMaxTabIndex + 1),
        key: this.medicalEditTabs.length + 1,
        content: recipelInfoEvt,
        closable: true,
        type: tabType,
        infusion:infusion,
        costDebug:costDebug,
      };
      this.SearchWesternInput = "";
      this.SearchChineseInput = "";
      this.SearchCostItemInput = "";
      this.SearchSurchargeInput = "";
      this.SearchInfusion=[""]
      this.medicalEditTabs.push(tab);
      this.medicalEditTabsValue = tab;
      this.medicalClickTabsValue = this.medicalEditTabsValue;
      console.log(this.medicalEditTabsValue,'新增');
      console.log(require("uuid").v1());
    },
    //获取字典数据值
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
    },
    GetOption(optionId) {
      console.log(optionId);
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
        if (optionId == "1014474470772899981"){
          this.ChineseUseOption = responseData.data;
          // if (this.isSpecial) {
          //   this.ChineseUseOption = this.ChineseUseOption.filter(
          //     (item) => item.name == "水冲"
          //   );
          // } 
        }else if (optionId == "1014474470772899990")
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
        }
      });
    },

    onSubmit(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave()
        } else {
          return false
        }
      });
    },
    doSave() {
      console.log(this.bizFormModel,'保存的结果');
      this.bizFormModel.company=this.clinic
      this.bizFormModel.createId=currentUser.id
     // this.bizFormModel.recipeTemplateDetail=JSON.stringify(this.bizFormModel.recipeTemplateDetail)
      //this.bizFormModel.recipeTemplateInfo=JSON.stringify(this.bizFormModel.recipeTemplateInfo)
      // let totalCheck=false
      // let paramsCheck=false
      // //判断西药处方
      // if (
      //     this.bizFormModel.type.value == "recipelType_0"
      //   ) {
      //     let total = 0
      //     this.bizFormModel.recipeTemplateDetail.forEach(item=>{
      //       if(item.isExtra==0){
      //         if(item.singleDosage&&item.frequency&&item.frequency.value&&item.days&&item.days.name){
      //         total = Math.ceil(
      //             BigNumber(item.singleDosage)
      //               .multipliedBy(item.frequency.value.split("_")[1])
      //               .multipliedBy(item.days.name)
      //               .toNumber()
      //         );
      //         if(item.drugStuffId.drug.total!=0&&total>item.drugStuffId.drug.total){
      //           this.$message.error(item.drugStuffId.name+'总量上限小于功效说明计算总量')
      //           totalCheck = true
      //         }
      //       }else{
      //         this.$message.error(tabElement.title+'信息未填写完整')
      //         paramsCheck = true
      //       }
      //       }
          
      //     })
           
      //   }
      //   if(paramsCheck){
      //     return
      //   }
      //   if(totalCheck){
      //     return
      //   }

      this.setLoad()
      allSave(this.bizFormModel).then(responseData => {
        if(responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
        this.$message.error("后台数据异常，请查看数据是否有否或者联系管理员解决！")
      })
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改模板处方'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['recipetemplateForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': "",
          'name': null,
        },
        'recipetemplateName': '',   // 模板名称
        'type': {     // 模板类型
          'value': null,
          'name': null,
        },
        'category': '0',   // 模板类别
        'diagnosis': '',   // 诊断
        'remarks':'',
        'createId':"",
        recipeTemplateInfo:{},
        recipeTemplateDetail:[{}],
        costDebug:{
          chineseTest:[],
        },
        infusion:{
          defaultNumber:1,//默认组号
            infusionProject:[[]],
            drippingSpeed:[""],  //滴速
            days:[{}],    //天数
            frequency:[{}],   //频次
            infuseUse:[{}],     //用法
            zushu:[1],
            excharge:[]
        }
      }
    },
    initOptions(This) {
      let company_search = {
        params: []
      }
        // 字段对应表上filter条件
        company_search.params.push.apply(company_search.params, [])
      // 数据权限: 诊所org_company
      this.pushDataPermissions(company_search.params, this.$route.meta.routerId, '41040096140492800')
      this.company_List.splice(0, this.company_List.length)
      listCompanyAll(company_search).then(responseData => {
        this.company_List = responseData.data
        // 获取初始项的值
        this.company_List.forEach( item => {
          if(item.id == this.bizFormModel.company.id) {
            this.bizFormModel.company = item
            return
          }
        })
      })
      
      let type_search = {
        params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': '1014474470772899974'}]
      }
        // 字段对应表上filter条件
        type_search.params.push.apply(type_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(type_search.params, this.$route.meta.routerId, '4005')
      this.type_List.splice(0, this.type_List.length)
      listDictItemAll(type_search).then(responseData => {
        this.type_List = responseData.data
        this.type_List=this.type_List.filter((item)=>item.name!="零售处方")
        this.type_List=this.type_List.filter((item)=>item.name!='附加费')
      })
    },

    //获取诊疗项目
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
            value: this.clinic.id,
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
      if (pattern2.test(this.SearchCostItemInput)&&this.SearchCostItemInput!=undefined) {
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
          this.$forceUpdate()
        }
      });
    },

    RowClickTreatmentTable(row) {
      let recipelDetailEvtList =
        this.bizFormModel.recipeTemplateDetail;
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

      this.bizFormModel.recipeTemplateDetail.push(
        JSON.parse(JSON.stringify(recipelDetailEvt))
      );
    },
    GetCostItemFee(index, row) {
      row.allFee = row.drugStuffId.price * row.total;
    },

    //输液处方加一组
     addGroup(item){
      item.infusion.defaultNumber=item.infusion.defaultNumber+1
      item.infusion.zushu.push(item.infusion.defaultNumber)
      item.infusion.infusionProject.push([])
      item.infusion.drippingSpeed.push("")
      item.infusion.days.push({})
      item.infusion.frequency.push({})
      item.infusion.infuseUse.push({})
      this.SearchInfusion.push("")
      console.log(item,'添加');
    },

     minusGroup(item,index){
     if(item.infusion.defaultNumber>1){
       
      item.infusion.defaultNumber=item.infusion.defaultNumber-1
      item.infusion.zushu.splice(index,1)
      item.infusion.infusionProject.splice(index,1)
      for (let i = 0; i < item.infusion.infusionProject.length; i++) {
        for (let j = 0; j < item.infusion.infusionProject[i].length; j++) {
        item.infusion.infusionProject[i][j].infuseGroup=i+1
        }
      }
      item.infusion.drippingSpeed.splice(index,1)
      item.infusion.days.splice(index,1)
      item.infusion.frequency.splice(index,1)
      item.infusion.infuseUse.splice(index,1)
      this.SearchInfusion.splice(index,1)
     }
    },

    //输液处方药品查询
     GetInfusionTable(index) {

      let search={
         columnName: "",
        limit: 10,
        offset: 0,
        order: "",
        params:[
           {
          columnName: "company_id",
          queryType: "=",
          value: this.clinic.id,
        },
        {
          columnName: "type",
          queryType: "in",
          value: ["medicalType_0",
        "medicalType_2"],
        },
        {
          columnName: "goods_name",
          queryType: "like",
          value: "",
        },
        ]
      }
      //判断是否输入的是英文
       var pattern2 = new RegExp("[A-Za-z]+");
      if (pattern2.test(this.SearchInfusion[index])&&this.SearchInfusion[index]!=undefined) {
        // console.log(this.SearchWesternInput,'字符');
        search.params[2].value =
          this.SearchInfusion[index].toUpperCase();
        search.params[2].columnName = "pinyin_code";
      } else {
        search.params[2].value = this.SearchInfusion[index];
        search.params[2].columnName = "goods_name";
      }
      listAllStock2(search).then((responseData) => {
        if (responseData.code == 100) {
          this.InfusionTable = responseData.data;
          this.$forceUpdate()
        }
      });
    },

    //输液处方药品选择
     //输液表格行点击选择添加到已选择西药处方的表格
    RowClickInfusionTable(row, column, event,index) {
      // console.log(this.medicalClickTabsValue.content,'就像');
      // let content = this.medicalClickTabsValue.content;
      let recipelDetailEvtList =
        this.bizFormModel.infusion.infusionProject[index];
      let count = 0;
      let hasFlag = false;
      console.log(row);

      recipelDetailEvtList.forEach((element) => {
        if (element.isExtra === 0) {
          count++;
          if (row.id === element.drugStuffId.drugStuffId) {
            hasFlag = true;
          }
        }
      });
      if (count == 5) {
        this.$message.error("每个输液处方最多只能添加5个药品");
        return;
      }

      let drugStuff = {
        drugStuffId: row.id,
        name: row.goodsName,
        price: row.price,
        retailPrice: row.retailPrice,
        isUnpackSell: row.isUnpackSell,
        dosisUnit: row.dosisUnit,
        preparationUnit: row.preparationUnit,
        pack: row.pack,
        drug: row,
      };

      let recipelDetailEvt = {
        allFee:0,
        total:0,
        company: this.Company,
        stuffType:0,
        isExtra:0,
        isUnpackSell: BigNumber(row.isUnpackSell).toNumber(),
        singleDosage:0,
        skinTest:{},
        drugStuffId: drugStuff,
        infuseGroup:index+1,
        drippingSpeed:"",
        days:{},
        frequency:{},
        infuseUse:{}
      };

        this.bizFormModel.infusion.infusionProject[index].push(
        JSON.parse(JSON.stringify(recipelDetailEvt))
      );
      
    },

     GetMedicalRecordAll() {
      let params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: "",
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
    GetChineseTable() {
       let search={
         columnName: "",
        limit: 10,
        offset: 0,
        order: "",
        params:[
           {
          columnName: "company_id",
          queryType: "=",
          value: this.clinic.id,
        },
        {
          columnName: "type",
          queryType: "in",
          value: ["medicalType_1"],
        },
        {
          columnName: "goods_name",
          queryType: "like",
          value: "",
        },
        ]
      }
      var pattern2 = new RegExp("[A-Za-z]+");
      if (pattern2.test(this.SearchChineseInput)&&this.SearchChineseInput!=undefined) {
        // console.log(this.SearchWesternInput,'字符');
        search.params[2].value =
          this.SearchChineseInput.toUpperCase();
        search.params[2].columnName = "pinyin_code";
      } else {
        search.params[2].value = this.SearchChineseInput;
        search.params[2].columnName = "goods_name";
      }
      listAllStock2(search).then((responseData) => {
        if (responseData.code == 100) {
          this.ChineseMedicineTable = responseData.data;
          this.$forceUpdate()
        }
      });
    },
     RowClickChineseTable(row) {
      let recipelDetailEvtList =
        this.bizFormModel.recipeTemplateDetail;
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
        drugStuffId: row.id,
        name: row.goodsName,
        price: row.price,
        retailPrice: row.retailPrice,
        isUnpackSell: row.isUnpackSell,
        dosisUnit: row.dosisUnit,
        preparationUnit: row.preparationUnit,
        pack: row.pack,
        drug: row,
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

      this.bizFormModel.recipeTemplateDetail.push(
        JSON.parse(JSON.stringify(recipelDetailEvt))
      );
      setTimeout(() => {
        this.MedicalCalculate()
      }, 0);
      
    },
    changeSingleDosage(row,index,item){
     
      //  this.medicalEditTabs.forEach((tabElement) => {
      //    if (tabElement.type === "recipelType_1"&&tabElement.key==item.key) {
      //    //for (let i = 0; i < tabElement.content.recipelDetailEvtList.length; i++) {
      //       console.log(tabElement.content.recipelDetailEvtList[index],'就是看看');
      //      if(row==""){
      //        tabElement.content.recipelDetailEvtList[index].singleDosage=" "
      //      }else{
      //        tabElement.content.recipelDetailEvtList[index].singleDosage=row
      //      }
      //    //}
      //    }
      //  })
       if(row==""){
             this.bizFormModel.recipeTemplateDetail[index].singleDosage=" "
           }else{
             this.bizFormModel.recipeTemplateDetail[index].singleDosage=row-0
           }
       setTimeout(() => {
        this.MedicalCalculate()
      }, 0);
      console.log(this.bizFormModel.recipeTemplateDetail,'就是情况');
    },
    GetWesternTable() {
      
      // this.SearchWesternModel.params[1].value = [
      //   "medicalType_0",
      //   "medicalType_2",
      // ];
      let search={
         columnName: "",
        limit: 10,
        offset: 0,
        order: "",
        params:[
           {
          columnName: "company_id",
          queryType: "=",
          value: this.clinic.id,
        },
        {
          columnName: "type",
          queryType: "in",
          value: ["medicalType_0",
        "medicalType_2"],
        },
        {
          columnName: "goods_name",
          queryType: "like",
          value: "",
        },
        ]
      }
      //判断是否输入的是英文
      var pattern2 = new RegExp("[A-Za-z]+");
      if (pattern2.test(this.SearchWesternInput)&&this.SearchWesternInput!=undefined) {
        // console.log(this.SearchWesternInput,'字符');
        search.params[2].value =
          this.SearchWesternInput.toUpperCase();
        search.params[2].columnName = "pinyin_code";
      } else {
        search.params[2].value = this.SearchWesternInput;
        search.params[2].columnName = "goods_name";
      }
        let recipeTemplate={
          barCode: "",
          bitCode: "",
          brandName: "阿司匹林",
          chineseMedicineUse: {name: ''},
          code: "YP-00085",
          company: {id: '998324809623052308', name: '惠兰诊所', label: '惠兰诊所'},
          createBy: "诊所001管理员(17386700305)",
          createDate: "2022-08-10 14:50:53",
          days: {name: ''},
          dosis: "2",
          dosisUnit: {name: 'g', value: 'medicalDosisUnit_0'},
          factory: {id: '1083126482167300096', name: '安徽药材'},
          frequency: {name: ''},
          goodsName: "阿司匹林胶囊",
          id: "1111009959611269241",
          insuranceCode: "",
          inventory: 3765,
          isUnpackSell: "1",
          nature: {name: '进口', value: 'medicalNature_1'},
          pack: {name: '盒', value: 'medicalPackUnit_2'},
          pinyinCode: "ASPLJN",
          preparation: "24",
          preparationUnit: {name: '粒', value: 'medicalPreparationUnit_1'},
          price: 25,
          remarks: "",
          retailPrice: 1,
          source: "德国",
          standardCode: "2222",
          status: "1",
          stockNumber: "90360",
          total: 0,
          type: {name: '西药', value: 'medicalType_0'},
          updateBy: "诊所001管理员(17386700305)",
          updateDate: "2022-10-10 10:14:27",
          westernMedicineUse: {name: ''},
        }
        
        listAllStock2(search).then((responseData) => {
        if (responseData.code == 100) {
          this.recipeTemplateTable=[]
          responseData.data.forEach((item)=>{
            recipeTemplate=item
            this.recipeTemplateTable.push(recipeTemplate)
          })
         this.$forceUpdate()
        }
      });
     
    },
    //西药表格行点击选择添加到已选择西药处方的表格
    RowClickWesternTable(row) {
      let recipelDetailEvtList =
        this.bizFormModel.recipeTemplateDetail;
      let count = 0;
      let hasFlag = false;
      if(row.days.value&&row.frequency.value&&row.singleDosage&&row.total!=0){
        let total = Math.ceil(
                  BigNumber(row.singleDosage)
                    .multipliedBy(row.frequency.value.split("_")[1])
                    .multipliedBy(row.days.name)
                    .toNumber()
              );
              if(total>row.total){
                this.$message.error('该药品设置的总量上限小于默认功效说明计算总量')
                return
              }
      }
      
      recipelDetailEvtList.forEach((element) => {
        if (element.isExtra === 0) {
          count++;
          if (row.id === element.drugStuffId.drugStuffId) {
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
        drugStuffId: row.id,
        name: row.goodsName,
        price: row.price,
        retailPrice: row.retailPrice,
        isUnpackSell: row.isUnpackSell,
        dosisUnit: row.dosisUnit,
        preparationUnit: row.preparationUnit,
        pack: row.pack,
        drug: row,
      };

      let recipelDetailEvt = {
        allFee: 0,
        total: 0,
        singleDosage:0,
        company: this.clinic,
        stuffType: 0,
        isExtra: 0,
        isUnpackSell: BigNumber(row.isUnpackSell).toNumber(),
        drugStuffId: drugStuff,
      };

      this.bizFormModel.recipeTemplateDetail.push(
        JSON.parse(JSON.stringify(recipelDetailEvt))
      );
      setTimeout(() => {
        this.MedicalCalculate()
      }, 0);
      
    },
     getDataFilterTable(data, isExtra,item) {
      let arr=[]
      if(data.length==undefined){
        arr.push(data)
      }else if(data.length>=1){
        for (let i = 0; i < data.length; i++) {
          if(data[i].isExtra==0&&data[i].drugStuffId.drug){
          if(data[i].drugStuffId.drug.type.value=='medicalType_0'){
          //   if(data[i].singleDosage==0&&data[i].singleDosage==" "&&data[i].drugStuffId.drug.singleDosage){
          //   data[i].singleDosage = data[i].drugStuffId.drug.singleDosage
          // }
          //console.log(item,'这是一个新址');
          if(data[i].singleDosage==0&&data[i].singleDosage==" "&&data[i].drugStuffId.drug.singleDosage){
            let flages=false;
              for (let i = 0; i < item.chineseTest.length; i++) {     
               if(item.chineseTest[i]==data[i].drugStuffId.drug.name){
                  flages=true;
                  break;
                 }
              }
              if(!flages){
                  data[i].singleDosage = data[i].drugStuffId.drug.singleDosage?data[i].drugStuffId.drug.singleDosage:""
                  item.chineseTest.push(data[i].drugStuffId.drug.name)
              }
          }
            if(!data[i].frequency&&data[i].drugStuffId.drug.frequency.value){
              data[i].frequency = data[i].drugStuffId.drug.frequency
            }
            if(!data[i].days&&data[i].drugStuffId.drug.days.value){
              data[i].days = data[i].drugStuffId.drug.days
            }
            if(!data[i].westernMedicineUse&&data[i].drugStuffId.drug.westernMedicineUse.value){
              data[i].westernMedicineUse = data[i].drugStuffId.drug.westernMedicineUse
            }
          }
          if(data[i].drugStuffId.drug.type.value=='medicalType_1'){
            let flages=false
          if(data[i].singleDosage==0&&data[i].singleDosage==" "&&data[i].drugStuffId.drug.singleDosage){
              for (let i = 0; i < item.chineseTest.length; i++) {
               if(item.chineseTest[i]==data[i].drugStuffId.drug.name){
                  flages=true;
                  break;
                 }
              }
              if(!flages){
                  data[i].singleDosage = data[i].drugStuffId.drug.singleDosage?data[i].drugStuffId.drug.singleDosage:""
                  item.chineseTest.push(data[i].drugStuffId.drug.name)
              }
          }
            if(!data[i].chineseMedicineUse&&data[i].drugStuffId.drug.chineseMedicineUse.value){
              data[i].chineseMedicineUse = data[i].drugStuffId.drug.chineseMedicineUse
            }
          }
          }
          
          arr.push(data[i])
        }     
      }
       return arr.filter((item) => item.isExtra === isExtra);
       this.MedicalCalculate()
     return arr
    },
    changeUse(row){
      this.changeData = row
    },
     DeleteMedicalRow(index,row,item) {
     
      this.bizFormModel.recipeTemplateDetail =
        this.bizFormModel.recipeTemplateDetail.filter(
          (item) => item !== row
        );
       if(row.infuseGroup>0){
         this.bizFormModel.recipeTemplateDetail =
        this.bizFormModel.recipeTemplateDetail.filter(
          (item) => item.infuseGroup!==row.infuseGroup && this.bizFormModel.recipeTemplateDetail.indexOf(item)!==index
        );
        this.bizFormModel.infusion.infusionProject[row.infuseGroup-1].splice(index,1)
        
       }
      this.MedicalCalculate();
    },

    //附加费删除
    DeleteExMedicalRow(index,row){
       this.bizFormModel.recipeTemplateDetail =
        this.bizFormModel.recipeTemplateDetail.filter(
          (item) => item.drugStuffId.drugStuffId!=row.drugStuffId.drugStuffId
        );
       // this.medicalClickTabsValue.infusion.excharge.splice(index,1)
         this.MedicalCalculate();
    },

    //计算价格
    MedicalCalculate(){
        let medicalFullAmount = 0; //处方所有费用 = 处方费用汇总 + 附加费汇总
        let medicalAmount = 0; //处方费用汇总
        let detailSeq = 0; //处方明细顺序
        let surchargeAmount = 0; //附加费汇总
        let recipelInfo = this.bizFormModel.recipeTemplateInfo;
        let recipelDetailEvtList = this.bizFormModel.recipeTemplateDetail
          ? this.bizFormModel.recipeTemplateDetail
          : [];
       if(this.bizFormModel.type.value=="recipelType_0"){
         recipelDetailEvtList.forEach((rowElement) => {
            if (rowElement.isExtra === 0) {
              detailSeq++;
              rowElement.seq = detailSeq;
             
              rowElement.singleDosage = rowElement.singleDosage
                ? rowElement.singleDosage
                : "";
              rowElement.total = rowElement.total ? rowElement.total : 0;
              rowElement.allFee = rowElement.allFee ? rowElement.allFee : 0;
              if (rowElement.frequency && rowElement.days) {
                let total = Math.ceil(
                  BigNumber(rowElement.singleDosage-0)
                    .multipliedBy(rowElement.frequency.value.split("_")[1])
                    .multipliedBy(rowElement.days.name)
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
       else if(this.bizFormModel.type.value=="recipelType_1"){
         recipelInfo.dosage = recipelInfo.dosage!='' ? recipelInfo.dosage : "";
          recipelDetailEvtList.forEach((rowElement) => {
            if (rowElement.isExtra === 0) {
              detailSeq++;
              rowElement.seq = detailSeq;
              if(rowElement.singleDosage==" "||rowElement.singleDosage==""){
               if(recipelInfo.dosage==""){
                  rowElement.singleDosage=""
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
               }else{
                  rowElement.singleDosage=""
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
              }else{
                if(recipelInfo.dosage==""){
                  rowElement.singleDosage = rowElement.singleDosage
                ? rowElement.singleDosage-0
                : 0;
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

               }else{
                   rowElement.singleDosage = rowElement.singleDosage
                ? rowElement.singleDosage-0
                : 0;
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
            }
          });
          medicalFullAmount = BigNumber(medicalFullAmount)
            .plus(medicalAmount)
            .toNumber();
            this.$forceUpdate()
       }
       //输液处方
       else if(this.bizFormModel.type.value=="recipelType_2"){
           let arr=0;
         this.bizFormModel.recipeTemplateDetail=[]
          for (let i = 0; i < this.bizFormModel.infusion.infusionProject.length; i++) {
                for (let j = 0; j < this.bizFormModel.infusion.infusionProject[i].length; j++) {
              this.bizFormModel.infusion.infusionProject[i][j].drippingSpeed=this.bizFormModel.infusion.drippingSpeed[this.bizFormModel.infusion.infusionProject[i][j].infuseGroup-1]
              this.bizFormModel.infusion.infusionProject[i][j].days=this.bizFormModel.infusion.days[this.bizFormModel.infusion.infusionProject[i][j].infuseGroup-1]
              this.bizFormModel.infusion.infusionProject[i][j].frequency=this.bizFormModel.infusion.frequency[this.bizFormModel.infusion.infusionProject[i][j].infuseGroup-1]
              this.bizFormModel.infusion.infusionProject[i][j].infuseUse=this.bizFormModel.infusion.infuseUse[this.bizFormModel.infusion.infusionProject[i][j].infuseGroup-1]
              this.bizFormModel.recipeTemplateDetail[arr]= this.bizFormModel.infusion.infusionProject[i][j]
              arr++;
              }
                
          }
        for (let i = 0; i < this.bizFormModel.infusion.excharge.length; i++) {
         this.bizFormModel.recipeTemplateDetail.push(this.bizFormModel.infusion.excharge[i])

        }
          recipelDetailEvtList = this.bizFormModel.recipeTemplateDetail
          ? this.bizFormModel.recipeTemplateDetail
          : [];
            console.log(this.medicalEditTabs,'kankan');
           
          //  recipelInfo.frequency = recipelInfo.frequency ? recipelInfo.frequency : 0;
          let infusionRecipelDetailEvtList=this.bizFormModel.recipeTemplateDetail
          infusionRecipelDetailEvtList.forEach((rowElement) => {
          
            if (rowElement.isExtra === 0) {
              detailSeq++;
              rowElement.seq = detailSeq;
              rowElement.singleDosage = rowElement.singleDosage
                ? rowElement.singleDosage
                : "";
              rowElement.total = rowElement.total ? rowElement.total : 0;
              rowElement.allFee = rowElement.allFee ? rowElement.allFee : 0;
              if (rowElement.frequency.value && rowElement.days.name) {
                let total = Math.ceil(
                  BigNumber(rowElement.singleDosage-0)
                    .multipliedBy(rowElement.frequency.value.split("_")[1])
                    .multipliedBy(rowElement.days.name)
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
             
              medicalAmount = BigNumber(medicalAmount)
                .plus(rowElement.allFee)
                .toNumber();
            }
          });
          medicalFullAmount = BigNumber(medicalFullAmount)
            .plus(medicalAmount)
            .toNumber();
       }
      //诊疗项目
       else if(this.bizFormModel.type.value=="recipelType_3"){
         recipelDetailEvtList.forEach((rowElement) => {
            if (rowElement.isExtra === 0) {
              detailSeq++;
              rowElement.seq = detailSeq;
              rowElement.total = rowElement.total ? rowElement.total : "";
              rowElement.allFee = BigNumber(rowElement.drugStuffId.price)
                .multipliedBy(rowElement.total-0)
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
       }

         //每个处方的附加费
        recipelDetailEvtList.forEach((rowElement) => {
          console.log('精神不好估计按时归还健康卡');
        // if(rowElement.infuseGroup==undefined){
            if (rowElement.isExtra === 1) {
            detailSeq++;
            rowElement.seq = detailSeq;
            rowElement.singleDosage = rowElement.singleDosage&&rowElement.singleDosage!=0
              ? rowElement.singleDosage
              : "";
            if (rowElement.isUnpackSell == "1") {
              rowElement.total = rowElement.singleDosage-0;
              rowElement.allFee = BigNumber(rowElement.drugStuffId.retailPrice)
                .multipliedBy(rowElement.total)
                .toNumber();
              rowElement.unitPrice = rowElement.drugStuffId.retailPrice;
              console.log(rowElement, "奇怪");
              // if (rowElement.drugStuffId.stuff.inventory < rowElement.total&&!this.isReadOnly&&tabElement.content.recipelInfo.dispensionStatus==0) {
              //   this.$message.error(
              //     rowElement.drugStuffId.name +
              //       "库存不足，请修改数量或者添加库存！"
              //   );
              // }
            } else {
              //材料
              if (rowElement.stuffType == "4") {
                rowElement.total = BigNumber(rowElement.singleDosage-0)
                  .multipliedBy(rowElement.drugStuffId.stuff.packNumber)
                  .toNumber();

                // if (rowElement.drugStuffId.stuff.inventory < rowElement.total&&!this.isReadOnly&&tabElement.content.recipelInfo.dispensionStatus==0) {
                //   this.$message.error(
                //     rowElement.drugStuffId.name +
                //       "库存不足，请修改数量或者添加库存！"
                //   );
                // }
              }
              //诊疗项目
              else if (rowElement.stuffType == "3") {
                rowElement.total = rowElement.singleDosage-0;
              }

              rowElement.unitPrice = rowElement.drugStuffId.price;
              rowElement.allFee = BigNumber(rowElement.singleDosage-0)
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

      //  recipelInfo.name = tabElement.title;
       // recipelInfo.seq = seq;
        recipelInfo.medicalAmount = medicalAmount;
        recipelInfo.surchargeAmount = surchargeAmount;
        recipelInfo.fee = medicalFullAmount;
        //fullAmount = BigNumber(fullAmount).plus(recipelInfo.fee).toNumber();
        console.log(this.bizFormModel,'看看处方价格');
        //this.payAmount = fullAmount;
    },
   init(item){
     this.SearchInfusion=[""]
     if (item.value == "recipelType_1") {  
          this.bizFormModel.recipeTemplateInfo= {
            company: this.Company,          
            fee: 0,
            recipelUse:  {
                  name: "",
                  value: "",
                },
            smallType:"",
            dosage:1,
          };
          this.bizFormModel.recipeTemplateDetail=[];
          this.bizFormModel.costDebug={chineseTest:[]}
          this.bizFormModel.infusion={
            defaultNumber:1,//默认组号
            infusionProject:[[]],
            drippingSpeed:[""],  //滴速
            days:[{}],    //天数
            frequency:[{}],   //频次
            infuseUse:[{}],     //用法
            zushu:[1],
            excharge:[]
          }
      } else {
       
          this.bizFormModel.recipeTemplateInfo={
            company: this.Company,
            fee: 0,         
            smallType:"",
          };
          this.bizFormModel.recipeTemplateDetail= [];
          this.bizFormModel.costDebug={chineseTest:[]}
           this.bizFormModel.infusion={
            defaultNumber:1,//默认组号
            infusionProject:[[]],
            drippingSpeed:[""],  //滴速
            days:[{}],    //天数
            frequency:[{}],   //频次
            infuseUse:[{}],     //用法
            zushu:[1],
            excharge:[]
          }
      }
   },
   //获取附加费
   GetSurchargeTable(){
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
        params: [
          {
            columnName: "company_id",
            queryType: "=",
            value: this.clinic.id,
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
      //console.log(this.SearchSurchargeInput,'字符');
      if (pattern3.test(this.SearchSurchargeInput)&&this.SearchSurchargeInput!=undefined) {
        SearchModel.params[1].value = this.SearchSurchargeInput.toUpperCase();
        SearchModel.params[1].columnName = "pinyin_code";
      } else {
        
        SearchModel.params[1].value = this.SearchSurchargeInput;
        SearchModel.params[1].columnName = "item_name";
      }
      listCostItemAll(SearchModel).then((responseData) => {
        if (responseData.code == 100) {
          responseData.data.forEach((element) => {
            element["stuffType"] = "3";
            this.SurchargeTable.push(element);
          });
          this.$forceUpdate()
        }
      });
    },
    GetSurchargeStuffTable() {
      let SearchModel = {
        columnName: "",
        limit: 10,
        offset: 0,
        order: "",
        params: [
          {
            columnName: "company_id",
            queryType: "=",
            value: this.clinic.id,
          },
          {
            columnName: "name",
            queryType: "like",
            value: "",
          },
          {
            columnName: "is_out_sell",
            queryType: "=",
            value: "1",
          },
          // {
          //   columnName: "inventory",
          //   queryType: ">",
          //   value: 0,
          // },
        ],
      };
      var pattern2 = new RegExp("[A-Za-z]+");
      if (pattern2.test(this.SearchSurchargeInput)&&this.SearchSurchargeInput!=undefined) {
         //console.log(this.SearchWesternInput,'字符');
        SearchModel.params[1].value = this.SearchSurchargeInput.toUpperCase();
        SearchModel.params[1].columnName = "pinyin_code";
      } else {
        SearchModel.params[1].value = this.SearchSurchargeInput;
        SearchModel.params[1].columnName = "name";
      }
      listStuffAll(SearchModel).then((responseData) => {
        if (responseData.code == 100) {
          responseData.data.forEach((element) => {
            element["stuffType"] = "4";
            this.SurchargeTable.push(element);
          });
          this.$forceUpdate()
        }
      });
    },
     RowClickSurchargeTable(row) {
       console.log(this.bizFormModel.recipeTemplateDetail,'这是什么');
      let recipelDetailEvtList =
        this.bizFormModel.recipeTemplateDetail;
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
          drugStuffId: row.id,
          name: row.name,
          price: row.priceOutSell,
          retailPrice: row.retailPrice,
          isUnpackSell: row.isUnpackSell
            ? BigNumber(row.isUnpackSell).toNumber()
            : 0,
          preparationUnit: row.minUnit,
          pack: row.packUnit,
          stuff: row,
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

      if(this.templateType==2){
        
       this.bizFormModel.infusion.excharge.push(JSON.parse(JSON.stringify(recipelDetailEvt)))
      }else{
         this.bizFormModel.recipeTemplateDetail.push(
        JSON.parse(JSON.stringify(recipelDetailEvt))
      );
      }
      // this.bizFormModel.recipeTemplateDetail.push(
      //   JSON.parse(JSON.stringify(recipelDetailEvt))
      // );
    },
    initInfusion(row){
      this.SearchInfusion=[""]
       let count=0
         for (let i = 0; i < row.recipeTemplateDetail.length; i++) {
              if(row.recipeTemplateDetail[i].isExtra===0){
                if(count<row.recipeTemplateDetail[i].infuseGroup){
                count=row.recipeTemplateDetail[i].infuseGroup
              }
              }
         }
      
      for (let i = 1; i < count; i++) {
        row.infusion.defaultNumber=row.infusion.defaultNumber+1;
        row.infusion.zushu.push(row.infusion.defaultNumber)
        row.infusion.infusionProject.push([])
        row.infusion.drippingSpeed.push("")
        row.infusion.days.push({})
        row.infusion.frequency.push({})
        row.infusion.infuseUse.push({})
        this.SearchInfusion.push("")
      }
      //this.excharge=null
      let arr=[]
       for (let i = 0; i < row.recipeTemplateDetail.length; i++) {
        
        
             if(row.recipeTemplateDetail[i].isExtra!=1){
               
             row.infusion.infusionProject[row.recipeTemplateDetail[i].infuseGroup-1].push(row.recipeTemplateDetail[i])
             row.infusion.drippingSpeed[row.recipeTemplateDetail[i].infuseGroup-1]=row.recipeTemplateDetail[i].drippingSpeed
             row.infusion.days[row.recipeTemplateDetail[i].infuseGroup-1]=row.recipeTemplateDetail[i].days
             row.infusion.frequency[row.recipeTemplateDetail[i].infuseGroup-1]=row.recipeTemplateDetail[i].frequency
             row.infusion.infuseUse[row.recipeTemplateDetail[i].infuseGroup-1]=row.recipeTemplateDetail[i].infuseUse
             }else{
              
               row.infusion.excharge.push(row.recipeTemplateDetail[i])
               //arr.push(element.recipelDetailEvtList[i])
             }

         }
    },
  },
  watch: {
  },
  created(){
    this.GetAllOption();
   // this.GetWesternTable()
  },
  mounted: function() {
   // this.GetMedicalRecordAll();
   
    this.$nextTick(() => {
      this.$on('openViewRecipetemplateDialog', function(recipetemplate,company) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看模板处方'
        this.bizFormModel = {...this.initFormModel(), ...recipetemplate}
        this.clinic=company
        this.initOptions(this.bizFormModel)
        if(this.bizFormModel.type.value=="recipelType_0"){
          this.templateType=0
        }else if(this.bizFormModel.type.value=="recipelType_1"){
          this.templateType=1
        }else if(this.bizFormModel.type.value=="recipelType_2"){
          this.templateType=2
          this.initInfusion(this.bizFormModel)
        }else if(this.bizFormModel.type.value=="recipelType_3"){
          this.templateType=3
        }
        this.tabIndex = '1'
        this.MedicalCalculate();
        this.dialogProps.visible = true
      })
      this.$on('openEditRecipetemplateDialog', function(recipetemplate,company) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改模板处方'
        console.log(company,'看看这是个什么东西');
        this.bizFormModel = {...this.initFormModel(), ...recipetemplate}
        this.clinic=company
        this.initOptions(this.bizFormModel)
         if(this.bizFormModel.type.value=="recipelType_0"){
          this.templateType=0
        }else if(this.bizFormModel.type.value=="recipelType_1"){
          this.templateType=1
        }else if(this.bizFormModel.type.value=="recipelType_2"){
          this.templateType=2
          this.initInfusion(this.bizFormModel)
        }else if(this.bizFormModel.type.value=="recipelType_3"){
          this.templateType=3
        }
        this.MedicalCalculate();
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddRecipetemplateDialog', function(item,index,company) {
        console.log(item,index,company,'这是什么');
        this.clinic=company
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加'+item.name+'模板'
        this.bizFormModel = this.initFormModel()
        this.bizFormModel.type=item
        this.initOptions(this.bizFormModel)
        this.init(item)
        this.tabIndex = '1'
        this.dialogProps.visible = true
        this.templateType=index
        console.log(this.clinic,"这是什么值");
        //this.recipeTemplateTable=[{}]
        this.GetWesternTable()
        //this.addMedicalEditTab(item)
          
      })
      this.$on('openCopyRecipetemplateDialog', function(recipetemplate) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加模板处方'
        this.bizFormModel = {...this.initFormModel(), ...recipetemplate}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
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