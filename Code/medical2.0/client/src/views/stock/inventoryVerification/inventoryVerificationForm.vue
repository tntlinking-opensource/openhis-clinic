<template>
  <el-dialog :title='dialogProps.title' :before-close='goBack' :visible.sync='dialogProps.visible' :close-on-click-modal='false' :close-on-press-escape="false" width='88%'
    v-loading='loading'>
    <div slot='title' class='dialog-header' style="margin-top:-5px">
      {{ dialogProps.title }}
      <!-- <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon> -->
      <div style="float:right;margin-right:200px;margin-top:-3px">
          <el-button type="primary" @click="exportExcel" icon='el-icon-upload2'>导 出</el-button>
          <el-button type="primary" v-if="this.inventoryVerifications.status=='0'" @click="save">保 存</el-button>
          <el-button type="primary" v-if="this.inventoryVerifications.status=='0'" @click="accomplish">完成盘点</el-button>
            <el-button @click="goBack">返 回</el-button>
      </div>
    </div>
    <div v-if="inventoryVerifications.type=='0'">
    <div>
       <div style="margin-top:-30px">
         <el-form
            :model="queryModel"
            @submit.native.prevent
            label-position="left"
            label-width="70px"
            ref="queryForm"
            :inline-message="true"
          >
          <el-row>
            <el-col :span="24/4">
                <el-form-item label="药品类型" prop="type">
                <el-select
                  v-model="queryModel.type"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择药品类型"
                >
                  <el-option
                    v-for="type in drugType"
                    :key="type.value"
                    :label="type.name"
                    :value="type"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
               <el-col :span="24/4">
                  <el-form-item  label-width="120px" label="药品名称/拼音码" prop="goodsName">
                <el-input
                  v-model="queryModel.goodsName"
                  :clearable="true"
                  placeholder="请输入药品名称/拼音码"
                 style="width:200px"
                ></el-input>
              </el-form-item>
               </el-col>
               <el-col :span="24/4">
                  <el-button
                    type="primary"
                    icon="el-icon-search"
                    @click="onSearch()"
                    :plain="true"
                  >搜索</el-button>
                <el-button
                    type="info"
                    icon="el-icon-refresh-left"
                    @click="reset"
                    :plain="true"
                  >重置</el-button>
               </el-col>
          </el-row>
         </el-form>
       </div>
    </div>
    <br>
        <div class="inventoryVerificationShow" style="margin-top:-30px">
          <el-row class="inventoryMessages">
            <el-col :span="24/4">
              盘点单号：{{this.inventoryVerifications.code}}
            </el-col>
             <el-col :span="24/4">
              盘点人：{{this.inventoryVerifications.createBy}}
            </el-col>
             <el-col :span="24/4">
              创建时间：{{this.inventoryVerifications.createDate}}
            </el-col>
            <el-col :span="24/4">
              <span v-if="inventoryVerifications.allPrice>=0" style="color:	#32CD32">
                盈亏总金额：{{this.inventoryVerifications.allPrice}} 元
              </span>
              <span v-else style="color:	#FF0000">
                盈亏总金额：{{this.inventoryVerifications.allPrice}} 元
              </span>
            </el-col>
          </el-row>
        </div>
        <br>
        <div>
          <el-table
          :data="inventoryVerificationDetail"
          style="width: 100%">
          <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
          </el-table-column>
          <!-- <el-table-column
            prop="drug.code"
            label="药品编码"
            width="width">
          </el-table-column> -->
          <el-table-column
            prop="drug.goodsName"
            label="药品名称"
            width="width">
          </el-table-column>
           <el-table-column
            prop="supplierStock.norms"
            label="规格"
            width="width">
          </el-table-column>
          <!-- <el-table-column
            prop="drug.locationNumber"
            label="货位号">
          </el-table-column> -->
          <el-table-column
            prop="drug.type.name"
            label="类型">
            <!-- <template scope="scope">
              {{scope.row.drug.type.name}}
            </template> -->
          </el-table-column>
          <el-table-column
            prop="drug.factory.name"
            label="生产厂家">
          </el-table-column>
           <el-table-column
            prop="supplierStock.supplierId.name"
            label="供应商">
          </el-table-column>
           <el-table-column
            prop="supplierStock.batchNo"
            label="批号">
          </el-table-column>
           <el-table-column
            prop="supplierStock.endDate"
            label="有效期">
            <template slot-scope="scope">
              {{getEndDate(scope.row.supplierStock.endDate)}}
            </template>
          </el-table-column>
           <el-table-column
            prop="currentInventory"
            label="当前库存数量">
            <template slot-scope="scope">
                              {{
                                Math.floor(
                                  scope.row.currentInventory / scope.row.drug.preparation
                                ) > 0
                                  ? Math.floor(
                                      scope.row.currentInventory /
                                        scope.row.drug.preparation
                                    ) +
                                    scope.row.drug.pack.name +
                                    (scope.row.currentInventory %
                                      scope.row.drug.preparation >
                                    0
                                      ? (scope.row.currentInventory %
                                          scope.row.drug.preparation) +
                                        scope.row.drug.preparationUnit.name
                                      : "")
                                  : scope.row.currentInventory +
                                    scope.row.drug.preparationUnit.name
                              }}
                            </template>
          </el-table-column>
            <el-table-column
              prop="medicinalStorageControl.surplusStock"
              label="可用库存"
              width="100px"
            >
              <template slot-scope="scope">
                {{
                  Math.floor(
                    scope.row.medicinalStorageControl.surplusStock / scope.row.drug.preparation
                  ) > 0
                    ? Math.floor(
                      scope.row.medicinalStorageControl.surplusStock /
                      scope.row.drug.preparation
                    ) +
                    scope.row.drug.pack.name +
                    (scope.row.medicinalStorageControl.surplusStock %
                    scope.row.drug.preparation >
                    0
                      ? (scope.row.medicinalStorageControl.surplusStock %
                        scope.row.drug.preparation) +
                      scope.row.drug.preparationUnit.name
                      : "")
                    : scope.row.medicinalStorageControl.surplusStock +
                    scope.row.drug.preparationUnit.name
                }}
              </template>
            </el-table-column>
           <el-table-column
            prop="checkInventory"
            label="盘点库存"
            width="220px"
            >
           <template slot-scope="scope">
             <div style="display:flex;">
                <el-input-number :disabled="inventoryVerifications.status!='0'" v-model="pack[scope.$index]" style="width:40% !important"  @input="changeprofitAndLoss(scope.row,scope.$index)" placeholder="" :min="0" :controls="false"></el-input-number>{{scope.row.drug.pack.name}}
                <el-input-number :disabled="inventoryVerifications.status!='0'" v-model="preparation[scope.$index]" style="width:40% !important" @input="changeprofitAndLoss(scope.row,scope.$index)" placeholder="" :min="0" :controls="false"></el-input-number>{{scope.row.drug.preparationUnit.name}}
