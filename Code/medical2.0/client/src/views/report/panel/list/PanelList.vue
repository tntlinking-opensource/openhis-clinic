<template>
  <el-col>
    <el-row>
      <el-table class="panel-list" :data='collectList'
                ref="storeList"
                row-key='id'
                :row-class-name="tableRowClassName"
                :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
                :highlight-current-row="isHighLight === 'store'"
                :cell-style="function() {return {borderBottom: 'none'} }"
                @row-click='onStoreClick'>
        <el-table-column show-overflow-tooltip label='我的收藏' prop='name'>
          <template slot-scope="scope">
            <div class="item">
              <div class="item-content">
                <svg-icon icon-class="panel" class="ds-icon-scene" />
                <span>{{scope.row.name}}</span>
              </div>
              <div class="item-close">
                <i class="el-icon-delete " @click.stop="remove(scope.row)" />
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-row>

    <el-row>
      <el-table class="panel-list" :data='reportList'
                ref="shareList"
                row-key='id'
                show-overflow-tooltip="true"
                :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
                :highlight-current-row="isHighLight === 'share'"
                :cell-style="function() {return {borderBottom: 'none'} }"
                @row-click='onShareClick'>
        <el-table-column show-overflow-tooltip label='我的报表' prop='name'>
          <template slot-scope="scope">
            <svg-icon icon-class="panel" class="ds-icon-scene" />
            <span>{{scope.row.name}}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-row>
  </el-col>
</template>

<script>
  import MainUI from '@/views/components/mainUI'
  import { get } from '@/api/report/panel/panel'
  import { loadTree } from '@/api/report/panel/share'
  import { deleteEnshrine, enshrineList } from '@/api/report/panel/enshrine'
  import { getPanelAllLinkageInfo } from '@/api/report/panel/linkage'
  import { queryPanelJumpInfo } from '@/api/report/panel/linkJump'
  import { uuid } from 'vue-uuid'

  import bus from '@/views/report/utils/bus'
  export default {
    extends: MainUI,
    name: 'PanelList',
    components: { },
    props: {
      setPageLoad: {
        type: Function,
        default: null
      },
      resetPageLoad: {
        type: Function,
        default: null
      }
    },
    data() {
      return {
        isHighLight: '',
        collectList: [],
        reportList: [],
        currentRowIndex: null
      }
    },
    computed: {
      panelInfo() {
        // return this.$store.state.panel.panelInfo
        return this.panelInfo
      }
    },
    watch: {

    },
    mounted() {
     // bus.$on('panle_start_list_refresh', this.refreshStarts)
      this.pageInit()
    },
    methods: {
      async pageInit() {
        this.getCollectList()
        this.getReportList()
      },
      tableRowClassName({row, rowIndex}) {
        row.rowIndex = rowIndex
      },
      getCollectList() {
        this.setPageLoad()
        enshrineList({}).then(responseData => {
          if(responseData.success) {
            this.collectList = responseData.data
          } else {
            this.showMessage(responseData)
          }
          this.resetPageLoad()
        })
      },
      getReportList() {
        this.setPageLoad()
        loadTree({}).then(responseData => {
          if(responseData.success) {
            let list = responseData.data
            list.forEach((item, index) => {
              this.reportList = [...this.reportList, ...item.children]
            })
          } else {
            this.showMessage(responseData)
          }
          this.resetPageLoad()
        })
      },
      onStoreClick(row, column, event) {
        this.isHighLight = 'store'
        this.currentRowIndex = row.rowIndex
        this.setPageLoad()
        get('panel/group/findOne/' + row.panelGroupId).then(response => {
          this.openStore(row, response)
        }).catch(error => {
          this.outputError(error)
          this.resetPageLoad()
        })
      },
      onShareClick(row, column, event) {
        this.isHighLight = 'share'
        this.setPageLoad()
        get('panel/group/findOne/' + row.id).then(response => {
          this.openShare(row, response)
        }).catch(error => {
          this.outputError(error)
          this.resetPageLoad()
        })
      },
      openStore(row, response) {
        if(window["vue-dataviewer"]) {
          bus.$emit('setComponentData', this.resetID(JSON.parse(response.data.panelData)))
          bus.$emit('setCanvasStyle', JSON.parse(response.data.panelStyle))
          const data = {
            id: row.panelGroupId,
            name: row.name
          }
          bus.$emit('setPanelInfo', data)
          this.resetPageLoad()

          // 刷新联动信息
          getPanelAllLinkageInfo(row.panelGroupId).then(rsp => {
            bus.$emit('setNowPanelTrackInfo', rsp.data)
          })

          // 刷新跳转信息
          queryPanelJumpInfo(row.panelGroupId).then(rsp => {
            bus.$emit('setNowPanelJumpInfo', rsp.data)
          })
        } else {
          let _this = this
          setTimeout(function() { _this.openStore(row, response) }, 50)
        }
      },
      openShare(row, response) {
        if(window["vue-dataviewer"]) {
          bus.$emit('setComponentData', this.resetID(JSON.parse(response.data.panelData)))
          bus.$emit('setCanvasStyle', JSON.parse(response.data.panelStyle))
          bus.$emit('setPanelInfo', row)
          this.resetPageLoad()

          // 刷新联动信息
          getPanelAllLinkageInfo(row.id).then(rsp => {
            bus.$emit('setNowPanelTrackInfo', rsp.data)
          })

          // 刷新跳转信息
          queryPanelJumpInfo(row.id).then(rsp => {
            bus.$emit('setNowPanelJumpInfo', rsp.data)
          })
        } else {
          let _this = this
          setTimeout(function() { _this.openShare(row, response) }, 50)
        }
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
        this.isHighLight = ''
        bus.$emit('setComponentData', [])
        bus.$emit('setCanvasStyle', [])
        bus.$emit('setPanelInfo', {
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

<style scoped>
  .panel-list {
    font-size: 12px;
  }
  .panel-list >>> .el-table__cell {
    padding: 10px 0;
  }
  .panel-list .item {
    display: flex;
    justify-content: space-between;
  }
  .panel-list .item .item-close {
    display: none;
  }
  .panel-list .item:hover .item-close {
    display: block;
  }
  .panel-list .item .item-content {
    display: flex;
    align-items: center;
  }
  .panel-list .item .item-content span {
    display: inline-block;
    overflow: hidden;
    width: 150px;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  .header-title {
    font-size: 14px;
    flex: 1;
    color: #606266;
    font-weight: bold;
    display: block;
    height: 100%;
    /*line-height: 36px;*/
  }

  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right:8px;
  }

  .custom-tree-node-list {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding:0 8px;
  }

  .dialog-css>>>.el-dialog__body {
    padding: 15px 20px;
  }
  .dialog-css >>>.el-dialog__body {
    padding: 10px 20px 20px;
  }

  .father .child {
    /*display: none;*/
    visibility: hidden;
  }
  .father:hover .child {
    /*display: inline;*/
    visibility: visible;
  }

</style>
