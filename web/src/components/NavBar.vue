<script setup>
import {reactive, ref} from 'vue'
import $ from 'jquery'
const activeIndex = ref('1')
const handleSelect = (key, keyPath) => {
  console.log(key, keyPath);
  if (key==='3'){
    centerDialogVisible.value=true
  }
};
const avatar=ref('')
const imageInfoSrc = (base64Image) => {
  return `http://localhost:8089/images/${base64Image}`;
};
let info=reactive()
const getAvatar=()=>{
   $.ajax({
    url: 'http://localhost:8089/getCurrentInfo',
    type: 'GET',
    success(data) {
       console.log(data)
       info=data
       avatar.value=imageInfoSrc(data.profileIconId)
      console.log(info)
    }}
   )
}
getAvatar()
const centerDialogVisible = ref(false)
const isPrivacy=(s)=>{
  if (s==="PRIVATE"){
    return "是"
  }else {
    return "否"
  }
}
</script>

<template>
  <el-menu
      :default-active="activeIndex"
      class="el-menu-demo"
      mode="horizontal"
      @select="handleSelect"
  >
    <router-link :to="{name:'ZhanJi'}">
    <el-menu-item index="1">
      战绩查询
    </el-menu-item>
  </router-link>
    <router-link :to="{name:'gongju'}">
    <el-menu-item index="2" >
       工具
    </el-menu-item>
    </router-link>
    <el-menu-item index="3" >
      <el-avatar :size="60" src="https://empty" @error="errorHandler">
        <img
           :src="avatar"
        />
      </el-avatar>
    </el-menu-item>
  </el-menu>

  <el-dialog v-model="centerDialogVisible" title="个人信息" width="500" center>
   <div style="text-align: center">
     <div style="margin-top: 10px">
     <span>
      现ID:{{info.gameName}}#{{info.tagLine}}
    </span>
     </div>
     <div style="margin-top: 10px">
     <span>
      原ID:{{info.internalName}}
    </span>
     </div>
     <div style="margin-top: 10px">
     <span>
      等级:{{info.summonerLevel}}
    </span>
     </div>
     <div style="margin-top: 10px">
       <span>
      是否隐私:{{isPrivacy(info.privacy)}}
    </span>
     </div>
   </div>
  </el-dialog>
</template>

<style scoped>

</style>