<script setup>
import $ from "jquery";
import {ElMessage} from "element-plus";
import {ref} from "vue";
import store from "@/store";
const getChampions=()=>{
  $.ajax({
    url: "http://localhost:8089/getChampions",
    type: "Get",
    success(resp) {
      store.state.champions=resp
      console.log(store.state.champions);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
}
getChampions()
const create55xunlian = () => {
  $.ajax({
    url: "http://localhost:8089/createFiveXunLian",
    type: "Post",
    success(resp) {
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
};
const dialogTableVisible = ref(false)
const dialogTableVisible1 = ref(false)
const zdjsdj = () => {
    store.state.isAutoAccept=!store.state.isAutoAccept
      $.ajax({
        url: "http://localhost:8089/autoAccecptMatch",
        type: "Post",
        data: {
          isAutoAccecptMatch: store.state.isAutoAccept
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
const statusMessage = ref('');
const putStatusMessage = () => {
  $.ajax({
    url: "http://localhost:8089/updateStateMessage",
    type: "Post",
    data: {
      message: statusMessage.value
    },
    success(resp) {
      console.log(resp);
      if (resp==="success"){
        ElMessage.success("修改成功")
      }
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
}
const backdrop = ref('');

const getBackgrop = () => {
  $.ajax({
    url: "http://localhost:8089/getBackdrop",
    type: "Get",
    data: {
      name: backdrop.value
    },
    success(resp) {
      window.open(resp);
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
};
const getPickName = ()=>{
   if (store.state.championId === -1) {
     return "设置秒选的英雄";
   }else {
      return "更改英雄，当前:"+store.state.champions.find(item => item.key === store.state.championId).name;
   }
}
const getPickName1 = ()=>{
  if (store.state.banChampionId === -1) {
    return "设置秒BAN的英雄";
  }else {
    return "更改英雄，当前:"+store.state.champions.find(item => item.key === store.state.banChampionId).name;
  }
}
const pickName=ref(getPickName())
const pickName1=ref(getPickName1())
const choose=()=>{
  dialogTableVisible.value=false
  pickName.value=getPickName()
  $.ajax({
    url: "http://localhost:8089/setPickChampion",
    type: "Post",
    data: {
      championId: store.state.championId,
      state:"start"
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
const ban=()=>{
  dialogTableVisible1.value=false
  pickName1.value=getPickName1()
  $.ajax({
    url: "http://localhost:8089/setBanChampion",
    type: "Post",
    data: {
      championId: store.state.banChampionId,
      state:"start"
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
const unchoose=()=>{
  store.state.championId=-1
  pickName.value=getPickName()
  $.ajax({
    url: "http://localhost:8089/setPickChampion",
    type: "Post",
    data: {
      championId: -1,
      state:"stop"
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
const unBan=()=>{
  store.state.banChampionId=-1
  pickName1.value=getPickName1()
  $.ajax({
    url: "http://localhost:8089/setBanChampion",
    type: "Post",
    data: {
      championId: -1,
      state:"stop"
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
//----------------------------
const championOrSkin=ref('')
const chooseDistantValue=ref(0)
const chooseCount=ref(0)
// 添加这个方法来处理图片的选择
const toggleImageSelection = (item) => {
  const index = chooseImage.value.findIndex(img => img.storeItemId === item.storeItemId);
  if (index === -1) {
    chooseImage.value.push(item);
    chooseDistantValue.value+=parseInt(item.disenchantValue)*parseInt(item.count)
  } else {
    chooseImage.value.splice(index, 1);
    chooseDistantValue.value-=parseInt(item.disenchantValue)*parseInt(item.count)
  }
  chooseCount.value=chooseImage.value.length
  console.log(chooseCount.value)
};

// 添加这个方法来检查图片是否被选中
const isImageSelected = (item) => {
  return chooseImage.value.some(img => img.storeItemId === item.storeItemId);
};
const dialogTableVisibleRecipes=ref(false)
const images=ref([])
const updateChampionOrSkin=(message)=>{
      championOrSkin.value=message
       distant(message)
}
const distant = (message) => {
  dialogTableVisibleRecipes.value=true
    $.ajax({
      url: "http://localhost:8089/getLootMap",
      type: "GET",
      data:{
        message:message
      },
      success(resp) {
        images.value = resp;
      },
      error(resp) {
        console.log(resp);
        ElMessage.error(resp.responseJSON?.msg || '请求失败');
      }
    });
};
const chooseAll=()=>{
  chooseImage.value=[]
  chooseCount.value=0
  chooseDistantValue.value=0
  chooseImage.value=images.value
  chooseCount.value=images.value.length
  let sum = 0;
  images.value.forEach(image => {
    sum += parseInt(image.disenchantValue) || 0;
  });
  chooseDistantValue.value = sum;
}
const concealChooseAll=()=>{
  chooseImage.value=[]
  chooseCount.value=0
  chooseDistantValue.value=0
}
const chooseImage=ref([])
const postDistant=()=>{
  dialogTableVisibleRecipes.value=false
  $.ajax({
    url: "http://localhost:8089/distant",
    type: "Post",
    contentType: "application/json",  // 设置内容类型为 JSON
    data: JSON.stringify(chooseImage.value),  // 将数组转换为 JSON 字符串
    success(resp) {
      console.log(resp);
      ElMessage.success("分解成功")
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
  chooseImage.value=[]
  chooseCount.value=0
  chooseDistantValue.value=0
}
//-----------
const onlineState=ref([
  {label: '在线', value: 'chat'},
  {label: '离开', value: 'away'},
  {label: '游戏中', value: 'dnd'},
  {label: '离线', value: 'offline'},
  {label: '手机在线', value: 'mobile'}
])
const stateMessage=ref('在线')
const updateOnlineState=()=>{
  console.log(stateMessage.value)
  $.ajax({
    url: "http://localhost:8089/updateOnlineState",
    type: "Post",
    data: {
      state: stateMessage.value
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
//---------------
const autoNextGame=()=>{
  store.state.autoNext=!store.state.autoNext
  $.ajax({
    url: "http://localhost:8089/setAutoContinueNextGame",
    type: "Post",
    data: {
      autoContinueNext: store.state.autoNext
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
const imageInfoSrc = (base64Image) => {
  return `data:image/jpeg;base64,${base64Image}`;
};
//
const checkName=ref('')
const  CheckName=()=>{
  $.ajax({
    url: "http://localhost:8089/checkNameAvailability",
    type: "Get",
    data: {
      name: checkName.value
    },
    success(resp) {
      if (resp==='true'){
        ElMessage.success('可用')
      }
      else {
        ElMessage.error("不可用")
      }
    },
    error(resp) {
      console.log(resp);
      ElMessage.error(resp.msg);
    }
  });
}
</script>

<template>


  <el-row :gutter="20">
    <el-col :span="6">
      <div style="width: 100%">
        <el-button type="primary" @click="create55xunlian">创建55训练模式</el-button>
      </div>
      <div  style="margin-top: 20px">
        <el-button   v-if="!store.state.isAutoAccept" type="success" @click="zdjsdj">开启自动接受对局</el-button>
        <el-button v-else type="danger" @click="zdjsdj">取消自动接受对局</el-button>
      </div>
      <div  style="margin-top: 20px">
        <el-button type="primary" @click="updateChampionOrSkin('champion')">批量分解英雄碎片</el-button>
      </div>
      <div  style="margin-top: 20px">
        <el-button type="primary" @click="updateChampionOrSkin('skin')">批量分解皮肤碎片</el-button>
      </div>
      <div  style="margin-top: 20px">
        <el-button type="primary" @click="updateChampionOrSkin('eternals')">批量分解永恒星碑</el-button>
      </div>
      <div  style="margin-top: 20px">
        <el-button type="primary" @click="updateChampionOrSkin('wardskin')">批量分解守卫</el-button>
      </div>
      <div  style="margin-top: 20px">
        <el-button type="primary" @click="updateChampionOrSkin('summonerricon')">批量分解图标</el-button>
      </div>
      <div  style="margin-top: 20px">
        <el-button type="primary" @click="updateChampionOrSkin('emote')">批量分解表情</el-button>
      </div>
      <div  style="margin-top: 20px">
        <el-button type="primary" @click="updateChampionOrSkin('companion')">批量分解小小英雄</el-button>
      </div>
    </el-col>
    <el-col :span="6">
      <div>
        <el-input style="width: 160px" v-model="backdrop" placeholder="格式为:xxxx#xxxx"></el-input>
        <el-button type="primary" @click="getBackgrop">下载某人的生涯背景</el-button>
      </div>
      <div style="margin-top: 20px">
        <el-button type="primary" @click="dialogTableVisible = true">{{pickName}}</el-button>
        <el-button v-if="store.state.championId!==-1" @click="unchoose" type="danger" round>取消秒选英雄</el-button>
        <el-dialog v-model="dialogTableVisible" title="秒选英雄" width="300">
          <el-select
              v-model="store.state.championId"
              filterable
              placeholder="Select"
              style="width: 240px"
          >
            <el-option
                v-for="item in store.state.champions"
                :key="item.key"
                :label="item.name"
                :value="item.key"
            />
          </el-select>
          <el-button @click="choose" type="primary" round>确定</el-button>
        </el-dialog>
      </div>
      <div style="margin-top: 20px">
        <el-input style="width: 160px" v-model="checkName" placeholder="格式为:xxxx#xxxx"></el-input>
        <el-button type="primary" @click="CheckName">检测id是否重复可用</el-button>
      </div>
    </el-col>
    <el-col :span="6">
      <div>
        <el-input style="width: 200px" v-model="statusMessage" placeholder="修改状态签名"></el-input>
        <el-button type="primary" @click="putStatusMessage">确定修改</el-button>
      </div>
      <div style="margin-top: 20px">
        <el-button type="primary" @click=" dialogTableVisible1 = true">{{pickName1}}</el-button>
        <el-button v-if="store.state.banChampionId!==-1" @click="unBan" type="danger" round>取消秒Ban英雄</el-button>
        <el-dialog v-model="dialogTableVisible1" title="秒Ban英雄" width="300">
          <el-select
              v-model="store.state.banChampionId"
              filterable
              placeholder="在线状态"
              style="width: 240px"
          >
            <el-option
                v-for="item in store.state.champions"
                :key="item.key"
                :label="item.name"
                :value="item.key"
            />
          </el-select>
          <el-button @click="ban" type="primary" round>确定</el-button>
        </el-dialog>
      </div>

    </el-col>
    <el-col :span="6">
      <div>
        <el-select
            v-model="stateMessage"
            filterable
            placeholder="Select"
            style="width: 130px"
        >
          <el-option
              v-for="item in onlineState"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
        <el-button type="primary" @click="updateOnlineState">修改在线状态</el-button>
      </div>
      <div style="margin-top: 20px">
        <el-button v-if="store.state.autoNext===false" type="primary" @click="autoNextGame">自动开启下一局</el-button>
        <el-button v-else type="danger" @click="autoNextGame">取消自动开启下一局</el-button>
      </div>
    </el-col>
  </el-row>
<!--  分解战利品对话框-->
  <el-dialog v-model="dialogTableVisibleRecipes" title="分解战利品" width="1500">
    <el-row>
      <el-col
          v-for="(item) in images"
          :key="item.storeItemId"
          :span="4"
      >
        <el-row>
          <el-col :span="12">
            <img
                :src="imageInfoSrc(item.base64Image)"
                :class="{ 'selected-image': isImageSelected(item) }"
                style="width: 100%; height: 100px; cursor: pointer;"
                alt=""
                @click="toggleImageSelection(item)"
            >
          </el-col>
          <el-col :span="12">
            <div style="margin-left: 15px">
              <div style="width: 100%">
                {{item.championName}}
              </div>
              <div style="width: 100%">
                价值:{{item.value}}
              </div>
              <div style="width: 100%">
                数量:{{item.count}}
              </div>
              <div style="width: 100%">
                分解:{{item.disenchantValue}}
              </div>
            </div>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <div>
      已选择:{{chooseCount}}个,分解约得到:{{chooseDistantValue}}
      <el-button type="primary" @click="chooseAll">全选</el-button>
      <el-button type="primary" @click="concealChooseAll">取消全选</el-button>
      <el-button type="primary" @click="postDistant">分解</el-button>
    </div>

  </el-dialog>
<!--  -->
</template>

<style scoped>
.selected-image {
  border: 3px solid #409EFF;
  box-shadow: 0 0 10px #409EFF;
}
</style>

