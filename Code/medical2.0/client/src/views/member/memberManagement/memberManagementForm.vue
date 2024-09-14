<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' v-if="dialogProps.visible" :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <!-- <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div> -->

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='memberManagementForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
                <el-col :span='24/2'>
          <el-form-item label='姓名' prop='patient.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.patient.name'></el-input>
            <el-select v-else @change="getPatient" v-model='bizFormModel.patient' value-key='id' filterable clearable placeholder='请选择患者信息' 
              @clear='bizFormModel.patient={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='patient in patient_List' :key='patient.id' :label='patient.name' :value='patient'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
          <el-col :span='24/2'>
          <el-form-item label='性别' prop='gender' >
            <el-input :disabled='true' v-model='bizFormModel.gender.name' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入性别"' >
            
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
         <el-row>
        <el-col :span='24/2'>
          <el-form-item label='身份证' prop='identityCard' >
            <el-input :disabled='true' v-model='bizFormModel.identityCard' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入身份证号"' ></el-input>
          </el-form-item>
        </el-col>
               <el-col :span='24/2'>
          <el-form-item label='电话' prop='phone' >
            <el-input :disabled='true' v-model='bizFormModel.phone' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入电话号"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
                  <el-col :span='24/2'>
          <el-form-item label='会员卡类型' prop='type' >
             <el-select :disabled='dialogProps.action == "view"'  v-model='bizFormModel.type'  filterable clearable placeholder='请选择会员卡类型' 
             @change="getMember">
             <el-option v-for='item in typeList' :key='item.value' :label='item.name' :value='item'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span='24/2'>
          <el-form-item label='会员卡名称' prop='memberSet' >
         
            <el-select :disabled='dialogProps.action == "view"' v-model='bizFormModel.memberSet'  filterable clearable placeholder='请选择会员卡' 
             @change="getCostItem">
             <el-option  v-for='item in memberSet_List' :key='item.id' :label='item.name' :value='item'></el-option>
            </el-select>
          </el-form-item>
        </el-col>      
      </el-row> 
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='卡号' prop='card' >
            <el-input :disabled='true' v-model='bizFormModel.card' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "卡号保存后自动生成"' ></el-input>
          </el-form-item>
        </el-col> 
      </el-row>
      
      <div v-if='memberItem.length>=1' style="margin-left:70px"> 
            <el-table
              :data="memberItem"
              style="width: 90%">
                <el-table-column
              label="序号"
              type="index"
              align="center">
            </el-table-column>     
              <el-table-column
                prop="costItem.itemName"
                label="项目名称"
                width="width">
              </el-table-column>
              <el-table-column
                prop="costItem.itemType.name"
                label="项目类型"
                width="width">
              </el-table-column>
              <el-table-column
                width="width"
                prop="costItem.salePrice"
                label="项目金额">
              </el-table-column>
                <el-table-column
                width="width"
                prop="number"
                label="数量">
                <!-- <template slot-scope="scope">
                  <span v-if="dialogProps.action == 'view'">
                    {{scope.row.useNumber}}/{{scope.row.number}}
                  </span>
                </template> -->
              </el-table-column>
              <el-table-column
                width="width"
                prop="costItem.unit.name"
                label="单位">
              </el-table-column>
            
            </el-table>
      </div>
      <div v-else-if="memberManagementDetail.length>0" style="margin-left:70px">
           <el-table
              :data="memberManagementDetail"
              style="width: 90%">
                <el-table-column
              label="序号"
              type="index"
              align="center">
            </el-table-column>     
              <el-table-column
                prop="memberItem.costItem.itemName"
                label="项目名称"
                width="width">
              </el-table-column>
              <el-table-column
                prop="memberItem.costItem.itemType.name"
                label="项目类型"
                width="width">
              </el-table-column>
              <el-table-column
                width="width"
                prop="memberItem.costItem.salePrice"
                label="项目金额">
              </el-table-column>
               <el-table-column
                width="width"
                prop="number"
                label="数量">
                <template slot-scope="scope">         
                    {{scope.row.useNumber}}/{{scope.row.number}}
                </template>
              </el-table-column>
              <el-table-column
                width="width"
                prop="memberItem.costItem.unit.name"
                label="单位">
              </el-table-column>
             
            </el-table>
      </div>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' type='primary' :plain='true' @click='onSubmit("memberManagementForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listCompanyAll } from '@/api/org/company'
import { listPatientAll } from '@/api/outpatient/patient'
import { listMemberSetAll } from '@/api/member/memberSet'
import { saveMemberManagement } from '@/api/member/memberManagement'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
import { listDictItemAll } from '@/api/sys/dictItem'
export default {
  extends: BaseUI,
  name: 'memberManagement-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
          company_List: [],  // 诊所id
          patient_List: [],  // 患者信息
          memberSet_List: [],  // 会员卡id
          typeList:[],  //会员卡类型
          memberItem:[],  //项目内容
          memberManagementDetail:[],  //会员详情
       dialogProps: {
        visible: false,
        action: '',
        title: ''
      },
      formRules: {
        
        'patient.id': [
            { required: true, message: '请选择患者信息', trigger: 'change' }
        ],
        'memberSet': [
            { required: true, message: '请选择会员卡名称', trigger: 'change' }
        ],
        'type': [
            { required: true, message: '请输入会员卡类型', trigger: 'change' }
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
    //获取项目
    getCostItem(row){
      console.log(row,'这是个啥');
      this.memberItem=row.memberItem
    },

    //获取对应会员卡类型的会员卡
    getMember(row){
      
      let memberSet_search = {
        params: []
      }
        // 字段对应表上filter条件
        memberSet_search.params.push.apply(memberSet_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id},{columnName: 'type', queryType: '=', value: row.value},{columnName: 'status', queryType: '=', value: 0},{columnName: 'failure', queryType: '=', value: 0},{columnName: 'number', queryType: '>', value: 0}])
      // 数据权限: 会员卡设置member_set
      this.pushDataPermissions(memberSet_search.params, this.$route.meta.routerId, '1222698883343517159')
      this.memberSet_List.splice(0, this.memberSet_List.length)
      listMemberSetAll(memberSet_search).then(responseData => {
        this.memberSet_List = responseData.data
      })
    },

    //获取患者信息
    getPatient(row){
        console.log(row,'获取患者信息');
        this.bizFormModel.gender=row.gender
        this.bizFormModel.phone=row.phone
        this.bizFormModel.identityCard =row.card
    },
    onSubmit(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave()
        } else {
          return false
        }
      });
    },
    doSave() {
      
      this.bizFormModel.age=this.bizFormModel.patient.age
      this.bizFormModel.memberName=this.bizFormModel.memberSet.name
      this.bizFormModel.name=this.bizFormModel.patient.name
      console.log(this.bizFormModel,'看看数据结构');
      //return
      this.setLoad()
      saveMemberManagement(this.bizFormModel).then(responseData => {
        if(responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
       this.outputError(error)
      })
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改会员卡管理'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['memberManagementForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': currentUser.company.id,
          'name': null,
        },
        'patient': {     // 患者信息
          'id': null,
          'name': null,
        },
        'memberSet': {     // 会员卡id
          'id': null,
          'name': null,
        },
        'name': '',   // 姓名
        'age': '',   // 年龄
        'gender': '',   // 性别
        'card': '',   // 卡号
        'type': '',   // 会员卡类型
        'memberName': '',   // 会员卡名称
        'status': '0',   // 状态
        'identityCard': '',   // 身份证号

      }
    },
    initOptions(This) {
      let company_search = {
        params: []
      }
        // 字段对应表上filter条件
        company_search.params.push.apply(company_search.params, [])
      // 数据权限: 诊所org_company
      this.pushDataPermissions(company_search.params, this.$route.meta.routerId, '41040096140492800')
      this.company_List.splice(0, this.company_List.length)
      listCompanyAll(company_search).then(responseData => {
        this.company_List = responseData.data
        // 获取初始项的值
        this.company_List.forEach( item => {
          if(item.id == this.bizFormModel.company.id) {
            this.bizFormModel.company = item
            return
          }
        })
      })
      let patient_search = {
        params: []
      }
        // 字段对应表上filter条件
        patient_search.params.push.apply(patient_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 患者表patient
      this.pushDataPermissions(patient_search.params, this.$route.meta.routerId, '1008489176147648530')
      this.patient_List.splice(0, this.patient_List.length)
      listPatientAll(patient_search).then(responseData => {
        this.patient_List = responseData.data
      })
      
      let type_search={
        params:[
          {
            
            columnName: "dict_type_id",
            queryType: "=",
            value: '1224037951067242497',
          
          }
        ]
      }
     listDictItemAll(type_search).then((responseData) => {
          if(responseData.code=='100'){
            
            this.typeList=responseData.data
            this.$forceUpdate()
          }
      })
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewMemberManagementDialog', function(memberManagement) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看会员'
        this.bizFormModel = {...this.initFormModel(), ...memberManagement}
        this.initOptions(this.bizFormModel)
        console.log(this.bizFormModel,'就是这个值');
        this.getMember(this.bizFormModel.type)
        this.memberItem=[]
        console.log(this.memberItem.length,'看这个值');
        this.memberManagementDetail=this.bizFormModel.memberManagementDetails
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditMemberManagementDialog', function(memberManagement) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改会员'
        this.bizFormModel = {...this.initFormModel(), ...memberManagement}
        this.initOptions(this.bizFormModel)
         this.getMember(this.bizFormModel.memberSet.type.value)
          this.memberItem=[]
        this.memberManagementDetail=this.bizFormModel.memberManagementDetails
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddMemberManagementDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加会员'
        this.memberManagementDetail=[]
        this.memberItem=[]
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.bizFormModel.memberSet=null
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyMemberManagementDialog', function(memberManagement) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加会员'
        this.bizFormModel = {...this.initFormModel(), ...memberManagement}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>