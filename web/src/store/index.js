import { createStore } from 'vuex'
import ModuleUser from './user'

export default createStore({
  state: {
    isAutoAccept: false,
    champions: [],
    championId: -1,
  },
  getters: {
    getIsAutoAccept: state => state.isAutoAccept,
    getCurrentChampion: state => state.championId,
    getChampions: state => state.champions
  },
  mutations: {
    setSsAutoAccept(state, value) {
      state.isAutoAccept = value;
    },
    setChampions(state, champions) {
      state.champions = champions;
    },
    setCurrentChampion(state, championId) {
      state.championId = championId
    }
  },
  actions: {
    updateGlobalValue({ commit }, value) {
      commit('setSsAutoAccept', value);
    },
    updateChampions({ commit }, champions) {
      commit('setChampions', champions);
    },
    updateCurrentChampion({ commit }, championId) {
      commit('setCurrentChampion', championId);
    }
  },
  modules: {
    user:ModuleUser,
  }
})
