<template>
  <div class="role">
    <!-- 页面标题 -->
    <div class="role__header">
      <h2 class="role__title">角色管理</h2>
      <p class="role__desc">管理系统中的角色和权限分配</p>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="role__search-card">
      <el-form :model="queryParams" inline class="role__search-form">
        <el-form-item label="角色名称">
          <el-input
            v-model="queryParams.roleName"
            placeholder="请输入角色名称"
            clearable
            class="role__search-input"
          />
        </el-form-item>
        <el-form-item label="角色标识">
          <el-input
            v-model="queryParams.roleKey"
            placeholder="请输入角色标识"
            clearable
            class="role__search-input"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            class="role__search-input"
          >
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery" class="role__search-btn">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset" class="role__search-btn">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 + 表格 -->
    <el-card shadow="never" class="role__table-card">
      <div class="role__toolbar">
        <el-button type="primary" @click="handleAdd" class="role__add-btn">
          <el-icon><Plus /></el-icon>
          新增角色
        </el-button>
      </div>

      <el-table
        :data="roleList"
        v-loading="loading"
        stripe
        class="role__table"
      >
        <el-table-column label="序号" width="70" align="center">
          <template #default="{ $index }">
            {{ (queryParams.pageNo - 1) * queryParams.pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="roleName" label="角色名称" min-width="140" />
        <el-table-column prop="roleKey" label="角色标识" min-width="140" />
        <el-table-column prop="roleSort" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="light" round>
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column prop="creatorName" label="创建人" width="100" />
        <el-table-column prop="updaterName" label="更新人" width="100" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              @click="handleDetail(row)"
            >
              详情
            </el-button>
            <el-button
              type="warning"
              link
              size="small"
              @click="handleAssignMenu(row)"
            >
              分配菜单
            </el-button>
            <el-popconfirm
              title="确定要删除此角色吗？"
              @confirm="handleDelete(row.id)"
            >
              <template #reference>
                <el-button type="danger" link size="small">
                  删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="role__pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNo"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
          :locale="{ total: `共 ${total} 项`, pageLabel: '第', goto: '前往', elevator: '页', pageSuffix: '页' }"
        />
      </div>
    </el-card>

    <!-- 新增 / 编辑角色对话框 -->
    <el-dialog
      v-model="formDialogVisible"
      :title="isEditing ? '编辑角色' : '新增角色'"
      width="540px"
      destroy-on-close
      class="role__form-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="90px"
        class="role__form"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="formData.roleKey" placeholder="请输入角色标识" />
        </el-form-item>
        <el-form-item label="排序" prop="roleSort">
          <el-input-number v-model="formData.roleSort" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-tooltip
            :content="hasMenus ? '该角色下已分配菜单，无法禁用' : ''"
            :disabled="!hasMenus"
          >
            <el-switch
              v-model="formData.status"
              :active-value="1"
              :inactive-value="0"
              active-text="正常"
              inactive-text="停用"
              :disabled="hasMenus"
            />
          </el-tooltip>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确认
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="角色详情"
      width="600px"
      destroy-on-close
      class="role__detail-dialog"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="角色名称">{{ detailData.roleName }}</el-descriptions-item>
        <el-descriptions-item label="角色标识">{{ detailData.roleKey }}</el-descriptions-item>
        <el-descriptions-item label="排序">{{ detailData.roleSort }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'danger'" effect="light" round>
            {{ detailData.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ detailData.creatorName || detailData.creater || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新人">{{ detailData.updaterName || detailData.updater || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 分配菜单对话框 -->
    <el-dialog
      v-model="menuDialogVisible"
      title="分配菜单权限"
      width="420px"
      destroy-on-close
      class="role__menu-dialog"
    >
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        show-checkbox
        node-key="id"
        :props="{ label: 'menuName', children: 'children' }"
        default-expand-all
        highlight-current
        class="role__menu-tree"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="menuSubmitting" @click="handleMenuSubmit">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import {
  listRoleByPage,
  addRole,
  updateRole,
  deleteRole,
  getRoleMenuIds,
  assignRoleMenus,
  getMenuTree,
} from '@teng-boot/shared/api/role'
import type {
  RoleVO,
  RoleAddDTO,
  RoleUpdateDTO,
  MenuTreeNode,
} from '@teng-boot/shared/api/role'

// ─── 数据状态 ──────────────────────────────────────────────
const roleList = ref<RoleVO[]>([])
const total = ref(0)
const loading = ref(false)

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  roleName: undefined as string | undefined,
  roleKey: undefined as string | undefined,
  status: undefined as number | undefined,
})

// ─── 生命周期 ──────────────────────────────────────────────
onMounted(() => {
  loadRoleList()
})

// ─── 数据加载 ──────────────────────────────────────────────
async function loadRoleList() {
  loading.value = true
  try {
    const res = await listRoleByPage(queryParams)
    roleList.value = res.data.list
    total.value = res.data.total
  } catch {
    // 响应拦截器已经处理了错误提示
  } finally {
    loading.value = false
  }
}

// ─── 搜索 / 重置 ──────────────────────────────────────────
function handleQuery() {
  queryParams.pageNo = 1
  loadRoleList()
}

function handleReset() {
  queryParams.roleName = undefined
  queryParams.roleKey = undefined
  queryParams.status = undefined
  queryParams.pageNo = 1
  loadRoleList()
}

// ─── 分页 ──────────────────────────────────────────────────
function handleSizeChange() {
  queryParams.pageNo = 1
  loadRoleList()
}

function handleCurrentChange() {
  loadRoleList()
}

// ─── 新增 / 编辑 对话框 ──────────────────────────────────
const formDialogVisible = ref(false)
const isEditing = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const hasMenus = ref(false)

const defaultFormData = (): RoleAddDTO => ({
  roleName: '',
  roleKey: '',
  roleSort: 0,
  status: 1,
  remark: '',
})

const formData = reactive<RoleAddDTO>(defaultFormData())
const resetFormData = () => Object.assign(formData, defaultFormData())

const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入角色标识', trigger: 'blur' }],
}

function handleAdd() {
  isEditing.value = false
  editingId.value = null
  hasMenus.value = false
  resetFormData()
  formDialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function handleEdit(row: RoleVO) {
  isEditing.value = true
  editingId.value = row.id
  formData.roleName = row.roleName
  formData.roleKey = row.roleKey
  formData.roleSort = row.roleSort
  formData.status = row.status
  formData.remark = row.remark ?? ''

  // 检查该角色是否已分配菜单，如有则禁用状态开关
  try {
    const menuIdsRes = await getRoleMenuIds(row.id)
    hasMenus.value = Array.isArray(menuIdsRes.data) && menuIdsRes.data.length > 0
  } catch {
    hasMenus.value = false
  }

  formDialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEditing.value && editingId.value !== null) {
      const payload: RoleUpdateDTO = {
        id: editingId.value,
        ...(formData as RoleAddDTO),
      }
      await updateRole(payload)
      ElMessage.success('角色更新成功')
    } else {
      await addRole(formData as RoleAddDTO)
      ElMessage.success('角色新增成功')
    }
    formDialogVisible.value = false
    loadRoleList()
  } catch {
    // 响应拦截器已经处理了错误提示
  } finally {
    submitting.value = false
  }
}

// ─── 删除 ──────────────────────────────────────────────────
async function handleDelete(id: number) {
  try {
    await deleteRole(id)
    ElMessage.success('角色删除成功')
    loadRoleList()
  } catch {
    // 响应拦截器已经处理了错误提示
  }
}

// ─── 详情对话框 ──────────────────────────────────────────
const detailDialogVisible = ref(false)
const detailData = reactive<RoleVO>({
  id: 0,
  roleName: '',
  roleKey: '',
  roleSort: 0,
  status: 1,
})

function handleDetail(row: RoleVO) {
  Object.assign(detailData, row)
  detailDialogVisible.value = true
}

// ─── 分配菜单 ──────────────────────────────────────────────
const menuDialogVisible = ref(false)
const menuTree = ref<MenuTreeNode[]>([])
const menuTreeRef = ref()
const currentRoleId = ref<number | null>(null)
const menuSubmitting = ref(false)

async function handleAssignMenu(row: RoleVO) {
  currentRoleId.value = row.id
  menuDialogVisible.value = true

  // 并行加载菜单树和已分配的菜单 ID
  try {
    const [treeRes, menuIdsRes] = await Promise.all([
      getMenuTree(),
      getRoleMenuIds(row.id),
    ])
    menuTree.value = treeRes.data
    const checkedMenuIds = menuIdsRes.data || []

    // 等待 DOM 渲染后设置勾选
    nextTick(() => {
      if (menuTreeRef.value) {
        // 先设置叶子节点
        menuTreeRef.value.setCheckedKeys(checkedMenuIds)
        // 然后处理父节点：如果某个父节点的所有子节点都被选中，则该父节点也应设为勾选
        setParentNodesChecked(menuTree.value, checkedMenuIds)
      }
    })
  } catch {
    // 响应拦截器已经处理了错误提示
  }
}

/**
 * 递归设置父节点的勾选状态
 * 如果某父节点的所有子节点都被选中，则该父节点也应设为勾选状态
 */
function setParentNodesChecked(
  nodes: MenuTreeNode[],
  checkedIds: number[],
) {
  if (!menuTreeRef.value) return

  for (const node of nodes) {
    if (node.children && node.children.length > 0) {
      // 检查该节点的所有子节点是否都在 checkedIds 中
      const childIds = node.children.map((child) => child.id)
      const allChildrenChecked = childIds.every((id) =>
        checkedIds.includes(id),
      )
      // 如果所有子节点都被选中，则设置父节点为勾选状态
      if (allChildrenChecked) {
        menuTreeRef.value.setChecked(node.id, true, false)
      }
      // 递归处理子节点
      setParentNodesChecked(node.children, checkedIds)
    }
  }
}

async function handleMenuSubmit() {
  if (currentRoleId.value === null) return

  menuSubmitting.value = true
  try {
    const checkedKeys = menuTreeRef.value?.getCheckedKeys() ?? []
    const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() ?? []
    const allKeys = [...new Set([...checkedKeys, ...halfCheckedKeys])]
    await assignRoleMenus(currentRoleId.value, allKeys)
    ElMessage.success('菜单权限分配成功')
    menuDialogVisible.value = false
  } catch {
    // 响应拦截器已经处理了错误提示
  } finally {
    menuSubmitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.role {
  // ─── 页面标题 ─────────────────────────────────────────
  &__header {
    margin-bottom: 20px;
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

  // ─── 搜索栏卡片 ───────────────────────────────────────
  &__search-card {
    border-radius: 12px;
    margin-bottom: 20px;

    :deep(.el-card__body) {
      padding: 20px 24px;
    }
  }

  &__search-form {
    display: flex;
    flex-wrap: wrap;
    align-items: flex-start;

    .el-form-item {
      margin-bottom: 0;
      margin-right: 16px;
    }
  }

  &__search-input {
    width: 180px;
  }

  &__search-btn {
    .el-icon {
      margin-right: 4px;
    }
  }

  // ─── 表格卡片 ─────────────────────────────────────────
  &__table-card {
    border-radius: 12px;

    :deep(.el-card__body) {
      padding: 20px 24px;
    }
  }

  &__toolbar {
    margin-bottom: 16px;
    display: flex;
    align-items: center;
  }

  &__add-btn {
    .el-icon {
      margin-right: 4px;
    }
  }

  &__table {
    width: 100%;

    :deep(.el-table__header th) {
      font-weight: 600;
      color: var(--text-primary);
    }
  }

  &__pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  // ─── 表单对话框 ───────────────────────────────────────
  &__form-dialog {
    :deep(.el-dialog__body) {
      padding-top: 20px;
    }
  }

  &__form {
    .el-form-item:last-child {
      margin-bottom: 0;
    }
  }

  // ─── 菜单树对话框 ─────────────────────────────────────
  &__menu-dialog {
    :deep(.el-dialog__body) {
      padding-top: 16px;
      max-height: 500px;
      overflow-y: auto;
    }
  }

  &__menu-tree {
    .el-tree-node__content {
      height: 34px;
    }
  }
}
</style>
