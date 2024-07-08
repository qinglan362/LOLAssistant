<script setup>
import { ref } from 'vue'
const activeIndex = ref('1')
const handleSelect = (key, keyPath) => {
  console.log(key, keyPath);
};

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
  </el-menu>
</template>

<style scoped>

</style>