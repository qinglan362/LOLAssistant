<script setup>
import { ElMessage } from 'element-plus'
import $ from 'jquery'
import { ref} from "vue";
const  name=ref('Love#85916')
const  MatchHistory=ref([])
const getInfo=()=>{
  $.ajax({
    url: "http://localhost:8089/matchesFromPuuid",
    type: "Get",
    data: {
      name:name.value,
    },
    success(resp) {
      console.log(resp)
      MatchHistory.value=resp
      console.log(MatchHistory.value[0][0].gameName)
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

      <el-input v-model="name"  style="width: 200px"/>
      <el-button  type="primary" style="margin-left: 20px" @click="getInfo">
        获取战绩
      </el-button>
    </el-form-item>
  </el-form>

  <el-table :data="MatchHistory" stripe style="width: 100%">
    <el-table-column prop="Object.gameName" label="gameName" width="180" />
    <el-table-column prop="deaths" label="deaths" width="180" />
    <el-table-column prop="address" label="Address" />
  </el-table>

</template>

<style scoped>

</style>