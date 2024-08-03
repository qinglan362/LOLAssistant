import { createStore } from 'vuex'
import ModuleUser from './user'

export default createStore({
  state: {
    isAutoAccept: false,
    champions: [],
    championId: -1,
    banChampionId: -1,
    autoNext: false,
  },
  getters: {
    getIsAutoAccept: state => state.isAutoAccept,
    getCurrentChampion: state => state.championId,
    getChampions: state => state.champions,
    getBanChampion: state => state.banChampionId,
    getAutoNext: state => state.autoNext
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
    },
    setBanChampion(state, championId) {
      state.banChampionId = championId
    },
    setAutoNext(state, value) {
      state.autoNext = value
    }
  },
  actions: {
    updateGlobalValue({commit}, value) {
      commit('setSsAutoAccept', value);
    },
    updateChampions({commit}, champions) {
      commit('setChampions', champions);
    },
    updateCurrentChampion({commit}, championId) {
      commit('setCurrentChampion', championId);
    },
    updateBanChampion({commit}, championId) {
      commit('setBanChampion', championId);
    },
    updateAutoNext({commit}, value) {
      commit('setAutoNext', value);
    }
  },
  modules: {
    user:ModuleUser,
  }
})
