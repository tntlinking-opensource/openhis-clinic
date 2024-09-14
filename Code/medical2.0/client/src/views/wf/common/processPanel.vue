<template>
  <div style="width: 100%; height: 100%; position: relative">
    <!-- 工作流配置 -->
    <wf-designer ref='designForm' :permission='permission'></wf-designer>
    <component ref='wfForm' :is='wfForm' v-on:after-load='afterWfFormload()'></component>


    <div class="process-panel-btn">
      <el-button style='float: right; margin-left:10px' v-show='bizAdd' type='primary' icon='el-icon-plus'  @click='onCreate'>添加</el-button>
      <el-button style='float: right' v-show='permission.add' :plain='true' @click='onEditProcess'>配置流程</el-button>
    </div>
  </div>
</template>

<script>
import WfBaseUI from './wfBaseUI'
import WfDesigner from '@/views/wf/designer/designerForm'
import {  getProcessBpmnByKey, getProcessByKey, getStartForm } from '@/api/wf/processdefinition'
import { listResourcePermission } from '@/api/admin/common/permission'
export default {
  extends: WfBaseUI,
  name: 'ProcessPanel',
  components: {
    WfDesigner
  },
  data() {
    return {
      wfForm:  null,  // 工作流表单
      currentProc: null, // 当前流程
      currentFormKey: null, // 当前的表单URL

      permission: {
        view: false,
        add: false,
        edit: false,
        suspend: false,
        export: false
      }
    }
  },
  props: {
    // 权限
    bizAdd: {
      default: () => {return null }
    },
    // 租户id（公司id)
    tenantId: {
      type: String,
      default: () => {return null}
    },
    // 流程key的前缀
    keyPrefix: {
      type: String,
      default: () => {return null}
    },
    // 流程初始时间
    procName: {
      type: String,
      default: ()=> {return ''}
    },
    // 审批表单url
    formKey: {
      type: String,
      default: ()=> {return ''}
    },
    // 流程实例标题
    procTitile: {
      type: String,
      default: ()=> {return ''}
    }
  },
  watch:{

  },
  methods: {
    // 新建流程审批
    onCreate() {
      let procKey = this.keyPrefix + this.tenantId              // 合同审批流程key
      this.$emit('v-loading', true)
      getProcessByKey(procKey, this.tenantId).then(responseData => {
        if(responseData.id) {
          this.startProcess(responseData)
        }else{
          // 没有找到流程
          if(responseData.code == 404) {
            this.showMessage({
              type:'warning',
              msg: '请联系公司管理员配置审批流程。'
            })
          } else {
            this.showMessage(responseData)
          }
        }
      }).catch(error => {
        this.$emit('v-loading', false)
        this.outputError(error)
      })
    },
    // 编辑流程
    onEditProcess() {
      let procKey = this.keyPrefix + this.tenantId              // 合同审批流程key
      this.$emit('v-loading', true)
      getProcessBpmnByKey(procKey, this.tenantId).then(responseData => {
        if(responseData.bpmn20Xml) {
          this.$refs.designForm.$emit('openEditProcessDialog', responseData, this.tenantId)
        }else{
          // 没有找到流程
          if(responseData.code == 404) {
            this.$refs.designForm.$emit('openDesigner', this.currentUser.company.id,
              this.createContractProcXml(procKey, this.procName, this.formKey, this.procTitile))
          } else {
            this.showMessage(responseData)
          }
        }
        this.$emit('v-loading', false)
      }).catch(error => {
        this.$emit('v-loading', false)
        this.outputError(error)
      })
    },
    /**
     * 创建合同审批流程
     * key 流程key
     * name 流程名称
     * formKey 审批单据url
     **/
    createContractProcXml(key, name, formKey, procTitile) {
      return  '<?xml version="1.0" encoding="UTF-8"?>' +
              '<bpmn:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">' +
                '<bpmn:process id="' + key + '" name="' + name + '" isExecutable="true" camunda:isStartableInTasklist="false">' +
                  '<bpmn:startEvent id="StartEvent_1" camunda:formKey="' + formKey + '">' +
                    '<bpmn:documentation>${form.user.name}' + procTitile + '</bpmn:documentation>' + 
                    '<bpmn:outgoing>SequenceFlow_03zea54</bpmn:outgoing>' +
                  '</bpmn:startEvent>' +
                  '<bpmn:sequenceFlow id="SequenceFlow_03zea54" sourceRef="StartEvent_1" targetRef="task_apply" />' +
                  '<bpmn:userTask id="task_apply" name="申请" camunda:candidateUsers="com.geeke.camundaex.candidate.impl.StarterCandidate" camunda:candidateGroups="com.geeke.camundaex.assign.impl.RandomAssign">' +
                    '<bpmn:incoming>SequenceFlow_03zea54</bpmn:incoming>' +
                  '</bpmn:userTask>' +
                '</bpmn:process>' +
                '<bpmndi:BPMNDiagram id="BPMNDiagram_1">' +
                  '<bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="' + key + '">' +
                    '<bpmndi:BPMNShape id="_BPMNShape_StartEvent_2" bpmnElement="StartEvent_1">' +
                      '<dc:Bounds x="173" y="102" width="36" height="36" />' +
                    '</bpmndi:BPMNShape>' +
                    '<bpmndi:BPMNEdge id="SequenceFlow_03zea54_di" bpmnElement="SequenceFlow_03zea54">' +
                      '<di:waypoint x="209" y="120" />' +
                      '<di:waypoint x="259" y="120" />' +
                    '</bpmndi:BPMNEdge>' +
                    '<bpmndi:BPMNShape id="UserTask_1f12mui_di" bpmnElement="task_apply">' +
                      '<dc:Bounds x="259" y="80" width="100" height="80" />' +
                    '</bpmndi:BPMNShape>' +
                  '</bpmndi:BPMNPlane>' +
                '</bpmndi:BPMNDiagram>' +
              '</bpmn:definitions>'
    },
    startProcess(proc) {
      getStartForm(proc.id).then(responseData => {
        if(responseData.hasOwnProperty('key')) {
          if(responseData.key) {
            this.currentProc = proc
            this.currentFormKey = responseData.key
            this.loadWfForm(responseData.key)
          } else {
            this.showMessage({type:'warning' , msg: '流程未配置form，请联系管理员。'})
          }
        }else{
          this.showMessage(responseData)
        }
        this.$emit('v-loading', false)
      }).catch(error => {
        this.$emit('v-loading', false)
        this.outputError(error)
      })
    },
    afterWfFormload() {
      this.$refs.wfForm.$emit('openCreateDialog', this.currentProc, this.currentFormKey)
    },
    initCompent() {
      this.$emit('v-loading', true)
      // 流程设计权限 3005是流程设计路由的id
      listResourcePermission(3005).then(responseData => {
        if(responseData.code == 100) {
          this.permission.view = responseData.data.find(item => {
            return item.permission === 'designer:read'
          })
          this.permission.add = responseData.data.find(item => {
            return item.permission === 'designer:create'
          })
          this.permission.edit = responseData.data.find(item => {
            return item.permission === 'designer:update'
          })
          this.permission.suspend = responseData.data.find(item => {
            return item.permission === 'designer:suspend'
          })
          this.permission.export = responseData.data.find(item => {
            return item.permission === 'designer:export'
          })

        }else{
          this.showMessage(responseData)
        }
        this.$emit('v-loading', false)
      }).catch(error => {
        this.$emit('v-loading', false)
        this.outputError(error)
      })
    }
  },
  mounted(){
    this.initCompent()
  }
}

</script>
<style lang="scss" scoped>
  .process-panel-btn {
    position: absolute;
    bottom: 24px;
    right: 0;
  }
</style>
