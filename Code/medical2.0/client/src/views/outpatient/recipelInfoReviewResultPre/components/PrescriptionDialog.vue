<template>
  <el-dialog :visible.sync="dialogVisible" title="处方查询" width="50%">
    <el-form :model="formData" ref="prescriptionForm" label-width="120px">
      <el-form-item label="处方编号">
        <el-input v-model="formData.prescriptionCode" readonly></el-input>
      </el-form-item>
      <el-form-item label="处方医生">
        <el-input v-model="formData.doctorName" readonly></el-input>
      </el-form-item>
      <el-form-item label="处方日期">
        <el-input v-model="formData.prescriptionDate" readonly></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="closeDialog">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: "PrescriptionDialog",
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
