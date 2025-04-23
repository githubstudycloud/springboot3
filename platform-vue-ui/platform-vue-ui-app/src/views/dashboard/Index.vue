<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :xs="24" :sm="12" :md="12" :lg="6" :xl="6" v-for="(item, index) in statsCards" :key="index">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-content">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-title">{{ item.title }}</div>
          </div>
          <div class="stat-icon">
            <el-icon><component :is="item.icon"></component></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="dashboard-charts">
      <!-- 任务状态统计 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card header="任务状态统计">
          <div class="chart-container">
            <!-- 此处可以集成图表组件，如 ECharts -->
            <div class="placeholder-chart">任务状态图表</div>
          </div>
        </el-card>
      </el-col>

      <!-- 系统资源使用情况 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card header="系统资源使用情况">
          <div class="chart-container">
            <!-- 此处可以集成图表组件，如 ECharts -->
            <div class="placeholder-chart">系统资源图表</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="dashboard-lists">
      <!-- 最近任务 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card header="最近任务">
          <el-table :data="recentTasks" style="width: 100%" stripe>
            <el-table-column prop="name" label="任务名称" min-width="150" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lastExecutionTime" label="最近执行时间" width="160" />
          </el-table>
        </el-card>
      </el-col>

      <!-- 系统日志 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card header="系统日志">
          <el-table :data="systemLogs" style="width: 100%" stripe>
            <el-table-column prop="timestamp" label="时间" width="160" />
            <el-table-column prop="level" label="级别" width="80">
              <template #default="scope">
                <el-tag :type="getLogLevelType(scope.row.level)">{{ scope.row.level }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="message" label="内容" min-width="150" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts">
export default {
  name: 'Dashboard'
}
</script>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

// 统计卡片数据
const statsCards = ref([
  { title: '总任务数', value: 128, icon: 'Document' },
  { title: '运行中任务', value: 23, icon: 'VideoPlay' },
  { title: '节点数量', value: 12, icon: 'Connection' },
  { title: '告警数', value: 5, icon: 'Warning' }
]);

// 最近任务数据
const recentTasks = ref([
  { name: '数据库备份任务', status: 'COMPLETED', lastExecutionTime: '2025-04-23 08:30:00' },
  { name: '日志清理任务', status: 'RUNNING', lastExecutionTime: '2025-04-23 09:00:00' },
  { name: '系统状态检查', status: 'COMPLETED', lastExecutionTime: '2025-04-23 07:15:00' },
  { name: '数据同步任务', status: 'FAILED', lastExecutionTime: '2025-04-23 06:45:00' },
  { name: '性能分析任务', status: 'PAUSED', lastExecutionTime: '2025-04-22 23:30:00' }
]);

// 系统日志数据
const systemLogs = ref([
  { timestamp: '2025-04-23 09:15:23', level: 'INFO', message: '系统启动成功' },
  { timestamp: '2025-04-23 09:10:45', level: 'WARN', message: '节点 node-03 连接超时' },
  { timestamp: '2025-04-23 09:05:12', level: 'ERROR', message: '数据库连接失败' },
  { timestamp: '2025-04-23 09:01:33', level: 'INFO', message: '用户 admin 登录成功' },
  { timestamp: '2025-04-23 09:00:00', level: 'INFO', message: '定时任务开始执行' }
]);

// 获取任务状态对应的标签类型
const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    'RUNNING': 'success',
    'COMPLETED': 'info',
    'FAILED': 'danger',
    'PAUSED': 'warning',
    'CANCELED': 'info'
  };
  return map[status] || '';
};

// 获取日志级别对应的标签类型
const getLogLevelType = (level: string) => {
  const map: Record<string, string> = {
    'INFO': 'info',
    'WARN': 'warning',
    'ERROR': 'danger',
    'DEBUG': ''
  };
  return map[level] || '';
};

// 页面加载时从API获取数据
onMounted(() => {
  // 这里可以调用API获取实际数据
  // 示例代码仅做演示
});
</script>

<style scoped>
.dashboard-container {
  padding: 20px 0;
}

.stat-card {
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
  height: 120px;
  border-radius: 8px;
}

.stat-card-content {
  position: relative;
  z-index: 2;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-title {
  font-size: 16px;
  color: #909399;
}

.stat-icon {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 48px;
  opacity: 0.2;
  color: #909399;
}

.dashboard-charts {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-chart {
  color: #909399;
  font-size: 16px;
  background-color: #f5f7fa;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
}

.dashboard-lists {
  margin-bottom: 20px;
}
</style>
