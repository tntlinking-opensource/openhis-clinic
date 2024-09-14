
const state = {
  type: 'sys',
  panelShow: false,
  sysName: null, // 系统名称
  sysAbbrname: null, // 系统简称
  projectLogo: null, // 项目logo
  loginLogo: null, // 登录框logo
  sysLogo: null, // 系统主页logo
  favicon: null, // 网页顶部 favicon 图标
  loginLayout: null,
  loginBg: null, // 登录页背景图
  loginBgcolor: null, // 登录页背景色
  loginGraph: null // 登录页配图
}

const actions = {
  showPanel({ commit }, data) {
    commit('SHOW_PANEL', data)
  },
  
  changeSetting({ commit }, data) {
    commit('CHANGE_SETTING', data)
  },
  
  saveSetting({ commit }) {
  
  },
  
  resetSetting({ commit }) {
  
  }
}

const mutations = {
  SHOW_PANEL(state, { isShow, type }) {
    state.panelShow = isShow;
    state.type = type
  },
  
  CHANGE_SETTING(state, { key, value }) {
    if (state.hasOwnProperty(key)) {
      state[key] = value;
    }
  }
}

export default {
  namespaced: true,
  state,
  actions,
  mutations
}
