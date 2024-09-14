<template>
  <el-row v-loading='loading'>
    <div class="page-container">
      <!--页签   开始-->
      <el-tabs v-model="activeName" @tab-click="onHandleClickTabs">
        <el-tab-pane label="常规审查" name="first">
          <review></review>
        </el-tab-pane>
        <el-tab-pane label="随机抽取" name="second">
          <random-review></random-review>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-row>
</template>

<script>
import Review from "./components/review"
import RandomReview from "./components/randomReview"
import MainUI from '@/views/components/mainUI'
export default {
  extends: MainUI,
  components: {
    Review,
    RandomReview
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
