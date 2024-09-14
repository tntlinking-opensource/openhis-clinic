<script>
export default {
  name: 'redirect',
  beforeRouteEnter(to, from, next) {
    next(vm => {
      vm.$router.replace(from.path)
    })
  },
  render: function(h) {
    return h()
  }
}
</script>
