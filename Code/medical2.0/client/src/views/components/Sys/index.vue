<template>
  <div class="drawer-container" v-loading="drawerLoading" element-loading-text="更新中">
    <div>
      <h3 class="drawer-title">系统设置</h3>
      <div class="drawer-item">
        <span>系统名称</span>
        <el-input v-if="sysData" v-model="sysData.sysName" style="width: 170px" placeholder="系统名称"></el-input>
        <el-popover
          class="img-tip-text"
          placement="left"
          title="显示位置"
          width="450"
          trigger="hover">
          <el-image fit="fill" :src="tipImg8"></el-image>
          <el-button type="text" slot="reference">图例说明</el-button>
        </el-popover>
      </div>
      <div class="drawer-item">
        <span>系统简称</span>
        <el-input  v-if="sysData" v-model="sysData.sysAbbrname" style="width: 170px" placeholder="系统简称"></el-input>
        <el-popover
          class="img-tip-text"
          placement="left"
          title="显示位置"
          width="450"
          trigger="hover">
          <el-image fit="fill" :src="tipImg7"></el-image>
          <el-button type="text" slot="reference">图例说明</el-button>
        </el-popover>
      </div>
      <div class="drawer-item img-item" :class="fileList1.length > 0? 'hasImage' : ''" style="padding-top: 12px">
        <i class="tip">建议：174*32，等比，png，用于登录页</i>
        <span>项目logo（<512K）</span>
        <el-popover
          class="img-tip"
          placement="left"
          title="显示位置"
          width="450"
          trigger="hover">
          <el-image fit="fill" :src="tipImg1"></el-image>
          <el-button type="text" slot="reference">图例说明</el-button>
        </el-popover>
        <image-upload
          ref="imageUpload"
          :on-change="(file, fileList) => handleChange(file, fileList, 'fileList1')"
          :on-remove="(file, fileList) => handleRemove(file, fileList, 'fileList1', 'projectLogoIndex')"
          :file-list="fileList1">
        </image-upload>
      </div>
      <div class="drawer-item img-item" :class="fileList2.length > 0? 'hasImage' : ''">
        <i class="tip">建议：152*28，等比，png，用于登录框</i>
        <span>登录框logo（<512K）</span>
        <el-popover
          class="img-tip"
          placement="left"
          title="显示位置"
          width="450"
          trigger="hover">
          <el-image fit="fill" :src="tipImg2"></el-image>
          <el-button type="text" slot="reference">图例说明</el-button>
        </el-popover>
        <image-upload
          ref="imageUpload"
          :on-change="(file, fileList) => handleChange(file, fileList, 'fileList2')"
          :on-remove="(file, fileList) => handleRemove(file, fileList, 'fileList2', 'loginLogoIndex')"
          :file-list="fileList2"></image-upload>
      </div>
      <div class="drawer-item img-item" :class="fileList3.length > 0? 'hasImage' : ''">
        <i class="tip">建议：170*30，等比，png，用于系统主页</i>
        <span>系统主页logo（<512K）</span>
        <el-popover
          class="img-tip"
          placement="left"
          title="显示位置"
          width="450"
          trigger="hover">
          <el-image fit="fill" :src="tipImg3"></el-image>
          <el-button type="text" slot="reference">图例说明</el-button>
        </el-popover>
        <image-upload
          ref="imageUpload"
          :on-change="(file, fileList) => handleChange(file, fileList, 'fileList3')"
          :on-remove="(file, fileList) => handleRemove(file, fileList, 'fileList3', 'sysLogoIndex')"
          :file-list="fileList3"></image-upload>
      </div>
      <div class="drawer-item img-item" :class="fileList4.length > 0? 'hasImage' : ''">
        <i class="tip">建议：16*16，等比，ico，用于网页顶部</i>
        <span>网站图标ICO</span>
        <el-popover
          class="img-tip"
          placement="left"
          title="显示位置"
          width="450"
          trigger="hover">
          <el-image fit="fill" :src="tipImg4"></el-image>
          <el-button type="text" slot="reference">图例说明</el-button>
        </el-popover>
        <image-upload
          ref="imageUpload"
          :on-change="(file, fileList) => handleChange(file, fileList, 'fileList4')"
          :on-remove="(file, fileList) => handleRemove(file, fileList, 'fileList4', 'faviconIndex')"
          :file-list="fileList4"></image-upload>
      </div>
      <h3 class="drawer-title">登录页</h3>
      <div class="drawer-item">
        <el-row class="drawer-style" :gutter="10">
          <el-col :span="6" v-for="(item, index) in loginLayouts" :key="index">
            <span v-if="sysData"
                  ref="drawerStyle"
                  :style="styleObject"
                  :class="sysData.loginLayout === item.code? 'is-active' : ''"
                  @click="handleLayout(item.code)">
              <i class="el-icon-check"></i>
            <img :src="item.icon" alt="">
          </span>
          </el-col>
        </el-row>
      </div>
      <div class="drawer-item">
        <span>背景色</span>
        <el-color-picker
          v-if="sysData"
          v-model="sysData.loginBgcolor"
          color-format="rgb"
          :predefine="predefine"
          class="theme-picker color-picker"
          popper-class="theme-picker-dropdown"/>
      </div>
      <div class="drawer-item img-item" :class="fileList5.length > 0? 'hasImage' : ''">
        <i class="tip">jpg/png，用于登录页背景图</i>
        <span>背景图（<512K）</span>
        <el-popover
          class="img-tip"
          placement="left"
          title="显示位置"
          width="450"
          trigger="hover">
          <el-image fit="fill" :src="tipImg5"></el-image>
          <el-button type="text" slot="reference">图例说明</el-button>
        </el-popover>
        <image-upload
          ref="imageUpload"
          :on-change="(file, fileList) => handleChange(file, fileList, 'fileList5')"
          :on-remove="(file, fileList) => handleRemove(file, fileList, 'fileList5', 'loginBgIndex')"
          :file-list="fileList5"></image-upload>
      </div>
      <div class="drawer-item img-item" :class="fileList6.length > 0? 'hasImage' : ''">
        <i class="tip">建议：690*500，等比，jpg/png，用于登录框</i>
        <span>登录框配图（<512K）</span>
        <el-popover
          class="img-tip"
          placement="left"
          title="显示位置"
          width="450"
          trigger="hover">
          <el-image fit="fill" :src="tipImg6"></el-image>
          <el-button type="text" slot="reference">图例说明</el-button>
        </el-popover>
        <image-upload
          ref="imageUpload"
          :on-change="(file, fileList) => handleChange(file, fileList, 'fileList6')"
          :on-remove="(file, fileList) => handleRemove(file, fileList, 'fileList6', 'loginGraphIndex')"
          :file-list="fileList6"></image-upload>
      </div>
      <div class="drawer-footer" style="text-align: center; padding: 10px 0">
        <el-button size="mini" type="primary" plain icon="el-icon-document-checked" @click="handleSysSave">保存设置</el-button>
      </div>
    </div>
  </div>
