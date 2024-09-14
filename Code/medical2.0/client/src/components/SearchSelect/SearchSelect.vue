<template>
  <el-select
    v-model="currentValue"
    :value-key='valueKey'
    filterable
    clearable
    v-bind="$attrs"
    :filter-method="searchFilter"
    v-load="loadMore(OptSize)"
    @clear="handleClear"
    @change="handleChange">
    <el-option
      v-for="(option, index) in searchOptions.slice(0, OptSize)"
      :key="index"
      :value="valueKey?option : option[optionValue]"
      :label="option[optionLabel]"></el-option>
  </el-select>
</template>

<script>
  /**
   * el-select 下拉选择框 在加载数据量过大时使用
   * 调用示例：
   * <search-select :optionSize="8" v-model="value" option-label="label" option-value="value" :options="options"></search-select>
   * Attributes：
   * optionSize：初始下拉选项的条数
   * option-label：选项的标签，若不设置则默认与 value 相同 （必填）
   * option-value：选项的值
   * options：下拉选项数组
   * 当 值绑定为对象时，必须指定 value-key 唯一标识，且只需设置 option-label 属性，无需设置 option-value 属性
   * 其他属性同 el-select 官方属性
   * 特性后续优化
   */
	export default {
		name: "SearchSelect",
    model: {
      prop: 'value',
      event: 'change'
    },
    props: {
      value: {
        type: Number | String | Boolean
      },
      valueKey: {
        type: String,
        default: null
      },
      optionLabel: {
        type: String | Number,
        default: 'label'
      },
      optionValue: {
        type: Number | String | Object,
        default: 'optionValue'
      },
      optionSize: {
        type: Number,
        default: 10
      },
      options: {
        type: Array,
        default: () => []
      }
    },
    data() {
		  return {
        currentValue: this.value,
        tempStash: [],
        searchOptions: [],
        OptSize: this.optionSize
      }
    },
    mounted() {
      this.init()
    },
    directives: {
      load: {
        bind(el, binding) {
          const SELECT_DOM = el.querySelector('.el-select-dropdown .el-select-dropdown__wrap')
          SELECT_DOM.addEventListener('scroll', function () {
            let isBottom = this.scrollHeight - this.scrollTop <= this.clientHeight
            if (isBottom) {
              binding.value()
            }
          })
        }
      }
    },
    watch: {
      value(newVal, oldVal) {
        if(newVal != oldVal) {
          this.$nextTick(() => {
            this.currentValue = this.value
          })
        }
      },
      options: {
        handler(newVal, oldVal) {
          if(newVal != oldVal) {
            this.$nextTick(() => {
              this.tempStash = this.options
              this.searchOptions = this.tempStash
            })
          }
        },
        // deep: true,
        // immediate: true
      }
    },
    methods: {
		  async init() {
        this.tempStash = this.options
        this.searchFilter(this.value)
      },
      searchFilter(query) {
        this.currentValue = query
        if (query) {
          this.searchOptions = this.tempStash
          this.searchOptions = this.searchOptions.filter((item) => {
            if (item[this.optionLabel].includes(query)) {
              return item
            }
          })
          // if (this.options.length === 0) {
          //   this.searchOptions.push({ [this.optionValue]: '', [this.optionLabel]: '' })
          // }
        } else {
          this.searchOptions = this.tempStash
        }
      },
      loadMore(size) {
		    if (size < 8) this.OptSize = 10 // 超过 7 条则才可滚动
		    return () => {
		      this.OptSize += 5
        }
      },
      handleChange(value) {
        this.$emit('change', value)
      },
      handleClear() {
        this.searchOptions = this.tempStash
        this.$emit('clear')
      }
    }
  }
</script>

<style scoped>

</style>
