<template>
  <div>

    <el-row>
      <el-col :span="6">
        <span><b>诊所：</b></span>
        <el-select
          v-model="otherInfo.companyId"
          filterable
          @change="getDrugOrStuff"
          placeholder="请选择诊所"
        >
          <el-option
            v-for="clinic in clinicList"
            :label="clinic.name"
            :value="clinic.id"
            :key="clinic.id"
          ></el-option>
        </el-select>
      </el-col>
      <el-col :span="6">
        <span><b>供应商：</b></span>
        <el-select
          v-model="otherInfo.supplierID"
          filterable
          placeholder="请选择供应商"
        >
          <el-option
            v-for="supplierxz in supplierLists"
            :label="supplierxz.name"
            :value="supplierxz.id"
            :key="supplierxz.id"
          ></el-option>
        </el-select>
      </el-col>

      <el-col :span="6">
        <span v-if="addType == 1"><b>药品名称：</b></span>
        <span v-else><b>材料名称：</b></span>
        <el-popover
          placement="right"
          v-if="!IsOnlyRead"
          width="600"
          trigger="click"
          popper-class="popover"
        >

          <el-table
            :data="newList"
            border
            highlight-current-row
            @row-click="RowClickSurchargeTable"
          >
            <el-table-column
              prop="goodsName"
              :label="addType == 1 ? '药品名称' : '材料名称'"
              v-if="addType == 1"
            >
            </el-table-column>
            <el-table-column
              prop="name"
              :label="addType == 1 ? '药品名称' : '材料名称'"
              v-if="addType == 2"
            >
            </el-table-column>
            <el-table-column
              prop="type.name"
              :label="addType == 1 ? '药品类型' : '材料类型'"
            >
            </el-table-column>
            <el-table-column
              prop="specification"
              label="规格"
              width="100"
              v-if="addType == 1"
            >
            </el-table-column>
            <el-table-column
              prop="price1"
              label="单价"
              width="100"
              v-if="addType == 2"
            >
            </el-table-column>
            <el-table-column
              prop="factory.name"
              label="厂家"
              width="100"
              v-if="addType == 1"
            >
            </el-table-column>
            <el-table-column
              prop="pack.name"
              label="单位"
              width="80"
              v-if="addType == 1"
            >
            </el-table-column>
            <el-table-column
              prop="price1"
              label="零售"
              width="80"
              v-if="addType == 1"
            >
            </el-table-column>
          </el-table>

          <!-- <span v-if="addType == 1">药品名称：</span>
        材料名称： -->
          <el-input
            style="width: 80%"
            slot="reference"
            :disabled="!otherInfo.companyId"
            v-model="searchName"
            :placeholder="drugStuffPlaceholder"
          ></el-input>

        </el-popover>
        <!-- @input="GetSurchargeTable" -->

        <!-- <el-autocomplete
          popper-class="my-autocomplete"
          v-model="goodsNameTemp"
          :fetch-suggestions="queryGoods"
          placeholder="请输入药品名称"
          @select="selectGoods"
        >
          <template slot-scope="{ item }">
            <div>{{ item.goodsName }}</div>
          </template>
        </el-autocomplete> -->
      </el-col>


      <el-col :span="1" style="margin-left:100px">
        <el-button type="primary" @click="importStudentExcel(addType)">批量导入</el-button>
      </el-col>
      <!-- <el-col :span="6">
        <div>
          <el-radio v-model="radio" label="1" border>药品</el-radio>
          <el-radio v-model="radio" label="2" border>材料</el-radio>
        </div>
      </el-col> -->
      <!-- <el-col :span="6">
        <el-link type="primary" @click="AddRow()">添加</el-link>
      </el-col> -->
    </el-row>
    <br>
    <el-row>
      <el-col :span="24">
        <el-table :data="storageDetailTable" style="width: 100%" height="350">
          <el-table-column prop="goodsName" :label="drugStuffName" width="200">
          </el-table-column>
          <!-- <el-table-column
            prop="goodsName"
            label="药品/材料名称"
            width="200"
            v-if="radio == 2"
          >
          </el-table-column> -->
          <el-table-column prop="specification" label="规格"></el-table-column>
          <el-table-column prop="factoryName" label="厂家"></el-table-column>
          <el-table-column label="生产批号">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.batchNumber"
                placeholder="生产批号"
              ></el-input>
            </template>
          </el-table-column>
          <el-table-column label="生产日期" :required="true" type="index" width="110">
            <template slot-scope="scope">
              <el-date-picker
                :id="'a' + scope.$index"
                v-model="scope.row.producedDate"
                placeholder="生产日期"
                type="date"
                :picker-options="expireTimeOption"
              ></el-date-picker>
            </template>
          </el-table-column>
          <el-table-column label="有效日期" :required="true" width="110">
            <template slot-scope="scope">
              <el-date-picker
                v-model="scope.row.expireDate"
                placeholder="有效日期"
                type="date"
              ></el-date-picker>
            </template>
          </el-table-column>
          <el-table-column label="数量" :required="true" width="150" prop="specification">
            <template slot-scope="scope">
              <div class="quantity-box">
                <el-input
                  v-model="scope.row.quantity"
                  type="number"
                  required="true"
                  placeholder="数量"
                  oninput="value=((value.replace(/[^0-9.]/g,'')).replace(/([0-9]+\.[0-9]{2})[0-9]*/,'$1'))"
                ></el-input
                >
                <span>{{ scope.row.pack.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column :required="true" label="进价">
            <template slot-scope="scope">
              <el-input
                v-if="systemParamConfig==1"
                disabled
                v-model="scope.row.outPrice"
                type="number"
                :change="checkPrice()"
                placeholder="进价"
              ></el-input>
              <el-input
                v-if="systemParamConfig==2"
                v-model="scope.row.inPrice"
                type="number"
                :change="checkPrice()"
                placeholder="进价"
              ></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="outPrice" label="零售价"></el-table-column>
          <el-table-column label="总进价" v-if="systemParamConfig==1">
            <template slot-scope="scope"
            >{{ calcTotalOutPrice(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column label="总进价" v-if="systemParamConfig==2">
            <template slot-scope="scope" v-if="systemParamConfig==2"
            >{{ calcTotalInPrice(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column label="总零售价">
            <template slot-scope="scope"
            >{{ calcTotalOutPrice(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column label="" fixed="right" width="100">
            <template slot-scope="scope">
              <el-button
                type="danger"
                size="small"
                @click.native.prevent="
                  deleteRow(scope.$index, storageDetailTable)
                "
              >删除
              </el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="3" :offset="12">品种：{{ CalcBreed() }}</el-col>
      <el-col :span="3">数量：{{ calcTotalNumber() }}</el-col>
      <el-col :span="3" v-if="systemParamConfig==1">总进价：{{ calcAllRetailPrice() }}</el-col>
      <el-col :span="3" v-if="systemParamConfig==2">总进价：{{ calcAllBid() }}</el-col>
      <el-col :span="3">总零售价：{{ calcAllRetailPrice() }}</el-col>
    </el-row>
    <el-row>
      <el-col :span="24 / 4">
        <div class="footer-box">
          <el-input
            v-model="otherInfo.withGoodsNo"
            placeholder="随货单号"
          ></el-input>
        </div>
      </el-col>
      <el-col :span="24 / 4">
        <div class="footer-box">
          <el-input
            v-model="otherInfo.invoiceNumber"
            placeholder="发票号"
          ></el-input>
        </div>
      </el-col>
      <el-col :span="24 / 4">
        <div class="footer-box">
          <el-input v-model="otherInfo.remarks" placeholder="备注"></el-input>
        </div>
      </el-col>
      <el-col :span="24 / 4">
        <div class="footer-box footer-box1">
          <el-button type="primary" @click="saveSupplierStock">确定</el-button>
          <el-button type="" @click="closeDialog()">取消</el-button>
        </div>
      </el-col>
    </el-row>
    <!-- 点击导入后弹出框 -->
    <el-dialog
    :title="addType===1 ?'导入药品':'导入材料'"
    :append-to-body="true"
    :visible.sync="importDialogVisible"
    width="45%"
    :before-close="cancellation">
      <!--limit:最大上传数，on-exceed：超过最大上传数量时的操作  -->
      <el-upload
        class="upload-demo"
        action=""
        :on-change="handleChange"
        :on-remove="handleRemove"
        :on-exceed="handleExceed"
        accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
        :auto-upload="false">
        <el-button icon="el-icon-upload" type="primary">点击上传</el-button><br>
      </el-upload>
      <el-button type="primary" icon="el-icon-download" @click="downloadExcel">模板下载</el-button>
      <el-button @click="cancellation()">取 消</el-button>
    </el-dialog>

    <el-dialog :title="addType===1 ?'药品信息导入错误':'材料信息导入错误'" :visible.sync="dialogVisible" width="36%"
    :append-to-body="true"
               :before-close="mistakeClose">
      <span> 导入成功<span style="white-space:pre;color:#08ff00;font-size: 16px">{{ chengGong }}</span>条&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>
      <span> 导入失败<span style="white-space:pre;color:red;font-size: 16px">{{ shiBai }}</span>条 </span><br>
      <span style="white-space:pre;color:red;font-size: 15px">{{ mistake }}</span>
      <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
  import {listDrugAll} from "@/api/stock/drug";
  import { getSubordinateClinic } from "@/api/stock/clinic";
  import {listSysParamConfigAll} from '@/api/sys/sysParamConfig';
  import {listStuffAll, listStuffPage, listDrug} from "@/api/stock/stuff";
  import {saveSupplierStockList,saveSupplierStockListV1} from "@/api/stock/supplierStock";
  import {getSysParamConfigById} from '@/api/sys/sysParamConfig'
  import {listPage} from "@/api/stock/medicinalStorageControl";
  import {listSupplierPage, getSupplierById, listSupplierAll} from "@/api/stock/supplier";
  import {BigNumber} from "bignumber.js";
  import BaseUI from "@/views/components/baseUI";
  import {getLocalCurrentCompany} from "@/utils/auth";
  import XLSX from 'xlsx'
  import Vue from "vue";
  import fileurl from "@/assets/file/inventory.xlsx";

  Vue.use(XLSX)

  export default {
    extends: BaseUI,
    props: {
      supplierList: null,
      addType: null,
    },

    data() {
      return {
        importDialogVisible: false, // 批量导入弹框变量
        dialogVisible: false, // 导入报错弹框变量
        chengGong: "",
        shiBai: "",
        mistake: "",
        newList: [],
        flag: true,
        //设置失效日期今天之前的日期不可选
        expireTimeOption: {
          // disabledDate(date) {
          //   //disabledDate 文档上：设置禁用状态，参数为当前日期，要求返回 Boolean
          //   return date.getTime() < Date.now() - 24 * 60 * 60 * 1000;
          // },
        },
        searchName: "", // 药品或材料的名称
        // radio: "1", // 1 药品，2材料
        company_id: JSON.parse(window.sessionStorage.getItem("currentUser"))
          .company.id /*诊所ID*/,
        // examine_id: "1005787933775863930" /*通过状态ID*/,
        // examine_value: "supplierStorageExamineStatus_0" /*通过状态Value值*/,
        examine_id: "2087542517503803429" /*待审核状态ID*/,
        examine_value: "supplierStorageExamineStatus_3" /*待审核状态Value值*/,
        title: "新增入库单",
        goodsNameTemp: "",
        drugPreparation: 0,
        stuffPackNum: 0,
        drugStuffName: "",//药品或者材料名称
        drugStuffPlaceholder: "",//对应的placeholder
        goodsInfo: null,
        queryList: [], // 全部药品 或材料
        selectGoodsTemp: null,
        storageDetailTable: [],
        supplierLists: [],//不同的供应商
        clinicList: [], // 诊所列表
        otherInfo: {
          companyId: null,
          supplierID: null,
          breed: 0 /*品种数量*/,
          totalNumber: 0 /*总数量*/,
          allBid: 0 /*总进价*/,
          allRetailPrice: 0 /*总零售价*/,
          withGoodsNo: "" /*供货单号*/,
          invoiceNumber: "" /*发票号*/,
          remarks: "" /*备注*/,
        },
        drugSearch: {
          params: [{columnName: "company_id", queryType: "=", value: ""}],
          offset: 0,
          limit: 100,
          columnName: "", // 排序字段名
          order: "", // 排序
        },
        stuffSearch: {
          params: [{columnName: "company_id", queryType: "=", value: ""}],
          offset: 0,
          limit: 100,
          columnName: "", // 排序字段名
          order: "", // 排序
        },
        supplierSearch: {
          params: [{columnName: "company_id", queryType: "=", value: ""}],
          offset: 0,
          limit: 100,
          columnName: "", // 排序字段名
          order: "", // 排序

        },
        permission: {
          view: false,
          add: false,
          edit: false,
          remove: false,
          export: false
        },
        //系统参数配置
        systemParamConfig: "",
        systemParamConfigSearch: {
          params: []
        },
      };
    },
    watch: {
      addType: function (newVal, oldVal) {
        this.otherInfo.supplierID = null
        this.otherInfo.companyId = null
        this.getClinicList()
        if (newVal == 1) this.getDrugList();
        if (newVal == 2) this.getStuffList();
      },

      searchName: function (newVal, oldVal) {
        var pattern = new RegExp("[A-Za-z]+");
        if (this.flag) {
          if (newVal === "") {
            this.newList = this.queryList;
            console.log(this.queryList, 'quanbu');
          } else if (pattern.test(newVal)) {

            window.setTimeout(() => {
              this.newList = this.queryList.filter((i) => {
                if (i.pinyinCode) {
                  let newSearchName = this.searchName.toUpperCase();
                  return i.pinyinCode.indexOf(newSearchName) > -1;
                }
                if (i.pinyinCode) {
                  return i.pinyinCode.indexOf(newSearchName) > -1;
                }
              });
              this.flag = true;
            }, 1000);
          } else {
            window.setTimeout(() => {
              this.newList = this.queryList.filter((i) => {
                if (i.name) {
                  return i.name.indexOf(this.searchName) > -1;
                }
                if (i.goodsName) {
                  return i.goodsName.indexOf(this.searchName) > -1;
                }
              });
              this.flag = true;
            }, 1000);
          }
        }
      },
      storageDetailTable: {
        handler(newVal, oldVal) {
          if (newVal.length !== 0) {
            newVal.forEach((i, ind) => {
              console.log(i.expireDate, "有效");
              console.log(i.produceDate, "生产");

              if (
                this.$moment(i.expireDate).format("YYYY-MM-DD HH:mm:ss") <
                this.$moment(i.producedDate).format("YYYY-MM-DD HH:mm:ss")
              ) {
                newVal[ind].expireDate = "";
                this.$message({
                  message: "有效期不可小于生产日期",
                  type: "warning",
                });

                // this.$nextTick(() => {
                //   document.getElementById('a'+ind).focus();
                // });
              }
              this.storageDetailTable = newVal;
              // }
            });
          }
        },
        immediate: true,
        deep: true, // 需要添加deep为true即可对obj进行深度监听
      },
    },
  methods: {
      getDrugOrStuff(val) {
        if (!val) return
          console.log(this.otherInfo.companyId);
          if (this.addType == 1) this.getDrugList(false);
          if (this.addType == 2) this.getStuffList(false);
      },
      // 获取诊所列表
      getClinicList() {
        getSubordinateClinic()
          .then(res => {
            console.log(res, 'res21212');
            if (res.code === '100') {
              this.clinicList = res.data
            }
          })
          .catch(err => {
            console.log(err);
          })
      },
      //  药品
      RowClickSurchargeTable(row) {
        console.log(row, "====????");

        this.searchName = row.goodsName ? row.goodsName : row.name;
        var newStorage = {
          goodsID: "",
          goodsName: "",
          specification: "",
          factoryName: "",
          factoryID: "",
          batchNumber: "",
          producedDate: null,
          expireDate: null,
          quantity: "",
          inPrice: "",
          outPrice: null,
          totalInPrice: "",
          totalOutPrice: "",
          pack: {},
          preparationUnit: {},
          preparation: 0,
          packNumber: 0,
        };
        newStorage.goodsID = row.id;
        newStorage.goodsName = row.goodsName ? row.goodsName : row.name;
        newStorage.goodsType = row.goodsType;
        newStorage.specification = row.specification;
        newStorage.factoryName = row.factory.name;
        newStorage.factoryID = row.factory.id;
        newStorage.outPrice = row.price;
        newStorage.pack = row.pack;
        newStorage.preparationUnit = row.preparationUnit
        newStorage.preparation = row.preparation - 0;
        newStorage.packNumber = row.packNumber;
        this.drugPreparation = row.preparation - 0;
        if (row.packNumber != undefined) {
          this.stuffPackNum = row.packNumber;
        } else {
          this.stuffPackNum = 0
        }

        // console.log(this.selectGoodsTemp, "0000333");
        this.selectGoodsTemp = newStorage;
        this.AddRow();
        // this.storageDetailTable.splice(
        //   this.storageDetailTable.length - 1,
        //   1,
        //   newStorage
        // );
      },
      //点击添加按钮
      AddRow() {
        if (this.selectGoodsTemp) {
          var newStorage = {
            goodsID: "",
            goodsName: "",
            specification: "",
            factoryName: "",
            factoryID: "",
            batchNumber: "",
            producedDate: null,
            expireDate: null,
            quantity: "",
            inPrice: "",
            outPrice: null,
            totalInPrice: "",
            totalOutPrice: "",
            pack: {},
            preparationUnit: {},
            preparation: 0,
            packNumber: 0,
          };
          newStorage.goodsID = this.selectGoodsTemp.goodsID;
          newStorage.goodsName = this.selectGoodsTemp.goodsName;
          newStorage.goodsType = this.selectGoodsTemp.goodsType;
          newStorage.specification = this.selectGoodsTemp.specification;
          newStorage.factoryName = this.selectGoodsTemp.factoryName;
          newStorage.factoryID = this.selectGoodsTemp.factoryID;
          newStorage.outPrice = this.selectGoodsTemp.outPrice;
          newStorage.pack = this.selectGoodsTemp.pack;
          newStorage.preparationUnit = this.selectGoodsTemp.preparationUnit
          newStorage.preparation = this.selectGoodsTemp.preparation
          newStorage.packNumber = this.selectGoodsTemp.packNumber
          console.log(this.selectGoodsTemp, "]]]]]");
          console.log(newStorage, '[[[[[?');
          this.storageDetailTable.push(newStorage);
          console.log("里面都是什么" + JSON.stringify(this.storageDetailTable))
        } else {
          this.$message({
            message: "请选择要新增入库的药品或材料",
            type: "warning",
          });
        }
        this.searchName = ""
      },
      // 全部材料
      getStuffList(hasSupplier = true) {
        this.storageDetailTable = []
        this.drugStuffPlaceholder = '输入材料名称/拼音码'
        this.drugStuffName = '材料名称'
        let stuff_search = {
          params: [],
        };
        // 字段对应表上filter条件
        stuff_search.params.push.apply(stuff_search.params, [
          {
            columnName: "company_id",
            queryType: "=",
            value: this.otherInfo.companyId,
          },
          {
            columnName: "is_out_sell",
            queryType: "=",
            value: "1",
          }
        ]);
        // 数据权限: 材料stuff
        this.pushDataPermissions(
          stuff_search.params,
          this.$route.meta.routerId,
          "1004462867645374480"
        );
        this.queryList.splice(0, this.queryList.length);
        if (this.otherInfo.companyId) {
          listStuffAll(stuff_search).then((responseData) => {
            if (responseData.data) {
              responseData.data.forEach((i) => {
                if (!i.price1) i.price1 = i.priceOutSell + "/" + i.packUnit.name;
                if (!i.price) i.price = i.priceOutSell;
                if (!i.pack) i.pack = i.packUnit;
                i.value = i.name;
                i.goodsType = 2;
                if (!i.specification)
                  i.specification =
                    i.packNumber + i.minUnit.name + "/" + i.packUnit.name;
              });
            }
            this.queryList = responseData.data;
            this.newList = this.queryList;
            console.log(this.newList, 'dadadada');
          });
        }
        if(!hasSupplier) return
        this.supplierSearch.params = [
          {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
          {
            columnName: "type",
            queryType: "=",
            value: "2",
          }
        ];
        this.supplierLists = null
        listSupplierAll(this.supplierSearch).then((responseData) => {
          // console.log(responseData);
          if (responseData.code == 100) {
            this.supplierLists = responseData.data
          }
        }).catch(() => {
        })
      },
      // 全部药品
      getDrugList(hasSupplier=true) {
        this.storageDetailTable = []
        this.drugStuffPlaceholder = '输入药品名称/拼音码'
        this.drugStuffName = '药品名称'
        let drug_search = {
          params: [],
        };
        // 字段对应表上filter条件
        drug_search.params.push.apply(drug_search.params, [
          {
            columnName: "company_id",
            queryType: "=",
            value: this.otherInfo.companyId,
          },
        ]);
        // 数据权限: 药品信息drug
        this.pushDataPermissions(
          drug_search.params,
          this.$route.meta.routerId,
          "1004078055755374623"
        );
        this.queryList.splice(0, this.queryList.length);
        if (this.otherInfo.companyId) {
          listDrugAll(drug_search).then((responseData) => {
            if (responseData.data) {
              responseData.data.forEach((i) => {
                i.value = i.goodsName;
                i.goodsType = 1;
                i.price1 = i.price + "/" + i.pack.name;
                i.specification =
                  i.dosis + i.dosisUnit.name + "*" + i.preparation + i.preparationUnit.name + "/" + i.pack.name;
              });
            }
            this.queryList = responseData.data;
            this.newList = this.queryList;
            console.log("111111" + this.newList)

          });
        }
        if(!hasSupplier) return
        this.supplierSearch.params = [
          {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
          {
            columnName: "type",
            queryType: "=",
            value: "1",
          }
        ];
        this.supplierLists = null
        listSupplierAll(this.supplierSearch).then((responseData) => {
          if (responseData.code == 100) {
            console.log(responseData, '呀品牌');
            this.supplierLists = responseData.data
          }
        }).catch(() => {
        })

      },
      // 进价不可为负数
      checkPrice() {
        this.storageDetailTable.forEach((i) => {
          let checkPlan = "" + i.inPrice;
          checkPlan = checkPlan
            .replace(/[^\d.]/g, "") // 清除“数字”和“.”以外的字符
            .replace(/\.{2,}/g, ".") // 只保留第一个. 清除多余的
            .replace(/^\./g, "") // 保证第一个为数字而不是.
            .replace(".", "$#$")
            .replace(/\./g, "")
            .replace("$#$", ".");
          if (checkPlan.indexOf(".") < 0 && checkPlan !== "") {
            // 以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额
            checkPlan = parseFloat(checkPlan) + "";
          } else if (checkPlan.indexOf(".") >= 0) {
            // checkPlan = checkPlan.replace(/^()*(\d+)\.(\d\d).*$/, "$1$2.$3"); // 只能输入两个小数
            checkPlan = checkPlan.replace(/^(\-)*(\d+)\.(\d{4}).*$/, '$1$2.$3'); // 只能输入两个小数
          }
          i.inPrice = checkPlan;
        });
        // let checkPlan = '' + this.numberValidateForm.price
        // checkPlan = checkPlan
        //   .replace(/[^\d.]/g, '') // 清除“数字”和“.”以外的字符
        //   .replace(/\.{2,}/g, '.') // 只保留第一个. 清除多余的
        //   .replace(/^\./g, '') // 保证第一个为数字而不是.
        //   .replace('.', '$#$')
        //   .replace(/\./g, '')
        //   .replace('$#$', '.')
        // if (checkPlan.indexOf('.') < 0 && checkPlan !== '') {
        //   // 以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额
        //   checkPlan = parseFloat(checkPlan) + ''
        // } else if (checkPlan.indexOf('.') >= 0) {
        //   checkPlan = checkPlan
        //     .replace(/^()*(\d+)\.(\d\d).*$/, '$1$2.$3') // 只能输入两个小数
        // }
        // this.numberValidateForm.price = checkPlan
      },

      async queryItem(queryString, callback) {
        await this.queryGoods(queryString, callback);
      },
      // 进价是否为零售价
      GetSysParamConfig() {
        listSysParamConfigAll(this.systemParamConfigSearch).then(responseData => {
          if (responseData.code == 100) {
            if (responseData.data.length >= 1) {
              responseData.data.forEach(data => {
                if (data.configName === "retailPrice") {
                  /*if (data.configValue){
                    this.systemParamConfig = 1
                  }else{
                    this.systemParamConfig = 2
                  }*/
                  this.systemParamConfig = ("true" === data.configValue) ? 1 : 2
                }
              })
              console.log(this.systemParamConfig, "this.systemParamConfig")
            }
          } else {
            this.showMessage(responseData)
          }
        }).catch(error => {
          this.outputError(error)
        })
      },
      /*根据入参触发查询药品*/
      queryGoods(queryString, callback) {
        this.goodsInfo = [];
        if (queryString != "") {
          setTimeout(() => {
            queryString = "%" + queryString + "%";
            this.drugSearch.params = [
              {
                columnName: "company_id",
                queryType: "=",
                value: this.company_id,
              },
            ];
            this.drugSearch.params.push({
              columnName: "goods_name",
              queryType: "like",
              value: queryString,
            });

            if (this.addType === "1") {
              listDrug(this.drugSearch)
                .then((responseData) => {
                  console.log(responseData, '33333');
                  if (responseData.code == 100) {
                    if (responseData.data.rows) {
                      console.log(responseData.data.rows, 111);
                      responseData.data.rows.forEach((element) => {
                        this.goodsInfo.push({
                          id: element.id,
                          goodsName: element.goodsName,
                          value: element.goodsName,
                          goodsType: 1,
                          specification:
                            element.dosis +
                            element.dosisUnit.name +
                            "*" +
                            element.preparationUnit.name,
                          factoryName: element.factory.name,
                          factoryID: element.factory.id,
                          outPrice: element.price,
                          pack: element.pack,
                        });
                      });
                      console.log(this.goodsInfo, "===");
                    } else {
                      return;
                    }
                  } else {
                    // this.$message.error(responseData);
                  }
                })
                .catch((error) => {
                  // this.$message.error(error);
                });

            }

            this.stuffSearch.params = [
              {
                columnName: "company_id",
                queryType: "=",
                value: this.company_id,
              },
            ];
            this.stuffSearch.params.push({
              columnName: "name",
              queryType: "like",
              value: queryString,
            });

            if (this.addType === "2") {
              listStuffPage(this.stuffSearch).then((responseData) => {
                if (responseData.code == 100) {
                  console.log(responseData.data, "？？？===");
                  if (responseData.data.rows) {
                    responseData.data.rows.forEach((element) => {
                      console.log(elemetn, '看看');
                      this.goodsInfo.push({
                        id: element.id,
                        goodsName: element.name,
                        value: element.name,
                        goodsType: 2,
                        specification: element.specifications,
                        factoryName: element.factory.name,
                        factoryID: element.factory.id,
                        outPrice: element.priceOutSell,
                        pack: element.pack,
                      });
                    });
                    console.log(this.goodsInfo, "===");
                  } else {
                    return;
                  }
                } else {
                  this.$message.error(responseData);
                }
              });
            }

            callback(this.goodsInfo);
          }, 1000);
        } else {
          callback(this.goodsInfo);
        }
      },
      //选中药品或材料
      selectGoods(item) {
        console.log(item, "////");
        this.selectGoodsTemp = item;
      },
      // 每条里的模糊查询后的选中
      selectGoodsItem(item) {
        this.selectGoodsTemp = item;
        var newStorage = {
          goodsID: "",
          goodsName: "",
          specification: "",
          factoryName: "",
          factoryID: "",
          batchNumber: "",
          producedDate: null,
          expireDate: null,
          quantity: "",
          inPrice: "",
          outPrice: null,
          totalInPrice: "",
          totalOutPrice: "",
          pack: {},
        };
        newStorage.goodsID = this.selectGoodsTemp.id;
        newStorage.goodsName = this.selectGoodsTemp.goodsName;
        newStorage.goodsType = this.selectGoodsTemp.goodsType;
        newStorage.specification = this.selectGoodsTemp.specification;
        newStorage.factoryName = this.selectGoodsTemp.factoryName;
        newStorage.factoryID = this.selectGoodsTemp.factoryID;
        newStorage.outPrice = this.selectGoodsTemp.outPrice;
        newStorage.pack = this.selectGoodsTemp.pack;
        console.log(this.selectGoodsTemp, "0000333");
        this.storageDetailTable.splice(
          this.storageDetailTable.length - 1,
          1,
          newStorage
        );
      },
      //保存药品或材料
      saveSupplierStock() {
        this.flag = true
        if (!this.otherInfo.companyId) {
          this.$message({message: "请选择诊所", type: "warning"});
          return;
        }
        if (!this.otherInfo.supplierID) {
          this.$message({message: "请选择供应商", type: "warning"});
          return;
        }

        if (this.storageDetailTable.length == 0) {
          this.$message({message: "请录入入库明细信息", type: "warning"});
          return;
        }

        var day = new Date();
        var saveInfo = {
          supplierStorage: {
            company: {
              id: this.otherInfo.companyId,
            },
            supplier: {
              id: this.otherInfo.supplierID,
            },
            code: "",
            breed: this.otherInfo.breed,
            number: this.stuffPackNum == 0 ? this.otherInfo.totalNumber * this.drugPreparation : this.otherInfo.totalNumber * this.stuffPackNum,
            allBid: this.otherInfo.allBid,
            allRetailPrice: this.otherInfo.allRetailPrice,
            cancelDate: null,
            examine: {
              id: this.examine_id,
              value: this.examine_value,
            },
            examineDate: this.$moment(day).format("YYYY-MM-DD HH:mm:ss"),
            withGoodsNo: this.otherInfo.withGoodsNo,
            invoiceNumber: this.otherInfo.invoiceNumber,
            remarks: this.otherInfo.remarks,
            type: this.addType + '',
          },
          supplierStockList: [],
        };
        console.log(this.storageDetailTable, 'lipu');
        this.storageDetailTable.forEach((element) => {
          if (this.systemParamConfig == 1) {
            element.inPrice = element.outPrice
          }
          try {
            if (element.quantity === "" || element.quantity == 0) {
              throw "数量";
            }
            if (element.inPrice === "") {
              throw "进价";
            }
            if (
              this.$moment(element.expireDate).format("YYYY-MM-DD HH:mm:ss") <
              this.$moment(element.producedDate).format("YYYY-MM-DD HH:mm:ss")
            ) {
              throw "时间";
            }
            if (
              !element.expireDate
            ) {
              throw "有效期";
            }
            var detail = {
              company: {
                // 诊所id
                id: this.otherInfo.companyId,
              },
              supplierId: {
                // 供应商
                id: this.otherInfo.supplierID,
              },
              drug: {
                // 药品名称
                id: element.goodsType === 1 ? element.goodsID : null,
                goodsName: element.goodsType === 1 ? element.goodsName : null,
              },
              supplierStorage: null,
              stuff: {
                // 材料名称
                id: element.goodsType === 2 ? element.goodsID : null,
                name: element.goodsType === 2 ? element.goodsName : null,
              },
              norms: element.specification, // 规格
              factory: {
                id: element.factoryID,
                name: element.factoryName,
              }, // 厂家
              batchNo: element.batchNumber, // 批号
              produceDate:
                element.producedDate !== ""
                  ? this.$moment(element.producedDate).format(
                  "YYYY-MM-DD HH:mm:ss"
                  )
                  : null, // 生产日期
              endDate:
                element.expireDate !== ""
                  ? this.$moment(element.expireDate).format("YYYY-MM-DD HH:mm:ss")
                  : null, // 有效期
              number: element.goodsType == 1 ? element.quantity * element.preparation : element.quantity * element.packNumber, // 数量
              bid: element.inPrice, // 进价
              retailPrice: element.outPrice, // 零售价
              allBid: element.totalInPrice, // 总进价
              allRetailPrice: element.totalOutPrice, // 总零售价
              remarks: "", // 备注信息
              leastBid: element.goodsType === 1 ? Number((element.inPrice / element.preparation).toFixed(4)) : Number((element.inPrice / element.packNumber).toFixed(4))
            };
            saveInfo.supplierStockList.push(detail);
          } catch (e) {
            console.log(e,'222222eeee');
            this.flag = e;
          }
        });
        console.log(this.flag, '报错');
        if (this.systemParamConfig == 1) {
          this.inPrice = this.outPrice
          this.totalInPrice = this.totalOutPrice
          saveInfo.supplierStockList.allBid = saveInfo.supplierStockList.allRetailPrice
          console.log(saveInfo.supplierStockList.allBid + "==进价")
          console.log(saveInfo.supplierStockList.allRetailPrice + "==进价价")
        }
        if (this.flag === "数量") {
          this.$message({message: "入库数量不可为空", type: "warning"});
          this.flag = "";
          return;
        } else if (this.flag === "进价") {
          this.$message({message: "进价不可为空", type: "warning"});
          this.flag = "";
          return;
        } else if (this.flag === "时间") {
          this.flag = "";
          this.$message({
            message: "有效期不可小于生产日期",
            type: "warning",
          });
          return;
        } else if (this.flag === "有效期") {
          this.$message({
            message: "请输入有效期",
            type: "warning",
          });
          return;
        } else {
          console.log(saveInfo, '看info');
          saveSupplierStockListV1(saveInfo).then((responseData) => {
            if (responseData.code == 100) {
              this.$message({message: "保存成功", type: "success"});
              this.closeDialog();
            } else {
              this.$message({message: responseData.msg, type: "error"});
            }
          });
        }
      },
      //关闭弹框
      closeDialog() {
        console.log("1111====");
        this.storageDetailTable = [];

        this.otherInfo = {
          supplierID: null,
          breed: 0 /*品种数量*/,
          totalNumber: 0 /*总数量*/,
          allBid: 0 /*总进价*/,
          allRetailPrice: 0 /*总零售价*/,
          withGoodsNo: "" /*供货单号*/,
          invoiceNumber: "" /*发票号*/,
          remarks: "" /*备注*/,
        };
        this.selectGoodsTemp = null;

        this.searchName = "";
        this.$emit("fatherMethod");
      },

      // 关闭导入报错
      mistakeClose(done) {
        this.$confirm('确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {
          });
      },

      deleteRow(index, tb) {
        this.storageDetailTable.splice(index, 1);
      },
      calcTotalInPrice(row) {
        var number = Number(row.quantity);
        if (isNaN(number)) return;
        var inPrice = Number(row.inPrice);
        if (isNaN(inPrice)) return;
        var result = number * inPrice;
        row.totalInPrice = result;
        return result;
      },
      calcTotalOutPrice(row) {
        var number = Number(row.quantity);
        if (isNaN(number)) return;
        var outPrice = Number(row.outPrice);
        if (isNaN(outPrice)) return;
        var result = number * outPrice;
        result = result.toFixed(2)
        row.totalOutPrice = result;
        return result;
      },
      CalcBreed() {
        if (this.storageDetailTable) {
          this.otherInfo.breed = this.storageDetailTable.length;
          return this.otherInfo.breed;
        }
        return 0;
      },
      calcTotalNumber() {
        var totalNumber = 0;
        if (this.storageDetailTable) {
          this.storageDetailTable.forEach((element) => {
            var number = Number(element.quantity);
            if (isNaN(number)) totalNumber += 0;
            else totalNumber += number;
          });
        }
        this.otherInfo.totalNumber = totalNumber;
        return totalNumber;
      },
      calcAllBid() {
        var allBid = 0;
        if (this.storageDetailTable) {
          this.storageDetailTable.forEach((element) => {
            var number = Number(element.totalInPrice);
            if (isNaN(number)) allBid += 0;
            else allBid += number;
          });
        }
        this.otherInfo.allBid = allBid;
        return allBid;
      },
      calcAllRetailPrice() {
        var allRetailPrice = 0;
        if (this.storageDetailTable) {
          this.storageDetailTable.forEach((element) => {
            var number = Number(element.totalOutPrice);
            if (isNaN(number)) allRetailPrice += 0;
            else allRetailPrice += number;
          });
        }
        this.otherInfo.allRetailPrice = allRetailPrice;
        return allRetailPrice;
      },
      testSelectGoods(data) {
        console.log("parent");
        console.log(data);
      },
      // 批量导入按钮点击事件
      importStudentExcel() {
        this.importDialogVisible = true;
        console.log(this.importDialogVisible)
      },

      // 关闭导入窗口
      cancellation() {
        this.importDialogVisible = false;
      },


      //上传文件时处理方法
      handleChange(file, fileList) {
        this.fileTemp = file.raw;
        if (this.fileTemp) {
          if ((this.fileTemp.type == 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')
            || (this.fileTemp.type == 'application/vnd.ms-excel')) {
            this.importfxx(this.fileTemp);
          } else {
            this.$message({
              type: 'warning',
              message: '附件格式错误，请删除后重新上传！'
            })
          }
        } else {
          this.$message({
            type: 'warning',
            message: '请上传附件！'
          })
        }
      },
      //超出最大上传文件数量时的处理方法
      handleExceed() {
        this.$message({
          type: 'warning',
          message: '超出最大上传文件数量的限制！'
        })
        return;
      },
      //移除文件的操作方法
      handleRemove(file, fileList) {
        this.fileTemp = null
      },

      async importfxx(obj) {
        let _this = this;
        let inputDOM = this.$refs.inputer;
        // 通过DOM取文件数据

        this.file = event.currentTarget.files[0];

        var rABS = false; //是否将文件读取为二进制字符串
        var f = this.file;
        var newList = this.newList;
        var storageDetailTable = this.storageDetailTable;
        let dr = 0;
        let wdr = 0;
        let a = 0;
        let cw = "";


        var reader = new FileReader();
        //if (!FileReader.prototype.readAsBinaryString) {
        FileReader.prototype.readAsBinaryString = function (f) {
          var binary = "";
          var rABS = false; //是否将文件读取为二进制字符串
          var pt = this;
          var wb; //读取完成的数据
          var outdata;
          var reader = new FileReader();
          reader.onload = function (e) {
            var bytes = new Uint8Array(reader.result);
            var length = bytes.byteLength;
            for (var i = 0; i < length; i++) {
              binary += String.fromCharCode(bytes[i]);
            }
            //此处引入，用于解析excel
            var XLSX = require("xlsx");
            if (rABS) {
              wb = XLSX.read(btoa(fixdata(binary)), {
                //手动转化
                type: "base64"
              });
            } else {
              wb = XLSX.read(binary, {
                type: "binary"
              });
            }
            outdata = XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]]);
            console.log("看看解析的啥呢么" + JSON.stringify(outdata))
            //outdata就是读取的数据（不包含标题行即表头，表头会作为对象的下标）
            //此处可对数据进行处理
            // let arr = [];
            // outdata.map(v => {
            //     let obj = {}
            //     obj.code = v['Code']
            //     obj.name = v['Name']
            //     obj.pro = v['province']
            //     obj.cit = v['city']
            //     obj.dis = v['district']
            //     arr.push(obj)
            // });
            // _this.da=arr;
            // _this.dalen=arr.length;
            for (let i = 0; i < outdata.length; i++) {
              for (let j = 0; j < newList.length; j++) {
                if (outdata[i].药品材料名称 == newList[j].goodsName && outdata[i].规格 == newList[j].specification && outdata[i].售价 == newList[j].price) {
                  newList[j].batchNumber = outdata[i].生产批号;
                  newList[j].producedDate = outdata[i].生产日期;
                  newList[j].expireDate = outdata[i].过期日期;
                  newList[j].quantity = outdata[i].入库数量;
                  newList[j].inPrice = outdata[i].进价;
                  newList[j].outPrice = newList[j].price;
                  storageDetailTable.push(newList[j]);
                  console.log(newList[j])
                  dr += 1;
                  a = 1;
                }
              }
              if (a !== 1){
                wdr += 1;
                cw += "表格第"+(i+1)+"行["+outdata[i].药品材料名称+"]信息异常，请核对后重新导入\n";
              }
              a = 0;
            }
            return this.storageDetailTable = storageDetailTable;
          };
          reader.readAsArrayBuffer(f);
        }
        if (rABS) {
          reader.readAsArrayBuffer(f);
        } else {
          reader.readAsBinaryString(f);
        }
        this.cancellation();

        setTimeout(() => {
          if (wdr!==0){
            console.log("到这没有")
            this.chengGong = dr
            this.shiBai = wdr
            this.mistake = cw
            this.dialogVisible = true
          }
        }, 3000);
      },

      // 下载模板
      downloadExcel() {
        let a = document.createElement('a');
        let evt = document.createEvent('MouseEvents');
        a.download = this.addType === 1 ? '药品导入信息' : '材料导入信息';
        a.href = fileurl;
        evt.initEvent('click', true, true);
        a.dispatchEvent(evt);
        window.URL.revokeObjectURL(a.href);
      },
    },
    mounted() {
      this.GetSysParamConfig();
      this.getClinicList()
      if (this.addType == 1) this.getDrugList();
      if (this.addType == 2) this.getStuffList();


    },
  };
</script>
<style scoped>
  .quantity-box {
    display: flex;

  }

  .footer-box {
    padding: 30px 20px 0 20px;
    margin-left: -20px;
  }

  .footer-box1 {
    display: flex;
    justify-content: right;
    align-items: center;
  }

  .popover {
    max-height: 300px !important;
  }
</style>
<style>
  .popover {
    max-height: 300px !important;
  }
</style>
