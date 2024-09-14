<template>
  <div @click="tableTwoPopoverClick">
    <el-header
      class="main-header"
      height="56px"
      :style="{ backgroundColor: settings.headerColor }"

    >
      <div
        class="header-left"
        v-if="settings.style === 'up-down' && settings.showLogo"
      >
        <div
          class="aside-header"
          :style="{
            color: isLight('headerColor')
              ? 'rgba(0, 0, 0, 0.65)'
              : 'rgb(255, 255, 255)',
          }"
        >
          <img class="sysLogo" :src="sysLogo" alt="" />
        </div>
      </div>
      <div class="header-content">
        <div class="hamburger-container">
          <svg
            t="1492500959545"
            @click="toggleClick"
            class="wscn-icon hamburger"
            :class="{ 'is-active': !isCollapse }"
            style=""
            viewBox="0 0 1024 1024"
            width="64"
            height="64"
            :fill="
              isLight('headerColor')
                ? 'rgba(0, 0, 0, 0.65)'
                : 'rgb(255, 255, 255)'
            "
          >
            <path
              d="M966.8023 568.849776 57.196677 568.849776c-31.397081 0-56.850799-25.452695-56.850799-56.850799l0 0c0-31.397081 25.452695-56.849776 56.850799-56.849776l909.605623 0c31.397081 0 56.849776 25.452695 56.849776 56.849776l0 0C1023.653099 543.397081 998.200404 568.849776 966.8023 568.849776z"
              p-id="1692"
            ></path>
            <path
              d="M966.8023 881.527125 57.196677 881.527125c-31.397081 0-56.850799-25.452695-56.850799-56.849776l0 0c0-31.397081 25.452695-56.849776 56.850799-56.849776l909.605623 0c31.397081 0 56.849776 25.452695 56.849776 56.849776l0 0C1023.653099 856.07443 998.200404 881.527125 966.8023 881.527125z"
              p-id="1693"
            ></path>
            <path
              d="M966.8023 256.17345 57.196677 256.17345c-31.397081 0-56.850799-25.452695-56.850799-56.849776l0 0c0-31.397081 25.452695-56.850799 56.850799-56.850799l909.605623 0c31.397081 0 56.849776 25.452695 56.849776 56.850799l0 0C1023.653099 230.720755 998.200404 256.17345 966.8023 256.17345z"
              p-id="1694"
            ></path>
          </svg>
        </div>
        <div class="header-menu" ref="headerMenu">
          <el-menu
            ref="menuUl"
            :key="menuKey"
            :default-active="defaultActive"
            :text-color="
              isLight('headerColor')
                ? 'rgba(0, 0, 0, 0.65)'
                : 'rgba(255, 255, 255, .8)'
            "
            :active-text-color="
              isLight('headerColor')
                ? 'rgba(0, 0, 0, 0.65)'
                : 'rgba(255, 255, 255, 1)'
            "
            class="el-menu-demo"
            mode="horizontal"
            :style="styleObject"
            @select="handleMenuSelect"
          >
            <el-menu-item
              ref="menuLi"
              v-for="(module, index) in handleRouters"
              v-show="
                index < limitNumber &&
                (module.parentId == '' || module.parentId == '0')
              "
              :key="module.id"
              :index="module.id"
            >
              <svg-icon
                :iconClass="module.cssClass"
                :style="{
                  color: isLight('headerColor')
                    ? 'rgba(0, 0, 0, 0.65)'
                    : 'rgba(255, 255, 255, .8)',
                  fill: 'currentColor',
                }"
              />
              {{ module.name }}
            </el-menu-item>
            <!-- 超出折叠 -->
            <el-submenu index="2" v-if="handleRouters.length > limitNumber">
              <template slot="title">更多</template>
              <el-menu-item
                class="header-sub-menu"
                v-for="(module, index) in handleRouters"
                v-show="index >= limitNumber"
                :key="module.id"
                :index="module.id"
              >
                <svg-icon :iconClass="module.cssClass" />
                {{ module.name }}
              </el-menu-item>
            </el-submenu>
          </el-menu>
        </div>

        <div class="header-function" style="max-width: 400px" ref="headerRight">
          <div
            class="company-name"
            v-show="currentUser.id != 1000 && currentUser.id != 1001"
          >
            <!-- <el-button type="primary" style="max-width: 184px" :style="{color: isLight('headerColor')? 'rgba(0, 0, 0, 0.65)' : 'rgb(255, 255, 255)'}">
              {{currentUser.company.name}}
            </el-button> -->
            <el-dropdown
              @command="changeCompany"
              trigger="click"
              placement="bottom-start"
            >
              <span class="el-dropdown-link" style="padding-right:12px">
                {{ company.name
                }}<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-for="item in companys"
                  :key="item.id"
                  :command="item.id"
                  >{{ item.name }}</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div class="messages">

             <div class="messages" v-if="drugIndateWarning" style="padding-left:10px">
            <el-popover
              placement="right"
              width="500"
              ref="popoverRefStuff"
              trigger="click"           >
              <stuff-indate></stuff-indate>
              <el-button
                class="notice-tip"
                :style="{
                  color: isLight('headerColor')
                    ? 'rgba(0, 0, 0, 0.65)'
                    : 'rgba(255, 255, 255, .8)',
                }"
                type="text"
                size="large"
                icon="el-icon-bell"
                ref="bntClick"
                slot="reference"
                @click="unreadStuffNotice"
              >
                <el-badge
                  :hidden="stuffIndateWarningTotal <= 0"
                  style="position: absolute; top: 0; right: -10px"
                  class="mark"
                  :max="10"
                  :value="stuffIndateWarningTotal"
                ></el-badge>
                 <span style="position:absolute;margin-top:20px;font-size:10px;text-align:center;color:white;margin-left:-30px">
                  材料预警
                </span>
              </el-button>

            </el-popover>

          </div>

          <div class="messages" v-if="drugIndateWarning" style="padding-left:30px">
            <el-popover
              placement="right"
              width="500"
              ref="popoverRefDrug"
              trigger="click"           >
              <drug-indate></drug-indate>
              <el-button
                class="notice-tip"
                :style="{
                  color: isLight('headerColor')
                    ? 'rgba(0, 0, 0, 0.65)'
                    : 'rgba(255, 255, 255, .8)',
                }"
                type="text"
                size="large"
                icon="el-icon-bell"
                ref="bntClick"
                slot="reference"
                @click="unreadDrugNotice"
              >
                <el-badge
                  :hidden="drugIndateWarningTotal <= 0"
                  style="position: absolute; top: 0; right: -10px"
                  class="mark"
                  :max="10"
                  :value="drugIndateWarningTotal"
                ></el-badge>
                 <span style="position:absolute;margin-top:20px;font-size:10px;text-align:center;color:white;margin-left:-30px">
                  药品预警
                </span>
              </el-button>

            </el-popover>

          </div>

          <div class="messages" style="padding-left:20px">
            <el-popover
              placement="right"
              width="500"
              ref="popoverRef"
              trigger="manual"
            >
              <tabSite ref="tabSite" />
