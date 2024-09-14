<template>
  <el-row v-loading="loading">
    <el-card class="page-container">
      <el-row>
        <el-col style="text-align:right;margin-bottom:10px">
          <el-button type="primary" icon="el-icon-upload2" @click='exportExcel'>导出</el-button>
        </el-col>
      </el-row>
      <!--  搜索栏  开始 -->
      <div class='query-form-container'>
          <el-row class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
                      <el-col :span="8">
              <el-form-item label='收费日期' prop='time'>
                <el-date-picker
                v-model="queryModel.dateRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :default-time="['00:00:00', '23:59:59']"
                value-format="yyyy-MM-dd HH:mm:ss"
                format="yyyy-MM-dd HH:mm:ss"
              >
              </el-date-picker>
              </el-form-item>
            </el-col>
              <el-col :span="4">
                <el-form-item label='医生名称' prop='name'>
                  <el-input
                    style="width:320px;"
                    v-model="queryModel.name"
                    placeholder="请输入医生姓名"
                  ></el-input>
                </el-form-item>
              </el-col>

            </el-form>
          </el-row>
          <el-row id="conditionOperation">
            <el-col :span="24" style="text-align:right;padding-right:5px">
              <!-- <div class="bg_btn">
                <el-button type="primary" @click="onSearch"
                  >查询</el-button
                >
                <el-button @click="resetCondition()">重置</el-button>
              </div> -->
                <el-button type="primary" icon="el-icon-search" @click='onSearch' :plain='true'>搜索</el-button>
                <el-button type="info" icon="el-icon-refresh-left" @click='resetCondition' :plain='true'>重置</el-button>
            </el-col>
          </el-row>
        </div>
      <!--  搜索栏  结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span="24">
          <div @mouseleave="moveTableOutside">
            <el-table
              class="dispensing_table"
              :data="dispensingList"
              height="calc(100vh - 284px)"
              border
              ref="mutipleTable1"
              @sort-change="onSortChange"
              @header-dragend="onChangeWidth"
              :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName"
              highlight-current-row
              show-summary
              :summary-method='getTotal'
            >
            <!-- show-summary -->
            <!-- :summary-method='getTotal' -->
              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
              <el-table-column prop="name" label="医生姓名">
              </el-table-column>
              <el-table-column prop="count" label="接诊次数" >
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.count?bigNum(scope.row.count):0}}次
                  </span>
                </template> </el-table-column>
              <el-table-column prop="grossAmount" label="应收金额">
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.grossAmount?bigNum(scope.row.grossAmount):0}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="actual" label="实收金额">实际
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.actual?bigNum(scope.row.actual):0}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="favorable" label="优惠金额">
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.favorable?bigNum(scope.row.favorable):0}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="registrationCost" label="挂号费" >
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.registrationCost?bigNum(scope.row.registrationCost):0}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="westCost" label="西药费" >
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.westCost?bigNum(scope.row.westCost):0}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="chineseCost" label="中草药费" >
                <template slot-scope="scope">
                  <span  style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.chineseCost?bigNum(scope.row.chineseCost):0}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="chinesePatentCost" label="中成药费" >
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.chinesePatentCost?bigNum(scope.row.chinesePatentCost):0}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="stuffCost" label="材料费" >
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.stuffCost?bigNum(scope.row.stuffCost):0}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="examinesCost" label="检验费" >
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.examinesCost?bigNum(scope.row.examinesCost):0}}元
                  </span>
                </template> </el-table-column>
              <el-table-column prop="checkoutCost" label="检查费" >
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.checkoutCost?bigNum(scope.row.checkoutCost):0}}元
                  </span>
                </template> </el-table-column>
              <el-table-column prop="therapyCost" label="理疗费" >
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.therapyCost?bigNum(scope.row.therapyCost):0}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="cureCost" label="治疗费" >
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.cureCost?bigNum(scope.row.cureCost):0}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="otherCost" label="其他" >
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{scope.row.otherCost?bigNum(scope.row.otherCost):0}}元
                  </span>
                </template>
              </el-table-column>
              <!-- <el-table-column prop="bid" label="进价单价(元)" width="90">
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{bigNum(scope.row.bid)}}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="bidTotal" label="进价总价(元)" width="90">
                <template slot-scope="scope">
                  <span>
                    {{bigNum(scope.row.bidTotal)}}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="price" label="销售单价(元)" width="90">
                <template slot-scope="scope">
                  <span>
                    {{bigNum(scope.row.price)}}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="priceTotal" label="销售总价(元)" width="90">
                <template slot-scope="scope">
                  <span>
                    {{bigNum(scope.row.priceTotal)}}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="profit" label="利润(元)" width="90">
                <template slot-scope="scope">
                  <span>
                    {{bigNum(scope.row.profit)}}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="factory" label="生产厂家">
              </el-table-column>
              <el-table-column prop="batchNo" label="生产批号">
              </el-table-column>
              <el-table-column prop="dispensingDate" label="发药日期">
              </el-table-column> -->
            </el-table>
          </div>
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
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
import ExportExcelButton from "@/components/ExportExcelButton";
import ViewColumnsSelect from "@/views/components/ViewColumnsSelect";
import QueryForm from "@/views/components/queryForm";
import MainUI from "@/views/components/mainUI";
import OperationIcon from "@/components/OperationIcon";
import { listDictItemAll } from "@/api/sys/dictItem";
import { getList,getAmount } from "@/api/stock/dispensing";
import { BigNumber } from "bignumber.js";
import { getWorkload,getWorkLoadStat,exportExcel } from "@/api/toll/doctorDetail";
export default {
  extends: MainUI,
  data() {
    return {
      queryModel: {
        name:'',
        recipelCode:'',
        type:'',
        dateRange:[this.addCreateDate(),new Date()]
      },
      search: {
        offset: 0,
        limit: 20,
        order: "",
        columnName: "",
        params: [
          {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
          {
            logic: "AND",
            queryType: "("
          },
          {
            columnName: "create_date",
            logic: "",
            queryType: 'between',
            value: [],
          },
          {
            logic: "",
            queryType: ")"
          },
        ],
      },
      currentPage: 1,
      pageSize: 20,
      dispensingTotal: 0,
      dispensingList: [],
      oprColumnWidth: 140, // 操作列宽
      filerData: {
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > Date.now() - 8.64e6;
          },
        },
      },
      allTotal:{}, //总计
      type_List: [], // 药品类型
      queryTypes: {
        goods_name: "like",
        type: "=",
        bar_code: "like",
      },
      amountTotal:{},//合计金额
    };
  },
  updated(){
     this.$nextTick(()=>{
       this.$refs.mutipleTable1.doLayout();
     });
  },
  methods: {

    //导出
    exportExcel(){
      this.search.columnName = 'doctorDetail'
      exportExcel(this.search).then((res)=>{
           const filename = decodeURI(res.headers.split(';')[1].split('=')[1]) || '.xls'
                const blob = new Blob([res.data], {

                type: 'application/octet-stream'

              })

                let url = window.URL.createObjectURL(blob);

                let link = document.createElement('a');

                link.style.display = 'none';

                link.href = url;

                link.setAttribute('download', filename);

                document.body.appendChild(link);

                link.click()
      }).catch((error)=>{
        this.outputError(error)
      })
    },

    addCreateDate(){
       let myDate = new Date();
        let lw = new Date(myDate.getTime() - 1000 * 60 * 60 * 24 * 30); //最后一个数字30可改，30天的意思
        console.log(lw.getDate());
        let lastY = lw.getFullYear();
        let lastM = lw.getMonth() + 1;
        let lastD = lw.getDate();
        let housrs= lw.getHours();
        let minutes=lw.getMinutes()
        let seconds=lw.getSeconds()
        let startData =
          lastY +
          "-" +
          (lastM < 10 ? "0" + lastM : lastM) +
          "-" +
          (lastD < 10 ? "0" + lastD : lastD)+
          " "+housrs+":"+minutes+":"+seconds //三十天之前日期
          let returnDate=new Date(startData)
        return returnDate;

    },
    indexMethod(index){
       return (this.currentPage-1)*this.search.limit+index +1;
    },
    getTotal(param){
      let { columns, data } = param;
      let arr = []
      columns.forEach((column, index) => {
          if (index === 0) {
            arr[index] = '合计';
          }else{
            arr[index] = ''
          }
      })
      arr[2]=this.amountTotal.count+"次"
      arr[3]=new BigNumber(Number(this.amountTotal.grossAmount)).toFormat(2)+'元'
      arr[4]=new BigNumber(Number(this.amountTotal.actual)).toFormat(2)+'元'
      arr[5]=new BigNumber(Number(this.amountTotal.favorable)).toFormat(2)+'元'
      arr[6]= new BigNumber(Number(this.amountTotal.registrationCost)).toFormat(2)+'元'
      arr[7]= new BigNumber(Number(this.amountTotal.westCost)).toFormat(2)+'元'
      arr[8]= new BigNumber(Number(this.amountTotal.chineseCost)).toFormat(2)+'元'
      arr[9]= new BigNumber(Number(this.amountTotal.chinesePatentCost)).toFormat(2)+'元'
      arr[10]= new BigNumber(Number(this.amountTotal.stuffCost)).toFormat(2)+'元'
      arr[11]= new BigNumber(Number(this.amountTotal.examinesCost)).toFormat(2)+'元'
      arr[12]= new BigNumber(Number(this.amountTotal.checkoutCost)).toFormat(2)+'元'
      arr[13]= new BigNumber(Number(this.amountTotal.therapyCost)).toFormat(2)+'元'
      arr[14]= new BigNumber(Number(this.amountTotal.cureCost)).toFormat(2)+'元'
      arr[15]= new BigNumber(Number(this.amountTotal.otherCost)).toFormat(2)+'元'

      console.log(arr)
      return arr
    },
    init() {
      if (this.queryModel.dateRange && this.queryModel.dateRange.length) {
           this.queryModel.dateRange[0]=this.$moment(this.queryModel.dateRange[0]).format(
              "YYYY-MM-DD HH:mm:ss"
            )
          this.queryModel.dateRange[1]=this.$moment(this.queryModel.dateRange[1]).format(
                  "YYYY-MM-DD HH:mm:ss"
                )
        this.search.params[2].value=this.queryModel.dateRange

      }
      this.setLoad();
      getWorkload(this.search).then((res) => {
        console.log(res,'看看这个');
        // getAmount(this.search).then(response=>{
          if(res.code=="100"){
              this.dispensingList=res.data.rows
              this.dispensingTotal = res.data.total
              getWorkLoadStat(this.search).then((res)=>{
                if(res.code=="100"){
                    this.amountTotal=res.data
                   this.resetLoad();
                }
              }).catch(()=>{})

          }
        // })
      });
    },
    onSearch() {
      console.log(this.queryModel.name,'看医生名称');
        this.currentPage = 1;
        this.search = {
          columnName:"",
          limit: this.pageSize,
          offset: this.currentPage - 1,
          order: "",
          params:[
            {
              columnName: "company_id",
              queryType: "=",
              value: currentUser.company.id,
            },

          ]
        }
        if(this.queryModel.dateRange && this.queryModel.dateRange.length){
          this.search.params.push(
             {
            logic: "AND",
            queryType: "("
          },
          {
            columnName: "create_date",
            logic: "",
            queryType: 'between',
            value: [],
          },
          {
            logic: "",
            queryType: ")"
          },
          )
        }
        if(this.queryModel.name!=undefined && this.queryModel.name!=''){
          this.search.params.push(
            {
              columnName: "su.name",
              queryType: "=",
              value: this.queryModel.name,
            },

          )}

        this.init();
    },
    resetCondition(){
      this.queryModel = {
        name:'',
        recipelCode:'',
        type:'',
        dateRange:[this.addCreateDate(),new Date()]
      }
      this.currentPage = 1;
      this.search = {
        offset: 0,
        limit: this.pageSize,
        order: "",
        columnName: "",
        params: [
          {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
          {
            logic: "AND",
            queryType: "("
          },
          {
            columnName: "create_date",
            logic: "",
            queryType: 'between',
            value: [],
          },
          {
            logic: "",
            queryType: ")"
          },
        ],
      }
      this.init()
    },
    onSizeChange(val) {
      this.currentPage = 1;
      this.search.limit = val;
      this.search.offset = (this.currentPage - 1) * val;
      this.init();
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit;
      this.currentPage = val;
      this.init();
    },
    initOptions(This) {
      let type_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1004078055755374603",
          },
        ],
      };
      // 响应字段的条件操作符，替换成触发字段的操作符
      type_search.params.forEach((item) => {
        if (this.queryTypes[item.columnName]) {
          item.queryType = this.queryTypes[item.columnName];
        }
      });
      // 字段对应表上filter条件
      type_search.params.push.apply(type_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        type_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.type_List.splice(0, this.type_List.length);
      listDictItemAll(type_search).then((responseData) => {
        this.type_List = responseData.data;
      });
    },
    bigNum(num){
      if(num||num==='0'){
        return new BigNumber(num).toFormat(2)
      }else{
        return ''
      }
    }
  },
  watch: {},
  mounted() {
    this.initOptions()
    this.init();
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
    width: 100% !important;
    input{
      width: 100% !important;
      padding-right: 0;
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
      text-align: right;
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
