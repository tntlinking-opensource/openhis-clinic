<template>
  <el-dialog :visible.sync="dialogVisible" title="取药结果查询" width="70%" :close-on-click-modal="false"  @close="closeDialog" >
    <el-form :model="formData" ref="medicineForm" label-width="120px">
      <el-row>
        <!-- 让两个表单项并排显示 -->
        <el-col :span="8" label-align="left" >
          <el-form-item label="医保处方编号">
            <el-input v-model="formData.hiRxno" readonly style="width: 100%;"></el-input>
          </el-form-item>
        </el-col>

        <el-col :span="8" label-align="left">
          <el-form-item label="医保结算时间">
            <el-input v-model="formData.setlTime" readonly style="width: 100%;"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 处方取药信息 -->
      <el-col :span="8" style="margin-top: 5px;font-size: 12px">处方取药信息</el-col>
      <!-- 渲染表格 -->
      <el-table :data="formData.eltdelts" style="width: 100%">
        <el-table-column prop="medinsListCodg" label="医药机构药品编号"></el-table-column>
        <el-table-column prop="drugGenname" label="通用名"></el-table-column>
        <el-table-column prop="drugProdname" label="药品商品名"></el-table-column>
        <el-table-column prop="drugDosform" label="药品剂型"></el-table-column>
        <el-table-column prop="drugSpec" label="药品规格"></el-table-column>
        <el-table-column prop="ent" label="数量"></el-table-column>
        <el-table-column prop="aprvno" label="批准文号"></el-table-column>
        <el-table-column prop="bchno" label="批次号"></el-table-column>
        <el-table-column prop="manuLotnum" label="生产批号"></el-table-column>
        <el-table-column prop="prdrName" label="生产厂家"></el-table-column>
      </el-table>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="closeDialog">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: "MedicineDialog",
  props: {
    dialogVisible: {
      type: Boolean,
      default: false
    },
    formData: {
      type: Object,
      default: () => ({
      })
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:dialogVisible', false); // 关闭模态框
    }
  }
};
</script>
