<template>
  <div class="layout-container" :class="[layout.style , layout.fixedHeader? 'fixed-header' : '', isCollapse? 'is-collapse':'']">
    <!-- 顶部导航 Header -->
    <nav-header @openPersonalInfoDialog="openPersonalInfoDialog"
                ref="NavHeader"
                :isCollapse="isCollapse"
                v-if="layout.style==='up-down'"
                :routers="routers"
                :sysLogo="sysLogo"
                @hamburger="getHamburgerStatus"
                @openSetting="onOpenSetting">
    </nav-header>

    <!-- Tab 标签页栏 - 固定定位，不随滚动移动 -->
    <div class="tabs-bar" :class="{ 'is-fixed': layout.fixedHeader }" v-if="visitedViews.length > 0">
      <div class="tabs-wrapper">
        <el-tabs
          v-model="activeTab"
          type="card"
          closable
          @tab-click="handleTabClick"
          @tab-remove="handleTabRemove"
        >
          <el-tab-pane
            v-for="item in visitedViews"
            :key="item.path"
            :label="item.title"
            :name="item.path"
            :closable="isClosable(item)"
          >
            <span slot="label" @contextmenu.prevent.stop="handleContextMenu($event, item)">
              <i v-if="item.meta && item.meta.cssClass" :class="item.meta.cssClass"></i>
              {{ item.title }}
            </span>
          </el-tab-pane>
        </el-tabs>

        <!-- 右键菜单 -->
        <ul
          v-show="contextMenuVisible"
          :style="{left: contextMenuLeft + 'px', top: contextMenuTop + 'px'}"
          class="contextmenu"
          ref="contextMenu"
          @contextmenu.prevent
        >
          <li @click.stop="refreshSelectedTag(selectedTag)">
            <i class="el-icon-refresh"></i> 刷新页面
          </li>
          <li v-if="isClosable(selectedTag)" @click.stop="closeSelectedTag(selectedTag)">
            <i class="el-icon-close"></i> 关闭标签
          </li>
          <li @click.stop="closeOthersTags">
            <i class="el-icon-circle-close"></i> 关闭其他
          </li>
          <li @click.stop="closeAllTags">
            <i class="el-icon-error"></i> 关闭全部
          </li>
        </ul>
      </div>

      <!-- 收藏列表 -->
      <div class="collect-container">
        <book-mark class="book-mark"></book-mark>
      </div>
    </div>

    <!-- 占位元素，防止内容被固定的 tab 栏遮挡 -->
    <div class="tabs-placeholder" v-if="visitedViews.length > 0 && layout.fixedHeader"></div>

    <el-container class="body-container" :style="">
      <!--工作区-->
      <el-main class="main" :style="{backgroundColor: settings.backgroundColor}">
        <div v-show="false" class="mask-layout" :class="isCollapse? 'is-collapse':''" v-loading="true"></div>
        <div class="main-content">
          <transition name="fade" mode="out-in">
            <keep-alive :include="cachedViews">
              <router-view :key="key" />
            </keep-alive>
          </transition>
        </div>
        <!-- 右边栏 -->
        <right-panel ref="settings">
          <Settings ref="layout" slot="layout"/>
          <Sys ref="sys" slot="sys"></Sys>
        </right-panel>
      </el-main>
    </el-container>

    <personal-info ref="personalInfo" v-on:username-changed="doGetCurrentUsername"></personal-info>

  </div>
</template>

<script>
import Sidebar from '@/components/sidebar'
import BaseUI from '@/views/components/baseUI'
import RightPanel from '@/components/RightPanel'
import Settings from '../components/Settings'
import Sys from '../components/Sys'
import PersonalInfo from '@/views/admin/common/personalInfo'
import BookMark from './components/bookmark'
import logoImage from '@/assets/images/Logo.png'
import NavHeader from './components/header'
import { isLightOrDark } from '@/utils/common'
import { getLocalRouters, getLocalSysSetting, getLocalCurrentUser } from '@/utils/auth'

