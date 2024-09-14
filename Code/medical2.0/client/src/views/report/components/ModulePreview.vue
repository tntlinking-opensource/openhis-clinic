<template>
  <div class="module-preview-container">
    <div>
      <component v-if="existDataView" :reportType="3" :is="reportView"></component>
    </div>
  </div>
</template>
<script>
  import Base from './Base'
  export default {
    extends: Base,
    name: 'ModulePreview',
    data() {
      return {

      }
    }
  }
</script>
<style scoped lang="scss">
  .module-preview-container {
    width: 100%;
    display: flex;
    height: calc(100vh - 114px);
    font-size: 20px;
    > div {
      flex: 1;
      /deep/ .bg {
        height: calc(100vh - 114px) !important;
      }
    }
  }
</style>
