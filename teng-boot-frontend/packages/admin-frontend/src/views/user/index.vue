<template>
  <div class="user-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-header__title">用户管理</h2>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" inline label-width="auto" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.userPhone" placeholder="请输入手机号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card shadow="never" class="table-card">
      <div class="table-toolbar">
        <div class="table-toolbar__left">
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
          <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>
        <div class="table-toolbar__right">
          <el-button @click="handleExport">
            <el-icon><Download /></el-icon>
            导出 Excel
          </el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
        row-key="id"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="序号" width="70" align="center">
          <template #default="{ $index }">
            {{ (queryParams.pageNo - 1) * queryParams.pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="userRole" label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.userRole === 'admin' ? 'danger' : 'primary'" size="small" effect="plain">
              {{ row.userRole === 'admin' ? '管理员' : row.userRole === 'user' ? '普通用户' : row.userRole }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userPhone" label="手机号" width="130" align="center" />
        <el-table-column prop="userGender" label="性别" width="70" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.userGender === 0" size="small" type="primary" effect="plain">M</el-tag>
            <el-tag v-else-if="row.userGender === 1" size="small" type="warning" effect="plain">F</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button type="warning" link size="small" @click="openPwdDialog(row)">
              修改密码
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="table-pagination">
        <el-pagination
          v-model:current-page="queryParams.pageNo"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.isEdit ? '编辑用户' : '新增用户'"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
        class="user-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="userRole">
              <el-select v-model="form.userRole" placeholder="请选择角色" style="width: 100%">
                <el-option
                  v-for="role in roleOptions"
                  :key="role.id"
                  :label="role.roleName"
                  :value="role.roleKey"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="userPhone">
              <el-input v-model="form.userPhone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="userRealName">
              <el-input v-model="form.userRealName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="userGender">
              <el-select v-model="form.userGender" placeholder="请选择性别" clearable style="width: 100%">
                <el-option label="男" :value="0" />
                <el-option label="女" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="userAge">
              <el-input-number v-model="form.userAge" :min="0" :max="150" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="userEmail">
              <el-input v-model="form.userEmail" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="简介" prop="userProfile">
              <el-input v-model="form.userProfile" placeholder="请输入简介" type="textarea" :rows="1" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="dialog.submitting" @click="submitForm">
          确认
        </el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="pwdDialog.visible"
      title="修改密码"
      width="420px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="pwdFormRef"
        :model="pwdForm"
        :rules="pwdFormRules"
        label-width="100px"
      >
        <el-form-item label="新密码" prop="userPassword">
          <el-input v-model="pwdForm.userPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPassword">
          <el-input v-model="pwdForm.checkPassword" type="password" placeholder="请确认新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="pwdDialog.submitting" @click="submitPwdForm">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download } from '@element-plus/icons-vue'
import { listUserByPage, addUser, updateUser, deleteUser, updateUserPwd, exportUser } from '@teng-boot/shared'
import type { UserInfo, UserQuery } from '@teng-boot/shared'
import { listAllRoles } from '@teng-boot/shared/api/role'
import type { RoleVO } from '@teng-boot/shared/api/role'

// ==================== 数据 ====================
const loading = ref(false)
const tableData = ref<UserInfo[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])
const roleOptions = ref<RoleVO[]>([])

const queryParams = reactive<UserQuery>({
  pageNo: 1,
  pageSize: 10,
  username: undefined,
  userPhone: undefined,
  status: undefined,
})

// ==================== 表单 ====================
const formRef = ref()
const form = reactive<Record<string, unknown>>({
  id: undefined,
  username: '',
  userRole: '',
  userPhone: '',
  userRealName: '',
  userGender: undefined,
  userAge: undefined,
  userEmail: '',
  userProfile: '',
})

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  userRole: [{ required: true, message: '请选择角色', trigger: 'change' }],
  userPhone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  userEmail: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
}

const dialog = reactive({
  visible: false,
  isEdit: false,
  submitting: false,
})

// ==================== 密码对话框 ====================
const pwdFormRef = ref()
const pwdDialog = reactive({
  visible: false,
  submitting: false,
  userId: 0,
  username: '',
})

const pwdForm = reactive({
  userPassword: '',
  checkPassword: '',
})

const pwdFormRules = {
  userPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度在 6 到 32 个字符', trigger: 'blur' },
  ],
  checkPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule: unknown, value: string, callback: (e?: Error) => void) => {
        if (value !== pwdForm.userPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
}

// ==================== 角色选项加载 ====================
const loadRoleOptions = async () => {
  try {
    const res = await listAllRoles()
    if (res.code === 200) {
      roleOptions.value = res.data
    }
  } catch {
    // 静默处理
  }
}

// ==================== 数据加载 ====================
const fetchData = async () => {
  loading.value = true
  try {
    const res = await listUserByPage({ ...queryParams })
    if (res.code === 200) {
      tableData.value = res.data.list
      total.value = res.data.total
    }
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}

// ==================== 搜索/重置 ====================
const handleSearch = () => {
  queryParams.pageNo = 1
  fetchData()
}

const handleReset = () => {
  queryParams.username = undefined
  queryParams.userPhone = undefined
  queryParams.status = undefined
  queryParams.pageNo = 1
  fetchData()
}

// ==================== 多选 ====================
const handleSelectionChange = (rows: UserInfo[]) => {
  selectedIds.value = rows.map(r => r.id)
}

// ==================== 新增 ====================
const openAddDialog = () => {
  dialog.isEdit = false
  form.id = undefined
  form.username = ''
  form.userRole = ''
  form.userPhone = ''
  form.userRealName = ''
  form.userGender = undefined
  form.userAge = undefined
  form.userEmail = ''
  form.userProfile = ''
  dialog.visible = true
}

// ==================== 编辑 ====================
const openEditDialog = (row: UserInfo) => {
  dialog.isEdit = true
  form.id = row.id
  form.username = row.username
  form.userRole = row.userRole
  form.userPhone = row.userPhone ?? ''
  form.userRealName = row.userRealName ?? ''
  form.userGender = row.userGender
  form.userAge = row.userAge
  form.userEmail = row.userEmail ?? ''
  form.userProfile = row.userProfile ?? ''
  dialog.visible = true
}

// ==================== 提交表单 ====================
const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  dialog.submitting = true
  try {
    const payload = { ...form }
    let res
    if (dialog.isEdit) {
      res = await updateUser(payload)
    } else {
      res = await addUser(payload)
    }
    if (res.code === 200 && res.data) {
      ElMessage.success(dialog.isEdit ? '更新成功' : '新增成功')
      dialog.visible = false
      fetchData()
    }
  } finally {
    dialog.submitting = false
  }
}

// ==================== 删除 ====================
const handleDelete = (row: UserInfo) => {
  ElMessageBox.confirm(`确认删除用户「${row.username}」吗？`, '确认删除', {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消',
  }).then(async () => {
    try {
      const res = await deleteUser([row.id])
      if (res.code === 200 && res.data) {
        ElMessage.success('删除成功')
        fetchData()
      }
    } catch {
      // 错误已在拦截器中处理
    }
  }).catch(() => {
    // 取消删除
  })
}

// ==================== 批量删除 ====================
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确认删除选中的 ${selectedIds.value.length} 个用户吗？`, '确认批量删除', {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消',
  }).then(async () => {
    try {
      const res = await deleteUser(selectedIds.value)
      if (res.code === 200 && res.data) {
        ElMessage.success('批量删除成功')
        selectedIds.value = []
        fetchData()
      }
    } catch {
      // 错误已在拦截器中处理
    }
  }).catch(() => {
    // 取消删除
  })
}

// ==================== 修改密码 ====================
const openPwdDialog = (row: UserInfo) => {
  pwdDialog.userId = row.id
  pwdDialog.username = row.username
  pwdForm.userPassword = ''
  pwdForm.checkPassword = ''
  pwdDialog.visible = true
}

const submitPwdForm = async () => {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return

  pwdDialog.submitting = true
  try {
    const res = await updateUserPwd(pwdDialog.userId, {
      userPassword: pwdForm.userPassword,
      checkPassword: pwdForm.checkPassword,
    })
    if (res.code === 200 && res.data) {
      ElMessage.success('密码修改成功')
      pwdDialog.visible = false
    }
  } finally {
    pwdDialog.submitting = false
  }
}

// ==================== 导出 Excel ====================
const handleExport = async () => {
  try {
    const blob = await exportUser({
      pageNo: queryParams.pageNo,
      pageSize: queryParams.pageSize,
      username: queryParams.username,
      userPhone: queryParams.userPhone,
      status: queryParams.status,
    })
    // blob 可能是 Blob 类型
    const blobData = blob as unknown as Blob
    const url = window.URL.createObjectURL(blobData)
    const a = document.createElement('a')
    a.href = url
    a.download = `用户数据_${new Date().toISOString().slice(0, 10)}.xlsx`
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
    ElMessage.success('导出成功')
  } catch {
    ElMessage.error('导出失败')
  }
}

// ==================== 初始化 ====================
onMounted(() => {
  loadRoleOptions()
  fetchData()
})
</script>

<style lang="scss" scoped>
.user-page {
  .page-header {
    margin-bottom: 20px;

    &__title {
      font-family: 'DM Serif Display', serif;
      font-size: 24px;
      font-weight: 400;
      color: var(--text-primary);
      margin: 0;
    }
  }

  .search-card {
    border-radius: 12px;
    margin-bottom: 20px;

    :deep(.el-card__body) {
      padding: 20px 20px 0;
    }
  }

  .search-form {
    :deep(.el-form-item) {
      margin-bottom: 20px;
    }
  }

  .table-card {
    border-radius: 12px;

    :deep(.el-card__body) {
      padding: 20px;
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

  .table-pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }

  .user-form {
    padding: 10px 0;
  }
}
</style>
