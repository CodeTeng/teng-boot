<template>
  <div class="menu-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-header__title">菜单管理</h2>
      <p class="page-header__desc">管理系统中的菜单、目录和功能权限</p>
    </div>

    <!-- 操作栏 + 树形表格 -->
    <el-card shadow="never" class="table-card">
      <!-- 操作栏 -->
      <div class="table-toolbar">
        <div class="table-toolbar__left">
          <el-button @click="handleToggleExpand">
            <el-icon>
              <component :is="allExpanded ? 'Fold' : 'Expand'" />
            </el-icon>
            {{ allExpanded ? '折叠全部' : '展开全部' }}
          </el-button>
        </div>
        <div class="table-toolbar__right">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增菜单
          </el-button>
        </div>
      </div>

      <!-- 树形表格 -->
      <el-table
        ref="tableRef"
        :key="tableKey"
        :data="menuTree"
        row-key="id"
        border
        :default-expand-all="allExpanded"
        :tree-props="{ children: 'children' }"
        v-loading="loading"
        class="menu-table"
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="180" />
        <el-table-column prop="icon" label="图标" width="70" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon && getIconComponent(row.icon)" :size="18">
              <component :is="getIconComponent(row.icon)" />
            </el-icon>
            <span v-else class="menu-table__icon-placeholder">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="70" align="center" />
        <el-table-column prop="path" label="路由地址" min-width="140" show-overflow-tooltip />
        <el-table-column prop="perms" label="权限标识" min-width="140" show-overflow-tooltip />
        <el-table-column prop="component" label="组件路径" min-width="140" show-overflow-tooltip />
        <el-table-column label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag
              :type="menuTypeTag(row.menuType)"
              size="small"
              effect="plain"
            >
              {{ menuTypeLabel(row.menuType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              :loading="(row as Record<string, unknown>)._statusLoading as boolean"
              @change="(val: boolean) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleAddSub(row)">
              新增
            </el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>

        <!-- 空数据提示 -->
        <template #empty>
          <div class="menu-table__empty">
            <el-icon :size="48" color="#d0d5dd"><FolderOpened /></el-icon>
            <p>{{ loading ? '加载中...' : '暂无菜单数据，请点击上方「新增菜单」创建' }}</p>
          </div>
        </template>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑菜单' : '新增菜单'"
      width="620px"
      :close-on-click-modal="false"
      destroy-on-close
      class="menu-dialog"
      @closed="handleDialogClosed"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        label-position="right"
        class="menu-form"
      >
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="menuTreeOptions"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            placeholder="请选择上级菜单（留空为顶级）"
            clearable
            check-strictly
            default-expand-all
            class="menu-form__tree-select"
            filterable
          />
        </el-form-item>

        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="formData.menuType">
            <el-radio value="M">目录</el-radio>
            <el-radio value="C">菜单</el-radio>
            <el-radio value="F">按钮</el-radio>
          </el-radio-group>
          <span class="menu-form__type-hint">{{ menuTypeHint }}</span>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input
                v-model="formData.menuName"
                placeholder="请输入菜单名称"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="orderNum">
              <el-input-number
                v-model="formData.orderNum"
                :min="0"
                :max="999"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="图标" prop="icon">
              <el-input
                v-model="formData.icon"
                placeholder="如 Monitor"
              >
                <template v-if="formData.icon && getIconComponent(formData.icon)" #prefix>
                  <el-icon :size="16">
                    <component :is="getIconComponent(formData.icon)" />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 路由地址：非按钮时显示 -->
            <el-form-item
              v-if="formData.menuType !== 'F'"
              label="路由地址"
              prop="path"
            >
              <el-input v-model="formData.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 组件路径：仅菜单(C)时显示 -->
        <el-form-item v-if="formData.menuType === 'C'" label="组件路径" prop="component">
          <el-input v-model="formData.component" placeholder="请输入组件路径，如 /views/system/index.vue" />
        </el-form-item>

        <!-- 权限标识：非目录时显示 -->
        <el-form-item v-if="formData.menuType !== 'M'" label="权限标识" prop="perms">
          <el-input v-model="formData.perms" placeholder="如 system:user:list" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            :active-value="1"
            :inactive-value="0"
            active-text="正常"
            inactive-text="停用"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus, Expand, Fold, FolderOpened } from '@element-plus/icons-vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import type { Component } from 'vue'
import {
  getMenuTree,
  addMenu,
  updateMenu,
  deleteMenu,
} from '@teng-boot/shared'
import type { MenuVO, MenuAddDTO, MenuUpdateDTO } from '@teng-boot/shared'

// ==================== 类型映射 ====================
const menuTypeLabelMap: Record<string, string> = {
  M: '目录',
  C: '菜单',
  F: '按钮',
}

const menuTypeTagMap: Record<string, string> = {
  M: 'warning',
  C: 'success',
  F: 'info',
}

function menuTypeLabel(type: string): string {
  return menuTypeLabelMap[type] || type
}

function menuTypeTag(type: string): string {
  return menuTypeTagMap[type] || 'info'
}

const menuTypeHintMap: Record<string, string> = {
  M: '目录下可挂载子菜单和按钮',
  C: '菜单对应一个功能页面',
  F: '按钮为页面内的操作权限标识',
}

const menuTypeHint = computed(() => menuTypeHintMap[formData.menuType])

// ==================== 图标解析 ====================
const iconComponentMap: Record<string, Component> =
  ElementPlusIconsVue as unknown as Record<string, Component>

function getIconComponent(name: string): Component | undefined {
  if (!name) return undefined
  // 尝试多种命名风格（PascalCase、首字母大写、kebab-case 转 PascalCase）
  return (
    iconComponentMap[name] ??
    iconComponentMap[name.charAt(0).toUpperCase() + name.slice(1)] ??
    iconComponentMap[
      name.replace(/-(\w)/g, (_, c: string) => c.toUpperCase())
    ]
  )
}

// ==================== 数据状态 ====================
const menuTree = ref<MenuVO[]>([])
const loading = ref(false)
const tableRef = ref()
const tableKey = ref(0)
const allExpanded = ref(true)

// ==================== 对话框状态 ====================
const dialogVisible = ref(false)
const isEditing = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)
const formRef = ref<FormInstance>()

interface MenuForm {
  parentId: number | undefined
  menuType: string
  menuName: string
  icon: string
  orderNum: number
  path: string
  component: string
  perms: string
  status: number
}

const createDefaultForm = (): MenuForm => ({
  parentId: undefined,
  menuType: 'M',
  menuName: '',
  icon: '',
  orderNum: 0,
  path: '',
  component: '',
  perms: '',
  status: 1,
})

const formData: MenuForm = reactive(createDefaultForm())

// ==================== 表单验证规则 ====================
const formRules = computed<FormRules>(() => {
  const rules: FormRules = {
    menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
    menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
  }
  // 非按钮时路由地址必填
  if (formData.menuType !== 'F') {
    rules.path = [{ required: true, message: '请输入路由地址', trigger: 'blur' }]
  }
  // 菜单时组件路径必填
  if (formData.menuType === 'C') {
    rules.component = [{ required: true, message: '请输入组件路径', trigger: 'blur' }]
  }
  return rules
})

// ==================== 树形数据过滤 ====================
/** 过滤掉按钮类型(F)的菜单，用于上级菜单选择 */
function filterButtonMenus(tree: MenuVO[]): MenuVO[] {
  return tree
    .filter((item) => item.menuType !== 'F')
    .map((item) => ({
      ...item,
      children: item.children?.length ? filterButtonMenus(item.children) : [],
    }))
}

/** 获取菜单树选项（过滤按钮类型） */
const menuTreeOptions = computed(() => filterButtonMenus(menuTree.value))

// ==================== 展开/折叠 ====================
function handleToggleExpand() {
  allExpanded.value = !allExpanded.value
  tableKey.value++
}

// ==================== 数据加载 ====================
async function loadMenuTree() {
  loading.value = true
  try {
    const res = await getMenuTree()
    if (res.code === 200) {
      menuTree.value = res.data
    }
  } catch {
    // 响应拦截器已处理错误
  } finally {
    loading.value = false
  }
}

// ==================== 新增 ====================
function resetForm() {
  Object.assign(formData, createDefaultForm())
  editingId.value = null
  isEditing.value = false
}

function handleAdd() {
  resetForm()
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

function handleAddSub(row: MenuVO) {
  resetForm()
  formData.parentId = row.id
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

// ==================== 编辑 ====================
function handleEdit(row: MenuVO) {
  isEditing.value = true
  editingId.value = row.id
  formData.parentId = row.parentId || undefined
  formData.menuType = row.menuType
  formData.menuName = row.menuName
  formData.icon = row.icon
  formData.orderNum = row.orderNum
  formData.path = row.path
  formData.component = row.component ?? ''
  formData.perms = row.perms ?? ''
  formData.status = row.status
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

// ==================== 提交 ====================
async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEditing.value && editingId.value !== null) {
      const payload: MenuUpdateDTO = {
        id: editingId.value,
        menuName: formData.menuName,
        parentId: formData.parentId,
        orderNum: formData.orderNum,
        path: formData.path,
        component: formData.component || undefined,
        perms: formData.perms || undefined,
        menuType: formData.menuType,
        icon: formData.icon || undefined,
        status: formData.status,
      }
      const res = await updateMenu(payload)
      if (res.code === 200) {
        ElMessage.success('菜单更新成功')
        dialogVisible.value = false
        loadMenuTree()
      }
    } else {
      const payload: MenuAddDTO = {
        menuName: formData.menuName,
        parentId: formData.parentId,
        orderNum: formData.orderNum,
        path: formData.path,
        component: formData.component || undefined,
        perms: formData.perms || undefined,
        menuType: formData.menuType,
        icon: formData.icon || undefined,
        status: formData.status,
      }
      const res = await addMenu(payload)
      if (res.code === 200) {
        ElMessage.success('菜单新增成功')
        dialogVisible.value = false
        loadMenuTree()
      }
    }
  } catch {
    // 响应拦截器已处理错误
  } finally {
    submitting.value = false
  }
}

// ==================== 删除 ====================
function handleDelete(row: MenuVO) {
  const message = row.children?.length
    ? `确认删除菜单「${row.menuName}」吗？该菜单包含子菜单，删除后子菜单将一并删除。`
    : `确认删除菜单「${row.menuName}」吗？`

  ElMessageBox.confirm(message, '确认删除', {
    type: 'warning',
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    confirmButtonClass: 'el-button--danger',
  })
    .then(async () => {
      try {
        const res = await deleteMenu(row.id)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          loadMenuTree()
        }
      } catch {
        // 响应拦截器已处理错误
      }
    })
    .catch(() => {
      // 取消删除
    })
}

