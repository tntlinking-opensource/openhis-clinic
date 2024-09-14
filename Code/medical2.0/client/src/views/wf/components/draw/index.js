// import BpmnRenderer from 'bpmn-js/lib/draw/BpmnRenderer';
// import BpmnRenderer from '@/components/custom/CustomRenderer';
 import BpmnRenderer from './CustomRenderer';
import TextRenderer from 'bpmn-js/lib/draw/TextRenderer';

import PathMap from 'bpmn-js/lib/draw/PathMap';

export default {
  __init__: [ 'bpmnRenderer' ],
  bpmnRenderer: [ 'type', BpmnRenderer ],
  textRenderer: [ 'type', TextRenderer ],
  pathMap: [ 'type', PathMap ]
};
