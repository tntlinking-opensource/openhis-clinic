<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='30%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>    
    
    <el-form :model='actionModel' :rules='formRules' 
      ref='actionForm' label-width='120px' label-position='right' class='edit-form'>    
    <!-- 主表单  开始-->
      <el-row>
        <el-col :span='24/1'>
          <el-form-item label='操作' prop='actionType.value'>
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='actionModel.actionType.name'></el-input>
            <el-select v-else v-model='actionModel.actionType' value-key='value' filterable clearable placeholder='请选择操作'>
             <el-option v-for='actionType in actionType_List' :key='actionType.value' :label='actionType.name' :value='actionType'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span='24/1'>
          <el-form-item label='对象类型' prop='objectType.id'>
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='actionModel.objectType.comments'></el-input>
            <el-select v-else v-model='actionModel.objectType' value-key='id' filterable clearable placeholder='请选择对象类型'>
             <el-option v-for='objectType in objectType_List' :key='objectType.id' :label='objectType.comments' :value='objectType'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span='24/1'>
          <el-form-item label='业务对象' prop='objectId'>
            <el-input :disabled='dialogProps.action == "view"' v-model='actionModel.objectId'  :maxlength='40':placeholder='dialogProps.action == "view"? "" : "请输入业务对象"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span='24/1'>
          <el-form-item label='对象名称' prop='objectName'>
            <el-input :disabled='dialogProps.action == "view"' v-model='actionModel.objectName'  :maxlength='512':placeholder='dialogProps.action == "view"? "" : "请输入对象名称"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item label='备注信息' prop='remarks'>        
            <el-input :disabled='dialogProps.action == "view"' v-model='actionModel.remarks' type='textarea'  
              :maxlength='255':placeholder='dialogProps.action == "view"? "" : "请输入备注信息"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    <!-- 主表单  结束-->
    
    <!--子表：   回收站 开始-->
      <el-divider content-position="left"> 回收站</el-divider>
      <el-row>
        <el-col>
    <el-table ref='action' :data='actionModel.actionRecycleList' border @current-change='(currentRow, oldCurrentRow) => {actionRecycleCurrentRow = currentRow}'>
      <el-table-column prop='tableName' label='表名' min-width='180px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'><span style="color:#F56C6C;margin-right: 4px">*</span>表名</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === actionRecycleCurrentRow' :prop="`actionRecycleList.${$index}.tableName`" :rules='formRules.actionRecycle_tableName' label-width=0>
            <el-input v-model='row.tableName'  :maxlength='200'clearable placeholder='请输入表名'></el-input>
          </el-form-item>
          <span v-else>{{row.tableName}}</span>
        </template>
      </el-table-column>
      <el-table-column prop='objectId' label='业务对象' min-width='180px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'><span style="color:#F56C6C;margin-right: 4px">*</span>业务对象</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === actionRecycleCurrentRow' :prop="`actionRecycleList.${$index}.objectId`" :rules='formRules.actionRecycle_objectId' label-width=0>
            <el-input v-model='row.objectId'  :maxlength='40'clearable placeholder='请输入业务对象'></el-input>
          </el-form-item>
          <span v-else>{{row.objectId}}</span>
        </template>
      </el-table-column>
      <el-table-column prop='objectName' label='对象名称' min-width='180px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>对象名称</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === actionRecycleCurrentRow' :prop="`actionRecycleList.${$index}.objectName`" :rules='formRules.actionRecycle_objectName' label-width=0>
            <el-input v-model='row.objectName'  :maxlength='200'clearable placeholder='请输入对象名称'></el-input>
          </el-form-item>
          <span v-else>{{row.objectName}}</span>
        </template>
      </el-table-column>
      <el-table-column v-if='dialogProps.action != "view"' label='操作' header-align='center' align='center' width='60px' fixed='right'>
        <template slot-scope='scope'>
          <el-tooltip class='item' effect='light' content='删除' placement='top-start'>
            <i class='el-icon-delete' style='color:#F56C6C;cursor:pointer;'
              @click='onDeleteRow(scope.$index, actionModel.actionRecycleList)'></i>
          </el-tooltip>         
        </template>
      </el-table-column>          
    </el-table>
    <el-button v-if='dialogProps.action != "view"' type='primary' 
      @click='onAddActionRecycleRow(actionModel.actionRecycleList)'>添加</el-button>
        </el-col>
      </el-row>
      <!--子表：   回收站 结束-->
    </el-form>
        
    <!-- 按钮  开始-->
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("actionForm")'>保 存</el-button>    
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span> 
    <!-- 按钮 结束-->
  </el-dialog>
