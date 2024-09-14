<template>
  <el-date-picker
    v-if='type == "datetimerange"'
    v-model='vModel'
    type='datetimerange'
    range-separator='至'
    start-placeholder='开始日期时间'
    end-placeholder='结束日期时间'
    align='right'
    value-format='yyyy-MM-dd HH:mm:ss'
    :picker-options='pickerOptions'
    unlink-panels>
  </el-date-picker>
  <el-date-picker
    v-else
    v-model='vModel'
    type='daterange'
    range-separator='至'
    start-placeholder='开始日期'
    end-placeholder='结束日期'
    align='right'
    value-format='yyyy-MM-dd'
    :picker-options='pickerOptions'
    unlink-panels>
  </el-date-picker>
</template>

<script>
export default {
  name: 'DataRange',
  data() {
    return {
      vModel: null,
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      }
    }
  },
  props: {
    //接受外部v-model传入的值
    value: {
      type: Array
    },
    type: {
      type: String,
      default: () => {return 'daterange'}
    }
  },
  watch:{
    //判断下拉框的值是否有改变
    vModel(val, oldVal) {
      //console.log('new: %s, old: %s', val, oldVal)
      if(val!=oldVal){
          this.$emit('input', this.vModel);
      }
    },
    value(val, oldVal){   //添加监听value值
      if(val!=oldVal){
        this.vModel=this.value;
      }
    },
  },
  mounted(){
    //初始话日期框的值
    this.vModel=this.value;
  }
}
</script>

<style scoped>
.el-date-editor--daterange.el-input, .el-date-editor--daterange.el-input__inner, .el-date-editor--timerange.el-input, .el-date-editor--timerange.el-input__inner {
    width: 100%;
}
.el-date-editor--datetimerange.el-input, .el-date-editor--datetimerange.el-input__inner {
    width: 100%;
}
</style>
