<template>
  <div>
    <div v-if="layoutType=='layout'" ref="rightPanel" :class="{panelShow: layoutPanelShow}" class="rightPanel-container">
      <div class="rightPanel-background" @click="panelShadowCLick"/>
      <div class="rightPanel">
        <div v-if="false" ref="handleBtn" class="handle-button" v-drag :style="{'top': buttonTop +'px', 'background-color': theme}" >
          <i :class="panelShow? 'el-icon-close' : 'el-icon-setting'" />
        </div>
        <div class="rightPanel-items">
          <slot :name="layoutType"/>
        </div>
      </div>
    </div>
    <div v-if="sysType=='sys'" ref="rightPanel" :class="{panelShow: sysPanelShow}" class="rightPanel-container">
      <div class="rightPanel-background" @click="panelShadowCLick"/>
      <div class="rightPanel">
        <div v-if="false" ref="handleBtn" class="handle-button" v-drag :style="{'top': buttonTop +'px', 'background-color': theme}" >
          <i :class="panelShow? 'el-icon-close' : 'el-icon-setting'" />
        </div>
        <div class="rightPanel-items">
          <slot :name="sysType"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
	export default {
		name: "RightPanel",
    props: {
      clickNotClose: {
        default: false,
        type: Boolean
      },
      buttonTop: {
        default: 250,
        type: Number
      }
    },
    data() {
		  return {
        elIsMove: false,
        layoutType: '',
        sysType: '',
      }
    },
    directives: {
      drag (el, binding, vnode) {
        let oDiv = el;
        let startTime = 0;
        document.onselectstart = () => {
          return false
        }
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
    mounted() {
      this.layoutType = this.layout
      this.sysType = this.system
    },
    watch: {

    },
    computed: {
		  ...Vuex.mapState({
        layout: (state) => state.settings.type,
        system: (state) => state.sys.type,
        theme: (state) => state.settings.theme,
        layoutPanelShow: (state) => state.settings.panelShow,
        sysPanelShow: (state) => state.sys.panelShow
      }),
      ...Vuex.mapGetters(['settings', 'sys'])
    },
    methods: {
      ...Vuex.mapActions({
        showLayoutPanel: 'settings/showPanel',
        showSysPanel: 'sys/showPanel',
      }),
      handleClick() {
        this.panelShow = !this.panelShow
      },
      panelShadowCLick() {
        this.showLayoutPanel(false);
        this.showSysPanel(false);
      }
    },
    beforeDestroy() {
      const elx = this.$refs.rightPanel
      elx.remove()
    }
	}
</script>

<style>
  .showRightPanel {
    overflow: hidden;
    position: relative;
    width: calc(100% - 15px);
  }
</style>

<style lang="scss" scoped>
  .rightPanel-background {
    position: fixed;
    top: 0;
    left: 0;
    opacity: 0;
    transition: opacity .3s cubic-bezier(.7, .3, .1, 1);
    background: rgba(0, 0, 0, .2);
    z-index: -1;
  }

  .rightPanel {
    width: 100%;
    max-width: 300px;
    height: 100vh;
    position: fixed;
    overflow-y: auto;
    top: 0;
    right: 0;
    box-shadow: 0px 0px 15px 0px rgba(0, 0, 0, .05);
    transition: all .25s cubic-bezier(.7, .3, .1, 1);
    transform: translate(100%);
    background: #fff;
    z-index: 2000;
  }

  .panelShow {
    transition: all .3s cubic-bezier(.7, .3, .1, 1);

    .rightPanel-background {
      z-index: 1999;
      opacity: 1;
      width: 100%;
      height: 100%;
    }

    .rightPanel {
      transform: translate(0);
    }
  }

  .handle-button {
    width: 36px;
    height: 36px;
    position: absolute;
    left: -36px;
    text-align: center;
    font-size: 14px;
    border-radius: 6px 0 0 6px !important;
    z-index: 0;
    pointer-events: auto;
    cursor: pointer;
    color: #fff;
    i {
      font-size: 14px;
      line-height: 36px;
    }
  }
</style>
