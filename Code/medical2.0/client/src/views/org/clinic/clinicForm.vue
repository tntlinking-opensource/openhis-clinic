<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='30%'
             @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑'
                     placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules'
             ref='clinicForm' label-width='120px' label-position='right' class='edit-form'>
      <div class="tab-item" v-show='tabIndex=="1"'>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='诊所名称' prop='name'>
              <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='100'
                        :placeholder='dialogProps.action == "view"? "" : "请输入诊所名称"' autofocus></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='租户' prop='lesseeId.id'>
              <el-input v-if='dialogProps.action == "view"' :disabled='true'
                        v-model='bizFormModel.lesseeId.userName'></el-input>
              <el-select v-else v-model='bizFormModel.lesseeId' value-key='id' filterable clearable placeholder='请选择租户'
                         @clear='bizFormModel.lesseeId={
                "id": null,
                                "name": null,
                }'>
                <el-option v-for='lesseeId in lesseeId_List' :key='lesseeId.id' :label='lesseeId.name'
                           :value='lesseeId'></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='医疗机构编码' prop='fixmedinsCode'>
              <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.fixmedinsCode' :maxlength='100'
                        :placeholder='dialogProps.action == "view"? "" : "如未开启医保则输入:000"' autofocus></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='地址(省)' prop='addressProvince'>
              <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.addressProvince' :maxlength='10'
                        :placeholder='dialogProps.action == "view"? "" : "请输入地址(省)"'></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='地址(市)' prop='addressCity'>
              <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.addressCity' :maxlength='10'
                        :placeholder='dialogProps.action == "view"? "" : "请输入地址(市)"'></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='地址(区)' prop='addressRegion'>
              <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.addressRegion' :maxlength='10'
                        :placeholder='dialogProps.action == "view"? "" : "请输入地址(区)"'></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='地址' prop='address'>
              <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.address' :maxlength='100'
                        :placeholder='dialogProps.action == "view"? "" : "请输入地址"'></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='手机号' prop='phoneNumber'>
              <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.phoneNumber' :maxlength='20'
                        :placeholder='dialogProps.action == "view"? "" : "请输入手机号"'></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='执业许可科目' prop='licenseSubject'>
              <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.licenseSubject' :maxlength='100'
                        :placeholder='dialogProps.action == "view"? "" : "请输入执业许可科目"'></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <!-- 多文件上传： 诊所许可证图片 -->
          <upload-file v-if='dialogProps.visible' v-model='bizFormModel.fileIdFile' titile='诊所许可证图片'
                       :action='dialogProps.action' :objectId='bizFormModel.fileId'></upload-file>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='产品版本' prop='version.id'>
              <el-input v-if='dialogProps.action == "view"' :disabled='true'
                        v-model='bizFormModel.version.name'></el-input>
              <el-select v-else v-model='bizFormModel.version' value-key='id' filterable clearable placeholder='请选择产品版本'
                         @clear='bizFormModel.version={
                "id": null,
                                "name": null,
                                                "roleId":null
                }'>
                <el-option v-for='version in version_List' :key='version.id' :label='version.name'
                           :value='version'></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='状态' prop='status'>
              <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.status' active-color='#13ce66'
                         inactive-color='#dbdfe6' :active-value='1' :inactive-value='0'></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='是否为机构' prop='isInstitution'>
              <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isInstitution' active-color='#13ce66'
                         inactive-color='#dbdfe6' :active-value='1' :inactive-value='0'></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='诊所开始时间' prop='startUseDate'>
              <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.startUseDate'
                              type='datetime' value-format='yyyy-MM-dd HH:mm:ss'
                              :placeholder='dialogProps.action == "view"? "" : "请输入诊所开始时间"'></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24/1'>
            <el-form-item label='诊所到期时间' prop='expireDate'>
              <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.expireDate' type='datetime'
                              value-format='yyyy-MM-dd HH:mm:ss'
                              :placeholder='dialogProps.action == "view"? "" : "请输入诊所到期时间"'></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-form-item label='备注信息' prop='remarks'>
              <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.remarks' type='textarea'
                        :maxlength='255' :placeholder='dialogProps.action == "view"? "" : "请输入备注信息"'
                        clearable></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true'
                 @click='onSubmit("clinicForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
  </el-dialog>
