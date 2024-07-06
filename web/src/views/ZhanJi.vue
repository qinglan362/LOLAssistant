<script setup>
import { ElMessage } from 'element-plus'
import $ from 'jquery'
import { ref} from "vue";
const  name=ref('')
const getInfo=()=>{
  $.ajax({
    url: "http://localhost:8089/matchesFromPuuid",
    type: "Get",
    data: {
      name:name.value,
    },
    success(resp) {
      console.log(resp)
    },
    error(resp) {
      console.log(resp)
      ElMessage.error(resp.msg);
    }
  });
}
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
</script>

<template>
  <el-form
      label-width="auto"
      style="max-width: 600px"
  >
    <el-form-item label="昵称">
      <el-input v-model="name" /> <el-button type="primary" @click="getInfo">获取战绩</el-button>
    </el-form-item>

  </el-form>
  <el-form
      label-width="auto"
      style="max-width: 600px"
  >
    <el-form-item label="">
    </el-form-item>
    <el-button type="primary" @click="create55xunlian">创建55训练模式</el-button>
  </el-form>
</template>

<style scoped>

</style>