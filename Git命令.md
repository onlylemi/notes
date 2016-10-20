# Git 操作

> 参考阅读：
*  [git - 简明指南](http://rogerdudler.github.io/git-guide/index.zh.html)
* [Git 官方教程](https://git-scm.com/book/zh/v2)
* [25个Git用法技巧](http://www.techug.com/25-git-tips)
* [图解Git](https://marklodato.github.io/visual-git-guide/index-zh-cn.html)

## 图解

![Git演示图](https://raw.githubusercontent.com/onlylemi/res/master/git-img.png)

* `workspace` —— 本地工作目录
* `index` —— 暂存区
* `local` —— 本地仓库，HEAD，指向最后一次提交的结果
* `remote` —— 远程仓库

## 命令

### 配置

* `git config --global user.name "name"` —— 配置全局 Git 用户名
* `git config --global user.email "you@example.com"` —— 配置全局 Git 邮箱
* `git config user.name "name"` —— 配置当前仓库 Git 用户名
* `git config user.email "you@example.com"` —— 配置当前仓库 Git 邮箱
* `git config --global user.name` —— 查看全局 Git 用户名
* `git config --global user.email` —— 查看全局 Git 邮箱
* `git config user.name` —— 查看当前仓库 Git 用户名
* `git config user.email` —— 查看当前仓库 Git 邮箱

### 初始化

* `git init` —— 仓库初始化
* `git clone [url]` —— 获取远程仓库到本地
* `git remote add [remote-name] [remote-url]` —— 添加一个新的远程仓库
* `git remote` —— 列出所有的 remote
* `git remote -v` —— 列出所有的 remote 的地址
* `git remote rm [remote-name]` —— 删除一个 remote
* `git reomte rename [old-name] [new-name]` —— 重命名 remote

### 基本操作

* `git add [file]` —— 添加修改文件，workspace -> index
* `git add .` —— t添加所有修改文件，workspace -> index
* `git commit -m "提交信息"` —— 文件提交，index -> local
* `git commit --amend` —— 与上次 commit 合并，index -> local 
* `git push [remote-name] [loca-branch]:[remote-branch]` —— 把本地的某个分支推送到远程仓库的某个分支，local -> remote
* `git fetch ` —— 抓取远程仓库至最新改动
* `git merge [branch]` —— 合并其他分支到当前分支
* `git pull [reomte] [branch]` —— 更新本地仓库到最新改动，remote -> workspace
* `git status` —— 查看修改状态
* `git log` —— 查看提交记录
* `git show` —— 展示提交的内容
* `git stash` —— 暂存修改
* `git stash pop` —— 弹出之前暂存的修改

### 分支相关

* `git checkout -b [branch-name]` —— 创建分支，并切换到该分支
* `git checkout [branch-name]` —— 切换到该分支
* `git branch [branch-name]` —— 创建分支
* `git branch -d [branch-name]` —— 删除分支 
* `git branch -m [old-name] [new-name]` —— 重命名分支
* `git branch` —— 查看所有分支
* `git branch -a` —— 查看远程所有分支

### 撤销操作

* `git reset --hard HEAD~` —— 撤销提交，local -> workspace，修改的文件也会丢失
* `git reset --soft HEAD~` —— 撤销提交，local -> index
* `git reset --mixed HEAD~` —— （默认）撤销提交，local -> workspace，修改的不会会丢失
* `git checkout [filename]` —— index -> workspace，修改丢失
* `git rm [filename]` —— 从版本库中移除，删除源文件
* `git rm [filename] --cached` —— 从版本库中移除，不删除源文件
* `git rm -r [folder-name]` —— 从版本库中移除，删除源文件

### 冲突处理

* `git diff` —— 对比 workspace 和 index
* `git diff HEAD` —— 独臂 workspace 与最后一次提交
* `git diff [source-branch] [target-branch]` —— 对比两个分支差异
* `git add [filename]` —— 冲突修改完，需 add 标记合并成功

## gitignore

在本地仓库根目录创建 .gitignore 文件。Win7 下不能直接创建，可以创建 ".gitignore." 文件，后面的标点自动被忽略；

* `/.idea` —— 过滤指定文件夹
* `/fd/*` —— 忽略根目录下的 /fd/ 目录的全部内容；
* `*.iml` —— 过滤指定的所有文件
* `!.gitignore` —— 不忽略该文件