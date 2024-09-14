<template>
  <div>
    <template v-if="item.children.length == 0">
      <router-link :to="item.url" :key="item.name">
        <el-menu-item :index="item.id" @click="(item.url === '/screenView' || item.url === '/reportDesign') && handleOpenWindow(item.url)" >
          <svg-icon :iconClass="item.cssClass"/>
          <span slot="title">{{ item.name }}</span>
        </el-menu-item>
      </router-link>
    </template>
    <el-submenu :index="item.id" v-else>
      <template slot="title">
        <svg-icon :iconClass="item.cssClass"/>
        <span slot="title">{{ item.name }}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.id"
        :item="child"></sidebar-item>
    </el-submenu>
  </div>
</template>

<script>
  import { getLocalToken } from '@/utils/auth'
  export default {
    name: 'SidebarItem',
    props: ['item'],
    data() {
      return {

      }
    },
    watch: {

    },
    computed: {

    },
    mounted() {

    },
    methods: {
      handleOpenWindow(url) {
        if (url) {
          switch (url) {
            case '/screenView':
              this.handleScreenView()
              break
            case '/reportDesign':
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
  }
  .el-menu-item:hover {
    background-color: rgba(0,0,0,.08)!important;
  }
</style>
