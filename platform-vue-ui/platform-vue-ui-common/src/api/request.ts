import axios, { AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { ElMessage } from 'element-plus';

// 创建 axios 实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8',
  },
});

// 请求拦截器
request.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token');
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data;
    // 根据自定义错误码判断请求是否成功
    if (res.code && res.code !== 200) {
      // 处理token过期
      if (res.code === 401) {
        ElMessage.error('登录已过期，请重新登录');
        localStorage.removeItem('token');
        window.location.href = '/login';
      } else {
        ElMessage.error(res.message || '请求失败');
      }
      return Promise.reject(new Error(res.message || '请求失败'));
    } else {
      return res;
    }
  },
  (error: AxiosError) => {
    // 处理 HTTP 错误状态码
    let message = '';
    if (error.response) {
      const status = error.response.status;
      switch (status) {
        case 400:
          message = '请求错误';
          break;
        case 401:
          message = '未授权，请登录';
          localStorage.removeItem('token');
          window.location.href = '/login';
          break;
        case 403:
          message = '拒绝访问';
          break;
        case 404:
          message = `请求地址出错: ${error.response.config.url}`;
          break;
        case 408:
          message = '请求超时';
          break;
        case 500:
          message = '服务器内部错误';
          break;
        case 501:
          message = '服务未实现';
          break;
        case 502:
          message = '网关错误';
          break;
        case 503:
          message = '服务不可用';
          break;
        case 504:
          message = '网关超时';
          break;
        case 505:
          message = 'HTTP版本不支持';
          break;
        default:
          message = `请求失败: ${error.message}`;
      }
    } else {
      message = `网络错误: ${error.message}`;
    }

    ElMessage.error(message);
    return Promise.reject(error);
  }
);

export default request;
