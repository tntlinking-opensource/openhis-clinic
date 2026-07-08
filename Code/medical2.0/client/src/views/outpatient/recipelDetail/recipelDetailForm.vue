<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action === "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='recipelDetailForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex==="1"'>  
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='处方' prop='recipelInfo.id' >
            <el-input v-if='dialogProps.action === "view"' :disabled='true' v-model='bizFormModel.recipelInfo.remarks'></el-input>
            <el-select v-else v-model='bizFormModel.recipelInfo' value-key='id' filterable clearable placeholder='请选择处方' 
              @clear='bizFormModel.recipelInfo={
                "id": null,
                                "remarks": null,
                }'>
             <el-option v-for='recipelInfo in recipelInfo_List' :key='recipelInfo.id' :label='recipelInfo.remarks' :value='recipelInfo'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='药品材料id' prop='drugStuffId' >
            <el-input :disabled='dialogProps.action === "view"' v-model='bizFormModel.drugStuffId' :maxlength='21' :placeholder='dialogProps.action === "view"? "" : "请输入药品材料id"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='单次用量' prop='singleDosage' >
            <el-input v-if='dialogProps.action === "view"' :disabled='true' v-model='bizFormModel.singleDosage'></el-input>
            <number-input v-else v-model="bizFormModel.singleDosage"  :precision="0"></number-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='总量' prop='total' >
            <el-input v-if='dialogProps.action === "view"' :disabled='true' v-model='bizFormModel.total'></el-input>
            <number-input v-else v-model="bizFormModel.total"  :precision="0"></number-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='总价' prop='allFee' >
            <el-input v-if='dialogProps.action === "view"' :disabled='true' v-model='bizFormModel.allFee'></el-input>
            <number-input v-else v-model="bizFormModel.allFee" currency='CNY' :precision="2"></number-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='西药用法' prop='westernMedicineUse.value' >
            <el-input v-if='dialogProps.action === "view"' :disabled='true' v-model='bizFormModel.westernMedicineUse.name'></el-input>
            <el-select v-else v-model='bizFormModel.westernMedicineUse' value-key='value' filterable clearable placeholder='请选择西药用法' 
              @clear='bizFormModel.westernMedicineUse={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='westernMedicineUse in westernMedicineUse_List' :key='westernMedicineUse.value' :label='westernMedicineUse.name' :value='westernMedicineUse'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='频次用量' prop='frequency.value' >
            <el-input v-if='dialogProps.action === "view"' :disabled='true' v-model='bizFormModel.frequency.name'></el-input>
            <el-select v-else v-model='bizFormModel.frequency' value-key='value' filterable clearable placeholder='请选择频次用量' 
              @clear='bizFormModel.frequency={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='frequency in frequency_List' :key='frequency.value' :label='frequency.name' :value='frequency'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='天数' prop='days.value' >
            <el-input v-if='dialogProps.action === "view"' :disabled='true' v-model='bizFormModel.days.name'></el-input>
            <el-select v-else v-model='bizFormModel.days' value-key='value' filterable clearable placeholder='请选择天数' 
              @clear='bizFormModel.days={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='days in days_List' :key='days.value' :label='days.name' :value='days'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='中药用法' prop='chineseMedicineUse.value' >
            <el-input v-if='dialogProps.action === "view"' :disabled='true' v-model='bizFormModel.chineseMedicineUse.name'></el-input>
            <el-select v-else v-model='bizFormModel.chineseMedicineUse' value-key='value' filterable clearable placeholder='请选择中药用法' 
              @clear='bizFormModel.chineseMedicineUse={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='chineseMedicineUse in chineseMedicineUse_List' :key='chineseMedicineUse.value' :label='chineseMedicineUse.name' :value='chineseMedicineUse'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='输液用法' prop='infuseUse.value' >
            <el-input v-if='dialogProps.action === "view"' :disabled='true' v-model='bizFormModel.infuseUse.name'></el-input>
            <el-select v-else v-model='bizFormModel.infuseUse' value-key='value' filterable clearable placeholder='请选择输液用法' 
              @clear='bizFormModel.infuseUse={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='infuseUse in infuseUse_List' :key='infuseUse.value' :label='infuseUse.name' :value='infuseUse'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='滴速' prop='drippingSpeed' >
            <el-input :disabled='dialogProps.action === "view"' v-model='bizFormModel.drippingSpeed' :maxlength='45' :placeholder='dialogProps.action === "view"? "" : "请输入滴速"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='皮试' prop='skinTest.value' >
            <el-input v-if='dialogProps.action === "view"' :disabled='true' v-model='bizFormModel.skinTest.name'></el-input>
            <el-select v-else v-model='bizFormModel.skinTest' value-key='value' filterable clearable placeholder='请选择皮试' 
              @clear='bizFormModel.skinTest={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='skinTest in skinTest_List' :key='skinTest.value' :label='skinTest.name' :value='skinTest'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='组数' prop='infuseGroup' >
            <el-input v-if='dialogProps.action === "view"' :disabled='true' v-model='bizFormModel.infuseGroup'></el-input>
            <number-input v-else v-model="bizFormModel.infuseGroup"  :precision="0"></number-input>
          </el-form-item>
        </el-col>
    </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action !== "view"' :disabled="flag" type='primary' :plain='true' @click='onSubmit("recipelDetailForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action !== "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action === "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listRecipelInfoAll } from '@/api/outpatient/recipelInfo'
import { getDictItemsByCode, DICT_CODE } from '@/utils/dictCache'
import { saveRecipelDetail } from '@/api/outpatient/recipelDetail'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'recipelDetail-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
          recipelInfo_List: [],  // 处方
          westernMedicineUse_List: [],  // 西药用法
          frequency_List: [],  // 频次用量
          days_List: [],  // 天数
          chineseMedicineUse_List: [],  // 中药用法
          infuseUse_List: [],  // 输液用法
          skinTest_List: [],  // 皮试
          flag:false,
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
      saveRecipelDetail(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改处方详情'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['recipelDetailForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': currentUser.company.id,
          'name': null,
        },
        'recipelInfo': {     // 父表 处方
          'id': validatenull(This) || validatenull(This.recipelInfo) ? null : This.recipelInfo.id,
          'remarks': validatenull(This) || validatenull(This.recipelInfo) ? null : This.recipelInfo.remarks,
        },
        'drugStuffId': '',   // 药品材料id
        'singleDosage': 0,    // 单次用量
        'total': 0,    // 总量
        'allFee': 0,    // 总价
        'westernMedicineUse': {     // 西药用法
          'value': null,
          'name': null,
        },
        'frequency': {     // 频次用量
          'value': null,
          'name': null,
        },
        'days': {     // 天数
          'value': null,
          'name': null,
        },
        'chineseMedicineUse': {     // 中药用法
          'value': null,
          'name': null,
        },
        'infuseUse': {     // 输液用法
          'value': null,
          'name': null,
        },
        'drippingSpeed': '',   // 滴速
        'skinTest': {     // 皮试
          'value': null,
          'name': null,
        },
        'infuseGroup': 0,    // 组数
        'remarks': '',   // 备注信息

      }
    },
    initOptions(This) {
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
      getDictItemsByCode(DICT_CODE.WESTERN_MEDICINE_USE).then((data) => {
        this.westernMedicineUse_List = data;
      });
      getDictItemsByCode(DICT_CODE.RECIPEL_DETAIL_FREQUENCY).then((data) => {
        this.frequency_List = data;
      });
      getDictItemsByCode(DICT_CODE.RECIPEL_DETAIL_DAYS).then((data) => {
        this.days_List = data;
      });
      getDictItemsByCode(DICT_CODE.CHINESE_MEDICINE_USE).then((data) => {
        this.chineseMedicineUse_List = data;
      });
      getDictItemsByCode(DICT_CODE.INFUSE_USE).then((data) => {
        this.infuseUse_List = data;
      });
      getDictItemsByCode(DICT_CODE.SKIN_TEST).then((data) => {
        this.skinTest_List = data;
      });
    }
  },
  watch: {
  },
  mounted: function() {
  },
  methods: {
    openViewRecipelDetailDialog(recipelDetail) {
      this.dialogProps.action = 'view'
      this.dialogProps.title = '查看处方详情'
      this.bizFormModel = {...this.initFormModel(), ...recipelDetail}
      this.initOptions(this.bizFormModel)
      this.tabIndex = '1'
      this.dialogProps.visible = true
    },
    openEditRecipelDetailDialog(recipelDetail) {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改处方详情'
      this.bizFormModel = {...this.initFormModel(), ...recipelDetail}
      this.initOptions(this.bizFormModel)
      this.tabIndex = '1'
      this.dialogProps.visible = true
    },
    openAddRecipelDetailDialog() {
      this.dialogProps.action = 'add'
      this.dialogProps.title = '添加处方详情'
      this.bizFormModel = this.initFormModel()
      this.initOptions(this.bizFormModel)
      this.tabIndex = '1'
      this.dialogProps.visible = true
    },
    openCopyRecipelDetailDialog(recipelDetail) {
      this.dialogProps.action = 'add'
      this.dialogProps.title = '添加处方详情'
      this.bizFormModel = {...this.initFormModel(), ...recipelDetail}
      this.initOptions(this.bizFormModel)
      this.tabIndex = '1'
      this.bizFormModel.id = null
      this.dialogProps.visible = true
    },
  },
}
</script>