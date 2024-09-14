const state = {
  noticeTotal:0,
  drugIndateWarningTotal:0,
  drugInventoryWarningTotal:0,
  stuffIndateWarningTotal:0,
  stuffInventoryWarningTotal:0,
};

const actions = {
  SetNoticeTotal({ commit }, data) {
    commit('Set_NoticeTotal', data)
  },
  SetDrugIndateWarningTotal({commit},data){
    commit('Set_DrugIndateWarningTotal',data)
  },
  SetDrugInventoryWarningTotal({commit},data){
    commit('Set_DrugInventoryWarningTotal',data)
  },
  SetStuffIndateWarningTotal({commit},data){
    commit('Set_StuffIndateWarningTotal',data)
  },
  SetStuffInventoryWarningTotal({commit},data){
    commit('Set_StuffInventoryWarningTotal',data)
  },
};


const mutations = {
  Set_NoticeTotal(state, data){
    state.noticeTotal = data;
  },
  Set_DrugIndateWarningTotal(state,data){
    state.drugIndateWarningTotal = data
  },
  Set_DrugInventoryWarningTotal(state,data){
    state.drugInventoryWarningTotal = data
  },
  Set_StuffIndateWarningTotal(state,data){
    state.stuffIndateWarningTotal = data
  },
  Set_StuffInventoryWarningTotal(state,data){
    state.stuffInventoryWarningTotal = data
  }
};

export default {
  namespaced: true,
  state,
  actions,
  mutations
}
