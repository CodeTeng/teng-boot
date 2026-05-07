<template>
  <div class="slider-captcha">
    <!-- 验证码图片区域 -->
    <div class="slider-captcha__image" ref="imageRef">
      <img
        v-if="bgImageSrc"
        :src="bgImageSrc"
        alt="验证码背景"
        class="slider-captcha__bg"
        @load="onBgLoaded"
      />
      <!-- 拼图碎片 -->
      <div
        v-if="jigsawImageSrc"
        class="slider-captcha__piece"
        :class="{
          'slider-captcha__piece--verified': verified,
          'slider-captcha__piece--snapping': isSnapping,
        }"
        :style="pieceStyle"
      />
      <!-- 加载中 -->
      <div v-if="loading" class="slider-captcha__loading">
        <div class="slider-captcha__loading-spinner" />
        <span>加载验证码中...</span>
      </div>
      <!-- 刷新按钮 -->
      <button
        v-if="!loading && !verified"
        class="slider-captcha__refresh"
        title="刷新验证码"
        @click="refresh"
      >
        <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="23 4 23 10 17 10" />
          <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10" />
        </svg>
      </button>
    </div>

    <!-- 滑块轨道 -->
    <div class="slider-captcha__track" ref="trackRef">
      <div
        class="slider-captcha__track-fill"
        :class="{ 'slider-captcha__track-fill--success': verified }"
        :style="{ width: `${progressPercent}%` }"
      />
      <div
        class="slider-captcha__track-btn"
        :class="{
          'slider-captcha__track-btn--dragging': dragging,
          'slider-captcha__track-btn--success': verified,
          'slider-captcha__track-btn--fail': showFail,
        }"
        :style="{ left: `${sliderLeft}px`, transition: btnTransition }"
        @mousedown="onDragStart"
        @touchstart.prevent="onDragStart"
      >
        <!-- 默认：右箭头 -->
        <svg v-if="!verified && !showFail" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <polyline v-if="!dragging" points="9 18 15 12 9 6" />
          <line v-else x1="5" y1="12" x2="19" y2="12" />
        </svg>
        <!-- 成功：勾 -->
        <svg v-if="verified" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="#fff" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="20 6 9 17 4 12" />
        </svg>
        <!-- 失败：叉 -->
        <svg v-if="showFail" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="#fff" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
          <line x1="18" y1="6" x2="6" y2="18" />
          <line x1="6" y1="6" x2="18" y2="18" />
        </svg>
      </div>
      <span class="slider-captcha__track-text" :class="{ 'is-hidden': dragging || verified || showFail }">
        {{ statusText }}
      </span>
    </div>

    <!-- 提示消息 -->
    <transition name="msg-fade">
      <div v-if="errorMsg" class="slider-captcha__msg slider-captcha__msg--error">
        {{ errorMsg }}
      </div>
      <div v-else-if="verified" class="slider-captcha__msg slider-captcha__msg--success">
        <svg viewBox="0 0 24 24" width="12" height="12" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="20 6 9 17 4 12" />
        </svg>
        验证通过
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { getCaptcha, checkCaptcha, buildCaptchaVerification } from '@teng-boot/shared'
import type { CaptchaData } from '@teng-boot/shared'

const emit = defineEmits<{
  (e: 'success', captchaVerification: string): void
  (e: 'fail'): void
}>()

const PIECE_SIZE = 50
const PIECE_Y = 55
const BTN_SIZE = 36
const RESET_DELAY_MS = 800

const imageRef = ref<HTMLElement | null>(null)
const trackRef = ref<HTMLElement | null>(null)

const captchaData = ref<CaptchaData | null>(null)
const loading = ref(false)
const errorMsg = ref('')

const dragging = ref(false)
const verified = ref(false)
const verifying = ref(false)
const showFail = ref(false)
const sliderLeft = ref(0)
const pieceOffset = ref(0)
const progressPercent = ref(0)
const isSnapping = ref(false)
const pieceTransition = ref('none')
const btnTransition = ref('none')

// 缓存的DOM引用和尺寸（避免 mousemove 中重复查询）
let cachedTrackWidth = 0
let cachedImgWidth = 310
let failTimer: ReturnType<typeof setTimeout> | null = null

// 图片源
const bgImageSrc = computed(() => {
  if (!captchaData.value?.originalImageBase64) return ''
  const raw = captchaData.value.originalImageBase64
  return raw.startsWith('data:') ? raw : `data:image/png;base64,${raw}`
})

const jigsawImageSrc = computed(() => {
  if (!captchaData.value?.jigsawImageBase64) return ''
  const raw = captchaData.value.jigsawImageBase64
  return raw.startsWith('data:') ? raw : `data:image/png;base64,${raw}`
})

