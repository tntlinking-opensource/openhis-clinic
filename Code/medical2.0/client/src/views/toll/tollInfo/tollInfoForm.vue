<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%'
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules'
      ref='tollInfoForm' label-width='120px' label-position='right' class='edit-form'>
      <div class="tab-item" v-show='tabIndex=="1"'>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='诊所id' prop='company.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.company.name'></el-input>
            <el-select v-else v-model='bizFormModel.company' value-key='id' filterable clearable placeholder='请选择诊所id'
              @clear='bizFormModel.company={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='company in company_List' :key='company.id' :label='company.name' :value='company'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='病例' prop='medical.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.medical.patient_tell'></el-input>
            <el-select v-else v-model='bizFormModel.medical' value-key='id' filterable clearable placeholder='请选择病例'
              @clear='bizFormModel.medical={
                "id": null,
                                "patient_tell": null,
                }'>
             <el-option v-for='medical in medical_List' :key='medical.id' :label='medical.patient_tell' :value='medical'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='应收(退)金额' prop='amountReceivable' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.amountReceivable'></el-input>
            <number-input v-else v-model="bizFormModel.amountReceivable" currency='CNY' :precision="2"></number-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='实收金额' prop='amountReceived' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.amountReceived'></el-input>
            <number-input v-else v-model="bizFormModel.amountReceived" currency='CNY' :precision="2"></number-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='优惠金额' prop='amountDiscounted' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.amountDiscounted'></el-input>
            <number-input v-else v-model="bizFormModel.amountDiscounted" currency='CNY' :precision="2"></number-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='折扣额度 ' prop='discount' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.discount'></el-input>
            <number-input v-else v-model="bizFormModel.discount"  :precision="2"></number-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='支付类型' prop='paymentType.value' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.paymentType.name'></el-input>
            <el-select v-else v-model='bizFormModel.paymentType' value-key='value' filterable clearable placeholder='请选择支付类型'
              @clear='bizFormModel.paymentType={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='paymentType in paymentType_List' :key='paymentType.value' :label='paymentType.name' :value='paymentType'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='单据状态，参考枚举' prop='amountStatus.value' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.amountStatus.name'></el-input>
            <el-select v-else v-model='bizFormModel.amountStatus' value-key='value' filterable clearable placeholder='请选择单据状态，参考枚举'
              @clear='bizFormModel.amountStatus={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='amountStatus in amountStatus_List' :key='amountStatus.value' :label='amountStatus.name' :value='amountStatus'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='收费单号' prop='tollNumber' >
            <el-input disabled v-model='bizFormModel.tollNumber' :maxlength='64' placeholder='保存后自动生成' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='收费类型，参考枚举' prop='tollType.value' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.tollType.name'></el-input>
            <el-select v-else v-model='bizFormModel.tollType' value-key='value' filterable clearable placeholder='请选择收费类型，参考枚举'
              @clear='bizFormModel.tollType={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='tollType in tollType_List' :key='tollType.value' :label='tollType.name' :value='tollType'></el-option>
            </el-select>
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
          <el-form-item label='患者' prop='patient.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.patient.name'></el-input>
            <el-select v-else v-model='bizFormModel.patient' value-key='id' filterable clearable placeholder='请选择患者'
              @clear='bizFormModel.patient={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='patient in patient_List' :key='patient.id' :label='patient.name' :value='patient'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
    </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' type='primary' :plain='true' @click='onSubmit("tollInfoForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listCompanyAll } from '@/api/org/company'
import { listMedicalRecordAll } from '@/api/outpatient/medicalRecord'
import { listDictItemAll } from '@/api/sys/dictItem'
import { listPatientAll } from '@/api/outpatient/patient'
import { saveTollInfo } from '@/api/toll/tollInfo'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'tollInfo-form',
  components: {
    OperationIcon
  },
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
          company_List: [],  // 诊所id
          medical_List: [],  // 病例
          paymentType_List: [],  // 支付类型
          amountStatus_List: [],  // 单据状态，参考枚举
          tollType_List: [],  // 收费类型，参考枚举
          patient_List: [],  // 患者
       dialogProps: {
        visible: false,
        action: '',
        title: ''
      },
      formRules: {
        'paymentType.value': [
            { required: true, message: '请选择支付类型', trigger: 'change' }
        ],
        'amountStatus.value': [
            { required: true, message: '请选择单据状态，参考枚举', trigger: 'change' }
        ],
        'tollNumber': [
            { required: true, message: '请选择收费单号', trigger: 'blur' }
        ],
        'tollType.value': [
            { required: true, message: '请选择收费类型，参考枚举', trigger: 'change' }
        ],
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
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave()
        } else {
          return false
        }
      });
    },
    doSave() {
      this.setLoad()
      saveTollInfo(this.bizFormModel).then(responseData => {
        if(responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改收费管理'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['tollInfoForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': currentUser.company.id,
          'name': null,
        },
        'medical': {     // 病例
          'id': null,
          'patient_tell': null,
        },
        'amountReceivable': 0,    // 应收(退)金额
        'amountReceived': 0,    // 实收金额
        'amountDiscounted': 0,    // 优惠金额
        'discount': 0,    // 折扣额度
        'paymentType': {     // 支付类型
          'value': null,
          'name': null,
        },
        'amountStatus': {     // 单据状态，参考枚举
          'value': null,
          'name': null,
        },
        'tollNumber': '',    // 收费单号
        'tollType': {     // 收费类型，参考枚举
          'value': null,
          'name': null,
        },
        'remarks': '',   // 备注信息
        'patient': {     // 患者
          'id': null,
          'name': null,
        },

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
      let medical_search = {
        params: []
      }
        // 字段对应表上filter条件
        medical_search.params.push.apply(medical_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 病历表medical_record
      this.pushDataPermissions(medical_search.params, this.$route.meta.routerId, '1014786989773332526')
      this.medical_List.splice(0, this.medical_List.length)
      listMedicalRecordAll(medical_search).then(responseData => {
        this.medical_List = responseData.data
      })
      let paymentType_search = {
        params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': '1024846610635146222'}]
      }
        // 字段对应表上filter条件
        paymentType_search.params.push.apply(paymentType_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(paymentType_search.params, this.$route.meta.routerId, '4005')
      this.paymentType_List.splice(0, this.paymentType_List.length)
      listDictItemAll(paymentType_search).then(responseData => {
        this.paymentType_List = responseData.data
      })
      let amountStatus_search = {
        params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': '1024846610635146229'}]
      }
        // 字段对应表上filter条件
        amountStatus_search.params.push.apply(amountStatus_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(amountStatus_search.params, this.$route.meta.routerId, '4005')
      this.amountStatus_List.splice(0, this.amountStatus_List.length)
      listDictItemAll(amountStatus_search).then(responseData => {
        this.amountStatus_List = responseData.data
      })
      let tollType_search = {
        params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': '1024846610635146243'}]
      }
        // 字段对应表上filter条件
        tollType_search.params.push.apply(tollType_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(tollType_search.params, this.$route.meta.routerId, '4005')
      this.tollType_List.splice(0, this.tollType_List.length)
      listDictItemAll(tollType_search).then(responseData => {
        this.tollType_List = responseData.data
      })
      let patient_search = {
        params: []
      }
        // 字段对应表上filter条件
        patient_search.params.push.apply(patient_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 患者表patient
      this.pushDataPermissions(patient_search.params, this.$route.meta.routerId, '1008489176147648530')
      this.patient_List.splice(0, this.patient_List.length)
      listPatientAll(patient_search).then(responseData => {
        this.patient_List = responseData.data
      })
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewTollInfoDialog', function(tollInfo) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看收费管理'
        this.bizFormModel = {...this.initFormModel(), ...tollInfo}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditTollInfoDialog', function(tollInfo) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改收费管理'
        this.bizFormModel = {...this.initFormModel(), ...tollInfo}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddTollInfoDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加收费管理'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyTollInfoDialog', function(tollInfo) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加收费管理'
        this.bizFormModel = {...this.initFormModel(), ...tollInfo}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }
}
</script>
