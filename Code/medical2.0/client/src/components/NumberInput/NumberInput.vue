<template>
  <el-input
    ref="numberInput"
    v-model="formatValue"
    @input="updateValue"
    v-currency="options"
    :clearable="clearable"
    :placeholder="placeholder"
    :disabled="disabled"
    :readonly="readonly"
    :value-range="valueRange"
    @blur="handleBlur"
    @focus="handleFocus"
    @change.capture="handleChange"
    @clear="handleClear">
    <slot slot="prepend" name="prepend"></slot>
    <slot slot="append" name="append"></slot>
  </el-input>
</template>

<script>
  import { CurrencyDirective, parse, setValue, getValue } from "vue-currency-input";
	export default {
		name: "NumberInput",
    model: {
      prop: 'value',
      event: 'input'
    },
    props: {
      value: {
        type: Number | String
      },
      // 语言环境
      locale: {
        type: String
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
      // 是否隐藏可忽略的十进制数字、货币符号和焦点分组符号
      distractionFree: {
        type: Boolean,
        default: false
      },
      clearable: {
        type: Boolean
      },
      disabled: {
        type: Boolean
      },
      readonly: {
        type: Boolean
      },
      prefixIcon: {
        type: String
      },
      suffixIcon: {
        type: String
      },
      placeholder: {
        type: String
      },
      valueRange: {
        type: Object
      }
    },
    directives: {
      currency: CurrencyDirective
    },
    data() {
		  return {
        formatValue: this.value,
        formatOldValue: null
      }
    },
    mounted() {
      setValue(this.$refs.numberInput, this.value)
    },
    watch:  {
      value(newVal, oldVal) {
        if(newVal != oldVal) {
          this.$nextTick(() => {
            setValue(this.$refs.numberInput, this.value)
          })
        }
      }
    },
    computed: {
      options() {
        return {
          locale: this.locale,
          currency: this.currency,
          valueRange: this.valueRange,
          precision: this.precision,
          distractionFree: this.distractionFree
        }
      }
    },
    methods: {
      updateValue(value) {
        this.$emit('input', parse(this.formatValue, this.options))
      },
      handleBlur(e) {
        this.$emit('blur', parse(this.formatValue, this.options), this.formatOldValue)
      },
      handleChange(value) {
        this.$emit('change', parse(this.formatValue, this.options), this.formatOldValue)
      },
      handleFocus(e) {
        this.$emit('focus', e)
        this.formatOldValue = getValue(this.$refs.numberInput)
      },
      handleClear() {
        setValue(this.$refs.numberInput, null)
        this.$emit('clear')
      }
    }
	}
</script>

<style scoped>

</style>
