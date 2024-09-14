<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='30%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='administrativeDivisionForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='上级区划代码' prop='parten.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.parten.name'></el-input>
            <el-select v-else v-model='bizFormModel.parten' value-key='id' filterable clearable placeholder='请选择上级区划代码' 
              @clear='bizFormModel.parten={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='parten in parten_List' :key='parten.id' :label='parten.name' :value='parten'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='区划名称' prop='name' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='50' :placeholder='dialogProps.action == "view"? "" : "请输入区划名称"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='区划级别' prop='level' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.level'></el-input>
            <number-input v-else v-model="bizFormModel.level"  :precision="0"></number-input>
          </el-form-item>
        </el-col>
      </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("administrativeDivisionForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listAdministrativeDivisionAll } from '@/api/org/administrativeDivision'
import { saveAdministrativeDivision } from '@/api/org/administrativeDivision'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'administrativeDivision-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
       flage:false,
          parten_List: [],  // 上级区划代码
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
      saveAdministrativeDivision(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改行政区域划分'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['administrativeDivisionForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'parten': {     // 上级节点 上级区划代码
          'id': validatenull(This) || validatenull(This.id) ? null : This.id,
          'name': validatenull(This) || validatenull(This.name) ? null : This.name,
        },
        'name': '',   // 区划名称
        'level': 0,    // 区划级别

      }
    },
    initOptions(This) {
      let parten_search = {
        params: []
      }
      // 数据权限: 行政区划分administrative_division
      this.pushDataPermissions(parten_search.params, this.$route.meta.routerId, '986666876811870222')
      this.parten_List.splice(0, this.parten_List.length)
      listAdministrativeDivisionAll(parten_search).then(responseData => {
        this.parten_List = responseData.data
      })
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewAdministrativeDivisionDialog', function(administrativeDivision) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看行政区域划分'
        this.bizFormModel = {...this.initFormModel(), ...administrativeDivision}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditAdministrativeDivisionDialog', function(administrativeDivision) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改行政区域划分'
        this.bizFormModel = {...this.initFormModel(), ...administrativeDivision}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddAdministrativeDivisionDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加行政区域划分'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyAdministrativeDivisionDialog', function(administrativeDivision) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加行政区域划分'
        this.bizFormModel = {...this.initFormModel(), ...administrativeDivision}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>