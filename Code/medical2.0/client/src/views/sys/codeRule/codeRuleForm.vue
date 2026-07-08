<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='30%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action === "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules' 
      ref='codeRuleForm' label-width='120px' label-position='right' class='edit-form'>    
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='编码' prop='code'>
            <el-input :disabled='dialogProps.action === "view" || currentUserId !== "1000"' v-model='bizFormModel.code' :maxlength='20' :placeholder='dialogProps.action === "view"? "" : "请输入编码"' autofocus></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='名称' prop='name'>
            <el-input :disabled='dialogProps.action === "view"' v-model='bizFormModel.name' :maxlength='128' :placeholder='dialogProps.action === "view"? "" : "请输入名称"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='规则' prop='ruleDef'>
            <el-input :disabled='dialogProps.action === "view"' v-model='bizFormModel.ruleDef' :maxlength='512' :placeholder='dialogProps.action === "view"? "" : "请输入规则"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col>
          <el-form-item label='备注信息' prop='remarks'>        
            <el-input :disabled='dialogProps.action === "view"' v-model='bizFormModel.remarks' type='textarea'  
             :maxlength='255' :placeholder='dialogProps.action === "view"? "" : "请输入备注信息"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action !== "view"' :disabled="flag" type='primary' :plain='true' @click='onSubmit("codeRuleForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action !== "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action === "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { saveCodeRule } from '@/api/sys/codeRule'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'codeRule-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      flag:false,
       dialogProps: {
        visible: false,
        action: '',
        title: '',
        
      },
      formRules: {
        'code': [
            { required: true, message: '请输入编码', trigger: 'blur' }
        ],
        'name': [
            { required: true, message: '请输入名称', trigger: 'blur' }
        ],
        'ruleDef': [
            { required: true, message: '请输入规则', trigger: 'blur' }
        ],
      },
      currentUserId: currentUser.id
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
      this.flag=true
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave()
        } else {
          this.flag=false
          return false
        }
      });
    },
    doSave() {
      this.setLoad()
      saveCodeRule(this.bizFormModel).then(responseData => {
        this.flag=false
        if(responseData.code === 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.flag=false
        this.outputError(error)
      })
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改系统编码规则'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['codeRuleForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
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
  },
  methods: {
    openViewCodeRuleDialog(codeRule) {
      this.dialogProps.action = 'view'
      this.dialogProps.title = '查看系统编码规则'
      this.bizFormModel = codeRule
      this.initOptions(this.bizFormModel)
      this.dialogProps.visible = true
    },
    openEditCodeRuleDialog(codeRule) {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改系统编码规则'
      this.bizFormModel = codeRule
      this.initOptions(this.bizFormModel)
      this.dialogProps.visible = true
    },
    openAddCodeRuleDialog() {
      this.dialogProps.action = 'add'
      this.dialogProps.title = '添加系统编码规则'
      this.bizFormModel = this.initFormModel()
      this.initOptions(this.bizFormModel)
      this.dialogProps.visible = true
    },
    openCopyCodeRuleDialog(codeRule) {
      this.dialogProps.action = 'add'
      this.dialogProps.title = '添加系统编码规则'
      this.bizFormModel = codeRule
      this.initOptions(this.bizFormModel)
      this.bizFormModel.id = null
      this.dialogProps.visible = true
    },
  },
}
</script>