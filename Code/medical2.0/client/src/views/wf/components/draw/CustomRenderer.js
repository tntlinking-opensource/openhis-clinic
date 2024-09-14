import BpmnRenderer from 'bpmn-js/lib/draw/BpmnRenderer';
import { traceConfig } from './config'

export default class CustomRenderer extends BpmnRenderer {
  constructor(config, eventBus, styles, pathMap, canvas, textRenderer, priority) {
    super(config, eventBus, styles, pathMap, canvas, textRenderer, priority)
  }

  drawShape(parentGfx, element) {
   let shape = super.drawShape(parentGfx, element)


    let hasTask = false
    if(traceConfig.tasks) {
        let idx = traceConfig.tasks.findIndex(task => {
            return task.taskDefinitionKey == element.id
        })
        hasTask = (idx >=0)
    }

    if(hasTask) {  // 有任务节点
      shape.style.setProperty('stroke', '#F56C6C')
    } else if(traceConfig.highlightEls.indexOf(element.id) >= 0) {    // 已经处理的节点
      shape.style.setProperty('stroke', '#67C23A')
    }

    if(traceConfig.currentEl == element.id) {       // 单前任务
      shape.style.setProperty('fill', '#F56C6C')
    }

    return shape
  }

  drawConnection(parentGfx, element) {
      let connection = super.drawConnection(parentGfx, element);

      if(traceConfig.highlightEls.indexOf(element.id) >= 0) {
        connection.style.setProperty('stroke', 'green')

        // TODO 箭头颜色
        // connection.style.setProperty('marker-end', markerend)
      }
      
      return connection;
  }

}
