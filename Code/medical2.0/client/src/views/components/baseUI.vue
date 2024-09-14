<template>
  <div  style="text-align:left" >
    list和form窗口的基础组件类，统一处理异常错误信息。
  </div>
</template>
<script>
import { getLocalDataPermisions, clearLocalData } from '@/utils/auth'
export default {
  name: 'BaseUI',
  data() {
    return {
      currentUser: window.currentUser,  // 单前登录用户
      loadcount: 0,   // loading加载次数
      loading: false,  // 加载标志，用与显示加载遮罩

      isAlert: false   // 已经弹出alert对话标志
    }
  },
  methods: {
    setLoad() {
      this.loadcount++
      this.loading = true
    },
    resetLoad() {
      this.loadcount--
      if(this.loadcount <= 0) {
        this.loadcount = 0
        this.loading = false
      }
    },
    getAttrValue(entity, attr, javaType) {
      let pos = attr.indexOf('.')
      if(pos == -1) {
        if(javaType == "java.math.BigDecimal") {  // 金额类型，保留两位小数，千分位
          return  (entity[attr] || 0).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,')
        }
        return entity[attr]
      }
      return this.getAttrValue(entity[attr.substr(0, pos)], attr.substr(pos + 1), javaType)
    },
    // 构造更多的组合查询条件 格式 （ （ groupOne）  groupLogic （ groupTwo ））
    compositeCondition(moreParm, isFirst) {
      moreParm = moreParm || this.moreParm  // this.moreParm 是主界面的组合挑选条件
      let firstLogic = isFirst ? '' : 'AND'
      let conditions = []
      let groupOne = []
      let groupTwo = []

      if(moreParm.groupOne) {
        moreParm.groupOne.forEach((item, index) => {
          if(item.column && item.queryType && item.value && (!(item.value instanceof Array) || (item.value instanceof Array && item.value.length > 0))) {
            if(groupOne.length == 0) {
              groupOne.push({logic: '', columnName: item.column.name, queryType: item.queryType, value: item.value})
            } else {
              groupOne.push({logic: item.logic, columnName: item.column.name, queryType: item.queryType, value: item.value})
            }
          }
        })
      }
      if(moreParm.groupTwo) {
        moreParm.groupTwo.forEach((item, index) => {
          if(item.column && item.queryType && item.value && (!(item.value instanceof Array) || (item.value instanceof Array && item.value.length > 0))) {
            if(groupTwo.length == 0) {
              groupTwo.push({logic: '', columnName: item.column.name, queryType: item.queryType, value: item.value})
            } else {
              groupTwo.push({logic: item.logic, columnName: item.column.name, queryType: item.queryType, value: item.value})
            }
          }
        })
      }

      if(groupOne.length > 0 && groupTwo.length > 0) {
        conditions.push({logic: firstLogic, queryType: '('})

        conditions.push({logic: '', queryType: '('})
        conditions = conditions.concat(groupOne)
        conditions.push({logic: '', queryType: ')'})

        conditions.push({logic: moreParm.groupLogic, queryType: '('})
        conditions = conditions.concat(groupTwo)
        conditions.push({logic: '', queryType: ')'})

        conditions.push({logic: '', queryType: ')'})
      } else if(groupOne.length > 0) {
        conditions.push({logic: firstLogic, queryType: '('})
        conditions = conditions.concat(groupOne)
        conditions.push({logic: '', queryType: ')'})
      } else if(groupTwo.length > 0) {
        conditions.push({logic: firstLogic, queryType: '('})
        conditions = conditions.concat(groupTwo)
        conditions.push({logic: '', queryType: ')'})
      }

      return conditions
    },
    pushDataPermissions(params, routerId, mataId) {
      let items = [];
      let permissions = getLocalDataPermisions()
      let thePermissions = permissions.filter((permission)=>{return permission.routerId == routerId && permission.metaId == mataId });
      if(thePermissions.length <= 0){
        return params
      }

      items.push({logic: 'AND', queryType: '('})
      let isFirst = true
      for (let permission of thePermissions) {
        let moreParm = JSON.parse(permission.conditions)
        let conditions = this.compositeCondition(moreParm, isFirst)
        if(conditions.length > 0) {
          if(isFirst) {
            items = items.concat(conditions)
          } else {
            items.push({logic: 'OR', queryType: ''})
            items = items.concat(conditions)
          }

          isFirst = false
        }

      }
      items.push({logic: '', queryType: ')'})

      params.push.apply(params, items)

      return params
    },
    outputError(error) {
      this.resetLoad()
      console.error(error.response ? error.response : error.message)
      this.$alert('出错了，请按F12查看浏览器日志。', '提示', {
        type: 'error'
      })
    },
    showMessage(msgData) {
      let tip = ''
      if(msgData.type == 'error' && msgData.data) {
        console.error(msgData.data)
        tip = '， 请按F12查看浏览器日志。'
      }

      if (msgData.type == 'warning' || msgData.type == 'error') {
        if(!this.isAlert) {
          this.isAlert = true
          this.$alert(msgData.msg + tip, msgData.type == 'warning' ? '警告' : '错误', {
            type: msgData.type,
            callback: action => {
              if(this.loadcount <= 0) {
                this.isAlert = false
              }
            }
          })
        }
      } else {
        this.$message({
          showClose: true,
          dangerouslyUseHTMLString: true,
          message: msgData.msg + tip,
          type: msgData.type
        })
      }
      if (msgData.code == 20011) {
        if(this.dialogProps) {   // 隐藏对话框
          this.dialogProps.visible = false
        }
        clearLocalData()
        if(this.$router.currentRoute.fullPath.indexOf("login?redirect=") === -1){
          this.$router.push({
            path: "/login",
            // 从哪个页面跳转
            query: { redirect: this.$router.currentRoute.fullPath }
          })
        }
        this.$router.go(0)
      }
    }
  },
  watch: {
    loadcount: function(val, oldVal) {
      if(val == 0) {
        this.isAlert = false
      }
    }
  }
}
</script>
