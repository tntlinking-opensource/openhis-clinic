<template>
  <div class="charge_container">
    
    <div class="charge_left">
      <div
        style="
          display: flex;
          justify-content: flex-end;
          margin-top: 10px;
        "
      >
        <el-button type="primary" @click="changeToRetail">零售收费</el-button>
      </div>
      <el-divider></el-divider>
      <div
        style="display: flex; justify-content: flex-start;"
      >
        <el-input
          v-model="selectCondition"
          placeholder="请输入患者姓名/卡号"
          prefix-icon="el-icon-search"
          style="margin-right: 8px"
          @keydown.enter.native="selectPatientList"
        ></el-input>
        <el-button type="text" @click="selectPatientList">搜索</el-button>
      </div>
      <div>
        <el-tabs
          stretch
          v-model="chargeStatusActiveName"
          @tab-click="selectPatientList"
        >
          <el-tab-pane label="待收费" name="1"></el-tab-pane>
          <el-tab-pane label="已收费" name="2"></el-tab-pane>
          <el-tab-pane label="已退费" name="3"></el-tab-pane>
        </el-tabs>
        <el-table
          :data="patientList"
          :show-header="false"
          highlight-current-row
          @row-click="selectPatientChange"
          style="width: 100%,height:80%;margin-top:-15px"
        >
          <el-table-column prop="name" label="姓名"> </el-table-column>
          <el-table-column
            prop="clinicDate"
            label="就诊时间"
            :formatter="clinicDateFormatter"
          >
          </el-table-column>
          <el-table-column prop="chargeStatus" label="收费状态">
          </el-table-column>
        </el-table>
        <div style="display: flex; justify-content: flex-end">
          <el-pagination
            layout="prev, next"
            :page-size="patientPageSize"
            prev-text="上一页"
            next-text="下一页"
            :current-page="patientCurrentPage"
            @prev-click="selectPatientList"
            @next-click="selectPatientList"
            :total="patientTotalCount"
          >
          </el-pagination>
        </div>
      </div>
    </div>
    <div class="charge_right">
      
      <div class="flex-space-between" style="margin-top: 10px">
        <div style="font-weight: bold">收费</div>
        <div class="flex-space-between">
          <div class="goods-back">已退药</div>
          <div style="margin-top: 5px; margin-right: 10px">23.00</div>
          <el-button
            :type="chargeStatusActiveName == '1' ? 'warning' : ''"
            :disabled="chargeStatusActiveName == '3'"
            @click="chargeClick"
            >{{ chargeStatusActiveName == "2" ? "退费" : "收费" }}</el-button
          >
          <el-button v-if="chargeStatusActiveName == '2'">开票</el-button>
        </div>
      </div>
      <div class="patientinfo_form">
        <el-form
          ref="patientInfoForm"
          :model="patientInfoForm"
          :rules="isRetail ? patientInfoRules : patientInfoNoRules"
          label-width="80px"
        >
          <el-row>
            <el-col :span="8">
              <el-form-item label="患者姓名" prop="name">
                <el-input v-model="patientInfoForm.name"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="patientInfoForm.gender">
                  <el-option
                    v-for="gender in genderList"
                    :key="gender.id"
                    :label="gender.name"
                    :value="gender.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="11">
              <div
                style="
                  display: flex;
                  flex-direction: row;
                  justify-content: flex-start;
                "
              >
                <el-form-item label="年龄" prop="age_year">
                  <el-input v-model="patientInfoForm.age_year" type="number">
                    <template slot="append">岁</template>
                  </el-input>
                </el-form-item>
                <div style="margin-left: -80px">
                  <el-form-item prop="age_month">
                    <el-input v-model="patientInfoForm.age_month" type="number">
                      <template slot="append">月</template>
                    </el-input>
                  </el-form-item>
                </div>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="10">
            <el-col :span="8">
              <el-form-item label="联系电话" prop="telephone">
                <el-input v-model="patientInfoForm.telephone"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="11">
              <el-form-item label="身份证号" prop="idCardNo">
                <el-input v-model="patientInfoForm.idCardNo"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="5"></el-col>
          </el-row>
        </el-form>
      </div>
      <div v-if="isRetail">
        <div>
          <el-autocomplete
            popper-class="my-autocomplete"
            v-model="goodsNameTemp"
            :fetch-suggestions="queryGoods"
            placeholder="请输入药品名称"
            @select="selectGoods"
          >
            <template slot-scope="{ item }">
              <div>{{ item.goodsName }}</div>
            </template>
          </el-autocomplete>
          <el-link type="primary" @click="AddRetailDrug()">添加</el-link>
        </div>
        <el-table :data="data" style="width: 100%">
          <el-table-column prop="prop" label="药品信息" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="厂家" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="单价" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="数量" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="单位" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="总额" width="width">
          </el-table-column>
        </el-table>
      </div>
      <div v-else>
        <el-card
          class="prescriptionCard"
          v-for="(prescriptionMain, index) in prescriptionMainList"
          :key="index"
        >
          <div v-if="prescriptionMain.prescriptionType == 1">
            
            <el-table :data="prescriptionMain.prescriptionDetail" style="width: 100%">
              <el-table-column prop="goodsName" label="西药处方" width="width">
              </el-table-column>
              <el-table-column prop="factoryName" label="厂家" width="width">
              </el-table-column>
              <el-table-column prop="price" label="单价" width="width">
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="width">
              </el-table-column>
              <el-table-column prop="unit" label="单位" width="width">
              </el-table-column>
              <el-table-column prop="totalCost" label="总额" width="width">
              </el-table-column>
            </el-table>
            <div class="flex-end">
              <div>共{{prescriptionMain.prescriptionDetail.length }},总计{{ getPrescriptionTotalCost(prescriptionMain.prescriptionDetail) }}元</div>
            </div>
          </div>
          <!--中药-->
          <div v-else-if="prescriptionMain.prescriptionType == 2">
            <el-table :data="prescriptionMain.prescriptionDetail" style="width: 100%">
              <el-table-column prop="goodsName" label="中药处方" width="width">
              </el-table-column>
              <el-table-column prop="factoryName" label="厂家" width="width">
              </el-table-column>
              <el-table-column prop="tisanesMethod" label="煎法" width="width">
              </el-table-column>
              <el-table-column prop="price" label="单价" width="width">
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="width">
              </el-table-column>
              <el-table-column prop="totalQuantity" label="总量" width="width">
              </el-table-column>
              <el-table-column prop="totalCost" label="总额" width="width">
              </el-table-column>
            </el-table>
            <div class="flex-space-between">
              <div class="flex-start">
                <div style="">{{ prescriptionMain.quantity }}剂</div>
                <div>用法：{{ prescriptionMain.useMethod }}</div>
                <div>{{ yypc }}</div>
                <!--煎药频次：1日1剂=-->
                <div>{{ fypc }}</div>
                <!--服药频次：1日2次=-->
                <div>{{ mcyl }}</div>
                <!--单次用量：一次500ml-->
              </div>
              <div>共{{ pz }},总计{{ zj }}元</div>
            </div>
          </div>
          <!--附加费-->
          <div v-if="prescriptionMain.prescriptionType == 3">
            <el-table :data="prescriptionMain.prescriptionDetail" style="width: 100%">
              <el-table-column prop="goodsName" label="附加费" width="width">
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="width">
              </el-table-column>
              <el-table-column prop="price" label="单价" width="width">
              </el-table-column>
              <el-table-column prop="totalCost" label="合计" width="width">
              </el-table-column>
            </el-table>
            <div class="flex-end">
              <div>共{{ prescriptionMain.prescriptionDetail.length }},总计{{ getPrescriptionTotalCost(prescriptionMain.prescriptionDetail) }}元</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
    <el-dialog
      title="收费"
      :visible.sync="chargeDialogVisible"
      width="50%"
      :close-on-click-modal="false"
      :before-close="dialogBeforeClose"
    >
      <div>
        <el-divider></el-divider>
        <div id="chargeTotalFee_Container">
          <div id="chargeTotalFee_Title">应收金额（元）：</div>
          <div id="chargeTotalFee_Fee">{{ chargeTotalFee }}</div>
        </div>
        <el-form ref="chargeForm" :model="chargeForm" label-width="80px">
          <el-row>
            <el-col :span="5">
              <el-form-item label="优惠金额">
                <el-input
                  v-model="chargeForm.discountAmount"
                  placeholder="优惠金额"
                >
                  <template slot="append">元</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="折扣">
                <el-input v-model="chargeForm.discountRate" placeholder="折扣">
                  <template slot="append">折</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="10">
              <el-form-item label="实收金额">
                <el-input v-model="model">
                  <template slot="append">元</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="支付方式">
                <el-radio-group v-model="model">
                  <el-radio label="1">现金</el-radio>
                  <el-radio label="2">支付宝</el-radio>
                  <el-radio label="3">微信</el-radio>
                  <el-radio label="3">银行卡</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="15">
              <el-form-item label="备注">
                <el-input v-model="model"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <el-divider></el-divider>
      </div>
      <div slot="footer">
        <el-button @click="chargeDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="chargeDialogVisible = false"
          >确认收费</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      title="退费明细"
      :visible.sync="refundDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <div>
        <el-divider></el-divider>
          <el-card
          class="prescriptionCard"
          v-for="(refundPrescriptionMain, index) in refundPrescriptionMainList"
          :key="index"
        >
          <div v-if="refundPrescriptionMain.prescriptionType == 1">
            <el-table :data="refundPrescriptionMain.prescriptionDetail" style="width: 100%">
              <el-table-column align="left" width="55px">
                <template slot="header"  slot-scope="scope">
                  <el-checkbox v-model="refundPrescriptionMain.isSelect"></el-checkbox>
                </template>
              </el-table-column>
              <el-table-column prop="goodsName" label="西药处方" width="width">
              </el-table-column>
              <el-table-column prop="factoryName" label="厂家" width="width">
              </el-table-column>
              <el-table-column prop="price" label="单价" width="width">
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="width">
              </el-table-column>
              <el-table-column prop="unit" label="单位" width="width">
              </el-table-column>
              <el-table-column prop="totalCost" label="总额" width="width">
              </el-table-column>
            </el-table>
          </div>
          <!--中药-->
          <div v-else-if="refundPrescriptionMain.prescriptionType == 2">
            <el-table  :data="refundPrescriptionMain.prescriptionDetail" style="width: 100%">
              <el-table-column align="left"  width="55px">
                <template slot="header" slot-scope="scope">
                  <el-checkbox v-model="refundPrescriptionMain.isSelect"></el-checkbox>
                </template>
              </el-table-column>
              <el-table-column prop="goodsName" label="中药处方" width="width">
              </el-table-column>
              <el-table-column prop="factoryName" label="厂家" width="width">
              </el-table-column>
              <el-table-column prop="tisanesMethod" label="煎法" width="width">
              </el-table-column>
              <el-table-column prop="price" label="单价" width="width">
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="width">
              </el-table-column>
              <el-table-column prop="totalQuantity" label="总量" width="width">
              </el-table-column>
              <el-table-column prop="totalCost" label="总额" width="width">
              </el-table-column>
            </el-table>
          </div>
          <!--附加费-->
          <div v-if="refundPrescriptionMain.prescriptionType == 3">
            <el-table :data="refundPrescriptionMain.prescriptionDetail" style="width: 100%">
              <el-table-column align="left"  width="55px">
                <template slot="header"  slot-scope="scope">
                  <el-checkbox v-model="refundPrescriptionMain.isSelect"></el-checkbox>
                </template>
              </el-table-column>
              <el-table-column prop="goodsName" label="附加费" width="width">
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="width">
              </el-table-column>
              <el-table-column prop="price" label="单价" width="width">
              </el-table-column>
              <el-table-column prop="totalCost" label="合计" width="width">
              </el-table-column>
            </el-table>
          </div>
        </el-card>
        <el-divider></el-divider>
      </div>
      <div slot="footer" class="flex-space-between">
        <div>实退金额：{{getRefundTotalCost()}}</div>
        <div>
          <el-button @click="refundDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="refundDialogVisible = false"
            >确 定</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    let cardIdNo = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("身份证号不能为空"));
      } else {
        let reg = /((^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$))/;
        if (!reg.test(value)) {
          callback(new Error("请输入正确的身份证号"));
        } else {
          callback();
        }
      }
    };
    return {
      isRetail: true, //是否零售
      selectCondition: "", //病人查询条件
      chargeStatusActiveName: "1", //收费状态标签页
      chargeDialogVisible: false,
      chargeTotalFee: 100,
      chargeForm: {
        discountAmount: 0,
        discountRate: 0,
      },
      refundDialogVisible: false,
      patientList: [
        {
          name: "邓诗诗",
          clinicDate: "2022-06-13 12:23:34",
          chargeStatus: "已收费",
        },
      ], //病人列表
      patientTotalCount: 0, //病人总数
      patientPageSize: 15, //每页显示的病人数量
      patientCurrentPage: 1, //病人当前页
      patientInfoForm: {
        name: "",
        gender: "",
        age_year: "",
        age_month: "",
        telephone: "",
        idCardNo: "",
      },
      patientInfoNoRules: null,
      patientInfoRules: {
        name: [
          { required: true, message: "请输入姓名", trigger: "blur" },
          { min: 1, max: 50, message: "长度在1到50个字符", trigger: "blur" },
        ],
        gender: [{ required: true, message: "请选择性别", trigger: "blur" }],
        age_year: [
          { required: true, message: "请输入年龄(岁)", trigger: "blur" },
        ],
        age_month: [
          { required: true, message: "请输入年龄(月)", trigger: "blur" },
        ],
        telephone: [
          { required: true, message: "请输入联系电话", trigger: "blur" },
          { min: 1, max: 20, message: "长度在1-20个字符", trigger: "blur" },
        ],
        idCardNo: [
          {
            required: true,
            trigger: "blur",
            validator: cardIdNo,
          },
        ],
      },
      genderList: [
        {
          id: 1,
          name: "男",
        },
        {
          id: 1,
          name: "女",
        },
      ],
      /*待收费处方列表*/
      prescriptionMainList:[{
        prescriptionType:1,
        isSelect:true,
        totalCost:50,
        prescriptionDetail:[{
          goodsName:"test",
          factoryName:"",
          price:10,
          quantity:5,
          unit:"",
          totalCost:50
        }]
      },{
        prescriptionType:2,
        isSelect:true,
        totalCost:150,
        quantity:3,
        useMethod:"test",
        prescriptionDetail:[{
          goodsName:"test2",
          factoryName:"",
          tisanesMethod:"",
          price:10,
          quantity:5,
          totalQuantity:15,
          totalCost:150
        }]
      },{
        prescriptionType:3,
        isSelect:true,
        totalCost:50,
        prescriptionDetail:[{
          goodsName:"test3",
          price:10,
          quantity:5,
          totalCost:50
        }]
      }],
      refundPrescriptionMainList:[{
        prescriptionType:1,
        isSelect:true,
        totalCost:50,
        prescriptionDetail:[{
          goodsName:"test",
          factoryName:"",
          price:10,
          quantity:5,
          unit:"",
          totalCost:50
        }]
      },{
        prescriptionType:2,
        isSelect:true,
        totalCost:150,
        prescriptionDetail:[{
          goodsName:"test2",
          factoryName:"",
          tisanesMethod:"",
          price:10,
          quantity:5,
          totalQuantity:15,
          totalCost:150
        }]
      },{
        prescriptionType:3,
        isSelect:true,
        totalCost:50,
        prescriptionDetail:[{
          goodsName:"test3",
          price:10,
          quantity:5,
          totalCost:50
        }]
      }]
    };
  },
  methods: {
    /*收费退费按钮点击*/
    chargeClick() {
      if (this.chargeStatusActiveName == "1") {
        this.chargeDialogVisible = true;
      } else {
        this.refundDialogVisible = true;
      }
    },
    getPrescriptionTotalCost(data){
       var totalCost = 0;
       data.forEach(element => {
        if(element.totalCost){
          totalCost+=element.totalCost
        }
      });
      return totalCost;
    },
    /*计算当前退费处方合计*/
    getRefundTotalCost(){
      var totalCost = 0;
      this.refundPrescriptionMainList.forEach(element => {
        if(element.totalCost){
          totalCost+=element.totalCost
        }
      });
      return totalCost;
    },
    /*切换到零售*/
    changeToRetail() {
      this.isRetail = true;
      //清空病人
      patientInfoForm = {
        name: "",
        gender: "",
        age_year: "",
        age_month: "",
        telephone: "",
        idCardNo: "",
      };
    },
    /*病人列表点击*/
    selectPatientChange(row) {
      this.isRetail = false;
      this.$nextTick(() => {
        this.$refs["patientInfoForm"].clearValidate();
      });
    },
    /*查询病人*/
    selectPatientList() {},
    clinicDateFormatter(row) {
      var date = new Date(row.clinicDate);
      return (
        date.getMonth() +
        1 +
        "/" +
        date.getDate() +
        " " +
        date.getHours() +
        ":" +
        date.getMinutes()
      );
    },

  },
};
</script>