</template>

<script>
  import ThemePicker from '@/components/ThemePicker'
  import imageUpload from './imageUpload'
  
  import { listSysSetingAll, saveSysSeting } from '@/api/sys/sysSeting.js'
  export default {
    name: "Sys",
    components: {
      ThemePicker,
      imageUpload
    },
    data() {
      return {
        baseApi: process.env.BASE_API,
        isFirstOpen: true,  // 首次文件标识
        drawerLoading: false,
        predefine: ['#018cb7', '#409EFF', '#1890ff', '#2f4050', '#212121', '#11a983', '#13c2c2', '#6959CD'],
        fileList1: [],
        fileList2: [],
        fileList3: [],
        fileList4: [],
        fileList5: [],
        fileList6: [],
        tempList: [],
        projectLogoIndex: -1,
        loginLogoIndex: -1,
        sysLogoIndex: -1,
        faviconIndex: -1,
        loginBgIndex : -1,
        loginGraphIndex: -1,
        sysData: {
          id:"",
          sysName: '',      // 系统名称
          sysAbbrname: '',  // 系统简称
          loginBgcolor: "", // 登录页背景色
          loginLayout: 'left-graph',
          loginLogo: {      // 登录框logo
            id: null,
            name: ""
          },
          projectLogo: {    // 项目logo
            id: null,
            name: ""
          },
          sysLogo: {        // 系统主页logo
            id: null,
            name: ""
          },
          favicon: {        // 网页顶部 favicon 图标
            id: null,
            name: ""
          },
          loginBg: {        // 登录页背景图
            id: null,
            name: ""
          },
          loginGraph: {     // 登录页配图
            id: null,
            name: ""
          },
        },
        loginLayouts: [
          {code: 'simple-center', icon: require('../../../assets/images/layout_simple.png')},
          {code: 'left-graph', icon: require('../../../assets/images/layout_left_graph.png')},
          {code: 'right-graph', icon: require('../../../assets/images/layout_right_graph.png')},
        ],
        tipImg1: require('../../../assets/images/xmbt.jpg'),
        tipImg2: require('../../../assets/images/dltb.jpg'),
        tipImg3: require('../../../assets/images/xtzylg.jpg'),
        tipImg4: require('../../../assets/images/wztb.jpg'),
        tipImg5: require('../../../assets/images/dlbjt.jpg'),
        tipImg6: require('../../../assets/images/dlpt.jpg'),
        tipImg7: require('../../../assets/images/xtjc.jpg'),
        tipImg8: require('../../../assets/images/xtmc.jpg')
      }
    },
    watch: {},
    mounted() {
      this.$nextTick(() => {
        this.$on('openSys', function() {
          if(this.isFirstOpen) {
            this.isFirstOpen = false
            this.getSysList()
          }
        })
      })
    },
    computed: {
      ...Vuex.mapGetters(['settings', 'sys']),
      styleObject() {
        return {
          '--border-active-color': this.settings.theme
        }
      }
    },
    methods: {
      getSysList() {
        this.drawerLoading = true
        let parmas = {}
        listSysSetingAll(parmas).then(response => {
          if (response.code == 100) {
            this.drawerLoading = false
            this.sysData = response.data[0];
            this.getFilesById('fileList1', 1, this.sysData.projectLogo.name)
            this.getFilesById('fileList2', 2, this.sysData.loginLogo.name)
            this.getFilesById('fileList3', 3, this.sysData.sysLogo.name)
            this.getFilesById('fileList4', 4, this.sysData.favicon.name)
            this.getFilesById('fileList5', 5, this.sysData.loginBg.name)
            this.getFilesById('fileList6', 6, this.sysData.loginGraph.name)
          }

        }).catch(error => {
          this.drawerLoading = false
        })
      },
      getFilesById(fileList, fileId, fileName) {
        let fileObj = {}
        if (fileId) {
          fileObj.name = fileName
          fileObj.url = `${this.baseApi}/sys/sysSeting/getFile/${fileId}`
          this[fileList].push(fileObj)
        }
      },
      handleChange(file, fileList, list) {
        let str = file.name.substring(file.name.lastIndexOf('.') + 1)
        let isLt512 = file.size / 1024 < 512
        if (!isLt512) {
          this.$message.warning('上传的图片不能超过512k')
          file = null
          this[list].splice(fileList.length - 1, 1)
          return false
        }
        if (fileList.length > 0) {
          this[list] = [fileList[fileList.length - 1]]
        }
      },
      handleRemove(file, fileList, list, index) {
        this[list] = fileList
        this.tempList.splice(this[index], 1)
      },
      async handleSysSave() {
        this.drawerLoading = true

        const sysData = {
          id: this.sysData.id,
          sysName: this.sysData.sysName,
          sysAbbrname: this.sysData.sysAbbrname,
          loginBgcolor: this.sysData.loginBgcolor,
          loginLayout: this.sysData.loginLayout,
          loginLogo: this.sysData.loginLogo,
          projectLogo: this.sysData.projectLogo,
          sysLogo: this.sysData.sysLogo,
          favicon: this.sysData.favicon,
          loginBg: this.sysData.loginBg,
          loginGraph: this.sysData.loginGraph,
          projectLogoIndex: -1,
          loginLogoIndex: -1,
          sysLogoIndex: -1,
          faviconIndex: -1,
          loginBgIndex: -1,
          loginGraphIndex: -1
        }
        this.tempList = []
        this.projectLogoIndex = this.fileIndex(this.fileList1)
        this.loginLogoIndex = this.fileIndex(this.fileList2)
        this.sysLogoIndex = this.fileIndex(this.fileList3)
        this.faviconIndex = this.fileIndex(this.fileList4)
        this.loginBgIndex = this.fileIndex(this.fileList5)
        this.loginGraphIndex = this.fileIndex(this.fileList6)

        sysData.projectLogoIndex =  this.projectLogoIndex
        sysData.loginLogoIndex = this.loginLogoIndex
        sysData.sysLogoIndex = this.sysLogoIndex
        sysData.faviconIndex = this.faviconIndex
        sysData.loginBgIndex = this.loginBgIndex
        sysData.loginGraphIndex = this.loginGraphIndex

        let formData = this.createFormData(sysData, this.tempList)
        saveSysSeting(formData).then(response => {
          if (response.code == 100) {
            this.$message({
              type: 'success',
              message: '保存成功'
            })
            this.drawerLoading = false
          } else {
            this.$message({
              type: 'error',
              message: '出错'
            })
            this.drawerLoading = false
          }
        })

      },
      fileIndex(fileList) {
        let index
        if (fileList.length > 0 && fileList[0].raw ) {
          this.tempList = [...this.tempList, ...fileList]
          index = this.tempList.length - 1
        } else {
          index = -1
        }
        return index
      },
      createFormData(bizFormModel, files) {
        let formData = new FormData()
        formData.append('sysSeting', JSON.stringify(bizFormModel))
        for (let idx in files) {
          formData.append('attachments', files[idx].raw)
        }
        return formData
      },
      handleLayout(code) {
        this.sysData.loginLayout = code
      }
    }
  }
