<template>
  <div class="book-mark-container" :style="styleObject" @click.stop>
    <i class="el-icon-star-on fly-shoot" :class="fly? 'star' : ''"></i>
    <el-menu
      mode="horizontal"
      class="current-menu"
      :index="currentKey"
      @select="handleCurrentSelect">
      <draggable
        v-model="collectList"
        group="site"
        class="current-drag"
        animation="300"
        dragClass="dragClass"
        ghostClass="ghostClass"
        chosenClass="chosenClass"
        @add="onCurrentAdd"
        @start="onCurrentStart"
        @end="onCurrentEnd"
        @sort="onSort">
        <el-menu-item
          class="book-mark-item"
          v-for="(item, index) in collectList"
          :key="item.id"
          :index="currentKey + '-' + item.id"
          @contextmenu.prevent.native="openMenu(item, index, $event)">
          <router-link :to="handleRouterLink(item.routerId)">
            <svg-icon :iconClass="getMarkIcon(item.routerId)"/>
            {{item.routerId.name}}
          </router-link>
        </el-menu-item>
      </draggable>
    </el-menu>
    <el-menu
      ref="moreMenu"
      mode="horizontal"
      class="more-menu"
      menu-trigger="click"
      @open="handleMoreOpen"
      @select="handleMoreSelect">
      <el-submenu :index="moreKey">
        <template slot="title" v-if="collectMoreList.length > 0">
          <i class="el-icon-d-arrow-right"></i>
        </template>
        <div v-if="collectMoreList.length > 10" class="more-menu-caret caret-top" :class="isUpDisabled? 'is-more-disabled' : ''" :style="styleObject" ref="caretTop">
          <i class="el-icon-caret-top"></i>
        </div>
        <div ref="moreDragContainer" class="more-drag-container" :style="{maxHeight: getMoreItemHeight + 'px'}">
          <draggable
            v-model="collectMoreList"
            :style="styleObject"
            group="site"
            class="more-drag"
            ref="moreDrag"
            animation="300"
            dragClass="dragMoreClass"
            ghostClass="ghostMoreClass"
            chosenClass="chosenMoreClass"
            @add="onMoreAdd"
            @start="onMoreStart"
            @end="onMoreEnd"
            @sort="onSort">
            <el-menu-item
              ref="menuItem"
              class="book-mark-item"
              v-for="(item, index) in collectMoreList"
              :key="item.id"
              :index="moreKey + '-' + item.id">
              <router-link :to="handleRouterLink(item.routerId)">
                <span>
                  <svg-icon :iconClass="getMarkIcon(item.routerId)"/>
                  {{item.routerId.name}}
                </span>
              </router-link>
                <i class="el-icon-delete" @click.stop="deleteCollect(item.id)"></i>
            </el-menu-item>
          </draggable>
        </div>
        <div v-if="collectMoreList.length > 10" class="more-menu-caret caret-bottom" :class="isDownDisabled? 'is-more-disabled' : ''" :style="styleObject" ref="caretBottom">
          <i class="el-icon-caret-bottom"></i>
        </div>
      </el-submenu>
    </el-menu>
    <div class="favorite" :style="styleObject">
      <span class="collect-num" :class="fly? 'num' : ''">+1</span>
      <el-tooltip :content="isCollected? '已收藏' : '未收藏'" placement="top" effect="light">
        <i class="el-icon-star-on" :class="isCollected? 'is-active' : ''" @click="handleCollect"></i>
      </el-tooltip>
    </div>

    <ul v-show="visible" :style="{left: left + 'px', top: top + 'px'}" class="contextmenu">
      <li @click="deleteCollect(currentId)">删除</li>
    </ul>
  </div>
</template>

