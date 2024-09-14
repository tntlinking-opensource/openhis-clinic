<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :append-to-body="true" :close-on-click-modal='false' width='40%'
      @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view"' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules' ref='addTodayWorkForm' label-width='120px' label-position='right' class='edit-form'>
      <div class="tab-item">
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='标题' prop='name' >
              <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入设备名称"' ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span='24/1'>
            <el-form-item label='全天' prop='allDay' >
              <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.allDay' active-color='#13ce66' inactive-color='#dbdfe6' active-value='0' inactive-value='1'></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='开始' prop='fromDate'>
               <el-date-picker :disabled='dialogProps.action == "view"' v-model="bizFormModel.fromDate" type="datetime" placeholder="选择开始日期" align="right" default-time="10:00:00"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span='24/1'>
            <el-form-item label='结束' prop='toDate' >
               <el-date-picker :disabled='dialogProps.action == "view"' v-model="bizFormModel.toDate" type="datetime" placeholder="选择结束日期" align="right" default-time="10:00:00"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
       
        <el-row>
          <el-col>
            <el-form-item label='备注信息' prop='remarks' >
              <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.remarks' type='textarea' autosize
                  :rows="3" :maxlength='255' :placeholder='dialogProps.action == "view"? "" : "请输入备注信息"'  clearable></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("addTodayWorkForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
  </el-dialog>
</template>
<script>
  import { validatenull } from '@/utils/validate'
  import BaseUI from '@/views/components/baseUI'
  import OperationIcon from '@/components/OperationIcon'
  import propertyCustom from  '@/views/sys/propertySet/components/propertyCustom'
  export default {
    extends: BaseUI,
    name: 'addTodayWork-form',
    components: {
      OperationIcon,
      propertyCustom
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
            { required: true, message: '请输入设备名称', trigger: 'blur' }
          ],
        }
      }
    },
    props: {
    },
    created(){
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
        this.dialogProps.visible = false
        this.flage=false
      },
      switchEdit() {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改日程'
        this.initOptions(this.bizFormModel)
      },
      onDialogClose() {
        this.dialogProps.visible = false
      },
      onDialogOpen() {
        this.$nextTick(() => {
          this.$refs['addTodayWorkForm'].clearValidate()
        })
      },
      initFormModel(This) {
        return {
          'name': '',   // 设备名称
          'remarks': '',   // 备注信息
          'allDay': '0', // 是否全天  0是 1否
          'fromDate': null,
          'toDade': null
        }
      },
      initOptions(This) {
      },
    },
    mounted: function() {
      this.$nextTick(() => {
        this.$on('openAddGroupDeviceDialog', function(parent, type) {
          this.dialogProps.action = 'add'
          this.dialogProps.title = '添加日程'
          this.bizFormModel = this.initFormModel(parent)
          this.initOptions(this.bizFormModel)
          this.dialogProps.visible = true
        })
      })
    }
  }
</script>
