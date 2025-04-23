import request from '../request';
import { TaskModel, QueryParams, PageResult } from '../../types';

export class TaskService {
  /**
   * 获取任务列表
   * @param params 分页和过滤参数
   * @returns 任务分页数据
   */
  static getTaskList(params: QueryParams): Promise<PageResult<TaskModel>> {
    return request.get('/tasks', { params });
  }

  /**
   * 获取任务详情
   * @param taskId 任务ID
   * @returns 任务详情
   */
  static getTaskDetail(taskId: string): Promise<TaskModel> {
    return request.get(`/tasks/${taskId}`);
  }

  /**
   * 创建任务
   * @param data 任务数据
   * @returns 创建结果
   */
  static createTask(data: Partial<TaskModel>): Promise<TaskModel> {
    return request.post('/tasks', data);
  }

  /**
   * 更新任务
   * @param taskId 任务ID
   * @param data 任务数据
   * @returns 更新结果
   */
  static updateTask(taskId: string, data: Partial<TaskModel>): Promise<TaskModel> {
    return request.put(`/tasks/${taskId}`, data);
  }

  /**
   * 删除任务
   * @param taskId 任务ID
   * @returns 操作结果
   */
  static deleteTask(taskId: string): Promise<void> {
    return request.delete(`/tasks/${taskId}`);
  }

  /**
   * 启动任务
   * @param taskId 任务ID
   * @returns 操作结果
   */
  static startTask(taskId: string): Promise<void> {
    return request.post(`/tasks/${taskId}/start`);
  }

  /**
   * 停止任务
   * @param taskId 任务ID
   * @returns 操作结果
   */
  static stopTask(taskId: string): Promise<void> {
    return request.post(`/tasks/${taskId}/stop`);
  }

  /**
   * 获取任务执行日志
   * @param taskId 任务ID
   * @param executionId 执行ID
   * @returns 任务日志
   */
  static getTaskLogs(taskId: string, executionId: string): Promise<string[]> {
    return request.get(`/tasks/${taskId}/executions/${executionId}/logs`);
  }
}
