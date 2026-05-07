<template>
  <div class="navbar">
    <!-- 左侧：折叠按钮 -->
    <div class="navbar__left">
      <el-icon
        :size="20"
        class="navbar__collapse-btn"
        @click="appStore.toggleSidebar"
      >
        <Fold v-if="!appStore.sidebarCollapsed" />
        <Expand v-else />
      </el-icon>
      <el-breadcrumb class="navbar__breadcrumb">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">
          首页
        </el-breadcrumb-item>
        <el-breadcrumb-item>{{ route.meta?.title }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 右侧：用户信息 -->
    <div class="navbar__right">
      <el-tooltip content="全屏" placement="bottom">
        <el-icon :size="18" class="navbar__action-icon">
          <FullScreen />
        </el-icon>
      </el-tooltip>

      <el-dropdown trigger="click" @command="handleCommand">
        <span class="navbar__user">
          <el-avatar :size="32" class="navbar__avatar">
            {{ userInitial }}
          </el-avatar>
          <span class="navbar__username">{{ username }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const username = computed(() => userStore.userInfo?.username ?? 'Admin')
const userInitial = computed(() => username.value.charAt(0).toUpperCase())

function handleCommand(command: string) {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;

  &__left {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  &__collapse-btn {
    cursor: pointer;
    color: var(--text-secondary);
    transition: color 0.2s;

    &:hover {
      color: var(--el-color-primary);
    }
  }

  &__breadcrumb {
    :deep(.el-breadcrumb__inner) {
      font-weight: 400;
    }
  }

  &__right {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  &__action-icon {
    cursor: pointer;
    color: var(--text-secondary);
    transition: color 0.2s;

    &:hover {
      color: var(--el-color-primary);
    }
  }

  &__user {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 8px;
    transition: background-color 0.2s;

    &:hover {
      background-color: var(--hover-bg);
    }
  }

  &__avatar {
    background-color: var(--el-color-primary);
    color: #fff;
    font-size: 14px;
    font-weight: 600;
  }

  &__username {
    font-size: 14px;
    color: var(--text-primary);
    font-weight: 500;
  }
}
</style>
