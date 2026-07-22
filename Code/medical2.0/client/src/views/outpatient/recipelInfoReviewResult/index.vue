<template>
  <el-row v-loading='loading'>
    <div class="page-container">
      <review-result></review-result>
    </div>
  </el-row>
</template>

<script>
import ReviewResult from "./components/reviewResult"
import listViewMixin from '@/mixins/listViewMixin'
import MainUI from '@/views/components/mainUI'
export default {
  extends: MainUI,
  mixins: [listViewMixin],
  components: {
    ReviewResult
  },
  data() {
    return {
    }
  }
}
</script>
