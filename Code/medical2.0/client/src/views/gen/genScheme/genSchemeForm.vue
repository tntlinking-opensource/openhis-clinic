<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' :close-on-click-modal='false' width='850px'
    @open='onDialogOpen()' v-loading='loading'>
    <div slot='title' class='dialog-header'>
      {{ dialogProps.title }}
      <OperationIcon v-show='dialogProps.action == "view" && permission.edit' type='primary' text='编辑' placement='top-start' icon-name='el-icon-edit' @click='switchEdit'></OperationIcon>
    </div>
    <el-form :model='bizFormModel' :rules='formRules'
      ref='genSchemeForm' label-width='120px' label-position='right' class='edit-form'>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='名称' prop='name'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.name' :maxlength='200' :placeholder='dialogProps.action == "view"? "" : "请输入名称"' autofocus></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='模板分类' prop='category.value'>
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.category.name'></el-input>
            <el-select v-else v-model='bizFormModel.category' value-key='value' filterable clearable placeholder='请选择模板分类'
              @clear='bizFormModel.category={
                "value": null,
                                "name": null,
                }'>
             <el-option v-for='category in category_List' :key='category.value' :label='category.name' :value='category'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='包路径' prop='packageName'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.packageName' :maxlength='500' :placeholder='dialogProps.action == "view"? "" : "请输入包路径"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='模块名' prop='moduleName'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.moduleName' :maxlength='30' :placeholder='dialogProps.action == "view"? "" : "请输入模块名"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='子模块名' prop='subModuleName'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.subModuleName' :maxlength='30' :placeholder='dialogProps.action == "view"? "" : "请输入子模块名"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='功能名' prop='functionName'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.functionName' :maxlength='500' :placeholder='dialogProps.action == "view"? "" : "请输入功能名"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='功能名简写' prop='functionNameSimple'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.functionNameSimple' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入功能名简写"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='作者' prop='functionAuthor'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.functionAuthor' :maxlength='100' :placeholder='dialogProps.action == "view"? "" : "请输入作者"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='编辑宽度' prop='dialogWidth'>
            <el-input :disabled='dialogProps.action == "view"' v-model='bizFormModel.dialogWidth' :maxlength='20' :placeholder='dialogProps.action == "view"? "" : "请输入编辑宽度"' ></el-input>
          </el-form-item>
        </el-col>
                <el-col :span='24/2'>
          <el-form-item label='编辑列数' prop='colCounts'>
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.colCounts'></el-input>
            <el-input-number v-else v-model='bizFormModel.colCounts' controls-position='right'></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/2'>
          <el-form-item label='业务表' prop='genTable.id'>
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='bizFormModel.genTable.name'></el-input>
            <el-select v-else v-model='bizFormModel.genTable' value-key='id' filterable clearable placeholder='请选择业务表'
              @clear='bizFormModel.genTable={
                "id": null,
                                "name": null,
                }'>
             <el-option v-for='genTable in genTable_List' :key='genTable.id' :label='genTable.name + " :" + genTable.comments' :value='genTable'></el-option>    
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
      <el-dropdown v-if='dialogProps.action != "view"' split-button type='warning' @click='generate("genSchemeForm", false)'>
        生成代码
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item><el-button  type='text' :plain='true' @click='generate("genSchemeForm", true)'>覆盖文件</el-button></el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-button v-if='dialogProps.action != "view"' :disabled="flage" type='primary' :plain='true' @click='onSubmit("genSchemeForm")'>保 存</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
      <el-button v-if='dialogProps.action == "view"' :plain='true' @click='onDialogClose()'>关 闭</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { generateCode } from '@/api/gen/genCode'
