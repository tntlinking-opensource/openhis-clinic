<template>
<el-row v-loading="loading">
    <el-card class="page-container">
        <!-- 药品和材料预警 -->
         <div style="margin-top:-20px;margin-left:-20px">
            <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
                <el-tab-pane label="药品预警" name="0">
                </el-tab-pane>
                <el-tab-pane label="材料预警" name="1">
                </el-tab-pane>
            </el-tabs>
        </div>

         <!-- 药品预警 -->
     <div v-if="activeName=='0'">
         <drug-warning :drugActiveName="drugType" @changeCurrentPage="changeCurrentPage" @changeTotal="changeTotal" :getCurrentPage="currentPage" :getSizeChange="pageSize"></drug-warning>
     </div>

     <!-- 材料预警 -->
     <div v-else>
         <stuff-warning :stuffActiveName="stuffType" @changeCurrentPage="changeCurrentPage" @changeTotal="changeTotal" :getCurrentPage="currentPage" :getSizeChange="pageSize"></stuff-warning>
     </div>


      <!-- 分页栏     开始 -->
      <el-row>
        <el-col :span="24">
          <el-pagination
            background
            @size-change="onSizeChange"
            @current-change="onCurrentChange"
            :current-page.sync="currentPage"
            :page-sizes="[20, 50, 100, dispensingTotal]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="dispensingTotal"
          >
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->
    </el-card>

</el-row>
</template>

<script>
import { validatenull } from "@/utils/validate";
import MainUI from "@/views/components/mainUI";
import drugWarning from "./components/drugWarning";
import stuffWarning from "./components/stuffWarning"
import {listSupplierAll} from "@/api/stock/supplier"
import { listDictItemAll } from "@/api/sys/dictItem";
import { BigNumber } from "bignumber.js";
import { getDrug,getDrugSalesStat,getBatchNumberDrug,getStuff,getStuffSalesStat,getBatchNumberStuff} from "@/api/stock/currentInventory";

export default {
  extends: MainUI,
  components:{drugWarning,stuffWarning},
  data() {
    return {
       activeName:'0',    //预警切换
       currentPage: 1,
       pageSize: 20,
       dispensingTotal: 0,
       drugType:0,
       stuffType:0,
    };
  },
  methods: {

    //修改分页
    changeCurrentPage(pagination){
        this.currentPage = pagination
    },

    //修改总数
    changeTotal(total){
        console.log(total,'fsafaf');
        this.dispensingTotal = total
    },

    handleClick(tab, event){
        this.activeName = tab.name
    },

    //分页切换
    onSizeChange(val) {
      this.currentPage = 1;
      this.pageSize = val

    //   this.search.limit = val;
    //   this.search.offset = (this.currentPage - 1) * val;
    //   if(this.activeName=='0'){
    //       this.init();
    //   }else{
    //       this.stuffInit()
    //   }
    },
    //分页切换
    onCurrentChange(val) {
    //   this.search.offset = (val - 1) * this.search.limit;

      this.currentPage = val;
    //    if(this.activeName=='0'){
    //       this.init();
    //   }else{
    //       this.stuffInit()
    //   }
    },
  },
  watch: {
     //监听activeName切换
    //  activeName:{
    //      handler(newVal,oldVal){
    //          if(newVal==)
    //      }
    //  }
  },
  mounted() {
      if(this.$route.query&&this.$route.query.type==1){
          this.drugType =1
      }else if(this.$route.query && this.$route.query.activeName=='1'&&this.$route.query.stuffType==0){
          this.activeName = '1'
      }else if(this.$route.query && this.$route.query.activeName=='1'&&this.$route.query.stuffType==1){
          this.activeName = '1'
          this.stuffType = 1
      }
  },
};
</script>
<style lang="scss" scoped>
.page-container{
  padding: 0;
}
.query-form-container{
  padding: 0px 0px 10px 0px;
}
.typeClass{
  /deep/ .el-input{
    width: 90% !important;
    input{
      width: 90% !important;
      padding-right: 0px;
    }
  }
}
.el-col{
  /deep/ .el-range-separator{
    width: 10%;
  }
}
.cell{
  span{
    display: inline-block;
    width: 100%;
    text-align: right;
  }
}
/deep/ .el-table__footer-wrapper{
  td:not(:nth-of-type(1)){
    .cell{
      display: inline-block;
      width: 100%;
      text-align: left;
    }
  }

}
.el-table::before{
  height: 0;
}
/deep/ .el-table colgroup col[name='gutter']{
  width:5px !important
}
/deep/ .el-table__body{
  width:100% !important
}
</style>
