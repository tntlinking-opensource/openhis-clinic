<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='supplierForm' label-width='120px' label-position='right' class='edit-form' style="marginTop: 10px">  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='名称' prop='name' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='64' :placeholder='dialogProps.action == "view"? "" : "请输入名称"' autofocus @input="pinyinInput"></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='联系人' prop='linkman' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.linkman' :maxlength='45' :placeholder='dialogProps.action == "view"? "" : "请输入联系人"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
           <el-col :span='24/2'>
          <el-form-item label='拼音码' prop='pinyinCode' >
            <el-input disabled=true v-model='bizFormModel.pinyinCode' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入拼音码"' ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span='24/2'>
          <el-form-item label='电话' prop='phone' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.phone' :maxlength='45' :placeholder='dialogProps.action == "view"? "" : "请输入电话"' ></el-input>
          </el-form-item>
        </el-col>
               
      </el-row>
              <el-row>
        <el-col :span='24/2'>
           <el-form-item label='信用代码' prop='creditCode' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.creditCode' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入信用代码"' ></el-input>
          </el-form-item>
        </el-col>
         <el-col :span='24/2'>
          <el-form-item label='邮箱' prop='mail' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.mail' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入邮箱"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
         <el-col :span='24/1'>
              <el-form-item label='是否启用' prop='status' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='value' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
         </el-col>
      </el-row>
              <el-row>
     
          <el-form-item label='所在地区' prop='' >
             <v-distpicker :disabled='dialogProps.action == "view"' :province="province" :city="city" :area="area"  @province="onChangeProvince" @city="onChangeCity" @area="onChangeArea"></v-distpicker>
          </el-form-item>
      </el-row>
      <el-row>
        <el-col :span="24/1">
        <el-form-item label='供应商分类' prop='type'>
          <div>
            <el-radio :disabled='dialogProps.action == "view"' v-model="bizFormModel.type" label="1" border>药品供应商</el-radio>
          <el-radio :disabled='dialogProps.action == "view"' v-model="bizFormModel.type" label="2" border>材料供应商</el-radio>
          </div>
        </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col>
          <el-form-item label='地址' prop='address' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.address' type='textarea'
                                  :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入地址"'  clearable></el-input>
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
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("supplierForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { saveSupplier } from '@/api/stock/supplier'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
import VDistpicker from 'v-distpicker'

let pinyin = require('js-pinyin');
export default {
  extends: BaseUI,
  name: 'supplier-form',
  components: {
    OperationIcon,
    VDistpicker
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
      //省市区三级联动
      province : '',
      city : '',
      area : '',
      value:'1',
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
        type: [
            { required: true, message: '请选择供应商类型', trigger: 'change' }
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
      this.bizFormModel.province=this.province
      this.bizFormModel.city=this.city
      this.bizFormModel.area=this.area
      this.bizFormModel.status=this.value
      saveSupplier(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改供应商管理'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['supplierForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': currentUser.company.id,
          'name': null,
        },
        'name': '',   // 名称
        'linkman': '',   // 联系人
        'phone': '',   // 电话
        'mail': '',   // 邮箱
        'province': '',   // 省
        'city': '',   // 市
        'creditCode': '',   // 信用代码
        'area': '',   // 区
        'address': '',   // 地址
        'remarks': '',   // 备注信息

      }
    },
    initOptions(This) {
    },
    // 获取到省市区三级联动的值
    onChangeProvince(data) {
        this.province=data.value
       
    },
    onChangeCity(data){
      this.city=data.value
    
    },
    onChangeArea(data){
      this.area=data.value
     
    },
     //输入框拼音码
     pinyinInput(value){
       this.bizFormModel.pinyinCode=pinyin.getCamelChars(value)
       
     },
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewSupplierDialog', function(supplier) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看供应商管理'
        this.bizFormModel = {...this.initFormModel(), ...supplier}
        this.initOptions(this.bizFormModel)
        this.value=this.bizFormModel.status
        this.tabIndex = '1'
        this.dialogProps.visible = true
         this.province=this.bizFormModel.province
        this.city=this.bizFormModel.city
        this.area=this.bizFormModel.area
      })
      this.$on('openEditSupplierDialog', function(supplier) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改供应商管理'
        this.bizFormModel = {...this.initFormModel(), ...supplier}
        this.initOptions(this.bizFormModel)
        this.value=this.bizFormModel.status
        this.tabIndex = '1'
        this.dialogProps.visible = true
        this.province=this.bizFormModel.province
        this.city=this.bizFormModel.city
        this.area=this.bizFormModel.area
      })
      this.$on('openAddSupplierDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加供应商管理'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
        this.province=''
        this.city=''
        this.area=''
      })
      this.$on('openCopySupplierDialog', function(supplier) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加供应商管理'
        this.bizFormModel = {...this.initFormModel(), ...supplier}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>
<style lang="scss">
  .distpicker-address-wrapper {
  select {
    padding: 0px 10px !important;
    height: 30px !important;
    font-size: 15px !important;
    line-height: 30px !important;
  }
}
</style>