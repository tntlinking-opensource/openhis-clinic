<template>
  <component v-if="existDataView" :reportType="3" :is="reportView"></component>
</template>
<script>
  import Base from './Base'
  export default {
    extends: Base,
    name: 'PrvEject',
    data() {
      return {

      }
    }
  }
</script>
