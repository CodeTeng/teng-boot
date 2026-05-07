<template>
  <div class="profile">
    <!-- 顶部导航 -->
    <header class="profile__header">
      <div class="profile__header-inner">
        <span class="profile__logo">Teng Boot</span>
        <n-button quaternary @click="router.push('/')">返回首页</n-button>
      </div>
    </header>

    <div class="profile__body">
      <div class="profile__card">
        <div class="profile__avatar-section">
          <n-avatar
            :size="72"
            color="#d97706"
            round
          >
            {{ displayName.charAt(0).toUpperCase() }}
          </n-avatar>
          <div>
            <h2 class="profile__name">{{ displayName }}</h2>
            <p class="profile__email">{{ userInfo?.userEmail || '未设置邮箱' }}</p>
          </div>
        </div>

        <n-divider />

        <div class="profile__info">
          <div class="profile__info-row">
            <span class="profile__info-label">用户名</span>
            <span class="profile__info-value">{{ userInfo?.username ?? '—' }}</span>
          </div>
          <div class="profile__info-row">
            <span class="profile__info-label">角色</span>
            <span class="profile__info-value">{{ roleText }}</span>
          </div>
          <div class="profile__info-row">
            <span class="profile__info-label">手机号</span>
            <span class="profile__info-value">{{ userInfo?.userPhone || '未设置' }}</span>
          </div>
          <div class="profile__info-row">
            <span class="profile__info-label">真实姓名</span>
            <span class="profile__info-value">{{ userInfo?.userRealName || '未设置' }}</span>
          </div>
          <div class="profile__info-row">
            <span class="profile__info-label">性别</span>
            <span class="profile__info-value">{{ genderText }}</span>
          </div>
          <div class="profile__info-row">
            <span class="profile__info-label">生日</span>
            <span class="profile__info-value">{{ userInfo?.userBirthday || '未设置' }}</span>
          </div>
          <div class="profile__info-row">
            <span class="profile__info-label">个人简介</span>
            <span class="profile__info-value profile__bio">{{ userInfo?.userProfile || '未填写' }}</span>
          </div>
        </div>

        <n-divider />

        <div class="profile__actions">
          <n-button @click="handleLogout" block>
            退出登录
          </n-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { getUserInfo, logout as authLogout } from '@teng-boot/shared/utils/auth'
import type { UserInfo } from '@teng-boot/shared/types'

const router = useRouter()
const message = useMessage()
const userInfo = ref<UserInfo | null>(null)

onMounted(() => {
  userInfo.value = getUserInfo()
  if (!userInfo.value) {
    message.warning('请先登录')
    router.push('/login')
  }
})

const displayName = computed(() => {
  return userInfo.value?.userRealName || userInfo.value?.username || '用户'
})

const roleText = computed(() => {
  const roles = userInfo.value?.roles
  if (!roles || roles.length === 0) return '—'
  return roles.join(', ')
})

const genderText = computed(() => {
  const gender = userInfo.value?.userGender
  if (gender === 1) return '男'
  if (gender === 2) return '女'
  return '未设置'
})

function handleLogout() {
  authLogout()
  message.success('已退出登录')
  router.push('/')
}
</script>

<style lang="scss" scoped>
.profile {
  min-height: 100vh;
  background: #fafaf9;

  &__header {
    background: #fff;
    border-bottom: 1px solid #e7e5e4;
  }

  &__header-inner {
    max-width: 600px;
    margin: 0 auto;
    padding: 0 24px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__logo {
    font-family: 'Fraunces', serif;
    font-size: 20px;
    font-weight: 600;
    font-variation-settings: 'SOFT' 50;
    color: #1c1917;
  }

  &__body {
    max-width: 600px;
    margin: 0 auto;
    padding: 40px 24px;
  }

  &__card {
    background: #fff;
    border: 1px solid #e7e5e4;
    border-radius: 20px;
    padding: 32px;
  }

  &__avatar-section {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  &__name {
    font-family: 'Fraunces', serif;
    font-size: 20px;
    font-weight: 500;
    font-variation-settings: 'SOFT' 50;
    color: #1c1917;
    margin: 0 0 4px;
  }

  &__email {
    font-size: 14px;
    color: #a8a29e;
    margin: 0;
  }

  &__info {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  &__info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__info-label {
    font-size: 14px;
    color: #a8a29e;
  }

  &__info-value {
    font-size: 14px;
    color: #1c1917;
    font-weight: 500;
  }

  &__bio {
    max-width: 200px;
    text-align: right;
    word-break: break-all;
  }

  &__actions {
    margin-top: 8px;
  }
}
</style>
