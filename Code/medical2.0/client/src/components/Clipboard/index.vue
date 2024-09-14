<template>
  <el-tooltip :content="tipText" :placement="placement" v-bind="$attrs">
    <el-button
      :icon="icon"
      :type="type"
      v-bind="$attrs"
      @click="handleCopy(content)">复制</el-button>
  </el-tooltip>
</template>

<script>
	export default {
		name: "Clipboard",
    props: {
      content: {
        type: String
      },
      icon: {
        type: String,
        default: () => 'el-icon-document-copy'
      },
      tipText: {
        type: String,
        default: () => '复制'
      },
      placement: {
        type: String,
        default: () => 'top'
      },
      type: {
        type: String,
        default: () => 'text'
      }
    },
    data() {
		  return {

      }
    },
    methods: {
      handleCopy(content) {
        if (content) {
          // 渐进式使用，暂不考虑兼容性
          let input = document.createElement('input')
          input.value = content
          document.body.appendChild(input)
          input.select()
          document.execCommand("Copy")
          input.remove()
          this.$message({
            type: 'success',
            message: '复制成功！'
          })
        }
      }
    }
	}
</script>

<style scoped>

</style>
