<script setup>
import $ from "jquery";
import {ElMessage} from "element-plus";

const getCurrent = () => {
  $.ajax({
    url: "http://localhost:8089/StartWebSocketApi",
    type: "GET",
    data: {
      message:"[6, \"OnJsonApiEvent_lol-champ-select_v1_session\"]"
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
      <router-link :to="{name:'someone'}">
        <el-button  type="primary" >
          获取某人战绩
        </el-button>
      </router-link>

      <router-link :to="{name:'current'}">
        <el-button  type="primary" style="margin-left: 100px" @click="getCurrent">
          获取当前队友战绩
        </el-button>
      </router-link>

</template>

<style scoped>

</style>