<script>
  import draggable from "vuedraggable"
  import { listCollectsByUserId, saveCollect, updateCollect, deleteCollect } from '@/api/collect/collect'
  import { getLocalCurrentUser } from '@/utils/auth'
	export default {
		name: "BookMark",
    components: { draggable },
    data() {
		  return {
        currentKey: '1',
        moreKey: '2',
        currentSelected: '/',
        moreSelected: '/',
        limit: 6,
        staticList: [],
        collectList: [],
        collectMoreList: [],
        isUpDisabled: true,
        isDownDisabled: false,
        moreDragHeight: 0,
        moreScrollUp: null,
        moreScrollDown: null,
        fly: false,
        isCollected: false,
        visible: false,
        top: 0,
        left: 0,
        currentId: '',
        isClick: false,
        isDeleteClick: false,
      }
    },
    watch: {
      $route(to, from) {
        this.fly = false
        this.isFavorite(to.meta.routerId)
      },
      visible(value) {
        if (value) {
          document.body.addEventListener('click', this.closeMenu)
        } else {
          document.body.removeEventListener('click', this.closeMenu)
        }
      }
    },
    computed: {
      ...Vuex.mapGetters(['settings']),
      styleObject() {
        return {
          '--active-color': this.settings.theme
        }
      },

      getMoreItemHeight() {
        return this.moreDragHeight * 10
      },

      getMarkIcon() {
        return (routerId) => {
          let properties = JSON.parse(routerId.properties)
          if (properties.cssClass) {
            return properties.cssClass
          }
        }
      },

      handleRouterLink() {
        return (routerId) => {
          const url = routerId.url
          const properties = JSON.parse(routerId.properties)
          if (this.hasContainSpecial(url)) {
            return `/${url.split(':')[0]}${properties.params}`
          } else {
            return `/${url}`
          }
        }
      }
    },
    mounted() {
      this.initMoreList()
      this.getDatas()
    },
    methods: {
      hasContainSpecial(url) {
        const specialStr = new RegExp(/:/g)
        return specialStr.test(url)
      },
		  getUserId() {
        return getLocalCurrentUser().id
      },
      getRouterId() {
        const { meta } = this.$route
        return meta.routerId
      },
      getDatas() {
        const userId = this.getUserId()
        return new Promise((resolve, reject) => {
          listCollectsByUserId(userId).then(responseData => {
            if(responseData.code == 100) {
              this.staticList = responseData.data;
              this.collectList = this.staticList.slice(0, this.limit)
              this.collectMoreList = this.staticList.slice(this.limit)
              this.isFavorite(this.getRouterId());
              this.isClick = true
              this.isDeleteClick = true
              resolve(true)
            }
          }).catch(error => {
            reject(error)
          })
        })
      },
      getCollectId() {
        const temp = this.staticList.find((item) => {
          return item.routerId.id == this.getRouterId()
        })
        if (temp) {
          return temp.id
        }
      },
      isFavorite(routerId) {
        const temp = this.staticList.find((item) => {
          return item.routerId.id == routerId
        })

        this.isCollected = temp? true : false
      },
      initMoreList() {
        const speed = 100
        const caretBottom = this.$refs.caretBottom
        const caretTop = this.$refs.caretTop
        document.body.addEventListener('click', this.close)
        if (this.collectMoreList.length > 10) {
          caretBottom.onmouseover = () => {
            this.moreScrollUp = setInterval(this.scrollUp, speed)
          }
          caretBottom.onmouseout = () => {
            clearInterval(this.moreScrollUp)
          }

          caretTop.onmouseover = () => {
            this.moreScrollDown = setInterval(this.scrollDown, speed)
          }
          caretTop.onmouseout = () => {
            clearInterval(this.moreScrollDown)
          }
        }
      },
      handleCollect() {
        const mark = {
          userId: {
            id: this.getUserId()
          },
          routerId: {
            id: this.getRouterId()
          }
        }
        if (this.isClick) {
          this.isClick = false
          if (!this.isCollected) {
            saveCollect(mark).then(responseData => {
              if(responseData.code == 100) {
                const id = responseData.data
                const name = this.$route.meta.name
                const url = this.$route.path.slice(1)
                const currentRouterId = this.$route.meta.routerId
                const properties = {
                  cssClass: this.$route.meta.cssClass
                }
                const routerId = { id: currentRouterId, url, name, properties: JSON.stringify(properties) }
                const tmp = { id, routerId }
                if (this.collectList.length < this.limit) {
                  this.collectList.push(tmp)
                } else {
                  this.collectMoreList.push(tmp)
                }
                this.staticList = [...this.collectList, ...this.collectMoreList]
                this.isClick = true
                this.isCollected = true
                this.fly = true
                this.$message({
                  type: 'success',
                  message: '收藏成功'
                })
              }
            }).catch(error => {
              this.isClick = true
              this.$message({
                type: 'error',
                message: '收藏失败'
              })
            })

          } else {
            this.deleteCollect()
          }
        }
      },
      handleMoreOpen() {
        this.$nextTick(() => {
          const menuItem = this.$refs.menuItem
          this.moreDragHeight = menuItem[0].$el.clientHeight
        })
      },
      scrollUp() {
        const moreDragContainer = this.$refs.moreDragContainer
        const moreDrag = this.$refs.moreDrag
        moreDragContainer.scrollTop += this.moreDragHeight
        this.isUpDisabled = false
        if (moreDragContainer.scrollTop >= (moreDrag.$el.clientHeight - moreDragContainer.clientHeight)) {
          this.isDownDisabled = true
          clearInterval(this.moreScrollUp)
        }
      },
      scrollDown() {
        const moreDragContainer = this.$refs.moreDragContainer
        moreDragContainer.scrollTop -= this.moreDragHeight
        this.isDownDisabled = false
        if (moreDragContainer.scrollTop === 0) {
          this.isUpDisabled = true
          clearInterval(this.moreScrollDown)
        }

      },
      handleCurrentSelect(key, keyPath) {
        this.$refs.moreMenu.close('2')
      },
      handleMoreSelect(key, keyPath) {},
      onCurrentAdd(e) {
        const currentLast = this.collectList.pop()
        this.collectMoreList.unshift(currentLast)
      },
      onMoreAdd(e) {
        const moreFirst = this.collectMoreList.shift()
        this.collectList.push(moreFirst)
      },
      // 开始拖拽
      onCurrentStart(e) {},
      // 拖拽结束
      onCurrentEnd(e) {
        if (this.collectMoreList.length > 10) {
          this.initMoreList()
        }
      },
      onSort(e) {
        const userId = this.getUserId()
        this.staticList = [...this.collectList, ...this.collectMoreList]
        updateCollect(userId, this.staticList).then(responseData => {
        }).catch(error => {})
      },

      // 开始拖拽
      onMoreStart(e) {},
      // 拖拽结束
      onMoreEnd(e) {},

      deleteCollect(currentId) {
        const id = currentId? currentId : this.getCollectId()
        const collectId = { id }
        this.closeMenu()
        if (this.isDeleteClick) {
          this.isDeleteClick = false
          deleteCollect(collectId).then(async responseData => {
            if(responseData.code == 100) {
              const isNew = await this.getDatas()
              if (isNew) {
                this.staticList = [...this.collectList, ...this.collectMoreList]
                if (this.collectMoreList.length === 0) {
                  this.$refs.moreMenu.close('2')
                }
              }
              this.isDeleteClick = true
              this.fly = false
              this.$message({
                type: 'info',
                message: '取消收藏'
              })
            }
          }).catch(error => {
            this.isDeleteClick = true
          })
        }
      },
      openMenu(item, index, e) {
        const menuMinWidth = 105
        const offsetLeft = this.$el.getBoundingClientRect().left
        const offsetWidth = this.$el.offsetWidth
        const maxLeft = offsetWidth - menuMinWidth
        const left = e.clientX - offsetLeft + 15

        if (left > maxLeft) {
          this.left = maxLeft
        } else {
          this.left = left
        }

        this.currentId = item.id
        this.top = e.clientY - 56
        this.visible = true
      },
      close() {
        this.closeMenu()
        this.$refs.moreMenu.close('2')
      },
      closeMenu() {
        this.visible = false
      },
      hasClass(element, cls) {
        return (' ' + element.className + ' ').indexOf(' ' + cls + ' ') > -1
      }
    }
	}
