<template>
    <el-form :model='expenseModel' :rules='formRules'
      ref='expenseForm' label-width='120px' label-position='right' class='edit-form'>
    <!-- 主表单  开始-->
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='申请人' prop='applier.id'>
            <el-input v-if='dialogProps.action == "view"' :disabled='true' v-model='expenseModel.applier.name'></el-input>
            <el-select v-else v-model='expenseModel.applier' value-key='id' filterable clearable placeholder='请选择申请人'>
             <el-option v-for='applier in applier_List' :key='applier.id' :label='applier.name' :value='applier'></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='事由' prop='name'>
            <el-input :disabled='dialogProps.action == "view"' v-model='expenseModel.name' :maxlength='40' :placeholder='dialogProps.action == "view"? "" : "请输入事由"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col :span='24/1'>
          <el-form-item label='金额' prop='amount'>
            <number-input v-if='dialogProps.action == "view"' disabled v-model="expenseModel.amount" currency='CNY' :precision="2"></number-input>
            <number-input v-else v-model="expenseModel.amount" currency='CNY' :precision="2"></number-input>
          </el-form-item>
        </el-col>
      </el-row>
              <el-row>
        <el-col>
          <el-form-item label='备注信息' prop='remarks'>
            <el-input :disabled='dialogProps.action == "view"' v-model='expenseModel.remarks' type='textarea' 
             :maxlength='255' :placeholder='dialogProps.action == "view"? "" : "请输入备注信息"' ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    <!-- 主表单  结束-->

    <!--子表：   费用明细 开始-->
      <el-divider content-position="left"> 费用明细</el-divider>
      <el-row>
        <el-col>
    <el-table ref='expense' :data='expenseModel.expenseDetailList' border @current-change='(currentRow, oldCurrentRow) => {expenseDetailCurrentRow = currentRow}'>
      <el-table-column prop='name' label='费用明细' min-width='180px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'><span style="color:#F56C6C;margin-right: 4px">*</span>费用明细</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === expenseDetailCurrentRow' :prop="`expenseDetailList.${$index}.name`" :rules='formRules.expenseDetail_name' label-width=0>
            <el-input v-model='row.name' :maxlength='128'clearable placeholder='请输入费用明细'></el-input>
          </el-form-item>
          <span v-else>{{row.name}}</span>
        </template>
      </el-table-column>
      <el-table-column prop='amount' label='金额' min-width='80px' header-align='center' align='right'>
        <template slot='header' slot-scope='{row,$index}'><span style="color:#F56C6C;margin-right: 4px">*</span>金额</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === expenseDetailCurrentRow' :prop="`expenseDetailList.${$index}.amount`" :rules='formRules.expenseDetail_amount' label-width=0>
            <number-input v-model="row.amount" currency='CNY' :precision="2" placeholder='请输入金额'></number-input>
          </el-form-item>
          <span v-else>{{row.amount}}</span>
        </template>
      </el-table-column>
      <el-table-column prop='remarks' label='备注信息' min-width='240px' header-align='center'>
        <template slot='header' slot-scope='{row,$index}'>备注信息</template>
        <template slot-scope='{row,$index}'>
          <el-form-item v-if='dialogProps.action != "view" && row === expenseDetailCurrentRow' :prop="`expenseDetailList.${$index}.remarks`" :rules='formRules.expenseDetail_remarks' label-width=0>
            <el-input v-model='row.remarks' type='textarea' :rows='1'
               :maxlength='255'clearable placeholder='请输入备注信息'></el-input>
          </el-form-item>
          <span v-else>{{row.remarks}}</span>
        </template>
      </el-table-column>
      <el-table-column v-if='dialogProps.action != "view"' label='操作' header-align='center' align='center' width='60px' fixed='right'>
        <template slot-scope='scope'>
          <el-tooltip class='item' effect='light' content='删除' placement='top-start'>
            <i class='el-icon-delete' style='color:#F56C6C;cursor:pointer;'
              @click='onDeleteRow(scope.$index, expenseModel.expenseDetailList)'></i>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>
    <el-button v-if='dialogProps.action != "view"' type='primary' 
      @click='onAddExpenseDetailRow(expenseModel.expenseDetailList)'>添加</el-button>
        </el-col>
      </el-row>
      <!--子表：   费用明细 结束-->
    </el-form>