<!--              <el-button-->
<!--                class="notice-tip"-->
<!--                :style="{-->
<!--                  color: isLight('headerColor')-->
<!--                    ? 'rgba(0, 0, 0, 0.65)'-->
<!--                    : 'rgba(255, 255, 255, .8)',-->
<!--                }"-->
<!--                type="text"-->
<!--                size="large"-->
<!--                icon="el-icon-bell"-->
<!--                ref="bntClick"-->
<!--                slot="reference"-->
<!--                @click="unreadNotice"-->
<!--              >-->
<!--                <el-badge-->
<!--                  :hidden="noticeTotal <= 0"-->
<!--                  style="position: absolute; top: 0; right: -10px"-->
<!--                  class="mark"-->
<!--                  :max="10"-->
<!--                  :value="noticeTotal"-->
<!--                ></el-badge>-->
<!--                  <span style="position:absolute;margin-top:20px;margin-left:-30px;font-size:10px;text-align:center;color:white;">-->
<!--                  信息-->
<!--                </span>-->
<!--              </el-button>-->
            </el-popover>

          </div>
          </div>

          <el-dropdown
            class="personal"
            :key="sizeKey"
            :size="settings.size"
            @command="handleCommand"
          >
            <a class="personal-link">
              <img v-if="userHeader" :src="userHeader" alt="" />
              <img v-else src="../../../assets/images/personal_avatar.jpeg" alt="" />
              <span
                class="username"
                :style="{
                  color: isLight('headerColor')
                    ? 'rgba(0, 0, 0, 0.65)'
                    : 'rgb(255, 255, 255)',
                }"
                >{{ currentUser.name }}</span
              >
            </a>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="personalInfo"
                ><svg-icon iconClass="profileInfo" /> 个人信息</el-dropdown-item
              >
              <el-dropdown-item command="layoutSet"
                ><svg-icon iconClass="layout" /> 布局设置</el-dropdown-item
              >
              <!--<el-dropdown-item command="record" v-if='currentUser.id != 1000' ><svg-icon iconClass="subscribed"/> 消息订阅</el-dropdown-item>-->
              <el-dropdown-item command="sysSet" v-if="currentUser.id == 1000"
                ><svg-icon iconClass="sysSet" /> 系统设置</el-dropdown-item
              >
              <el-dropdown-item command="logout" divided
                ><svg-icon iconClass="loginout" /> 退出登录</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!--  <unsubscribe ref="unsubscribe" v-if="dialogVisible"></unsubscribe>
