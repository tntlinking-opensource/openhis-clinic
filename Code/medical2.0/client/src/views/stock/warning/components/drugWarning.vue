<template>
    <div>
       <div style="padding-top:10px">
             <el-radio-group v-model="tabPosition" @change="changStatus" style="margin-bottom: 10px;">
                <el-radio-button label="0">有效期预警</el-radio-button>
                <el-radio-button label="1">库存预警</el-radio-button>
            </el-radio-group>
            <div style="float:right">
              <el-button type="primary" icon='el-icon-upload2' @click="exportTable">导出</el-button>
            </div>
        </div>

        <!-- 有效期预警 -->
        <div v-if="tabPosition=='0'">

           <div class='query-form-container'>
          <el-row class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
              <el-col :span="6">
                <el-form-item label='药品名称' prop='name'>
                  <el-input
                    style="width:320px;"
                    v-model="queryModel.name"
                    placeholder="请输入药品名称或者拼音码"
                  ></el-input>
                </el-form-item>
              </el-col>
                <el-col :span="6">
              <el-form-item label='供应商' prop='type'>
                <el-select
                  v-model="queryModel.supplier"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择供应商"
                  class="typeClass"
                >
                  <el-option
                    v-for="type in supplierList"
                    :key="type.id"
                    :label="type.name"
                    :value="type.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
              <el-col :span="6">
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
            <el-col :span="6">
              <el-form-item label='是否启用' prop='status'>
                <el-select
                  v-model="queryModel.status"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择是否启用"
                  class="typeClass"
                >
                  <el-option
                    v-for="type in isEnable"
                    :key="type.value"
                    :label="type.name"
                    :value="type.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
                <el-form-item label='剩余数量' prop='inventory'>
                <el-select
                  v-model="queryModel.inventory"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择剩余数量"
                  class="typeClass"
                >
                  <el-option
                    v-for="type in inventorys"
                    :key="type.value"
                    :label="type.name"
                    :value="type.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            </el-form>
          </el-row>
          <el-row id="conditionOperation">
            <el-col :span="24" style="text-align:right;padding-right:5px">

                <el-button type="primary" icon="el-icon-search" @click='drugBatchNumber' :plain='true'>搜索</el-button>
                <el-button type="info" icon="el-icon-refresh-left" @click='resetCondition' :plain='true'>重置</el-button>
            </el-col>
          </el-row>
        </div>
      <!--  搜索栏  结束 -->

          <el-row>
        <el-col :span="24">
          <div @mouseleave="moveTableOutside">
            <el-table
              class="dispensing_table"
              :data="indateList"
              height="calc(100vh - 330px)"
              border
              ref="mutipleTable1"
              @sort-change="onSortChange"
              @header-dragend="onChangeWidth"
              :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName"
              highlight-current-row
            >
            <!-- show-summary
             :summary-method='getTotal' -->
              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
             <el-table-column prop="drug.goodsName" label="药品名称" > </el-table-column>
              <el-table-column prop="drug.type.name" label="药品分类">
              </el-table-column>

              <el-table-column prop="norms" label="规格">
                 </el-table-column>
                <el-table-column prop="factory.name" label="生产厂商" > </el-table-column>
                <el-table-column prop="supplierId.name" label="供应商" > </el-table-column>
                <el-table-column prop="batchNo" label="批号" > </el-table-column>
              <el-table-column prop="number" label="剩余数量" >
                  <template slot-scope="scope">
                  {{Math.floor(scope.row.number/scope.row.drug.preparation) >= 0 ? Math.floor(scope.row.number/scope.row.drug.preparation)+(scope.row.drug.pack.name)+((scope.row.number%scope.row.drug.preparation > 0) ? (scope.row.number%scope.row.drug.preparation) + scope.row.drug.preparationUnit.name:""):scope.row.number+scope.row.drug.preparationUnit.name}}
                </template>
                </el-table-column>
                <el-table-column prop="endDate" label="有效期" > </el-table-column>
                <el-table-column prop="cancellation" label="状态" >
                    <template slot-scope="scope">
                      <span v-if="scope.row.cancellation=='0'" style="color:orange">
                          即将过期
                      </span>
                      <span v-else style="color:red">
                         已过期
                      </span>
                    </template>
                </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
        </div>

        <!-- 库存预警 -->
        <div v-else><el-row>

          <div class='query-form-container'>
          <el-row class='search-row'>
            <el-form :model='inventoryQueryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
              <el-col :span="6">
                <el-form-item label='药品名称' prop='name'>
                  <el-input
                    style="width:320px;"
                    v-model="inventoryQueryModel.name"
                    placeholder="请输入药品名称或者拼音码"
                  ></el-input>
                </el-form-item>
              </el-col>

              <el-col :span="6">
              <el-form-item label='药品类型' prop='type'>
                <el-select
                  v-model="inventoryQueryModel.type"
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
            <el-col :span="6">
              <el-form-item label='是否启用' prop='status'>
                <el-select
                  v-model="inventoryQueryModel.status"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择是否启用"
                  class="typeClass"
                >
                  <el-option
                    v-for="type in isEnable"
                    :key="type.value"
                    :label="type.name"
                    :value="type.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
                <el-form-item label='剩余数量' prop='inventory'>
                <el-select
                  v-model="inventoryQueryModel.inventory"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择剩余数量"
                  class="typeClass"
                >
                  <el-option
                    v-for="type in inventorys"
                    :key="type.value"
                    :label="type.name"
                    :value="type.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            </el-form>
          </el-row>
          <el-row id="conditionOperation">
            <el-col :span="24" style="text-align:right;padding-right:5px">

                <el-button type="primary" icon="el-icon-search" @click='drugInventory' :plain='true'>搜索</el-button>
                <el-button type="info" icon="el-icon-refresh-left" @click='resetCondition' :plain='true'>重置</el-button>
            </el-col>
          </el-row>
        </div>

        <el-col :span="24">
          <div @mouseleave="moveTableOutside">
            <el-table
              class="dispensing_table"
              :data="inventoryList"
              height="calc(100vh - 330px)"
              border
              ref="mutipleTable1"
              @sort-change="onSortChange"
              @header-dragend="onChangeWidth"
              :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName"
              highlight-current-row
            >
            <!-- show-summary
             :summary-method='getTotal' -->
              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
             <el-table-column prop="goodsName" label="药品名称" > </el-table-column>
              <el-table-column prop="type.name" label="药品分类">
              </el-table-column>
                 <el-table-column prop="dosis" label="规格" key="slot">
                   <template #default="scope">
                    {{scope.row.dosis}}{{scope.row.dosisUnit.name}}*{{scope.row.preparation}}{{scope.row.preparationUnit.name}}/{{scope.row.pack.name}}
                   </template>
                 </el-table-column>
                <el-table-column prop="factory.name" label="生产厂商" > </el-table-column>
              <el-table-column prop="inventory" label="剩余数量" key="slot">
                <template #default="scope">
                  <span style="color:orange">
                  {{Math.floor(scope.row.stock.surplusStock/scope.row.preparation) >= 0 ? Math.floor(scope.row.stock.surplusStock/scope.row.preparation)+(scope.row.pack.name)+((scope.row.stock.surplusStock%scope.row.preparation > 0) ? (scope.row.stock.surplusStock%scope.row.preparation) + scope.row.preparationUnit.name:""):scope.row.stock.surplusStock+scope.row.preparationUnit.name}}
                  </span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
        </div>
    </div>
