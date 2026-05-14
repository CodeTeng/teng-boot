<template>
  <div class="sidebar">
    <!-- Logo -->
    <div class="sidebar__logo" :class="{ 'sidebar__logo--collapsed': appStore.sidebarCollapsed }">
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
  background-color: var(--sidebar-bg);
  user-select: none;

  &__logo {
    height: 56px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    padding: 0 20px;
    gap: 10px;
    border-bottom: 1px solid var(--sidebar-border);
    flex-shrink: 0;
    overflow: hidden;

    &--collapsed {
      justify-content: center;
      padding: 0;
    }
  }

  &__logo-icon {
    color: var(--el-color-primary);
    display: flex;
    align-items: center;
    flex-shrink: 0;
  }

  &__logo-text {
    font-family: 'DM Serif Display', 'PingFang SC', 'Microsoft YaHei', sans-serif;
    font-size: 18px;
    color: var(--text-primary);
    white-space: nowrap;
    letter-spacing: -0.3px;
  }

  &__menu {
    flex: 1;
    border-right: none !important;
    padding: 12px 8px;
    overflow-y: auto;

    // 菜单项 & 子菜单标题共同样式
    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      height: 44px;
      line-height: 44px;
      border-radius: 6px;
      margin-bottom: 4px;
      padding: 0 12px !important;
      color: var(--sidebar-text-secondary);
      font-size: 14px;
      transition: all 0.2s ease;

      &:hover {
        background-color: var(--sidebar-hover-bg) !important;
        color: var(--sidebar-active-text) !important;
      }
    }

    // 菜单项选中状态
    :deep(.el-menu-item.is-active) {
      background-color: var(--sidebar-active-bg) !important;
      color: var(--sidebar-active-text) !important;
      font-weight: 500;
      position: relative;

      // 左侧指示器
      &::before {
        content: '';
        position: absolute;
        left: -8px;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 20px;
        background-color: var(--el-color-primary);
        border-radius: 0 2px 2px 0;
      }
    }

    // 子菜单展开时父级标题高亮
    :deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
      color: var(--sidebar-active-text) !important;
    }

    // ========== 折叠状态 ==========
    &.el-menu--collapse {
      padding: 12px 8px;

      :deep(.el-menu-item),
      :deep(.el-sub-menu__title) {
        justify-content: center;
        padding: 0 !important;

        // 隐藏文字
        > span {
          display: none;
        }

        // 图标居中，去掉右侧 margin
        > .el-icon,
        > .el-sub-menu__icon-arrow {
          margin-right: 0 !important;
          margin-left: 0 !important;
        }
      }

      // 折叠时隐藏左侧指示器和圆角
      :deep(.el-menu-item.is-active) {
        border-radius: 6px;

        &::before {
          display: none;
        }
      }
    }
  }
}
</style>
