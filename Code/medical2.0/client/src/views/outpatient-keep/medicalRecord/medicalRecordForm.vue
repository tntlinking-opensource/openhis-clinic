<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='medicalRecordForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='医生id' prop='doctor.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.doctor.name'></el-input>
            <el-select v-else v-model='bizFormModel.doctor' value-key='id' filterable clearable placeholder='请选择医生id' 
              @clear='bizFormModel.doctor={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='doctor in doctor_List' :key='doctor.id' :label='doctor.name' :value='doctor'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='挂号id' prop='registration.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.registration.id'></el-input>
            <el-select v-else v-model='bizFormModel.registration' value-key='id' filterable clearable placeholder='请选择挂号id' 
              @clear='bizFormModel.registration={
                "id": null,
                }'>
             <el-option v-for='registration in registration_List' :key='registration.id' :label='registration.id' :value='registration'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='主诉' prop='patientTell' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.patientTell' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入主诉"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='现病史' prop='nowHistory' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.nowHistory' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入现病史"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='既往史' prop='beforeHistory' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.beforeHistory' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入既往史"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='诊断' prop='diagnose' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.diagnose' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入诊断"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='个人史' prop='personalHistory' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.personalHistory' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入个人史"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='过敏史' prop='allergyHistory' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.allergyHistory' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入过敏史"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='疾病史' prop='diseaseHistory' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.diseaseHistory' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入疾病史"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='婚孕史' prop='pregnancyHistory' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.pregnancyHistory' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入婚孕史"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='传染病史' prop='infectiousHistory' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.infectiousHistory' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入传染病史"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='月经史' prop='lunariaHistory' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.lunariaHistory' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入月经史"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='家族史' prop='familyHistory' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.familyHistory' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入家族史"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='手术史' prop='surgeryHistory' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.surgeryHistory' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入手术史"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='输血史' prop='transfusionHistory' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.transfusionHistory' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入输血史"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='体格检查' prop='physiqueCheck' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.physiqueCheck' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入体格检查"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='辅助检查' prop='assistCheck' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.assistCheck' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入辅助检查"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='急诊诊断' prop='emergencyDiagnose' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.emergencyDiagnose' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入急诊诊断"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='急诊效果' prop='emergencyEffect' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.emergencyEffect' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入急诊效果"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='其他检查' prop='otherCheck' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.otherCheck' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入其他检查"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='诊断时间' prop='diagnoseDate' >
            <el-date-picker :disabled='dialogProps.action == "view"' v-model='bizFormModel.diagnoseDate' type='datetime' value-format='yyyy-MM-dd HH:mm:ss' :placeholder='dialogProps.action == "view"? "" : "请输入诊断时间"' ></el-date-picker>             
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='医嘱事项' prop='doctorAdvice' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.doctorAdvice' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入医嘱事项"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='总费用' prop='fee' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.fee'></el-input>
            <number-input v-else v-model="bizFormModel.fee" currency='CNY' :precision="2"></number-input>
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
         <!-- 多文件上传： 上传附件 -->
        <upload-file v-if='dialogProps.visible' v-model='bizFormModel.fileIdFile' titile='上传附件' :action='dialogProps.action' :objectId='bizFormModel.fileId'></upload-file>
      </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("medicalRecordForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listUserAll } from '@/api/admin/user'
import { listRegistrationAll } from '@/api/outpatient/registration'
import uploadFile from '@/views/components/uploadFile'
import { saveMedicalRecord } from '@/api/outpatient/medicalRecord'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'medicalRecord-form',
  components: {
    uploadFile,
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
          doctor_List: [],  // 医生id
          registration_List: [],  // 挂号id
          flage:false,
       dialogProps: {
        visible: false,
        action: '',
        title: ''
      },
      formRules: {
        'doctor.id': [
            { required: true, message: '请选择医生id', trigger: 'change' }
        ],
        'registration.id': [
            { required: true, message: '请选择挂号id', trigger: 'change' }
        ],
        'patientTell': [
            { required: true, message: '请输入主诉', trigger: 'blur' }
        ],
        'diagnose': [
            { required: true, message: '请输入诊断', trigger: 'blur' }
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
      saveMedicalRecord(formData).then(responseData => {
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
      this.dialogProps.title = '修改病历填写'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['medicalRecordForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': currentUser.company.id,
          'name': null,
        },
        'doctor': {     // 医生id
          'id': null,
          'name': null,
        },
        'registration': {     // 父表 挂号id
          'id': validatenull(This) || validatenull(This.registration) ? null : This.registration.id,
        },
        'patientTell': '',   // 主诉
        'nowHistory': '',   // 现病史
        'beforeHistory': '',   // 既往史
        'diagnose': '',   // 诊断
        'personalHistory': '',   // 个人史
        'allergyHistory': '',   // 过敏史
        'diseaseHistory': '',   // 疾病史
        'pregnancyHistory': '',   // 婚孕史
        'infectiousHistory': '',   // 传染病史
        'lunariaHistory': '',   // 月经史
        'familyHistory': '',   // 家族史
        'surgeryHistory': '',   // 手术史
        'transfusionHistory': '',   // 输血史
        'physiqueCheck': '',   // 体格检查
        'assistCheck': '',   // 辅助检查
        'emergencyDiagnose': '',   // 急诊诊断
        'emergencyEffect': '',   // 急诊效果
        'otherCheck': '',   // 其他检查
        'diagnoseDate': '',   // 诊断时间
        'doctorAdvice': '',   // 医嘱事项
        'fee': 0,    // 总费用
        'remarks': '',   // 备注信息
        'fileId': '',
        'fileIdFile': {
          deletes: [],  // 待删除（已上传）的文件列表
          uploads: []   // 待上传的文件列表
         },

      }
    },
    initOptions(This) {
      let doctor_search = {
        params: [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
      }
        // 字段对应表上filter条件
        doctor_search.params.push.apply(doctor_search.params, [])
        // 表有禁用字段 
        doctor_search.params.push.apply(doctor_search.params, [{columnName: 'is_locked', queryType: '=', value: '0'}])
      // 数据权限:  用户sys_user
      this.pushDataPermissions(doctor_search.params, this.$route.meta.routerId, '4004')
      this.doctor_List.splice(0, this.doctor_List.length)
      listUserAll(doctor_search).then(responseData => {
        this.doctor_List = responseData.data
      })
      let registration_search = {
        params: []
      }
        // 字段对应表上filter条件
        registration_search.params.push.apply(registration_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 挂号表registration
      this.pushDataPermissions(registration_search.params, this.$route.meta.routerId, '1008534118685450402')
      this.registration_List.splice(0, this.registration_List.length)
      listRegistrationAll(registration_search).then(responseData => {
        this.registration_List = responseData.data
      })
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewMedicalRecordDialog', function(medicalRecord) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看病历填写'
        this.bizFormModel = {...this.initFormModel(), ...medicalRecord}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditMedicalRecordDialog', function(medicalRecord) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改病历填写'
        this.bizFormModel = {...this.initFormModel(), ...medicalRecord}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddMedicalRecordDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加病历填写'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyMedicalRecordDialog', function(medicalRecord) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加病历填写'
        this.bizFormModel = {...this.initFormModel(), ...medicalRecord}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>