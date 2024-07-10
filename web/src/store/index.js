import { createStore } from 'vuex'
import ModuleUser from './user'

export default createStore({
  state: {
    isAutoAccept: false,
  },
  getters: {
    getIsAutoAccept: state => state.isAutoAccept
  },
  mutations: {
    setSsAutoAccept(state, value) {
      state.isAutoAccept = value;
    }
  },
  actions: {
    updateGlobalValue({ commit }, value) {
      commit('setSsAutoAccept', value);
    }
  },
  modules: {
    user:ModuleUser,
  }
})
