<template>
  <div class="search-item"><b class="label">{{label}}：</b>
    <slot name="default"></slot>
  </div>
</template>

<script>
export default {
  name: 'ViewColumnsSelect',
  components: {
  },
  data() {
      return {};
  },
  props: {
    label: {
      type: String
    }
  },
  watch:{
  },
  methods: {
  },
  mounted() {
  }
}
</script>

<style lang="scss" scoped>
  .search-item {
    padding: 0 15px;
    display:  flex;
    align-items: center;
    .label{
     flex-shrink: 0;
      flex-grow: 0;
      padding: 0 5px;
    }
  }
</style>
