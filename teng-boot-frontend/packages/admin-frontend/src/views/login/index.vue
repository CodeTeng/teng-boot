<template>
  <div class="login-page">
    <!-- 左侧品牌区域 -->
    <div class="login-page__brand">
      <div class="login-page__brand-bg">
        <div class="login-page__grid" />
      </div>
      <div class="login-page__brand-content">
        <div class="login-page__logo">
          <el-icon :size="40" color="#fff"><Monitor /></el-icon>
        </div>
        <h1 class="login-page__title">Teng Boot</h1>
        <p class="login-page__subtitle">企业级全栈开发框架管理后台</p>
        <div class="login-page__brand-footer">
          <div class="login-page__stat">
            <span class="login-page__stat-value">v0.1</span>
            <span class="login-page__stat-label">版本</span>
          </div>
          <div class="login-page__stat">
            <span class="login-page__stat-value">Vue 3</span>
            <span class="login-page__stat-label">技术栈</span>
          </div>
          <div class="login-page__stat">
            <span class="login-page__stat-value">TS</span>
            <span class="login-page__stat-label">类型安全</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="login-page__form-wrapper">
      <div class="login-page__form-container">
        <div class="login-page__form-header">
          <h2 class="login-page__form-title">欢迎回来</h2>
          <p class="login-page__form-desc">请登录您的账户以继续</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="login-page__form"
          size="large"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="userPassword">
            <el-input
              v-model="form.userPassword"
              type="password"
              placeholder="密码"
              show-password
              :prefix-icon="Lock"
            />
          </el-form-item>

          <!-- 滑块验证码 -->
          <el-form-item>
            <div class="login-page__captcha-area">
              <SliderCaptcha
                ref="captchaRef"
                @success="handleCaptchaSuccess"
              />
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="login-page__submit-btn"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <p class="login-page__switch">
          没有账户？
          <a href="javascript:void(0)" @click="switchToUser">前往用户端注册</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { User, Lock, Monitor } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { login } from '@teng-boot/shared'
import { setToken, setUserInfo } from '@teng-boot/shared/utils/auth'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const captchaRef = ref<InstanceType<typeof SliderCaptcha>>()
const loading = ref(false)
const captchaVerification = ref('')

const form = reactive({
  username: '',
  userPassword: '',
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  userPassword: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

/** 滑块验证成功回调 */
function handleCaptchaSuccess(verification: string) {
  captchaVerification.value = verification
}

/** 登录 */
async function handleLogin() {
  formRef.value?.validate(async (valid) => {
    if (!valid) return

    if (!captchaVerification.value) {
      ElMessage.warning('请先完成滑块验证')
      return
    }

    loading.value = true
    try {
      const res = await login({
        username: form.username,
        userPassword: form.userPassword,
        captchaVerification: captchaVerification.value,
      })

      if (res.code === 200 && res.data) {
        const user = res.data

        // 检查是否为管理员
        if (!user.roles?.includes('admin')) {
          ElMessage.error('您没有管理员权限，无法登录管理后台')
          loading.value = false
          captchaRef.value?.reset()
          captchaVerification.value = ''
          return
        }

        // 存储 token 和用户信息
        if (user.token) {
          setToken(user.token)
        }
        setUserInfo(user)
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } else {
        ElMessage.error(res.message || '登录失败')
        captchaRef.value?.refresh()
        captchaVerification.value = ''
      }
    } catch (err: any) {
      ElMessage.error(err?.message || '登录失败，请重试')
      captchaRef.value?.refresh()
      captchaVerification.value = ''
    } finally {
      loading.value = false
    }
  })
}

function switchToUser() {
  window.open('/user', '_blank')
}
</script>

<style lang="scss" scoped>
.login-page {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;

  /* ===== 左侧品牌区域 ===== */
  &__brand {
    position: relative;
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
  }

  &__brand-bg {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%);
    z-index: 0;
  }

  &__grid {
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
    background-size: 60px 60px;
    mask-image: radial-gradient(ellipse at 50% 50%, black 30%, transparent 70%);
    -webkit-mask-image: radial-gradient(ellipse at 50% 50%, black 30%, transparent 70%);
  }

  &__brand-content {
    position: relative;
    z-index: 1;
    text-align: center;
    padding: 40px;
    animation: brandFadeIn 1s ease-out;
  }

  &__logo {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 24px;

    .el-icon {
      background: rgba(255, 255, 255, 0.08);
      border-radius: 16px;
      padding: 12px;
      backdrop-filter: blur(8px);
    }
  }

  &__title {
    font-family: 'DM Serif Display', serif;
    font-size: 42px;
    font-weight: 400;
    color: #f1f5f9;
    margin: 0 0 12px;
    letter-spacing: -0.02em;
  }

  &__subtitle {
    font-size: 15px;
    color: #64748b;
    margin: 0 0 48px;
    letter-spacing: 0.3px;
  }

  &__brand-footer {
    display: flex;
    justify-content: center;
    gap: 40px;
  }

  &__stat {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
  }

  &__stat-value {
    font-size: 15px;
    color: #e2e8f0;
    font-weight: 600;
  }

  &__stat-label {
    font-size: 12px;
    color: #475569;
    text-transform: uppercase;
    letter-spacing: 1px;
  }

  /* ===== 右侧表单区域 ===== */
  &__form-wrapper {
    width: 480px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f8fafc;
    flex-shrink: 0;
  }

  &__form-container {
    width: 360px;
  }

  &__form-header {
    margin-bottom: 32px;
  }

  &__form-title {
    font-family: 'DM Serif Display', serif;
    font-size: 28px;
    font-weight: 400;
    color: #0f172a;
    margin: 0 0 8px;
  }

  &__form-desc {
    font-size: 14px;
    color: #94a3b8;
    margin: 0;
  }

  &__form {
    :deep(.el-input__wrapper) {
      background-color: #fff;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
      border: 1px solid #e2e8f0;
      border-radius: 10px;
      transition: all 0.2s ease;

      &:hover {
        border-color: #cbd5e1;
      }

      &.is-focus {
        border-color: var(--el-color-primary);
        box-shadow: 0 0 0 3px rgba(var(--el-color-primary-rgb), 0.1);
      }
    }

    .el-form-item {
      margin-bottom: 22px;
    }
  }

  &__captcha-area {
    width: 100%;
  }

  &__submit-btn {
    width: 100%;
    height: 48px;
    border-radius: 10px;
    font-size: 15px;
    font-weight: 600;
    letter-spacing: 2px;
  }

  &__switch {
    text-align: center;
    font-size: 13px;
    color: #94a3b8;
    margin-top: 24px;

    a {
      color: var(--el-color-primary);
      text-decoration: none;
      font-weight: 500;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}

@keyframes brandFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
