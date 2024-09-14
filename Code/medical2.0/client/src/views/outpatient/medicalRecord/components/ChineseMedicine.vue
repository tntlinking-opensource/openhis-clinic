<template>
  <div class="ChineseMedicine">
    <p>{{ChineseMedicine.goodsName ? ChineseMedicine.goodsName : ChineseMedicine.drugStuffId.name }}</p>
    <div>
      <el-input v-model="ChineseMedicine.singleDosage" @input="GetChineseFee" :disabled='IsOnlyRead' style="width: 100%" placeholder="数量">
        
        <span slot="suffix" style="font-size: 16px" >{{ ChineseMedicine.preparationUnit ? ChineseMedicine.preparationUnit.name : ChineseMedicine.drugStuffId.drug.preparationUnit.name }}</span>
        <el-select
          v-model="ChineseMedicine.chineseMedicineUse"
          slot="append"
          :disabled='IsOnlyRead'
          style="width: 120px"
          placeholder="煎法"
        >
          <el-option
            v-for="item in ChineseUseTimeOption"
            :key="item.value"
            :label="item.name"
            :value="{ name:item.name, value: item.value }"
          >
          </el-option>
        </el-select>
      </el-input>
    </div>
  </div>
</template>

<script>
export default {
    props: {
        ChineseMedicine: {
            type: Object
        },
        ChineseUseTimeOption: {
          type: Object
        },
        IsOnlyRead: {
          type: Boolean
        }
    },
  data() {
    return {
      //数量
      singleDosage: "",
      //单位
      Unit: "",
      UnitOption: [],
      chineseMedicineUse: "",
    };
  },
  methods: {
    GetChineseFee() {
      this.ChineseMedicine.allFee = this.ChineseMedicine.price * this.ChineseMedicine.singleDosage
      this.$emit('GetChineseFee')
    }
  }
};
</script>
<style lang='scss' scoped>
$borderColor: rgb(219, 219, 219); //灰色边框下划线
.ChineseMedicine {
  width: calc(100% - 2px);
  height: 100%;
  border: 1px solid $borderColor;
  border-radius: 2px;
  p {
    font-size: 14px;
    margin: 0;
    padding: 10px;
    height: 50%;
    display: flex;
    align-items: center;
  }
}
</style>