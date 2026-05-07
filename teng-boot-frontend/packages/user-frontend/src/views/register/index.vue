<template>
  <div class="user-register">
    <!-- 波浪背景装饰 -->
    <div class="user-register__wave">
      <svg viewBox="0 0 1440 320" preserveAspectRatio="none">
        <path
          fill="rgba(217, 119, 6, 0.04)"
          d="M0,224L48,213.3C96,203,192,181,288,181.3C384,181,480,203,576,202.7C672,203,768,181,864,176C960,171,1056,181,1152,197.3C1248,213,1344,235,1392,245.3L1440,256L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"
        />
      </svg>
    </div>

    <div class="user-register__container">
      <!-- 品牌区 -->
      <div class="user-register__brand">
        <span class="user-register__brand-mark">Teng Boot</span>
        <p class="user-register__brand-desc">创建您的账户以开启更多功能</p>
      </div>

      <!-- 表单卡片 -->
      <div class="user-register__card">
        <h2 class="user-register__title">创建账户</h2>
        <p class="user-register__subtitle">请填写注册信息</p>

        <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
          <n-form-item path="username">
            <n-input
              v-model:value="form.username"
              placeholder="用户名（4~32位，字母数字下划线）"
              size="large"
              maxlength="32"
            >
              <template #prefix>
                <n-icon><PersonOutline /></n-icon>
              </template>
            </n-input>
          </n-form-item>

          <n-form-item path="userPassword">
            <n-input
              v-model:value="form.userPassword"
              type="password"
              placeholder="密码（6~32位）"
              show-password-on="click"
              size="large"
              maxlength="32"
            >
              <template #prefix>
                <n-icon><LockClosedOutline /></n-icon>
              </template>
            </n-input>
          </n-form-item>

          <n-form-item path="checkPassword">
            <n-input
              v-model:value="form.checkPassword"
              type="password"
              placeholder="确认密码"
              show-password-on="click"
              size="large"
              maxlength="32"
            >
              <template #prefix>
                <n-icon><LockClosedOutline /></n-icon>
              </template>
            </n-input>
          </n-form-item>

          <!-- 滑块验证码 -->
          <n-form-item>
            <div class="user-register__captcha-area">
              <SliderCaptcha
                ref="captchaRef"
                @success="handleCaptchaSuccess"
              />
            </div>
          </n-form-item>

          <n-form-item>
            <n-button
              type="primary"
              :loading="loading"
              size="large"
              block
              @click="handleRegister"
            >
              注 册
            </n-button>
          </n-form-item>
        </n-form>

        <p class="user-register__footer">
          已有账户？
          <router-link to="/login">去登录</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormRules, FormInst } from 'naive-ui'
import { useMessage } from 'naive-ui'
import { PersonOutline, LockClosedOutline } from '@vicons/ionicons5'
import { register } from '@teng-boot/shared'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const router = useRouter()
const message = useMessage()
const formRef = ref<FormInst | null>(null)
const captchaRef = ref<InstanceType<typeof SliderCaptcha>>()
const loading = ref(false)
const captchaVerification = ref('')

const form = reactive({
  username: '',
  userPassword: '',
  checkPassword: '',
})

/** 用户名正则：4~32 位字母/数字/下划线 */
const usernamePattern = /^[a-zA-Z0-9_]{4,32}$/

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名' },
    {
      pattern: usernamePattern,
      message: '用户名需为 4~32 位字母、数字或下划线',
    },
  ],
  userPassword: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 32, message: '密码长度为 6~32 位' },
  ],
  checkPassword: [
    { required: true, message: '请确认密码' },
    {
      validator: (_rule, value) => {
        if (value !== form.userPassword) {
          return new Error('两次输入的密码不一致')
        }
        return true
      },
      trigger: ['input', 'blur'],
    },
  ],
}

/** 滑块验证成功回调 */
function handleCaptchaSuccess(verification: string) {
  captchaVerification.value = verification
}

/** 注册 */
async function handleRegister() {
  formRef.value?.validate(async (errors) => {
    if (errors) return

    if (!captchaVerification.value) {
      message.warning('请先完成滑块验证')
      return
    }

    loading.value = true
    try {
      const res = await register({
        username: form.username,
        userPassword: form.userPassword,
        checkPassword: form.checkPassword,
        captchaVerification: captchaVerification.value,
      })

      if (res.code === 200 && res.data) {
        message.success('注册成功')
        router.push('/login')
      } else {
        message.error(res.message || '注册失败')
        captchaRef.value?.refresh()
        captchaVerification.value = ''
      }
    } catch (err: any) {
      message.error(err?.message || '注册失败，请重试')
      captchaRef.value?.refresh()
      captchaVerification.value = ''
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.user-register {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafaf9;
  padding: 24px;
  position: relative;
  overflow: hidden;

  &__wave {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    line-height: 0;

    svg {
      width: 100%;
      height: 200px;
    }
  }

  &__container {
    position: relative;
    z-index: 1;
    width: 100%;
    max-width: 400px;
  }

  &__brand {
    text-align: center;
    margin-bottom: 32px;
  }

  &__brand-mark {
    font-family: 'Fraunces', serif;
    font-size: 28px;
    font-weight: 600;
    font-variation-settings: 'SOFT' 50;
    color: #1c1917;
  }

  &__brand-desc {
    font-size: 14px;
    color: #a8a29e;
    margin: 8px 0 0;
  }

  &__card {
    background: #fff;
    border: 1px solid #e7e5e4;
    border-radius: 20px;
    padding: 36px 32px 28px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.04);
  }

  &__title {
    font-family: 'Fraunces', serif;
    font-size: 24px;
    font-weight: 500;
    font-variation-settings: 'SOFT' 50;
    color: #1c1917;
    margin: 0 0 4px;
  }

  &__subtitle {
    font-size: 14px;
    color: #a8a29e;
    margin: 0 0 24px;
  }

  &__captcha-area {
    width: 100%;
  }

  &__footer {
    text-align: center;
    font-size: 13px;
    color: #a8a29e;
    margin: 16px 0 0;

    a {
      color: #d97706;
      text-decoration: none;
      font-weight: 500;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>
