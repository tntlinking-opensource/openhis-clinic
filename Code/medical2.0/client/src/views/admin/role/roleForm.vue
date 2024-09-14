<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='30%'
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules'
      ref='roleForm' label-width='120px' label-position='right' class='edit-form'
      style="marginTop: 10px">

              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='代码' prop='code'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.code' :maxlength='32' :placeholder='dialogProps.action == "view"? "" : "请输入代码"' autofocus></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='名称' prop='name'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='32' :placeholder='dialogProps.action == "view"? "" : "请输入名称"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='是否启用' prop='isLocked'>
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isLocked' active-color='#13ce66' inactive-color='#dbdfe6' :active-value='0' :inactive-value='1'></el-switch>
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
      <el-button v-if='dialogProps.action != "view"'  :disabled="flage" type='primary' :plain='true' @click='onSubmit("roleForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { treeCompany } from '@/api/org/company'
import { saveRole } from '@/api/admin/role'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'role-form',
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
        'company.id': [
            { required: true, message: '请选择公司', trigger: 'change' }
        ],
        'code': [
            { required: true, message: '请输入代码', trigger: 'blur' }
        ],
        'name': [
            { required: true, message: '请输入名称', trigger: 'blur' }
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
      saveRole(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改角色'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['roleForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 父表 公司
          'id': validatenull(This) || validatenull(This.company) ? null : This.company.id,
          'name': validatenull(This) || validatenull(This.company) ? null : This.company.name,
        },
        'code': '',   // 代码
        'name': '',   // 名称
        'isLocked': 0,    // 禁用
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
      this.$on('openViewRoleDialog', function(role) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看角色'
        this.bizFormModel = role
        // this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openEditRoleDialog', function(role) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改角色'
        this.bizFormModel = role
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openAddRoleDialog', function(parent) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加角色'
        this.bizFormModel = this.initFormModel(parent)
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openCopyRoleDialog', function(role) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加角色'
        this.bizFormModel = role
        this.initOptions(this.bizFormModel)
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }
}
</script>
