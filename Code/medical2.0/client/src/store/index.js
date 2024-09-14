import mutations from './mutations'
import actions from './actions'
import getters from './getters'
Vue.use(Vuex);

/**
 * 处理导入 modules 下模块
 */
let modules = {};
const modulesFiles = require.context('./modules', true, /\.js$/);
modulesFiles.keys().forEach(module => {
  const com = modulesFiles(module);
  
  // 获取模块名
  const comName = module.replace(/^\.\/(.*)\.\w+$/, '$1');
  // 添加模块对象
  modules[comName] = com.default;
})

const state = {
    showLoading: false,
    breadcrumbItems: []
};

export default new Vuex.Store({
    modules,
    state,
    mutations,
    actions,
    getters
});
