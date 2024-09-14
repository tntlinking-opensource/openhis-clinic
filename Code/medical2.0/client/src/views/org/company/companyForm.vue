<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='companyForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='租户' prop='parent.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.parent.name'></el-input>
            <el-cascader v-else v-model='bizFormModel.parent.id' :options='parent_List' :key='key_parent' ref='parentCascader' @change='onParentChange' :props='{value: "id", label: "name", checkStrictly: true, emitPath: false}' filterable clearable placeholder='请选择租户' />
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='全称' prop='fullName' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.fullName' :maxlength='512' :placeholder='dialogProps.action == "view"? "" : "请输入全称"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='名称' prop='name' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入名称"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='编码' prop='code' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.code' :maxlength='64' :placeholder='dialogProps.action == "view"? "" : "请输入编码"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='排序' prop='sort' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.sort'></el-input>
            <number-input v-else v-model="bizFormModel.sort"  :precision="2"></number-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='地址（省）' prop='addressProvince' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.addressProvince' :maxlength='10' :placeholder='dialogProps.action == "view"? "" : "请输入地址（省）"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='地址（市）' prop='addressCity' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.addressCity' :maxlength='10' :placeholder='dialogProps.action == "view"? "" : "请输入地址（市）"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='地址（区）' prop='addressRegion' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.addressRegion' :maxlength='10' :placeholder='dialogProps.action == "view"? "" : "请输入地址（区）"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='地址' prop='address' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.address' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入地址"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='电话' prop='phoneNumber' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.phoneNumber' :maxlength='21' :placeholder='dialogProps.action == "view"? "" : "请输入电话"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col>
          <el-form-item label='备注信息' prop='remarks' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.remarks' type='textarea'
                                  :maxlength='255' :placeholder='dialogProps.action == "view"? "" : "请输入备注信息"'  clearable></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='执业许可科目' prop='licenseSubject' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.licenseSubject' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入执业许可科目"' ></el-input>
          </el-form-item>
        </el-col>
                        </el-row>
      <el-row>
         <!-- 多文件上传： 诊所许可证图片 -->
        <upload-file v-if='dialogProps.visible' v-model='bizFormModel.fileIdFile' titile='诊所许可证图片' :action='dialogProps.action' :objectId='bizFormModel.fileId'></upload-file>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='诊所版本' prop='version.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.version.name'></el-input>
            <el-select v-else v-model='bizFormModel.version' value-key='id' filterable clearable placeholder='请选择诊所版本' 
              @clear='bizFormModel.version={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='version in version_List' :key='version.id' :label='version.name' :value='version'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='状态' prop='status' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.status' active-color='#13ce66' inactive-color='#dbdfe6' :active-value='1' :inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='诊所启用时间' prop='startUseDate' >
            <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.startUseDate' type='datetime' value-format='yyyy-MM-dd HH:mm:ss' :placeholder='dialogProps.action == "view"? "" : "请输入诊所启用时间"' ></el-date-picker>             
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='诊所到期时间' prop='expireDate' >
            <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.expireDate' type='datetime' value-format='yyyy-MM-dd HH:mm:ss' :placeholder='dialogProps.action == "view"? "" : "请输入诊所到期时间"' ></el-date-picker>             
          </el-form-item>
        </el-col>
      </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("companyForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { treeCompany } from '@/api/org/company'
import { listClinicVersionAll } from '@/api/clinic/clinicVersion'
import uploadFile from '@/views/components/uploadFile'
import { saveCompany } from '@/api/org/company'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'company-form',
  components: {
    uploadFile,
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
          key_parent: 1,    // el-cascader key
          parent_List: [],  // 租户
          version_List: [],  // 诊所版本
           flage:false,
       dialogProps: {
        visible: false,
        action: '',
        title: '',
       
      },
      formRules: {
        'fullName': [
            { required: true, message: '请输入全称', trigger: 'blur' }
        ],
        'name': [
            { required: true, message: '请输入名称', trigger: 'blur' }
        ],
        'code': [
            { required: true, message: '请输入编码', trigger: 'blur' }
        ],
        'sort': [
            { required: true, message: '请选择排序', trigger: 'blur' }
        ],
        'addressProvince': [
            { required: true, message: '请输入地址（省）', trigger: 'blur' }
        ],
        'addressCity': [
            { required: true, message: '请输入地址（市）', trigger: 'blur' }
        ],
        'addressRegion': [
            { required: true, message: '请输入地址（区）', trigger: 'blur' }
        ],
        'address': [
            { required: true, message: '请输入地址', trigger: 'blur' }
        ],
        'phoneNumber': [
            { required: true, message: '请输入电话', trigger: 'blur' }
        ],
      }
    }    
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
      let formData = this.createFormData(this.bizFormModel)
      saveCompany(formData).then(responseData => {
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
      createFormData(bizFormModel) {
        let formData = new FormData()
        let deletes = []
        for(let idx in bizFormModel.fileIdFile.uploads) {
          formData.append('fileIdUploads', bizFormModel.fileIdFile.uploads[idx].raw)
        }
        deletes = deletes.concat(bizFormModel.fileIdFile.deletes)
        formData.append('entity', JSON.stringify(bizFormModel))
        formData.append('deleteIds', JSON.stringify(deletes))

        return formData
      },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改公司'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['companyForm'].clearValidate()
      })
    },
    onParentChange() {
      let nodes = this.$refs['parentCascader'].getCheckedNodes()
      if(nodes.length > 0) {
        if(nodes[0] && nodes[0].data && nodes[0].data.children) {
          this.bizFormModel.sort = (nodes[0].data.children.length + 1) * 10000
        } else if(nodes[0]){
            this.bizFormModel.sort = 10000
        } else {
          this.bizFormModel.sort = (this.parent_List.length + 1) * 10000
        }
      }else{
        this.bizFormModel.sort = (this.parent_List.length + 1) * 10000
      }
    },
    initFormModel(This) {
      return {
        'parent': {     // 上级节点 租户
          'id': validatenull(This) || validatenull(This.id) ? null : This.id,
          'name': validatenull(This) || validatenull(This.name) ? null : This.name,
        },
        'fullName': '',   // 全称
        'name': '',   // 名称
        'code': '',   // 编码
        'sort': This && This.children ? (This.children.length + 1) * 10000 : (this.parent_List ? (this.parent_List.length + 1) * 10000 : 10000),
        'addressProvince': '',   // 地址（省）
        'addressCity': '',   // 地址（市）
        'addressRegion': '',   // 地址（区）
        'address': '',   // 地址
        'phoneNumber': '',   // 电话
        'remarks': '',   // 备注信息
        'licenseSubject': '',   // 执业许可科目
        'fileId': '',
        'fileIdFile': {
          deletes: [],  // 待删除（已上传）的文件列表
          uploads: []   // 待上传的文件列表
         },
        'version': {     // 诊所版本
          'id': null,
          'name': null,
        },
        'status': 1,    // 状态
        'startUseDate': '',   // 诊所启用时间
        'expireDate': '',   // 诊所到期时间

      }
    },
    initOptions(This) {
      this.key_parent++
      let parent_search = {
        params: []
      }
        // 字段对应表上filter条件
        parent_search.params.push.apply(parent_search.params, [])
      // 数据权限: 公司org_company
      this.pushDataPermissions(parent_search.params, this.$route.meta.routerId, '41040096140492800')
      this.parent_List.splice(0, this.parent_List.length)
      treeCompany(parent_search).then(responseData => {
        this.parent_List = responseData.data
      })
      let version_search = {
        params: []
      }
        // 字段对应表上filter条件
        version_search.params.push.apply(version_search.params, [{columnName: 'company_id', queryType: '=', value: function() {var user = JSON.parse(sessionStorage.getItem('currentUser')); return user.company.id;}()}])
      // 数据权限: 诊所版本clinic_version
      this.pushDataPermissions(version_search.params, this.$route.meta.routerId, '987744398207139863')
      this.version_List.splice(0, this.version_List.length)
      listClinicVersionAll(version_search).then(responseData => {
        this.version_List = responseData.data
      })
    }
  },
  watch: {
    parent_List(newVal, oldVal) {
      if(newVal != oldVal && this.dialogProps.action == 'add' ) {
        this.$nextTick(() => {
          this.onParentChange()
        })
      }
    },
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewCompanyDialog', function(company) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看公司'
        this.bizFormModel = {...this.initFormModel(), ...company}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditCompanyDialog', function(company) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改公司'
        this.bizFormModel = {...this.initFormModel(), ...company}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddCompanyDialog', function(parent) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加公司'
        this.bizFormModel = this.initFormModel(parent)
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyCompanyDialog', function(company) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加公司'
        this.bizFormModel = {...this.initFormModel(), ...company}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>