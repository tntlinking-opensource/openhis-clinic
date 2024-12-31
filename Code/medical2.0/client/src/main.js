// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import '@/icons'
// import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import moment from "moment"

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(ElementUI)

//引入 省市区三级联动
import Distpicker from 'v-distpicker'


Vue.prototype.$moment = moment

//全局使用省市区三级联动
Vue.component('v-distpicker', Distpicker)

//引入汉字转拼英码
import pinyin from 'js-pinyin'
pinyin.setOptions({checkPolyphone: false, charCase: 0})

import './assets/element-variables.scss'
//import * as filters from './utils/filters'
import filters from './utils/filters'

import NumberInput from './components/NumberInput'
Vue.use(NumberInput)

import './utils/global'
import VueCropper from 'vue-cropper'
Vue.use(VueCropper)

Vue.use(ELEMENT, {
  size: store.getters.settings.size,
})
Vue.config.productionTip = false


/* 注入全局filter*/
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

// 报表
import i18n from '@/views/report/lang'
import Vue from "vue";
Vue.config.devtools = true;

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
