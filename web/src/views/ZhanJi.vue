<script setup>

</script>

<template>
  <router-link :to="{name:'someone'}">
    <el-button  type="primary" >
      获取某非云顶战绩
    </el-button>
  </router-link>

  <router-link  style="margin-left: 100px" :to="{name:'someoneTft'}">
    <el-button  type="primary" >
      获取某人云顶战绩
    </el-button>
  </router-link>

  <router-link :to="{name:'current'}">
    <el-button  type="primary" style="margin-left: 100px" >
      获取当前队友战绩
    </el-button>
  </router-link>

</template>

<style scoped>

</style>
