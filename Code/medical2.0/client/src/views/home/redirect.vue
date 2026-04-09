<template>
  <div></div>
</template>

<script>
export default {
  name: 'Redirect',
  beforeCreate() {
    const { params, query } = this.$route
    const { to } = query

    // 如果有 to 参数，跳转到指定路径
    if (to) {
      this.$router.replace({
        path: to,
        query: query
      })
    } else {
      // 否则跳转到首页
      this.$router.replace('/homepage')
    }
  },
  render(h) {
    return h() // 避免渲染任何内容
  }
}
</script>
