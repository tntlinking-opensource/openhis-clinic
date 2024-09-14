<template>
  <div class="layout-container" :class="[layout.style , layout.fixedHeader? 'fixed-header' : '', isCollapse? 'is-collapse':'']">
    <nav-header @openPersonalInfoDialog="openPersonalInfoDialog"
                ref="NavHeader"
                :isCollapse="isCollapse"
                v-if="layout.style==='up-down'"
                :routers="routers"
                :sysLogo="sysLogo"
                @hamburger="getHamburgerStatus"
                @openSetting="onOpenSetting">
    </nav-header>
    <el-container class="body-container" :style="">
      <!--左边栏-->
      <div class="aside-container" :style="{minHeight: getWindowHeight() + 'px', backgroundColor: settings.sidebarColor}">
        <div
          v-if="layout.style==='left-right' && layout.showLogo"
          class="aside-header"
          :style="{color: isLight('sidebarColor')? 'rgba(0, 0, 0, 0.65)' : 'rgb(255, 255, 255)'}">
          <img v-if="!isCollapse" class="sysLogo" :src="sysLogo" alt="">
        </div>
        <sidebar :isCollapse="isCollapse" :routers="menus.menus"></sidebar>
      </div>
      <!--工作区-->
      <el-main class="main" :style="{backgroundColor: settings.backgroundColor}">
        <div class="app-header">
          <nav-header  @openPersonalInfoDialog="openPersonalInfoDialog"
                      :isCollapse="isCollapse"
                      v-if="layout.style==='left-right'"
                      :routers="routers"
                      :sysLogo="sysLogo"
                      @hamburger="getHamburgerStatus"
                      @openSetting="onOpenSetting">
          </nav-header>
          <div class="sub-bar">
            <!-- 面包屑 -->
            <div class="breadcrumb-container">
              <el-breadcrumb separator-class="el-icon-arrow-right">
                <span class="default-bread" v-if="breadcrumbItems[0] && breadcrumbItems[0].to !=='/homepage'"
                      @click="$router.push({path: '/homepage'})"><el-link :underline="false">首页</el-link> | </span>
                <template v-for="breadcrembItem in breadcrumbItems">
                  <template v-if="breadcrembItem.to === ''">
                    <el-breadcrumb-item>{{breadcrembItem.title}}</el-breadcrumb-item>
                  </template>
                  <template v-else>
                    <el-breadcrumb-item :to="{ 'path': breadcrembItem.to }"> {{ breadcrembItem.title }}</el-breadcrumb-item>
                  </template>
                </template>
              </el-breadcrumb>
            </div>

            <!-- 收藏列表 -->
            <div class="collect-container">
              <book-mark class="book-mark"></book-mark>
            </div>
          </div>
        </div>
        <div v-show="false" class="mask-layout" :class="isCollapse? 'is-collapse':''" v-loading="true"></div>
        <div class="main-content">
          <transition name="fade" mode="out-in">
            <router-view></router-view>
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

