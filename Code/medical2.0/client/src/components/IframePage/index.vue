<template>
  <div class="iframe-container">
    <iframe
      :src="url"
      frameborder="0"
      allowfullscreen
      loading="lazy"
      class="iframe-content"
    />
  </div>
</template>

<script>
export default {
  name: 'IframePage',
  props: {
    url: {
      type: String,
      required: true
    }
  }
}
</script>

<style scoped>
.iframe-container {
  width: 100%;
  height: 100%;
}

.iframe-content {
  width: 100%;
  height: 1000px;
  border: none;
}
</style>
