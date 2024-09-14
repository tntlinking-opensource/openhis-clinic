<template>
</template>
<script>
  import Vue from 'vue'
  export default {
    methods: {
      openRegistration(){
        let page = window.open('http://61.172.179.73:5019')
        // let page = window.open('http://61.172.179.48:19028/')
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
