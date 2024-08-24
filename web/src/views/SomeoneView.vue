<script setup>
import { ref } from "vue";
import $ from "jquery";
import { ElMessage } from "element-plus";

const name = ref('Love#85916');
const MatchesListInfo = ref([]);
const OneMatchWin = ref([]);
const OneMatchFail = ref([]);

const handleCurrentChange = (val) => {
  currentPage.value = val;
  getInfo()
}
const currentPage=ref(1);
const getInfo = () => {
  MatchesListInfo.value = [];
  OneMatchWin.value = [];
  OneMatchFail.value = [];
  $.ajax({
    url: "http://localhost:8089/matchesFromPuuid",
    type: "GET",
    data: {
      name: name.value,
      page:currentPage.value
    },
    success(resp) {
      MatchesListInfo.value = resp;
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp);
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

const wujinkuangchao=ref([])
const getOneMatch = (row) => {
  mapNameClick.value=row.mapName;
  initDouhun();
  TotalFirstToEighthTeam.value = [];
  OneMatchWin.value = [];
  OneMatchFail.value = [];
  wujinkuangchao.value=[];
  $.ajax({
    url: "http://localhost:8089/MatchOneDetail",
    type: "GET",
    data: {
      gameId: row.gameId,
    },
    success(resp) {
      resp.forEach((item) => {
        if (item.subteamPlacement!=='0'){
           if (item.subteamPlacement === '1') {
            firstTeam.value.push(item);
           }else if (item.subteamPlacement === '2') {
             secondTeam.value.push(item);
           }else if (item.subteamPlacement === '3') {
             thirdTeam.value.push(item);
           }else if (item.subteamPlacement === '4') {
             fourthTeam.value.push(item);
           }else if (item.subteamPlacement === '5') {
              fifthTeam.value.push(item);
           } else if (item.subteamPlacement === '6') {
                sixthTeam.value.push(item);
            }else if (item.subteamPlacement === '7') {
                seventhTeam.value.push(item);
            } else if (item.subteamPlacement === '8') {
                eighthTeam.value.push(item);
            }
        }
        else{
          if (item.matchesListInfo.mapName==='无尽狂潮'){
             wujinkuangchao.value.push(item);
          }else{
            if (item.matchesListInfo.win) {
              OneMatchWin.value.push(item);
            } else {
              OneMatchFail.value.push(item);
            }
          }
        }
      });
      TotalFirstToEighthTeam.value.push(firstTeam.value,secondTeam.value,thirdTeam.value,fourthTeam.value,fifthTeam.value,sixthTeam.value,seventhTeam.value,eighthTeam.value);
      console.log(wujinkuangchao)
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp);
    }
  });
};
//
const imageInfoSrc = (base64Image) => {

  return `http://localhost:8089/images/${base64Image}`;
};

//判断单独显示斗魂还是正常显示
const  mapNameClick = ref()
const showPerks=(mapName)=>{
  if (mapName === "斗魂竞技场") {
    return "斗魂竞技场";
  }
  if (mapName === "无尽狂潮") {
    return "无尽狂潮";
  }
  return "普通模式";
}
//斗魂竞技场的数据单独做显示
const initDouhun = () => {
  firstTeam.value = [];
  secondTeam.value = [];
  thirdTeam.value = [];
  fourthTeam.value = [];
  fifthTeam.value = [];
  sixthTeam.value = [];
  seventhTeam.value = [];
  eighthTeam.value = [];
};
const firstTeam = ref([]);
const secondTeam = ref([]);
const thirdTeam = ref([]);
const fourthTeam = ref([]);
const fifthTeam = ref([]);
const sixthTeam = ref([]);
const seventhTeam = ref([]);
const eighthTeam = ref([]);
const TotalFirstToEighthTeam = ref([]);
const returnCloro=(index)=>{
   if (index<=3) {
     return 'background-color:rgb(221,242,245)';
   }else{
      return 'background-color:rgb(252,241,241)';
    }
}
const showHeader=(index)=>{
  return index===0;
}
const styleConfig=(index)=>{
  return index===0?'margin-top: 0px':'margin-top: 20px';
}

const returnMyColorFail= ({row}) => {
  if (row.gameName === name.value) {
    return 'background-color:rgb(248,224,224)'
  }else{
    return 'background-color:rgb(252,241,241)'
  }
}
const returnMyColorWin= ({row}) => {
  if (row.gameName === name.value) {
    return 'background-color:rgb(220,242,245)'
  }else{
    return 'background-color:rgb(243,250,251)'
  }
}
const returnTableColor=({row})=>{
  console.log(row.matchesListInfo.win)
  if (row.matchesListInfo.win===true){
    if (row.gameName === name.value) {
      return 'background-color:rgb(220,242,245)'
    }else{
      return 'background-color:rgb(243,250,251)'
    }
  }else{
    if (row.gameName === name.value) {
      return 'background-color:rgb(248,224,224)'
    }else{
      return 'background-color:rgb(252,241,241)'
    }
  }
}
</script>

<template>
  <div class="input-container">
    <el-input v-model="name" style="width: 150px" placeholder="请输入ID"></el-input>
    <el-button type="primary" @click="getInfo">获取该人战绩</el-button>
    <h4 style="margin-left: 50px">获取战绩可能有2-4秒延迟</h4>
  </div>
  <el-row style="margin-top: 10px">
    <el-col :span="6">
      <el-table @row-click="getOneMatch" :row-style="getBackgorundColor" :show-header="false" :data="MatchesListInfo"
                style="width: 100%">
        <el-table-column prop="championName" label="championName" width="110"/>
        <el-table-column prop="mapName" label="mapName" width="140"/>
        <el-table-column prop="date" label="date" width="160"/>
        <el-table-column
            prop="win"
            label="win"
            width="60"
            :formatter="winFormatter"
        />
      </el-table>
      <el-pagination
          @current-change="handleCurrentChange"
          layout="prev, pager, next"
          :total="560" />
    </el-col>
    <el-col :span="18">
      <div v-if="showPerks(mapNameClick)==='普通模式'">
         <el-table :data="OneMatchWin"
                   :row-style="returnMyColorWin"
                   width="100%"
         >
           <el-table-column label="游戏昵称" width="170">
             <template #default="scope">
               <el-link @click="handleGameNameClick(scope.row.gameName)">{{ scope.row.gameName }}</el-link>
             </template>
           </el-table-column>
           <el-table-column prop="championName" label="角色" width="160">
             <template v-slot="scope">
               <!--            通过读取本地-->
<!--               <img v-if='isTrue(scope.row.matchesListInfo.championName)' :src="scope.row.championImage" alt="" style="width: 35px; height: 35px">-->
               <img :src="imageInfoSrc(scope.row.championImage.image)" alt="" style="width: 35px; height: 35px">
               <!--            通过网络直接获取-->
               <!--            <img :src="scope.row.championImage" alt="" style="width: 35px; height: 35px">-->
               {{ scope.row.matchesListInfo.championName }}
             </template>
           </el-table-column>
           <el-table-column  label="天赋" width="240">
             <template  v-slot="scope">
               <el-popover
                   v-for="(item,index) in scope.row.perkImage"
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
           <el-table-column  label="召唤师技能" width="100">
             <template v-slot="scope">
               <el-popover
                   v-for="(item,index) in scope.row.spellsImage"
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
           <el-table-column  prop="champLevel" label="角色等级" width="80"/>
           <el-table-column  label="KDA" width="120">
             <template v-slot="scope">
                 <el-popover
                     placement="top-start"
                     :width="440"
                     trigger="hover"
                 >
                   <template #reference>
                     {{scope.row.kills}}-{{scope.row.deaths}}-{{scope.row.assists}}
                   </template >
                   <el-table :data="[scope.row]">
                     <el-table-column  label="造成伤害" width="110">
                       <template #default="{ row }">
                         {{ row.totalDamageDealtToChampions}}
                       </template>
                     </el-table-column>
                       <el-table-column  label="承受伤害" width="110">
                         <template #default="{ row }">
                           {{ row.totalDamageTaken}}
                         </template>
                       </el-table-column>
                     <el-table-column  label="补兵数" width="110">
                       <template #default="{ row }">
                         {{ row.totalMinionsKilled}}
                       </template>
                     </el-table-column>
                     <el-table-column  label="插眼" width="110">
                       <template #default="{ row }">
                         {{ row.wardsPlaced}}
                       </template>
                     </el-table-column>
                   </el-table>
                 </el-popover>
             </template>
           </el-table-column>
           <el-table-column prop="" label="装备" width="300">
             <template v-slot="scope">
               <el-popover
                   v-for="(item,index) in scope.row.itemsImage"
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
                   :width="720"
                   trigger="hover"
               >
                 <template #reference>
                   <img :src="imageInfoSrc(scope.row.rankImage)" style="width: 40px;height: 40px" alt="">
                 </template >

                 <el-table :data="[scope.row]">
                   <el-table-column   label="单双排" width="350">
                     <el-table-column  label="当前赛季最高段位" width="110">
                       <template #default="{ row }">
                         {{ row.rank.ranked_SOLO_5x5_CurrentSeason_HighestTier+row.rank.ranked_SOLO_5x5_CurrentSeason_HighestDivision}}
                       </template>
                     </el-table-column>
                     <el-table-column  label="当前段位" width="130">
                       <template #default="{ row }">
                         {{ row.rank.ranked_SOLO_5x5_CurrentSeason_Tier+row.rank.ranked_SOLO_5x5_CurrentSeason_Division+"  "+row.rank.ranked_SOLO_5x5_CurrentSeason_LeaguePoints}}
                       </template>
                     </el-table-column>
                     <el-table-column label="历史最高段位" width="110">
                       <template #default="{ row }">
                         {{ row.rank.ranked_SOLO_5x5_PreviousSeasonHighestTier}}
                       </template>
                     </el-table-column>
                   </el-table-column>
                   <el-table-column label="灵活组排" width="350">
                     <el-table-column  label="当前赛季最高段位" width="110">
                       <template #default="{ row }">
                         {{ row.rank.ranked_FLEX_SR_CurrentSeason_HighestTier+row.rank.ranked_FLEX_SR_CurrentSeason_HighestDivision}}
                       </template>
                     </el-table-column>
                     <el-table-column  label="当前段位" width="130">
                       <template #default="{ row }">
                         {{ row.rank.ranked_FLEX_SR_CurrentSeason_Tier+row.rank.ranked_FLEX_SR_CurrentSeason_Division+"  "+row.rank.ranked_FLEX_SR_CurrentSeason_LeaguePoints}}
                       </template>
                     </el-table-column>
                     <el-table-column label="历史最高段位" width="110">
                       <template #default="{ row }">
                         {{ row.rank.ranked_FLEX_SR_PreviousSeasonHighestTier}}
                       </template>
                     </el-table-column>
                   </el-table-column>
                 </el-table>
               </el-popover>
             </template>
           </el-table-column>
         </el-table>
         <el-table :data="OneMatchFail"
                   :row-style="returnMyColorFail"
                   width="100%"
         >
           <el-table-column label="游戏昵称" width="160">
             <template #default="scope">
               <el-link @click="handleGameNameClick(scope.row.gameName)">{{ scope.row.gameName }}</el-link>
             </template>
           </el-table-column>
           <el-table-column prop="championName" label="角色" width="160">
             <template v-slot="scope">
               <!--            通过读取本地-->
               <!--               <img v-if='isTrue(scope.row.matchesListInfo.championName)' :src="scope.row.championImage" alt="" style="width: 35px; height: 35px">-->
               <img :src="imageInfoSrc(scope.row.championImage.image)" alt="" style="width: 35px; height: 35px">
               <!--            通过网络直接获取-->
               <!--            <img :src="scope.row.championImage" alt="" style="width: 35px; height: 35px">-->
               {{ scope.row.matchesListInfo.championName }}
             </template>
           </el-table-column>
           <el-table-column  label="天赋" width="240">
             <template  v-slot="scope">
               <el-popover
                   v-for="(item,index) in scope.row.perkImage"
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
           <el-table-column  label="召唤师技能" width="100">
             <template v-slot="scope">
               <el-popover
                   v-for="(item,index) in scope.row.spellsImage"
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
           <el-table-column prop="champLevel" label="角色等级" width="80"/>
           <el-table-column  label="KDA" width="120">
             <template v-slot="scope">
               <el-popover
                   placement="top-start"
                   :width="440"
                   trigger="hover"
               >
                 <template #reference>
                   {{scope.row.kills}}-{{scope.row.deaths}}-{{scope.row.assists}}
                 </template >
                 <el-table :data="[scope.row]">
                   <el-table-column  label="造成伤害" width="110">
                     <template #default="{ row }">
                       {{ row.totalDamageDealtToChampions}}
                     </template>
                   </el-table-column>
                   <el-table-column  label="承受伤害" width="110">
                     <template #default="{ row }">
                       {{ row.totalDamageTaken}}
                     </template>
                   </el-table-column>
                   <el-table-column  label="补兵数" width="110">
                     <template #default="{ row }">
                       {{ row.totalMinionsKilled}}
                     </template>
                   </el-table-column>
                   <el-table-column  label="插眼" width="110">
                     <template #default="{ row }">
                       {{ row.wardsPlaced}}
                     </template>
                   </el-table-column>
                 </el-table>
               </el-popover>
             </template>
           </el-table-column>
           <el-table-column prop="" label="装备" width="300">
             <template v-slot="scope">
               <el-popover
                   v-for="(item,index) in scope.row.itemsImage"
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
                   :width="720"
                   trigger="hover"
               >
                 <template #reference>
                   <img :src="imageInfoSrc(scope.row.rankImage)" style="width: 40px;height: 40px" alt="">
                 </template >

                 <el-table :data="[scope.row]">
                   <el-table-column   label="单双排" width="350">
                     <el-table-column  label="当前赛季最高段位" width="110">
                       <template #default="{ row }">
                         {{ row.rank.ranked_SOLO_5x5_CurrentSeason_HighestTier+row.rank.ranked_SOLO_5x5_CurrentSeason_HighestDivision}}
                       </template>
                     </el-table-column>
                     <el-table-column  label="当前段位" width="130">
                       <template #default="{ row }">
                         {{ row.rank.ranked_SOLO_5x5_CurrentSeason_Tier+row.rank.ranked_SOLO_5x5_CurrentSeason_Division+"  "+row.rank.ranked_SOLO_5x5_CurrentSeason_LeaguePoints}}
                       </template>
                     </el-table-column>
                     <el-table-column label="历史最高段位" width="110">
                       <template #default="{ row }">
                         {{ row.rank.ranked_SOLO_5x5_PreviousSeasonHighestTier}}
                       </template>
                     </el-table-column>
                   </el-table-column>
                   <el-table-column label="灵活组排" width="350">
                     <el-table-column  label="当前赛季最高段位" width="110">
                       <template #default="{ row }">
                         {{ row.rank.ranked_FLEX_SR_CurrentSeason_HighestTier+row.rank.ranked_FLEX_SR_CurrentSeason_HighestDivision}}
                       </template>
                     </el-table-column>
                     <el-table-column  label="当前段位" width="130">
                       <template #default="{ row }">
                         {{ row.rank.ranked_FLEX_SR_CurrentSeason_Tier+row.rank.ranked_FLEX_SR_CurrentSeason_Division+"  "+row.rank.ranked_FLEX_SR_CurrentSeason_LeaguePoints}}
                       </template>
                     </el-table-column>
                     <el-table-column label="历史最高段位" width="110">
                       <template #default="{ row }">
                         {{ row.rank.ranked_FLEX_SR_PreviousSeasonHighestTier}}
                       </template>
                     </el-table-column>
                   </el-table-column>
                 </el-table>
               </el-popover>
             </template>
           </el-table-column>
         </el-table>
       </div>
      <div v-if="showPerks(mapNameClick)==='无尽狂潮'">
        <el-table :data="wujinkuangchao"
                  :row-style="returnTableColor"
                  width="100%"
        >
          <el-table-column label="游戏昵称" width="170">
            <template #default="scope">
              <el-link @click="handleGameNameClick(scope.row.gameName)">{{ scope.row.gameName }}</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="championName" label="角色" width="160">
            <template v-slot="scope">
              <!--            通过读取本地-->
<!--              <img v-if='isTrue(scope.row.matchesListInfo.championName)' :src="scope.row.championImage" alt="" style="width: 35px; height: 35px">-->
              <img  :src="imageInfoSrc(scope.row.championImage.image)" alt="" style="width: 35px; height: 35px">
              <!--            通过网络直接获取-->
              <!--            <img :src="scope.row.championImage" alt="" style="width: 35px; height: 35px">-->
              {{ scope.row.matchesListInfo.championName }}
            </template>
          </el-table-column>
          <el-table-column prop="" label="选择特性" width="300">
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
          <el-table-column  label="召唤师技能" width="100">
            <template v-slot="scope">
              <el-popover
                  v-for="(item,index) in scope.row.spellsImage"
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
          <el-table-column  prop="champLevel" label="角色等级" width="80"/>
          <el-table-column  label="KDA" width="120">
            <template v-slot="scope">
              <el-popover
                  placement="top-start"
                  :width="440"
                  trigger="hover"
              >
                <template #reference>
                  {{scope.row.kills}}-{{scope.row.deaths}}-{{scope.row.assists}}
                </template >
                <el-table :data="[scope.row]">
                  <el-table-column  label="造成伤害" width="110">
                    <template #default="{ row }">
                      {{ row.totalDamageDealtToChampions}}
                    </template>
                  </el-table-column>
                  <el-table-column  label="承受伤害" width="110">
                    <template #default="{ row }">
                      {{ row.totalDamageTaken}}
                    </template>
                  </el-table-column>
                  <el-table-column  label="补兵数" width="110">
                    <template #default="{ row }">
                      {{ row.totalMinionsKilled}}
                    </template>
                  </el-table-column>
                  <el-table-column  label="插眼" width="110">
                    <template #default="{ row }">
                      {{ row.wardsPlaced}}
                    </template>
                  </el-table-column>
                </el-table>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column prop="" label="装备" width="300">
            <template v-slot="scope">
              <el-popover
                  v-for="(item,index) in scope.row.itemsImage"
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
                  :width="720"
                  trigger="hover"
              >
                <template #reference>
                  <img :src="imageInfoSrc(scope.row.rankImage)" style="width: 40px;height: 40px" alt="">
                </template >

                <el-table :data="[scope.row]">
                  <el-table-column   label="单双排" width="350">
                    <el-table-column  label="当前赛季最高段位" width="110">
                      <template #default="{ row }">
                        {{ row.rank.ranked_SOLO_5x5_CurrentSeason_HighestTier+row.rank.ranked_SOLO_5x5_CurrentSeason_HighestDivision}}
                      </template>
                    </el-table-column>
                    <el-table-column  label="当前段位" width="130">
                      <template #default="{ row }">
                        {{ row.rank.ranked_SOLO_5x5_CurrentSeason_Tier+row.rank.ranked_SOLO_5x5_CurrentSeason_Division+"  "+row.rank.ranked_SOLO_5x5_CurrentSeason_LeaguePoints}}
                      </template>
                    </el-table-column>
                    <el-table-column label="历史最高段位" width="110">
                      <template #default="{ row }">
                        {{ row.rank.ranked_SOLO_5x5_PreviousSeasonHighestTier}}
                      </template>
                    </el-table-column>
                  </el-table-column>
                  <el-table-column label="灵活组排" width="350">
                    <el-table-column  label="当前赛季最高段位" width="110">
                      <template #default="{ row }">
                        {{ row.rank.ranked_FLEX_SR_CurrentSeason_HighestTier+row.rank.ranked_FLEX_SR_CurrentSeason_HighestDivision}}
                      </template>
                    </el-table-column>
                    <el-table-column  label="当前段位" width="130">
                      <template #default="{ row }">
                        {{ row.rank.ranked_FLEX_SR_CurrentSeason_Tier+row.rank.ranked_FLEX_SR_CurrentSeason_Division+"  "+row.rank.ranked_FLEX_SR_CurrentSeason_LeaguePoints}}
                      </template>
                    </el-table-column>
                    <el-table-column label="历史最高段位" width="110">
                      <template #default="{ row }">
                        {{ row.rank.ranked_FLEX_SR_PreviousSeasonHighestTier}}
                      </template>
                    </el-table-column>
                  </el-table-column>
                </el-table>
              </el-popover>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-if="showPerks(mapNameClick)==='斗魂竞技场'">
        <el-table :style="styleConfig(index)" :show-header="showHeader(index)" v-for="(teamItem, index) in TotalFirstToEighthTeam"   :row-style="returnCloro(index)" :key="index" :data="teamItem" >
            <el-table-column  label="名次" width="80">
              <template #default="scope">
               第{{scope.row.subteamPlacement}}名
              </template>
            </el-table-column>
            <el-table-column label="游戏昵称" width="160">
              <template #default="scope">
                <el-link @click="handleGameNameClick(scope.row.gameName)">{{ scope.row.gameName }}</el-link>
              </template>
            </el-table-column>
          <el-table-column prop="ratedRating" label="分数" width="70">
          </el-table-column>
            <el-table-column prop="championName" label="角色" width="160">
              <template v-slot="scope">
                <!--            通过读取本地-->
<!--                <img v-if='isTrue(scope.row.matchesListInfo.championName)' :src="scope.row.championImage" alt="" style="width: 35px; height: 35px">-->
                <img  :src="imageInfoSrc(scope.row.championImage.image)" alt="" style="width: 35px; height: 35px">
                <!--            通过网络直接获取-->
                <!--            <img :src="scope.row.championImage" alt="" style="width: 35px; height: 35px">-->
                {{ scope.row.matchesListInfo.championName }}
              </template>
            </el-table-column>
            <el-table-column  label="召唤师技能" width="100">
              <template v-slot="scope">
                <el-popover
                    v-for="(item,index) in scope.row.spellsImage"
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
          <el-table-column  prop="champLevel" label="角色等级" width="80"/>
          <el-table-column prop="" label="海克斯" width="200">
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
          <el-table-column  label="KDA" width="120">
            <template v-slot="scope">
              <el-popover
                  placement="top-start"
                  :width="440"
                  trigger="hover"
              >
                <template #reference>
                  {{scope.row.kills}}-{{scope.row.deaths}}-{{scope.row.assists}}
                </template >
                <el-table :data="[scope.row]">
                  <el-table-column  label="造成伤害" width="110">
                    <template #default="{ row }">
                      {{ row.totalDamageDealtToChampions}}
                    </template>
                  </el-table-column>
                  <el-table-column  label="承受伤害" width="110">
                    <template #default="{ row }">
                      {{ row.totalDamageTaken}}
                    </template>
                  </el-table-column>
                  <el-table-column  label="补兵数" width="110">
                    <template #default="{ row }">
                      {{ row.totalMinionsKilled}}
                    </template>
                  </el-table-column>
                  <el-table-column  label="插眼" width="110">
                    <template #default="{ row }">
                      {{ row.wardsPlaced}}
                    </template>
                  </el-table-column>
                </el-table>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column prop="" label="装备" width="300">
            <template v-slot="scope">
              <el-popover
                  v-for="(item,index) in scope.row.itemsImage"
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
                    :width="720"
                    trigger="hover"
                >
                  <template #reference>
                    <img :src="imageInfoSrc(scope.row.rankImage)" style="width: 40px;height: 40px" alt="">
                  </template >

                  <el-table :data="[scope.row]">
                    <el-table-column   label="单双排" width="350">
                      <el-table-column  label="当前赛季最高段位" width="110">
                        <template #default="{ row }">
                          {{ row.rank.ranked_SOLO_5x5_CurrentSeason_HighestTier+row.rank.ranked_SOLO_5x5_CurrentSeason_HighestDivision}}
                        </template>
                      </el-table-column>
                      <el-table-column  label="当前段位" width="130">
                        <template #default="{ row }">
                          {{ row.rank.ranked_SOLO_5x5_CurrentSeason_Tier+row.rank.ranked_SOLO_5x5_CurrentSeason_Division+"  "+row.rank.ranked_SOLO_5x5_CurrentSeason_LeaguePoints}}
                        </template>
                      </el-table-column>
                      <el-table-column label="历史最高段位" width="110">
                        <template #default="{ row }">
                          {{ row.rank.ranked_SOLO_5x5_PreviousSeasonHighestTier}}
                        </template>
                      </el-table-column>
                    </el-table-column>
                    <el-table-column label="灵活组排" width="350">
                      <el-table-column  label="当前赛季最高段位" width="110">
                        <template #default="{ row }">
                          {{ row.rank.ranked_FLEX_SR_CurrentSeason_HighestTier+row.rank.ranked_FLEX_SR_CurrentSeason_HighestDivision}}
                        </template>
                      </el-table-column>
                      <el-table-column  label="当前段位" width="130">
                        <template #default="{ row }">
                          {{ row.rank.ranked_FLEX_SR_CurrentSeason_Tier+row.rank.ranked_FLEX_SR_CurrentSeason_Division+"  "+row.rank.ranked_FLEX_SR_CurrentSeason_LeaguePoints}}
                        </template>
                      </el-table-column>
                      <el-table-column label="历史最高段位" width="110">
                        <template #default="{ row }">
                          {{ row.rank.ranked_FLEX_SR_PreviousSeasonHighestTier}}
                        </template>
                      </el-table-column>
                    </el-table-column>
                  </el-table>
                </el-popover>
              </template>
            </el-table-column>
        </el-table>
      </div>
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
