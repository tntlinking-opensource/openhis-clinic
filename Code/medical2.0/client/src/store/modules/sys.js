
const state = {
  type: 'sys',
  panelShow: false
}

const actions = {
  showPanel({ commit }, data) {
    commit('SHOW_PANEL', data)
  }
}

const mutations = {
  SHOW_PANEL(state, { isShow, type }) {
    state.panelShow = isShow;
    state.type = type
  }
}

export default {
  namespaced: true,
  state,
  actions,
  mutations
}
