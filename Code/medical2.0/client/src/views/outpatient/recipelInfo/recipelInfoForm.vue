<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='recipelInfoForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='病历' prop='medicalRecord.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.medicalRecord.diagnose'></el-input>
            <el-select v-else v-model='bizFormModel.medicalRecord' value-key='id' filterable clearable placeholder='请选择病历' 
              @clear='bizFormModel.medicalRecord={
                "id": null,
                                "diagnose": null,
                }'>
             <el-option v-for='medicalRecord in medicalRecord_List' :key='medicalRecord.id' :label='medicalRecord.diagnose' :value='medicalRecord'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='处方类别' prop='recipelType.value' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.recipelType.name'></el-input>
            <el-select v-else v-model='bizFormModel.recipelType' value-key='value' filterable clearable placeholder='请选择处方类别' 
              @clear='bizFormModel.recipelType={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='recipelType in recipelType_List' :key='recipelType.value' :label='recipelType.name' :value='recipelType'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='剂量' prop='dosage' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.dosage' :maxlength='45' :placeholder='dialogProps.action == "view"? "" : "请输入剂量"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='用法' prop='recipelUse.value' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.recipelUse.name'></el-input>
            <el-select v-else v-model='bizFormModel.recipelUse' value-key='value' filterable clearable placeholder='请选择用法' 
              @clear='bizFormModel.recipelUse={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='recipelUse in recipelUse_List' :key='recipelUse.value' :label='recipelUse.name' :value='recipelUse'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='频次用量' prop='frequency.value' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.frequency.name'></el-input>
            <el-select v-else v-model='bizFormModel.frequency' value-key='value' filterable clearable placeholder='请选择频次用量' 
              @clear='bizFormModel.frequency={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='frequency in frequency_List' :key='frequency.value' :label='frequency.name' :value='frequency'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='服用频次' prop='takeFrequency.value' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.takeFrequency.name'></el-input>
            <el-select v-else v-model='bizFormModel.takeFrequency' value-key='value' filterable clearable placeholder='请选择服用频次' 
              @clear='bizFormModel.takeFrequency={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='takeFrequency in takeFrequency_List' :key='takeFrequency.value' :label='takeFrequency.name' :value='takeFrequency'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='单次用量' prop='singleDosage' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.singleDosage' :maxlength='11' :placeholder='dialogProps.action == "view"? "" : "请输入单次用量"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='处方总价' prop='fee' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.fee'></el-input>
            <number-input v-else v-model="bizFormModel.fee" currency='CNY' :precision="2"></number-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='是否付款' prop='isPay' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isPay' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
                        </el-row>
      <el-row>
        <el-col>
          <el-form-item label='备注信息' prop='remarks' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.remarks' type='textarea'
                                  :maxlength='255' :placeholder='dialogProps.action == "view"? "" : "请输入备注信息"'  clearable></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='付款时间' prop='payDate' >
            <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.payDate' type='datetime' value-format='yyyy-MM-dd HH:mm:ss' :placeholder='dialogProps.action == "view"? "" : "请输入付款时间"' ></el-date-picker>             
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='是否退药' prop='isReturnDrug' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isReturnDrug' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='退药时间' prop='returnDrugDate' >
            <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.returnDrugDate' type='datetime' value-format='yyyy-MM-dd HH:mm:ss' :placeholder='dialogProps.action == "view"? "" : "请输入退药时间"' ></el-date-picker>             
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='是否退费' prop='isReturnFee' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isReturnFee' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='退费时间' prop='returnFeeDate' >
            <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.returnFeeDate' type='datetime' value-format='yyyy-MM-dd HH:mm:ss' :placeholder='dialogProps.action == "view"? "" : "请输入退费时间"' ></el-date-picker>             
          </el-form-item>
        </el-col>
    </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("recipelInfoForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listMedicalRecordAll } from '@/api/outpatient/medicalRecord'
import { listDictItemAll } from '@/api/sys/dictItem'
import { saveRecipelInfo } from '@/api/outpatient/recipelInfo'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'recipelInfo-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
          medicalRecord_List: [],  // 病历
          recipelType_List: [],  // 处方类别
          recipelUse_List: [],  // 用法
          frequency_List: [],  // 频次用量
          takeFrequency_List: [],  // 服用频次
          flage:false,
       dialogProps: {
        visible: false,
        action: '',
        title: '',
        
      },
      formRules: {
      }
    }    
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
          this.doSave()
        } else {
          this.flage=false
          return false
        }
      });
    },
    doSave() {
      this.setLoad()
      saveRecipelInfo(this.bizFormModel).then(responseData => {
        this.flage=false
        if(responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
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
      this.dialogProps.title = '修改处方信息'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['recipelInfoForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': currentUser.company.id,
          'name': null,
        },
        'medicalRecord': {     // 病历
          'id': null,
          'diagnose': null,
        },
        'recipelType': {     // 处方类别
          'value': null,
          'name': null,
        },
        'dosage': '',   // 剂量
        'recipelUse': {     // 用法
          'value': null,
          'name': null,
        },
        'frequency': {     // 频次用量
          'value': null,
          'name': null,
        },
        'takeFrequency': {     // 服用频次
          'value': null,
          'name': null,
        },
        'singleDosage': '',   // 单次用量
        'fee': 0,    // 处方总价
        'isPay': '0',   // 是否付款
        'remarks': '',   // 备注信息
        'payDate': '',   // 付款时间
        'isReturnDrug': '0',   // 是否退药
        'returnDrugDate': '',   // 退药时间
        'isReturnFee': '0',   // 是否退费
        'returnFeeDate': '',   // 退费时间

      }
    },
    initOptions(This) {
      let medicalRecord_search = {
        params: []
      }
        // 字段对应表上filter条件
        medicalRecord_search.params.push.apply(medicalRecord_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 病历表medical_record
      this.pushDataPermissions(medicalRecord_search.params, this.$route.meta.routerId, '1014786989773332526')
      this.medicalRecord_List.splice(0, this.medicalRecord_List.length)
      listMedicalRecordAll(medicalRecord_search).then(responseData => {
        this.medicalRecord_List = responseData.data
      })
      let recipelType_search = {
        params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': '1014474470772899974'}]
      }
        // 字段对应表上filter条件
        recipelType_search.params.push.apply(recipelType_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(recipelType_search.params, this.$route.meta.routerId, '4005')
      this.recipelType_List.splice(0, this.recipelType_List.length)
      listDictItemAll(recipelType_search).then(responseData => {
        this.recipelType_List = responseData.data
      })
      let recipelUse_search = {
        params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': '1014474470772899981'}]
      }
        // 字段对应表上filter条件
        recipelUse_search.params.push.apply(recipelUse_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(recipelUse_search.params, this.$route.meta.routerId, '4005')
      this.recipelUse_List.splice(0, this.recipelUse_List.length)
      listDictItemAll(recipelUse_search).then(responseData => {
        this.recipelUse_List = responseData.data
      })
      let frequency_search = {
        params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': '1014474470772899985'}]
      }
        // 字段对应表上filter条件
        frequency_search.params.push.apply(frequency_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(frequency_search.params, this.$route.meta.routerId, '4005')
      this.frequency_List.splice(0, this.frequency_List.length)
      listDictItemAll(frequency_search).then(responseData => {
        this.frequency_List = responseData.data
      })
      let takeFrequency_search = {
        params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': '1014474470772899990'}]
      }
        // 字段对应表上filter条件
        takeFrequency_search.params.push.apply(takeFrequency_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(takeFrequency_search.params, this.$route.meta.routerId, '4005')
      this.takeFrequency_List.splice(0, this.takeFrequency_List.length)
      listDictItemAll(takeFrequency_search).then(responseData => {
        this.takeFrequency_List = responseData.data
      })
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewRecipelInfoDialog', function(recipelInfo) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看处方信息'
        this.bizFormModel = {...this.initFormModel(), ...recipelInfo}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditRecipelInfoDialog', function(recipelInfo) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改处方信息'
        this.bizFormModel = {...this.initFormModel(), ...recipelInfo}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddRecipelInfoDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加处方信息'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyRecipelInfoDialog', function(recipelInfo) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加处方信息'
        this.bizFormModel = {...this.initFormModel(), ...recipelInfo}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>