<!--              <el-input :disabled="inventoryVerifications.status!='0'" v-model="preparation[scope.$index]" style="width:80px" @input="changeprofitAndLoss(scope.row,scope.$index)" placeholder="" ></el-input>{{scope.row.drug.preparationUnit.name}}-->
             </div>
           </template>
          </el-table-column>
          <el-table-column
            prop="profitAndLoss"
            label="盘盈盘亏">
              <template slot-scope="scope">
            <span v-if="scope.row.profitAndLoss!=undefined">
               <span v-if="scope.row.profitAndLoss<0" style="color:	#FF0000">
                  {{
                Math.floor(
                  Math.abs(scope.row.profitAndLoss) / scope.row.drug.preparation
                ) > 0
                  ? Math.ceil(
                      scope.row.profitAndLoss /
                        scope.row.drug.preparation
                    ) +
                    scope.row.drug.pack.name +
                    (Math.abs(scope.row.profitAndLoss) %
                      scope.row.drug.preparation >
                    0
                      ? (Math.abs(scope.row.profitAndLoss) %
                          scope.row.drug.preparation) +
                        scope.row.drug.preparationUnit.name
                      : "")
                  : scope.row.profitAndLoss +
                    scope.row.drug.preparationUnit.name
              }}
               </span>
               <span v-else style="color:	#32CD32">
                  {{
                Math.floor(
                  scope.row.profitAndLoss / scope.row.drug.preparation
                ) > 0
                  ? Math.floor(
                      scope.row.profitAndLoss /
                        scope.row.drug.preparation
                    ) +
                    scope.row.drug.pack.name +
                    (scope.row.profitAndLoss %
                      scope.row.drug.preparation >
                    0
                      ? (scope.row.profitAndLoss %
                          scope.row.drug.preparation) +
                        scope.row.drug.preparationUnit.name
                      : "")
                  : scope.row.profitAndLoss +
                    scope.row.drug.preparationUnit.name
              }}
               </span>
            </span>
            </template>
          </el-table-column>


           <el-table-column
            prop="remarks"
            label="备注">
              <template slot-scope="scope">
              <el-input :disabled="inventoryVerifications.status!='0'" v-model="scope.row.remarks" placeholder=""></el-input>
           </template>
          </el-table-column>
          <el-table-column
            prop="profitAndLossPrice"
            label="盈亏金额">
              <template slot-scope="scope">
              <span v-if="scope.row.profitAndLossPrice>=0" style="color:	#32CD32">
                  {{scope.row.profitAndLossPrice}} 元
              </span>
              <span v-else style="color:	#FF0000">
                  {{scope.row.profitAndLossPrice}} 元
              </span>
           </template>
          </el-table-column>
        </el-table>
        </div>
    </div>

    <!-- 盘点材料 -->
    <div v-if="inventoryVerifications.type=='1'">
    <div>
       <div >
         <el-form
            :model="queryModel"
            @submit.native.prevent
            label-position="left"
            label-width="70px"
            ref="queryForm"
            :inline-message="true"
          >
          <el-row>
            <el-col :span="24/3">
                <el-form-item label="材料类型" prop="type">
                <el-select
                  v-model="queryModel.type"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择药品类型"
                >
                  <el-option
                    v-for="type in stuffType"
                    :key="type.value"
                    :label="type.name"
                    :value="type"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
               <el-col :span="24/3">
                  <el-form-item  label-width="120px" label="材料名称/拼音码" prop="name">
                <el-input
                  v-model="queryModel.goodsName"
                  :clearable="true"
                  placeholder="请输入材料名称/拼音码"
                 style="width:200px"
                ></el-input>
              </el-form-item>
               </el-col>
               <el-col :span="24/3">
                  <el-button
                    type="primary"
                    icon="el-icon-search"
                    @click="onSearch()"
                    :plain="true"
                  >搜索</el-button>
                <el-button
                    type="info"
                    icon="el-icon-refresh-left"
                    @click="reset"
                    :plain="true"
                  >重置</el-button>
               </el-col>
          </el-row>
         </el-form>
       </div>
    </div>
    <br>
        <div class="inventoryVerificationShow" style="margin-top:-30px">
          <el-row class="inventoryMessages">
            <el-col :span="24/4">
              盘点单号：{{this.inventoryVerifications.code}}
            </el-col>
             <el-col :span="24/4">
              盘点人：{{this.inventoryVerifications.createBy}}
            </el-col>
             <el-col :span="24/4">
              创建时间：{{this.inventoryVerifications.createDate}}
            </el-col>
             <el-col :span="24/4">
              <span v-if="inventoryVerifications.allPrice>=0" style="color:	#32CD32">
                盈亏金额：{{this.inventoryVerifications.allPrice}} 元
              </span>
              <span v-else style="color:	#FF0000">
                盈亏金额：{{this.inventoryVerifications.allPrice}} 元
              </span>
            </el-col>
          </el-row>
        </div>
        <br>
        <div>
          <el-table
          :data="inventoryVerificationDetail"
          style="width: 100%">
          <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
          </el-table-column>
          <el-table-column
            prop="stuff.code"
            label="材料编码"
            width="width">
          </el-table-column>
          <el-table-column
            prop="stuff.name"
            label="材料名称"
            width="width">
          </el-table-column>
          <el-table-column
            prop="stuff.locationNumber"
            label="货位号">
          </el-table-column>
          <el-table-column
            prop="stuff.type.name"
            label="类型">
            <!-- <template scope="scope">
              {{scope.row.drug.type.name}}
            </template> -->
          </el-table-column>
          <el-table-column
            prop="stuff.specifications"
            label="规格">
             <template slot-scope="scope">
              {{scope.row.stuff.packNumber}}{{scope.row.stuff.minUnit.name}}/{{scope.row.stuff.packUnit.name}}
            </template>
          </el-table-column>
          <el-table-column
            prop="drug.factory.name"
            label="生产厂家">
          </el-table-column>
           <el-table-column
            prop="currentInventory"
            label="当前库存数量">
            <template slot-scope="scope">
                {{
                  Math.floor(
                        scope.row.currentInventory / scope.row.stuff.packNumber
                      ) > 0
                      ? Math.floor(
                          scope.row.currentInventory /
                            scope.row.stuff.packNumber
                        ) +
                        scope.row.stuff.packUnit.name +
                        (scope.row.currentInventory %
                          scope.row.stuff.packNumber >
                        0
                          ? (scope.row.currentInventory %
                              scope.row.stuff.packNumber) +
                            scope.row.stuff.minUnit.name
                          : "")
                      : scope.row.currentInventory +
                        scope.row.stuff.minUnit.name
                }}
              </template>
          </el-table-column>
            <el-table-column
              prop="medicinalStorageControl.surplusStock"
              label="可用库存"
              width="100px"
            >
              <template slot-scope="scope">
                {{
                  Math.floor(
                    scope.row.medicinalStorageControl.surplusStock / scope.row.stuff.packNumber
                  ) > 0
                    ? Math.floor(
                    scope.row.medicinalStorageControl.surplusStock /
                    scope.row.stuff.packNumber
                    ) +
                    scope.row.stuff.packUnit.name +
                    (scope.row.medicinalStorageControl.surplusStock %
                    scope.row.stuff.packNumber >
                    0
                      ? (scope.row.medicinalStorageControl.surplusStock %
                      scope.row.stuff.packNumber) +
                      scope.row.stuff.minUnit.name
                      : "")
                    : scope.row.medicinalStorageControl.surplusStock +
                    scope.row.stuff.minUnit.name
                }}
              </template>
            </el-table-column>
           <el-table-column
            prop="checkInventory"
            label="盘点库存"
            width="220px"
            >
           <template slot-scope="scope">
              <el-input :disabled="inventoryVerifications.status!='0'" v-model="pack[scope.$index]" style="width:80px" @input="changeprofitAndLoss(scope.row,scope.$index)" placeholder=""></el-input>{{scope.row.stuff.packUnit.name}}
              <el-input :disabled="inventoryVerifications.status!='0'" v-model="preparation[scope.$index]" style="width:80px" @input="changeprofitAndLoss(scope.row,scope.$index)" placeholder=""></el-input>{{scope.row.stuff.minUnit.name}}
           </template>
          </el-table-column>
          <el-table-column
            prop="profitAndLoss"
            label="盘盈盘亏">
              <template slot-scope="scope">
            <span v-if="scope.row.profitAndLoss!=undefined">
               <span v-if="scope.row.profitAndLoss<0"  style="color:	#FF0000">
              {{
              Math.floor(
                         Math.abs(scope.row.profitAndLoss) / scope.row.stuff.packNumber
                      ) > 0
                      ? Math.ceil(
                          scope.row.profitAndLoss /
                            scope.row.stuff.packNumber
                        ) +
                        scope.row.stuff.packUnit.name +
                        (Math.abs(scope.row.profitAndLoss) %
                          scope.row.stuff.packNumber >
                        0
                          ? (Math.abs(scope.row.profitAndLoss) %
                              scope.row.stuff.packNumber) +
                            scope.row.stuff.minUnit.name
                          : "")
                      : scope.row.profitAndLoss +
                        scope.row.stuff.minUnit.name
                }}
               </span>
               <span v-else style="color:	#32CD32">
                   {{
                  Math.floor(
                        scope.row.profitAndLoss / scope.row.stuff.packNumber
                      ) > 0
                      ? Math.floor(
                          scope.row.profitAndLoss /
                            scope.row.stuff.packNumber
                        ) +
                        scope.row.stuff.packUnit.name +
                        (scope.row.profitAndLoss %
                          scope.row.stuff.packNumber >
                        0
                          ? (scope.row.profitAndLoss %
                              scope.row.stuff.packNumber) +
                            scope.row.stuff.minUnit.name
                          : "")
                      : scope.row.profitAndLoss +
                        scope.row.stuff.minUnit.name
                }}
               </span>
            </span>
            </template>
          </el-table-column>


           <el-table-column
            prop="remarks"
            label="备注">
              <template slot-scope="scope">
              <el-input :disabled="inventoryVerifications.status!='0'" v-model="scope.row.remarks" placeholder=""></el-input>
           </template>
          </el-table-column>
           <el-table-column
            prop="profitAndLossPrice"
            label="盈亏金额">
              <template slot-scope="scope">
              <span v-if="scope.row.profitAndLossPrice>=0" style="color:	#32CD32">
                  {{scope.row.profitAndLossPrice}} 元
              </span>
              <span v-else style="color:	#FF0000">
                  {{scope.row.profitAndLossPrice}} 元
              </span>
           </template>
          </el-table-column>
        </el-table>
        </div>
    </div>
    <!-- <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' type='primary' :plain='true' @click='onSubmit("inventoryVerificationForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span> -->
      <!-- 分页栏     开始 -->
      <el-row>
        <el-col :span='24'>
          <el-pagination
            background
            @size-change='onSizeChange'
            @current-change='onCurrentChange'
            :current-page='currentPage'
            :page-sizes='[20, 50, 100, inventoryVerificationDetailTotal]'
            :page-size='10'
            layout='total, sizes, prev, pager, next, jumper'
            :total='inventoryVerificationDetailTotal'>
          </el-pagination>
        </el-col>
      </el-row>
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import {getInventoryVerificationDetailByInventoryId,listInventoryVerificationDetailPage,saveAll,exportExcel} from '@/api/stock/inventoryVerificationDetail'
import { saveInventoryVerification,accomplishInventoryVerification } from '@/api/stock/inventoryVerification'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
import { listDictItemAll } from "@/api/sys/dictItem";
export default {
  extends: BaseUI,
  name: 'inventoryVerification-form',
  components: {
    OperationIcon
  },
  data() {
    return {
      currentPage: 1,
      inventoryVerifications:{},   //库存盘点主表存储
      inventoryVerificationDetail:[],  //库存盘点详情
      inventoryVerificationDetailTotal:0,
      preparation:[],  //药品/材料最小单位数量集合
      pack:[],        //药品/最小单位数量集合
      drugType:[],   //药品类型
      stuffType:[],  //材料类型
      bizFormModel: this.initFormModel(),
      queryModel:{
        goodsName: "", // 药品名称
         type: {
          // 药品类型
          value: "",
          name: "",
        },
      },  //搜索框
      tabIndex: '1',
       dialogProps: {
        visible: false,
        action: '',
        title: ''
      },
       searchParms: {
        params: [{columnName: 'company_id', queryType: '=', value: ""}],
        offset: 0,
        limit: 20,
		    columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      saveInformation:[],    //分页数据暂存
      formRules: {
        'code': [
            { required: true, message: '请输入盘点单号', trigger: 'blur' }
        ],
      }
    }
  },
  props: {
    // 权限
    permission: {
      type: Object
    }
  },
    methods: {
      maxValue(row,position){
        let nameOne = 0
        let nameTwo = 0
        if (Math.floor(row.medicinalStorageControl.surplusStock / row.drug.preparation) > 0) {
          nameOne = Math.floor(row.medicinalStorageControl.surplusStock / row.drug.preparation);
          if (row.medicinalStorageControl.surplusStock % row.drug.preparation > 0) {
            nameTwo = row.medicinalStorageControl.surplusStock % row.drug.preparation;
          }
        }else {
          nameOne = row.medicinalStorageControl.surplusStock;
        }
        if (position == 1) {
          return nameOne
        } else {
          return nameTwo
        }
      },
      //获取有效期
      getEndDate(row){
       let date = new Date(row)
        let year = date.getFullYear()
        let month = date.getMonth()
        let day = date.getDate();
        return year+"-"+month+"-"+day
      },

    //导出
    exportExcel(){
      exportExcel(this.inventoryVerifications).then((res)=>{
        console.log(res,'这是个啥');
       //const filename = decodeURI(res.headers['content-disposition'].split(';')[1].split('=')[1]) || '.xls'
        const filename = "库存盘点.xls"
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
        this.$message.error(error)
      })
    },

    //返回
    goBack(){
      this.$emit('save-finished')
            this.dialogProps.visible=false
    },

    //完成盘点
    accomplish(){
      //盘点库存不能小于当前库存数量-可用库存
      if (!this.checkStock()) {
        return;
      }
      Promise.all([
        this.save()
      ]).then(()=>{
        accomplishInventoryVerification(this.inventoryVerifications).then((res)=>{
        if(res.code=='100'){
          this.$message.success("完成盘点")
          this.$emit('save-finished')
            this.dialogProps.visible=false
        }else{
          this.$message.error("完成盘点失败，请联系后台管理员!")
        }
      }).catch((error)=>{
        this.outputError()
      })
      })
    },
      //盘点库存不能小于当前库存数量-可用库存
      checkStock(){
        let arr = [];
        console.log(this.inventoryVerificationDetail,"wotian")
        for(let i=0;i<this.inventoryVerificationDetail.length;i++){
          console.log(this.inventoryVerificationDetail[i].currentInventory,
            this.inventoryVerificationDetail[i].medicinalStorageControl.surplusStock,
            this.inventoryVerificationDetail[i].checkInventory,
            this.inventoryVerificationDetail[i].currentInventory - this.inventoryVerificationDetail[i].medicinalStorageControl.surplusStock)
          let preoccupiedInventory = this.inventoryVerificationDetail[i].currentInventory - this.inventoryVerificationDetail[i].medicinalStorageControl.surplusStock
          if (preoccupiedInventory > this.inventoryVerificationDetail[i].checkInventory) {
            arr.push(this.inventoryVerificationDetail[i].drug.goodsName)
          }
        }
        if(arr.length==0){
          return true
        }
        let str = JSON.stringify(arr).replace(/,/g, ', ')
        this.$message.warning("药品"+str+"存在库存占用，请等待门诊发药完成后在进行盘点")
        return false
      },
    //保存
    save(){
      this.saveInformation[this.currentPage-1]=this.inventoryVerificationDetail
      console.log(this.saveInformation,'保存的数据');
      return saveAll(this.saveInformation).then((res)=>{
        if(res.code=='100'&&res.data=='执行成功'){
          this.$message.success("保存成功！")
            this.$emit('save-finished')
            this.dialogProps.visible=false
        }else{

          this.$$message.error("保存失败，数据异常，请重新尝试或联系管理员!")
        }
      }).catch((error)=>{
        this.outputError(error)
      })
    },

    //重置
    reset(){
      this.queryModel={
         goodsName: "", // 药品名称
         type: {
          // 药品类型
          value: "",
          name: "",
        },
      }
      this.saveInformation=[]
      this.getInventoryVerificationDetail()
    },

    //搜索
    onSearch(){
        this.saveInformation=[]
        this.getInventoryVerificationDetail()
    },

    //盘盈盘亏计算
    changeprofitAndLoss(row,index){
     if(this.inventoryVerifications.type=='0'){
        let figure1=this.pack[index]*row.drug.preparation
        let figure2=this.preparation[index]-0
        let figure3=figure1+figure2
        let num=0
        num = (figure1+figure2)-row.currentInventory
        this.inventoryVerificationDetail[index].profitAndLoss=num
        //计算价格
        let price=0
        let retailPrice=0
        let drugNum=num/row.drug.preparation>0?Math.floor(num/row.drug.preparation):Math.ceil(num/row.drug.preparation)  //计算整数包装
        let residueNum=num-(drugNum*row.drug.preparation)   //计算拆零数量

        price=(drugNum*row.drug.price).toFixed(2)-0
        if(row.drug.isUnpackSell=='1'){
          retailPrice=(residueNum*row.drug.retailPrice).toFixed(2)-0
        }


        this.inventoryVerificationDetail[index].profitAndLossPrice=price+retailPrice
        this.inventoryVerificationDetail[index].checkInventory=figure1+figure2

        //计算获得值后需要赋值
        this.saveInformation[this.currentPage-1]=this.inventoryVerificationDetail
        let allPrice=0
        for (let i = 0; i < this.saveInformation.length; i++) {
          for (let j = 0; j < this.saveInformation[i].length; j++) {
              allPrice+=this.saveInformation[i][j].profitAndLossPrice
          }
        }
        this.inventoryVerifications.allPrice=allPrice.toFixed(2)

     }else{
       console.log("flsakalskflafk");
       let figure1=(this.pack[index]-0)*row.stuff.packNumber
       let figure2=this.preparation[index]-0
       let num=0
       num = (figure1+figure2)-row.currentInventory
       this.inventoryVerificationDetail[index].profitAndLoss=num

        //计算价格
        if(row.stuff.isOutSell=='1'){
        let price=0
        let retailPrice=0
        let drugNum=0
        if(num==0){
          drugNum=0
        } else{
          drugNum=num/row.stuff.packNumber>0?Math.floor(num/row.stuff.packNumber):Math.ceil(num/row.stuff.packNumber)
        }//计算整数包装
        let residueNum=num-(drugNum*row.stuff.packNumber)   //计算拆零数量

        price=(drugNum*row.stuff.packNumber).toFixed(2)-0
        if(row.stuff.isUnpackSell=='1'){
          retailPrice=(residueNum*row.stuff.retailPrice).toFixed(2)-0
        }


        this.inventoryVerificationDetail[index].profitAndLossPrice=price+retailPrice
       this.inventoryVerificationDetail[index].checkInventory=figure1+figure2
       console.log(this.inventoryVerificationDetail[index].profitAndLoss,'第一个值');

        //计算获得值后需要赋值
        this.saveInformation[this.currentPage-1]=this.inventoryVerificationDetail
        let allPrice=0
        for (let i = 0; i < this.saveInformation.length; i++) {
          for (let j = 0; j < this.saveInformation[i].length; j++) {
              allPrice+=this.saveInformation[i][j].profitAndLossPrice
          }
        }
        this.inventoryVerifications.allPrice=allPrice.toFixed(2)
        }

     }
    },

     indexMethod(index){
       return (this.currentPage-1)*this.searchParms.limit+index +1;
    },
     onSizeChange(val) {
      this.saveInformation=[]
      this.currentPage = 1
      this.searchParms.limit = val;
      this.searchParms.offset = (this.currentPage - 1) * val
      this.getInventoryVerificationDetail()
    },
    onCurrentChange(val) {
      let num = JSON.parse(JSON.stringify(this.currentPage))
      this.saveInformation[num-1]=this.inventoryVerificationDetail
      this.searchParms.offset = (val - 1) * this.searchParms.limit
     this.currentPage = val
     this.getInventoryVerificationDetail()
    },

    getInventoryVerificationDetail(){
      this.searchParms.params = [{columnName: 'company_id', queryType: '=', value: this.inventoryVerifications.company.id},{columnName: 'inventory_verification_id', queryType: '=', value: this.inventoryVerifications.id}]
     if(this.inventoryVerifications.type=='0'){
        if(this.queryModel.goodsName!=''&&this.queryModel!=undefined){
         //判断是否输入的是英文
      var pattern = new RegExp("[A-Za-z]+");
      if (pattern.test(this.queryModel.goodsName)) {
        let pinyinCode =
          this.queryModel.goodsName.toUpperCase();
          this.searchParms.params.push({columnName: 'drug.pinyin_code', queryType: 'like', value: pinyinCode})
      } else {
        this.searchParms.params.push({columnName: 'drug.goods_name', queryType: 'like', value: this.queryModel.goodsName})
      }

      }
      if(this.queryModel.type!=undefined&&this.queryModel.type.value!=''){
        this.searchParms.params.push({columnName: 'drug.type', queryType: '=', value: this.queryModel.type.value})
      }
     }else{
        if(this.queryModel.goodsName!=''&&this.queryModel!=undefined){
         //判断是否输入的是英文
      var pattern = new RegExp("[A-Za-z]+");
      if (pattern.test(this.queryModel.goodsName)) {
        let pinyinCode =
          this.queryModel.goodsName.toUpperCase();
          this.searchParms.params.push({columnName: 'stuff.pinyin_code', queryType: 'like', value: pinyinCode})
      } else {
        this.searchParms.params.push({columnName: 'stuff.name', queryType: 'like', value: this.queryModel.goodsName})
      }

      }
      if(this.queryModel.type!=undefined&&this.queryModel.type.value!=''){
        this.searchParms.params.push({columnName: 'stuff.type', queryType: '=', value: this.queryModel.type.value})
      }
     }

     if(this.saveInformation.length>0){
        if(this.saveInformation[this.currentPage-1].length>0){
        this.pack=[]
        this.preparation=[]
        this.inventoryVerificationDetail=this.saveInformation[this.currentPage-1]
        this.inventoryVerificationDetail.forEach(item=>{
            if(item.checkInventory==0){
                this.preparation.push(0)
                this.pack.push(0)
            }else if(item.checkInventory!=0&&this.inventoryVerifications.type=='0'){
              let packNum=Math.floor(item.checkInventory/item.drug.preparation)
              this.pack.push(packNum)
              let preparationNum=item.checkInventory-(packNum*item.drug.preparation)
              this.preparation.push(preparationNum)


            }else{
               let packNum=Math.floor(item.checkInventory/item.stuff.packNumber)
              this.pack.push(packNum)
              let preparationNum=item.checkInventory-(packNum*item.stuff.packNumber)
              this.preparation.push(preparationNum)
            }

          })
      return
      }
     }
       if(this.inventoryVerifications.type=='1'){

        this.searchParms.columnName='stuff.location_number ASC,stuff.id DESC'
      }
      listInventoryVerificationDetailPage(this.searchParms,this.inventoryVerifications.type).then(responseData => {
        if(responseData.code == '100') {
          this.pack=[]
          this.preparation=[]
          this.inventoryVerificationDetail=responseData.data.rows
          this.inventoryVerificationDetailTotal=responseData.data.total
          console.log(this.saveInformation.length,'看看结果');
          if (this.inventoryVerificationDetail !== null){
            if(!this.saveInformation.length>0){
              let number = Math.ceil(responseData.data.total/this.searchParms.limit)
              for (let i = 0; i < number; i++) {
                this.saveInformation.push([])
              }
              this.saveInformation[0].push(responseData.data.rows)
            }

            console.log(this.inventoryVerificationDetail,'看看结果');
            this.inventoryVerificationDetail.forEach(item=>{
              if(item.checkInventory==0){
                this.preparation.push(0)
                this.pack.push(0)
              }else if(item.checkInventory!=0&&this.inventoryVerifications.type=='0'){
                let packNum=Math.floor(item.checkInventory/item.drug.preparation)
                this.pack.push(packNum)
                let preparationNum=item.checkInventory-(packNum*item.drug.preparation)
                this.preparation.push(preparationNum)
              }else{
                let packNum=Math.floor(item.checkInventory/item.stuff.packNumber)
                this.pack.push(packNum)
                let preparationNum=item.checkInventory-(packNum*item.stuff.packNumber)
                this.preparation.push(preparationNum)
              }

            })
          }

        } else {
          this.showMessage(responseData)
        }
      }).catch(error => {
        this.outputError(error)
      })
    },

    getInventoryVerificationDetailInit(row){
      let search={
        params: [],
        offset: 0,
        limit: 20,
		    columnName: '',      // 排序字段名
        order: ''            // 排序
      }
      this.currentPage=1
      this.saveInformation=[]
      search.params = [{columnName: 'company_id', queryType: '=', value: row.company.id},{columnName: 'inventory_verification_id', queryType: '=', value: row.id}]
      if(this.inventoryVerifications.type=='1'){

        search.columnName='stuff.location_number ASC,stuff.id DESC'
      }
      listInventoryVerificationDetailPage(search,this.inventoryVerifications.type).then(responseData => {
        if(responseData.code == '100') {
          this.pack=[]
          this.preparation=[]
          this.inventoryVerificationDetail=responseData.data.rows
          this.inventoryVerificationDetailTotal=responseData.data.total
          console.log(this.inventoryVerificationDetail,'看看结果');
          //获取数据页数
          let currentNum = Math.ceil(responseData.data.total/search.limit)
          for (let i = 0; i < currentNum; i++) {
              this.saveInformation.push([])
          }
          this.saveInformation[0].push(responseData.data.rows)

          this.inventoryVerificationDetail.forEach(item=>{
            if(item.checkInventory==0){
                this.preparation.push(0)
                this.pack.push(0)
            }else if(item.checkInventory!=0&&this.inventoryVerifications.type=='0'){
              let packNum=Math.floor(item.checkInventory/item.drug.preparation)
              this.pack.push(packNum)
              let preparationNum=item.checkInventory-(packNum*item.drug.preparation)
              this.preparation.push(preparationNum)


            }else{
               let packNum=Math.floor(item.checkInventory/item.stuff.packNumber)
              this.pack.push(packNum)
              let preparationNum=item.checkInventory-(packNum*item.stuff.packNumber)
              this.preparation.push(preparationNum)
            }

          })
        } else {
          this.showMessage(responseData)
        }
      }).catch(error => {
        this.outputError(error)
      })
    },
    onSubmit(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave()
        } else {
          return false
        }
      });
    },
    doSave() {
      this.setLoad()
      saveInventoryVerification(this.bizFormModel).then(responseData => {
        if(responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改库存盘点'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['inventoryVerificationForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': currentUser.company.id,
          'name': null,
        },
        'code': '',   // 盘点单号
        'status': '',   // 盘点状态

      }
    },
    initOptions(This) {
    },
    getDrugType(){
       let type_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1004078055755374603",
          },
        ],
      };
       listDictItemAll(type_search).then((responseData) => {
        this.drugType=responseData.data;
      });
    },
    getStuff(){
       let type_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "1004462867645374476",
          },
        ],
      };
       listDictItemAll(type_search).then((responseData) => {
        this.stuffType=responseData.data;
      });

    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewInventoryVerificationDialog', function(inventoryVerification) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '新增盘点'
        this.saveInformation=[]
        this.inventoryVerificationDetail=[]
        this.inventoryVerificationDetailTotal=0
        this.currentPage=1
        this.searchParms={
        params: [{columnName: 'company_id', queryType: '=', value: ""}],
        offset: 0,
        limit: 20,
		    columnName: '',      // 排序字段名
        order: ''            // 排序
      },

        this.queryModel={
        goodsName: "", // 药品名称
         type: {
          // 药品类型
          value: "",
          name: "",
        },
      },  //搜索框
       this.inventoryVerifications={}
        this.inventoryVerifications=inventoryVerification

        this.getInventoryVerificationDetailInit(inventoryVerification)
        //获取药品类型
        if(inventoryVerification.type=='0'){
          this.getDrugType()
        }else{
          this.getStuff()
        }


        this.inventoryVerifications.allPrice=inventoryVerification.allPrice
        // this.bizFormModel = {...this.initFormModel(), ...inventoryVerification}
        // this.initOptions(this.bizFormModel)
        // this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditInventoryVerificationDialog', function(inventoryVerification) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改库存盘点'
        this.bizFormModel = {...this.initFormModel(), ...inventoryVerification}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddInventoryVerificationDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加库存盘点'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyInventoryVerificationDialog', function(inventoryVerification) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加库存盘点'
        this.bizFormModel = {...this.initFormModel(), ...inventoryVerification}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }
}
</script>
<style>
.inventoryVerificationShow{
  border: 15px solid rgb(231, 234, 234);
  background: rgb(231, 234, 234);
}
.inventoryMessages{
  /* background: rgb(231, 234, 234); */
}
</style>
