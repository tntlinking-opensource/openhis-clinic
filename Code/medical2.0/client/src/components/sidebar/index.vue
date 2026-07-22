<template>
  <!-- 外层容器：flex垂直布局，撑满父容器，确保版权沉底 -->
  <div class="sidebar-wrapper" :style="{ height: '100%' }">
    <el-menu
      class="nav-menu"
      :default-active="defaultActive"
      :background-color="settings.sidebarColor"
      :text-color="isLight('sidebarColor')? 'rgba(0, 0, 0, 0.65)' : 'rgb(191, 203, 217)'"
      :active-text-color="settings.theme"
      :default-openeds="defaultOpeneds"
      :collapse="isCollapse"
      :style="styleObject">
      <sidebar-item v-for="(router, index) in routers" :key="index" :item="router"></sidebar-item>
    </el-menu>

    <!-- 版权信息区域：左对齐 + 协议单独一行 + 沉底 + 超链接 -->
    <div
      class="sidebar-copyright"
      :style="{
        backgroundColor: settings.sidebarColor,
        color: isLight('sidebarColor') ? 'rgba(0, 0, 0, 0.5)' : 'rgba(255, 255, 255, 0.5)',
        fontSize: '9.5px',
        textAlign: 'left', // 左对齐
        padding: isCollapse ? '12px 12px' : '16px 20px',
        lineHeight: '1.6',
        borderTop: isLight('sidebarColor') ? '1px solid rgba(0,0,0,0.05)' : '1px solid rgba(255,255,255,0.05)',
        marginTop: 'auto'
      }">
      <!-- 第一行：版权信息 -->
      <div v-if="!isCollapse">Copyright © 2025 湖北天天数链技术有限公司</div>
      <!-- 第二行：协议前缀 -->
      <div v-if="!isCollapse" style="margin-top: 6px;">本系统软件源代码许可来源于</div>
      <!-- 第三行：协议名称（单独占一行） -->
      <div v-if="!isCollapse" style="margin-top: 2px;">
        <a
          href="https://open.tntlinking.com/communityTreaty"
          target="_blank"
          rel="noopener noreferrer"
          :style="{
            color: settings.theme, // 复用主题色作为链接色
            textDecoration: 'none',
            display: 'inline-block' // 确保单独一行的排版稳定性
          }">
          《天天开源软件（社区版）许可协议》
        </a>
      </div>

      <!-- 折叠状态下简短显示（居中，避免文字溢出） -->
      <div v-if="isCollapse" style="font-size: 10px; text-align: center;">©2025 天天数链</div>
    </div>
  </div>
</template>

<script>
  import { isLightOrDark } from '@/utils/common'
  import { getLocalToken } from '@/utils/auth'
  import SidebarItem from './SidebarItem'
  export default {
    name: 'sidebar',
    components: { SidebarItem },
    props: ['isCollapse', 'routers'],
    data() {
      return {
        defaultOpeneds: []
      }
    },
    watch: {
      routers() {
        // if (this.routers.length > 0) {
        //   this.defaultActive = this.routers[0].id
        //   this.$router.push(this.routers[0].url)
        // }
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
      },
      defaultActive() {
        const route = this.$route
        // if (route.meta.parentId && route.meta.parentId != 0) {
        //   this.getMenus(route.meta.parentId)
        // }
        if (route.meta.family && route.meta.parentId !== 0) {
          this.getMenus(route.meta.family[0])
        }
        return route.meta.routerId
      }
    },
    mounted() {

    },
    methods: {
      ...Vuex.mapActions({
        getMenus: 'menus/getMenus'
      }),
      handleOpenWindow(url) {
        if (url) {
          switch (url) {
            case 'screenView':
              this.handleScreenView()
              break
            case 'reportDesign':
              this.openReportDesign()
              break
            default:
              break
          }
        }
      },
      handleScreenView() {
        return false
        // 暂时禁用
        // let screenWindow = null;
        // this.openWindow(screenWindow)
      },
      openReportDesign() {
        const token = getLocalToken()
        window.open(process.env.REPORT_CLIENT_URL + "?accss_tocken=" + token, "dataease")
      },
      openWindow(view) {
        let routeData = this.$router.resolve({
          path: "/screenView"
        });
        if (!view) {
          let width = screen.availWidth;
          let height = screen.availHeight;
          window.name = 'index';
          view = window.open(
            routeData.href,
            "screenView",
            "menubar=0,scrollbar=0,resizable=1,location=0,status=0, top=10, left=10, width=" + width + ", height=" + height
          );
          view.focus();
        }
        //   let width = screen.availWidth;
        //   let height = screen.availHeight;
        // window.open(
        //   'https://www.hightopo.com/demo/Seari_AirPort',
        //   "screenView",
        //   "menubar=0,scrollbar=0,resizable=1,location=no,status=0, top=10, left=10, width=" + width + ", height=" + height
        // );
      }
    }
  };
</script>

<style lang="scss" scoped>
  a {
    text-decoration: none;
  }
  .el-menu .svg-icon {
    color: rgb(191, 203, 217);
    fill: currentColor;
  }
  .el-menu-item.is-active .svg-icon, .el-submenu__title.is-active .svg-icon {
    color: var(--active-color) !important;
    fill: currentColor!important;
  }
  .el-menu-item, .el-submenu__title {
    color: rgb(191, 203, 217);
    fill: currentColor;
  }
</style>
<style lang="scss">
  .nav-menu {
    .el-submenu__title:hover,
    .el-menu-item:hover {
      background-color: rgba(0,0,0,.08)!important;
    }
    .el-menu-item:focus, .el-menu-item:active {
      background-color: rgba(0,0,0,0)!important;
    }

    &.el-menu--collapse {
      .el-submenu > .el-submenu__title {
        > span {
          height: 0!important;
          width: 0!important;
          overflow: hidden;
          visibility: hidden!important;
          display: inline-block;
        }
        .el-submenu__icon-arrow {
          display: none;
        }
      }
    }
  }
  .el-menu--vertical .el-submenu .el-submenu__title,
  .el-menu--vertical .el-submenu.is-opened .el-submenu__title {
    &:hover {
      background-color: rgba(0,0,0,0)!important;
    }
    background-color: rgba(0,0,0,0)!important;
  }
  .el-menu-item:hover {
    background-color: rgba(0,0,0,.08)!important;
  }
  .sidebar-wrapper {
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }

  /* 可选：优化滚动条样式 */
  .nav-menu::-webkit-scrollbar {
    width: 4px;
  }
  .nav-menu::-webkit-scrollbar-thumb {
    background-color: rgba(0,0,0,0.1);
  }

  /* 核心布局：确保容器撑满高度，版权沉底 */
  .sidebar-wrapper {
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }

  /* 菜单占满剩余高度，内容超出可滚动 */
  .nav-menu {
    flex: 1;
    overflow-y: auto;
    overflow-x: hidden;
  }

  /* 优化链接hover效果（兼容scoped样式） */
  .sidebar-copyright a {
    color: var(--el-color-primary) !important; /* 复用element-plus主题色 */
    text-decoration: none;
  }

  .sidebar-copyright a:hover {
    text-decoration: underline !important;
  }

  /* 可选：优化滚动条样式 */
  .nav-menu::-webkit-scrollbar {
    width: 4px;
  }
  .nav-menu::-webkit-scrollbar-thumb {
    background-color: rgba(0,0,0,0.1);
  }

  /* 确保折叠状态下布局正常 */
  :deep(.el-menu--collapse) {
    width: 64px !important;
  }
</style>
