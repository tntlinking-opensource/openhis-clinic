<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='70%'
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules'
      ref='noticeSendForm' label-width='120px' label-position='top' class='edit-form'>
        <el-row :gutter="20">
          <el-col :span='24/2'>
            <el-form-item label='标题' prop='title' >
              <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.title' :maxlength='50' :placeholder='dialogProps.action == "view"? "" : "请输入标题"' autofocus></el-input>
            </el-form-item>
         </el-col>
          <el-col :span='24/2'>
            <el-form-item label='接收人' prop='receivedBy' >
              <el-cascader
                :disabled='dialogProps.action == "view"'
                :key="cascaderKey"
                v-model="bizFormModel.receivedBy"
                :options="options"
                :props="props"
                collapse-tags
                clearable></el-cascader>
            </el-form-item>
          </el-col>
        </el-row>
      <el-row style="margin-top:10px" :gutter="20">
         <el-col :span='24/4'>
            <el-form-item  label-width="0" prop='isSite' >
              <el-checkbox :disabled='dialogProps.action == "view"' border  label="是否站内消息" v-model='bizFormModel.isSite' active-color='#13ce66' inactive-color='#dbdfe6' :true-label="'1'" :false-label="'0'"></el-checkbox>
            </el-form-item>
         </el-col>
        <el-col :span='24/4'>
          <el-form-item label-width="0" prop='isEmail' >
            <el-checkbox :disabled='dialogProps.action == "view"' border label="是否邮件消息" v-model='bizFormModel.isEmail' active-color='#13ce66' inactive-color='#dbdfe6' :true-label="'1'" :false-label="'0'"></el-checkbox>
          </el-form-item>
        </el-col>
        <el-col :span='24/4'>
          <el-form-item label-width="0" prop='isWechat' >
            <el-checkbox :disabled='dialogProps.action == "view"' border label="是否微信端消息" v-model='bizFormModel.isWechat' active-color='#13ce66' inactive-color='#dbdfe6' :true-label="'1'" :false-label="'0'"></el-checkbox>
          </el-form-item>
        </el-col>
         <el-col :span='24/4'>
              <el-form-item label-width="0" prop='isSms' >
                <el-checkbox :disabled='dialogProps.action == "view"' border label="是否发送短信" v-model='bizFormModel.isSms' active-color='#13ce66' inactive-color='#dbdfe6' :true-label="'1'" :false-label="'0'"></el-checkbox>
              </el-form-item>
          </el-col>
      </el-row>
      <el-row>
        <el-col :span='24'>
          <el-tabs tab-position="top" >
            <el-tab-pane label="站内消息内容" :disabled="bizFormModel.isSite === '0'">
              <div style="height: 345px">
                   <Editor ref="editorSite"  v-model="bizFormModel.siteContent" :disabled="bizFormModel.isSite === '0'  || dialogProps.action == 'view'">
                   </Editor>
              </div>
            </el-tab-pane>
            <el-tab-pane label="邮件消息内容" :disabled="bizFormModel.isEmail === '0'">
              <div style="height: 345px">
                <Editor ref="editorEmail"  v-model="bizFormModel.emailContent"  :disabled="bizFormModel.isEmail === '0' || dialogProps.action == 'view'">
                </Editor>
              </div>
            </el-tab-pane>
            <el-tab-pane label="微信消息内容" :disabled="bizFormModel.isWechat === '0'">
              <el-form-item label-width="0">
                <el-input type="textarea"  v-model="bizFormModel.wechatContent" :disabled="bizFormModel.isWechat === '0'"></el-input>
              </el-form-item>
            </el-tab-pane>
            <el-tab-pane label="短信消息内容" :disabled="bizFormModel.isSms === '0'">
              <el-form-item label-width="0">
                <el-input type="textarea" :disabled="bizFormModel.isSms === '0'" v-model="bizFormModel.smsContent"></el-input>
              </el-form-item>
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
      <!--  <el-row>
        <el-col :span='24/2'>
          <el-form-item label='站内消息内容' prop='siteContent' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.siteContent' :maxlength='1000' :placeholder='dialogProps.action == "view"? "" : "请输入站内消息内容"' ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span='24/2'>
          <el-form-item label='微信消息内容' prop='wechatContent' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.wechatContent' :maxlength='500' :placeholder='dialogProps.action == "view"? "" : "请输入微信消息内容"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span='24/2'>
              <el-form-item label='邮件消息内容' prop='emailContent' >
                <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.emailContent' :maxlength='1000' :placeholder='dialogProps.action == "view"? "" : "请输入邮件消息内容"' ></el-input>
              </el-form-item>
        </el-col>
        <el-col :span='24/2'>
          <el-form-item label='短信消息内容' prop='smsContent' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.smsContent' :maxlength='500' :placeholder='dialogProps.action == "view"? "" : "请输入短信消息内容"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>-->
          <el-row style="margin-top:10px">
            <el-col >
                <el-form-item label='备注信息' label-width="70px" prop='remarks' >
                  <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.remarks' type='textarea'
                            :autosize="{ minRows: 3}"
                            :maxlength='255' :placeholder='dialogProps.action == "view"? "" : "请输入备注信息"'  clearable></el-input>
                  </el-form-item>
            </el-col>
          </el-row>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("noticeSendForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { saveNoticeSend } from '@/api/noticesend/noticeSend'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
import { getCompanyTree } from '@/api/noticesend/noticeSend'
import Editor from  "./editor.vue"
export default {
  extends: BaseUI,
  name: 'noticeSend-form',
  components: {
    OperationIcon,
    Editor
  },
  data() {
    return {
      bizFormModel: this.initFormModel(),
      cascaderKey:0,
      options:[],
      flage:false,
      props: { multiple: true,
        emitPath:false,
        value: 'id',
        label: 'name',
        children: 'children' },  //多选
        
      dialogProps: {
        visible: false,
        action: '',
        title: '',
        
      },
      formRules: {
        'title': [
            { required: true, message: '请输入标题', trigger: 'blur' }
        ],
        'receivedBy': [
            { required: true, message: '请输入接收人', trigger: 'blur' }
        ],
      }
    }
  },
  watch: {
    'bizFormModel.isSite':{     //站内消息
      handler(newValue,old){
        if(newValue === "0"){
          this.bizFormModel.siteContent = "";
          this.$refs.editorSite.content = "";
        }
      }
    },
    'bizFormModel.isEmail':{     //邮件消息
      handler(newValue,old){
        if(newValue === "0"){
          this.bizFormModel.emailContent = "";
          this.$refs.editorEmail.content = "";
        }
      }
    },
    'bizFormModel.isWechat':{     //微信消息
      handler(newValue,old){
        if(newValue === "0"){
          this.bizFormModel.wechatContent = "";
        }
      }
    },
    'bizFormModel.isSms':{        //短信消息
      handler(newValue,old){
        if(newValue === "0"){
          this.bizFormModel.smsContent = "";
        }
      }
    },
  },
  props: {
    // 权限
    permission: {
      type: Object
    }
  },
  methods: {
    onSubmit(formName) {
      this.flage=true
      console.log(this.bizFormModel);
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave()
        } else {
          this.flage=false
          return false
        }
      });
    },
    doSave() {
      this.setLoad()
      this.bizFormModel.receivedBy = this.bizFormModel.receivedBy.toString();
      saveNoticeSend(this.bizFormModel).then(responseData => {
        this.flage=false
        if(responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.flage=false
        this.outputError(error)
      })
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改消息发送公告'
    },
    onDialogClose() {
      this.dialogProps.visible = false
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['noticeSendForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'senderId': currentUser.id,   // 消息发送人id
        'title': '',   // 标题
        'isSite': '0',   // 是否站内消息
        'isWechat': '0',   // 是否微信端消息
        'isEmail': '0',   // 是否邮件
        'isSms': '0',   // 是否短信
        'siteContent': '',   // 站内消息内容
        'wechatContent': '',   // 微信消息内容
        'emailContent': '',   // 邮件消息内容
        'smsContent': '',   // 短信消息内容
        'receivedBy': '',   // 接收人
        'remarks': '',   // 备注信息

      }
    },
    initOptions(This) {
      This.receivedBy = This.receivedBy.split(",");
    },
    companyTree(){
      getCompanyTree('2').then(result =>{
        if(result.code === "100"){
          this.options = result.data;
        }
      });
    },
  },
  mounted: function() {
    this.companyTree();
    this.$nextTick(() => {
      this.$on('openViewNoticeSendDialog', function(noticeSend) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看消息发送公告'
        this.bizFormModel = noticeSend
        this.dialogProps.visible = true
        this.initOptions(this.bizFormModel)
      })
      this.$on('openEditNoticeSendDialog', function(noticeSend) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改消息发送公告'
        this.bizFormModel = noticeSend
        this.dialogProps.visible = true
        this.initOptions(this.bizFormModel)
      })
      this.$on('openAddNoticeSendDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加消息发送公告'
        this.bizFormModel = this.initFormModel()
        this.dialogProps.visible = true
        this.initOptions(this.bizFormModel)
      })
      this.$on('openCopyNoticeSendDialog', function(noticeSend) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加消息发送公告'
        this.bizFormModel = noticeSend
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
        this.initOptions(this.bizFormModel)
      })
    })
  }
}
</script>

