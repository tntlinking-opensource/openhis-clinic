<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' :append-to-body='true' @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
    </div>
    <el-form ref='selectUserForm' :model='selectModel' label-width='0px' label-position='right' class='edit-form'>
      <el-row v-for='(node, idxNode) in selectModel.actNodes' :key='idxNode'>
        <el-card shadow='hover'>
          <div slot='header' class='clearfix'>
            <span>{{node.name}}</span>
            <!-- <el-button style='float: right; padding: 3px 0' type='text'>操作按钮</el-button> -->
          </div>
            <el-form-item :prop="`actNodes.${idxNode}.assignees`" :rules="rules.assignees">
              <el-radio-group v-if='node.type == "SingleSelect"' v-model='node.assignees[0]'>
                <el-radio-button v-for='(user, index) in node.users' :key='index' :label='user'>{{user.name}}</el-radio-button>
              </el-radio-group>
              <el-checkbox-group v-else v-model='node.assignees'>
                <el-checkbox-button v-for='(user, index) in node.users' :key='index' :label='user'>{{user.name}}</el-checkbox-button>
              </el-checkbox-group>
            </el-form-item>
        </el-card>
        <span>&nbsp;</span>
      </el-row>
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
export default {
  extends: BaseUI,
  name: 'select-user',
  components: {

  },  
  data() {
    return {
      selectModel: {
        actNodes: []
      },
      dialogProps: {
        visible: false,
        action: '',
        title: '选择办理人'
      },
      rules: {
        assignees:[
          { required: true, message: '请选择办理人', trigger: 'change'},
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
      this.$refs.selectUserForm.validate(valid => {
        if (valid) {
          this.$emit('select-user-finished', this.selectModel.actNodes)
          this.dialogProps.visible = false
        }
      });      

    },
    onDialogClose() {
      this.dialogProps.visible = false
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
      this.$on('openUserDialog', function(actNodes) {
        this.selectModel.actNodes = actNodes

        this.dialogProps.visible = true
      })
    }) 
  }  
}
</script>