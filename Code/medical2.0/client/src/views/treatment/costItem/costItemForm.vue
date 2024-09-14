<template>
  <el-dialog
    :title="dialogProps.title"
    :visible.sync="dialogProps.visible"
    :close-on-click-modal="false"
    width="60%"
    @open="onDialogOpen()"
    v-loading="loading"
    @close="close"
  >
    <div slot="title" class="dialog-header">
      {{ dialogProps.title }}
      <OperationIcon
        v-show="dialogProps.action == 'view' && permission.edit"
        type="primary"
        text="编辑"
        placement="top-start"
        icon-name="el-icon-edit"
        @click="switchEdit"
      ></OperationIcon>
    </div>
    
    <el-form
      :model="bizFormModel"
      :rules="formRules"
      ref="costItemForm"
      style="marginTop: 10px"
      label-width="120px"
      label-position="right"
      class="edit-form"
    >
    <el-row>
    <el-col :span="11">
      <div class="tab-item" v-show="tabIndex == '1'">
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="项目编码" prop="itemCode">
              <el-input
                disabled
                v-model="bizFormModel.itemCode"
                :maxlength="64"
                placeholder="保存后自动生成"
                autofocus
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="国家编码" prop="itemCountryCode">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.itemCountryCode"
                :maxlength="64"
                :placeholder="dialogProps.action == 'view' ? '' : '请输入国家编码'"
                autofocus
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="项目名称" prop="itemName">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.itemName"
                :maxlength="64"
                @input="pinyinInput"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入项目名称'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="拼音码" prop="pinyinCode">
              <el-input
                disabled=true
                v-model="bizFormModel.pinyinCode"
                :maxlength="64"
                @input="pinyinInput"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入拼音码'
                "
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="项目类型" prop="isPackage">
                 <el-radio :disabled='dialogProps.action == "view"' v-model="bizFormModel.isPackage" label="0">单项项目</el-radio>
                 <el-radio :disabled='dialogProps.action == "view"' v-model="bizFormModel.isPackage" label="1">组合项目</el-radio>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="项目类别" prop="itemType.value">
              <el-input
                v-if="dialogProps.action == 'view'"
                :disabled="true"
                v-model="bizFormModel.itemType.name"
              ></el-input>
              <el-select
                v-else
                v-model="bizFormModel.itemType"
                value-key="value"
                filterable
                clearable
                placeholder="请选择项目类别"
                @clear="
                  bizFormModel.itemType = {
                    value: null,
                    name: null,
                  }
                "
              >
                <el-option
                  v-for="itemType in itemType_List"
                  :key="itemType.value"
                  :label="itemType.name"
                  :value="itemType"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="单位" prop="unit.value">
              <el-input
                v-if="dialogProps.action == 'view'"
                :disabled="true"
                v-model="bizFormModel.unit.name"
              ></el-input>
              <el-select
                v-else
                v-model="bizFormModel.unit"
                value-key="value"
                filterable
                clearable
                placeholder="请选择单位"
                @clear="
                  bizFormModel.unit = {
                    value: null,
                    name: null,
                  }
                "
              >
                <el-option
                  v-for="unit in unit_List"
                  :key="unit.value"
                  :label="unit.name"
                  :value="unit"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="成本价" prop="costPrice">
              <el-input
                v-if="dialogProps.action == 'view'"
                :disabled="true"
                v-model="bizFormModel.costPrice"
              ></el-input>
              <number-input
                v-else
                :disabled="salesFlage"
                v-model="bizFormModel.costPrice"
                currency="CNY"
                :precision="2"
              ></number-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="销售价" prop="salePrice">
              <el-input
                v-if="dialogProps.action == 'view'"
                :disabled="true"
                v-model="bizFormModel.salePrice"
              ></el-input>
              <number-input
                v-else
                :disabled="salesFlage"
                v-model="bizFormModel.salePrice"
                currency="CNY"
                :precision="2"
              ></number-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="是否启用" prop="isUse">
              <el-switch
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.isUse"
                active-color="#13ce66"
                inactive-color="#dbdfe6"
                active-value="1"
                inactive-value="0"
              ></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-form-item label="备注信息" prop="remarks">
              <el-input
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.remarks"
                type="textarea"
                :maxlength="255"
                :placeholder="
                  dialogProps.action == 'view' ? '' : '请输入备注信息'
                "
                clearable
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="是否执行划扣" prop="isDeduct">
              <el-switch
                :disabled="dialogProps.action == 'view'"
                v-model="bizFormModel.isDeduct"
                active-color="#13ce66"
                inactive-color="#dbdfe6"
                active-value="1"
                inactive-value="0"
              ></el-switch>
            </el-form-item>
          </el-col>
        </el-row> -->
      </div>
    </el-col>
    <el-col :span="2">
    <div class="verticalBar"></div>
    </el-col>
    <div v-if="objectType=='1'">
       <el-col :span="11">
      <el-row>
        <el-col :span="24/1">
            <el-form-item label="诊疗子项目名称" prop="itemNames">
                <el-select
                :disabled="dialogProps.action == 'view'"
                   multiple
                  v-model="subjectName"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择子项目"
                  @change="changeMessage"
                >
                  <el-option
                    v-for="itemNames in itemName_List"
                    :key="itemNames.id"
                    :label="itemNames.itemName"
                    :value="itemNames.itemName"
                  ></el-option>
                </el-select>
              </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
             <el-table
                :data="subproject"
                border
                style="width: 100%"
                max-height="250">
                <el-table-column
                  fixed
                  prop="itemCode"
                  label="项目编码"
                  width="100">
                </el-table-column>
                <el-table-column
                  prop="itemName"
                  label="子项目名"
                  width="120">
                </el-table-column>
                <el-table-column
                  prop="quantity"
                  label="数量"
                  width="120">
                  <template slot-scope="scope">
                  <el-input-number :disabled="dialogProps.action == 'view'" size="mini" v-model="subproject[scope.$index].quantity" @change="handleChange" :min="0"   ></el-input-number>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="salePrice"
                  label="销售价格"
                 
                  width="160">
                   <template slot-scope="scope">
                      {{ fun(scope.row.salePrice) }}
                    </template>
                </el-table-column>
               
                <el-table-column
                  fixed="right"
                  label="操作"
                  width="50">
                  <template slot-scope="scope">
                    <el-button
                      :disabled="dialogProps.action == 'view'"
                      @click.native.prevent="deleteRow(scope.$index, tableData)"
                      type="text"
                      size="small">
                      移除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
        </el-col>
      </el-row>
    </el-col>
    </div>
    <div v-else>
      <el-col :span="11">
      <el-form ref="form" :model="examine" label-width="80px">
        <el-form-item label="参考单位">
          <el-input  label="请输入参考单位" :disabled="dialogProps.action == 'view'" v-model="examine.referenceUnit"></el-input>
        </el-form-item>
        <el-form-item label="参考值">
          <el-input label="请输入参考值，换行添加多组" :disabled="dialogProps.action == 'view'" type="textarea" v-model="examine.referenceValue"></el-input>
        </el-form-item>
      </el-form>
      </el-col>
    </div>
    </el-row>
    </el-form>
   
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' type='primary' :disabled="flage" :plain='true' @click='onSubmit("costItemForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>

import { validatenull } from "@/utils/validate";
import { listDictItemAll } from "@/api/sys/dictItem";
import { saveCostItem } from "@/api/treatment/costItem";
import BaseUI from "@/views/components/baseUI";
import OperationIcon from "@/components/OperationIcon";

let pinyin = require('js-pinyin');
export default {
  
  extends: BaseUI,
  name: "costItem-form",
  components: {
    OperationIcon,
  },
  data() {
    const salePrice = (rule, val, callback) => {
      if (val <= 0 || val === "") {
        callback("请输入销售价格");
      } else {
        callback("请输入销售价格");
      }
    };
    return {
      user: JSON.parse(sessionStorage.getItem("currentUser")),
      bizFormModel: this.initFormModel(this.user),
      tabIndex: "1",
      subjectName:[],//子项目名称封装
      itemType_List: [], // 项目类别
      itemName_List:[],//子项目名称
      unit_List: [], // 单位
      flage:false,
      subproject:[],//子项目表格集合
      examine:{
        referenceUnit:"",
        referenceValue:""
      },
      arr:[],
      salesFlage:false,//销售价格是否能修改
      sale:0,//暂存子项目销售价格
      cost:0,//暂存子项目市场价
      dto:{
        costItem:{},
        costItemPackage:[],
      },//封装对象
      dialogProps: {
        visible: false,
        action: '',
        title: '',
        
      },
       radio:"0",   //是否为打包项目
       objectType:"1",
      formRules: {
        itemName: [
          { required: true, message: "请输入项目名称", trigger: "blur" },
        ],
        salePrice: [
          { required: salePrice, message: "请输入销售价格", trigger: "blur" },
        ],
        "itemType.value":[
          { required: salePrice, message: "请选择项目类别", trigger: "change" }
        ],
        "unit.value":[
          { required: salePrice, message: "请选择项目单位", trigger: "change" }
        ],
        isPackage:[
          { required: true, message: '请选择项目类型', trigger: 'change' }
        ]
      },
    };
  },
  props: {
    // 权限
    permission: {
      type: Object,
    },
   
  },
  methods: {
    onSubmit(formName) {
      console.log(this.subproject);
      this.flage=true
      this.$refs[formName].validate(valid => {
        if (valid) {
          if(this.bizFormModel.isPackage=="0"&&this.bizFormModel.itemType.value=="treatmentItemType_0"){
            this.dto.costItem=this.bizFormModel
            this.dto.costItemPackage=[]
            this.dto.costItemPackage.push(this.examine)
          }else{
            this.dto.costItem=this.bizFormModel
            this.dto.costItemPackage=this.subproject
          }
          
          this.doSave();
        } else {
          this.flage=false
          this.salesFlage=false
          return false
        }
      });
     
    },
    doSave() {
      this.setLoad()
      console.log(this.dto,"所有");
     // return
      saveCostItem(this.dto).then(responseData => {
        this.flage=false
        this.salesFlage=false
         if(responseData.code == 100) {
           this.dialogProps.visible = false
           this.$emit('save-finished',"1")
         } else {
           this.showMessage(responseData)
         }
         this.resetLoad()
      }).catch(error => {
        this.flage=false
        this.outputError(error)
      })
    },
    switchEdit() {
      this.dialogProps.action = "edit";
      this.dialogProps.title = "修改诊疗项目";
      this.initOptions(this.bizFormModel);
    },
    onDialogClose() {
      this.dialogProps.visible = false;
      this.salesFlage=false
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["costItemForm"].clearValidate();
      });
    },
    initFormModel(This) {
      return {
        company: {
          // 诊所id
          id: This ? This.company.id : null,
          name: null,
        },
        itemCode: "", // 项目编码
        itemName: "", // 项目名称
        itemType: {
          // 项目类别
          value: null,
          name: null,
        },
        unit: {
          // 单位
          value: null,
          name: null,
        },
        costPrice: 0, // 成本价
        salePrice: 0, // 销售价
        isUse: "1", // 状态
        remarks: "", // 备注信息
        isDeduct: "0", // 是否执行划扣
      };
    },
    initOptions(This) {
      let itemType_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "998465736089977631",
          },
        ],
      };
      // 字段对应表上filter条件
      itemType_search.params.push.apply(itemType_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        itemType_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.itemType_List.splice(0, this.itemType_List.length);
      listDictItemAll(itemType_search).then((responseData) => {
        this.itemType_List = responseData.data;
      });
      let unit_search = {
        params: [
          {
            columnName: "dict_type_id",
            queryType: "=",
            value: "999976636865404934",
          },
        ],
      };
      // 字段对应表上filter条件
      unit_search.params.push.apply(unit_search.params, []);
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(
        unit_search.params,
        this.$route.meta.routerId,
        "4005"
      );
      this.unit_List.splice(0, this.unit_List.length);
      listDictItemAll(unit_search).then((responseData) => {
        this.unit_List = responseData.data;
      });
    },

    //子项目移除
    deleteRow(index, rows) {
       //计算销售价
        this.bizFormModel.salePrice-=(this.subproject[index].quantity*this.subproject[index].salePrice)
        this.bizFormModel.costPrice-=(this.subproject[index].quantity*this.subproject[index].costPrice)
        this.sale-=(this.subproject[index].quantity*this.subproject[index].salePrice)
        this.cost-=(this.subproject[index].quantity*this.subproject[index].costPrice)

        this.subproject[index].quantity=0;
        this.subproject.splice(index, 1);
        this.subjectName.splice(index,1);
        if(this.subproject.length!=0){
          this.salesFlage=true
        }else{
          this.salesFlage=false
        }
      
       
      },
    assign(row){
      let a='0'
      for (let i = 0; i < row.length; i++) {
        if(row[i].isPackage===a){
          this.itemName_List.push(row[i])
        }
        
      }      
      
    },
    //子项目select下拉框事件
    changeMessage(value){
      
      if(value.length==0){
         this.arr=[]
         for (let i = 0; i < this.subproject.length; i++) {
          this.subproject[i].quantity=0         
         }
        
         this.bizFormModel.salePrice=this.bizFormModel.salePrice-this.sale
         this.bizFormModel.costPrice=this.bizFormModel.costPrice-this.cost
         this.sale=0
         this.cost=0
       }else{
          if(this.arr.length==0){
            this.arr=value
         }else{
            for (let i = 0; i < this.arr.length; i++) {
              for (let j = 0; j < value.length; j++) {
                if(this.arr[i]==value[j]){
                  this.arr.splice(i,1);
                }           
              }       
            }
            if(this.arr.length!=0){
              for (let i = 0; i < this.subproject.length; i++) {
                for (let j = 0; j < this.arr.length; j++) {
                  if(this.subproject[i].itemName==this.arr[j]){
                     //计算销售价
                    this.bizFormModel.salePrice-=(this.subproject[i].quantity*this.subproject[i].salePrice)
                    this.sale-=(this.subproject[i].quantity*this.subproject[i].salePrice)
                    this.bizFormModel.costPrice-=(this.subproject[i].quantity*this.subproject[i].costPrice)
                    this.cost-=(this.subproject[i].quantity*this.subproject[i].costPrice)

                    this.subproject[i].quantity=0
                  }
                  
                }           
              }
            }
             this.arr=[]
             this.arr=value
          }
        
       }
     // this.subproject=[]
      //增加
      
      if(this.subjectName.length>this.subproject.length){
      let selectChange1=[];         
        for (let i = 0; i < this.subjectName.length; i++) {
          let flages=false;
          for (let j = 0; j < this.subproject.length; j++) {
            if(this.subjectName[i]==this.subproject[j].itemName){
              flages=true;
              break;
            }
          } 
          if(!flages){
            console.log("hhah");
            selectChange1.push(this.subjectName[i]);
          }
        }
       console.log(selectChange1,'123');
        for (let x = 0; x < this.itemName_List.length; x++) {
        for (let j = 0; j < selectChange1.length; j++) {
            if(selectChange1[j]==this.itemName_List[x].itemName){
              this.subproject.push(this.itemName_List[x])
            }
        }
      }
      }else{//减少
        let selectChange2=[];
        for (let i = 0; i < this.subproject.length; i++) {
          for (let j = 0; j < this.subjectName.length; j++) {
           if(this.subjectName[j]==this.subproject[i].itemName){
            selectChange2.push(this.subproject[i]);
           }
          }     
        }
       this.subproject=[]
       this.subproject=selectChange2
      }
    //  if(this.subjectName.length!=null){
    //     for (let x = 0; x < this.subjectName.length; x++) {
    //     for (let j = 0; j < this.itemName_List.length; j++) {
    //         if(this.subjectName[x]==this.itemName_List[j].itemName){
    //           this.subproject.push(this.itemName_List[j])
    //         }
    //     }
    //   }
    //  }
      if(this.subproject.length!=0){
          this.salesFlage=true
        }else{
          this.salesFlage=false
        }
      
    },
    fun(val){
　　　　return Number(val).toFixed(2);
　　},
    handleChange(currentValue, oldValue){ 
       this.bizFormModel.salePrice=this.bizFormModel.salePrice-this.sale
       this.sale=0
       this.bizFormModel.costPrice=this.bizFormModel.costPrice-this.cost
       this.cost=0
      for (let i = 0; i < this.subproject.length; i++) {              
        if(this.subproject[i].quantity!=undefined){
          this.sale+=(this.subproject[i].quantity*this.subproject[i].salePrice)
          this.cost+=(this.subproject[i].quantity*this.subproject[i].costPrice)
        }
      }
       this.bizFormModel.salePrice=this.bizFormModel.salePrice+this.sale
       this.bizFormModel.costPrice=this.bizFormModel.costPrice+this.cost
     },
     //添加弹出框的回调事件
     close(){
       this.salesFlage=false
     },
     //输入框拼音码
     pinyinInput(value){
       this.bizFormModel.pinyinCode=pinyin.getCamelChars(value)
       
     },
  },
  watch: {
    "bizFormModel.isPackage":function(newVal, oldVal){
          if(newVal=="0"&&this.bizFormModel.itemType.value=="treatmentItemType_0"){
            this.objectType="0"
          }else{
            this.objectType="1"
          }
    },
    "bizFormModel.itemType":function(newVal, oldVal){
       if(this.bizFormModel.isPackage=="0"&&newVal.value=="treatmentItemType_0"){
            this.objectType="0"
          }else{
            this.objectType="1"
          }
    }
  },
  mounted: function () {
    this.$nextTick(() => {
      this.$on("openViewCostItemDialog", function (costItemDTO) {
        this.dialogProps.action = "view";
        this.subjectName=[]
        this.subproject=[]
        this.dialogProps.title = "查看诊疗项目";
        this.bizFormModel = { ...this.initFormModel(), ...costItemDTO.costItem };
        this.initOptions(this.bizFormModel);
       if(costItemDTO.costItemPackage[0]!=null&&costItemDTO.costItems[0]!=null){
          for (let i = 0; i < costItemDTO.costItemPackage.length; i++) {
          this.subproject.push(costItemDTO.costItemPackage[i])     
        }
        for (let i = 0; i < costItemDTO.costItems.length; i++) {
          
            this.subjectName.push(costItemDTO.costItems[i].itemName)
                         
        }
        for (let i = 0; i < this.subproject.length; i++) {
          for (let j = 0; j < costItemDTO.costItems.length; j++) {
            if(this.subproject[i].costItemId==costItemDTO.costItems[j].id){
              this.subproject[i].itemCode=costItemDTO.costItems[j].itemCode
              this.subproject[i].itemName=costItemDTO.costItems[j].itemName
              this.subproject[i].salePrice=costItemDTO.costItems[j].salePrice
            }
          }
          
        }
       }else if(costItemDTO.costItemPackage[0]!=null&&costItemDTO.costItems[0]==null){
          this.examine=costItemDTO.costItemPackage[0]
       }
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
      this.$on("openEditCostItemDialog", function (costItemDTO) {
        this.subjectName=[]
        this.subproject=[]
        this.itemName_List=[]
        this.arr=[]
        this.examine={
           referenceUnit:"",
           referenceValue:""
        }
       
        this.dialogProps.action = "edit";
        this.dialogProps.title = "修改诊疗项目";
       
        
        this.bizFormModel = { ...this.initFormModel(), ...costItemDTO.response.costItem};
        this.initOptions(this.bizFormModel);
        
        if(costItemDTO.response.costItemPackage[0]!=null&&costItemDTO.response.costItems[0]!=null){
          for (let i = 0; i < costItemDTO.response.costItemPackage.length; i++) {
          this.subproject.push(costItemDTO.response.costItemPackage[i])     
        }
        for (let i = 0; i < costItemDTO.response.costItems.length; i++) {
         
            this.subjectName.push(costItemDTO.response.costItems[i].itemName)
                 
        }
        for (let i = 0; i < this.subproject.length; i++) {
          for (let j = 0; j < costItemDTO.response.costItems.length; j++) {
            if(this.subproject[i].costItemId==costItemDTO.response.costItems[j].id){
              this.subproject[i].itemCode=costItemDTO.response.costItems[j].itemCode
              this.subproject[i].itemName=costItemDTO.response.costItems[j].itemName
              this.subproject[i].salePrice=costItemDTO.response.costItems[j].salePrice
              this.subproject[i].costPrice=costItemDTO.response.costItems[j].costPrice
            }
          }          
        }
       }else if(costItemDTO.response.costItemPackage[0]!=null&&costItemDTO.response.costItems[0]==null){
          this.examine=costItemDTO.response.costItemPackage[0]
       }

        
        if(this.subproject.length>0){
          this.salesFlage=true
        }else{
          this.salesFlage=false
        }
        this.sale=0
        this.cost=0
        //将subproject子项目中的价钱赋值给sale
        for (let i = 0; i < this.subproject.length; i++) {
          this.sale+=(this.subproject[i].quantity*this.subproject[i].salePrice)
          this.cost+=(this.subproject[i].quantity*this.subproject[i].costPrice)
        }
         console.log(this.cost,"现在价格");
        this.assign(costItemDTO.res)
        this.arr=this.subjectName
        this.tabIndex = "1";
        this.dialogProps.visible = true;
        
      });
      this.$on("openAddCostItemDialog", function (subprojects) {
        this.itemName_List=[]
        this.arr=[]
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加诊疗项目";
        this.bizFormModel = this.initFormModel(this.user);
        this.initOptions(this.bizFormModel);
        this.tabIndex = "1";
        this.dialogProps.visible = true;
        
        this.assign(subprojects);
        this.subproject=[]
        this.subjectName=[]
        this.sale=0
        this.cost=0
      });
      this.$on("openCopyCostItemDialog", function (costItemDTO) {
        // this.dialogProps.action = "add";
        // this.dialogProps.title = "添加诊疗项目";
        // this.bizFormModel = { ...this.initFormModel(this.user), ...costItem };
        // this.initOptions(this.bizFormModel);
        // this.tabIndex = "1";
        // this.bizFormModel.id = null; //把id设置为空，添加一个新的
        // this.dialogProps.visible = true;
        // this.subjectName=[]
        // this.subproject=[]
        // this.itemName_List=[]
        // this.arr=[]
        // this.examine={
        //    referenceUnit:"",
        //    referenceValue:""
        // }
       
        this.dialogProps.action = "add";
        this.dialogProps.title = "添加诊疗项目";
       
        
        this.bizFormModel = { ...this.initFormModel(), ...costItemDTO.response.costItem};
        this.initOptions(this.bizFormModel);
        
        if(costItemDTO.response.costItemPackage[0]!=null&&costItemDTO.response.costItems[0]!=null){
          for (let i = 0; i < costItemDTO.response.costItemPackage.length; i++) {
          this.subproject.push(costItemDTO.response.costItemPackage[i])     
        }
        for (let i = 0; i < costItemDTO.response.costItems.length; i++) {
         
            this.subjectName.push(costItemDTO.response.costItems[i].itemName)
                 
        }
        for (let i = 0; i < this.subproject.length; i++) {
          for (let j = 0; j < costItemDTO.response.costItems.length; j++) {
            if(this.subproject[i].costItemId==costItemDTO.response.costItems[j].id){
              this.subproject[i].itemCode=costItemDTO.response.costItems[j].itemCode
              this.subproject[i].itemName=costItemDTO.response.costItems[j].itemName
              this.subproject[i].salePrice=costItemDTO.response.costItems[j].salePrice
              this.subproject[i].costPrice=costItemDTO.response.costItems[j].costPrice
            }
          }          
        }
       }else if(costItemDTO.response.costItemPackage[0]!=null&&costItemDTO.response.costItems[0]==null){
          this.examine=costItemDTO.response.costItemPackage[0]
       }

        
        if(this.subproject.length>0){
          this.salesFlage=true
        }else{
          this.salesFlage=false
        }
        this.sale=0
        this.cost=0
        //将subproject子项目中的价钱赋值给sale
        for (let i = 0; i < this.subproject.length; i++) {
          this.sale+=(this.subproject[i].quantity*this.subproject[i].salePrice)
          this.cost+=(this.subproject[i].quantity*this.subproject[i].costPrice)
        }
        this.bizFormModel.id = null;
        this.bizFormModel.itemCode = "";
         console.log(this.cost,"现在价格");
        this.assign(costItemDTO.res)
        this.arr=this.subjectName
        this.tabIndex = "1";
        this.dialogProps.visible = true;
      });
    });
  },
};
</script>
<style >
 .verticalBar {
        width: 2px;
        height: 500px;
        background: rgba(235, 231, 231, 0.897);
        display: inline-block;
        margin-top: 10px;
        vertical-align: top;
        margin-right: 29px;
        margin-left: 30px;
    }
</style>