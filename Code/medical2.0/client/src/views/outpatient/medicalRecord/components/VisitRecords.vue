<template>
  <div class="visitRecords">
    <ul class="patientInfo">
      <li>
        患者姓名：{{ NowVisitInfo.info.name }}（{{ NowVisitInfo.info.age }}岁）
      </li>
      <li>性别：{{ NowVisitInfo.info.gender.name }}</li>
      <li>就诊时间：{{ NowVisitInfo.medicalRecord.diagnoseDate }}</li>
      <li>科室医生：{{ NowVisitInfo.medicalRecord.createBy }}</li>
    </ul>
    <div class="titleBox">
      <h4><el-checkbox v-model="checkedForm.record">病例</el-checkbox></h4>
      <el-button class="titleBtn">{{ NowVisitInfo.info.treatType }}</el-button>
    </div>
    <ul class="infoItem">
      <li
        v-show="item.value"
        v-for="(item, index) in AllMedicineRecordLabelList"
        :key="index"
      >
        <p class="infoName">【{{ item.label }}】</p>
        <p class="itemContent">{{ item.value }}</p>
      </li>
    </ul>
    <div class="titleBox" v-if="NowVisitInfo.recipelInfoEvtList.length">
      <h4>
        <el-checkbox v-model="checkedForm.Prescription">处方</el-checkbox>
      </h4>
    </div>
    <ul class="translateBox" v-if="NowVisitInfo.recipelInfoEvtList.length">
      <li v-if="WesternMedicineModel.recipelDetailEvtList.length">
        <p>【西药处方】</p>
        <el-table
          :data="WesternMedicineModel.recipelDetailEvtList"
          border
          style="width: 100%"
        >
          <el-table-column type="index" label="序号" width="50" align="center">
          </el-table-column>
          <el-table-column prop="drugStuffId.name" label="名称" align="center">
          </el-table-column>
          <el-table-column prop="singleDosage" label="单次用量" width="100">
          </el-table-column>
          <el-table-column
            prop="westernMedicineUse.name"
            label="用法"
            width="100"
          >
          </el-table-column>
          <el-table-column prop="frequency.name" label="频度" width="120">
          </el-table-column>
          <el-table-column prop="days.name" label="天数" width="100">
          </el-table-column>
          <el-table-column prop="total" label="总量" width="100">
          </el-table-column>
          <el-table-column
            prop="drugStuffId.price"
            label="单价"
            width="100"
            align="center"
          >
          </el-table-column>
          <el-table-column
            prop="allFee"
            label="合计"
            width="100"
            align="center"
          >
          </el-table-column>
        </el-table>
        <p class="userTypeBox">
          {{ WesternMedicineModel.recipelInfo.remarks }}
        </p>
      </li>
      <li v-if="ChineseMedicineModel.recipelDetailEvtList.length">
        <p>【中药处方】</p>
        <ul class="medicineBox">
          <li
            v-for="(item, index) in ChineseMedicineModel.recipelDetailEvtList"
            :key="index"
          >
            {{ item.drugStuffId.name }}&nbsp;&nbsp;{{ item.singleDosage
            }}{{ item.drugStuffId.dosisUnit.name }}
          </li>
        </ul>
        <p class="userTypeBox">
          {{
            ChineseMedicineModel.recipelInfo.dosage
          }}剂&nbsp;&nbsp;&nbsp;&nbsp;{{
            ChineseMedicineModel.recipelInfo.recipelUse.name
          }}&nbsp;&nbsp;&nbsp;&nbsp;{{
            ChineseMedicineModel.recipelInfo.frequency.name
          }}&nbsp;&nbsp;&nbsp;&nbsp;{{
            ChineseMedicineModel.recipelInfo.takeFrequency.name
          }}
        </p>
      </li>
      <li v-if="InfusionItemList.length">
        <p>【输液处方】</p>
        <div v-for="(item, index) in InfusionItemList" :key="index">
          <InfusionPrescription
            :InfusionUseOption="InfusionUseOption"
            :InfusionOption="InfusionOption"
            :DayNumOption="DayNumOption"
            :FrequencyOption="FrequencyOption"
            :InfoModel="item"
            :IsOnlyRead="IsOnlyRead"
            :GroupNumber="index + 1"
            :key="index"
          />
        </div>
        <p class="userTypeBox">{{ InfusionModel.remarks }}</p>
      </li>
      <li v-if="CostItemList.length">
        <p>【诊疗项目】</p>
        <el-table
          :data="CostItemList"
          border
          style="width: 100%"
          class="tableStyle"
        >
          <el-table-column type="index" label="序号" width="50" align="center">
          </el-table-column>
          <el-table-column prop="drugStuffId.name" label="项目名称">
          </el-table-column>
          <el-table-column prop="total" label="数量" width="100">
          </el-table-column>
          <el-table-column prop="drugStuffId.price" label="单价" width="80">
          </el-table-column>
          <el-table-column
            prop="allFee"
            label="合计"
            width="100"
            align="center"
          >
          </el-table-column>
        </el-table>
      </li>
      <li v-if="SurchargeList.length">
        <p>【附加费】</p>
        <el-table
          :data="SurchargeList"
          border
          style="width: 100%"
          class="tableStyle"
        >
          <el-table-column type="index" label="序号" width="50" align="center">
          </el-table-column>
          <el-table-column prop="drugStuffId.name" label="费用名称">
          </el-table-column>
          <el-table-column prop="total" label="数量" width="100">
          </el-table-column>
          <el-table-column prop="drugStuffId.price" label="单价" width="80">
          </el-table-column>
          <el-table-column
            prop="allFee"
            label="合计"
            width="100"
            align="center"
          >
          </el-table-column>
        </el-table>
      </li>
    </ul>
  </div>
