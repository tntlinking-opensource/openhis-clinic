<template>
  <div class="edit-container">
    <!-- 添加任务栏 -->
    <div class="addPanel-position-box" v-if="editPersonStyle">
      <div class="addPanel-container">
        <div class="panel-text-box">
          <i class="iconfont icon-layout"></i>
          <span>面板 / </span>
          <span>{{ initLayout.length }}</span>
        </div>
        <el-divider direction="vertical"></el-divider>
        <div class="add-panel-box" @click="addPanelDialog = true">
          <i class="el-icon-circle-plus" :style="{color: settings.theme}"></i>
          <span>添加面板</span>
        </div>
        <div class="saveBtn-box">
          <el-button @click="exitEdit">退出编辑</el-button>
          <el-button type="primary" class="save-btn" @click="saveLayoutData">保存</el-button>
        </div>
      </div>
    </div>
    <!-- 面板对话框 -->
    <el-dialog title="选择面板" :visible.sync="addPanelDialog" width="40%">
      <span>
        <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox>
        <div style="margin: 15px 0;"></div>
        <el-checkbox-group v-model="checkPanelItem" @change="handleCheckedCitiesChange">
          <el-checkbox v-for="(item, index) in panels"
            :label="item.id"
            :key="item.id">
            {{ item.name }}</el-checkbox>
        </el-checkbox-group>
      </span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" plain @click="getPanelItem">确 定</el-button>
        <el-button @click="resetPanel">取 消</el-button>
      </span>
    </el-dialog>
    <suspend-btn @handleConfig="editDesktopEvent" @handleToTop="toDesktopTop"></suspend-btn>
    <!-- 自定义布局项 -->
    <grid-layout
      :layout.sync="layout"
      :col-num="100"
      :row-height="30"
      :is-draggable="draggable"
      :is-resizable="resizable"
      :is-mirrored="mirrored"
      :vertical-compact="verticalCompact"
      :margin="margin"
      :use-css-transforms="transforms"
    >
      <grid-item
        :class="isTransform? '' : 'noTransform'"
        v-for="(item, index) in layout"
        :x="item.x"
        :y="item.y"
        :w="item.w"
        :h="item.h"
        :i="item.i"
        :key="index"
        :static="item.static"
        @moved="movedEvent"
        @resized="reSizedEvent"
      >

        <component
          :panelSetIcon="panelSetIcon"
          @parentPageLoad="handleLoad"
          @deletePanelItemEvent="deletePanelItem"
          :key="item.i" :is="item.is" :id="item.i">
        </component>
      </grid-item>
    </grid-layout>

    <!-- 由于该gridLayout插件布局原因，流程弹出需放在外部 -->
    <component ref='wfForm' :is='wfForm' v-on:save-finished='handleSaveFinished' v-on:after-load='handleAfterLoad'></component>
    <wf-viewer ref='viewForm' :permission='permission'></wf-viewer>
    <report-view ref='reportView'></report-view>
  </div>
</template>

