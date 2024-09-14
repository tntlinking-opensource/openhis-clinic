<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules' 
      ref='routerForm' label-width='120px' label-position='right' class='edit-form'>    

              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='父级' prop='parent.id' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.parent.name'></el-input>
            <el-cascader v-else v-model='bizFormModel.parent.id' :options='parent_List' :key='key_parent' ref='parentCascader' @change='onParentChange' :props='{value: "id", label: "name", checkStrictly: true, emitPath: false}' filterable clearable placeholder='请选择父级' />
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='名称' prop='name' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入名称"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='代码' prop='code' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.code' :maxlength='64' :placeholder='dialogProps.action == "view"? "" : "请输入代码"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='URL' prop='url' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.url' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入URL"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='可授权' prop='canPermission' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.canPermission' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='禁用' prop='isLocked' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isLocked' active-color='#13ce66' inactive-color='#dbdfe6' :active-value='1' :inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='显示序号' prop='sort' >
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.sort'></el-input>
            <el-input-number v-else v-model='bizFormModel.sort' controls-position='right'></el-input-number>
          </el-form-item>
        </el-col>
                        </el-row>
            <el-row>
                <el-col>
                    <el-form-item label='扩展属性' prop='properties' >
                        <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.properties' type='textarea'
                                  :maxlength='1024' :placeholder='dialogProps.action == "view"? "" : "请输入扩展属性"'  clearable></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("routerForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { treeRouter } from '@/api/admin/router'
import { saveRouter } from '@/api/admin/router'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'router-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      bizFormModel: this.initFormModel(),
          key_parent: 1,    // el-cascader key
          parent_List: [],  // 父级
          flage:false,
       dialogProps: {
        visible: false,
        action: '',
        title: '',
        
      },
      formRules: {
        'name': [
            { required: true, message: '请输入名称', trigger: 'blur' }
        ],
        'properties': [
            { required: true, message: '请输入扩展属性', trigger: 'blur' }
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
      saveRouter(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改路由'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['routerForm'].clearValidate()
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
        'parent': {     // 上级节点 父级
          'id': validatenull(This) || validatenull(This.id) ? null : This.id,
          'name': validatenull(This) || validatenull(This.name) ? null : This.name,
        },
        'name': '',   // 名称
        'code': '',   // 代码
        'url': '',   // URL
        'canPermission': '1',   // 可授权
        'isLocked': 0,    // 禁用
          'sort': This && This.children ? (This.children.length + 1) * 10000 : (this.parent_List ? (this.parent_List.length + 1) * 10000 : 10000),
        'properties': '',   // 扩展属性

      }
    },
    initOptions(This) {
      this.key_parent++
      let parent_search = {
        params: []
      }
        // 字段对应表上filter条件
        parent_search.params.push.apply(parent_search.params, [])
        // 表有禁用字段
        parent_search.params.push.apply(parent_search.params, [{columnName: 'is_locked', queryType: '=', value: '0'}])
      // 数据权限: 路由sys_router
      this.pushDataPermissions(parent_search.params, this.$route.meta.routerId, '4003')
      this.parent_List.splice(0, this.parent_List.length)
      treeRouter(parent_search).then(responseData => {
        this.parent_List = responseData.data
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
      this.$on('openViewRouterDialog', function(router) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看路由'
        this.bizFormModel = router
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openEditRouterDialog', function(router) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改路由'
        this.bizFormModel = router
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openAddRouterDialog', function(parent) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加路由'
        this.bizFormModel = this.initFormModel(parent)
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openCopyRouterDialog', function(router) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加路由'
        this.bizFormModel = router
        this.initOptions(this.bizFormModel)
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>