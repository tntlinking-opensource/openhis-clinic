<template>
  <el-select v-model="selectedSupplierID" filterable placeholder="请选择供应商" clearable>
    <el-option v-for="supplierxz in supplierLists" :label="supplierxz.name" :value="supplierxz.id" :key="supplierxz.id">
    </el-option>
  </el-select>
</template>

<script>
  import {
    listSupplierPage,
    getSupplierById,
    listSupplierAll
  } from "@/api/stock/supplier";
  import BaseUI from "@/views/components/baseUI";
  export default {
    extends: BaseUI,
    props: {
      value: {
        type: String,
        default: ''
      },
      type:{
        type:String,
        default:'1'
      }
    },

    data() {
      return {
        supplierLists: [], //不同的供应商
        selectedSupplierID: null,
        supplierSearch: {
          params: [{ columnName: "company_id", queryType: "=", value: "" }],
          offset: 0,
          limit: 100,
          columnName: "", // 排序字段名
          order: "", // 排序
        },
      };
    },
    computed:{
    },
    watch: {
      value() {
        this.selectedSupplierID = this.value;
      },
      selectedSupplierID() {
        this.$emit('input', this.selectedSupplierID);
      },
      type()
      {
        this.loadSupplierList();
      }
    },
    methods: {
      loadSupplierList() {
          this.supplierLists =[];
        this.supplierSearch.params = [
          {
            columnName: "company_id",
            queryType: "=",
            value: currentUser.company.id,
          },
          {
            columnName: "type",
            queryType: "=",
            value: this.type,
          }
        ];
        listSupplierAll(this.supplierSearch).then((responseData) => {
          // console.log(responseData);
          if (responseData.code == 100) {
            this.supplierLists = responseData.data
          }
        }).catch(() => {})
      }
    },
    mounted() {
      this.loadSupplierList();
    },
  };
</script>

<style>
</style>
