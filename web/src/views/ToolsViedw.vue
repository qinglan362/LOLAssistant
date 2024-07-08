<script setup>
import $ from "jquery";
import {ElMessage} from "element-plus";
import {ref} from "vue";
const zdjs=ref(false)
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
    zdjs.value=!zdjs.value
  $.ajax({
    url: "http://localhost:8089/start-accept-game",
    type: "Get",
    success(resp) {
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
    <el-button v-if="zdjs" type="success" @click="zdjsdj">开启自动接受对局</el-button>
     <el-button v-else type="danger" @click="zdjsdj">取消自动接受对局</el-button>
</template>

<style scoped>

</style>