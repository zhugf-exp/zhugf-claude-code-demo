# Git 常用命令

## 一、首次配置

```bash
# 查看当前配置
git config --list

# 设置用户名和邮箱
git config --global user.name "你的名字"
git config --global user.email "your-email@example.com"

# 设置默认分支名
git config --global init.defaultBranch main
```

## 二、创建与获取仓库

```bash
# 初始化当前目录为 Git 仓库
git init

# 克隆远程仓库
git clone <仓库地址>

# 克隆指定分支
git clone -b <分支名> <仓库地址>
```

## 三、查看状态与提交

```bash
# 查看工作区状态
git status

# 查看文件差异
git diff

# 查看暂存区差异
git diff --staged

# 将文件加入暂存区
git add <文件名>

# 将所有修改加入暂存区
git add .

# 提交暂存区内容
git commit -m "提交说明"

# 查看提交记录
git log

# 以简洁图形方式查看提交记录
git log --oneline --graph --decorate --all
```

## 四、分支操作

```bash
# 查看本地分支
git branch

# 查看所有分支
git branch -a

# 创建分支
git branch <分支名>

# 切换分支
git switch <分支名>

# 创建并切换到新分支
git switch -c <分支名>

# 合并指定分支到当前分支
git merge <分支名>

# 删除已合并的本地分支
git branch -d <分支名>

# 强制删除本地分支
git branch -D <分支名>
```

## 五、远程仓库

```bash
# 查看远程仓库地址
git remote -v

# 添加远程仓库
git remote add origin <仓库地址>

# 拉取远程更新并合并到当前分支
git pull

# 推送当前分支到远程仓库
git push

# 首次推送并建立上游关联
git push -u origin <分支名>

# 获取远程更新，但不自动合并
git fetch

# 删除远程分支
git push origin --delete <分支名>
```

## 六、撤销与恢复

```bash
# 取消文件的暂存，保留文件修改
git restore --staged <文件名>

# 丢弃工作区中未提交的文件修改
git restore <文件名>

# 修改最近一次提交说明
git commit --amend -m "新的提交说明"

# 创建一个新提交，撤销指定提交
git revert <提交ID>

# 查看历史操作记录
git reflog

# 恢复到指定提交，并保留工作区修改
git reset --soft <提交ID>

# 恢复到指定提交，并取消暂存但保留文件修改
git reset --mixed <提交ID>

# 恢复到指定提交并丢弃之后的修改，谨慎使用
git reset --hard <提交ID>
```

## 七、暂存工作进度

```bash
# 暂存当前未完成的修改
git stash

# 暂存修改并添加说明
git stash push -m "说明"

# 查看暂存列表
git stash list

# 恢复最近一次暂存并从列表删除
git stash pop

# 恢复指定暂存但保留暂存记录
git stash apply stash@{0}

# 删除指定暂存
git stash drop stash@{0}
```

## 八、标签与查找

```bash
# 查看标签
git tag

# 创建轻量标签
git tag v1.0.0

# 创建带说明的标签
git tag -a v1.0.0 -m "版本 1.0.0"

# 推送标签
git push origin v1.0.0

# 查看某次提交详情
git show <提交ID>

# 查找包含指定内容的提交
git log -S"关键字" --oneline

# 查看指定文件的修改历史
git log --follow -- <文件名>
```

## 九、常用协作流程

```bash
# 1. 更新主分支
git switch main
git pull origin main

# 2. 创建功能分支
git switch -c feature/<功能名>

# 3. 修改代码后提交
git add .
git commit -m "feat: 完成功能"

# 4. 推送功能分支
git push -u origin feature/<功能名>
```

## 十、提交信息示例

```text
feat: 新增用户登录功能
fix: 修复订单金额计算错误
docs: 更新项目使用说明
refactor: 重构数据处理逻辑
test: 增加用户服务测试
chore: 更新项目依赖
```

> 注意：`git reset --hard` 会直接丢弃未保存的修改，使用前请确认不再需要这些内容。
