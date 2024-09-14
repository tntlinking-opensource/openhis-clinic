<template>
  <el-menu class="nav-menu"
           :default-active="defaultActive"
           :background-color="settings.sidebarColor"
           :text-color="isLight('sidebarColor')? 'rgba(0, 0, 0, 0.65)' : 'rgb(191, 203, 217)'"
           :active-text-color="settings.theme"
           :default-openeds="defaultOpeneds"
           :collapse="isCollapse"
           :style="styleObject">
    <sidebar-item v-for="(router, index) in routers" :key="index" :item="router"></sidebar-item>
  </el-menu>
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
        if (route.meta.family && route.meta.parentId != 0) {
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
</style>
