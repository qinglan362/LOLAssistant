<template>
  <nav>
    <div class="mb-4">
      <router-link :to="{name: 'ZhanJi'}" >
        <el-button>Default</el-button>
      </router-link>
      <el-button type="success">Success</el-button>
      <el-button type="info">Info</el-button>
      <el-button type="warning">Warning</el-button>
      <el-button type="danger">Danger</el-button>
    </div>
  </nav>
  <router-view/>
</template>

<style>

</style>
<script setup lang="ts">
</script>