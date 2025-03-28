<template>
  <el-row v-loading='loading'>
    <div class="page-container">
      <review-result-pre></review-result-pre>
    </div>
  </el-row>
</template>

<script>
import ReviewResult from "./components/reviewResultPre.vue"
import MainUI from '@/views/components/mainUI'
import ReviewResultPre from "./components/reviewResultPre.vue";
export default {
  extends: MainUI,
  components: {
    ReviewResultPre,
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
