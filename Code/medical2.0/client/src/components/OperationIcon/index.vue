<template>
  <el-tooltip v-if='text == "" || text == undefined' class=item effect=light :content='content' :placement='placement' @mouseenter.native='onMouseEnter' @mouseleave.native='onMouseLeave'>
    <i class="opt-icon" :class='iconName' :style='"color:" + leaveColor() + ";cursor:pointer;"' @click='onIconClik'></i>
    <!--<el-button :type="type" :icon="iconName" circle  @click='onIconClik'></el-button>-->
  </el-tooltip>
  <i  class="opt-icon" v-else :class='iconName' :style='"color:" + leaveColor() + ";cursor:pointer;"' @click='onIconClik'>{{text}}</i>


  <!--<el-button
    size="mini"
    type="text"
    :icon="iconName"
    :style='"color:" + leaveColor()'
    @click='onIconClik'
    @mouseenter.native='onMouseEnter'
    @mouseleave.native='onMouseLeave'>{{content}}</el-button>-->
</template>

<script>
export default {
  name: 'OperationIcon',
  props: {
    // icon classname
    iconName: {
      type: String,
      default: () => {return 'el-icon-picture'}
    },
    // 类型: primary、success、info、warning、danger
    type: {
      type: String,
      default: () => {return 'primary'}
    },
    // 显示的内容
    content: {
      type: String,
      default: () => {return '请设置提示信息'}
    },
    // Tooltip 的出现位置
    placement: {
      type: String,
      default: () => {return 'top-start'}
    },
    // 文本
    text: {
      type: String,
      default: () => {''}
    }
  },
  computed: {
    ...Vuex.mapGetters(['settings'])
  },
  methods: {
    onMouseEnter(e) {
      e.target.style.color = this.enterColor()
    },
    onMouseLeave(e) {
      e.target.style.color = this.leaveColor()
    },
    onIconClik(e) {
      this.$emit('click', e)
    },
    enterColor() {
      if(this.type == 'primary') {
        return this.settings.theme
      } else if(this.type == 'success') {
        return '#85ce61'
      } else if(this.type == 'warning') {
        return '#ebb563'
      } else if(this.type == 'danger') {
        return '#f78989'
      }
      return '#a6a9ad'
    },
    leaveColor() {
      if(this.type == 'primary') {
        return this.settings.theme
      } else if(this.type == 'success') {
        return '#67c23a'
      } else if(this.type == 'warning') {
        return '#e6a23c'
      } else if(this.type == 'danger') {
        return '#f56c6c'
      }
      return '#909399'
    }
  }
}
</script>
<style lang="scss" scoped>
  .opt-icon {
    font-size: 15px;
    margin: 0 2px;
  }
  .el-table--default {
    .opt-icon {
      font-size: 16px;
      margin: 0 2px;
    }
  }

  .el-table--mini {
    .opt-icon {
      font-size: 14px;
      margin: 0 2px;
    }
  }

</style>

