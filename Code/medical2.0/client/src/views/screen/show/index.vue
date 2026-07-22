<template>
</template>
<script>
export default {
    data(){
        return{

        }
    },
    methods:{
      toRescue() {
        window.open(this.grafanaUrl, "_blank")
      },
      openRegistration(){
        let page = window.open(this.grafanaUrl)
        page.onload=()=>{
          page.document.title = "新致云诊所"
        }
      },
    },
    computed: {
      grafanaUrl() {
        return (process.env.GRAFANA_URL || 'http://61.172.179.48:19017') + '/d/f3be469f-8112-4e72-8338-46e6f1555f4d/e98791-e7a780-e58ebf-e695b0-e68dae-e5a4a7-e5b18f?orgId=1'
      }
    },
    mounted(){
      this.openRegistration();
    }
}
</script>
