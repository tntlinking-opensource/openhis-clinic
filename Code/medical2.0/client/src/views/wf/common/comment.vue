<template>
  <el-drawer title='审批历史' :visible.sync='drawer' direction='rtl' :modal='false' size='20%' close-on-press-escape>
    <div style='height:100%' v-loading='loading'>
      <el-timeline :reverse='false'>
        <el-timeline-item
          v-for='(comment, index) in comments'
          :key='index'
          :timestamp='comment.time'>
          {{comment.activityName}}
          <br>
          由 {{comment.commentBy}} {{comment.operation}}
          <br>
          <div v-if='comment.operation != "提交" && comment.operation != "认领"'>意见: {{comment.message}}</div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-drawer>
</template>

<script>
import { getProcInstComments } from '@/api/wf/procinst'
export default {
  name: 'Comment',
  data() {
    return {
      procInstId: null,
      drawer: false,
      loading: false,
      comments: []
    }
  },
  methods: {
    listComents(procInstId){
      this.comments = []
      this.loading = true
      getProcInstComments(procInstId).then(responseData => {
        if(responseData.code == 100) {
          this.comments = responseData.data
        } else {
          //this.showMessage(responseData)
        }
        this.loading = false
      }).catch(error => {
        //this.outputError(error)
        this.loading = false
      })      
    }
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openComment', function(procInstId) {
        this.procInstId = procInstId
        this.listComents(procInstId)

        this.drawer = true
      })
    })
  }
}
</script>