export default {
  extends: BaseUI,
  data() {
    return {
      sysLogo: logoImage,
      isCollapse: false,
      header: 'Jeeke demo',
      currentUsername: '',
      userId: currentUser.id,

      webSocketUrl:process.env.WEB_SOCKET_URL,
      websocket:null,
      lockReconnect: false,
      timeout: 300 * 1000,
      timeoutObj:  null,
      serverTimeoutObj:  null,
      timeoutNum:  null,

      // 标签页相关数据
      activeTab: '',
      visitedViews: [],
      cachedViews: [],
      contextMenuVisible: false,
      contextMenuLeft: 0,
      contextMenuTop: 0,
      selectedTag: {},
      affixTags: ['/homepage', '/']
    }
  },
  destroyed(){
    if(this.websocket){
      this.websocket.close()
    }
    document.removeEventListener('click', this.handleDocumentClick)
  },
  watch: {
    $route: {
      immediate: true,
      handler(route) {
        this.addView(route)
        this.moveToCurrentTag(route)
      }
    }
  },
  computed: {
    layout() {
      const { style, fixedHeader, showLogo } = this.settings;
      return {
        style,
        fixedHeader,
        showLogo,
      }
    },

    isLight() {
      return function(colorType) {
        return isLightOrDark(this.settings[colorType])
      }
    },

    routers() {
      let items = [];
      let routers = getLocalRouters()
      for (let router of routers) {
        let routerProperties = JSON.parse(router.properties)
        let item = {
          id: router.id,
          code: router.code,
          name: router.name,
          url: router.url,
          level: router.level,
          parentId: router.parent && router.parent.id ? router.parent.id: '',
          nameFullPath: routerProperties.nameFullPath,
          cssClass: routerProperties.cssClass,
          component: routerProperties.component
        }
        items.push(item)
      }
      return items
    },

    key() {
      return this.$route.path
    },

    ...Vuex.mapGetters(['menus', 'breadcrumbItems', 'settings', 'sys'])
  },
  mounted() {
    console.log(this.$router,'====');
    this.connectWebSocket()
    let setting = getLocalSysSetting()
    this.sysLogo =   require('@/assets/images/projectLogo.png');
    this.changeWebTitle(setting.sysAbbrname)
    this.doGetCurrentUsername();
    if(this.$store.getters.toPath){
      let routers = getLocalRouters()
      const url = this.removeBlock(JSON.stringify( {url:this.$store.getters.toPath.substring(1)} ));
      if(routers.includes(url)){
        this.$router.push(this.$store.getters.toPath);
      }
    }
    this.initTags()

    this.$nextTick(() => {
      document.addEventListener('click', this.handleDocumentClick)
    })
  },
  beforeCreate() {
    window.currentUser = getLocalCurrentUser();

  },
  methods: {
    handleDocumentClick(e) {
      const contextMenu = this.$refs.contextMenu
      if (contextMenu && !contextMenu.contains(e.target)) {
        this.closeContextMenu()
      }
    },

    connectWebSocket(){
      if(this.userId){
        try {
          if ('WebSocket' in window) {
            this.websocket = new WebSocket(this.webSocketUrl+this.userId);
            this.initWebSocket()
          }
        }catch (e) {
          this.reconnect();
          console.log(e)
        }
      }
    },
    start(){
      const self = this;
      self.timeoutObj && clearTimeout(self.timeoutObj);
      self.serverTimeoutObj && clearTimeout(self.serverTimeoutObj);
      self.timeoutObj = setTimeout(function(){
        if (self.websocket.readyState === 1) {
          self.websocket.send("heartCheck");
        }else{
          self.reconnect();
        }
        self.serverTimeoutObj = setTimeout(function() {
          self.websocket.close();
        }, self.timeout);
      }, self.timeout)
    },
    reset(){
      clearTimeout(this.timeoutObj);
      clearTimeout(this.serverTimeoutObj);
      this.start();
    },
    reconnect() {
      const that = this;
      if(that.lockReconnect) {
        return;
      }
      that.lockReconnect = true;
      that.timeoutnum && clearTimeout(that.timeoutnum);
      that.timeoutnum = setTimeout(function () {
        that.connectWebSocket();
        that.lockReconnect = false;
      },20 * 1000);
    },
    initWebSocket(){
      this.websocket.onerror = this.setErrorMessage;
      this.websocket.onopen = this.setOnopenMessage;
      this.websocket.onmessage = this.setOnmessageMessage;
      this.websocket.onclose = this.setOncloseMessage;
    },
    setOnopenMessage(){
      console.log("连接成功");
      this.start();
    },
    setOnmessageMessage(event){
      this.reset();
      if("" === event.data){
        return  false;
      }
      const {total,data} =  JSON.parse(event.data);
      let that = this;
      this.$store.dispatch('notices/SetNoticeTotal',Number(total));
      if(total && Number(total) > 0){
        this.$notify.closeAll();
        this.$nextTick(()=>{
          let message = "<div class='msg-box'><ul class='msg-list'>";
          data.forEach((item) => {
            message += "<li>" + item.title + "</li>";
          });
          message += "</ul></div>";
          this.$notify({
            title: total+'条：未读消息通知',
            duration: Number(total) * 1500,
            position: 'bottom-right',
            dangerouslyUseHTMLString: true,
            message:message,
            onClick:function(){
              that.$refs.NavHeader.unreadNotice();
            },
          });
        });
      }
    },
    setOncloseMessage(){
      console.log('连接关闭');
      this.reconnect();
    },
    setErrorMessage(){
      console.log("连接异常");
      this.reconnect();
    },

    ...Vuex.mapActions({
      showPanel: 'settings/showPanel',
    }),

    initTags() {
      const route = this.$route
      if (route.path !== '/login') {
        this.addView(route)
      }
    },

    isActive(route) {
      return route.path === this.$route.path
    },

    isClosable(view) {
      if (!view || !view.path) return false
      if (this.affixTags.includes(view.path)) return false
      if (view.meta && view.meta.affix) return false
      return true
    },

    isAffix(view) {
      if (!view || !view.path) return false
      return this.affixTags.includes(view.path) || (view.meta && view.meta.affix)
    },

    addView(route) {
      const { name } = route
      if (name) {
        const hasVisited = this.visitedViews.some(v => v.path === route.path)
        if (!hasVisited) {
          this.visitedViews.push({
            name: route.name,
            path: route.path,
            title: route.meta && route.meta.name || route.name || '无标题',
            meta: route.meta || {}
          })
        }

        if (!route.meta || !route.meta.noCache) {
          if (!this.cachedViews.includes(name)) {
            this.cachedViews.push(name)
          }
        }
      }
      this.activeTab = route.path
    },

    moveToCurrentTag(route) {
      this.activeTab = route.path
    },

    handleTabClick(tab) {
      const route = this.visitedViews.find(v => v.path === tab.name)
      if (route && route.path !== this.$route.path) {
        this.$router.push(route.path)
      }
    },

    handleTabRemove(targetName) {
      const view = this.visitedViews.find(v => v.path === targetName)
      if (view && this.isClosable(view)) {
        this.closeSelectedTag(view)
      }
    },

    handleContextMenu(e, tag) {
      e.preventDefault()
      e.stopPropagation()

      if (!tag) return
      this.selectedTag = tag

      this.contextMenuLeft = e.clientX
      this.contextMenuTop = e.clientY

      this.$nextTick(() => {
        this.contextMenuVisible = true
      })
    },

    closeContextMenu() {
      this.contextMenuVisible = false
    },

    closeSelectedTag(view) {
      this.closeContextMenu()
      this.visitedViews = this.visitedViews.filter(v => v.path !== view.path)
      this.cachedViews = this.cachedViews.filter(v => v !== view.name)

      if (this.isActive(view)) {
        this.toLastView(this.visitedViews, view)
      }
    },

    closeOthersTags() {
      this.closeContextMenu()
      const selected = this.selectedTag
      if (!selected || !selected.path) return

      this.visitedViews = this.visitedViews.filter(v => {
        return this.isAffix(v) || v.path === selected.path
      })
      this.cachedViews = this.visitedViews.filter(v => {
        return !v.meta || !v.meta.noCache
      }).map(v => v.name)

      if (!this.isActive(selected)) {
        this.$router.push(selected.path)
      }
    },

    closeAllTags() {
      this.closeContextMenu()
      this.visitedViews = this.visitedViews.filter(v => this.isAffix(v))
      this.cachedViews = this.visitedViews.filter(v => {
        return !v.meta || !v.meta.noCache
      }).map(v => v.name)

      if (this.visitedViews.length > 0) {
        const currentPath = this.$route.path
        const isCurrentAffix = this.visitedViews.some(v => v.path === currentPath)
        if (!isCurrentAffix) {
          this.$router.push(this.visitedViews[0].path)
        }
      } else {
        if (this.$route.path !== '/homepage') {
          this.$router.push('/homepage')
        }
      }
    },

    toLastView(visitedViews, view) {
      const latestView = visitedViews.slice(-1)[0]
      if (latestView) {
        this.$router.push(latestView.path)
      } else {
        if (view.path !== '/homepage' && view.path !== '/') {
          this.$router.push('/homepage')
        }
      }
    },

    refreshSelectedTag(view) {
      this.closeContextMenu()

      const index = this.cachedViews.indexOf(view.name)
      if (index > -1) {
        this.cachedViews.splice(index, 1)
      }

      this.$nextTick(() => {
        this.$router.replace({
          path: '/redirect',
          query: {
            to: this.$route.fullPath
          }
        })
      })
    },

    openPersonalInfoDialog() {
      this.$refs.personalInfo.$emit('openPersonalInfoDialog')
    },

    doGetCurrentUsername() {
      this.currentUsername = currentUser.name
    },

    getHamburgerStatus(collapse) {
      this.isCollapse = collapse;
      this.header = collapse? 'Jeeke' : 'Jeeke demo'
    },

    getWindowHeight() {
      const winHeight = document.documentElement.clientHeight || document.body.clientHeight;
      return this.layout.style === 'up-down'? winHeight - 56 : winHeight;
    },
    removeBlock (str) {
      if (str) {
        const reg = /^\{/gi;
        const reg2 = /\}$/gi;
        str = str.replace(reg, '');
        str = str.replace(reg2, '');
        return str
      } else {
        return str
      }
    },
    onOpenSetting(type, onType) {
      this.$nextTick(() => {
        this.$refs[type].$emit(onType)
      })
    },
    changeFavicon(icon) {
      let link = document.querySelector("link[rel*='Icon']") || document.createElement('link');
      link.type = 'image/x-icon';
      link.rel = 'Shortcut Icon';
      link.href = icon;
      document.getElementsByTagName('head')[0].appendChild(link);
    },
    changeWebTitle(title) {
      document.title = title
    },
  },
  components: { Sidebar, RightPanel, Settings, Sys, NavHeader, PersonalInfo, BookMark }
};
</script>

