<template>
  <el-row v-loading="loading">
    <el-card class="page-container">
        <!-- 药品材料切换 -->
        <div style="margin-top:-20px;margin-left:-20px">
            <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
                <el-tab-pane label="药品库存" name="0">

                </el-tab-pane>
                <el-tab-pane label="材料库存" name="1">

                </el-tab-pane>
            </el-tabs>
        </div>

        <div style="padding-top:10px">
             <el-radio-group v-model="tabPosition" @change="changStatus" style="margin-bottom: 10px;">
                <el-radio-button label="0">按批号</el-radio-button>
                <el-radio-button label="1">按商品</el-radio-button>
            </el-radio-group>
            <div style="float:right;">
              <el-button type="primary" icon='el-icon-upload2' @click="exportTable">导出</el-button>
            </div>
        </div>
      <!--  搜索栏  开始 -->
     <div v-if="activeName=='0'" style="padding-top:10px">
         <!-- 按批号和商品进行显示 -->
        <div v-if="tabPosition=='0' && dispensingList!=null">
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

                <el-button type="primary" icon="el-icon-search" @click='drugBatchNumber(1)' :plain='true'>搜索</el-button>
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
              height="calc(100vh - 300px)"
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

              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
             <el-table-column prop="drug.goodsName" label="药品名称" > </el-table-column>
              <el-table-column prop="drug.type.name" label="药品分类">
              </el-table-column>

              <el-table-column prop="supplierStock.norms" label="规格">
                 </el-table-column>
                <el-table-column prop="drug.factory.name" label="生产厂商" > </el-table-column>
                <el-table-column prop="supplierStock.supplierId.name" label="供应商" > </el-table-column>
                <el-table-column prop="supplierStock.batchNo" label="批号" > </el-table-column>
                <el-table-column prop="drug.price" label="售价(元)" > </el-table-column>
                <el-table-column prop="" label="售价合计(元)" >
                    <template slot-scope="scope">
                        {{getPriceTotal(scope.row)}}
                    </template>
                </el-table-column>
                <el-table-column prop="supplierStock.bid" label="成本价(元)" > </el-table-column>
                <el-table-column prop="" label="成本合计(元)" >
                    <template slot-scope="scope">
                        {{(scope.row.supplierStock.leastBid*(scope.row.storageStock-scope.row.usedStock-scope.row.reimburseStock)).toFixed(2)}}
                    </template>
                </el-table-column>
              <el-table-column prop="number" label="剩余数量" >
                  <template slot-scope="scope">
                  {{Math.floor((scope.row.storageStock-scope.row.usedStock-scope.row.reimburseStock)/scope.row.drug.preparation) >= 0 ? Math.floor((scope.row.storageStock-scope.row.usedStock-scope.row.reimburseStock)/scope.row.drug.preparation)+(scope.row.drug.pack.name)+(((scope.row.storageStock-scope.row.usedStock-scope.row.reimburseStock)%scope.row.drug.preparation > 0) ? ((scope.row.storageStock-scope.row.usedStock-scope.row.reimburseStock)%scope.row.drug.preparation) + scope.row.drug.preparationUnit.name:""):(scope.row.storageStock-scope.row.usedStock-scope.row.reimburseStock)+scope.row.drug.preparationUnit.name}}
                </template>
                </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
        </div>