</template>
<script>
import { validatenull } from "@/utils/validate";
import MainUI from "@/views/components/mainUI";
import {listSupplierAll} from "@/api/stock/supplier"
import { listDictItemAll } from "@/api/sys/dictItem";
import { BigNumber } from "bignumber.js";
import {getDrugIndateWarning,getDrugInventoryWarning,exportTable} from "@/api/stock/warning";
export default {
  extends: MainUI,
  props:['drugActiveName','getCurrentPage','getSizeChange'],
  data() {
    return {
       type:0,    //预警切换
       tabPosition:"0",   //预警切换
       supplierList:[],   //药品供应商
       type_List:[],    //药品类型
       queryTypes: {
        goods_name: "like",
        type: "=",
        bar_code: "like",
      },
      indateSearch:{
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
            columnName:"cancellation",
            queryType:"<>",
            value:"1"
          },
        ],
      },
      inventorySearch:{
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
        ],
      },
      indateList:[],   //效期预警数据集合
      sizeChange:20,   //每页显示个数
      currentPage:1,  //每页显示条数
      queryModel: {
        name:'',
        type:'',
        status:'',
        inventory:"",
        supplier:"",
      },
      inventoryQueryModel:{
        name:'',
        type:'',
        status:'',
        inventory:"",
        supplier:"",
      },
      isEnable:[
          {
              value:"0",
              name:"否"
          },
          {
              value:"1",
              name:"是"
          }
      ],
      inventorys:[
          {
              value:"0",
              name:"等于0"
          },
          {
              value:"1",
              name:"大于0"
          }
      ],

      inventoryList:[],   //库存预警集合

    };
  },
  methods: {

    exportTable(){

      if(this.tabPosition=='0'){
        this.indateSearch.columnName = '0'
        //按有效期导出
        exportTable(this.indateSearch).then((res)=>{
               console.log(res,'这是个啥');
               const filename = decodeURI(res.headers.split(';')[1].split('=')[1]) || '.xls'
                //const filename = "药品有效期预警.xls"
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
        }).catch((Error)=>{
          this.outputError(Error)
        })
      }else{
        this.inventorySearch.columnName = "1"
        //按库存导出
        exportTable(this.inventorySearch).then((res)=>{
                const filename = decodeURI(res.headers.split(';')[1].split('=')[1]) || '.xls'
                //const filename = "药品库存预警.xls"
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
        }).catch((Error)=>{
          this.outputError(Error)
        })
      }
    },

    resetCondition(){
      if(this.tabPosition=='0'){
        this.queryModel={
         name:'',
        type:'',
        status:'',
        inventory:"",
        supplier:"",
      }
      this.drugBatchNumber()
      }else{
        this.inventoryQueryModel ={
          name:'',
          type:'',
          status:'',
          inventory:"",
          supplier:"",
        }
        this.drugInventory()
      }
    },

    //库存搜索
    drugInventory(){
      this.currentPage = 1
      this.inventorySearch = {
          columnName:"",
          limit: this.sizeChange,
          offset: this.currentPage - 1,
          order: "",
          params:[
            {
              columnName: "company_id",
              queryType: "=",
              value: currentUser.company.id,
            },
            {
              columnName:"type",
              queryType:"=",
              value:validatenull(this.inventoryQueryModel.type.value)
                    ? ""
                    : this.inventoryQueryModel.type.value
            },
            {
              columnName:"status",
              queryType:"=",
              value:validatenull(this.inventoryQueryModel.status)
              ? ""
              :this.inventoryQueryModel.status
            }
          ]
      }
      if(this.inventoryQueryModel.name!=undefined && this.inventoryQueryModel.name!=''){

             var pattern2 = new RegExp("[A-Za-z]+");
                if (pattern2.test(this.inventoryQueryModel.name)) {
                  this.inventorySearch.params.push(
                    {
                      columnName: "pinyin_code",
                      queryType: "like",
                      value: this.inventoryQueryModel.name.toUpperCase(),
                    },

                  )
                } else {
                  this.inventorySearch.params.push(
                    {
                      columnName: "goods_name",
                      queryType: "like",
                      value: this.inventoryQueryModel.name,
                    },

                  )
                }
          }
          if(this.inventoryQueryModel.inventory && this.inventoryQueryModel.inventory!=''){
              if(this.inventoryQueryModel.inventory=='0'){
                   this.inventorySearch.params.push(
                    {
                      columnName: "inventory",
                      queryType: "=",
                      value: 0,
                    })
              }else{
                   this.inventorySearch.params.push(
                    {
                      columnName: "inventory",
                      queryType: ">",
                      value: 0,
                    })
              }
          }
          this.setLoad()
          getDrugInventoryWarning(this.inventorySearch).then((res)=>{
            if(res.code==100){
              console.log(res.data,'获取库存');
              if(this.currentPage==1){
                this.$emit("changeCurrentPage",1)
            }
              this.$emit("changeTotal",res.data.total)
              this.inventoryList = res.data.rows
              this.resetLoad()
            }
          }).catch((error)=>{
            this.outputError(error)
          })
    },

    //有效期搜索
    drugBatchNumber(){
      this.currentPage = 1
       this.indateSearch = {
          columnName:"",
          limit: this.sizeChange,
          offset: this.currentPage - 1,
          order: "",
          params:[
            {
              columnName: "company_id",
              queryType: "=",
              value: currentUser.company.id,
            },
            {
              columnName: "drug.type",
              queryType: "=",
              value: validatenull(this.queryModel.type.value)
                    ? ""
                    : this.queryModel.type.value,
            },
            {
              columnName: "drug.status",
              queryType: "=",
              value: validatenull(this.queryModel.status)
                    ? ""
                    : this.queryModel.status,
            },
            {
                columnName:"cancellation",
                queryType:"<>",
                value:"1"
            },
            {
                columnName:"drug_id",
                queryType:"<>",
                value:"''"
            }
          ]
        }

        if(this.queryModel.name!=undefined && this.queryModel.name!=''){

             var pattern2 = new RegExp("[A-Za-z]+");
                if (pattern2.test(this.queryModel.name)) {
                  this.indateSearch.params.push(
                    {
                      columnName: "drug.pinyin_code",
                      queryType: "like",
                      value: this.queryModel.name.toUpperCase(),
                    },

                  )
                } else {
                  this.indateSearch.params.push(
                    {
                      columnName: "drug.goods_name",
                      queryType: "like",
                      value: this.queryModel.name,
                    },

                  )
                }
          }
          if(this.queryModel.inventory && this.queryModel.inventory!=''){
              if(this.queryModel.inventory=='0'){
                   this.indateSearch.params.push(
                    {
                      columnName: "number",
                      queryType: "=",
                      value: 0,
                    })
              }else{
                   this.indateSearch.params.push(
                    {
                      columnName: "number",
                      queryType: ">",
                      value: 0,
                    })
              }
            }
        if(this.queryModel.supplier && this.queryModel.supplier!=''){
            this.indateSearch.params.push(
                {
                    columnName: "supplier_id",
                    queryType: "=",
                    value: this.queryModel.supplier,
                }
            )
        }


        this.setLoad();
        getDrugIndateWarning(this.indateSearch).then(res=>{
        if(res.code==100){
          console.log(res.data,'获取有效期');
         if(this.currentPage==1){
            this.$emit("changeCurrentPage",1)
         }
          this.$emit("changeTotal",res.data.total)
          this.indateList = res.data.rows
          this.resetLoad();
        }
      }).catch((error)=>{
        this.outputError(error)
      })
    },

    //表格序号
    indexMethod(index){
       return (this.currentPage-1)*this.indateSearch.limit+index +1;
    },

    //获取合计
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
    //  if(this.tabPosition=='0'){
    //      arr[8] = new BigNumber(Number(this.allTotal.totalSellingPrice)).toFormat(2)
    //      arr[10] = new BigNumber(Number(this.allTotal.totalPrice)).toFormat(2)
    //  }else{
    //       //售价合计
    //   arr[6] = new BigNumber(Number(this.allTotal.totalSellingPrice)).toFormat(2)

    //   //进价合计
    //   arr[7] = new BigNumber(Number(this.allTotal.totalPrice)).toFormat(2)
    //  }
      console.log(arr)
      return arr
    },

    //初始化获取库存预警
    inventoryInit(){
      this.setLoad()
      getDrugInventoryWarning(this.inventorySearch).then((res)=>{
        if(res.code==100){
          console.log(res.data,'获取库存');
          if(this.currentPage==1){
            this.$emit("changeCurrentPage",1)
         }
          this.$emit("changeTotal",res.data.total)
          this.inventoryList = res.data.rows
          this.resetLoad();
        }
      }).catch((error)=>{
        this.outputError(error)
      })
    },

    //初始化获取有效期预警
    init(){

      this.setLoad();
      //初始化页码
      getDrugIndateWarning(this.indateSearch).then(res=>{
        if(res.code==100){
          console.log(res.data,'获取有效期');
         if(this.currentPage==1){
            this.$emit("changeCurrentPage",1)
         }
          this.$emit("changeTotal",res.data.total)
          this.indateList = res.data.rows
          this.resetLoad();
        }
      }).catch((error)=>{
        this.outputError(error)
      })
    },

    //初始化数据
    initOptions(){
       this.tabPosition = '0'
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

      //获取供应商
      let supplierSearch = {
        order:"",
        params: [
          {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
          {
            columnName: "type",
            queryType: "=",
            value: '1',
          }
        ],
      }
        listSupplierAll(supplierSearch).then((res)=>{
            if(res.code==100){
                this.supplierList = res.data
            }
        }).catch((error)=>{
            this.$message.error(error)
        })
    },

    handleClick(tab, event){
        this.type = tab.name
    },
    changStatus(){
      if(this.tabPosition=='0'){

      }else{

      }
    },
  },
  watch: {
    drugActiveName:function(newVal,oldVal){
          this.tabPosition = newVal
    },
    getSizeChange:function(newVal,oldVal){
      this.sizeChange = newVal
      this.currentPage = 1

      if(this.tabPosition=='0'){
        this.indateSearch.limit = newVal;
        this.indateSearch.offset = (this.currentPage - 1) * newVal;
        this.init()
      }else{
        this.inventorySearch.limit = newVal;
        this.inventorySearch.offset = (this.currentPage-1) * newVal
        this.inventoryInit()
      }
    },
    getCurrentPage:function(newVal,oldVal){

      this.currentPage = newVal

      if(this.tabPosition=='0'){
        this.indateSearch.offset = (newVal - 1) * this.indateSearch.limit;
        this.init()
      }else{
        this.inventorySearch.offset = (newVal - 1) * this.indateSearch.limit;
        this.inventoryInit();
      }
    },
    tabPosition:{
      handler(newVal,oldVal){
        console.log(newVal,'获取变换值');
        this.tabPosition = newVal
        if(this.tabPosition=='0'){
          this.currentPage = 1
          this.queryModel={
            name:'',
            type:'',
            status:'',
            inventory:"",
            supplier:"",
          }
          this.indateSearch={
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
                  columnName:"cancellation",
                  queryType:"<>",
                  value:"1"
                },
              ],
            }
          this.init()
        }else{
          this.currentPage = 1
          this.inventoryQueryModel = {
              name:'',
              type:'',
              status:'',
              inventory:"",
              supplier:"",
          }
          this.inventorySearch = {
             offset: 0,
              limit: 20,
              order: "",
              columnName: "",
              params: [
                {
                  columnName: "company_id",
                  queryType: "=",
                  value: currentUser.company.id,
                }
              ],
          }
          this.inventoryInit()
        }
      }
    },
    // tableData是el-table绑定的数据
      tableData: {
          // 解决表格显示错位问题
          handler () {
              this.$nextTick(() => {
                  // tableRef是el-table绑定的ref属性值
                  this.$refs.mutipleTable1.doLayout()// 对 Table 进行重新布局
              })
          },
          deep: true
      },
  },
  updated(){
    if(this.$refs.mutipleTable1){
      this.$nextTick(() => {
            // tableRef是el-table绑定的ref属性值
            this.$refs.mutipleTable1.doLayout()// 对 Table 进行重新布局
        })
    }
  },
  mounted() {
    this.initOptions();
    if(!this.$route.query.type){
      this.init()
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
// 设置表格body的高度
 /deep/.el-table__body-wrapper {
  //解决数据展示超出body高度不滚动bug
  overflow-y: auto;
   // 减去的是表格header的高度
   height: calc(100% - 44px) !important;
 }
</style>
