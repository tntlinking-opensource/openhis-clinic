<template>
  <el-popover placement="bottom" width="10" trigger="click" >
    <div  style="text-align:right" >
      <OperationIcon type='primary' content='全部显示' placement='top-start' icon-name='el-icon-check' @click='onShowAllColumnView()'></OperationIcon>
      <OperationIcon type='primary' content='默认配置' placement='top-start' icon-name='el-icon-refresh' @click='onResetColumnView()'></OperationIcon>
    </div>
    <div  style="text-align:left" >
      <el-checkbox v-for="column in vModel" v-model='column.display' :key='column.prop' :label='column.label' @change='onColumnViewChange(column)' style="text-align:left"></el-checkbox>
    </div>
    <OperationIcon slot='reference' type='primary' content='自定义显示列' placement='top-start' icon-name='el-icon-tickets'></OperationIcon>
  </el-popover>
</template>

<script>
import OperationIcon from '@/components/OperationIcon'
export default {
  name: 'ViewColumnsSelect',
  components: {
    OperationIcon
  },
  data() {
    return {
      vModel: null
    }
  },
  props: {
    //接受外部v-model传入的值
    value: {
      type: Array
    }
  },
  watch:{
    vModel(val, oldVal) {
      //console.log('new: %s, old: %s', val, oldVal)
      if(val!=oldVal){
          this.$emit('input', this.vModel);
      }
    },
  },
  methods: {
    async init() {

    },
    onResetColumnView(){
      this.$emit('show-default-column')
    },
    onShowAllColumnView(){
      this.$emit('show-all-column')
    },
    onColumnViewChange(column) {
      this.$emit('save-column-view', column)
    },
    onMouseEnter(e) {
      e.target.style.color = '#409EFF'
    },
    onMouseLeave(e) {
      e.target.style.color = '#909399'
    }
  },
  mounted() {
    this.vModel=this.value;
    this.init()
  }
}
</script>

