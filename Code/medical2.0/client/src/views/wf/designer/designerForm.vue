<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' fullscreen :lock-scroll='true'
    @open='onDialogOpen()' :before-close='onBeforDialogClose' @close='onDialogClose()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
    </div>    
    <div class='containers' ref='content'>
      <assigne-user ref='assigneUserForm' v-on:assinge-finished='afterAssigne'></assigne-user>
      <div class='canvas' ref='canvas'></div>
      <div id='js-properties-panel' class='panel'></div>
      <ul class='buttons'>

        <el-button :disabled='!changed' type='primary' icon='el-icon-upload2' :plain='true' @click='onSave2Server()'>保存</el-button>
        <el-button icon='el-icon-download' :plain='true' @click='onSave2Local()'>保存到本地</el-button>
        <el-button icon='el-icon-download' :plain='true' @click='onSaveSVG()'>保存图片</el-button>
        <el-upload class='upload-demo inline-block' action='' :show-file-list='false' :on-change='onFileChange' :auto-upload='false' accept='.bpmn,.xml'>
          <el-button :plain='true'>导入</el-button>
        </el-upload>
      	<el-button :plain='true' @click='onAutoFit()'>自适应</el-button>
      </ul>
    </div> 
  </el-dialog>
</template>

<script>
// 引入相关的依赖
// import BpmnViewer from 'bpmn-js'
import BpmnModeler from 'bpmn-js/lib/Modeler'
// 引入的是右侧属性栏这个框
import propertiesPanelModule from 'bpmn-js-properties-panel'  
// 引入的是右侧属性栏里的内容
//import propertiesProviderModule from 'bpmn-js-properties-panel/lib/provider/camunda'
import propertiesProviderModule from '../components/provider'
import camundaModdleDescriptor from 'camunda-bpmn-moddle/resources/camunda'
import customTranslate from '../components/customTranslate/customTranslate'
import BaseUI from '@/views/components/baseUI'
import assigneUser from './assigneUserForm'
import { createDeployment } from '@/api/wf/deployment'

