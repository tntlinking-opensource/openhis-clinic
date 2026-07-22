// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import '@/icons'
const Vue = window.Vue
import App from './App'
import router from './router'
import store from './store'
import moment from "moment"
import axios from 'axios'

// Element UI 已通过 CDN 在 index.html 中加载，默认 size 为 mini

//引入 省市区三级联动
import Distpicker from 'v-distpicker'


Vue.prototype.$moment = moment
Vue.prototype.$axios = axios

//全局使用省市区三级联动
Vue.component('v-distpicker', Distpicker)

//引入汉字转拼英码
import pinyin from 'js-pinyin'
pinyin.setOptions({checkPolyphone: false, charCase: 0})

// Element UI 主题变量已通过 CDN 加载，无需额外导入
//import * as filters from './utils/filters'
import filters from './utils/filters'

import NumberInput from './components/NumberInput'
Vue.use(NumberInput)

import './utils/global'
import VueCropper from 'vue-cropper'
Vue.use(VueCropper)

Vue.config.productionTip = false

// 强制设置 Element UI 尺寸为 mini（确保在 Vue 实例创建后生效）
Vue.prototype.$ELEMENT = { size: 'mini', zIndex: 3000 }


/* 注入全局filter*/
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

// 报表
import i18n from '@/views/report/lang'
Vue.config.devtools = process.env.NODE_ENV !== 'production';

/* eslint-disable no-new */
let vm = new Vue({
  el: '#app',
  i18n,
  store,
  router,
  template: '<App/>',
  components: { App }
})

export default vm

// 解决 ResizeObserver 循环报错（ElementUI 必加）不需要可注释
const debounce = (func, wait) => {
  let timeout;
  return function (...args) {
    clearTimeout(timeout);
    timeout = setTimeout(() => func.apply(this, args), wait);
  };
};
const resizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends resizeObserver {
  constructor(callback) {
    super(debounce(callback, 50));
  }
};
