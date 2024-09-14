<template>
  <el-select v-model="selectedValue" filterable placeholder="请选择" clearable>
    <el-option v-for="t in selectedList" :label="t.name" :value="t.value" :key="t.value">
    </el-option>
  </el-select>
</template>

<script>
  //针对所有数据字段的下拉选择控件
  import {
    listDictItemAll
  } from "@/api/sys/dictItem";
  let alldicitemSelectList = null;

  export default {
    props: {
      value: {
        type: String,
        default: ''
      },
      type: {
        type: String,
        default: null
      }
    },
    data() {
      return {
        selectedList: [],
        selectedValue: null
      };
    },
    computed: {},
    watch: {
      value() {
        this.selectedValue = this.value;
      },
      selectedValue() {
        this.$emit('input', this.selectedValue);
      },
      type() {
        this.refreshSelectedList();
      }
    },
    methods: {
      refreshSelectedList() {
        if (!this.type || !alldicitemSelectList) {
          this.selectedList = [];
          return;
        }

        let arr = [];
        for (let i in alldicitemSelectList) {
          let d = alldicitemSelectList[i];
          if (d.dictType && d.dictType.code == this.type) {
            arr.push(d);
          }
        }
        this.selectedList = arr;
      },
      loadAllDicItem() {
        if (alldicitemSelectList != null) {
          this.refreshSelectedList();
          return;
        }
        let that = this;
        let search = {
          params: [],
          offset: 0,
          limit: 100,
          columnName: "", // 排序字段名
          order: "", // 排序
        };

        listDictItemAll(search).then(res => {
          if (res.code == '100') {
            alldicitemSelectList = res.data;
            that.refreshSelectedList();
          } else {
            console.log('load all dicitem error:' + res.code);
          }
        }).catch(error => {})
      }
    },
    mounted() {
      this.loadAllDicItem();
    },
  };
</script>

<style>
</style>
