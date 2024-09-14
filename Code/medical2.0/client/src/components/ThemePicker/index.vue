<template>
  <el-color-picker
    v-model="themeVal"
    :predefine="predefine"
    class="theme-picker"
    popper-class="theme-picker-dropdown"
  />
</template>

<script>
  import theme from './mixins/theme'
	export default {
		name: "ThemePicker",
    mixins: [theme()],
    data() {
		  return {
        predefine: ['#018cb7','#409EFF', '#1890ff', '#2f4050','#212121','#11a983', '#13c2c2', '#6959CD'],
      }
    },
    watch: {

    },
    computed: {

    },
    methods: {

    }
	}
</script>

<style>
  .theme-message, .theme-picker-dropdown {
    z-index: 99999 !important;
  }

  .theme-picker .el-color-picker__trigger {
    height: 26px !important;
    width: 26px !important;
    padding: 2px;
  }

  .theme-picker-dropdown .el-color-dropdown__link-btn {
    display: none;
  }
</style>
