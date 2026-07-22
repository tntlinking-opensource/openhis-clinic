<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action === "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules' 
      ref='propertySetForm' label-width='120px' label-position='right' class='edit-form'>    

              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='名称' prop='name' >
            <el-input :disabled='dialogProps.action === "view"' v-model='bizFormModel.name' :maxlength='128' :placeholder='dialogProps.action === "view"? "" : "请输入名称"' autofocus></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='属性定义' prop='propertiesDef' >
            <el-input :disabled='dialogProps.action === "view"' v-model='bizFormModel.propertiesDef'  :placeholder='dialogProps.action === "view"? "" : "请输入属性定义"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
                    <el-row>
                <el-col>
                    <el-form-item label='备注信息' prop='remarks' >
                        <el-input :disabled='dialogProps.action === "view"' v-model='bizFormModel.remarks' type='textarea'
                                  :maxlength='255' :placeholder='dialogProps.action === "view"? "" : "请输入备注信息"'  clearable></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action !== "view"' :disabled="flag" type='primary' :plain='true' @click='onSubmit("propertySetForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action !== "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action === "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { savePropertySet } from '@/api/sys/propertySet'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'propertySet-form',
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
        'name': [
            { required: true, message: '请输入名称', trigger: 'blur' }
        ],
        'propertiesDef': [
            { required: true, message: '请输入属性定义', trigger: 'blur' }
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
      savePropertySet(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改属性集'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['propertySetForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'name': '',   // 名称
        'propertiesDef': '',   // 属性定义
        'remarks': '',   // 备注信息

      }
    },
    initOptions(This) {
    },
    openViewPropertySetDialog(propertySet) {
      this.dialogProps.action = 'view'
      this.dialogProps.title = '查看属性集'
      this.bizFormModel = propertySet
      this.initOptions(this.bizFormModel)
      this.dialogProps.visible = true
    },
    openEditPropertySetDialog(propertySet) {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改属性集'
      this.bizFormModel = propertySet
      this.initOptions(this.bizFormModel)
      this.dialogProps.visible = true
    },
    openAddPropertySetDialog() {
      this.dialogProps.action = 'add'
      this.dialogProps.title = '添加属性集'
      this.bizFormModel = this.initFormModel()
      this.initOptions(this.bizFormModel)
      this.dialogProps.visible = true
    },
    openCopyPropertySetDialog(propertySet) {
      this.dialogProps.action = 'add'
      this.dialogProps.title = '添加属性集'
      this.bizFormModel = propertySet
      this.initOptions(this.bizFormModel)
      this.bizFormModel.id = null
      this.dialogProps.visible = true
    },
  },
  watch: {
  },
  mounted: function() {
  },
}
</script>