/**
 * 实现千分位过滤器
 * decimal 保留小数位数  默认为两位小数
 **/
/* export function thousandsFilter(value, decimal) {
  if (typeof (value) === 'number') {
  	if(typeof (decimal) != 'number') {
  		decimal = 2
  	}
  	return (value).toFixed(decimal).replace(/(\d)(?=(\d{3})+\.)/g, '$1,')
  }
} */

export default {
  'datetimeFormat': function(dataStr, pattern = 'YYYY-MM-DD HH:mm:ss') {
    return Vue.prototype.$moment(dataStr).format(pattern);
  },
  'dateFormat': function(dataStr, pattern = 'YYYY-MM-DD') {
    return Vue.prototype.$moment(dataStr).format(pattern);
  },
  'timeFormat': function(dataStr, pattern = 'HH:mm:ss') {
    return Vue.prototype.$moment(dataStr).format(pattern);
  },
  'formatMoney': function(v) {
    if (v > 100000000) {
      return (v / 100000000).toFixed(2) + "亿";
    } else if (v > 10000) {
      return (v / 10000).toFixed(2) + "万";
    }
    return v;
  },
  'yesNo': function(v) {
    console.log('yes no', v);
    return v ? '是' : '否';
  },
  'getObjectValue': function(obj, str = '', text = '') {
    if (!obj || typeof(obj) !== 'object' || !str) return text;
    const param = str.split('.');
    return param.reduce((res, curr, index) => {
      if ((index + 1) < param.length) {
        return res[curr] || {}
      } else {
        return res[curr] || text
      }
    }, obj)
  },
  'numberFormatter': function(number = 0) {
    number = Math.floor((+number) * 100) / 100
    let num = number.toString().split(".");
    num = num.length ? (num.length === 1 ? [num[0], ''] : num) : ['', ''];
    const numbers = num[0].split('').reverse();
    const decimal = num[1] ? '.' + num[1] : '';
    const segs = []
    while (numbers.length) segs.push(numbers.splice(0, 3).join(''))
    return segs.join(',').split('').reverse().join('') + decimal
  },
  'thousandsFilter': function(value, decimal) {
    if (typeof(value) === 'number') {
  	   if (typeof(decimal) != 'number') {
        decimal = 2
      }
      return (value).toFixed(decimal).replace(/(\d)(?=(\d{3})+\.)/g, '$1,')
    }
  }
};
