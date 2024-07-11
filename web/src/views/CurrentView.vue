<!--<script setup>-->
<!--import {ref} from "vue";-->
<!--import {ElMessage} from "element-plus";-->
<!--import $ from "jquery";-->
<!--const tableData1=ref([])-->
<!--const tableData2=ref([])-->
<!--const tableData3=ref([])-->
<!--const tableData4=ref([])-->
<!--const tableData5=ref([])-->

<!--const getData=()=>{-->
<!--  $.ajax({-->
<!--    url: "http://localhost:8089/getCurrentGameSummonersId",-->
<!--    type: "GET",-->
<!--    success(resp) {-->
<!--      tableData1.value=resp[0]-->
<!--      tableData2.value=resp[1]-->
<!--      tableData3.value=resp[2]-->
<!--      tableData4.value=resp[3]-->
<!--      tableData5.value=resp[4]-->
<!--      console.log(resp);-->
<!--    },-->
<!--    error(resp) {-->
<!--      console.log(resp);-->
<!--      ElMessage.error(resp.msg);-->
<!--    }-->
<!--  });-->
<!--}-->
<!--getData()-->
<!--</script>-->

<!--<template>-->
<!--  <el-row :gutter="20">-->
<!--    <el-col :span="5">-->
<!--      <el-table :data="tableData1" style="width: 100%">-->
<!--        <el-table-column prop="gameName" label="Date" width="180" />-->
<!--        <el-table-column prop="championName" label="Name" width="180" />-->
<!--        <el-table-column prop="mapName" label="Address" />-->
<!--        <el-table-column prop="mapName" label="Address" />-->
<!--        <el-table-column prop="zhanji" label="Address" />-->
<!--      </el-table>-->
<!--    </el-col>-->
<!--    <el-col :span="5">-->
<!--      <el-table :data="tableData2" style="width: 100%">-->
<!--        <el-table-column prop="date" label="Date" width="180" />-->
<!--        <el-table-column prop="name" label="Name" width="180" />-->
<!--        <el-table-column prop="address" label="Address" />-->
<!--      </el-table>-->
<!--    </el-col>-->
<!--    <el-col :span="5">-->
<!--      <el-table :data="tableData3" style="width: 100%">-->
<!--        <el-table-column prop="date" label="Date" width="180" />-->
<!--        <el-table-column prop="name" label="Name" width="180" />-->
<!--        <el-table-column prop="address" label="Address" />-->
<!--      </el-table>-->
<!--    </el-col>-->
<!--    <el-col :span="5">-->
<!--      <el-table :data="tableData4" style="width: 100%">-->
<!--      <el-table-column prop="date" label="Date" width="180" />-->
<!--      <el-table-column prop="name" label="Name" width="180" />-->
<!--      <el-table-column prop="address" label="Address" />-->
<!--    </el-table></el-col>-->
<!--    <el-col :span="4">-->
<!--      <el-table :data="tableData5" style="width: 100%">-->
<!--        <el-table-column prop="date" label="Date" width="180" />-->
<!--        <el-table-column prop="name" label="Name" width="180" />-->
<!--        <el-table-column prop="address" label="Address" />-->
<!--      </el-table>-->
<!--    </el-col>-->
<!--  </el-row>-->
<!--</template>-->

<!--<style scoped>-->

<!--.el-row {-->
<!--  margin-bottom: 20px;-->
<!--}-->
<!--.el-row:last-child {-->
<!--  margin-bottom: 0;-->
<!--}-->
<!--.el-col {-->
<!--  border-radius: 4px;-->
<!--}-->

<!--.grid-content {-->
<!--  border-radius: 4px;-->
<!--  min-height: 36px;-->
<!--}-->
<!--</style>-->
<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";
import $ from "jquery";

const tableData1 = ref([]);
const tableData2 = ref([]);
const tableData3 = ref([]);
const tableData4 = ref([]);
const tableData5 = ref([]);

const preprocessData = (data) => {
  return data.map(item => {
    return {
      ...item,
      zhanji: `${item.kills}-${item.deaths}-${item.assists}`
    };
  });
};

const getData = () => {
  $.ajax({
    url: "http://localhost:8089/getCurrentGameSummonersId",
    type: "GET",
    success(resp) {
      tableData1.value = preprocessData(resp[0]);
      tableData2.value = preprocessData(resp[1]);
      tableData3.value = preprocessData(resp[2]);
      tableData4.value = preprocessData(resp[3]);
      tableData5.value = preprocessData(resp[4]);
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
};

getData();
</script>

<template>
  <el-row :gutter="20">
    <el-col :span="5">
      <el-table :data="tableData1" style="width: 100%">
        <el-table-column prop="gameName" label="Game Name" width="180" />
        <el-table-column prop="championName" label="Champion Name" width="180" />
        <el-table-column prop="mapName" label="Map Name" />
        <el-table-column prop="zhanji" label="K/D/A" />
      </el-table>
    </el-col>
    <el-col :span="5">
      <el-table :data="tableData2" style="width: 100%">
        <el-table-column prop="gameName" label="Game Name" width="180" />
        <el-table-column prop="championName" label="Champion Name" width="180" />
        <el-table-column prop="mapName" label="Map Name" />
        <el-table-column prop="zhanji" label="K/D/A" />
      </el-table>
    </el-col>
    <el-col :span="5">
      <el-table :data="tableData3" style="width: 100%">
        <el-table-column prop="gameName" label="Game Name" width="180" />
        <el-table-column prop="championName" label="Champion Name" width="180" />
        <el-table-column prop="mapName" label="Map Name" />
        <el-table-column prop="zhanji" label="K/D/A" />
      </el-table>
    </el-col>
    <el-col :span="5">
      <el-table :data="tableData4" style="width: 100%">
        <el-table-column prop="gameName" label="Game Name" width="180" />
        <el-table-column prop="championName" label="Champion Name" width="180" />
        <el-table-column prop="mapName" label="Map Name" />
        <el-table-column prop="zhanji" label="K/D/A" />
      </el-table>
    </el-col>
    <el-col :span="4">
      <el-table :data="tableData5" style="width: 100%">
        <el-table-column prop="gameName" label="Game Name" width="180" />
        <el-table-column prop="championName" label="Champion Name" width="180" />
        <el-table-column prop="mapName" label="Map Name" />
        <el-table-column prop="zhanji" label="K/D/A" />
      </el-table>
    </el-col>
  </el-row>
</template>

<style scoped>
.el-row {
  margin-bottom: 20px;
}
.el-row:last-child {
  margin-bottom: 0;
}
.el-col {
  border-radius: 4px;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
</style>
