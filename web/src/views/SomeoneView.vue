<script setup>
import { ref } from "vue";
import $ from "jquery";
import { ElMessage } from "element-plus";

const name = ref('Love#85916');
const MatchHistory = ref([]);
const tableData = ref([]);
const tableData2Win = ref([]);
const tableData2False = ref([]);

const getInfo = () => {
  tableData.value = [];
  tableData2Win.value = [];
  tableData2False.value = [];
  MatchHistory.value = [];
  $.ajax({
    url: "http://localhost:8089/matchesFromPuuid",
    type: "GET",
    data: {
      name: name.value,
    },
    success(resp) {
      console.log(resp);
      MatchHistory.value = resp;
      console.log(MatchHistory.value[0][0].gameName);
      for (let i = 0; i < MatchHistory.value.length; i++) {
        for (let j = 0; j < MatchHistory.value[i].length; j++) {
          if (MatchHistory.value[i][j].gameName === name.value)
            tableData.value.push(MatchHistory.value[i][j]);
        }
      }
      console.log(MatchHistory);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
};

const handleGameNameClick = (gameName) => {
  name.value = gameName;
  getInfo();
};

function winFormatter(cellValue) {
  return cellValue.win ? '胜利' : '失败';
}

const getBackgorundColor = ({row}) => {
  return row.win
      ? 'background: linear-gradient(to right, rgb(161,219,227), white)'
      : 'background: linear-gradient(to right, rgb(235,165,165), white)';
};

const getOneMatch = (row) => {
  tableData2Win.value = [];
  tableData2False.value = [];
  for (let i = 0; i < MatchHistory.value.length; i++) {
    if (MatchHistory.value[i][0].gameId === row.gameId) {
      for (let j = 0; j < MatchHistory.value[i].length; j++) {
        if (MatchHistory.value[i][j].win === true)
          tableData2Win.value.push(MatchHistory.value[i][j]);
        else {
          tableData2False.value.push(MatchHistory.value[i][j]);
        }
      }
    }
  }
  console.log(tableData2Win);
};
</script>

<template>
  <div class="input-container">
    <el-input v-model="name" style="width: 150px" placeholder="请输入ID"></el-input>
    <el-button type="primary" @click="getInfo">获取该人战绩</el-button>
    <h4 style="margin-left: 20px">获取战绩可能有2-4秒延迟</h4>
  </div>

  <el-row style="margin-top: 20px">
    <el-col :span="8">
      <el-table @row-click="getOneMatch" :row-style="getBackgorundColor" :show-header="false" :data="tableData"
                style="width: 100%">
        <el-table-column prop="championName" label="championName" width="120"/>
        <el-table-column prop="mapName" label="mapName" width="140"/>
        <el-table-column prop="date" label="date" width="180"/>
        <el-table-column
            prop="win"
            label="win"
            width="60"
            :formatter="winFormatter"
        />
      </el-table>
    </el-col>

    <el-col :span="16">
      <el-table :data="tableData2Win"
                row-style="background-color:rgb(221,242,245)"
                style="width: 100%"
      >
        <el-table-column label="游戏昵称" width="180">
          <template #default="scope">
            <el-link @click="handleGameNameClick(scope.row.gameName)">{{ scope.row.gameName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="championName" label="角色名称" width="180"/>
        <el-table-column prop="champLevel" label="角色等级" width="180"/>
        <el-table-column prop="kills" label="击杀数" width="180"/>
        <el-table-column prop="deaths" label="死亡数" width="180"/>
        <el-table-column prop="assists" label="助攻数" width="180"/>
      </el-table>

      <el-table :data="tableData2False"
                row-style="background-color:rgb(252,241,241)"
                style="width: 100%;
                 margin-top: 50px">
        <el-table-column label="游戏昵称" width="180">
          <template #default="scope">
            <el-link @click="handleGameNameClick(scope.row.gameName)">{{ scope.row.gameName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="championName" label="角色名称" width="180"/>
        <el-table-column prop="champLevel" label="角色等级" width="180"/>
        <el-table-column prop="kills" label="击杀数" width="180"/>
        <el-table-column prop="deaths" label="死亡数" width="180"/>
        <el-table-column prop="assists" label="助攻数" width="180"/>
      </el-table>
    </el-col>
  </el-row>
</template>

<style scoped>
.input-container {
  display: flex;
  align-items: center;
}

.input-container h5 {
  margin-left: 10px;
}
</style>
