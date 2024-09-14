<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='30%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules' 
      ref='companyCodeRuleForm' label-width='120px' label-position='right' class='edit-form'>    
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='规则' prop='ruleDef'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.ruleDef' :maxlength='512' :placeholder='dialogProps.action == "view"? "" : "请输入规则"' autofocus></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col>
          <el-form-item label='备注信息' prop='remarks'>        
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.remarks' type='textarea'  
             :maxlength='255' :placeholder='dialogProps.action == "view"? "" : "请输入备注信息"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("companyCodeRuleForm")'>保 存</el-button>    
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { saveCompanyCodeRule } from '@/api/sys/companyCodeRule'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'companyCodeRule-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      flage:false,
       dialogProps: {
        visible: false,
        action: '',
        title: '',
        
      },
      formRules: {
        'ruleDef': [
            { required: true, message: '请输入规则', trigger: 'blur' }
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
      saveCompanyCodeRule(this.bizFormModel).then(responseData => {
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
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['companyCodeRuleForm'].clearValidate()
      })
    },
    switchEdit() {
      this.dialogProps.action = 'view'
      this.dialogProps.title = '查看公司编码规则'
      this.initOptions(this.bizFormModel)
    },
    initFormModel(This) {
      return {
        'company': {     // 公司
          'id': null,
          'name': null,
        },
        'sysRule': {     // 系统编号规则
          'id': null,
          'name': null,
        },
        'code': '',   // 编码
        'name': '',   // 名称
        'ruleDef': '',   // 规则
        'remarks': '',   // 备注信息

      }
    },
    initOptions(This) {
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewCompanyCodeRuleDialog', function(companyCodeRule) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看公司编码规则'
        this.bizFormModel = companyCodeRule
        // this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openEditCompanyCodeRuleDialog', function(companyCodeRule) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改公司编码规则'
        this.bizFormModel = companyCodeRule
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openAddCompanyCodeRuleDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加公司编码规则'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openCopyCompanyCodeRuleDialog', function(companyCodeRule) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加公司编码规则'
        this.bizFormModel = companyCodeRule
        this.initOptions(this.bizFormModel)
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>