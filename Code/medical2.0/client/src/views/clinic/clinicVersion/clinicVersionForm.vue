<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='30%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='clinicVersionForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='版本名称' prop='name' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='50' :placeholder='dialogProps.action == "view"? "" : "请输入版本名称"' autofocus></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='版本编号' prop='code' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.code' :maxlength='50' :placeholder='dialogProps.action == "view"? "" : "请输入版本编码"' autofocus></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='版本价格' prop='price' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.price'></el-input>
            <number-input v-else v-model="bizFormModel.price" currency='CNY' :precision="2"></number-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='版本描述' prop='description' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.description' :maxlength='200' :placeholder='dialogProps.action == "view"? "" : "请输入版本描述"' ></el-input>
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
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("clinicVersionForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { saveClinicVersion } from '@/api/clinic/clinicVersion'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'clinicVersion-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
      flage:false,
       dialogProps: {
        visible: false,
        action: '',
        title: '',
        
      },
      formRules: {
        'name': [
            { required: true, message: '请输入版本名称', trigger: 'blur' }
        ],
        'price': [
            { required: true, message: '请选择版本价格', trigger: 'blur' }
        ],
        'code': [
            { required: true, message: '请输入版本编号', trigger: 'blur' }
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
        //debugger
        if (valid) {
          //debugger
          this.doSave()
        } else {
          this.flage=false
          return false
        }
      });
    },
    doSave() {
      this.setLoad()
     // debugger
      saveClinicVersion(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改诊所版本'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['clinicVersionForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'name': '',   // 版本名称
        'code': '',   // 版本编号
        'price': 0,    // 版本价格
        'description': '',   // 版本描述
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
      this.$on('openViewClinicVersionDialog', function(clinicVersion) {
       // debugger
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看诊所版本'
        this.bizFormModel = {...this.initFormModel(), ...clinicVersion}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditClinicVersionDialog', function(clinicVersion) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改诊所版本'
        this.bizFormModel = {...this.initFormModel(), ...clinicVersion}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddClinicVersionDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加诊所版本'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyClinicVersionDialog', function(clinicVersion) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加诊所版本'
        this.bizFormModel = {...this.initFormModel(), ...clinicVersion}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>