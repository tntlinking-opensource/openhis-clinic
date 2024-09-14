<template>
    <div>
              <i style="float: right; padding: 3px 0; position: relative; z-index: 1" class="el-icon-close" @click="closeTab"></i>

      <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
        <el-tab-pane label="材料有效期" name="stuffIndate">
          <el-table :data="stuffIndateList" :row-key="getRowKeys" @expand-change="expandSelect" :expand-row-keys="expands"  border v-loading="loading" highlight-current-row>
          <el-table-column align="center" prop="stuff.name" label="材料名称">
          </el-table-column>
            <el-table-column align="center" prop="endDate" label="有效期">
                <template slot-scope="scope">
                    {{getTime(scope.row.endDate)}}
                </template>
            </el-table-column>
            <el-table-column
                label="操作"
                align="center"
                width="100">
                <template slot-scope="scope">
                    <el-button @click="checkStuff(scope.row)" type="text" size="small">查看</el-button>
                    <!-- <el-button type="text" size="small">编辑</el-button> -->
                </template>
            </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="材料库存" name="stuffInventory">
        <el-table :data="inventoryList"	:row-key="getRowKeys" @expand-change="expandSelect" :expand-row-keys="expands" border v-loading="loading" highlight-current-row>

        <el-table-column align="center" prop="name" label="材料名称">
          </el-table-column>
            <el-table-column align="center" prop="inventory" label="库存">
              <template slot-scope="scope">
                  <span style="color:orange">
                  {{Math.floor(scope.row.stock.surplusStock/scope.row.packNumber) >= 0 ? Math.floor(scope.row.stock.surplusStock/scope.row.packNumber)+(scope.row.packUnit.name)+((scope.row.stock.surplusStock%scope.row.packNumber > 0) ? (scope.row.stock.surplusStock%scope.row.packNumber) + scope.row.minUnit.name:""):scope.row.stock.surplusStock+scope.row.minUnit.name}}
                  </span>
              </template>
            </el-table-column>
            <el-table-column
                label="操作"
                align="center"
                width="100">
                <template slot-scope="scope">
                    <el-button @click="checkStuffInventory(scope.row)" type="text" size="small">查看</el-button>
                    <!-- <el-button type="text" size="small">编辑</el-button> -->
                </template>
            </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>


      <el-col :span='24'>
        <el-pagination
          @size-change='onSizeChange'
          @current-change='onCurrentChange'
          :current-page.sync='currentPage'
          :page-sizes='[5, 10, 20, 40, total]'
          :page-size='5'
          :pager-count="3"
          background
          layout="total,sizes,prev, pager, next"
          :total='total'>
        </el-pagination>
      </el-col>
    </div>
</template>
<script>
import {getStuffIndateWarning,getStuffInventoryWarning} from "@/api/stock/warning";

import MainUI from "@/views/components/mainUI";
export default {
    extends: MainUI,
    data(){
        return{
            expands: [], // 通过该属性设置Table目前的展开行，需要设置row-key属性才能使用，该属性为展开行的keys数组
           stuffIndateList:[],
           inventoryList:[],
           activeName: 'stuffIndate',
           total:0,
           currentPage:1,
           search: {
                offset: 0,
                limit: 5,
                order: "",
                columnName: "",
                params: [
                    {
                        columnName: "company_id",
                        queryType: "=",
                        value: currentUser.company.id,
                    },
                    {
                        columnName:"cancellation",
                        queryType:"<>",
                        value:"1"
                    },
                ],
            },
            inventorySearch:{
                offset: 0,
                limit: 5,
                order: "",
                columnName: "",
                params: [
                {
                    columnName: "company_id",
                    queryType: "=",
                    value: currentUser.company.id,
                },
                ],
            }
        }
    },
    mounted(){
        this.activeName = 'stuffIndate';
        this.init();
        this.inventoryInit();
    },
    methods:{
      getTime(time){
        let date = new Date(time);
        let year = date.getFullYear();
        let month = date.getMonth() + 1;
        month = month < 10 ? '0' + month : month;
        let day = date.getDate()
        day = day < 10 ? '0' + day : day;
        return year + '-' + month + '-' + day;
      },

        checkStuff(){
            this.$router.push({
                path:"warning",
                query:{
                    activeName:'1',
                    stuffType:0,
                }
            });
            this.$parent.doToggle();
        },
        checkStuffInventory(){
            this.$router.push({
                path:"warning",
                query:{
                    activeName:'1',
                    stuffType:1,
                }
            });
            this.$parent.doToggle();
        },
        init(){
            this.setLoad()
            getStuffIndateWarning(this.search).then((res)=>{
                if(res.code==100){
                    if(res.data.rows){
                         this.stuffIndateList = res.data.rows;
                    }else{
                         this.stuffIndateList = [];
                    }
                    this.total = res.data.total;
                    this.$store.dispatch("notices/SetStuffIndateWarningTotal",res.data.total);
                    this.resetLoad();
                }
            }).catch((error)=>{
                this.outputError(error)
            })
        },

        inventoryInit(){
            getStuffInventoryWarning(this.inventorySearch).then((res)=>{
                if(res.code==100){
                    if(res.data.rows){
                        this.inventoryList = res.data.rows
                    }else{
                        this.inventoryList = []
                    }
                    if(this.activeName=='stuffInventory'){
                        this.total = res.data.total
                    }
                    this.$store.dispatch('notices/SetStuffInventoryWarningTotal',res.data.total)
                    this.resetLoad();
                }
            }).catch((error)=>{
                this.outputError(error)
            })
        },

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
        handleClick(tab, event){  //tab页切换
            if(tab.name!=='all'){
                if(tab.name === 'stuffIndate'){
                    this.currentPage =1
                    this.search={
                    offset: 0,
                    limit: 5,
                    order: "",
                    columnName: "",
                    params: [
                    {
                        columnName: "company_id",
                        queryType: "=",
                        value: currentUser.company.id,
                    },
                    {
                        columnName:"cancellation",
                        queryType:"<>",
                        value:"1"
                    },
                    ],
                }
                this.init();
                }else{
                   this.currentPage=1
                   this.inventorySearch={
                        offset: 0,
                        limit: 5,
                        order: "",
                        columnName: "",
                        params: [
                        {
                            columnName: "company_id",
                            queryType: "=",
                            value: currentUser.company.id,
                        },
                        ],
                    }
                  this.inventoryInit();
                }
            }
        },
         onSizeChange(val) {
        this.currentPage = 1
        if(this.activeName === 'stuffIndate'){
            this.search.limit = val;
            this.search.offset = (this.currentPage - 1) * val;
            this.init()
        }else{
            this.inventorySearch.limit = val;
            this.inventorySearch.offset = (this.currentPage - 1) * val;
            this.inventoryInit();
        }
      },
      onCurrentChange(val) {
        this.currentPage = val;
        if(this.activeName ==='stuffIndate'){
            this.search.offset = (val - 1) * this.search.limit;
            this.init()
        }else{
            this.inventorySearch.offset = (val-1) * this.inventorySearch.limit;
            this.inventoryInit();
        }
      },
    },
}
</script>
