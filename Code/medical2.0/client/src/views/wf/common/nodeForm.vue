<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' :append-to-body='true' @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
    </div>
    <el-form ref='selectNoeForm' :model='selectModel' label-width='0px' label-position='right' class='edit-form'>
      <el-form-item prop="checkNodes" :rules="rules.checkNodes">
        <el-checkbox-group v-model="selectModel.checkNodes">

            <el-row v-for='(node, idxNode) in actNodes' :key='idxNode'>
              <el-card  shadow='hover'>
                <div slot='header' class='clearfix'>
                  <span>
                     <el-checkbox :label='node' :disabled='inExclusion(node)'>{{node.name}}</el-checkbox>
                  </span>
                </div>


                  <el-radio-group v-if='node.type == "SingleSelect"' v-model='node.assignees[0]' :disabled='!inSelected(node)'>
                    <el-radio-button v-for='(user, index) in node.users' :key='' :label='user' :key='index'>{{user.name}}</el-radio-button>
                  </el-radio-group>
                  <el-checkbox-group v-else v-model='node.assignees' :disabled='!inSelected(node)'>
                    <el-checkbox-button v-for='(user, index) in node.users' :label='user' :key='index'>{{user.name}}</el-checkbox-button>
                  </el-checkbox-group>




              </el-card>
              <span>&nbsp;</span>
            </el-row>



        </el-checkbox-group>
      </el-form-item>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button type='primary' :plain=true @click='onOk()'>确 定</el-button> 
      <el-button :plain=true @click='onDialogClose()'>取 消</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import BaseUI from '@/views/components/baseUI'
export default {
  extends: BaseUI,
  name: 'select-node',
  components: {

  },  
  data() {
    var validateSelected = (rule, value, callback) => {
      if(!value || value.length <= 0) {
        callback(new Error('请选退回的节点'))
        return
      } else {
        for(let node of value) {
          if(!node.assignees || node.assignees.length <=0) {
            callback(new Error('回退节点[' + node.name + ']需选择用户'))
            return
          }
        }
      }
      callback()
    }

    return {
      actNodes: [],    // 待选节点列表
      selectModel: {
        checkNodes: []
      },
      dialogProps: {
        visible: false,
        action: '',
        title: '选择节点'
      },

      rules: {
        checkNodes:[
           { validator: validateSelected, trigger: 'blur',required: false }
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
      this.$refs.selectNoeForm.validate(valid => {
        if (valid) {
          //debugger
          this.$emit('select-node-finished', this.selectModel.checkNodes)
          this.dialogProps.visible = false
        }
      });
    },
    onDialogClose() {
      this.dialogProps.visible = false
    },
    inSelected(curNode) {
      if(this.selectModel.checkNodes.indexOf(curNode) >= 0) {
        return true
      }
      return false
    },
    inExclusion(curNode) {
      for(let node of this.selectModel.checkNodes) {
        if(node.excNodes.indexOf(curNode.id) >= 0) {
          return true
        }
      }
      return false
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
      this.$on('openNodeDialog', function(actNodes) {
        this.actNodes = actNodes
        this.dialogProps.visible = true
      })
    }) 
  }  
}
</script>