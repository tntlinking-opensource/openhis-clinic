import { savePersonalTheme, deletePersonalTheme } from '@/api/sys/personalTheme'
import { getLocalPersonalTheme } from '@/utils/auth'

const getTheme = (key) => {
  let obj = {}
  let curTheme
  obj = getLocalPersonalTheme()
  if (obj) {
    obj = obj.theme;
    curTheme = obj[key];
    return curTheme
  }
}

const state = {
  type: 'layout',
  panelShow: false,
  theme: getTheme('theme') ,
  headerColor: getTheme('headerColor'),
  sidebarColor: getTheme('sidebarColor'),
  backgroundColor: getTheme('backgroundColor'),
  showTagsView: getTheme('showTagsView'),
  fixedHeader: getTheme('fixedHeader'),
  showLogo: getTheme('showLogo'),
  style: getTheme('style'),
  size: getTheme('size')
}

const actions = {
  showPanel({ commit }, data) {
    commit('SHOW_PANEL', data)
  },

  changeSetting({ commit }, data) {
    commit('CHANGE_SETTING', data)
  },

  saveSetting({ commit }, personalTheme) {
    return savePersonalTheme(personalTheme)
  },

  resetSetting({ commit }) {
    const id = getLocalPersonalTheme().id;
    if (id) {
      return deletePersonalTheme({ id })
    }
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
  },
  
  RESET_SETTING(state, data) {
    Object.assign(state, data);
  },
}

export default {
  namespaced: true,
  state,
  actions,
  mutations
}