<style scoped>
.charge_container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: space-around;
}

.charge_left {
  width: 30%;
  height: 100%;
}

.charge_right {
  height: 100%;
  width: 67%;
}

.patientinfo_form {
  background: #f2f2f2;
  border-radius: 10px;
  margin: 10px 0px 10px 0px;
  padding: 10px 10px 0px 10px;
}

.first_column {
  font: bold black;
}

#chargeTotalFee_Container {
  display: flex;
  justify-content: flex-start;
  padding: 20px;
  margin: 10px;
  background-color: rgba(207, 227, 236, 0.803921568627451);
  border-radius: 7px;
}

#chargeTotalFee_Title {
  font-family: "Arial Normal", "Arial", sans-serif;
  font-weight: 400;
  font-style: normal;
  font-size: 21px;
  margin-right: 8px;
}

#chargeTotalFee_Fee {
  font-weight: 800;
  font-style: black;
  font-size: 25px;
  margin-top: -4px;
}

.flex-space-between {
  display: flex;
  justify-content: space-between;
}

.flex-start{
  display:flex;
  justify-content: flex-start;
}

.flex-end{
  display:flex;
  justify-content: flex-end;
}

.el-divider--horizontal{
  margin:5px 0;
}

.prescriptionCard{
  margin:5px 0;
}

.goods-back{
  position: absolute;
  top:20px;
  right: 200px;
  width: 60px;
  height: 25px;
  text-align: center;
  vertical-align: center;
  color:red;
  border: 1px solid red;
  border-radius: 45%;
  cursor: none;
  -webkit-transform: rotate(-45deg);
  float: right;
  z-index: 1;
}
</style>