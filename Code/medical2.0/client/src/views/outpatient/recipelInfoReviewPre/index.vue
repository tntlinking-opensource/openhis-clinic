<template>
  <el-row v-loading='loading'>
    <div class="page-container">
      <!--页签   开始-->
      <el-tabs v-model="activeName" >
        <el-tab-pane label="处方审核" name="first">
          <reviewPre></reviewPre>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-row>
</template>

<script>
import ReviewPre from "./components/reviewPre.vue"

import MainUI from '@/views/components/mainUI'
export default {
  extends: MainUI,
  components: {
    ReviewPre
  },
  data() {
    return {
      activeName: 'first'
    }
  },
  methods: {},
  watch: {
  },
  mounted() {
  }
}
</script>
