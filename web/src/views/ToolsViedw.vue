<script setup>
import $ from "jquery";
import {ElMessage} from "element-plus";
import {ref} from "vue";
import store from "@/store";
const getChampions=()=>{
  $.ajax({
    url: "http://localhost:8089/getChampions",
    type: "Get",
    success(resp) {
      store.state.champions=resp
      console.log(store.state.champions);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
}
getChampions()
const create55xunlian = () => {
  $.ajax({
    url: "http://localhost:8089/createFiveXunLian",
    type: "Post",
    success(resp) {
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
};
const dialogTableVisible = ref(false)
const mxyx = () => {
  dialogTableVisible.value = true
  $.ajax({
    url: "http://localhost:8089/StartWebSocketApi",
    type: "Get",
    data: {
      message: "[5, \"OnJsonApiEvent_lol-champ-select_v1_session\"]"
    },
    success(resp) {
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
};
const zdjsdj = () => {
  let status = ref('');
  let message=ref('')
  if (store.state.isAutoAccept === true) {
    status.value = 'StopWebSocketApi';
    message.value='[6, "OnJsonApiEvent_lol-matchmaking_v1_search"]'
  } else {
    status.value = 'StartWebSocketApi';
    message.value='[5, "OnJsonApiEvent_lol-matchmaking_v1_search"]'
  }
      $.ajax({
        url: `http://localhost:8089/${encodeURIComponent(status.value)}`,
        type: "Get",
        data: {
          message: message.value
        },
        success(resp) {
          console.log(resp);
        },
        error(resp) {
          console.log(resp);
          ElMessage.error(resp.msg);
        }
      });
  store.state.isAutoAccept = !store.state.isAutoAccept;
}

const backdrop = ref('');

const getBackgrop = () => {
  $.ajax({
    url: "http://localhost:8089/getBackdrop",
    type: "Get",
    data: {
      name: backdrop.value
    },
    success(resp) {
      window.open(resp);
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
};
const getPickName = ()=>{
   if (store.state.championId === -1) {
     return "设置匹配秒选的英雄";
   }else {
      return "更改英雄，当前:"+store.state.champions.find(item => item.key === store.state.championId).name;
   }
}
const pickName=ref(getPickName())

const choose=()=>{
  dialogTableVisible.value=false
  pickName.value=getPickName()
  $.ajax({
    url: "http://localhost:8089/setPickChampion",
    type: "Post",
    data: {
      championId: store.state.championId
    },
    success(resp) {
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
}
const unchoose=()=>{
  store.state.championId=-1
  pickName.value=getPickName()
  $.ajax({
    url: "http://localhost:8089/setPickChampion",
    type: "Post",
    data: {
      championId: -1
    },
    success(resp) {
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
  $.ajax({
    url: "http://localhost:8089/StopWebSocketApi",
    type: "Get",
    data: {
      message:  "[6, \"OnJsonApiEvent_lol-champ-select_v1_session\"]"
    },
    success(resp) {
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
}
</script>

<template>
  <el-button type="primary" @click="create55xunlian">创建55训练模式</el-button>
  <el-button v-if="!store.state.isAutoAccept" type="success" @click="zdjsdj">开启自动接受对局</el-button>
  <el-button v-else type="danger" @click="zdjsdj">取消自动接受对局</el-button>
  <el-input style="width: 200px;margin-left: 30px" v-model="backdrop" placeholder="格式为:xxxx#xxxx"></el-input>
  <el-button type="primary" @click="getBackgrop">下载某人的生涯背景</el-button>
  <el-button type="primary" @click="mxyx">{{pickName}}</el-button>
  <el-button v-if="store.state.championId!==-1" @click="unchoose" type="danger" round>取消秒选英雄</el-button>
  <el-dialog v-model="dialogTableVisible" title="秒选英雄" width="300">
    <el-select
        v-model="store.state.championId"
        filterable
        placeholder="Select"
        style="width: 240px"
    >
      <el-option
          v-for="item in store.state.champions"
          :key="item.key"
          :label="item.name"
          :value="item.key"
      />
    </el-select>
    <el-button @click="choose" type="primary" round>确定</el-button>
  </el-dialog>

</template>

<style scoped>
</style>

