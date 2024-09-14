/**
 * Created by jiachenpan on 16/11/18.
 */

export function isvalidUsername(str) {
  const valid_map = ['admin', 'editor']
  return valid_map.indexOf(str.trim()) >= 0
}

/* 合法uri*/
export function validateURL(textval) {
  const urlregex = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/
  return urlregex.test(textval)
}

/* 小写字母*/
export function validateLowerCase(str) {
  const reg = /^[a-z]+$/
  return reg.test(str)
}

/* 大写字母*/
export function validateUpperCase(str) {
  const reg = /^[A-Z]+$/
  return reg.test(str)
}

/* 大小写字母*/
export function validatAlphabets(str) {
  const reg = /^[A-Za-z]+$/
  return reg.test(str)
}
/**
 * 判断是否为空
 */
export function validatenull(val) {
  if (typeof val === 'boolean') {
    return false
  }
  if (val instanceof Array) {
    if (val.length === 0) return true
  } else if (val instanceof Object) {
    for(var key in val){
      return false
    }
    return true;
  } else {
    if (val === 'null' || val == null || val === 'undefined' || val === undefined || val === '') return true
    return false
  }
  return false
}

/**
 * 判断手机号码是否正确
 */
export function isvalidatemobile(phone) {
  const list = []
  let result = true
  let msg = ''
  var isPhone = /^0\d{2,3}-?\d{7,8}$/
  // 增加134 减少|1349[0-9]{7}，增加181,增加145，增加17[678]
  // const isMob = /^((\+?86)|(\(\+86\)))?(13[0123456789][0-9]{8}|15[012356789][0-9]{8}|18[012356789][0-9]{8}|14[57][0-9]{8}|17[3678][0-9]{8})$/
  if (!validatenull(phone)) {
    if (phone.length === 11) {
      if (isPhone.test(phone)) {
        msg = '手机号码格式不正确'
      } else {
        result = false
      }
    } else {
      msg = '手机号码长度不为11位'
    }
  } else {
    msg = '手机号码不能为空'
  }
  list.push(result)
  list.push(msg)
  return list
}

export function isMobileNumber (rule, value, callback) {
  if (rule.required) {
    if (!value) {
      callback(new Error("请输入电话号码"));
    } else {
      const reg = /^1[3|4|5|6|7|8|9][0-9]\d{8}$/;
      const isPhone = reg.test(value);
      value = Number(value); //转换为数字
      if (typeof value === "number" && !isNaN(value)) {//判断是否为数字
        value = value.toString(); //转换成字符串
        if (value.length !== 11 || !isPhone) { //判断是否为11位手机号
          callback(new Error("手机号码格式如:138xxxx8888"));
        } else {
          callback();
        }
      } else {
        callback(new Error("请输入电话号码"));
      }
    }
  } else {
    callback();
  }
};

/**
 * 非法字符
 */
export function validaPatrn(str) {
  const patrn = /[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]/im; 
  return patrn.test(str)
}

/**
 * 中文
 */
export function validaChinese(str) {
  const reg = /^[^\u4e00-\u9fa5]*$/
  return reg.test(str)
}

/**
 * 经纬度
 */
 export function isLatitudeAndlongitude (rule, value, callback) {
    let LatitudeAndlongitude = ''
    if(value.indexOf("，") != -1){  //寻找每一个中文逗号，并替换
      value = value.replace(/，/ig,',');
    }
    LatitudeAndlongitude = value.split(",") 
    // 经度,整数部分为0-180小数部分为0到15位
    var longreg = /^(\-|\+)?(((\d|[1-9]\d|1[0-7]\d|0{1,3})\.\d{0,15})|(\d|[1-9]\d|1[0-7]\d|0{1,3})|180\.0{0,15}|180)$/
    // 纬度,整数部分为0-90小数部分为0到15位
    var latreg = /^(\-|\+)?([1-8]?\d{1}\.\d{0,15}|90\.0{0,15}|[1-8]?\d{1}|90)$/
    if (LatitudeAndlongitude[0] && !longreg.test(LatitudeAndlongitude[0])) {
      callback(new Error('经度整数范围正负0-180,小数0到15位!东经正,西经负。'))
    } else if (LatitudeAndlongitude[1] && !latreg.test(LatitudeAndlongitude[1])){
      callback(new Error('纬度整数范围正负0-90,小数0到15位!北纬正,南纬负。'))
    } else if (LatitudeAndlongitude.length > 2 ){
      callback(new Error('格式错误!经纬度格式如: 118.88 , 78.88'))
    } else {
      callback()
    }
}