<!-- 全局样式 -->
<style lang="scss">
@import '../../assets/scss/common.scss';
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
.logoFade-enter,
.logoFade-leave-to {
  opacity: 0;
}
.logoFade-enter-to,
.logoFade-leave {
  opacity: 1;
}
.logoFade-enter-active,
.logoFade-leave-active {
  transition: all .1s;
}

.layout-container {
  min-height: 100%;
  position: relative;
}

// Tab 栏样式 - 固定定位
.tabs-bar {
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0,0,0,.12), 0 0 3px 0 rgba(0,0,0,.04);
  height: 37px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
  position: relative;
  z-index: 10;

  // 固定定位模式
  &.is-fixed {
    position: fixed;
    top: 56px; // header 高度
    left: 0;
    right: 0;
    z-index: 100;
  }

  .tabs-wrapper {
    flex: 1;
    overflow: hidden;
    position: relative;

    /deep/ .el-tabs {
      .el-tabs__header {
        margin: 0;
        border-bottom: none;

        .el-tabs__nav-wrap {
          &::after {
            display: none;
          }
        }

        .el-tabs__nav {
          border: none;
          border-radius: 0;
        }

        .el-tabs__item {
          height: 28px;
          line-height: 28px;
          border: 1px solid #d8dce5;
          border-radius: 3px;
          margin-right: 5px;
          margin-top: 4px;
          padding: 0 12px;
          font-size: 12px;
          color: #495060;
          background: #fff;
          transition: all .3s;

          &:hover {
            color: #409EFF;
          }

          &.is-active {
            background-color: #409EFF;
            color: #fff;
            border-color: #409EFF;

            &::before {
              content: '';
              background: #fff;
              display: inline-block;
              width: 8px;
              height: 8px;
              border-radius: 50%;
              position: relative;
              margin-right: 4px;
            }
          }

          .el-icon-close {
            width: 14px;
            height: 14px;
            line-height: 14px;
            border-radius: 50%;
            margin-left: 4px;
            transition: all .3s;

            &:hover {
              background-color: rgba(255,255,255,0.3);
              color: #fff;
            }
          }

          &:not(.is-closable) {
            padding: 0 15px;
          }
        }
      }

      .el-tabs__content {
        display: none;
      }
    }
  }

  .collect-container {
    flex-shrink: 0;
    margin-left: 10px;
    line-height: 28px;
  }
}

