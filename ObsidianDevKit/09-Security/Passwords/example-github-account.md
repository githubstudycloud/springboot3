---
service: GitHub
url: https://github.com
username: john_developer
email: john@example.com
password-hint: 公司标准格式 + 出生年份 + 特殊符号
2fa: enabled
2fa-type: authenticator
recovery-codes: 保险箱文件夹A-3
last-updated: 2024-12-17
tags: [password, development, critical]
---

# 🔐 GitHub账户信息

> ⚠️ **注意**：本文件不包含实际密码，仅作为提示和管理用途

## 服务信息
- **用途**：个人和公司项目的代码托管
- **重要级别**：🔴 高（包含所有项目代码）
- **关联账户**：
  - 公司邮箱
  - VS Code同步
  - CI/CD服务

## 账户详情
- **用户名**：john_developer
- **主邮箱**：john@example.com
- **备用邮箱**：john.backup@example.com
- **创建日期**：2019-03-15

## 安全设置
- **2FA状态**：✅ 已启用
- **2FA应用**：Google Authenticator（手机1）
- **备用2FA**：Authy（手机2）
- **SSH密钥**：
  - 个人笔记本：~/.ssh/id_ed25519
  - 公司电脑：~/.ssh/id_rsa_work
- **个人访问令牌**：
  - CI/CD令牌：密码管理器 - "GitHub PAT CI"
  - IDE令牌：密码管理器 - "GitHub PAT IDE"

## 安全问题提示
- Q1: 第一个宠物的名字？
  - 提示：大学时期的金毛
- Q2: 母亲的娘家姓？
  - 提示：奶奶的姓氏

## 恢复代码位置
- **物理位置**：家中保险箱，文件夹A-3
- **数字备份**：加密USB（标记为"Backup-2024"）
- **云备份**：个人Dropbox/加密文件夹

## 相关配置
```bash
# Git全局配置
git config --global user.name "John Developer"
git config --global user.email "john@example.com"
git config --global core.sshCommand "ssh -i ~/.ssh/id_ed25519"
```

## 重要提醒
- [ ] 每6个月更新一次密码
- [ ] 每年检查和清理个人访问令牌
- [ ] 定期审查有权限的第三方应用

## 紧急联系
- 如果账户被盗，立即：
  1. 从另一设备登录并更改密码
  2. 撤销所有个人访问令牌
  3. 检查最近的提交历史
  4. 通知团队成员

## 相关链接
- [[git-ssh-setup|Git SSH设置指南]]
- [[github-security-best-practices|GitHub安全最佳实践]]
- [[development-tools-passwords|开发工具密码管理]]

---
*最后更新：2024-12-17*
*下次密码更新：2025-06-17*