<template>
</template>
<script>
  import Vue from 'vue'
  export default {
    methods: {
      openRegistration(){
        let page = window.open(process.env.FOLLOW_UP_URL || 'http://61.172.179.73:5019')
        page.onload=()=>{
          page.document.title = "新致云诊所"
        }
      },
    },
    data() {
      return {
      }
    },
    mounted() {
      this.openRegistration();
    },
  }
</script>