// 占位元素 - 防止内容被固定的 tab 栏遮挡
.tabs-placeholder {
  height: 37px;
  width: 100%;
}

// 右键菜单样式
.contextmenu {
  position: fixed;
  margin: 0;
  background: #fff;
  z-index: 9999;
  list-style-type: none;
  padding: 5px 0;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 400;
  color: #333;
  box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, .3);
  border: 1px solid #ebeef5;
  min-width: 120px;

  li {
    margin: 0;
    padding: 8px 16px;
    cursor: pointer;
    display: flex;
    align-items: center;
    transition: all 0.2s;

    i {
      margin-right: 8px;
      font-size: 14px;
      color: #909399;
    }

    &:hover {
      background: #f5f7fa;
      color: #409EFF;

      i {
        color: #409EFF;
      }
    }

    &:not(:last-child) {
      border-bottom: 1px solid #f0f0f0;
    }
  }
}

// 主体容器
.body-container {
  height: 100%;
  width: 100%;
  min-width: 1120px;
  overflow: hidden;

  // 当 tab 固定时，给 body-container 添加顶部间距
  .layout-container.fixed-header & {
    padding-top: 37px; // tab 栏高度
  }
}

.main {
  margin: 0px;
  padding: 0px;
  width: 100%;

  .main-content {
    margin: 10px;
  }
}

