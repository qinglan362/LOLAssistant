<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";
import $ from "jquery";

const All = ref([]);


const getData = () => {
  $.ajax({
    url: "http://localhost:8089/getCurrentGameSummonersId",
    type: "GET",
    success(resp) {
        All.value=resp;
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
};

getData();

const imageInfoSrc = (base64Image) => {
  return `http://localhost:8089/images/${base64Image}`;
};
const winOrFalse=(win)=>{
  if (win==='false'){
    return '失败'
  }else{
    return '胜利'
  }
}
const getBackgorundColor = ({row}) => {
  return row.win
      ? 'background: linear-gradient(to right, rgb(161,219,227), white)'
      : 'background: linear-gradient(to right, rgb(235,165,165), white)';
};
</script>

<template>
  <el-row :gutter="24">
    <el-col :span="6" v-for="(item,index) in All" :key="index">
       {{item.name}}
      <el-avatar :size="60" :src="imageInfoSrc(item.icon)" @error="errorHandler">
        <img
            src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png"
         alt=""/>
      </el-avatar>
      <el-avatar :size="60" :src="imageInfoSrc(item.rank)" @error="errorHandler">
        <img
            src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png"
            alt=""/>
      </el-avatar>
      <el-table :row-style="getBackgorundColor" :show-header="false" :data="item.matchRecords" style="width: 100%">
          <el-table-column prop="championName" label="角色" width="60" />
          <el-table-column  label="" width="60">
            <template #default="scope">
              <img
                  width="50px"
                  height="50px"
                  :src="imageInfoSrc(scope.row.championIcon)"
                  alt=""/>
            </template>
          </el-table-column>
        <el-table-column prop="mapName" label="模式"  width="75"/>
        <el-table-column  label="K/D/A"  width="50">
          <template #default="scope">
            {{scope.row.kills}}/{{scope.row.deaths}}/{{scope.row.assists}}
          </template>
        </el-table-column>
        <el-table-column prop="date" label="日期"  width="75"/>
        <el-table-column   width="50">
          <template #default="scope">
            {{winOrFalse(scope.row.win)}}
          </template>
        </el-table-column>
      </el-table>
    </el-col>
  </el-row>
</template>

<style scoped>

</style>