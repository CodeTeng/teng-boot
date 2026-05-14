<template>
  <el-container class="main-layout">
    <el-aside
      :width="appStore.sidebarCollapsed ? '64px' : '240px'"
      class="main-layout__aside"
    >
      <Sidebar />
    </el-aside>
    <el-container class="main-layout__right">
      <el-header class="main-layout__header" height="56px">
        <Navbar />
      </el-header>
      <el-main class="main-layout__content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/app'
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'

const appStore = useAppStore()
</script>

<style lang="scss" scoped>
.main-layout {
  height: 100vh;
  overflow: hidden;

  &__aside {
    background-color: var(--sidebar-bg);
    border-right: 1px solid var(--sidebar-border);
    transition: width 0.28s ease;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }

  &__right {
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }

  &__header {
    background: var(--header-bg);
    border-bottom: 1px solid var(--border-color);
    display: flex;
    align-items: center;
    padding: 0 20px;
    flex-shrink: 0;
  }

  &__content {
    background: var(--content-bg);
    padding: 24px;
    overflow-y: auto;
    flex: 1;
  }
}
</style>
