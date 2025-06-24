# 🚀 ObsidianDevKit 快速设置指南

## 📋 前置要求

- [Obsidian](https://obsidian.md/) v1.0.0+
- Git（用于版本控制）
- 基础的Markdown知识

## 🔧 安装步骤

### 1. 克隆仓库
```bash
git clone https://github.com/yourusername/ObsidianDevKit.git
cd ObsidianDevKit
```

### 2. 在Obsidian中打开
1. 打开Obsidian
2. 点击左下角的 "打开其他仓库" 图标
3. 选择 "打开文件夹作为仓库"
4. 选择刚刚克隆的 `ObsidianDevKit` 文件夹

### 3. 信任作者并启用插件
首次打开时，Obsidian会询问是否信任此仓库的作者，选择"信任作者并启用插件"。

## 🔌 必装插件配置

### 核心插件（内置）
确保以下核心插件已启用：
- ✅ 文件恢复
- ✅ 搜索
- ✅ 快速切换
- ✅ 标签面板
- ✅ 页面预览
- ✅ 笔记重组
- ✅ 命令面板
- ✅ 模板
- ✅ 日记

### 社区插件安装

1. **打开设置**：`Ctrl/Cmd + ,`
2. **进入社区插件**：设置 → 社区插件
3. **关闭安全模式**：关闭"安全模式"
4. **浏览插件**：点击"浏览"

#### 必装插件列表

##### 1. Calendar
- **用途**：日历视图管理每日笔记
- **配置**：
  ```
  - 每日笔记文件夹：01-Daily/2024/12-December
  - 每日笔记格式：YYYY-MM-DD
  - 每日笔记模板：10-Templates/daily-note.md
  ```

##### 2. Dataview
- **用途**：动态查询和数据展示
- **配置**：
  ```
  - 启用JavaScript查询：✅
  - 启用内联查询：✅
  - 启用内联JavaScript查询：✅
  ```

##### 3. Templater
- **用途**：高级模板功能
- **配置**：
  ```
  - 模板文件夹位置：10-Templates
  - 启用文件夹模板：✅
  - 触发Templater替换的热键：Alt+E
  ```

##### 4. Tasks
- **用途**：任务管理
- **配置**：
  ```
  - 全局过滤器：#task
  - 完成日期格式：✅ YYYY-MM-DD
  ```

##### 5. Kanban
- **用途**：看板视图
- **配置**：默认即可

##### 6. Git
- **用途**：版本控制
- **配置**：
  ```
  - 自动拉取间隔：10分钟
  - 自动提交间隔：30分钟
  - 提交信息：vault backup: {{date}}
  ```

##### 7. Obsidian Cryptography（可选）
- **用途**：加密敏感内容
- **配置**：设置一个强密码

## ⚙️ 个性化配置

### 1. 快捷键设置
建议设置以下快捷键（设置 → 快捷键）：

| 功能 | 快捷键 | 说明 |
|------|--------|------|
| 创建每日笔记 | `Ctrl+D` | 快速创建今天的笔记 |
| 插入模板 | `Ctrl+T` | 从模板创建内容 |
| 快速切换 | `Ctrl+P` | 快速打开文件 |
| 全局搜索 | `Ctrl+Shift+F` | 搜索所有内容 |
| 切换编辑/预览 | `Ctrl+E` | 切换视图模式 |
| 插入任务 | `Alt+T` | 创建任务项 |

### 2. 外观主题
推荐主题：
- **Minimal**：简洁优雅
- **Blue Topaz**：功能丰富
- **Things**：美观实用

安装方法：设置 → 外观 → 管理 → 浏览主题

### 3. CSS代码片段
创建自定义样式：`.obsidian/snippets/custom.css`

```css
/* 自定义字体 */
body {
    font-family: 'Microsoft YaHei', 'Helvetica Neue', sans-serif;
}

/* 代码块样式 */
.markdown-preview-view code {
    background-color: #f5f5f5;
    padding: 2px 4px;
    border-radius: 3px;
}

/* 任务样式 */
.task-list-item-checkbox:checked {
    background-color: #4caf50;
}
```

## 📁 文件夹使用说明

### 日常工作流
1. **早晨**：在 `01-Daily` 创建今日笔记
2. **工作中**：临时想法记录到 `00-Inbox`
3. **晚上**：整理Inbox内容到相应文件夹

### 项目管理
1. 新项目在 `02-Projects/Active` 创建
2. 使用项目模板初始化
3. 完成后移动到 `02-Projects/Archive`

### 知识管理
1. 技术笔记放在 `03-Development` 对应子文件夹
2. 学习资料放在 `04-Learning`
3. 代码片段保存到 `07-Resources/Snippets`

## 🔄 同步设置

### Git同步
```bash
# 首次设置
git config user.name "Your Name"
git config user.email "your.email@example.com"

# 日常同步
git add .
git commit -m "Daily update"
git push
```

### 自动备份
使用Git插件可以自动备份，或使用以下脚本：

```bash
#!/bin/bash
# backup.sh
cd /path/to/ObsidianDevKit
git add .
git commit -m "Auto backup: $(date '+%Y-%m-%d %H:%M:%S')"
git push
```

添加到cron定时任务：
```bash
*/30 * * * * /path/to/backup.sh
```

## 🎯 使用技巧

### 1. 快速创建笔记
- 使用 `[[` 创建链接时，如果页面不存在会自动创建
- 在文件浏览器中右键 → 从模板新建

### 2. 标签系统
- 项目相关：`#project-name`
- 技术栈：`#javascript #golang #docker`
- 状态：`#active #completed #archived`
- 类型：`#bug #feature #meeting`

### 3. 搜索技巧
- `tag:#golang`：搜索特定标签
- `file:2024-12`：搜索文件名
- `section:"代码示例"`：搜索章节
- `/regex/`：正则表达式搜索

### 4. Dataview查询
```dataview
table status, due
from #task
where !completed
sort due asc
```

## 🐛 常见问题

### Q: 插件无法安装
**A**: 检查网络连接，或手动从GitHub下载插件

### Q: Git同步冲突
**A**: 使用 `git pull --rebase` 解决冲突

### Q: 模板不工作
**A**: 检查Templater插件设置中的模板文件夹路径

### Q: 中文搜索不准确
**A**: 安装中文分词插件：Search Text Extractor

## 📚 进阶资源

- [Obsidian官方文档](https://help.obsidian.md/)
- [Obsidian社区论坛](https://forum.obsidian.md/)
- [Dataview文档](https://blacksmithgu.github.io/obsidian-dataview/)
- [Templater文档](https://silentvoid13.github.io/Templater/)

## 🤝 社区支持

- 加入[Obsidian Discord](https://discord.gg/obsidianmd)
- 关注[Reddit r/ObsidianMD](https://www.reddit.com/r/ObsidianMD/)
- 查看[Awesome Obsidian](https://github.com/kmaasrud/awesome-obsidian)

---

🎉 **恭喜！** 你已经完成了ObsidianDevKit的基础设置。开始你的知识管理之旅吧！

如有问题，请查看 [[README|主文档]] 或提交Issue。

*Happy Note Taking! 📝*