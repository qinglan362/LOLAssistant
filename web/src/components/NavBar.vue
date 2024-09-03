<script setup>
import { ref} from 'vue'
import $ from 'jquery'
const activeIndex = ref('1')
const handleSelect = (key, keyPath) => {
  console.log(key, keyPath);
};
const avatar=ref('')
const imageInfoSrc = (base64Image) => {
  return `http://localhost:8089/images/${base64Image}`;
};
const getAvatar=()=>{
   $.ajax({
    url: 'http://localhost:8089/getCurrentInfo',
    type: 'GET',
    success(data) {
      console.log(data)
       avatar.value=imageInfoSrc(data.profileIconId)
    }}
   )
}
getAvatar()
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
</template>

<style scoped>

</style>