<script setup>
import $ from "jquery";
import {ElMessage} from "element-plus";
import {ref} from "vue";
import store from "@/store";
const create55xunlian=()=>{
  $.ajax({
    url: "http://localhost:8089/createFiveXunLian",
    type: "Post",
    success(resp) {
      console.log(resp)
    },
    error(resp) {
      console.log(resp)
      ElMessage.error(resp.msg);
    }
  });
}
const  zdjsdj= () =>{
  let status=ref('')
  let url=ref('')
  if (store.state.isAutoAccept===true){
      status.value='StopWebSocketApi'
      url.value="[6, \"OnJsonApiEvent_lol-lobby_v2_lobby\"]"
  }else{
      status.value='StartWebSocketApi'
       url.value="[5, \"OnJsonApiEvent_lol-lobby_v2_lobby\"]"
  }
  $.ajax({
    url:  `http://localhost:8089/${encodeURIComponent(status.value)}`,
    type: "Get",
    data:{
      message:url.value
    },
    success(resp) {
      console.log(resp)
    },
    error(resp) {
      console.log(resp)
      ElMessage.error(resp.msg);
    }
  });
  store.state.isAutoAccept=!store.state.isAutoAccept
}
const backdrop=ref('')
const getBackgrop=()=>{
  $.ajax({
    url: "http://localhost:8089/getBackdrop",
    type: "Get",
    data:{
      name:backdrop.value
    },
    success(resp) {
      window.open(resp)
      console.log(resp)
    },
    error(resp) {
      console.log(resp)
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

</template>

<style scoped>

</style>