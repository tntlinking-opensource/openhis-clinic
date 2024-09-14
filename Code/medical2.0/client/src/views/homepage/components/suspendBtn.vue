<template>
  <div class="suspend-btn-container" v-drag :style="styleObject">
    <div class="suspend-btn">
      <el-tooltip effect="dark" content="编辑我的桌面" placement="left">
        <span class="opt-btn" @click.stop="handleConfig" :style="{color: isLight('theme')? 'rgba(0, 0, 0, 0.65)' : 'rgba(255, 255, 255, .8)'}">
          <i class="el-icon-setting"></i>
        </span>
      </el-tooltip>
      <div class="border-h" :style="{backgroundColor: isLight('theme')? 'rgba(0, 0, 0, 0.65)' : 'rgba(255, 255, 255, .8)'}"></div>
      <el-tooltip effect="dark" content="回到顶部" placement="left">
        <span class="opt-btn" @click.stop="handleToTop" :style="{color: isLight('theme')? 'rgba(0, 0, 0, 0.65)' : 'rgba(255, 255, 255, .8)'}">
          <i class="el-icon-arrow-up"></i>
        </span>
      </el-tooltip>
    </div>
  </div>
</template>

<script>
  import { isLightOrDark } from '@/utils/common'
	export default {
		name: "SuspendBtn",
    directives: {
      drag (el, binding, vnode) {
        let oDiv = el;
        let startTime = 0;
        oDiv.onmousedown = function (e) {
          startTime = e.timeStamp
          let disY = e.clientY - oDiv.offsetTop
          document.onmousemove = (e) => {
            let t = e.clientY - disY
            oDiv.style.top = t + 'px'
          }
          document.onmouseup = (e) => {
            if (startTime) {
              let diffTime = e.timeStamp - startTime
              diffTime < 150 && vnode.context.handleClick()
              startTime = 0
            }
            document.onmousemove = null
            document.onmouseup = null
          }
          return false
        }
      }
    },
    computed: {
      ...Vuex.mapGetters(['settings']),
      isLight() {
        return function(colorType) {
          return isLightOrDark(this.settings[colorType])
        }
      },
      styleObject() {
        return {
          '--active-color': this.settings.theme
        }
      }
    },
    methods: {
      handleConfig() {
        this.$emit('handleConfig')
      },
      handleToTop() {
        this.$emit('handleToTop')
      },
      handleClick(e) {}
    }
	}
</script>

<style scoped lang="scss">
  .suspend-btn-container {
    position: fixed;
    right: 20px;
    width: 40px;
    bottom: 150px;
    z-index: 1000;
    &:hover {
      cursor: move;
    }
    .suspend-btn {
      border-radius: 24px;
      background: var(--active-color)!important;
      box-shadow: 0 2px 14px 0 rgba(0,0,0,.3);
      overflow: hidden;
      .border-h {
        height: 1px;
        width: 20px;
        margin: 5px auto;
        opacity: .5;
      }
      .opt-btn {
        width: 30px;
        height: 30px;
        transition: all .2s cubic-bezier(.215,.61,.355,1);
        margin: 5px;
        position: relative;
        cursor: pointer;
        display: flex;
        justify-content: center;
        align-items: center;
        border-radius: 100%;
        /*&:after {
          content: '';
          position: absolute;
          height: 1px;
          width: 100%;
          left: 0;
          background-color: #f5f5f5;
          bottom: 0;
        }
        &:last-child {
          &:after {
            height: 0;
          }
        }*/
        &:hover {
          background: rgba(255,255,255, .08);
        }
      }
    }
  }
</style>
