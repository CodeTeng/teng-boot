<template>
  <div class="error-page">
    <div class="error-page__content">
      <h1 class="error-page__code">404</h1>
      <h2 class="error-page__title">页面未找到</h2>
      <p class="error-page__desc">您访问的页面不存在或已被移除</p>
      <el-button type="primary" @click="router.push('/dashboard')">
        返回控制台
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style lang="scss" scoped>
.error-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;

  &__content {
    text-align: center;
  }

  &__code {
    font-family: 'DM Serif Display', serif;
    font-size: 120px;
    color: var(--el-color-primary);
    margin: 0;
    line-height: 1;
    opacity: 0.25;
  }

  &__title {
    font-size: 24px;
    color: var(--text-primary);
    margin: 16px 0 8px;
  }

  &__desc {
    font-size: 14px;
    color: var(--text-secondary);
    margin: 0 0 24px;
  }
}
</style>