<!-- 药品查询 -->
          <div v-else-if="tabPosition=='1' && dispensingList!=null">
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
              height="calc(100vh - 300px)"
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

              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
             <el-table-column prop="goodsName" label="药品名称" > </el-table-column>
              <el-table-column prop="type.name" label="药品分类">
              </el-table-column>

              <el-table-column prop="dosis" label="规格">
                <template slot-scope="scope">
                  <template>
                    {{scope.row.dosis}}{{scope.row.dosisUnit.name}}*{{scope.row.preparation}}{{scope.row.preparationUnit.name}}/{{scope.row.pack.name}}
                  </template>
                </template>
                 </el-table-column>
                <el-table-column prop="factory.name" label="生产厂商" > </el-table-column>
                <el-table-column prop="price" label="售价(元)" > </el-table-column>
                <el-table-column prop="" label="售价合计(元)" >
                    <template slot-scope="scope">
                        {{getPriceTotal(scope.row)}}
                    </template>
                </el-table-column>
                <el-table-column prop="bid" label="成本合计(元)" >
                    <template slot-scope="scope">
                        {{scope.row.bid==undefined?0:scope.row.bid}}
                    </template>
                </el-table-column>
              <el-table-column prop="inventory" label="剩余数量" >
                <template slot-scope="scope">
                  {{Math.floor(scope.row.inventory/scope.row.preparation) >= 0 ? Math.floor(scope.row.inventory/scope.row.preparation)+(scope.row.pack.name)+((scope.row.inventory%scope.row.preparation > 0) ? (scope.row.inventory%scope.row.preparation) + scope.row.preparationUnit.name:""):scope.row.inventory+scope.row.preparationUnit.name}}
                </template>
                 </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
          </div>
     </div>

<!-- 材料栏 -->

    <div v-else>
         <!-- 按批号和商品进行显示 -->
        <div v-if="tabPosition=='0' && stuffList!=null">
             <div class='query-form-container'>
          <el-row class='search-row'>
            <el-form :model='stuffQueryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
              <el-col :span="6">
                <el-form-item label='材料名称' prop='name'>
                  <el-input
                    style="width:320px;"
                    v-model="stuffQueryModel.name"
                    placeholder="请输入材料名称或者拼音码"
                  ></el-input>
                </el-form-item>
              </el-col>
               <el-col :span="6">
              <el-form-item label='供应商' prop='type'>
                <el-select
                  v-model="stuffQueryModel.supplier"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择供应商"
                  class="typeClass"
                >
                  <el-option
                    v-for="type in stuffSupplierList"
                    :key="type.id"
                    :label="type.name"
                    :value="type.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
              <el-col :span="6">
              <el-form-item label='材料类型' prop='type'>
                <el-select
                  v-model="stuffQueryModel.type"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择材料类型"
                  class="typeClass"
                >
                  <el-option
                    v-for="type in stuffTypeList"
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
                  v-model="stuffQueryModel.status"
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
                  v-model="stuffQueryModel.inventory"
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

                <el-button type="primary" icon="el-icon-search" @click='stuffBatchNumber(1)' :plain='true'>搜索</el-button>
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
              :data="stuffList"
              height="calc(100vh - 300px)"
              border
              ref="mutipleTable2"
              @sort-change="onSortChange"
              @header-dragend="onChangeWidth"
              :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName"
              highlight-current-row
              show-summary
              :summary-method='getTotal'

            >

              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
             <el-table-column prop="stuff.name" label="材料名称" >
                </el-table-column>
               <el-table-column prop="stuff.type.name" label="材料分类">
              </el-table-column>

              <el-table-column prop="supplierStock.norms" label="规格">
                 </el-table-column>
                <el-table-column prop="stuff.factory.name" label="生产厂商" > </el-table-column>
                <el-table-column prop="supplierStock.supplierId.name" label="供应商" > </el-table-column>
                <el-table-column prop="supplierStock.batchNo" label="批号" > </el-table-column>
                <el-table-column prop="stuff.priceOutSell" label="售价(元)" > </el-table-column>
                <el-table-column prop="" label="售价合计(元)" >
                    <template slot-scope="scope">
                        {{getStuffPriceTotal(scope.row)}}
                    </template>
                </el-table-column>
                <el-table-column prop="supplierStock.bid" label="成本价(元)" > </el-table-column>
                <el-table-column prop="" label="成本合计(元)" >
                    <template slot-scope="scope">
                        {{(scope.row.supplierStock.leastBid*(scope.row.storageStock-scope.row.usedStock-scope.row.reimburseStock)).toFixed(2)}}
                    </template>
                </el-table-column>
              <el-table-column prop="number" label="剩余数量" >
                  <template slot-scope="scope">
                  {{Math.floor((scope.row.storageStock-scope.row.usedStock-scope.row.reimburseStock)/scope.row.stuff.packNumber) >= 0 ? Math.floor((scope.row.storageStock-scope.row.usedStock-scope.row.reimburseStock)/scope.row.stuff.packNumber)+(scope.row.stuff.packUnit.name)+(((scope.row.storageStock-scope.row.usedStock-scope.row.reimburseStock)%scope.row.stuff.packNumber > 0) ? ((scope.row.storageStock-scope.row.usedStock-scope.row.reimburseStock)%scope.row.stuff.packNumber) + scope.row.stuff.minUnit.name:""):(scope.row.storageStock-scope.row.usedStock-scope.row.reimburseStock)+scope.row.stuff.minUnit.name}}
                </template>
                </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
        </div>

