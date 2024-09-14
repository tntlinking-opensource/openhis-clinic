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
              <el-form-item label='发药日期' prop='time'>
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
                <el-form-item label="诊所"  style="margin-right: 3px;">
              <el-select v-model="queryModel.organizationId" placeholder="诊所">
                <el-option label="全部" value="qb"></el-option>
                <el-option v-for="item in jglist" :value="item.jgid" :key="item.jgid"  :label="item.jgmc">
                  </el-option>
              </el-select>
            </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='药品名称' prop='name'>
                <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入药品名称'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='处方单号' prop='recipelCode'>
                <el-input v-model='queryModel.recipelCode' :clearable='true' placeholder='请输入处方单号'></el-input>
              </el-form-item>
            </el-col>
              <el-col :span="4">
              <el-form-item label='药品类型' prop='type'>
                <el-select
                  v-model="queryModel.type"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择药品类型"
                  class="typeClass"
                >
                  <el-option
                    v-for="type in type_List"
                    :key="type.value"
                    :label="type.name"
                    :value="type"
                  ></el-option>
                </el-select>
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
              height="calc(100vh - 325px)"
              border
              ref="mutipleTable1"
              @sort-change="onSortChange"
              @header-dragend="onChangeWidth"
              :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName"
              highlight-current-row
              show-summary
              :summary-method='getTotal'
              id="exportTable"
            >
              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
             <el-table-column prop="company.name" label="诊所名称">
              </el-table-column>
              <el-table-column prop="recipelCode" label="处方单号">
              </el-table-column>
              <el-table-column prop="type" label="药品类型" width="70"> </el-table-column>
              <el-table-column prop="name" label="药品名称"> </el-table-column>
              <el-table-column prop="norms" label="规格" width="80"> </el-table-column>
              <el-table-column prop="number" label="数量" width="50"> </el-table-column>
              <el-table-column prop="unit" label="单位" width="50"> </el-table-column>
              <el-table-column prop="bid" label="进价单价(元)" width="90">
                <template slot-scope="scope">
                  <span style="display:inline-block;width:100%;text-align:right">
                    {{bigNum(scope.row.bid)}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="bidTotal" label="进价总价(元)" width="90">
                <template slot-scope="scope">
                  <span>
                    {{bigNum(scope.row.bidTotal)}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="price" label="销售单价(元)" width="90">
                <template slot-scope="scope">
                  <span>
                    {{bigNum(scope.row.price)}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="priceTotal" label="销售总价(元)" width="90">
                <template slot-scope="scope">
                  <span>
                    {{bigNum(scope.row.priceTotal)}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="profit" label="利润(元)" width="90">
                <template slot-scope="scope">
                  <span>
                    {{bigNum(scope.row.profit)}}元
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="factory" label="生产厂家">
              </el-table-column>
              <el-table-column prop="batchNo" label="生产批号">
              </el-table-column>
              <el-table-column prop="dispensingDate" label="发药日期">
              </el-table-column>
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
import DispensingForm from "./dispensingForm";
import ExportExcelButton from "@/components/ExportExcelButton";
import ViewColumnsSelect from "@/views/components/ViewColumnsSelect";
import QueryForm from "@/views/components/queryForm";
import MainUI from "@/views/components/mainUI";
import OperationIcon from "@/components/OperationIcon";
import { listDictItemAll } from "@/api/sys/dictItem";
import { getOrganizationList,getAmount } from "@/api/stock/dispensing";
import { BigNumber } from "bignumber.js";
 import * as XLSX from 'xlsx'
import XLSXD from 'xlsx-style'
import { getjglist} from "@/api/toll/tollInfo";
export default {
  extends: MainUI,
  components: {
    DispensingForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
  },
  data() {
    return {
      queryModel: {
        name:'',
        recipelCode:'',
        type:'',
        dateRange:[this.addCreateDate(),new Date()],
        organizationId:"",   //机构id
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
      jglist:[],  //机构集合
       YpjxcRc: {
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        orderby:"batch_no",
        jgid:null,
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
    };
  },
  updated(){
     this.$nextTick(()=>{
       this.$refs.mutipleTable1.doLayout();
     });
  },
  methods: {
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
    //  console.log(this.allTotal.bidTotalAmount?this.allTotal.bidTotalAmount:0,'fafsaf');
      let { columns, data } = param;
      let arr = []
      columns.forEach((column, index) => {
          if (index === 0) {
            arr[index] = '合计';
          }else{
            arr[index] = ''
          }
      })
      // this.allTotal.bidTotalAmount = this.allTotal.bidTotalAmount!=undefined?this.allTotal.bidTotalAmount:0
      //arr[9] = new BigNumber(Number(this.allTotal.bidTotalAmount)).toFormat(2)+"元"
      // arr[4] = this.allTotal.numberAmount
      let priceTotalAmount =this.allTotal==null?0:this.allTotal.priceTotalAmount
      let profitAmount = this.allTotal==null?0:this.allTotal.profitAmount
      let bidTotalAmount = this.allTotal==null?0:this.allTotal.bidTotalAmount
      arr[9] = new BigNumber(Number(bidTotalAmount)).toFormat(2)+"元"
      arr[11] = new BigNumber(Number(priceTotalAmount)).toFormat(2)+"元"
      arr[12] = new BigNumber(Number(profitAmount)).toFormat(2)+"元"
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
      console.log(this.jglist,'看看');
      let arrId = []
      this.jglist.forEach((item)=>{
        arrId.push(item.jgid)
      })

      // this.search.params.push({
      //   columnName:"company.id",
      //   queryType:"in",
      //   value:arrId
      // })
      this.search.params[0].columnName = "company.id"
      this.search.params[0].queryType = "in"
      this.search.params[0].value = arrId
      //this.setLoad();
      getOrganizationList(this.search).then((res) => {
        this.dispensingList = res.data.rows;
        this.dispensingTotal = res.data.total;
        getAmount(this.search).then(response=>{
          this.allTotal = response.data
          //this.resetLoad();
        })
      });
    },
    onSearch() {
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
        if (this.queryModel.dateRange && this.queryModel.dateRange.length) {
        this.search.params.push({
            logic: "AND",
            queryType: "("
          },{
            columnName: "create_date",
            logic: "",
            queryType: 'between',
            value: [],
          },{
            logic: "",
            queryType: ")"
          })
      }
      if(this.queryModel.name&&this.queryModel.name!=''){
          this.search.params.push({
              columnName: "drug.goods_name",
              queryType: "like",
              value: this.queryModel.name,
            },)
      }
      if(this.queryModel.recipelCode&&this.queryModel.recipelCode!=''){
        this.search.params.push({
              columnName: "recipelInfo.code",
              queryType: "=",
              value: this.queryModel.recipelCode,
            },)
      }
      this.search.params.push( {
              columnName: "drug.type",
              queryType: "=",
              value: validatenull(this.queryModel.type.value)
                    ? ""
                    : this.queryModel.type.value,
            })

         if (this.queryModel.dateRange && this.queryModel.dateRange.length) {
                this.queryModel.dateRange[0]=this.$moment(this.queryModel.dateRange[0]).format(
                    "YYYY-MM-DD HH:mm:ss"
                  )
                this.queryModel.dateRange[1]=this.$moment(this.queryModel.dateRange[1]).format(
                        "YYYY-MM-DD HH:mm:ss"
                      )
              this.search.params[2].value=this.queryModel.dateRange
              
            }
          if(this.queryModel.organizationId&&this.queryModel.organizationId!=""){
              this.search.params[0].columnName = "company.id"
              this.search.params[0].queryType = "="
              this.search.params[0].value = this.queryModel.organizationId
          }else{
              let arrId = []
              this.jglist.forEach((item)=>{
                arrId.push(item.jgid)
              })
              this.search.params[0].columnName = "company.id"
              this.search.params[0].queryType = "in"
              this.search.params[0].value = arrId
          }
           getOrganizationList(this.search).then((res) => {
              this.dispensingList = res.data.rows;
              this.dispensingTotal = res.data.total;
              getAmount(this.search).then(response=>{
                this.allTotal = response.data
                //this.resetLoad();
              })
            });
    },
    resetCondition(){
      this.queryModel = {
        name:'',
        recipelCode:'',
        type:'',
        dateRange:[this.addCreateDate(),new Date()],
        organizationId:"",   //机构id
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

       getjglist(this.YpjxcRc).then((responseData)=>{
              if (responseData.code == 100){
                  if(responseData.data.length>0){
                      // responseData.data.forEach((item)=>{
                      //     if(item.jgid!=currentUser.company.id){
                      //     this.jglist.push({
                      //         jgid:item.jgid,
                      //         jgmc:item.jgmc,
                      //      })
                      //      //this.zsids+=item.jgid + ','
                      //      this.zsids.push({jgid:item.jgid})
                      //    }
                      // })
                      this.jglist = responseData.data.filter((item) => item.jgid !=currentUser.company.id)
                      console.log(this.jglist,'不算');
                //       this.zsids= this.zsids.substring(0, this.zsids.lastIndexOf(','));
                //       console.info("zdzdzdzdzd:"+this.zsids)
                      this.init();
                   }
              }
          })
    },
    bigNum(num){
      if(num||num==='0'){
        return new BigNumber(num).toFormat(2)
      }else{
        return ''
      }
    },
    exportExcel () {
          let biaoge=[]
            biaoge[0]={
              recipelCode:"处方单号",
              type:"药品类型",
              name:"药品名称",
              norms:"规格",
              number:"数量",
              unit:"单位",
              bid:"进价单价(元)",
              bidTotal:"进价总价(元)",
              price:"销售单价(元)",
              priceTotal:"销售总价(元)",
              profit:"利润(元)",
              factory:"生产厂家",
              batchNo:"生产批号",
              dispensingDate:"发药日期",
            }
            for (let i = 0; i < this.dispensingList.length; i++) {
              let arr={
                recipelCode:"",
                type:"",
                name:"",
                norms:"",
                number:"",
                unit:"",
                bid:"",
                bidTotal:"",
                price:"",
                priceTotal:"",
                profit:"",
                factory:"",
                batchNo:"",
                dispensingDate:"",
              }
              arr.recipelCode=this.dispensingList[i].recipelCode
              arr.type=this.dispensingList[i].type
              arr.name=this.dispensingList[i].name
              arr.norms=this.dispensingList[i].norms
              arr.number=this.dispensingList[i].number
              arr.unit=this.dispensingList[i].unit
              arr.bid=this.dispensingList[i].bid
              arr.bidTotal=this.dispensingList[i].bidTotal
              arr.price=this.dispensingList[i].price
              arr.priceTotal=this.dispensingList[i].priceTotal
              arr.profit=this.dispensingList[i].profit
              arr.factory=this.dispensingList[i].factory
              arr.batchNo=this.dispensingList[i].batchNo
              arr.dispensingDate=this.dispensingList[i].dispensingDate
              biaoge.push(arr)
            }
            let num=biaoge.length
            biaoge[num]={
                recipelCode:"合计",
                type:"",
                name:"",
                norms:"",
                number:"",
                unit:"",
                bid:"",
                bidTotal:new BigNumber(Number(this.allTotal!=null?this.allTotal.bidTotalAmount:0)).toFormat(2)+"",
                price:"",
                priceTotal:new BigNumber(Number(this.allTotal.priceTotalAmount)).toFormat(2)+"",
                profit:new BigNumber(Number(this.allTotal.profitAmount)).toFormat(2)+"",
                factory:"",
                batchNo:"",
                dispensingDate:"",
            }
            let web = XLSX.utils.book_new();
            let contentWs = XLSX.utils.json_to_sheet(biaoge);
            contentWs["A1"]={
              t:"s",
              v:this.Company.name +' 药品销售明细表',
              s:{
                alignment:{
                  horizontal:"center",
                  vertical:"center",
                  wrapText:true,
                },
              }
            }
            for (let i = 0; i < biaoge.length; i++) {
                contentWs["A"+(i+2)]={
                    t:"s",
                    v:biaoge[i].recipelCode,
                    s:{
                     alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
              contentWs["B"+(i+2)]={
                    t:"s",
                    v:biaoge[i].type,
                    s:{
                      alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
                contentWs["C"+(i+2)]={
                    t:"s",
                    v:biaoge[i].name,
                    s:{
                     alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
                contentWs["D"+(i+2)]={
                    t:"s",
                    v:biaoge[i].norms,
                    s:{
                     alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
                contentWs["E"+(i+2)]={
                    t:"s",
                    v:biaoge[i].number,
                    s:{
                     alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
               
                contentWs["F"+(i+2)]={
                    t:"s",
                    v:biaoge[i].unit,
                    s:{
                      alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
                contentWs["G"+(i+2)]={
                    t:"s",
                    v:biaoge[i].bid,
                    s:{
                      alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
                contentWs["H"+(i+2)]={
                    t:"s",
                    v:biaoge[i].bidTotal,
                    s:{
                      alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
                contentWs["I"+(i+2)]={
                    t:"s",
                    v:biaoge[i].price,
                    s:{
                      alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
                contentWs["J"+(i+2)]={
                    t:"s",
                    v:biaoge[i].priceTotal,
                    s:{
                      alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
                contentWs["K"+(i+2)]={
                    t:"s",
                    v:biaoge[i].profit,
                    s:{
                      alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }

                contentWs["L"+(i+2)]={
                    t:"s",
                    v:biaoge[i].factory,
                    s:{
                      alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
                contentWs["M"+(i+2)]={
                    t:"s",
                    v:biaoge[i].batchNo,
                    s:{
                      alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
                contentWs["N"+(i+2)]={
                    t:"s",
                    v:biaoge[i].dispensingDate,
                    s:{
                      alignment:{
                        horizontal:"center",
                        vertical:"center",
                        wrapText:true,
                      },
                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                      
                    }
                  }
                }
             
            }
            const mergeTitle = [
            
              {
                s: {r: 0, c: 0},
                e: {r: 0, c: 13}
              },
             
              ]
              //冻结第一行和第一列：
              contentWs['!freeze'] = {
                xSplit: "11",  //冻结列
                ySplit: "1",  //冻结行
                topLeftCell: "A2",  //在未冻结区域的左上角显示的单元格，默认为第一个未冻结的单元格
                state: "frozen"
              }
              contentWs["!merges"]=mergeTitle

            contentWs["!cols"]=[{wch:20},{wch:10},{wch:20},{wch:15},{wch:10},{wch:10},{wch:15},{wch:15},{wch:15},{wch:15},{wch:15},{wch:25},{wch:15},{wch:20}]
            contentWs["!rows"]={}

            contentWs["!margins"]={left:1.0,right:1.0,top:1.0,bottom:1.0,header:0.5,footer:0.5}
            XLSX.utils.book_append_sheet(web,contentWs,"药品销售明细表");
             // //使用xlsx-style写入文件方式，使得自定义样式生效
            const tmpDown=new Blob([
              this.s2ab(
                XLSXD.write(web,{
                  bookType:"xlsx",
                  BookSST:true,
                  type:"binary",
                  cellStyles:true,
                })
              )
            ])
           this.downExcel(tmpDown,'药品销售明细表.xlsx')       
    },
    downExcel(obj,fileName){
        const a_node=document.createElement("a");
        a_node.download=fileName;
        if("msSaveOrOpenBlob" in navigator){
          window.navigator.msSaveOrOpenBlob(obj,fileName)
        }else{
          a_node.href=URL.createObjectURL(obj);
        }
        a_node.click()

        setTimeout(()=>{
          URL.revokeObjectURL(obj)
        },2000)
      },
      s2ab(s){
        if(typeof ArrayBuffer !=="undefined"){
          const buf=new ArrayBuffer(s.length);
          const view=new Uint8Array(buf);
          for(let i=0;i!=s.length;++i){
            view[i]=s.charCodeAt(i)&0xff
          }
          return buf;
        }else{
          const buf=new Array(s.length);
          for(let i=0;i!=s.length;++i){
            buf[i]=s.charCodeAt(i)&0xff;
          }
          return buf;
        }
      },
  },
  watch: {},
  mounted() {
    this.initOptions()
    
  },
  computed:{
    Company() {
      let company = JSON.parse(sessionStorage.getItem("currentUser")).company;
      return {
        id: company.id,
        label: company.label,
        name: company.name,
      };
    },
  }
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