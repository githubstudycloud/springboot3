# 🔐 密码管理指南

> ⚠️ **重要安全提示**：
> - **永远不要**在Obsidian中明文存储密码
> - 使用专业的密码管理器（如1Password、Bitwarden、KeePass）
> - 本文件夹仅用于存储密码提示和相关信息
> - 敏感内容请使用Obsidian Cryptography插件加密

## 📋 密码管理最佳实践

### 1. 使用密码管理器
推荐工具：
- **1Password** - 企业级，功能全面
- **Bitwarden** - 开源，可自托管
- **KeePass** - 本地存储，完全控制

### 2. 密码策略
- 长度：至少16个字符
- 复杂度：大小写字母+数字+特殊字符
- 唯一性：每个账户使用不同密码
- 定期更换：重要账户每3-6个月

### 3. 双因素认证（2FA）
优先级：
1. 硬件密钥（YubiKey）
2. 认证器应用（Google Authenticator、Authy）
3. 短信验证（最不安全）

## 📝 密码记录模板

```markdown
---
service: 服务名称
url: https://example.com
username: 用户名
email: email@example.com
password-hint: 密码提示（不是密码本身！）
2fa: enabled/disabled
2fa-type: authenticator/sms/hardware
recovery-codes: 存储位置提示
last-updated: 2024-12-17
tags: [password, category]
---

## 服务信息
- **用途**：这个账户用来做什么
- **重要级别**：高/中/低
- **关联账户**：是否与其他服务关联

## 安全问题
- Q1: 提示，不是答案
- Q2: 提示，不是答案

## 备注
- 特殊要求或限制
- 密码规则（如必须包含特殊字符）
```

## 🔒 安全存储建议

### Obsidian内的安全措施
1. **安装加密插件**
   ```
   Settings → Community plugins → Browse → Obsidian Cryptography
   ```

2. **加密敏感笔记**
   - 使用强密码短语
   - 定期更换加密密码
   - 不要忘记主密码！

3. **文件权限**
   - 限制Obsidian vault的访问权限
   - 定期备份到加密存储
   - 使用Git时添加.gitignore

### 示例.gitignore
```gitignore
# 安全相关
09-Security/Passwords/*
!09-Security/Passwords/password-management-guide.md
09-Security/Sensitive/*
09-Security/Licenses/*

# 个人隐私
06-Personal/Finance/*
06-Personal/Health/medical-records/*

# 临时文件
.obsidian/workspace
.trash/
.DS_Store
```

## 🚨 紧急情况处理

### 密码泄露应对
1. **立即行动**
   - [ ] 更改受影响账户密码
   - [ ] 检查账户活动记录
   - [ ] 启用2FA（如果还没有）
   - [ ] 通知相关方

2. **后续措施**
   - [ ] 更新密码管理器中的记录
   - [ ] 检查其他使用相似密码的账户
   - [ ] 记录事件用于将来参考

### 账户恢复准备
- 保存恢复代码的安全位置
- 备用邮箱和手机号码
- 身份验证文件扫描件

## 📊 密码强度检查清单

定期（每季度）检查：
- [ ] 所有密码都是唯一的
- [ ] 密码长度符合要求
- [ ] 2FA已启用在所有支持的服务上
- [ ] 恢复选项是最新的
- [ ] 删除不再使用的账户

## 🔗 相关资源
- [[security-best-practices|安全最佳实践]]
- [[2fa-setup-guide|2FA设置指南]]
- [[encryption-tools|加密工具推荐]]

---

⚠️ **记住**：安全第一！宁可麻烦一点，也不要图方便而降低安全标准。

*最后更新：2024-12-17*