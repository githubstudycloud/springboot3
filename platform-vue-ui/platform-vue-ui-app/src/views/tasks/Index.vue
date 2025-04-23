<template>
  <div class="task-manager-container">
    <!-- 操作栏 -->
    <div class="task-header">
      <el-row :gutter="20" justify="space-between" align="middle">
        <el-col :span="18">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="任务名称">
              <el-input v-model="searchForm.name" placeholder="请输入任务名称" clearable @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="任务状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="6" style="text-align: right">
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>创建任务
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 任务表格 -->
    <el-card shadow="never" class="task-table-card">
      <el-table
        v-loading="loading"
        :data="taskList"
        border
        stripe
        style="width: 100%"
        @row-click="handleRowClick"
      >
        <el-table-column prop="name" label="任务名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="type" label="任务类型" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cron" label="定时表达式" width="120" show-overflow-tooltip />
        <el-table-column prop="lastExecutionTime" label="最近执行时间" width="160" />
        <el-table-column prop="nextExecutionTime" label="下次执行时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'PAUSED' || row.status === 'CREATED' || row.status === 'FAILED'" 
              type="success" 
              size="small" 
              @click.stop="handleStart(row)"
            >
              启动
            </el-button>
            <el-button 
              v-if="row.status === 'RUNNING'" 
              type="warning" 
              size="small" 
              @click.stop="handleStop(row)"
            >
              暂停
            </el-button>
            <el-button type="primary" size="small" @click.stop="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定要删除该任务吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" size="small" @click.stop>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 创建/编辑任务对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogType === 'create' ? '创建任务' : '编辑任务'"
      width="650px"
    >
      <el-form 
        ref="taskFormRef"
        :model="taskForm"
        :rules="taskRules"
        label-width="100px"
        label-position="right"
      >
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="taskForm.name" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务类型" prop="type">
          <el-select v-model="taskForm.type" placeholder="请选择任务类型" style="width: 100%">
            <el-option v-for="item in taskTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-slider v-model="taskForm.priority" :min="1" :max="10" show-stops show-input />
        </el-form-item>
        <el-form-item label="定时表达式" prop="cron">
          <el-input v-model="taskForm.cron" placeholder="请输入Cron表达式" />
        </el-form-item>
        <el-form-item label="超时时间(秒)" prop="timeout">
          <el-input-number v-model="taskForm.timeout" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大重试次数" prop="maxRetryCount">
          <el-input-number v-model="taskForm.maxRetryCount" :min="0" :max="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="taskForm.description" 
            type="textarea" 
            rows="3" 
            placeholder="请输入任务描述" 
          />
        </el-form-item>
        <el-form-item label="参数" prop="parameters">
          <el-input 
            v-model="taskFormParametersStr" 
            type="textarea" 
            rows="5" 
            placeholder="请输入JSON格式的任务参数" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="formSubmitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { useTaskStore } from '../../store/modules/task';
import { TaskModel } from 'platform-vue-ui-common';
import type { FormInstance, FormRules } from 'element-plus';

// 路由实例
const router = useRouter();

// 任务存储
const taskStore = useTaskStore();

// 表格数据
const loading = computed(() => taskStore.loading);
const taskList = computed(() => taskStore.taskList);
const total = computed(() => taskStore.totalItems);
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索表单
const searchForm = reactive({
  name: '',
  status: ''
});

// 状态选项
const statusOptions = [
  { label: '已创建', value: 'CREATED' },
  { label: '运行中', value: 'RUNNING' },
  { label: '已暂停', value: 'PAUSED' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '失败', value: 'FAILED' },
  { label: '已取消', value: 'CANCELED' }
];

// 任务类型选项
const taskTypeOptions = [
  { label: '数据同步', value: 'DATA_SYNC' },
  { label: '数据处理', value: 'DATA_PROCESS' },
  { label: '系统维护', value: 'SYSTEM_MAINTENANCE' },
  { label: '报告生成', value: 'REPORT_GENERATION' },
  { label: '数据备份', value: 'DATA_BACKUP' }
];

// 对话框相关
const dialogVisible = ref(false);
const dialogType = ref<'create' | 'edit'>('create');
const taskFormRef = ref<FormInstance>();
const formSubmitting = ref(false);
const taskForm = reactive<Partial<TaskModel>>({
  name: '',
  description: '',
  type: '',
  priority: 5,
  cron: '',
  timeout: 60,
  maxRetryCount: 3,
  parameters: {}
});

// 表单校验规则
const taskRules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入任务名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择任务类型', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请设置优先级', trigger: 'change' }
  ],
  cron: [
    { required: true, message: '请输入Cron表达式', trigger: 'blur' }
  ]
});

