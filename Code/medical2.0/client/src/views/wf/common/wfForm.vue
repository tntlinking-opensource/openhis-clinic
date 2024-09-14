<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false'  @open='onDialogOpen()' v-loading='loading' fullscreen :lock-scroll='true'>
    <div slot='title' class='dialog-header'>
      <el-row :span='24'>
        <el-col :span='18'>{{ dialogProps.title }}</el-col>
      </el-row>
      <el-row>
        <el-col :span='24' style="position: absolute">
          <el-tabs class="wf-tabs" v-model='activeName' type='card'>
            <el-tab-pane name='form'>
              <span slot="label"><i class='el-icon-edit'></i> 表单</span>
            </el-tab-pane>
            <el-tab-pane name='process'>
              <span slot="label"><i class='el-icon-data-line'></i> 流程</span>
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
    </div>
    <forward-form ref='forwardForm' :wfDaiog='this' v-on:forward-user-finished='afterForwardSelected'></forward-form>
    <select-user ref='userForm' :wfDaiog='this' v-on:select-user-finished='afterUserSelected'></select-user>
    <select-node ref='nodeForm' :wfDaiog='this' v-on:select-node-finished='afterNodeSelected'></select-node>

    <div v-show='activeName=="form"'>
      <component ref='bizCompnent' :is='bizCompnent' :wfDaiog='this' :action='dialogProps.action' :task='task' :procDef='procDef' :formKey='formKey' :bizData='bizData' :options='bizOptions' v-on:after-load-data='afterLoadData' v-on:afer-step-change='afterStepChange'></component>
      <el-divider content-position='left'>流程附件</el-divider>
      <el-upload v-if='dialogProps.action=="create" || dialogProps.action == "approve"' class='upload-demo inline-block' ref='upload' action='' :on-change='onFileChange' :on-remove='onFileRemove' :file-list='fileList' :auto-upload='false'>
        <el-button v-if='dialogProps.action != "viewTask" || dialogProps.action != "viewBiz"' type='text' :plain='true'>点击上传</el-button>
      </el-upload>
      <el-table :data='attachments' style='width: 100%'>
        <el-table-column prop='name' label='附件名'>
          <template slot-scope='{row, $index}'>
            {{row.name}}
            <el-link type='primary' @click='downLoadAttachment(row)'>下载<i class='el-icon-download'></i></el-link>
          </template>
        </el-table-column>
        <el-table-column label='节点' width='160px'>
          <template slot-scope='{row, $index}'>
            <span v-if='histoicComments.length>0'>{{ histoicComments.find(item=>item.taskId==row.taskId).activityName }}</span>
          </template>
        </el-table-column>
        <el-table-column label='上传人' width='120px'>
          <template slot-scope='{row, $index}'>
            <span v-if='histoicComments.length>0'>{{ histoicComments.find(item=>item.taskId==row.taskId).commentBy }}</span>
          </template>
        </el-table-column>
        <el-table-column prop='createTime' label='日期' width='160px'></el-table-column>
      </el-table>

      <div v-if='dialogProps.action=="approve" || dialogProps.action=="viewTask" || dialogProps.action=="viewBiz"'>
        <el-divider content-position='left'><span style='color:#F56C6C;margin-right: 4px'>*</span>处理意见</el-divider>
        <el-form v-if='dialogProps.action=="approve"' :model='approveModel' ref='modelForm' :rules='formRules' label-width='0px'>
          <el-form-item prop='comment'>
            <el-input type='textarea' :rows='2' placeholder='请输入意见' v-model='approveModel.comment'></el-input>
          </el-form-item>
        </el-form>
        <el-steps :active='pageSize' finish-status='Info'>
          <el-step v-for='(comment, index) in pageComments' :key='index' :title='comment.activityName' :icon='frtCommentIcon(comment)'>
            <template slot='description'>
              <el-row>由 {{comment.commentBy}} {{comment.operation}}</el-row>
              <el-row v-if='comment.operation != "提交" && comment.operation != "认领"'>意见: {{comment.message}}</el-row>
              <el-row v-if='attachments.findIndex(item=>item.commentId == comment.id)>=0'>
                附件：
                <div v-for='attachment in attachments.filter(item=>item.commentId == comment.id)'>
                  {{ attachment.name }}
                  <el-link type='primary' @click='downLoadAttachment(attachment)'>下载<i class='el-icon-download'></i></el-link>
                </div>
              </el-row>
              <el-row>{{comment.time}}</el-row>
            </template>
          </el-step>
        </el-steps>
        <el-pagination background @current-change='onCurrentChange' :current-page.sync='currentPage' :page-sizes='[pageSize]' :page-size='pageSize' layout='prev, next' :total='histoicComments.length'>
        </el-pagination>
      </div>
    </div>
    <div v-show='activeName=="process"'>
      <viewer ref='viewer' :traceConfig='traceConfig' :histoicComments='histoicComments' :attachments='attachments'></viewer>
    </div>

    <span slot='footer' class='dialog-footer'>
      <span v-if='dialogProps.action == "create"'>
        <el-button-group class="btnGroup" v-if='steps > 1' :style="{marginLeft: (settings.size === 'mini' || settings.size === 'small')? '-84px' : '-102px'}">
          <el-button :disabled='step <= 1' icon="el-icon-arrow-left" :plain='true' @click='onPrevStep'>上一步</el-button>
          <el-button :disabled='step >= steps' :plain='true' @click='onNextStep'>下一步<i class="el-icon-arrow-right el-icon--right"></i></el-button>
        </el-button-group>
        <span style="margin: 0 20px"></span>
        <el-button :disabled='step < steps' type='primary' :plain='true' @click='onCreate()'>申 请</el-button>
        <el-button :plain='true' @click='onSaveDraft()'>保存草稿</el-button>
      </span>
      <span v-else-if='dialogProps.action == "approve"'>
	      <span v-if='task.assignee == task.owner'>
          <span v-if='task.assignee == task.extInfo.applicantId && task.taskDefinitionKey == task.extInfo.appActId'>
            <el-button-group class="btnGroup" v-if='steps > 1'>
              <el-button :disabled='step <= 1' icon="el-icon-arrow-left" :plain='true' @click='onPrevStep'>上一步</el-button>
              <el-button :disabled='step >= steps' :plain='true' @click='onNextStep'>下一步<i class="el-icon-arrow-right el-icon--right"></i></el-button>
            </el-button-group>
            <span style="margin: 0 20px"></span>
            <el-button :disabled='step < steps' type='primary' :plain='true' @click='onReApply()'>重申请</el-button>
            <el-button type='danger' :plain='true' @click='onTerminate()'>终 止</el-button>
          </span>
          <span v-else>
            <el-button type='primary' :plain='true' @click='onAgree()'>提 交</el-button>
            <el-button type='primary' :plain='true' @click='onForward()'>转 派</el-button>
            <el-button type='primary' :plain='true' @click='onCommission()'>委 派</el-button>
            <el-button type='warning' :plain='true' @click='onReject()'>拒 绝</el-button>
            <el-button type='warning' :plain='true' @click='onBack()'>回 退</el-button>
          </span>
        </span>
	      <span v-else>
          <el-button type='primary' :plain='true' @click='onPropose()'>拟 办</el-button>
        </span>
      </span>
      <div v-if='dialogProps.action == "viewTask" || dialogProps.action == "viewBiz"'>
        <el-button v-if='dialogProps.action == "viewTask" && bizData && !bizData.approved' type='warning'    :plain='true' @click='onReverse()'>撤 回</el-button>
        <el-button :plain='true' @click='onDialogClose()'>关 闭</el-button>
      </div>
      <el-button style="margin-left: 10px;" v-else :plain='true' @click='onDialogClose()'>取 消</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { getAttachments } from '@/api/wf/wfDraft'
