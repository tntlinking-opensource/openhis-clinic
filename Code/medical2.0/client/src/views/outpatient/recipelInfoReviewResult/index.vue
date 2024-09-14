<template>
  <el-row v-loading='loading'>
    <div class="page-container">
      <review-result></review-result>
    </div>
  </el-row>
</template>

<script>
import ReviewResult from "./components/reviewResult"
import MainUI from '@/views/components/mainUI'
export default {
  extends: MainUI,
  components: {
    ReviewResult
  },
  data() {
    return {
    }
  },
  methods: {},
  watch: {
  },
  mounted() {
  }
}
</script>