// 参数字符串，用于表单输入
const taskFormParametersStr = computed({
  get: () => {
    try {
      return JSON.stringify(taskForm.parameters || {}, null, 2);
    } catch (error) {
      return '{}';
    }
  },
  set: (val: string) => {
    try {
      taskForm.parameters = JSON.parse(val);
    } catch (error) {
      ElMessage.warning('JSON格式错误，请检查');
    }
  }
});

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  const map: Record<string, string> = {
    'CREATED': 'info',
    'RUNNING': 'success',
    'PAUSED': 'warning',
    'COMPLETED': '',
    'FAILED': 'danger',
    'CANCELED': 'info'
  };
  return map[status] || '';
};

// 获取状态标签文本
const getStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    'CREATED': '已创建',
    'RUNNING': '运行中',
    'PAUSED': '已暂停',
    'COMPLETED': '已完成',
    'FAILED': '失败',
    'CANCELED': '已取消'
  };
  return map[status] || status;
};

// 获取优先级标签类型
const getPriorityType = (priority: number) => {
  if (priority >= 8) return 'danger';
  if (priority >= 5) return 'warning';
  return 'info';
};

// 初始化数据
const initData = async () => {
  try {
    await taskStore.fetchTaskList(currentPage.value, pageSize.value, {
      name: searchForm.name,
      status: searchForm.status
    });
  } catch (error) {
    console.error('加载任务列表失败:', error);
    ElMessage.error('加载任务列表失败');
  }
};

// 重置表单
const resetTaskForm = () => {
  taskForm.id = undefined;
  taskForm.name = '';
  taskForm.description = '';
  taskForm.type = '';
  taskForm.priority = 5;
  taskForm.cron = '';
  taskForm.timeout = 60;
  taskForm.maxRetryCount = 3;
  taskForm.parameters = {};
};

// 提交表单
const submitForm = async () => {
  if (!taskFormRef.value) return;
  
  await taskFormRef.value.validate(async (valid) => {
    if (!valid) return;
    
    formSubmitting.value = true;
    
    try {
      if (dialogType.value === 'create') {
        await taskStore.createTask(taskForm);
        ElMessage.success('创建任务成功');
      } else {
        if (!taskForm.id) {
          ElMessage.error('任务ID不能为空');
          return;
        }
        await taskStore.updateTask(taskForm.id, taskForm);
        ElMessage.success('更新任务成功');
      }
      
      dialogVisible.value = false;
      await initData();
    } catch (error) {
      console.error(dialogType.value === 'create' ? '创建任务失败:' : '更新任务失败:', error);
      ElMessage.error(dialogType.value === 'create' ? '创建任务失败' : '更新任务失败');
    } finally {
      formSubmitting.value = false;
    }
  });
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  initData();
};

// 重置搜索
const resetSearch = () => {
  searchForm.name = '';
  searchForm.status = '';
  handleSearch();
};

// 创建任务
const handleCreate = () => {
  dialogType.value = 'create';
  resetTaskForm();
  dialogVisible.value = true;
};

// 编辑任务
const handleEdit = (row: TaskModel) => {
  dialogType.value = 'edit';
  
  // 复制任务数据到表单
  Object.keys(taskForm).forEach(key => {
    // @ts-ignore
    taskForm[key] = row[key];
  });
  
  dialogVisible.value = true;
};

// 删除任务
const handleDelete = async (row: TaskModel) => {
  try {
    await taskStore.deleteTask(row.id);
    ElMessage.success('删除任务成功');
    if (taskList.value.length === 1 && currentPage.value > 1) {
      currentPage.value--;
    }
    await initData();
  } catch (error) {
    console.error('删除任务失败:', error);
    ElMessage.error('删除任务失败');
  }
};

// 启动任务
const handleStart = async (row: TaskModel) => {
  try {
    await taskStore.startTask(row.id);
    ElMessage.success('启动任务成功');
    await initData();
  } catch (error) {
    console.error('启动任务失败:', error);
    ElMessage.error('启动任务失败');
  }
};

// 停止任务
const handleStop = async (row: TaskModel) => {
  try {
    await taskStore.stopTask(row.id);
    ElMessage.success('停止任务成功');
    await initData();
  } catch (error) {
    console.error('停止任务失败:', error);
    ElMessage.error('停止任务失败');
  }
};

// 行点击
const handleRowClick = (row: TaskModel) => {
  router.push(`/tasks/${row.id}`);
};

// 页码变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page;
  initData();
};

// 每页条数变化
const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
  initData();
};

// 组件挂载时初始化数据
onMounted(() => {
  initData();
});
</script>

<style scoped>
.task-manager-container {
  padding: 20px;
}

.task-header {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.task-table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
