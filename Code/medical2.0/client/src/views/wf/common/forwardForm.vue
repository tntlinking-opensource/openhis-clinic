<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' :append-to-body='true' @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
    </div>

    <el-form :model='userModel' :rules='formRules' ref='forwardUserForm' label-width='0px' label-position='right' class='edit-form'>
      <el-form-item prop='actUser'>
        <el-select v-model='userModel.actUser' value-key='id' filterable placeholder='请选择用户'>
          <el-option v-for='user in users' :key='user.id' :label='user.name' :value='{id: user.id, name: user.name}'></el-option>
        </el-select>
      </el-form-item>
    </el-form>

    <span slot='footer' class='dialog-footer'>
      <el-button type='primary' :plain='true' @click='onOk()'>确 定</el-button>
      <el-button :plain='true' @click='onDialogClose()'>取 消</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import BaseUI from '@/views/components/baseUI'
import { listUserAll } from '@/api/admin/user'
export default {
  extends: BaseUI,
  name: 'forward-user',
  components: {

  },
  data() {
    return {
      users: [],
      userModel: {
        actUser: null
      },
      dialogProps: {
        visible: false,
        action: '',
        title: '选择办理人'
      },
      formRules: {
        'actUser': [
            { required: true, message: '请选择办理人', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    onDialogOpen() {
/*      this.$nextTick(() => {

      })*/
    },
    onOk() {
      this.$refs['forwardUserForm'].validate(valid => {
        if (valid) {
          this.$emit('forward-user-finished', this.userModel.actUser)
          this.dialogProps.visible = false
        }
      })
    },
    onDialogClose() {
      this.dialogProps.visible = false
    },
    init() {
      this.setLoad()
      listUserAll({}).then(responseData => {
        if(responseData.code == 100) {
          this.users = responseData.data
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    }
  },
  props: {
    wfDaiog: {
      type: Object,
      default: () => {return null}
    }
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openForwardDialog', function() {
        this.users = []
        this.userModel.actUser = null
        this.init()
        this.dialogProps.visible = true
      })
    })
  }
}
</script>
