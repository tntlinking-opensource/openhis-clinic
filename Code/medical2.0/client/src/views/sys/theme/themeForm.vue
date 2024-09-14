<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='30%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules' 
      ref='themeForm' label-width='120px' label-position='right' class='edit-form'>    

              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='名称' prop='name'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入名称"' autofocus></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col>
          <el-form-item label='主题' prop='theme'>        
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.theme' type='textarea'  
             :maxlength='2000' :placeholder='dialogProps.action == "view"? "" : "请输入主题"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='默认' prop='isDefault'>
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isDefault' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("themeForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { saveTheme } from '@/api/sys/theme'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'theme-form',
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
        'theme': [
            { required: true, message: '请输入主题', trigger: 'blur' }
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
      saveTheme(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改系统主题'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['themeForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'name': '',   // 名称
        'theme': '',   // 主题
        'isDefault': 1,   // 默认
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
      this.$on('openViewThemeDialog', function(theme) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看系统主题'
        this.bizFormModel = theme
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openEditThemeDialog', function(theme) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改系统主题'
        this.bizFormModel = theme
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openAddThemeDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加系统主题'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openCopyThemeDialog', function(theme) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加系统主题'
        this.bizFormModel = theme
        this.initOptions(this.bizFormModel)
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>