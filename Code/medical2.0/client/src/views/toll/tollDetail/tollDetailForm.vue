<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='tollDetailForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='处方' prop='recipel.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.recipel.name'></el-input>
            <el-select v-else v-model='bizFormModel.recipel' value-key='id' filterable clearable placeholder='请选择处方' 
              @clear='bizFormModel.recipel={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='recipel in recipel_List' :key='recipel.id' :label='recipel.name' :value='recipel'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='收费主表' prop='tollId.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.tollId.name'></el-input>
            <el-select v-else v-model='bizFormModel.tollId' value-key='id' filterable clearable placeholder='请选择收费主表' 
              @clear='bizFormModel.tollId={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='tollId in tollId_List' :key='tollId.id' :label='tollId.name' :value='tollId'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' type='primary' :plain='true' @click='onSubmit("tollDetailForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listRecipelInfoAll } from '@/api/outpatient/recipelInfo'
import { listTollInfoAll } from '@/api/toll/tollInfo'
import { saveTollDetail } from '@/api/toll/tollDetail'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'tollDetail-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
          recipel_List: [],  // 处方
          tollId_List: [],  // 收费主表
       dialogProps: {
        visible: false,
        action: '',
        title: ''
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
      saveTollDetail(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改收费明细管理'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['tollDetailForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'recipel': {     // 处方
          'id': null,
          'name': null,
        },
        'tollId': {     // 父表 收费主表
          'id': validatenull(This) || validatenull(This.tollId) ? null : This.tollId.id,
          'name': validatenull(This) || validatenull(This.tollId) ? null : This.tollId.name,
        },

      }
    },
    initOptions(This) {
      let recipel_search = {
        params: []
      }
        // 字段对应表上filter条件
        recipel_search.params.push.apply(recipel_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 处方信息recipel_info
      this.pushDataPermissions(recipel_search.params, this.$route.meta.routerId, '1014474470772900008')
      this.recipel_List.splice(0, this.recipel_List.length)
      listRecipelInfoAll(recipel_search).then(responseData => {
        this.recipel_List = responseData.data
      })
      let tollId_search = {
        params: []
      }
        // 字段对应表上filter条件
        tollId_search.params.push.apply(tollId_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 收费主表toll_info
      this.pushDataPermissions(tollId_search.params, this.$route.meta.routerId, '1024846610635145502')
      this.tollId_List.splice(0, this.tollId_List.length)
      listTollInfoAll(tollId_search).then(responseData => {
        this.tollId_List = responseData.data
      })
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewTollDetailDialog', function(tollDetail) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看收费明细管理'
        this.bizFormModel = {...this.initFormModel(), ...tollDetail}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditTollDetailDialog', function(tollDetail) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改收费明细管理'
        this.bizFormModel = {...this.initFormModel(), ...tollDetail}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddTollDetailDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加收费明细管理'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyTollDetailDialog', function(tollDetail) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加收费明细管理'
        this.bizFormModel = {...this.initFormModel(), ...tollDetail}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>