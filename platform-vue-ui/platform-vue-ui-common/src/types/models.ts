/**
 * 任务模型
 */
export interface TaskModel {
  id: string;
  name: string;
  description: string;
  status: 'CREATED' | 'RUNNING' | 'PAUSED' | 'COMPLETED' | 'FAILED' | 'CANCELED';
  type: string;
  priority: number;
  cron: string;
  timeout: number;
  retryCount: number;
  maxRetryCount: number;
  createTime: string;
  updateTime: string;
  lastExecutionTime: string;
  nextExecutionTime: string;
  creator: string;
  executor: string;
  parameters: Record<string, any>;
  tags: string[];
}

/**
 * 任务执行记录
 */
export interface TaskExecutionModel {
  id: string;
  taskId: string;
  startTime: string;
  endTime: string;
  status: 'RUNNING' | 'COMPLETED' | 'FAILED' | 'CANCELED';
  result: string;
  error: string;
  executedBy: string;
  duration: number;
  nodeId: string;
}

/**
 * 用户模型
 */
export interface UserModel {
  id: string;
  username: string;
  email: string;
  phone: string;
  avatar: string;
  status: 'ACTIVE' | 'INACTIVE' | 'LOCKED';
  roles: string[];
  permissions: string[];
  createTime: string;
  updateTime: string;
  lastLoginTime: string;
  department: string;
  position: string;
}

/**
 * 日志模型
 */
export interface LogModel {
  id: string;
  timestamp: string;
  level: 'INFO' | 'WARN' | 'ERROR' | 'DEBUG';
  source: string;
  message: string;
  details: string;
  threadId: string;
  traceId: string;
  userId: string;
  ip: string;
  userAgent: string;
}

/**
 * 节点模型
 */
export interface NodeModel {
  id: string;
  name: string;
  status: 'ONLINE' | 'OFFLINE' | 'MAINTENANCE';
  ip: string;
  port: number;
  cpu: number;
  memory: number;
  disk: number;
  lastHeartbeat: string;
  startTime: string;
  tags: string[];
  region: string;
  metrics: Record<string, any>;
}

/**
 * 数据源模型
 */
export interface DataSourceModel {
  id: string;
  name: string;
  type: 'MYSQL' | 'POSTGRESQL' | 'ORACLE' | 'SQLSERVER' | 'MONGODB' | 'REDIS' | 'ELASTICSEARCH';
  url: string;
  username: string;
  status: 'ACTIVE' | 'INACTIVE' | 'ERROR';
  createTime: string;
  updateTime: string;
  lastTestTime: string;
  properties: Record<string, string>;
}
