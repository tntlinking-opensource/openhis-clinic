<template>
  <div
    v-if="isLoaded"
    class="login-container"
    :class="sysData.loginLayout"
    :style="{ backgroundImage: 'url(' + backImg + ')' }"
  >
    <div class="login-header">
      <div class="login-logo">
        <!-- <img :src="sysData.projectLogo" alt="" /> -->
      </div>
      <div class="login-title">
        {{ sysData.sysName }}
      </div>
      <div class="login-right"></div>
    </div>
    <div class="login-form">
      <el-form
        class="form"
        :model="loginForm"
        :rules="rules"
        ref="loginForm"
        autoComplete="on"
        label-position="left"
        @submit.native.prevent
      >
        <div class="login-body">
          <h1 class="form-title">
            <!-- <img :src="sysData.loginLogo" alt="" /> -->
            诊所管理系统
          </h1>
          <el-alert
            class="login-error"
            v-show="isError"
            :title="loginError"
            type="error"
            show-icon
            :closable="false"
          ></el-alert>
          <el-form-item prop="username">
            <el-input
              name="username"
              v-model="loginForm.username"
              autoComplete="on"
              autofocus="autofocus"
              placeholder="用户名"
              @input="isError = false"
            >
              <div class="login-username" slot="prepend"></div>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              name="password"
              type="password"
              v-model.trim="loginForm.password"
              placeholder="口令"
              @input="isError = false"
            >
              <template slot="prepend"
                ><div class="login-password"></div
              ></template>
            </el-input>
          </el-form-item>
          <el-button
            type="primary"
            :loading="showLoading"
            @click.native.prevent="doLogin"
            native-type="submit"
            >登录</el-button
          >
        </div>
      </el-form>
      <div
        class="graph"
        :style="{ backgroundImage: 'url(' + sysData.loginGraph + ')' }"
      ></div>
    </div>

    <div class="footer">@Copyright 2021.08.12</div>


    <el-dialog
      title="登陆提醒"
      :visible.sync="dialogVisible"
      width="40%"
      append-to-body
    >
      <p class="til">检测到当前账号绑定多个诊所，请选择需要登录的诊所</p>
      <ul>
        <li v-for="(i, ind) in firmList" :key="i.id" @click="firmIndex = ind">
          <p><span v-show="firmIndex === ind"></span></p>
          <p>{{ i.name }}</p>
        </li>
      </ul>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmLogin()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from "@/api/auth";
import BaseUI from "@/views/components/baseUI";
import { listSysSetingAll, getFirmList } from "@/api/sys/sysSeting.js";
import {
  setLocalToken,
  setLocalCurrentUser,
  setLocalCompanys,
  getLocalRouters,
  setLocalRouters,
  setLocalDataPermisions,
  setLocalSysSetting,
  setLocalCurrentCompany,
  setLocalPersonalTheme,
} from "@/utils/auth";
import { routerTree, handleFamily } from "@/utils/routerTree";

import backImg from "@/assets/images/KCI8nCoKFf.jpg";

const _import = require("@/router/_import_" + process.env.NODE_ENV);

