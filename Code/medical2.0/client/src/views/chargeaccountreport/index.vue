<template>
  <div id="report-container">
    <div class="flex-space-between"  id="condition">
      <div class="flex-start">
        <el-input
          v-model="condition.operName"
          prefix-icon="el-icon-search"
          style="width:250px;margin-right:8px;"
          placeholder="请输入收费人姓名"
        ></el-input>
        <div>收费日期：</div>
        <el-date-picker
          v-model="condition.chargeDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
        <el-radio style="margin-left:8px;margin-top:8px;" v-model="condition.mainOrDetail" label="1">汇总</el-radio>
        <el-radio style="margin-top:8px;" v-model="condition.mainOrDetail" label="2">明细</el-radio>
      </div>
      <div>
          <el-button type="primary">查询</el-button>
          <el-button>导出</el-button>
      </div>
    </div>
    <div>
        <el-table
            :data="data"
            style="width: 100%">
            <el-table-column
                prop="prop"
                label="序号"
                width="width">
            </el-table-column>
            <el-table-column
                prop="prop"
                label="收费人"
                width="width">
            </el-table-column>
            <el-table-column
                prop="prop"
                label="收费日期"
                width="width">
            </el-table-column>
            <el-table-column
                prop="prop"
                label="应收/退(元)"
                width="width">
            </el-table-column>
            <el-table-column
                prop="prop"
                label="实收金额"
                width="width">
            </el-table-column>
            <el-table-column
                prop="prop"
                label="优惠(元)"
                width="width">
            </el-table-column>
            <el-table-column
                prop="prop"
                label="现金支付"
                width="width">
            </el-table-column>
            <el-table-column
                prop="prop"
                label="微信支付"
                width="width">
            </el-table-column>
            <el-table-column
                prop="prop"
                label="支付宝支付"
                width="width">
            </el-table-column>
            <el-table-column
                prop="prop"
                label="银行卡支付"
                width="width">
            </el-table-column>
            <el-table-column
                prop="prop"
                label="现金退款"
                width="width">
            </el-table-column>
        </el-table>
        <div class="flex-end">
          <el-pagination
            @size-change="getChargeAccountData"
            @current-change="getChargeAccountData"
            :page-sizes="[5, 10, 15, 20]"
            :page-size="chargeAccountTablePagesize"
            :current-page="chargeAccountTableCurrentPage"
            layout="total,sizes, prev, pager, next,jumper"
            :total="chargeAccountTableTotal"
          >
          </el-pagination>
        </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      condition: {
        operName: "",
        chargeDateRange: "",
        mainOrDetail:"1",
      },
      chargeAccountTablePagesize:10,
      chargeAccountTableCurrentPage:1,
      chargeAccountTableTotal:0,
    };
  },
  methods:{
      getChargeAccountData(){

      },
  }
};
</script>

<style scoped>
#report-container {
  width: 100%;
  height: 100%;
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
  width: 80px;
  text-align: right;
  vertical-align: center;
}

#condition{
  margin: 10px 10px;
}
</style>