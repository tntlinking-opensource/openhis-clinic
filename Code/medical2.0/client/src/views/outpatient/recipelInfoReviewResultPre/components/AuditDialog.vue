<template>
  <el-dialog :visible.sync="dialogVisible" title="电子处方审核查询" width="60%"
             :close-on-click-modal="false"  @close="closeDialog"
  ><el-form :model="formData" ref="auditForm" label-width="120px">
    <el-row gutter={20}>
      <el-col :span="8">
        <el-form-item label="医保处方编号">
          <el-input v-model="formData.hiRxno" readonly></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="医保医师姓名">
          <el-input v-model="formData.pharName" readonly></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="医保医师代码">
          <el-input v-model="formData.pharCode" readonly></el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row gutter={20}>
      <el-col :span="8">
        <el-form-item label="处方审核状态">
          <el-input v-model="formData.rxChkStasCodg" readonly></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="处方审核意见">
          <el-input v-model="formData.rxChkOpnn" readonly></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="处方审核时间">
          <el-input v-model="formData.rxChkTime" readonly></el-input>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="closeDialog">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: "AuditDialog",
  props: {
    dialogVisible: {
      type: Boolean,
      default: false
    },
    formData: {
      type: Object,
      default: () => ({
        reviewerName: '',
        reviewResult: '',
        reviewContent: ''
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
