<script setup>
import {ref} from "vue";
import $ from "jquery";
import {ElMessage} from "element-plus";
//获取战绩列表
const name = ref('边界感#23610')
const MatchesListInfo = ref([]);
const getInfo = () => {
  MatchesListInfo.value = [];
  $.ajax({
    url: "http://localhost:8089/matchesTFTFromPuuid",
    type: "GET",
    data: {
      name: name.value,
      page: currentPage.value,
      type:chooseMoshi.value
    },
    success(resp) {
      MatchesListInfo.value = resp;
      console.log(MatchesListInfo);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp);
    }
  });
};

//高亮显示哪个是自己
const puuId=ref('');
const returnMyColorMe=(row)=>{
  if (row.puuid===puuId.value){
    return "background: linear-gradient(to right, rgb(206, 186, 226), rgb(229, 247, 252));"
  }
}
//获取某一局
const oneMatchDetail=ref([]);
const getOneMatch = (row) => {
  oneMatchDetail.value = [];
  console.log(row)
  $.ajax({
    url: "http://localhost:8089/TFTMatchOneDetail",
    type: "GET",
    data: {
      currentPage: currentPage.value,
      index: MatchesListInfo.value.indexOf(row),
      name:row.name
    },
    success(resp) {
      oneMatchDetail.value = resp.tftoneMatchDetail;
      console.log(oneMatchDetail);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp);
    }
  });
};
//切换某页
const currentPage=ref(1);
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getInfo()
}
//返回游戏模式
const returnGameModeName= (val)=>{
 if (val[1]==="pairs"){
   return "双人作战"
 }
 if (val[1]==="standard"){
    if (val[0]==="ranked") {
      return "排位赛"
    }else if (val[0]==="normal"){
      return "匹配模式"
    }
 }
 if (val[1]==="turbo"){
   return "狂暴模式"
 }
 if (val[1]==="pve"){
   return "发条鸟的试炼"
 }
}
//点击名字再搜索他
const handleGameNameClick = (gameName) => {
  name.value = gameName;
  getInfo();
};
//显示图片的base64编码
const imageInfoSrc = (base64Image) => {
  return `http://localhost:8089/images/${base64Image}`;
 // return `data:image/jpeg;base64,${base64Image}`;
};
//名次
// const returnPlacement=(val)=>{
//   for (let i=0;i<val.length;i++){
//     if (val[i].gameName===name.value){
//       return val[i].placement
//     }
//   }
// }
// const moshi = [
//   {
//     value: 'all',
//     label: '全部模式',
//   },
//   {
//     value: 'turbo',
//     label: '狂暴模式',
//   },
//   {
//     value: 'pairs',
//     label: '双人作战',
//   },
//   {
//     value: 'ranked',
//     label: '排位赛',
//   },
//   {
//     value: 'normal',
//     label: '匹配赛',
//   },
//   {
//     value: 'pve',
//     label: '发条鸟的试炼',
//   }
// ]
const  chooseMoshi=ref('全部')
</script>

<template>
  <div class="input-container">
    <el-input v-model="name" style="width: 150px" placeholder="请输入ID"></el-input>
