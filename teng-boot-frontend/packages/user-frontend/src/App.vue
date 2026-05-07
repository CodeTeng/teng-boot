<template>
  <n-config-provider :theme-overrides="themeOverrides" :locale="zhCN" :date-locale="dateZhCN">
    <n-message-provider>
      <n-dialog-provider>
        <router-view />
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { zhCN, dateZhCN } from 'naive-ui'
import type { GlobalThemeOverrides } from 'naive-ui'

const themeOverrides: GlobalThemeOverrides = {
  common: {
    primaryColor: '#d97706',
    primaryColorHover: '#f59e0b',
    primaryColorPressed: '#b45309',
    primaryColorSuppl: '#d97706',
    borderRadius: '12px',
    fontFamily: '"Instrument Sans", "Helvetica Neue", sans-serif',
  },
  Card: {
    borderRadius: '16px',
  },
  Button: {
    borderRadius: '10px',
    fontWeight: '600',
  },
  Input: {
    borderRadius: '10px',
  },
}
</script>

<style lang="scss">
html,
body,
#app {
  margin: 0;
  padding: 0;
  width: 100%;
  min-height: 100vh;
  font-family: 'Instrument Sans', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
