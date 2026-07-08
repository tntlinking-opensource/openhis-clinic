<template>
  <el-select
    v-model="innerValue"
    value-key="value"
    filterable
    clearable
    :placeholder="placeholder"
    :disabled="disabled"
    :size="size"
    @clear="onClear"
    @change="onChange"
  >
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.name"
      :value="item"
    />
  </el-select>
</template>

<script>
import { listDictItemAll } from '@/api/sys/dictItem'
import { getDictItemsByCode } from '@/utils/dictCache'

export default {
  name: 'DictSelect',
  props: {
    value: {
      type: Object,
      default: () => ({ value: null, name: null })
    },
    dictTypeId: {
      type: String,
      default: ''
    },
    dictCode: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: '请选择'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    size: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      options: []
    }
  },
  computed: {
    innerValue: {
      get() {
        return this.value
      },
      set(val) {
        this.$emit('input', val)
      }
    }
  },
  created() {
    this.loadDictItems()
  },
  methods: {
    async loadDictItems() {
      try {
        if (this.dictCode) {
          // 推荐方式：按字典类型 code 获取（带缓存）
          this.options = await getDictItemsByCode(this.dictCode)
        } else if (this.dictTypeId) {
          // 兼容方式：按 dict_type_id 获取
          const res = await listDictItemAll({
            params: [{ columnName: 'dict_type_id', queryType: '=', value: this.dictTypeId }]
          })
          if (res.code === 100) {
            this.options = res.data || []
          }
        }
      } catch (error) {
        console.error('加载字典项失败:', error)
      }
    },
    onClear() {
      this.$emit('input', { value: null, name: null })
      this.$emit('change', { value: null, name: null })
    },
    onChange(val) {
      this.$emit('input', val)
      this.$emit('change', val)
    }
  }
}
</script>
