<template>
  <div class="home">
    <!-- ===== 导航栏 ===== -->
    <header class="home__header" :class="{ 'home__header--scrolled': scrolled }">
      <div class="home__header-inner">
        <div class="home__logo">
          <span class="home__logo-mark">Teng Boot</span>
        </div>
        <nav class="home__nav">
          <a href="#features" class="home__nav-link">特性</a>
          <a href="#about" class="home__nav-link">关于</a>
          <!-- 根据登录状态显示不同内容 -->
          <template v-if="isLoggedIn">
            <span class="home__user-greeting">{{ userInfo?.username }}</span>
            <n-button quaternary @click="router.push('/profile')">个人中心</n-button>
            <n-button type="primary" @click="handleLogout">退出登录</n-button>
          </template>
          <template v-else>
            <n-button quaternary @click="router.push('/login')">登录</n-button>
            <n-button type="primary" @click="router.push('/login')">开始使用</n-button>
          </template>
        </nav>
      </div>
    </header>

    <!-- ===== Hero 区域 ===== -->
    <section class="home__hero">
      <div class="home__hero-bg" />
      <div class="home__hero-content">
        <span class="home__hero-badge">v0.1 — 抢先体验</span>
        <h1 class="home__hero-title">
          构建你的下一个
          <span class="home__hero-highlight">卓越项目</span>
        </h1>
        <p class="home__hero-desc">
          Teng Boot 是一个现代化的全栈开发框架，融合前沿技术栈与优雅的开发体验，
          帮助团队快速构建企业级应用。
        </p>
        <div class="home__hero-actions">
          <template v-if="isLoggedIn">
            <n-button type="primary" size="large" @click="router.push('/profile')">
              个人中心
              <template #icon><n-icon><ArrowForward /></n-icon></template>
            </n-button>
          </template>
          <template v-else>
            <n-button type="primary" size="large" @click="router.push('/profile')">
              开始使用
              <template #icon><n-icon><ArrowForward /></n-icon></template>
            </n-button>
            <n-button quaternary size="large" @click="router.push('/login')">
              登录账户
            </n-button>
          </template>
        </div>
        <div class="home__hero-stats">
          <div class="home__stat-item">
            <span class="home__stat-num">Vue 3.5</span>
            <span class="home__stat-label">前端框架</span>
          </div>
          <div class="home__stat-item">
            <span class="home__stat-num">TypeScript</span>
            <span class="home__stat-label">类型安全</span>
          </div>
          <div class="home__stat-item">
            <span class="home__stat-num">Naive UI</span>
            <span class="home__stat-label">组件库</span>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== 特性展示 ===== -->
    <section id="features" class="home__features">
      <div class="home__features-inner">
        <div class="home__section-header">
          <h2 class="home__section-title">核心特性</h2>
          <p class="home__section-desc">为现代 Web 开发精心打造的完整工具链</p>
        </div>

        <div class="home__features-grid">
          <div
            v-for="(feature, index) in features"
            :key="index"
            class="home__feature-card"
            :style="{ animationDelay: `${index * 0.1}s` }"
          >
            <div class="home__feature-icon" :style="{ background: feature.bg }">
              <n-icon :size="28" :color="feature.color">
                <component :is="feature.icon" />
              </n-icon>
            </div>
            <h3 class="home__feature-title">{{ feature.title }}</h3>
            <p class="home__feature-desc">{{ feature.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== Footer ===== -->
    <footer class="home__footer">
      <div class="home__footer-inner">
        <p>&copy; 2026 Teng Boot. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { ArrowForward, Code, Speedometer, Shield, Layers, ColorPalette, Bulb } from '@vicons/ionicons5'
import { getUserInfo, logout as authLogout } from '@teng-boot/shared/utils/auth'
import type { UserInfo } from '@teng-boot/shared/types'

const router = useRouter()
const message = useMessage()
const scrolled = ref(false)
const userInfo = ref<UserInfo | null>(null)

const isLoggedIn = computed(() => !!userInfo.value)

function handleScroll() {
  scrolled.value = window.scrollY > 40
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  // 检查登录状态
  userInfo.value = getUserInfo()
})

onUnmounted(() => window.removeEventListener('scroll', handleScroll))

function handleLogout() {
  authLogout()
  userInfo.value = null
  message.success('已退出登录')
  router.push('/')
}

const features = [
  {
    title: 'Vue 3 组合式 API',
    desc: '采用 Vue 3 Composition API 构建，逻辑复用更灵活，代码组织更清晰。',
    icon: Code,
    color: '#d97706',
    bg: '#fffbeb',
  },
  {
    title: '高性能架构',
    desc: '基于 Vite 6 构建，极速的热更新和编译速度，开发体验流畅。',
    icon: Speedometer,
    color: '#059669',
    bg: '#ecfdf5',
  },
  {
    title: '完善的安全体系',
    desc: '内置路由守卫、权限校验、滑块验证码，全方位保护应用安全。',
    icon: Shield,
    color: '#3b82f6',
    bg: '#eff6ff',
  },
  {
    title: '模块化设计',
    desc: 'Monorepo 架构，共享类型与工具包，多应用间代码复用最大化。',
    icon: Layers,
    color: '#8b5cf6',
    bg: '#f5f3ff',
  },
  {
    title: '精美 UI 组件',
    desc: '基于 Naive UI 构建，丰富的组件库和可定制的主题系统。',
    icon: ColorPalette,
    color: '#ec4899',
    bg: '#fdf2f8',
  },
  {
    title: '持续进化',
    desc: '采用前沿技术栈，TypeScript 5.7 + Pinia + Vue Router，持续跟进生态演进。',
    icon: Bulb,
    color: '#f97316',
    bg: '#fff7ed',
  },
]
</script>

<style lang="scss" scoped>
.home {
  overflow-x: hidden;

  /* ===== 导航栏 ===== */
  &__header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
    transition: all 0.3s ease;

    &--scrolled {
      background: rgba(255, 255, 255, 0.85);
      backdrop-filter: blur(16px);
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
    }
  }

  &__header-inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__logo-mark {
    font-family: 'Fraunces', serif;
    font-size: 22px;
    font-weight: 600;
    font-variation-settings: 'SOFT' 50;
    color: #1c1917;
    letter-spacing: -0.02em;
  }

  &__nav {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  &__nav-link {
    font-size: 14px;
    color: #57534e;
    text-decoration: none;
    padding: 8px 16px;
    border-radius: 8px;
    transition: all 0.2s ease;

    &:hover {
      background: #f5f5f4;
      color: #1c1917;
    }
  }

  &__user-greeting {
    font-size: 14px;
    font-weight: 500;
    color: #1c1917;
    padding: 0 12px;
  }

  /* ===== Hero 区域 ===== */
  &__hero {
    position: relative;
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 120px 24px 80px;
    overflow: hidden;
  }

  &__hero-bg {
    position: absolute;
    inset: 0;
    background: linear-gradient(180deg, #fafaf9 0%, #fff 40%, #fefce8 100%);
    z-index: 0;

    &::before {
      content: '';
      position: absolute;
      top: -50%;
      right: -20%;
      width: 600px;
      height: 600px;
      background: radial-gradient(circle, rgba(217, 119, 6, 0.06) 0%, transparent 70%);
      border-radius: 50%;
    }

    &::after {
      content: '';
      position: absolute;
      bottom: -30%;
      left: -10%;
      width: 400px;
      height: 400px;
      background: radial-gradient(circle, rgba(251, 191, 36, 0.05) 0%, transparent 70%);
      border-radius: 50%;
    }
  }

  &__hero-content {
    position: relative;
    z-index: 1;
    text-align: center;
    max-width: 720px;
    animation: heroFadeIn 0.8s ease-out;
  }

  &__hero-badge {
    display: inline-block;
    padding: 6px 16px;
    background: #fffbeb;
    border: 1px solid #fde68a;
    border-radius: 20px;
    font-size: 13px;
    color: #b45309;
    font-weight: 500;
    margin-bottom: 24px;
  }

  &__hero-title {
    font-family: 'Fraunces', serif;
    font-size: 56px;
    font-weight: 500;
    font-variation-settings: 'SOFT' 50;
    color: #1c1917;
    line-height: 1.1;
    margin: 0 0 20px;
    letter-spacing: -0.03em;
  }

  &__hero-highlight {
    color: #d97706;
    background: linear-gradient(135deg, #d97706, #f59e0b);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  &__hero-desc {
    font-size: 17px;
    color: #78716c;
    line-height: 1.7;
    margin: 0 auto 32px;
    max-width: 560px;
  }

  &__hero-actions {
    display: flex;
    justify-content: center;
    gap: 12px;
    margin-bottom: 48px;
  }

  &__hero-stats {
    display: flex;
    justify-content: center;
    gap: 48px;
  }

  &__stat-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__stat-num {
    font-size: 16px;
    font-weight: 600;
    color: #1c1917;
  }

  &__stat-label {
    font-size: 13px;
    color: #a8a29e;
  }

  /* ===== 特性区域 ===== */
  &__features {
    padding: 80px 24px;
    background: #fafaf9;
  }

  &__features-inner {
    max-width: 1100px;
    margin: 0 auto;
  }

  &__section-header {
    text-align: center;
    margin-bottom: 48px;
  }

  &__section-title {
    font-family: 'Fraunces', serif;
    font-size: 36px;
    font-weight: 500;
    font-variation-settings: 'SOFT' 50;
    color: #1c1917;
    margin: 0 0 12px;
  }

  &__section-desc {
    font-size: 16px;
    color: #78716c;
    margin: 0;
  }

  &__features-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;

    @media (max-width: 900px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 560px) {
      grid-template-columns: 1fr;
    }
  }

  &__feature-card {
    background: #fff;
    border: 1px solid #e7e5e4;
    border-radius: 16px;
    padding: 28px;
    transition: all 0.3s ease;
    animation: cardFadeIn 0.6s ease-out backwards;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06);
      border-color: #d6d3d1;
    }
  }

  &__feature-icon {
    width: 52px;
    height: 52px;
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;
  }

  &__feature-title {
    font-size: 17px;
    font-weight: 600;
    color: #1c1917;
    margin: 0 0 8px;
  }

  &__feature-desc {
    font-size: 14px;
    color: #78716c;
    line-height: 1.6;
    margin: 0;
  }

  /* ===== Footer ===== */
  &__footer {
    border-top: 1px solid #e7e5e4;
    background: #fff;
  }

  &__footer-inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px;
    text-align: center;
    font-size: 14px;
    color: #a8a29e;
  }
}

@keyframes heroFadeIn {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