import { validatenull } from '@/utils/validate'
import { getProcessBpmnById } from '@/api/wf/processdefinition'
import { histoicFlow, getProcInstComments, getProcInstAttachments } from '@/api/wf/procinst'
import { listTask } from '@/api/wf/task'
import { parseExtInfoForList } from '@/views/wf/utils/wfUtil'
import { downloadAttachment } from '@/api/wf/attachment'
import { frtCommentIcon } from '@/views/wf/utils/wfUtil'
import selectUser from './userForm'
import selectNode from './nodeForm'
import forwardForm from './forwardForm'
import viewer from '../components/viewer'
import BaseUI from '@/views/components/baseUI'
export default {
  extends: BaseUI,
  name: 'wf-form',
  components: {
  	selectUser,
    selectNode,
    forwardForm,
    viewer
  },
  data() {
    return {
      draft: null,      // 单据草稿
      procDef: null,    // 流程定义对象，发起流程时保存流程定义（仅在创建流程时使用）
      task: null,       // 任务对象，审批任务是保存任务 （仅在查看任务时使用）
      bizData: null,    // 业务对象
      definitionId: null,  // 业务数据的审批流程id  （仅在查看业务对象时使用）
      formKey: null,    // 表单form key

      // 分步骤填写的表单
      steps: 1,         // 总步骤数
      step: 1,          // 单前步骤数

      bizOptions: null,
      bizCompnent:  null,
      traceConfig: null,  // {bpmnModel: bpmn模型对象，histoicFlow: 跟踪节点和顺序流, }
      histoicComments: [],  // 流程审批历史
      approveModel: {
        comment: ''   // 审批意见
      },
      requestType: '',   //请求类型 create agree
      activeName: 'form',
      currentPage: 1,   // 历史审批当前页数
      pageSize: 5,      // 每页显示条数
      pageComments: [],     // 当前页的审批记录
      uploadFiles: [],		// 附件列表
      fileList: [],       // upload上传加载的文件列表
      attachments: [],    // 流程附件列表
      dialogProps: {
        visible: false,
        action: '',
        title: ''
      },
      formRules: {
        'comment': [
            { required: true, message: '请输入意见', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...Vuex.mapGetters(["settings"])
  },
  watch:{
    // 监听tabs是否切换
    activeName(val, oldVal) {
      if(val != oldVal && val == 'process' && this.traceConfig == null){
        if(this.dialogProps.action == 'create') {
          this.getProcDefBpmn(this.procDef.id)
        } else if(this.dialogProps.action == 'approve') {
          this.tracePorcInst(this.task.processDefinitionId, this.task.processInstanceId, this.task.taskDefinitionKey)
        } else if(this.dialogProps.action == 'viewBiz') {
          this.tracePorcInst(this.definitionId, this.bizData.procInst, '')
        } else if(this.dialogProps.action == 'viewTask') {
          this.tracePorcInst(this.task.processDefinitionId, this.task.processInstanceId, '')
        } else {
          console.error("没有对[" + this.dialogProps.action + "]处理")
        }
      }
    }
  },
  methods: {
    // 保存草稿
    onSaveDraft() {
      this.requestType = 'savedraft'
      this.$refs.bizCompnent.doSaveDraft(this.draft, this.uploadFiles)
    },
    // 创建流程
    onCreate() {
      this.requestType = 'create'
      this.$refs.bizCompnent.doCreate(this.uploadFiles)
    },
    // 重新发起申请
    onReApply() {
      this.$refs['modelForm'].validate(valid => {
        if (valid) {
          this.requestType = 'reapply'
          this.$refs.bizCompnent.doReApply(this.approveModel.comment, this.uploadFiles)
        }
      })
    },
    // 审批同意
    onAgree() {
      this.$refs['modelForm'].validate(valid => {
        if (valid) {
          this.requestType = 'agree'
          this.$refs.bizCompnent.doAgree(this.approveModel.comment, this.uploadFiles)
        }
      })
    },
    // 转派
    onForward() {
      this.$refs['modelForm'].validate(valid => {
        if (valid) {
          this.requestType = 'forward'
          this.$refs.forwardForm.$emit('openForwardDialog')
        }
      })
    },
    // 委派onCommission
    onCommission() {
      this.$refs['modelForm'].validate(valid => {
        if (valid) {
          this.requestType = 'commission'
          this.$refs.forwardForm.$emit('openForwardDialog')
        }
      })
    },
    // 拟办（办理委派任务）
    onPropose() {
      this.$refs['modelForm'].validate(valid => {
        if (valid) {
          this.requestType = 'propose'
          this.$refs.bizCompnent.doPropose(this.approveModel.comment, this.uploadFiles)
        }
      })
    },
    // 拒绝
    onReject() {
      this.$refs['modelForm'].validate(valid => {
        if (valid) {
          this.requestType = 'reject'
          this.$refs.bizCompnent.doReject(this.approveModel.comment, this.uploadFiles)
        }
      })
    },
    // 退回
    onBack() {
      this.$refs['modelForm'].validate(valid => {
        if (valid) {
          this.requestType = 'back'
          this.$refs.bizCompnent.doListActivity()
        }
      })
    },
    // 终止流程
    onTerminate() {
      this.$refs['modelForm'].validate(valid => {
        if (valid) {
          this.requestType = 'terminate'
          this.$refs.bizCompnent.doTerminate(this.approveModel.comment, this.uploadFiles)
        }
      })
    },
    // 撤回
    onReverse() {
      this.requestType = 'reverse'
      this.$refs.bizCompnent.doReverse(this.task)
    },
    // 上一步
    onPrevStep() {
      this.$refs.bizCompnent.doPrevStep()
    },
    // 下一步
    onNextStep() {
      this.$refs.bizCompnent.doNextStep()
    },
    onDialogOpen() {
      this.$nextTick(() => {
        // 清空文件列表
        if(this.dialogProps.action=="create" || this.dialogProps.action == "approve") {
          this.$refs.upload.clearFiles()
          this.uploadFiles = []
        }
      })
    },
    onDialogClose() {
      this.dialogProps.visible = false
    },
    afterStepChange(step, steps) {
      this.step = step
      this.steps = steps
    },
    afterForwardSelected(actUser) {
      let actNodes = [{
      	assignees: [actUser]
      }]
      if(this.requestType == 'forward') {
      	this.$refs.bizCompnent.doForward(this.approveModel.comment, this.uploadFiles, actNodes)
      } else {
      	this.$refs.bizCompnent.doCommission(this.approveModel.comment, this.uploadFiles, actNodes)
      }
    },
    afterUserSelected(actNodes) {
      if(this.requestType == 'create') {
        this.$refs.bizCompnent.doCreate(this.uploadFiles, actNodes)
      }else if(this.requestType == 'agree') {
        this.$refs.bizCompnent.doAgree(this.approveModel.comment, this.uploadFiles, actNodes)
      }else if(this.requestType == 'reapply') {
        this.$refs.bizCompnent.doReApply(this.approveModel.comment, this.uploadFiles, actNodes)
      }
    },
    afterNodeSelected(actNodes) {
      this.$refs.bizCompnent.doBack(this.approveModel.comment, this.uploadFiles, actNodes)
    },
    afterLoadData(bizData) {
      this.bizData = bizData
    },
    handleResponse(responseData) {
      if(responseData.code == 100) {
        if(this.requestType == 'savedraft') {
          this.dialogProps.visible = false
          this.$emit('save-finished', this.requestType)
          this.showMessage(responseData)
        }else if(responseData.data.code == 'Done' || responseData.data.code == 'AutoSubmit') {
  	      this.dialogProps.visible = false
  	      this.$emit('save-finished', this.requestType)
          if(responseData.data && responseData.data.msg) {
            responseData.msg = responseData.data.msg
          }
  	      this.showMessage(responseData)
  	  	}else if(responseData.data.code == 'SelectUser') {
  	  	   // 显示用户选择框
  	  	   this.$refs.userForm.$emit('openUserDialog', responseData.data.actNodes)
  	  	}else if(responseData.data.code == 'SelectNode') {
  	  		// 显示节点选择框
          this.$refs.nodeForm.$emit('openNodeDialog', responseData.data.actNodes)
  	  	}else {
  	  		this.showMessage({type: responseData.data.code == 'Warning' ? 'warning':'error', msg: responseData.data.msg})
  	  	}

  	  }else{
  	    this.showMessage(responseData)
  	  }
    },
    getProcDefBpmn(procDefId) {
      this.setLoad()
      getProcessBpmnById(procDefId).then(responseData => {
        if(responseData.bpmn20Xml) {
          this.traceConfig = {
            bpmnModel: responseData,
            histoicFlow: [],
            currentEl: ''
          }
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    async tracePorcInst(procDefId, procInstId, curTaskKey) {
      this.setLoad()
      try {
        let [bpmnRespData, histoicFlowRespData, activityTasks] = await Promise.all([
          getProcessBpmnById(procDefId),
          histoicFlow(procInstId),
          listTask({processInstanceId: procInstId})
        ])

        if(bpmnRespData.bpmn20Xml && histoicFlowRespData.code == 100 && activityTasks instanceof Array) {
          this.traceConfig = {
            bpmnModel: bpmnRespData,
            histoicFlow: histoicFlowRespData.data,
            currentEl: curTaskKey,
            activityTask: parseExtInfoForList(activityTasks)
          }
        } else {
          this.showMessage(bpmnRespData.code != 100 ? bpmnRespData : histoicFlowRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onCurrentChange(val) {
      this.pageComments = []
      this.currentPage = val
      let totalPage = Math.ceil(this.histoicComments.length/this.pageSize)  // 总页数
      let offset = (totalPage - this.currentPage)*this.pageSize
      let last = Math.min((totalPage - this.currentPage + 1)*this.pageSize, this.histoicComments.length) -1
      for(let idx = last; idx >= offset; idx--) {
        this.pageComments[last-idx] = this.histoicComments[idx]
      }
    },
    frtCommentIcon(comment) {
      return frtCommentIcon(comment)
    },
    onFileChange(file, fileList) {
      this.uploadFiles = fileList
    },
    onFileRemove(file, fileList) {
      this.uploadFiles = fileList
    },
    downLoadAttachment(attachment) {
      downloadAttachment(attachment.taskId, attachment.id).then(responseData=>{
        const link = document.createElement('a')
        var url = window.URL.createObjectURL(responseData)
        link.href = url;
        link.download = attachment.name
        link.click()
      })
    },
    initTaskInfo(formKey, procInstId) {
      let subFormKey = formKey.split("#")[1]  // 子表表单
      this.formKey = subFormKey
      this.traceConfig = null
      this.histoicComments = []
      this.currentPage = 1
      this.activeName = 'form'
      this.fileList = []
      this.attachments = []
      // 获取流程审批备注
      getProcInstComments(procInstId).then(responseData => {
        if(responseData.code == 100) {
          this.histoicComments = responseData.data
          this.currentPage = Math.max(Math.ceil(this.histoicComments.length/this.pageSize), 1)
          this.onCurrentChange(this.currentPage)
        } else {
          //this.showMessage(responseData)
        }
        //this.resetLoad()
      }).catch(error => {
        //this.outputError(error)
      })

      // 获取流程附件
      getProcInstAttachments(procInstId).then(responseData => {
        if(responseData.code == 100) {
          this.attachments = responseData.data
        } else {
          //this.showMessage(responseData)
        }
        //this.resetLoad()
      }).catch(error => {
        //this.outputError(error)
      })

      if(subFormKey.substr(0,1) == '/') {
        this.bizCompnent = () => import('@/views' + subFormKey)
      }else{
        this.bizCompnent = () => import('@/views/' + subFormKey)
      }
      this.approveModel = {
        comment: ''   // 审批意见
      }
    },
    b64toFile(b64Data, filename, contentType) {
      let sliceSize = 512;
      let byteCharacters = atob(b64Data);
      let byteArrays = [];

      for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
          let slice = byteCharacters.slice(offset, offset + sliceSize);
          let byteNumbers = new Array(slice.length);

          for (let i = 0; i < slice.length; i++) {
              byteNumbers[i] = slice.charCodeAt(i);
          }
          let byteArray = new Uint8Array(byteNumbers);
          byteArrays.push(byteArray);
      }

      let file = new File(byteArrays, filename, {type: contentType});
      return file;
    }
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openCreateDialog', function(procDef, formKey, options) {
        this.bizOptions = options
        let subFormKey = formKey.split("#")[1]   // 子表表单
        this.formKey = subFormKey
        this.traceConfig = null
        this.histoicComments = []
        this.activeName = 'form'
        this.procDef = procDef
        this.fileList = []

        if(subFormKey.substr(0,1) == '/') {
          this.bizCompnent = () => import('@/views' + subFormKey)
        }else{
          this.bizCompnent = () => import('@/views/' + subFormKey)
        }
  	    this.approveModel = {
          comment: ''   // 审批意见
  	    }

        this.dialogProps.action = 'create'
        this.dialogProps.title = '创建流程--' + (procDef.name ? procDef.name : procDef.key)
        this.dialogProps.visible = true
      })


      this.$on('openDraft', function(draft, formKey) {
        let subFormKey = formKey.split("#")[1]   // 子表表单
        this.formKey = subFormKey
        this.traceConfig = null
        this.histoicComments = []
        this.activeName = 'form'
        this.draft = draft
        this.fileList = []
        this.setLoad()
        getAttachments(draft.id).then(responseData => {
          if(responseData.code == 100) {
            for(let idx in responseData.data) {
              let attachment = responseData.data[idx]
              let file = this.b64toFile(attachment.fileContent, attachment.name, attachment.type)
              this.fileList.push({
                name: file.name,
                percentage: 0,
                raw: file,
                size: file.size,
                status: "ready"
              })
            }
            this.uploadFiles = this.fileList
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })

        // 构建流程定义对象和业务对象
        this.procDef = {id: draft.procdefId , name: draft.procdefName}
        this.bizData = JSON.parse(draft.formData)

        if(subFormKey.substr(0,1) == '/') {
          this.bizCompnent = () => import('@/views' + subFormKey)
        }else{
          this.bizCompnent = () => import('@/views/' + subFormKey)
        }
        this.approveModel = {
          comment: ''   // 审批意见
        }

        this.dialogProps.action = 'create'
        this.dialogProps.title = '创建流程--' + draft.procdefName
        this.dialogProps.visible = true
      })

      this.$on('openApproveDialog', function(task) {
        this.task = task
        this.initTaskInfo(task.formKey, task.processInstanceId)
        this.dialogProps.action = 'approve'
        this.dialogProps.title = '审批--' + (task.name ? task.name : task.key)
        this.dialogProps.visible = true
      })

      this.$on('openViewDialog', function(task) {
        this.task = task
        this.initTaskInfo(task.formKey, task.processInstanceId)
        this.dialogProps.action = 'viewTask'
        this.dialogProps.title = '查看'
        this.dialogProps.visible = true
      })

      this.$on('openViewBizDialog', function(formKey, definitionId, biz) {
        this.bizData = biz
        this.definitionId = definitionId
        this.initTaskInfo(formKey, biz.procInst)
        this.dialogProps.action = 'viewBiz'
        this.dialogProps.title = '查看'
        this.dialogProps.visible = true
      })

      // 加载完成
      this.$emit('after-load')
    })
  }
}
</script>

<style lang='scss' scoped>
.el-pagination {
  float: left;
  margin-top: 10px;
}
.inline-block {
  display: inline-block;
}
.dialog-header {
  border-bottom: solid 1px #fff;
}
  .dialog-footer {
    .btnGroup {
      position: absolute;
      left: 50%;
    }
  }
</style>
