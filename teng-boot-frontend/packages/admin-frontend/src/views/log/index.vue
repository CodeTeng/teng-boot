<template>
  <div class="log-page">
    <!-- 页面标题 -->
    <div class="log-page__header">
      <h2 class="log-page__title">操作日志</h2>
      <p class="log-page__desc">查看系统操作记录和审计信息</p>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="log-page__search-card">
      <el-form :model="queryParams" inline class="log-page__search-form">
        <el-form-item label="用户名">
          <el-input
            v-model="queryParams.username"
            placeholder="请输入用户名"
            clearable
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select
            v-model="queryParams.operation"
            placeholder="请选择"
            clearable
            style="width: 140px"
          >
            <el-option label="全部" value="" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
            <el-option label="GET" value="GET" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作系统">
          <el-select
            v-model="queryParams.os"
            placeholder="请选择"
            clearable
            style="width: 130px"
          >
            <el-option label="全部" value="" />
            <el-option label="Windows" value="Windows" />
            <el-option label="macOS" value="macOS" />
            <el-option label="Linux" value="Linux" />
            <el-option label="Android" value="Android" />
            <el-option label="iOS" value="iOS" />
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

    <!-- 数据表格 -->
    <el-card shadow="never" class="log-page__table-card">
      <el-table
        :data="logList"
        v-loading="loading"
        stripe
        class="log-page__table"
      >
        <el-table-column label="序号" width="70" align="center">
          <template #default="{ $index }">
            {{ (queryParams.pageNo - 1) * queryParams.pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="value" label="操作描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="operationTagType(row.operation)" size="small" effect="light" round>
              {{ operationLabel(row.operation) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="url" label="请求路径" min-width="200" show-overflow-tooltip />
        <el-table-column label="耗时" width="100" align="center">
          <template #default="{ row }">
            <span>{{ row.costTime }} ms</span>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP 地址" width="150" />
        <el-table-column prop="os" label="操作系统" width="110" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
      </el-table>

      <div class="log-page__pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNo"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="loadLogList"
          @current-change="loadLogList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { listLogByPage } from '@teng-boot/shared/api/log'
import type { SysLog } from '@teng-boot/shared/types'

const loading = ref(false)
const logList = ref<SysLog[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  username: undefined as string | undefined,
  operation: undefined as string | undefined,
  os: undefined as string | undefined,
})

/** 操作类型对应的 tag 颜色 */
function operationTagType(operation: string): string {
  const map: Record<string, string> = {
    POST: 'success',
    PUT: 'warning',
    DELETE: 'danger',
    GET: 'info',
  }
  return map[operation] ?? 'info'
}

/** 操作类型显示文本 */
function operationLabel(operation: string): string {
  const map: Record<string, string> = {
    POST: '新增',
    PUT: '修改',
    DELETE: '删除',
    GET: '查询',
  }
  return map[operation] ?? operation
}

async function loadLogList() {
  loading.value = true
  try {
    const res = await listLogByPage({
      pageNo: queryParams.pageNo,
      pageSize: queryParams.pageSize,
      username: queryParams.username || undefined,
      operation: queryParams.operation || undefined,
      os: queryParams.os || undefined,
    })
    if (res.code === 200) {
      logList.value = res.data.list
      total.value = res.data.total
    }
  } catch {
    // 错误拦截器已处理
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.pageNo = 1
  loadLogList()
}

function handleReset() {
  queryParams.username = undefined
  queryParams.operation = undefined
  queryParams.os = undefined
  queryParams.pageNo = 1
  loadLogList()
}

onMounted(() => {
  loadLogList()
})
</script>

<style lang="scss" scoped>
.log-page {
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

  &__table-card {
    border-radius: 12px;

    :deep(.el-card__body) {
      padding: 20px 24px;
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
}
</style>
