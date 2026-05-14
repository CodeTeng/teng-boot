<template>
  <div class="profile-page">
    <div class="page-header">
      <h2 class="page-header__title">个人中心</h2>
    </div>

    <!-- 基本信息 -->
    <el-card shadow="never" class="profile-card">
      <template #header>
        <div class="card-header">
          <span class="card-header__title">基本信息</span>
          <el-button type="primary" @click="openEditDialog">
            <el-icon><Edit /></el-icon>
            编辑信息
          </el-button>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名" :span="1">
          {{ userInfo?.username ?? '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="真实姓名" :span="1">
          {{ userInfo?.userRealName ?? '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="性别" :span="1">
          {{ genderLabel(userInfo?.userGender) }}
        </el-descriptions-item>
        <el-descriptions-item label="年龄" :span="1">
          {{ userInfo?.userAge ?? '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号" :span="1">
          {{ userInfo?.userPhone ?? '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱" :span="1">
          {{ userInfo?.userEmail ?? '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="生日" :span="1">
          {{ userInfo?.userBirthday ?? '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="个人简介" :span="1">
          {{ userInfo?.userProfile ?? '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="角色" :span="1">
          <el-tag
            v-for="role in userInfo?.roles ?? []"
            :key="role"
            size="small"
            :type="role === 'admin' ? 'danger' : 'primary'"
            effect="plain"
            style="margin-right: 4px"
          >
            {{ role === 'admin' ? '管理员' : role === 'user' ? '普通用户' : role }}
          </el-tag>
          <span v-if="!userInfo?.roles?.length">-</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="1">
          {{ userInfo?.createTime ?? '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 修改密码 -->
    <el-card shadow="never" class="profile-card">
      <template #header>
        <div class="card-header">
          <span class="card-header__title">修改密码</span>
        </div>
      </template>
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
        style="max-width: 480px"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入旧密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="userPassword">
          <el-input
            v-model="passwordForm.userPassword"
            type="password"
            placeholder="请输入新密码（至少6位）"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPassword">
          <el-input
            v-model="passwordForm.checkPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="pwdLoading" @click="handleUpdatePwd">
            确认修改
          </el-button>
          <el-button @click="resetPasswordForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 编辑信息对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑个人信息"
      width="640px"
      :close-on-click-modal="false"
      @closed="resetEditForm"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="90px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="editForm.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="userRealName">
              <el-input v-model="editForm.userRealName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="userGender">
              <el-select v-model="editForm.userGender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="userAge">
              <el-input-number
                v-model="editForm.userAge"
                :min="0"
                :max="150"
                placeholder="请输入年龄"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="userPhone">
              <el-input v-model="editForm.userPhone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="userEmail">
              <el-input v-model="editForm.userEmail" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生日" prop="userBirthday">
              <el-date-picker
                v-model="editForm.userBirthday"
                type="date"
                placeholder="请选择生日"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="个人简介" prop="userProfile">
              <el-input
                v-model="editForm.userProfile"
                type="textarea"
                :rows="3"
                placeholder="请输入个人简介"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="handleUpdateUser">
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateMyUser, updateMyPwd } from '@teng-boot/shared'
import type { UserUpdateMyDTO, UserUpdatePwdDTO } from '@teng-boot/shared/types'

const userStore = useUserStore()
const router = useRouter()

const userInfo = computed(() => userStore.userInfo)

// ====== 性别标签 ======
function genderLabel(gender: number | undefined): string {
  if (gender === 1) return '男'
  if (gender === 0) return '女'
  return '-'
}

// ====== 编辑对话框 ======
const editDialogVisible = ref(false)
const editFormRef = ref<FormInstance>()
const editLoading = ref(false)

const editForm = reactive<UserUpdateMyDTO>({
  username: '',
  userPhone: '',
  userRealName: '',
  userGender: undefined,
  userAge: undefined,
  userEmail: '',
  userBirthday: '',
  userProfile: '',
})

const editRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  userPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  userEmail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
}

function openEditDialog() {
  const info = userInfo.value
  if (info) {
    editForm.username = info.username ?? ''
    editForm.userPhone = info.userPhone ?? ''
    editForm.userRealName = info.userRealName ?? ''
    editForm.userGender = info.userGender
    editForm.userAge = info.userAge
    editForm.userEmail = info.userEmail ?? ''
    editForm.userBirthday = info.userBirthday ?? ''
    editForm.userProfile = info.userProfile ?? ''
  }
  editDialogVisible.value = true
}

function resetEditForm() {
  editFormRef.value?.resetFields()
}

async function handleUpdateUser() {
  const valid = await editFormRef.value?.validate().catch(() => false)
  if (!valid) return

  editLoading.value = true
  try {
    const res = await updateMyUser({ ...editForm })
    if (res.code === 200) {
      ElMessage.success('个人信息修改成功')
      editDialogVisible.value = false
      await userStore.fetchUserInfo()
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } catch {
    ElMessage.error('修改失败，请稍后重试')
  } finally {
    editLoading.value = false
  }
}

// ====== 修改密码 ======
const passwordFormRef = ref<FormInstance>()
const pwdLoading = ref(false)

const passwordForm = reactive<UserUpdatePwdDTO>({
  oldPassword: '',
  userPassword: '',
  checkPassword: '',
})

const validateCheckPassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value !== passwordForm.userPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' },
  ],
  userPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码至少6位', trigger: 'blur' },
  ],
  checkPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateCheckPassword, trigger: 'blur' },
  ],
}

function resetPasswordForm() {
  passwordFormRef.value?.resetFields()
}

async function handleUpdatePwd() {
  const valid = await passwordFormRef.value?.validate().catch(() => false)
  if (!valid) return

  pwdLoading.value = true
  try {
    const res = await updateMyPwd({ ...passwordForm })
    if (res.code === 200) {
      ElMessage.success('密码修改成功，即将重新登录')
      resetPasswordForm()
      setTimeout(() => {
        userStore.logout()
        router.push('/login')
      }, 1500)
    } else {
      ElMessage.error(res.message || '密码修改失败')
    }
  } catch {
    ElMessage.error('密码修改失败，请稍后重试')
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.profile-page {
  .page-header {
    margin-bottom: 16px;

    &__title {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
    }
  }

  .profile-card {
    margin-bottom: 16px;

    :deep(.el-card__header) {
      padding: 16px 20px;
    }
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;

    &__title {
      font-size: 15px;
      font-weight: 600;
      color: var(--text-primary);
    }
  }
}
</style>
