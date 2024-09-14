<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='supplierStockForm' label-width='120px' label-position='right' class='edit-form'>  
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
          <el-form-item label='供应商' prop='supplierId.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.supplierId.name'></el-input>
            <el-select v-else v-model='bizFormModel.supplierId' value-key='id' filterable clearable placeholder='请选择供应商' 
              @clear='bizFormModel.supplierId={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='supplierId in supplierId_List' :key='supplierId.id' :label='supplierId.name' :value='supplierId'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='药品名称' prop='drug.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.drug.goodsName'></el-input>
            <el-select v-else v-model='bizFormModel.drug' value-key='id' filterable clearable placeholder='请选择药品名称' 
              @clear='bizFormModel.drug={
                "id": null,
                                "goodsName": null,
                }'>
             <el-option v-for='drug in drug_List' :key='drug.id' :label='drug.goodsName' :value='drug'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='入库单id' prop='supplierStorage.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.supplierStorage.code'></el-input>
            <el-select v-else v-model='bizFormModel.supplierStorage' value-key='id' filterable clearable placeholder='请选择入库单id' 
              @clear='bizFormModel.supplierStorage={
                "id": null,
                                "code": null,
                }'>
             <el-option v-for='supplierStorage in supplierStorage_List' :key='supplierStorage.id' :label='supplierStorage.code' :value='supplierStorage'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='材料名称' prop='stuff.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.stuff.name'></el-input>
            <el-select v-else v-model='bizFormModel.stuff' value-key='id' filterable clearable placeholder='请选择材料名称' 
              @clear='bizFormModel.stuff={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='stuff in stuff_List' :key='stuff.id' :label='stuff.name' :value='stuff'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='规格' prop='norms' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.norms' :maxlength='64' :placeholder='dialogProps.action == "view"? "" : "请输入规格"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='厂家' prop='factory.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.factory.name'></el-input>
            <el-select v-else v-model='bizFormModel.factory' value-key='id' filterable clearable placeholder='请选择厂家' 
              @clear='bizFormModel.factory={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='factory in factory_List' :key='factory.id' :label='factory.name' :value='factory'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='批号' prop='batchNo' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.batchNo' :maxlength='45' :placeholder='dialogProps.action == "view"? "" : "请输入批号"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='生产日期' prop='produceDate' >
            <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.produceDate' type='datetime' value-format='yyyy-MM-dd HH:mm:ss' :placeholder='dialogProps.action == "view"? "" : "请输入生产日期"' ></el-date-picker>             
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='有效期' prop='endDate' >
            <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.endDate' type='datetime' value-format='yyyy-MM-dd HH:mm:ss' :placeholder='dialogProps.action == "view"? "" : "请输入有效期"' ></el-date-picker>             
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
                <el-col :span='24/2'>
          <el-form-item label='进价' prop='bid' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.bid'></el-input>
            <number-input v-else v-model="bizFormModel.bid" currency='CNY' :precision="2"></number-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='零售价' prop='retailPrice' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.retailPrice'></el-input>
            <number-input v-else v-model="bizFormModel.retailPrice" currency='CNY' :precision="2"></number-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='总进价' prop='allBid' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.allBid'></el-input>
            <number-input v-else v-model="bizFormModel.allBid" currency='CNY' :precision="2"></number-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='总零售价' prop='allRetailPrice' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.allRetailPrice'></el-input>
            <number-input v-else v-model="bizFormModel.allRetailPrice" currency='CNY' :precision="2"></number-input>
          </el-form-item>
        </el-col>
    </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("supplierStockForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listCompanyAll } from '@/api/org/company'
import { listSupplierAll } from '@/api/stock/supplier'
import { listDrugAll } from '@/api/stock/drug'
import { listSupplierStorageAll } from '@/api/stock/supplierStorage'
import { listStuffAll } from '@/api/stock/stuff'
import { listManufactureFactoryAll } from '@/api/basicdata/manufactureFactory'
import { saveSupplierStock } from '@/api/stock/supplierStock'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'supplierStock-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
          company_List: [],  // 诊所id
          supplierId_List: [],  // 供应商
          drug_List: [],  // 药品名称
          supplierStorage_List: [],  // 入库单id
          stuff_List: [],  // 材料名称
          factory_List: [],  // 厂家
          flage:false,
       dialogProps: {
        visible: false,
        action: '',
        title: '',
        
      },
      formRules: {
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
      saveSupplierStock(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改供应商库存'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['supplierStockForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': null,
          'name': null,
        },
        'supplierId': {     // 供应商
          'id': null,
          'name': null,
        },
        'drug': {     // 药品名称
          'id': null,
          'goodsName': null,
        },
        'supplierStorage': {     // 父表 入库单id
          'id': validatenull(This) || validatenull(This.supplierStorage) ? null : This.supplierStorage.id,
          'code': validatenull(This) || validatenull(This.supplierStorage) ? null : This.supplierStorage.code,
        },
        'stuff': {     // 材料名称
          'id': null,
          'name': null,
        },
        'norms': '',   // 规格
        'factory': {     // 厂家
          'id': null,
          'name': null,
        },
        'batchNo': '',   // 批号
        'produceDate': '',   // 生产日期
        'endDate': '',   // 有效期
        'number': 0,    // 数量
        'bid': 0,    // 进价
        'retailPrice': 0,    // 零售价
        'allBid': 0,    // 总进价
        'allRetailPrice': 0,    // 总零售价
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
      })
      let supplierId_search = {
        params: []
      }
        // 字段对应表上filter条件
        supplierId_search.params.push.apply(supplierId_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 供应商supplier
      this.pushDataPermissions(supplierId_search.params, this.$route.meta.routerId, '1005526731044757538')
      this.supplierId_List.splice(0, this.supplierId_List.length)
      listSupplierAll(supplierId_search).then(responseData => {
        this.supplierId_List = responseData.data
      })
      let drug_search = {
        params: []
      }
        // 字段对应表上filter条件
        drug_search.params.push.apply(drug_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 药品信息drug
      this.pushDataPermissions(drug_search.params, this.$route.meta.routerId, '1004078055755374623')
      this.drug_List.splice(0, this.drug_List.length)
      listDrugAll(drug_search).then(responseData => {
        this.drug_List = responseData.data
      })
      let supplierStorage_search = {
        params: []
      }
        // 字段对应表上filter条件
        supplierStorage_search.params.push.apply(supplierStorage_search.params, [{columnName: 'company_id', queryType: '=', value: function() {var user = JSON.parse(sessionStorage.getItem('currentUser')); return user.company.id;}()}])
      // 数据权限: 入库单supplier_storage
      this.pushDataPermissions(supplierStorage_search.params, this.$route.meta.routerId, '1007238052174135324')
      this.supplierStorage_List.splice(0, this.supplierStorage_List.length)
      listSupplierStorageAll(supplierStorage_search).then(responseData => {
        this.supplierStorage_List = responseData.data
      })
      let stuff_search = {
        params: []
      }
        // 字段对应表上filter条件
        stuff_search.params.push.apply(stuff_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 材料stuff
      this.pushDataPermissions(stuff_search.params, this.$route.meta.routerId, '1004462867645374480')
      this.stuff_List.splice(0, this.stuff_List.length)
      listStuffAll(stuff_search).then(responseData => {
        this.stuff_List = responseData.data
      })
      let factory_search = {
        params: []
      }
        // 字段对应表上filter条件
        factory_search.params.push.apply(factory_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 生产厂家manufacture_factory
      this.pushDataPermissions(factory_search.params, this.$route.meta.routerId, '1016206064147988493')
      this.factory_List.splice(0, this.factory_List.length)
      listManufactureFactoryAll(factory_search).then(responseData => {
        this.factory_List = responseData.data
      })
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewSupplierStockDialog', function(supplierStock) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看供应商库存'
        this.bizFormModel = {...this.initFormModel(), ...supplierStock}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditSupplierStockDialog', function(supplierStock) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改供应商库存'
        this.bizFormModel = {...this.initFormModel(), ...supplierStock}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddSupplierStockDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加供应商库存'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopySupplierStockDialog', function(supplierStock) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加供应商库存'
        this.bizFormModel = {...this.initFormModel(), ...supplierStock}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>