<script>
  import VueGridLayout from 'vue-grid-layout'
  import oftenApply from '../components/oftenApply'
  import Person from '../components/Person'
  import suspendBtn from '../components/suspendBtn'


  import {listUserGridAll,saveUserGrid} from "@/api/grid/userGrid"


  let newPermission = [];                    //权限 所拥有的模块
  const permissionId = [];

  import WfBaseUI from '@/views/wf/common/wfBaseUI'
  import WfViewer from '@/views/wf/designer/viewerForm'
  import reportView from '../components/report/reportView'
  import { getLocalRouters, getLocalCurrentUser } from '@/utils/auth'
  export default {
    inject:['reload'],
    extends: WfBaseUI,
    data () {
      return {
        comps:[], //组件list
        wfForm: null,
        isTransform: true,
        currentTask: null,
        currentWfData: null,
        currentFormKey: null,
        permission: {
          view: false,
          start: false,
          export: false
        },
        wfType: '',

        margin:[5,5],   //网格之间的边距 两个数字组成的数组 第一个数字为水品距离 第二个为垂直距离
        transforms: true, // 标识是否使用CSS属性      margin定义栅格中的元素边距。
        verticalCompact: true, // 标识布局是否垂直压缩。
        resizable: false, // 标识栅格中的元素是否可调整大小。
        draggable: false, // 标识栅格中的元素是否可拖拽。
        mirrored: false, // 是否镜像反转

        defaultCheck :[],                     //默认选中的模块

        editPersonStyle:false,        //是否显示编辑按钮
        panelSetIcon:true,            //是否显示删除图标  false显示  true隐藏
        initLayout:[],           //全部的布局模块

        layout:[],                    //页面加载的布局模块
        DBLayout:[],                  //保存布局信息

        checkPanelItem: [],       //选中的菜单
        panels: [],               //拥有的菜单权限
        addPanelDialog: false,    //弹框
        checkAll: false,
        isIndeterminate: true,     //设置 indeterminate -选中状态，只负责样式控制

        id:null,                  //自定义信息id
        search: {
          params: [],
          offset: 0,
          limit: 10,
          columnName: '',       // 排序字段名
          order: ''             // 排序
        },
      }
    },
    created () {
      if(newPermission.length !== 0){
        newPermission = [];
      }
      if(this.$route.meta.routerId){        // 当前菜单id
        const  routers = getLocalRouters();   //所有路由信息
        for(const menu of routers){
          if(menu.parent.id === this.$route.meta.routerId ){ // 路由父id 和当前页面id 一致时 把路由当作权限加载进权限数组
            if(menu.code.indexOf("/") > -1){
              let url = menu.code;
              if(url.substr(0,1) === '/') {
                let vueComp= () => import('@/views' + url);
                this.initLayout.push({is:vueComp,i:menu.id,"x":0,"y":0,"w":30,"h":9,"name":menu.name,"moved":false});
              }else{
                let vueComp = () => import('@/views/' + url);
                this.initLayout.push({is:vueComp,i:menu.id,"x":0,"y":0,"w":30,"h":9,"name":menu.name,"moved":false});
              }
            }
            newPermission.push({id:menu.id,name:menu.name});
            permissionId.push(menu.id);
          }
        }
      }
    },
    mounted(){
      const routerId = this.$route.meta.routerId;
      const userId = currentUser.id;
      this.search.params.push({
        columnName: 'user_id',
        queryType: '=',
        value: userId
      });
      this.search.params.push({
        columnName: 'router_id',
        queryType: '=',
        value: routerId
      });
      listUserGridAll(this.search).then(result =>{             //查询用户自定义布局
        if(result.code === "100"){
          if(result.data.length === 0){   // 当前用户未自定义  把拥有的模块全部展示
            let Check = [];                                     //选中的模块
            for (const attr in newPermission){
              this.panels.push(newPermission[attr]);            //  多选模块信息
              Check.push(newPermission[attr].id);
            }
            this.defaultCheck = Check;
            this.initPanel();
          }else{
              let data = result.data[0];                        //  用户自定义数据
              this.id = data.id;                                //  自定义数据id
              this.defaultCheck = data.defaultCheck.split(","); //  默认选中的节点
              let gridInfo = JSON.parse(data.gridInfo);
              for(let menu of this.initLayout){
                for(let info of gridInfo){
                  if(info.i === menu.i){
                    info.is = menu.is;
                    menu.x = info.x;
                    menu.y = info.y;
                    menu.w = info.w;
                    menu.h = info.h;
                  }
                }
              }
              this.layout = gridInfo;          //  自定义布局信息
              for (const attr in newPermission){
                this.panels.push(newPermission[attr]);          //  拥有权限的多选模块信息
              }
              this.initPanel();
            }
          }
      });
    },
    computed: {
      ...Vuex.mapGetters(['settings']),
      styleObject() {
        return {
          '--active-color': this.settings.theme
        }
      }
    },
    methods: {
      handleSaveFinished() {
        this.$refs.workbench.getWfList()
      },
      handleAfterLoad() {
        switch (this.wfType) {
          case 'MY_TASK_APPROVE':
            this.$refs.wfForm.$emit('openApproveDialog', this.currentWfData)
            break;
          case 'MY_PROCESS_START':
            this.$refs.wfForm.$emit('openCreateDialog', this.currentWfData, this.currentFormKey)
            break;
          case 'MY_APPLIED_VIEW':
            this.$refs.wfForm.$emit('openViewDialog', this.currentWfData)
            break;
          case 'MY_APPROVED_VIEW':
            this.$refs.wfForm.$emit('openViewDialog', this.currentWfData)
            break;
        }
      },
      handleLoad(params) {
        switch (params[0]) {
          case 'MY_TASK_APPROVE':
            this.currentWfData = params[1]
            this.wfForm = params[2]
            this.wfType = 'MY_TASK_APPROVE'
            break;
          case 'MY_PROCESS_VIEW':
            this.$refs.viewForm.$emit('openViewer', params[1])
            this.wfType = 'MY_PROCESS_VIEW'
            break;
          case 'MY_PROCESS_START':
            this.currentWfData = params[1]
            this.currentFormKey = params[2]
            this.wfForm = params[3]
            this.wfType = 'MY_PROCESS_START'
            break;
          case 'MY_APPLIED_VIEW':
            this.currentWfData = params[1]
            this.wfForm = params[2]
            this.wfType = 'MY_APPLIED_VIEW'
            break;
          case 'MY_APPROVED_VIEW':
            this.currentWfData = params[1]
            this.wfForm = params[2]
            this.wfType = 'MY_APPLIED_VIEW'
            break;
          case 'REPORT':
            this.$refs.reportView.$emit('openReportView', params[1])
            break;
        }

      },
      // 初始化面板项
      initPanel () {
        let arr = [];  // 需要加载的模块数组
        this.checkPanelItem =  this.defaultCheck;  //this.defaultCheck;    // 选中的模块
        if(this.layout.length > 0){
            for(const item of this.layout){        // 当模块存在于 选中的数组中时  放入需要加载的数组模块中
                this.defaultCheck.indexOf(item.i) > -1 && permissionId.indexOf(item.i) > -1 ? arr.push(item): null;
            }
          }else{
            for(const item of this.initLayout){        // 当模块存在于 选中的数组中时  放入需要加载的数组模块中
              this.defaultCheck.indexOf(item.i) > -1 && permissionId.indexOf(item.i) > -1 ? arr.push(item): null;
            }
          }

          this.layout = arr;          //展示布局模块
          this.DBLayout = this.deepCopy(arr);
          this.checkAll = this.panels.length === this.defaultCheck.length;  //是否展示全选
          this.isIndeterminate = !this.checkAll;
      },
      movedEvent(i, newX, newY) {  //移动结束触发 注意:只有当位置相对上一次发生改变才会触发
        console.log(i, newX, newY);
      },
      reSizedEvent(i, newH, newW, newHPx, newWPx) { // 放大缩小结束 触发事件 注意:必须当大小相对上一次发生改变结束时才会触发
        console.log(i, newH, newW, newHPx, newWPx)
      },

      // 全选面板选项
      handleCheckAllChange (val) {
        // if(val){
        //   const arr = [];
        //   for(const obj of this.panels){
        //       arr.push(obj.id);
        //   }
        //   this.checkPanelItem = arr;
        // }else{
        //   this.checkPanelItem = ['552789263395020915'];   //个人信息模块
        // }
        this.checkPanelItem = val ? this.panels.map(item => item.id) : [];
        this.isIndeterminate = false;

      },
      // 多选面板选项
      handleCheckedCitiesChange(value) {
        // 目前首页默认拥有模块已改，不一致，复选节点全选有问题，待后续改
        const checkedCount = value.length;
        this.checkAll = checkedCount === this.panels.length;
        this.isIndeterminate = checkedCount > 0 && checkedCount < this.panels.length
      },
      // 添加面板
      getPanelItem () {
        const newLayout = [];
        const layout = this.initLayout;
        if (layout) {
          for (const attr in layout) {
            this.checkPanelItem.filter(item => {
              if (item === layout[attr].i && permissionId.indexOf(item) > -1) {
                newLayout.push(layout[attr]);
              }
            })
          }
          this.layout = newLayout
        }
        this.addPanelDialog = false
      },
      // 恢复默认设置面板
      resetPanel() {
        this.checkPanelItem = this.defaultCheck;
        this.addPanelDialog = false
      },
      // 保存最新面板布局参数
      saveLayoutData () {
        const defaultCheck = [];
        this.checkPanelItem.forEach((item,i)=>{ //保存前和权限做一次比对 筛选掉没有权限的保存模板
           permissionId.indexOf(item) > -1 ? defaultCheck.push(item) : null;
        });
        const userGird = {
          id: this.id ,
          userId: currentUser.id,
          gridInfo: JSON.stringify(this.layout),
          defaultCheck: defaultCheck.toString(),
          routerId: this.$route.meta.routerId,
        };
        saveUserGrid(userGird).then(result =>{
          if (result.code === '100') {
            this.$message.success({
              message: '保存成功',
              type: 'success',
              duration: 500
            });
            this.DBLayout = this.deepCopy(this.layout);
            this.defaultCheck = this.checkPanelItem;
          }
          this.editPersonStyle = false;
          this.panelSetIcon = true;
          this.draggable = false;
          this.resizable = false;
        });
      },
      // 根据面板id删除面板
      deletePanelItem (panelId) {
        const deleteName = [];
        const layout = Array.from(this.layout);
        for (const attr in layout) {
          if (layout[attr].i === panelId) {
            deleteName.push(layout[attr].i);
            delete layout[attr]
          }
        }
        this.layout = layout.filter(item => {
          if (item !== undefined) {
            return item
          }
        });
        this.checkPanelItem = this.checkPanelItem
          .concat(deleteName)
          .filter((item, index, Arr) => {
            return Arr.indexOf(item) === Arr.lastIndexOf(item)
          })
      },
      //开始编辑
      editDesktopEvent(){
        this.editPersonStyle = true;  //显示退出编辑按钮
        this.panelSetIcon = false;    //显示关闭图标
        this.draggable = true;        //是否拖动布局
        this.resizable = true;        //是否可调整大小
      },
      // 退出编辑
      exitEdit(){
        this.editPersonStyle = false;
        this.panelSetIcon = true;
        this.draggable = false;
        this.resizable = false;
  /*      this.checkPanelItem.filter(v => {         // 选中但是没有保存 筛选去除掉 布局
          if(!this.defaultCheck.includes(v)){       // 当前布局模块id 取消选中 恢复默认 !false 删除布局模块
            this.deletePanelItem(v);
          }
        })*/
        //this.reload();
        this.restore();
      },
      restore(){          //还原上次保存的数据
        this.checkPanelItem = this.defaultCheck;
        this.layout = this.deepCopy(this.DBLayout);
      },
      toDesktopTop() {
        this.$parent.$el.scrollTop = 0
      },
      deepCopy(source){
        if (typeof source != "object") {
          return source;
        }
        if (source == null) {
          return source;
        }
        const newObj = source.constructor === Array ? [] : {};  //开辟一块新的内存空间
        for (let i in source) {
          newObj[i] = this.deepCopy(source[i]);
        }
      return newObj;
    }
    },
    components: {
      document,
      GridLayout: VueGridLayout.GridLayout,
      GridItem: VueGridLayout.GridItem,
      oftenApply,
      Person,
      suspendBtn,
      WfViewer,
      reportView
    }
  }