import { listDictItemAll } from '@/api/sys/dictItem'
import { listGenTableAll } from '@/api/gen/genTable'
import { saveGenScheme } from '@/api/gen/genScheme'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'genScheme-form',
  components: {
    OperationIcon
  },
  data() {
    return {
      bizFormModel: this.initFormModel(),
          category_List: [],  // 模板分类
          genTable_List: [],  // 业务表
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
        'category.value': [
            { required: true, message: '请选择模板分类', trigger: 'change' }
        ],
        'packageName': [
            { required: true, message: '请输入包路径', trigger: 'blur' }
        ],
        'moduleName': [
            { required: true, message: '请输入模块名', trigger: 'blur' }
        ],
        'functionName': [
            { required: true, message: '请输入功能名', trigger: 'blur' }
        ],
        'functionNameSimple': [
            { required: true, message: '请输入功能名简写', trigger: 'blur' }
        ],
        'dialogWidth': [
            { required: true, message: '请输入编辑宽度', trigger: 'blur' }
        ],
        'colCounts': [
            { required: true, message: '请选择编辑列数', trigger: 'blur' }
        ],
        'genTable.id': [
            { required: true, message: '请选择业务表', trigger: 'change' }
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
      saveGenScheme(this.bizFormModel).then(responseData => {
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
      this.dialogProps.title = '修改代码方案'
      this.initOptions(this.bizFormModel)
    },
    onDialogClose() {
      this.dialogProps.visible = false
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['genSchemeForm'].clearValidate()
      })
    },
    initFormModel(This) {
      return {
        'name': '',   // 名称
        'category': {     // 模板分类
          'value': null,
          'name': null,
        },
        'packageName': '',   // 包路径
        'moduleName': '',   // 模块名
        'subModuleName': '',   // 子模块名
        'functionName': '',   // 功能名
        'functionNameSimple': '',   // 功能名简写
        'functionAuthor': '',   // 作者
        'dialogWidth': '',   // 编辑宽度
        'colCounts': 0,    // 编辑列数
        'genTable': {     // 业务表
          'id': null,
          'name': null,
        },
        'remarks': '',   // 备注信息

      }
    },
    initOptions(This) {
      let category_search = {
        params: [{'columnName':'dict_type_id', 'queryType': '=', 'value': '5004'}]
      }
        // 字段对应表上filter条件
        category_search.params.push.apply(category_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(category_search.params, this.$route.meta.routerId, '4005')
      this.category_List.splice(0, this.category_List.length)
      listDictItemAll(category_search).then(responseData => {
        this.category_List = responseData.data
      })
      let genTable_search = {
        params: []
      }
        // 字段对应表上filter条件
        genTable_search.params.push.apply(genTable_search.params, [])
      // 数据权限: 业务表gen_table
      this.pushDataPermissions(genTable_search.params, this.$route.meta.routerId, '4001')
      this.genTable_List.splice(0, this.genTable_List.length)
      listGenTableAll(genTable_search).then(responseData => {
        this.genTable_List = responseData.data
      })
    },
    doGenerateCode() {
      this.setLoad()
      generateCode(this.bizFormModel).then(responseData => {
        if(responseData.code == 100) {
          this.loading = false
          this.dialogProps.visible = false
          this.$emit('save-finished')
        }
        this.showMessage(responseData)
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    generate(formName, replace) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          /* 是否替换文件 */
          this.bizFormModel.replace = replace
          this.doGenerateCode();
        } else {
          return false
        }
      })
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openViewGenSchemeDialog', function(genScheme) {
        this.dialogProps.action = 'view'
        this.dialogProps.title = '查看代码方案'
        this.bizFormModel = genScheme
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openEditGenSchemeDialog', function(genScheme) {
        this.dialogProps.action = 'edit'
        this.dialogProps.title = '修改代码方案'
        this.bizFormModel = genScheme
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openAddGenSchemeDialog', function() {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加代码方案'
        this.bizFormModel = this.initFormModel()
        this.initOptions(this.bizFormModel)
        this.dialogProps.visible = true
      })
      this.$on('openCopyGenSchemeDialog', function(genScheme) {
        this.dialogProps.action = 'add'
        this.dialogProps.title = '添加代码方案'
        this.bizFormModel = genScheme
        this.initOptions(this.bizFormModel)
        this.bizFormModel.id = null   //把id设置为空，添加一个新的
        this.dialogProps.visible = true
      })
    })
  }
}
</script>
