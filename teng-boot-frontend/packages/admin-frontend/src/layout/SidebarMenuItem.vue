<template>
  <!-- 目录(有子节点)：el-sub-menu -->
  <el-sub-menu v-if="hasChildren" :index="item.path || item.id.toString()">
    <template #title>
      <el-icon v-if="item.icon">
        <component :is="getIcon(item.icon)" />
      </el-icon>
      <span>{{ item.menuName }}</span>
    </template>
    <SidebarMenuItem
      v-for="child in filteredChildren"
      :key="child.id"
      :item="child"
    />
  </el-sub-menu>

  <!-- 菜单项：el-menu-item -->
  <el-menu-item v-else :index="item.path || item.id.toString()">
    <el-icon v-if="item.icon">
      <component :is="getIcon(item.icon)" />
    </el-icon>
    <template #title>
      <span>{{ item.menuName }}</span>
    </template>
  </el-menu-item>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import * as Icons from '@element-plus/icons-vue'
import type { MenuVO } from '@teng-boot/shared/types'

const props = defineProps<{
  item: MenuVO
}>()

/** 只保留 menuType 为 M(目录) 或 C(菜单) 且可见、启用的子节点 */
const filteredChildren = computed(() => {
  if (!props.item.children) return []
  return props.item.children.filter(
    (child) =>
      (child.menuType === 'M' || child.menuType === 'C') &&
      child.visible === 1 &&
      child.status === 1
  )
})

/** 是否有需要渲染的子节点 */
const hasChildren = computed(() => filteredChildren.value.length > 0)

/** 根据图标名称动态获取 Element Plus 图标组件 */
function getIcon(iconName: string) {
  return (Icons as Record<string, unknown>)[iconName]
}
</script>