export default {
  extends: BaseUI,
  data() {
    return {
      backImg:backImg,
      dialogVisible: false,
      firmList: [],
      firmIndex: 0,
      isLoaded: false,
      baseApi: process.env.BASE_API,
      displayLoading: false,
      loginForm: {
        username: process.env.NODE_ENV == "development" ? "15827280002" : "", //system  1824422426
        password: process.env.NODE_ENV == "development" ? "280002" : "",
      },
      rules: {
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
        ],
        password: [{ required: true, message: "请输入口令", trigger: "blur" }],
      },
      loginBg: require("../../assets/images/logbg.jpg"),
      loginSubBg: require("../../assets/images/logsubbg.jpg"),
      sysData: {
        sysName: "", // 系统名称
        sysAbbrname: "", // 系统简称
        loginBgcolor: "", // 登录页背景色
        loginLayout: "simple-center",
        projectLogo: "", // 项目logo
        loginLogo: "", // 登录框logo
        sysLogo: "", // 系统主页logo
        favicon: "", // 网页顶部 favicon 图标
        loginBg: "", // 登录页背景图
        loginGraph: "", // 登录页配图
      },
      isError: false,
      loginError: "",
    };
  },
  components: {},
  computed: {
    ...Vuex.mapGetters(["showLoading", "settings"]),
    styleObject() {
      return {
        "--active-color": this.settings.theme,
      };
    },
  },
  methods: {
    ...Vuex.mapActions({
      changeSetting: "settings/changeSetting",
    }),

    getSysSetting() {
      let parmas = {};
      listSysSetingAll(parmas)
        .then((response) => {
          if (response.code == 100) {
            let result = response.data[0];
            this.sysData.sysName = result.sysName;
            this.sysData.sysAbbrname = result.sysAbbrname;
            this.sysData.loginBgcolor = result.loginBgcolor;
            this.sysData.loginLayout = result.loginLayout;
            this.sysData.projectLogo = `${this.baseApi}/sys/sysSeting/getFile/1`;
            this.sysData.loginLogo = `${this.baseApi}/sys/sysSeting/getFile/2`;
            this.sysData.sysLogo = `${this.baseApi}/sys/sysSeting/getFile/3`;
            // this.sysData.favicon = `${this.baseApi}/sys/sysSeting/getFile/4`;
            this.sysData.loginBg = `${this.baseApi}/sys/sysSeting/getFile/5`;
            this.sysData.loginGraph = `${this.baseApi}/sys/sysSeting/getFile/6`;
            setLocalSysSetting(this.sysData);

            // this.changeFavicon(this.sysData.favicon);
            this.changeWebTitle(this.sysData.sysAbbrname);
          }
          this.isLoaded = true;
        })
        .catch((error) => {
          this.isLoaded = true;
        });
    },
    changeFavicon(icon) {
      let link =
        document.querySelector("link[rel*='Icon']") ||
        document.createElement("link");
      link.type = "image/x-icon";
      link.rel = "Shortcut Icon";
      link.href = icon;
      document.getElementsByTagName("head")[0].appendChild(link);
    },
    changeWebTitle(title) {
      document.title = title;
    },
    login(id) {
      this.$store.dispatch("setLoading", true);
      getToken(this.loginForm.username, this.loginForm.password, id)
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
    confirmLogin() {
      this.dialogVisible = false;
      this.login(this.firmList[this.firmIndex].id);
    },
    async doLogin() {
      this.$refs["loginForm"].validate(async (valid) => {
        if (valid) {
          const res = await getFirmList(this.loginForm.username,this.loginForm.password);
          setLocalCompanys(res);
          this.firmList = res;
          if (res.length > 1) {
            this.dialogVisible = true;
          } else {
            this.login(res[0] ? res[0].id : "");
          }
        }
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
      window.sessionStorage.setItem("User_P", this.loginForm.password);
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
      this.$router.push("/");
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
  },
  created: function () {
    this.getSysSetting();
  },
};
</script>

<style scoped>
.til {
  margin: 0 auto;
}
ul {
  max-height: 200px;
  overflow-y: scroll;
}
ul li {
  display: flex;
  justify-content: left;
  align-items: center;
  padding: 0px 50px;
}
ul li p {
  margin-top: 10px;
  margin-bottom: 2;
}
ul li p:nth-child(1) {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  position: relative;
  border: 1px solid #333;
  margin-right: 20px;
}

ul li p:nth-child(1) span {
  width: 10px;
  height: 10px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  background: #409eff;
}
</style>
<style rel='stylesheet/scss' lang='scss' scoped>
.login-container {
  min-width: 1000px;
  width: 100%;
  height: 100%;
  position: relative;
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
  overflow: auto;
  z-index: 1001;
  .login-header {
    height: 100px;
    min-width: 1000px;
    width: 80%;
    margin: 0 auto;
    z-index: 1000;
    box-sizing: border-box;
    transition: all 0.5s ease;
    .login-logo {
      float: left;
      margin: 0;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      img {
        display: inline-block;
        overflow: hidden;
        height: 32px;
      }
    }
    .login-title {
      float: left;
      padding-left: 20px;
      font-weight: bold;
      font-size: 24px;
      color: #555;
      line-height: 100px;
      position: relative;
      top: 5px;
    }
  }
  .el-input {
    width: 100%;
  }
  .el-button {
    width: 100%;
  }
  .login-form {
    position: absolute;
    border-radius: 4px;
    top: 50%;
    left: 50%;
    -webkit-box-shadow: 0 10px 40px rgba(1, 140, 183, 0.15);
    box-shadow: 0 10px 40px rgba(1, 140, 183, 0.15);
  }

  .footer {
    font-size: 12px;
    position: absolute;
    right: 20px;
    bottom: 20px;
    color: #fff;
  }
}
.simple-center {
  .login-form {
    width: 400px;
    margin: 0 auto;
    margin-top: -159px;
    margin-left: -200px;
    background-color: #fff;
    .footer {
      position: absolute;
      color: #f5f5f5;
      font-size: 14px;
      bottom: -200px;
      width: 100%;
      text-align: center;
    }
  }
  .form-title {
    font-size: 14px;
    text-align: center;
    margin-bottom: 50px;
    img {
      display: inline-block;
      height: 28px;
      overflow: hidden;
    }
  }
  .login-body {
    padding: 30px;
    text-align: center;
    .form-title {
      font-size: 25px;
    }
    .profile-img {
      width: 96px;
      height: 96px;
      margin: 0 auto 10px;
      display: block;
    }
    .login-error {
      position: relative;
      font-size: 14px;
      color: #f00;
      margin-bottom: 5px;
    }
    .login-username {
      width: 20px;
      height: 20px;
      margin: 0px -10px;
      background-image: url("../../assets/images/user.png");
    }
    .login-password {
      width: 20px;
      height: 20px;
      margin: 0px -10px;
      background-image: url("../../assets/images/lock.png");
    }
  }
  .graph {
    display: none;
  }
}
.left-graph {
  .login-form {
    min-width: 1000px;
    background-color: #fff;
    /*width: 80%;*/
    width: 1000px;
    margin: 0 auto;
    margin-top: -250px;
    /*margin-left: -40%;*/
    margin-left: -500px;
    &:after {
      content: "";
      height: 0;
      line-height: 0;
      display: block;
      visibility: hidden;
      clear: both;
    }
    .footer {
      position: absolute;
      color: #f5f5f5;
      font-size: 14px;
      bottom: -50px;
      width: 100%;
      text-align: center;
    }
    .graph {
      height: 100%;
      margin-right: 450px;
      background-position: center top;
      background-size: 100%;
      background-repeat: no-repeat;
      min-height: 500px;
      border-radius: 4px 0 0 4px;
      overflow: hidden;
    }
    .form {
      box-sizing: border-box;
      position: absolute;
      top: 0;
      right: 0;
      width: 450px;
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 500px;
      .login-body {
        width: 80%;
        margin-top: -100px;
        .form-title {
          font-size: 14px;
          text-align: center;
          margin-bottom: 50px;
          img {
            display: inline-block;
            height: 28px;
            overflow: hidden;
          }
        }
        .login-username {
          width: 20px;
          height: 20px;
          margin: 0px -10px;
          background-image: url("../../assets/images/user.png");
        }
        .login-password {
          width: 20px;
          height: 20px;
          margin: 0px -10px;
          background-image: url("../../assets/images/lock.png");
        }
      }
    }
  }
}
.right-graph {
  .login-form {
    min-width: 1000px;
    background-color: #fff;
    /*width: 80%;*/
    width: 1000px;
    margin: 0 auto;
    margin-top: -250px;
    /*margin-left: -40%;*/
    margin-left: -500px;
    &:after {
      content: "";
      height: 0;
      line-height: 0;
      display: block;
      visibility: hidden;
      clear: both;
    }
    .footer {
      position: absolute;
      color: #f5f5f5;
      font-size: 14px;
      bottom: -50px;
      width: 100%;
      text-align: center;
    }
    .graph {
      height: 100%;
      border-radius: 0 4px 4px 0;
      margin-left: 450px;
      background-position: center top;
      background-size: 100%;
      background-repeat: no-repeat;
      min-height: 500px;
      overflow: hidden;
    }
    .form {
      box-sizing: border-box;
      width: 450px;
      float: left;
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 500px;
      .login-body {
        width: 80%;
        margin-top: -100px;
        .form-title {
          font-size: 25px;
          text-align: center;
          margin-bottom: 50px;
          img {
            display: inline-block;
            height: 28px;
            overflow: hidden;
          }
        }
        .login-username {
          width: 20px;
          height: 20px;
          margin: 0px -10px;
          background-image: url("../../assets/images/user.png");
        }
        .login-password {
          width: 20px;
          height: 20px;
          margin: 0px -10px;
          background-image: url("../../assets/images/lock.png");
        }
      }
    }
  }
}
</style>
