<template>
  <div class="app-icon" :style="styleObject">
    <svg-icon class="icon" :iconClass="icon" :style="{fill: settings.theme}"/>
    <span class="angle l-t"></span>
    <span class="angle r-t"></span>
    <span class="angle l-b"></span>
    <span class="angle r-b"></span>
    <div class="icon-bg"></div>
  </div>
</template>

<script>
  import { isLightOrDark } from '@/utils/common'
	export default {
		name: "AppIcon",
    props: ['icon'],
    data() {
		  return {

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
    }
	}
</script>

<style scoped lang="scss">
.app-icon {
  margin: 0 auto;
  position: relative;
  width: 36px;
  height: 36px;
  border: 1px solid #dbdbdb;
  display: flex;
  justify-content: center;
  align-items: center;
  .angle {
    position: absolute;
    width: 6px;
    height: 6px;
    border: 2px solid var(--active-color)!important;
    transition: all .4s;
    &.l-t {
      border-right: 0!important;
      border-bottom: 0!important;;
      left: -1px;
      top: -1px
    }
    &.r-t {
      border-left: 0!important;;
      border-bottom: 0!important;;
      right: -1px;
      top: -1px
    }
    &.l-b {
      border-right: 0!important;;
      border-top: 0!important;;
      left: -1px;
      bottom: -1px;
    }
    &.r-b {
      border-left: 0!important;;
      border-top: 0!important;;
      right: -1px;
      bottom: -1px;
    }
  }
  .icon {
    font-size: 14px;
    transition: all .15s;
  }
  .icon-bg {
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: var(--active-color)!important;
    filter: alpha(opacity=8);
    -moz-opacity: .08;
    opacity: .08;
    z-index: 0;
  }
}
</style>
