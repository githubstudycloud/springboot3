import { defineStore } from 'pinia';
import { TaskModel, TaskService, PageResult } from 'platform-vue-ui-common';

interface TaskState {
  taskList: TaskModel[];
  currentTask: TaskModel | null;
  loading: boolean;
  totalItems: number;
  pageSize: number;
  currentPage: number;
}

export const useTaskStore = defineStore('task', {
  state: (): TaskState => ({
    taskList: [],
    currentTask: null,
    loading: false,
    totalItems: 0,
    pageSize: 10,
    currentPage: 1
  }),
  
  getters: {
    getTaskById: (state) => (id: string): TaskModel | undefined => {
      return state.taskList.find(task => task.id === id);
    },
    getTasksByStatus: (state) => (status: string): TaskModel[] => {
      return state.taskList.filter(task => task.status === status);
    }
  },
  
  actions: {
    // 获取任务列表
    async fetchTaskList(page = 1, size = 10, params = {}) {
      this.loading = true;
      
      try {
        const response = await TaskService.getTaskList({
          page,
          size,
          ...params
        });
        
        const result = response as unknown as PageResult<TaskModel>;
        
        this.taskList = result.content;
        this.totalItems = result.totalElements;
        this.currentPage = page;
        this.pageSize = size;
        
        return result;
      } catch (error) {
        console.error('获取任务列表失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // 获取任务详情
    async fetchTaskDetail(taskId: string) {
      this.loading = true;
      
      try {
        const task = await TaskService.getTaskDetail(taskId);
        this.currentTask = task;
        return task;
      } catch (error) {
        console.error('获取任务详情失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // 创建任务
    async createTask(taskData: Partial<TaskModel>) {
      this.loading = true;
      
      try {
        const task = await TaskService.createTask(taskData);
        // 刷新任务列表
        await this.fetchTaskList(this.currentPage, this.pageSize);
        return task;
      } catch (error) {
        console.error('创建任务失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // 更新任务
    async updateTask(taskId: string, taskData: Partial<TaskModel>) {
      this.loading = true;
      
      try {
        const task = await TaskService.updateTask(taskId, taskData);
        
        // 更新本地任务列表
        const index = this.taskList.findIndex(t => t.id === taskId);
        if (index !== -1) {
          this.taskList[index] = { ...this.taskList[index], ...task };
        }
        
        // 如果是当前查看的任务，更新当前任务
        if (this.currentTask && this.currentTask.id === taskId) {
          this.currentTask = { ...this.currentTask, ...task };
        }
        
        return task;
      } catch (error) {
        console.error('更新任务失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // 删除任务
    async deleteTask(taskId: string) {
      this.loading = true;
      
      try {
        await TaskService.deleteTask(taskId);
        
        // 从本地任务列表移除
        this.taskList = this.taskList.filter(task => task.id !== taskId);
        
        // 如果删除的是当前任务，清空当前任务
        if (this.currentTask && this.currentTask.id === taskId) {
          this.currentTask = null;
        }
        
        return true;
      } catch (error) {
        console.error('删除任务失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // 启动任务
    async startTask(taskId: string) {
      try {
        await TaskService.startTask(taskId);
        
        // 更新任务状态
        const index = this.taskList.findIndex(task => task.id === taskId);
        if (index !== -1) {
          this.taskList[index] = { ...this.taskList[index], status: 'RUNNING' };
        }
        
        // 如果是当前查看的任务，更新当前任务
        if (this.currentTask && this.currentTask.id === taskId) {
          this.currentTask = { ...this.currentTask, status: 'RUNNING' };
        }
        
        return true;
      } catch (error) {
        console.error('启动任务失败:', error);
        throw error;
      }
    },
    
    // 停止任务
    async stopTask(taskId: string) {
      try {
        await TaskService.stopTask(taskId);
        
        // 更新任务状态
        const index = this.taskList.findIndex(task => task.id === taskId);
        if (index !== -1) {
          this.taskList[index] = { ...this.taskList[index], status: 'PAUSED' };
        }
        
        // 如果是当前查看的任务，更新当前任务
        if (this.currentTask && this.currentTask.id === taskId) {
          this.currentTask = { ...this.currentTask, status: 'PAUSED' };
        }
        
        return true;
      } catch (error) {
        console.error('停止任务失败:', error);
        throw error;
      }
    }
  }
});
