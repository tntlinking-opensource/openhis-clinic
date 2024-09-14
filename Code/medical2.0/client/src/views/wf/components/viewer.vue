<template> 
  <div class='containers' ref='content'>
    <el-popover
      ref='popover'
      v-model='curactComments && curactComments.length>0'
      placement='right'
      :title='curElement.businessObject.name'
      width='240'
      trigger='manual'
      content='如果去掉这个属性，在显示时会改变流程图的位置导致闪烁问题'>
      <el-timeline :reverse='false'>
        <el-timeline-item v-for='(comment, index) in curactComments' :key='index' :color='comment.isTask ? "#F56C6C" : "#67C23A"' :timestamp='comment.isTask ? comment.created : comment.time'>
          <div v-if='comment.isTask'>
            <el-row>待 {{comment.extInfo.approverName}} 处理</el-row>
          </div>
          <div v-else>
            <el-row>由 {{comment.commentBy}} {{comment.operation}}</el-row>
            <el-row>意见: {{comment.message}}</el-row>
            <el-row v-if='attachments.findIndex(item=>item.commentId == comment.id)>=0'>
              附件：
              <div v-for='attachment in attachments.filter(item=>item.commentId == comment.id)'>
                {{ attachment.name }}
                <el-link type='primary' @click='downLoadAttachment(attachment)'>下载<i class='el-icon-download'></i></el-link>
              </div>
            </el-row>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-popover>

    <div class='canvas' ref='canvas'></div>
    <ul class='buttons'>
      <el-button icon='el-icon-download' :plain='true' @click='onSaveSVG()'>保存图片</el-button>
    	<el-button :plain='true' @click='onAutoFit()'>自适应</el-button>
    </ul>
  </div> 
</template>

<script>
// 引入相关的依赖
import BpmnViewer from 'bpmn-js/lib/Viewer'

// 参考bpmn-js Viewer，替换默认的model配置
import CoreModule from '../components/core';
import TranslateModule from 'diagram-js/lib/i18n/translate';
import SelectionModule from 'diagram-js/lib/features/selection';
import OverlaysModule from 'diagram-js/lib/features/overlays';
import { downloadAttachment } from '@/api/wf/attachment'
import { traceConfig } from './draw/config'

export default {
  name: 'viewer',
  data () {
    return {
      // bpmn建模器
      bpmnModeler: null,
      bpmnSvg: null,      // 流程图片文件
      bpmnXml: null,      // 流程定义文件
      curactComments: [],  // 当前活动节点审批记录
      curElement: {businessObject:{name:''}}  // 当前元素对象
    }
  },
  props: {
    traceConfig: {
      type: Object,
      default: () => {return null}
    },
    histoicComments: {
      type: Array,
      default: () => {return []}
    },
    attachments: {
      type: Array,
      default: () => {return []}
    }
  },
  watch:{
    //判断下拉框的值是否有改变
    traceConfig(val, oldVal) {
      if(val != oldVal){
        this.showDiagram()
      }
    }, 
  },    
  methods: {
    // 显示流程图
    showDiagram() {
      if(this.traceConfig && this.traceConfig.histoicFlow 
        && this.traceConfig.bpmnModel && this.traceConfig.bpmnModel.bpmn20Xml) {

        // 赋值配置变量
        traceConfig.highlightEls = this.traceConfig.histoicFlow
        traceConfig.currentEl = this.traceConfig.currentEl
        traceConfig.tasks = this.traceConfig.activityTask
        let bpmn = this.initBpmnModel()  
        this.impXml(this, bpmn, this.traceConfig.bpmnModel.bpmn20Xml)
      }
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
        this.impXml(this, bpmn, this.processModel.bpmn20Xml)
      })
    },
    // 流程图自适应画板
    onAutoFit() {
      // 让图能自适应屏幕
      var canvas = this.bpmnModeler.get('canvas')
      canvas.zoom('fit-viewport')
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

          _this.addEventBusListener()

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

        this.bpmnModeler = new BpmnViewer({
          container: canvas,
          modules:[
            CoreModule,
            TranslateModule,
            SelectionModule,
            OverlaysModule
          ]          
        })

        // 下载画图
        let _this = this
        // 给图绑定事件，当图有发生改变就会触发这个事件
        this.bpmnModeler.on('commandStack.changed', function () {

          _this.saveSVG(function (err, svg) {
            _this.bpmnSvg =  err ? null : svg
          })

          _this.saveDiagram(function (err, xml) {
            _this.bpmnXml = err ? null : xml
          })       
        })
      }

      return this.bpmnModeler
    },
    addEventBusListener() {
      // 监听 element
      let _this = this
      const eventBus = this.bpmnModeler.get('eventBus')
      // element.hover/element.out/element.click/element.dblclick/element.mousedown
      eventBus.on('element.hover', function(e) {
        if(_this.curElement == e.element) return
        _this.curElement = e.element

        if (!e || e.element.type == 'bpmn:UserTask') {
          _this.$refs.popover.referenceElm = e.gfx
          setTimeout(
            function(){
              // 审批信息
              let arr = _this.histoicComments.filter((item)=>{
                return item.activityId == e.element.id
              })

              // 加入待审批任务信息
              if(traceConfig.tasks) {
                traceConfig.tasks.forEach(item => {
                  if(item.taskDefinitionKey == e.element.id) {
                    item.isTask = true
                    arr.splice(0, 0, item)
                  }
                })
              }


              _this.curactComments = arr
              console.log('==============================================')
              console.log(_this.curactComments)
            },
            500
          )
        } else {
          _this.$refs.popover.referenceElm = undefined
          _this.curactComments = []       
        }
      })

    },
    downLoadAttachment(attachment) {
      downloadAttachment(attachment.taskId, attachment.id).then(responseData=>{
        const link = document.createElement('a')
        var url = window.URL.createObjectURL(responseData)
        link.href = url;
        link.download = attachment.name
        link.click()
      })
    }    
  },
  mounted () {
    this.$nextTick(() => {
      this.showDiagram()
    })
  }
}
</script>

<style lang='scss' scoped>
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
  .buttons{
    position: absolute;
    left: 20px;
    bottom: 30px;
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
</style>

<style lang='scss'>
  .bjs-powered-by {
    display: none
  }
</style>