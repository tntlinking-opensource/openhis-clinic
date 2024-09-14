<template>
  <div class="screenLayout-container">
    暂时禁用，请联系管理员！
    <!-- <iframe style="width: 100%; height: 100%" src="http://localhost:9528/" frameborder="0"></iframe> -->
    <!--<el-button @click='fn'>测试</el-button>-->
  </div>
</template>

<script>
  import { getLocalToken } from '@/utils/auth'
	export default {
		name: "screenView",

    methods: {
      fn() {
        let token = getLocalToken()

        window.open(process.env.REPORT_URL + "?accss_tocken=" + token, "dataease")
      }
    }
	}


</script>

<style scoped lang="scss">
  .screenLayout {
    position: absolute;
    left:0;
    top: 0;
    right: 0;
    bottom: 0;
    .toolbar {
      position: absolute;
      height: 30px;
      background-color: rgba(0, 0, 0, .15);
      bottom: 0;
      width: 100%;
    }
  }
  .screenLayout-container {
    width: 100%;
    height: calc(100vh - 114px);
    display: flex;
    background-color: #f5f5f5;
    justify-content: center;
    align-items: center;
    color: #E6A23C;
    font-size: 20px;
  }
</style>

