<template>
  <span>{{dicItemName}}</span>
</template>

<script>
  //针对所有数据字典的显示，传入类型和值即可显示对应的名称
  import {
    listDictItemAll
  } from "@/api/sys/dictItem";
  let alldicitemList = null;

  export default {
    props: {
      dictValue: {
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
        dicItemName: ''
      };
    },
    computed: {},
    watch: {
      dictValue() {
        this.refreshName();
      },
      type() {
        this.refreshName();
      }
    },
    methods: {
      refreshName() { 
        if (!this.type || !this.dictValue || !alldicitemList) {
          this.dicItemName = '';
          return;
        }
        for (let i in alldicitemList) {
          let d = alldicitemList[i];
          if (d.dictType && d.dictType.code == this.type && d.value == this.dictValue) {
            this.dicItemName = d.name;
            return;
          }
        }
        this.dicItemName = '-';
      },
      loadAllDicItem() {
        if (alldicitemList != null) {
          this.refreshName();
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
            alldicitemList = res.data;
            that.refreshName();
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