</script>

<style lang="scss">
  .noTransform {
    transform: none!important;
  }
  .edit-container {
    background-color: #ebeef5;
    background-image: radial-gradient(#ccc 4%, transparent 0);
    background-size: 45px 45px;
  }
  /* 头部添加面板区域 */
  .addPanel-position-box {
    position: sticky;
    top: 0px;
    z-index: 100;
  }
  .addPanel-container {
    height: 56px;
    display: flex;
    align-items: center;
    font-size: 14px;
    position: relative;
    background: #ebeef5;
  }
  .el-icon-circle-plus {
    margin-right: 10px;
    cursor: pointer;
  }
  .saveBtn-box {
    height: 100%;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    position: absolute;
    right: 10px;
  }
  .icon-layout {
    margin-right: 10px;
  }
  .panel-text-box {
    padding: 5px 10px;
  }
  .add-panel-box {
    padding: 5px 10px;
    cursor: pointer;
  }
 .el-icon-close {
    font-size: 1.2em;
    cursor: pointer;
  }
  .add-panel-box:hover {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 2px;
  }
  /* 自定义布局项 */
  .vue-grid-layout {
    width: 100%;
  }
  /* 多选框 */
  .el-checkbox-group {
    display: flex;
    flex-direction: column;
  }
  .el-checkbox {
    margin-bottom: 10px;
  }
</style>