-->
  </div>
</template>

<script>
import { getUserById } from '@/api/admin/user'
import { getPhotoById } from "@/api/sys/sysSeting";
import { logOut, getToken } from "@/api/auth";
import { isLightOrDark } from "@/utils/common";
import tabSite from "../../notice/noticeRecordSite/tabSite";
import drugIndate from "../../components/drugIndate";
import stuffIndate from "../../components/stuffIndate";
import {
  clearLocalData,
  getLocalCompanys,
  setLocalToken,
  setLocalCurrentUser,
  getLocalRouters,
  setLocalRouters,
  setLocalDataPermisions,
  setLocalCurrentCompany,
  setLocalPersonalTheme,
} from "@/utils/auth";
import { routerTree, handleFamily } from "@/utils/routerTree";
import {getUserIndateWarning} from "@/api/admin/router";

import backImg from "@/assets/images/KCI8nCoKFf.jpg";

const _import = require("@/router/_import_" + process.env.NODE_ENV);
import axios from 'axios'

export default {

  name: "NavHeader",
  components: { tabSite,drugIndate,stuffIndate},
  props: {
    isCollapse: Boolean,
    routers: Array,
    sysLogo: "",
  },
  data() {
    return {
      longRange:{Data:"API.Manage",AppId:"Oh_Newtouch_Clinic"},
      dialogVisible: false,
      limitNumber: 5,
      noticeTotal: 0,
      drugIndateWarningTotal:0,
      stuffIndateWarningTotal:0,
      drugIndateWarning:false,
      activeIndex: "1",
      isActive: true,
      currentUser: window.currentUser,
      sizeKey: 0,
      currentActive: "",
      menuKey: 0,
      companys: getLocalCompanys(),
      company: window.currentUser.company,
      //用户头像
      userHeader:'',
    };
  },
  mounted: function () {
    const that = this;
    if (that.routers.length <= 0) {
      that.$alert("该账号暂未分配权限，请联系管理员！", "", {
        confirmButtonText: "知道了",
        type: "warning",
        callback: (action) => {},
      });
    }
    getUserById(that.currentUser.id).then(res=>{
      if(res.code == 100){
        if(res.data.userExt&&res.data.userExt.photoId){
           getPhotoById(res.data.userExt.photoId).then((responseData) => {
              const src = `data:image/png;base64,${responseData}`;
              that.userHeader = src;
           })
        }

      }
    })
    that.setLimitNumber();
    that.getUserIsIndate();

  },
  watch: {
    //检查未读消息变化
    "$store.state.notices.noticeTotal": function (val) {
      this.noticeTotal = val;
    },
    "$store.state.notices.drugIndateWarningTotal":function (val) {
      this.drugIndateWarningTotal = val+this.$store.state.notices.drugInventoryWarningTotal;
      this.$forceUpdate()
    },
    "$store.state.notices.drugInventoryWarningTotal":function (val){
      this.drugIndateWarningTotal = val+this.$store.state.notices.drugIndateWarningTotal
    },
     "$store.state.notices.stuffIndateWarningTotal":function(val){
       this.stuffIndateWarningTotal = val+this.$store.state.notices.stuffInventoryWarningTotal
    },
    "$store.state.notices.stuffInventoryWarningTotal":function(val){
       this.stuffIndateWarningTotal = val+this.$store.state.notices.stuffIndateWarningTotal
    },
    "settings.size"(val, old) {
      if (val) {
        // 实时更新组件渲染 或者 使用 this.$forceUpdate()
        this.sizeKey += 1;
      }
    },
    $route(val, old) {
      this.menuKey += 1;
    },
  },
  computed: {
    ...Vuex.mapGetters(["settings", "menus"]),
    handleRouters() {
      let routers = [];
      this.routers.forEach((item, index) => {
        if (!item.code && item.parentId == 0) {
          routers.push(item);
        }
      });

      return routers;
    },

    isLight() {
      return function (colorType) {
        return isLightOrDark(this.settings[colorType]);
      };
    },

    styleObject() {
      return {
        "--active-color": this.isLight("headerColor")
          ? "rgba(0, 0, 0, 0.65)"
          : "rgb(255, 255, 255)",
      };
    },

    defaultActive() {
      const route = this.$route;
      let routers = [];
      if (this.routers.length <= 0) {
        return "";
      }
      this.routers.forEach((item, index) => {
        if (!item.code && item.parentId == 0) {
          routers.push(item);
        }
      });
      // if (route.meta.parentId && route.meta.parentId != 0) {
      //   this.getMenus(route.meta.parentId)
      //   return route.meta.parentId
      if (route.meta.family && route.meta.parentId != 0) {
        this.getMenus(route.meta.family[0]);
        // console.log(this.menus, "1");
        // this.$router.push(this.menus.menus[0].url);
        return route.meta.family[0];
      } else {
        if (route.path == "/") {
          // 默认展示第一个菜单模块
          // this.getMenus(routers[0].id)

          // return routers[0].id
          // 默认展示首页
          this.$router.push({ path: "/homepage" });

          // 待后续 所有用户首页权限设置后 再处理
          // let currentUser = this.currentUser.id
          // if (currentUser != '1000' && currentUser != '1001') {
          //   this.$router.push({path: '/homepage'})
          // } else {
          //   this.getMenus(routers[0].id)
          //   this.$router.push(this.menus.menus[0].url)
          //   return routers[0].id
          // }
        }

        if (!this.menus.topMenuId) {
          this.getMenus(routers[0].id);
          // console.log(this.menus, "2");
          // this.$router.push(this.menus.menus[0].url);
          return routers[0].id;
        } else {
          this.getMenus(this.menus.topMenuId);
          // console.log(this.menus.menus[0].url, "3.menus[0]");
          // this.$router.push(this.menus.menus[0].url);
          return this.menus.topMenuId;
        }
        // this.$router.push(this.menus.menus[0].url)
      }
    },
  },
  methods: {
    tableTwoPopoverClick () {

		},
    getUserIsIndate(){
      console.log(currentUser.id);
      let router = JSON.parse(sessionStorage.getItem("routers"));
      console.log(router);
      let flags = false;
      router.forEach((item)=>{
        if(item.code==='stock/warning'){
            flags = true;
        }
      });

      if(flags){
        getUserIndateWarning(currentUser.id).then((res)=>{
        if(res.code==100){

          if(res.data!=null&&res.data!=undefined){
            this.drugIndateWarning=true;
          }else{
            console.log(111111111111111111111111111);
            this.drugIndateWarning = false;
          }
        }
      }).catch((error)=>{
        this.outputError(error)
      })
      }else{
        this.drugIndateWarning = false;
      }
    },
    unreadDrugNotice(){
      // this.$refs.popoverRefDrug.doToggle();
    },
    unreadStuffNotice(){
      // this.$refs.popoverRefStuff.doToggle();
    },
    ...Vuex.mapActions({
      changeSetting: "settings/changeSetting",
    }),
    changeCompany(command) {
      if (this.companys.length !== 0)
        this.company = this.companys.find((i) => command === i.id);
      this.login(command);
      // 刷新页面
      window.location.reload();
    },
    login(id) {
      getToken(
        this.currentUser.loginname,
        window.sessionStorage.getItem("User_P"),
        id
      )
        .then((responseData) => {
          if (responseData.code == 100) {
            this.handleLoginInfo(responseData.data);
          } else {
            this.isError = true;
            this.loginError = responseData.msg;
          }
          this.$store.dispatch("setLoading", false);
        })
        .catch((error) => {
          this.$store.dispatch("setLoading", false);
          this.outputError(error);
        });
    },
    initIndexRouter() {
      let indexRouter = {
        path: "/",
        name: "/",
        component: _import("home/index"),
        children: [...this.generateChildRouters()],
      };
      // this.$router.addRoutes([indexRouter])
      this.$router.addRoute("/", indexRouter);
    },
    generateChildRouters() {
      let routers = getLocalRouters();
      let routerTreeArr = routerTree(routers);
      if (!routers) {
        return [];
      }

      let childRouters = [];
      for (let router of routers) {
        if (router.code) {
          let routerProps = JSON.parse(router.properties);
          let childRouter = {
            path: router.url,
            name: router.code,
            component: _import(router.code + "/index"),
            meta: {
              name: router.name,
              cssClass: routerProps.cssClass,
              routerId: router.id,
              parentId: router.parent.id,
              family: handleFamily(
                routerTreeArr,
                (item) => item.id === router.id
              ),
              linkUrl: routerProps.linkUrl,
              requiresAuth: routerProps.meta.requiresAuth,
              nameFullPath: routerProps.nameFullPath,
            },
          };
          childRouters.push(childRouter);
        }
      }
      let redirectRouter = {
        path: "redirect",
        component: _import("redirect/index"),
      };
      childRouters.push(redirectRouter);
      return childRouters;
    },
    handleLoginInfo(userData) {
      setLocalCurrentUser({
        id: userData.userId,
        name: userData.username,
        loginname: userData.loginname,
        company: userData.company,
        department: userData.department,
      });
      setLocalCurrentCompany({
        id: userData.company.id,
        code: userData.company.code,
        name: userData.company.name,
      });
      setLocalToken(userData.token);
      setLocalRouters(userData.routers);
      setLocalDataPermisions(userData.dataPermisions);

      const personalTheme = userData.personalTheme;
      personalTheme.theme = JSON.parse(personalTheme.theme);
      setLocalPersonalTheme(personalTheme);
      this.changeTheme(personalTheme.theme);

      // 初始化首页路由
      this.initIndexRouter();

      const routers = userData.routers; // 当前用户的路由
      if (this.$route.query.redirect) {
        // 判断有无重定向
        const url = this.removeBlock(
          JSON.stringify({ url: this.$route.query.redirect.substring(1) })
        );
        if (routers.includes(url)) {
          // 判断路由是否包含 重定向路径 判断有无权限
          this.$router.push(this.$route.query.redirect);
        }
      }
      if (this.$route.path !== "/") {
        this.$router.push("/");
      } else {
        location.reload();
      }
      // console.log(this.$route.path,'sss');

      // this.$router.push("/empty");
      /*      let redirect = decodeURIComponent(
        this.$route.query.redirect || '/'
      )
      this.$router.push(redirect);*/
    },
    removeBlock(str) {
      if (str) {
        const reg = /^\{/gi;
        const reg2 = /\}$/gi;
        str = str.replace(reg, "");
        str = str.replace(reg2, "");
        return str;
      } else {
        return str;
      }
    },
    changeTheme(theme) {
      const settings = [
        "theme",
        "headerColor",
        "sidebarColor",
        "backgroundColor",
        "size",
        "style",
        "showTagsView",
        "fixedHeader",
        "showLogo",
      ];
      this.$ELEMENT.size = theme.size;
      settings.forEach((item) => {
        this.handleSetting(item, theme[item]);
      });
    },
    handleSetting(key, value) {
      this.changeSetting({ key, value });
    },
    setLimitNumber() {
      this.$nextTick(() => {
        const menus = [...this.$refs.menuUl.$el.children]; // htmlcollection对象 转数组
        let menuUlWidth = 0;
        let lis = [];
        menus.forEach((item, index) => {
          menuUlWidth += item.offsetWidth;
        });
        const width = document.body.getBoundingClientRect().width;
        const usefullWidth = width - 660;

        if (usefullWidth < menuUlWidth) {
          this.limitNumber = 4;
        } else {
          this.limitNumber = 5;
        }
      });
    },
    unreadNotice() {
      this.$refs.popoverRef.doToggle();
      this.$refs.tabSite.onSearch();
    },
    ...Vuex.mapActions({
      getMenus: "menus/getMenus",
    }),
    async handleMenuSelect(menuId) {
      await this.getMenus(menuId);
      this.$router.push(this.menus.menus[0].url);
    },
    toggleClick() {
      this.isActive = this.isCollapse;
      // this.isCollapse = !this.isCollapse
      this.$emit("hamburger", !this.isActive);
    },
    handleCommand(command) {
      switch (command) {
        case "logout":
          this.doLogOut();
          break;
        case "personalInfo":
          this.$emit("openPersonalInfoDialog");
          break;
        case "record": //消息订阅
          this.dialogVisible = true;
          this.$nextTick(() => {
            // this.$refs.unsubscribe.dialogVisible = true;
          });
          break;
        case "layoutSet":
          this.$store.dispatch("settings/showPanel", {
            isShow: true,
            type: "layout",
          });
          this.$emit("openSetting", "layout", "openLayout");
          break;
        case "sysSet":
          this.$store.dispatch("sys/showPanel", { isShow: true, type: "sys" });
          this.$emit("openSetting", "sys", "openSys");
      }
    },
    doLogOut() {
      this.$confirm("确定要退出系统吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          logOut()
            .then((responseData) => {
              clearLocalData();
              this.$router.push("/login");
              // 模拟f5刷新，避免出现Duplicate named routes definition警告。
              this.$router.go(0);
            })
            .catch((error) => {
              this.outputError(error);
            });
        })
        .catch(() => {});
    },
  },
};
</script>