</template>

<script>
import InfusionPrescription from "./InfusionPrescription.vue";
export default {
  components: { InfusionPrescription },
  props: {
    NowVisitInfo: {
      type: Object,
    },
    InfusionUseOption: {
      type: Object,
    },
    InfusionOption: {
      type: Object,
    },
    DayNumOption: {
      type: Object,
    },
    FrequencyOption: {
      type: Object,
    },
    checkedForm: {
      type: Object,
    },
  },
  data() {
    return {
      IsOnlyRead: true,
      //全部病例配置
      AllMedicineRecordLabelList: [
        {
          label: "主诉",
          name: "patientTell",
          value: "",
        },
        {
          label: "现病史",
          name: "nowHistory",
          value: "",
        },
        {
          label: "既往史",
          name: "beforeHistory",
          value: "",
        },
        {
          label: "其他检查",
          name: "otherCheck",
          value: "",
        },
        {
          label: "诊断",
          name: "diagnose",
          value: "",
        },
        {
          label: "个人史",
          name: "personalHistory",
          value: "",
        },
        {
          label: "过敏史",
          name: "allergyHistory",
          value: "",
        },
        {
          label: "疾病史",
          name: "diseaseHistory",
          value: "",
        },
        {
          label: "传染病史",
          name: "infectiousHistory",
          value: "",
        },
        {
          label: "手术史",
          name: "surgeryHistory",
          value: "",
        },
        {
          label: "输血史",
          name: "transfusionHistory",
          value: "",
        },
        {
          label: "体格检查",
          name: "physiqueCheck",
          value: "",
        },
        {
          label: "急诊诊断",
          name: "emergencyDiagnose",
          value: "",
        },
        {
          label: "急诊效果",
          name: "emergencyEffect",
          value: "",
        },
        {
          label: "家族史",
          name: "familyHistory",
          value: "",
        },
        {
          label: "月经史",
          name: "lunariaHistory",
          value: "",
        },
        {
          label: "婚育史",
          name: "pregnancyHistory",
          value: "",
        },
        {
          label: "辅助检查",
          name: "assistCheck",
          value: "",
        },
      ],
      WesternMedicineModel: {
        recipelInfo: {},
        recipelDetailEvtList: [],
      },
      ChineseMedicineModel: {
        recipelInfo: {
          recipelUse: {},
          frequency: {},
          takeFrequency: {},
        },
        recipelDetailEvtList: [],
      },
      InfusionItemList: [],
      InfusionModel: {},
      CostItemList: [],
      SurchargeList: [],
    };
  },
  mounted() {
    this.InitData();
  },
  methods: {
    InitData() {
      this.WesternMedicineModel = {
        recipelInfo: {},
        recipelDetailEvtList: [],
      };
      this.ChineseMedicineModel = {
        recipelInfo: {
          recipelUse: {},
          frequency: {},
          takeFrequency: {},
        },
        recipelDetailEvtList: [],
      };
      this.InfusionItemList = [];
      this.InfusionModel = {};
      this.CostItemList = [];
      this.SurchargeList = [];
      this.AllMedicineRecordLabelList.map((item) => {
        item.value = this.NowVisitInfo.medicalRecord[`${item.name}`];
      });
      this.NowVisitInfo.recipelInfoEvtList.map((prescriptionItem) => {
        switch (prescriptionItem.recipelInfo.recipelType.name) {
          case "西药处方":
            this.WesternMedicineModel = prescriptionItem;
            break;
          case "中药处方":
            this.ChineseMedicineModel = prescriptionItem;
            break;
          case "输液处方":
            this.InfusionModel = prescriptionItem.recipelInfo;
            this.InfusionItemList = [];
            let GroupArr = [];
            prescriptionItem.recipelDetailEvtList.map((item) => {
              GroupArr.push(item.infuseGroup);
            });
            GroupArr = [...new Set(GroupArr)].sort();
            GroupArr.map((group) => {
              let InfoModel = {
                infuseUse: {},
                frequency: {},
                days: {},
                drippingSpeed: "",
                infuseGroup: group,
                infusionList: [],
              };
              this.InfusionItemList.push(JSON.parse(JSON.stringify(InfoModel)));
              prescriptionItem.recipelDetailEvtList.map((item) => {
                if (group == item.infuseGroup) {
                  this.InfusionItemList[group - 1].infuseUse = item.infuseUse;
                  this.InfusionItemList[group - 1].frequency = item.frequency;
                  this.InfusionItemList[group - 1].days = item.days;
                  this.InfusionItemList[group - 1].drippingSpeed =
                    item.drippingSpeed;
                  this.InfusionItemList[group - 1].infuseGroup = item.group;
                  this.InfusionItemList[group - 1].infusionList.push(item);
                }
              });
            });
            break;
          case "诊疗项目":
            this.CostItemList = prescriptionItem.recipelDetailEvtList;
            break;
          case "附加费":
            this.SurchargeList = prescriptionItem.recipelDetailEvtList;
            break;
          default:
            break;
        }
      });
    },
  },
};
</script>