</script>

<style lang="scss" scoped>

</style>
<style lang="scss">
.book-mark-container {
  position: relative;
  display: flex;
  align-items: center;
  .el-menu {
    border-bottom: none!important;
    position: relative;
    &:after {
      content: '';
      position: absolute;
      width: 1px;
      height: 24px;
      top: 50%;
      margin-top: -12px;
      right: 0;
      background-color: #ddd;
    }
    .el-menu-item, .el-submenu {
      height: 36px !important;
      line-height: 36px;
      font-size: 12px;
      padding: 0 10px;
      border-bottom: 0;
      &.is-active {
        border-bottom: 0;
        //color: var(--active-color) !important;
        color: #555!important;
        &:focus {
          background: none;
        }
      }
      &:before {
        content: '';
        position: absolute;
        left: 0;
        width: 1px;
        height: 20px;
        background-color: #eee;
        top: 50%;
        margin-top: -9px;
      }
      &:first-child {
        &:before {
          width: 0!important;
        }
      }
      &:hover {
        background: none;
        background-color: rgba(255,255,255,0) !important;
        color: var(--active-color) !important;
        .svg-icon {
          color: var(--active-color) !important;
          fill: currentColor;
        }
      }
      &:focus {
        background: none;
        background-color: rgba(255,255,255,0) !important;
      }
      .el-submenu__title {
        height: 36px !important;
        line-height: 36px;
        font-size: 12px;
        padding: 0;
        border-bottom: none!important;
        .el-submenu__icon-arrow {
          display: none;
        }
      }
      &:not(.is-disabled):hover {
        color: var(--active-color) !important;
      }
      a {
        text-decoration: none;
        color: #555;
        &:hover {
          color: var(--active-color) !important;
        }
      }
    }
    .current-drag {
      display: flex;
      .el-menu-item {
        line-height: 36px!important;
      }
    }
  }
  .current-menu {
    &:after {
      display: none;
    }
  }
  .more-menu {
    .el-submenu {
      padding: 0!important;
      i:hover {
        color: var(--active-color) !important;
      }
    }
    i {
      font-size: 12px;
    }
    font-size: 12px;
    .is-active .el-submenu__title {
      border-bottom: none!important;
    }
  }
  .favorite {
    padding: 0 10px;
    color: #999;
    cursor: pointer;
    position: relative;
    &:hover {
      color: #555;
    }
    .collect-num {
      position: absolute;
      color: var(--active-color) !important;
      right: 50px;
      top: 20px;
      opacity: 0;
      &.num {
        animation: flyNum 2s ease 1.5s;
      }
    }
    .is-active {
      color: var(--active-color) !important;
      animation: star 1s ease;
    }
  }
  .ghostClass{
    background-color: #f5f5f5;
  }
  .chosenClass{
    color: #999999 !important;
    background-color: #f5f5f5!important;
  }
  .ghostMoreClass{
    background-color: #f5f5f5!important;
    i {
      display: none!important;
    }
  }
  .chosenMoreClass{
    i {
      display: none!important;
    }
    color: #999999 !important;
    background-color: #f5f5f5!important;
  }
  .fly-shoot {
    position: absolute;
    opacity: 0;
    color: var(--active-color) !important;
    right: 28px;
    transform:rotate(720deg);
    z-index: 10;
    &.star {
      animation: fly 2s ease .5s;
    }
  }

  .contextmenu {
    margin: 0;
    background: #fff;
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 5px 0;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 400;
    color: #333;
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, .3);
    li {
      margin: 0;
      padding: 7px 16px;
      cursor: pointer;
      &:hover {
        background: #eee;
      }
    }
  }
}
.book-mark-item {
  font-size: 12px!important;
  /*height: 30px!important;*/
  /*line-height: 30px!important;*/
  display: flex;
  align-items: center;
  justify-content: space-between;
  i {
    display: none;
    font-size: 12px!important;
    &:hover {
      color: var(--active-color) !important;
    }
  }
}
.more-menu-caret {
  text-align: center;
  cursor: pointer;
  color: #999;
  &:hover {
    color: var(--active-color) !important;
    background-color: #f5f5f5;
  }
  &.caret-top {
    border-bottom: 1px solid #f5f5f5;
  }
  &.caret-bottom {
    border-top: 1px solid #f5f5f5;
  }
}
.more-drag {
  .book-mark-item {
    color: #555!important;
    &.is-active {
      //color: var(--active-color) !important;
      color: #555!important;
      background-color: rgba(0, 0, 0, 0) !important;
    }
    &:hover {
      i {
        display: block;
      }
      background-color: rgba(0, 0, 0, 0) !important;
    }
    a {
      text-decoration: none;
      color: #555;
      &:hover {
        color: var(--active-color) !important;
        .svg-icon {
          color: var(--active-color) !important;
          fill: currentColor;
        }
      }
    }
    i:hover {
      color: var(--active-color) !important;
    }
    .ghostMoreClass{
      background-color: #f5f5f5!important;
    }
    .chosenMoreClass{
      color: #999999 !important;
      background-color: #f5f5f5!important;
    }
  }
}
 .more-drag-container {
   overflow: hidden;
 }
