import { defineStore } from 'pinia';
import { UserModel } from 'platform-vue-ui-common';

interface UserState {
  token: string;
  info: Partial<UserModel>;
  permissions: string[];
  roles: string[];
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('token') || '',
    info: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    permissions: [],
    roles: []
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token,
    username: (state) => state.info.username || '',
    hasPermission: (state) => (permission: string) => state.permissions.includes(permission),
    hasRole: (state) => (role: string) => state.roles.includes(role)
  },
  
  actions: {
    setToken(token: string) {
      this.token = token;
      localStorage.setItem('token', token);
    },
    
    setUserInfo(info: Partial<UserModel>) {
      this.info = info;
      localStorage.setItem('userInfo', JSON.stringify(info));
    },
    
    setPermissions(permissions: string[]) {
      this.permissions = permissions;
    },
    
    setRoles(roles: string[]) {
      this.roles = roles;
    },
    
    login(token: string, userInfo: Partial<UserModel>) {
      this.setToken(token);
      this.setUserInfo(userInfo);
    },
    
    logout() {
      this.token = '';
      this.info = {};
      this.permissions = [];
      this.roles = [];
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
    },
    
    // 从服务器获取用户信息
    async fetchUserInfo() {
      try {
        // 这里应该调用实际的API
        // const response = await userService.getUserInfo();
        // this.setUserInfo(response.data);
        // this.setPermissions(response.data.permissions || []);
        // this.setRoles(response.data.roles || []);
        
        // 模拟数据
        const mockUserInfo = {
          id: '1',
          username: 'admin',
          email: 'admin@example.com',
          roles: ['admin'],
          permissions: ['read', 'write', 'delete']
        };
        
        this.setUserInfo(mockUserInfo);
        this.setPermissions(mockUserInfo.permissions);
        this.setRoles(mockUserInfo.roles);
        
        return mockUserInfo;
      } catch (error) {
        console.error('获取用户信息失败:', error);
        throw error;
      }
    }
  }
});
