<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <!-- <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon> -->
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='memberItemForm' label-width='120px' label-position='right' class='edit-form'>  
      <div class="tab-item" v-show='tabIndex=="1"'>  
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='诊所id' prop='company.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.company.name'></el-input>
            <el-select v-else v-model='bizFormModel.company' value-key='id' filterable clearable placeholder='请选择诊所id' 
              @clear='bizFormModel.company={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='company in company_List' :key='company.id' :label='company.name' :value='company'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='会员卡id' prop='member.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.member.name'></el-input>
            <el-select v-else v-model='bizFormModel.member' value-key='id' filterable clearable placeholder='请选择会员卡id' 
              @clear='bizFormModel.member={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='member in member_List' :key='member.id' :label='member.name' :value='member'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='对应的项目id' prop='costItem.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.costItem.name'></el-input>
            <el-select v-else v-model='bizFormModel.costItem' value-key='id' filterable clearable placeholder='请选择对应的项目id' 
              @clear='bizFormModel.costItem={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='costItem in costItem_List' :key='costItem.id' :label='costItem.name' :value='costItem'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='赠送项目数量' prop='number' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.number'></el-input>
            <number-input v-else v-model="bizFormModel.number"  :precision="0"></number-input>
          </el-form-item>
        </el-col>
      </el-row>
      </div>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' type='primary' :plain='true' @click='onSubmit("memberItemForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listCompanyAll } from '@/api/org/company'
import { listMemberSetAll } from '@/api/member/memberSet'
import { listCostItemAll } from '@/api/treatment/costItem'
import { saveMemberItem } from '@/api/member/memberItem'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'memberItem-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
      tabIndex: '1',
          company_List: [],  // 诊所id
          member_List: [],  // 会员卡id
          costItem_List: [],  // 对应的项目id
       dialogProps: {
        visible: false,
        action: '',
        title: ''
      },
      formRules: {
        'company.id': [
            { required: true, message: '请选择诊所id', trigger: 'change' }
        ],
        'member.id': [
            { required: true, message: '请选择会员卡id', trigger: 'change' }
        ],
        'costItem.id': [
            { required: true, message: '请选择对应的项目id', trigger: 'change' }
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
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.doSave()
        } else {
          return false
        }
      });
    },
    doSave() {
      this.setLoad()
      saveMemberItem(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改会员卡详情'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['memberItemForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'company': {     // 诊所id
          'id': currentUser.company.id,
          'name': null,
        },
        'member': {     // 会员卡id
          'id': null,
          'name': null,
        },
        'costItem': {     // 对应的项目id
          'id': null,
          'name': null,
        },
        'number': 0,    // 赠送项目数量

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
      let member_search = {
        params: []
      }
        // 字段对应表上filter条件
        member_search.params.push.apply(member_search.params, [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}])
      // 数据权限: 会员卡设置member_set
      this.pushDataPermissions(member_search.params, this.$route.meta.routerId, '1222698883343517159')
      this.member_List.splice(0, this.member_List.length)
      listMemberSetAll(member_search).then(responseData => {
        this.member_List = responseData.data
      })
      let costItem_search = {
        params: []
      }
        // 字段对应表上filter条件
        costItem_search.params.push.apply(costItem_search.params, [{columnName: 'company_id', queryType: '=', value: function() {var user = JSON.parse(sessionStorage.getItem('currentUser')); return user.company.id;}()}])
      // 数据权限: 费用项目cost_item
      this.pushDataPermissions(costItem_search.params, this.$route.meta.routerId, '998465736089977637')
      this.costItem_List.splice(0, this.costItem_List.length)
      listCostItemAll(costItem_search).then(responseData => {
        this.costItem_List = responseData.data
      })
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewMemberItemDialog', function(memberItem) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看会员卡详情'
        this.bizFormModel = {...this.initFormModel(), ...memberItem}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditMemberItemDialog', function(memberItem) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改会员卡详情'
        this.bizFormModel = {...this.initFormModel(), ...memberItem}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddMemberItemDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加会员卡详情'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyMemberItemDialog', function(memberItem) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加会员卡详情'
        this.bizFormModel = {...this.initFormModel(), ...memberItem}
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>