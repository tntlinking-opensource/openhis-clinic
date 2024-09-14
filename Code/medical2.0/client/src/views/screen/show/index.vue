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
        // window.open('http://36.:8088/',"_blank")
        window.open('http://61.172.179.48:19017/d/f3be469f-8112-4e72-8338-46e6f1555f4d/e98791-e7a780-e58ebf-e695b0-e68dae-e5a4a7-e5b18f?orgId=1',"_blank")
      },
      openRegistration(){
        let page = window.open('http://61.172.179.48:19017/d/f3be469f-8112-4e72-8338-46e6f1555f4d/e98791-e7a780-e58ebf-e695b0-e68dae-e5a4a7-e5b18f?orgId=1')
        // let page = window.open('http://61.172.179.48:19019/link/KDfh2ogZ')
        // let page = window.open('http://localhost:19019/link/KDfh2ogZ')
        page.onload=()=>{
          page.document.title = "新致云诊所"
        }
      },
    },
    mounted(){
      this.openRegistration();
    }
}
</script>
