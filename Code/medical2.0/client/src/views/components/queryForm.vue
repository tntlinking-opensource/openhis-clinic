<template>
  <el-container v-loading='loading' class="query-form">
    <el-main>
      <ConditionPanel ref='conditionPanel' v-model='vModel' :tableId='tableId' :schemeId='schemeId' :curNode='curNode' :exclude='exclude' :routerId='routerId'></ConditionPanel>
    </el-main>
    <el-aside class="condition-aside" width="210px">
      <el-row :gutter='5'>
        <el-col>
          <el-button-group>
            <el-tooltip  effect="light" content="搜索" placement="top-start">
              <el-button type="primary" icon="el-icon-search" @click='onSearch()' :plain='true'></el-button>
            </el-tooltip>
            <el-tooltip  effect="light" content="简要" placement="top-start">
              <el-button type='primary' icon='el-icon-d-arrow-left' @click='onMoreCodition()' :plain='true'></el-button>
            </el-tooltip>
          </el-button-group> 
            <!-- <el-button type='primary' icon='el-icon-search' @click='onSearch()' :plain='true'>搜索</el-button>
            <el-button type='info' icon='el-icon-d-arrow-left' @click='onMoreCodition()' :plain='true'>简要</el-button> -->
        </el-col>
      </el-row>
      <el-row :gutter='5'>
        <el-col>
          <el-button-group>
            <el-tooltip  effect="light" content="重置" placement="top-start">
              <el-button type="primary" icon='el-icon-refresh-left' @click='onResetCondition()' :plain='true'></el-button>
            </el-tooltip>
            <el-tooltip  effect="light" content="保存" placement="top-start">
              <el-button type="primary" icon='el-icon-tickets' @click='onSaveCondition' :plain='true'></el-button>
            </el-tooltip>
          </el-button-group> 
            <!-- <el-button icon='el-icon-refresh-left' @click='onResetCondition()' :plain='true'>重置</el-button>
            <el-button icon='el-icon-tickets' @click='onSaveCondition' :plain='true'>保存</el-button> -->
        </el-col>
      </el-row>
      <el-row :gutter='5'>
        <el-col>
          <el-select style="width: 190px" v-model="conditionSelected" value-key='id' clearable placeholder='我的查询' @change='onChange'>
            <el-option v-for="item in conditionList" :key="item.id" :label="item.name" :value="item">
              <span style="float: left">{{ item.name }}</span>
              <span style="padding:0 0 0 30px; float: right; color: #8492a6; font-size: 13px">
                <i class='el-icon-delete' @click.stop='onDeleteCondition(item)'></i>
              </span>
            </el-option>
          </el-select>
        </el-col>
      </el-row>
    </el-aside>

    <!-- 保存查询的名称对话框 -->
    <el-dialog :title='conditionSelected ? "更新查询标题" : "输入查询标题"' :visible.sync='dialogVisible' width='350px'>
      <el-form :model='nameModel' ref='nameForm' label-width='0px' label-position='right' class='edit-form' :rules='formRules'>
        <el-form-item prop='name'>
          <el-input v-model='nameModel.name'></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onDoSaveCondition">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
import BaseUI from './baseUI'
import { listQueryConditionAll, saveQueryCondition, deleteQueryCondition } from '@/api/sys/queryCondition'
import ConditionPanel from './conditonPanel'
export default {
  extends: BaseUI,
  name: 'QueryForm',
  components: {
    ConditionPanel
  },
  data() {
    return {
      vModel: null,
      conditionSelected: null, // 我的查询列表框选择的值
      conditionList: [],       // 我的查询列表框选项列表
      dialogVisible: false,   // 查询标题框显示标志
      nameModel: {
        name: ''   // 查询标题名
      },

      formRules: {
        'name': [
            { required: true, message: '请输入查询标题', trigger: 'blur' }
        ],
      }
    }
  },
  props: {
    value: {
      type: Object,
      default: () => {return {}}
    },
    tableId: {
      type: String
    },
    schemeId: {
      type: String
    },
    // 不作为条件的字段
    exclude: {
      type: String,
      default: () => {return null}
    },
    curNode: {
      type: Object,
      default: () => {return {}}
    },
    routerId: {
      type: String
    }
  },
  watch:{
    vModel(val, oldVal) {
      if(val != oldVal){
        this.$emit('input', this.vModel)
      }
    }
  },
  methods: {
    init() {
      listQueryConditionAll({
        params: [{columnName: 'user_id', queryType: '=', value: currentUser.id},
          {columnName: 'router_id', queryType: '=', value: this.routerId}
        ],
        columnName: 'name',       // 排序字段名
        order: ''             // 排序
      }).then(responseData => {
        if(responseData.code == 100) {
          this.conditionList = responseData.data
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onChange(val) {
      if(val) {
        this.vModel = JSON.parse(val.conditions)
        this.$emit('input', this.vModel)
        this.onSearch()
      }
    },
    onSearch() {
      this.$emit('search')
    },
    onMoreCodition() {
      this.$emit('moreCodition')
    },
    onResetCondition() {
      this.$refs.conditionPanel.$emit('reset')
    },
    onDeleteCondition(condition) {
      deleteQueryCondition(condition).then(responseData => {
        if(responseData.code == 100) {
          let arr = this.conditionList.filter(item => item.id != condition.id)
          this.conditionList = arr
          this.showMessage({type: 'success', msg: '删除成功'})
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    // 点击保存按钮
    onSaveCondition() {
      if(this.conditionSelected) {
        this.nameModel.name = this.conditionSelected.name
      } else {
        this.nameModel.name = ''
      }
      this.dialogVisible = true
    },
    // 点击保存对话框确定按钮
    onDoSaveCondition() {
        this.$refs['nameForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          saveQueryCondition({
            id: this.conditionSelected ? this.conditionSelected.id : null,
            userId: currentUser.id,
            routerId: this.routerId,
            name: this.nameModel.name,
            conditions: JSON.stringify(this.value)
          }).then(responseData => {
            if(responseData.code == 100) {

              // todo 查询列表
              listQueryConditionAll({
                params: [{columnName: 'user_id', queryType: '=', value: currentUser.id},
                  {columnName: 'router_id', queryType: '=', value: this.routerId}
                ],
                columnName: 'name',       // 排序字段名
                order: ''             // 排序
              }).then(responseData => {
                if(responseData.code == 100) {
                  this.conditionList = responseData.data
                }
              })

              this.dialogVisible = false
              this.showMessage({type: 'success', msg: '保存成功'})
            } else {
              this.showMessage(responseData)
            }
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)
          })

        } else {
          return false
        }
      });
    }
  },
  mounted(){
    this.vModel = this.value
    this.$nextTick(() => {
      this.init()
    })
  }
}
</script>
<style lang="scss" scoped>
  .condition-aside {
    .el-row {
      margin-bottom: 5px;
    }
  }
</style>
