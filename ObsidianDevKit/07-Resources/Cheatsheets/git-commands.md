---
title: Git命令速查表
category: cheatsheet
tags: [git, version-control, commands]
created: 2024-12-01
updated: 2024-12-17
---

# 📋 Git命令速查表

## 🚀 基础命令

### 初始化和配置
```bash
# 初始化仓库
git init

# 克隆仓库
git clone <url>
git clone <url> <directory>

# 配置用户信息
git config --global user.name "Your Name"
git config --global user.email "email@example.com"

# 查看配置
git config --list
git config user.name
```

### 基本操作
```bash
# 查看状态
git status
git status -s  # 简短格式

# 添加文件
git add <file>
git add .
git add -A
git add -p  # 交互式添加

# 提交
git commit -m "message"
git commit -am "message"  # 添加并提交
git commit --amend  # 修改最后一次提交

# 查看差异
git diff  # 工作区 vs 暂存区
git diff --staged  # 暂存区 vs 最后提交
git diff HEAD  # 工作区 vs 最后提交
```

## 🌿 分支管理

### 分支操作
```bash
# 查看分支
git branch
git branch -a  # 所有分支
git branch -r  # 远程分支

# 创建分支
git branch <branch-name>
git checkout -b <branch-name>  # 创建并切换

# 切换分支
git checkout <branch-name>
git switch <branch-name>  # 新命令

# 合并分支
git merge <branch-name>
git merge --no-ff <branch-name>  # 禁用快进

# 删除分支
git branch -d <branch-name>  # 安全删除
git branch -D <branch-name>  # 强制删除

# 重命名分支
git branch -m <old-name> <new-name>
```

### 远程分支
```bash
# 推送到远程
git push origin <branch-name>
git push -u origin <branch-name>  # 设置上游

# 拉取远程分支
git fetch origin
git checkout -b <branch> origin/<branch>

# 删除远程分支
git push origin --delete <branch-name>
git push origin :<branch-name>  # 旧语法
```

## 📚 提交历史

### 查看日志
```bash
# 基本日志
git log
git log --oneline
git log --graph
git log -n 5  # 最近5条

# 格式化日志
git log --pretty=format:"%h - %an, %ar : %s"
git log --pretty=oneline --graph --all

# 搜索日志
git log --grep="keyword"
git log --author="name"
git log --since="2 weeks ago"
git log --until="2023-12-31"

# 文件历史
git log -- <file>
git log -p <file>  # 显示差异
```

### 查看具体提交
```bash
git show <commit-hash>
git show HEAD
git show HEAD~2  # 前2个提交
```

## 🔄 撤销操作

### 撤销修改
```bash
# 撤销工作区修改
git checkout -- <file>
git restore <file>  # 新命令

# 撤销暂存
git reset HEAD <file>
git restore --staged <file>  # 新命令

# 撤销提交
git reset --soft HEAD~1  # 保留修改
git reset --hard HEAD~1  # 丢弃修改
git revert <commit>  # 创建新提交来撤销
```

### 找回丢失的提交
```bash
git reflog
git checkout <lost-commit-hash>
```

## 🌐 远程仓库

### 远程操作
```bash
# 查看远程仓库
git remote -v
git remote show origin

# 添加远程仓库
git remote add <name> <url>

# 修改远程仓库
git remote set-url origin <new-url>

# 删除远程仓库
git remote rm <name>

# 重命名远程仓库
git remote rename <old> <new>
```

### 同步操作
```bash
# 拉取
git fetch
git fetch --all

# 拉取并合并
git pull
git pull --rebase  # 变基而非合并

# 推送
git push
git push -f  # 强制推送（危险）
git push --tags  # 推送标签
```

## 🏷️ 标签管理

```bash
# 查看标签
git tag
git tag -l "v1.8*"

# 创建标签
git tag v1.0
git tag -a v1.0 -m "version 1.0"  # 附注标签
git tag v1.0 <commit-hash>  # 给历史提交打标签

# 查看标签信息
git show v1.0

# 推送标签
git push origin v1.0
git push origin --tags

# 删除标签
git tag -d v1.0  # 本地
git push origin :refs/tags/v1.0  # 远程
```

## 🔧 高级操作

### 暂存工作
```bash
# 暂存当前工作
git stash
git stash save "message"

# 查看暂存
git stash list

# 恢复暂存
git stash pop  # 恢复并删除
git stash apply  # 仅恢复
git stash apply stash@{2}  # 指定暂存

# 删除暂存
git stash drop
git stash clear  # 清空所有
```

### 变基操作
```bash
# 变基
git rebase <branch>
git rebase -i HEAD~3  # 交互式变基

# 变基冲突处理
git rebase --continue
git rebase --abort
git rebase --skip
```

### 子模块
```bash
# 添加子模块
git submodule add <url> <path>

# 初始化子模块
git submodule init
git submodule update

# 克隆包含子模块的项目
git clone --recursive <url>
```

## 🔍 调试和修复

### 查找问题
```bash
# 二分查找问题提交
git bisect start
git bisect bad  # 当前有问题
git bisect good <commit>  # 已知的好提交
git bisect reset  # 结束

# 查找文件修改者
git blame <file>
```

### 清理
```bash
# 清理未跟踪文件
git clean -n  # 预览
git clean -f  # 执行
git clean -fd  # 包括目录

# 压缩历史
git gc
git gc --aggressive
```

## 💡 实用技巧

### 别名设置
```bash
git config --global alias.co checkout
git config --global alias.br branch
git config --global alias.ci commit
git config --global alias.st status
git config --global alias.unstage 'reset HEAD --'
git config --global alias.last 'log -1 HEAD'
git config --global alias.visual '!gitk'
```

### 常用组合
```bash
# 美化的日志
git log --graph --pretty=format:'%Cred%h%Creset -%C(yellow)%d%Creset %s %Cgreen(%cr) %C(bold blue)<%an>%Creset' --abbrev-commit

# 查看今天的提交
git log --since="midnight"

# 查看未推送的提交
git log origin/main..HEAD

# 查看即将拉取的内容
git log HEAD..origin/main
```

## 🚨 危险操作（谨慎使用）

```bash
# 强制推送
git push -f

# 硬重置
git reset --hard <commit>

# 清空历史
git checkout --orphan new_branch
git add -A
git commit -m "Initial commit"
git branch -D main
git branch -m main
```

## 📋 工作流示例

### Feature Branch工作流
```bash
# 1. 创建功能分支
git checkout -b feature/new-feature

# 2. 开发并提交
git add .
git commit -m "Add new feature"

# 3. 推送到远程
git push -u origin feature/new-feature

# 4. 创建Pull Request（在GitHub/GitLab上）

# 5. 合并后清理
git checkout main
git pull
git branch -d feature/new-feature
```

---

## 🔗 相关资源
- [[git-workflow|Git工作流详解]]
- [[github-collaboration|GitHub协作指南]]
- [Pro Git Book](https://git-scm.com/book)

*提示：可以将常用命令设置为别名，提高效率！*