</script>

<style lang="scss" scoped>
  .drawer-container {
    padding: 24px;
    font-size: 14px;
    line-height: 1.5;
    word-wrap: break-word;

    .drawer-title {
      margin: 0;
      padding: 12px 0;
      color: rgba(0, 0, 0, .85);
      font-size: 14px;
      line-height: 22px;

      &:first-child {
        border-top: none;
      }

      border-top: 4px solid #f5f5f5;
    }

    .drawer-item {
      position: relative;
      width: 100%;
      color: rgba(0, 0, 0, .65);
      font-size: 14px;
      padding: 12px 0;
      display: flex;
      align-content: center;
      justify-content: space-between;
      align-items: center;

      &.img-item {
        padding-top: 20px;
        padding-bottom: 34px;
        border-top: 1px solid #dbdbdb;
      }

      .tip {
        position: absolute;
        bottom: 14px;
        left: 0;
        font-size: 12px;
        font-style: normal;
        color: #e85f3e;
      }

      .img-tip, .img-tip-text {
        position: absolute;
        right: 10px;
        bottom: 105px;
        z-index: 10;
      }

      .img-tip-text {
        bottom: auto;
        top: -10px;
      }

      .drawer-theme {
        width: 100%;

        > div {
          width: 30%;
          float: left;

          .sys-theme {
            text-align: center;

            p {
              margin: 0;
            }

            border: 1px solid #ffffff;
          }
        }

        .sys-theme {
          span {
            display: inline-block;
            width: 30px;
            height: 30px;
            background-color: #018cb7;
          }

          p {
            font-size: 12px;
            text-align: center;
          }
        }
      }

      .drawer-radio {
        float: right;
      }

      .drawer-style {
        width: 100%;

        span {
          display: inline-block;
          width: 50px;
          height: 50px;
          border: 1px solid #ffffff;
          cursor: pointer;
          transition: all .3s;
          border-radius: 6px;
          position: relative;
          -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .055), 0 0 2px rgba(0, 0, 0, .3);
          -moz-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .055), 0 0 2px rgba(0, 0, 0, .3);
          box-shadow: inset 0 1px 1px rgba(0, 0, 0, .055), 0 0 2px rgba(0, 0, 0, .3);

          img {
            display: inline-block;
            width: 100%;
          }

          i {
            position: absolute;
            font-size: 20px;
            left: 50%;
            margin-left: -10px;
            top: 50%;
            margin-top: -10px;
            display: none;
            font-weight: bold;
          }

          &.is-active {
            box-shadow: inset 0 1px 1px rgba(0, 0, 0, .055), 0 0 2px var(--border-active-color);
            border: 1px solid var(--border-active-color);

            i {
              display: block;
              color: var(--border-active-color);
            }
          }
        }
      }

      .drawer-switch {
        float: right
      }

      .color-picker {
        float: right;
        height: 26px;
        margin: -3px 8px 0 0;
      }
    }

    .upload-item {
      text-align: right;
    }
  }
</style>
<style lang="scss">
  .drawer-container {
    .drawer-radio {
      .el-radio-button__inner {
        padding: 7px 8px !important;
      }
    }
    .drawer-item.hasImage {
      height: 140px;
      align-items: start;
      .el-upload-list {
        position: absolute;
        left: 0;
        width: 100%;
        label, i {
          display: none;
        }
      }
    }
    .el-upload-list__item {
      transition: none !important;
      &.is-success:focus:not(:hover) {
        label, i {
          display: none;
        }
      }
    }
    .el-upload-list--picture {
      .el-upload-list__item-thumbnail {
        background-color: #f5f5f5;
      }
    }
  }
</style>
