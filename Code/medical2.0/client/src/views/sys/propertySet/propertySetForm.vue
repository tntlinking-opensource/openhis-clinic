<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules' 
      ref='propertySetForm' label-width='120px' label-position='right' class='edit-form'>    

              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='名称' prop='name' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入名称"' autofocus></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='属性定义' prop='propertiesDef' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.propertiesDef'  :placeholder='dialogProps.action == "view"? "" : "请输入属性定义"' ></el-input>
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
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("propertySetForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
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
      flage:false,
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
      savePropertySet(this.bizFormModel).then(responseData => {
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
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewPropertySetDialog', function(propertySet) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看属性集'
        this.bizFormModel = propertySet
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openEditPropertySetDialog', function(propertySet) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改属性集'
        this.bizFormModel = propertySet
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openAddPropertySetDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加属性集'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openCopyPropertySetDialog', function(propertySet) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加属性集'
        this.bizFormModel = propertySet
        this.initOptions(this.bizFormModel)
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>