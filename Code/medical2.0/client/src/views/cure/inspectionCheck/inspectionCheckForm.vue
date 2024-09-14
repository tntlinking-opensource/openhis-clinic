<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='inspectionCheckForm' label-width='120px' label-position='right' class='edit-form'>  
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
          <el-form-item label='患者id' prop='patientId' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.patientId' :maxlength='20' :placeholder='dialogProps.action == "view"? "" : "请输入患者id"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='登记信息id' prop='registrationId' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.registrationId' :maxlength='20' :placeholder='dialogProps.action == "view"? "" : "请输入登记信息id"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='诊疗项目id' prop='costItemId' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.costItemId' :maxlength='20' :placeholder='dialogProps.action == "view"? "" : "请输入诊疗项目id"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='项目名称' prop='name' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入项目名称"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='类型' prop='type' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.type' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='填写状态' prop='status' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.status' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='患者姓名' prop='patientName' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.patientName' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入患者姓名"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='性别' prop='sex' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.sex' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入性别"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='手机号' prop='phone' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.phone' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入手机号"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='诊断' prop='diagnose' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.diagnose' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入诊断"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='开单医生' prop='completeBy' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.completeBy' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入开单医生"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='开单时间' prop='completeDate' >
            <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.completeDate' type='datetime' value-format='yyyy-MM-dd HH:mm:ss' :placeholder='dialogProps.action == "view"? "" : "请输入开单时间"' ></el-date-picker>             
          </el-form-item>
        </el-col>
    </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' type='primary' :plain='true' @click='onSubmit("inspectionCheckForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listCompanyAll } from '@/api/org/company'
import { saveInspectionCheck } from '@/api/cure/inspectionCheck'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'inspectionCheck-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
          company_List: [],  // 诊所id
       dialogProps: {
        visible: false,
        action: '',
        title: ''
      },
      formRules: {
        'company.id': [
            { required: true, message: '请选择诊所id', trigger: 'change' }
        ],
        'patientId': [
            { required: true, message: '请输入患者id', trigger: 'blur' }
        ],
        'registrationId': [
            { required: true, message: '请输入登记信息id', trigger: 'blur' }
        ],
        'costItemId': [
            { required: true, message: '请输入诊疗项目id', trigger: 'blur' }
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
      saveInspectionCheck(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改检验检查'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['inspectionCheckForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': currentUser.company.id,
          'name': null,
        },
        'patientId': '',   // 患者id
        'registrationId': '',   // 登记信息id
        'costItemId': '',   // 诊疗项目id
        'name': '',   // 项目名称
        'type': '',   // 类型（0：检验；1：检查）
        'status': '0',   // 填写状态（0：未填写；1：已填写）
        'patientName': '',   // 患者姓名
        'sex': '',   // 性别
        'phone': '',   // 手机号
        'diagnose': '',   // 诊断
        'completeBy': '',   // 开单医生
        'completeDate': '',   // 开单时间

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
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewInspectionCheckDialog', function(inspectionCheck) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看检验检查'
        this.bizFormModel = {...this.initFormModel(), ...inspectionCheck}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditInspectionCheckDialog', function(inspectionCheck) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改检验检查'
        this.bizFormModel = {...this.initFormModel(), ...inspectionCheck}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddInspectionCheckDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加检验检查'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyInspectionCheckDialog', function(inspectionCheck) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加检验检查'
        this.bizFormModel = {...this.initFormModel(), ...inspectionCheck}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>