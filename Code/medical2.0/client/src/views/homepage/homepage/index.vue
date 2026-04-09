<template>
  <div class="home-container">
    <div class="user-info-wrapper">
      <div class="user-info-card">
        <div class="user-avatar">
          <i class="el-icon-user-solid"></i>
        </div>
        <div class="user-details">
          <div class="welcome-text">欢迎登录</div>
          <div class="user-name">{{ currentUser.name || '未知用户' }}</div>
          <div class="clinic-info">
            <div class="info-item">
              <i class="el-icon-office-building"></i>
              <span class="label">机构名称：</span>
              <span class="value">{{ clinicName }}</span>
            </div>
            <div class="info-item">
              <i class="el-icon-postcard"></i>
              <span class="label">机构编码：</span>
              <span class="value">{{ institutionCode }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getLocalCurrentUser } from '@/utils/auth'
export default {
  name: 'Home',
  data() {
    return {
      currentUser: {}
    }
  },
  computed: {
    clinicName() {
      return (this.currentUser.company && this.currentUser.company.name) || '未知诊所'
    },
    institutionCode() {
      return (this.currentUser.company && this.currentUser.company.fixmedinsCode) || '暂无编码'
    }
  },
  beforeCreate() {
    window.currentUser = getLocalCurrentUser()
  },
  created() {
    this.currentUser = window.currentUser || {}
  }
}
</script>

<style lang="scss" scoped>
.home-container {
  width: 100%;
  min-height: 100vh;
  background: #f5f7fa;
  padding-top: 100px;
  box-sizing: border-box;
}

.user-info-wrapper {
  display: flex;
  justify-content: center;
  padding: 0 20px;
}

.user-info-card {
  background: #fff;
  border-radius: 24px;
  padding: 100px 120px;
  display: flex;
  align-items: center;
  gap: 50px;
  box-shadow: 0 20px 60px rgba(64, 158, 255, 0.15);
  min-width: 600px;
}

.user-avatar {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #4facfe 0%, #409EFF 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60px;
  color: #fff;
  flex-shrink: 0;
  box-shadow: 0 10px 30px rgba(64, 158, 255, 0.3);
}

.user-details {
  flex: 1;
}

.welcome-text {
  font-size: 16px;
  color: #909399;
  margin-bottom: 12px;
  text-transform: uppercase;
  letter-spacing: 3px;
}

.user-name {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 30px;
}

.clinic-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 18px;
  color: #606266;
}

.info-item i {
  margin-right: 12px;
  font-size: 20px;
  color: #409EFF;
}

.info-item .label {
  color: #909399;
  margin-right: 8px;
  min-width: 120px;
}

.info-item .value {
  color: #303133;
  font-weight: 500;
}
</style>
