<template>
  <el-drawer :title='`${bussObject.name}--历史记录`' :visible.sync='drawer' direction='rtl' :modal='false' size='20%' close-on-press-escape >
    <div style='height:100%' v-loading='loading'>
      <el-timeline :reverse='false'>
        <el-timeline-item
          v-for="(activity, index) in activities"
          :key="index"
          :timestamp="activity.createDate">
          由 {{activity.createBy}} {{activity.actionType.name}}
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-drawer>
</template>

<script>
import { listActionAll } from '@/api/sys/action'
export default {
  name: 'History',
  data() {
    return {
      drawer: false,
      loading: false,
      activities: []
    }
  },
  props: {
    bussObject: {
      type: Object
    }    
  },
  watch:{
    bussObject(val, oldVal) {
      if(val!=oldVal){
        this.getHistory(val.id)
      }
    }, 
  },  
  methods: {
    getHistory(objectId){
      this.activities = []
      this.drawer = true
      this.loading = true
      let search = {
        params: [{
          columnName: 'object_id',
          queryType: '=',
          value: objectId
        }],
        columnName: '',       // 排序字段名
        order: ''             // 排序
      }
      listActionAll(search).then(responseData => {
        if(responseData.code == 100) {
          this.activities = responseData.data
        } else {
          //this.showMessage(responseData)
        }
        this.loading = false
      }).catch(error => {
        //this.outputError(error)
        this.loading = false
      })      
    } 
  }
}
</script>

