<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules' 
      ref='resourceForm' label-width='120px' label-position='right' class='edit-form'>    

              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='名称' prop='name' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入名称"' autofocus></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='代码' prop='code' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.code' :maxlength='64' :placeholder='dialogProps.action == "view"? "" : "请输入代码"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='Url' prop='url' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.url' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入Url"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='权限' prop='permission' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.permission' :maxlength='32' :placeholder='dialogProps.action == "view"? "" : "请输入权限"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='可授权' prop='canPermission' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.canPermission' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='默认权限' prop='isDefault' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isDefault' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='禁用' prop='isLocked' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isLocked' active-color='#13ce66' inactive-color='#dbdfe6' :active-value='1' :inactive-value='0'></el-switch>
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
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("resourceForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { saveResource } from '@/api/admin/resource'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'resource-form',
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
        'code': [
            { required: true, message: '请输入代码', trigger: 'blur' }
        ],
        'url': [
            { required: true, message: '请输入Url', trigger: 'blur' }
        ],
        'permission': [
            { required: true, message: '请输入权限', trigger: 'blur' }
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
      saveResource(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改资源'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['resourceForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'name': '',   // 名称
        'code': This ? 'This.router.code' : null,   // 代码
        'url': This ? 'This.router.url' : null,   // Url
        'permission': '',   // 权限
        'canPermission': '1',   // 可授权
        'isDefault': '0',   // 默认权限
        'isLocked': 0,    // 禁用
        'router': {     // 父表 路由
          'id': validatenull(This) || validatenull(This.router) ? null : This.router.id,
          'name': validatenull(This) || validatenull(This.router) ? null : This.router.name,
        },
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
      this.$on('openViewResourceDialog', function(resource) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看资源'
        this.bizFormModel = resource
        // this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openEditResourceDialog', function(resource) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改资源'
        this.bizFormModel = resource
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openAddResourceDialog', function(parent) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加资源'
        this.bizFormModel = this.initFormModel(parent)
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openCopyResourceDialog', function(resource) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加资源'
        this.bizFormModel = resource
        this.initOptions(this.bizFormModel)
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>