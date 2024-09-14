<template>
  <el-upload
    ref="imageUpload"
    style="width: 90px"
    class="upload-item"
    action="#"
    :headers="uploadHeaders"
    accept=".jpg,.jpeg,.png,.ico"
    :auto-upload="false"
    :on-success="onSuccess"
    :on-change="onChange"
    :on-preview="onPreview"
    :on-remove="onRemove"
    :before-remove="()=>false"
    :multiple="false"
    :before-upload="beforeUpload"
    :on-exceed="onExceed"
    :http-request="httpRequest"
    :file-list="fileList"
    list-type="picture">
    <el-button size="mini">选择图片</el-button>
  </el-upload>
</template>

<script>
  import { getLocalToken } from '@/utils/auth'
	export default {
		name: "imageUpload",
    props: {
      fileList: {
        type: Array
      },
      onChange: {
        type: Function
      },
      beforeRemove: {
        type: Function
      },
      beforeUpload: {
        type: Function
      },
      onRemove: {
        type: Function
      },
      onExceed: {
        type: Function
      },
      onSuccess: {
        type: Function
      },
      onPreview: {
        type: Function
      },
      httpRequest: {
        type: Function
      }
    },
    data() {
		  return {
        uploadHeaders: {'X-Token': getLocalToken()},
      }
    },
    methods: {
      handleExceed(files, fileList) {
        this.$message.warning(`限制只能选择1个文件`)
      },
      submit() {
        this.$refs.imageUpload.submit()
      }
    }
	}
</script>

<style scoped>
  .upload-item {
    text-align: right;
  }
</style>
