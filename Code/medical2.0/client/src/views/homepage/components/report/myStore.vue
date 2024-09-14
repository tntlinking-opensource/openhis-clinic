<template>
  <div>
    <el-row v-if="collectList.length > 0">
      <el-col :span="24">
        <ul class="data-list">
          <li v-for="(item, index) in collectList" :key="index" @click="onStoreClick(item)">
            <span class="title" style="cursor: pointer">
              <label for="" class="title-icon"><i class="el-icon-data-line" style="color: #fff"></i></label> {{item.name}}
            </span>
            <i class="el-icon-delete " @click.stop="remove(item)" />
          </li>
        </ul>
      </el-col>
    </el-row>
    <!--<el-row class="card-pagination" v-if="collectList.length > 0">
      <el-col :span='24'>
        <el-pagination
          background
          @size-change='onSizeChange'
          @current-change='onCurrentChange'
          :current-page.sync='currentPage'
          :page-size='5'
          layout='prev, pager, next'
          :total='dataTotal'>
        </el-pagination>
      </el-col>
    </el-row>-->
    <div class="no-data" v-if="collectList.length === 0">暂无数据</div>
  </div>
</template>

<script>
  import { isLightOrDark } from '@/utils/common'
  import { get } from '@/api/report/panel/panel'
  import { loadTree } from '@/api/report/panel/share'
  import { deleteEnshrine, enshrineList } from '@/api/report/panel/enshrine'
  import { getPanelAllLinkageInfo } from '@/api/report/panel/linkage'
  import { queryPanelJumpInfo } from '@/api/report/panel/linkJump'
  import { uuid } from 'vue-uuid'
  // import bus from '@/views/report/utils/bus'
	export default {
		name: "MyStore",
    props: ['id', 'panelSetIcon'],
    data() {
		  return {
        collectList: [],
        currentRowIndex: null,
        search: {
          offset: 0,
          limit: 5
        },
        currentPage: 1,
        dataTotal: 0,
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
      panelInfo() {
        return this.$store.state.panel.panelInfo
      }
    },
    watch:{

    },
    mounted() {
      // bus.$on('panle_start_list_refresh', this.refreshStarts)
      this.pageInit()
    },
    methods: {
      setCardLoad(isLoad) {
        this.$emit('setCardLoad', isLoad)
      },
      onSizeChange(val) {
        this.currentPage = 1
        this.search.limit = val;
        this.search.offset = (this.currentPage - 1) * val
        this.getTaskList()
      },
      onCurrentChange(val) {
        this.search.offset = (val - 1) * this.search.limit
        this.currentPage = val
        this.getTaskList()
      },
      async pageInit() {
        this.getCollectList()
      },
      tableRowClassName({row, rowIndex}) {
        row.rowIndex = rowIndex
      },
      getCollectList() {
        this.setCardLoad(true)
        enshrineList({}).then(responseData => {
          if(responseData.success) {
            this.collectList = responseData.data
            this.setCardLoad(false)
          } else {
            this.showMessage(responseData)
            this.setCardLoad(false)
          }
        })
      },
      onStoreClick(row) {
        this.currentRowIndex = row.rowIndex
        this.$emit('parent', 'REPORT')
        get('panel/group/findOne/' + row.panelGroupId).then(response => {
          this.$store.commit('report/setComponentData', this.resetID(JSON.parse(response.data.panelData)))
          this.$store.commit('report/setCanvasStyle', JSON.parse(response.data.panelStyle))
          const data = {
            id: row.panelGroupId,
            name: row.name
          }
          this.$store.dispatch('panel/setPanelInfo', data)
          // bus.$emit('set-panel-show-type', 0)

          // 刷新联动信息
          getPanelAllLinkageInfo(row.panelGroupId).then(rsp => {
            this.$store.commit('report/setNowPanelTrackInfo', rsp.data)
          })

          // 刷新跳转信息
          queryPanelJumpInfo(row.panelGroupId).then(rsp => {
            this.$store.commit('report/setNowPanelJumpInfo', rsp.data)
          })
        })
      },
      remove(row) {
        deleteEnshrine(row.panelGroupId).then(res => {
          this.collectList.splice(row.rowIndex, 1)
          if (row.rowIndex === this.currentRowIndex) {
            this.clearPanel()
            this.pageInit()
          }
          // this.getCollectList()
          // this.panelInfo && this.panelInfo.id && row.panelGroupId === this.panelInfo.id && this.setMainNull()
        })
      },
      clearPanel() {
        this.$store.commit('report/setComponentData', [])
        this.$store.commit('report/setCanvasStyle', {})
        this.$store.dispatch('panel/setPanelInfo', {
          id: null,
          name: '',
          preStyle: null
        })
      },
      setMainNull() {
        // this.$refs.storeList.setCurrentRow(this.collectList[0])
        // this.$store.dispatch('panel/setPanelInfo', { id: null, name: '', preStyle: null })
      },
      resetID(data) {
        if (data) {
          data.forEach(item => {
            item.type !== 'custom' && (item.id = uuid.v1())
          })
        }
        return data
      },
      refreshStarts(isStar) {
        this.getCollectList()
        !isStar && this.clearPanel()
      }
    }
	}
</script>

<style scoped lang="scss">
  .el-card {
    height: 100%;
  }
  .index-module-card.el-card /deep/ .el-card__header {
    padding: 10px 16px!important;
    .card-close, .card-setting {
      float: right;
      padding: 3px 0
    }
    /deep/ .often-body {
      position: relative;
      min-height: 210px;
    }
    .el-card-title {
      font-weight: bold;
    }
  }
  .card-radio-group {
    float: right;
    /deep/ .el-radio-button--mini .el-radio-button__inner {
      padding: 5px 15px;
    }
  }
  .title-icon {
    background-color: var(--active-color)!important;
    border-radius: 100%;
    padding: 5px;
    width: 22px;
    height: 22px;
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    top: 50%;
    margin-top: -11px;
    left: 0;
    i {
      color: #fff!important;
      font-size: 12px!important;
    }
  }
  .card-pagination {
    position: absolute;
    bottom: 0;
    right: 0;
  }
  ul.data-list {
    list-style: none;
    padding: 0;
    margin: 0;
    li {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 34px;
      font-size: 12px;
      padding: 0 10px;
      &:nth-child(odd) {
        background-color: #fbfbfb;
      }
      &:hover {
        background-color: #f5f5f5;
      }
      span {
        display: inline-block;
        &:first-child {
          flex: 1;
          white-space: nowrap;
          text-overflow: ellipsis;
          overflow: hidden;
          display: inline-block;
          height: 100%;
          line-height: 34px;
          position: relative;
          padding-left: 32px;
        }
        &.text-span {
          padding: 0 20px;
          width: 200px;
          text-align: left;
        }
      }
    }
  }
  .no-data {
    position: absolute;
    width: 100%;
    font-size: 12px;
    text-align: center;
    top: 48.99%;
  }
</style>