</template>

<script>
import { validatenull } from '@/utils/validate'

import { listDictItemAll } from '@/api/sys/dictItem'
import { listGenTableAll } from '@/api/gen/genTable'

import { saveAction } from '@/api/sys/action'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'action-form',
  components: {
    OperationIcon
  },  
  data() {
    return {
      actionModel: this.initFormModel(),
          actionType_List: [],  // 操作
          objectType_List: [],  // 对象类型
          flage:false,
       dialogProps: {
        visible: false,
        action: '',
        title: '',
        
      },
      formRules: {
        // 主表验证
        // 子表验证 回收站
        'actionRecycle_tableName': [
            { required: true, message: '请输入表名', trigger: 'blur' }
        ],
        'actionRecycle_objectId': [
            { required: true, message: '请输入业务对象', trigger: 'blur' }
        ],
      },
    actionRecycleCurrentRow: {},     // 子表 回收站 当前选择行
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
      saveAction(this.actionModel).then(responseData => {
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
    onAddActionRecycleRow(tableData) {
        tableData.push({
            'action': {     // 父表 日志
              'id': validatenull(parent) || validatenull(parent.action) ? null : parent.action.id,
            },
            'tableName': '',   // 表名
            'objectId': '',   // 业务对象
            'objectName': '',   // 对象名称
            'remarks': '',   // 备注信息
        })
    },
    onDeleteRow(index, tableData) {
        tableData.splice(index, 1)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['actionForm'].clearValidate()
      })
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改操作日志'
      this.initOptions(this.actionModel)
    },
    initFormModel(parent) {
      return {
        'actionType': {     // 操作
          'value': '',
          'name': '',
        },
        'objectType': {     // 对象类型
          'id': 0,
          'comments': '',
        },
        'objectId': '',   // 业务对象
        'objectName': '',   // 对象名称
        'status': 0,
        'remarks': '',   // 备注信息

        actionRecycleList: [],       // 子表列表
      }
    },
    initOptions() {
      // 主表
      let This = this.actionModel
      let actionType_search = {
        params: [{'columnName': 'dict_type_id', 'queryType': '=', 'value': '5000'}]
      }
      // 字段对应表上filter条件
        actionType_search.params.push.apply(actionType_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(actionType_search.params, this.$route.meta.routerId, '4005')
      this.actionType_List = []
      listDictItemAll(actionType_search).then(responseData => {
        this.actionType_List = responseData.data
      })
      let objectType_search = {
        params: []
      }
      // 字段对应表上filter条件
        objectType_search.params.push.apply(objectType_search.params, [])
      // 数据权限: 业务表gen_table
      this.pushDataPermissions(objectType_search.params, this.$route.meta.routerId, '4001')
      this.objectType_List = []
      listGenTableAll(objectType_search).then(responseData => {
        this.objectType_List = responseData.data
      })

      // 子表  回收站
      This = this.actionRecycleCurrentRow
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewActionDialog', function(action) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看操作日志'
        this.actionModel = action
        // this.initOptions()
        this.dialogProps.visible = true
      })
      this.$on('openEditActionDialog', function(action) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改操作日志'
        this.actionModel = action
        this.initOptions()
        this.dialogProps.visible = true
      })
      this.$on('openAddActionDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加操作日志'
        this.actionModel = this.initFormModel()
        this.initOptions()
        this.dialogProps.visible = true
      })
      this.$on('openCopyActionDialog', function(action) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '添加操作日志'
        this.actionModel = action
        this.initOptions()
        //把id设置为空，添加一个新的
        this.actionModel.id = null
        for (var i = 0; i <= this.actionModel.actionRecycleList.length - 1; i++) {
            this.actionModel.actionRecycleList[i].id = null
        }        
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>