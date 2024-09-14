<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='80%'
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
    </div>
    <el-form :model='bizFormModel' :rules='formRules' v-if="dialogProps.dataLoaded"
      ref='reviewForm' label-width='120px' label-position='right' class='edit-form'>
      <el-row>
        <el-col :span='5'>
          <el-form-item label='患者姓名' >
            <el-input :disabled='true' v-model='bizFormModel.recipelInfoReview.recipelInfo.registration.patientId.name' :maxlength='128' :placeholder='""' ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span='5'>
          <el-form-item label='性别'>
            <el-input :disabled='true' v-model='bizFormModel.recipelInfoReview.recipelInfo.registration.patientId.gender.name' :maxlength='128' :placeholder='""' ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span='5'>
          <el-form-item label='年龄' >
            <el-input :disabled='true' v-model='bizFormModel.recipelInfoReview.recipelInfo.registration.patientId.age' :maxlength='128' :placeholder='""' ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span='5'>
          <el-form-item label='身份证' >
            <el-input :disabled='true' v-model='bizFormModel.recipelInfoReview.recipelInfo.registration.patientId.card' :maxlength='128' :placeholder='""' ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span='4'>
          <el-form-item label='联系方式' >
            <el-input :disabled='true' v-model='bizFormModel.recipelInfoReview.recipelInfo.registration.patientId.phone' :maxlength='128' :placeholder='""' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span='24/2'>
          <el-form-item label='西医诊断' >
            <el-input :disabled='true' v-model='bizFormModel.westernDiagnose' type='textarea'
                      :maxlength='2000' :placeholder='""'></el-input>
          </el-form-item>
        </el-col>
        <el-col :span='24/2'>
          <el-form-item label='中医诊断' >
            <el-input :disabled='true' v-model='bizFormModel.chinaDiagnose' type='textarea'
                      :maxlength='2000' :placeholder='""'></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <span class="price">本处方费用{{bizFormModel.recipelInfoReview.recipelInfo.fee}}元</span>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24/1">
          <el-divider content-position="left">处方信息</el-divider>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="3">
          <el-input
            disabled
            v-model="bizFormModel.recipelInfoReview.recipelInfo.recipelType.name"></el-input>
        </el-col>
        <el-col :span="4">
          <!--<el-checkbox v-if="bizFormModel.recipelInfoReview.recipelInfo.recipelType.value == 'recipelType_0'"
                       style="margin-left: 20px;"
                       disabled
                       v-model="bizFormModel.recipelInfoReview.recipelInfo.chronicDisease"  >是否慢病</el-checkbox>-->
          <el-switch
            v-if="bizFormModel.recipelInfoReview.recipelInfo.recipelType.value == 'recipelType_0'"
            style="margin-left: 20px;"
            v-model="bizFormModel.recipelInfoReview.recipelInfo.chronicDisease"
            active-text="是否慢病"
            disabled>
          </el-switch>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24/1">
          <el-table :data="bizFormModel.recipelDetails" >
            <el-table-column label="序号" type="index" align="center">
            </el-table-column>
            <el-table-column
              prop="drugStuffId.name"
              label="药品名称"
              width="width">
              <template slot-scope="scope">
                {{ scope.row.drugStuffId.name }}
                <div v-if="bizFormModel.recipelInfoReview.recipelInfo.recipelType.value=='recipelType_0'">
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
              prop="drugStuffId.drug.factory.name"
              label="厂家"
              width="width"
            >
            </el-table-column>

            <el-table-column
              prop="westernMedicineUse.name"
              label="用法"
              width="width"
              v-if="bizFormModel.recipelInfoReview.recipelInfo.recipelType.value == 'recipelType_0'"
            >
            </el-table-column>
            <el-table-column
              prop="singleDosage"
              label="单次用量"
              width="width"
              v-if="bizFormModel.recipelInfoReview.recipelInfo.recipelType.value == 'recipelType_0'"
            >
              <template slot-scope="scope">
                <template>{{scope.row.singleDosage?scope.row.singleDosage+''+scope.row.drugStuffId.dosisUnit.name:"" }}</template>
              </template>
            </el-table-column>
            <el-table-column
              prop="frequency"
              label="频次"
              align="center"
              v-if="bizFormModel.recipelInfoReview.recipelInfo.recipelType.value == 'recipelType_0'"
              width="width"
            >
              <template slot-scope="scope"
              >{{ scope.row.infuseUse.name
                }}{{ scope.row.frequency.name }}</template
              >
            </el-table-column>
            <el-table-column prop="days.name" label="天数" width="width" align="center"
                             v-if="bizFormModel.recipelInfoReview.recipelInfo.recipelType.value == 'recipelType_0'">
              <template slot-scope="scope">
                {{scope.row.days.name}}天
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
            <el-table-column prop="allFee" width="width" label="总额">
              <template slot-scope="scope">
                {{scope.row.allFee}}元
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row v-if="bizFormModel.recipelInfoReview.recipelInfo.recipelType.value  =='recipelType_1'">
        <el-input disabled v-model="bizFormModel.recipelInfoReview.recipelInfo.dosage" oninput="value=value.replace(/[^\d.]/g,'')" style="width: 60px"></el-input>
        &nbsp;剂 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;用法：
        <el-select disabled v-model="bizFormModel.recipelInfoReview.recipelInfo.recipelUse" placeholder="请选择" style="width: 110px">
          <el-option v-for="pitem in ChineseUseOption" :key="pitem.value"
                     :label="pitem.name"
                     :value="{ name: pitem.name, value: pitem.value }">
          </el-option>
        </el-select>
        <el-select disabled v-model="bizFormModel.recipelInfoReview.recipelInfo.frequency" placeholder="请选择" style="width: 110px">
          <el-option v-for="pitem in ChineseTimeOption" :key="pitem.value"
                     :label="pitem.name"
                     :value="{ name: pitem.name, value: pitem.value }">
          </el-option>
        </el-select>
        <el-select disabled v-model="bizFormModel.recipelInfoReview.recipelInfo.takeFrequency" placeholder="请选择" style="width: 110px">
          <el-option v-for="pitem in JSON.parse(
                            JSON.stringify(FrequencyOption)
                          )" :key="pitem.value" :label="pitem.name" :value="{
                            name: pitem.name,
                            value: pitem.value,
                          }">
          </el-option>
        </el-select>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 一次&nbsp;<el-input disabled v-model="bizFormModel.recipelInfoReview.recipelInfo.singleDosage" style="width: 60px"></el-input>&nbsp;
        <span>ml</span>
      </el-row>
      <el-row>
        <el-col :span="24/1">
          <el-divider content-position="left">附加费</el-divider>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24/1">
          <el-table :data="bizFormModel.additionalCharges">
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
                {{scope.row.allFee}}元
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24/1">
          <el-divider content-position="left">医嘱事项</el-divider>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24/1">
          <el-input
            type="textarea"
            :rows="2"
            v-model="bizFormModel.recipelInfoReview.recipelInfo.entrust"
            disabled>
          </el-input>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24/1">
          <el-divider content-position="left">处方审查内容</el-divider>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24/3">
          <el-form-item label='是否合理' >
            <el-radio-group :disabled="dialogProps.action == 'view'" v-model="bizFormModel.recipelInfoReview.reviewResult">
              <el-radio :label="1">合理</el-radio>
              <el-radio :label="0">不合理</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="24/3">
          <el-form-item label='审查人'  v-if='bizFormModel.recipelInfoReview.reviewStatus == "1"'>
            <el-input :disabled='true'  v-model='bizFormModel.recipelInfoReview.reviewerName' :placeholder='""'></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24/3">
          <el-form-item label='审查时间'  v-if='bizFormModel.recipelInfoReview.reviewStatus == "1"'>
            <el-input :disabled='true'  v-model='bizFormModel.recipelInfoReview.createDate' :placeholder='""'></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24/1">
          <el-input
            type="textarea"
            :rows="2"
            :placeholder='"请输入内容"'
            :disabled="dialogProps.action == 'view'"
            v-model="bizFormModel.recipelInfoReview.reviewContent">
          </el-input>
        </el-col>
      </el-row>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("reviewForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmitAndContinue("reviewForm")'>保存并继续</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
  </el-dialog>
</template>
<script>
  import { saveRecipelInfoReview } from '@/api/outpatient/recipelInfoReview'
  import {listDictItemAll} from "@/api/sys/dictItem"
  import BaseUI from '@/views/components/baseUI'
  import OperationIcon from '@/components/OperationIcon'

  export default {
  extends: BaseUI,
  name: 'review-form',
  components: {
    OperationIcon
  },
    computed: {
    },
  data() {
    return {
      bizFormModel: this.initFormModel(),
      flage: false,
      dialogProps: {
        visible: false,
        action: '',
        title: '',
        dataLoaded: false
      },
      formRules: {
        'name': [
          {required: true, message: '请输入名称', trigger: 'blur'}
        ],
        'theme': [
          {required: true, message: '请输入主题', trigger: 'blur'}
        ],
      },
      //中药用法下拉列表
      ChineseUseOption: [],
      ChineseTimeOption: [],
      ChineseUseTimeOption: [],
      //特殊诊所
      isSpecial: false,
      //西药频度下拉列表
      FrequencyOption: [],
    };
  },
  props: {
    // 权限
    permission: {
      type: Object
    }
  },
  methods: {
    onSubmit(formName) {
      this.flage=true
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave(false)
        } else {
          this.flage=false
          return false
        }
      });
    },
    onSubmitAndContinue(formName){
      this.flage=true
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave(true)
        } else {
          this.flage=false
          return false
        }
      });
    },
    doSave(visible) {
      this.setLoad()
      this.bizFormModel.recipelInfoReview.reviewerName = this.currentUser.name
      this.bizFormModel.recipelInfoReview.reviewStatus = 1
      saveRecipelInfoReview(this.bizFormModel.recipelInfoReview).then(responseData => {
        this.flage=false
        if(responseData.code == 100) {
          if (visible) {
            this.$emit('save-and-continue');
          }else {
            this.dialogProps.visible = visible
            this.$emit('save-finished');
          }
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.flage=false
        this.outputError(error)
      })
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改处方审查'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false
      this.$emit('save-finished');
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['reviewForm'].clearValidate()
      })
    },
    updateData(review){
      this.bizFormModel = review
    },
    initFormModel(This) {
      return {

      }
    },
    initOptions(This) {
      if (this.ChineseUseOption.length === 0) {
        this.getOption("1014474470772899981")
      }
      if (this.ChineseTimeOption.length === 0) {
        this.getOption("1014474470772899985")
      }
      if (this.ChineseUseTimeOption.length === 0) {
        this.getOption("1014474470772900058")
      }
      if (this.FrequencyOption.length === 0) {
        this.getOption("1014474470772899990")
      }
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
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewReviewDialog', function(review) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看处方审查'
        this.bizFormModel = review
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
        this.dialogProps.dataLoaded = true
      })
      this.$on('openEditReviewDialog', function(review) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改处方审查'
        this.bizFormModel = review
        this.initOptions(this.bizFormModel)
        console.log(this.bizFormModel,"明细页面")
        this.dialogProps.visible = true
        this.dialogProps.dataLoaded = true
      })
      this.$on('openAddReviewDialog', function(review) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加处方审查'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
        this.dialogProps.dataLoaded = true
      })
      this.$on('openCopyReviewDialog', function(review) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加处方审查'
        this.bizFormModel = review
        this.initOptions(this.bizFormModel)
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
        this.dialogProps.dataLoaded = true
      })
    })
  }
}
</script>
<style scoped>
  .price{
    padding: 8px 0 0;
    font-size: 14px;
    font-weight: 700;
    color: midnightblue;
    line-height: 28px;
  }
</style>
