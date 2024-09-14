<template>
  <div id="report-container">
    <el-radio-group v-model="tabPosition">
      <el-radio-button label="main">汇总报表</el-radio-button>
      <el-radio-button label="detail">明细报表</el-radio-button>
    </el-radio-group>
    <div style="margin-top:8px;">
      <div v-if="tabPosition == 'main'">
        <div>
          <el-row>
            <el-col :span="7" class="flex-start">
              <div class="condition-name">收费日期：</div>
              <el-date-picker
              style="width:320px;"
                v-model="mainCondition.chargeDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
              ></el-date-picker>
            </el-col>
            <el-col :span="6"  class="flex-start">
              <div class="condition-name">费用名称：</div>
              <el-select
                v-model="mainCondition.mainCostType"
                placeholder="费用名称"
              >
                <el-option
                  v-for="mainCostType in mainCostTypes"
                  :key="mainCostType.value"
                  :label="mainCostType.name"
                  :value="mainCostType.value"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="6"   class="flex-start">
              <div class="condition-name">收费人：</div>
              <el-select
                v-model="mainCondition.chargeOper"
                placeholder="收费人"
              >
                <el-option
                  v-for="chargeOper in chargeOpers"
                  :key="chargeOper.id"
                  :label="chargeOper.name"
                  :value="chargeOper.id"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="5"  class="flex-start">
              <div class="condition-name">患者姓名：</div>
              <el-input
                v-model="mainCondition.patientName"
                style="width:200px;"
                placeholder="患者姓名"
              ></el-input>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="7"  class="flex-start">
              <div class="condition-name">收费单号：</div>
              <el-input
                style="width:320px;"
                v-model="mainCondition.chargeCode"
                placeholder="收费单号"
              ></el-input>
            </el-col>
            <el-col :span="6"  class="flex-start">
              <div class="condition-name">开单科室：</div>
              <el-select
                v-model="mainCondition.prescriptionDept"
                placeholder="开单科室"
              >
                <el-option
                  v-for="prescriptionDept in prescriptionDepts"
                  :key="prescriptionDept.id"
                  :label="prescriptionDept.name"
                  :value="prescriptionDept.id"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="6"  class="flex-start">
              <div class="condition-name">开单医生：</div>
              <el-select
                v-model="mainCondition.prescriptionOper"
                placeholder="收费人"
              >
                <el-option
                  v-for="prescriptionOper in prescriptionOpers"
                  :key="prescriptionOper.id"
                  :label="prescriptionOper.name"
                  :value="prescriptionOper.id"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="5"  class="flex-start">
              <div class="condition-name">收费状态：</div>
              <el-select
               style="width:200px;"
                v-model="mainCondition.chargeStatu"
                placeholder="收费状态"
              >
                <el-option
                  v-for="chargeStatu in chargeStatus"
                  :key="chargeStatu.id"
                  :label="chargeStatu.name"
                  :value="chargeStatu.id"
                >
                </el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>
        <div class="flex-end" style="margin-top:8px">
          <el-button type="primary">查询</el-button>
          <el-button>导出</el-button>
        </div>
        <el-table :data="data" style="width: 100%">
          <el-table-column prop="prop" label="序号" fixed width="width">
          </el-table-column>
          <el-table-column prop="prop" fixed label="患者姓名" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="性别" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="年龄" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="收费单号" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="费用名称" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="开单科室" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="开单医生" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="单据状态" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="应收(退)金额" width="150px">
          </el-table-column>
          <el-table-column prop="prop" label="实收金额" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="优惠金额" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="现金支付" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="微信支付" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="支付宝支付" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="银行卡支付" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="现金退款" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="收费人" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="收费日期" width="width">
          </el-table-column>
        </el-table>
        <div class="flex-end">
          <el-pagination
            @size-change="getChargeMainData"
            @current-change="getChargeMainData"
            :page-sizes="[5, 10, 15, 20]"
            :page-size="chargeMainTablePagesize"
            :current-page="chargeMainTableCurrentPage"
            layout="total,sizes, prev, pager, next,jumper"
            :total="chargeMainTableTotal"
          >
          </el-pagination>
        </div>
      </div>
      <div v-else>
        <div>
          <el-row>
            <el-col :span="7"  class="flex-start">
              <div class="condition-name">收费日期：</div>
              <el-date-picker
                style="width:320px;"
                v-model="detailCondition.chargeDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
              ></el-date-picker>
            </el-col>
            <el-col :span="6"   class="flex-start">
              <div class="condition-name">收费分类：</div>
              <el-select
              style="width:250px;"
                v-model="detailCondition.detailCostType"
                placeholder="收费分类"
              >
                <el-option
                  v-for="detailCostType in detailCostTypes"
                  :key="detailCostType.value"
                  :label="detailCostType.name"
                  :value="detailCostType.value"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="6"   class="flex-start">
              <div class="condition-name">收费人：</div>
              <el-select
                v-model="detailCondition.chargeOper"
                placeholder="收费人"
              >
                <el-option
                  v-for="chargeOper in chargeOpers"
                  :key="chargeOper.id"
                  :label="chargeOper.name"
                  :value="chargeOper.id"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="5"   class="flex-start">
              <div class="condition-name">患者姓名：</div>
              <el-input style="width:200px;"
                v-model="detailCondition.patientName"
                placeholder="患者姓名"
              ></el-input>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="7"   class="flex-start"> 
              <div class="condition-name">收费单号：</div>
              <el-input
                 style="width:320px;"
                v-model="detailCondition.chargeCode"
                placeholder="收费单号"
              ></el-input>
            </el-col>
            <el-col :span="6" class="flex-start">
              <div class="condition-name">收费项目：</div>
              <el-input
              style="width:250px;"
                v-model="detailCondition.chargeItem"
                placeholder="收费项目"
              ></el-input>
            </el-col>
            <el-col :span="6" class="flex-start">
              <div class="condition-name">开单科室：</div>
              <el-select
                v-model="detailCondition.prescriptionDept"
                placeholder="开单科室"
              >
                <el-option
                  v-for="prescriptionDept in prescriptionDepts"
                  :key="prescriptionDept.id"
                  :label="prescriptionDept.name"
                  :value="prescriptionDept.id"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="5" class="flex-start">
              <div class="condition-name">开单医生：</div>
              <el-select
                style="width:200px;"
                v-model="detailCondition.prescriptionOper"
                placeholder="收费人"
              >
                <el-option
                  v-for="prescriptionOper in prescriptionOpers"
                  :key="prescriptionOper.id"
                  :label="prescriptionOper.name"
                  :value="prescriptionOper.id"
                >
                </el-option>
              </el-select>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="7"  class="flex-start">
              <div class="condition-name">收费状态：</div>
              <el-select
                 style="width:320px;"
                v-model="detailCondition.chargeStatu"
                placeholder="收费人"
              >
                <el-option
                  v-for="chargeStatu in chargeStatus"
                  :key="chargeStatu.id"
                  :label="chargeStatu.name"
                  :value="chargeStatu.id"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="17">
              <div class="flex-end">
                <el-button type="primary">查询</el-button>
                <el-button>导出</el-button>
              </div>
            </el-col>
          </el-row>
        </div>

        <el-table :data="data" style="width: 100%">
          <el-table-column prop="prop" label="序号" fixed width="width">
          </el-table-column>
          <el-table-column prop="prop" fixed label="患者姓名" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="收费单号" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="科室" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="医生" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="收费项目" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="收费分类" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="数量" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="单据状态" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="应收/退(元)" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="收费人" width="width">
          </el-table-column>
          <el-table-column prop="prop" label="收费日期" width="width">
          </el-table-column>
        </el-table>
        <div class="flex-end">
          <el-pagination
            @size-change="getChargeDetailData"
            @current-change="getChargeDetailData"
            :page-sizes="[5, 10, 15, 20]"
            :page-size="chargeDetailTablePagesize"
            :current-page="chargeDetailTableCurrentPage"
            layout="total,sizes, prev, pager, next,jumper"
            :total="chargeDetailTableTotal"
          >
          </el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tabPosition: "main",
      /*汇总报表查询条件*/
      mainCondition: {
        chargeDateRange: "" /*收费日期范围*/,
        mainCostType: 0,
        chargeOper: null,
        patientName: "",
        chargeCode: "",
        prescriptionDept: null,
        prescriptionOper: null,
        chargeStatu: 0,
      },
      mainCostTypes: [
        {
          value: 0,
          name: "全部",
        },
        {
          value: 1,
          name: "零售售药",
        },
        {
          value: 2,
          name: "处方",
        },
        {
          value: 3,
          name: "挂号",
        },
      ],
      detailCondition: {
        chargeDateRange: "" /*收费日期范围*/,
        detailCostType: 0,
        chargeOper: null,
        patientName: "",
        chargeCode: "",
        chargeItem: "",
        prescriptionDept: null,
        prescriptionOper: null,
        chargeStatu: 0,
      },
      detailCostTypes: [
        {
          value: 0,
          name: "全部",
        },
        {
          value: 1,
          name: "西药处方",
        },
        {
          value: 2,
          name: "中药处方",
        },
      ],

      chargeOpers: [
        {
          id: 1,
          name: "张三",
        },
        {
          id: 2,
          name: "李四",
        },
      ],
      prescriptionDepts: [
        {
          id: 1,
          name: "内科",
        },
        {
          id: 2,
          name: "外科",
        },
      ],
      prescriptionOpers: [
        {
          id: 1,
          name: "张三",
        },
        {
          id: 2,
          name: "李四",
        },
      ],
      chargeStatus: [
        {
          id: 0,
          name: "全部",
        },
        {
          id: 1,
          name: "收费",
        },
        {
          id: 2,
          name: "退费",
        },
      ],
      chargeMainTablePagesize: 10 /*主表页大小*/,
      chargeMainTableCurrentPage: 1 /*当前主表页*/,
      chargeMainTableTotal: 0 /*主表总数据量*/,

      chargeDetailTablePagesize: 10 /*明细页大小*/,
      chargeDetailTableCurrentPage: 1 /*当前明细页*/,
      chargeDetailTableTotal: 0 /*明细总数据量*/,
    };
  },
  methods: {
    getChargeMainData() {},
    getChargeDetailData() {},
  },
};
</script>

<style scoped>
#report-container {
  width: 98%;
  height: 96%;
  margin:10px 10px 0;
}

.flex-space-around {
  display: flex;
  justify-content: space-around;
}

.flex-space-between {
  display: flex;
  justify-content: space-between;
}

.flex-start {
  display: flex;
  justify-content: flex-start;
}

.flex-end {
  display: flex;
  justify-content: flex-end;
}

.condition-name {
  width: 70px;
  text-align: right;
  vertical-align: center;
  font-size: 70%;
  margin-top:5px;
}

.el-row{
  margin-top:8px;
}
</style>