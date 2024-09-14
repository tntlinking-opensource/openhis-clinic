<template>
  <el-select v-model="selectedType" filterable placeholder="请选择类型" clearable>
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
        typeList: [{name:'药品',id:'1'},{name:'材料',id:'2'}],
        selectedType: null
      };
    },
    computed:{
    },
    watch: {
      value() {
        this.selectedType = this.value;
      },
      selectedType() {
        this.$emit('input', this.selectedType);
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
