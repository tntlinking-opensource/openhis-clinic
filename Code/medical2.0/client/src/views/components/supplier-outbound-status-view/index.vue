<template>
  <span>{{statusName}}</span>
</template>

<script>
  export default {
    props: {
      value: {
        type: Number,
        default: null
      }
    },

    data() { 
      return {};
    },
    computed:{
      statusName()
      {
        if(!this.value)return '';
        switch(this.value)
        {
          case 1:return '待审核';
          case 2:return '已审核';
          case 3:return '已撤销';
        }
        return '';
      }
    },
    watch: { 
    },
    methods: {
    },
    mounted() {
    },
  };
</script>

<style>
</style>
