<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='50%' 
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>

    <el-form :model='bizFormModel' :rules='formRules' 
      ref='dictTypeForm' label-width='120px' label-position='right' class='edit-form'>
        <!--tabs切换  开始-->
        <el-row>
          <el-radio-group v-model="tabIndex" style="margin-bottom: 10px;">
            <el-radio-button label="1">基本信息</el-radio-button>
                <el-radio-button label="2">字典项</el-radio-button>
          </el-radio-group>
        </el-row>
        <!--tabs切换  结束-->
      <div class="tab-item" v-show='tabIndex=="1"'>
        <!-- 主表单  开始-->

              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='字典类型编码' prop='code' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.code' :maxlength='64' :placeholder='dialogProps.action == "view"? "" : "请输入字典类型编码"' autofocus></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='字典类型名' prop='name' >
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='128' :placeholder='dialogProps.action == "view"? "" : "请输入字典类型名"' ></el-input>
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
        <el-col :span='24/1' v-if="currentUser.id == 1000">
          <el-form-item label='系统级' prop='isSystem' >
            <el-switch :disabled='dialogProps.action == "view"' v-model='bizFormModel.isSystem' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
        <!-- 主表单  结束-->

      </div>

    <!--子表：   字典项 开始-->
    <div class="tab-item" v-show='tabIndex=="2"'>
      <el-row>
        <el-col>
            <div class="tab-option" :style="{top: tabOptionBtnTop}">
              <el-button v-if='dialogProps.action != "view"' type='primary' :plain='true' icon='el-icon-plus'
                         @click='onAddDictItemRow(bizFormModel.dictItemList)'></el-button>
            </div>
          <el-table ref='dictType' :data='bizFormModel.dictItemList' border @current-change='(currentRow, oldCurrentRow) => {dictItemCurrentRow = currentRow}'>
            <el-table-column prop='name' label='名称' min-width='120px' header-align='center'>
              <template slot='header' slot-scope='{row,$index}'><span style="color:#F56C6C;margin-right: 4px">*</span>名称</template>
              <template slot-scope='{row,$index}'>
                <el-form-item data-num='2' :ref="`dictItemList.${$index}.name`" v-if='dialogProps.action != "view" && row === dictItemCurrentRow' :prop="`dictItemList.${$index}.name`" :rules='formRules.dictItem_name' label-width=0>
                  <el-input v-model='row.name'  :maxlength='64' clearable placeholder='请输入名称'></el-input>
                </el-form-item>
                <span v-else>{{row.name}}</span>
                  </template>
                </el-table-column>
            <el-table-column prop='value' label='值' min-width='160px' header-align='center'>
              <template slot='header' slot-scope='{row,$index}'><span style="color:#F56C6C;margin-right: 4px">*</span>值</template>
              <template slot-scope='{row,$index}'>
                <el-form-item data-num='2' :ref="`dictItemList.${$index}.value`" v-if='dialogProps.action != "view" && row === dictItemCurrentRow' :prop="`dictItemList.${$index}.value`" :rules='formRules.dictItem_value' label-width=0>
                  <el-input v-model='row.value'  :maxlength='128' clearable placeholder='请输入值'></el-input>
                </el-form-item>
                <span v-else>{{row.value}}</span>
                  </template>
                </el-table-column>
            <el-table-column prop='remarks' label='备注信息' min-width='200px' header-align='center'>
              <template slot='header' slot-scope='{row,$index}'>备注信息</template>
              <template slot-scope='{row,$index}'>
                <el-form-item data-num='2' :ref="`dictItemList.${$index}.remarks`" v-if='dialogProps.action != "view" && row === dictItemCurrentRow' :prop="`dictItemList.${$index}.remarks`" :rules='formRules.dictItem_remarks' label-width=0>
                  <el-input v-model='row.remarks'  :maxlength='255' clearable placeholder='请输入备注信息'></el-input>
                </el-form-item>
                <span v-else>{{row.remarks}}</span>
                  </template>
                </el-table-column>
            <el-table-column v-if='dialogProps.action != "view"' label='操作' header-align='center' align='center' width='60px'>
              <template slot-scope='scope'>
                <el-tooltip class='item' effect='light' content='删除' placement='top-start'>
                  <i class='el-icon-delete' style='color:#F56C6C;cursor:pointer;'
                     @click='onDeleteRow(scope.$index, bizFormModel.dictItemList)'></i>
                </el-tooltip>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </div>
    <!--子表：   字典项 结束-->
    </el-form>

    <!-- 按钮  开始-->
    <span slot='footer' class='dialog-footer'>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("dictTypeForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
    <!-- 按钮 结束-->
  </el-dialog>
</template>

<script>
import { validatenull } from '@/utils/validate'

import { saveDictType } from '@/api/sys/dictType'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'dictType-form',
  components: {
    OperationIcon
  },
  data() {
    return {
      bizFormModel: this.initFormModel(),
      flage:false,
      tabIndex: '1',
       dialogProps: {
        visible: false,
        action: '',
        title: '',
        
      },
      formRules: {
        // 主表验证
        'code': [
            { required: true, message: '请输入字典类型编码', trigger: 'blur' }
        ],
        'name': [
            { required: true, message: '请输入字典类型名', trigger: 'blur' }
        ],
        // 子表验证 字典项
        'dictItem_name': [
            { required: true, message: '请输入名称', trigger: 'blur' }
        ],
        'dictItem_value': [
            { required: true, message: '请输入值', trigger: 'blur' }
        ],
      },
    dictItemCurrentRow: {},     // 子表 字典项 当前选择行
    }
  },
  props: {
    // 权限
    permission: {
      type: Object
    }
  },
  computed: {
    ...Vuex.mapGetters(["settings"]),
    tabOptionBtnTop() {
      let top
      switch (this.settings.size) {
        case 'medium':
          top = -46 + 'px'
              break;
        case 'small':
          top = -42 + 'px'
              break;
        case 'mini':
          top = -38 + 'px'
              break;
        default:
          top = -50 + 'px'
      }
      return top
    }
  },
  methods: {
    onSubmit(formName) {
      this.flage=true
      this.$refs[formName].validate((valid, object) => {
        if (valid) {
          this.doSave()
        } else {
          this.flage=false
          // 处理校验定位
          let arr = []
          let numArr = []
          for (let i in object) {
            let dom = this.$refs[i]
            if (!dom) {
              this.tabIndex = 1
              return
            } else {
              arr.push(dom)
              if (Object.prototype.toString.call(arr) !== '[object Object]') {
                arr.forEach(item => {
                  numArr.push(item.$el.dataset.num)
                })
                this.tabIndex = Math.min(...numArr)
              }
            }
          }
          
          return false
        }
      });
    },
    doSave() {
      this.setLoad()
      saveDictType(this.bizFormModel).then(responseData => {
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
    onAddDictItemRow(tableData) {
        tableData.push({
            'dictType': {     // 父表 字典类型表ID
              'id': validatenull(parent) || validatenull(parent.dictType) ? null : parent.dictType.id,
              'name': validatenull(parent) || validatenull(parent.dictType) ? null : parent.dictType.name,
            },
            'name': '',   // 名称
            'value': '',   // 值
            'remarks': '',   // 备注信息
        })
    },
    onDeleteRow(index, tableData) {
        tableData.splice(index, 1)
    },
    switchEdit() {
      this.dialogProps.action = 'edit'
      this.dialogProps.title = '修改字典类型管理'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false  
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['dictTypeForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'code': '',   // 字典类型编码
        'name': '',   // 字典类型名
        'remarks': '',   // 备注信息
        'isSystem': '0',   // 系统级

        dictItemList: [],       // 子表列表
      }
    },
    initOptions() {
      // 主表
      let This = this.bizFormModel

      // 子表  字典项
      This = this.dictItemCurrentRow
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewDictTypeDialog', function(dictType) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看字典类型管理'
        this.bizFormModel = dictType
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openEditDictTypeDialog', function(dictType) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改字典类型管理'
        this.bizFormModel = dictType
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openAddDictTypeDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加字典类型管理'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
      this.$on('openCopyDictTypeDialog', function(dictType) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加字典类型管理'
        this.bizFormModel = dictType
        this.initOptions(this.bizFormModel)
        this.tabIndex = '1'
        //把id设置为空，添加一个新的
        this.bizFormModel.id = null
        for (var i = 0; i <= this.bizFormModel.dictItemList.length - 1; i++) {
            this.bizFormModel.dictItemList[i].id = null
        }        
        this.dialogProps.visible = true
      })
    })
  }  
}
</script>
<style lang="scss" scoped>
  .tab-item {
    position: relative;
    .tab-option {
      position: absolute;
      top: -46px;
      right: 0;
    }
  }
</style>