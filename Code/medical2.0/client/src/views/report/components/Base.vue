<template>
</template>
<script>
  import Cookies from 'js-cookie'
  export default {
    name: 'Base',
    data() {
      return {
        existDataView: false,
        reportView: null
      }
    },
    watch: {
      existDataView(val, oldValue) {
        if( val != oldValue && val ) {
          this.reportView = () => import('./Report')
        }
      }
    },
    methods: {
      // 递归检查报表的dataviewer是否加载
      wactchDataView () {
        if(window["vue-dataviewer"]) {
          this.existDataView = true
        } else {
          setTimeout(this.wactchDataView, 50)
        }
      }
    },
    mounted() {
      // Cookies.set('Authorization', sessionStorage.getItem('token'))
      // 避免因域名相同（端口或许不同）时 共用一个 cookie 'Authorization'，要两者分开定义
      Cookies.set('x-token', sessionStorage.getItem('token'))

      this.wactchDataView()
    }
  }
</script>
