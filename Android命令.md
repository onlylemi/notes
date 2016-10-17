# Android 命令

## ADB

* `adb install [.apk]` —— 安装 apk
* `adb install -r [.apk]` —— 强制安装
* `adb uninstall [package-name]` —— 卸载应用
* `adb devices` —— 显示当前运行的全部设备
* `adb pull [remote-file] [local-file]` —— 获取设备中的文件到本地
* `adb push [local-file] [remote-file]` —— 向设备中写入文件
* `adb root` —— 获取设备 root 权限
* `adb remount` —— 重新挂载系统分区，使系统分区重新可写
* `adb shell` —— 进入该设备
* `adb logcat` —— 查看 log 信息

## Gradle

* `gradle -v` —— gradle 版本
* `gradle tasks` —— 列出 task 列表
* `gradle asD` —— 编译 debug 打包
* `gradle asR` —— 编译 release 打包
* `gradle clean` —— 删除 build 文件夹
* `gradle build` —— 编译 debug、release 打包，生成 build 文件夹