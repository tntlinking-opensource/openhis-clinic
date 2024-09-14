<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' fullscreen
    @open='onDialogOpen()'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
    </div>    
    <viewer ref='viewer' :traceConfig='traceConfig'></viewer>
  </el-dialog>
</template>

<script>
import viewer from '../components/viewer'
import BaseUI from '@/views/components/baseUI'

export default {
  extends: BaseUI,
  name: 'wf-designer',
  components: {
    viewer
  },  
  data () {
    return {
      traceConfig: null,
      dialogProps: {
        visible: false,
        title: ''
      }
    }
  },
  methods: {
    onDialogOpen() {

    },
  },
  mounted () {
    this.$nextTick(() => {
      this.$on('openViewer', function(process) {
        this.traceConfig = {
          bpmnModel:process,
          histoicFlow: [],
          currentEl: ''
        }

        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看流程'
        this.dialogProps.visible = true
      })

    })
  }
}
</script>