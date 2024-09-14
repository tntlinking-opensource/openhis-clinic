<template>
  <div class="container">
    <el-form :inline="true" ref="sysParamConfigForm" :model="sysParamConfigForm" label-width="180px">
      <el-form-item label="慢病处方最多可开">
        <el-input v-model="sysParamConfigForm.chronicDays"
                  placeholder="请输入内容"
                  @input="handleInput('chronicDays')"
                  maxlength="10"
                  @change="chronicDaysHandleChange">
          <template slot="append">天</template>
        </el-input>
      </el-form-item>
      <el-form-item label="普通处方最多可开">
          <el-input v-model="sysParamConfigForm.normalDays"
                    placeholder="请输入内容"
                    @input="handleInput('normalDays')"
                    maxlength="10"
                    @change="normalDaysHandleChange">
            <template slot="append">天</template>
          </el-input>
      </el-form-item>
      <el-form-item label="挂号费默认金额：">
        <el-input v-model="sysParamConfigForm.registrationFee"
                  @input="limitNum('registrationFee')"
                  type="text"
                  @change="registrationFeeHandleChange"
                  >
          <template slot="append">元</template>
        </el-input>
      </el-form-item>
      <el-form-item label="药品进价是否为零售价">
        <el-checkbox label="" v-model="sysParamConfigForm.retailPrice === 'true'?true:false"
                     @change="retailPriceHandleChange"></el-checkbox>
      </el-form-item>
    </el-form>

    <!-- 保存按钮 -->
    <div class="save-button">
      <el-button type="primary" size="medium" @click="saveData">保存</el-button>
    </div>
  </div>
</template>

<script>
  import MainUI from '@/views/components/mainUI'
  import {listSysParamConfigAll, saveSysParamConfigList} from '@/api/sys/sysParamConfig'
  import {getLocalCurrentCompany} from "@/utils/auth";

  export default {
  extends: MainUI,
  components: {
  },
  data() {
    return {
      permission: {
        view: false,
        add: false,
        edit: false,
        remove: false,
        export: false
      },
      //这个对象很重要，每次增加了类型config_name都要对应加上
      sysParamConfigForm: {
        chronicDays: null,//慢病处方天数
        normalDays: null,//普通处方天数
        registrationFee: null,//默认挂号金额
        retailPrice: null,//是否默认零售价
      },
      saveMap:new Map(),
      search: {
        columnName: "company_id",
        queryType: "=",
        value: getLocalCurrentCompany().id
      },
    };
  },
  methods: {
    /**
     *  页面初始化，主要是将后台数据结构转换成页面需要的
     * @returns {Promise<void>}
     */
    async pageInit() {
      listSysParamConfigAll({params:[this.search]}).then(responseData => {
        if(responseData.code == 100) {
          if (responseData.data.length >= 1){
            responseData.data.forEach(data=>{
              this.saveMap.set(data.configName, data);
              if (this.sysParamConfigForm.hasOwnProperty(data.configName)) {
                this.sysParamConfigForm[data.configName] = data.configValue;
              }
            })
          }else {
            this.initSaveDatas();
          }
        } else {
          this.showMessage(responseData)
        }
      }).catch(error => {
        this.outputError(error)
      })
    },
    /**
     * 初始化saveMap
     */
    initSaveDatas(){
      for (let key in this.sysParamConfigForm) {
        if (this.sysParamConfigForm.hasOwnProperty(key)) {
          console.log(key); // 输出键名
          let data = {
            id: null,
            configName: key,
            configValue: this.sysParamConfigForm[key],
            companyId: getLocalCurrentCompany().id
          };
          this.saveMap.set(key, data);
        }
      }
    },
    chronicDaysHandleChange(value){
      let chronicDays = this.saveMap.get("chronicDays");
      chronicDays.configValue = String(value);
    },
    normalDaysHandleChange(value){
      let normalDays = this.saveMap.get("normalDays");
      normalDays.configValue = String(value);

    },
    registrationFeeHandleChange(value){
      let registrationFee = this.saveMap.get("registrationFee");
      registrationFee.configValue = String(value);
    },
    retailPriceHandleChange(value){
      let retailPrice = this.saveMap.get("retailPrice");

      retailPrice.configValue = String(value);
      this.sysParamConfigForm.retailPrice = String(value);
    },
    /**
     * input中只允许输入正整数
     * @param field
     */
    handleInput(field) {
      let value = this.sysParamConfigForm[field]
      // 在输入事件中检查输入值是否为整数
      if (!/^\d+$/.test(this.inputValue)) {
        // 如果不是整数，清除输入值
        this.sysParamConfigForm[field] = value.replace(/[^\d]/g, '');
      }
    },
    /**
     * input中只允许输入小数并保留2位
     * @param amount
     */
    //用户输入01、02等，则展示成1、2，等于非小数时过滤第一位0
    limitNum(field) {
      let amount = this.sysParamConfigForm[field]
        .replace(/[^\d.]/g, "") //只能输入数字
        .replace(/^(\-)*(\d+)\.(\d\d).*$/, "$1$2.$3") //只能输入两个小数
        .replace(/\.{2,}/g, "."); //出现多个点时只保留第一个
      // 第一位不让输小数点
      if (amount == ".") {
        amount = "";
      }
      // 如果第一位是0，第二位必须大于0或者小数点
      if (amount.substring(0, 1) == 0) {
        if (amount.substring(1, 2) > 0) {
          amount = amount.substring(1, 2);
        } else if (
          amount.substring(1, 2) === 0 ||
          amount.substring(1, 2) === "0"
        ) {
          amount = "0";
        }
      } else {
        // 如果第一位数字大于0（不等于0肯定就大于0），仅需考虑第二位是小数点的情况
        if (amount.indexOf(".") !== -1) {
          if (amount.substring(0, 1) > 0) {
            console.log("第一位大于0");
          } else {
            console.log("第一位等于0");
            if (amount.substring(2, 3) > 0) {
              console.log("小数点后第一位大于0");
            } else {
              console.log("小数点后第一位等于0");
              amount = "0.";
            }
          }
        } else {
          console.log("没有小数点，正常输入");
        }
      }
      this.sysParamConfigForm[field] = amount;
    },
    /**
     * 将页面数据结构转换成后台对象
     * @returns {[]}
     */
    getSaveData(){
      let saveDatas = [];
      this.saveMap.forEach((value, key) => {
        console.log(`${key}: ${value}`);
        saveDatas.push(value);
      })
      return saveDatas;
    },
    /**
     * 保存数据
     */
    saveData(){
      console.log('saveData')
      let saveDatas = this.getSaveData();
      saveSysParamConfigList(saveDatas).then(responseData => {
        if(responseData.code == 100) {
          this.showMessage({type: 'success', msg: '操作成功'})
        } else {
          this.showMessage(responseData.msg);
        }
      }).catch(error => {
        this.outputError(error)
      })
    }
  },
  watch: {
  },
  mounted() {
    this.pageInit()
  }
}
</script>

<style scoped>
  .save-button {
    display: flex;
    justify-content: center;
    margin-top: auto; /* 将按钮推到底部 */
    padding: 20px; /* 可选：为按钮添加一些内边距 */
  }
</style>
