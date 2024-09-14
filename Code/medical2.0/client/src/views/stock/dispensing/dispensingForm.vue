<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='dispensingForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='诊所id' prop='company.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.company.name'></el-input>
            <el-select v-else v-model='bizFormModel.company' value-key='id' filterable clearable placeholder='请选择诊所id' 
              @clear='bizFormModel.company={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='company in company_List' :key='company.id' :label='company.name' :value='company'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='挂号id' prop='registration.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.registration.id'></el-input>
            <el-select v-else v-model='bizFormModel.registration' value-key='id' filterable clearable placeholder='请选择挂号id' 
              @clear='bizFormModel.registration={
                "id": null,
                }'>
             <el-option v-for='registration in registration_List' :key='registration.id' :label='registration.id' :value='registration'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='库存id' prop='supplierStock.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.supplierStock.id'></el-input>
            <el-select v-else v-model='bizFormModel.supplierStock' value-key='id' filterable clearable placeholder='请选择库存id' 
              @clear='bizFormModel.supplierStock={
                "id": null,
                }'>
             <el-option v-for='supplierStock in supplierStock_List' :key='supplierStock.id' :label='supplierStock.id' :value='supplierStock'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='处方信息id' prop='recipelInfo.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.recipelInfo.name'></el-input>
            <el-select v-else v-model='bizFormModel.recipelInfo' value-key='id' filterable clearable placeholder='请选择处方信息id' 
              @clear='bizFormModel.recipelInfo={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='recipelInfo in recipelInfo_List' :key='recipelInfo.id' :label='recipelInfo.name' :value='recipelInfo'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='数量' prop='number' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.number'></el-input>
            <number-input v-else v-model="bizFormModel.number"  :precision="0"></number-input>
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
      <el-button v-if='dialogProps.action != "view"' type='primary' :plain='true' @click='onSubmit("dispensingForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listCompanyAll } from '@/api/org/company'
import { listRegistrationAll } from '@/api/outpatient/registration'
import { listSupplierStockAll } from '@/api/stock/supplierStock'
import { listRecipelInfoAll } from '@/api/outpatient/recipelInfo'
import { saveDispensing } from '@/api/stock/dispensing'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'dispensing-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
          company_List: [],  // 诊所id
          registration_List: [],  // 挂号id
          supplierStock_List: [],  // 库存id
          recipelInfo_List: [],  // 处方信息id
       dialogProps: {
        visible: false,
        action: '',
        title: ''
      },
      formRules: {
        'registration.id': [
            { required: true, message: '请选择挂号id', trigger: 'change' }
        ],
        'supplierStock.id': [
            { required: true, message: '请选择库存id', trigger: 'change' }
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
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave()
        } else {
          return false
        }
      });
    },
    doSave() {
      this.setLoad()
      saveDispensing(this.bizFormModel).then(responseData => {
        if(responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改发药明细'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['dispensingForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': currentUser.company.id,
          'name': null,
        },
        'registration': {     // 挂号id
          'id': null,
        },
        'supplierStock': {     // 库存id
          'id': null,
        },
        'recipelInfo': {     // 处方信息id
          'id': null,
          'name': null,
        },
        'number': 0,    // 数量
        'remarks': '',   // 备注信息

      }
    },
    initOptions(This) {
      let company_search = {
        params: []
      }
        // 字段对应表上filter条件
        company_search.params.push.apply(company_search.params, [])
      // 数据权限: 诊所org_company
      this.pushDataPermissions(company_search.params, this.$route.meta.routerId, '41040096140492800')
      this.company_List.splice(0, this.company_List.length)
      listCompanyAll(company_search).then(responseData => {
        this.company_List = responseData.data
        // 获取初始项的值
        this.company_List.forEach( item => {
          if(item.id == this.bizFormModel.company.id) {
            this.bizFormModel.company = item
            return
          }
        })
      })
      let registration_search = {
        params: []
      }
        // 字段对应表上filter条件
        registration_search.params.push.apply(registration_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 挂号表registration
      this.pushDataPermissions(registration_search.params, this.$route.meta.routerId, '1008534118685450402')
      this.registration_List.splice(0, this.registration_List.length)
      listRegistrationAll(registration_search).then(responseData => {
        this.registration_List = responseData.data
      })
      let supplierStock_search = {
        params: []
      }
        // 字段对应表上filter条件
        supplierStock_search.params.push.apply(supplierStock_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 供应商库存supplier_stock
      this.pushDataPermissions(supplierStock_search.params, this.$route.meta.routerId, '1005591224273698934')
      this.supplierStock_List.splice(0, this.supplierStock_List.length)
      listSupplierStockAll(supplierStock_search).then(responseData => {
        this.supplierStock_List = responseData.data
      })
      let recipelInfo_search = {
        params: []
      }
        // 字段对应表上filter条件
        recipelInfo_search.params.push.apply(recipelInfo_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 处方信息recipel_info
      this.pushDataPermissions(recipelInfo_search.params, this.$route.meta.routerId, '1014474470772900008')
      this.recipelInfo_List.splice(0, this.recipelInfo_List.length)
      listRecipelInfoAll(recipelInfo_search).then(responseData => {
        this.recipelInfo_List = responseData.data
      })
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewDispensingDialog', function(dispensing) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看发药明细'
        this.bizFormModel = {...this.initFormModel(), ...dispensing}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditDispensingDialog', function(dispensing) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改发药明细'
        this.bizFormModel = {...this.initFormModel(), ...dispensing}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddDispensingDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加发药明细'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyDispensingDialog', function(dispensing) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加发药明细'
        this.bizFormModel = {...this.initFormModel(), ...dispensing}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>