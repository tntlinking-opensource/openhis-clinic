<template>
  <el-card class="page-container">
    <div id="storage">
    <el-row id="conditionRow">
      <el-col :span="3">
        <el-select v-model="condition.supplier.id" placeholder="供应商">
          <el-option label="全部" value="">全部</el-option>
          <el-option
            v-for="supplierxz in supplierList"
            :key="supplierxz.id"
            :label="supplierxz.name"
            :value="supplierxz.id"
          >
          </el-option>
        </el-select>
      </el-col>
      <el-col :span="3">
        <el-select v-model="condition.status" placeholder="入库单状态">
          <el-option label="全部" value="">全部</el-option>
          <el-option
            v-for="statusxz in statusList"
            :label="statusxz.name"
            :value="statusxz.value"
            :key="statusxz.value"
          ></el-option>
        </el-select>
      </el-col>
      <el-col :span="4">
        <el-input v-model="condition.batchNo" placeholder="生产批号"></el-input>
      </el-col>
      <el-col :span="8" style="text-align:center">
        <el-date-picker
          :picker-options="filerData.pickerOptions"
          v-model="condition.dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"
        >
        </el-date-picker>
      </el-col>
      <el-col :span="3" style="display:flex;justivy-content:space-around">
               <el-button
                    type="primary"
                    icon="el-icon-search"
                    @click="querySupplierStorageList()"
                    :plain="true"
                  >搜索</el-button>
                <el-button
                    type="info"
                    icon="el-icon-refresh-left"
                    @click="resetCondition()"
                    :plain="true"
                  >重置</el-button>
            </el-col>
    </el-row>
    <el-row style="margin-top:10px;margin-bottom:10px">
      <el-col :span="24" style="text-align:right">
        <div class="bg_btn">
          <el-button type="primary" @click="editAndAddStorage(1)"
            >新增药品</el-button
          >
          <el-button type="primary" @click="editAndAddStorage(2)"
            >新增材料</el-button
          >
          <el-button>扫描条码</el-button>
        </div>
      </el-col>
    </el-row>
    <!-- <el-row id="conditionOperation">
      <el-col :span="2" :offset="22">
        <div class="bg_btn">
          <el-button type="primary" @click="querySupplierStorageList()"
            >查询</el-button
          >
          <el-button @click="resetCondition()">重置</el-button>
        </div>
      </el-col>
    </el-row> -->
    <el-row style="height: 250px">
      <el-col :span="24">
        <el-table
          border
          height="230"
          :data="storageTable"
          style="width: 100%"
          highlight-current-row
          @selection-change="selectionChange">
          <!-- @row-click="textRowClick"-->
        <el-table-column
          type="selection"
          width="55">
        </el-table-column>
          <el-table-column type="index" :index="indexMethod" width="50px" label="序号" align="center">
          </el-table-column>
          <el-table-column prop="code" label="入库单号">
            <template slot-scope="scope">
              <div class="examine-box">
                <span>{{ scope.row.code }}</span>
                <span
                  class="examine"
                  v-if="scope.row.examine.name == '已作废'"
                  >{{ scope.row.examine.name }}</span
                >
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="supplier.name"
            label="供应商"
          ></el-table-column>
          <el-table-column prop="breed" label="品种数量" width="80px"></el-table-column>
          <!-- <el-table-column prop="number" label="明细数量"></el-table-column> -->
          <el-table-column prop="type" label="物品类型" width="80px">
            <template slot-scope="scope">
              <span v-if="scope.row.type=='1'">
                 药品
              </span>
              <span v-else>
                材料
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="createBy" label="入库人"></el-table-column>
          <el-table-column prop="createDate" label="入库日期"></el-table-column>
          <el-table-column label="作废日期"></el-table-column>
          <el-table-column prop="examine.name" label="状态" width="80px"></el-table-column>
          <el-table-column
            prop="examineDate"
            label="审批日期"
          ></el-table-column>
        </el-table>
        <el-pagination
          @size-change="storageTableSizeChange"
          @current-change="storageTableCurrentChange"
          :page-sizes="[100,200,storageTableTotal]"
          :page-size="storageTablePagesize"
          :current-page="storageTableCurrentPage"
          layout="total,sizes, prev, pager, next,jumper"
          :total="storageTableTotal"
        >
        </el-pagination>
      </el-col>
    </el-row>
    <el-row style="height: 250px; margin-top: 10px">
      <el-col :span="24">
        <el-table
          id="exportTableStorage"
          border
          height="230"
          :data="storageDetailTable"
          show-summary
          sum-text=""
          :summary-method="getDetailSummaries"
          style="width: 100%"
        >
        <el-table-column type="index" :index="indexMethod" width="50px" label="序号" align="center">
          </el-table-column>
          <el-table-column prop="norms" label="入库单号">
            <template slot-scope="scope">
              <span>{{ scope.row.code }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="drug.goodsName" label="药品/材料名称">
            <template slot-scope="scope">
              <span v-if="scope.row.drug.goodsName==''">{{ scope.row.stuff.name }}</span>
              <span v-else>{{ scope.row.drug.goodsName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="norms" label="规格" width="80px"></el-table-column>
          <el-table-column
            prop="factory.name"
            label="厂家名称"
          ></el-table-column>
          <el-table-column prop="batchNo" label="生产批号"></el-table-column>
          <el-table-column
            prop="produceDate"
            label="生产日期"
          ></el-table-column>
          <el-table-column prop="endDate" label="效期"></el-table-column>
          <el-table-column prop="number" label="数量" width="80px"></el-table-column>
          <el-table-column prop="bid" label="进价" width="60px"></el-table-column>
          <el-table-column prop="leastBid" label="最小单位进价" width="100px"></el-table-column>
          <el-table-column prop="allBid" label="总进价" width="100px">
            <template slot-scope="scope" v-if="systemParamConfig==2">
             <!-- {{scope.row.leastBid*scope.row.inventory}}-->
             {{scope.row.allBid}}
            </template>
            <template slot-scope="scope" v-if="systemParamConfig==1">
              <!-- {{scope.row.leastBid*scope.row.inventory}}-->
              {{scope.row.allRetailPrice}}
            </template>
          </el-table-column>
          <el-table-column prop="retailPrice" label="零售价" width="80px"></el-table-column>
          <el-table-column
            prop="allRetailPrice"
            label="总零售价"
             width="100px"
          >
          <template slot-scope="scope">
            {{scope.row.allRetailPrice}}
          </template>
          </el-table-column>
        </el-table>
        <!-- <el-pagination
          @size-change="storageDetailTableSizeChange"
          @current-change="storageDetailTableCurrentChange"
          :page-sizes="[5, 10, 15, 20]"
          :page-size="storageDetailTablePagesize"
          :current-page="storageDetailTableCurrentPage"
          layout="total,sizes, prev, pager, next,jumper"
          :total="storageDetailTableTotal"
        >
        </el-pagination> -->
      </el-col>
    </el-row>
    <el-row style="margin-top: 10px;">
      <!-- <el-col :span="4" :offset="1">
        <div class="spaninput">
          <span>随货单号：</span>
          <el-input
            id="withGoodsNo"
            v-model="otherInfo.withGoodsNo"
            :readonly="true"
          >
          </el-input>
        </div>
      </el-col>
      <el-col :span="4" :offset="1">
        <div class="spaninput">
          <span>发票号：</span>
          <el-input v-model="otherInfo.invoiceNumber" :readonly="true">
          </el-input>
        </div>
      </el-col> -->
      <el-col :span="2" :offset="18"  style="text-align:right">
        <el-button @click="destoryStorage()"
        :style="{ 'background': btnType?'white':'gray','color':btnType?'#606266':'white' }">
          作废</el-button
        >
      </el-col>
      <el-col :span="2" style="text-align:right">
        <el-button @click="exportExcel" :style="{ 'background': btnType?'white':'gray','color':btnType?'#606266':'white' }">导出明细</el-button>
      </el-col>
      <el-col :span="2" style="text-align:right">
        <el-button @click="searchDetail" :style="{ 'background':'white','color':'#606266' }">查询明细</el-button>
      </el-col>
    </el-row>

    <el-dialog
      custom-class="customWidth"
      width="90%"
      modal="true"
      :close-on-click-modal="false"
      :title="dialogTitle"
      :visible.sync="dialogVisible"
    >
      <AddStorage
        @fatherMethod="closeDialog"
        :supplierList="supplierList"
        :addType="addType"
      ></AddStorage>
    </el-dialog>
  </div>
  </el-card>
</template>

<script>
import AddStorage from "./components/addstorage";
import { listSupplierPage, getSupplierById,listSupplierAll } from "@/api/stock/supplier";
import {
  bulkUpdateSupplierStorage,
  bulkUpdateSupplierStorage1,
  listSupplierStorageAll,
  listSupplierStoragePage,
} from "@/api/stock/supplierStorage";
import {
  listSupplierStockAll,
  listSupplierStockPage,
  getSupplierStockBySid,
} from "@/api/stock/supplierStock";
import { listDictItemAll } from "@/api/sys/dictItem";

 import FileSaver from 'file-saver'
 import * as XLSX from 'xlsx'
//import XLSX from 'xlsx'
import XLSXD from 'xlsx-style'
import {listSysParamConfigAll} from "../../../api/sys/sysParamConfig";
export default {
  components: { AddStorage },
  data() {
    return {
      btnType: false,
      filerData: {
        pickerOptions: {
          // disabledDate(time) {
          //   return time.getTime() > Date.now() - 8.64e6;
          // },
        },
      },
      company_id: "998324809623052308" /*诊所ID*/,
      examine_destroy_id: "1005787933775863932",
      examine_destroy_value: "supplierStorageExamineStatus_2" /*作废状态value*/,
      storageTableCurrentPage: 1,
      storageTablePagesize: 100 /*主表最大显示行数*/,
      storageTableTotal: 0 /*主表共多少条数据*/,
      storageDetailTableCurrentPage: 1,
      storageDetailTablePagesize: 5 /*主表最大显示行数*/,
      storageDetailTableTotal: 0 /*主表共多少条数据*/,
      supplierList: null,
      statusList: null,
      storageCode:'',//入库单号
      supplierName:'',//供应商名称
      initialNum:[],//明细表赋值
      invalid:[],//作废明细
      condition: {
        supplier: {
          id: null,
        },
        status: null,
        dateRange: [] /*时间范围*/,
        batchNo: "",
      },
      storageTable: null,
      otherInfo: {
        withGoodsNo: "" /*供货单号*/,
        invoiceNumber: "" /*发票号*/,
      },
      storageDetailTable: null,
      dialogVisible: false,
      dialogTitle: "",
      isAdd: 0 /*0修改 1新增*/,
      addtype: 1, //新增入库单类型 1为药品 2为材料
      selectStorage: [],
      supplierSearch: {
        params: [{ columnName: "company_id", queryType: "=", value: "" }],
        offset: 0,
        limit: 100,
        columnName: "", // 排序字段名
        order: "", // 排序
      },
      statusSearch: {
        params: [{ columnName: "company_id", queryType: "=", value: "" }],
        offset: 0,
        limit: 100,
        columnName: "", // 排序字段名
        order: "", // 排序
      },
      supplierStorageSearch: {
        params: [{ columnName: "company_id", queryType: "=", value: "" }],
        offset: 0,
        limit: 10000,
        columnName: "code", // 排序字段名
        order: "desc", // 排序
      },
      systemParamConfig:"",
      systemParamConfigSearch: {
        params: [
        ]
      },
      supplierStockSearch: {
        params: [{ columnName: "company_id", queryType: "=", value: "" }],
        offset: 0,
        limit: 10000,
        columnName: "", // 排序字段名
        order: "", // 排序
      },
    };
  },
  methods: {
    selectionChange(rows){
      if(rows.length==0){
        this.btnType = false;
      }
      this.selectStorage = rows;
      this.searchDetail();
    },
    // 进价是否为零售价
    GetSysParamConfig(){
      listSysParamConfigAll(this.systemParamConfigSearch).then(responseData => {
        if(responseData.code == 100) {
          if (responseData.data.length >= 1){
            responseData.data.forEach(data=>{
              if (data.configName === "retailPrice") {
                /*if (data.configValue){
                  this.systemParamConfig = 1
                }else{
                  this.systemParamConfig = 2
                }*/
                this.systemParamConfig = ("true" === data.configValue) ? 1 : 2
              }
            })
            console.log(this.systemParamConfig,"this.systemParamConfig")
          }
        } else {
          this.showMessage(responseData)
        }
      }).catch(error => {
        this.outputError(error)
      })
    },
    //查询明细
    searchDetail(){
      console.log(this.systemParamConfig+"== systemParamConfig")
      this.storageDetailTable = []
      this.storageDetailTableTotal = null
      this.invalid = []
      this.selectStorage.forEach(item=>{
        this.supplierStockSearch.params = [
          { columnName: "company_id", queryType: "=", value: currentUser.company.id },
          {
            columnName: "supplier_storage_id",
            queryType: "=",
            value: item.id,
          },
        ];
        listSupplierStockPage(this.supplierStockSearch)
        .then((responseData) => {
           if (responseData.code == 100) {
            this.storageDetailTableTotal += responseData.data.total;
            this.invalid.push(...responseData.data.rows)
            let initalArr=[]
            initalArr=responseData.data.rows;
            this.initialNum=[]
            for (let i = 0; i < initalArr.length; i++) {
              initalArr[i].code = item.code
              initalArr[i].supplier = item.supplier.name
              this.initialNum.push(initalArr[i].number)
              if(initalArr[i].drug.goodsName!=''){
                  initalArr[i].inventory = initalArr[i].number
                  initalArr[i].number=Math.floor(initalArr[i].number/initalArr[i].drug.preparation) >= 0 ? Math.floor(initalArr[i].number/initalArr[i].drug.preparation)+(initalArr[i].drug.pack.name)+((initalArr[i].number%initalArr[i].drug.preparation > 0) ? (initalArr[i].number%initalArr[i].drug.preparation) + initalArr[i].drug.preparationUnit.name:""):initalArr[i].number+initalArr[i].drug.preparationUnit.name
                  initalArr[i].allRetailPrice=initalArr[i].allRetailPrice//initalArr[i].retailPrice/initalArr[i].drug.preparation
               }else if(initalArr[i].stuff.name!=''){
                  initalArr[i].inventory = initalArr[i].number
                  initalArr[i].number=Math.floor(initalArr[i].number/initalArr[i].stuff.packNumber) >= 0 ? Math.floor(initalArr[i].number/initalArr[i].stuff.packNumber)+initalArr[i].stuff.packUnit.name+((initalArr[i].number%initalArr[i].stuff.packNumber > 0) ? (initalArr[i].number%initalArr[i].stuff.packNumber)+initalArr[i].stuff.minUnit.name:""):initalArr[i].number+initalArr[i].stuff.minUnit.name
                  initalArr[i].allRetailPrice=initalArr[i].allRetailPrice//initalArr[i].retailPrice/initalArr[i].stuff.packNumber
              }
            }
            this.storageDetailTable.push(...initalArr);
           } else {
             this.$message.error(responseData);
           }
        })
        .catch((error) => {
          this.$message.error(error);
        });
      })
      if(this.selectStorage.length>0) this.btnType = true;
    },
    /*textRowClick(rows) {
      if(rows.length==0){
        this.btnType = false;
      }
      this.selectStorage = rows;
      this.searchDetail();
    },*/
    pageInit() {
      var day = new Date();
      // this.condition.dateRange = [
      //   new Date(day.getFullYear(), day.getMonth(), day.getDate(), 0, 0),
      //   new Date(day.getFullYear(), day.getMonth(), day.getDate(), 23, 59, 59),
      // ];

      this.supplierSearch.params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: currentUser.company.id,
        },
      ];
      listSupplierAll(this.supplierSearch)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.supplierList = responseData.data;
          } else {
            this.$message.error(responseData);
          }
        })
        .catch((error) => {
          this.$message.error(error);
        });

      this.statusSearch.params = [
        {
          columnName: "dict_type_id",
          queryType: "=",
          value: "1005787933775863928",
        },
      ];
      listDictItemAll(this.statusSearch).then((responseData) => {
        this.statusList = responseData.data;
      });
      this.querySupplierStorageList();
    },
    storageTableSizeChange(val) {
      this.storageTablePagesize = val;
      this.querySupplierStorageList();
    },
    storageTableCurrentChange(val) {
      this.storageTableCurrentPage = val;
      this.querySupplierStorageList();
    },
    storageDetailTableSizeChange(val) {
      this.storageDetailTablePagesize = val;
      this.storageTableRowClick(this.selectStorage);
    },
    storageDetailTableCurrentChange(val) {
      this.storageDetailTableCurrentPage = val;
      this.storageTableRowClick(this.selectStorage);
    },
    querySupplierStorageList() {
      this.supplierStorageSearch.offset =
        (this.storageTableCurrentPage - 1) * this.storageTablePagesize;
      this.supplierStorageSearch.limit = this.storageTablePagesize;
      this.supplierStorageSearch.params = [
        { columnName: "company_id", queryType: "=", value: currentUser.company.id },
      ];
      if (this.condition.supplier.id) {
        this.supplierStorageSearch.params.push({
          columnName: "supplier_id",
          queryType: "=",
          value: this.condition.supplier.id,
        });
      }
      if (this.condition.status) {
        this.supplierStorageSearch.params.push({
          columnName: "examine",
          queryType: "=",
          value: this.condition.status,
        });
      }

      if(this.condition.batchNo){
        this.supplierStorageSearch.params.push({
          columnName: "ss.batch_no",
          queryType: "=",
          value: this.condition.batchNo,
        });
      }

      this.supplierStorageSearch.params.push({
        columnName: "create_date",
        queryType: ">=",
        value: this.condition.dateRange[0]
          ? this.$moment(this.condition.dateRange[0]).format(
              "YYYY-MM-DD HH:mm:ss"
            )
          : "",
      });
      this.supplierStorageSearch.params.push({
        columnName: "create_date",
        queryType: "<=",
        value: this.condition.dateRange[1]
          ? this.$moment(this.condition.dateRange[1]).format(
              "YYYY-MM-DD HH:mm:ss"
            )
          : "",
      });
      listSupplierStoragePage(this.supplierStorageSearch)
        .then((responseData) => {
          if (responseData.code == 100) {
           // console.log(responseData,'aaaaa');
           if(responseData.data.rows!=null){
              let arr=[]
            arr = responseData.data.rows;
            // for (let i = 0; i < arr.length; i++) {

            //   getSupplierStockBySid(arr[i].id).then((res)=>{
            // //    console.log(res,"七管");
            //   // console.log(res.data.norms.split("*")[1].split("/")[0].split("")[res.data.norms.split("*")[1].split("/")[0].split("").length-1],"qiege");
            //        if(res.data[0].stuff.name==''){
            //        arr[i].types='药品'

            //        //arr[i].number=Math.floor(res.data.number/res.data.drug.preparation) > 0 ? Math.floor(res.data.number/res.data.drug.preparation)+(res.data.norms.split("/")[1])+((res.data.number%res.data.drug.preparation > 0) ? (res.data.number%res.data.drug.preparation) + res.data.norms.split("*")[1].split("/")[0].split("")[res.data.norms.split("*")[1].split("/")[0].split("").length-1]:""):res.data.number+res.data.norms.split("*")[1].split("/")[0].split("")[res.data.norms.split("*")[1].split("/")[0].split("").length-1]

            //      }else if(res.data[0].stuff.name!=''){
            //        arr[i].types='材料'

            //       // arr[i].number=Math.floor(res.data.number/res.data.stuff.packNumber) > 0 ? Math.floor(res.data.number/res.data.stuff.packNumber)+(res.data.norms.split("*")[1])+((res.data.number%res.data.stuff.packNumber > 0) ? (res.data.number%res.data.stuff.packNumber)+res.data.norms.split("*")[0].split("")[res.data.norms.split("*")[0].split("").length-1]:""):res.data.number+res.data.norms.split("*")[0].split("")[res.data.norms.split("*")[0].split("").length-1]

            //     }

            //   }).catch(()=>{})

            // }

            clearTimeout(this.timer);
            this.timer=setTimeout(()=>{
              this.storageTable=arr
               this.storageTableTotal = responseData.data.total;
            },400)

            this.clearOtherAndDetailData();
           }else{
              this.storageTable=responseData.data.rows
               this.storageTableTotal = responseData.data.total;
           }

          } else {
            this.$message.error(responseData);
          }
        })
        .catch((error) => {
          this.$message.error(error);
        });
    },
    storageTableRowClick(row) {
      this.storageCode=row.code
      this.supplierName=row.supplier.name
      this.btnType = true;
      this.selectStorage = row;
      this.supplierStockSearch.offset =
        (this.storageDetailTableCurrentPage - 1) *
        this.storageDetailTablePagesize;
      this.supplierStockSearch.limit = this.storageDetailTablePagesize;
      this.supplierStockSearch.params = [
        { columnName: "company_id", queryType: "=", value: currentUser.company.id },
        {
          columnName: "supplier_storage_id",
          queryType: "=",
          value: row.id,
        },
      ];
      listSupplierStockPage(this.supplierStockSearch)
        .then((responseData) => {
           if (responseData.code == 100) {
             this.storageDetailTableTotal = responseData.data.total;
              this.invalid=responseData.data.rows
             console.log(this.invalid,'qiqi');
            let initalArr=[]
            initalArr=responseData.data.rows;

            this.initialNum=[]
            for (let i = 0; i < initalArr.length; i++) {
              initalArr[i].code = this.selectStorage.code
              this.initialNum.push(initalArr[i].number)
              if(initalArr[i].drug.goodsName!=''){
                  initalArr[i].number=Math.floor(initalArr[i].number/initalArr[i].drug.preparation) >= 0 ? Math.floor(initalArr[i].number/initalArr[i].drug.preparation)+(initalArr[i].norms.split("/")[1])+((initalArr[i].number%initalArr[i].drug.preparation > 0) ? (initalArr[i].number%initalArr[i].drug.preparation) + initalArr[i].norms.split("*")[1].split("/")[0].split("")[initalArr[i].norms.split("*")[1].split("/")[0].split("").length-1]:""):initalArr[i].number+initalArr[i].norms.split("*")[1].split("/")[0].split("")[initalArr[i].norms.split("*")[1].split("/")[0].split("").length-1]
                }else if(initalArr[i].stuff.name!=''){
                  initalArr[i].number=Math.floor(initalArr[i].number/initalArr[i].stuff.packNumber) >= 0 ? Math.floor(initalArr[i].number/initalArr[i].stuff.packNumber)+(initalArr[i].norms.split("*")[1])+((initalArr[i].number%initalArr[i].stuff.packNumber > 0) ? (initalArr[i].number%initalArr[i].stuff.packNumber)+initalArr[i].norms.split("*")[0].split("")[initalArr[i].norms.split("*")[0].split("").length-1]:""):initalArr[i].number+initalArr[i].norms.split("*")[0].split("")[initalArr[i].norms.split("*")[0].split("").length-1]
              }
            }
            console.log(initalArr,'dada');
            this.storageDetailTable = initalArr;
            console.log(this.storageDetailTable, "----");
           } else {
             this.$message.error(responseData);
           }
        })
        .catch((error) => {
          this.$message.error(error);
        });
      this.otherInfo.withGoodsNo = row.withGoodsNo;
      this.otherInfo.invoiceNumber = row.invoiceNumber;
    },
    getDetailSummaries(param) {
      console.log(param);
      const { columns, data } = param;
      const sums = [];
      columns.forEach((column, index) => {
        if (index === 0) {
          if (data == null) sums[index] = "共计:0";
          else sums[index] = "共计:" + data.length;
          return;
        } else if (index === 8) {
          if (data == null) sums[index] = 0;
          else {
            const values = data.map((item) => Number(item[column.property]));
            if (!values.every((value) => isNaN(value))) {
              sums[index] = values.reduce((prev, curr) => {
                const value = Number(curr);
                if (!isNaN(value)) {
                  return prev + curr;
                } else {
                  return prev;
                }
              }, 0);
            }
          }
        } else {
          sums[index] = "";
        }
      });

      return sums;
    },
    editAndAddStorage(type) {
        this.addType = type
        this.isAdd = 1;
        this.dialogVisible = true;
        if(this.addType == 1){
          this.dialogTitle = "新增药品入库单";
        }else{
          this.dialogTitle = "新增材料入库单";
        }

    },
    closeDialog() {
      this.dialogVisible = false;
      this.querySupplierStorageList();
    },
    resetCondition() {
      var day = new Date();
      this.condition = {
        supplier: {
          id: null,
        },
        status: null,
        dateRange: [
          // new Date(day.getFullYear(), day.getMonth(), day.getDate(), 0, 0),
          // new Date(
          //   day.getFullYear(),
          //   day.getMonth(),
          //   day.getDate(),
          //   23,
          //   59,
          //   59
          // ),
        ] /*时间范围*/,
        drugName: "",
      };
      this.clearData();
      this.pageInit();
    },
    clearData() {
      // this.storageTable = null;
      this.clearOtherAndDetailData();
    },
    clearOtherAndDetailData() {
      this.storageDetailTable = null;
      this.otherInfo.withGoodsNo = null;
      this.otherInfo.invoiceNumber = null;
      this.selectStorage = [];
    },
    destoryStorage() {
       //  this.selectStorage.number=this.initialNum
      if (this.selectStorage.length==1&&this.invalid.length>0) {
        // console.log(this.selectStorage.examine.id);
        console.log(this.examine_destroy_id);
        if (this.selectStorage[0].examine.value == this.examine_destroy_value) {
          this.$message({
            message: "已作废入库单不用重复作废",
            type: "warning",
          });
          return;
        }
        for (let i = 0; i < this.invalid.length; i++) {
          if(this.invalid[i].initial>this.initialNum[i]){

                this.$message({
                message: "该库存已被占用",
                type: "warning",
              });
              return;
          }
        }
        // if(this.selectStorage.initial > this.selectStorage.number){
        //     this.$message({
        //     message: "该库存已被占用",
        //     type: "warning",
        //   });
        //   return;
        // }
        this.$confirm("是否确定要作废选中的入库单", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            this.selectStorage[0].examine.id = this.examine_destroy_id;
            this.selectStorage[0].examine.value = this.examine_destroy_value;

            var destoryList = [];
            destoryList.push(this.selectStorage[0]);
            bulkUpdateSupplierStorage1(this.selectStorage[0])
              .then((responseData) => {
                if (responseData.code == 100) {
                  this.$message({ message: "作废成功", type: "success" });
                  this.querySupplierStorageList();
                } else {
                  this.$message.error(responseData);
                }
              })
              .catch((error) => {
                this.$message.error(error);
              });
          })
          .catch(() => {
            return;
          });
      } else if(this.selectStorage.length>1) {
        this.$message({ message: "一次只能选择一个入库单作废", type: "error" });
      }else if(this.invalid.length==0){
        this.$message({ message: "请先查询明细", type: "error" });
      }
     },
     //导出表格
      exportExcel () {
        let biaoge=[]
        biaoge[0]={
             code:"单据号",
             name:"药品/材料名称",
             norms:"规格",
             number:"数量",
             bid:"进价",
             allBid:"总进价",
             retailPrice:"零售价",
             allRetailPrice:"总零售价",
             batchNo:"生产批号",
             produceDate:"生产日期",
             endDate:"有效期",
             createDate:"入库时间",
             supplier:"供货单位",
             factory:"厂家名称",
             beizhu:"备注"
        }

        let numbers=0;
        let allRetailPrices=0
        for (let i = 0; i < this.storageDetailTable.length; i++) {
          let arr={
          code:'',
          name:'',
          norms:'',
          number:'',
          bid:'',
          allBid:'',
          retailPrice:'',
          allRetailPrice:'',
          batchNo:'',
          produceDate:'',
          endDate:'',
          createDate:'',
          supplier:'',
          factory:'',
        }

          if(this.storageDetailTable[i].drug.goodsName==''){
            arr.name=this.storageDetailTable[i].stuff.name
          }else{
            arr.name=this.storageDetailTable[i].drug.goodsName
          }
          arr.norms=this.storageDetailTable[i].norms
          arr.factory=this.storageDetailTable[i].factory.name
          arr.batchNo=this.storageDetailTable[i].batchNo
          arr.code=this.storageDetailTable[i].code
          arr.supplier=this.storageDetailTable[i].supplier
          arr.createDate=this.storageDetailTable[i].createDate.split(" ")[0]
          arr.produceDate=this.storageDetailTable[i].produceDate.split(" ")[0]
          arr.endDate=this.storageDetailTable[i].endDate.split(" ")[0]
          arr.number=this.storageDetailTable[i].number
          arr.bid=this.storageDetailTable[i].bid+''
          // arr.leastBid=this.storageDetailTable[i].leastBid+''
          arr.allBid=this.storageDetailTable[i].allBid+''
          arr.retailPrice=this.storageDetailTable[i].retailPrice+''
          arr.allRetailPrice=this.storageDetailTable[i].allRetailPrice+''
          arr.beizhu=''
          biaoge.push(arr)
          numbers+=this.storageDetailTable[0].number.substring(0,this.storageDetailTable[0].number.length-1)-0
          allRetailPrices+=this.storageDetailTable[i].allRetailPrice

        }
     //  console.log(formatDate(new Date(),"yyyy-MM-dd"),'字符串');
        let num=biaoge.length
         biaoge[num]={
             code:'合计',
             name:"",
             norms:"",
             number:"",
             bid:"",
            //  leastBid:"",
             allBid:"",
             retailPrice:"",
             allRetailPrice:""+allRetailPrices,
             batchNo:"",
             produceDate:"",
             endDate:"",
             createDate:'',
            //  number:""+numbers,
             supplier:'',
             factory:"",
             beizhu:""
        }
        // biaoge[num+1]={
        //      code:'制单人：',
        //      name:"",
        //      norms:"",
        //      number:"",
        //      bid:"",
        //     //  leastBid:"",
        //      allBid:"",
        //      retailPrice:"",
        //      allRetailPrice:"库管：",
        //      batchNo:"",
        //      produceDate:"",
        //      endDate:"",
        //      createDate:'收货人：',
        //      supplier:'',
        //      factory:"",
        //      beizhu:"收货日期："
        // }

           let web = XLSX.utils.book_new();
           let headers={
             name:"药品/材料名称",
             norms:"规格",
             factory:"厂家名称",
             batchNo:"生产批号",
             produceDate:"生产日期",
             endDate:"有效期",
             number:"数量",
             bid:"进价",
             leastBid:"最小单位进价",
             allBid:"总进价",
             retailPrice:"零售价",
             allRetailPrice:"总零售价"
           }
           //biaoge.unshift(headers)
            // let contentWs = XLSX.utils.json_to_sheet(biaoge,{
            //   headers:['name','norms','factory','batchNo','produceDate','endDate','number','bid','leastBid','allBid','retailPrice','allRetailPrice'],
            //   skipHeader:false,
            //   orgin: "A4",
            // });
             let contentWs = XLSX.utils.json_to_sheet(biaoge);

            contentWs["A1"]={
              t:"s",
              v:this.storageDetailTable[0].company.name + ' 入库明细表',
              s:{
                alignment:{
                  horizontal:"center",
                  vertical:"center",
                  wrapText:true,
                },
              }
            }

            for (let i = 0; i < biaoge.length; i++) {
             if(i==biaoge.length-1){
                contentWs["A"+(i+2)]={
                    t:"s",
                    v:biaoge[i].code,
                    s:{
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
                    v:biaoge[i].name,
                    s:{

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
                    v:biaoge[i].norms,
                    s:{

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
                    v:biaoge[i].number,
                    s:{

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
                    v:biaoge[i].bid,
                    s:{

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
                    v:biaoge[i].allBid,
                    s:{

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
                    v:biaoge[i].retailPrice,
                    s:{

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
                    v:biaoge[i].allRetailPrice,
                    s:{

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
                    v:biaoge[i].batchNo,
                    s:{

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
                    v:biaoge[i].produceDate,
                    s:{

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
                    v:biaoge[i].endDate,
                    s:{

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
                    v:biaoge[i].createDate,
                    s:{

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
                    v:biaoge[i].supplier,
                    s:{

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
                    v:biaoge[i].factory,
                    s:{

                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                    }
                  }
                }
                if(biaoge[i].beizhu==''){
                   contentWs["O"+(i+2)]={
                    t:"s",
                    v:"",
                    s:{

                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                    }
                  }
                }
                }else{
                  contentWs["O"+(i+2)]={
                    t:"s",
                    v:biaoge[i].beizhu,
                    s:{

                      border: {
                      top: { style: 'thin' },
                      left: { style: 'thin' },
                      bottom: { style: 'thin' },
                      right: { style: 'thin' },
                    }
                  }
                }
                }
             }else{
                contentWs["A"+(i+2)]={
                    t:"s",
                    v:biaoge[i].code,
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
                      right: { style: 'thin' }
                    }
                  }
                }
              contentWs["B"+(i+2)]={
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
                      right: { style: 'thin' }
                    }
                  }
                }
                contentWs["C"+(i+2)]={
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
                      right: { style: 'thin' }
                    }
                  }
                }
                contentWs["D"+(i+2)]={
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
                      right: { style: 'thin' }
                    }
                  }
                }
                contentWs["E"+(i+2)]={
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
                      right: { style: 'thin' }
                    }
                  }
                }

                contentWs["F"+(i+2)]={
                    t:"s",
                    v:biaoge[i].allBid,
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
                      right: { style: 'thin' }
                    }
                  }
                }
                contentWs["G"+(i+2)]={
                    t:"s",
                    v:biaoge[i].retailPrice,
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
                      right: { style: 'thin' }
                    }
                  }
                }
                contentWs["H"+(i+2)]={
                    t:"s",
                    v:biaoge[i].allRetailPrice,
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
                      right: { style: 'thin' }
                    }
                  }
                }
                contentWs["I"+(i+2)]={
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
                      right: { style: 'thin' }
                    }
                  }
                }
                contentWs["J"+(i+2)]={
                    t:"s",
                    v:biaoge[i].produceDate,
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
                      right: { style: 'thin' }
                    }
                  }
                }
                contentWs["K"+(i+2)]={
                    t:"s",
                    v:biaoge[i].endDate,
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
                      right: { style: 'thin' }
                    }
                  }
                }

                contentWs["L"+(i+2)]={
                    t:"s",
                    v:biaoge[i].createDate,
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
                      right: { style: 'thin' }
                    }
                  }
                }
                contentWs["M"+(i+2)]={
                    t:"s",
                    v:biaoge[i].supplier,
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
                      right: { style: 'thin' }
                    }
                  }
                }
                contentWs["N"+(i+2)]={
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
                      right: { style: 'thin' }
                    }
                  }
                }
                if(biaoge[i].beizhu==''){
                   contentWs["O"+(i+2)]={
                    t:"s",
                    v:"",
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
                      right: { style: 'thin' }
                    }
                  }
                }
                }else{
                  contentWs["O"+(i+2)]={
                    t:"s",
                    v:biaoge[i].beizhu,
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
                      right: { style: 'thin' }
                    }
                  }
                }
                }
             }

            }


             const mergeTitle = [

              {
                s: {r: 0, c: 0},
                e: {r: 0, c: 14}
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

            contentWs["!cols"]=[{wch:20},{wch:20},{wch:12},{wch:10},{wch:10},{wch:15},{wch:15},{wch:15},{wch:15},{wch:10},{wch:10},{wch:10},{wch:20},{wch:20},{wch:15}]
            contentWs["!rows"]={}

            contentWs["!margins"]={left:1.0,right:1.0,top:1.0,bottom:1.0,header:0.5,footer:0.5}





            XLSX.utils.book_append_sheet(web,contentWs,"入库明细表");

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
          //  XLSX.writeFile(web,'入库明细表.xlsx');
           this.downExcel(tmpDown,'入库明细表.xlsx')
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
      indexMethod(index){
       return (this.storageTableCurrentPage-1)*this.storageTablePagesize+index +1;
    },
      indexMethod2(index){
       return (this.storageDetailTableCurrentPage-1)*this.storageDetailTablePagesize+index +1;
    }
  },
  mounted() {
    this.GetSysParamConfig();
    this.pageInit();
  },
};
</script>


<style rel="stylesheet/scss" lang="scss" scoped>
.page-container{
  padding: 0;
}
.examine-box {
  padding: 15px 20px;
  position: relative;
}
.examine {
  color: red;
  position: absolute;
  top: 10px;
  right: 0;
  transform: rotate(45deg);
}

.bg_btn button {
  width: 100px;
}

#conditionRow {
  margin-top: 1%;
}
#conditionOperation {
  margin-top: 1%;
}

.spaninput {
  display: flex;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  font-size: 12px;
}
/deep/ .el-table__fixed-right-patch{
    width:5px !important
  }
  /deep/ .el-table colgroup col[name='gutter']{
    width:5px !important
  }
  /deep/ .el-table__body{
    width:100% !important
  }
</style>
