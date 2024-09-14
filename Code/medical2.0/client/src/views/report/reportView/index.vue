<template>
  <el-row v-loading='loading'>
    <el-container class="report-container">
      <div class="page-left-container" v-show="!isPanelCollapse">
        <el-aside>
          <PanelList :setPageLoad="setLoad" :resetPageLoad="resetLoad"></PanelList>
        </el-aside>
      </div>
      <div class="page-right-container" style="padding: 0; position: relative">
        <div class="row-caret" @click="onCaretClick">
          <i :class="isPanelCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
        </div>
        <el-main style="height: 100%">
          <Preview></Preview>
        </el-main>
      </div>
    </el-container>
  </el-row>
</template>

<script>
  import MainUI from '@/views/components/mainUI'
  import PanelList from '@/views/report/panel/list/PanelList'
  import Preview from '@/views/report/components/Preview'
  export default {
    extends: MainUI,
    components: {
      PanelList,
      Preview
    },
    data() {
      return {
        isPanelCollapse: false,
        // componentData: [],
        // panelInfo: {
        //   id: null,
        //   name: '',
        //   preStyle: null
        // }
      }
    },
    methods: {
      onCaretClick() {
        this.isPanelCollapse = !this.isPanelCollapse
      },
      // clear() {
      //   this.$store.dispatch('panel/setPanelInfo', {
      //     id: null,
      //     name: '',
      //     preStyle: null
      //   })
      //   this.$store.dispatch('panel/setMainActiveName', 'PanelMain')
      // }
    },
    watch: {
    },
    mounted() {
      // this.clear()
    }
  }
</script>
<style lang="scss">
  .report-container {
    height: calc(100vh - 114px);
    .page-left-container {
      transition: all .3s;
      overflow-y: auto;
      .el-table .cell {
        white-space: nowrap;
        overflow:hidden;
        text-overflow: ellipsis;
      }
    }
    .row-caret {
      position: absolute;
      height: 40px;
      width: 40px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      box-sizing: border-box;
      color: #555;
      border-right: 1px solid #f5f5f5;
      border-bottom: 1px solid #f5f5f5;
      z-index: 1000;
      i {
        transition: all .3s;
      }
      &:hover i {
        color: #999;
      }
    }
  }
</style>
