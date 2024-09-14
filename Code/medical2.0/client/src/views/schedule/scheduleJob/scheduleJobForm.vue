<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='30%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules' 
      ref='scheduleJobForm' label-width='120px' label-position='right' class='edit-form'>    

              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='任务名称' prop='name' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入任务名称"' autofocus></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='任务编号' prop='code' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.code' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入任务编号"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='任务表达式' prop='cron' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.cron' :maxlength='30' :placeholder='dialogProps.action == "view"? "" : "请输入任务表达式"' >
              <template slot="append">
                <el-button type="primary" @click="handleShowCron">
                  生成表达式
                  <i class="el-icon-time el-icon--right"></i>
                </el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='任务状态' prop='status' >
            <el-input disabled  v-model='bizFormModel.status' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入任务状态"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='是否禁用' prop='isLock' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isLock' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
                    <el-row>
                <el-col>
                    <el-form-item label='备注' prop='remarks' >
                        <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.remarks' type='textarea'
                                  :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入备注"'  clearable></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-dropdown v-if='dialogProps.action == "edit"' split-button type='warning' @click='startTask("scheduleJobForm")'>
        启动任务
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item><el-button  type='text' :plain='true' @click='restartTask("scheduleJobForm")'>保存并启动</el-button></el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
       <el-button v-if='dialogProps.action == "edit"' type='primary' :plain='true' @click='endTask("scheduleJobForm")'>暂 停</el-button>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("scheduleJobForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
    <el-dialog title="Cron表达式生成器" width="80%" :visible.sync="openCron" append-to-body destroy-on-close class="scrollbar">
      <crontab @hide="openCron=false" @fill="crontabFill" :expression="expression"></crontab>
    </el-dialog>
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { saveScheduleJob,startTask,endTask,restartTask } from '@/api/schedule/scheduleJob'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
import Crontab from '@/components/Crontab'

export default {
  extends: BaseUI,
  name: 'scheduleJob-form',
  components: {
    OperationIcon,Crontab
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
      openCron: false,
      // 传入的表达式
      expression: "",
      formRules: {
        'name': [
            { required: true, message: '请输入任务名称', trigger: 'blur' }
        ],
        'code': [
            { required: true, message: '请输入任务编号', trigger: 'blur' }
        ],
        'cron': [
            { required: true, message: '请输入任务表达式', trigger: 'blur' }
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
    /** cron表达式按钮操作 */
    handleShowCron() {
      this.expression = this.bizFormModel.cron;
      this.openCron = true;
    },
    /** 确定后回传值 */
    crontabFill(value) {
      this.bizFormModel.cron = value;
    },

    //取消任务
    endTask(formName){
      this.$refs[formName].validate(valid => {
        if (valid) {
          if (this.bizFormModel.status=='已暂停') {
            this.$message.warning("当前任务暂停状态不可重复暂停");
            return  false;
          }
          this.setLoad()
          endTask(this.bizFormModel).then(responseData => {
            if(responseData.code == 100) {
              this.dialogProps.visible = false
              this.$emit('save-finished')
              this.$message.success(responseData.data)
            } else {
              this.showMessage(responseData)
            }
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)
          })
        } else {
          return false
        }
      });
    },
    //启动定时任务
    startTask(formName){
      this.$refs[formName].validate(valid => {
        if (valid) {
          if (this.bizFormModel.status=='执行中') {
              this.$message.warning("当前任务执行状态不可重复启动");
              return  false;
          }
          this.setLoad()
             startTask(this.bizFormModel).then(responseData => {
              if(responseData.code == 100) {
                this.dialogProps.visible = false
                this.$emit('save-finished')
                this.$message.success(responseData.data)
              } else {
                this.showMessage(responseData)
              }
              this.resetLoad()
            }).catch(error => {
              this.outputError(error)
            })
        } else {
          return false
        }
      });
    },

    //保存并重启任务
    restartTask(formName){
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.setLoad()
          saveScheduleJob(this.bizFormModel).then(responseData => {
              if(responseData.code == 100) {
                //任务重启
                  restartTask(this.bizFormModel).then(responseData => {
                    if(responseData.code == 100) {
                      this.dialogProps.visible = false
                      this.$emit('save-finished')
                      this.$message.success(responseData.data)
                    } else {
                      this.showMessage(responseData)
                    }
                    this.resetLoad()
                  }).catch(error => {
                    this.outputError(error)
                  })
              } else {
                this.showMessage(responseData)
              }
              this.resetLoad()
            }).catch(error => {
               this.outputError(error)
          })
        } else {return false}
      });
    },




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
      saveScheduleJob(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改定时任务管理'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['scheduleJobForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'name': '',   // 任务名称
        'code': '',   // 任务编号
        'cron': '',   // 任务表达式
        'status': '',   // 任务状态
        'isLock': '',   // 是否禁用
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
      this.$on('openViewScheduleJobDialog', function(scheduleJob) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看定时任务管理'
        this.bizFormModel = scheduleJob
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openEditScheduleJobDialog', function(scheduleJob) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改定时任务管理'
        this.bizFormModel = scheduleJob
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openAddScheduleJobDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加定时任务管理'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openCopyScheduleJobDialog', function(scheduleJob) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加定时任务管理'
        this.bizFormModel = scheduleJob
        this.initOptions(this.bizFormModel)
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>
