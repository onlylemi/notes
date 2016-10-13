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

### 撤销操作

### 分支相关

### 冲突处理

### 其他



* 从本地仓库中删除

```
git rm file.txt         // 从版本库中移除，删除文件
git rm file.txt --cached // 从版本库中移除，不删除原始文件
git rm -r xxx           // 从版本库中删除指定文件夹
```

* 从本地仓库中添加新的文件

```
git add .               // 添加所有文件
git add file.txt        // 添加指定文件
```

* 提交，把缓存内容提交到 HEAD 里

```
git commit -m "注释"
```

* 撤销

```
// 撤销最近的一个提交.
git revert HEAD

// 取消 commit + add
git reset --mixed

// 取消 commit
git reset --soft

// 取消 commit + add + local working
git reset --hard
```

* 把本地提交 push 到远程服务器

```
git push [remote-name] [loca-branch]:[remote-branch]
例：git push origin master:master
```

* 查看状态

```
git status
```

* 从远程库中下载新的改动

```
git fetch [remote-name]/[branch]
```

* 合并下载的改动到分支

```
git merge [remote-name]/[branch]
```

* 从远程库中下载新的改动

```
pull = fetch + merge

git pull [remote-name] [branch]
例：git pull origin master
```

* 分支

```
// 列出分支
git branch

// 创建一个新的分支
git branch (branch-name)

// 删除一个分支
git branch -d (branch-nam)

// 删除 remote 的分支
git push (remote-name) :(remote-branch)
```

* 切换分支

```
// 切换到一个分支
git checkout [branch-name]

// 创建并切换到该分支
git checkout -b [branch-name]
```

## gitignore

在本地仓库根目录创建 .gitignore 文件。Win7 下不能直接创建，可以创建 ".gitignore." 文件，后面的标点自动被忽略；

```
/.idea          // 过滤指定文件夹
/fd/*           // 忽略根目录下的 /fd/ 目录的全部内容；
*.iml           // 过滤指定的所有文件
!.gitignore     // 不忽略该文件
```