// ==================== 状态切换 ====================
async function handleStatusChange(row: MenuVO, newStatus: boolean) {
  const targetStatus = newStatus ? 1 : 0
  const rowExt = row as MenuVO & { _statusLoading: boolean }
  rowExt._statusLoading = true
  try {
    const payload: MenuUpdateDTO = {
      id: row.id,
      menuName: row.menuName,
      parentId: row.parentId,
      orderNum: row.orderNum,
      path: row.path,
      menuType: row.menuType,
      status: targetStatus,
      icon: row.icon || undefined,
      perms: row.perms || undefined,
      component: row.component || undefined,
    }
    const res = await updateMenu(payload)
    if (res.code === 200) {
      row.status = targetStatus
      ElMessage.success(targetStatus === 1 ? '已启用' : '已停用')
    }
  } catch {
    // 响应拦截器已处理错误
  } finally {
    rowExt._statusLoading = false
  }
}

// ==================== 对话框关闭 ====================
function handleDialogClosed() {
  resetForm()
  formRef.value?.clearValidate()
}

// ==================== 初始化 ====================
onMounted(() => {
  loadMenuTree()
})
</script>

<style lang="scss" scoped>
.menu-page {
  .page-header {
    margin-bottom: 20px;

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
  }

  .table-card {
    border-radius: 12px;

    :deep(.el-card__body) {
      padding: 20px 24px;
    }
  }

  .table-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;

    &__left,
    &__right {
      display: flex;
      align-items: center;
      gap: 10px;
    }
  }

  // ─── 树形表格 ─────────────────────────────────
  .menu-table {
    width: 100%;

    :deep(.el-table__header th) {
      font-weight: 600;
      color: var(--text-primary);
    }

    :deep(.el-table__row) {
      .el-button {
        padding: 0 4px;
      }
    }

    &__icon-placeholder {
      color: #c0c4cc;
    }

    &__empty {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;
      padding: 40px 0;

      p {
        margin: 0;
        font-size: 14px;
        color: var(--text-secondary);
      }
    }
  }

  // ─── 对话框 ──────────────────────────────────
  .menu-dialog {
    :deep(.el-dialog__body) {
      padding-top: 20px;
      padding-bottom: 10px;
    }
  }

  .menu-form {
    &__tree-select {
      width: 100%;
    }

    &__type-hint {
      margin-left: 12px;
      font-size: 12px;
      color: var(--text-secondary);
    }

    .el-form-item:last-child {
      margin-bottom: 0;
    }
  }
}
</style>
