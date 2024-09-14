<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='30%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='lesseeForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='租户名' prop='userName' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.userName' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入租户名"' autofocus></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='电话号码' prop='phoneNumber' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.phoneNumber' :maxlength='20' :placeholder='dialogProps.action == "view"? "" : "请输入电话号码"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='公司名称' prop='company' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.company' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入公司名称"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='联系人姓名' prop='contactName' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.contactName' :maxlength='50' :placeholder='dialogProps.action == "view"? "" : "请输入联系人姓名"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='地址（省）' prop='addressProvince' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.addressProvince' :maxlength='10' :placeholder='dialogProps.action == "view"? "" : "请输入地址（省）"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='地址（市）' prop='addressCity' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.addressCity' :maxlength='10' :placeholder='dialogProps.action == "view"? "" : "请输入地址（市）"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='地址（区）' prop='addressRegion' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.addressRegion' :maxlength='10' :placeholder='dialogProps.action == "view"? "" : "请输入地址（区）"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='联系地址' prop='address' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.address' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入联系地址"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='状态' prop='isUse' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isUse' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col>
          <el-form-item label='备注' prop='remarks' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.remarks' type='textarea'
                                  :maxlength='255' :placeholder='dialogProps.action == "view"? "" : "请输入备注"'  clearable></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("lesseeForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { saveLessee } from '@/api/org/lessee'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'lessee-form',
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
        'userName': [
            { required: true, message: '请输入租户名', trigger: 'blur' }
        ],
        'phoneNumber': [
            { required: true, message: '请输入电话号码', trigger: 'blur' }
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
      saveLessee(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改租户'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['lesseeForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'userName': '',   // 租户名
        'phoneNumber': '',   // 电话号码
        'company': '',   // 公司名称
        'contactName': '',   // 联系人姓名
        'addressProvince': '',   // 地址（省）
        'addressCity': '',   // 地址（市）
        'addressRegion': '',   // 地址（区）
        'address': '',   // 联系地址
        'isUse': '1',   // 状态
        'remarks': '',   // 备注

      }
    },
    initOptions(This) {
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewLesseeDialog', function(lessee) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看租户'
        this.bizFormModel = {...this.initFormModel(), ...lessee}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditLesseeDialog', function(lessee) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改租户'
        this.bizFormModel = {...this.initFormModel(), ...lessee}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddLesseeDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加租户'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyLesseeDialog', function(lessee) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加租户'
        this.bizFormModel = {...this.initFormModel(), ...lessee}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>