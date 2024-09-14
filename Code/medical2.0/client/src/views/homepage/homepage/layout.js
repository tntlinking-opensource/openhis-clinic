

const initPanelsLayout = [
  {"x":0,"y":0,"w":30,"h":9,"i":"552789263395020803","name":"常用应用","moved":false},
  {"x":30,"y":0,"w":70,"h":9,"i":"557507851905990691","name":"常用流程","moved":false},
  {"x":0,"y":9,"w":30,"h":9,"i":"759165723333517543","name":"常用报表","moved":false},
  {"x":30,"y":9,"w":70,"h":9,"i":"552789263395020805","name":"我的日程","moved":false},
];

const  demo = {
  i: "0", // 索引值 必填
  h: 4,   // 高度   必填
  w: 7,   // 宽度   必填
  x: 2,   // x 轴距离 必填
  y: 0,   // y轴距离 必填
  minW:5, // 最小宽度 当 w的值小于minW时 采用minW的值
  minH:3, // 同上
  maxW:8, // 的最大宽度。如果w大于maxW，那么w将被设置为maxW。
  maxH:6, // 同上
  isDraggable:true, // 单独控制这一个盒子是否可以拖动 未填写 继承父元素的 非必填
  isResizable:true, // 单独控制这一个盒子是否可以调整大小 未填写 继承父元素的 非必填
  static:false, // 这个盒子是静态的  不能被其他元素改变位置 会被覆盖在底部
  dragIgnoreFrom: '', // 属性这值为css 选择器 项的哪些元素不应触发项的拖动事件// 具体不知道干嘛的 没有用到
  dragAllowFrom:'', // 属性这值为css 选择器 项的哪些元素应触发项的拖动事件 // 文档这样写的
  resizeIgnoreFrom:'',//属性这值为css 选择器 表示项的哪些元素不应触发项的调整大小事件。//来自文档翻译
};

export { initPanelsLayout }