// 拼图碎片样式
const pieceStyle = computed(() => ({
  top: `${PIECE_Y}px`,
  left: `${pieceOffset}px`,
  width: `${PIECE_SIZE}px`,
  height: `${PIECE_SIZE}px`,
  backgroundImage: `url(${jigsawImageSrc.value})`,
  backgroundSize: '100% 100%',
  backgroundRepeat: 'no-repeat',
  backgroundPosition: 'center',
  transition: pieceTransition.value,
}))

const statusText = computed(() => {
  if (verified.value) return '验证通过'
  if (showFail.value) return '验证失败，请重试'
  if (dragging.value) return '松开完成验证'
  return '向右拖动滑块填充拼图'
})

// ===== 验证码加载 =====

async function loadCaptcha() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await getCaptcha()
    if (res.code === 200 && res.data) {
      captchaData.value = res.data
    } else {
      errorMsg.value = '验证码加载失败，请重试'
    }
  } catch {
    errorMsg.value = '验证码加载失败，请重试'
  } finally {
    loading.value = false
  }
}

function onBgLoaded() {
  // 背景图加载完成后可触发额外逻辑
}

// ===== 拖动逻辑 =====

let startX = 0
let startLeft = 0

function cacheDims() {
  const track = trackRef.value
  if (track) cachedTrackWidth = track.getBoundingClientRect().width - BTN_SIZE
  const imgEl = imageRef.value?.querySelector('img')
  if (imgEl) cachedImgWidth = imgEl.clientWidth || 310
}

function onDragStart(e: MouseEvent | TouchEvent) {
  if (verified.value || showFail.value || loading.value || verifying.value || !jigsawImageSrc.value) return
  errorMsg.value = ''
  dragging.value = true
  pieceTransition.value = 'none'
  btnTransition.value = 'none'
  isSnapping.value = false
  cacheDims()
  const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX
  startX = clientX
  startLeft = sliderLeft.value
  document.addEventListener('mousemove', onDragMove)
  document.addEventListener('mouseup', onDragEnd)
  document.addEventListener('touchmove', onDragMove, { passive: false })
  document.addEventListener('touchend', onDragEnd)
}

function onDragMove(e: MouseEvent | TouchEvent) {
  if (!dragging.value || cachedTrackWidth <= 0) return
  e.preventDefault()
  const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX
  let newLeft = startLeft + (clientX - startX)
  newLeft = Math.max(0, Math.min(cachedTrackWidth, newLeft))
  sliderLeft.value = newLeft
  pieceOffset.value = (newLeft / cachedTrackWidth) * (cachedImgWidth - PIECE_SIZE)
  progressPercent.value = (newLeft / cachedTrackWidth) * 100
}

async function onDragEnd() {
  document.removeEventListener('mousemove', onDragMove)
  document.removeEventListener('mouseup', onDragEnd)
  document.removeEventListener('touchmove', onDragMove)
  document.removeEventListener('touchend', onDragEnd)
  if (!dragging.value) return
  dragging.value = false

  // 最小拖动距离检查
  if (cachedTrackWidth > 0 && sliderLeft.value / cachedTrackWidth < 0.05) {
    sliderLeft.value = 0
    pieceOffset.value = 0
    progressPercent.value = 0
    pieceTransition.value = 'left 0.3s ease'
    btnTransition.value = 'left 0.3s ease'
    return
  }

  if (!captchaData.value) return
  const offset = Math.round((sliderLeft.value / cachedTrackWidth) * (cachedImgWidth - PIECE_SIZE))

  verifying.value = true
  try {
    const encrypted = await buildCaptchaVerification(offset, captchaData.value.secretKey)
    const res = await checkCaptcha({ captchaType: 'blockPuzzle', captchaVerification: encrypted })
    if (res.code === 200 && res.data === true) {
      verified.value = true
      emit('success', encrypted)
    } else {
      handleVerifyFail('验证失败，请重试')
    }
  } catch {
    handleVerifyFail('网络异常，请重试')
  } finally {
    verifying.value = false
  }
}

function handleVerifyFail(msg: string) {
  showFail.value = true
  errorMsg.value = msg
  emit('fail')
  if (failTimer) clearTimeout(failTimer)
  failTimer = setTimeout(() => refresh(), RESET_DELAY_MS)
}

// ===== 暴露方法 =====

function markVerified() {
  verified.value = true
  errorMsg.value = ''
  showFail.value = false
}

function reset() {
  progressPercent.value = 0
  pieceOffset.value = 0
  sliderLeft.value = 0
  dragging.value = false
  verified.value = false
  showFail.value = false
  errorMsg.value = ''
  isSnapping.value = false
  pieceTransition.value = 'none'
  btnTransition.value = 'none'
}

