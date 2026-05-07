<template>
  <div class="sidebar">
    <!-- Logo -->
    <div class="sidebar__logo">
      <div class="sidebar__logo-icon">
        <el-icon :size="28"><Monitor /></el-icon>
      </div>
      <transition name="fade">
        <span v-show="!appStore.sidebarCollapsed" class="sidebar__logo-text">
          Teng Boot
        </span>
      </transition>
    </div>

    <!-- 动态菜单 -->
    <el-menu
      :default-active="route.path"
      :collapse="appStore.sidebarCollapsed"
      :collapse-transition="false"
      background-color="transparent"
      :router="true"
      class="sidebar__menu"
    >
      <SidebarMenuItem
        v-for="item in menuTree"
        :key="item.id"
        :item="item"
      />
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { getUserMenus } from '@teng-boot/shared/api/menu'
import type { MenuVO } from '@teng-boot/shared/types'
import { Monitor } from '@element-plus/icons-vue'
import SidebarMenuItem from './SidebarMenuItem.vue'

const route = useRoute()
const appStore = useAppStore()
const menuTree = ref<MenuVO[]>([])

/** 过滤根级菜单：只显示 menuType 为 M(目录) 或 C(菜单) 且 visible=1 status=1 */
function filterMenus(menus: MenuVO[]): MenuVO[] {
  return menus.filter(
    (item) =>
      (item.menuType === 'M' || item.menuType === 'C') &&
      item.visible === 1 &&
      item.status === 1
  )
}

onMounted(async () => {
  try {
    const res = await getUserMenus()
    if (res.code === 200 && res.data) {
      menuTree.value = filterMenus(res.data)
    }
  } catch {
    // 静默失败，菜单为空
  }
})
</script>

<style lang="scss" scoped>
.sidebar {
  display: flex;
  flex-direction: column;
  height: 100%;
  user-select: none;

  &__logo {
    height: 56px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    flex-shrink: 0;
    overflow: hidden;
  }

  &__logo-icon {
    color: var(--el-color-primary);
    display: flex;
    align-items: center;
    flex-shrink: 0;
  }

  &__logo-text {
    font-family: 'DM Serif Display', serif;
    font-size: 18px;
    color: #e2e8f0;
    white-space: nowrap;
  }

  &__menu {
    flex: 1;
    border-right: none;
    padding: 8px;
    overflow-y: auto;

    .el-menu-item,
    .el-sub-menu__title {
      border-radius: 8px;
      margin: 2px 0;
      color: #94a3b8;
      transition: all 0.2s ease;

      &:hover {
        background-color: rgba(255, 255, 255, 0.06);
        color: #e2e8f0;
      }
    }

    .el-menu-item {
      &.is-active {
        background-color: var(--el-color-primary);
        color: #fff;
      }
    }
  }
}
</style>
