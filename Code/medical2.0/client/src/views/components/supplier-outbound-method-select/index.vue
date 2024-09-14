<template>
  <el-select v-model="selectedMethod" filterable placeholder="请选择出库方式" clearable>
    <el-option v-for="t in typeList" :label="t.name" :value="t.id" :key="t.id">
    </el-option>
  </el-select>
</template>

<script>
  export default {
    props: {
      value: {
        type: String,
        default: ''
      }
    },

    data() {
      return {
        typeList: [{name:'退还供方',id:'1'},{name:'药品报损',id:'2'},{name:'调拔出库',id:'3'},{name:'其他',id:'4'}],
        selectedMethod: null
      };
    },
    computed:{
    },
    watch: {
      value() {
        this.selectedMethod = this.value;
      },
      selectedMethod() {
        this.$emit('input', this.selectedMethod);
      }
    },
    methods: {
    },
    mounted() {
    },
  };
</script>

<style>
</style>