function refresh() {
  reset()
  loadCaptcha()
}

defineExpose({ reset, markVerified, refresh })

onMounted(() => {
  loadCaptcha()
})

onUnmounted(() => {
  if (failTimer) clearTimeout(failTimer)
  document.removeEventListener('mousemove', onDragMove)
  document.removeEventListener('mouseup', onDragEnd)
  document.removeEventListener('touchmove', onDragMove)
  document.removeEventListener('touchend', onDragEnd)
})
</script>

<style lang="scss" scoped>
.slider-captcha {
  width: 310px;
  border-radius: 10px;
  border: 1px solid #e4e7ed;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  user-select: none;

  // ===== 图片区域 =====
  &__image {
    position: relative;
    width: 310px;
    height: 155px;
    overflow: hidden;
    background: #f5f7fa;
  }

  &__bg {
    display: block;
    width: 310px;
    height: 155px;
    object-fit: cover;
  }

  &__loading {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 10px;
    background: #f5f7fa;
    color: #909399;
    font-size: 13px;

    &-spinner {
      width: 24px;
      height: 24px;
      border: 2.5px solid #e4e7ed;
      border-top-color: #409eff;
      border-radius: 50%;
      animation: captcha-spin 0.8s linear infinite;
    }
  }

  &__refresh {
    position: absolute;
    top: 6px;
    right: 6px;
    width: 28px;
    height: 28px;
    border-radius: 6px;
    border: none;
    background: rgba(0, 0, 0, 0.45);
    color: #fff;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0.7;
    transition: opacity 0.2s, background 0.2s;
    z-index: 5;

    &:hover {
      opacity: 1;
      background: rgba(0, 0, 0, 0.6);
    }
  }

  // ===== 拼图碎片 =====
  &__piece {
    position: absolute;
    pointer-events: none;
    border-radius: 3px;
    box-shadow:
      0 0 0 2px rgba(255, 255, 255, 0.9),
      0 4px 12px rgba(0, 0, 0, 0.25);
    z-index: 2;

    &--verified {
      box-shadow:
        0 0 0 2px rgba(103, 194, 58, 0.8),
        0 4px 12px rgba(103, 194, 58, 0.3);
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &--snapping {
      box-shadow:
        0 0 0 2px rgba(245, 108, 108, 0.8),
        0 4px 12px rgba(245, 108, 108, 0.3);
    }
  }

  // ===== 轨道 =====
  &__track {
    position: relative;
    height: 40px;
    background: #f5f7fa;
    border-top: 1px solid #e4e7ed;
    display: flex;
    align-items: center;
  }

  &__track-fill {
    position: absolute;
    left: 0;
    top: 0;
    height: 100%;
    background: linear-gradient(90deg, #a0cfff, #409eff);
    pointer-events: none;
    border-radius: 0;

    &--success {
      background: linear-gradient(90deg, #85ce61, #67c23a);
    }
  }

  &__track-btn {
    position: absolute;
    top: 2px;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.18);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: grab;
    z-index: 3;
    color: #409eff;
    transform: translateX(-50%);
    border: 2px solid #409eff;
    transition: box-shadow 0.2s, border-color 0.2s, background-color 0.2s;

    &:hover {
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
    }

    &--dragging {
      cursor: grabbing;
      box-shadow: 0 2px 12px rgba(64, 158, 255, 0.4);
      transform: translateX(-50%) scale(1.06);
    }

    &--success {
      border-color: #67c23a;
      background: #67c23a;
      color: #fff;
      cursor: default;
      box-shadow: 0 2px 8px rgba(103, 194, 58, 0.35);
    }

    &--fail {
      border-color: #f56c6c;
      background: #f56c6c;
      color: #fff;
      box-shadow: 0 2px 8px rgba(245, 108, 108, 0.35);
    }
  }

  &__track-text {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    font-size: 13px;
    color: #909399;
    pointer-events: none;
    white-space: nowrap;
    transition: opacity 0.25s;
    z-index: 1;

    &.is-hidden {
      opacity: 0;
    }
  }

  // ===== 提示消息 =====
  &__msg {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    padding: 6px 12px;
    font-size: 12px;
    text-align: center;
    line-height: 1.5;

    &--error {
      color: #f56c6c;
      background: #fef0f0;
    }

    &--success {
      color: #67c23a;
      background: #f0f9eb;
    }
  }
}

.msg-fade-enter-active,
.msg-fade-leave-active {
  transition: opacity 0.25s ease;
}

.msg-fade-enter-from,
.msg-fade-leave-to {
  opacity: 0;
}

@keyframes captcha-spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