<style lang='scss' scoped>
$borderColor: rgb(219, 219, 219); //灰色边框下划线
$fontAndBgColor: #169bd5; //蓝色按钮和字体
.visitRecords {
  padding: 0 10px;
  max-height: 60vh;
  .patientInfo {
    width: 100%;
    height: 40px;
    border: 1px solid $borderColor;
    line-height: 40px;
    padding: 0 4px;
    border-radius: 2px;
    list-style: none;
    display: flex;
    justify-content: space-between;
  }
  .titleBox {
    width: 100%;
    height: 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    h4 {
      margin: 0;
      color: $fontAndBgColor;
    }
    .titleBtn {
      background-color: $fontAndBgColor;
      color: white;
    }
  }
  .infoItem {
    width: 100%;
    padding: 4px;
    list-style: none;
    border: 1px solid $borderColor;
    li {
      display: flex;
      justify-content: space-between;
      p {
        margin: 0;
      }
      .infoName {
        width: 90px;
        font-weight: bold;
      }
      .itemContent {
        flex: 1;
      }
    }
  }
  .translateBox {
    width: 100%;
    padding: 4px;
    list-style: none;
    border: 1px solid $borderColor;
    li {
      margin-bottom: 20px;
      p {
        margin: 0;
        font-weight: bold;
      }
      .medicineBox {
        width: 100%;
        padding: 4px;
        list-style: none;
        display: flex;
        margin-bottom: 10px;
        li {
          margin-right: 30px;
        }
      }
      .userTypeBox {
        padding-left: 6px;
        color: $fontAndBgColor;
      }
    }
  }
}
</style>