<!--    <el-select-->
<!--        v-model="chooseMoshi"-->
<!--        placeholder="Select"-->
<!--        size="large"-->
<!--        style="width: 240px;margin-left: 20px"-->
<!--    >-->
<!--      <el-option-->
<!--          v-for="item in moshi"-->
<!--          :key="item.value"-->
<!--          :label="item.label"-->
<!--          :value="item.value"-->
<!--      />-->
<!--    </el-select>-->
    <el-button type="primary" @click="getInfo">获取该人云顶之弈战绩</el-button>
    <h4 style="margin-left: 50px">获取战绩可能有2-4秒延迟</h4>
  </div>
  <el-row style="margin-top: 10px">
    <el-col :span="5">
      <el-table @row-click="getOneMatch"
                row-style="background: linear-gradient(to right, rgb(206, 186, 226), rgb(229, 247, 252));"
                :show-header="false"
                :data="MatchesListInfo"
                style="width: 100%">
        <el-table-column label="palcement" width="80">
          <template v-slot="scope">
             第{{scope.row. placement}}名
          </template>
        </el-table-column>
        <el-table-column  label="gameMode" width="120">
          <template v-slot="scope">
            {{returnGameModeName(scope.row.tags)}}
          </template>
        </el-table-column>
        <el-table-column prop="date" label="date" width="160"/>

      </el-table>
      <el-pagination
          @current-change="handleCurrentChange"
          layout="prev, pager, next"
          :total="250" />
    </el-col>

    <el-col :span="19">

        <el-table :data="oneMatchDetail"
                  :row-style="returnMyColorMe"
                  :default-sort="{ prop: 'placement', order: 'ascending' }"

        >
          <el-table-column label="游戏昵称" width="170">
            <template #default="scope">
              <el-link @click="handleGameNameClick(scope.row.gameName)">{{ scope.row.gameName }}</el-link>
            </template>
          </el-table-column>
          <el-table-column  prop="placement"
                           label="名次"
                           width="80">
          </el-table-column>
          <el-table-column prop="championName" label="角色" width="160">
            <template v-slot="scope">
              <img  :src="imageInfoSrc(scope.row.companion.image)" alt="" style="width: 35px; height: 35px">
              {{scope.row.level}}级
            </template>
          </el-table-column>
          <el-table-column label="棋子" style="width: 700px; height: 70px;">
            <template v-slot="scope">


              <div style="display: flex; align-items: center; margin-bottom: 10px;" v-for="(unit, unitIndex) in scope.row.units" :key="unitIndex">
                <el-popover
                    placement="top-start"
                    :width="200"
                    trigger="hover"
                    :content="unit.unitImage.toolTips"
                    :key="index"
                >
                  <template #reference>
                    <img :src="imageInfoSrc(unit.unitImage.image)" alt="" style="width: 35px; height: 35px">
                  </template>
                </el-popover>
                <div style="display: flex;">
                  <el-popover
                      v-for="(item, index) in unit.itemImage"
                      placement="top-start"
                      :width="200"
                      trigger="hover"
                      :content="item.toolTips"
                      :key="index"
                  >
                    <template #reference>
                      <img :src="imageInfoSrc(item.image)" alt="" style="width: 35px; height: 35px">
                    </template>
                  </el-popover>
                   </div>
              </div>

            </template>
          </el-table-column>
          <el-table-column label="羁绊" style="width: 300px">
            <template v-slot="scope">
              <el-popover
                  v-for="(item,index) in scope.row.traits"
                  placement="top-start"
                  :width="200"
                  trigger="hover"
                  :content="item.toolTips"
                  :key="index"
              >
                <template #reference>
                  <img :src="imageInfoSrc(item.image)" alt="" style="width: 35px; height: 35px">
                </template>
              </el-popover>

            </template>
          </el-table-column>
          <el-table-column label="海克斯" style="width: 200px">
            <template v-slot="scope">
              <el-popover
                  v-for="(item,index) in scope.row.augments"
                  placement="top-start"
                  :width="200"
                  trigger="hover"
                  :content="item.toolTips"
                  :key="index"
              >
                <template #reference>
                  <img :src="imageInfoSrc(item.image)" alt="" style="width: 35px; height: 35px">
                </template>
              </el-popover>
            </template>
          </el-table-column>

          <el-table-column prop="" label="段位" width="150">
            <template v-slot="scope">
              <el-popover
                  placement="top-start"
                  :width="1100"
                  trigger="hover"
              >
                <template #reference>
                  <img :src="imageInfoSrc(scope.row.rankImage)" style="width: 40px;height: 40px" alt="">
                </template >

                <el-table :data="[scope.row.rank]">
                  <el-table-column   label="排位赛" width="350">
                    <el-table-column  label="当前赛季最高段位" width="110">
                      <template #default="{ row }">
                        {{ row.ranked_TFT_CurrentSeason_HighestTier+row.ranked_TFT_CurrentSeason_HighestDivision}}
                      </template>
                    </el-table-column>
                    <el-table-column  label="当前段位" width="130">
                      <template #default="{ row }">
                        {{ row.ranked_TFT_CurrentSeason_Tier+row.ranked_TFT_CurrentSeason_Division+"  "+row.ranked_TFT_CurrentSeason_LeaguePoints}}
                      </template>
                    </el-table-column>
                    <el-table-column label="历史最高段位" width="110">
                      <template #default="{ row }">
                        {{ row.ranked_TFT_PreviousSeasonHighestTier}}
                      </template>
                    </el-table-column>
                  </el-table-column>
                  <el-table-column   label="双人作战" width="350">
                    <el-table-column  label="当前赛季最高段位" width="110">
                      <template #default="{ row }">
                        {{ row.ranked_TFT_DOUBLE_UP_CurrentSeason_HighestTier+row.ranked_TFT_DOUBLE_UP_CurrentSeason_HighestDivision}}
                      </template>
                    </el-table-column>
                    <el-table-column  label="当前段位" width="130">
                      <template #default="{ row }">
                        {{ row.ranked_TFT_DOUBLE_UP_CurrentSeason_Tier+row.ranked_TFT_DOUBLE_UP_CurrentSeason_Division+"  "+row.ranked_TFT_DOUBLE_UP_CurrentSeason_LeaguePoints}}
                      </template>
                    </el-table-column>
                    <el-table-column label="历史最高段位" width="110">
                      <template #default="{ row }">
                        {{ row.ranked_TFT_DOUBLE_UP_PreviousSeasonHighestTier}}
                      </template>
                    </el-table-column>
                  </el-table-column>
                  <el-table-column   label="狂暴模式" width="350">
                    <el-table-column  label="当前赛季最高段位" width="110">
                      <template #default="{ row }">
                        {{ row.ranked_TFT_TURBO_CurrentSeason_HighestTier+row.ranked_TFT_TURBO_CurrentSeason_HighestDivision}}
                      </template>
                    </el-table-column>
                    <el-table-column  label="当前段位" width="130">
                      <template #default="{ row }">
                        {{ row.ranked_TFT_TURBO_CurrentSeason_Tier+row.ranked_TFT_TURBO_CurrentSeason_Division+"  "+row.ranked_TFT_TURBO_CurrentSeason_LeaguePoints}}
                      </template>
                    </el-table-column>
                    <el-table-column label="历史最高段位" width="110">
                      <template #default="{ row }">
                        {{ row.ranked_TFT_TURBO_PreviousSeasonHighestTier}}
                      </template>
                    </el-table-column>
                  </el-table-column>
                </el-table>
              </el-popover>
            </template>
          </el-table-column>
        </el-table>
    </el-col>


  </el-row>
</template>

<style scoped>

</style>