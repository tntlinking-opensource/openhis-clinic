<template>
  <el-card class="index-module-card" v-loading="isCardLoading" :style="styleObject">
    <div slot="header">
      <span class="el-card-title">常用应用</span>
      <!-- <i style="margin-left: 10px;" class="app-config el-icon-setting"></i> -->
      <i style="margin-left: 10px;" v-show="!isPanelSetIcon" class="card-close el-icon-close" @click="deletePanelItem"></i>
      <i style="margin-left: 10px;" v-show="isPanelSetIcon" class="card-setting iconfont icon-setting"></i>
    </div>
    <div class="often-body" style="position: relative; min-height: 210px;">
      <el-row>
        <el-carousel height="200px" :autoplay="false" indicator-position="outside" arrow="never" :loop="false">
          <el-carousel-item v-for="num in carouselNum" :key="num">
            <el-col :span="6" v-for="(item, index) in carouselRouters(num)" :key="item.id" v-if="index < 8">
              <div class="often-app" @click="handleAppClick(item.id)">
                <app-icon :icon="item.cssClass"></app-icon>
                <span class="app-name">{{item.name}}</span>
              </div>
            </el-col>
          </el-carousel-item>
        </el-carousel>
        <div class="no-data" v-if="appLists.length === 0">暂无数据</div>
      </el-row>
    </div>
  </el-card>
</template>

<script>
  import { isLightOrDark } from '@/utils/common'
  import appIcon from './appIcon'
  import { getLocalRouters } from '@/utils/auth'
export default {
  props: ['id', 'panelSetIcon'],
  components: { appIcon },
  data () {
    return {
      isCardLoading: false,
      panelId: this.id,
      isPanelSetIcon: this.panelSetIcon,
      appLists: []
    }
  },
  watch:{
    panelSetIcon:{
      deep:true,
      handler(){
        this.isPanelSetIcon = this.panelSetIcon;
      }
    }
  },
  mounted() {
    this.getRouters()
  },
  computed: {
    ...Vuex.mapGetters(['settings', 'menus']),
    routers() {
      return function () {
        return new Promise((resolve, reject) => {
          let items = [];
          let routers = getLocalRouters()
          // 增加用户体验
          setTimeout(() => {
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

            resolve(items)
          }, 500)
        })
      }
    },
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
    carouselNum() {
      return Math.ceil(this.appLists.length / 8)
    },
    carouselRouters() {
      return function (num) {
        return this.appLists.slice((num - 1) * 8, num * 8)
      }
    }
  },
  methods: {
    ...Vuex.mapActions({
      getMenus: 'menus/getMenus'
    }),
    getRouters() {
      this.isCardLoading = true
      this.routers().then(result => {
        this.isCardLoading = false
        result.forEach((item, index) => {
          if (!item.code && item.parentId == 0) {
            this.appLists.push(item)
          }
        })
      })
    },
    handleAppClick(menuId) {
      this.getMenus(menuId)
    },
    // 删除面板项发送事件
    deletePanelItem () {
      this.$emit('deletePanelItemEvent', this.panelId)
    }
  }
}
</script>

<style lang="scss" scoped>
  .index-module-card.el-card /deep/ .el-card__header {
    padding: 10px 16px!important;
    .card-close, .card-setting {
      float: right;
      padding: 3px 0
    }
  }
.el-card {
  height: 100%;
}

.el-card-title {
  font-weight: bold;
}
.app-config {
  float: right;
  font-size: 1.2em;
  padding: 3px 0;
  cursor: pointer;
}
  .often-app {
    text-align: center;
    cursor: pointer;
    padding: 10px 0;
    .app-name {
      padding: 10px 0;
      font-size: 12px;
      display: inline-block;
    }
    &:hover {
      .app-name {
        color: var(--active-color)!important;
      }
      /deep/ .icon {
        font-size: 18px;
      }
      /deep/ .l-t {
        left: -6px!important;
        top: -6px!important;
      }
      /deep/ .r-t {
        right: -6px!important;
        top: -6px!important;
      }
      /deep/ .l-b {
        left: -6px!important;
        bottom: -6px!important;
      }
      /deep/ .r-b {
        right: -6px!important;
        bottom: -6px!important;
      }
    }
  }
  /deep/ .el-carousel__indicators--outside button {
    background-color: var(--active-color)!important;
  }
  .no-data {
    position: absolute;
    width: 100%;
    font-size: 12px;
    text-align: center;
    top: 48.99%;
  }
</style>