// 其他原有样式...
.fixed-header {
  &.up-down {
    .body-container {
      position: absolute;
      height: auto!important;
      top: 56px;
      bottom: 0;
    }
    .main {
      margin-top: 0;
    }
  }
  &.left-right {
    .body-container {
      position: absolute;
      height: auto!important;
      top: 0;
      bottom: 0;
    }
    .main {
      margin-top: 0;
    }
  }
}

.aside-header {
  color: #fff;
  font-size: 16pt;
  text-align: center;
  margin: 0px auto;
  padding: 0px;
  width: 200px;
  height: 56px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  .sysLogo {
    max-width: 100%;
    max-height: 100%;
    width: auto;
    height: auto;
    display: block;
    object-fit: contain;
  }
}

.nav-menu:not(.el-menu--collapse) {
  width: 200px;
  height: 100%;
  overflow: auto;
}

.hamburger {
  display: inline-block;
  cursor: pointer;
  width: 20px;
  height: 20px;
  transform: rotate(90deg);
  transition: 0.38s;
  transform-origin: 50% 50%;
  margin: 20px 0px 0px -10px;
}
.hamburger.is-active {
  transform: rotate(0deg);
}
.breadcrumb-container {
  float: left;
}
.collect-container {
  float: right;
}
.default-bread {
  float: left;
  margin-right: 10px;
  display: flex;
  justify-content: center;
  color: #C0C4CC;
  .el-link {
    margin-right: 10px;
    font-size: 12px;
  }
}
</style>

<style lang="scss">
.msg-box {
  height: 40px;
  width: 200px;
  overflow:hidden;
  margin:auto;
  position:relative;
}
@keyframes animation {
  100% {
    transform:translateY(-100px)
  }
}
.msg-list {
  animation:animation 10s linear infinite;
}
.msg-list:hover {
  animation-play-state:paused;
}
</style>
