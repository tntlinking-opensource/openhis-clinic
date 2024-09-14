<template>
  <div  style="text-align:left" >
    工作流单据基础类。
  </div>
</template>
<script>
import { saveWfDraft } from '@/api/wf/wfDraft'
import { getLocalRouters, getLocalCurrentUser, getLocalDataPermisions } from '@/utils/auth'
export default {
  name: 'BizForm',
  data() {
    return {
      bizFormModel: this.initFormModel()
    }
  },
  props: {
    formKey: {
      type: String,
      default: () => {return null}
    },
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
    },
    options: {
      type: Object,
      default: () => {return null}
    }
  },       
  methods: {
    getRouterId() {
      let key = this.formKey
      if(key.substr(0,1) == '/') {
        key = key.substr(1)
      }
      key = key.substr(0, key.lastIndexOf("/"))
      let routers = getLocalRouters()
      for(let router of routers) {
        if(router.code == key) {
          return router.id
        }
      }

      return null
    },
    pushDataPermissions(params, routerId, mataId) {
      // 根据formKey重新设置router id
      routerId  = this.getRouterId()
      if(!routerId) {
        return params
      }

      let items = [];
      let permissions = getLocalDataPermisions()

      let thePermissions = permissions.filter((permission)=>{return permission.routerId == routerId && permission.metaId == mataId });
      if(thePermissions.length <= 0){
        return items
      }

      items.push({logic: 'AND', queryType: '('})
      let roleId = null
      items.push({logic: '', queryType: '('})
      for (let permission of thePermissions) {
        if(roleId == null) {
          items.push({logic: '', columnName: permission.columnName, queryType: permission.operator, value: permission.values})
          roleId = permission.roleId
        } else if (roleId != permission.roleId) {
          items.push({logic: '', queryType: ')'})
          items.push({logic: 'OR', queryType: '('})
          items.push({logic: '', columnName: permission.columnName, queryType: permission.operator, value: permission.values})
          roleId = permission.roleId
        } else {
          items.push({logic: 'AND', columnName: permission.columnName, queryType: permission.operator, value: permission.values})
        }
      }
      items.push({logic: '', queryType: ')'})
      items.push({logic: '', queryType: ')'})

      params.push.apply(params, items)
      return params
    },
    createFormData(bizFormModel, files) {
      let formData = new FormData()
      formData.append('strEntity', JSON.stringify(bizFormModel))
      for(let idx in files) {
        formData.append('attachments', files[idx].raw)
      }
      return formData
    },

    // 需要子类中重构
    initFormModel() {
      return {}
    },

    // 保存草稿，files附件列表, draftModel 草稿对象
    doSaveDraft(draftModel, files) {
      if(draftModel) {   // 从草稿打开创建，保存时更新原草稿
        draftModel.applyer = getLocalCurrentUser()
        draftModel.name = draftModel.procdefName + '-' + this.bizFormModel.name
        draftModel.formData = JSON.stringify(this.bizFormModel)
      } else {
        draftModel = {}
        draftModel.procdefId = this.procDef.id
        draftModel.procdefName = this.procDef.name
        draftModel.applyer = getLocalCurrentUser()  
        draftModel.name = this.procDef.name + '-' + this.bizFormModel.name
        draftModel.formData = JSON.stringify(this.bizFormModel)
      }
      let formData = this.createFormData(draftModel, files)
      this.setLoad()
      saveWfDraft(formData).then(responseData => {
        if(responseData.code == 100) {
          this.handleResponse(responseData)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    handleResponse(responseData) {
      this.wfDaiog.handleResponse(responseData)
    },
    setLoad() {
      this.wfDaiog.setLoad()
    },
    resetLoad() {
      this.wfDaiog.resetLoad()
    },
    showMessage(responseData) {
      this.wfDaiog.showMessage(responseData)
    },
    outputError(error) {
      this.wfDaiog.outputError(error)
    }
  }
}
</script>
