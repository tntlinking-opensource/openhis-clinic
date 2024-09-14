<template>
   <el-form :model='bizFormModel' :rules='formRules' 
     ref='leaveForm' label-width='120px' label-position='right' class='edit-form'>    
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='请假人' prop='leaver.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.leaver.name'></el-input>
            <el-select v-else v-model='bizFormModel.leaver' value-key='id' filterable clearable placeholder='请选择请假人' 
              @clear='bizFormModel.leaver={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='leaver in leaver_List' :key='leaver.id' :label='leaver.name' :value='leaver'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='事由' prop='name' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='40' :placeholder='dialogProps.action == "view"? "" : "请输入事由"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='休假天数' prop='days' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.days'></el-input>
            <number-input v-else v-model="bizFormModel.days"  :precision="0"></number-input>
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
   </el-form>
</template>
<script>
import BizForm from '@/views/wf/common/bizForm'
import { validatenull } from '@/utils/validate'
import { listUserAll } from '@/api/admin/user'
import { listDictItemAll } from '@/api/sys/dictItem'
import { getLeaveById, createLeave, reapplyLeave, getLeaveByTaskId, agreeLeave, forwardLeave, commissionLeave, proposeLeave, backLeave, rejectLeave, terminateLeave, reverseLeave, listBackActivity, saveDraftLeave } from '@/api/test/leave'
export default {
  extends: BizForm,
  name: 'leave-wf-form',
  components: {
  },  
  data() {
    return {
      tabIndex: '1',
          leaver_List: [],  // 请假人
       dialogProps: {
        visible: false,
        action: '',
        title: ''
      },
      formRules: {
        'leaver.id': [
            { required: true, message: '请选择请假人', trigger: 'change' }
        ],
        'name': [
            { required: true, message: '请输入事由', trigger: 'blur' }
        ],
        'days': [
            { required: true, message: '请选择休假天数', trigger: 'blur' }
        ],
      }
    }    
  },
  methods: {
    loadModelData(taskId) {
      this.setLoad()
      getLeaveByTaskId(taskId).then(responseData => {
        if(responseData.code == 100) {
          this.bizFormModel = { ...this.initFormModel(), ...responseData.data }
          this.$emit('after-load-data', this.bizFormModel)        // 表单数据对象传给父节点
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)         
      })
    },
    // 保存草稿，files附件列表, draftModel 草稿对象
    doSaveDraft(draftModel, files) {
      if(!draftModel) {   // 不是从草稿打开创建，保存时更新原草稿
        draftModel = {
            procdefId: this.procDef.id,
            procdefName: this.procDef.name,
            name: this.procDef.name + '-' + this.bizFormModel.name
        }
      }
      this.setLoad()
      let formData = this.createFormData(this.bizFormModel, files)
      formData.append('strDraft', JSON.stringify(draftModel))
      saveDraftLeave(formData).then(responseData => {
        this.handleResponse(responseData)
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)         
      })
    },
    // 创建一个新流程 process流程 actNodes是提交的用户任务节点及其审批用户
    doCreate(files, actNodes) {
      this.$refs['leaveForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.bizFormModel.actNodes = actNodes   // 选择节点及审批用户

          let formData = this.createFormData(this.bizFormModel, files)
          createLeave(this.procDef.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)         
          })
          
        }
      })
    },
    // 重新发起申请 comment 意见, files文件附件列表，actNodes提交节点和用户信息
    doReApply(comment, files, actNodes) {
      this.$refs['leaveForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.bizFormModel.actNodes = actNodes   // 选择节点及审批用户
          this.bizFormModel.taskComement = comment  // 意见

          let formData = this.createFormData(this.bizFormModel, files)
          reapplyLeave(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)         
          })
        }
      })
    },
    // 同意 comment 意见, files文件附件列表，actNodes提交节点和用户信息
    doAgree(comment, files, actNodes) {
      this.$refs['leaveForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.bizFormModel.actNodes = actNodes   // 选择节点及审批用户
          this.bizFormModel.taskComement = comment  // 意见

          let formData = this.createFormData(this.bizFormModel, files)
          agreeLeave(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)         
          })
        }
      })
    },
    // 转派任务
    doForward(comment, files, actNodes) {
      this.$refs['leaveForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.bizFormModel.actNodes = actNodes   // 选择节点及审批用户
          this.bizFormModel.taskComement = comment  // 意见

          let formData = this.createFormData(this.bizFormModel, files)
          forwardLeave(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)         
          })
        }
      })
    },
    // 委派任务
    doCommission(comment, files, actNodes) {
      this.$refs['leaveForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.bizFormModel.actNodes = actNodes   // 选择节点及审批用户
          this.bizFormModel.taskComement = comment  // 意见
          
          let formData = this.createFormData(this.bizFormModel, files)
          commissionLeave(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)         
          })
        }
      })
    },    
    // 拟办（办理委派过来的任务）
    doPropose(comment, files) {
      this.$refs['leaveForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.bizFormModel.taskComement = comment  // 意见

          let formData = this.createFormData(this.bizFormModel, files)
          proposeLeave(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)         
          })
        }
      })
    },
    // 拒绝
    doReject(comment, files) {
      this.$refs['leaveForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.bizFormModel.taskComement = comment  // 意见

          let formData = this.createFormData(this.bizFormModel, files)
          rejectLeave(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)         
          })
        }
      })
    },    
    // 退回 comment 意见, actNodes退回节点和用户信息
    doBack(comment, files, actNodes) {
      this.$refs['leaveForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.bizFormModel.actNodes = actNodes   // 选择节点及审批用户
          this.bizFormModel.taskComement = comment  // 意见

          let formData = this.createFormData(this.bizFormModel, files)
          backLeave(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)         
          })
        }
      })
    },
    // 终止流程 comment 意见, actNodes退回节点和用户信息
    doTerminate(comment, files) {
      this.setLoad()
      this.bizFormModel.taskComement = comment  // 意见

      let formData = this.createFormData(this.bizFormModel, files)
      terminateLeave(this.task.id, formData).then(responseData => {
        if(responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
        }
        this.showMessage(responseData)
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)         
      })
    },
    // 撤回任务
    doReverse(task) {
      this.setLoad()
      reverseLeave(task.id).then(responseData => {
        this.handleResponse(responseData)
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)         
      })
    },
    doListActivity() {
      this.$refs['leaveForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          listBackActivity(this.task.id).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)         
          })
        }
      })
    },
    initFormModel(This) {
      return {
        'leaver': {     // 请假人
          'id': null,
          'name': null,
        },
        'name': '',   // 事由
        'days': 0,    // 休假天数
        'remarks': '',   // 备注信息

      }
    },
    initOptions(This) {
      let leaver_search = {
        params: []
      }
        // 字段对应表上filter条件
        leaver_search.params.push.apply(leaver_search.params, [])
        // 表有禁用字段 
        leaver_search.params.push.apply(leaver_search.params, [{columnName: 'is_locked', queryType: '=', value: '0'}])
      // 数据权限:  用户sys_user
      this.pushDataPermissions(leaver_search.params, this.$route.meta.routerId, '4004')
      this.leaver_List.splice(0, this.leaver_List.length)
      listUserAll(leaver_search).then(responseData => {
        this.leaver_List = responseData.data
      })
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.dialogProps.visible = true
      this.initOptions()
      this.tabIndex = '1'
      if(this.action == 'create') {
        this.$refs['leaveForm'].resetFields()
        // 从草稿创建
        if(this.bizData) {
          this.bizFormModel = { ...this.initFormModel(), ...this.bizData }
        }
      } else if(this.action == 'approve') {
        // 重新申请
        let reApply = this.task.assignee == this.task.owner && this.task.assignee == this.task.extInfo.applicantId && this.task.taskDefinitionKey == this.task.extInfo.appActId
        if(!reApply) {
          this.dialogProps.action = "view"
        }      
        this.loadModelData(this.task.id)
      } else if(this.action == 'viewBiz') {
        this.dialogProps.action = "view"
        this.bizFormModel = { ...this.initFormModel(), ...this.bizData }
      } else if(this.action == 'viewTask') {
        this.dialogProps.action = "view"
        this.loadModelData(this.task.id)
      } else {
        console.err("=============== 业务审批单中，实现的：" + this.action)
      }
    })
  }  
}
</script>