<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='30%'
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules'
      ref='departmentForm' label-width='120px' label-position='right' class='edit-form'>

              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='上级部门' prop='parent.id'>
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.parent.name'></el-input>
            <el-cascader v-else v-model='bizFormModel.parent.id' :options='parent_List' :key='key_parent' ref='parentCascader' @change='onParentChange' :props='{value: "id", label: "name", checkStrictly: true, emitPath: false}' filterable clearable placeholder='请选择上级部门' />
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='编码' prop='code'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.code' :maxlength='10' :placeholder='dialogProps.action == "view"? "" : "请输入编码"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='名称' prop='name'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入名称"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='排序' prop='sort'>
            <number-input v-if='dialogProps.action == "view"' disabled v-model="bizFormModel.sort" :precision="0"></number-input>
            <number-input v-else v-model="bizFormModel.sort" :precision="0"></number-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='总监' prop='director.id'>
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.director.name'></el-input>
            <el-select v-else v-model='bizFormModel.director' value-key='id' filterable clearable placeholder='请选择总监' @clear='bizFormModel.director={"id": null,"name": null,}'>
             <el-option v-for='director in director_List' :key='director.id' :label='director.name' :value='director'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='经理' prop='manager.id'>
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.manager.name'></el-input>
            <el-select v-else v-model='bizFormModel.manager' value-key='id' filterable clearable placeholder='请选择经理' @clear='bizFormModel.manager={"id": null,"name": null,}'>
             <el-option v-for='manager in manager_List' :key='manager.id' :label='manager.name' :value='manager'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col>
          <el-form-item label='备注信息' prop='remarks'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.remarks' type='textarea' 
             :maxlength='255' :placeholder='dialogProps.action == "view"? "" : "请输入备注信息"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("departmentForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { treeCompany } from '@/api/org/company'
import { treeDepartment } from '@/api/org/department'
import { listUserAll } from '@/api/admin/user'
import { saveDepartment } from '@/api/org/department'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'department-form',
  components: {
    OperationIcon
  },
  data() {
    return {
      bizFormModel: this.initFormModel(),
      flage:false,
                  key_parent: 1,    // el-cascader key
          parent_List: [],  // 上级部门
                                          director_List: [],  // 总监
                  manager_List: [],  // 经理
               dialogProps: {
        visible: false,
        action: '',
        title: '',
        
      },
      formRules: {
        'company.id': [
            { required: true, message: '请选择公司', trigger: 'change' }
        ],
        'code': [
            { required: true, message: '请输入编码', trigger: 'blur' }
        ],
        'name': [
            { required: true, message: '请输入名称', trigger: 'blur' }
        ],
        'sort': [
            { required: true, message: '请选择排序', trigger: 'blur' }
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
      saveDepartment(this.bizFormModel).then(responseData => {
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
    onDialogClose() {
      this.dialogProps.visible = false
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['departmentForm'].clearValidate()
      })
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改部门'
      this.initOptions(this.bizFormModel)
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
        'company': {     // 父表 公司
          'id': validatenull(This) || validatenull(This.company) ? null : This.company.id,
          'name': validatenull(This) || validatenull(This.company) ? null : This.company.name,
        },
        'parent': {     // 上级节点 上级部门
          'id': validatenull(This) || validatenull(This.id) ? null : This.id,
          'name': validatenull(This) || validatenull(This.name) ? null : This.name,
        },
        'code': '',   // 编码
        'name': '',   // 名称
          'sort': This && This.children ? (This.children.length + 1) * 10000 : (this.parent_List ? (this.parent_List + 1) * 10000 : 10000),
        'director': {     // 总监
          'id': null,
          'name': null,
        },
        'manager': {     // 经理
          'id': null,
          'name': null,
        },
        'remarks': '',   // 备注信息

      }
    },
    initOptions(This) {

                this.key_parent++
      let parent_search = {
        params: [{'columnName':'company_id', 'queryType': '=', 'value': This.company.id}]
      }
        // 字段对应表上filter条件
        parent_search.params.push.apply(parent_search.params, [])
      // 数据权限: 部门org_department
      this.pushDataPermissions(parent_search.params, this.$route.meta.routerId, '52676365136650250')
      this.parent_List.splice(0, this.parent_List.length)
      treeDepartment(parent_search).then(responseData => {
        this.parent_List = responseData.data
      })
                                                        let director_search = {
        params: []
      }
        // 字段对应表上filter条件
        director_search.params.push.apply(director_search.params, [])
        // 表有禁用字段
        director_search.params.push.apply(director_search.params, [{columnName: 'is_locked', queryType: '=', value: '0'}])
      // 数据权限:  用户sys_user
      this.pushDataPermissions(director_search.params, this.$route.meta.routerId, '4004')
      this.director_List = []
      listUserAll(director_search).then(responseData => {
        this.director_List = responseData.data
      })
                let manager_search = {
        params: []
      }
        // 字段对应表上filter条件
        manager_search.params.push.apply(manager_search.params, [])
        // 表有禁用字段
        manager_search.params.push.apply(manager_search.params, [{columnName: 'is_locked', queryType: '=', value: '0'}])
      // 数据权限:  用户sys_user
      this.pushDataPermissions(manager_search.params, this.$route.meta.routerId, '4004')
      this.manager_List = []
      listUserAll(manager_search).then(responseData => {
        this.manager_List = responseData.data
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
    }

  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewDepartmentDialog', function(department) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看部门'
        this.bizFormModel = department
        // this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openEditDepartmentDialog', function(department) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改部门'
        this.bizFormModel = department
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openAddDepartmentDialog', function(parent) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加部门'
        this.bizFormModel = this.initFormModel(parent)
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openCopyDepartmentDialog', function(department) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加部门'
        this.bizFormModel = department
        this.initOptions(this.bizFormModel)
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }
}
</script>
