<template>
  <div class="input-number-range">
    <number-input v-model="beginValue" :currency="type == 'java.math.BigDecimal' ? 'CNY' : null" :precision="precision" clearable></number-input>
    <span>至</span>
    <number-input v-model="endValue" :currency="type == 'java.math.BigDecimal' ? 'CNY' : null" :precision="precision" clearable></number-input>
  </div>
</template>

<script>
export default {
  name: 'NumberRangeInput',
  model: {
    prop: 'arrValue',
    event: 'input'
  },
  data() {
    return {
      beginValue: this.arrValue && this.arrValue.length > 0? this.arrValue[0] : null,
      endValue: this.arrValue && this.arrValue.length > 0? this.arrValue[1] : null
    }
  },
  props: {
    // 接受外部v-model传入的值
    arrValue: {
      type: Array
    },
    // 货币类型
    currency: {
      type: String | Object,
      default: null
    },
    // 小数位数
    precision: {
      type: Number,
      default: 0
    },
    // 数据类型 （一般数值、金额）
    type: {
      type: String
    }
  },
  watch: {
    //判断下拉框的值是否有改变
    beginValue(val, oldVal) {
      //console.log('new: %s, old: %s', val, oldVal)
      if(val!=oldVal){
          this.$emit('input', [this.beginValue, this.endValue]);
      }
    },
    endValue(val, oldVal) {
      //console.log('new: %s, old: %s', val, oldVal)
      if(val!=oldVal){
          this.$emit('input', [this.beginValue, this.endValue]);
      }
    }
  },
  mounted(){
    //初始话日期框的值

    if (this.value instanceof Array){
      if (this.value.length === 2){
        this.beginValue = this.value[0]
        this.endValue = this.value[1]
      }
    }
  },
   methods: {
    //验证只能数字
    proving(e){
      let num = e.target.value || ''
      let code = e.which || e.keyCode
      let str = (e.key && e.key != 'Unidentified') ? e.key : num.substr(num.length - 1)
      console.log('|type:' + e.type + '|code:' + code + '|str:' + str + '|value112:' + num)

/*      //禁止输入中文
      if(code == '229') {
        console.log('----------------------------------------====')
        e.returnValue = false
        return false
      }*/

      //无论任何情况，皆可执行 回退、删除、左方向、右方向
      if(code == '8' || code == '46' || code == '37' || code == '39') {
        return true
      }
      //没有满足任何一种情况，中断执行
      if(!(/[\d.]/.test(str) || code == '190')) {
        e.returnValue = false
        return false
      }
      if(num.length > 12 ||
        (num.indexOf('.') >= 0 && code == '190') ||
        ((num.indexOf('.') == num.length - 3) && num.length > 3) ||
        (num.length == 0 && code == '190')) {
        e.returnValue = false
        return false
      }
    }
  }
}

</script>

<style lang="scss" scoped>
  .input-number-range {
    display: flex;
    box-sizing: border-box;
    justify-content: center;
    align-content: center;
    span {
      padding: 0 2px;
      font-size: 12px;
      display: flex;
      align-items: center;
    }
  }
</style>
