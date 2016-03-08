# git 操作

## git 命令

* 创建本地仓库

```
git init
```

* 把本地仓库一远程仓库关联

```
git remote add origin https://github.com/you/yourpro.git
```

* 从本地仓库中删除

```
git rm file.txt         // 从版本库中移除，删除源文件
git rm file.txt -cached // 从版本库中移除，不删除原始文件
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

* 把本地提交 push 到远程服务器

```
git push origin master

origin: https://github.com/you/yourpro.git
master: 默认分支
```

* 查看状态

```
git status
```

* 从远程库中下载新的改动

```
git pull origin master
```

## gitignore

在本地仓库根目录创建 .gitignore 文件。Win7 下不能直接创建，可以创建 ".gitignore." 文件，后面的标点自动被忽略；

```
/.idea          // 过滤指定文件夹
/fd/*           // 忽略根目录下的 /fd/ 目录的全部内容；
*.iml           // 过滤指定的所有文件
!.gitignore     // 不忽略该文件
```

