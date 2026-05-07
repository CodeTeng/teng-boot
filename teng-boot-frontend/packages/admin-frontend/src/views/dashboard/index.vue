<template>
  <div class="dashboard">
    <div class="dashboard__header">
      <h2 class="dashboard__title">控制台</h2>
      <p class="dashboard__desc">欢迎回来，这是您的管理概览</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="dashboard__stats">
      <el-col v-for="stat in stats" :key="stat.label" :xs="24" :sm="12" :lg="6">
        <el-card
          shadow="never"
          class="dashboard__stat-card"
          @click="handleCardClick(stat)"
          style="cursor: pointer"
        >
          <div class="dashboard__stat-card-inner">
            <div class="dashboard__stat-icon" :style="{ background: stat.bg }">
              <el-icon :size="24" :color="stat.color">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="dashboard__stat-info">
              <span v-if="!stat.loading" class="dashboard__stat-value">{{ stat.value }}</span>
              <el-skeleton v-else variant="text" :rows="1" style="width: 80px" />
              <span class="dashboard__stat-label">{{ stat.label }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-row :gutter="20">
      <el-col :xs="24" :lg="16">
        <el-card shadow="never" class="dashboard__section-card">
          <template #header>
            <span class="dashboard__section-title">快捷入口</span>
          </template>
          <div class="dashboard__quick-links">
            <div
              v-for="link in quickLinks"
              :key="link.label"
              class="dashboard__quick-link"
              @click="router.push(link.path)"
            >
              <div class="dashboard__quick-link-icon" :style="{ background: link.bg }">
                <el-icon :size="22" :color="link.color">
                  <component :is="link.icon" />
                </el-icon>
              </div>
              <span class="dashboard__quick-link-label">{{ link.label }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card shadow="never" class="dashboard__section-card">
          <template #header>
            <span class="dashboard__section-title">系统信息</span>
          </template>
          <div class="dashboard__info-list">
            <div class="dashboard__info-item">
              <span class="dashboard__info-label">框架版本</span>
              <span class="dashboard__info-value">Spring Boot 3.x</span>
            </div>
            <div class="dashboard__info-item">
              <span class="dashboard__info-label">前端框架</span>
              <span class="dashboard__info-value">Vue 3 + Element Plus</span>
            </div>
            <div class="dashboard__info-item">
              <span class="dashboard__info-label">运行环境</span>
              <span class="dashboard__info-value">{{ environment }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, UserFilled, Menu as MenuIcon, Folder } from '@element-plus/icons-vue'
import {
  listUserByPage,
  listFileByPage,
} from '@teng-boot/shared'
import { listAllRoles } from '@teng-boot/shared/api/role'
import { getMenuTree } from '@teng-boot/shared/api/menu'

const router = useRouter()
const environment = ref('加载中...')

interface StatItem {
  label: string
  value: string
  icon: unknown
  color: string
  bg: string
  loading: boolean
  path: string
}

const stats = reactive<StatItem[]>([
  { label: '用户总数', value: '0', icon: User, color: '#3b82f6', bg: '#eff6ff', loading: true, path: '/user' },
  { label: '角色总数', value: '0', icon: UserFilled, color: '#10b981', bg: '#ecfdf5', loading: true, path: '/role' },
  { label: '菜单总数', value: '0', icon: MenuIcon, color: '#8b5cf6', bg: '#f5f3ff', loading: true, path: '/menu' },
  { label: '文件总数', value: '0', icon: Folder, color: '#f59e0b', bg: '#fffbeb', loading: true, path: '/file' },
])

const quickLinks = [
  { label: '用户管理', icon: User, color: '#3b82f6', bg: '#eff6ff', path: '/user' },
  { label: '角色管理', icon: UserFilled, color: '#10b981', bg: '#ecfdf5', path: '/role' },
  { label: '菜单管理', icon: MenuIcon, color: '#8b5cf6', bg: '#f5f3ff', path: '/menu' },
  { label: '文件管理', icon: Folder, color: '#f59e0b', bg: '#fffbeb', path: '/file' },
  { label: '操作日志', icon: MenuIcon, color: '#ef4444', bg: '#fef2f2', path: '/log' },
]

/** 递归统计菜单总数 */
function countMenus(items: unknown[]): number {
  let count = 0
  for (const item of items as { children?: unknown[] }[]) {
    count++
    if (item.children && item.children.length > 0) {
      count += countMenus(item.children)
    }
  }
  return count
}

function handleCardClick(stat: StatItem) {
  router.push(stat.path)
}

onMounted(async () => {
  environment.value = navigator.userAgent.includes('Windows') ? 'Windows' : 'Linux'

  try {
    // 并行加载统计数据
    const [userRes, roleRes, menuRes, fileRes] = await Promise.allSettled([
      listUserByPage({ pageNo: 1, pageSize: 1 }),
      listAllRoles(),
      getMenuTree(),
      listFileByPage({ pageNo: 1, pageSize: 1 }),
    ])

    if (userRes.status === 'fulfilled' && userRes.value.code === 200) {
      stats[0].value = userRes.value.data.total.toLocaleString()
      stats[0].loading = false
    }

    if (roleRes.status === 'fulfilled' && roleRes.value.code === 200) {
      stats[1].value = roleRes.value.data.length.toLocaleString()
      stats[1].loading = false
    }

    if (menuRes.status === 'fulfilled' && menuRes.value.code === 200) {
      stats[2].value = countMenus(menuRes.value.data).toLocaleString()
      stats[2].loading = false
    }

    if (fileRes.status === 'fulfilled' && fileRes.value.code === 200) {
      stats[3].value = fileRes.value.data.total.toLocaleString()
      stats[3].loading = false
    }
  } catch {
    // 个别 API 失败不影响整体
  }
})
</script>

<style lang="scss" scoped>
.dashboard {
  &__header {
    margin-bottom: 24px;
  }

  &__title {
    font-family: 'DM Serif Display', serif;
    font-size: 24px;
    font-weight: 400;
    color: var(--text-primary);
    margin: 0 0 6px;
  }

  &__desc {
    font-size: 14px;
    color: var(--text-secondary);
    margin: 0;
  }

  &__stats {
    margin-bottom: 20px;
  }

  &__stat-card {
    border-radius: 12px;
    margin-bottom: 20px;
    transition: transform 0.2s ease, box-shadow 0.2s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }

    :deep(.el-card__body) {
      padding: 20px;
    }
  }

  &__stat-card-inner {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  &__stat-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  &__stat-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__stat-value {
    font-size: 22px;
    font-weight: 700;
    color: var(--text-primary);
    line-height: 1.2;
  }

  &__stat-label {
    font-size: 13px;
    color: var(--text-secondary);
  }

  &__section-card {
    border-radius: 12px;
    margin-bottom: 20px;
  }

  &__section-title {
    font-weight: 600;
    font-size: 15px;
    color: var(--text-primary);
  }

  // 快捷入口
  &__quick-links {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
  }

  &__quick-link {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 16px 8px;
    border-radius: 10px;
    cursor: pointer;
    transition: background-color 0.2s ease;

    &:hover {
      background-color: var(--hover-bg, rgba(0, 0, 0, 0.03));
    }
  }

  &__quick-link-icon {
    width: 44px;
    height: 44px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__quick-link-label {
    font-size: 13px;
    color: var(--text-primary);
    font-weight: 500;
  }

  // 系统信息
  &__info-list {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  &__info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 10px;
    border-bottom: 1px solid var(--border-color, #f0f0f0);

    &:last-child {
      border-bottom: none;
      padding-bottom: 0;
    }
  }

  &__info-label {
    font-size: 13px;
    color: var(--text-secondary);
  }

  &__info-value {
    font-size: 13px;
    color: var(--text-primary);
    font-weight: 500;
  }
}
</style>
