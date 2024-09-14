
export function tabOptionBtnTop(size) {
  let top
  switch (size) {
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

/**
 * 处理校验定位
 */
export function handleValidateForm(_this, object) {
  let arr = []
  let numArr = []
  for (let i in object) {
    if (object.hasOwnProperty(i)) {
      let dom = _this.$refs[i]
      if (!dom) {
        let num = +(i.split('_')[1])
        if (num > 1 && i.indexOf('PRO') > -1) {
          _this.tabIndex = num
          return
        } else {
          _this.tabIndex = 1
          return
        }
      } else {
        arr.push(dom)
        if (Object.prototype.toString.call(arr) !== '[object Object]') {
          arr.forEach(item => {
            numArr.push(item.$el.dataset.num)
          })
          _this.tabIndex = Math.min(...numArr)
        }
      }
    }
  }
  
  return false
}

/**
 * 校验列表行表单
 */
export function validateField(_this, form, index) {
  let result = true
  for (let item of _this.$refs[form].fields) {
    if(item.prop.split(".")[1] == index) {
      _this.$refs[form].validateField(item.prop, (error)=>{
        if(error){
          result = false
        }
      })
    }
    if(!result) break
  }
  return result
}

/**
 * 校验列表行
 */
export function validTableFormText(_this, form, tableData) {
  // let currentIndex = tableData.length == 0? -1 : tableData.length - 1
  // if( !_this.handleValidateField('leaseResourceTypeForm', currentIndex) ) return
  let isValid = true
  if (tableData && tableData.length > 0) {
    for (let i = 0; i < tableData.length; i++) {
      if( !validateField(_this, form, i) ) {
        isValid = false
      }
    }
  }
  return isValid
}
