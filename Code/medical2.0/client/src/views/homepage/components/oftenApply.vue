<template>
  <el-card class="index-module-card">
    <!-- 标题 -->
    <div slot="header">
      <span class="el-card-title">常用流程</span>
      <i
        v-show="!isPanelSetIcon"
        class="el-icon-close"
        style="float: right; padding: 3px 0"
        @click="deletePanelItem"
      ></i>
      <i
        v-show="isPanelSetIcon"
        class="iconfont icon-setting"
        style="float: right; padding: 3px 0"
      ></i>
    </div>
    <!-- 流程区域 -->
    <div class="often-apply-box">
      <div class="apply-item">
        <el-avatar class="avatar-icon" :size="50"> user </el-avatar>
        <span class="apply-title">请假流程</span>
      </div>
      <div class="apply-item">
        <el-avatar class="avatar-icon" :size="50"> user </el-avatar>
        <span class="apply-title">测试实施流程</span>
      </div>
      <div class="apply-item">
        <el-avatar class="avatar-icon" :size="50"> user </el-avatar>
        <span class="apply-title">测试实施流程</span>
      </div>
      <div class="apply-item">
        <el-avatar class="avatar-icon" :size="50"> user </el-avatar>
        <span class="apply-title">测试实施流程</span>
      </div>
      <div class="apply-item">
        <el-avatar class="avatar-icon" :size="50"> user </el-avatar>
        <span class="apply-title">测试实施流程</span>
      </div>
    </div>
  </el-card>
</template>

<script>
export default {
  props: ['id', 'panelSetIcon'],
  data () {
    return {
      panelId: this.id,
      isPanelSetIcon: this.panelSetIcon
    }
  },
  watch:{
    panelSetIcon:{
      deep:true,
      handler(){
        this.isPanelSetIcon = this.panelSetIcon;
      }
    }
  },
  methods: {
    // 删除面板项发送事件
    deletePanelItem () {
      this.$emit('deletePanelItemEvent', this.panelId)
    }
  }
}
</script>

<style lang="scss" scoped>
  .index-module-card.el-card /deep/ .el-card__header {
    padding: 10px 16px!important;
    .card-close, .card-setting {
      float: right;
      padding: 3px 0
    }
  }
.el-card {
  height: 100%;
}
.el-icon-close {
  font-size: 1.2em;
  cursor: pointer;
}
.el-card-title {
  font-weight: bold;
}
.often-apply-box {
  display: grid;
  grid-template-columns: repeat(1, 100%);
}
.often-apply-box .apply-item {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-bottom: 20px;
}
.apply-item .apply-title {
  margin-left: 10px;
}
.apply-item .avatar-icon {
  background-color: #2f7dcd !important;
}
</style>
