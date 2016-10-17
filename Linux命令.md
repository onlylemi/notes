# Linux 命令

> 参考阅读：[Linux 命令手册](http://linux.51yip.com/)

## 基本命令

* `ls` —— 输出该目录下的所有文件
* `ls -F` —— 显示目录中的文件
* `ls -l` —— 显示文件和目录的详细信息
* `ls -a` —— 显示隐藏文件
* `ls *[0-9]*` —— 显示包含数字的目录和文件
* `cd [文件夹]` —— 进入到该文件下
* `cat [文件]` —— 输出该文件的内容
* `pwd` —— 显示工作路径
* `lsusb` —— 显示 USB 设备
* `date` —— 显示系统日期
* `mkdir [folder-name]` —— 创建文件夹
* `rm [file-name]` —— 删除文件
* `rmdir [folder-name]` —— 删除文件夹
* `rm -rf [folder-name]` —— 删除目录及其内容
* `mv [file1] [file2]` —— 重命名/移动文件
* `cp [file1] [file2]` —— 复制一个文件
* `cp -a [dir1] [dir2]` —— 复制一个目录
* `shutdown -h now` —— 关机
* `reboot` —— 重启
* `logout` —— 注销

## 压缩相关

* `bunzip2 [*.bz2]` —— 解压 .bz2 文件
* `bzip2 [file]` —— 压缩
* `gunzip [*.gz]` —— 解压 .gz 文件
* `gzip2 [file]` —— 压缩
* `rar a [file.rar] [file]` —— 压缩
* `rar x [file.rar]` —— 解压
* `unrar x [file.rar]` —— 解压
* `tar -cvf [file.zip] [file]` —— 压缩
* `tar -tf [file.zip]` —— 显示压缩包中的内容 
* `tar -xvf [file.zip]` —— 释放压缩包
* `tar -xvf [file.zip] -C [/tmp]` —— 将压缩包释放到 /tmp 目录下 
* `tar -cvfj [file.bz2] [file]` —— 压缩
* `tar -xvfj [file.bz2】` —— 解压 bzip2 格式的压缩包 
* `tar -cvfz [file.gz] [file]` —— 创建 gzip 格式的压缩包 
* `tar -xvfz [file.gz]` —— 解压 gzip 格式的压缩包 
* `zip [file.zip] file1` —— 压缩 
* `zip -r [file.zip] [file] [file2]` —— 几个文件压缩
* `unzip [file.zip]` —— 解压

## 配置

* `/etc/profile` —— 
* `/etc/bash.bashrc` —— 系统级的 bashrc 文件
* `~/.profile` —— 当用户登录时，该文件仅仅执行一次
* `~/.bashrc` —— 用户级的 bashrc 文件，环境变量一般放到这儿
* `source ~/.bashrc` —— 环境变量配置完成后，执行该命令应用
* `export PATH=$PATH:/tmp/../../` —— 环境变量配置