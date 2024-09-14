<template>
  <el-dialog :title='dialogProps.title'
             :visible.sync='dialogProps.visible'
             :close-on-click-modal='false'
             @open='onDialogOpen()'
             v-loading='loading'
             fullscreen
             :lock-scroll='true'>
    <Preview></Preview>
  </el-dialog>
</template>

<script>
  import BaseUI from '@/views/components/baseUI'
  import Preview from '@/views/report/components/Preview'
	export default {
    extends: BaseUI,
		name: "ReportView",
    components: {
      Preview
    },
    data() {
		  return {
        dialogProps: {
          visible: false,
          title: ''
        }
      }
    },
    methods: {
      onDialogOpen() {

      },
      onDialogClose() {
        this.dialogProps.visible = false
      },
    },
    mounted() {
		  this.$nextTick(() => {
        this.$on('openReportView', (report) => {
          if (report) {
            this.dialogProps.title = report.name
          }
          this.dialogProps.visible = true
        })
      })
    }
  }
</script>

<style scoped lang="scss">
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
