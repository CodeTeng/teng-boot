<template>
  <div class="file-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-header__title">文件管理</h2>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" inline label-width="auto" class="search-form">
        <el-form-item label="文件名">
          <el-input v-model="queryParams.fileName" placeholder="请输入文件名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="文件类型">
          <el-input v-model="queryParams.fileType" placeholder="请输入文件类型" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-input v-model="queryParams.bizType" placeholder="请输入业务类型" clearable style="width: 140px" />
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
    <el-card shadow="never" class="table-card">
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        style="width: 100%"
        row-key="id"
      >
        <el-table-column label="序号" width="70" align="center">
          <template #default="{ $index }">
            {{ (queryParams.pageNo - 1) * queryParams.pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="文件名" min-width="240" show-overflow-tooltip>
          <template #default="{ row }">
            <span
              v-if="isImage(row.fileType)"
              class="file-link"
              @click="handlePreview(row)"
            >
              {{ row.fileName }}
            </span>
            <a v-else :href="row.fileUrl" target="_blank" class="file-link">
              {{ row.fileName }}
            </a>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="文件大小" width="120" align="center">
          <template #default="{ row }">
            {{ formatSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="fileType" label="文件类型" width="110" align="center" />
        <el-table-column prop="bizType" label="业务类型" width="110" align="center" />
        <el-table-column prop="createTime" label="上传时间" width="180" align="center" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
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

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="preview.visible"
      :url-list="preview.urlList"
      @close="preview.visible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElImageViewer } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { listFileByPage, deleteFile } from '@teng-boot/shared'
import type { FileRecordVO, FileQuery } from '@teng-boot/shared'

// ==================== 数据 ====================
const loading = ref(false)
const tableData = ref<FileRecordVO[]>([])
const total = ref(0)

const queryParams = reactive<FileQuery>({
  pageNo: 1,
  pageSize: 10,
  fileName: undefined,
  fileType: undefined,
  bizType: undefined,
})

// ==================== 图片预览 ====================
const preview = reactive({
  visible: false,
  urlList: [''] as string[],
})

const IMAGE_TYPES = ['png', 'jpg', 'jpeg', 'webp', 'svg', 'gif', 'bmp']

const isImage = (fileType: string): boolean => {
  return IMAGE_TYPES.includes(fileType.toLowerCase())
}

const handlePreview = (row: FileRecordVO) => {
  preview.urlList = [row.fileUrl]
  preview.visible = true
}

// ==================== 文件大小格式化 ====================
const formatSize = (bytes: number): string => {
  if (bytes <= 0) return '0 B'
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

// ==================== 数据加载 ====================
const fetchData = async () => {
  loading.value = true
  try {
    const res = await listFileByPage({ ...queryParams })
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
  queryParams.fileName = undefined
  queryParams.fileType = undefined
  queryParams.bizType = undefined
  queryParams.pageNo = 1
  fetchData()
}

// ==================== 删除 ====================
const handleDelete = (row: FileRecordVO) => {
  ElMessageBox.confirm(`确认删除文件「${row.fileName}」吗？`, '确认删除', {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消',
  }).then(async () => {
    try {
      const res = await deleteFile(row.id)
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

// ==================== 初始化 ====================
onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.file-page {
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

  .table-pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }

  .file-link {
    color: var(--el-color-primary);
    cursor: pointer;
    text-decoration: none;
    transition: color 0.2s;

    &:hover {
      color: var(--el-color-primary-dark-2);
      text-decoration: underline;
    }
  }
}
</style>