<!-- 按材料商品 -->

          <div v-else-if="tabPosition=='1' && stuffList!=null">
            <div class='query-form-container'>
          <el-row class='search-row'>
            <el-form :model='stuffQueryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>
              <el-col :span="6">
                <el-form-item label='材料名称' prop='name'>
                  <el-input
                    style="width:320px;"
                    v-model="stuffQueryModel.name"
                    placeholder="请输入材料名称或者拼音码"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="6">
              <el-form-item label='材料类型' prop='type'>
                <el-select
                  v-model="stuffQueryModel.type"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择材料类型"
                  class="typeClass"
                >
                  <el-option
                    v-for="type in stuffTypeList"
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
                  v-model="stuffQueryModel.status"
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
                  v-model="stuffQueryModel.inventory"
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

                <el-button type="primary" icon="el-icon-search" @click='stuffOnSearch' :plain='true'>搜索</el-button>
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
              :data="stuffList"
              height="calc(100vh - 330px)"
              border
              ref="mutipleTable2"
              @sort-change="onSortChange"
              @header-dragend="onChangeWidth"
              :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName"
              highlight-current-row
              show-summary
              :summary-method='getTotal'

            >

              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
             <el-table-column prop="name" label="材料名称" > </el-table-column>
              <el-table-column prop="type.name" label="材料分类">
              </el-table-column>

              <el-table-column prop="specifications" label="规格">
                <template slot-scope="scope">
                  <template>
                    {{scope.row.packNumber}}{{scope.row.minUnit.name}}/{{scope.row.packUnit.name}}
                  </template>
                </template>
                 </el-table-column>
                <el-table-column prop="factory.name" label="生产厂商" > </el-table-column>
                <el-table-column prop="priceOutSell" label="售价(元)" > </el-table-column>
                <el-table-column prop="" label="售价合计(元)" >
                    <template slot-scope="scope">
                        {{getStuffPriceTotal(scope.row)}}
                    </template>
                </el-table-column>
                <el-table-column prop="bid" label="成本合计(元)" >
                    <template slot-scope="scope">
                        {{scope.row.bid==undefined?0:scope.row.bid}}
                    </template>
                </el-table-column>
              <el-table-column prop="inventory" label="剩余数量" >
                <template slot-scope="scope">
                  {{Math.floor(scope.row.inventory/scope.row.packNumber) >= 0 ? Math.floor(scope.row.inventory/scope.row.packNumber)+(scope.row.packUnit.name)+((scope.row.inventory%scope.row.packNumber > 0) ? (scope.row.inventory%scope.row.packNumber) + scope.row.minUnit.name:""):scope.row.inventory+scope.row.minUnit.name}}
                </template>
                 </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
          </div>
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
import ExportExcelButton from "@/components/ExportExcelButton";
import ViewColumnsSelect from "@/views/components/ViewColumnsSelect";
import QueryForm from "@/views/components/queryForm";
import MainUI from "@/views/components/mainUI";
import {listSupplierAll} from "@/api/stock/supplier"
import OperationIcon from "@/components/OperationIcon";
import { listDictItemAll } from "@/api/sys/dictItem";
import { getList,getAmount } from "@/api/stock/dispensing";
import { BigNumber } from "bignumber.js";
import { getDrug,getDrugSalesStat,getBatchNumberDrug,getStuff,getStuffSalesStat,getBatchNumberStuff,getDrugSalesStatByNumber,getStuffSalesStatNumber,exportTable} from "@/api/stock/currentInventory";
export default {
  extends: MainUI,
  data() {
    return {
      tabPosition:'1',
      queryModel: {
        name:'',
        type:'',
        status:'1',
        inventory:"1",
        supplier:"",
      },
      stuffQueryModel:{
        name:'',
        type:'',
        status:'1',
        inventory:"1",
        supplier:"",
      },
      activeName:'0',  //药品材料切换
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
      stuffList:[],  //材料集合
      stuffSearch:{
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
            columnName: "is_out_sell",
            queryType: "=",
            value: '1',
          }
        ],
      },
      stuffTypeList:[],   //材料类型
      supplierList:[],   //药品供应商
      stuffSupplierList:[], // 材料供应商
    };
  },
  methods: {

      //药品信息导出
      exportTable(){
       if(this.activeName=='0'){
          if(this.tabPosition=='0'){
             //按照批号导出
             this.search.columnName = '2'
             exportTable(this.search).then((res)=>{
                console.log(res,'这是个啥');
              //const filename = decodeURI(res.headers['content-disposition'].split(';')[1].split('=')[1]) || '.xls'
                const filename = "药品当前库存-按批号.xls"
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
          }else{
            //按照商品导出
            this.search.columnName = '1'
              exportTable(this.search).then((res)=>{
                console.log(res,'这是个啥');
              //const filename = decodeURI(res.headers['content-disposition'].split(';')[1].split('=')[1]) || '.xls'
                const filename = "药品当前库存-按商品.xls"
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
          }
       }else{
         if(this.tabPosition=='0'){
           //按照批号导出
            this.stuffSearch.columnName = '4'
               exportTable(this.stuffSearch).then((res)=>{
                console.log(res,'这是个啥');
              //const filename = decodeURI(res.headers['content-disposition'].split(';')[1].split('=')[1]) || '.xls'
                const filename = "材料当前库存-按批号.xls"
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
         }else{
            //按照商品导出
              this.stuffSearch.columnName = '3'
               exportTable(this.stuffSearch).then((res)=>{
                console.log(res,'这是个啥');
              //const filename = decodeURI(res.headers['content-disposition'].split(';')[1].split('=')[1]) || '.xls'
                const filename = "材料当前库存-按商品.xls"
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
         }
       }
      },

      //材料按批号
      stuffBatchNumber(type,limit,offset){
        this.stuffList =null
        if(type==1){
          this.currentPage=1
        }
        this.stuffSearch = {
          columnName:"",
          limit: type==1?this.pageSize:type==2?limit:limit,
          offset: type==1?this.currentPage - 1:type==2?0:offset,
          order: "",
          params:[
            {
              columnName: "company_id",
              queryType: "=",
              value: currentUser.company.id,
            },
            {
              columnName: "stuff.is_out_sell",
              queryType: "=",
              value: '1',
            },
            {
              columnName: "stuff.type",
              queryType: "=",
              value: validatenull(this.stuffQueryModel.type.value)
                    ? ""
                    : this.stuffQueryModel.type.value,
            },
            {
              columnName: "stuff.status",
              queryType: "=",
              value: validatenull(this.stuffQueryModel.status)
                    ? ""
                    : this.stuffQueryModel.status,
            },
            {
                columnName:"storage_stock",
                queryType:">",
                value:"0"
            },
            {
                columnName:"type",
                queryType:"=",
                value:"2"
            },
          ]
        }

        if(this.stuffQueryModel.name!=undefined && this.stuffQueryModel.name!=''){

             var pattern2 = new RegExp("[A-Za-z]+");
                if (pattern2.test(this.stuffQueryModel.name)) {
                  this.stuffSearch.params.push(
                    {
                      columnName: "stuff.pinyin_code",
                      queryType: "like",
                      value: this.stuffQueryModel.name.toUpperCase(),
                    },

                  )
                } else {
                  this.stuffSearch.params.push(
                    {
                      columnName: "stuff.name",
                      queryType: "like",
                      value: this.stuffQueryModel.name,
                    },

                  )
                }
          }
          if(this.stuffQueryModel.inventory && this.stuffQueryModel.inventory!=''){
              if(this.stuffQueryModel.inventory=='0'){
                   this.stuffSearch.params.push(
                    {
                      columnName: "a.storage_stock-a.used_stock-a.reimburse_stock",
                      queryType: "=",
                      value: 0,
                    })
              }else{
                   this.stuffSearch.params.push(
                    {
                      columnName: "a.storage_stock-a.used_stock-a.reimburse_stock",
                      queryType: ">",
                      value: 0,
                    })
              }
          }
          if(this.tabPosition=='0'&&this.stuffQueryModel.supplier){
            this.stuffSearch.params.push(
                {
                    columnName: "supplierId.supplier_id",
                    queryType: "=",
                    value: this.stuffQueryModel.supplier,
                }
            )
        }
        let salesSearch={
              offset: 0,
              limit: 20,
              order: "",
              columnName: "",
              params:[
                  {
                    columnName: "company_id",
                    queryType: "=",
                    value: currentUser.company.id,
                  },
                  {
                    columnName: "is_out_sell",
                    queryType: "=",
                    value: '1',
                  },
              ]
          }
            this.setLoad();
            getBatchNumberStuff(this.stuffSearch).then((res) => {
                console.log(res,'看看这个');
                if(res.code=="100"){
                    this.stuffList=res.data.rows
                    this.dispensingTotal = res.data.total
                    getStuffSalesStatNumber(this.stuffSearch).then((res)=>{
                    if(res.code=="100"){
                        console.log(res.data,'获取计算价格');
                        this.allTotal = res.data
                        this.resetLoad();
                    }
                    }).catch(()=>{})
                }
            });
      },

      //材料搜索
      stuffOnSearch(){

        this.currentPage = 1;
        this.stuffSearch = {
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
            {
              columnName: "is_out_sell",
              queryType: "=",
              value: '1',
            },
            {
              columnName: "type",
              queryType: "=",
              value: validatenull(this.stuffQueryModel.type.value)
                    ? ""
                    : this.stuffQueryModel.type.value,
            },
            {
              columnName: "status",
              queryType: "=",
              value: validatenull(this.stuffQueryModel.status)
                    ? ""
                    : this.stuffQueryModel.status,
            }
          ]
        }

        if(this.stuffQueryModel.name!=undefined && this.stuffQueryModel.name!=''){

             var pattern2 = new RegExp("[A-Za-z]+");
                if (pattern2.test(this.stuffQueryModel.name)) {
                  this.stuffSearch.params.push(
                    {
                      columnName: "pinyin_code",
                      queryType: "like",
                      value: this.stuffQueryModel.name.toUpperCase(),
                    },

                  )
                } else {
                  this.stuffSearch.params.push(
                    {
                      columnName: "name",
                      queryType: "like",
                      value: this.stuffQueryModel.name,
                    },

                  )
                }
          }
          if(this.stuffQueryModel.inventory && this.stuffQueryModel.inventory!=''){
              if(this.stuffQueryModel.inventory=='0'){
                   this.stuffSearch.params.push(
                    {
                      columnName: "stock.storage_stock-stock.used_stock-stock.reimburse_stock",
                      queryType: "=",
                      value: 0,
                    })
              }else{
                   this.stuffSearch.params.push(
                    {
                      columnName: "stock.storage_stock-stock.used_stock-stock.reimburse_stock",
                      queryType: ">",
                      value: 0,
                    })
              }
          }

          this.stuffInit()
      },

      //计算材料的售价合计
      getStuffPriceTotal(item){
          let price;
          if(this.tabPosition=='0'){
              price = (((item.stuff.priceOutSell)/item.stuff.packNumber)*(item.storageStock-item.usedStock-item.reimburseStock)).toFixed(2)
          }else{

              price = (((item.priceOutSell)/item.packNumber)*item.inventory).toFixed(2)
          }
          return price;
      },

      //材料初始化获取类型
      stuffInitOptions(){
          this.tabPosition = '1'
          let type_search = {
        params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': '1004462867645374476'}]
      }
      // 响应字段的条件操作符，替换成触发字段的操作符
      type_search.params.forEach(item => {
        if(this.queryTypes[item.columnName]) {
          item.queryType = this.queryTypes[item.columnName]
        }
      })
      // 字段对应表上filter条件
        type_search.params.push.apply(type_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(type_search.params, this.$route.meta.routerId, '4005')
      this.stuffTypeList.splice(0, this.stuffTypeList.length)
      listDictItemAll(type_search).then(responseData => {
        this.stuffTypeList = responseData.data
      })
      },
      stuffInit(){
            let salesSearch={
              offset: 0,
              limit: 20,
              order: "",
              columnName: "",
              params:[
                  {
                    columnName: "company_id",
                    queryType: "=",
                    value: currentUser.company.id,
                  },
                  {
                    columnName: "is_out_sell",
                    queryType: "=",
                    value: '1',
                  },
              ]
          }
            this.setLoad();
            getStuff(this.stuffSearch).then((res) => {
                console.log(res,'看看这个');
                if(res.code=="100"){
                    this.stuffList=res.data.rows
                    this.dispensingTotal = res.data.total
                    getStuffSalesStat(salesSearch).then((res)=>{
                    if(res.code=="100"){
                        console.log(res.data,'获取计算价格');
                        this.allTotal = res.data
                        this.resetLoad();
                    }
                    }).catch(()=>{})
                }
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
                    value: '2',
                }
                ],
            }
                listSupplierAll(supplierSearch).then((res)=>{
                    if(res.code==100){
                        this.stuffSupplierList = res.data
                    }
                }).catch((error)=>{
                    this.$message.error(error)
                })
      },
      //药品显示状态
      changStatus(){

      },

      //获取售价合计
      getPriceTotal(item){
          let price;

           if(this.tabPosition=='0'){
               price = ((item.drug.price/item.drug.preparation)*(item.storageStock-item.usedStock-item.reimburseStock)).toFixed(2)
           }else{
               price = ((item.price/item.preparation)*item.inventory).toFixed(2)
           }
        // let price =  item.price * Math.floor(item.inventory/item.preparation-0)
        // if(item.isUnpackSell=='1'){
        //      let retailPrice = item.retailPrice*(item.inventory - (Math.floor(item.inventory/item.preparation-0)*(item.preparation-0)))
        //      price = price + retailPrice
        // }

         return price;
      },
    handleClick(tab, event){
        this.activeName = tab.name
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
     if(this.tabPosition=='0'){
         arr[8] = new BigNumber(Number(this.allTotal.totalSellingPrice)).toFormat(2)
         arr[10] = new BigNumber(Number(this.allTotal.totalPrice)).toFormat(2)
     }else{
          //售价合计
      arr[6] = new BigNumber(Number(this.allTotal.totalSellingPrice)).toFormat(2)

      //进价合计
      arr[7] = new BigNumber(Number(this.allTotal.totalPrice)).toFormat(2)
     }
      // // arr[4] = this.allTotal.numberAmount
      // arr[10] = new BigNumber(Number(this.allTotal.priceTotalAmount)).toFormat(2)
      // arr[11] = new BigNumber(Number(this.allTotal.profitAmount)).toFormat(2)
      console.log(arr)
      return arr
    },
    init() {
         let salesSearch={
              offset: 0,
              limit: 20,
              order: "",
              columnName: "",
              params:[
                  {
                    columnName: "company_id",
                    queryType: "=",
                    value: currentUser.company.id,
                  },
              ]
          }
      this.setLoad();
      getDrug(this.search).then((res) => {
        console.log(res,'看看这个');
          if(res.code=="100"){
             this.dispensingList=res.data.rows
            this.dispensingTotal = res.data.total
            getDrugSalesStat(this.search).then((res)=>{
              if(res.code=="100"){
                  console.log(res.data,'获取计算价格');
                  this.allTotal = res.data
                 this.resetLoad();
              }
            }).catch(()=>{})
          }
      });
    },

    //药品批号获取药品库存
    drugBatchNumber(type,limit,offset){
        if(type==1){
          this.currentPage =1
        }
        this.search = {
          columnName:"",
          limit: type==1?this.pageSize:type==2?limit:limit,
          offset:type==1?this.currentPage - 1:type==2?0:offset,
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
                columnName:"storage_stock",
                queryType:">",
                value:"0"
            },
            {
                columnName:"type",
                queryType:"=",
                value:"1"
            },
          ]
        }

        if(this.queryModel.name!=undefined && this.queryModel.name!=''){

             var pattern2 = new RegExp("[A-Za-z]+");
                if (pattern2.test(this.queryModel.name)) {
                  this.search.params.push(
                    {
                      columnName: "drug.pinyin_code",
                      queryType: "like",
                      value: this.queryModel.name.toUpperCase(),
                    },

                  )
                } else {
                  this.search.params.push(
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
                   this.search.params.push(
                    {
                      columnName: "a.storage_stock-a.used_stock-a.reimburse_stock",
                      queryType: "=",
                      value: 0,
                    })
              }else{
                   this.search.params.push(
                    {
                      columnName: "a.storage_stock-a.used_stock-a.reimburse_stock",
                      queryType: ">",
                      value: 0,
                    })
              }
            }
        if(this.tabPosition=='0'&&this.queryModel.supplier){
            this.search.params.push(
                {
                    columnName: "supplierId.supplier_id",
                    queryType: "=",
                    value: this.queryModel.supplier,
                }
            )
        }
          let salesSearch={
              offset: 0,
              limit: 20,
              order: "",
              columnName: "",
              params:[
                  {
                    columnName: "company_id",
                    queryType: "=",
                    value: currentUser.company.id,
                  },
              ]
          }

        this.setLoad();
     getBatchNumberDrug(this.search).then((res) => {
        console.log(res,'看看这个');
          if(res.code=="100"){
             this.dispensingList=res.data.rows
            this.dispensingTotal = res.data.total
            getDrugSalesStatByNumber(this.search).then((res)=>{
              if(res.code=="100"){
                  console.log(res.data,'获取计算价格');
                  this.allTotal = res.data
                 this.resetLoad();
              }
            }).catch(()=>{})
           }
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
            {
              columnName: "type",
              queryType: "=",
              value: validatenull(this.queryModel.type.value)
                    ? ""
                    : this.queryModel.type.value,
            },
            {
              columnName: "status",
              queryType: "=",
              value: validatenull(this.queryModel.status)
                    ? ""
                    : this.queryModel.status,
            }
          ]
        }

        if(this.queryModel.name!=undefined && this.queryModel.name!=''){

             var pattern2 = new RegExp("[A-Za-z]+");
                if (pattern2.test(this.queryModel.name)) {
                  this.search.params.push(
                    {
                      columnName: "pinyin_code",
                      queryType: "like",
                      value: this.queryModel.name.toUpperCase(),
                    },

                  )
                } else {
                  this.search.params.push(
                    {
                      columnName: "goods_name",
                      queryType: "like",
                      value: this.queryModel.name,
                    },

                  )
                }
          }
          if(this.queryModel.inventory && this.queryModel.inventory!=''){
              if(this.queryModel.inventory=='0'){
                   this.search.params.push(
                    {
                      columnName: "stock.storage_stock-stock.used_stock-stock.reimburse_stock",
                      queryType: "=",
                      value: 0,
                    })
              }else{
                   this.search.params.push(
                    {
                      columnName: "stock.storage_stock-stock.used_stock-stock.reimburse_stock",
                      queryType: ">",
                      value: 0,
                    })
              }
          }

        this.init();
    },
    resetCondition(){
      if(this.activeName=='0'){
        this.queryModel = {
        name:'',
        type:'',
        status:'',
        inventory:'',
        supplier:"",
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
          }
        ],
      }
     if(this.tabPosition=='0'){
         this.drugBatchNumber(1)
     }else{
          this.init()
     }
      }else{
          this.stuffQueryModel = {
                name:'',
                type:'',
                status:'',
                inventory:'',
          }
          this.currentPage = 1;
          this.stuffSearch = {
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
                    columnName: "is_out_sell",
                    queryType: "=",
                    value: '1',
                    }
                ],
          }
           if(this.tabPosition=='0'){
                this.currentPage = 1
                this.stuffBatchNumber(1)
             }else{
                this.stuffInit()
             }
      }
    },
    onSizeChange(val) {
      this.currentPage = 1;

      if(this.activeName=='0'){
         this.search.limit = val;
         this.search.offset = (this.currentPage - 1) * val;
         if(this.tabPosition=='0'){

           this.drugBatchNumber(2,this.search.limit,0)
         }else{
            this.init();
         }
      }else{
        this.stuffSearch.limit = val;
        this.stuffSearch.offset = (this.currentPage - 1) * val;
         if(this.tabPosition=='0'){
           this.stuffBatchNumber(2,this.stuffSearch.limit,0);
         }else{
            this.stuffInit()
         }
      }
    },
    onCurrentChange(val) {

      this.currentPage = val;
       if(this.activeName=='0'){
           this.search.offset = (val - 1) * this.search.limit;
           if(this.tabPosition=='0'){
           this.drugBatchNumber(3,this.search.limit,this.search.offset)
         }else{
            this.init();
         }
      }else{
        this.stuffSearch.offset = (val-1) * this.stuffSearch.limit
          if(this.tabPosition=='0'){
           this.stuffBatchNumber(3,this.stuffSearch.limit,this.stuffSearch.offset);
         }else{
            this.stuffInit()
         }
      }
    },
    initOptions(This) {
      this.tabPosition = '1'
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
      this.onSearch();
    },
    bigNum(num){
      if(num||num==='0'){
        return new BigNumber(num).toFormat(2)
      }else{
        return ''
      }
    }
  },
  updated(){
    if(this.activeName=='0'){
      if(this.$refs.mutipleTable1){
        this.$nextTick(() => {
                  // tableRef是el-table绑定的ref属性值
                  this.$refs.mutipleTable1.doLayout()// 对 Table 进行重新布局
              })
      }
    }else{
      if(this.$refs.mutipleTable2){
        this.$nextTick(() => {
                  // tableRef是el-table绑定的ref属性值
                  this.$refs.mutipleTable2.doLayout()// 对 Table 进行重新布局
              })
      }
    }
  },
  watch: {
     tabPosition:{
          handler(newVal, oldVal) {
            //   console.log(newVal,'获取变换值');
            //  this.dispensingList=[]
            //  this.stuffList=[]
            if(this.activeName=='0'){
              this.dispensingList = null
                console.log(this.tabPosition,'状态')
                //按药品批号获取
                if(this.tabPosition=="0"){
                    this.currentPage = 1
                    this.drugBatchNumber(1)
                }else{
                    this.onSearch()
                }
          }else{
              this.stuffList = null
              if(this.tabPosition == '0'){
                this.currentPage = 1
                  this.stuffBatchNumber(1)
              }else{
                  this.stuffOnSearch()
              }
          }
        },
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
      activeName:{
          handler(newVal,oldVal){
              this.queryModel = {
                name:'',
                type:'',
                status:'',
                inventory:"",
              }

              this.stuffQueryModel = {
                name:'',
                type:'',
                status:'',
                inventory:"",
              }
              this.tabPosition = '1'
              if(newVal=='0'){
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
                        }
                    ],
                    }
                  this.initOptions();
                  this.init();
              }else{
                  this.currentPage = 1;
                    this.stuffSearch = {
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
                            columnName: "is_out_sell",
                            queryType: "=",
                            value: '1',
                            }
                        ],
                    }
                  this.stuffInitOptions()
                  this.stuffInit()
              }
          }
      }
  },
  mounted() {
    this.initOptions();
    this.init();
    //this.drugBatchNumber(1)
    this.onSearch();
    //this.stuffBatchNumber(1)
    // this.stuffOnSearch()
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
