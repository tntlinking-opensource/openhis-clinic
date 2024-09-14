<template>
  <el-dialog title='个人信息' :visible.sync='dialogVisible' width='560px'
    @open='handleDialogOpen()' :lock-scroll='true' v-loading='loading'>
    <div slot='title' class='dialog-header'>个人信息</div>
    <el-form :model='userModel' :rules='rules'
      ref='personalInfoForm' label-width='80px' label-position='right'>
      <el-form-item label='姓名' prop='name'>
        <el-col :span='16'><el-input ref='username' :maxlength='32' v-model='userModel.name' autofocus></el-input></el-col>
      </el-form-item>
      <el-form-item label='登录名' prop='loginName'>
        <el-col :span='16'><el-input disabled :maxlength='32' v-model='userModel.loginName'></el-input></el-col>
      </el-form-item>
      <el-form-item label='手机号' prop='phone'>
        <el-col :span='16'><el-input :maxlength='11' v-model='userModel.phone'></el-input></el-col>
      </el-form-item>
      <!-- <el-form-item label='邮箱' prop='email'>
        <el-col :span='16'><el-input :maxlength='32' v-model='userModel.email'></el-input></el-col>
      </el-form-item> -->
      <el-form-item><el-checkbox v-model='userModel.loginPasswordUpdate' @change='handleChangePasswordChanged'>修改密码</el-checkbox></el-form-item>
      <el-form-item label='密码' prop='loginPassword'>
        <el-col :span='16'>
          <el-input type='password' ref='password' :disabled='!userModel.loginPasswordUpdate' v-model='userModel.loginPassword' auto-complete='off'></el-input>
        </el-col>
      </el-form-item>
      <el-form-item label='确认密码' prop='loginPasswordConfirm'>
        <el-col :span='16'>
          <el-input type='password' v-model='userModel.loginPasswordConfirm' :disabled='!userModel.loginPasswordUpdate' auto-complete='off'></el-input>
        </el-col>
      </el-form-item>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button type='primary' :plain='true' @click="submitForm('personalInfoForm')">确 定</el-button>
      <el-button :plain='true' @click="handleDialogClose()">取 消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { getCurrentUser, updateCurrentUser } from '@/api/admin/common/personalInfo'
import BaseUI from '@/views/components/baseUI'
import { getLocalCurrentUser } from '@/utils/auth'
export default {
  extends: BaseUI,
  name: 'personal-info',
  data() {
    var validatePassword = (rule, value, callback) => {
      if (this.userModel.loginPasswordUpdate && (value === '' || value === undefined)) {
        callback(new Error('请输入密码'));
      } else {
        if (this.userModel.loginPasswordConfirm !== '') {
          this.$refs.personalInfoForm.validateField('loginPasswordConfirm');
        }
        callback()
      }
    }
    var validateCheckPassword = (rule, value, callback) => {
      if (this.userModel.loginPasswordUpdate && (value === '' || value === undefined)) {
        callback(new Error('请再次输入密码'))
      } else if (this.userModel.loginPasswordUpdate && (value !== this.userModel.loginPassword)) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      loading: false,
      userModel: {
        id: '',
        name: '',
        loginName: '',
        phone:'',
        email:'',
        loginPassword: '',
        loginPasswordUpdate: false
      },
      dialogVisible: false,
      rules: {
        name: [
          { required: true, message: '请输入用户名称', trigger: 'blur' },
          { min: 3, max: 32, message: '长度在 3 到 32 个字符', trigger: 'blur' }
        ],
        loginName: [
          { required: true, message: '请输入登录名', trigger: 'blur' },
          { min: 3, max: 32, message: '长度在 3 到 32 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { validator: this.isMobileNumber, trigger: "blur" }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { validator: this.isEmail, trigger: "blur" }
        ],
        loginPassword: [
          { validator: validatePassword, trigger: 'blur' }
        ],
        loginPasswordConfirm: [
          { validator: validateCheckPassword, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    isMobileNumber(rule, value, callback) {
      if (!value) {
        return new Error("请输入手机号码");
      } else {
        const reg = /^1[3|4|5|6|7|8|9][0-9]\d{8}$/;
        const isPhone = reg.test(value);
        value = Number(value); //转换为数字
        if (typeof value === "number" && !isNaN(value)) {//判断是否为数字
          value = value.toString(); //转换成字符串
          if (value.length !== 11 || !isPhone) { //判断是否为11位手机号
            callback(new Error("手机号码格式如:138xxxx8888"));
          } else {
            callback();
          }
        } else {
          callback(new Error("请输入电话号码"));
        }
      }
    },
    //邮箱校验
     isEmail(rule, value, callback) {
      if (!value) {
        callback();
      } else {
        const reg = /^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/;
        const email = reg.test(value);
        if (!email) {
          callback(new Error("邮箱格式如:xxx@163.com"));
        } else {
          callback();
        }
      }
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doUpdatePersonalInfo()
        } else {
          return false;
        }
      })
    },
    doUpdatePersonalInfo() {
      this.loading = true
      if(!this.userModel.loginPasswordUpdate) {
          this.userModel.password = ''
      }
      updateCurrentUser(this.userModel).then(responseData => {
        if(responseData.code == 100) {
          let currentUser = window.currentUser
          currentUser.name = this.userModel.name
          currentUser.phone = this.userModel.phone
          currentUser.email = this.userModel.email
          getLocalCurrentUser(currentUser)
          this.$emit('username-changed')
          this.userModel.loginPasswordUpdate = false
          this.loading = false
          this.dialogVisible = false
        } else {
          this.showMessage(responseData)
        }
      })
      .catch(error => {
        this.outputError(error)
      })
    },
    handleDialogClose() {
      this.dialogVisible = false
    },
    handleDialogOpen() {
      this.$nextTick(() => {
        this.$refs['personalInfoForm'].clearValidate()
        this.$refs['username'].focus()
      })
    },
    handleChangePasswordChanged(val) {
      this.$refs['personalInfoForm'].clearValidate()
      if(val) {
        this.$nextTick(() => {
            this.$refs['password'].focus()
        })
      }
    }
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openPersonalInfoDialog', function() {
        this.loading = true
        this.changePassword = false
        getCurrentUser()
        .then(responseData => {
          if(responseData.code == 100) {
            this.userModel = responseData.data
            this.userModel.password = ''
            this.dialogVisible = true
          } else {
            this.showMessage(responseData)
          }
        })
        .catch(error => {
          this.outputError(error)
        })
      })
    })
  }
}
</script>



