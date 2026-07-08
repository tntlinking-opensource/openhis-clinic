<template>
  <el-row v-loading='loading'>
    <div class="page-container">
      <review-result-pre></review-result-pre>
    </div>
  </el-row>
</template>

<script>
import listViewMixin from '@/mixins/listViewMixin'
import MainUI from '@/views/components/mainUI'
import ReviewResultPre from "./components/reviewResultPre.vue";
export default {
  extends: MainUI,
  mixins: [listViewMixin],
  components: {
    ReviewResultPre,
  },
  data() {
    return {
    }
  }
}
</script>
