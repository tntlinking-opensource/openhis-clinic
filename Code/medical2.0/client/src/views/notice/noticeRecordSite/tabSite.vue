<template>
  <div>
      <i style="float: right; padding: 3px 0; position: relative; z-index: 1" class="el-icon-close" @click="closeTab"></i>

      <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
        <el-tab-pane label="未读" name="unread">
          <el-table :data="noticeRecordSiteList" :row-key="getRowKeys" @expand-change="expandSelect" :expand-row-keys="expands"  border v-loading="loading" highlight-current-row>
          <el-table-column type="expand">
            <template slot-scope="props">
              <span id="unread">
                {{onOpen(props.row,props.$index)}}
              </span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="title" label="标题">
          </el-table-column>
            <el-table-column align="center" prop="createDate" label="接收时间"/>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已读" name="read">
        <el-table :data="noticeRecordSiteList"	:row-key="getRowKeys" @expand-change="expandSelect" :expand-row-keys="expands" border v-loading="loading" highlight-current-row>
    <!--      <el-table-column align="center" prop="readStatus" label="状态">
            <template slot-scope="scope">
              {{scope.row.readStatus === "0"? "未读":"已读"}}
            </template>
          </el-table-column>-->
          <el-table-column type="expand">
            <template slot-scope="props">
                <span id="read">
                  {{onOpen(props.row,props.$index)}}
                </span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="title" label="标题">
        <!--    <template slot-scope="scope">
              <el-link type="primary" underline >{{scope.row.noticeRecord.title}}</el-link>
            </template>-->
          </el-table-column>
  <!--
          <el-table-column align="center" :show-overflow-tooltip="true"   prop="noticeRecord.siteContent" label="内容"/>
  -->
          <el-table-column align="center" prop="createDate" label="接收时间"/>
        </el-table>
      </el-tab-pane>
<!--      <el-tab-pane label="全部标记已读" name="all">
      </el-tab-pane>-->
    </el-tabs>


      <el-col :span='24'>
        <el-pagination
          @size-change='onSizeChange'
          @current-change='onCurrentChange'
          :current-page.sync='currentPage'
          :page-sizes='[5, 10, 20, 40, noticeRecordSiteTotal]'
          :page-size='5'
          background
          layout="total,sizes,prev, pager, next"
          :total='noticeRecordSiteTotal'>
        </el-pagination>
      </el-col>

  <!--    <el-dialog title="通告" ref="dialog" id="dialog"  v-if="contentVisible" :visible.sync="contentVisible" :close-on-click-modal="false"	width="30%" :modal-append-to-body="false"
        :before-close="handleClose">
        <span id="msg" ref="msg"></span>
      </el-dialog>-->
  </div>
</template>
<script>
  import { listNoticeReceiveSitePage, saveNoticeReceiveSite } from '@/api/noticesend/noticeReceiveSite'

  import MainUI from '@/views/components/mainUI'
  export default {
    extends: MainUI,
    name: "tabSite",
    data() {
      return {
        expands: [], // 通过该属性设置Table目前的展开行，需要设置row-key属性才能使用，该属性为展开行的keys数组
        contentVisible:false, //消息显示
        loading:false,
        activeName: 'unread',
        readStatus:"0",
        unreadList:[],        //未读的消息id
        noticeRecordSiteList:[],
        noticeRecordSiteTotal:0,
        currentPage: 1,
        search: {
          params: [{'columnName':'receive_by', 'queryType': '=', 'value': currentUser.id}],
          offset: 0,
          limit: 5,
          orderby: ''
        },
      };
    },
    created(){
    },
    mounted(){
      this.activeName = "unread";
      // this.onSearch();
    },
    methods: {
      closeTab(){       //关闭显示框
        this.$parent.doToggle();
      },
      getRowKeys(row) { // 行数据的Key
        return row.id
      },
      // table每次只能展开一行
      expandSelect(row, expandedRows) {
        this.expands = [];
        if (expandedRows.length > 0) {
          row ? this.expands.push(row.id) : ''
        }
      },
      //关闭页面
      handleClose(){
        this.contentVisible = false;
      },
      onOpen(row,index){    //点击链接跳转
        this.$nextTick(()=> {
          let msg = document.getElementById(this.activeName);         //内容主体
          msg.innerHTML = row.siteContent;
          if(row.readStatus === "0"){
             saveNoticeReceiveSite({id:row.id,readStatus:'1'}).then(result =>{ //查询赋值并修改 状态为已读
                if(result.code === "100"){
                  row.readStatus = "1";
                }
            })
          }
        });
 /*       const obj = row.noticeRecord;
        this.content = obj.siteContent;
        this.$nextTick(()=>{
          let msg = document.getElementById(this.activeName);         //内容主体
          //  (.*?)  (\S*) 两种拿到中间值 str.match(/<a>(.*?)<\/a>/i)
          if(this.content.match(/<a>(\S*)<\/a>/)){          //判断有无a标签跳转
            const txt = this.content.match(/<a>(\S*)<\/a>/)[1];
            msg.innerHTML = this.content.replace("<a>"+txt+"</a>",'<a class="el-link el-link--primary is-underline" onclick="skip()">'+ txt +'</a>');
          }else{
            msg.innerHTML = this.content;
          }
        });
      */
      },
/*      beforeName(newName,oldName){
          if(newName === "all"){
            this.$confirm('全部消息已读, 是否继续?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              if(this.unreadList){
                bulkUpdateNoticeRecordSite(this.unreadList).then(result =>{
                      if(result.code ==="100"){
                        this.$store.dispatch('notices/SetNoticeTotal',0);
                        this.unreadList = [];
                        this.$message.success("全部已读");
                      }
                })
              }
            }).catch(() => {

            });
            return false;
          }
      },*/
      handleClick(tab, event) {
        if(tab.name !== "all"){
          tab.name === "unread" ? this.readStatus = "0":this.readStatus = "1";
          this.currentPage = 1;
          this.search.offset = 0;
          // this.onSearch();
        }
      },
      onSearch(){
        console.log("执行看");
        this.setLoad();
        this.search.params = [{'columnName':'receive_by', 'queryType': '=', 'value': currentUser.id}];
        this.search.params.push({'columnName':'read_status', 'queryType': '=', 'value': this.readStatus});
        listNoticeReceiveSitePage(this.search).then(responseData => {
          if(responseData.code == 100) {
            this.noticeRecordSiteTotal = responseData.data.total;

            this.noticeRecordSiteList = responseData.data.rows === null ? []:responseData.data.rows;

            if(this.readStatus === "0" && this.noticeRecordSiteList){
            /*  this.unreadList = [];
              this.noticeRecordSiteList.some((item) =>{  //存储未读消息id
                this.unreadList.push({id:item.id,readStatus:"1"})
              });*/
              this.$store.dispatch('notices/SetNoticeTotal',this.noticeRecordSiteTotal);
            }
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      },
      onSizeChange(val) {
        this.currentPage = 1
        this.search.limit = val;
        this.search.offset = (this.currentPage - 1) * val;
        this.onSearch()
      },
      onCurrentChange(val) {
        this.search.offset = (val - 1) * this.search.limit;
        this.currentPage = val;
        this.onSearch()
      },
    }
  };
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
  .el-icon-close {
    font-size: 1.2em;
    cursor: pointer;
  }
</style>

