<template>
  <el-card class="index-module-card" v-loading="isCardLoading" :style="styleObject">
    <div slot="header">
      <span class="el-card-title">个人工作台</span>
      <i style="margin-left: 10px;" v-show="!isPanelSetIcon" class="card-close el-icon-close" @click="deletePanelItem"></i>
      <i style="margin-left: 10px;" v-show="isPanelSetIcon" class="card-setting iconfont icon-setting"></i>
      <el-radio-group v-model="currentType" size="mini" class="card-radio-group" @change="handleTypeChange">
        <el-radio-button label="MY_TASK">我的待办</el-radio-button>
        <el-radio-button label="MY_PROCESS">我要申请</el-radio-button>
        <el-radio-button label="MY_APPLIED">我的申请</el-radio-button>
        <el-radio-button label="MY_APPROVED">我已审批</el-radio-button>
      </el-radio-group>
    </div>
    <div class="often-body" style="position: relative; min-height: 210px;">
      <div v-if="currentType === 'MY_TASK'" class="empty-placeholder">暂无待办</div>
      <div v-if="currentType === 'MY_PROCESS'" class="empty-placeholder">暂无申请</div>
      <div v-if="currentType === 'MY_APPLIED'" class="empty-placeholder">暂无记录</div>
      <div v-if="currentType === 'MY_APPROVED'" class="empty-placeholder">暂无记录</div>
    </div>
  </el-card>
</template>

<script>
  import { isLightOrDark } from '@/utils/common'
	export default {
		name: "WorkBench",
    props: ['id', 'panelSetIcon'],
    components: {},
    data () {
      return {
        isCardLoading: false,
        panelId: this.id,
        isPanelSetIcon: this.panelSetIcon,
        currentType: 'MY_TASK',
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
    computed: {
      ...Vuex.mapGetters(['settings']),
      isLight() {
        return (colorType) => {
          return isLightOrDark(this.settings[colorType])
        }
      },
      styleObject() {
        return {
          '--active-color': this.settings.theme
        }
      }
    },
    mounted() {

    },
    methods: {
      handleTypeChange(type) {
        this.currentType = type
      },
      deletePanelItem () {
        this.$emit('deletePanelItemEvent', this.panelId)
      }
    }
	}
</script>

<style lang="scss" scoped>
  .el-card {
    height: 100%;
  }
  .index-module-card.el-card ::v-deep .el-card__header {
    padding: 10px 16px!important;
    .card-close, .card-setting {
      float: right;
      padding: 3px 0
    }
    ::v-deep .often-body {
      position: relative;
      min-height: 210px;
    }
    .el-card-title {
      font-weight: bold;
    }
  }
  .card-radio-group {
    float: right;
    ::v-deep .el-radio-button--mini .el-radio-button__inner {
      padding: 5px 15px;
    }
  }
  .title-icon {
    background-color: var(--active-color)!important;
    border-radius: 100%;
    padding: 5px;
    width: 22px;
    height: 22px;
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    top: 50%;
    margin-top: -11px;
    left: 0;
    i {
      color: #fff!important;
      font-size: 12px!important;
    }
  }
  .empty-placeholder {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 210px;
    color: #909399;
    font-size: 14px;
  }
  .card-pagination {
    position: absolute;
    bottom: 0;
    right: 0;
  }

  ul.data-list {
    list-style: none;
    padding: 0;
    margin: 0;
    li {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 34px;
      font-size: 12px;
      padding: 0 10px;
      &:nth-child(odd) {
        background-color: #fbfbfb;
      }
      &:hover {
        background-color: #f5f5f5;
      }
      span {
        display: inline-block;
        &:first-child {
          flex: 1;
          white-space: nowrap;
          text-overflow: ellipsis;
          overflow: hidden;
          display: inline-block;
          height: 100%;
          line-height: 34px;
          position: relative;
          padding-left: 32px;
        }
        &.text-span {
          padding: 0 20px;
          width: 200px;
          text-align: left;
        }
      }
    }
  }
</style>