<style lang="scss">
@import "../../../assets/scss/common.scss";
</style>
<style lang="scss" scoped>
.company-name .el-dropdown-link {
  cursor: pointer;
  color: white;
}
.company-name .el-icon-arrow-down {
  font-size: 12px;
}
.red-point {
  position: relative;
}

.red-point::before {
  content: " ";
  border: 3px solid red; /*设置红色*/
  border-radius: 3px; /*设置圆角*/
  position: absolute;
  z-index: 1000;
  right: 0%;
}

.nav-menu:not(.el-menu--collapse) {
  width: 200px;
  height: 100%;
  overflow: auto;
}
.main-header {
  display: flex;
  padding: 0;
  min-width: 1160px;
  .header-function {
    float: right;
    line-height: 56px;
    height: 100%;
    .personal-link {
      padding: 0 10px;
      text-decoration: none;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
      word-break: break-all;
    }
    .personal-link:hover {
    }
    img {
      -moz-border-radius: 50%;
      -webkit-border-radius: 50%;
      border-radius: 50%;
      vertical-align: middle;
      width: 32px;
      height: 32px;
    }
    .username {
      margin-left: 3px;
      font-size: 10pt;
      color: #fff;
    }
    .company-name {
      float: left;
      display: flex;
      align-items: center;
      height: 100%;
      position: relative;
      &:after {
        position: absolute;
        content: "";
        width: 1px;
        height: 14px;
        right: 0;
        top: 50%;
        margin-top: -7px;
        background-color: rgba(255, 255, 255, 0.5);
      }
      .el-button {
        font-size: 14px;
        background: none;
        border: none;
        outline: none;
        cursor: default;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        word-break: break-all;
      }
    }
    .messages {
      float: left;
      height: 100%;
      > span {
        display: flex;
        height: 100%;
      }
      /deep/ .el-popover__reference-wrapper {
        display: flex;
        align-items: center;
      }
      .el-button {
        span {
          width: 100%;
          display: inline-block;
          white-space: nowrap;
          text-overflow: ellipsis;
         // overflow: hidden;
          word-break: break-all;
          text-align: center;
        }
      }
      .notice-tip {
        max-width: 89px;
        width: 30px;
        margin-left: 10px;
        position: relative;
        /deep/ .el-icon-bell {
          font-size: 18px !important;
        }
      }
    }
    .personal {
      display: flex;
      align-items: center;
    }
  }
  .header-left {
    width: 200px;
  }
  .header-content {
    position: relative;
    flex: 1;
    .hamburger-container {
      float: left;
      height: 100%;
      width: 50px;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: rgba(255, 255, 255, 0.05);
      transition: background-color 0.3s;
      &:hover {
        background-color: rgba(255, 255, 255, 0.1);
      }
    }
  }
  .header-menu {
    position: absolute;
    top: 5px;
    left: 60px;
    display: inline-block;
    .el-menu {
      background: none;
      border-bottom: 0;
    }
    .el-menu--horizontal {
      .el-menu-item {
        height: 45px !important;
        line-height: 45px !important;
        position: relative;
        border-bottom: 0;
        i {
          width: auto;
          margin-right: 0;
        }
        &:hover {
          background-color: rgba(0, 0, 0, 0) !important;
          color: rgba(255, 255, 255, 1);
        }
        &:focus {
          background-color: rgba(255, 255, 255, 0);
        }
        &:after {
          position: absolute;
          content: "";
          width: 0;
          left: 50%;
          height: 2px;
          border-radius: 6px;
          transition: all 0.3s;
          background-color: var(--active-color) !important;
          bottom: 0;
        }
        &.is-active {
          font-weight: bold;
          color: rgba(255, 255, 255, 1);
          background-color: rgba(255, 255, 255, 0);
          .svg-icon {
            color: var(--active-color) !important;
            fill: currentColor;
          }
          &:after {
            position: absolute;
            content: "";
            width: 30px;
            left: 50%;
            margin-left: -15px;
            background-color: var(--active-color) !important;
            bottom: 0;
          }
          &:hover {
            font-weight: bold;
            color: rgba(255, 255, 255, 1);
            background-color: rgba(255, 255, 255, 0) !important;
          }
        }
      }
      .is-active {
        /deep/ .el-submenu__title {
          font-weight: bold;
          background-color: rgba(255, 255, 255, 0);
          border-bottom: none;
          .svg-icon {
            color: var(--active-color) !important;
            fill: currentColor;
          }
          &:after {
            position: absolute;
            content: "";
            width: 30px;
            left: 50%;
            margin-left: -15px;
            height: 2px;
            border-radius: 6px;
            background-color: var(--active-color) !important;
            bottom: 0;
          }
        }
      }
    }
    /deep/ .el-submenu__title {
      height: 45px !important;
      line-height: 45px !important;
      position: relative;
      border-bottom: 0;
      &:after {
        position: absolute;
        content: "";
        width: 0;
        left: 50%;
        height: 1px;
        transition: all 0.3s;
        background-color: var(--active-color) !important;
        bottom: 0;
      }
      i {
        color: var(--active-color) !important;
      }
      .svg-icon {
        color: var(--active-color) !important;
        fill: currentColor;
      }
      &:hover {
        background-color: rgba(255, 255, 255, 0);
      }
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
}
.hamburger {
  display: inline-block;
  cursor: pointer;
  width: 20px;
  height: 20px;
  transform: rotate(90deg);
  transition: 0.38s;
  transform-origin: 50% 50%;
}
.hamburger.is-active {
  transform: rotate(0deg);
}
.header-sub-menu {
  color: #555 !important;
}
/deep/ .el-dropdown-menu__item:not(.is-disabled) {
  &:hover {
    .svg-icon {
      color: var(--active-color) !important;
      fill: currentColor;
    }
  }
}
</style>
