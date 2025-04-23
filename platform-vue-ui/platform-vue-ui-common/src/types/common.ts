/**
 * 通用分页查询参数
 */
export interface QueryParams {
  page?: number;
  size?: number;
  sort?: string;
  order?: 'asc' | 'desc';
  [key: string]: any;
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  empty: boolean;
}

/**
 * 通用API响应结构
 */
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

/**
 * 树形结构节点
 */
export interface TreeNode {
  id: string | number;
  label: string;
  children?: TreeNode[];
  [key: string]: any;
}

/**
 * 选项类型
 */
export interface Option {
  label: string;
  value: string | number;
  disabled?: boolean;
  [key: string]: any;
}

/**
 * 文件类型
 */
export interface FileInfo {
  id?: string;
  name: string;
  url?: string;
  size?: number;
  type?: string;
  lastModified?: number;
  [key: string]: any;
}
