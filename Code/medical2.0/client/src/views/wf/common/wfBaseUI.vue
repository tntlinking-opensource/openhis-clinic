<template>
  <div  style="text-align:left" >
    工作流窗口的基础组件类，流程相关业务处理。
  </div>
</template>
<script>
import BaseUI from '@/views/components/baseUI'
export default {
  extends: BaseUI,
  name: 'WfBaseUI',
  data() {
    return {

    }
  },
  methods: {
    loadWfForm(fullUrl) {
      let pcKey = fullUrl.split("$")[0]    // 截取pc审批地址
      let formKey = pcKey.split("#")[0]    // 截取主表单，#前部分

      if(formKey.substr(0,1) == '/') {
        this.wfForm = () => import('@/views' + formKey)
      }else{
        this.wfForm = () => import('@/views/' + formKey)
      }
    },
  },
  mounted() {

  }
}
</script>