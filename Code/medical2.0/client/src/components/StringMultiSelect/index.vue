<template>
  <div>
   <el-select v-model='arrSelected' multiple :placeholder=placeholder >
    <el-option v-for='item in opts' :key='item[valKey]' :label='item[label]' :value='item[valKey]'></el-option>
  </el-select>
</div>
</template>

<script>
import { validatenull } from '@/utils/validate'

export default {
  name: 'StringMultiSelect',
  data() {
    return {
      arrSelected: []
    }
  },
  props: {
    // 接受外部v-model传入的值
    value: {
      type: String
    },
    placeholder: {
      type: String,
      default: () => {return '请选择'}
    },
    // 选择器选择项
    opts: {
      type: Array,
      default: () => {return []}
    },

    valKey: {
      type: String,
      required: true
    },
    label: {
      type: String,
      required: true
    }
  },
  watch: {
    value(val, oldVal) {
      if(val != oldVal) {
        this.init()
      }
    },
    // 判断下拉框的值是否有改变
    arrSelected(val, oldVal) {
      // console.log('new: %s, old: %s', val, oldVal)
      if(val!=oldVal){
        if(this.arrSelected.length > 0) {
          this.$emit('input', this.arrSelected.join(',')); 
        } else {
          this.$emit('input', null); 
        }
      }
    }
  },  
  mounted(){
    // 初始值
    this.init()
  },
  methods: {
    init() {
      if(validatenull(this.value)) {
        this.arrSelected = []
      } else {
        this.arrSelected = this.value.split(',')
      }
    }
  }
}

</script>