import { UserTaskConfig } from '@/views/wf/components/provider/parts/UserTaskConfig'
export default {
  extends: BaseUI,
  name: 'wf-designer',
  components: { 
    assigneUser
  },  
  data () {
    return {
      tenantId: null,
      // bpmn建模器
      bpmnModeler: null,
      strXml: null,       // 创建流程时，指定的流程xml串
      bpmnSvg: null,      // 流程图片文件
      bpmnXml: null,      // 流程定义文件
      // processName: '',
      processModel: null,
      changed: false,   // 流程图修改，未保存到服务器的标志
      saved: false,    // 流程图有保存到服务器状态标志
      loading: false,
      dialogProps: {
        visible: false,
        title: ''
      }
    }
  },
  methods: {
    // 导入文件
    onFileChange(file) {
      if(this.changed) {
        this.$confirm('流程图有修改，未保存。确定导入流程定义文件吗？', '确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.doFileChange(file)
        })
      } else {
        this.doFileChange(file)
      }
    },
    // 部署流程定义
    onSave2Server() {
      let _this = this
      // 流程编号
      // this.bpmnModeler.getDefinitions().rootElements[0].id
      // this.bpmnModeler.getDefinitions().diagrams[0].plane.bpmnElement.id
      let processKey = this.bpmnModeler.getDefinitions().rootElements[0].id

      let blobXml = new Blob(this.bpmnXml.match(/./g), { type: 'text/plan' })
      let fXml = new File([blobXml],  processKey + '.bpmn', { type: 'text/plan' })
      let blobSvg = new Blob(this.bpmnSvg.match(/./g), { type: 'text/plan' })
      let fSvg = new File([blobSvg],  processKey + '.svg', { type: 'text/plan' })

      let formData = new FormData()
      if(this.tenantId && this.tenantId != 'all') {
        formData.append('tenant-id', this.tenantId)
      }
      formData.append('deployment-name', processKey)
      formData.append('enable-duplicate-filtering', true)
      formData.append('deploy-changed-only', true)
      formData.append('deployment-source', 'process application')
      formData.append('data', fXml)
      formData.append('diagram', fSvg)

      this.setLoad()
      createDeployment(formData).then(responseData => {
        if(responseData.code) {   // 有错误码
          this.showMessage(responseData)
        } else {
          this.changed = false
          this.saved = true
          this.showMessage({type: 'success', msg: '保存成功'})
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)       
      })
    },
    // 保存为本地流程文件
    onSave2Local() {
      let processKey = this.bpmnModeler.getDefinitions().rootElements[0].id
      const encodedData = encodeURIComponent(this.bpmnXml)
      const link = document.createElement('a')
      link.href = 'data:application/bpmn20-xml;charset=UTF-8,' + encodedData   //文件url
      link.download = processKey + '.bpmn'
      link.click()
    },
    // 保存图像文件
    onSaveSVG() {
      let processKey = this.bpmnModeler.getDefinitions().rootElements[0].id
      const encodedData = encodeURIComponent(this.bpmnSvg)
      const link = document.createElement('a')
      link.href = 'data:application/bpmn20-xml;charset=UTF-8,' + encodedData
      link.download = processKey + '.svg'
      link.click()
    },
    // 相应对话框打开事件， 如果action为add 则创建新的流程图；如果action 为edit，则打开流程模型的xml
    onDialogOpen() {
      this.$nextTick(() => {
        let bpmn = this.initBpmnModel()  
        let _this = this
        if(this.dialogProps.action == 'add') {
          let diagram = this.strXml ? this.strXml : this.initialDiagram()
          this.impXml(this, bpmn, diagram)
        } else if(this.dialogProps.action == 'edit') {
          this.impXml(this, bpmn, this.processModel.bpmn20Xml)
        }
      })
    },
    // 响应关闭前事件，检查流程图是否修改
    onBeforDialogClose(done) {
      if(this.changed) {
        this.$confirm('流程图有修改，未保存。确定关闭吗？', '确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          done()
        })
      } else {
        done()
      }
    },
    // 响应关闭事件
    onDialogClose() {
     // 关闭刷新主页面列表
     if(this.saved) {
        this.$emit('save-finished')
      }
    },
    // 流程图自适应画板
    onAutoFit() {
      // 让图能自适应屏幕
      var canvas = this.bpmnModeler.get('canvas')
      canvas.zoom('fit-viewport')
    },
    // 文件变化时执行文件读取
    doFileChange(file) {
      let _this = this
      var reader = new FileReader();//new一个FileReader实例
      reader.onload = function(e) {

        let xml = e.target.result;
        _this.impXml(_this, _this.bpmnModeler, xml)
      }
      reader.readAsText(file.raw);
    },    
    // 将字符串转换成图显示出来 _this设计表单对象
    impXml(_this, bpmn, xml) {
      bpmn.importXML(xml, function (err) {
        if (err) {
          console.error(err)
          _this.showMessage({type:'error', msg:'文件格式错误，请检查！'})
        } else {
          // 让图能自适应屏幕
          var canvas = bpmn.get('canvas')
          canvas.zoom('fit-viewport')
          _this.changed = false

          _this.saveSVG(function (err, svg) {
            _this.bpmnSvg =  err ? null : svg
          })

          _this.saveDiagram(function (err, xml) {
            _this.bpmnXml = err ? null : xml
          })          
        }
      })
    },
    // 下载为SVG格式,done是个函数，调用的时候传入的
    saveSVG (done) {
      // 把传入的done再传给bpmn原型的saveSVG函数调用
      this.bpmnModeler.saveSVG(done)
    },
    // 下载为SVG格式,done是个函数，调用的时候传入的
    saveDiagram (done) {
      // 把传入的done再传给bpmn原型的saveXML函数调用
      this.bpmnModeler.saveXML({ format: true }, function (err, xml) {
        done(err, xml)
      })
    },
    // 初始化流程模型对象
    initBpmnModel() {
      if(this.bpmnModeler == null) {
        // 获取到属性ref为“content”的dom节点
        // this.container = this.$refs.content
        // 获取到属性ref为“canvas”的dom节点
        const canvas = this.$refs.canvas

        var customTranslateModule = {
          translate: [ 'value', customTranslate ]
        }

        // 建模，官方文档这里讲的很详细
        this.bpmnModeler = new BpmnModeler({
          container: canvas,
          // 添加控制板
          propertiesPanel: {
            parent: '#js-properties-panel'
          },
          additionalModules: [
            propertiesProviderModule,
            propertiesPanelModule,
            customTranslateModule
          ],
          moddleExtensions: {
            // 如果要在属性面板中维护camunda：XXX属性，需要添加
            camunda: camundaModdleDescriptor
          }
        })

        // 下载画图
        let _this = this
        // 给图绑定事件，当图有发生改变就会触发这个事件
        this.bpmnModeler.on('commandStack.changed', function() {
          _this.changed = true

          _this.saveSVG(function (err, svg) {
            _this.bpmnSvg =  err ? null : svg
          })

          _this.saveDiagram(function (err, xml) {
            _this.bpmnXml = err ? null : xml
          })       
        })

        this.addModelerListener()
        this.addEventBusListener()
      }

      return this.bpmnModeler
    },
    addModelerListener() {
      // 监听 modeler
      const modeling = this.bpmnModeler.get('modeling')
      this.bpmnModeler.on('shape.added', e => {
        // 用户任务的指派初始化
        if(e.element && e.element.type == 'bpmn:UserTask') {
          if(!e.element.businessObject.candidateGroups) {
            e.element.businessObject['candidateGroups'] = 'com.geeke.camundaex.assign.impl.ManulAssign'
          }
          if(!e.element.businessObject.candidateUsers) {
             e.element.businessObject['candidateUsers'] = 'com.geeke.camundaex.candidate.impl.CfgCandidate'
          }          
        }
      })
    },
    addEventBusListener() {
      var moddle = this.bpmnModeler.get('moddle')
      var newCondition = moddle.create('bpmn:FormalExpression', {
        body: '1'
      });

      const modeling = this.bpmnModeler.get('modeling')
      const eventBus = this.bpmnModeler.get('eventBus') // 需要使用eventBus
      let oldCandidateUsers = null  // 保存candidateUsers变更前的值，每次click一个新的节点后，重新赋值。
      eventBus.on('element.click', function(e) {
        if(e.element && e.element.type == 'bpmn:UserTask') {
          oldCandidateUsers = e.element.businessObject['candidateUsers']
        }
      })

      eventBus.on('element.changed', function(e) {
        if(e.element && e.element.type == 'bpmn:UserTask') {
          if( oldCandidateUsers != e.element.businessObject['candidateUsers']) {
            if(e.element.businessObject['assignee']) {
              modeling.updateProperties(e.element, {assignee: null})
            }
            oldCandidateUsers = e.element.businessObject['candidateUsers']
          }
          // 设置循环基数
          if(e.element.businessObject.loopCharacteristics && e.element.businessObject.loopCharacteristics.$type == 'bpmn:MultiInstanceLoopCharacteristics' && (!e.element.businessObject.loopCharacteristics.loopCardinality || !e.element.businessObject.loopCharacteristics.loopCardinality.body)) {
              let loopCharacteristics = e.element.businessObject.loopCharacteristics
              loopCharacteristics.loopCardinality = newCondition
              modeling.updateProperties(e.element, {
                      loopCharacteristics : loopCharacteristics
              })
          }
        }
      })
    },
    doSelectUser(element, node) {
      this.$refs.assigneUserForm.$emit('openAssigneDialog', element)
    },
    afterAssigne(element, actUsers) {
      let modeling = this.bpmnModeler.get('modeling')
      let groups = JSON.stringify(actUsers)
      modeling.updateProperties(element, {assignee: groups})
    },
    // 
    initialDiagram() {
      let procId = 'Proc_' + (new Date()).valueOf().toString(36)
      return '<?xml version="1.0" encoding="UTF-8"?>' +
              '<bpmn:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">' + 
                '<bpmn:process id="' + procId + '" isExecutable="true">' +
                  '<bpmn:startEvent id="StartEvent_1">' + 
                    '<bpmn:outgoing>SequenceFlow_03zea54</bpmn:outgoing>' +
                  '</bpmn:startEvent>' +
                  '<bpmn:sequenceFlow id="SequenceFlow_03zea54" sourceRef="StartEvent_1" targetRef="task_apply" />' +
                  '<bpmn:userTask id="task_apply" name="申请" camunda:candidateUsers="com.geeke.camundaex.candidate.impl.StarterCandidate" camunda:candidateGroups="com.geeke.camundaex.assign.impl.RandomAssign">' + 
                    '<bpmn:incoming>SequenceFlow_03zea54</bpmn:incoming>' +
                  '</bpmn:userTask>' + 
                '</bpmn:process>' +
                '<bpmndi:BPMNDiagram id="BPMNDiagram_1">' + 
                  '<bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="' + procId + '">' + 
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
    }
  },
  mounted () {
    UserTaskConfig.doSelectUser = this.doSelectUser
    this.$nextTick(() => {
      this.$on('openDesigner', function(tenantId, strXml) {
        this.tenantId = tenantId
        this.strXml = strXml
        this.dialogProps.action = 'add'
        this.dialogProps.title = '创建流程'
        this.processModel = null
        this.dialogProps.visible = true
      })
      this.$on('openEditProcessDialog', function(process, tenantId) {
        this.tenantId = tenantId
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '编辑流程'
        this.processModel = process
        this.dialogProps.visible = true
      })
    })
  }
}
</script>

<style lang='scss'>

/*左边工具栏以及编辑节点的样式*/
  @import 'bpmn-js/dist/assets/diagram-js.css';
  @import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css';
  @import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css';
  @import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css';
  /*右边工具栏样式*/
  @import 'bpmn-js-properties-panel/dist/assets/bpmn-js-properties-panel.css';
  .containers{
    position: absolute;
    background-color: #ffffff;
    width: 100%;
    height: 90%;
  }
  .canvas{
    width: 100%;
    height: 100%;
  }
  .panel{
    position: absolute;
    right: 0;
    top: 0;
    width: 300px;
  }
  .buttons{
    position: absolute;
    left: 20px;
    bottom: 20px;
    &>li{
      display:inline-block;margin: 5px;
      &>a{
        color: #999;
        background: #eee;
        cursor: not-allowed;
        padding: 8px;
        border: 1px solid #ccc;
        &.active{
          color: #333;
          background: #fff;
          cursor: pointer;
        }
      }
    }
  }
  .inline-block {
    display: inline-block;
  }
  .bjs-powered-by {
    display: none
  }
</style>