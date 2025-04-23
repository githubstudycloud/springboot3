<template>
  <div class="data-table-container">
    <el-table
      v-bind="$attrs"
      :data="data"
      :border="border"
      :stripe="stripe"
      :height="height"
      :max-height="maxHeight"
      :size="size"
      v-loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column v-if="selection" type="selection" width="55" />
      <el-table-column v-if="showIndex" type="index" width="70" label="序号" />
      
      <slot></slot>
      
      <template #empty>
        <slot name="empty">
          <el-empty description="暂无数据" />
        </slot>
      </template>
    </el-table>
    
    <div v-if="pagination" class="data-table-pagination">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        :page-sizes="pageSizes"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: 'DataTable'
}
</script>

<script setup lang="ts">
import { computed, ref } from 'vue'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  border: {
    type: Boolean,
    default: true
  },
  stripe: {
    type: Boolean,
    default: true
  },
  selection: {
    type: Boolean,
    default: false
  },
  showIndex: {
    type: Boolean,
    default: false
  },
  height: {
    type: [String, Number],
    default: ''
  },
  maxHeight: {
    type: [String, Number],
    default: ''
  },
  size: {
    type: String,
    default: 'default'
  },
  loading: {
    type: Boolean,
    default: false
  },
  pagination: {
    type: Boolean,
    default: true
  },
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  },
  total: {
    type: Number,
    default: 0
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  }
})

const emit = defineEmits([
  'update:currentPage',
  'update:pageSize',
  'selection-change',
  'page-change',
  'size-change'
])

const handleSelectionChange = (selection: any[]) => {
  emit('selection-change', selection)
}

const handleSizeChange = (size: number) => {
  emit('update:pageSize', size)
  emit('size-change', size)
}

const handleCurrentChange = (page: number) => {
  emit('update:currentPage', page)
  emit('page-change', page)
}
</script>

<style scoped>
.data-table-container {
  width: 100%;
}

.data-table-pagination {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}
</style>
