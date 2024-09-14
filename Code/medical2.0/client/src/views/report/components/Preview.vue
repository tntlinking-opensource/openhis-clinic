<template>
  <component v-if="existDataView" :reportType="2" :is="reportView"></component>
</template>
<script>
  import Base from './Base'
  export default {
    extends: Base,
    name: 'Preview',
    components: {
    },
    data() {
      return {
      }
    },
    mounted() {
    },
    methods: {

    }
  }
</script>