</template>
<script>
  import {validatenull} from '@/utils/validate'
  import {listTenantAll} from '@/api/tenant/tenant'
  import {listClinicVersionAll} from '@/api/clinic/clinicVersion'
  import uploadFile from '@/views/components/uploadFile'
  import {saveClinic} from '@/api/org/clinic'
  import BaseUI from '@/views/components/baseUI'
  import OperationIcon from '@/components/OperationIcon'

  export default {
    extends: BaseUI,
    name: 'clinic-form',
    components: {
      uploadFile,
      OperationIcon
    },
    data() {
      return {
        bizFormModel: this.initFormModel(),
        tabIndex: '1',
        lesseeId_List: [],  // 租户
        version_List: [],  // 产品版本
        flage: false,
        dialogProps: {
          visible: false,
          action: '',
          title: '',

        },
        formRules: {
          'name': [
            {required: true, message: '请输入诊所名称', trigger: 'blur'}
          ],
          'fixmedinsCode': [
            {required: true, message: '请输入医疗机构代码', trigger: 'blur'}
          ],
          'lesseeId.id': [
            {required: true, message: '请选择租户', trigger: 'change'}
          ],
          'phoneNumber': [
            {required: true, message: '请输入手机号', trigger: 'blur'}
          ],
          'version.id': [
            {required: true, message: '请选择产品版本', trigger: 'change'}
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
        this.flage = true
        this.$refs[formName].validate(valid => {
          if (valid) {
            this.doSave()
          } else {
            this.flage = false
            return false
          }
        });
      },
      doSave() {
        this.setLoad()
        let formData = this.createFormData(this.bizFormModel)
        saveClinic(formData).then(responseData => {
          this.flage = false
          if (responseData.code == 100) {
            this.dialogProps.visible = false
            this.$emit('save-finished')
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.flage = false
          this.outputError(error)
        })
      },
      createFormData(bizFormModel) {
        let formData = new FormData()
        let deletes = []
        for (let idx in bizFormModel.fileIdFile.uploads) {
          formData.append('fileIdUploads', bizFormModel.fileIdFile.uploads[idx].raw)
        }
        deletes = deletes.concat(bizFormModel.fileIdFile.deletes)
        formData.append('entity', JSON.stringify(bizFormModel))
        formData.append('deleteIds', JSON.stringify(deletes))

        return formData
      },
      switchEdit() {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改诊所'
        this.initOptions(this.bizFormModel)
      },
      onDialogClose() {
        this.dialogProps.visible = false
      },
      onDialogOpen() {
        this.$nextTick(() => {
          this.$refs['clinicForm'].clearValidate()
        })
      },
      initFormModel(This) {
        return {
          'name': '',   // 诊所名称
          'lesseeId': {     // 父表 租户
            'id': validatenull(This) || validatenull(This.lesseeId) ? null : This.lesseeId.id,
            'name': validatenull(This) || validatenull(This.lesseeId) ? null : This.lesseeId.name,
          },
          'fixmedinsCode':'' ,//医疗机构编码
          'addressProvince': '',   // 地址(省)
          'addressCity': '',   // 地址(市)
          'addressRegion': '',   // 地址(区)
          'address': '',   // 地址
          'phoneNumber': '',   // 手机号
          'licenseSubject': '',   // 执业许可科目
          'parentId': '', //zuhu
          'fileId': '',
          'fileIdFile': {
            deletes: [],  // 待删除（已上传）的文件列表
            uploads: []   // 待上传的文件列表
          },
          'version': {     // 产品版本
            'id': null,
            'name': null,
            'roleId': null
          },
          'status': 1,    // 状态
          'isInstitution': 0,    // 是否为机构
          'startUseDate': '',   // 诊所开始时间
          'expireDate': '',   // 诊所到期时间
          'remarks': '',   // 备注信息

        }
      },
      initOptions(This) {
        let lesseeId_search = {
          params: []
        }
        // 数据权限: 租户lessee
        this.pushDataPermissions(lesseeId_search.params, this.$route.meta.routerId, '985180319911059468')
        this.lesseeId_List.splice(0, this.lesseeId_List.length)
        listTenantAll(lesseeId_search).then(responseData => {
          this.lesseeId_List = responseData.data
        })
        let version_search = {
          params: []
        }
        // 数据权限: 诊所版本clinic_version
        this.pushDataPermissions(version_search.params, this.$route.meta.routerId, '987744398207139863')
        this.version_List.splice(0, this.version_List.length)
        listClinicVersionAll(version_search).then(responseData => {
          this.version_List = responseData.data
        })
      }
    },
    watch: {},
    mounted: function () {
      this.$nextTick(() => {
        this.$on('openViewClinicDialog', function (clinic) {
          this.dialogProps.action = 'view'
          this.dialogProps.title = '查看诊所'
          this.bizFormModel = {...this.initFormModel(), ...clinic}
          this.initOptions(this.bizFormModel)
          this.tabIndex = '1'
          this.dialogProps.visible = true
        })
        this.$on('openEditClinicDialog', function (clinic) {
          this.dialogProps.action = 'edit'
          this.dialogProps.title = '修改诊所'
          this.bizFormModel = {...this.initFormModel(), ...clinic}
          this.initOptions(this.bizFormModel)
          this.tabIndex = '1'
          this.dialogProps.visible = true
        })
        this.$on('openAddClinicDialog', function () {
          this.dialogProps.action = 'add'
          this.dialogProps.title = '添加诊所'
          this.bizFormModel = this.initFormModel()
          this.initOptions(this.bizFormModel)
          this.tabIndex = '1'
          this.dialogProps.visible = true
        })
        this.$on('openCopyClinicDialog', function (clinic) {
          this.dialogProps.action = 'add'
          this.dialogProps.title = '添加诊所'
          this.bizFormModel = {...this.initFormModel(), ...clinic}
          this.initOptions(this.bizFormModel)
          this.tabIndex = '1'
          this.bizFormModel.id = null   //把id设置为空，添加一个新的
          this.dialogProps.visible = true
        })
      })
    }
  }
</script>