import NavHeader from './components/header'
import { isLightOrDark } from '@/utils/common'
import { getLocalRouters, getLocalSysSetting, getLocalCurrentUser } from '@/utils/auth'
export default {
  extends: BaseUI,
  data() {
    return {
      sysLogo: '',
      isCollapse: false,
      header: 'Jeeke demo',
      currentUsername: '',
      userId: currentUser.id,

      webSocketUrl:process.env.WEB_SOCKET_URL,
      websocket:null,
      lockReconnect: false,//是否真正建立连接
      timeout: 300 * 1000,//300秒一次心跳
      timeoutObj:  null,//心跳心跳倒计时
      serverTimeoutObj:  null,//心跳倒计时
      timeoutNum:  null,//断开 重连倒计时
    }
  },
  destroyed(){
    //窗口销毁调用
    if(this.websocket){
      this.websocket.close()
    }
  },
  watch: {

  },
  methods: {
    //连接webSocket
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
    //开启心跳
    start(){
      const self = this;
      self.timeoutObj && clearTimeout(self.timeoutObj);
      self.serverTimeoutObj && clearTimeout(self.serverTimeoutObj);
      self.timeoutObj = setTimeout(function(){
        //这里发送一个心跳，后端收到后，返回一个心跳消息，
        if (self.websocket.readyState === 1) {//如果连接正常
            self.websocket.send("heartCheck");
        }else{//否则重连
          self.reconnect();
        }
        self.serverTimeoutObj = setTimeout(function() {
          //超时关闭
          self.websocket.close();
        }, self.timeout);
      }, self.timeout)
    },
    //重置心跳
    reset(){
      clearTimeout(this.timeoutObj);
      clearTimeout(this.serverTimeoutObj);
      this.start();
    },
    //重新连接
    reconnect() {
      const that = this;
      if(that.lockReconnect) {
        return;
      }
      that.lockReconnect = true;
      //没连接上会一直重连，设置延迟避免请求过多
      that.timeoutnum && clearTimeout(that.timeoutnum); // 卸载定时器
      that.timeoutnum = setTimeout(function () {
        //新连接
        that.connectWebSocket();
        that.lockReconnect = false;
      },20 * 1000);
    },
    //初始化webSocket
    initWebSocket(){
      //异常消息
      this.websocket.onerror = this.setErrorMessage;
      // //连接成功
      this.websocket.onopen = this.setOnopenMessage;
      //收到消息的回调
      this.websocket.onmessage = this.setOnmessageMessage;
      //连接关闭的回调
      this.websocket.onclose = this.setOncloseMessage;
    },
    setOnopenMessage(){ //连接成功
      console.log("连接成功");
      this.start();
    },
    setOnmessageMessage(event){ //收到消息的回调
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
          //"<div class='msg-box'><ul class='msg-list'><li>"+ data[0].noticeRecord.title +"</li></ul></div>"
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
    setOncloseMessage(){  //连接关闭的回调
        console.log('连接关闭');
        //重连
        this.reconnect();
    },
    setErrorMessage(){  //连接异常
        console.log("连接异常");
        //重连
        this.reconnect();
    },
    ...Vuex.mapActions({
        showPanel: 'settings/showPanel',
    }),

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
    ...Vuex.mapGetters(['menus', 'breadcrumbItems', 'settings', 'sys'])
  },
  mounted() {
    console.log(this.$router,'====');
    //连接websocket
    this.connectWebSocket()
    let setting = getLocalSysSetting()
    this.sysLogo = setting.sysLogo
    // this.changeFavicon(setting.favicon)
    this.changeWebTitle(setting.sysAbbrname)
    this.doGetCurrentUsername();
    if(this.$store.getters.toPath){
      let routers = getLocalRouters()
      // 去除路径的 ‘/’ 字符  获取对象的 string 并去除首尾大括号  判断路由中的url 字段是否一致
      const url = this.removeBlock(JSON.stringify( {url:this.$store.getters.toPath.substring(1)} ));
      if(routers.includes(url)){
          this.$router.push(this.$store.getters.toPath);
      }
    }
  },
  beforeCreate() {
    // 当前登录用户放进全局参数中
    window.currentUser = getLocalCurrentUser();
    console.log('再一次');
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
  &.up-down {
    .body-container {

    }
    .nav-menu {
      border-right: none;
    }
    .aside-container {
      border-right: 1px solid #e6e6e6;
    }
  }

  &.left-right {
    .nav-menu {
      border: none;
    }
    .aside-container {
      z-index: 1001;
      box-shadow: 2px 0 6px rgba(0,0,0, .35);
    }
  }
}

.fixed-header {
  &.up-down {
    .body-container {
      position: absolute;
      height: auto!important;
      top: 56px;
      bottom: 0;
    }
    .main {
      margin-top: 37px;
      /*height: calc(100vh - 93px);*/
    }
    .app-header {
      position: absolute;
      z-index: 10;
      right: 0;
      top: 0;
      -webkit-transition: 0.3s width ease-in-out;
      transition: 0.3s width ease-in-out;
      width: calc(100% - 200px);
    }
    &.is-collapse {
      .app-header {
        width: calc(100% - 64px);
      }
    }
  }
  &.left-right {
    .body-container {
      position: absolute;
      height: auto!important;
      top: 0;
      bottom: 0;
    }
    .app-header {
      position: fixed;
      top: 0;
      right: 0;
      z-index: 9;
      width: calc(100% - 200px);
      -webkit-transition: 0.3s width ease-in-out;
      transition: 0.3s width ease-in-out;
    }
    .main {
      margin-top: 93px;
      /*height: calc(100vh - 93px);*/
    }
    &.is-collapse {
      .app-header {
        width: calc(100% - 64px);
      }
    }
  }
}



.body-container {
  height: 100%;
  width: 100%;
  min-width: 1120px;
  overflow: hidden;
  .aside-container {
    min-height: 100%;
    margin: 0px;
    padding: 0px;
  }
}

.aside-header {
  color: #fff;
  font-size: 16pt;
  text-align: center;
  margin: 0px auto;
  padding: 0px;
  line-height: 56px;
  width: 100%;
  height: 56px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  .sysLogo {
    height: 26px;
    display: inline-block;
  }
}


.nav-menu:not(.el-menu--collapse) {
  width: 200px;
  height: 100%;
  overflow: auto;
}

.main {
  margin: 0px;
  padding: 0px;
  width: 100%;
  .main-header {
    padding-right: 10px;
    .header-function {
      text-align: right;
      line-height: 56px;
      .personal-link {
        padding: 13px 10px 14px;
        text-decoration: none;
      }
      .personal-link:hover {

      }
      img {
        -moz-border-radius: 50%;
        -webkit-border-radius: 50%;
         border-radius: 50%;
         vertical-align:middle;
      }
      .username {
        margin-left: 3px;
        font-size: 10pt;
        color: #fff;
      }
    }
  }
  .sub-bar {
    // overflow: hidden;
    background: #fff;
    border-bottom: 1px solid #d8dce5;
    -webkit-box-shadow: 0 1px 3px 0 rgba(0,0,0,.12), 0 0 3px 0 rgba(0,0,0,.04);
    box-shadow: 0 1px 3px 0 rgba(0,0,0,.12), 0 0 3px 0 rgba(0,0,0,.04);
  }
  .main-content {
    margin: 10px;
    /*overflow: auto;*/
  }
  .el-breadcrumb {
    height: 36px;
    line-height: 36px;
    padding-left: 10px;
    border-radius: 0px;
    /*background-color: #F5F4F4;*/
    /*border-bottom: solid 1px #eeeeee;*/
    .el-breadcrumb__item {
      font-size: 12px;
    }
  }
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
