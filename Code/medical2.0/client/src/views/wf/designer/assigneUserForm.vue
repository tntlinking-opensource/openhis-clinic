<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' :append-to-body='true' @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
    </div>

    <el-form :model='userModel' :rules='formRules' ref='assigneUserForm' label-width='0px' label-position='right' class='edit-form'>
      <el-form-item prop='actUsers'>
        <el-select v-model='userModel.actUsers' value-key='id' multiple filterable placeholder='请选择用户'>
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
  name: 'assigne-user',
  components: {

  },
  data() {
    return {
      element: null, // 当前配置的元素
      users: [],
      userModel: {
        actUsers: []
      },
      dialogProps: {
        visible: false,
        action: '',
        title: '选择用户'
      },
      formRules: {
        'actUsers': [
            { required: true, message: '请选择用户', trigger: 'change' }
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
      this.$refs['assigneUserForm'].validate(valid => {
        if (valid) {
          this.$emit('assinge-finished', this.element, this.userModel.actUsers)
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
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openAssigneDialog', function(element) {
        this.element = element
        if(element.businessObject.assignee) {
          this.userModel.actUsers = JSON.parse(element.businessObject.assignee)
        } else {
          this.userModel.actUsers = []
        }
        this.users = []
        this.init()
        this.dialogProps.visible = true
      })
    })
  }
}
</script>