.is-more-disabled {
  color: #f5f5f5;
  &:hover {
    background-color: #fff;
    color: #f5f5f5!important;
  }
}

@keyframes star
{
  0% {transform: scale(1) rotate(0deg)}
  50% {transform: scale(1.5) rotate(45deg)}
  100% {transform: scale(1) rotate(0deg)}
}
@-webkit-keyframes star
{
  0% {transform: scale(1) rotate(0deg)}
  50% {transform: scale(1.5) rotate(45deg)}
  100% {transform: scale(1) rotate(0deg)}
}
@keyframes fly
{
  0%   {top: 10px; right: 10px; opacity: 1; transform:rotate(0deg);}
  30%  {top: 30px; right: 30px; opacity: .8; transform:rotate(360deg);}
  100% {top: 10px; right: 50px; opacity: 0; transform:rotate(720deg);}
}
@-webkit-keyframes fly
{
  0%   {top: 10px; right: 10px; opacity: 1; transform:rotate(0deg);}
  30%  {top: 30px; right: 30px; opacity: .8; transform:rotate(360deg);}
  100% {top: 10px; right: 50px; opacity: 0; transform:rotate(720deg);}
}
@keyframes flyNum
{
  0% { top: 20px; opacity: 0 }
  30% { top: 30px; opacity: 1}
  100% { top: 10px; opacity: 0 }
}
@-webkit-keyframes flyNum
{
  0% { top: 20px; opacity: 0 }
  30% { top: 30px; opacity: 1}
  100% { top: 10px; opacity: 0 }
}
</style>