</template>

<script>
import BizForm from '@/views/wf/common/bizForm'
import { validatenull } from '@/utils/validate'
import { listUserAll } from '@/api/admin/user'
import { listDictItemAll } from '@/api/sys/dictItem'
import { listExpenseAll } from '@/api/test/expense'

import { getExpenseById, createExpense, getExpenseByTaskId, agreeExpense, forwardExpense, commissionExpense, proposeExpense, backExpense, rejectExpense, terminateExpense, listBackActivity } from '@/api/test/expense'
export default {
  extends: BizForm,
  name: 'expense-wf-form',
  components: {
  },
  data() {
    return {
      expenseModel: this.initFormModel(),
          applier_List: [],  // 申请人
          expense_ExpenseDetail_List: [],    // 费用
       dialogProps: {
        visible: false,
        action: '',
        title: ''
      },
      formRules: {
        // 主表验证
        'applier.id': [
            { required: true, message: '请选择申请人', trigger: 'change' }
        ],
        'name': [
            { required: true, message: '请输入事由', trigger: 'blur' }
        ],
        'amount': [
            { required: true, message: '请选择金额', trigger: 'blur' }
        ],
        // 子表验证 费用明细
        'expenseDetail_expense': [
            { required: true, message: '请输入费用', trigger: 'blur' }
        ],
        'expenseDetail_name': [
            { required: true, message: '请输入费用明细', trigger: 'blur' }
        ],
        'expenseDetail_amount': [
            { required: true, message: '请输入金额', trigger: 'blur' }
        ],
      },
    expenseDetailCurrentRow: {},     // 子表 费用明细 当前选择行
    }
  },
  props: {
    wfDaiog: {
      type: Object,
      default: () => {return null}
    },
    // 动作标志 create，approve
    action: {
      type: String,
      default: () => {return ''}
    },
    task: {
      type: Object,
      default: () => {return null}
    },
    procDef: {
      type: Object,
      default: () => {return null}
    },
    bizData: {
      type: Object,
      default: () => {return null}
    }
  },
  methods: {
    getModelDataById(bizId) {
      this.setLoad()
      getExpenseById(bizId).then(responseData => {
        if(responseData.code == 100) {
          this.expenseModel = responseData.data
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    loadModelData(taskId) {
      this.setLoad()
      getExpenseByTaskId(taskId).then(responseData => {
        if(responseData.code == 100) {
          this.expenseModel = responseData.data
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    // 创建一个新流程 process流程 actNodes是提交的用户任务节点及其审批用户
    doCreate(files, actNodes) {
      this.$refs['expenseForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.expenseModel.actNodes = actNodes   // 选择节点及审批用户

          let formData = this.createFormData(this.expenseModel, files)
          createExpense(this.procDef.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)
          })

        }
      })
    },
    // 同意 comment 意见, files文件附件列表，actNodes提交节点和用户信息
    doAgree(comment, files, actNodes) {
      this.$refs['expenseForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.expenseModel.actNodes = actNodes   // 选择节点及审批用户
          this.expenseModel.taskComement = comment  // 意见

          let formData = this.createFormData(this.expenseModel, files)
          agreeExpense(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)
          })
        }
      })
    },
    // 转派任务
    doForward(comment, files, actNodes) {
      this.$refs['expenseForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.expenseModel.actNodes = actNodes   // 选择节点及审批用户
          this.expenseModel.taskComement = comment  // 意见

          let formData = this.createFormData(this.expenseModel, files)
          forwardExpense(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)
          })
        }
      })
    },
    // 委派任务
    doCommission(comment, files, actNodes) {
      this.$refs['expenseForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.expenseModel.actNodes = actNodes   // 选择节点及审批用户
          this.expenseModel.taskComement = comment  // 意见

          let formData = this.createFormData(this.expenseModel, files)
          commissionExpense(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)
          })
        }
      })
    },
    // 拟办（办理委派过来的任务）
    doPropose(comment, files) {
      this.$refs['expenseForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.expenseModel.taskComement = comment  // 意见

          let formData = this.createFormData(this.expenseModel, files)
          proposeExpense(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)
          })
        }
      })
    },
    // 拒绝
    doReject(comment, files) {
      this.$refs['expenseForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.expenseModel.taskComement = comment  // 意见

          let formData = this.createFormData(this.expenseModel, files)
          rejectExpense(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)
          })
        }
      })
    },
    // 退回 comment 意见, actNodes退回节点和用户信息
    doBack(comment, files, actNodes) {
      this.$refs['expenseForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          this.expenseModel.actNodes = actNodes   // 选择节点及审批用户
          this.expenseModel.taskComement = comment  // 意见

          let formData = this.createFormData(this.expenseModel, files)
          backExpense(this.task.id, formData).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)
          })
        }
      })
    },
    // 终止流程 comment 意见, actNodes退回节点和用户信息
    doTerminate(comment, files) {
      this.setLoad()
      this.expenseModel.taskComement = comment  // 意见

      let formData = this.createFormData(this.expenseModel, files)
      terminateExpense(this.task.id, formData).then(responseData => {
        if(responseData.code == 100) {
          this.dialogProps.visible = false
          this.$emit('save-finished')
        }
        this.showMessage(responseData)
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    doListActivity() {
      this.$refs['expenseForm'].validate(valid => {
        if (valid) {
          this.setLoad()
          listBackActivity(this.task.id).then(responseData => {
            this.handleResponse(responseData)
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)
          })
        }
      })
    },
    createFormData(expenseModel, files) {
      let formData = new FormData()
      formData.append('strEntity', JSON.stringify(expenseModel))
      for(let idx in files) {
        formData.append('attachments', files[idx].raw)
      }
      return formData
    },
    initFormModel(This) {
      return {
        'applier': {     // 申请人
          'id': 0,
          'name': null,
        },
        'name': '',   // 事由
        'amount': 0,    // 金额
        'remarks': '',   // 备注信息

        expenseDetailList: [],       // 子表列表
      }
    },
    initOptions() {
      // 主表
      let This = this.expenseModel
      let applier_search = {
        params: []
      }
      // 字段对应表上filter条件
        applier_search.params.push.apply(applier_search.params, [])
      // 数据权限:  用户sys_user
      this.pushDataPermissions(applier_search.params, this.$route.meta.routerId, '4004')
      this.applier_List = []
      listUserAll(applier_search).then(responseData => {
        this.applier_List = responseData.data
      })

      // 子表  费用明细
      This = this.expenseDetailCurrentRow
      let expense_search = {
        params: []
      }
      // 字段对应表上filter条件
        expense_search.params.push.apply(expense_search.params, [])
      // 数据权限: 费用申请表test_expense
      this.pushDataPermissions(expense_search.params, this.$route.meta.routerId, '4016')
      this.expense_ExpenseDetail_List = []
      listExpenseAll(expense_search).then(responseData => {
        this.expense_ExpenseDetail_List = responseData.data
      })
    },
    onAddExpenseDetailRow(tableData) {
        tableData.push({
            'expense': {     // 父表 费用
              'id': validatenull(parent) || validatenull(parent.expense) ? null : parent.expense.id,
              'name': validatenull(parent) || validatenull(parent.expense) ? null : parent.expense.name,
            },
            'name': '',   // 费用明细
            'amount': 0,    // 金额
            'remarks': '',   // 备注信息
        })
    },
    onDeleteRow(index, tableData) {
        tableData.splice(index, 1)
    }
  },
  watch: {
  },
  mounted: function() {
    this.$nextTick(() => {
      this.initOptions()
      if(this.action == 'create') {
        this.$refs['expenseForm'].resetFields()
      } else if(this.action == 'approve') {
        this.loadModelData(this.task.id)
      } else if(this.action == 'viewBiz') {
        // 需从数据库获取，否则子表没有数据
        // this.expenseModel = this.bizData
        this.getModelDataById(this.bizData.id)
      } else if(this.action == 'viewTask') {
         this.loadModelData(this.task.id)
      } else {
        console.err("=============== 业务审批单中，实现的：" + this.action)
      }
    })
  }
}
</script>
